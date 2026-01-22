package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.pensioner.service.NewEntryService;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("gcc/api/department/newentry")
@RestController
public class NewEntryAPIController {
	
	@Autowired
	private NewEntryService newEntryService;
	
	@Autowired
	public NewEntryAPIController(NewEntryService newEntryService) {
	    this.newEntryService = newEntryService;
	}
	
	
	@GetMapping("/retirementdetails")
	public Map<String, Object> getRetriedDetails(	       
	        @RequestParam(value = "deptId", required = false) Integer deptId,
	        @RequestParam(value = "file_category", required = false) Integer file_category,	
	        @RequestParam(value = "fromDate", required = false) String fromDate1,
	        @RequestParam(value = "toDate", required = false) String toDate1)
	{
		
		Map<String, Object> response = new HashMap<>();
	    if (deptId == null) {
	    	response.put("status", "deptid");
            response.put("message", "Department Id is null");
            return response;
            
	    } 

	    System.out.println("Received deptId: " + deptId);
	    System.out.println("Received fromDate: " + fromDate1);
	    System.out.println("Received toDate: " + toDate1);
	    
	    LocalDate fromDate;
	    LocalDate toDate ;
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	   
	    LocalDate now = LocalDate.now();
	    int currentYear = now.getYear();
	    
	    if(fromDate1.isEmpty()) {
	    	fromDate = LocalDate.of(currentYear, 4, 1);
	    }
	    
	    if(toDate1.isEmpty()) {
	    	toDate = LocalDate.of(currentYear + 1, 3, 31); // March 31 of the next year
	    }
	    
	    if(fromDate1.isEmpty()  && toDate1.isEmpty()) {
	     fromDate = LocalDate.of(currentYear, 4, 1); // April 1 of the current year
	     toDate = LocalDate.of(currentYear + 1, 3, 31); // March 31 of the next year
	    
	    }
	    else {
	    	try {
	    			    		
	    		 // Check if the input date is in yyyy-MM-dd format
	            if (fromDate1.matches("\\d{4}-\\d{2}-\\d{2}") && toDate1.matches("\\d{4}-\\d{2}-\\d{2}")) {
	                // Parse from yyyy-MM-dd format
	                fromDate = LocalDate.parse(fromDate1); 
	                toDate = LocalDate.parse(toDate1);
	            } else {
	                // Parse from dd-MM-yyyy format
	                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	                fromDate = LocalDate.parse(fromDate1, formatter1);
	                toDate = LocalDate.parse(toDate1, formatter1);
	            }
	           
	        } catch (DateTimeParseException e) {
	            response.put("status", "error");
	            response.put("message", "Invalid date format. Expected format: dd-MM-yyyy");
	            return response;
	        }
	    	
	    }
	    // Format the dates if needed (e.g., for logging or passing in a specific format)
	    
	    String formattedFromDate = fromDate.format(formatter);
	    String formattedToDate = toDate.format(formatter);

	    System.out.println("Received deptId after date: " + deptId);
	    System.out.println("Fetching data from " + formattedFromDate + " to " + formattedToDate);

	    	    
	    Map<String, Object> datalist = newEntryService.fetchEmplyeeDetails(formattedFromDate, formattedToDate, deptId);
	    System.out.println("datalist="+datalist);
	    List<String> emps= newEntryService.getRegisteredEmps(deptId);
	    
	 // Format the dates for frontend in "yyyy-MM-dd"
	    DateTimeFormatter frontendFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    String frontendFromDate = fromDate.format(frontendFormatter);
	    String frontendToDate = toDate.format(frontendFormatter);
	    response.put("fromDate", frontendFromDate);
	    response.put("toDate", frontendToDate);
	    
	 // Extract emp_no values from emps
	  //  Set<String> empNos = emps.stream().map(emp -> (String) emp.get("emp_no")).collect(Collectors.toSet());

	    //System.out.println("rrrrrrrrrrrr");
	    if (datalist.containsKey("list") && datalist.get("list") instanceof List) {
	        List<?> list = (List<?>) datalist.get("list");
	        if (list.isEmpty()) {
	            System.out.println("inside>>>>>");
	            response.put("status", "nodata");
	            response.put("message", "No data available for the selected date range");
	        }
	        else {
		    	//System.out.println("ttttttttttttt");
		        // Filter the datalist "list" to exclude matching EMPLOYEE_CODE values
		       /*
	        	List<Map<String, Object>> filteredList = ((List<Map<String, Object>>) datalist.get("list")).stream()
		                .filter(employee -> !emps.contains(employee.get("EMPLOYEE_CODE")))
		                .collect(Collectors.toList());
*/
	        	
	        	List<Map<String, Object>> originalList =
	        	        (List<Map<String, Object>>) datalist.get("list");

	        	List<Map<String, Object>> filteredList = new ArrayList<Map<String, Object>>();

	        	for (Map<String, Object> employee : originalList) {
	        	    Object empCode = employee.get("EMPLOYEE_CODE");
	        	    if (!emps.contains(empCode)) {
	        	        filteredList.add(employee);
	        	    }
	        	}
	        	
		        if (filteredList.isEmpty()) {
		            response.put("status", "nodata");
		            response.put("message", "No data available for the selected date range");
		        } else {
		            datalist.put("list", filteredList);
		            response.put("status", "success");
		            response.put("details", datalist);
		            System.out.println("Received datalist:"+datalist);
		            System.out.println("Received filteredList:"+filteredList);
		        }
		    }
	    }
	    
	    
	    
	    return response;
	}
    
    
    
