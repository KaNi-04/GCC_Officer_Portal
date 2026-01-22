package in.gov.chennaicorporation.gccoffice.sos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/sos")
@Controller("SOSRestController")
public class SOSController {
	
	private String BasePath = "modules/sos/";
	
	@GetMapping({"", "/", "/index"})
	public String dashboard(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"inbox";
	}
	
	@GetMapping({"/new"})
	public String sosNew(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"new_sos";
	}
	
	@GetMapping({"/pending"})
	public String sosPending(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"pending_inbox";
	}
	
	@GetMapping({"/closed"})
	public String sosClosed(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"closed_inbox";
	}

}
