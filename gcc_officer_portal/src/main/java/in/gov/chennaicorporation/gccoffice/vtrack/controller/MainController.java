package in.gov.chennaicorporation.gccoffice.vtrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;
import in.gov.chennaicorporation.gccoffice.vtrack.service.VehicleService;

@RequestMapping("/gcc/vtrack")
@Controller("vTrackController")
public class MainController {
	private final RestTemplate restTemplate;
	private String BasePath = "modules/vtrack/";
	
	@Autowired	
	public VehicleService vehicleService;
	
	@Autowired
	public MainController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@GetMapping({"/","/index","/messagelist"})
	public String messagelist(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		List<Map<String,Object>> VehicleLists=vehicleService.getAllSavedMessages();
		model.addAttribute("VehicleLists", VehicleLists);
		return BasePath+"messagelist";
	}
	
}
