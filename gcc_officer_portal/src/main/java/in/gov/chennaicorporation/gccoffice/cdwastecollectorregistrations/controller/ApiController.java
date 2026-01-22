package in.gov.chennaicorporation.gccoffice.cdwastecollectorregistrations.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.cdwastecollectorregistrations.service.RegistrationService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

@RestController("cdapiController")
@RequestMapping("/gcc/api/registration")
public class ApiController {
	
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	
	@Autowired  
    public ApiController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

    @Autowired
    private Environment environment;
    
    @GetMapping("/pending")
	 public String getUserDetails(){
		 String apiUrl = appConfig.cdwastecollectors + "/volunteer/wastecollectors/api/getuserdetails";
		 ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
		 return responseEntity.getBody();		 
	 }
    
    @GetMapping("/pendingbyid")
	 public String getUserDetailsById(@RequestParam("registerId") Long registerId){
		 String apiUrl = appConfig.cdwastecollectors + "/volunteer/wastecollectors/api/getuserbyid"+"?registerId="+ registerId;
		 ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
		 return responseEntity.getBody();		 
	 }
    
    
//    @PostMapping("/acceptbyid")
//    public String acceptRegistration(@RequestParam("registerId") Long registerId) {
//        // Your logic to accept the registration
//    	 String apiUrl = appConfig.cdwastecollectors +"/volunteer/wastecollector/api/accept"+"?registerId="+ registerId;
//    	 ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
//    	 return responseEntity.getBody();	
//    }

    @PostMapping("/acceptbyid")
    public ResponseEntity<String> acceptRegistration(@RequestParam("registerId") Long registerId) {
        String apiUrl = appConfig.cdwastecollectors + "/volunteer/wastecollectors/api/accept?registerId=" + registerId;
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, null, String.class);
        return ResponseEntity.ok("Registration Accepted for ID: " + registerId);
    }


    
    @PostMapping("/rejectbyid")
    public ResponseEntity<String> rejectRegistration(
        @RequestParam("registerId") Long registerId, 
        @RequestParam("reason") String reason) {
        
        String apiUrl = appConfig.cdwastecollectors + "/volunteer/wastecollectors/api/reject?registerId=" + registerId + "&reason=" + reason;
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, null, String.class);
        return ResponseEntity.ok("Registration Rejected for ID: " + registerId);
    }

    @GetMapping("/approvallist")
	 public String getApprovalDetails(){
		 String apiUrl = appConfig.cdwastecollectors + "/volunteer/wastecollectors/api/approvedlist";
		 ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
		 return responseEntity.getBody();		 
	 }
    
    @GetMapping("/rejectedlist")
	 public String getRejectedDetails(){
		 String apiUrl = appConfig.cdwastecollectors + "/volunteer/wastecollectors/api/rejectedlist";
		 ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
		 return responseEntity.getBody();		 
	 }

    
}




