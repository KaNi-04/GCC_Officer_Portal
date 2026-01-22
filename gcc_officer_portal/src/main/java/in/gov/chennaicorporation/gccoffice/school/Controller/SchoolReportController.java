package in.gov.chennaicorporation.gccoffice.school.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import in.gov.chennaicorporation.gccoffice.school.Service.SchoolReportService;


@RestController
@RequestMapping("/gcc/api/gccschool/report")
public class SchoolReportController {
	@Autowired
	private SchoolReportService schoolReportService;

	 @Autowired
	    public SchoolReportController(SchoolReportService schoolReportService) {
	        this.schoolReportService = schoolReportService;
	    }
	 
	 @GetMapping("/getAEOList")
		public ResponseEntity<?> getAEOList(){
			return schoolReportService.getAEOList();
		}
		
		@GetMapping("/getAEOZone")
		public ResponseEntity<?> getAEOZone(@RequestParam String udise){
			return schoolReportService.getAEOZone(udise);
		}
		
		@GetMapping("/getAEOWard")
		public ResponseEntity<?> getAEOWard(@RequestParam String udise, @RequestParam String zone){
			return schoolReportService.getAEOWard(udise, zone);
		}
		
		@GetMapping("/getAEOSchoolData")
		public ResponseEntity<?> getAEOSchoolData(@RequestParam (required = false) List<String> udise,
				                                  @RequestParam (required = false) String zone, 
				                                  @RequestParam (required = false) String ward){
			
			
			return schoolReportService.getAEOSchoolData(udise, zone, ward);
		}
		
		
		@GetMapping("/getAEOStudentData")
		public ResponseEntity<?> getAEOStudentData(@RequestParam (required = false) List<String> udise){
			System.out.println("list="+udise);
			return schoolReportService.getAEOStudentData(udise);
		}
		
		
		@GetMapping("/getAEOStudentsCount")
		public ResponseEntity<?> getAEOStudentsCount(){
			return schoolReportService.getAEOStudentsCount();
		}
		
		@GetMapping("/getRegisteredStud")
		public ResponseEntity<?> getRegisteredStud(@RequestParam String udise){
			return schoolReportService.getRegisteredStud(udise);
		}
}
