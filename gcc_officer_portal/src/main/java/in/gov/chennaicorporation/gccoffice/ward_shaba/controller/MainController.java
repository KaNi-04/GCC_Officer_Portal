package in.gov.chennaicorporation.gccoffice.ward_shaba.controller;

import java.util.List;
import java.util.Map;


import org.hibernate.hql.internal.ast.tree.RestrictableStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.service.Base64Util;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;
import in.gov.chennaicorporation.gccoffice.ward_shaba.service.Petition;

@RequestMapping("/gcc/ward_shaba")
@Controller("WardShabaController")
public class MainController {
	private final RestTemplate restTemplate;
	private String BasePath = "modules/ward_shaba/";
	private Petition petition;
	
	@Autowired
	public MainController(RestTemplate restTemplate,Petition petition) {
		this.restTemplate = restTemplate;
		this.petition = petition;
	}

	@GetMapping({ "", "/", "/index", "/dashboard" })
	public String dashboard(Model model) {
		String LoginUserId = LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId", LoginUserId);
		return BasePath + "index";
	}

	@GetMapping({ "/event_creation" })
	public String eventcreation(Model model) {
		String LoginUserId = LoginUserInfo.getLoginUserId();
		
		List<Map<String, Object>> wardList = petition.getUserWard(LoginUserId);
		String wardtxt = "0";
		// Ensure the list is not empty to avoid IndexOutOfBoundsException
		if (!wardList.isEmpty()) {
		    // Get the first map (the first result)
		    Map<String, Object> wardMap = wardList.get(0);

		    // Extract the 'ward' value from the map
		     wardtxt = (String) wardMap.get("ward");

		    // Print the ward value
		    System.out.println("User Ward Text: " + wardtxt);
		} else {
		    System.out.println("No ward found for the user.");
		}
				
		model.addAttribute("LoginUserId", LoginUserId);
		model.addAttribute("myWard", wardtxt);
		return BasePath + "eventcreation";
	}

	@GetMapping("/new_minutes")
	public String newPetition(@RequestParam(required = false) String event,
			@RequestParam(value = "eventid", required = false) String eventid, Model model) {
		String LoginUserId = LoginUserInfo.getLoginUserId();

		List<Map<String, Object>> wardList = petition.getUserWard(LoginUserId);
		String wardtxt = "0";
		// Ensure the list is not empty to avoid IndexOutOfBoundsException
		if (!wardList.isEmpty()) {
		    // Get the first map (the first result)
		    Map<String, Object> wardMap = wardList.get(0);

		    // Extract the 'ward' value from the map
		     wardtxt = (String) wardMap.get("ward");

		    // Print the ward value
		    System.out.println("User Ward Text: " + wardtxt);
		} else {
		    System.out.println("No ward found for the user.");
		}
		
		model.addAttribute("LoginUserId", LoginUserId);
		model.addAttribute("myWard", wardtxt);
		if (event != null) {
			model.addAttribute("eventName", event);
			model.addAttribute("eventId", eventid);
		}

		return BasePath + "new_minutes";
	}

	@GetMapping("/minutes-list")
	public String petitionList(
			@RequestParam(required = false) String event,
			@RequestParam(value = "eventid", required = false) String eventid, 
			Model model) {
		String LoginUserId = LoginUserInfo.getLoginUserId();
		
		List<Map<String, Object>> wardList = petition.getUserWard(LoginUserId);
		String wardtxt = "0";
		// Ensure the list is not empty to avoid IndexOutOfBoundsException
		if (!wardList.isEmpty()) {
		    // Get the first map (the first result)
		    Map<String, Object> wardMap = wardList.get(0);

		    // Extract the 'ward' value from the map
		     wardtxt = (String) wardMap.get("ward");

		    // Print the ward value
		    System.out.println("User Ward Text: " + wardtxt);
		} else {
		    System.out.println("No ward found for the user.");
		}
		
		model.addAttribute("LoginUserId", LoginUserId);
		model.addAttribute("myWard", wardtxt);
		
		if (event != null) {
			model.addAttribute("eventName", event);
			model.addAttribute("eventId", eventid);
		}
		return BasePath + "minutes_list";
	}

}