    @GetMapping("/employeedetails")
    public ResponseEntity<Map<String, Object>> getEmployeedetailsbyCode(
            @RequestParam String empCode) {

        Map<String, Object> datalistbycode = newEntryService.fetchEmplyeeDetailsbyCode(empCode);
        return ResponseEntity.ok(datalistbycode);
    }
    
    @PostMapping("/saveEmployeeDetails")
    public ResponseEntity<String> saveRetirementEmployeeDetails(@RequestBody Map<String, Object> requestData) {
        try {
                      
            // Call service method to save details
            boolean isSaved = newEntryService.saveRetirementEmployeeDetails(requestData);

            if (isSaved) {
                return ResponseEntity.ok("File moved to Audit successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving details.");
        }
    }
    
    
    
    
    
    
    //family pension
    @PostMapping("/saveFamilyPensionDetails")
    public ResponseEntity<String> saveFamilyPensionDetails(@RequestBody Map<String, Object> requestData) {
        try {
        	
                    	
            // Call service method to save details
            boolean isSaved = newEntryService.saveFamilyPensionDetails(requestData);

            if (isSaved) {
                return ResponseEntity.ok("File moved to Audit successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving details.");
        }
    }
    
    //pendency api
    
    @PostMapping("/savePendecnyDetails")
    public ResponseEntity<String> savePendecnyDetails(@RequestBody Map<String, Object> requestData) {
        try {
        	                   	
            // Call service method to save detailstypeOfRetirement
            boolean isSaved = newEntryService.savePendencyDetails(requestData);

            if (isSaved) {
                return ResponseEntity.ok("File moved to Audit successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving details.");
        }
    }
    
  //gis saving api controller 
    @PostMapping("/saveEmployeeDetailsGis")
        public ResponseEntity<String> saveEmployeeDetailsingis(@RequestBody Map<String, Object> requestData) {
            try {
                // Extract data from the request body
               

                // Call service method to save details
                boolean isSaved = newEntryService.savenewEntryDetailsGis(requestData);

                if (isSaved) {
                    return ResponseEntity.ok("File moved to Pension successfully!");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving details.");
            }
        }
    
    

    
  //provsionalretirement saving api controller
    @PostMapping("/saveEmployeeDetailsProvisionalretirement")
    public ResponseEntity<String> saveEmployeeDetailsProvisionalretirement(@RequestBody Map<String, Object> requestData) {
        try {
            // Extract data from the request body
           

            // Call service method to save details
            boolean isSaved = newEntryService.savenewEntryDetailsProvisionalretirement(requestData);

            if (isSaved) {
                return ResponseEntity.ok("File moved to Pension successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving details.");
        }
    }
    
    
    
  //cps saving api controller
    @PostMapping("/saveEmployeeDetailsPensionScheme")
       public ResponseEntity<String> saveEmployeeDetailspensionscheme(@RequestBody Map<String, Object> requestData) {
           try {
               // Extract data from the request body
              

               // Call service method to save details
                boolean isSaved = newEntryService.savenewEntryDetailspensionscheme(requestData);

               if (isSaved) {
                   return ResponseEntity.ok("File Submitted successfully!");
               } else {
                   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save details.");
               }
           } catch (Exception e) {
               e.printStackTrace();
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving details.");
           }
       }
    
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> checkEmployeeExists(@RequestParam String empCode) {
        boolean exists = newEntryService.doesEmployeeExist(empCode);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cpsexists")
    public ResponseEntity<Map<String, Boolean>> checkEmployeeExistsCps(@RequestParam String empCode) {
        boolean exists = newEntryService.doesEmployeeExistgis(empCode);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/prexists")
    public ResponseEntity<Map<String, Boolean>> checkEmployeeExistsRpr(@RequestParam String empCode) {
        boolean exists = newEntryService.doesEmployeeExistRPR(empCode);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    
    
    @GetMapping("/familypensionexists")
    public ResponseEntity<Map<String, Boolean>> checkEmployeeExistsfp(@RequestParam String empCode) {
        boolean exists = newEntryService.doesEmployeeExistfp(empCode);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    

}