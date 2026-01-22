package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;


@RequestMapping("/gcc/api/callenter/attendance")
@RestController
public class AttendanceApiController {
	
	private AppConfig appConfig;
	private final RestTemplate restTemplate;
	
	@Autowired
    public AttendanceApiController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

	
	
    @GetMapping("/getAttendancedetails")
	 public String getAttendancedetails(@RequestParam String fromDate,@RequestParam String toDate){
		 String apiUrl = appConfig.attendanceReport + "/callcenter/api/attendance/getattendancereport"+"?fromDate="+ fromDate + "&toDate="+ toDate;
		 ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
		 return responseEntity.getBody();
	 }

}
