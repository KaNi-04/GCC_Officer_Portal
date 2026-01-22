package in.gov.chennaicorporation.gccoffice.zerohours_petition.controller;

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

@RequestMapping("/gcc/zerohours_petition")
@Controller("ZeorHoursPetitionController")
public class MainController {
	private final RestTemplate restTemplate;
	private String BasePath = "modules/zerohours_petition/";
	@Autowired
	public MainController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
 
	@GetMapping({"", "/", "/index", "/dashboard"})
	public String dashboard(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"index";
	}
	
	@GetMapping({"/new_petition"})
	public String newPetition(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"new_petition";
	}
	
	@GetMapping("/petition-ack/{petitionNo}")
    public String petitionAck(Model model,@PathVariable("petitionNo") String petitionNo) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		model.addAttribute("petitionNo",petitionNo);
		return BasePath+"petition_ack";
	}
	
	@GetMapping("/petition-list")
    public String petitionList(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"petition_list";
	}
	
	@GetMapping("/unmapped-petition-list")
    public String unMappedPetitionList(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"petition_not_mapped_list";
	}
	
	@GetMapping("/update-petition")
    public String petitionUpadte(Model model,@RequestParam(value="petitionNo", required = false) String petitionNo) {
		
		if (petitionNo != null && !petitionNo.isEmpty()) {
			// Decrypt the encrypted value
			 petitionNo = Base64Util.decodeBase64(petitionNo);
		}
		else {
			 petitionNo = null;
		}
       
        
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		model.addAttribute("petitionNo",petitionNo);
		return BasePath+"petition_update";
	}
	
	@GetMapping("/view-petition")
    public String petitionView(Model model,@RequestParam(value="petitionNo", required = false) String petitionNo) {
		
		if (petitionNo != null && !petitionNo.isEmpty()) {
			// Decrypt the encrypted value
			 petitionNo = Base64Util.decodeBase64(petitionNo);
		}
		else {
			 petitionNo = null;
		}
       
        
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		model.addAttribute("petitionNo",petitionNo);
		return BasePath+"petition_view";
	}
	
	@GetMapping("/view-petition-modal")
    public String petitionViewModal(Model model,@RequestParam(value="petitionNo", required = false) String petitionNo) {
		
		if (petitionNo != null && !petitionNo.isEmpty()) {
			// Decrypt the encrypted value
			 petitionNo = Base64Util.decodeBase64(petitionNo);
			 //System.out.println(petitionNo);
		}
		else {
			 petitionNo = null;
		}
       
        
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		model.addAttribute("petitionNo",petitionNo);
		return BasePath+"petition_view_modal_page";
	}
	
	@GetMapping("/search-petition")
    public String petitionSearch(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		//model.addAttribute("petitionNo",petitionNo);
		return BasePath+"petition_update";
	}
	
	@GetMapping("/complaint-list")
    public String complaintList(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"complaint_list";
	}
	
	//report list
	@GetMapping("/report/list")
    public String pendingList(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		return BasePath+"pending_list";
	}
}
