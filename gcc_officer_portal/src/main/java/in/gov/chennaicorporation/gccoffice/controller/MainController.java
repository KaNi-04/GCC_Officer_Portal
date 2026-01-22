package in.gov.chennaicorporation.gccoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc")
@Controller("gccOfficeController")
public class MainController {
   
    @GetMapping({"", "/", "/index"})
	public String main(Model model) {
    	String LoginUserId = LoginUserInfo.getLoginUserId();
        if (LoginUserId != null && !LoginUserId.isEmpty()) {
            System.out.println("String UserID: " + LoginUserId);
        }
		model.addAttribute("LoginUserId",LoginUserId);
		return "index";
	}
	
	@GetMapping("/widget-statistic")
	public String widgetStatistic(Model model) {
		return "widget-statistic";
	}
}
