package in.gov.chennaicorporation.gccoffice.vtrack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.security.SecureRandom;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.sql.DataSource;

import in.gov.chennaicorporation.gccoffice.controller.DateTimeUtil;

@Service
public class VehicleService {

	private JdbcTemplate jdbcTemplate;

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int STRING_LENGTH = 15;
	private static final Random RANDOM = new SecureRandom();

	@Autowired
	private Environment environment;
	private String fileBaseUrl;

	@Autowired
	public VehicleService(Environment environment) {
		this.fileBaseUrl = environment.getProperty("fileBaseUrl");
	}

	@Autowired
	public void setDataSource(@Qualifier("mysqlVehiclTrackingDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public static String generateRandomString() {
		StringBuilder result = new StringBuilder(STRING_LENGTH);
		for (int i = 0; i < STRING_LENGTH; i++) {
			result.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
		}
		return result.toString();
	}

	public static String generateRandomStringForFile(int String_Lenth) {
		StringBuilder result = new StringBuilder(String_Lenth);
		for (int i = 0; i < STRING_LENGTH; i++) {
			result.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
		}
		return result.toString();
	}

	private byte[] compressImage(BufferedImage image, float quality) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);

		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
		writer.setOutput(imageOutputStream);

		ImageWriteParam param = writer.getDefaultWriteParam();
		if (param.canWriteCompressed()) {
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
		}

		writer.write(null, new javax.imageio.IIOImage(image, null, null), param);

		writer.dispose();
		imageOutputStream.close();

