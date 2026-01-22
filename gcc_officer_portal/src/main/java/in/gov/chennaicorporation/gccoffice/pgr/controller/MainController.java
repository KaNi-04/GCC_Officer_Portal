package in.gov.chennaicorporation.gccoffice.pgr.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.pgr.service.PGRCommon;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/pgr")
@Controller("pgrController")
public class MainController {
    private final LoginUserInfo loginUserInfo;
    private final PGRCommon dashboards;
    
    @Autowired
    public MainController(LoginUserInfo loginUserInfo,
    		PGRCommon dashboards) {
        this.loginUserInfo = loginUserInfo;
        this.dashboards = dashboards;
    }

    @GetMapping({"", "/", "/index"})
	public String main(Model model) {
    	String userId = LoginUserInfo.getLoginUserId();
        if (userId != null && !userId.isEmpty()) {
            System.out.println("String UserID: " + userId);
        }
		return "modules/pgr/index";
	}
     
	@GetMapping("/all-list")
	public String allList(Model model) {
		return "modules/pgr/all-list";
	}
	
	@GetMapping("/qa-qc-list")
	public String qaqcList(Model model) {
		return "modules/pgr/qa-qc-list";
	}
	
	@GetMapping("/zone-wise-reports/zone-wise-with-reopen")
	public String zoneWiseWithReopnList(Model model) {
		return "modules/pgr/zone-wise-with-reopen";
	}
	
	@GetMapping("/zone-wise-reports/zone-wise-without-reopen")
	public String zoneWiseWithoutReopnList(Model model) {
		return "modules/pgr/zone-wise-without-reopen";
	}
}
