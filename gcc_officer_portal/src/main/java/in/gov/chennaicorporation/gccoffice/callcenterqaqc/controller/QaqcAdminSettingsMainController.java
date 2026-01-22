package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcAdminSettingService;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/callcenterqaqcadminsettings") 
@Controller("callCenterQaQcAdminSettingsController")
public class QaqcAdminSettingsMainController {
	
	@Autowired
	private QaqcAdminSettingService adminService;
	
	@GetMapping("/adminsettings")
	public String viewAdminSettings(Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		
		
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		
		List<Map<String, Object>> Counts=adminService.getAdminSettingCounts();
		model.addAttribute("Counts", Counts);
		
		List<Map<String, Object>> agents=adminService.getAgentsForCallingtype();
		model.addAttribute("Agents", agents);
		
				
		return "modules/callcenterqaqc/qaqcadminsettings";
	}

}
