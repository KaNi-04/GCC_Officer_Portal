package in.gov.chennaicorporation.gccoffice.ward_shaba.controller;

import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;
import in.gov.chennaicorporation.gccoffice.ward_shaba.service.Petition;

@RequestMapping("gcc/api/ward_shaba")
@RestController("WardShabaRest") // Mayor Petition Rest
public class APIController {

	private Petition petition;

	@Autowired
	public APIController(Petition petition) {
		this.petition = petition;
	}

	@PostMapping("/saveevent")
	public ResponseEntity<Map<String, Object>> saveMinutes(
			@RequestParam("minutesDate") String minutesDate,
			@RequestParam("minutesName") String minutesName,
			@RequestParam("ward") String ward,
			@RequestParam(value = "attachment", required = false) MultipartFile attachment) {

		try {
			// Save file if present and get the file path
			String attachmentPath = attachment != null && !attachment.isEmpty()
					? petition.saveFile(attachment, "minutes")
					: ""; // Empty if no file uploaded

			// Save event details
			Map<String, Object> result = petition.saveMinutes(minutesDate, minutesName, attachmentPath,ward);

			if (result.containsKey("minutesId")) {
				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(Map.of("message", "Failed to save event"));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "Error: " + e.getMessage()));
		}
	}

	@GetMapping("/getAllEvents")
	public ResponseEntity<List<Map<String, Object>>> getAllEvents(@RequestParam("ward") String ward) {
		List<Map<String, Object>> events = petition.getAllEvents(ward);
		return ResponseEntity.ok(events);
	}

	@PostMapping(value = "/savePetition", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> savePetition(@RequestParam(value = "zone", required = false) String zone,
			@RequestParam(value = "ward", required = false) String ward,
			@RequestParam(value = "complaintNature", required = false) String complaintNature,
			@RequestParam(value = "eventid", required = false) String minutesid) {

		System.out.println("Received Data - Zone: " + zone + ", Ward: " + ward + ", Complaint: " + complaintNature
				+ ", Event ID: " + minutesid);

		try {
			Map<String, Object> result = petition.savePetition(zone, ward, complaintNature, minutesid);

			// Extract minutes_no and minutes_id from the result
			Integer minutesId = (Integer) result.get("minutes_id");
			String minutesNo = (String) result.get("minutes_no");

			if (minutesId != null && minutesId > 0 && minutesNo != null) {
				return ResponseEntity.ok(result); // ✅ Send JSON response
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(Collections.singletonMap("error", "Failed to save minutes"));
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	@GetMapping("/getMinutesNames")
	public ResponseEntity<List<Map<String, Object>>> getMinutesNames(@RequestParam("ward") String ward) {
		List<Map<String, Object>> minutesList = petition.getAllMinutesNames(ward);
		return ResponseEntity.ok(minutesList);
	}

	@GetMapping("/getMinutesDetails")
	public ResponseEntity<?> getMinutesDetails(
			@RequestParam("minutesId") int minutesId,
			@RequestParam("ward") String ward) {
		System.out.println("Fetching details for minutesId: " + minutesId);

		List<Map<String, Object>> detailsList = petition.getMinutesDetailsById(minutesId,ward);

		if (detailsList == null || detailsList.isEmpty()) {
			System.out.println("No details found for minutesId: " + minutesId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data found");
		}

		System.out.println("Data found: " + detailsList);
		return ResponseEntity.ok(detailsList);
	}

	@PostMapping("/saveComplaintUpdate")
	public ResponseEntity<String> saveMinutes1(@RequestParam("minutesNo") String minutesNo,
			@RequestParam("replyMessage") String replyMessage,
			@RequestParam(value = "document", required = false) MultipartFile document) {

		try {
			System.out.println("minutesNo = " + minutesNo);
			System.out.println("replyMessage = " + replyMessage);
			System.out.println("document = " + document);
			String documentPath = petition.saveFile(document, "minutes");
			petition.saveUpdateMinutes1(minutesNo, replyMessage, documentPath);
			return ResponseEntity.ok("Minutes details saved successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

	@GetMapping("/get-file-size")
	public @ResponseBody FileSizeResponse getFileSize(@RequestParam String filePath) {
		try {
			// Convert to absolute path
			// String filepath1=""+filePath;
			File file = new File(filePath);
			Path path = file.toPath();

			// Debug log
			System.out.println("Checking file size for: " + path.toString());

			if (!Files.exists(path)) {
				System.out.println("File does not exist: " + path);
				return new FileSizeResponse(-1, "File not found");
			}

			float size = Files.size(path);
			String readableSize = formatFileSize(size);
			return new FileSizeResponse(size, readableSize);
		} catch (IOException e) {
			e.printStackTrace();
			return new FileSizeResponse(-1, "Error reading file");
		}
	}

	// Method to format file size in KB, MB, GB
	private String formatFileSize(float size) {
		if (size <= 0)
			return "N/A";
		String[] units = { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	// Response DTO
	public static class FileSizeResponse {
		private float size;
		private String readableSize;

		public FileSizeResponse(float size, String readableSize) {
			this.size = size;
			this.readableSize = readableSize;
		}

		public float getSize() {
			return size;
		}

		public String getReadableSize() {
			return readableSize;
		}
	}

}
