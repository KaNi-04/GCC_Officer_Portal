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
import in.gov.chennaicorporation.gccoffice.pensioner.service.ReturnFromAuditService;

@RequestMapping("gcc/api/department/return")
@RestController
public class ReturnFromAuditApiController {

	@Autowired
	private ReturnFromAuditService returnFromAuditService;
	
	@Autowired
	private DeptAuditService deptAuditService;
	
	@PostMapping("/updateEmployeeDetails")
    public ResponseEntity<String> updateEmployeeDetails(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");
            String filemovedby =(String)requestData.get("filemovedby");
        	
            boolean isSaved = returnFromAuditService.updateEntryDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=returnFromAuditService.savehistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File sent to Audit successfully!");
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
	
	
	@PostMapping("/updateReturnFamilyPension")
    public ResponseEntity<String> updateReturnFamilyPension(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");	        	
            String filemovedby =(String)requestData.get("filemovedby");
            
        	
        	
            boolean isSaved = returnFromAuditService.updateEntryDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=returnFromAuditService.saveFamilyPensionReturnHistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File sent to Audit successfully!");
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
	
	
	@PostMapping("/updateReturnPendency")
    public ResponseEntity<String> updatePendencyPension(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");	        	
            String filemovedby =(String)requestData.get("filemovedby");
            
        	
        	
            boolean isSaved = returnFromAuditService.updateEntryDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=returnFromAuditService.savePendencyHistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File sent Audit successfully!");
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
