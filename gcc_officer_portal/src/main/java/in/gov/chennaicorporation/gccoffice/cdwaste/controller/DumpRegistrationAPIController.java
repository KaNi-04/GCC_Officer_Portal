package in.gov.chennaicorporation.gccoffice.cdwaste.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.cdwaste.service.OfficerService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;


@RestController
@RequestMapping("/gcc/api/dumpregistration")
public class DumpRegistrationAPIController {

	
	@Autowired
	private  OfficerService officerService;
	
    private final RestTemplate restTemplate;
    private final AppConfig appConfig;

    @Autowired
    public DumpRegistrationAPIController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
    

    
    @GetMapping("/wastecollectorlist")
    public ResponseEntity<String> getWasteCollectorList(
            @RequestParam(required = false) Long mobileNo,
            @RequestParam(required = false) Long refId) {

        StringBuilder apiUrl = new StringBuilder(appConfig.dumpregister + "/dumpregistration/api/wastecollectorlist");

        // If at least one param is present, append '?'
        if (mobileNo != null || refId != null) {
            apiUrl.append("?");
        }

        // Append parameters
        List<String> params = new ArrayList<>();
        if (mobileNo != null) {
            params.add("mobileNo=" + mobileNo);
        }
        if (refId != null) {
            params.add("refId=" + refId);
        }

        apiUrl.append(String.join("&", params));

        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl.toString(), String.class);
            return ResponseEntity.ok(responseEntity.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch data: " + e.getMessage());
        }
    }

  @GetMapping("/getallcharges")
  public ResponseEntity<String> getpaymentcharges() {
      String apiUrl = appConfig.dumpregister + "/dumpregistration/dropdown/api/getallcharges";

      try {
          ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
          return ResponseEntity.ok(responseEntity.getBody()); // Return the real response, not a fixed string
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("Failed to fetch data: " + e.getMessage());
      }
  }
  
  
  
  @PostMapping("/saveWasteRequest")
  public ResponseEntity<String> saveWasteRequest(@RequestBody Map<String, Object> payload) {
      try {
    	  officerService.saveRequestDetails(payload);
          return ResponseEntity.ok("Saved successfully");
      } catch (Exception e) {
          e.printStackTrace();
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving request");
      }
  }
  

  
  
  @GetMapping("/getless1tonlist")
  public ResponseEntity<String> getWasteCollectorLess1TonList() {
      StringBuilder apiUrl = new StringBuilder(appConfig.dumpregister + "/dumpregistration/api/getless1tonlist");

      try {
          ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl.toString(), String.class);
          return ResponseEntity.ok(responseEntity.getBody());
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("Failed to fetch data: " + e.getMessage());
      }
  }

  @GetMapping("/getless1tonlistbyId")
  public ResponseEntity<String> getWasteCollectorLess1TonListbyId(@RequestParam(required = true) Long refId) {
      if (refId == null) {
          return ResponseEntity.badRequest().body("refId is required");
      }

      try {
          String apiUrl = appConfig.dumpregister + "/dumpregistration/api/getless1tonlistbyid?refId=" + refId;

          ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
          return ResponseEntity.ok(responseEntity.getBody());

      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("Failed to fetch data: " + e.getMessage());
      }
  }


  
  @GetMapping("/getAllDetails")
	public ResponseEntity<?> getAllRequestDetails(
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
	        @RequestParam(required = false) String wastequantity,
	        @RequestParam(required = false) String zone) {
	    try {

	        List<Map<String, Object>> response = officerService.getAllRequestDetails(fromDate, toDate, wastequantity, zone);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Failed to fetch data: " + e.getMessage()));
	    }
	}

  
  @GetMapping("/getquantity")
	 public List<Map<String, Object>> getwastequantity() {
	        return officerService.getAllwastequantity();
	    }

//fetch all zones

	@GetMapping("/getzones")
	public ResponseEntity<?> getAllZones() {
	   try {
	       List<Map<String, Object>> response = officerService.getZonesList();
	       return ResponseEntity.ok(response);
	   } catch (Exception e) {
	       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	               .body(Collections.singletonMap("error", "Failed to fetch data: " + e.getMessage()));
	   }
	}
    
}

