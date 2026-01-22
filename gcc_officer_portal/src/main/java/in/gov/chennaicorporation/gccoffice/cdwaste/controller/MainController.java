package in.gov.chennaicorporation.gccoffice.cdwaste.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;


import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/dumpregistration") 
@Controller("dumpregistrationMainController")
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
	
	@GetMapping("/officer")
	public String viewOfficer(Model model)
	{	
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		
        
		return "modules/cdwastecollect/officer";
	}
	
	
	
	@GetMapping("/lessthanoneton")
	public String viewLessoneTonList(Model model)
	{	
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		      
		return "modules/cdwastecollect/lessoneton";
	}
	
	
	@GetMapping("/overallreport")
	public String wasteCollectionReports(Model model)
	{	 
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		return "modules/cdwastecollect/reports";
	}
	




}
