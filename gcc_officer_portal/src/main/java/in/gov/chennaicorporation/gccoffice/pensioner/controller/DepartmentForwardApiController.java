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

import in.gov.chennaicorporation.gccoffice.pensioner.service.DepartmentForwardService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.DeptAuditService;

@RequestMapping("gcc/api/pension/forward")
@RestController
public class DepartmentForwardApiController {
	
	@Autowired
	private DeptAuditService deptAuditService;
	
	@Autowired
	private DepartmentForwardService departmentForwardService;
	
	
	@PostMapping("/updateEmployeeDetails")
    public ResponseEntity<String> updateEmployeeDetails(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");	        	
            String filemovedby =(String)requestData.get("filemovedby");
       	
            boolean isSaved = departmentForwardService.updateEntryDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=departmentForwardService.savehistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File Forward to Department successfully!");
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
	
	//family pension
	@PostMapping("/updateForwardFamilyPension")
    public ResponseEntity<String> updateForwardFamilyPension(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");	        	
            String filemovedby =(String)requestData.get("filemovedby");
            
        	
        	
            boolean isSaved = departmentForwardService.updateEntryDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=departmentForwardService.saveFamilyPensionForwardHistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File Forward to Department successfully!");
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
	
	@PostMapping("/updateForwardPendency")
    public ResponseEntity<String> updateForwardPendency(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");	        	
            String filemovedby =(String)requestData.get("filemovedby");
            
        	
        	
            boolean isSaved = departmentForwardService.updateEntryDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=departmentForwardService.savePendencyForwardHistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File Forward to Department successfully!");
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
