package in.gov.chennaicorporation.gccoffice.greencommittee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.greencommittee.service.DashboardService;
import in.gov.chennaicorporation.gccoffice.greencommittee.service.PendinglistService;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/greencommittee")
@Controller("greencommitteeMainController")
public class Maincontroller {

	@Autowired
	private LoginUserInfo loginUserInfo;

	@Autowired
	private PendinglistService pendinglistService;

	@Autowired
	private DashboardService dashboardService;

	@GetMapping("/dashboard")
	public String viewdashboard(Model model) {
		String LoginUserId = LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId", LoginUserId);

		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);

		return "modules/greencommittee/dashboard";
	}

	@GetMapping("/createmeeting")
	public String viewcreatemeeting(Model model) {
		String LoginUserId = LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId", LoginUserId);

		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);

		return "modules/greencommittee/createmeeting";
	}

	@GetMapping("/pendinglist")
	public String viewpendinglist(Model model) {
		String LoginUserId = LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId", LoginUserId);

		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);

		return "modules/greencommittee/pendinglist";
	}

	@GetMapping("/monitoring")
	public String viewmonitoring(Model model) {
		String LoginUserId = LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId", LoginUserId);

		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);

		return "modules/greencommittee/monitoring";
	}

	@GetMapping("/reports")
	public String viewreports(Model model) {
		String LoginUserId = LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId", LoginUserId);

		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);

		return "modules/greencommittee/reports";
	}

}
