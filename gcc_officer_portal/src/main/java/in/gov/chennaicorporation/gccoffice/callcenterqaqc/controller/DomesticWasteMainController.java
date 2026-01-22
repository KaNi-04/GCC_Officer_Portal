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

@RequestMapping("/gcc/domestic_waste") 
@Controller("callCenterDomesticWasteController")
public class DomesticWasteMainController {
	
	@Autowired
	private QaqcAgentsService agentsService;
	
	@GetMapping("/registerpage")
	public String viewDomesticWastepage(Model model) {
		String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);
	    
	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("userId",userId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
	    
	    List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
	    
	    return "modules/callcenterqaqc/domesticwastecomplaints";
	}
	
	
	@GetMapping("/checkstatus")
	public String viewComplaintspage(Model model) {
		String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);
	    
	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("userId",userId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
	    
	    List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
	    
	    return "modules/callcenterqaqc/domesticwastecheckstatus";
	}
	
	@GetMapping("/cancelpage")
	public String viewpendingpage(Model model) {
		String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);
	    
	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("userId",userId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
	    
	    List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
	    
	    return "modules/callcenterqaqc/domesticwastecancel";
	}
	
	

}
