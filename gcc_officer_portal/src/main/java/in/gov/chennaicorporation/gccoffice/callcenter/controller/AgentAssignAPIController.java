package in.gov.chennaicorporation.gccoffice.callcenter.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.callcenter.service.AgentAssignService;


@RequestMapping("/gcc/api/callcenter/admin")
@RestController("callcentercontroller")
public class AgentAssignAPIController
{
	private AgentAssignService campaignmaster;
	
	@Autowired
	public AgentAssignAPIController(AgentAssignService campaignmaster) {
		this.campaignmaster = campaignmaster;
	}
	
	@GetMapping("/getcategory")
	public List<Map<String, Object>> getcategory()
	{
		return campaignmaster.getcategory();			
	}
	
	@GetMapping("/getcampaignbycategory")
	public List<Map<String, Object>> getcampaignbycategory(@RequestParam int id)
	{
		return campaignmaster.getcampaignbycategory(id);
	}
	
//	@GetMapping("/getcampaignbyid")
//	public  List<Map<String, Object>> getcampaignbyid(@RequestParam int id)
//	{
//		return campaignmaster.getcampaignbyid(id);
//	}
	
	@GetMapping("/getagentsfortask")
	public  List<Map<String, Object>> getagentsfortask(@RequestParam int id)
	{
		return campaignmaster.getagentsfortask(id);
	}
	
	@PostMapping("/addagentsfortask")
	public ResponseEntity<List<Map<String, Object>>> addagentsfortask(@RequestBody List<Map<String, Object>> requestList) {
	    //System.out.println("Received POST request with data: " + requestList);

	    List<Map<String, Object>> insertedAgents = new ArrayList<>();  // To store successfully inserted agents

	    try {
	        for (Map<String, Object> request : requestList) {
	            int agentId = (int) request.get("agentId");
	            int categoryId = (int) request.get("categoryId");
	            int campaignId = (int) request.get("campaignId");

	            // Call the service method to insert the agent details
	            int rowsAffected = campaignmaster.addagentsfortask(agentId, categoryId, campaignId);

	            if (rowsAffected > 0) {
	                // If the insert was successful, add the agent data to the response list
	                Map<String, Object> insertedAgent = new HashMap<>();
	                insertedAgent.put("categoryId", categoryId);
	                insertedAgent.put("campaignId", campaignId);
	                insertedAgent.put("agentId", agentId);

	                insertedAgents.add(insertedAgent);  // Add the successfully inserted agent to the list
	            } else {
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                        .body(Collections.singletonList(Collections.singletonMap("error", "Failed to add agent for campaignId: " + campaignId)));
	            }
	        }

	        // Return the list of successfully inserted agents
	        return ResponseEntity.status(HttpStatus.CREATED).body(insertedAgents);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Collections.singletonList(Collections.singletonMap("error", "Invalid request data: " + e.getMessage())));
	    }
	}
	
	@GetMapping("/getassignedcampaign")
	public  List<Map<String, Object>> checkcampaigninassignedtable(@RequestParam int id)
	{
		return campaignmaster.checkcampaigninassignedtable(id);
	}

	@GetMapping("/deleteassignedbyid")
	public int editagentsfortask(@RequestParam int id)
	{
		return campaignmaster.editagentsfortask(id);
	}
	
	  @GetMapping("/getcampaignbyid")
		public  List<Map<String, Object>> getcampaignquestionbyid(@RequestParam int id){
			return campaignmaster.getcampaignquestionbyid(id);
		}
}
