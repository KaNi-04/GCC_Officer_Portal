package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.FollowUpService;
import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.InprogresscallService;
import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.PublicFollowUpService;
import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcAdminSettingService;
import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcAgentsService;
import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcCallsService;
import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcService;
import in.gov.chennaicorporation.gccoffice.entity.AppUserEntity;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;


@RequestMapping("/gcc/callcenterqaqc") 
@Controller("callCenterQaQcMainController")
public class MainController {
	
	@Autowired
    private QaqcService qaqcService;
	
	@Autowired
	private QaqcAgentsService agentsService;
	
	@Autowired
	private QaqcAdminSettingService adminService;
	
	@Autowired
	private InprogresscallService inprogresscallService;
	
	@Autowired	
	private QaqcCallsService qaqcCallsService;
	
	@Autowired
	private FollowUpService followUpService;
	
	@Autowired
	private PublicFollowUpService publicService;

	
	@GetMapping("/admin/qaqcgrievancedashboard")
	public String viewqaqcdashboard(Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		
		List<Map<String,Object>> previousday=agentsService.getPreviousDayDashboardCounts();		
		model.addAttribute("previousday", previousday);
		//System.out.println(previousday);
		
		List<Map<String,Object>> agentcount=agentsService.getAgentPreviousDayCount();	
		model.addAttribute("agentcount", agentcount);
		
		return "modules/callcenterqaqc/qaqcgrievancedashboard";
	}
	
	@GetMapping("/admin/qaqcassigntaskagents")
	public String viewAgentAssign(Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
				
		return "modules/callcenterqaqc/qaqctaskagentassign";
	}
		
	
	@GetMapping("/admin/qaqcfollowuplist")
	public String viewAdminFollowuplist(Model model) {
				
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId); 
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
				
