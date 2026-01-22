package in.gov.chennaicorporation.gccoffice.vendor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.vendor.service.StreetVendorService;
import in.gov.chennaicorporation.gccoffice.vendor.service.createventService;

@RestController("streetvendorApiController")
@RequestMapping("/gcc/api/streetvendor")
public class ApiController {
	
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	
	@Autowired
	private StreetVendorService streetVendorService;
	
	@Autowired
	public createventService createventservice;
	
	@Autowired  
    public ApiController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

    @Autowired
    private Environment environment;
    
    @GetMapping("/vendorCommiteeList")
	 public String getVendorCommiteeDetails(){
		 String apiUrl = appConfig.mobileservice + "/gccofficialapp/api/streetvendor/vendingCommiteeList";
		 ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
		 return responseEntity.getBody();		 
	 }
    
//    @GetMapping("/vendorCommiteeListbyID")
//    public String getVendorCommiteeDetailsByID(@RequestParam("id") Long id) {
//        String apiUrl = appConfig.mobileservice + "/gccofficialapp/api/streetvendor/vendingCommiteeListbyId?id=" + id;
//        ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
//        return responseEntity.getBody();
//    }
    
    @GetMapping("/vendorCommiteeListbyID")
    public ResponseEntity<List<Map<String, Object>>> getVendorCommiteeDetailsByID(@RequestParam("id") Long id) {
        String apiUrl = appConfig.mobileservice + "/gccofficialapp/api/streetvendor/vendingCommiteeListbyId?id=" + id;
        ResponseEntity<List> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, List.class);
        //System.out.println("responseEntity...."+responseEntity);
        return ResponseEntity.ok(responseEntity.getBody());
    }

    @GetMapping("/vendingCommiteeList")
    public ResponseEntity<List<Map<String, Object>>> getVendingCommiteeList(@RequestParam("date") String date) {
        String apiUrl = appConfig.mobileservice + "/gccofficialapp/api/streetvendor/vendingCommiteeList?date=" + date;
        ResponseEntity<List> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, List.class);
        
        List<String> saved_request_no=createventservice.removeSavedVendorDetails();
        //System.out.println("saved_request_no="+saved_request_no);
        
     // Filter out matching vendor_req_id entries
        List<Map<String, Object>> originalList = responseEntity.getBody();
        List<Map<String,Object>> filteredList = originalList.stream().filter(item -> !saved_request_no.contains(String.valueOf(item.get("vendor_req_id"))))
        		.collect(Collectors.toList());
        
        return ResponseEntity.ok(filteredList);
    }
    
    
//    @GetMapping("/vendorDetailsByReqId")
//    public ResponseEntity<List<Map<String, Object>>> getVendorDetailsByReqId(@RequestParam("requestId") String requestId) {
//        String apiUrl = appConfig.mobileservice + "/gccofficialapp/api/streetvendor/vendorDetailsByReqId?requestId=" + requestId;
//        ResponseEntity<List> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, List.class);
//        return ResponseEntity.ok(responseEntity.getBody());
//    }


    @PostMapping("/savedetailsforvendor")
    public ResponseEntity<Map<String, Object>> savedetailsforvendor(@RequestBody Map<String, Object> payload)
    {
    	return createventservice.saveVendorDetails(payload);
    }
    
    @PostMapping("/saveBulkVendorDetails")
    public ResponseEntity<Map<String, Object>> saveBulkVendorDetails(@RequestBody Map<String, Object> payload) {
        return createventservice.saveBulkVendorDetails(payload);
    }

    
    @GetMapping("/vendingCommiteeDetails")
    public List<Map<String, Object>> getvendingCommiteeDetails(@RequestParam("event_req_id") String event_req_id) {
        
        return createventservice.getCommiteeDetails(event_req_id);
    }
    
    @GetMapping("/vendingDetailsWeb")
    public List<Map<String, Object>> getvendingDetailsWeb(@RequestParam String cby) {
        
        return streetVendorService.getCommiteeDetailsWeb(cby);
    }
    
    @GetMapping("/webVendorDetailsByReqId")
    public List<Map<String, Object>> getwebVendorDetailsByReqId(@RequestParam("requestId") String requestId) {
        
        return streetVendorService.getwebVendorDetailsByReqId(requestId);
    }
    
