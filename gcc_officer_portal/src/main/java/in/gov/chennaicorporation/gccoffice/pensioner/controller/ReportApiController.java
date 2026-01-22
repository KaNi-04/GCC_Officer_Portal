package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.pensioner.service.ReportApiService;

@RequestMapping("gcc/api/report")
@RestController
public class ReportApiController {
	@Autowired
	private ReportApiService reportApiService;
	
	
	
	//pensioner and report side common dropdown
	@GetMapping("/getFileCategories")
	@ResponseBody
	public ResponseEntity<?> getFileCategories() {
	    try {
	        List<Map<String, Object>> categories = reportApiService.getFileCategories();
	        return ResponseEntity.ok(categories);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Failed to fetch categories"));
	    }
	}

	
	@GetMapping("/getReportData")
	@ResponseBody
	public List<Map<String, Object>> getReportData(@RequestParam String startDate, 
	                                               @RequestParam String endDate,
	                                               @RequestParam String deptId
	                                               ) {
	    return reportApiService.getReportData(startDate, endDate, deptId);
	}

	
	
	
	@GetMapping("/getReportDetails")
	@ResponseBody
	public List<Map<String, Object>> getReportDetails(@RequestParam String startDate, 
	                                               @RequestParam String endDate,
	                                               @RequestParam Integer deptId,
	                                               @RequestParam Integer fileCatId,
	                                               @RequestParam(required = false) List<String> fileStatus
	                                               ) {
		System.out.println("startdate>>>>>"+startDate);
		System.out.println("endDate>>>>>"+endDate);
		System.out.println("deptId>>>>>"+deptId);
		System.out.println("fileCatId>>>>>"+fileCatId);
		System.out.println("fileStatus>>>>>"+fileStatus);
		
		
			 return reportApiService.getReportDetails(startDate, endDate, deptId,fileCatId,fileStatus);
		   
	}
	
	
	
	//pensioner side report dropdown 
	
	@GetMapping("/getdepartment")
	@ResponseBody
	public ResponseEntity<?> getdepartment() {
	    try {
	        List<Map<String, Object>> categories = reportApiService.getdepartment();
	        return ResponseEntity.ok(categories);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Failed to fetch categories"));
	    }
	}
	
	@GetMapping("/getstatus")
	@ResponseBody
	public ResponseEntity<?> getstatus() {
	    try {
	        List<Map<String, Object>> categories = reportApiService.getstatus();
	        return ResponseEntity.ok(categories);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Failed to fetch categories"));
	    }
	}
	
	
	
	@GetMapping("/getReportDatapension")
	@ResponseBody
	public List<Map<String, Object>> getReportDatapension(@RequestParam String startDate, 
	                                               @RequestParam String endDate,
	                                               @RequestParam(required = true) String fileCategoryId)
	                                                {
		System.out.println("fileCategoryId ========>"+fileCategoryId);
	    return reportApiService.getReportDatapension(startDate, endDate, fileCategoryId);
	}

	
	
	
	@GetMapping("/getReportDetailspension")
	@ResponseBody
	public List<Map<String, Object>> getReportDetailspension(@RequestParam String startDate, 
	                                                  @RequestParam String endDate,
	                                                  @RequestParam List<String> deptIds,
	                                                  @RequestParam Integer fileCatId,
	                                                  @RequestParam(required = false) List<String> fileStatus) {
	    
	    System.out.println("startdate>>>>>"+startDate);
	    System.out.println("endDate>>>>>"+endDate);
	    System.out.println("deptIds: " + deptIds); 
	    System.out.println("fileCatId>>>>>"+fileCatId);
	    System.out.println("fileStatus>>>>>"+fileStatus);
	    
	    return reportApiService.getReportDetailspension(startDate, endDate, deptIds, fileCatId, fileStatus);
	}
	
	@GetMapping("/getPensionerDetails")
	@ResponseBody
	public List<Map<String, Object>> getDepartmentpending(@RequestParam int  file_category ) {
	    return reportApiService.getDepartmentpending(file_category);
	}
	
	
	
	
	
}
