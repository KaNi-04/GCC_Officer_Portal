package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcAdminSettingService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@RequestMapping("gcc/api/callcenterqaqc/adminsettings")
@RestController
public class QaqcAdminSettingsApiController {
	
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	
	
	@Autowired  
    public QaqcAdminSettingsApiController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
	
	@Autowired
	private QaqcAdminSettingService adminService;
	
	@PostMapping("/saveproductivity")
	public ResponseEntity<Map<String, String>> saveproductivity(@RequestParam int productivity_count)
	{
		Map<String, String> response = new HashMap<>();
		try {
            
            // Save task ID
            int rowsInserted = adminService.saveProductivity(productivity_count);

            if (rowsInserted > 0) {
                response.put("status", "success");
                response.put("message", "Productivity inserted successfully");
                return ResponseEntity.ok(response);
            }

            response.put("status", "fail");
            response.put("message", "Productivity not saved");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);  // Change to BAD_REQUEST for failure

        }  catch (Exception e) {
            // Logging the error
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error saving task", e);
            
            response.put("status", "error");
            response.put("message", "Failed to save productivity");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
	}
	
	
	@PostMapping("/updateproductivity")
	public ResponseEntity<Map<String, String>> updateProductivityById(@RequestBody Map<String, Integer> requestData)
	{			Map<String, String> response = new HashMap<>();
		try {
            
            // Save task ID
	        int productivityCount = requestData.getOrDefault("productivity_count", 0); // Default to 0 if not provided
	        int rowsUpdated = adminService.updateProductivity(productivityCount);

	        
            if (rowsUpdated > 0) {
                response.put("status", "success");
                response.put("message", "Productivity saved successfully");
                return ResponseEntity.ok(response);
            }

            response.put("status", "fail");
            response.put("message", "Productivity not saved");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);  // Change to BAD_REQUEST for failure

        }  catch (Exception e) {
            // Logging the error
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error saving task", e);
            
            response.put("status", "error");
            response.put("message", "Failed to save productivity");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
	}
	
	@GetMapping("/getproductivity")
	public Integer getProductivity()
	{
		return adminService.getProductivity();
	}
	
	@GetMapping("/getadminsettingcounts")
	public List<Map<String, Object>> getadminsettingcounts()
	{
		return adminService.getAdminSettingCounts();
				 
	}
	
	@GetMapping("/assigncallingtype")
	public List<Map<String, Object>> assigncallingtype()
	{
		return adminService.getAgentsForCallingtype();
				 
	}
	
	@PostMapping("/updateCallType") // Bulk update endpoint
	public ResponseEntity<Map<String, String>> updateCallType(@RequestBody List<Map<String, Object>> selectedAgents) {
					
	    Map<String, String> response = new HashMap<>();

	    try {
	        int rowsUpdated = 0;
	       
	        // Loop through each selected agent and update their calling_type
	        for (Map<String, Object> agent : selectedAgents) {
	            String callingType = (String) agent.get("calling_type");

	            // Parse agent_id to Integer if it's passed as a String
	            int agentId = Integer.parseInt(agent.get("agent_id").toString());  // Ensure it's parsed as Integer

	            // Update calling type for the agent
	            rowsUpdated += adminService.updateCallType(callingType, agentId);
	            
	            
	        }
	        adminService.saveCallingTypeInSettings();

	        if (rowsUpdated > 0) {
	            response.put("status", "success");
	            response.put("message", "Agents Assigned successfully");
	            return ResponseEntity.ok(response);
	        } else {
	            response.put("status", "error");
	            response.put("message", "No agents updated");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }
	    } catch (Exception e) {
	        response.put("status", "error");
	        response.put("message", "Error occurred while updating calling types: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	@PostMapping("/updateagentname")
	public ResponseEntity<Map<String, String>> updateAgentnameById(@RequestBody Map<String, String> requestData){
		Map<String, String> response = new HashMap<>();
		
		try {
	        // Extract and convert agent_id to int
	        String agentIdStr = requestData.get("agent_id");
	        String updatedName = requestData.get("agent_name");

	        // Validate input
	        if (agentIdStr == null || updatedName == null || updatedName.trim().isEmpty()) {
	            response.put("status", "error");
	            response.put("message", "Agent ID and name are required!");
	            return ResponseEntity.badRequest().body(response);
	        }

	        int agentId;
	        try {
	            agentId = Integer.parseInt(agentIdStr);
	        } catch (NumberFormatException e) {
	            response.put("status", "error");
	            response.put("message", "Invalid Agent ID format!");
	            return ResponseEntity.badRequest().body(response);
	        }

	        // Call service layer to update the agent name
	        int updatedRows = adminService.updateAgentName(agentId, updatedName); // Now agentId is an int

	        if (updatedRows == -1) { // If name already exists
	            response.put("status", "error");
	            response.put("message", "Agent name already exists!");
	            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	        } else if (updatedRows > 0) {
	            response.put("status", "success");
	            response.put("message", "Agent name updated successfully!");
	            return ResponseEntity.ok(response);
	        } else {
	            response.put("status", "error");
	            response.put("message", "Agent not found or update failed.");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }

	    }
		catch (Exception e) {
	        response.put("status", "error");
	        response.put("message", "Error occurred while updating Agent name " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
}
