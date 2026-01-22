package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcAdminSettingService;
import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcAgentAssignService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;


@RestController
@RequestMapping("gcc/api/callcenterqaqc/agentassign")
public class QaqcAgentAssignAPIController {
	
	private final RestTemplate restTemplate;
	
	@Autowired  
    public QaqcAgentAssignAPIController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
	
	@Autowired
	private QaqcAgentAssignService agentAssignService;
	
	@Autowired
	private QaqcAdminSettingService adminService;
	
	
			
	@GetMapping("/getavailableagents")
	public ResponseEntity<Map<String, Object>> getAvailableAgents(@RequestParam int totalDataCount) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        // Fetch available agents
	        List<Map<String, Object>> availableAgents = agentAssignService.getAvailableOutboundAgents();
	        Integer productivity_count = adminService.getProductivity();

	        if (availableAgents.isEmpty() || totalDataCount == 0) {
	            response.put("status", "error");
	            response.put("message", "No available agents or totalDataCount must be greater than 0.");
	            return ResponseEntity.ok(response);
	        }
	        
	        
	        // Process and filter agents based on productivity_count
	        List<Map<String, Object>> filteredAgents = new ArrayList<>();
	        for (Map<String, Object> agent : availableAgents) {
	            Integer agentId = (Integer) agent.get("agent_id");
	            String agentName = (String) agent.get("agent_name");
	            Integer todayCount=agentAssignService.getTodayCountForAgent(agentId);
	            Integer campaignPendingCount = agentAssignService.getCampaignPendingCountForAgent(agentId);
	            Integer pendingCounts=todayCount+campaignPendingCount;
	         // Log the details of each agent
	            //System.out.println("Agent ID: " + agentId + ", Today Count: " + todayCount +", Campaign Pending Count: " + campaignPendingCount + ", Pending Counts: " + pendingCounts);
	            
	            if(pendingCounts<productivity_count) {
	            filteredAgents.add(Map.of(
	                "agent_id", agentId,
	                "agent_name", agentName,
	                "campaignPendingCount", campaignPendingCount,
	                "todayCount",todayCount
	            ));
	            //System.out.println("Agent added to filteredAgents: " + agentName);

	            }	           
	            
	        }
	        //System.out.println("Filtered Agents Size: " + filteredAgents.size());

	        
	        if (filteredAgents.isEmpty()) {
	            response.put("status", "error");
	            response.put("message", "No Available Agents,To continue Increase the Productivity");
	            return ResponseEntity.ok(response);
	        }
	        
	        // Calculate agent_available_count based on filtered agents
	        int agent_available_count = filteredAgents.size();
	        
	        // Calculate calls per agent using filtered agent count
	        int calls_per_agent = totalDataCount / agent_available_count;
	        int remainder = totalDataCount % agent_available_count;

	        // Distribute calls_per_agent and remainder
	        List<Map<String, Object>> finalFilteredAgents = new ArrayList<>();
	        filteredAgents.sort(Comparator.comparingInt(agent -> (Integer) agent.get("campaignPendingCount")));

	        for (int i = 0; i < filteredAgents.size(); i++) {
	            Map<String, Object> agent = filteredAgents.get(i);
	            Integer agentId = (Integer) agent.get("agent_id");
	            String agentName = (String) agent.get("agent_name");
	            Integer todayCount = (Integer) agent.get("todayCount");
	            Integer campaignPendingCount = (Integer) agent.get("campaignPendingCount");
	            // Add 1 extra call for agents handling the remainder, starting from the first agent
	            int additionalCalls = (i < remainder) ? 1 : 0;
	            int totalCallsCount = todayCount+campaignPendingCount + calls_per_agent + additionalCalls;

	            if (totalCallsCount < productivity_count) {
	                finalFilteredAgents.add(Map.of(
	                    "agent_id", agentId,
	                    "agent_name", agentName,
	                    "totalCallsCount", totalCallsCount,
	                    "todayCount",todayCount,
	                    "campaignPendingCount", campaignPendingCount,
	                    "calls_per_agent", calls_per_agent + additionalCalls
	                ));
	               // System.out.println("Agent added to final======filteredAgents: " + agentName);
	            }
	        }
	        //System.out.println("Final Filtered Agents Size: " + finalFilteredAgents.size());
	        
