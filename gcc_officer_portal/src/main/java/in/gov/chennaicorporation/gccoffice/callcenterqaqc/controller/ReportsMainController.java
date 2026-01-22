package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;


@RequestMapping("/gcc/callcenterqaqcreports") 
@Controller("callCenterQaQcReportsController")
public class ReportsMainController {
	
	
	@GetMapping("/overallreport")
	public String viewOverallReports(Model model) {
		String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);
	    
	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
	    
	    return "modules/callcenterqaqc/overallreport";
	}
	
	
	@GetMapping("/qaqctasklist")
	public String viewqaqctasklist(Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);

		
		return "modules/callcenterqaqc/qaqctasklist";
	}
	
	@GetMapping("/qaqcagentperformance")
	public String viewAgentPerformance(Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
				
		return "modules/callcenterqaqc/qaqcagentperformance";
	}

}
