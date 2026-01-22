package in.gov.chennaicorporation.gccoffice.callcenter.controller;

import org.apache.poi.ss.usermodel.*;


import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.callcenter.service.AddcategoryServices;
import in.gov.chennaicorporation.gccoffice.callcenter.service.AgentAssignService;
import in.gov.chennaicorporation.gccoffice.callcenter.service.CampaignService;

@RestController("callcenterapicontroller")
@RequestMapping("gcc/api/campaign")
public class CampaignAPIController {
	
	 @Autowired
	 private CampaignService campaignService;
	 
	 @Autowired
	 private AddcategoryServices addCategoryServices; // Injecting the service
	 
	 @Autowired
	 private AgentAssignService agentsAssignService;
	 
	@Autowired	
	 private JdbcTemplate jdbcTemplate;

	 @Autowired
	 private Environment environment;
	 
	 @GetMapping("/getCampaignDetails")
	 public ResponseEntity<Map<String, Object>> getCampaignDetails() {
	     Map<String, Object> response = new HashMap<>();
	     List<Map<String, Object>> data = campaignService.getCampaignDetails();
	     
	     response.put("data", data);
	     return ResponseEntity.ok(response);
	 }
	 
	 
	 // Endpoint to get campaign details by ID
	 @GetMapping("/getCampaignDetailsById")
	    public @ResponseBody List<Map<String, Object>> getCampaignDetailsById(@RequestParam("campaign_id") Long campaignId) {
	        return campaignService.getCampaignDetailsById(campaignId);
	    }
	 
	 
	 @PostMapping("/updateCampaign")
	    public ResponseEntity<String> updateCampaign(
	            @RequestBody Map<String, Object> requestData) {

	        Long campaign_id = ((Number) requestData.get("campaign_id")).longValue();
	        String end_date = (String) requestData.get("end_date");
	        List<Map<String, Object>> questionsData = 
	                (List<Map<String, Object>>) requestData.get("questionsData");

	        if (campaign_id == null) {
	            return ResponseEntity.badRequest().body("Campaign ID is required.");
	        }

	        try {
	            // Update campaign details
	            campaignService.updateCampaign(campaign_id, end_date);

	            // Save questions and answers
	            for (Map<String, Object> questionMap : questionsData) {
	                String category = (String) questionMap.get("category");
	                String question = (String) questionMap.get("question");
	                String answerType = (String) questionMap.get("answerType");
	                List<String> answers = (List<String>) questionMap.get("answers");

	                if (category != null && question != null && answerType != null) {
	                    campaignService.saveQuestionsAndAnswers(
	                        category, 
	                        String.valueOf(campaign_id),  // Corrected data type
	                        question, 
	                        answerType, 
	                        answers
	                    );
	                }
	            }

	            return ResponseEntity.ok("Campaign updated successfully.");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Error updating campaign: " + e.getMessage());
	        }
	    }


	    
	    //save question and ansewers for campaign
	    @PostMapping("/saveQuestionsAndAnswers")
	    public ResponseEntity<String> saveQuestions(@RequestBody List<Map<String, Object>> requestData) {
	        String response = campaignService.saveQuestionsAnswers(requestData);
	        return ResponseEntity.ok(response);
	    }
	    
