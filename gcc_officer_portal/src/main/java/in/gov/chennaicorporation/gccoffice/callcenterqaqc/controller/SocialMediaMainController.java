package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/callcenterqaqcsocialmedia") 
@Controller("callCenterQaQcSocialMediaController")
public class SocialMediaMainController {
	
	@GetMapping("/socialmediacomplaints")
	public String viewSocialMediaComplaints(Model model) {
		String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);
	    
	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("userId", userId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
	    
	    return "modules/callcenterqaqc/socialmediacomplaints";
	}
	
	
	@GetMapping("/smfinishedcomplaints")
	public String viewSocialMediaCompletedComplaints(Model model) {
		String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);
	    
	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("userId", userId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
	    
	    return "modules/callcenterqaqc/socialmediacompletedlist";
	}

}
