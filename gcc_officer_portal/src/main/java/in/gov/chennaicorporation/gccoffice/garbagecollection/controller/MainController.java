package in.gov.chennaicorporation.gccoffice.garbagecollection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.garbagecollection.modeldata.Request;

@RequestMapping("/garbagecollection")
@Controller("garbageCollectionController")
public class MainController {
	
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	
	@Autowired
    public MainController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
	
	@GetMapping({"", "/", "/index", "/dashboard"})
	public String home(Model model) {
		return "modules/garbagecollection/index";
	}
	
	@GetMapping("/request-create")
	public String category(Model model) {
		
		//String apiUrl = appConfig.qrAssetFeedback + "/api/category";
        //AssetCategory[] assetCategoryList = restTemplate.getForObject(apiUrl, AssetCategory[].class);
        
		//model.addAttribute("assetcategorylist",assetCategoryList);
		return "modules/taxcollection/request-create";
	}
	
	@GetMapping("/request-new")
	public String newRequestList(Model model) {
		String apiUrl = appConfig.garbageCollection+"/api/getRequest";
        Request[] requestList = restTemplate.getForObject(apiUrl, Request[].class);
		model.addAttribute("requestlist",requestList);
		return "modules/garbagecollection/request-new";
	}
	
	@GetMapping("/request-pending")
	public String pendingRequestList(Model model) {
		String apiUrl = appConfig.garbageCollection+"/api/getPendingRequest";
        Request[] requestList = restTemplate.getForObject(apiUrl, Request[].class);
		model.addAttribute("requestlist",requestList);
		return "modules/garbagecollection/request-pending";
	}
	
	@GetMapping("/request-completed")
	public String completedRequestList(Model model) {
		String apiUrl = appConfig.garbageCollection+"/api/getCompletedRequest";
        Request[] requestList = restTemplate.getForObject(apiUrl, Request[].class);
		model.addAttribute("requestlist",requestList);
		return "modules/garbagecollection/request-completed";
	}
	
	@GetMapping("/request-outofscope")
	public String outofscopeRequestList(Model model) {
		String apiUrl = appConfig.garbageCollection+"/api/getOutOfScopeRequest";
        Request[] requestList = restTemplate.getForObject(apiUrl, Request[].class);
		model.addAttribute("requestlist",requestList);
		return "modules/garbagecollection/request-outofscope";
	}
	
}