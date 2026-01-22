package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.pensioner.service.DeptAuditService;

@RequestMapping("gcc/api/department/audit")
@RestController
public class DeptAuditApiController {
	
	@Autowired
	private DeptAuditService deptAuditService;

	
	@GetMapping("/getemployeedetails")
    public List<Map<String, Object>> getEmployeedetailsbyCode(
            @RequestParam String tempId) {

        List<Map<String, Object>> employeeDetails = deptAuditService.fetchEmployeeById(tempId);
        System.out.println("employeeDetails==="+employeeDetails);
        return employeeDetails;
    }
	
	
	
	@GetMapping("/getemployeedetailscps")
    public List<Map<String, Object>> getEmployeedetailsbyCodeCps(
            @RequestParam String tempId) {

        List<Map<String, Object>> employeeDetails = deptAuditService.fetchEmployeeByIdCps(tempId);
        System.out.println("employeeDetails==="+employeeDetails);
        return employeeDetails;
    }
 
 @GetMapping("/getzonebenefitsdetails")
    public List<Map<String, Object>> getdetailsWithZonebenefits(
            @RequestParam String tempId) {

        List<Map<String, Object>> zoneBenefitDetails = deptAuditService.fetchDetailsWithZoneBenefitsById(tempId);
        System.out.println("zoneBenefitDetails==="+zoneBenefitDetails);
        return zoneBenefitDetails;
    }
 
 @GetMapping("/getfilependingbenefits")
    public List<Map<String, Object>> getAllBenefitsDetails(
            @RequestParam String tempId) {

        List<Map<String, Object>> allBenefitDetails = deptAuditService.fetchFilePendingBenefits(tempId);
        System.out.println("allBenefitDetails==="+allBenefitDetails);
        return allBenefitDetails;
    }
 
 @GetMapping("/getpartialdetails")
    public List<Map<String, Object>> getAllPartialDetails(
            @RequestParam String tempId) {

        List<Map<String, Object>> partialDetails = deptAuditService.fetchPartialCompleted(tempId);
        System.out.println("partialDetails==="+partialDetails);
        return partialDetails;
    }
 
 
 
 @GetMapping("/fileMovementhistory")
    public List<Map<String, Object>> getFileMovementHistory(@RequestParam String tempId) {
        List<Map<String, Object>> fileHistory = deptAuditService.fetchFileMovementHistory(tempId);
        
        return fileHistory;
 }
 
 
 @PostMapping("/updateEmployeeDetails")
    public ResponseEntity<String> updateEmployeeDetails(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");	        	
            String filemovedby =(String)requestData.get("filemovedby");
            
        	
        	
            boolean isSaved = deptAuditService.updateEntryDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=deptAuditService.savehistory(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File moved to Return from Audit successfully!");
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
	 
	//update the family pension details
	 
		 @PostMapping("/updateFamilyPensionDetails")
		    public ResponseEntity<String> updateFamilyPensionDetails(@RequestBody Map<String, Object> requestData) {
		        try {
		                      
		            // Call service method to save 
		        	String tempId = (String) requestData.get("tempId");	        	
		            String filemovedby =(String)requestData.get("filemovedby");
		            
		        	
		        	
		            boolean isSaved = deptAuditService.updateEntryDetails(requestData);

		            if (isSaved) {
		            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
		            	System.out.println("emp_details:==:"+emp_details);
		            	boolean updated=deptAuditService.saveFamilyPensionHistory(emp_details,filemovedby);
		            	
		            	if(updated)
		            	{
		            		return ResponseEntity.ok("File moved to Return from Audit successfully!");
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
	            
	        	
	        	
	            boolean isSaved = deptAuditService.updateEntryDetails(requestData);

	            if (isSaved) {
	            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
	            	System.out.println("emp_details:==:"+emp_details);
	            	boolean updated=deptAuditService.savePendencyHistory(emp_details,filemovedby);
	            	
	            	if(updated)
	            	{
	            		return ResponseEntity.ok("File moved to Return from Audit successfully!");
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
