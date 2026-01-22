package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcAgentsService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("gcc/api/callcenterqaqc/agent")
@RestController
public class QaqcAgentsAPIController {
	
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	
	
	@Autowired  
    public QaqcAgentsAPIController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
	
	@Autowired
	private QaqcAgentsService agentsService;
	
		
	@GetMapping("/getdataforagents")
	public ResponseEntity<Map<String, Object>> getdataforagents(@RequestParam int agent_id,@RequestParam int qaqc_id)
	{
		Map<String, Object> response = new HashMap<>();
		
		try {
			
		List<Map<String, Object>> tasks = agentsService.getdataforagents(agent_id,qaqc_id);
		
		if (tasks.isEmpty()) {
            
            response.put("status", "no_data");
            response.put("message", "No data found for the given agent_id and qaqc_id.");
        } else {
            // Success case
            response.put("status", "success");
            response.put("message", "Data retrieved successfully.");
            response.put("tasks", tasks);
        } 
        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", "An error occurred: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
					
	}
	
	@PostMapping("/savecallstatus")
	public ResponseEntity<String> savecallstatus(@RequestParam String call_status,@RequestParam String remarks,@RequestParam String complaintNumber)//get full body data 
	{
				
		agentsService.savecallstatus(call_status, remarks,complaintNumber);//get all dbdata and save here and service also
		agentsService.savecallstatusinlogs(call_status, remarks,complaintNumber);//get all dbdata and save here and service also
       
        return ResponseEntity.ok("Data Inserted successfully.");
					
	}

	
	
	@GetMapping("/getagentperformancecount")
    public List<Map<String, Object>> getAgentPerformanceCount(
            @RequestParam(required = false) String startDate, 
            @RequestParam(required = false) String endDate) {
        
    	List<Map<String, Object>> counts = agentsService.getAgentPerformanceCount(startDate, endDate);
//    	System.out.println("fromdates: " + startDate);
//    	System.out.println("todate: " + endDate);
//    	System.out.println("overallcounts: " + counts );
        return counts;
    }
		
	@GetMapping("/getagentperformancelist")
    public List<Map<String, Object>> getAgentPerformanceList(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {
        
        // Fetch agent performance data from service
        List<Map<String, Object>> agentPerformanceData = agentsService.getAgentPerformanceList(startDate, endDate);

        // Log or return the data
        //System.out.println("Agent Performance Data: " + agentPerformanceData);
        return agentPerformanceData;
    }
	
	@GetMapping("/agenttaskwise")
	public List<Map<String, Object>> getAgentTaskwiseData(@RequestParam String agentId,@RequestParam String startDate,@RequestParam String endDate) {
	    // Call the service to fetch agent task-wise data
	    List<Map<String, Object>> agentTaskwiseData = agentsService.getAgentTaskwiseData(agentId, startDate, endDate);
	    //System.out.println("Agent Task Wise Data: " + agentTaskwiseData);
	    return agentTaskwiseData;
	}
	
	@GetMapping("/agentattendedcomplaints")
	public List<Map<String,Object>> getAttendedComplaintStatus(
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate,
	        @RequestParam(required = false) String complaintNumber,
	        @RequestParam(required = false) String complaintMobileNumber){

	    List<Map<String, Object>> agentAttendedData = agentsService.getAttendedComplaintStatus(startDate, endDate, complaintNumber, complaintMobileNumber);
	    //System.out.println("Agent attended Data: " + agentAttendedData);
	    return agentAttendedData;
	}
	
	@GetMapping("/attendedcomplaintshistory")
	public List<Map<String,Object>> getAttendedComplainthistory(
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate,
	        @RequestParam(required = false) String complaintNumber,
	        @RequestParam(required = false) String complaintMobileNumber){

	    List<Map<String, Object>> agentAttendedData = agentsService.getAttendedComplainthistory(startDate, endDate, complaintNumber, complaintMobileNumber);
	    //System.out.println("Agent attended Data: " + agentAttendedData);
	    return agentAttendedData;
	}
	
	
	@GetMapping("/agenttaskassign")
	public List<Map<String, Object>> getAgentTaskwiseAssign(@RequestParam String agentId) {
	// Call the service to fetch agent task-wise data
	List<Map<String, Object>> agentTaskwiseAssign = agentsService.getAgentTaskwiseAssign(agentId);
	//System.out.println("Agent Task Wise Assign: " + agentTaskwiseAssign);
	return agentTaskwiseAssign;
	}
	
	
	
}
