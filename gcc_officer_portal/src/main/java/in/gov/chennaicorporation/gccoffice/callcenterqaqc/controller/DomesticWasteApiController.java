package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;



import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.DomesticWasteService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@RestController
@RequestMapping("/gcc/api/domestic_waste")
public class DomesticWasteApiController {
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private DomesticWasteService domesticWasteService;
	
	

	
	@GetMapping("/getitems")
	public ResponseEntity<Map<String, Object>> getItemsList() {
	    String apiUrl = appConfig.domesticurl + "/get-all-masters";
	    Map<String, Object> responseMap = new HashMap<>();

	    try {
	        String response = restTemplate.getForObject(apiUrl, String.class);

	        responseMap.put("status", "success");
	        responseMap.put("data", new ObjectMapper().readValue(response, Object.class)); // parse JSON array into list
	        return ResponseEntity.ok(responseMap);

	    } catch (HttpClientErrorException | HttpServerErrorException ex) {
	        responseMap.put("status", "error");
	        responseMap.put("code", ex.getStatusCode().value());
	        responseMap.put("message", ex.getStatusText());
	        responseMap.put("details", ex.getResponseBodyAsString());
	        return ResponseEntity.status(ex.getStatusCode()).body(responseMap);

	    } catch (ResourceAccessException ex) {
	        responseMap.put("status", "error");
	        responseMap.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
	        responseMap.put("message", "Service unavailable");
	        responseMap.put("details", ex.getMessage());
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(responseMap);

	    } catch (Exception ex) {
	        responseMap.put("status", "error");
	        responseMap.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
	        responseMap.put("message", "Unexpected error");
	        responseMap.put("details", ex.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
	    }
	}
	
	@PostMapping("/saverequest")
	public ResponseEntity<?> saveRequest(
	        @RequestParam String user_name,
	        @RequestParam String mobile,
	        @RequestParam String address,
	        @RequestParam String latitude,
	        @RequestParam String longitude,
	        @RequestParam String street_name,
	        @RequestParam String street_id,
	        @RequestParam String remarks,
	        @RequestParam("items") String itemsJson,
			@RequestParam String userId,@RequestParam String zone,@RequestParam String ward) {

	    try {
	        Map<String, Object> result = domesticWasteService.saveMultipleWasteRequests(
	                user_name, mobile, address,
	                latitude, longitude,
	                street_name, street_id, remarks,
	                 itemsJson,userId,zone,ward
	        );

	        return ResponseEntity.ok(result);

	    } catch (Exception e) {
	        return ResponseEntity.status(500).body(Map.of("status", false, "message", e.getMessage()));
	    }
	}
	
	@GetMapping("/getalldomesticwastelist")
    public Map<String, Object> getFilterReports(
    		 @RequestParam(required = false) String startDate,
    	        @RequestParam(required = false) String endDate,
    	        @RequestParam(required = false) String mobile,
    	        @RequestParam(required = false) String requestId) {
		Map<String, Object> response = new HashMap<>();

		try {
			  System.out.println("startDate="+startDate);
			  System.out.println("endDate="+endDate);
			  System.out.println("mobile="+mobile);
			  System.out.println("requestId="+requestId);
			  
		List<Map<String, Object>> details =domesticWasteService.getAllComplaintsList(startDate,endDate,mobile,requestId) ;
        System.out.println(details);
        if (details == null || details.isEmpty()) {
        	response.put("status", "nodata");
            response.put("message", "No Data");
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
	
	
	
	@GetMapping("/checkstatusbymobile")
	public Map<String, Object> getStatusByMobileNumber(
	        @RequestParam(required = false) String mobile) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        System.out.println("mobile=" + mobile);

	        // Construct external API URL
	        String url = appConfig.domesticurl + "/get-user-bymobile?mobile=" + mobile;

	        // Call the external API
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> apiResponse = restTemplate.getForEntity(url, String.class);

	        if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {
	            ObjectMapper mapper = new ObjectMapper();

	            // Convert JSON string to List of Maps
	            List<Map<String, Object>> itemsList = mapper.readValue(apiResponse.getBody(), 
	                    new TypeReference<List<Map<String, Object>>>() {});

	            if (itemsList.isEmpty()) {
	                response.put("status", "no data");
	                response.put("data", Collections.emptyList());
	            } else {
	                response.put("status", "success");
	                response.put("data", itemsList);
	            }
	        } else {
	            response.put("status", "no data");
	            response.put("data", Collections.emptyList());
	        }


	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", e.getMessage());
	    }

	    return response;
	}

	
	
	@GetMapping("/getpendingcomplaints")
	public Map<String, Object> getpendingcomplaints(
	        @RequestParam String startDate,
	        @RequestParam String endDate,
	        @RequestParam (required = false) String mobile) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        
	    	List<Map<String,Object>> list = domesticWasteService.getpendingcomplaints(startDate,endDate,mobile);

	        if (!list.isEmpty()) {
	            
	                response.put("status", "success");
	                response.put("data", list);
	            
	        } else {
	            response.put("status", "nodata");
	            response.put("data", Collections.emptyList());
	        }


	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", e.getMessage());
	    }

	    return response;
	}
	
	@GetMapping("/getDetailsByReqId")
	public Map<String, Object> getDetailsByReqId(
	        @RequestParam String requestId) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        
	    	List<Map<String,Object>> list = domesticWasteService.getDetailsByReqId(requestId);

	        if (!list.isEmpty()) {
	            
	                response.put("status", "success");
	                response.put("data", list);
	            
	        } else {
	            response.put("status", "nodata");
	            response.put("data", Collections.emptyList());
	        }


	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", e.getMessage());
	    }

	    return response;
	}
	
	@PostMapping("/cancelbyagents")
    public ResponseEntity<Map<String, Object>> saveAnswer(@RequestParam String requestId,@RequestParam String remarks,@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = domesticWasteService.changeStatus(requestId,remarks,userId);
            if (success) {
                response.put("status", "success");
                response.put("message", "Complaint cancelled successfully!");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Fail to Update.");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Exception: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


}
