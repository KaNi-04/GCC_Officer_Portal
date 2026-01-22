package in.gov.chennaicorporation.gccoffice.taxcollection.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;
import in.gov.chennaicorporation.gccoffice.taxcollection.entity.TaxCollectionRequestEntity;
import in.gov.chennaicorporation.gccoffice.taxcollection.entity.TaxCollectionRequestStatusEntity;
import in.gov.chennaicorporation.gccoffice.taxcollection.repository.TaxCollectionRequestRepository;
import in.gov.chennaicorporation.gccoffice.taxcollection.service.TaxCollectionService;

@RequestMapping("/gcc/api/taxcollection")
@RestController("taxCollectionRestController")
public class TaxCollectionRestController {
	
    private final TaxCollectionRequestRepository taxCollectionRequestRepository;
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	private final TaxCollectionService taxCollectionService;
	private final String uploadDirectory;
	
	String bodyMessage = "";
	@Autowired
    public TaxCollectionRestController(
    		TaxCollectionRequestRepository taxCollectionRequestRepository, 
    		RestTemplate restTemplate, 
    		AppConfig appConfig,
    		TaxCollectionService taxCollectionService,
    		@Value("${file.upload.directory}") String uploadDirectory) {
		this.taxCollectionRequestRepository = taxCollectionRequestRepository;
		this.restTemplate = restTemplate;
        this.appConfig = appConfig;
        this.taxCollectionService = taxCollectionService;
        this.uploadDirectory = uploadDirectory;
    }
	
	// API Calls List
	@GetMapping(value = "/getPropertTaxPersonalDetails")
	public String getPtaxpersonalDetails(@RequestParam String ptax) {
		String ptaxTxt=ptax;
        String[] ptaxparts = ptaxTxt.split("-");
		String serviceId = "PT";
        String SetInput = "{\"ZONE\":\""+ptaxparts[0]+"\",\"DIV\":\""+ptaxparts[1]+"\",\"BILL\":\""+ptaxparts[2]+"\",\"SB\":\""+ptaxparts[3]+"\"}";
        
        ResponseEntity<String> response = restTemplate.getForEntity("https://chennaicorporation.gov.in/gcc/online-payment/property-tax/mobile-reg/mob_Asses_det.jsp?ServiceID={serviceId}&SetInput={SetInput}", 
        		String.class, 
        		serviceId, 
        		SetInput);
        bodyMessage = response.getBody().trim();

        return bodyMessage;
	}
	
	// List 
	@GetMapping(value = "/getTaxCollectionRequest")
	private List<TaxCollectionRequestEntity> getTaxCollectionRequest(){
        return taxCollectionRequestRepository.showAll();
    }
	
	@GetMapping(value = "/getTaxCollectionRequestById")
	private ResponseEntity<?> getTaxCollectionRequestById(@RequestParam("request_id") Integer request_id) {
	    Optional<TaxCollectionRequestEntity> result = taxCollectionRequestRepository.findById(request_id);
	    
	    if (result.isPresent()) {
	        return ResponseEntity.ok(result.get()); // Return the entity if it exists
	    } else {
	        return ResponseEntity.notFound().build(); // Return a 404 Not Found if the entity does not exist
	    }
	}
	
	// Save & Update
	@RequestMapping(value = "/saveTaxCollectionRequest", method = RequestMethod.POST)
	public String saveTaxCollectionRequest(HttpServletRequest request, 
			@RequestParam("name") String name,
			@RequestParam("mobile") String mobile,
			@RequestParam("ptax") String ptax,
			@RequestParam("usertype") String usertype,
			@RequestParam("availabilitydate") String availabilitydate,
			@RequestParam(name = "created_by", required = false) Integer created_by) {
		TaxCollectionRequestEntity taxCollectionRequestEntity = new TaxCollectionRequestEntity();
		
		String userId = LoginUserInfo.getLoginUserId();
		System.out.println("TaxCollectionRestController -> Login User ID: "+userId);
		
		LocalDateTime cdate = LocalDateTime.now();
		
		if (created_by == null) {
		    created_by = 0;
		}
		
		taxCollectionRequestEntity.setName(name);
		taxCollectionRequestEntity.setMobile(mobile);
		taxCollectionRequestEntity.setPtax(ptax);
		taxCollectionRequestEntity.setUsertype(usertype);
		taxCollectionRequestEntity.setAvailabilitydate(availabilitydate);
		taxCollectionRequestEntity.setCreatedBy(created_by);
		taxCollectionRequestEntity.setcDate(cdate.toString());
		taxCollectionRequestEntity.setIsActive(true);
		taxCollectionRequestEntity.setIsDelete(false);
		taxCollectionRequestEntity.setStatus("open");
		Integer lastinsertid = taxCollectionService.saveTaxCollectionRequest(taxCollectionRequestEntity);
		return "Success";
	}
	
	@RequestMapping(value = "/saveTaxCollectionRequestStatus", method = RequestMethod.POST)
	public String saveTaxCollectionRequestStatus(HttpServletRequest request, 
			@RequestParam("status") String status,
			@RequestParam("comments") String comments,
			@RequestParam("request_id") TaxCollectionRequestEntity request_id,
			@RequestParam(name = "created_by", required = false) Integer created_by) {
		
		TaxCollectionRequestStatusEntity taxCollectionRequestStatusEntity = new TaxCollectionRequestStatusEntity();
		
		LocalDateTime cdate = LocalDateTime.now();
		
		if (created_by == null) {
		    created_by = 0;
		}
		
		taxCollectionRequestStatusEntity.setStatus(status);
		taxCollectionRequestStatusEntity.setComments(comments);
		taxCollectionRequestStatusEntity.setTaxCollectionRequestEntity(request_id);
		taxCollectionRequestStatusEntity.setCreatedBy(created_by);
		taxCollectionRequestStatusEntity.setcDate(cdate.toString());
		taxCollectionRequestStatusEntity.setIsActive(true);
		taxCollectionRequestStatusEntity.setIsDelete(false);
		//Integer lastinsertid = taxCollectionService.saveTaxCollectionRequest(taxCollectionRequestStatusEntity);
		return "Success";
	}
	
	@GetMapping("/files")
	public ResponseEntity<List<String>> listFiles() {
	    try {
	        List<String> files = Files.list(Paths.get(uploadDirectory))
	                .map(path -> path.getFileName().toString())
	                .collect(Collectors.toList());

	        return ResponseEntity.ok(files);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
	    }
	}
	
}
