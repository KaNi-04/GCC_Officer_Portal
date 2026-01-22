package in.gov.chennaicorporation.gccoffice.circular.controller;

import org.hibernate.hql.internal.ast.tree.RestrictableStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.service.Base64Util;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/circular")
@Controller("CircularController")
public class MainController {
	private final RestTemplate restTemplate;
	private Environment environment;
	private String BasePath = "modules/circular/";
	
	@Autowired
	public MainController(RestTemplate restTemplate,Environment environment) {
		this.restTemplate = restTemplate;
		this.environment = environment;
	}
 
	@GetMapping({"", "/", "/index", "/dashboard"})
	public String dashboard(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"index";
	}
	
	@GetMapping({"/new"})
	public String newCircular(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"new_circular";
	}
	
	@GetMapping("/list")
    public String inbox(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		String uploadDirectory = environment.getProperty("file.upload.directory");
        String serviceFolderName = environment.getProperty("cir_foldername");
        uploadDirectory = uploadDirectory + serviceFolderName;
        model.addAttribute("uploadDirectory",uploadDirectory);
		return BasePath+"inbox";
	}
	
	@GetMapping("/report")
    public String report(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"report";
	}
}