	    //save file and get path
	    private String saveFile(MultipartFile file) throws IOException {
	    	
	    	String uploadDirectory = environment.getProperty("file.upload.directory");
	        
	        String folderName = environment.getProperty("campaign.foldername");
	        	        
	        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + file.getOriginalFilename();

	        String folderPath = uploadDirectory + "/" + folderName + "/excel/";
	        
	        
	        Path dirPath = Paths.get(folderPath);
	        if (!Files.exists(dirPath)) {
	            Files.createDirectories(dirPath);
	        }
	               
	        Path filePath = dirPath.resolve(fileName);
	        
	        Files.write(filePath, file.getBytes());
	        
	        return filePath.toAbsolutePath().toString();
	    }

	    
	    //read uploaded excel data and make condition for each category uploading same excel columns are other some other?
	    private List<Map<String, Object>> readExcelData(InputStream inputStream, List<String> categoryColumns) throws IOException {
	        List<Map<String, Object>> dataList = new ArrayList<>();

	        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
	            Sheet sheet = workbook.getSheetAt(0);
	            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

	            // Read header row from Excel
	            Row headerRow = sheet.getRow(0);
	            List<String> excelHeaders = new ArrayList<>();
	            for (Cell cell : headerRow) {
	                excelHeaders.add(cell.getStringCellValue().trim());
	            }

	            // Compare Excel headers with category columns
	            if (!excelHeaders.containsAll(categoryColumns)) {
	               // throw new IllegalArgumentException("Excel data does not match the category columns.");
	            	
	            }

	            // Proceed with reading data only if headers match
	            for (Row row : sheet) {
	                if (row.getRowNum() == 0) continue; // Skip header row

	                Map<String, Object> dataMap = new HashMap<>();
	                for (Cell cell : row) {
	                    String cellValue = "";
	                    switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
	                        case STRING:
	                            cellValue = cell.getStringCellValue();
	                            break;
	                        case NUMERIC:
	                            if (DateUtil.isCellDateFormatted(cell)) {
	                                cellValue = new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
	                            } else {
	                            	 // Check if value is an integer (whole number)
	                                if (cell.getNumericCellValue() % 1 == 0) {
	                                    cellValue = String.valueOf((long) cell.getNumericCellValue()); // Format as integer
	                                } else {
	                                    cellValue = String.valueOf(cell.getNumericCellValue()); // Keep decimal values as they are
	                                }
	                            }
	                            break;
	                        case BOOLEAN:
	                            cellValue = String.valueOf(cell.getBooleanCellValue());
	                            break;
	                        default:
	                            cellValue = "Unknown";
	                            break;
	                    }
	                    // Map cell data to correct column name
	                    dataMap.put(excelHeaders.get(cell.getColumnIndex()), cellValue);
	                }

	                if (!dataMap.isEmpty()) {
	                    dataList.add(dataMap);
	                }
	            }
	        }

	        return dataList;
	    }


	   

	    
	    
	    @GetMapping("/getExcelData")
	    public ResponseEntity<List<Map<String, Object>>> getExcelData(@RequestParam("filePath") String filePath) {
	        try {
	            // Read data from the uploaded Excel file
	            List<Map<String, Object>> excelData = readExcelData(new FileInputStream(filePath), null);
	            
	            // Return data as JSON
	            return ResponseEntity.ok(excelData);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }

	    
	    @GetMapping("/getlastCampaignid")
	    public List<Map<String, Object>> getLastRow()
	    {
	    	return campaignService.getLastRow();
	    }
	    
	    @GetMapping("/startcampaign")
	    public List<Map<String, Object>> getDataforAgents(@RequestParam int campaign_id,@RequestParam int calls_per_agent)
	    {  	  
	  		return campaignService.getDataforAgents(campaign_id, calls_per_agent);
	    }
	    
	    @GetMapping("/viewcampaignstatus")
	    public List<Map<String, Object>> viewcampaignstatus(@RequestParam int campaign_id)
	    {  	  
	  		return campaignService.viewcampaignstatus(campaign_id);
	    }
	    
	    @GetMapping("/viewcompletedby")
	    public List<Map<String, Object>> viewcompletedby(@RequestParam int campaign_id,@RequestParam int agent_id)
	    {  	  
	  		return campaignService.viewcompletedby(campaign_id,agent_id);
	    }
	    


	    
	    
	    @PostMapping(value = "/savecampaign", consumes = {"multipart/form-data"})
	    public ResponseEntity<Map<String, String>> saveCampaign(
	            @RequestParam String campaignId,
	            @RequestParam String categoryId,
	            @RequestParam String eventStartDate,
	            @RequestParam String eventEndDate,
	            @RequestParam int noOfCalls,
	            @RequestParam String description,
	            @RequestParam(required = false) String questionsData, // JSON as String
	            @RequestParam(required = false) String selectedAgents // JSON string
	    ) {
	       

	        // Convert JSON strings to List objects
	        List<Map<String, Object>> questionsDataList = new ArrayList<>();
	        List<String> selectedAgentsList = new ArrayList<>();

	        try {
	            if (questionsData != null) {
	                questionsDataList = new ObjectMapper().readValue(questionsData, new TypeReference<List<Map<String, Object>>>() {});
	            }

	            if (selectedAgents != null) {
	                selectedAgentsList = new ObjectMapper().readValue(selectedAgents, new TypeReference<List<String>>() {});
	            }
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of("status", "error", "message", "Invalid JSON format in request data"));
	        }

	        // Campaign Details Mapping
	        Map<String, Object> campaignDetails = new HashMap<>();
	        campaignDetails.put("campaign_id", campaignId);
	        campaignDetails.put("category_id", categoryId);
	        campaignDetails.put("start_date", eventStartDate);
	        campaignDetails.put("end_date", eventEndDate);
	        campaignDetails.put("calls_per_agent", noOfCalls);
	        campaignDetails.put("description", description);

	        try {
	            campaignService.saveCampaignDetails(campaignDetails);

	            // Step 3: Assign Agents for the Campaign
	            if (!selectedAgentsList.isEmpty()) {
	                int parsedCampaignId = Integer.parseInt(campaignId);
	                int parsedCategoryId = Integer.parseInt(categoryId);

	                System.out.println("selectedAgentsList====== " + selectedAgentsList);

	                int calls_assign = noOfCalls * selectedAgentsList.size();
	                System.out.println("calls_assign==  " + calls_assign);

	                List<Integer> DataIds=agentsAssignService.getDataIdForInitialAssign(parsedCategoryId, parsedCampaignId);
	                
	                for (String agentId : selectedAgentsList) {
	                    int selectedAgentId = Integer.parseInt(agentId);
	                    			                    
	                    agentsAssignService.addAgentsForCampaign(selectedAgentId, parsedCategoryId, parsedCampaignId);
	                }
	                //System.out.println("dataids=== " + DataIds);
	                if (!DataIds.isEmpty()) {
	                    int dataIndex = 0; // Initialize data index pointer

	                    for (String agentId : selectedAgentsList) {
	                        int selectedAgentId = Integer.parseInt(agentId);

	                        // Iterate for noOfCalls times and assign data IDs to the current agent
	                        for (int i = 0; i < noOfCalls && dataIndex < DataIds.size(); i++, dataIndex++) {
	                            int DataId = DataIds.get(dataIndex);

	                            agentsAssignService.updateAgentForCampaign(
	                                selectedAgentId,
	                                parsedCategoryId,
	                                parsedCampaignId,
	                                DataId
	                            );
	                        }
	                    }
	                    
	                    // Update campaign status to 'ONGOING' after successful assignment
	                    campaignService.updateCampaignStatus(parsedCampaignId, parsedCategoryId);
	                }

	                else {
	                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                        .body(Map.of("status", "error", "message", "No data available for initial assignment."));
	                }
	                
	            }

	            // Step 4: Save Questions and Answers
	            if (!questionsDataList.isEmpty()) {
	                for (Map<String, Object> questionMap : questionsDataList) {
	                    String question = (String) questionMap.get("question");
	                    String answerType = (String) questionMap.get("answerType");
	                    List<String> answers = (List<String>) questionMap.get("answers");

	                    if (question != null && answerType != null) {
	                        campaignService.saveQuestionsAndAnswers(
	                                categoryId,
	                                campaignId,
	                                question,
	                                answerType,
	                                answers
	                        );
	                    } else {
	                        System.out.println("Skipping invalid question data: " + questionMap);
	                    }
	                }
	            }

	        }
	        
	       
	        catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Map.of("status", "error", "message", "Failed to save campaign details: " + e.getMessage()));
	        }

	        return ResponseEntity.ok(Map.of("status", "success", "message", "Campaign details saved successfully"));
	    }


	    //save excel data separate method
	    
	    @PostMapping(value = "/saveexcelcampaign"  ,consumes = {"multipart/form-data"})
	    public Map<String, String> saveExcelCampaign(
	            @RequestParam String name,
	            @RequestParam String category,
	            @RequestPart MultipartFile file) {

	       //System.out.println("Received campaign data:");
	       //System.out.println("Name: " + name);
	       //System.out.println("Category: " + category);

	       //System.out.println("File Name: " + file.getOriginalFilename());

	        Map<String, Object> campaignDetails = new HashMap<>();
	        campaignDetails.put("campaign_name", name);
	        campaignDetails.put("category_id", category);


	        try {
	            // Get category-specific columns
	            List<Map<String, Object>> categoryColumns = addCategoryServices.getCategoryColumns(Integer.parseInt(category));
	            /*
	            //System.out.println("categorycolmns: " + categoryColumns);
	            List<String> expectedHeaders = categoryColumns.stream()
	                    .map(column -> column.get("column_name").toString())
	                    .collect(Collectors.toList());
	            */
	            List<String> expectedHeaders = new ArrayList<String>();

	            for (Map<String, Object> column : categoryColumns) {
	                Object columnNameObj = column.get("column_name");
	                if (columnNameObj != null) {
	                    expectedHeaders.add(columnNameObj.toString());
	                }
	            }
	            // Fetch field_ columns from upload_data table
	            List<String> fieldColumns = campaignService.getUploadDataFieldColumns();
	            //System.out.println("fieldColumns: " + fieldColumns);

	            
	            // Read uploaded Excel data and extract headers
	            List<Map<String, Object>> excelData = readExcelData(file.getInputStream(), expectedHeaders);
	            
	           // System.out.println("excedata: " + excelData );
	            List<String> uploadedHeaders = new ArrayList<>(excelData.get(0).keySet()); // Extract headers from first row
	            
	            //System.out.println("uploadeddata : " + uploadedHeaders);

	         // Check column count mismatch with field_ columns
	            if (uploadedHeaders.size() > fieldColumns.size()) {
	                Map<String, String> response = new HashMap<>();
	                response.put("status", "column_mismatch");  // Changed status to differentiate
	                response.put("message", "Extend the columns in Database, Inform Admin.");
	                return response;
	            }

	            // Validate headers
	            if (!new HashSet<>(expectedHeaders).equals(new HashSet<>(uploadedHeaders))) {
	                Map<String, String> response = new HashMap<>();
	                response.put("status", "header_mismatch");  // Changed status to differentiate
	                response.put("message", "Excel data does not match the category columns.");
	                return response;
	            }


	            String fileUrl = saveFile(file);
	            campaignDetails.put("excel_url", fileUrl);

	            //System.out.println("Campaign Details: " + campaignDetails);
	            //System.out.println("Excel Data: " + excelData);
	            	            
	            campaignService.saveCampaignDetailsExcel(campaignDetails);
	            int lastId = campaignService.getLastInsertedCampaignId();

	            String excelUrl = campaignService.getExcelUrlByCampaignId(lastId);
	            //List<Map<String, Object>> excelDatas = readExcelDataupdated(new FileInputStream(excelUrl));
	            List<Map<String, Object>> excelDatas = readExcelData(new FileInputStream(excelUrl), uploadedHeaders);
	            //System.out.println("excelDatas:" + excelDatas);
	            
	            for (Map<String, Object> row : excelDatas) {
	                campaignService.saveUploadData(category, lastId, row,expectedHeaders);
	                //System.out.println("exceldatas:" + excelDatas);
	            }

	            Map<String, String> response = new HashMap<>();
	            response.put("status", "success");
	            response.put("message", "Campaign and Excel saved successfully");
	            return response;

	        } 
	        catch (IOException e) {
	            e.printStackTrace();
	            Map<String, String> response = new HashMap<>();
	            response.put("status", "error");
	            response.put("message", "Failed to save campaign details: "+ e.getMessage());
	            return response;
	        }
	    }

	    
	    
	    
	    
	    @GetMapping("/availableAgents")
	    public ResponseEntity<List<Map<String, Object>>> getAvailableAgents() {
	        // Query 1: Fetch OUTBOUND agents
	        String outboundAgentsQuery = "SELECT agent_name, agent_id, calling_type " +
	                                     "FROM gcc_1913_qaqc.agents_list " +
	                                     "WHERE calling_type='OUTBOUND'";

	        List<Map<String, Object>> outboundAgents = jdbcTemplate.queryForList(outboundAgentsQuery);

	        //System.out.println("outboundagents" + outboundAgents);
	        // Query 2: Fetch agents from ongoing campaigns
	        String ongoingCampaignAgentsQuery = "SELECT ca.agent_id " +
	                                            "FROM gcc_1913_campaign.campaign_request cr " +
	                                            "JOIN gcc_1913_campaign.agent_assign ca " +
	                                            "ON ca.campaign_id = cr.campaign_id " +
	                                            "WHERE cr.campaign_status = 'ONGOING'";

	        List<Map<String, Object>> ongoingAgents = jdbcTemplate.queryForList(ongoingCampaignAgentsQuery);

	        //System.out.println("ongoingAgents" + ongoingAgents);
	        // Extract agent IDs from both lists
	        /*
	        Set<Integer> ongoingAgentIds = ongoingAgents.stream()
	                .map(agent -> (Integer) agent.get("agent_id"))
	                .collect(Collectors.toSet());

	        // Filter OUTBOUND agents who are NOT in ongoing agents
	        List<Map<String, Object>> availableAgents = outboundAgents.stream()
	                .filter(agent -> !ongoingAgentIds.contains(agent.get("agent_id")))
	                .collect(Collectors.toList());

	        */
	        Set<Integer> ongoingAgentIds = new HashSet<>();

	        for (Map<String, Object> agent : ongoingAgents) {
	            Object idObj = agent.get("agent_id");
	            if (idObj != null) {
	                ongoingAgentIds.add(Integer.parseInt(idObj.toString()));
	            }
	        }

	        // Filter OUTBOUND agents who are NOT in ongoing agents
	        List<Map<String, Object>> availableAgents = new ArrayList<>();

	        for (Map<String, Object> agent : outboundAgents) {
	            Object idObj = agent.get("agent_id");
	            if (idObj != null) {
	                Integer agentId = Integer.parseInt(idObj.toString());
	                if (!ongoingAgentIds.contains(agentId)) {
	                    availableAgents.add(agent);
	                }
	            }
	        }
	        
	        return ResponseEntity.ok(availableAgents);
	    }
	    


	    private Object getCellValue(Cell cell) {
	        switch (cell.getCellType()) {
	            case STRING:
	                return cell.getStringCellValue().trim();
	            case NUMERIC:
	                if (DateUtil.isCellDateFormatted(cell)) {
	                    return cell.getDateCellValue();  // Return Date for date-formatted cells
	                }
	                double numericValue = cell.getNumericCellValue();
	                
	                // ✅ Convert numeric values that are likely mobile numbers to string
	                if (String.valueOf((long) numericValue).length() >= 10) { // Assuming mobile numbers are at least 10 digits
	                    return String.valueOf((long) numericValue);
	                }
	                
	                // ✅ Convert to int if no decimal part, else keep as double
	                return (numericValue % 1 == 0) ? (int) numericValue : numericValue;
	            case BOOLEAN:
	                return cell.getBooleanCellValue();
	            case FORMULA:
	                return cell.getCellFormula();  // Return formula as string
	            case BLANK:
	                return "";
	            default:
	                return "";
	        }
	    }

	    
	    
	    //question and answers report page api
	    @GetMapping("/question-answers")
	    public ResponseEntity<?> getQuestionAnswers(@RequestParam int campaignId) {
	        List<Map<String, Object>> response = campaignService.getQuestionAnswers(campaignId);
	        return ResponseEntity.ok(response);
	    }

	    
	    //dizo campaignlist apicontroller
	    // Query Parameter Approach
	    @GetMapping("/statistics")
	    public ResponseEntity<Map<String, Object>> getCampaignStatisticsByQuery(@RequestParam int campaignId) {
	        Map<String, Object> statistics = campaignService.getCampaignStatistics(campaignId);
	        return ResponseEntity.ok(statistics);
	    }
	    
	    @GetMapping("/assignagentid")
	    public ResponseEntity<List<Map<String, Object>>> getAllAgents() {
	        List<Map<String, Object>> assignAgentId = campaignService.getAllAgents();
	        return ResponseEntity.ok(assignAgentId);
	    }
	    
	    @PostMapping("/close")
	    public ResponseEntity<String> closeCampaign(@RequestParam("campaignId") int campaignId) {
	        if (campaignId <= 0) {
	            return ResponseEntity.badRequest().body("Invalid Campaign ID");
	        }

	        System.out.println("Received request to close campaign ID: " + campaignId); // Debugging log

	        int rowsUpdated = campaignService.closeCampaign(campaignId);
	        if (rowsUpdated > 0) {
	            return ResponseEntity.ok("Campaign closed successfully");
	        } else {
	            return ResponseEntity.status(400).body("Failed to close campaign. Campaign ID " + campaignId + " may not exist.");
	        }
	    }
	    
	    @PostMapping("/auto-close")
	    public ResponseEntity<String> autoCloseExpiredCampaigns() {
	    	
	        int rowsUpdated = campaignService.autoCloseExpiredCampaigns();

	        if (rowsUpdated > 0) {
	            return ResponseEntity.ok(rowsUpdated + " expired campaigns closed successfully.");
	        } else {
	            return ResponseEntity.ok("No expired campaigns found.");
	        }
	    }

	    
	    
}
