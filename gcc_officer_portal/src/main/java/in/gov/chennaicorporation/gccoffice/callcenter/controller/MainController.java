	package in.gov.chennaicorporation.gccoffice.callcenter.controller;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.callcenter.service.AddcategoryServices;
import in.gov.chennaicorporation.gccoffice.callcenter.service.AgentsService;
import in.gov.chennaicorporation.gccoffice.callcenter.service.AttendanceService;
import in.gov.chennaicorporation.gccoffice.callcenter.service.AgentAssignService;
import in.gov.chennaicorporation.gccoffice.callcenter.service.CampaignService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;


@RequestMapping("/gcc/callcenter")
@Controller("callCenterCampaignMainController")
public class MainController {
	
	
	
	@Autowired
	private AgentAssignService campaignmaster;
	
	@Autowired
    private CampaignService campaignService;

	@Autowired
	private AddcategoryServices addCategoryServices;
	
	@Autowired
	private AgentsService  agentsService;
	
	@Autowired
	private AttendanceService attendanceSerivce;
	
	private AppConfig appConfig;
	private RestTemplate restTemplate;
	
	@Autowired
	public MainController(AgentAssignService campaignmaster,AgentsService  agentsService,CampaignService campaignService,AddcategoryServices addCategoryServices,RestTemplate restTemplate, AppConfig appConfig)
	{		
		this.campaignmaster=campaignmaster;
		this.campaignService=campaignService;
		this.addCategoryServices=addCategoryServices;
		this.agentsService=agentsService;
		this.restTemplate = restTemplate;
        this.appConfig = appConfig;
		
	}
	

	@GetMapping("/admin/addcategory")

	public String addcategory(Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);

		List<Map<String, Object>> categories = addCategoryServices.getAllCategories();

		//System.out.println(categories);

		model.addAttribute("categories", categories);

		return "modules/callcenter/addcategory";

	}
	
	@GetMapping("/admin/createcampaign")

    public String createcampaign(Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);

		/*
		 * List<Map<String, Object>> campaigns = campaignService.getCampaignDetails();
		 * 
		 * 
		 * System.out.println("campaignlist:"+campaigns);
		 * 
		 * model.addAttribute("campaigns", campaigns);
		 */

        return "modules/callcenter/createcampaign"; // Name of the Thymeleaf template (e.g., campaignList.html)

    }
	
	@GetMapping("/admin/campaignlist")
	public String viewCampaignList(Model model)
	{	
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		
		List<Map<String, Object>> campaigns = campaignService.getDetailsForStart();
        	
        model.addAttribute("campaigns", campaigns);
        
        List<Map<String, Object>> activeCampaigns =campaignService.getActiveCampaigns();
	    model.addAttribute("activeCampaigns", activeCampaigns);
        
		return "modules/callcenter/campaignlist";
	}


	@GetMapping("/agentscampaign")
    public String targetPage(Model model)
	{   
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		
        List<Map<String, Object>> agentsCampaigns=agentsService.getAgentsALLCampaigns(userId);
        
        List<Map<String, Object>> agentsCampaignsList=agentsService.getAgentsALLCampaignsList(userId);
        
        List<Map<String, Object>> agentsCampaignsList1=agentsService.getAgentsALLCampaignsList1(userId);
        
        //List<Map<String, Object>> CampActions=agentsService.getCallCategories();
        //model.addAttribute("CampActions", CampActions);
        
        model.addAttribute("agentsCampaignsList", agentsCampaignsList);
        model.addAttribute("agentsCampaigns", agentsCampaigns);
        model.addAttribute("agentsCampaignsList1", agentsCampaignsList1);
        //System.out.println("Agents Campaigns List: " + agentsCampaignsList);
        //System.out.println("Agents Campaigns : " + agentsCampaigns);
        
        return "modules/callcenter/agentscampaign";
    }
	
	
	@GetMapping("/admin/campaignreport")
	public String campaignreport(Model model)
	{		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		
		return "modules/callcenter/campaignreport";
	}
	
	@GetMapping("/attendancereport")
	public String AttendanceList(
	        @RequestParam(required = false) String fromDate,
	        @RequestParam(required = false) String toDate,
	        Model model) {
	    List<Map<String, Object>> attendanceData = Collections.emptyList();

	    if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
	        String apiUrl = appConfig.attendanceReport + "/callcenter/api/attendance/getattendancereport?fromDate=" + fromDate + "&toDate=" + toDate;

	        try {
	            ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
	            String responseBody = responseEntity.getBody();

	            if (responseBody != null && !responseBody.isEmpty()) {
	                ObjectMapper objectMapper = new ObjectMapper();
	                if (responseBody.trim().startsWith("{")) {
	                    // Single object response
	                    Map<String, Object> singleData = objectMapper.readValue(responseBody, new TypeReference<>() {});
	                    attendanceData = List.of(singleData);
	                } else {
	                    // Array response
	                    attendanceData = objectMapper.readValue(responseBody, new TypeReference<>() {});
	                }
	            }
	        } catch (Exception e) {
	            System.err.println("Error fetching attendance data: " + e.getMessage());
	        }
	    }

	    model.addAttribute("attendanceData", attendanceData);
	    model.addAttribute("fromDate", fromDate);
	    model.addAttribute("toDate",  toDate);
	    model.addAttribute("LoginUserId", LoginUserInfo.getLoginUserId());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());

	    return "modules/callcenter/attendancereport";
	}
	

}
