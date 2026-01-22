package in.gov.chennaicorporation.gccoffice.mtm.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/mtm")
@Controller("mtmController")
public class MainController {
	
	private final RestTemplate restTemplate;
	private final LoginUserInfo loginUserInfo;
	private final AppConfig appConfig;
	
	@Autowired
    public MainController(RestTemplate restTemplate, AppConfig appConfig,LoginUserInfo loginUserInfo) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
        this.loginUserInfo = loginUserInfo;
    }
	
	@GetMapping({"", "/", "/index", "/dashboard"})
	public String home(Model model,HttpSession session) {
		session.setAttribute("userName", loginUserInfo.getLoginUserId().toString());
		return "modules/mtm/index";
	}
}