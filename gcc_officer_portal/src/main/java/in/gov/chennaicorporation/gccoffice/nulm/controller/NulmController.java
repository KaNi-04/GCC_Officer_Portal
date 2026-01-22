package in.gov.chennaicorporation.gccoffice.nulm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import in.gov.chennaicorporation.gccoffice.nulm.service.NulmService;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@Controller
@RequestMapping("/gcc/nulm")
public class NulmController {
	
	@Autowired
	private NulmService nulmService;
	
	@GetMapping("/appointment")
	public String viewappoinmentMaster(Model model) {
		System.out.println("appointment");
		return "modules/nulm/appointment";
	}

	@GetMapping("/self-enrollment")
	public String viewselfenrollment(Model model) {
		System.out.println("self-enrollment");
		return "modules/nulm/staff-enrollment";
	}

	@GetMapping("/create-order")
	public String viewcreateorder(Model model) {
		System.out.println("create-order");
		return "modules/nulm/create-order";
	}
	
	@GetMapping("/scheme-group")
	public String viewschemegroup(Model model) {
		System.out.println("scheme-group");
		return "modules/nulm/scheme-group";
	}
	
	@GetMapping("/dropout")
	public String viewappointmentscreengroup(Model model) {
		System.out.println("dropout");
		return "modules/nulm/dropout";
	}
	
