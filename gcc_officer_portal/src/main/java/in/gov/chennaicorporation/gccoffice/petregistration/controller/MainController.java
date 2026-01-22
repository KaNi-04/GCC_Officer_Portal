package in.gov.chennaicorporation.gccoffice.petregistration.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.petregistration.data.PetCountInfo;
import in.gov.chennaicorporation.gccoffice.petregistration.data.RequestList;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/petregistration")
@Controller("petregistration")
public class MainController {
	private final RestTemplate restTemplate;
	private final LoginUserInfo loginUserInfo;
	private final AppConfig appConfig;
    
    @Autowired
    public MainController(RestTemplate restTemplate,LoginUserInfo loginUserInfo, AppConfig appConfig) {
    	this.restTemplate = restTemplate;
        this.loginUserInfo = loginUserInfo;
        this.appConfig = appConfig;
    }
    
    @Lookup
    public static String[] getDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        String formattedDate = dateFormat.format(calendar.getTime());

        String currentDate = dateFormat.format(new Date());
        System.out.println("formattedDate : "+formattedDate);
        return new String[] { formattedDate, currentDate };
    }

    
    @GetMapping({"", "/", "/index", "/dashboard"})
	public String main(Model model,@RequestParam(required=false) String zone) {
    	String userId = LoginUserInfo.getLoginUserId();
    	String baseUrl = appConfig.petRegistration + "/api/admin/dashboard";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl).queryParam("zone",zone);	
    	PetCountInfo dash = restTemplate.getForObject(builder.toUriString(),PetCountInfo.class);
        if (userId != null && !userId.isEmpty()) {
            System.out.println("String UserID: " + userId);
        }
        model.addAttribute("dash",dash);
		return "modules/petregistration/index";
	}
    
    @GetMapping("/requestlist")
    public String viewRequest(Model model,
    		@RequestParam(required=false) String fromDate,
    		@RequestParam(required =false) String toDate,
    		@RequestParam(required=false) String zone) {
    	
    	if(fromDate==null && toDate==null) {
			String[] dates = getDates();
			 fromDate=dates[0];
			 toDate=dates[1];
			 model.addAttribute(fromDate);
			 model.addAttribute(toDate);
		}
    	
    	String baseUrl = appConfig.petRegistration + "/api/admin/licenserequest";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("zone",zone)
    			.queryParam("fromDate",fromDate)
    			.queryParam("toDate",toDate);
    	
    	RequestList[] requestList = restTemplate.getForObject(builder.toUriString(), RequestList[].class);
    	model.addAttribute("petlist",requestList);
    	
    	return "modules/petregistration/request-list";
    }
    
    @GetMapping("/acceptedlist")
    public String viewAccepted(Model model,
    		@RequestParam(required=false) String fromDate,
    		@RequestParam(required =false) String toDate,
    		@RequestParam(required=false) String zone) {
    	
    	if(fromDate==null && toDate==null) {
			String[] dates = getDates();
			 fromDate=dates[0];
			 toDate=dates[1];
			 model.addAttribute(fromDate);
			 model.addAttribute(toDate);
		}
    	
    	String baseUrl = appConfig.petRegistration + "/api/admin/acceptedlist";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("zone",zone)
    			.queryParam("fromDate",fromDate)
    			.queryParam("toDate",toDate);
    	
    	RequestList[] requestList = restTemplate.getForObject(builder.toUriString(), RequestList[].class);
    	model.addAttribute("petlist",requestList);
    	
    	return "modules/petregistration/accepted-list";
    }
    
    @GetMapping("/rejectedlist")
    public String viewRejected(Model model,
    		@RequestParam(required=false) String fromDate,
    		@RequestParam(required =false) String toDate,
    		@RequestParam(required=false) String zone) {
    	
    	if(fromDate==null && toDate==null) {
			String[] dates = getDates();
			 fromDate=dates[0];
			 toDate=dates[1];
			 model.addAttribute(fromDate);
			 model.addAttribute(toDate);
		}
    	
    	String baseUrl = appConfig.petRegistration + "/api/admin/rejectedlist";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("zone",zone)
    			.queryParam("fromDate",fromDate)
    			.queryParam("toDate",toDate);
    	
    	RequestList[] requestList = restTemplate.getForObject(builder.toUriString(), RequestList[].class);
    	model.addAttribute("petlist",requestList);
    	
    	return "modules/petregistration/accepted-list";
    }
    
    @GetMapping("/licenseissued")
    public String viewLicenseIssued(Model model,
    		@RequestParam(required=false) String fromDate,
    		@RequestParam(required =false) String toDate,
    		@RequestParam(required=false) String zone) {
    	
    	if(fromDate==null && toDate==null) {
			String[] dates = getDates();
			 fromDate=dates[0];
			 toDate=dates[1];
			 model.addAttribute(fromDate);
			 model.addAttribute(toDate);
		}
    	
    	String baseUrl = appConfig.petRegistration + "/api/admin/licenseIssued";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("zone",zone)
    			.queryParam("fromDate",fromDate)
    			.queryParam("toDate",toDate);
    	
    	RequestList[] requestList = restTemplate.getForObject(builder.toUriString(), RequestList[].class);
    	model.addAttribute("petlist",requestList);
    	
    	return "modules/petregistration/accepted-list";
    }
}
