package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/callcenterqaqccomplaintstatus") 
@Controller("callCenterQaQcComplaintStatusController")
public class ComplaintStatusMainController {
	
	@GetMapping("/complaintstatus")
	public String viewstatuscheck(Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		
						
		return "modules/callcenterqaqc/complaintstatus";
	}

}
