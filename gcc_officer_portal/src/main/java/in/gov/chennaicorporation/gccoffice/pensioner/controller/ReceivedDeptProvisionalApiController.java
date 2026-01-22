package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.pensioner.service.DeptAuditService;

import in.gov.chennaicorporation.gccoffice.pensioner.service.ReceivedDeptProvisionalService;

@RestController
@RequestMapping("gcc/api/pensioner/received/provisional")
public class ReceivedDeptProvisionalApiController {
	
	@Autowired
	private ReceivedDeptProvisionalService receivedDeptProvisionalService;
	
	@Autowired
	private DeptAuditService deptAuditService;
	

	@Autowired
	public ReceivedDeptProvisionalApiController(ReceivedDeptProvisionalService receivedDeptProvisionalService) {
	    this.receivedDeptProvisionalService = receivedDeptProvisionalService;
	}
	
	//sent to completed list
	@PostMapping("/updateEmployeeDetailsCompleted")
    public ResponseEntity<String> updateEmployeeDetailsCompleted(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");
            String filemovedby =(String)requestData.get("filemovedby");

        	
        	
            boolean isSaved = receivedDeptProvisionalService.updateEntryDetailsCompleted(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=receivedDeptProvisionalService.savehistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File Completed successfully!");
            	}
            	else {
            		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details in history.");
            	}
                
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving details.");
        }
    }
	
	
	
	
	// returned from pension to department
	@PostMapping("/updateEmployeeDetailsReturn")
    public ResponseEntity<String> updateEmployeeDetailsReturn(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");
            String filemovedby =(String)requestData.get("filemovedby");

        	
        	
            boolean isSaved = receivedDeptProvisionalService.updateEntryDetailsReturntoDepartment(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=receivedDeptProvisionalService.savehistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File moved to Department successfully!");
            	}
            	else {
            		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details in history.");
            	}
                
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving details.");
        }
    }
	
	
	//sent again to pension from department
	@PostMapping("/updateEmployeeDetailsSentToPension")
    public ResponseEntity<String> updateEntryDetailsSenttoPension(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");
            String filemovedby =(String)requestData.get("filemovedby");

        	
        	
            boolean isSaved = receivedDeptProvisionalService.updateEntryDetailsSenttoPension(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=receivedDeptProvisionalService.savehistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File moved to Pension successfully!");
            	}
            	else {
            		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details in history.");
            	}
                
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving details.");
        }
    }
	
	

}