		return "modules/callcenterqaqc/qaqcfollowuplist";
	}
	
	
	@GetMapping("/admin/unattendedcomplaints")
	public String viewAdminUnattendedClose(Model model) {
				
		String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);
	    
	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("userId", userId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
				
		return "modules/callcenterqaqc/unattendedclosing";
	}
	
	
	
	
	
	@GetMapping("/agent/agentgrievancedashboard")
	public String viewAgentGrievenceDashboardpage(
			  @RequestParam(required = false) String startDate,
			    @RequestParam(required = false) String endDate,
	   	    Model model
	) {
		String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);
	    
	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
	    
	    List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
	    
	    
	    
	 // Default dates if not provided
	    if (startDate == null || endDate == null) {
	        LocalDate today = LocalDate.now();
	        startDate = today.toString();
	        endDate = today.toString();
	    }

	    List<Map<String, Object>> details = agentsService.getAgentDashboard(userId, startDate, endDate);
	    model.addAttribute("details", details);

	    List<Map<String, Object>> details2 = agentsService.getAgentPerformanceData(userId, startDate, endDate);
	    model.addAttribute("details2", details2);

	    List<Map<String, Object>> tasks = agentsService.getAgentTaskDetails(userId, startDate, endDate);
	    model.addAttribute("tasks", tasks);
	    //System.out.println("tasks: "+tasks);
	    return "modules/callcenterqaqc/agentgrievancedashboard";
	}
	
	
	
	@PostMapping("/agent/agentgrievancedashboard")
	public String viewAgentGrievenceDashboard(
	    @RequestParam(required = false) String startDate,
	    @RequestParam(required = false) String endDate,
	    Model model
	) {
	    String loginUserId = LoginUserInfo.getLoginUserId();
	    int userId = Integer.parseInt(loginUserId);

	    // Default dates if not provided
	    if (startDate == null || endDate == null) {
	        LocalDate today = LocalDate.now();
	        startDate = today.toString();
	        endDate = today.toString();
	    }

	    model.addAttribute("LoginUserId", loginUserId);
	    model.addAttribute("agent_name", LoginUserInfo.getCurrentUserDetails().getUsername());
	    model.addAttribute("UserRole", LoginUserInfo.getUserRole());
	    
	    List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
	    
//	    System.out.println("startdate: "+startDate );
//	    System.out.println("enddate:" + endDate);

	    List<Map<String, Object>> details = agentsService.getAgentDashboard(userId, startDate, endDate);
	    model.addAttribute("details", details);

	    List<Map<String, Object>> details2 = agentsService.getAgentPerformanceData(userId, startDate, endDate);
	    model.addAttribute("details2", details2);

	    List<Map<String, Object>> tasks = agentsService.getAgentTaskDetails(userId, startDate, endDate);
	    model.addAttribute("tasks", tasks);

	    return "modules/callcenterqaqc/agentgrievancedashboard";
	    
	}
	
    @GetMapping("/agent/qaqccalls")
	public String viewAgentQaqcCalls(	    @RequestParam(required = false) String startDate,
		    @RequestParam(required = false) String endDate,Model model) {
		
		String LoginUserId = LoginUserInfo.getLoginUserId();		
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("userId",userId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		
		List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
		
		// Default dates if not provided
	    if (startDate == null || endDate == null) {
	        LocalDate today = LocalDate.now();
	        startDate = today.toString();
	        endDate = today.toString();
	    }
		
	    List<Map<String, Object>> details = agentsService.getAgentDashboard(userId, startDate, endDate);
	    model.addAttribute("details", details);
        
        List<Map<String, Object>> qaqcCallslists=qaqcCallsService.getClosedComplaints(userId);        
        model.addAttribute("qaqcCallsLists",qaqcCallslists);
        
        List<Map<String, Object>> qaqcCallsAction=qaqcCallsService.getCallCategories();       
        model.addAttribute("qaqcCallsActioin",qaqcCallsAction);
        
        List<Map<String, Object>> qaqcSubmitDetails=qaqcCallsService.getSubmitDetails(userId);       
        model.addAttribute("qaqcSubmitDetails",qaqcSubmitDetails);
				
		return "modules/callcenterqaqc/agentqaqccalls";
	}
	
	
	
	@GetMapping("agent/qaqcinprogresscall")
	public String viewqaqcin_progress_call(	    @RequestParam(required = false) String startDate,
		    @RequestParam(required = false) String endDate,Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("userId",userId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		// Default dates if not provided
	    if (startDate == null || endDate == null) {
	        LocalDate today = LocalDate.now();
	        startDate = today.toString();
	        endDate = today.toString();
	    }
		
	    List<Map<String, Object>> details = agentsService.getAgentDashboard(userId, startDate, endDate);
	    model.addAttribute("details", details);
		
		List<Map<String, Object>> inprogresslists=inprogresscallService.getInProgressCalls(userId);
        model.addAttribute("inprogresslists", inprogresslists);
        
        List<Map<String, Object>> qaqcactionlists=inprogresscallService.getActionsCatergory();
		model.addAttribute("qaqcactionlists", qaqcactionlists);
		
		List<Map<String, Object>> inSubmitDetails=inprogresscallService.getInprogressSubmitDetails(userId);       
        model.addAttribute("inSubmitDetails",inSubmitDetails);
		
		return "modules/callcenterqaqc/agentqaqcinprogresscalls";	
	}
	
	@GetMapping("/agent/qaqcfollowupcalls")
	public String viewAgentQaqcFollowupCalls(@RequestParam(required = false) String startDate,
		    @RequestParam(required = false) String endDate,Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("userId",userId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		// Default dates if not provided
	    if (startDate == null || endDate == null) {
	        LocalDate today = LocalDate.now();
	        startDate = today.toString();
	        endDate = today.toString();
	    }
		
		
	    List<Map<String, Object>> details = agentsService.getAgentDashboard(userId, startDate, endDate);
	    model.addAttribute("details", details);
		
		List<Map<String, Object>> qaqcfollowuplists=followUpService.getFollowUpCalls(userId);
		model.addAttribute("qaqcfollowuplists",qaqcfollowuplists);
		
		List<Map<String, Object>> qaqccategorylists=followUpService.getFollowUpCatergory();
        
        model.addAttribute("qaqccategorylists",qaqccategorylists);
        
        List<Map<String, Object>> fcSubmitDetails=followUpService.getFollowupSubmitDetails(userId);       
        model.addAttribute("fcSubmitDetails",fcSubmitDetails);
				
		return "modules/callcenterqaqc/agentqaqcfollowupcalls";
	}
	
	
	@GetMapping("/agent/qaqcpublicfollowupcalls")
	public String viewAgentQaqcPublicFollowupCalls(@RequestParam(required = false) String startDate,
		    @RequestParam(required = false) String endDate,Model model) {
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("userId",userId);
		model.addAttribute("LoginUserId",LoginUserId);
		
		String agent_name=LoginUserInfo.getCurrentUserDetails().getUsername();
		model.addAttribute("agent_name", agent_name);
		List<Map<String, Object>> agent=agentsService.getAgentName(userId);
	    model.addAttribute("agent", agent);
		
		String UserRole = LoginUserInfo.getUserRole();
		model.addAttribute("UserRole", UserRole);
		// Default dates if not provided
	    if (startDate == null || endDate == null) {
	        LocalDate today = LocalDate.now();
	        startDate = today.toString();
	        endDate = today.toString();
	    }
		
		
	    List<Map<String, Object>> details = agentsService.getAgentDashboard(userId, startDate, endDate);
	    model.addAttribute("details", details);
		
		List<Map<String, Object>> qaqcfollowuplists=publicService.getFollowUpCalls(userId);
		model.addAttribute("qaqcfollowuplists",qaqcfollowuplists);
		
		List<Map<String, Object>> qaqccategorylists=publicService.getFollowUpCatergory();
        
        model.addAttribute("qaqccategorylists",qaqccategorylists);
        
        List<Map<String, Object>> fcSubmitDetails=publicService.getFollowupSubmitDetails(userId);       
        model.addAttribute("fcSubmitDetails",fcSubmitDetails);
				
		return "modules/callcenterqaqc/agentqaqcpublicfollowupcalls";
	}
	
		
}