		return byteArrayOutputStream.toByteArray();
	}

	public String fileUpload(String name, String id, MultipartFile file) {

		int lastInsertId = 0;
		// Set the file path where you want to save it
		String uploadDirectory = environment.getProperty("file.upload.directory");
		String serviceFolderName = environment.getProperty("vtrack_foldername");
		var year = DateTimeUtil.getCurrentYear();
		var month = DateTimeUtil.getCurrentMonth();
		var date = DateTimeUtil.getCurrentDay();

		uploadDirectory = uploadDirectory + serviceFolderName + "/" + year + "/" + month + "/" + date;

		try {
			// Create directory if it doesn't exist
			Path directoryPath = Paths.get(uploadDirectory);
			if (!Files.exists(directoryPath)) {
				Files.createDirectories(directoryPath);
			}

			// Datetime string
			String datetimetxt = DateTimeUtil.getCurrentDateTime();
			// File name
			System.out.println(file.getOriginalFilename());
			String fileName = name + "_" + id + "_" + datetimetxt + "_" + generateRandomStringForFile(10) + "_"
					+ file.getOriginalFilename();
			fileName = fileName.replaceAll("\\s+", ""); // Remove space on filename

			String filePath = uploadDirectory + "/" + fileName;

			String filepath_txt = "/" + serviceFolderName + "/" + year + "/" + month + "/" + date + "/" + fileName;

			// Create a new Path object
			Path path = Paths.get(filePath);

			// Get the bytes of the file
			byte[] bytes = file.getBytes();

			// Compress the image
			// BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
			// byte[] compressedBytes = compressImage(image, 0.5f); // Compress with 50%
			// quality

			// Write the bytes to the file
			Files.write(path, bytes);

			System.out.println(filePath);
			return filepath_txt;

		} catch (IOException e) {
			e.printStackTrace();
			return "Failed to save file " + file.getOriginalFilename();
		}
	}

	public List<Map<String, Object>> getAllTemplateNames() {
		String sql = "SELECT `id`, `name` as template_name, `whatsapp_temp_name`, `whatsapp_temp_id` FROM `whatsapp_template` WHERE `isactive`=1 AND `isdelete`=0";
		return jdbcTemplate.queryForList(sql);
	}

	public int saveMessageDetails(String date, String tempType, String imgPath) {
		String sql = "INSERT INTO `message_list` (`msg_date`, `template_type`, `msg_img`) VALUES (?,?,?)";
		int rowsAffected = jdbcTemplate.update(sql, date, tempType, imgPath);
		return rowsAffected;
	}

	public List<Map<String, Object>> getAllSavedMessages() {
		String sql = "SELECT "
				+ "ml.`id` as msgid, "
				+ "ml.`msg_date` as msg_date , "
				+ "DATE_FORMAT(ml.`msg_date`, '%d-%m-%Y') AS msg_temp_event_date, "
				+ "DATE_FORMAT(ml.`cdate`, '%d-%m-%Y %r') AS createddate, "
				+ "ml.`msg_img` as msg_img, "
				+ "CONCAT('" + fileBaseUrl + "/gcc/files', ml.msg_img) AS msg_attachfile, "
				+ "wt.`name` as template_name, "
				+ "wt.`id` as template_id "
				+ "FROM `message_list` ml "
				+ "JOIN `whatsapp_template` wt ON ml.`template_type`=wt.`id` "
				+ "WHERE ml.`isactive` = 1 AND ml.`isdelete` = 0";
		return jdbcTemplate.queryForList(sql);
	}

	public String sendMessage(String msgid, String datetxt, String fileurl) {

		String sql = "SELECT "
				+ "ml.`id` as msgid, "
				+ "DATE_FORMAT(ml.`msg_date`, '%d-%m-%Y') AS msg_temp_event_date, "
				+ "CONCAT('" + fileBaseUrl + "/gcc/files', ml.msg_img) AS msg_attachfile, "
				+ "wt.`id` as template_id "
				+ "FROM `message_list` ml "
				+ "JOIN `whatsapp_template` wt ON ml.`template_type`=wt.`id` "
				+ "WHERE ml.`isactive` = 1 AND ml.`isdelete` = 0 AND ml.`id`=?";
		// Fetch the data
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, msgid);

		// Initialize variables to store the results
		String tempid = "";
		String msgDate = "";
		String fileURL = "";
		String sendTo = "";
		// Check if the result list is not empty
		if (!result.isEmpty()) {
			Map<String, Object> row = result.get(0); // Get the first row (assuming id is unique)

			// Extract values from the result map and assign to variables
			tempid = String.valueOf(row.get("template_id"));
			msgDate = String.valueOf(row.get("msg_temp_event_date"));
			fileURL = String.valueOf(row.get("msg_attachfile"));

			sql = "SELECT `id`, `name`, `mobile`, `userid`, `tempids` FROM `send_msg_to` WHERE `isactive` = 1 AND `isdelete` = 0 AND FIND_IN_SET(?, tempids);";
			List<Map<String, Object>> mobileResult = jdbcTemplate.queryForList(sql, tempid);
			// Create a StringBuilder to hold the comma-separated mobile numbers
			StringBuilder sendToBuilder = new StringBuilder();

			// Iterate through the mobileResult list and build the sendTo string
			for (Map<String, Object> mobileRow : mobileResult) {
				String mobile = String.valueOf(mobileRow.get("mobile"));

				// Append the mobile number to the StringBuilder
				if (sendToBuilder.length() > 0) {
					sendToBuilder.append(",");
				}
				sendToBuilder.append(mobile);
			}

			// Convert StringBuilder to String
			sendTo = sendToBuilder.toString();

			// Now you have the sendTo string with all mobile numbers
			System.out.println("Mobile Numbers: " + sendTo); // For debugging, you can print this value
		}

		String urlString = "";

		String username = "2000233507";
		String password = "h2YjFNcJ";
		// String sendTo="9176617754"; //9176617754,8610011680,9360777472,9123565217
		// String msgDate=datetxt;
		// String fileURL=fileurl;

		String apikey = "5c995535-6244-11f0-98fc-02c8a5e042bd";
		String from = "919445061913";
		switch (tempid) {
			case "1":

				System.out.println("TempID is 1 (Vehicle March Out -> officer_test -> 1308878)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1"
						+ "&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Vehicle+Monitoring+System%2A%0A%0AZone-wise+vehicle+march-out+details+for+Today%27s+%28"
						+ msgDate
						+ "%29+AM+shift+are+shared+above.%0A%0AFor+further+details%2C+please+click+on+the+button+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true"
						+ "&footer=GCC+-+IT+Cell"
						+ "&buttonUrlParam=login.php%3Funame%3Dgcc.admin2024@jtrack.in%26password%3D123456";

				// urlString
				// ="https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template&apikey="+apikey+"&from="+from+"&to=9444173345&templateid=2784943&url=";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20vehicle%20march-out%20details";
				break;
			case "2":
				System.out.println("TempID is 2 (Fule Dip Details -> fuel_dip_details -> 1309072)");

				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Vehicle+Monitoring+System%2A%0A%0AYesterday%27s+%28" + msgDate
						+ "%29+Zone+wise+Fuel+Dip+Details+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Fuel%20Dip%20Details";
				break;
			case "3":
				System.out.println("TempID is 3 (Late Marchout Details -> late_marchout_details -> 1309075)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Vehicle+Monitoring+System%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone-wise+Late+Marchout+Details+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Late%20Marchout%20Details";
				break;
			case "4":
				System.out
						.println("TempID is 4 (LMV Route Deviation Details -> lmv_route_deviation_details -> 1309081)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Vehicle+Monitoring+System%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone-wise+Compactors+LMV+Route+Deviation+Details+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Compactors%20LMV%20Route%20%20Deviation%20Details";
				break;
			case "5":
				System.out
						.println("TempID is 5 (HMV Route Deviation Details -> hmv_route_deviation_details -> 1309083)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Vehicle+Monitoring+System%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone-wise+Compactors+HMV+Route+Deviation+Details+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Compactors%20HMV%20Route%20%20Deviation%20Details";
				break;

			case "6":
				System.out.println(
						"TempID is 6 (C&D Waste Removal Monitoring -> c_and_d_waste_removal_monitoring -> 1335786)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+C%26D+Waste+Removal+Monitoring%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+C%26D+Waste+Removal+Details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20C%26D%20Waste%20Removal%20details";
				break;

			case "7":
				System.out
						.println("TempID is 7 (Enforcement Team Monitoring -> enforcement_team_monitoring -> 1362100)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Enforcement+Team+Monitoring%2A%0A%0AYesterday%27s+%28" + msgDate
						+ "%29+Zone+wise+Enforcement+Team+activity+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Enforcement%20Team%20activity%20details";
				break;

			case "8":
				System.out.println("TempID is 8 (Toilets Monitoring -> gcc_toilets_monitoring -> 1475869)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Toilets+Monitoring%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+Toilets+Morning+inspection+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Toilets%20Morning%20inspection%20details";
				break;

			case "9":
				System.out.println("TempID is 9 (Toilets Monitoring -> gcc_toilets_evening_monitoring -> 1475881)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Toilets+Monitoring%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+Toilets+Evening+inspection+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Toilets%20Evening%20inspection%20details";
				break;

			case "10":
				System.out.println("TempID is 10 (Bus Shelter Monitoring -> 1508454 - gcc_bus_shelter_monitoring)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Bus+Shelter+Monitoring%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+Bus+Shelter+inspection+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Bus%20Shelter%20inspection%20details";
				break;

			case "11":
				System.out.println("TempID is 11 (Still Catch Pit -> 1566758 - silt_catch_pit_cleaning )");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Silt+catch+Pit+Cleaning%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+Silt+Catch+Pit+cleaning+details+shared+above+for+your+reference%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Silt%20Catch%20Pit%20cleaning%20details";
				break;

			case "12":
				System.out.println("TempID is 12 (1590940 - tree_planting_gcc - IMAGE - STATIC)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Tree+Planting%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+tree+planting+details+shared+above+for+your+reference%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20tree%20planting%20details";
				break;

			case "13":
				System.out.println("TempID is 13 (1590940 - Chennai Schools (userd of Com Temp ) - IMAGE - STATIC)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						// +
						// "&caption=%2AGCC+The+Chennai+School%2A%0A%0AToday%27s+%28"+msgDate+"%29+Zone+wise+Students+Aadhar+linked+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&caption=%2AGCC+The+Chennai+School%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+Students+Aadhar+linked+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Students%20Aadhar%20linked%20details";
				break;

			case "14":
				System.out.println("TempID is 14 (1943196 - pet_wp_report - IMAGE - STATIC)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						// +
						// "&caption=%2AGCC+The+Chennai+School%2A%0A%0AToday%27s+%28"+msgDate+"%29+Zone+wise+Students+Aadhar+linked+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&caption=%2AGCC+PET+LICENSE+%26+VACCINATION%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+Pet+license+%26+Vaccination+details+shared+above+for+your+kind+reference."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Pet%20license%20%26%20Vaccination%20details";
				break;

			case "15":
				System.out.println(
						"TempID is 15 (1590940 - GCC POS Penalty Monitoring Zone (userd of Com Temp ) - IMAGE - STATIC)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+POS+Penalty+Monitoring%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29++POS+Penalty+Zone+wise+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=POS%20Penalty%20Zone%20wise%20details";

				break;

			case "16":
				System.out.println(
						"TempID is 16 (1590940 - GCC POS Penalty Monitoring Catagory (userd of Com Temp ) - IMAGE - STATIC)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+POS+Penalty+Monitoring%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29++POS+Penalty+Catagory+wise+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=POS%20Penalty%20Catagory%20wise%20details";
				break;

			case "17":
				System.out.println(
						"TempID is 17 (1590940 - GCC Enforcement Team Monitoring Temp (userd of Com Temp ) - IMAGE - STATIC)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Enforcement+Team+Monitoring%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+Enforcement+Team+activity+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Zone%20wise%20Enforcement%20Team%20activity%20details";

				break;

			case "21":
				System.out.println("TempID is 21 (1590940 - Namma Salai - IMAGE - STATIC)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Enforcement+Team+Monitoring%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+Enforcement+Team+activity+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Namma%20salai%20activity%20details";

				break;

			case "22":
				System.out.println("TempID is 22 (1590940 - Victoria Hall Booking - IMAGE - STATIC)");
				urlString = "https://media.smsgupshup.com/GatewayAPI/rest?"
						+ "userid=" + username
						+ "&password=" + password
						+ "&send_to=" + sendTo
						+ "&v=1.1&format=json"
						+ "&msg_type=IMAGE"
						+ "&method=SENDMEDIAMESSAGE"
						+ "&caption=%2AGCC+Enforcement+Team+Monitoring%2A%0A%0AToday%27s+%28" + msgDate
						+ "%29+Zone+wise+Enforcement+Team+activity+details+shared+above+for+your+reference.%0A%0AFor+more+details%2C+please+click+the+link+below."
						+ "&media_url=" + fileURL
						+ "&isTemplate=true&footer=GCC+-+IT+Cell";

				urlString = "https://sendapiv1.pinbot.ai/pinwa/sendMessage?type=template"
						+ "&apikey=" + apikey
						+ "&from=" + from
						+ "&to={{to}}&templateid=2784943"
						+ "&url=" + fileURL
						+ "&placeholders=Victoria%20Hall%20Booking%20Details";

				break;

			default:
				System.out.println("TempID (" + tempid + ") is unknown");
		}

		if (!urlString.isBlank()) {
			sql = "SELECT `id`, `name`, `mobile`, `userid`, `tempids` FROM `send_msg_to` WHERE `isactive` = 1 AND `isdelete` = 0 AND FIND_IN_SET(?, tempids);";
			List<Map<String, Object>> mobileResult2 = jdbcTemplate.queryForList(sql, tempid);
			for (Map<String, Object> mobileRow2 : mobileResult2) {
				String mobile = String.valueOf(mobileRow2.get("mobile"));
				String finalUrl = urlString.replace("{{to}}", mobile);
				String response = sendWhatsAppMessage(finalUrl);
				System.out.println("WhatsApp response: " + response);
			}
		}

		return "success";
	}

	private String sendWhatsAppMessage(String urlString) {
		String response = "";
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			response = String.valueOf(responseCode);
			System.out.println("Response Code for URL: " + urlString + " is " + responseCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public String disablemessage(String msgid) {
		String sql = "update message_list set isdelete =1, isactive = 0 where id = ?";

		int rowsAffected = jdbcTemplate.update(sql, msgid);

		if (rowsAffected > 0) {
			return "success";
		} else {
			return "failed";
		}
	}

	public String enableMessage(String msgid) {
		String sql = "update message_list set isdelete =0, isactive = 1 where id = ?";

		int rowsAffected = jdbcTemplate.update(sql, msgid);

		if (rowsAffected > 0) {
			return "success";
		} else {
			return "failed";
		}
	}
}