	        if (finalFilteredAgents.isEmpty()) {
	            response.put("status", "error");
	            response.put("message", "No Available Agents,To continue Increase the Productivity");
	            return ResponseEntity.ok(response);
	        }
	        
	        
	        int agent_available_count2 = finalFilteredAgents.size();
	        
	        // Calculate calls per agent using filtered agent count
	        int calls_per_agent2 = totalDataCount / agent_available_count2;
	        int remainder2 = totalDataCount % agent_available_count2;

	        // Distribute calls_per_agent and remainder
	        List<Map<String, Object>> LastfinalFilteredAgents = new ArrayList<>();
	        finalFilteredAgents.sort(Comparator.comparingInt(agent -> (Integer) agent.get("campaignPendingCount")));

	        for (int i = 0; i < finalFilteredAgents.size(); i++) {
	            Map<String, Object> agent = finalFilteredAgents.get(i);
	            Integer agentId = (Integer) agent.get("agent_id");
	            String agentName = (String) agent.get("agent_name");
	            Integer todayCount1 = (Integer) agent.get("todayCount");
	            Integer campaignPendingCount = (Integer) agent.get("campaignPendingCount");
	            // Add 1 extra call for agents handling the remainder, starting from the first agent
	            int additionalCalls1 = (i < remainder2) ? 1 : 0;
	            int totalCallsCount1 = todayCount1+campaignPendingCount + calls_per_agent2 + additionalCalls1;

	            if (totalCallsCount1 < productivity_count) {
	            	LastfinalFilteredAgents.add(Map.of(
	                    "agent_id", agentId,
	                    "agent_name", agentName,
	                    "totalCallsCount", totalCallsCount1,
	                    "todayCount1",todayCount1,
	                    "campaignPendingCount", campaignPendingCount,
	                    "calls_per_agent", calls_per_agent2 + additionalCalls1
	                ));
	               // System.out.println("Agent added to Lastfinal======LastfinalFilteredAgents: " + agentName);
	            }
	        }
	        //System.out.println("Filtered Agents Size: " + LastfinalFilteredAgents.size());
	        
	        if (LastfinalFilteredAgents.isEmpty()) {
	            response.put("status", "error");
	            response.put("message", "No Available Agents,To continue Increase the Productivity");
	            return ResponseEntity.ok(response);
	        }

	        // Build response
	        response.put("status", "success");
	        response.put("message", "Available agents fetched successfully");
	        response.put("available_agents", LastfinalFilteredAgents);
	        response.put("productivity_count", productivity_count);
	        response.put("agent_available_count", LastfinalFilteredAgents.size());

	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace();

	        // Return error response in case of failure
	        response.put("status", "error");
	        response.put("message", "Failed to fetch available agents");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	
	@PostMapping("/taskassign")
	public ResponseEntity<Map<String, Object>> assignTasks(@RequestBody Map<String, Object> requestData) {
	    Map<String, Object> response;
	    try {
	        // Delegate transactional work to the service
	        response = agentAssignService.assignTasksTransactionally(requestData);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response = new HashMap<>();
	        response.put("status", "error");
	        response.put("message", "An error occurred: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	@GetMapping("/deleteRecords")
	public ResponseEntity<String> deleteRecordsInUploadData() {
	    try {
	        int rowsAffected = agentAssignService.deleteUploadDataTable();
	        if (rowsAffected > 0) {
	            return ResponseEntity.ok("success");
	        } else {
	            return ResponseEntity.ok("No records to delete");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting records");
	    }
	}
	
	
}
