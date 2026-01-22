package in.gov.chennaicorporation.gccoffice.greencommittee.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.greencommittee.service.ReportService;

@RestController
@RequestMapping("/gcc/api/greencommittee/report")
public class ReportApicontroller {

	@Autowired
	private ReportService reportService;
	
	@GetMapping("/getgreencommitteelist")
	 public List<Map<String, Object>> getgreencommitteelist(@RequestParam String meetingid,
	                                                        @RequestParam String zones) {

	     List<String> refnums = reportService.getrefnums(meetingid, zones);
	     System.out.println("refnums="+refnums);
	     
	     
	     List<Map<String, Object>> finalList = new ArrayList<>();

	     if (refnums.isEmpty()) {
	         return Collections.emptyList();
	     }

	     for (String ref : refnums) {
	    	 System.out.println("ref="+ref);
	         Map<String, Object> data = reportService.getlistdata(ref);
	         if (data != null && !data.isEmpty()) {
	             finalList.add(data);
	         }
	     }

	     return finalList;
	 }
	
}
