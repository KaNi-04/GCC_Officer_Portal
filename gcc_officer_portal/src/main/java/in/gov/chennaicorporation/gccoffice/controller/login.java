package in.gov.chennaicorporation.gccoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/gcc")
@Controller("gccLoginController")
public class login {
	@GetMapping("/login")
	public String home(Model model) {
		return "login";
	}
}