// sanjay
    
//    @GetMapping("/wards-by-zone")
//    
//    public List<Map<String, Object>> getWardsByZone(@RequestParam("zoneId") int zoneId) {
//        return streetVendorService.fetchWardsByZone(zoneId);
//    }
    
    

//        @PostMapping("/saveDetails")
//        public ResponseEntity<Map<String, Object>> saveVenderDetails(
//        		
//        		@RequestParam String latitude,
//        		@RequestParam String longitude,
//                @RequestParam String zone,
//                @RequestParam String ward,
//                @RequestParam String landmark,
//                @RequestParam String vending_district,
//                @RequestParam int vending_pincode,
//                @RequestParam int vending_category,
//                @RequestParam String vending_space,
//                @RequestParam boolean pm_svanidhi_loan,
//                @RequestParam boolean bank_acc_status,
//                @RequestParam (required = false, name = "bank_passbook" )MultipartFile bank_passbook,
//                @RequestParam MultipartFile street_vendor_photo,
//                @RequestParam String name,
//                @RequestParam String f_h_name,
//                @RequestParam String mobile_number,
//                @RequestParam String dob,
//                @RequestParam String gender,
//                @RequestParam int social_category,
//                @RequestParam boolean diff_abled,
//                @RequestParam int education_status,
//                @RequestParam int marital_status,
//                @RequestParam int no_of_fam_mem,
//                @RequestParam boolean are_any_fam_mem_invol_str,
////                @RequestParam String vending_address,
//                
//                @RequestParam String door_no,
//                @RequestParam String streetname,
//                @RequestParam String areaname,
//                @RequestParam String present_district,
//                @RequestParam int present_pincode,
//                @RequestParam MultipartFile ration_card_photo,
//                @RequestParam MultipartFile aadhar_front_photo,
//                @RequestParam MultipartFile aadhar_back_photo,
//                @RequestParam String aadharNo) {
//
//            String presentAddress = door_no + ", " + streetname + ", " + areaname;
//
//            System.out.println("bank_passbook...."+bank_passbook);
//            System.out.println("ration_card_photo...."+ration_card_photo);
//            System.out.println("street_vendor_photo...."+street_vendor_photo);
//            System.out.println("aadhar_front_photo...."+aadhar_front_photo);
//            System.out.println("aadhar_back_photo...."+aadhar_back_photo);
//            
//            String bankPassbookPath = "";
//
//            // Only upload passbook if status is true
//            if (bank_acc_status) {
//                if (bank_passbook != null && !bank_passbook.isEmpty()) {
//                    bankPassbookPath = streetVendorService.fileUpload	(bank_passbook, "bank_passbook");
//                } else {
//                    return ResponseEntity.badRequest().body(Map.of(
//                            "status", false,
//                            "description", "Bank passbook is required when account status is Yes"
//                    ));
//                }
//            }
//            String rationCardPath = streetVendorService.fileUpload(ration_card_photo, "ration_card");
//            String streetVendorPhotoPath = streetVendorService.fileUpload(street_vendor_photo, "vendor_photo");
//            String aadharFrontPhotoPath = streetVendorService.fileUpload(aadhar_front_photo, "aadhar_front");
//            String aadharBackPhotoPath = streetVendorService.fileUpload(aadhar_back_photo, "aadhar_back");
//
//            String saveStatus = streetVendorService.saveStreetVendorDetails(
//            		latitude, longitude,zone, ward, landmark, vending_district, vending_pincode, vending_category, vending_space,
//                    pm_svanidhi_loan, bank_acc_status, bankPassbookPath,
//                    streetVendorPhotoPath, name, f_h_name, mobile_number,dob,
//                    gender, social_category, diff_abled, education_status, marital_status,
//                    no_of_fam_mem, are_any_fam_mem_invol_str,presentAddress, present_district, present_pincode,
//                    rationCardPath,aadharFrontPhotoPath, aadharBackPhotoPath,aadharNo);
//
//            if (saveStatus == null || saveStatus.equals("error")) {
//                return ResponseEntity.ok(Map.of(
//                    "status", false,
//                    "message", "failed",
//                    "description", "Failed to save the details"
//                ));
//            }
//
//            return ResponseEntity.ok(Map.of(
//                "status", true,
//                "message", "success",
//                "description", "Details saved successfully",
//                "vendor_req_id", saveStatus
//            ));
//        }
    
    
    @PostMapping("/saveDetails")
    public ResponseEntity<Map<String, Object>> saveVenderDetails(
    		
    		
    		@RequestParam (required = false)String latitude,
    		@RequestParam (required = false)String longitude,
            @RequestParam (required = false)String zone,
            @RequestParam (required = false)String ward,
            @RequestParam (required = false)String landmark,
            @RequestParam (required = false)String vending_district,
            @RequestParam (required = false)Integer vending_pincode,
            @RequestParam Integer vending_category,
            @RequestParam Integer tamilvending_category,
            @RequestParam (required = false)String vending_space,
            @RequestParam (required = false) Boolean pm_svanidhi_loan,
            @RequestParam (required = false) Boolean bank_acc_status,
            @RequestParam (required = false, name = "bank_passbook" )MultipartFile bank_passbook,
            @RequestParam (required = false) MultipartFile street_vendor_photo,
            @RequestParam String name,
            @RequestParam (required = false)String f_h_name,
            @RequestParam String mobile_number,
            @RequestParam (required = false)String dob,
            
            @RequestParam (required = false)String gender,
            @RequestParam (required = false)Integer social_category,
            @RequestParam (required = false)Boolean diff_abled,
            @RequestParam (required = false)Integer education_status,
            @RequestParam (required = false)Integer marital_status,
            @RequestParam (required = false)Integer no_of_fam_mem,
            @RequestParam (required = false)Boolean are_any_fam_mem_invol_str,
            
            @RequestParam (required = false)String door_no,
            @RequestParam (required = false)String streetname,
            @RequestParam (required = false)String areaname,
            @RequestParam (required = false)String present_district,
            @RequestParam (required = false)Integer present_pincode,
            @RequestParam (required = false)MultipartFile ration_card_photo,
            @RequestParam (required = false)MultipartFile aadhar_front_photo,
            @RequestParam (required = false)MultipartFile aadhar_back_photo,
            @RequestParam (required = false)String aadharNo,
            @RequestParam (required = false)String tamil_name,
            @RequestParam (required = false)String tamil_f_h_name,
            @RequestParam (required = false)String tamil_landmark,
            @RequestParam (required = false)String tamil_gender,
            @RequestParam (required = false)String tamil_present_district,
            @RequestParam String userId) {

        //String presentAddress = door_no + ", " + streetname + ", " + areaname;
    	
    		String data=tamil_name +"=="+tamil_f_h_name+"=="+tamil_landmark+"=="+tamil_gender+"=="+tamil_present_district;
        
    		//System.out.println("data="+data);
    		
    	String doorNoVal = (door_no != null && !door_no.trim().isEmpty()) ? door_no : "";
    	String streetNameVal = (streetname != null && !streetname.trim().isEmpty()) ? streetname : "";
    	String areaNameVal = (areaname != null && !areaname.trim().isEmpty()) ? areaname : "";
    	
    	

    	String presentAddress = String.join(", ", doorNoVal, streetNameVal, areaNameVal);
 
        String bankPassbookPath = null;

        // Only upload passbook if status is true
        if (Boolean.TRUE.equals(bank_acc_status)) {
        	
            if (bank_passbook != null && !bank_passbook.isEmpty()) {
            	
                bankPassbookPath = streetVendorService.fileUpload(bank_passbook, "street_vendor_bank");
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", false,
                        "description", "Bank passbook is required when account status is Yes"
                ));
            }
        }
        
        
        String rationCardPath=null;
        String streetVendorPhotoPath=null;
        String aadharFrontPhotoPath=null;
        String aadharBackPhotoPath=null;
        
        if(ration_card_photo != null && !ration_card_photo.isEmpty()) 
        {
        	 rationCardPath = streetVendorService.fileUpload(ration_card_photo, "street_vendor_ration");
        }
        if (street_vendor_photo != null && !street_vendor_photo.isEmpty()) 
        {
        	 streetVendorPhotoPath = streetVendorService.fileUpload(street_vendor_photo, "street_vendor_photo");
        }
        if (aadhar_front_photo != null && !aadhar_front_photo.isEmpty()) 
        {
        	 aadharFrontPhotoPath = streetVendorService.fileUpload(aadhar_front_photo, "aadhar_front");
        }
        if (aadhar_back_photo != null && !aadhar_back_photo.isEmpty())
        {
        	  aadharBackPhotoPath = streetVendorService.fileUpload(aadhar_back_photo, "aadhar_back");
        }
        

        String saveStatus = streetVendorService.saveStreetVendorDetails(
        		latitude, longitude,zone, ward, landmark, vending_district, vending_pincode, vending_category, vending_space,
                pm_svanidhi_loan, bank_acc_status, bankPassbookPath,
                streetVendorPhotoPath, name, f_h_name, mobile_number,dob,
                gender, social_category, diff_abled, education_status, marital_status,
                no_of_fam_mem, are_any_fam_mem_invol_str,presentAddress, present_district, present_pincode,
                rationCardPath,aadharFrontPhotoPath, aadharBackPhotoPath,aadharNo,userId,tamil_name,tamil_f_h_name,tamil_landmark,tamil_gender,tamil_present_district,tamilvending_category);

        if (saveStatus == null || saveStatus.equals("error")) {
            return ResponseEntity.ok(Map.of(
                "status", false,
                "message", "failed",
                "description", "Failed to save the details"
            ));
        }

        
        return ResponseEntity.ok(Map.of(
            "status", true,
            "message", "success",
            "description", "Details saved successfully",
            "vendor_req_id", saveStatus
        ));
    }
        

        
        // check mobile number registered
