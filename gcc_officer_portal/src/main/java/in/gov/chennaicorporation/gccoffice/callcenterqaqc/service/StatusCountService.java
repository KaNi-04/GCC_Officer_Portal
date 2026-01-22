package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@Service
public class StatusCountService {

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    @Autowired
    private AppConfig appconfig;

    public StatusCountService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> fetchComplaintCounts(String fromDate, String toDate, String compType, String zoneName, String compMode, String compGroup, String region) {
    	//https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=common_api&jsonResp=Yes&subService=dashboardAbs&fromDate=&toDate=&compType=&zoneName=&compMode=&compGroup=
    	String apiUrl;
        if ("SOCIAL MEDIA".equalsIgnoreCase(compMode)) {
    	
         apiUrl = appconfig.qaqcurl+"serviceId=common_api&jsonResp=Yes"
                + "&subService=dashboardAbs"
                + "&fromDate=" + fromDate
                + "&toDate=" + toDate;                
        } 
        
        else {
        	apiUrl = appconfig.qaqcurl+"serviceId=common_api&jsonResp=Yes"
                    + "&subService=dashboardAbs"
                    + "&fromDate=" + fromDate
                    + "&toDate=" + toDate
                    +"&excludeMode=7";
                    
        }
        
     // Append parameters based on their availability
        if (!compType.isEmpty()) {
            apiUrl += "&compType=" + compType;
        }

        if (!zoneName.isEmpty()) {
            apiUrl += "&zoneName=" + zoneName;
        }

        if (!compMode.isEmpty()) {
            apiUrl += "&compMode=" + compMode;
        }

        if (!compGroup.isEmpty()) {
            apiUrl += "&compGroup=" + compGroup;
        }
        
        if (!region.isEmpty()) {
            apiUrl += "&region=" + region;
        }

        Map<String, Object> counts = new HashMap<>();
        System.out.println("url: " + apiUrl);

        try {
            // Get the raw response
            String response = restTemplate.getForObject(apiUrl, String.class);
            //System.out.println("API Response: " + response);  // Log the response for debugging

            JsonNode jsonResponse = objectMapper.readTree(response);

            // Check if "ListResult" exists and is an array
            JsonNode listResult = jsonResponse.path("ListResult");
            
            if (listResult.isArray() && !listResult.isEmpty()) {
                JsonNode resultNode = listResult.get(0);
                if (resultNode != null && !resultNode.isNull()) {
                    counts.put("Total Complaints", resultNode.path("RECEIVED").asText(""));
                    counts.put("In Progress", resultNode.path("PENDING").asText(""));
                    counts.put("Redressed", resultNode.path("REDRESSED").asText(""));
                    counts.put("Reopened", resultNode.path("REOPEN").asText(""));
                    counts.put("Closed", resultNode.path("CLOSED").asText(""));
                }
            }
            
            //System.out.println(fromDate);
            //System.out.println(toDate);
            
			String url="https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=ms_dashboard_details&From_date="+fromDate+"&To_date="+toDate+"&jsonResp=Yes&Status=closed&isQcuser=Yes&ComplaintType="+compType+"&ComplaintGroupId="+compGroup+"&compmode="+compMode+"&Zoneid="+zoneName+"&RegionId="+region;
			System.out.println("url==="+url);			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response1 = restTemplate.getForEntity(url, String.class);
			String rawResponse = response1.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> responseMap = objectMapper.readValue(rawResponse, Map.class);
			List<Map<String, Object>> details = (List<Map<String, Object>>) responseMap.get("Details");
			/*
			long completedCount = (details == null || details.isEmpty()) ? 0 : 
		        details.stream().filter(entry -> "Completed".equals(entry.get("Current Status"))).count();
		    */
			long completedCount = 0;

			if (details != null && !details.isEmpty()) {
			    for (Map<String, Object> entry : details) {
			        Object statusObj = entry.get("Current Status");
			        if ("Completed".equals(statusObj)) {
			            completedCount++;
			        }
			    }
			}
			
			counts.put("Withdraw", String.valueOf(completedCount));
			 
			int closedCount = Integer.parseInt(String.valueOf(counts.getOrDefault("Closed", "0")));
	        closedCount -= completedCount; // Adjust closed count
	        counts.put("Closed", String.valueOf(closedCount));
			    	
			  			
        } catch (Exception e) {
            e.printStackTrace();
        }

        return counts;

    }
    

    public List<String> fetchComplaintTypes(String compGroup) {
        // Construct the API URL with the compGroup parameter
        String apiUrl = appconfig.qaqcurl + 
                        "serviceId=common_api&jsonResp=Yes&subService=compTypelist&compGroup=" + compGroup;
        List<String> complaintTypes = new ArrayList<>();

        try {
            // Call the external API
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                
                // Navigate to "ListResult" and extract "COMPLAINTTYPENAME"
                JsonNode listResultNode = rootNode.path("ListResult");
                if (listResultNode.isArray()) {
                    for (JsonNode item : listResultNode) {
                        String complaintTypeName = item.path("COMPLAINTTYPENAME").asText().trim();
                        complaintTypes.add(complaintTypeName);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching complaint types for compGroup " + compGroup + ": " + e.getMessage());
        }

        return complaintTypes;
    }

    
    
    
    
    //for complaintgroup api
    public List<String> fetchComplaintGroup() {
        String apiUrl = appconfig.qaqcurl+"serviceId=common_api&jsonResp=Yes&subService=compGrouplist";
        List<String> complaintGroup = new ArrayList();

        try {
            // Call the external API
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                
                // Navigate to "ListResult" and extract "COMPLAINTTYPENAME"
                JsonNode listResultNode = rootNode.path("ListResult");
                if (listResultNode.isArray()) {
                    for (JsonNode item : listResultNode) {
                        String complaintTypeName = item.path("COMPLAINTGROUPNAME").asText().trim();
                        complaintGroup.add(complaintTypeName);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching complaint types: " + e.getMessage());
        }

        return complaintGroup;
    }
    
    
    
    //for modes api
    public List<String> fetchModes() {
        String apiUrl = appconfig.qaqcurl+"serviceId=common_api&jsonResp=Yes&subService=compModelist";
        List<String> modes = new ArrayList();

        try {
            // Call the external API
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                
                // Navigate to "ListResult" and extract "COMPLAINTTYPENAME"
                JsonNode listResultNode = rootNode.path("ListResult");
                if (listResultNode.isArray()) {
                    for (JsonNode item : listResultNode) {
                        String complaintModeName = item.path("COMP_MODE").asText().trim();
                        modes.add(complaintModeName);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching complaint types: " + e.getMessage());
        }

        return modes;
    }
    
    
    // for zones api
    public List<String> fetchZones() {
        String apiUrl = appconfig.qaqcurl+"serviceId=common_api&jsonResp=Yes&subService=zoneNamelist";
        List<String> zones = new ArrayList();

        try {
            // Call the external API
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                
                // Navigate to "ListResult" and extract "COMPLAINTTYPENAME"
                JsonNode listResultNode = rootNode.path("ListResult");
                if (listResultNode.isArray()) {
                    for (JsonNode item : listResultNode) {
                        String complaintzone = item.path("NAME").asText().trim();
                        zones.add(complaintzone);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching complaint types: " + e.getMessage());
        }

        return zones;
    }
    
    
    

}


