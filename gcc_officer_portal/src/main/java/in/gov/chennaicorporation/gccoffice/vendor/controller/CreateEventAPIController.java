package in.gov.chennaicorporation.gccoffice.vendor.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.vendor.service.createventService;
import in.gov.chennaicorporation.gccoffice.vendor.service.StreetVendorService;

@RequestMapping("/gcc/api/vending")
@RestController
public class CreateEventAPIController {
	
	 @Autowired
	 private createventService createventservices;
	 
	 @Autowired
	 private StreetVendorService StreetVendorService;
	 
//	 @Autowired
//	 private Environment environment;

	 @PostMapping("/save")
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

	         
	         String eventFilePath = StreetVendorService.fileUpload(file, "event_file");

	         if (eventFilePath == null) {
	             response.put("status", false);
	             response.put("message", "failed");
	             response.put("description", "File upload failed");
	             return ResponseEntity.internalServerError().body(response);
	         }

	         
	         String saveStatus = createventservices.insertEvent(eventName, eventDate, eventFilePath, cby);

	         if (saveStatus == null || saveStatus.equalsIgnoreCase("error")) {
	             response.put("status", false);
	             response.put("message", "failed");
	             response.put("description", "Failed to save event details");
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

	 
	 
	    //save file and get path
//	    private String saveFile(MultipartFile file) throws IOException {
//	    	
//	    	String uploadDirectory = environment.getProperty("file.upload.directory");
//	        
//	        String folderName = environment.getProperty("vending.foldername");
//	        	        
//	        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + file.getOriginalFilename();
//
//	        String folderPath = uploadDirectory + "/" + folderName + "/";
//	        
//	        
//	        Path dirPath = Paths.get(folderPath);
//	        if (!Files.exists(dirPath)) {
//	            Files.createDirectories(dirPath);
//	        }
//	               
//	        Path filePath = dirPath.resolve(fileName);
//	        
//	        Files.write(filePath, file.getBytes());
//	        
//	        return filePath.toAbsolutePath().toString();
//	    }

	    
	    @GetMapping("/dropdown-Event")
	    public ResponseEntity<List<Map<String, Object>>> ViewDropdownEvent() {
	        try {
	            List<Map<String, Object>> result = createventservices.ViewDropdownEvent();
	            return ResponseEntity.ok(result);
	        } catch (Exception e) {
	            Map<String, Object> error = new HashMap<>();
	            error.put("error", "Unable to fetch event list");
	            error.put("details", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body(Collections.singletonList(error));
	        }
	    }
	    
	    
	    @GetMapping("/committedropdown")
	    public List<Map<String, Object>> ViewCommitteeDropdownEvent() {
	       
	    	return createventservices.ViewCommitteeDropdownEvent();
	           	        
	    }
	    
	    @GetMapping("/getreports")
	    public List<Map<String, Object>> getreports(
	            @RequestParam(required = false) String meeting,
	            @RequestParam(required = false) String status,
	            @RequestParam(required = false) String zone,
	            @RequestParam(required = false) String ward,
	            @RequestParam String userId) {

	        List<Map<String, Object>> streetdb = StreetVendorService.getdataforreport(zone,ward); // BASE LIST
	        List<Map<String, Object>> vendingdb = createventservices.getdataforreport(meeting, status,userId); // FILTERED REQUEST LIST

	        // Convert vendingdb to map: vdid → vendor record
	        Map<Integer, Map<String, Object>> vendingMap = vendingdb.stream()
	                .filter(m -> m.get("vdid") != null)
	                .collect(Collectors.toMap(
	                        m -> Integer.parseInt(m.get("vdid").toString()),
	                        m -> m
	                ));

	        List<Map<String, Object>> finalList = new ArrayList<>();

	        // Merge logic
	        for (Map<String, Object> row : streetdb) {

	            int id = Integer.parseInt(row.get("id").toString());
	         
	            if (!vendingMap.containsKey(id)) {
	                continue;
	            }

	            // Merge map
	            Map<String, Object> merged = new HashMap<>(row);
	            Map<String, Object> vdata = vendingMap.get(id);

	            merged.put("status", vdata.get("status"));
	            merged.put("remarks", vdata.get("remarks"));
	            merged.put("uid_no", vdata.get("uid_no"));
	            merged.put("event_req_id", vdata.get("event_req_id"));
	            merged.put("cdate", vdata.get("cdate"));

	            finalList.add(merged);
	        }

	        return finalList;
	    }

	    @GetMapping("/finalvendorDetailsById")
	    public List<Map<String, Object>> finalvendorDetailsById(String requestId){
	    	
	    	return createventservices.finalvendorDetailsById(requestId);
	    }

	    
}
