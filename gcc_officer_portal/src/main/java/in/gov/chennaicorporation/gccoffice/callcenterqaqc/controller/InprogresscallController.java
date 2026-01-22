package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.InprogresscallService;
import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcCallsService;

@RequestMapping("gcc/api/callcenterqaqc/agent/inprogresscalls") 
@RestController

public class InprogresscallController {
	
	@Autowired
	InprogresscallService inprogresscallService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired	
	private QaqcCallsService qaqcCallsService;
	
	@PostMapping("/update")
	@ResponseBody
	public ResponseEntity<String> updateCallDetails(@RequestParam("complaintNumber") String complaintNumber,
	                                                @RequestParam("dataId") int dataId,
	                                                @RequestParam("userId") String userId,
	                                                @RequestParam("action") String action,
	                                                @RequestParam(value = "remainderDate", required = false,defaultValue = "-") String remainderDate,
	                                                @RequestParam("remarks") String remarks) {
	    try {
//	        System.out.println("Updating Complaint Number: " + complaintNumber);
//	        System.out.println("Updating Data ID: " + dataId);
//	        System.out.println("Action: " + action);
//	        System.out.println("Remarks: " + remarks);
	        int updated_agent = Integer.parseInt(userId);
//			System.out.println("updated_agent: " + updated_agent);
	        
	      //to insert incorrect office mobile number details in table
            if (action.equals("MOBILE_NUM")) {
            	inprogresscallService.saveMobileNumDetails(complaintNumber, remarks);
            }
            
	        Map<String, Object> complaintDetails = qaqcCallsService.getComplaintDetails(dataId);

	        Integer agentId = (Integer) complaintDetails.get("agent_id");
		    Map<String, Object> erp_username=qaqcCallsService.getErpnameById(agentId);
		    String Erpname=(String) erp_username.get("erp_username");
	        		    
		    if ("WRONG_ASSIGN".equals(action)) {
	        	
	            boolean statusUpdated = qaqcCallsService.ChangeStatusInErp(complaintNumber, remarks, action, Erpname);

	            if (!statusUpdated) {
	                return ResponseEntity.ok("error");
	            }
	        }
		    
	        // Update complaint details
	        inprogresscallService.updateInprogressComplaintDetails(complaintNumber, dataId, action, remarks,updated_agent);	        
	        inprogresscallService.updateComplaintHistoryDetails(complaintNumber, dataId, action, remarks,updated_agent, remainderDate);
	        Map<String, Object> logDetails = qaqcCallsService.getComplaintDetails(dataId);
	        // Upload to logs
	       qaqcCallsService.uploadComplaintDetailsInLogs(logDetails, action, complaintNumber,remainderDate);

	        return ResponseEntity.ok("Success");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
	    }
	}
	
	@GetMapping("/getinprogresssubmittedcalls")
	public List<Map<String, Object>> getInprogressSubmitDetails(@RequestParam int userId)
	{
		return inprogresscallService.getInprogressSubmitDetails(userId);
	}

}
