package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.ScpService;

@RestController
@RequestMapping("/gcc/api/scp")
public class ScpApiController {
	
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ScpService scpservice;
	
	@GetMapping("/getdatabyfilters")
	public Map<String, Object> getdatabyfilters(
	        @RequestParam String startDate,
	        @RequestParam String endDate,
	        @RequestParam String zone,
	        @RequestParam String ward) {

	    Map<String, Object> response = new HashMap<>();

	    try {
	    	
	    	DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	        String formattedStartDate = LocalDate.parse(startDate, inputFormat).format(outputFormat);
	        String formattedEndDate = LocalDate.parse(endDate, inputFormat).format(outputFormat);
	    	
	        // Build API URL
	        String apiUrl = "https://gccservices.in/gccofficialapp/api/asset/scpfilterReports"
	                + "?assetTypeId=2"
	                + "&fromDate=" + formattedStartDate
	                + "&toDate=" + formattedEndDate
	                + "&loginId=5662";

	        if (!zone.isEmpty()) {
	            apiUrl += "&zone=" + zone;
	        }
	        if (!ward.isEmpty()) {
	            apiUrl += "&ward=" + ward;
	        }

	        System.out.println("apiUrl=" + apiUrl);

	        // Call the external API
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> apiResponse = restTemplate.getForEntity(apiUrl, String.class);

	        if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {

	            ObjectMapper mapper = new ObjectMapper();

	            // Parse full JSON
	            JsonNode rootNode = mapper.readTree(apiResponse.getBody());

	            // Extract "scpreport" array
	            JsonNode reportNode = rootNode.path("scpreport");

	            if (reportNode.isMissingNode() || !reportNode.isArray() || reportNode.size() == 0) {
	                response.put("status", "nodata");
	                response.put("data", Collections.emptyList());
	            } else {
	                // Convert JSON array → List<Map<String, Object>>
	                List<Map<String, Object>> reportList = mapper.convertValue(
	                        reportNode,
	                        new TypeReference<List<Map<String, Object>>>() {}
	                );

	                response.put("status", "success");
	                response.put("data", reportList);
	                if(zone.isEmpty() && ward.isEmpty()) {
	                	response.put("table", "table1");
	                }
	                else if(ward.isEmpty() && !zone.isEmpty()) {
	                	response.put("table", "table2");
	                }
	                else if(!ward.isEmpty() && !zone.isEmpty()) {
	                	response.put("table", "table3");
	                }
	                
	            }
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
	
	@GetMapping("/getzones")
	public ResponseEntity<Map<String, Object>> getzones() {
	    String apiUrl = "https://gccservices.in/gccrestservices/dropdown/api/zone";
	    Map<String, Object> responseMap = new HashMap<>();

	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        String response = restTemplate.getForObject(apiUrl, String.class);

	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode rootArray = objectMapper.readTree(response);

	        // ✅ The response is an array, so get the first element
	        if (rootArray.isArray() && rootArray.size() > 0) {
	            JsonNode firstObject = rootArray.get(0);

	            boolean status = firstObject.path("status").asBoolean(false);

	            if (status) {
	                JsonNode dataArray = firstObject.path("data");
	                responseMap.put("status", "success");
	                responseMap.put("code", 200);
	                responseMap.put("message", "Data found");
	                responseMap.put("data", objectMapper.convertValue(
	                        dataArray, new TypeReference<List<Map<String, Object>>>() {}));
	                return ResponseEntity.ok(responseMap);
	            } else {
	                responseMap.put("status", "failure");
	                responseMap.put("code", 404);
	                responseMap.put("message", "No Zones found");
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
	            }
	        } else {
	            responseMap.put("status", "failure");
	            responseMap.put("code", 404);
	            responseMap.put("message", "Invalid response format");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
	        }

	    } catch (Exception ex) {
	        responseMap.put("status", "error");
	        responseMap.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
	        responseMap.put("message", "Unexpected error occurred");
	        responseMap.put("details", ex.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
	    }
	}
	
	
	@GetMapping("/getwardbyzone")
	public ResponseEntity<Map<String, Object>> getwardbyzone( @RequestParam String zone) {
	    String apiUrl = "https://gccservices.in/gccrestservices/dropdown/api/wardbyzone?zoneId="+zone;
	    Map<String, Object> responseMap = new HashMap<>();

	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        String response = restTemplate.getForObject(apiUrl, String.class);

	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode rootArray = objectMapper.readTree(response);

	        // ✅ The response is an array, so get the first element
	        if (rootArray.isArray() && rootArray.size() > 0) {
	            JsonNode firstObject = rootArray.get(0);

	            boolean status = firstObject.path("status").asBoolean(false);

	            if (status) {
	                JsonNode dataArray = firstObject.path("data");
	                responseMap.put("status", "success");
	                responseMap.put("code", 200);
	                responseMap.put("message", "Data found");
	                responseMap.put("data", objectMapper.convertValue(
	                        dataArray, new TypeReference<List<Map<String, Object>>>() {}));
	                return ResponseEntity.ok(responseMap);
	            } else {
	                responseMap.put("status", "failure");
	                responseMap.put("code", 404);
	                responseMap.put("message", "No Zones found");
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
	            }
	        } else {
	            responseMap.put("status", "failure");
	            responseMap.put("code", 404);
	            responseMap.put("message", "Invalid response format");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
	        }

	    } catch (Exception ex) {
	        responseMap.put("status", "error");
	        responseMap.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
	        responseMap.put("message", "Unexpected error occurred");
	        responseMap.put("details", ex.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
	    }
	}
	
	
	@GetMapping("/getstreetdetails")
	public Map<String, Object> getstreetdetails(
	        @RequestParam String startDate,
	        @RequestParam String endDate,
	        @RequestParam String variable) {

	    Map<String, Object> response = new HashMap<>();

	    try {
	        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	        String formattedStartDate = LocalDate.parse(startDate, inputFormat).format(outputFormat);
	        String formattedEndDate = LocalDate.parse(endDate, inputFormat).format(outputFormat);

	        // 🔹 Call external API
	        String apiUrl = "https://gccservices.in/gccofficialapp/api/asset/scpReportAssetList"
	                + "?assetTypeId=2"
	                + "&fromDate=" + formattedStartDate
	                + "&toDate=" + formattedEndDate
	                + "&loginId=5662"
	                + "&status=Cleaned"
	                + "&streetid=" + variable;

	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> apiResponse = restTemplate.getForEntity(apiUrl, String.class);

	        List<Map<String, Object>> reportData = new ArrayList<>();

	        if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode rootNode = mapper.readTree(apiResponse.getBody());
	            JsonNode reportNode = rootNode.path("scpreport");

	            if (reportNode.isArray() && reportNode.size() > 0) {
	                reportData = mapper.convertValue(
	                        reportNode,
	                        new TypeReference<List<Map<String, Object>>>() {}
	                );
	            }
	        }

	        if (reportData.isEmpty()) {
	            response.put("status", "nodata");
	            response.put("data", Collections.emptyList());
	        } else {
	            // 🔹 Enrich each data item with questions_part
	            List<Map<String, Object>> enrichedData = scpservice.enrichScpReportWithQuestions(reportData);

	            response.put("status", "success");
	            response.put("data", enrichedData);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", e.getMessage());
	    }

	    return response;
	}


//	@PostMapping("/saveanswer")
//    public ResponseEntity<Map<String, Object>> saveAnswer(@RequestBody List<Map<String, Object>> answers) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            boolean success = scpservice.saveAnswers(answers);
//            if (success) {
//                response.put("status", "success");
//                response.put("message", "Feedback submitted successfully!");
//                return ResponseEntity.ok(response);
//            } else {
//                response.put("status", "error");
//                response.put("message", "Fail to Save.");
//                return ResponseEntity.status(500).body(response);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.put("status", "error");
//            response.put("message", "Exception: " + e.getMessage());
//            return ResponseEntity.status(500).body(response);
//        }
//    }
	
	
	@PostMapping("/saveanswer")
	public ResponseEntity<Map<String, Object>> saveAnswer(@RequestBody List<Map<String, Object>> answers) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        String result = scpservice.saveAnswers(answers);

	        if ("success".equals(result)) {
	            response.put("status", "success");
	            response.put("message", "Feedback submitted successfully!");
	            return ResponseEntity.ok(response);

	        } else if ("duplicate".equals(result)) {
	            response.put("status", "duplicate");
	            response.put("message", "Already submitted by another one, So refresh the page");
	            return ResponseEntity.ok(response);

	        } else {
	            response.put("status", "error");
	            response.put("message", "Fail to Save.");
	            return ResponseEntity.ok(response);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", "Exception: " + e.getMessage());
	        return ResponseEntity.status(500).body(response);
	    }
	}

	
	
	@GetMapping("/getreportdatabyfilters")
	public Map<String, Object> getreportdatabyfilters(
	        @RequestParam String startDate,
	        @RequestParam String endDate,
	        @RequestParam String zone,
	        @RequestParam String ward) {

	    Map<String, Object> response = new HashMap<>();

	    try {
	    	
	    	DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	        String formattedStartDate = LocalDate.parse(startDate, inputFormat).format(outputFormat);
	        String formattedEndDate = LocalDate.parse(endDate, inputFormat).format(outputFormat);
	    	
	        // Build API URL
	        String apiUrl = "https://gccservices.in/gccofficialapp/api/asset/scpfilterReports"
	                + "?assetTypeId=2"
	                + "&fromDate=" + formattedStartDate
	                + "&toDate=" + formattedEndDate
	                + "&loginId=5662";

	        if (!zone.isEmpty()) {
	            apiUrl += "&zone=" + zone;
	        }
	        if (!ward.isEmpty()) {
	            apiUrl += "&ward=" + ward;
	        }

	        System.out.println("apiUrl=" + apiUrl);

	        // Call the external API
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> apiResponse = restTemplate.getForEntity(apiUrl, String.class);

	        if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {

	            ObjectMapper mapper = new ObjectMapper();

	            // Parse full JSON
	            JsonNode rootNode = mapper.readTree(apiResponse.getBody());

	            // Extract "scpreport" array
	            JsonNode reportNode = rootNode.path("scpreport");

	            if (reportNode.isMissingNode() || !reportNode.isArray() || reportNode.size() == 0) {
	                response.put("status", "nodata");
	                response.put("data", Collections.emptyList());
	            } else {
	                // Convert JSON array → List<Map<String, Object>>
	                List<Map<String, Object>> reportList = mapper.convertValue(
	                        reportNode,
	                        new TypeReference<List<Map<String, Object>>>() {}
	                );
	                
	                
	                // Declare dbMap outside conditional blocks
	                Map<String, Map<String, Object>> dbMap = new HashMap<>();
	                List<Map<String, Object>> dbList = new ArrayList<>();
/*
	                // Fetch DB data based on filters
	                if (zone.isEmpty() && ward.isEmpty()) {
	                    dbList = scpservice.getZoneWiseReportData();
	                    dbMap = dbList.stream().collect(Collectors.toMap(
	                            row -> String.valueOf(row.get("zone")),
	                            row -> row
	                    ));
	                } else if (ward.isEmpty() && !zone.isEmpty()) {
	                    dbList = scpservice.getWardWiseReportData(zone);
	                    dbMap = dbList.stream().collect(Collectors.toMap(
	                            row -> String.valueOf(row.get("ward")),
	                            row -> row
	                    ));
	                } else if (!ward.isEmpty() && !zone.isEmpty()) {
	                    dbList = scpservice.getStreetWiseReportData(zone, ward);
	                    dbMap = dbList.stream().collect(Collectors.toMap(
	                            row -> String.valueOf(row.get("streetid")),
	                            row -> row
	                    ));
	                }
	                */
	             // Fetch DB data based on filters
	                if (zone.isEmpty() && ward.isEmpty()) {

	                    dbList = scpservice.getZoneWiseReportData();

	                    for (Map<String, Object> row : dbList) {
	                        String key = String.valueOf(row.get("zone"));
	                        dbMap.put(key, row);
	                    }

	                } else if (ward.isEmpty() && !zone.isEmpty()) {

	                    dbList = scpservice.getWardWiseReportData(zone);

	                    for (Map<String, Object> row : dbList) {
	                        String key = String.valueOf(row.get("ward"));
	                        dbMap.put(key, row);
	                    }

	                } else if (!ward.isEmpty() && !zone.isEmpty()) {

	                    dbList = scpservice.getStreetWiseReportData(zone, ward);

	                    for (Map<String, Object> row : dbList) {
	                        String key = String.valueOf(row.get("streetid"));
	                        dbMap.put(key, row);
	                    }
	                }
	                
	                
	                // Merge API data with DB data
	             // Merge API data with DB data
	                for (int i = 0; i < reportList.size(); i++) {
	                    Map<String, Object> apiRow = reportList.get(i);
	                    String variableValue = String.valueOf(apiRow.get("VARIABLE"));
	                    boolean isLast = (i == reportList.size() - 1); // check if it's the last element

	                    if (dbMap.containsKey(variableValue)) {
	                        Map<String, Object> dbRow = dbMap.get(variableValue);
	                        apiRow.put("Completed", dbRow.get("Completed"));
	                        apiRow.put("Yes", dbRow.get("YesCount"));
	                        apiRow.put("No", dbRow.get("NoCount"));
	                        apiRow.put("Available", dbRow.get("AvailableCount"));
	                        apiRow.put("Not Available", dbRow.get("NotAvailableCount"));
	                        apiRow.put("Image Not Clear", dbRow.get("ImageNotClearCount"));
	                        apiRow.put("LidGoodYesCount", dbRow.get("LidGoodYesCount"));
	                        apiRow.put("LidGoodNoCount", dbRow.get("LidGoodNoCount"));
	                    } else {
	                        // If last list → use "-"
	                        if (isLast) {
	                            apiRow.put("Completed", "-");
	                            apiRow.put("Yes", "-");
	                            apiRow.put("No", "-");
	                            apiRow.put("Available", "-");
	                            apiRow.put("Not Available", "-");
	                            apiRow.put("Image Not Clear", "-");
	                            apiRow.put("LidGoodYesCount", "-");
	                            apiRow.put("LidGoodNoCount", "-");
	                        } else {
	                            // For all others → use 0
	                            apiRow.put("Completed", 0);
	                            apiRow.put("Yes", 0);
	                            apiRow.put("No", 0);
	                            apiRow.put("Available", 0);
	                            apiRow.put("Not Available", 0);
	                            apiRow.put("Image Not Clear", 0);
	                            apiRow.put("LidGoodYesCount", 0);
	                            apiRow.put("LidGoodNoCount", 0);
	                        }
	                    }
	                }


	                response.put("status", "success");
	                response.put("data", reportList);
	                if(zone.isEmpty() && ward.isEmpty()) {
	                	response.put("table", "table1");
	                }
	                else if(ward.isEmpty() && !zone.isEmpty()) {
	                	response.put("table", "table2");
	                }
	                else if(!ward.isEmpty() && !zone.isEmpty()) {
	                	response.put("table", "table3");
	                }
	                
	            }
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
	
	
	@GetMapping("/getdetailreportdata")
	public Map<String, Object> getDetailreportdatabyfilters(
	        @RequestParam String startDate,
	        @RequestParam String endDate) {
		
		 Map<String, Object> response = new HashMap<>();
		
		 try {
			 
			 List<Map<String,Object>> list=scpservice.getdetailreport(startDate,endDate);
			 
			 if(list.isEmpty()) {
				 response.put("status", "nodata");
			     response.put("message", "No Data");
			 }
			 else {
				 	response.put("status", "success");
			        response.put("data", list);
			        response.put("message", "Fetched Data Successfully");
			 }
		 }
		 catch (Exception e) {
		        e.printStackTrace();
		        response.put("status", "error");
		        response.put("message", e.getMessage());
		    }
		 
		return response;
	}
	

}
