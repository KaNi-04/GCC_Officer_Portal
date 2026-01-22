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
import in.gov.chennaicorporation.gccoffice.pensioner.service.PensionAuditService;

@RequestMapping("gcc/api/pensioner/audit")
@RestController
public class PensionAuditApiController {
	
	@Autowired
	private DeptAuditService deptAuditService;
	
	@Autowired
	private PensionAuditService pensionAuditService;
	
	@PostMapping("/updateEmployeeDetails")
    public ResponseEntity<String> updateEmployeeDetails(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");
            String filemovedby =(String)requestData.get("filemovedby");

        	
        	
            boolean isSaved = pensionAuditService.updateEntryDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=pensionAuditService.savehistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("Audit Certificate Received successfully!");
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
	 
	 
	 
	 @PostMapping("/updateFamilyPensionDetails")
	    public ResponseEntity<String> updateFamilyPensionDetails(@RequestBody Map<String, Object> requestData) {
	        try {
	                      
	            // Call service method to save 
	        	String tempId = (String) requestData.get("tempId");	        	
	            String filemovedby =(String)requestData.get("filemovedby");
	            
	        	
	        	
	            boolean isSaved = pensionAuditService.updateEntryDetails(requestData);

	            if (isSaved) {
	            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
	            	System.out.println("emp_details:==:"+emp_details);
	            	boolean updated=pensionAuditService.saveFamilyPensionHistory(emp_details,filemovedby);
	            	
	            	if(updated)
	            	{
	            		return ResponseEntity.ok("Audit Certificate Received successfully!");
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
	 
	 
	 
	//pendency
	 @PostMapping("/updatePendencyDetails")
	    public ResponseEntity<String> updatePendencyDetails(@RequestBody Map<String, Object> requestData) {
	        try {
	                      
	            // Call service method to save 
	        	String tempId = (String) requestData.get("tempId");	        	
	            String filemovedby =(String)requestData.get("filemovedby");
	            
	        	
	        	
	            boolean isSaved = pensionAuditService.updateEntryDetails(requestData);

	            if (isSaved) {
	            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
	            	System.out.println("emp_details:==:"+emp_details);
	            	boolean updated=pensionAuditService.savePendencyHistory(emp_details,filemovedby);
	            	
	            	if(updated)
	            	{
	            		return ResponseEntity.ok("Audit Certificate Received successfully!");
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
