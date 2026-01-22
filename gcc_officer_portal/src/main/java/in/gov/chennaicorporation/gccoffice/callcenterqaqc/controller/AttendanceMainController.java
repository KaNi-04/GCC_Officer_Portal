package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/callcenterattendance") 
@Controller("callCenterAttendanceController")
public class AttendanceMainController {
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/attendancereport")
	public String AttendanceList(
	        @RequestParam(required = false) String fromDate,
	        @RequestParam(required = false) String toDate,
	        Model model) {
	    List<Map<String, Object>> attendanceData = Collections.emptyList();

	    if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
	        String apiUrl = appConfig.attendanceReport + "/callcenter/api/attendance/getattendancereport?fromDate=" + fromDate + "&toDate=" + toDate;

	        try {
	            ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
	            String responseBody = responseEntity.getBody();

	            if (responseBody != null && !responseBody.isEmpty()) {
	                ObjectMapper objectMapper = new ObjectMapper();
	                if (responseBody.trim().startsWith("{")) {
	                    // Single object response
	                    Map<String, Object> singleData = objectMapper.readValue(responseBody, new TypeReference<>() {});
	                    attendanceData = List.of(singleData);
	                } else {
	                    // Array response
	                    attendanceData = objectMapper.readValue(responseBody, new TypeReference<>() {});
	                }
	            }
	        } catch (Exception e) {
	            System.err.println("Error fetching attendance data: " + e.getMessage());
	        }
	    }

	    model.addAttribute("attendanceData", attendanceData);
	    model.addAttribute("fromDate", fromDate);
	    model.addAttribute("toDate",  toDate);
	    model.addAttribute("LoginUserId", LoginUserInfo.getLoginUserId());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());

	    return "modules/callcenter/attendancereport";
	}

}
