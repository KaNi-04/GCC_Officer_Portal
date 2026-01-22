package in.gov.chennaicorporation.gccoffice.cdwastecollectorregistrations.controller;

import java.util.Collections;
import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.cdwastecollectorregistrations.service.RegistrationService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;



@RequestMapping("/gcc/cdwaste")
@Controller("cdcontroller")
public class MainController {
	
	private final LoginUserInfo loginUserInfo;
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	
	@Autowired
    public MainController(RestTemplate restTemplate,LoginUserInfo loginUserInfo,
    		AppConfig appConfig) {
    	this.restTemplate = restTemplate;
    	this.appConfig = appConfig;
        this.loginUserInfo = loginUserInfo;     
    }
    		
	@Autowired
	private RegistrationService registrationservice;
	
	@GetMapping("/registration")
	public String SavecdRegistraion(Model model){	
		return "modules/cdwaste/registration";
	}

	@GetMapping("/pending")
	public String Pendinglist(Model model) throws JsonMappingException, JsonProcessingException {
	    String apiUrl = appConfig.cdwastecollectors + "/volunteer/wastecollectors/api/getuserdetails";
	    ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

	    // Get the response body
	    String responseBody = responseEntity.getBody();

	    ObjectMapper objectMapper = new ObjectMapper();

	    if (responseBody != null && !responseBody.isEmpty()) {
	        // Check if the response is an object or an array
	        if (responseBody.trim().startsWith("{")) { // Single object case
	            Map<String, Object> userDetails = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
	            model.addAttribute("userDetails", List.of(userDetails)); // Convert to a list for consistency
	        } else { // It's expected to be an array
	            List<Map<String, Object>> userDetails = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});
	            model.addAttribute("userDetails", userDetails); // Add the list directly
	        }
	    } else {
	        // If response is null or empty, pass an empty list to the model
	        model.addAttribute("userDetails", Collections.emptyList());
	    }

	    return "modules/cdwaste/pending"; // Return the view
	}


	@GetMapping("/approval")
	public String approvalList(Model model) throws JsonMappingException, JsonProcessingException {
	    String apiUrl = appConfig.cdwastecollectors + "/volunteer/wastecollectors/api/approvedlist";
	    ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

	    // Get the response body
	    String responseBody = responseEntity.getBody();

	    ObjectMapper objectMapper = new ObjectMapper();

	    if (responseBody != null && !responseBody.isEmpty()) {
	        // Check if the response is an object or an array
	        if (responseBody.trim().startsWith("{")) { // Single object case
	            Map<String, Object> approvalDetails = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
	            model.addAttribute("approvalDetails", List.of(approvalDetails)); // Convert to list
	        } else { // It's an array
	            List<Map<String, Object>> approvalDetails = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});
	            model.addAttribute("approvalDetails", approvalDetails);
	        }
	    } else {
	        model.addAttribute("approvalDetails", Collections.emptyList());
	    }

	    return "modules/cdwaste/approval"; // Return the appropriate view
	}

	@GetMapping("/rejected")
	public String rejectedList(Model model) throws JsonMappingException, JsonProcessingException {
	    String apiUrl = appConfig.cdwastecollectors + "/volunteer/wastecollectors/api/rejectedlist";
	    ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

	    // Get the response body
	    String responseBody = responseEntity.getBody();

	    ObjectMapper objectMapper = new ObjectMapper();

	    if (responseBody != null && !responseBody.isEmpty()) {
	        // Check if the response is an object or an array
	        if (responseBody.trim().startsWith("{")) { // Single object case
	            Map<String, Object> rejectedDetails = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
	            model.addAttribute("rejectedDetails", List.of(rejectedDetails)); // Convert to list
	        } else { // It's an array
	            List<Map<String, Object>> rejectedDetails = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});
	            model.addAttribute("rejectedDetails", rejectedDetails);
	        }
	    } else {
	        model.addAttribute("rejectedDetails", Collections.emptyList());
	    }

	    return "modules/cdwaste/rejected"; // Return the appropriate view
	}

	


}
