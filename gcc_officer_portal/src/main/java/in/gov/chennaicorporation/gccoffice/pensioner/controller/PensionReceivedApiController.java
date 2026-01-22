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
import in.gov.chennaicorporation.gccoffice.pensioner.service.PensionReceivedService;

@RequestMapping("gcc/api/department/received")
@RestController
public class PensionReceivedApiController {

	@Autowired
	private DeptAuditService deptAuditService;
	
	@Autowired
	private PensionReceivedService pensionReceivedService;
	
	@PostMapping("/updateEmployeeDetails")
    public ResponseEntity<String> updateEmployeeDetails(@RequestBody Map<String, Object> requestData) {
        try {
            // Call service method to save 
        	String empNo = (String) requestData.get("empNo");
        	String tempId = (String) requestData.get("tempId");      	
            String filemovedby =(String)requestData.get("filemovedby");
                    	
            boolean isSaved = pensionReceivedService.updateEntryDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	List<Map<String, Object>> zone_benefits=pensionReceivedService.fetchZonebenefitsById(tempId);
            	System.out.println("zone_benefits:==:"+zone_benefits);
            	
            	boolean updated=pensionReceivedService.saveHistory(emp_details,zone_benefits,filemovedby);
            	
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
	
	//gis
	@PostMapping("/updateEmployeeDetailsgis")
    public ResponseEntity<String> updateEmployeeDetailsGis(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save 
        	String tempId = (String) requestData.get("tempId");	        	
            String filemovedby =(String)requestData.get("filemovedby");
       	
            boolean isSaved = pensionReceivedService.updateEntryGisDetails(requestData);

            if (isSaved) {
            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
            	System.out.println("emp_details:==:"+emp_details);
            	boolean updated=pensionReceivedService.savehistoryGis(emp_details,filemovedby);
            	
            	if(updated)
            	{
            		return ResponseEntity.ok("File completed successfully!");
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
