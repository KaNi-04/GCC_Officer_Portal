package in.gov.chennaicorporation.gccoffice.petregistration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@RequestMapping("/gcc/api/petregistration")
@RestController("petRegistrationRest")
public class APIController {
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	String bodyMessage = "";
	
	@Autowired
    public APIController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
	
	@GetMapping(value = "/getpetimg")
	public String getPetImage(@RequestParam String mobno,@RequestParam String uniqueid) {
		String baseURL = appConfig.petRegistration+"/api/getpetimg";
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL)
				.queryParam("mobno", mobno)
				.queryParam("uniqueid", uniqueid );
		
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(),String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	
	@GetMapping(value = "/getvaccineimg")
	public String getVaccineImage(@RequestParam String mobno,@RequestParam String uniqueid) {
		String baseURL = appConfig.petRegistration+"/api/getvacimg";
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL)
				.queryParam("mobno", mobno)
				.queryParam("uniqueid", uniqueid );
		
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(),String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	
	@GetMapping(value = "/getapplicantimg")
	public String getApplicantImage(@RequestParam String mobno,@RequestParam String uniqueid) {
		String baseURL = appConfig.petRegistration+"/api/getapplicantimg";
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL)
				.queryParam("mobno", mobno)
				.queryParam("uniqueid", uniqueid );
		
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(),String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	
	@GetMapping(value = "/getaddressproofimg")
	public String getAddressProofImage(@RequestParam String mobno,@RequestParam String uniqueid) {
		String baseURL = appConfig.petRegistration+"/api/getaddproofimg";
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL)
				.queryParam("mobno", mobno)
				.queryParam("uniqueid", uniqueid );
		
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(),String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	
}
