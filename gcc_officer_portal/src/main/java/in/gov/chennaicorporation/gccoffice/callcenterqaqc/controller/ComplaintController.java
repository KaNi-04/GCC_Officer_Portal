package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.ComplaintService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.http.*;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import org.springframework.web.multipart.MultipartFile;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Base64;

@CrossOrigin
@RestController
@RequestMapping("gcc/api/callcenterqaqc/pgrcomplaint")
public class ComplaintController {

	private final RestTemplate restTemplate;

	public ComplaintController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Autowired
	private ComplaintService complaintService;

	@GetMapping("/getComplaintCategories")
	public @ResponseBody String getComplaintCategories() {
		return complaintService.fetchComplaintCategories();
	}

	@GetMapping("/getComplaintSubTypes")
	public @ResponseBody String getComplaintSubTypes(@RequestParam String groupId) {
		return complaintService.fetchComplaintSubTypes(groupId);
	}

	@GetMapping("/getComplaintDetailsbyId")
	public List<Map<String, Object>> getComplaintDetailsById(@RequestParam String complaintid) {
		return complaintService.fetchComplaintDetailsById(complaintid);
	}

	@GetMapping("/getComplaintDetailsByMobile")
	public List<Map<String, Object>> getComplaintDetailsByMobile(@RequestParam String mobileNo) {
		return complaintService.fetchComplaintDetailsByMobile(mobileNo);
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerComplaint(
			@RequestParam(value = "ComplainantName", required = false) String complainantName,
			@RequestParam(value = "ComplainantAddr", required = false) String complainantAddr,
			@RequestParam(value = "MobileNo", required = false) String mobileNo,
			@RequestParam(value = "Email", required = false) String email,
			@RequestParam(value = "ComplaintType", required = false) String complaintType,
			@RequestParam(value = "ComplaintTitle", required = false) String complaintTitle,
			@RequestParam(value = "ComplaintDetails", required = false) String complaintDetails,
			@RequestParam(value = "StreetId", required = false) String streetId,
			@RequestParam(value = "Comp_Image", required = false) String compImage,
			@RequestParam(value = "latitude", required = false) String latitude,
			@RequestParam(value = "longtitude", required = false) String longtitude,
			@RequestParam(value = "Landmark", required = false) String landmark,
			@RequestParam(value = "gender", required = false) String gender) {

		String externalUrl = "https://erp.chennaicorporation.gov.in/pgr/external/mobileservice";

		try {
			// Create the URL object
			URL url = new URL(externalUrl);

			// Create the HttpURLConnection object
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true);

			// Create the parameters string, handling null values
			String params = "serviceId=RegComplaint" + "&ComplainantName="
					+ URLEncoder.encode(complainantName != null ? complainantName : "", "UTF-8") + "&ComplainantAddr="
					+ URLEncoder.encode(complainantAddr != null ? complainantAddr : "", "UTF-8") + "&MobileNo="
					+ URLEncoder.encode(mobileNo != null ? mobileNo : "", "UTF-8") + "&Email="
					+ URLEncoder.encode(email != null ? email : "", "UTF-8") + "&ComplaintType="
					+ URLEncoder.encode(complaintType != null ? complaintType : "", "UTF-8") + "&ComplaintTitle="
					+ URLEncoder.encode(complaintTitle != null ? complaintTitle : "", "UTF-8") + "&ComplaintDetails="
					+ URLEncoder.encode(complaintDetails != null ? complaintDetails : "", "UTF-8") + "&StreetId="
					+ URLEncoder.encode(streetId != null ? streetId : "", "UTF-8") + "&Comp_Image="
					+ URLEncoder.encode(compImage != null ? compImage : "", "UTF-8") + "&latitude="
					+ URLEncoder.encode(latitude != null ? latitude : "", "UTF-8") + "&longtitude="
					+ URLEncoder.encode(longtitude != null ? longtitude : "", "UTF-8") + "&Landmark="
					+ URLEncoder.encode(landmark != null ? landmark : "", "UTF-8") + "&gender="
					+ URLEncoder.encode(gender != null ? gender : "", "UTF-8") + "&compmode=2";
			// Write parameters to request body
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = params.getBytes("UTF-8");
				os.write(input, 0, input.length);
			}

			// Read the response
			int responseCode = conn.getResponseCode();
			BufferedReader reader;
			if (responseCode == HttpURLConnection.HTTP_OK) {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			// Return the response from the external API
			//return ResponseEntity.status(responseCode).body(response.toString());
			ObjectMapper mapper = new ObjectMapper();
	        Map<String, String> responseMap = mapper.readValue(response.toString(), Map.class);

	        String complaintNo = responseMap.getOrDefault("Complaint Number", "N/A");
	        //System.out.println("complaintNo===="+complaintNo);
	        
	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_JSON)
	                .body(Map.of("complaintNumber", complaintNo));

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing the request.");
		}
	}

	@PostMapping("/updateComplaint")
	public ResponseEntity<String> updateComplaint(@RequestParam("ComplaintId") String complaintId,
			@RequestParam(value = "Message", required = false) String message,
			@RequestParam(value = "Status") String status,
			@RequestPart(value = "Comp_Image", required = false) MultipartFile compImage) {

		String userType = "Public";

		//System.out.println("message:-" + message + "\n UserType:-" + userType + "\n Status:-" + status);
		//System.out.println(compImage == null);
		String externalUrl = "https://erp.chennaicorporation.gov.in/pgr/external/mobileservice";

		try {
			// Convert and compress the image to base64 if provided
			String base64Image = null;
			if (compImage != null && !compImage.isEmpty()) {
				base64Image = compressAndEncodeToBase64(compImage);
				//System.out.println("imageee" + base64Image);
			}

			// Construct the POST body with parameters
			String params = "serviceId=UpdateComplaint" + "&ComplaintId="
					+ URLEncoder.encode(complaintId != null ? complaintId : "", "UTF-8") + "&Message="
					+ URLEncoder.encode(message != null ? message : "", "UTF-8") + "&UserType="
					+ URLEncoder.encode(userType, "UTF-8") + "&Status="
					+ URLEncoder.encode(status != null ? status : "", "UTF-8") + "&Comp_Image="
					+ URLEncoder.encode(compImage != null ? base64Image : "", "UTF-8")
					+ "&Token=718333469976265947044293";
			// Open connection and send POST request
			URL url = new URL(externalUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// Send parameters
			OutputStream os = conn.getOutputStream();
			os.write(params.getBytes());
			os.flush();
			os.close();

			// Handle the response
			int responseCode = conn.getResponseCode();
			BufferedReader reader;
			if (responseCode == HttpURLConnection.HTTP_OK) {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			return ResponseEntity.status(responseCode).body(response.toString());

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing the request.");
		}
	}

	private String compressAndEncodeToBase64(MultipartFile file) throws IOException {
		// Convert MultipartFile to BufferedImage
		BufferedImage originalImage = ImageIO.read(file.getInputStream());

		// Resize/Compress the image
		int targetWidth = 800; // You can adjust the width as needed
		int targetHeight = (int) (originalImage.getHeight() * (800.0 / originalImage.getWidth())); // Preserve aspect
																									// ratio

		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
		g.dispose();

		// Compress the image by writing it to a ByteArrayOutputStream in JPEG format
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resizedImage, "jpg", baos); // You can change the format to "png" or "jpg"
		byte[] compressedBytes = baos.toByteArray();

		// Convert the compressed image to base64
		return Base64.getEncoder().encodeToString(compressedBytes);
	}
	
	////////////////////////////////////// PGR Check //////////////////////////////////////////
	

@GetMapping("/checktypeforduplicate")
	public ResponseEntity<?> getComplaintMatch(
	        @RequestParam("mobileNo") String mobileNo,
	        @RequestParam("complaintId") String complaintId,
	        @RequestParam("streetName") String streetName
	) {
	    try {
	        // 1. Map complaintId to complaint title
	    	Map<String, String> complaintIdToTitle = new HashMap<>();
	    	complaintIdToTitle.put("97", "Removal of Garbage");
	    	complaintIdToTitle.put("98", "Overflowing of Garbage Bin");
	    	complaintIdToTitle.put("99", "Removal of Debris");
	    	complaintIdToTitle.put("100", "Absenteeism of Sweepers");
	    	complaintIdToTitle.put("101", "Absenteeism of Door to door garbage Collector");
	    	complaintIdToTitle.put("102", "Improper Sweeping");
	    	complaintIdToTitle.put("103", "Provison of Garbage Bin");
	    	complaintIdToTitle.put("104", "Broken Bin");
	    	complaintIdToTitle.put("105", "Shifting of Garbage Bin");
	    	complaintIdToTitle.put("106", "Cleaning of Water Table");
	    	complaintIdToTitle.put("107", "Nuisance by Garbage Tractor/Truck");
	    	complaintIdToTitle.put("108", "Burning of Garbage");
	    	complaintIdToTitle.put("109", "Garbage Lorry without Net");
	    	complaintIdToTitle.put("110", "Spilling of Garbage from Lorry");
	    	complaintIdToTitle.put("111", "Burning of Garbage at Dumping Ground");
	    	complaintIdToTitle.put("112", "Pot hole fill up / Repairs to the damaged surface");
	    	complaintIdToTitle.put("113", "Relaying of Road");
	    	complaintIdToTitle.put("114", "Formation of New Road");
	    	complaintIdToTitle.put("115", "Repairs to existing Footpath");
	    	complaintIdToTitle.put("116", "Request to provide Footpath");
	    	complaintIdToTitle.put("117", "Removal of Shops in the Footpath");
	    	complaintIdToTitle.put("118", "Mosquito Menace");
	    	complaintIdToTitle.put("119", "Street Dogs");
	    	complaintIdToTitle.put("120", "Stray Cattle");
	    	complaintIdToTitle.put("121", "Stray Pigs");
	    	complaintIdToTitle.put("122", "Death of Stray Animals");
	    	complaintIdToTitle.put("123", "Public Health / Dengue / Malaria  / Gastro Enteritis");
	    	complaintIdToTitle.put("124", "Transfer Station Smell");
	    	complaintIdToTitle.put("125", "Flies Menace from Dumping Ground");
	    	complaintIdToTitle.put("126", "Open Defecation");
	    	complaintIdToTitle.put("127", "Illegal Draining of Sewage to SWD / Open Site");
	    	complaintIdToTitle.put("128", "Complaints regarding unhygenic Restaurants");
	    	complaintIdToTitle.put("129", "Complaints regarding quality of food in hotels");
	    	complaintIdToTitle.put("130", "Road Side Eateries");
	    	complaintIdToTitle.put("131", "Slaughter House related complaints");
	    	complaintIdToTitle.put("132", "Unauthorized Sale of Meat and Meat products");
	    	complaintIdToTitle.put("133", "Illegal Slaughtering");
	    	complaintIdToTitle.put("134", "Unhygenic and Improper Transport of Meat and Livestock");
	    	complaintIdToTitle.put("135", "Biomedical waste / Health hazard waste removal");
	    	complaintIdToTitle.put("136", "Complaints regarding Corporation Hospitals");
	    	complaintIdToTitle.put("137", "Complaints regarding CDH");
	    	complaintIdToTitle.put("138", "Stagnation of Water");
	    	complaintIdToTitle.put("139", "Obstruction of Water Flow");
	    	complaintIdToTitle.put("140", "Desilting of Drain");
	    	complaintIdToTitle.put("141", "Desilting of Canal");
	    	complaintIdToTitle.put("142", "Repairs to Storm Water Drain");
	    	complaintIdToTitle.put("143", "Covering  Manholes of Storm Water Drain");
	    	complaintIdToTitle.put("144", "New Drain Construction");
	    	complaintIdToTitle.put("145", "Disposal of Removed Silt on the Road");
	    	complaintIdToTitle.put("146", "Non burning of Street lights");
	    	complaintIdToTitle.put("147", "Electric shock due to street light");
	    	complaintIdToTitle.put("148", "Damage to the Electric pole");
	    	complaintIdToTitle.put("149", "New Street lights");
	    	complaintIdToTitle.put("150", "Shifting of Street light pole");
	    	complaintIdToTitle.put("151", "Overhead cable wires running in a haphazard manner");
	    	complaintIdToTitle.put("152", "Removal of Fallen Trees");
	    	complaintIdToTitle.put("153", "Complaints regarding Park");
	    	complaintIdToTitle.put("154", "Complaints regarding Playground");
	    	complaintIdToTitle.put("155", "Complaints regarding Centre Median");
	    	complaintIdToTitle.put("156", "Complaints regarding Traffic Island");
	    	complaintIdToTitle.put("157", "Unauthorized Tree Cutting");
	    	complaintIdToTitle.put("158", "Obstruction of Trees");
	    	complaintIdToTitle.put("159", "Complaints regarding Public Toilets");
	    	complaintIdToTitle.put("160", "Complaints regarding Free Usage of Public Toilets");
	    	complaintIdToTitle.put("161", "Complaints regarding Cleanliness of Toilets in Theatre");
	    	complaintIdToTitle.put("162", "Complaints regarding Cleanliness of Toilets in Shopping Complex");
	    	complaintIdToTitle.put("163", "Building Plan Sanction");
	    	complaintIdToTitle.put("164", "Violation of DCR/Building By laws");
	    	complaintIdToTitle.put("165", "Unauthorized / Illegal Construction");
	    	complaintIdToTitle.put("166", "Complaints related to Property Tax");
	    	complaintIdToTitle.put("167", "Complaints related to Professional Tax");
	    	complaintIdToTitle.put("168", "Complaints related to Trade Licence");
	    	complaintIdToTitle.put("174", "Name Error (Spelling Related)");
	    	complaintIdToTitle.put("175", "Change of Address in Electoral Roll");
	    	complaintIdToTitle.put("176", "Issue of Voter ID");
	    	complaintIdToTitle.put("177", "Inclusion, Deletion, Correction in Voter List");
	    	complaintIdToTitle.put("178", "Complaints regarding Voter List");
	    	complaintIdToTitle.put("179", "Name not found in the Electoral Roll");
	    	complaintIdToTitle.put("180", "Issue of Birth and Death Certificate");
	    	complaintIdToTitle.put("181", "Encroachment on the Public Property");
	    	complaintIdToTitle.put("182", " Complaints related to Schools");
	    	complaintIdToTitle.put("183", "Sanction of Financial Assistance under Moovalaur Thirumana Thittam");
	    	complaintIdToTitle.put("184", "Complaints related to issue of all types of Registration Certificate");
	    	complaintIdToTitle.put("185", "Slow Progress of Work");
	    	complaintIdToTitle.put("186", "Poor Quality of Work");
	    	complaintIdToTitle.put("187", "Unauthorized Advertisement Boards");
	    	complaintIdToTitle.put("188", "Complaints regarding Bridges / Flyovers / Subways");
	    	complaintIdToTitle.put("189", "Complaints related Shopping Complex");
	    	complaintIdToTitle.put("190", "Complaints regarding Community Hall");
	    	complaintIdToTitle.put("191", "Complaints regarding Burial Ground");
	    	complaintIdToTitle.put("192", "Complaints regarding any other CoC building");
	    	complaintIdToTitle.put("193", "Parking Issue");
	    	complaintIdToTitle.put("195", "Complaints Regarding Plastics");
	    	complaintIdToTitle.put("196", "No electricity in public toilet");
	    	complaintIdToTitle.put("197", "No water supply in public toilet");
	    	complaintIdToTitle.put("198", "Public toilet blockage");
	    	complaintIdToTitle.put("199", "Public toilet cleaning");
	    	complaintIdToTitle.put("200", "Burning of street light in daytime");
	    	complaintIdToTitle.put("201", "Illegal Parking on foot path");
	    	complaintIdToTitle.put("202", "Complaints regarding non availability of Doctors");
	    	complaintIdToTitle.put("203", "Complaints regarding non availability of medicines");
	    	complaintIdToTitle.put("204", "Complaints regarding laboratory issues");
	    	complaintIdToTitle.put("205", "Issues regarding amma baby care kits (After Delivery)");
	    	complaintIdToTitle.put("206", "Issues regarding Amma nutrition kit for pregnant woman");
	    	complaintIdToTitle.put("207", "Issues regarding Muthulakshmi Reddy Maternity Benefit Scheme (MRMBS)");
	    	complaintIdToTitle.put("208", "Issues regarding janani suraksha yojana scheme (JSY)");
	    	complaintIdToTitle.put("209", "Travel pass related complaints");
	    	complaintIdToTitle.put("210", "Food related complaints");
	    	complaintIdToTitle.put("212", "Covid Health Related Queries");
	    	complaintIdToTitle.put("213", "Hospital and Covid care centre(CCC) Related Queries");
	    	complaintIdToTitle.put("214", "Medical Emergencies");
	    	complaintIdToTitle.put("215", "Essentials and Medicines");
	    	complaintIdToTitle.put("216", "PSYCHO-SOCIO support");
	    	complaintIdToTitle.put("218", "Quarantine and isolation Related Queries");
	    	complaintIdToTitle.put("219", "General Health Queries");
	    	complaintIdToTitle.put("220", "Disinfection request");
	    	complaintIdToTitle.put("221", "Non Covid Health Related Queries");
	    	complaintIdToTitle.put("222", "Milling/Scraping of Road before Relaying of Road");
	    	complaintIdToTitle.put("226", "Food Requirement");
	    	complaintIdToTitle.put("227", "Relief Center Requirement");
	    	complaintIdToTitle.put("228", "Covid19 Ambulance Service");
	    	complaintIdToTitle.put("229", "Air Quality");
	    	complaintIdToTitle.put("230", "Online Payment Issue");
	    	complaintIdToTitle.put("231", "General Revision Objection");
	    	complaintIdToTitle.put("232", "Water Mixed with Sewarage");
	    	complaintIdToTitle.put("233", "Sewerage Overflow");
	    	complaintIdToTitle.put("234", "Drinking water supply");
	    	complaintIdToTitle.put("235", "Electricity Shock");
	    	complaintIdToTitle.put("236", "Electricity Unavailability");
	    	complaintIdToTitle.put("237", "Insufficient Barricading");
	    	complaintIdToTitle.put("238", "Fallen poles/towers/hoardings/other infra");
	    	complaintIdToTitle.put("239", "Fallen EB cables");
	    	complaintIdToTitle.put("240", "Project information not provided (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("241", "Project information not provided (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("242", "Project information not provided (MC Road)");
	    	complaintIdToTitle.put("243", "Project information not provided (Washermenpet Metro)");
	    	complaintIdToTitle.put("244", "Project information not provided (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("245", "Project information not provided (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("246", "Advance information on consultation not provided (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("247", "Advance information on consultation not provided (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("248", "Advance information on consultation not provided (MC Road)");
	    	complaintIdToTitle.put("249", "Advance information on consultation not provided (Washermenpet Metro)");
	    	complaintIdToTitle.put("250", "Advance information on consultation not provided (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("251", "Advance information on consultation not provided (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("252", "Location and area of consultation not provided (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("253", "Location and area of consultation not provided (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("254", "Location and area of consultation not provided (MC Road)");
	    	complaintIdToTitle.put("255", "Location and area of consultation not provided (Washermenpet Metro)");
	    	complaintIdToTitle.put("256", "Location and area of consultation not provided (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("257", "Location and area of consultation not provided (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("258", "Information on detailed work plan not provided (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("259", "Information on detailed work plan not provided (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("260", "Information on detailed work plan not provided (MC Road)");
	    	complaintIdToTitle.put("261", "Information on detailed work plan not provided (Washermenpet Metro)");
	    	complaintIdToTitle.put("262", "Information on detailed work plan not provided (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("263", "Information on detailed work plan not provided (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("264", "Others (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("265", "Others (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("266", "Others (MC Road)");
	    	complaintIdToTitle.put("267", "Others (Washermenpet Metro)");
	    	complaintIdToTitle.put("268", "Others (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("269", "Others (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("270", "No information in advance provided on the date and time of civil works (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("271", "No information in advance provided on the date and time of civil works (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("272", "No information in advance provided on the date and time of civil works (MC Road)");
	    	complaintIdToTitle.put("273", "No information in advance provided on the date and time of civil works (Washermenpet Metro)");
	    	complaintIdToTitle.put("274", "No information in advance provided on the date and time of civil works (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("275", "No information in advance provided on the date and time of civil works (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("276", "No information provided on the nature and schedule of civil work (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("277", "No information provided on the nature and schedule of civil work (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("278", "No information provided on the nature and schedule of civil work (MC Road)");
	    	complaintIdToTitle.put("279", "No information provided on the nature and schedule of civil work (Washermenpet Metro)");
	    	complaintIdToTitle.put("280", "No information provided on the nature and schedule of civil work (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("281", "No information provided on the nature and schedule of civil work (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("282", "No consultation carried out with the people affected by the civil works (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("283", "No consultation carried out with the people affected by the civil works (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("284", "No consultation carried out with the people affected by the civil works (MC Road)");
	    	complaintIdToTitle.put("285", "No consultation carried out with the people affected by the civil works (Washermenpet Metro)");
	    	complaintIdToTitle.put("286", "No consultation carried out with the people affected by the civil works (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("287", "No consultation carried out with the people affected by the civil works (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("288", "No arrangement made for temporary displacement (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("289", "No arrangement made for temporary displacement (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("290", "No arrangement made for temporary displacement (MC Road)");
	    	complaintIdToTitle.put("291", "No arrangement made for temporary displacement (Washermenpet Metro)");
	    	complaintIdToTitle.put("292", "No arrangement made for temporary displacement (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("293", "No arrangement made for temporary displacement (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("294", "No display board on the project installed at work site (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("295", "No display board on the project installed at work site (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("296", "No display board on the project installed at work site (MC Road)");
	    	complaintIdToTitle.put("297", "No display board on the project installed at work site (Washermenpet Metro)");
	    	complaintIdToTitle.put("298", "No display board on the project installed at work site (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("299", "No display board on the project installed at work site (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("300", "Temporary access to structures not provided and or not adequate (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("301", "Temporary access to structures not provided and or not adequate (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("302", "Temporary access to structures not provided and or not adequate (MC Road)");
	    	complaintIdToTitle.put("303", "Temporary access to structures not provided and or not adequate (Washermenpet Metro)");
	    	complaintIdToTitle.put("304", "Temporary access to structures not provided and or not adequate (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("305", "Temporary access to structures not provided and or not adequate (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("306", "No safety measures taken at worksite (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("307", "No safety measures taken at worksite (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("308", "No safety measures taken at worksite (MC Road)");
	    	complaintIdToTitle.put("309", "No safety measures taken at worksite (Washermenpet Metro)");
	    	complaintIdToTitle.put("310", "No safety measures taken at worksite (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("311", "No safety measures taken at worksite (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("312", "Water supply disrupted and alternate arrangement not provided (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("313", "Water supply disrupted and alternate arrangement not provided (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("314", "Water supply disrupted and alternate arrangement not provided (MC Road)");
	    	complaintIdToTitle.put("315", "Water supply disrupted and alternate arrangement not provided (Washermenpet Metro)");
	    	complaintIdToTitle.put("316", "Water supply disrupted and alternate arrangement not provided (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("317", "Water supply disrupted and alternate arrangement not provided (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("318", "Electricity supply disrupted and alternate arrangement not provided (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("319", "Electricity supply disrupted and alternate arrangement not provided (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("320", "Electricity supply disrupted and alternate arrangement not provided (MC Road)");
	    	complaintIdToTitle.put("321", "Electricity supply disrupted and alternate arrangement not provided (Washermenpet Metro)");
	    	complaintIdToTitle.put("322", "Electricity supply disrupted and alternate arrangement not provided (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("323", "Electricity supply disrupted and alternate arrangement not provided (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("324", "Disposal of debris not appropriately managed (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("325", "Disposal of debris not appropriately managed (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("326", "Disposal of debris not appropriately managed (MC Road)");
	    	complaintIdToTitle.put("327", "Disposal of debris not appropriately managed (Washermenpet Metro)");
	    	complaintIdToTitle.put("328", "Disposal of debris not appropriately managed (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("329", "Disposal of debris not appropriately managed (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("330", " Temporary arrangement for parking not provided (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("331", " Temporary arrangement for parking not provided (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("332", " Temporary arrangement for parking not provided (MC Road)");
	    	complaintIdToTitle.put("333", " Temporary arrangement for parking not provided (Washermenpet Metro)");
	    	complaintIdToTitle.put("334", " Temporary arrangement for parking not provided (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("335", " Temporary arrangement for parking not provided (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("336", "GRM and helpline numbers not displayed at worksite (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("337", "GRM and helpline numbers not displayed at worksite (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("338", "GRM and helpline numbers not displayed at worksite (MC Road)");
	    	complaintIdToTitle.put("339", "GRM and helpline numbers not displayed at worksite (Washermenpet Metro)");
	    	complaintIdToTitle.put("340", "GRM and helpline numbers not displayed at worksite (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("341", "GRM and helpline numbers not displayed at worksite (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("342", "Drinking water/ other basic needs for Labour not available at worksite (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("343", "Drinking water/ other basic needs for Labour not available at worksite (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("344", "Drinking water/ other basic needs for Labour not available at worksite (MC Road)");
	    	complaintIdToTitle.put("345", "Drinking water/ other basic needs for Labour not available at worksite (Washermenpet Metro)");
	    	complaintIdToTitle.put("346", "Drinking water/ other basic needs for Labour not available at worksite (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("347", "Drinking water/ other basic needs for Labour not available at worksite (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("348", "Noise levels are very high (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("349", "Noise levels are very high (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("350", "Noise levels are very high (MC Road)");
	    	complaintIdToTitle.put("351", "Noise levels are very high (Washermenpet Metro)");
	    	complaintIdToTitle.put("352", "Noise levels are very high (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("353", "Noise levels are very high (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("354", " Sprinkling of water at worksite not carried out (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("355", " Sprinkling of water at worksite not carried out (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("356", " Sprinkling of water at worksite not carried out (MC Road)");
	    	complaintIdToTitle.put("357", " Sprinkling of water at worksite not carried out (Washermenpet Metro)");
	    	complaintIdToTitle.put("358", " Sprinkling of water at worksite not carried out (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("359", " Sprinkling of water at worksite not carried out (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("360", "The worksite is not barricaded;  (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("361", "The worksite is not barricaded;  (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("362", "The worksite is not barricaded;  (MC Road)");
	    	complaintIdToTitle.put("363", "The worksite is not barricaded;  (Washermenpet Metro)");
	    	complaintIdToTitle.put("364", "The worksite is not barricaded;  (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("365", "The worksite is not barricaded;  (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("366", " No information on number of labour working at the site (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("367", " No information on number of labour working at the site (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("368", " No information on number of labour working at the site (MC Road)");
	    	complaintIdToTitle.put("369", " No information on number of labour working at the site (Washermenpet Metro)");
	    	complaintIdToTitle.put("370", " No information on number of labour working at the site (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("371", " No information on number of labour working at the site (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("372", "Labourer not using the safety equipment (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("373", "Labourer not using the safety equipment (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("374", "Labourer not using the safety equipment (MC Road)");
	    	complaintIdToTitle.put("375", "Labourer not using the safety equipment (Washermenpet Metro)");
	    	complaintIdToTitle.put("376", "Labourer not using the safety equipment (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("377", "Labourer not using the safety equipment (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("378", "Sewerage pipe is damaged (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("379", "Sewerage pipe is damaged (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("380", "Sewerage pipe is damaged (MC Road)");
	    	complaintIdToTitle.put("381", "Sewerage pipe is damaged (Washermenpet Metro)");
	    	complaintIdToTitle.put("382", "Sewerage pipe is damaged (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("383", "Sewerage pipe is damaged (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("384", "Others (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("385", "Others (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("386", "Others (MC Road)");
	    	complaintIdToTitle.put("387", "Others (Washermenpet Metro)");
	    	complaintIdToTitle.put("388", "Others (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("389", "Others (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("390", "Insufficient Barricading (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("391", "Insufficient Barricading (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("392", "Insufficient Barricading (MC Road)");
	    	complaintIdToTitle.put("393", "Insufficient Barricading (Washermenpet Metro)");
	    	complaintIdToTitle.put("394", "Insufficient Barricading (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("395", "Insufficient Barricading (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("396", "Non Removal of Debris (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("397", "Non Removal of Debris (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("398", "Non Removal of Debris (MC Road)");
	    	complaintIdToTitle.put("399", "Non Removal of Debris (Washermenpet Metro)");
	    	complaintIdToTitle.put("400", "Non Removal of Debris (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("401", "Non Removal of Debris (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("402", "Potholes fill up (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("403", "Potholes fill up (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("404", "Potholes fill up (MC Road)");
	    	complaintIdToTitle.put("405", "Potholes fill up (Washermenpet Metro)");
	    	complaintIdToTitle.put("406", "Potholes fill up (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("407", "Potholes fill up (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("408", "Damage to utilities (TNEB, CMWSSB, BSNL) (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("409", "Damage to utilities (TNEB, CMWSSB, BSNL) (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("410", "Damage to utilities (TNEB, CMWSSB, BSNL) (MC Road)");
	    	complaintIdToTitle.put("411", "Damage to utilities (TNEB, CMWSSB, BSNL) (Washermenpet Metro)");
	    	complaintIdToTitle.put("412", "Damage to utilities (TNEB, CMWSSB, BSNL) (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("413", "Damage to utilities (TNEB, CMWSSB, BSNL) (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("414", "Encroaching Public Space (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("415", "Encroaching Public Space (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("416", "Encroaching Public Space (MC Road)");
	    	complaintIdToTitle.put("417", "Encroaching Public Space (Washermenpet Metro)");
	    	complaintIdToTitle.put("418", "Encroaching Public Space (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("419", "Encroaching Public Space (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("420", "Need Pathway ramp (Thiruvotriyur High Road)");
	    	complaintIdToTitle.put("421", "Need Pathway ramp (Arunachaleshwar Road)");
	    	complaintIdToTitle.put("422", "Need Pathway ramp (MC Road)");
	    	complaintIdToTitle.put("423", "Need Pathway ramp (Washermenpet Metro)");
	    	complaintIdToTitle.put("424", "Need Pathway ramp (Khadar Nawas Khan Road)");
	    	complaintIdToTitle.put("425", "Need Pathway ramp (Race Course road + Guindy MMI)");
	    	complaintIdToTitle.put("426", "Cleanliness in Parks");
	    	complaintIdToTitle.put("427", "Greenary in Parks");
	    	complaintIdToTitle.put("428", "Play Equipment");
	    	complaintIdToTitle.put("429", "Opening and closing hours");
	    	complaintIdToTitle.put("430", "Toilets in parks");
	    	complaintIdToTitle.put("431", "Non functional lights/ no lights in parks");
	    	complaintIdToTitle.put("432", "Safety in parks/playgrounds");
	    	complaintIdToTitle.put("433", "Women related safety issues in parks");
	    	complaintIdToTitle.put("434", "Availability or cleanliness of dustbins in public toilets");
	    	complaintIdToTitle.put("435", "Cleanliness/ water supply in toilets");
	    	complaintIdToTitle.put("436", "Broken sinks, toilets or urinals and doors");
	    	complaintIdToTitle.put("437", "Request for new toilet");
	    	complaintIdToTitle.put("438", "Illegal activities in toilet");
	    	complaintIdToTitle.put("439", "Toilets not in use/ closed");
	    	complaintIdToTitle.put("440", "Inadequate lighting in and around the toilet");
	    	complaintIdToTitle.put("441", "Safety in public toilets");
	    	complaintIdToTitle.put("442", "Cleanliness in footpath");
	    	complaintIdToTitle.put("443", "Electrical wires/obstruction on footpath");
	    	complaintIdToTitle.put("444", "Unsafe dark spots/ corners");
	    	complaintIdToTitle.put("445", "Public defecation/urination");
	    	complaintIdToTitle.put("446", "Women related safety issues");
	    	complaintIdToTitle.put("447", "Inadequate light in a particular location or spot/ dark spots");

	    	 String expectedType;
	         if (complaintIdToTitle.containsKey(complaintId)) {
	             expectedType = complaintIdToTitle.get(complaintId).trim().toLowerCase();
	         } else {
	             // Fallback: Use sub-type text directly if not found in map (i.e. for "Others")
	             expectedType = complaintId.trim().toLowerCase();
	         }

	        // 2. Date range: last 7 days
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        LocalDate today = LocalDate.now();
	        String endDate = today.format(formatter);
	        String startDate = today.minusDays(7).format(formatter);
	        int repYear = today.getYear();

	        // 3. Construct external URL
	        String externalUrl = String.format(
	                "https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=getComplaintListWithImg" +
	                "&From_date=%s&To_date=%s&jsonResp=Yes&UserType=Public&Token=&PageIndex=null&Status=null&repYear=%d&MobileNo=%s",
	                startDate, endDate, repYear, mobileNo
	        );

	        System.out.println("External API URL: " + externalUrl);

	        // 4. Fetch and parse response
	        RestTemplate restTemplate = new RestTemplate();
	        String json = restTemplate.getForObject(externalUrl, String.class);
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode rootArray = mapper.readTree(json);

	        boolean found = false;

	        if (rootArray.isArray() && rootArray.size() > 0) {
	            JsonNode complaintList = rootArray.get(0).path("ComplaintListHistory");

	            if (complaintList.isArray()) {
	                for (JsonNode item : complaintList) {
	                    String type = item.path("complainttype").asText("").trim().toLowerCase();
	                    String street = item.path("Street").asText("").trim().toLowerCase();

	                    if (type.equals(expectedType) &&
	                        street.equals(streetName.trim().toLowerCase())) {
	                        found = true;
	                        break;
	                    }
	                }
	            }
	        }

	        // 5. Return response
	        Map<String, Object> response = new HashMap<>();
	        response.put("duplicate", found);
	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Failed to process the request"));
	    }
	}

}
