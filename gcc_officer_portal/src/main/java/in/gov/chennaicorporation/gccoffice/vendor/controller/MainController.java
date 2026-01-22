package in.gov.chennaicorporation.gccoffice.vendor.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;
import in.gov.chennaicorporation.gccoffice.vendor.service.StreetVendorService;

@RequestMapping("/gcc/vendor") 
@Controller("vendorMainController")
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
	private StreetVendorService streetVendorService;
	
	@GetMapping("/addevent")
	public String viewAddEvent(Model model)
	{	
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);		
        
		return "modules/vendors/addevent";
	}
	
	@GetMapping("/requestlist")
	public String viewRequestList(Model model)
	{	
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String zone=streetVendorService.getloginUserZone(userId);
		model.addAttribute("zone",zone);
		//System.out.println("zone="+zone);		
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);		
        
		return "modules/vendors/RequestList";
	}
	
	@GetMapping("/eventlist")
	public String viewEventList(Model model)
	{	
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String zone=streetVendorService.getloginUserZone(userId);
		model.addAttribute("zone",zone);
		//System.out.println("zone="+zone);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);		
        
		return "modules/vendors/EventList";
	}
	
	@GetMapping("/streetvendorform")
	public String streetvendorform(Model model)
	{	
				
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);		
		
		
		
		List<Map<String, Object>>communities=streetVendorService.fetchCommunities();
	    //System.out.println("communities===="+communities);
	    model.addAttribute("communities", communities);
	    
	    
	    
	    List<Map<String, Object>>maritalStatuses=streetVendorService.fetchMaritalStatus();
	    //System.out.println("maritalStatuses===="+maritalStatuses);
	    model.addAttribute("maritalStatuses", maritalStatuses);
		
	    
	    
	    List<Map<String, Object>>educationList=streetVendorService.fetchEducationStatus();
	    //System.out.println("educationList===="+educationList);
	    model.addAttribute("educationList", educationList);
	    
	    
	    
	    List<Map<String, Object>>vendingCategories=streetVendorService.fetchvendingCategory();
	    //System.out.println("vendingCategory===="+vendingCategories);
	    model.addAttribute("vendingCategories", vendingCategories);
	    
	    List<Map<String, Object>>tamilvendingCategories=streetVendorService.fetchtamilvendingCategory();
	    //System.out.println("tamilvendingCategories===="+tamilvendingCategories);
	    model.addAttribute("tamilvendingCategories", tamilvendingCategories);
	    
	    
		//List<Map<String, Object>>zones=streetVendorService.fetchZones();
	    //System.out.println("zones===="+zones);
	    //model.addAttribute("zones", zones);

		return "modules/vendors/streetVendorRequestForm";
	}
	
	
	@GetMapping("/streetvendorlist")
	public String viewVendorRequestList(Model model)
	{	
		String LoginUserId =LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId); 
		
		LoginUserInfo.getCurrentUserDetails().getUsername();
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);	
		
		
		List<Map<String, Object>>communities=streetVendorService.fetchCommunities();	    
	    model.addAttribute("communities", communities);
	    
	    	    
	    List<Map<String, Object>>maritalStatuses=streetVendorService.fetchMaritalStatus();
	    model.addAttribute("maritalStatuses", maritalStatuses);
		
	    	    
	    List<Map<String, Object>>educationList=streetVendorService.fetchEducationStatus();
	    model.addAttribute("educationList", educationList);
	    
	    	    
	    List<Map<String, Object>>vendingCategories=streetVendorService.fetchvendingCategory();
	    model.addAttribute("vendingCategories", vendingCategories);
	    
	    List<Map<String, Object>>tamilvendingCategories=streetVendorService.fetchtamilvendingCategory();
	    model.addAttribute("tamilvendingCategories", tamilvendingCategories);
		
		return "modules/vendors/streetVendorRequestList";
	}
	
	@GetMapping("/zonerequests")
	public String viewZoneBasedDetails(Model model)
	{	
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);	
		
		String zone=streetVendorService.getloginUserZone(userId);
		model.addAttribute("zone",zone);
		//System.out.println("zone="+zone);
		
		List<Map<String, Object>>communities=streetVendorService.fetchCommunities();	    
	    model.addAttribute("communities", communities);
	    
	    	    
	    List<Map<String, Object>>maritalStatuses=streetVendorService.fetchMaritalStatus();
	    model.addAttribute("maritalStatuses", maritalStatuses);
		
	    	    
	    List<Map<String, Object>>educationList=streetVendorService.fetchEducationStatus();
	    model.addAttribute("educationList", educationList);
	    
	    	    
	    List<Map<String, Object>>vendingCategories=streetVendorService.fetchvendingCategory();
	    model.addAttribute("vendingCategories", vendingCategories);
	    
	    List<Map<String, Object>>tamilvendingCategories=streetVendorService.fetchtamilvendingCategory();
	    model.addAttribute("tamilvendingCategories", tamilvendingCategories);
		
		return "modules/vendors/zonerequests";
	
	}
	

}
