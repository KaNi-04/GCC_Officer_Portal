package in.gov.chennaicorporation.gccoffice.ward_shaba.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.com_petition.data.ComplaintType;
import in.gov.chennaicorporation.gccoffice.service.Base64Util;
import in.gov.chennaicorporation.gccoffice.service.GetMappingUserDetails;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;
import in.gov.chennaicorporation.gccoffice.controller.DateTimeUtil;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@Service("wardSabhaPetition")
public class Petition {
	private JdbcTemplate jdbcTemplate;
	private final Environment environment;
//	private PetitionMaster petitionMaster;
	private GetMappingUserDetails getMappingUserDetails;

	@Autowired
	public Petition(Environment environment,
			GetMappingUserDetails getMappingUserDetails) {
		this.environment = environment;
//		this.petitionMaster = petitionMaster;
		this.getMappingUserDetails = getMappingUserDetails;
	}

	@Autowired
	public void setDataSource(@Qualifier("mysqlWardShabaDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Map<String, Object> savePetition(String zone, String ward, String complaintNature, String minutesid)
			throws DataAccessException {
		final int eventid; // Declare as final

		if (minutesid != null && !minutesid.trim().isEmpty()) {
			try {
				eventid = Integer.parseInt(minutesid);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid event ID: " + minutesid);
			}
		} else {
			eventid = 0; // Assign default value
		}

		// Format current date as yyyy/MM/dd
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String currentDate = sdf.format(new Date());

		// Get the latest number for today's minutes_no
		String latestNoQuery = "SELECT COALESCE(MAX(CAST(SUBSTRING_INDEX(minutes_no, '/', -1) AS UNSIGNED)), 0) FROM minutes_list WHERE minutes_no LIKE ?";
		Integer lastNumber = jdbcTemplate.queryForObject(latestNoQuery, new Object[] { currentDate + "/%" },
				Integer.class);

		int nextNumber = (lastNumber != null) ? lastNumber + 1 : 1;
		String minutesNo = currentDate + "/" + nextNumber; // Example: 2024/07/30/1
		System.out.println("minutes-no:" + minutesNo);
		// Insert into DB
		String sqlQuery = "INSERT INTO minutes_list (zone, ward, minutes_details, minutes_id, minutes_no) VALUES (?, ?, ?, ?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, zone);
			ps.setString(2, ward);
			ps.setString(3, complaintNature);
			ps.setInt(4, eventid); // Now eventid is final
			ps.setString(5, minutesNo);
			return ps;
		}, keyHolder);

		// Get the inserted ID
		int generatedId = keyHolder.getKey().intValue();

		// Prepare response
		Map<String, Object> response = new HashMap<>();
		response.put("minutes_id", generatedId);
		response.put("minutes_no", minutesNo);
		return response;
	}

	@Transactional
	public boolean updatePetitionNumber(int petitionId, String petitionNoString) {
		String sqlQuery = "UPDATE `petition` SET `petition_no`='" + petitionNoString + "' WHERE `petition_id`='"
				+ petitionId + "'";
		jdbcTemplate.update(sqlQuery);
		return true;
	}

	@Transactional
	public String addPetitionFile(String createdBy, int petitionId, String petitionNoString, MultipartFile page1,
			MultipartFile page2, MultipartFile page3, MultipartFile page4) {

		String fileName = "";
		for (int i = 1; i <= 4; i++) {
			MultipartFile file = null;
			switch (i) {
			case 1:
				file = page1;
				fileName = "Scan1";
				break;
			case 2:
				file = page2;
				fileName = "Scan2";
				break;
			case 3:
				file = page3;
				fileName = "Scan3";
				break;
			case 4:
				file = page4;
				fileName = "Scan4";
				break;
			}
			if (file != null && !file.isEmpty()) {
				try {
					// Get the bytes of the file
					byte[] bytes = file.getBytes();

					// Set the file path where you want to save it
					String uploadDirectory = environment.getProperty("file.upload.directory");
					String serviceFolderName = environment.getProperty("zh_foldername");
					uploadDirectory = uploadDirectory + serviceFolderName;

					// File name
					fileName = petitionId + "_" + fileName + "_" + petitionNoString + "_" + file.getOriginalFilename();

					// File Size
					Long fileSize = file.getSize();

					// File Type
					String fileType = file.getContentType();

					// Create directory if it doesn't exist
					Path directoryPath = Paths.get(uploadDirectory);
					if (!Files.exists(directoryPath)) {
						Files.createDirectories(directoryPath);
					}

					String filePath = uploadDirectory + "/" + fileName;

					// Create a new Path object
					Path path = Paths.get(filePath);

					// Write the bytes to the file
					Files.write(path, bytes);

					// Save the file path to a database
					String sqlQuery = "INSERT INTO `pentition_files`(`created_by`, `file_name`, `file_size`, `file_type`, `petition_no`, `petition_id`) "
							+ "VALUES ('" + createdBy + "','" + fileName + "','" + fileSize + "','" + fileType + "','"
							+ petitionNoString + "','" + petitionId + "')";
					jdbcTemplate.update(sqlQuery);

				} catch (IOException e) {
					e.printStackTrace();
					return "Failed to save file " + file.getOriginalFilename();
				}
			}

		}
		return "success";
	}

