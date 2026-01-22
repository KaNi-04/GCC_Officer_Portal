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
import in.gov.chennaicorporation.gccoffice.pensioner.service.DeptPartiallyCompService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.PensionPartiallyCompService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.PensionReceivedService;

@RequestMapping("gcc/api/pensioner/partial")
@RestController
public class PensionPartiallyCompApiController {
	
	@Autowired
	public DeptPartiallyCompService deptPartiallyCompleteService;
	
	@Autowired
	public DeptAuditService deptAuditService;
	
	@Autowired
	public PensionReceivedService pensionReceivedService;
	
	@Autowired
	public PensionPartiallyCompService pensionPartiallyCompService;
	
	@PostMapping("/updateEmployeeDetails")
    public ResponseEntity<String> updateEmployeeDetails(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");	        	
            String filemovedby =(String)requestData.get("filemovedby");
            int fs1=0;
       	
            boolean isSaved = pensionPartiallyCompService.updateEntryCompletedDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchPartialCompleted(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	           	            	
            	
            	List<Map<String, Object>> zone_benefits=pensionReceivedService.fetchZonebenefitsById(tempId);
            	System.out.println("zone_benefits:==:"+zone_benefits);
            	
            	List<Map<String, Object>> pension_benefits=pensionReceivedService.fetchPensionbenefitsById(tempId);
            	System.out.println("pension_benefits:==:"+pension_benefits);
            	
            	
            	boolean updated=pensionPartiallyCompService.saveHistory(emp_details,zone_benefits,pension_benefits,filemovedby);
            	
            	if (!emp_details.isEmpty()) {  // Ensure list is not empty
            	    Map<String, Object> empDetailMap = emp_details.get(0); // Get first map from the list
            	    
            	    // Ensure the key exists in the map
            	    if (empDetailMap.containsKey("file_status")) {
            	        Object fileStatusObj = empDetailMap.get("file_status");
            	        
            	        if (fileStatusObj != null) {
            	            String fs = fileStatusObj.toString(); // Convert to String safely
            	             fs1 = Integer.parseInt(fs); // Convert to integer if needed
            	            System.out.println("file_status: " + fs1);
            	        } else {
            	            System.out.println("file_status is null");
            	        }
            	    } else {
            	        System.out.println("Key 'file_status' not found in emp_details");
            	    }
            	} else {
            	    System.out.println("emp_details list is empty");
            	}

            	
            	if(updated)
            	{
            		if(fs1==11 && fs1!=0)
            		{
            			return ResponseEntity.ok("File Completed successfully!");
            		}           		
            		else if(fs1==10 && fs1!=0){
            			return ResponseEntity.ok("File Partially Completed!");
            		}
            		else {
            			return ResponseEntity.ok("File Status is 0!");
            		}
            		
            		
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