//        @GetMapping("/check-mobile")
//        public ResponseEntity<Boolean> checkMobileNumber(@RequestParam String mobilenumber) {
//            boolean exists = streetVendorService.isMobileRegistered(mobilenumber);
//            
//            return ResponseEntity.ok(exists);
//        }
    

    @GetMapping("/check-mobile")
    public ResponseEntity<Map<String, Object>> checkMobile(@RequestParam String mobilenumber) {

        Map<String, Object> response = new HashMap<>();

        // Step 1: streetVendorService → check vendor_details
        Integer vdid = streetVendorService.findLatestVendorIdByMobile(mobilenumber);

        if (vdid == null) {
            // mobile not registered
            response.put("exists", false);
            response.put("blocked", false);
            return ResponseEntity.ok(response);
        }

        // Mobile exists
        response.put("exists", true);
        response.put("vdid", vdid);

        // Step 2: createventService → check vendor_request_list & vendor_postponed_list
        Map<String, Object> vcStatus = createventservice.checkVendorInVendingTables(vdid);
        String status = vcStatus.get("status").toString();

        switch (status) {
            case "POSTPONED":
                response.put("blocked", true);
                break;
            case "Approved":
                response.put("blocked", true);
                break;
            case "Rejected":
                response.put("blocked", false);
                break;
            case "NO_EVENT":  // exists but never applied in vending committee
                response.put("blocked", true);
                break;
            default:
                response.put("blocked", true);
                break;
        }

        return ResponseEntity.ok(response);
    }

        
        
        
        @PostMapping("/updateDetails")
        public ResponseEntity<Map<String, Object>> updateVenderDetails(
        		
        		@RequestParam (required = false)String latitude,
        		@RequestParam (required = false)String longitude,
                @RequestParam (required = false)String zone,
                @RequestParam (required = false)String ward,
                @RequestParam (required = false)String landmark,
                @RequestParam (required = false)String vending_district,
                @RequestParam (required = false)Integer vending_pincode,
                @RequestParam Integer vending_category,
                @RequestParam Integer tamilvending_category,
                @RequestParam (required = false)String vending_space,
                @RequestParam (required = false) Boolean pm_svanidhi_loan,
                @RequestParam (required = false) Boolean bank_acc_status,
                @RequestParam (required = false, name = "bank_passbook" )MultipartFile bank_passbook,
                @RequestParam (required = false) MultipartFile street_vendor_photo,
                @RequestParam String name,
                @RequestParam (required = false)String f_h_name,
                @RequestParam String mobile_number,
                @RequestParam (required = false)String dob,
                
                @RequestParam (required = false)String gender,
                @RequestParam (required = false)Integer social_category,
                @RequestParam (required = false)Boolean diff_abled,
                @RequestParam (required = false)Integer education_status,
                @RequestParam (required = false)Integer marital_status,
                @RequestParam (required = false)Integer no_of_fam_mem,
                @RequestParam (required = false)Boolean are_any_fam_mem_invol_str,
                
                @RequestParam (required = false)String door_no,
                @RequestParam (required = false)String streetname,
                @RequestParam (required = false)String areaname,
                @RequestParam (required = false)String present_district,
                @RequestParam (required = false)Integer present_pincode,
                @RequestParam (required = false)MultipartFile ration_card_photo,
                @RequestParam (required = false)MultipartFile aadhar_front_photo,
                @RequestParam (required = false)MultipartFile aadhar_back_photo,
                @RequestParam (required = false)String aadharNo,
                @RequestParam (required = false)String raw_bank_passbook,
                @RequestParam (required = false)String raw_street_vendor_photo,
                @RequestParam (required = false)String raw_aadhar_front_photo,
                @RequestParam (required = false)String raw_aadhar_back_photo,
                @RequestParam (required = false)String raw_ration_card_photo,
                @RequestParam (required = false)String tamil_name,
                @RequestParam (required = false)String tamil_f_h_name,
                @RequestParam (required = false)String tamil_landmark,
                @RequestParam (required = false)String tamil_gender,
                @RequestParam (required = false)String tamil_present_district,
                @RequestParam String userId,
                @RequestParam String vendor_req_id){
        	
        	
        	String data = streetVendorService.logEntry(userId, vendor_req_id);
        	
        	if(data.equalsIgnoreCase("success")) {
        	
        	String bankPassbookPath = null;
        	String rationCardPath=null;
            String streetVendorPhotoPath=null;
            String aadharFrontPhotoPath=null;
            String aadharBackPhotoPath=null;
            
            
            
            bankPassbookPath=raw_bank_passbook;
            if(bank_passbook!=null && !bank_passbook.isEmpty()) {
            	bankPassbookPath = streetVendorService.fileUpload	(bank_passbook, "street_vendor_bank");
			 }
            
            rationCardPath=raw_ration_card_photo;
            if(ration_card_photo!=null && !ration_card_photo.isEmpty()) {
            	rationCardPath = streetVendorService.fileUpload	(ration_card_photo, "street_vendor_ration");
			 }
            
            streetVendorPhotoPath=raw_street_vendor_photo;
            if(street_vendor_photo!=null && !street_vendor_photo.isEmpty()) {
            	streetVendorPhotoPath = streetVendorService.fileUpload	(street_vendor_photo, "street_vendor_photo");
			 }
            
            aadharFrontPhotoPath=raw_aadhar_front_photo;
            if(aadhar_front_photo!=null && !aadhar_front_photo.isEmpty()) {
            	aadharFrontPhotoPath = streetVendorService.fileUpload	(aadhar_front_photo, "aadhar_front");
			 }
            
            aadharBackPhotoPath=raw_aadhar_back_photo;
            if(aadhar_back_photo!=null && !aadhar_back_photo.isEmpty()) {
            	aadharBackPhotoPath = streetVendorService.fileUpload	(aadhar_back_photo, "aadhar_back");
			 }
            
            String doorNoVal = (door_no != null && !door_no.trim().isEmpty()) ? door_no : "";
        	String streetNameVal = (streetname != null && !streetname.trim().isEmpty()) ? streetname : "";
        	String areaNameVal = (areaname != null && !areaname.trim().isEmpty()) ? areaname : "";
        	
        	
        	String presentAddress = String.join(", ", doorNoVal, streetNameVal, areaNameVal);
        	
        	String sts=streetVendorService.updateStreetVendorDetailsWeb(
            		latitude, longitude,zone, ward, landmark, vending_district, vending_pincode, vending_category, vending_space,
                    pm_svanidhi_loan, bank_acc_status, bankPassbookPath,
                    streetVendorPhotoPath, name, f_h_name, mobile_number,dob,
                    gender, social_category, diff_abled, education_status, marital_status,
                    no_of_fam_mem, are_any_fam_mem_invol_str,presentAddress, present_district, present_pincode,
                    rationCardPath,aadharFrontPhotoPath, aadharBackPhotoPath,aadharNo,tamil_name,tamil_f_h_name,tamil_landmark,tamil_gender,tamil_present_district,tamilvending_category,userId,vendor_req_id);
            
            if(sts.equals("Error")) {
            	return ResponseEntity.ok(Map.of(
                        "status", false,
                        "message", "failed",
                        "description", "Failed to update the details"
                    ));
            }
        	
        	return ResponseEntity.ok(Map.of(
                    "status", true,
                    "message", "success",
                    "description", "Details Updated successfully"                  
                ));
        	
        }
       
        else {
        	
        	return ResponseEntity.ok(Map.of(
                    "status", false,
                    "message", "failed",
                    "description", "Issues in log entry,So failed to update"
                ));
        }
        	
        }
     
        @GetMapping("/vendingDetailsbyZoneWeb")
        public List<Map<String, Object>> vendingDetailsbyZoneWeb(@RequestParam String zone) {
            
            return streetVendorService.vendingDetailsbyZoneWeb(zone);
        }
        
        
        @GetMapping("/getvedingcommitteelist")
	    public List<Map<String, Object>> getvedingcommitteelist(@RequestParam String date,@RequestParam String zones)
	    {
        	//System.out.println(date);
        	List<Map<String, Object>> streetdb= streetVendorService.getvedingcommitteelist(date,zones);
        	List<Map<String, Object>> savedList = createventservice.getsavedids();
        	List<Map<String, Object>> postponedList = createventservice.getPostponedList();
        	
        	 // Convert savedList → Set<Integer>
        	Set<Integer> savedIds = savedList.stream()
                    .filter(m -> m.get("vdid") != null)
                    .map(m -> Integer.parseInt(m.get("vdid").toString()))
                    .collect(Collectors.toSet());

        	// Convert postponedList → Map<vdid, Map>
            Map<Integer, Map<String, Object>> postponedMap = postponedList.stream()
                    .filter(m -> m.get("vdid") != null)
                    .collect(Collectors.toMap(
                            m -> Integer.parseInt(m.get("vdid").toString()),
                            m -> m
                    ));
            List<Map<String, Object>> finalList = new ArrayList<>();
            
            for (Map<String, Object> row : streetdb) {

                int id = Integer.parseInt(row.get("id").toString());

                // Skip if already saved in vendor_request_list
                if (savedIds.contains(id)) {
                    continue;
                }

                // If postponed, add status & remarks
                if (postponedMap.containsKey(id)) {
                    Map<String, Object> postponed = postponedMap.get(id);

                    row.put("vcp_status", postponed.get("status"));
                    row.put("vcp_remarks", postponed.get("remarks"));

                } 

                finalList.add(row);
            }

            return finalList;
	    }
             
        
      @GetMapping("/vendorDetailsById")
      public Map<String, Object> vendorDetailsById(@RequestParam("requestId") String requestId) {
    	// From gcc_street_vendor DB
    	    List<Map<String, Object>> mainDetails = streetVendorService.vendorDetailsById(requestId);
    	    Map<String, Object> vendor = mainDetails.get(0);

    	    // From vendingcommitte DB
    	    Map<String, Object> latestPostponed = createventservice.getLatestPostponed(requestId);
    	    List<Map<String, Object>> postponedHistory = createventservice.getPostponedHistory(requestId);

    	    vendor.put("latestPostponed", latestPostponed);
    	    vendor.put("postponedHistory", postponedHistory);

    	    return vendor;
      }
      
      @GetMapping("/getwardbyzone")
	    public List<Map<String, Object>> ViewWardDropdown(@RequestParam String zone) {
	       
	    	return streetVendorService.ViewWardDropdown(zone);
	           	        
	    }
        
        
}
    
    

