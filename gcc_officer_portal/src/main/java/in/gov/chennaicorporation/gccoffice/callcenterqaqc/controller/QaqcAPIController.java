package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.FollowUpService;
import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;



@RequestMapping("gcc/api/callcenterqaqc")
@RestController("GrivanceQCapiController")

public class QaqcAPIController {
	
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	
	
	@Autowired  
    public QaqcAPIController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

	@Autowired
    private QaqcService qaqcService;
	
	@Autowired
	private FollowUpService followUpService;

 
    
    @GetMapping("/getComplaintDetails/{complaintNumber}")
    public Map<String, Object> getComplaintDetails(
            @PathVariable String complaintNumber,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String compmode,
            @RequestParam(required = false) String deptname) {

        Map<String, Object> resultMap = new HashMap<>();

        try {
            String url = "https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=getComplaintByID&ComplaintId=" 
                + complaintNumber + "&imgUrlonly=yes";
            System.out.println("Request URL: " + url);

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String responseBody = responseEntity.getBody();
                //System.out.println("Response Body: " + responseBody);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(responseBody);

                JsonNode complaintDetails = rootNode.path(0).path("ComplaintListHistory").path(0);
                
                String registerDate=complaintDetails.path("complaintopendate").asText("N/A");
                //System.out.println("registerdate====="+registerDate);
                
                String formattedDate = "N/A"; // Default value if parsing fails

                if (!registerDate.equals("N/A") && !registerDate.isEmpty()) {
                    try {
                        // Parse the input date string
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                        Date date = inputFormat.parse(registerDate);

                        // Format the date to dd-MM-yyyy
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        formattedDate = outputFormat.format(date);
                    } catch (Exception e) {
                        e.printStackTrace(); // Log exception for debugging
                    }
                }

                if (!complaintDetails.isMissingNode()) {
                    // Existing fields
                    resultMap.put("complaintid", complaintDetails.path("complaintid").asText("N/A"));
                    resultMap.put("complaintdate", formattedDate);
                    resultMap.put("complainantName", complaintDetails.path("CustomerName").asText("N/A"));
                    resultMap.put("mobileNumber", complaintDetails.path("CustomerMobile").asText("N/A"));
                    resultMap.put("complaintType", complaintDetails.path("complainttype").asText("N/A"));
                    resultMap.put("description", complaintDetails.path("complaintdetails").asText("N/A"));
                    resultMap.put("currentStatus", complaintDetails.path("complaintcurrentstatus").asText("N/A"));
                    resultMap.put("Street", complaintDetails.path("Street").asText("N/A"));
                    resultMap.put("Area", complaintDetails.path("Area").asText("N/A"));
                    resultMap.put("Landmark", complaintDetails.path("Landmark").asText("N/A"));
                    resultMap.put("Location", complaintDetails.path("Location").asText("N/A"));
                    resultMap.put("Zone", complaintDetails.path("Zone").asText("N/A"));
                    resultMap.put("Division", complaintDetails.path("Division").asText("N/A"));
                    resultMap.put("OfficialMobile", complaintDetails.path("OfficialMobile").asText("N/A"));
                    resultMap.put("gender", gender != null ? gender : complaintDetails.path("GENDER").asText("N/A"));
                    resultMap.put("deptname", deptname != null ? deptname : complaintDetails.path("DEPT_NAME").asText("N/A"));
                    resultMap.put("compmode", compmode != null ? compmode : complaintDetails.path("COMP_MODE").asText("N/A"));

                 // Comments section
                    JsonNode comments = complaintDetails.path("Comments");
                    if (!comments.isMissingNode()) {
                        List<Map<String, Object>> commentList = new ArrayList<>();
                        for (JsonNode commentNode : comments) {
                            Map<String, Object> commentData = new HashMap<>();
                            
                            // Extracting comment details
                            commentData.put("user", commentNode.path("user").asText("N/A"));
                            commentData.put("message", commentNode.path("message").asText("N/A"));
                            commentData.put("comment_date", commentNode.path("comment_date").asText("N/A"));
                            
                            // Handling media link (Image) as part of userimages array for each comment
                            JsonNode userImages = commentNode.path("userimages");
                            if (!userImages.isMissingNode() && userImages.isArray() && userImages.size() > 0) {
                                String mediaLink = userImages.get(0).path("Comp_Image").asText(null);
                                // If media link is valid, add it to the comment data
                                if (mediaLink != null && !mediaLink.equals("null") && !mediaLink.isEmpty()) {
                                    commentData.put("Comp_Image", mediaLink);
                                } else {
                                    commentData.put("Comp_Image", "No image available");
                                }
                            } else {
                                commentData.put("Comp_Image", "No image available");
                            }

                            // Add the comment data to the list
                            commentList.add(commentData);
                        }
                        resultMap.put("comments", commentList);
                    }


                 // Handling media link (Image) as part of userimages array
                    String mediaLink = complaintDetails.path("mediaLink").asText(null);
                    List<Map<String, String>> userImagesList = new ArrayList<>();
                    Map<String, String> imageMap = new HashMap<>();
                    imageMap.put("Comp_Image", mediaLink != null && !mediaLink.equals("null") ? mediaLink : null);
                    userImagesList.add(imageMap);
                    resultMap.put("userimages", userImagesList);


                   // System.out.println("Image URL from API: " + mediaLink);
                }
            } else {
                resultMap.put("error", "Failed to retrieve complaint details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("error", "Error processing request");
        }

        return resultMap;
    }
    
 
    @GetMapping("/followuplist")
    @ResponseBody
    public List<Map<String, Object>> getFollowUpList(
            @RequestParam(value = "callStatus", required = false) String callStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {

        // Set default 'toDate' to a future date (1 year ahead) if not provided
        if (toDate == null) {
            toDate = LocalDate.now().plusMonths(1); // Set to one year ahead or any other future date
           //toDate= qaqcService.getMaxRemainderdate();
        }

        // Set default 'fromDate' to the start of the current month if not provided
        if (fromDate == null) {
            fromDate = LocalDate.now().withDayOfMonth(1);  // Start from the first day of the current month
        }

        // Print debug info
//        System.out.println("Received callStatus: " + callStatus);
//        System.out.println("From Date: " + fromDate);
//        System.out.println("To Date: " + toDate);

        // Call the service to fetch the filtered data based on the status and date range
        return qaqcService.getFollowUpDetailsByStatus(callStatus, fromDate, toDate);
    }

    
    @GetMapping("/followup/todayCount")
    public ResponseEntity<Map<String, Long>> getTodayFollowUpCount(@RequestParam String followupStatus) {
        LocalDate today = LocalDate.now(); // Get the current date
        Map<String, Long> counts = qaqcService.countFollowUpsByDateAndStatus(today, followupStatus);
        
        return ResponseEntity.ok(counts);  // Return the count in the response
    }
    
    @GetMapping("/callhistory")
    public ResponseEntity<List<Map<String, Object>>> getCallHistory(@RequestParam String complaintNumber) {
        List<Map<String, Object>> callHistory = followUpService.getCallHistory(complaintNumber);
        if (callHistory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }
        return ResponseEntity.ok(callHistory);
    }
    
    @GetMapping("/getfilterreport")
    public Map<String, Object> getFilterReports(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String status,
            @RequestParam String group,
            @RequestParam String type,
            @RequestParam String mode,
            @RequestParam String zone,
            @RequestParam String region) {
		Map<String, Object> response = new HashMap<>();

		try {
			
        LocalDate fromLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate toLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        String formattedFromDate = fromLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String formattedToDate = toLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        String url="https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=ms_dashboard_details&From_date="+formattedFromDate+"&To_date="+formattedToDate+"&jsonResp=Yes&Status="+status+"&isQcuser=Yes"+"&ComplaintType="+type+"&ComplaintGroupId="+group+"&compmode="+mode+"&Zoneid="+zone+"&RegionId="+region;
		System.out.println(url);
		
		RestTemplate restTemplate = new RestTemplate();

		// Fetch response as String
		ResponseEntity<String> responses = restTemplate.getForEntity(url, String.class);
		String rawResponse = responses.getBody();

		// Preprocess response to remove invalid characters if needed
		if (rawResponse != null) {
			//rawResponse = rawResponse.replaceAll("\"\"", "\""); // Replace double-double quotes
			//rawResponse = rawResponse.replaceAll("(?<![,{])\"R", "\"R"); // Example: Fix missing commas
		}
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> responseMap = objectMapper.readValue(rawResponse, Map.class);
		List<Map<String, Object>> details = (List<Map<String, Object>>) responseMap.get("Details");
        //System.out.println(details);
        if (details == null) {
        	response.put("status", "nodata");
            response.put("message", "No Data, please change criteria");
        }
        else {
        	response.put("status", "success");
            response.put("details", details);
        }
        
		}
		catch (Exception e) {
			response.put("status", "error");
	        response.put("message", "An error occurred while fetching data.");
	        e.printStackTrace();
		}
        
        return response;
    }
    
    @GetMapping("/getunattendedcount")
    public Map<String, Object> GetUnattendedComplaints(@RequestParam int ucount) 
    {
        Map<String, Object> response = new HashMap<>();

        try {
            // Logging input parameters
            //System.out.println("Received Data:   " + ucount);

            // Fetch unattended complaints
            List<Map<String, Object>> details = qaqcService.getUnattendedlist(ucount);

            if (details != null && !details.isEmpty()) {
                response.put("status", "success");
                response.put("details", details);
            } else {
                response.put("status", "nodata");
                response.put("message", "No unattended complaints found for the given criteria.");
            }
        } catch (Exception e) {
            System.err.println("Error while fetching unattended complaints: " + e.getMessage());
            e.printStackTrace(); // Log the complete stack trace for debugging

            response.put("status", "error");
            response.put("message", "An error occurred while fetching unattended complaints. Please try again.");
        }

        return response;
    }

    @PostMapping("/closingunattendedcomplaints")
    public ResponseEntity<Map<String, Object>> closeUnattendedComplaints(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract values from request map
            String userId = (String) request.get("userId");
            String remarks = (String) request.get("remarks");
            List<String> complaintNumbers = (List<String>) request.get("complaintNumbers");

            if (userId == null || remarks == null || complaintNumbers == null || complaintNumbers.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Invalid request data.");
                return ResponseEntity.badRequest().body(response);
            }

            // Get ERP Username
            String erp_name = qaqcService.getErpusernamebyId(userId);
            //System.out.println("ERP Name: " + erp_name);

            List<String> completedComplaints = new ArrayList<>();
            List<String> failedComplaints = new ArrayList<>();

            // Process each complaint number
            for (String complaintNumber : complaintNumbers) {
                boolean isSuccess = qaqcService.ChangeStatusInErp(complaintNumber, erp_name, remarks);
                if (isSuccess) {
                    completedComplaints.add(complaintNumber);
                } else {
                    failedComplaints.add(complaintNumber);
                }
            }
                       
            int updated=qaqcService.UpdateInHistoryTable(completedComplaints);
            //System.out.println("Updated rows in history table: " + updated);
            
            int updateInlogs=qaqcService.insertInLogs(completedComplaints,remarks,userId);
            //System.out.println("Updated rows in logs table: " + updateInlogs);
            
            //System.out.println("failedComplaints===="+failedComplaints);

            // Response handling
            if (complaintNumbers.size() == completedComplaints.size()) {
                response.put("status", "success");
                response.put("message", "All complaints Completed successfully.");
                response.put("completedCount", completedComplaints.size());
            } else if (!completedComplaints.isEmpty()) {
                response.put("status", "partial_success");
                response.put("message", "Some complaints failed to process.");
                response.put("completedCount", completedComplaints.size());
                response.put("failedCount", failedComplaints.size());
                response.put("failedComplaints", failedComplaints);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to process all complaints.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "An internal server error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    
    

}
