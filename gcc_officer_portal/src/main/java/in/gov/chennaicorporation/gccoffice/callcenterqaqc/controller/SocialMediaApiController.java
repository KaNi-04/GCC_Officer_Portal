package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.SocialMediaService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@RequestMapping("gcc/api/callcenterqaqc/socialmedia")
@RestController
public class SocialMediaApiController {

	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	
	@Autowired
	public SocialMediaService socialservice;
	
	@Autowired  
    public SocialMediaApiController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
	
	@PostMapping("/update")
	@ResponseBody
	public ResponseEntity<String> updateCallDetails(@RequestParam("complaintNumber") String complaintNumber,			
			@RequestParam("userId") String userId,@RequestParam("action") String action,@RequestParam("remarks") String remarks
			) {
		try {
//			System.out.println("Updating Complaint Number: " + complaintNumber);
//			System.out.println("userId= " + userId);
//			System.out.println("action= " + action);
//			System.out.println("remarks="+remarks);
			int updated_agent = Integer.parseInt(userId);
//			System.out.println("updated_agent: " + updated_agent);
			if(remarks.isEmpty())
			{
				remarks="N/A";
			}
			
			String erp_name=socialservice.getErpusernamebyId(userId);
//			System.out.println("erp_name=="+erp_name);
			if (erp_name.isEmpty()) {
				return ResponseEntity.ok("erpname");
			}
			
			if ("COMPLETED".equals(action) || "REOPEN".equals(action))
			{	
				boolean statusUpdated =ChangeStatusInErp(complaintNumber,action,remarks,erp_name);
				System.out.println("statusUpdated="+statusUpdated); 
				
				if (!statusUpdated)
				{
					return ResponseEntity.ok("error");
				} 
			}
			

			Map<String, Object> details =getDetailsByComplaintNumber(complaintNumber);
			int rowInserted=socialservice.uploadComplaintDetailsInLogs(details,action,remarks,updated_agent);
			if(rowInserted!=1)
			{
				return ResponseEntity.ok("save");
			}	
			return ResponseEntity.ok("Success");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
		}
	}
	
	public  Map<String, Object> getDetailsByComplaintNumber(String complaintNumber) {
		
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
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
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
                    resultMap.put("gender", complaintDetails.path("GENDER").asText("N/A"));
                    resultMap.put("deptname",complaintDetails.path("DEPT_NAME").asText("N/A"));
                    resultMap.put("compmode", complaintDetails.path("COMP_MODE").asText("N/A"));
                    resultMap.put("Officialname", complaintDetails.path("Officialname").asText("N/A"));

                 
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

	
	public boolean ChangeStatusInErp(String complaintNumber,String action ,String remarks,String erp_name) {
	    String baseUrl = "https://erp.chennaicorporation.gov.in/pgr/external/mobileservice?";
	    String status = "";
	    
	   	    	 	       
	   //String Erpname ="QAops1";
	  
	    try {
	        // Encode parameters
	        String encodedComplaintNumber = URLEncoder.encode(complaintNumber, StandardCharsets.UTF_8.toString()).replace("+", "%20");
	        String encodedRemarks = URLEncoder.encode(remarks, StandardCharsets.UTF_8.toString()).replace("+", "%20");
	        String encodedErpname = URLEncoder.encode(erp_name, StandardCharsets.UTF_8.toString()).replace("+", "%20");
	        
	     // Determine status based on action
	        if ("COMPLETED".equals(action)) {
	            status = "11";
	        } else if ("REOPEN".equals(action)) {	        	
	            status = "9";
	        } else {
	            return false; // Invalid action, return false
	        }

	        // Construct the URL
	        String urlString = baseUrl
	                + "serviceId=UpdateComplaint"
	                + "&ComplaintId=" + encodedComplaintNumber
	                + "&Message=" + encodedRemarks
	                + "&UserType=Official"
	                + "&Status=" + status
	                + "&Comp_Image="
	                + "&erp_username=" + encodedErpname;

	        System.out.println("ERP Hitting URL="+urlString);

	        // Send the request
	        return sendRequest(urlString);
	       // return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	private boolean sendRequest(String urlString) {
	    try {
	        URL url = new URL(urlString);
	        URLConnection urlcon = url.openConnection();
	        InputStream stream = urlcon.getInputStream();
	        StringBuilder responseBuilder = new StringBuilder();
	        int i;
	        while ((i = stream.read()) != -1) {
	            responseBuilder.append((char) i);
	        }
	        stream.close();

	        // Parse the response
	        String response = responseBuilder.toString();
	        //System.out.println("Raw Response: " + response);

	        // Check ResultStatus in the response
	        if (response.contains("\"ResultStatus\": true")) {
	            //System.out.println("ResultStatus is true in the response.");
	            return true;
	        } else {
	            System.out.println("ResultStatus is false or not present in the response.");
	            return false;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	@GetMapping("/getsocialmediacomps")
    public Map<String, Object> viewSocialMediaData(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String status,
            @RequestParam String group,
            @RequestParam String type,          
            @RequestParam String zone,
            @RequestParam String region) {
		Map<String, Object> response = new HashMap<>();

		try {
			
        LocalDate fromLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate toLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        String formattedFromDate = fromLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String formattedToDate = toLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        String mode="SOCIAL MEDIA";
        
        String encoded_mode=URLEncoder.encode(mode, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        
        //String url="https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=ms_dashboard_details&From_date="+formattedFromDate+"&To_date="+formattedToDate+"&jsonResp=Yes&Status="+status+"&isQcuser=Yes"+"&ComplaintType="+type+"&ComplaintGroupId="+group+"&compmode="+encoded_mode+"&Zoneid="+zone;
        
        //String url="https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=ms_dashboard_details&From_date="+formattedFromDate+"&To_date="+formattedToDate+"&jsonResp=Yes&Status="+status+"&isQcuser=Yes"+"&ComplaintType="+type+"&ComplaintGroupId="+group+"&compmode="+mode+"&Zoneid="+zone;

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
	
	@GetMapping("/socialmediacompletedlist")
    public Map<String, Object> viewSocialMediaCompletedData(
            @RequestParam String startDate,
            @RequestParam String endDate,  @RequestParam String status,                 
            @RequestParam String zone) {
		
		System.out.printf("==================="+startDate,endDate,zone);
		Map<String, Object> response = new HashMap<>();

		try {
			
        LocalDate fromLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate toLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        String formattedFromDate = fromLocalDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedToDate = toLocalDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        List<Map<String, Object>> details= socialservice.getdetails(formattedFromDate,formattedToDate,status,zone);
        if(details==null)
        {
        	response.put("status", "nodata");
	        response.put("message", "No data for Selected filter");
        }
        else {
        	response.put("status", "success");
        	response.put("details", details);
        }
		}
		catch (DateTimeParseException e) {
	        response.put("status", "error");
	        response.put("message", "Invalid date format. Please use 'yyyy-MM-dd'.");
	        e.printStackTrace();
	    }
		catch (Exception e) {
			response.put("status", "error");
	        response.put("message", "An error occurred while fetching data.");
	        e.printStackTrace();
		}
        
        return response;
    }
	
}
