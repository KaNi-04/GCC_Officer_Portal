package in.gov.chennaicorporation.gccoffice.taxcollection.controller;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.Asset;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetCategory;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetCategoryQuestion;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetParameter;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetParameterQuestions;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetQuestion;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;
import in.gov.chennaicorporation.gccoffice.taxcollection.entity.TaxCollectionRequestEntity;
import in.gov.chennaicorporation.gccoffice.taxcollection.repository.TaxCollectionRequestRepository;

@RequestMapping("/gcc/taxcollection")
@Controller("taxCollectionController")
public class MainController {
	
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	private final TaxCollectionRequestRepository taxCollectionRequestRepository;
	
	@Autowired
    public MainController(
    		RestTemplate restTemplate, 
    		AppConfig appConfig,
    		TaxCollectionRequestRepository taxCollectionRequestRepository) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
        this.taxCollectionRequestRepository = taxCollectionRequestRepository;
    }
	
	@GetMapping({"", "/", "/index", "/dashboard"})
	public String home(Model model) {
		return "modules/taxcollection/index";
	}
	
	@GetMapping("/request-create")
	public String CreateRequest(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		List<TaxCollectionRequestEntity> taxCollectionRequestEntities = taxCollectionRequestRepository.showAll();
        model.addAttribute("taxCollectionRequestEntities",taxCollectionRequestEntities);
		return "modules/taxcollection/request-create";
	}
	
	@GetMapping("/request-list")
	public String RequestList(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		List<TaxCollectionRequestEntity> taxCollectionRequestEntities = taxCollectionRequestRepository.showAll();
        model.addAttribute("taxCollectionRequestEntities",taxCollectionRequestEntities);
		return "modules/taxcollection/request-list";
	}
	
	@GetMapping("/showimage")
	public String showimage(Model model) {
		return "modules/taxcollection/request-create";
	}
	
}