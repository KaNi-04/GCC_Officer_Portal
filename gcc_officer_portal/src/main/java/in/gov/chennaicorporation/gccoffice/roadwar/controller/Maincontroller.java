package in.gov.chennaicorporation.gccoffice.roadwar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/roadwarweb")
@Controller("RoadWarMainController")
public class Maincontroller {

	@Autowired
	private LoginUserInfo loginUserInfo;
	
	
	@GetMapping("/regform")
	public String viewdashboard(Model model) {
		String LoginUserId = LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId", LoginUserId);

		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);

		return "modules/roadwar/regform";
	}
	
}
