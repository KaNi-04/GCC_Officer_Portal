package in.gov.chennaicorporation.gccoffice.circular.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.circular.service.CircularMaster;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("gcc/api/circular")
@RestController("circularRest") // Circular Rest
public class APIController {
	private CircularMaster circularMaster;
	
	@Autowired
	public APIController(CircularMaster circularMaster) {
		this.circularMaster = circularMaster;
	}
	
	@GetMapping(value="/getCircularTypes")
	public List getCircularTypeList() {
		return circularMaster.getCircularType();
	}
	
	@GetMapping(value="/getDeprtmentList")
	public List getDepartmentList() {
		return circularMaster.getDepartment();
	}
	
	@GetMapping(value="/getCircularList")
	public List getCircularList() {
		return circularMaster.getCircular();
	}
	
	@PostMapping(value="/saveCircular")
	public String savePetition(
			@RequestParam("createdBy") String createdBy,
			@RequestParam("doc_cat") String circularCategory,
			@RequestParam("circularType") String circularType,
			@RequestParam("department") String department,
			@RequestParam("Date") String Date,
			@RequestParam("subject") String subject,
			@RequestParam("scan_page1") MultipartFile scan_file1) {
		
		return circularMaster.saveCircular(createdBy,circularCategory,circularType,department,Date,subject,scan_file1);
	}
	
	@GetMapping(value="/getReport")
	public List getReport() {
		return circularMaster.getReport();
	}
}
