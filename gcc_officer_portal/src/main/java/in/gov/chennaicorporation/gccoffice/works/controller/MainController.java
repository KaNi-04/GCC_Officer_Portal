package in.gov.chennaicorporation.gccoffice.works.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.service.Base64Util;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;
import in.gov.chennaicorporation.gccoffice.works.service.ERPEstimateService;
import in.gov.chennaicorporation.gccoffice.works.service.EstimateService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/gcc/works")
@Controller("worksController")
public class MainController {
	private String BasePath = "modules/works/";
	
    @Autowired
    private EstimateService estimateService;
    @Autowired
    private ERPEstimateService erpestimateService;

    private Map<String, List<String>> zoneWardMap = new HashMap<>();

    @GetMapping({"", "/", "/index", "/dashboard"})
    public String home(Model model) {
        return BasePath+"index";
    }

    @GetMapping("/erp-works-update") // not in use
    public String CreateRequest(Model model) {
        List<String> zones = new ArrayList<>(zoneWardMap.keySet());

        String LoginUserId = LoginUserInfo.getLoginUserId();
        model.addAttribute("LoginUserId", LoginUserId);
        model.addAttribute("zones", zones);
        return BasePath+"erp-works-update";
    }

    @GetMapping("/nonerp-works-add")
    public String nonErpCreateRequest(Model model) {
        //List<Integer> zones = new ArrayList<>();
        List<String> zones = new ArrayList<>(zoneWardMap.keySet());
        System.out.println("zones: " + zones);

        List<Map<String, Object>> reasons = estimateService.getAllReasons();
        model.addAttribute("reasons", reasons);

        String LoginUserId = LoginUserInfo.getLoginUserId();
        model.addAttribute("LoginUserId", LoginUserId);

        model.addAttribute("zones", zones);
        return BasePath+"non-erp-works-add";
    }

    public MainController() {
    	zoneWardMap.put("00", generateWardList("000", "000"));
        zoneWardMap.put("01", generateWardList("001", "014"));
        zoneWardMap.put("02", generateWardList("015", "021"));
        zoneWardMap.put("03", generateWardList("022", "033"));
        zoneWardMap.put("04", generateWardList("034", "048"));
        zoneWardMap.put("05", generateWardList("049", "063"));
        zoneWardMap.put("06", generateWardList("064", "078"));
        zoneWardMap.put("07", generateWardList("079", "093"));
        zoneWardMap.put("08", generateWardList("094", "108"));
        zoneWardMap.put("09", generateWardList("109", "126"));
        zoneWardMap.put("10", generateWardList("127", "142"));
        zoneWardMap.put("11", generateWardList("143", "155"));
        zoneWardMap.put("12", generateWardList("156", "167"));
        zoneWardMap.put("13", generateWardList("168", "180"));
        zoneWardMap.put("14", generateWardList("181", "191"));
        zoneWardMap.put("15", generateWardList("192", "200"));
    }

    private List<String> generateWardList(String startStr, String endStr) {
        int start = Integer.parseInt(startStr);
        int end = Integer.parseInt(endStr);
        List<String> wards = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            wards.add(String.format("%03d", i));
        }
        return wards;
    }

    @GetMapping("/wards")
    @ResponseBody
    public List<String> getWards(@RequestParam String zone) {
        return zoneWardMap.getOrDefault(zone, Collections.emptyList());
    }
    //ak
    
   
    @GetMapping("/erpworkslist") // To diplay updated ERP works
    public String worksList(Model model) {
        //List<Integer> zones = new ArrayList<>(zoneWardMap.keySet());
        
        String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
	
        //List<Map<String, Object>> workDetails = estimateService.getAllERPWorks(LoginUserId);
        
        //model.addAttribute("workDetails", workDetails);
        //model.addAttribute("zones", zones);
        return BasePath+"erpworkslist";
    }
    
    @GetMapping("/erpworkslist_report") // To diplay updated ERP works
    public String erpworkslist_report(Model model) {
        //List<Integer> zones = new ArrayList<>(zoneWardMap.keySet());
        
        String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
	
        //List<Map<String, Object>> workDetails = estimateService.getAllERPWorks(LoginUserId);
        
        //model.addAttribute("workDetails", workDetails);
        //model.addAttribute("zones", zones);
        return BasePath+"erpworkslist_report";
    }

    @GetMapping("/nonerpworkslist") // To display updated Non-ERP Works
    public String nonerpworksList(Model model) {
        List<String> zones = new ArrayList<>(zoneWardMap.keySet());
        
        String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
        List<Map<String, Object>> workDetails = estimateService.getAllNonERPWorks(LoginUserId);
        
        model.addAttribute("workDetails", workDetails);
        model.addAttribute("zones", zones);
        return BasePath+"nonerpworkslist";
    }
    
    //////////////////////////////////////////////////////////////
    
    @GetMapping("/erpWorksList") // to update ERP Works
    public String erpworksList(Model model) {
        List<String> zones = new ArrayList<>(zoneWardMap.keySet());
        
        String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		//List<Map<String, Object>> workDetails = erpestimateService.getAllWorks(LoginUserId);
		
        //model.addAttribute("workDetails", workDetails);
        model.addAttribute("zones", zones);
        return BasePath+"erp-works-list";
    }
    

    @GetMapping("/view-works-details-modal")
    public String worksViewModal(Model model,@RequestParam(value="estid", required = false) String estid) {
    	System.out.println("EST ID: "+estid);
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		model.addAttribute("estid",estid);
		return BasePath+"works_details_view_modal_page";
	}
    
    @GetMapping("/update-works-details-modal")
    public String worksUpdateModal(Model model,@RequestParam(value="estid", required = false) String estid) {
    	System.out.println("EST ID: "+estid);
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		model.addAttribute("estid",estid);
		return BasePath+"works_details_update_modal_page";
	}
    
    @GetMapping("/user-view-works-details-modal")
    public String userworksViewModal(Model model,@RequestParam(value="estid", required = false) String estid) {
    	System.out.println("EST ID: "+estid);
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		model.addAttribute("estid",estid);
		return BasePath+"user_works_details_view_modal_page";
	}
    
    @GetMapping("/user-update-works-details-modal")
    public String userworksUpdateModal(Model model,@RequestParam(value="estid", required = false) String estid) {
    	System.out.println("EST ID: "+estid);
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		model.addAttribute("estid",estid);
		return BasePath+"user_works_details_update_modal_page";
	}
    
    @GetMapping("/update_works_stage_modal")
    public String updateWorksStageModal(Model model,@RequestParam(value="estid", required = false) String estid) {
    	System.out.println("EST ID: "+estid);
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		model.addAttribute("estid",estid);
		return BasePath+"update_works_stage_modal";
	}
    
    @GetMapping("/edit_stage_modal")
    public String editStageModal(Model model,@RequestParam(value="estid", required = false) String estid,
    		@RequestParam(value="tmid", required = false) String tmid) {
    	System.out.println("EST ID: "+estid);
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		model.addAttribute("estid",estid);
		model.addAttribute("tmid",tmid);
		return BasePath+"edit_stage_modal";
	}
    
    @GetMapping("/update_project_status")
    public String updateProjectStatus(Model model) {
    	List<Map<String, Object>> projectList = estimateService.getProjectList();
        model.addAttribute("projects", projectList);
        
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		model.addAttribute("LoginUserId",LoginUserId);
		
		return BasePath+"project_status_update";
	}
    
}
