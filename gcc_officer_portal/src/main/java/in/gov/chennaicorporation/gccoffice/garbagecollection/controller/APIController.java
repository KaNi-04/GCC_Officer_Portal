package in.gov.chennaicorporation.gccoffice.garbagecollection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@RequestMapping("/api/garbagecollection")
@RestController("garbageCollectionRestController")
public class APIController {
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	String bodyMessage = "";
	@Autowired
    public APIController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
	
	@GetMapping(value = "/getGarbageDetails")
	public String getGarbageDetails(@RequestParam String requestid) {
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.garbageCollection+"/api/getRequestById?requestid={requestid}", String.class,requestid);
        bodyMessage = response.getBody().trim();

        return bodyMessage;
	}
	
	@PostMapping(value = "/saveStatus")
	public String saveStatus(@RequestParam("status") String status, @RequestParam("comments") String comments, @RequestParam("request_id") String request_id) {
		//System.out.println("I AM IN");
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.garbageCollection+"/api/saveStatus?status={status}&comments={comments}&request_id={request_id}", String.class,status,comments,request_id);
        bodyMessage = response.getBody().trim();

        return bodyMessage;
	}
	
	@GetMapping(value = "/getStatus")
	public String getStatus(@RequestParam String requestid) {
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.garbageCollection+"/api/getStatusByRequestId?requestid={requestid}", String.class,requestid);
        bodyMessage = response.getBody().trim();

        return bodyMessage;
	}
	
	@GetMapping(value = "/getStatusById")
	public String getStatusById(@RequestParam String statusid) {
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.garbageCollection+"/api/getStatusById?request_status_id={statusid}", String.class,statusid);
        bodyMessage = response.getBody().trim();

        return bodyMessage;
	}
	
}