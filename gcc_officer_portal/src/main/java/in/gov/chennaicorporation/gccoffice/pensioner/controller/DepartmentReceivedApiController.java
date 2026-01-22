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

import in.gov.chennaicorporation.gccoffice.pensioner.service.DepartmentReceivedService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.DeptAuditService;

@RequestMapping("gcc/api/pension/received")
@RestController
public class DepartmentReceivedApiController {
	
	@Autowired
	private DeptAuditService deptAuditService;
	
	@Autowired
	private DepartmentReceivedService departmentReceivedService;
	
	@PostMapping("/updateforretirementcompleted")
	public ResponseEntity<String> updateRetirementEmployeeDetailsCompleted(@RequestBody Map<String, Object> requestData) {
	    try {
	        // Extract request parameters
	        String tempId = (String) requestData.get("tempId");
	        String filemovedby = (String) requestData.get("filemovedby");
	        int file_Status = 0; // Initialize file_status

	        // Call service method to save the details
	        boolean isSaved = departmentReceivedService.updateEntryCompletedDetails(requestData);

	        if (isSaved) {
	            // Fetch employee details
	            List<Map<String, Object>> emp_details = deptAuditService.fetchEmployeeById(tempId);
	            System.out.println("emp_details:==:" + emp_details);

	            // Fetch pension benefits details
	            List<Map<String, Object>> pension_details = departmentReceivedService.fetchPensionbenefitsById(tempId);
	            System.out.println("pension_details:==:" + pension_details);

	            // Save retirement completed history
	            boolean updated = departmentReceivedService.saveRetirementCompletedhistory(emp_details, pension_details, filemovedby);

	            // Retrieve file_status from emp_details
	            if (!emp_details.isEmpty()) {
	                Map<String, Object> firstEmployeeDetails = emp_details.get(0);
	                Object fileStatusObj = firstEmployeeDetails.get("file_status");

	                if (fileStatusObj != null) {
	                    try {
	                        file_Status = Integer.parseInt(fileStatusObj.toString());
	                        System.out.println("Parsed file_status: " + file_Status);
	                    } catch (NumberFormatException e) {
	                        System.err.println("Failed to parse file_status: " + fileStatusObj);
	                    }
	                } else {
	                    System.out.println("file_status is null in emp_details.");
	                }
	            } else {
	                System.out.println("emp_details is empty.");
	            }

	            // Final response logic
	            if (updated && file_Status != 0) {
	                if (file_Status == 11) {
	                    return ResponseEntity.ok("File Completed successfully!");
	                } else if(file_Status == 9) {
	                    //return ResponseEntity.ok("File Moved Department to Complete Pending fields!");
	                	return ResponseEntity.ok("File Moved to Department Partially Completed!");
	                }
	                else {
	                    //return ResponseEntity.ok("File Moved Department to Complete Pending fields!");
	                	return ResponseEntity.ok("File Moved to Pension Partially Completed!");
	                }
	            } else {
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                        .body("Failed to save details in history or invalid file_status.");
	            }
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details for completed.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error occurred while saving details.");
	    }
	}
	
	
	@PostMapping("/updateforretirementpending")
	public ResponseEntity<String> updateRetirementEmployeeDetailsPending(@RequestBody Map<String, Object> requestData) {
	    try {
	        // Extract request parameters
	        String tempId = (String) requestData.get("tempId");
	        String filemovedby = (String) requestData.get("filemovedby");
	        

	        // Call service method to save the details
	        boolean isSaved = departmentReceivedService.updateEntryPendingDetails(requestData);

	        if (isSaved) {
	            // Fetch employee details
	            List<Map<String, Object>> emp_details = deptAuditService.fetchEmployeeById(tempId);
	            System.out.println("emp_details:==:" + emp_details);

	            // Fetch pension benefits details
	            List<Map<String, Object>> pension_details = departmentReceivedService.fetchPensionbenefitsById(tempId);
	            System.out.println("pension_details:==:" + pension_details);

	            // Save retirement completed history
	            boolean updated = departmentReceivedService.saveRetirementPendinghistory(emp_details, pension_details, filemovedby);

	            if(updated)
            	{
            		return ResponseEntity.ok("File is Pending!");
            	}
            	else {
            		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details in history.");
            	}
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details for pending.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error occurred while saving details.");
	    }
	}
	
	
	@PostMapping("/updateforretirementreturn")
	public ResponseEntity<String> updateRetirementEmployeeDetailsReturn(@RequestBody Map<String, Object> requestData) {
	    try {
	        // Extract request parameters
	        String tempId = (String) requestData.get("tempId");
	        String filemovedby = (String) requestData.get("filemovedby");
	        

	        // Call service method to save the details
	        boolean isSaved = departmentReceivedService.updateEntryReturnDetails(requestData);

	        if (isSaved) {
	            // Fetch employee details
	            List<Map<String, Object>> emp_details = deptAuditService.fetchEmployeeById(tempId);
	            System.out.println("emp_details:==:" + emp_details);

	            // Save retirement completed history
	            boolean updated = departmentReceivedService.saveRetirementreturnhistory(emp_details,filemovedby);

	            if(updated)
            	{
            		return ResponseEntity.ok("File Returned to Department!");
            	}
            	else {
            		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details in history.");
            	}
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details for pending.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error occurred while saving details.");
	    }
	}

}
