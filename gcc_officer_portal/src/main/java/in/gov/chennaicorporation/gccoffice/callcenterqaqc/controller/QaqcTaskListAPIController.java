package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcAdminSettingService;
import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcTaskListService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@RequestMapping("gcc/api/callcenterqaqc/qaqctasklist")
@RestController
public class QaqcTaskListAPIController {

	
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	
	
	@Autowired  
    public QaqcTaskListAPIController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
	
	@Autowired
	private QaqcTaskListService tasklist;
	
    @GetMapping("/overalltaskcount")
    public List<Map<String, Object>> getComplaintAndCallStatusCounts(
            @RequestParam(required = false) String startDate, 
            @RequestParam(required = false) String endDate) {
        
    	List<Map<String, Object>> counts = tasklist.getComplaintAndCallStatusCounts(startDate, endDate);
    	//System.out.println("overallcounts:" + counts);
        return counts;
    }
    
    
    
    @GetMapping("/taskdatacount")
    public List<Map<String, Object>> getTaskwiseData(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {
    	List<Map<String, Object>> taskdata = tasklist.getTaskwiseData(startDate, endDate);
    	//System.out.println("taskdata "+ taskdata);
        return taskdata;
    }
    
    
    
    @GetMapping("/agentdatacount")
    public List<Map<String, Object>> getAgentwiseData(@RequestParam String qaqc_id) 
    {
    	 List<Map<String, Object>> agentwisedata = tasklist.getAgentwiseData(qaqc_id);
       //System.out.println("agentdata "+agentwisedata);
    	 return agentwisedata;
    }
	
	
}
