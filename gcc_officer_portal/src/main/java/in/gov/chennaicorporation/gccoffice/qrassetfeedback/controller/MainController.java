package in.gov.chennaicorporation.gccoffice.qrassetfeedback.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.Asset;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetCategory;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetCategoryQuestion;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetParameter;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetParameterQuestions;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetQuestion;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.Feedback;

@RequestMapping("/gcc/qrfeedback")
@Controller("qrAssetFeedbackController")
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
		return "modules/qrfeedback/index";
	}
	
	@GetMapping("/master/asset-category")
	public String category(Model model) {
		
		String apiUrl = appConfig.qrAssetFeedback + "/api/category";
        AssetCategory[] assetCategoryList = restTemplate.getForObject(apiUrl, AssetCategory[].class);
        
		model.addAttribute("assetcategorylist",assetCategoryList);
		model.addAttribute("qrAssetFeedbackApiUrl",appConfig.qrAssetFeedback);
		return "modules/qrfeedback/master/asset-category";
	}
	
	@GetMapping("/master/asset-category-questions")
	public String categoryQuestions(Model model) {
		String apiUrl = appConfig.qrAssetFeedback + "/api/category";
        AssetCategory[] assetCategoryList = restTemplate.getForObject(apiUrl, AssetCategory[].class);
        
		model.addAttribute("assetcategorylist",assetCategoryList);
		
		apiUrl = appConfig.qrAssetFeedback + "/api/category/question";
		AssetCategoryQuestion[] assetCategoryQuestionList = restTemplate.getForObject(apiUrl, AssetCategoryQuestion[].class);
        
		model.addAttribute("assetcategoryquestionlist",assetCategoryQuestionList);
		return "modules/qrfeedback/master/asset-category-questions";
	}
	
	@GetMapping("/master/asset-parameter")
	public String parameter(Model model) {
		String apiUrl = appConfig.qrAssetFeedback + "/api/parameter";
		AssetParameter[] assetParameterList = restTemplate.getForObject(apiUrl, AssetParameter[].class);
        
		model.addAttribute("assetparameterlist",assetParameterList);
		return "modules/qrfeedback/master/asset-parameter";
	}
	
	@GetMapping("/master/asset-parameter-questions")
	public String parameterQuestions(Model model) {
		String apiUrl = appConfig.qrAssetFeedback + "/api/parameter/question";
		AssetParameterQuestions[] assetParameterQuestionsList = restTemplate.getForObject(apiUrl, AssetParameterQuestions[].class);
        
		model.addAttribute("assetparameterquestionslist",assetParameterQuestionsList);
		return "modules/qrfeedback/master/asset-parameter-questions";
	}
	
	// For Asset
	@GetMapping("/assets/asset-list")
	public String asset(Model model) {
		String apiUrl = appConfig.qrAssetFeedback + "/api/asset";
		Asset[] assetList = restTemplate.getForObject(apiUrl, Asset[].class);
        
		model.addAttribute("assetlist",assetList);
		return "modules/qrfeedback/assets/asset-list";
	}

	@GetMapping("/assets/asset-questions")
	public String assetQuestions(Model model) {
		String apiUrl = appConfig.qrAssetFeedback + "/api/asset/question";
		AssetQuestion[] assetQuestionsList = restTemplate.getForObject(apiUrl, AssetQuestion[].class);
        
		model.addAttribute("assetquestionslist",assetQuestionsList);
		return "modules/qrfeedback/assets/asset-questions";
	}
	
	@GetMapping("/feedbacks/feedback-list")
	public String assetFeedback(Model model) {
		String apiUrl = appConfig.qrAssetFeedback + "/api/feedback";
		Feedback[] feedbackList = restTemplate.getForObject(apiUrl, Feedback[].class);
		model.addAttribute("feedbacklist",feedbackList);
		return "modules/qrfeedback/feedbacks/feedback-list";
	}
	@GetMapping("/feedbacks/feedback-list-yes")
	public String assetFeedbackByTypeYes(Model model) {
		String option = "[yes]";
		String apiUrl = appConfig.qrAssetFeedback + "/api/feedback/data/option/"+option;
		Feedback[] feedbackList = restTemplate.getForObject(apiUrl, Feedback[].class);
		model.addAttribute("feedbacklist",feedbackList);
		return "modules/qrfeedback/feedbacks/feedback-list-yes";
	}
	@GetMapping("/feedbacks/feedback-list-no")
	public String assetFeedbackByTypeNo(Model model) {
		String option = "[no]";
		String apiUrl = appConfig.qrAssetFeedback + "/api/feedback/data/option/"+option;
		Feedback[] feedbackList = restTemplate.getForObject(apiUrl, Feedback[].class);
		model.addAttribute("feedbacklist",feedbackList);
		return "modules/qrfeedback/feedbacks/feedback-list-no";
	}
	
}