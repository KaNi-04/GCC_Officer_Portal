package in.gov.chennaicorporation.gccoffice.mtm.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@RequestMapping("/gcc/api/mtm")
@RestController("mtmapicontroller")
public class APIController {
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	String bodyMessage = "";
	
	@Autowired
    public APIController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
	
	@GetMapping(value="/setEventId")
    public String setEventId(HttpSession session,
                                @RequestParam("eventid") String eventid){
        if ("0".equals(eventid)) {
        	session.setAttribute("eventid", null);
        }else{
            session.setAttribute("eventid", eventid);
        }

        return "success";
    }
	
	/*
	@GetMapping(value = "/dashboard")
	public String getFeedbackAssetdetails(@RequestParam String fbId) {
		String feedbackId = fbId;
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.qrAssetFeedback+"/api/feedback/{feedbackId}",String.class,feedbackId);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	*/
	
	@GetMapping(value = "/getPetitionStatusPublic")
	public String getPetitionStatusPublic(@RequestParam String petition_no) {
		String baseURL = appConfig.mtm+"/api/getPetitionStatusPublic";
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL)
				.queryParam("petition_no", petition_no);
		
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(),String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	
	@GetMapping(value = "/getComMappingDetailsByPetId")
	public String getComMappingDetailsByPetId(@RequestParam String petid) {
		String baseURL = appConfig.mtm+"/api/getComMappingDetailsByPetId";
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL)
				.queryParam("petid", petid);
		
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(),String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	
	@GetMapping(value = "/getComMappingActionByMapId")
	public String getComMappingActionByMapId(@RequestParam String mapid) {
		String baseURL = appConfig.mtm+"/api/getComMappingActionByMapId";
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL)
				.queryParam("mapid", mapid);
		
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(),String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	
	@GetMapping(value = "/petitionFilesBypetitionId")
	public String petitionFilesBypetitionId(@RequestParam String pid) {
		String baseURL = appConfig.mtm+"/api/petitionFilesBypetitionId";
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL)
				.queryParam("pid", pid);
		
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(),String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	
	
	@GetMapping(value = "/viewBlobData")
	public String viewBlobData(@RequestParam String id,@RequestParam String table) {
		String baseURL = appConfig.mtm+"/api/viewBlobData";
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL)
				.queryParam("id", id)
				.queryParam("table", table);
		
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(),String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	
	
	@GetMapping(value = "/getComMappingDetailsByMapId")
	public String getComMappingDetailsByMapId(@RequestParam String mapid) {
		String baseURL = appConfig.mtm+"/api/getComMappingDetailsByMapId";
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL)
				.queryParam("mapid", mapid);
		
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(),String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
}
