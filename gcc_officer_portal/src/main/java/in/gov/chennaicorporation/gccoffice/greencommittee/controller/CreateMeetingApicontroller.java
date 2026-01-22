package in.gov.chennaicorporation.gccoffice.greencommittee.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.greencommittee.service.CreateMeetingService;

@RestController
@RequestMapping("/gcc/api/greencommittee/createmeeting")
public class CreateMeetingApicontroller {

	@Autowired
	private  AppConfig appConfig;
	
	@Autowired
	private CreateMeetingService createMeetingService;
	
	
	
	@PostMapping("/savemeeting")
	 public ResponseEntity<Map<String, Object>> saveEventWithFile(
	         @RequestParam("eventName") String eventName,
	         @RequestParam("eventDate") String eventDateStr,
	         @RequestParam("file") MultipartFile file,
	         @RequestParam("cby") String cby) {

	     Map<String, Object> response = new HashMap<>();

	     try {
	        
	         if (file == null || file.isEmpty()) {
	             response.put("status", false);
	             response.put("message", "failed");
	             response.put("description", "File is required");
	             return ResponseEntity.badRequest().body(response);
	         }

	         if (eventName == null || eventName.trim().isEmpty()) {
	             response.put("status", false);
	             response.put("message", "failed");
	             response.put("description", "Event name cannot be empty");
	             return ResponseEntity.badRequest().body(response);
	         }
	         
	         LocalDate eventDate;
	         try {
	             eventDate = LocalDate.parse(eventDateStr);
	         } catch (Exception ex) {
	             response.put("status", false);
	             response.put("message", "failed");
	             response.put("description", "Invalid date format (use yyyy-MM-dd)");
	             return ResponseEntity.badRequest().body(response);
	         }

	         
	         String eventFilePath = createMeetingService.fileUpload(file, "meeting_file");

	         if (eventFilePath == null) {
	             response.put("status", false);
	             response.put("message", "failed");
	             response.put("description", "File upload failed");
	             return ResponseEntity.internalServerError().body(response);
	         }

	         
	         String saveStatus = createMeetingService.insertEvent(eventName, eventDate, eventFilePath, cby);

	         if (saveStatus == null || saveStatus.equalsIgnoreCase("error")) {
	             response.put("status", false);
	             response.put("message", "failed");
	             response.put("description", "Failed to save meeting details");
	             return ResponseEntity.internalServerError().body(response);
	         }

	         
	         response.put("status", true);
	         response.put("message", "success");
	         response.put("description", "Details saved successfully");
	         return ResponseEntity.ok(response);

	     } catch (Exception e) {
	         e.printStackTrace();

	         response.put("status", false);
	         response.put("message", "failed");
	         response.put("description", "Internal server error: " + e.getMessage());
	         return ResponseEntity.internalServerError().body(response);
	     }
	 }
}
