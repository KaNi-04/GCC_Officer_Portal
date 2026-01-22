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
import in.gov.chennaicorporation.gccoffice.pensioner.service.FileClosingService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.NewEntryService;

@RequestMapping("gcc/api/fileclosing")
@RestController
public class FileClosingApiController {
	
	
	@Autowired
	private FileClosingService fileClosingService;
	
	
	@Autowired
	private NewEntryService newEntryService;
	
	@Autowired
	private DeptAuditService deptAuditService;
	
	@GetMapping("/getdetails")	
	 public List<Map<String, Object>> getEmployeedetailsbyCode( @RequestParam String empCode, @RequestParam String deptId){
		
			int dept_id=Integer.parseInt(deptId);
		
			int file_cat= newEntryService.getRetirementFileCategoryId();
			System.out.println("file_cat>>>>>>"+file_cat);
			String tempId=fileClosingService.getTempId(empCode,dept_id,file_cat);
			
			System.out.println("empCode>>>>>"+empCode);
			System.out.println("deptId>>>>>>"+dept_id);
			System.out.println("tempId>>>>>>"+tempId);
			
	        List<Map<String, Object>> employeeDetails = fileClosingService.fetchEmployeeByTempId(tempId);
	        System.out.println("employeeDetails==="+employeeDetails);
	        
	        return employeeDetails;
	    }
	
		
			@GetMapping("/getemployeedetails")
		    public List<Map<String, Object>> getEmployeedetailsbyCode(
		            @RequestParam String tempId) {
				
				System.out.println("temporary==="+tempId);
		
		        List<Map<String, Object>> employeeDetails = fileClosingService.fetchEmployeeById(tempId);
		        System.out.println("employeeDetails==="+employeeDetails);
		        return employeeDetails;
		    }
			
			
			@PostMapping("/updateDetails")
		    public ResponseEntity<String> updateDetails(@RequestBody Map<String, Object> requestData) {
		        try {
		                      
		            // Call service method to save 
		        	String tempId = (String) requestData.get("tempId");	        	
		           String filemovedby =(String)requestData.get("filemovedby");
		           
		           
		        	System.out.println("data"+requestData);
		        	
		            boolean isSaved = fileClosingService.updateEntryDetails(requestData);
		            
		            

		            if (isSaved) {
		            	List<Map<String, Object>> emp_details=deptAuditService.fetchEmployeeById(tempId);
		            	System.out.println("emp_details:==:"+emp_details);
		            	boolean updated=fileClosingService.saveclosinghistory(emp_details,filemovedby);
		            	
		            	if(updated)
		            	{
		            		return ResponseEntity.ok("File Closed successfully!");
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
	
			
			//closed list api
			
			@GetMapping("/getcloseddetails")	
			 public List<Map<String, Object>> getClosedEmployeedetails(){
								
			        List<Map<String, Object>> employeeDetails = fileClosingService.fetchClosingEmployee();
			        System.out.println("employeeDetails==="+employeeDetails);
			        
			        return employeeDetails;
			    }
	
	
	

}
