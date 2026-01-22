package in.gov.chennaicorporation.gccoffice.callcenter.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.callcenter.service.AgentsService;

@RequestMapping("/gcc/api/agents")
@RestController
public class AgentsAPIController {

	  @Autowired
	  private AgentsService  agentsService;
	
	  @GetMapping("/excel")
	    public List<Map<String, Object>> fetchDataFromExcel(@RequestParam int categoryId,@RequestParam Long campaignId) {
	        try {
	            return agentsService.fetchDataFromExcel(categoryId,campaignId);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null; // Handle the exception as needed
	        }
	    }
	  
	  
	  @PostMapping("/saveExcelData")
	    public String saveExcelData( @RequestParam int categoryId,@RequestParam Long campaignId,@RequestParam String filePath) {

	        try {
	            // Fetch data from Excel
	            List<Map<String, Object>> excelData = agentsService.getExcelData(categoryId,campaignId, filePath);
	            // Save data into the database
	            agentsService.saveExcelData(excelData);
	            return "Data saved successfully";
	        } catch (IOException e) {
	            e.printStackTrace();
	            return "Failed to save data";
	        }
	    }
	  
	  @GetMapping("/getdataByIds")
	  public List<Map<String, Object>> getdataByIds(@RequestParam List<Integer> ids) {
	      return agentsService.getdataByIds(ids);
	  }
	  
	  @GetMapping("/getPendingDataByIds")
	  public List<Map<String, Object>> getPendingDataByIds(@RequestParam List<Integer> ids) {
	      return agentsService.getPendingDataByIds(ids);
	  }
	  
	 
	  
//	  @PostMapping("/updatecall")
//	  public ResponseEntity<String> updatecall(@RequestBody Map<String, Object> callData)
//	  {
//		  String call_status = (String) callData.get("call_status");
//	      int data_id = (int) callData.get("data_id");
//	      int agent_id=(int) callData.get("agent_id");
//	      int campaign_id=(int) callData.get("campaign_id");
//	      
//	      if (call_status == null || call_status.isEmpty()) {
//	          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Call status is required.");
//	      }
//	      
//	      agentsService.saveInLogsTable(campaign_id, agent_id, data_id,call_status);
//	      
//	      return ResponseEntity.ok("Data Inserted successfully.");
//	  }
	  
	  @GetMapping("/getAgentNames")
	  public  List<Map<String, Object>> getAgentsNameById(@RequestParam List<Integer> agentIds){
		  return agentsService.getAgentsNameById(agentIds);
	  }
	  
	  @GetMapping("/getDataFields")
	    public List<Map<String, Object>> getDataDetailsById(
	            @RequestParam String originalDataIds) throws JsonProcessingException {

	        // Parse originalDataIds from JSON string
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<List<Integer>> originalDataIdsList = objectMapper.readValue(originalDataIds, new TypeReference<List<List<Integer>>>() {});

	        // Prepare a list to hold the results for each sub-array
	        List<Map<String, Object>> groupedResults = new ArrayList<>();

	        // Iterate through each sub-array of originalDataIds
	        for (List<Integer> dataIds : originalDataIdsList) {
	            List<Map<String, Object>> results = agentsService.getDataDetailsById(dataIds);
	            // Group results according to your business logic
	            Map<String, Object> combinedResult = new HashMap<>();
	            for (Map<String, Object> result : results) {
	                combinedResult.putAll(result);
	            }
	            groupedResults.add(combinedResult);
	        }

	        return groupedResults;
	    }
	  
	  
	  @GetMapping("/getCompletedDataFields")
	    public Map<String, Object> getCompletedDataDetailsById(
	            @RequestParam String originalCDataIds) throws JsonProcessingException {

	        // Parse originalDataIds from JSON string
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<Integer> originalCDataIdsList = objectMapper.readValue(originalCDataIds, new TypeReference<List<Integer>>() {});

	        // Prepare a list to hold the results for each sub-array
	        //List<Map<String, Object>> groupedCompletedResults = new ArrayList<>();
	        List<Integer> completedDataIds = new ArrayList<>(); // List to hold data_ids with Complete_count > 0

	        // Iterate through each sub-array of originalDataIds
	       
	            List<Map<String, Object>> results = agentsService.getCompletedDataDetailsById(originalCDataIdsList);
	            // Group results according to your business logic
	            //Map<String, Object> CompletedcombinedResult = new HashMap<>();
	            for (Map<String, Object> result : results) {
	            	
	            	if (result.get("Complete_count") != null && ((Number) result.get("Complete_count")).intValue() > 0) {
	            		completedDataIds.add((Integer) result.get("data_id"));
	                }
	            	//groupedCompletedResults.add(result);
	            }
	            
	        

	     // Prepare the final response containing grouped results and completed dataIds
	        Map<String, Object> response = new HashMap<>();
	        //response.put("groupedCompletedResults", groupedCompletedResults);
	        response.put("completedDataIds", completedDataIds);
	        return response;
	    }
	
