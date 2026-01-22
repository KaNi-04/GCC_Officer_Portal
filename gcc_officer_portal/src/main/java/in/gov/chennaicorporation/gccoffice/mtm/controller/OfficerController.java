package in.gov.chennaicorporation.gccoffice.mtm.controller;

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

@RequestMapping("/gcc/mtm/officer")
@Controller("mtmOfficerController")
public class OfficerController {
	
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	private final String basePath = "modules/mtm/officer";
	
	@Autowired
    public OfficerController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
	
	@GetMapping({"", "/", "/index", "/dashboard"})
	public String home(Model model) {
		return basePath+"/index";
	}
	
	@GetMapping({"/petition-list"})
	public String showPetitionList(Model model) {
		return basePath+"/petition-list";
	}
}