	public Map<String, Object> saveMinutes(String minutesDate, String minutesName, String attachmentPath, String ward) {
		String sql = "INSERT INTO ward_minutes (minutes_date, minutes_name, is_active, is_delete,minutes_img, ward) VALUES (?, ?, 1, 0,?,?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, minutesDate);
			ps.setString(2, minutesName);
			ps.setString(3, attachmentPath);
			ps.setString(4, ward);
			return ps;
		}, keyHolder);

		// Prepare response data
		Map<String, Object> response = new HashMap<>();

		if (keyHolder.getKey() != null) {
			response.put("minutesId", keyHolder.getKey().intValue());
			response.put("minutesDate", minutesDate);
			response.put("minutesName", minutesName);
			response.put("ward", ward);
			response.put("message", "Event saved successfully!");
		}

		return response;
	}

	public List<Map<String, Object>> getAllMinutesNames(String ward) {
		String query = "SELECT minutes_id, minutes_name FROM ward_minutes WHERE is_active = 1 AND is_delete = 0 AND `ward`=?";
		return jdbcTemplate.queryForList(query,ward);
	}
	
	public List<Map<String, Object>> getUserWard(String userid) {
		String query = "SELECT `ward` FROM `gcc_apps`.`user_zone_ward_list` WHERE `isactive` = 1 AND `appusers_userid` = ? LIMIT 1";
		return jdbcTemplate.queryForList(query,userid);
	}

	public List<Map<String, Object>> getMinutesDetailsById(int minutesId, String ward) {
		/*String query = "SELECT ml.minutes_id,ml.ref_img,ml.minutes_no, ml.minutes_details, DATE_FORMAT(ml.created_date, '%d-%m-%Y') AS created_date, "
				+ "ml.remarks, wm.minutes_name,wm.minutes_img " + "FROM minutes_list ml "
				+ "LEFT JOIN ward_minutes wm ON ml.minutes_id = wm.minutes_id " + "WHERE ml.minutes_id = ?";*/
		String query ="SELECT ml.minutes_id,ml.ref_img,ml.minutes_no, ml.minutes_details, DATE_FORMAT(ml.created_date, '%d-%m-%Y') AS created_date, "
				+ "ml.remarks, wm.minutes_name,wm.minutes_img FROM ward_minutes wm "
				+ "LEFT JOIN minutes_list ml ON wm.minutes_id = ml.minutes_id WHERE wm.minutes_id = ? AND wm.ward=?";

		return jdbcTemplate.queryForList(query, minutesId, ward);
	}

	// Method to save file and return path
	public String saveFile(MultipartFile file, String fileCategory) throws IOException {
		if (file == null || file.isEmpty()) {
			return ""; // Return empty if no file uploaded
		}

		// Define upload directory
		String uploadDirectory = environment.getProperty("file.upload.directory");
		String serviceFolder = environment.getProperty("minutes_files");

		var year =DateTimeUtil.getCurrentYear();
        var month =DateTimeUtil.getCurrentMonth();
        var date =DateTimeUtil.getCurrentDay();

		uploadDirectory = uploadDirectory + serviceFolder + "/" + year + "/" + month;

		// String fullUploadPath = uploadDirectory + "/" + serviceFolder + "/" + year +
		// "/" + month;

		// Create directory if it does not exist
		Path directoryPath = Paths.get(uploadDirectory);
		if (!Files.exists(directoryPath)) {
			Files.createDirectories(directoryPath);
		}

		String fileName = fileCategory + "minutes_file" + file.getOriginalFilename();
		// Construct the file path
		String filepath = uploadDirectory + "/" + fileName;
		String filepath_txt = "/" + serviceFolder + "/" + year + "/" + month + "/" + fileName;

		Path path = Paths.get(filepath);

		// Ensure the directory exists
		Files.createDirectories(path.getParent());

		// Write the file to the specified path
		Files.write(path, file.getBytes());

		// Save file to disk
		Files.write(Paths.get(filepath), file.getBytes());

		return filepath_txt; // Return the relative path to be stored in DB
	}

	public void saveUpdateMinutes1(String minutesNo, String replyMessage, String documentPath) {
		String sql = "UPDATE minutes_list SET remarks = ?, ref_img = ? WHERE minutes_no = ?";
		jdbcTemplate.update(sql, replyMessage, documentPath, minutesNo);
	}

	public List<Map<String, Object>> getAllEvents(String ward) {
		String sql = "SELECT CASE WHEN COUNT(ml.minutes_id) > 0 THEN COUNT(ml.minutes_id) ELSE 0 "
				+ "	END AS minutes_count, wm.minutes_id, wm.minutes_date, wm.minutes_name, wm.minutes_img  "
				+ "	FROM ward_minutes wm " + "	LEFT JOIN minutes_list ml ON wm.minutes_id = ml.minutes_id  "
				+ "	WHERE wm.is_active = 1 AND wm.is_delete = 0 AND wm.`ward`=?"
				+ "	 GROUP BY wm.minutes_id, wm.minutes_date, wm.minutes_name, wm.minutes_img";

		List<Map<String, Object>> events = jdbcTemplate.queryForList(sql,ward);

		// Ensure correct image path formatting
		events.forEach(event -> {
			if (event.get("minutes_img") != null) {
				String imagePath = event.get("minutes_img").toString();
				// Remove extra slashes
				imagePath = imagePath.replaceAll("/{2,}", "/");
				event.put("minutes_img", imagePath);
			}
		});

		return events;
	}
}