	  @GetMapping("/getCompletedDataById")
	  public  List<Map<String, Object>> getCompletedDataById(@RequestParam List<Integer> completedDataIds){
		  return agentsService.getCompletedDataById(completedDataIds);
	  }
	  
	  
	  @PostMapping("/furtherAssignedAgents")
	  public ResponseEntity<Map<String, String>> updateAgentAssigndata(
	          @RequestBody Map<String, Object> requestData) {

	      String campaign_id = (String) requestData.get("campaign_id");
	      String category_id = (String) requestData.get("category_id");
	      String call_per_agent = (String) requestData.get("call_per_agent");
	      
	      
	      
	      

	      // Correct key name for selected agents
	      List<String> selectedAgentsList = (List<String>) requestData.get("selected_agents");

	      //System.out.println("campaign_id=" + campaign_id);
	      //System.out.println("category_id=" + category_id);
	      //System.out.println("call_per_agent=" + call_per_agent);
	      //System.out.println("selectedAgentsList=" + selectedAgentsList);
	      
	      int campaignId = Integer.parseInt(campaign_id);
	      int categoryId = Integer.parseInt(category_id);
	      int callperagent = Integer.parseInt(call_per_agent);
	      
	      agentsService.resetAgentAssignmentForNextDay(categoryId,campaignId);
	      
	      int calls_assign = callperagent * selectedAgentsList.size();
          //System.out.println("calls_assign==  " + calls_assign);
          
          List<Integer> DataIds=agentsService.getDataIdForFurtherAssign(categoryId, campaignId);
          //System.out.println("dataids=== " + DataIds);
          
          if (!DataIds.isEmpty()) {
              int dataIndex = 0; // Initialize data index pointer

              for (String agentId : selectedAgentsList) {
                  int selectedAgentId = Integer.parseInt(agentId);

                  agentsService.addAgentsForCampaign(selectedAgentId, categoryId, campaignId);
                  
                  // Iterate for noOfCalls times and assign data IDs to the current agent
                  for (int i = 0; i < callperagent && dataIndex < DataIds.size(); i++, dataIndex++) {
                      int DataId = DataIds.get(dataIndex);

                      agentsService.updateAgentForCampaign(
                          selectedAgentId,
                          categoryId,
                          campaignId,
                          DataId
                      );
                  }
              }
              
          }
          else {
              return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("status", "error", "message", "No data available for Further assign."));
          }

	      return ResponseEntity.ok(Map.of("status", "success", "message", "Campaign details saved successfully"));
	  }


	  
	  ////////////////////Ajith code//////////////////////
	  
	  @GetMapping("/Campaignview")
	    public ResponseEntity<?> getCampaignview(@RequestParam int categoryId, @RequestParam int agentId, @RequestParam int dataId) {
	        try {
	            List<Map<String, Object>> response = agentsService.getFilteredFields(categoryId, agentId, dataId);

	            if (response.isEmpty()) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matching data found.");
	            }

	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
	        }
	    }



	  @GetMapping("/questions")
	  public List<Map<String, Object>> getCampaignQuestions(
	          @RequestParam int categoryId, 
	          @RequestParam int campaignId) {
	      
	      return agentsService.getCampaignQuestions(categoryId, campaignId);
	  }

		@GetMapping("/fetchAnswer")
	    public List<Map<String, Object>> fetchAnswer(
	        @RequestParam int campaignId, 
	        @RequestParam int agentId, 
	        @RequestParam int questionId, 
	        @RequestParam int dataId) {
	      
	      return agentsService.FetchAnswer(campaignId,agentId,questionId,dataId);
	  }
		
		
		@PostMapping("/submitAnswers")
	    public ResponseEntity<String> submitAnswers(@RequestBody Map<String, List<Map<String, Object>>> requestData) {
	        List<Map<String, Object>> answers = requestData.get("answers");

	        if (answers == null || answers.isEmpty()) {
	            return ResponseEntity.badRequest().body("No answers provided.");
	        }

	        agentsService.saveAnswers(answers);
	        return ResponseEntity.ok("Answers submitted successfully");
	    }
		
		
		@GetMapping("/callstatus")
	    public List<Map<String, Object>> getCallStatus() {
		  //System.out.println("dropdown");
	        return agentsService.getCallStatus();
	    }
		
		@GetMapping("/checkCounts")
	    public List<Map<String, Object>> checkCounts(
	        @RequestParam int campaignId, 
	        @RequestParam int dataId) {
	      
	      return agentsService.checkCounts(campaignId,dataId);
	  }
		
		@PostMapping("/savecall")
		public ResponseEntity<String> savecall(@RequestBody Map<String, Object> callData) {	      
		    String call_status = (String) callData.get("call_status");
		    String remarks = (String) callData.get("remarks");
		    int data_id = Integer.parseInt(callData.get("data_id").toString());
		    int agent_id = Integer.parseInt(callData.get("agent_id").toString());
		    int campaign_id = Integer.parseInt(callData.get("campaign_id").toString());
		
		   // System.out.println("controler");
		    //System.out.println(call_status);
		    
		    // Add a debug statement to check the received values
		   // System.out.println("Received call_status: " + call_status);
		
		    if (call_status == null || call_status.isEmpty()) {
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Call status is required.");
		    }
		  
		
		    agentsService.saveCallLogs(call_status, remarks,data_id,agent_id);
		    
		    agentsService.saveInLogsTable(campaign_id, agent_id, data_id,call_status,remarks);
		    
		    return ResponseEntity.ok("Data Inserted successfully.");
		}

	  


}
