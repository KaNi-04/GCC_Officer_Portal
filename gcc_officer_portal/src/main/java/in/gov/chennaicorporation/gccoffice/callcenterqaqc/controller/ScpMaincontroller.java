package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcAgentsService;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/callcenterqaqcscp") 
@Controller("callCenterQaQcScpController")
public class ScpMaincontroller {
		
	@Autowired
	private QaqcAgentsService agentsService;
	
	@GetMapping("/scpverification")
	public String viewDomesticWastepage(Model model) {
		String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);
	    
	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("userId",userId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
	    
	    List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
	    
	    return "modules/callcenterqaqc/scpverification";
	}
	
	
	@GetMapping("/scpreport")
	public String viewReportPage(Model model) {
		String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);
	    
	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("userId",userId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
	    
	    List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
	    
	    return "modules/callcenterqaqc/scpreport";
	}

}
