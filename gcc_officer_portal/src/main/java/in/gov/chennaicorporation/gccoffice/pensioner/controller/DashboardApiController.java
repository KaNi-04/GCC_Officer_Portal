package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.pensioner.service.DashboardApiService;

@RequestMapping("/gcc/api/pensionerdashboard")
@RestController
public class DashboardApiController {
	@Autowired
    private DashboardApiService dashboardApiService;

	@GetMapping("/dashboard")
	//@ResponseBody
	public List<Map<String, Object>> getDashboardData() {
		
	    return dashboardApiService.fetchCategoryCounts();
	}
	
	  
	  @GetMapping("/getDepartments")
		//@ResponseBody
		public List<Map<String, Object>> getDepartment() {
		    return dashboardApiService.getDepartment();
		}
	  
	  @GetMapping("/getEmpNoByFileName")
	    public List<Map<String, Object>> getEmployeeDetails(@RequestParam("file_id") String FileId) {
			//System.out.println("before fetch");
	        return dashboardApiService.getEmployeeDetails(FileId);
	    }
	  
	  
	  //department
	 
	  @GetMapping("/departmentdashboard") 
		//@ResponseBody
		public List<Map<String, Object>> getDepartmentDashboardData(@RequestParam(required = false) String deptId) {
			//System.out.println("deptid"+ deptId);
		    return dashboardApiService.DepartmenfetchCategoryCounts(deptId);
		}
	  
	  
	


	  @GetMapping("/stageWiseCounts")
	  public ResponseEntity<List<Map<String, Object>>> getDepartmentStageWiseCounts(
	          @RequestParam(required = false) String deptId,
	          @RequestParam(required = false) String startDate,
	          @RequestParam(required = false) String endDate) {
		  
		  if(startDate.isEmpty())
		  {
			  startDate=null;
		  }
		  if(endDate.isEmpty())
		  {
			  endDate=null;
		  }
	      List<Map<String, Object>> stageWiseCounts = dashboardApiService.getStageWiseCounts(deptId, startDate, endDate);
	    
	      return ResponseEntity.ok(stageWiseCounts);
	  }
	  
	  @GetMapping("/categoryCounts")
	  public List<Map<String, Object>> getDepartmenCategoryCounts(
	          @RequestParam(required = false) String deptId,
	          @RequestParam(required = false) String startDate,
	          @RequestParam(required = false) String endDate) {
		  
		  if(startDate.isEmpty())
		  {
			  startDate=null;
		  }
		  if(endDate.isEmpty())
		  {
			  endDate=null;
		  }
		  
	      return dashboardApiService.fetchchartCategoryCounts(deptId, startDate, endDate);
	  }

		@GetMapping("/dashboardsearch")
		//@ResponseBody
		public List<Map<String, Object>> fetchDeparmentDashboardData(
		        @RequestParam(required = false) String startdate,
		        @RequestParam(required = false) String enddate,@RequestParam(required = false) String deptId) {
			if(deptId.isEmpty())
			  {
				  deptId=null;
			  }
			  
		    //System.out.println("Endpoint hit with startdate: " + startdate + ", enddate: " + enddate);
		    try {
		        return dashboardApiService.fetchCategorysearchCounts(startdate, enddate,deptId);
		    } catch (Exception e) {
		        e.printStackTrace(); // Print fetchCategoryCounts stack trace to the logs
		        throw e; // Rethrow the exception to propagate the 500 error
		    }
		}
	
//pension
		
		  @GetMapping("/pensiondashboard")
			//@ResponseBody
			public List<Map<String, Object>> getPensionDashboardData(@RequestParam(required = false) String deptId) {
				//System.out.println("deptid"+ deptId);
			    return dashboardApiService.DepartmenfetchCategoryCounts(deptId);
		  }
		
		  
		  @GetMapping("/fetchEmpDetails")
		    public List<Map<String, Object>> getEmpDetails(@RequestParam(required = false) String Empcode) {
			  //System.out.println("checking");
			  //System.out.println(Empcode);
			  
		        return dashboardApiService.getEmpDetails(Empcode);
		    }
		    
		    @GetMapping("/fetchPensionEmpDetails")
		    public List<Map<String, Object>> getPensionEmpDetails(@RequestParam(required = false) String Empcode) {
			  //System.out.println("checking");
			  //System.out.println(Empcode);
			  
		        return dashboardApiService.getPensionEmpDetails(Empcode);
		    }
		    
		    
		    @GetMapping("/fetchTempDetails")
		    public List<Map<String, Object>> getTempEmpDetails(@RequestParam(required = false) String tempId) {
			 // System.out.println("checking");
			 // System.out.println(tempId);
			  
		        return dashboardApiService.getTempEmpDetails(tempId);
		    }
		    
		
			

}