	@GetMapping("/updateStaff")
    public String viewUpdateScreen(
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "division", required = false) String division,
            Model model) {

        // Fetch the full list or filtered list based on parameters
        List<Map<String, Object>> enrollments = nulmService.getEnrollments(department, null);

        // Add the list to the model to be accessed in the view
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("selectedDepartment", department);  // Pass the selected department

        // Return the view name
        return "modules/nulm/updated-staff";
    }
		
	@GetMapping("/salaryDetails")
	public String getSalaryDetails(
	         @RequestParam(value = "month", required = false) String month,
	         @RequestParam(value = "year", required = false) Integer year,  // Use Integer instead of int
	         Model model) {

		 List<Map<String, Object>> salaryDetails = new ArrayList<>();
		
		 // Check if month and year are provided
		 if (month != null && year != null) {
		     salaryDetails = nulmService.getAttendanceWithSalary(month, year);
		 } else {
		     // Handle case when no month or year is provided
		     model.addAttribute("message", "Please select a month and year.");
		 }
		
		 model.addAttribute("salaryCounts", salaryDetails);
		 System.out.println("salarydetails for month: " + month + ", year: " + year);
		 return "modules/nulm/salary-details";
	}
	/*
	@GetMapping("/salary-approve")
	public String viewSalaryApproveScreen(@RequestParam(required = false) String month,
	                                      @RequestParam(required = false) Integer year,
	                                      @RequestParam(required = false) String salaryStatus,
	                                      Model model) {
	    List<Map<String, Object>> salaryInitiatedCounts;

	    // If both month and year are provided, fetch the data; otherwise, return an empty list
	    if (month != null && year != null) {
	        salaryInitiatedCounts = nulmService.getFilteredSalaryDetails(month, year, salaryStatus);
	    } else {
	        salaryInitiatedCounts = Collections.emptyList(); // Empty list if no month and year are provided
	    }

	    model.addAttribute("salaryInitiatedCounts", salaryInitiatedCounts);
	    return "modules/nulm/salary-approve";
	}
	*/
	@GetMapping("/salary-approve")
	public String viewSalaryApproveScreen(@RequestParam(required = false) String month,
	                                      @RequestParam(required = false) Integer year,
	                                      @RequestParam(required = false) String salaryStatus,  // Added department parameter
	                                      Model model) {
	    
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		List<Map<String, Object>> salaryInitiatedCounts;

	    // Fetch data if both month and year are provided, otherwise return an empty list
	    if (month != null && year != null) {
	        salaryInitiatedCounts = nulmService.getFilteredSalaryDetails(month, year, salaryStatus,LoginUserId); // Pass department to the service method
	    } else {
	        salaryInitiatedCounts = Collections.emptyList(); // Empty list if no month and year are provided
	    }

	    model.addAttribute("salaryInitiatedCounts", salaryInitiatedCounts);
	    
	    return "modules/nulm/salary-approve";
	}
	
	@GetMapping("/salary-report")
	public String viewSalaryReport(
	        @RequestParam(required = false) String month, 
	        @RequestParam(required = false) Integer year, 
	        @RequestParam(required = false) String salaryStatus, 
	        @RequestParam(required = false) String groupName,
	        Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
	    // Initialize an empty salaryReport list
	    List<Map<String, Object>> salaryReport = new ArrayList<>();
	    
	    // Only fetch the report if month and year are provided
	    if (month != null && year != null) {
	        salaryReport = nulmService.getSalaryReport(month, year, salaryStatus, groupName, LoginUserId);
	    }

	    // Add the fetched data to the model
	    model.addAttribute("salaryReport", salaryReport);
	    
	    return "modules/nulm/salary-report";  // Returns the view 'salary-report'
	}
	

	@GetMapping("/salary-zonereport")
	public String viewZoneWiseReport(
	        @RequestParam(required = false) String month, 
	        @RequestParam(required = false) Integer year, 
	        @RequestParam(required = false) String salaryStatus, 
	        @RequestParam(required = false) String groupName, 
	        @RequestParam(required = false) String zone,
	        @RequestParam(required = false) String division,
	        Model model) {
		//System.out.println("Zone: "+zone);
		
	    // Initialize an empty salaryReport list
	    List<Map<String, Object>> zoneWiseReport = new ArrayList<>();
	    
	    // Only fetch the report if month, year and zone are provided
	    if (month != null && year != null && zone!= null) {
	        zoneWiseReport = nulmService.getZoneWiseReport(month, year, salaryStatus ,groupName, zone, division);
	    }

	    // Add the fetched data to the model
	    model.addAttribute("zonewiseReport", zoneWiseReport);
	    
	    return "modules/nulm/zone-wise-report";  // Returns the view 'salary-zonereport'
	}
	
	@GetMapping("/attendance-report")
	public String viewAttendanceReport( 
	        @RequestParam(required = false) String fromdate,
	        @RequestParam(required = false) String todate,
	        @RequestParam(required = false) String groupName, 
	        @RequestParam(required = false) String zone,
	        @RequestParam(required = false) String division,
	        Model model) {
		
	    // Initialize an empty salaryReport list
	    List<Map<String, Object>> attendanceReport = new ArrayList<>();
	    
	    // Only fetch the report if month, year and zone are provided
	    if (division != null && zone!= null) {
	        attendanceReport = nulmService.getAttendanceReport(fromdate ,todate ,groupName, zone, division);
	    }

	    // Add the fetched data to the model
	    model.addAttribute("attendanceReport", attendanceReport);
	    
	    return "modules/nulm/attendance-report";  
	}
	
	@GetMapping("/consolidated-attendance-report") // need to add
    public String viewConsolidatedAttendanceReport(
            @RequestParam(required = false) String fromdate,
            @RequestParam(required = false) String todate,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String division,
            Model model) {

        // Initialize an empty salaryReport list
        List<Map<String, Object>> consolidatedAttendanceReport = new ArrayList<>();

        // Only fetch the report if month, year and zone are provided
        if (division != null && zone != null) {
            consolidatedAttendanceReport = nulmService.getConsolidatedAttendanceReport(fromdate, todate, groupName, zone, division);
        }

        // Add the fetched data to the model
        model.addAttribute("consolidatedAttendanceReport", consolidatedAttendanceReport);
        model.addAttribute("fromdate", fromdate);
        model.addAttribute("todate", todate);

        return "modules/nulm/attendance-report";
    }

 @GetMapping("/attendance-dates") //need to add
    public ResponseEntity<Map<String, List<String>>> getAttendanceDates(
            @RequestParam(required = false) String enrollmentId,
            @RequestParam(required = false) String fromdate,
            @RequestParam(required = false) String todate,
            @RequestParam(required = false) String type,
            Model model) {

        return ResponseEntity.ok(nulmService.fetchAttendanceDates(enrollmentId, fromdate, todate, type));

    }


@GetMapping("/self-help-group") // need to add
    public String viewSelfHelpGroup(Model model) {

        // Fetch the full list or filtered list based on parameters
        List<Map<String, Object>> selfHelpGroup = nulmService.getSelfHelpGroupDetails();

        // Add the list to the model to be accessed in the view
        model.addAttribute("selfHelpGroup", selfHelpGroup);

        // Return the view name
        return "modules/nulm/selfHelpGroup";
    }


@GetMapping("/salary-nonInitiated")
    public String viewSalaryNotInitiatedReport(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String groupName,
            Model model) {
        System.out.println(" salary-nonInitiated groupname====" + groupName);
        if (groupName != null && !groupName.isEmpty()) {
            groupName = URLDecoder.decode(groupName, StandardCharsets.UTF_8);
        }

        List<Map<String, Object>> salaryNotInitiatedReport = new ArrayList<>();

        if (month != null && year != null) {
            salaryNotInitiatedReport = nulmService.getSalaryNotInitiatedReport(month, year, groupName);
        }

        model.addAttribute("salaryNonInitiatedReport", salaryNotInitiatedReport);

        return "modules/nulm/salary-nonInitiatedreport";  
    }
}
