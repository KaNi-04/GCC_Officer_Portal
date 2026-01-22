package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import in.gov.chennaicorporation.gccoffice.pensioner.service.CompletedService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.DepartmentForwardService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.DepartmentReceivedService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.DeptAuditService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.NewEntryService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.PensionAuditService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.PensionPartiallyCompService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.PensionReceivedService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.ReceivedDeptProvisionalService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.ReportApiService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.ReturnFromAuditService;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@Controller
@RequestMapping("/gcc/pensioner")
public class PensionerMainController {
		
	
	@Autowired
	private NewEntryService newEntryService; 
	
	@Autowired
	private DeptAuditService deptAuditService;
	
	@Autowired
	private ReturnFromAuditService returnFromAuditService;
	
	@Autowired
	private PensionAuditService pensionAuditService;
	
	@Autowired
	private DepartmentForwardService departmentForwardService;
	
	@Autowired
	private PensionReceivedService pensionReceivedService;
	
	@Autowired
	private DepartmentReceivedService departmentReceivedService;
	
	@Autowired
	private ReceivedDeptProvisionalService receivedDeptProvisionalService;
	
	@Autowired
	private CompletedService completedService;
	
	@Autowired
	private PensionPartiallyCompService pensionPartiallyCompService;
	
	@Autowired
	private ReportApiService reportApiService;
	
	
		//////////////////////////////Pensioner pages////////////////////////////////
			
		//yogi
		@GetMapping("/audit/retirement")
		public String pensionerauditretirment(Model model) {
		
		int file_cat= newEntryService.getRetirementFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
	    
	    String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		Integer deptId = newEntryService.getloginuserDeptId(userId);
		String deptname=newEntryService.getDeptNameById(deptId);
		model.addAttribute("LoginUserId",LoginUserId);
		String UserRole = LoginUserInfo.getUserRole();
	    System.out.println("deptId:"+ deptId);	
	    model.addAttribute("deptId", deptId);
	    model.addAttribute("deptname", deptname);
	    model.addAttribute("userId", userId);
	    
	    List<Map<String, Object>>auditlist= pensionAuditService.fetchDetailsForAudit(file_cat);
	    	    
	    System.out.println("auditlist===="+auditlist);
	    model.addAttribute("auditlist", auditlist);
		
		return "modules/pensioner/pensioner/audit/retirement";
		}
		
		
		
		@GetMapping("/audit/familypension")
		public String pensionerfamilypension(Model model) {
			
			int file_cat= newEntryService.getFamilyPensionFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
			
			String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
			
			  System.out.println("deptId:"+ deptId);	
			    model.addAttribute("deptId", deptId);
			    model.addAttribute("deptname", deptname);
			    model.addAttribute("userId", userId);   
			    List<Map<String, Object>>auditlist= pensionAuditService.fetchDetailsForAudit(file_cat);
			    	    
			    System.out.println("auditlist===="+auditlist);
			    model.addAttribute("auditlist", auditlist);
			
			return "modules/pensioner/pensioner/audit/familypension";
		}
		
		
		@GetMapping("/audit/pendency")
		public String pensionerpendency(Model model) {
			
			int file_cat= newEntryService.getPendencyFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
			
			String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			
			
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			
			
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
			
			 System.out.println("deptId:"+ deptId);	
			    model.addAttribute("deptId", deptId);
			    model.addAttribute("deptname", deptname);
			    model.addAttribute("userId", userId);   
			    List<Map<String, Object>>auditlist= pensionAuditService.fetchDetailsForAudit(file_cat);
			    	    
			    System.out.println("auditlist===="+auditlist);
			    model.addAttribute("auditlist", auditlist);
			
			return "modules/pensioner/pensioner/audit/pendency";
		}
		
		
		///////////////////COMPLETED LIST PAGES//////////////
		
		@GetMapping("/completed/retirement")
		public String pensionerretirement(Model model) {
			int file_cat= newEntryService.getRetirementFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>provisionallist= completedService.fetchDetailsCompletedRetirementPensionside(file_cat);
		    	    
		    System.out.println("provisionallist===="+provisionallist);
		    model.addAttribute("provisionallist", provisionallist);
		
		return "modules/pensioner/pensioner/completed/retirement";
		}
		
		
		@GetMapping("/completed/familypension")
		public String pensionercompleted(Model model) {
			int file_cat= newEntryService.getFamilyPensionFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>familypensioncompletedlist= completedService.fetchDetailsCompletedFamilyPensionPensionSide(file_cat);
		    	    
		    System.out.println("provisionallist===="+familypensioncompletedlist);
		    model.addAttribute("provisionallist", familypensioncompletedlist);
		
		return "modules/pensioner/pensioner/completed/familypension";
		}
		
		
		
		@GetMapping("/completed/pendency")
		public String pensionercompletedpendency(Model model) {
			int file_cat= newEntryService.getPendencyFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>pendencycompletedlist= completedService.fetchDetailsCompletedPendencyPensionSide(file_cat);
		    	    
		    System.out.println("provisionallist===="+pendencycompletedlist);
		    model.addAttribute("provisionallist", pendencycompletedlist);
		
		return "modules/pensioner/pensioner/completed/pendency";
		}
		
		
		@GetMapping("/completed/gis")
		public String pensionercompletedgis(Model model) {
			int file_cat= newEntryService.getGISFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>gislist= completedService.fetchDetailsForrecivedGiscompletedPensionside(file_cat);
		    	    
		    System.out.println("auditlist===="+gislist);
		    model.addAttribute("auditlist", gislist);
			
			return "modules/pensioner/pensioner/completed/gis";
		}
		
		@GetMapping("/completed/provisionalretirement")
		public String pensionercompletedretirement(Model model) {
			int file_cat= newEntryService.getPRFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>provisionallist= completedService.fetchDetailsProvisionalDepartmentPensionside(file_cat);
		    	    
		    System.out.println("provisionallist===="+provisionallist);
		    model.addAttribute("provisionallist", provisionallist);
			
		
		return "modules/pensioner/pensioner/completed/provisionalretirement";
		}
		
		
		
		@GetMapping("/completed/contributorypensionscheme")
		public String pensionercompletedcontributory( 
		        Model model) {
			int file_cat= newEntryService.getCPSFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>completedCPSList= completedService.getCPSListPensionside(file_cat);
		    	    
		    System.out.println("provisionallist===="+completedCPSList);
		    model.addAttribute("provisionallist", completedCPSList);
		    // Add the list to the model
		    model.addAttribute("completedCPSList", completedCPSList);
		
		return "modules/pensioner/pensioner/completed/contributorypensionscheme";
		}
		

		
		
		//Ajith 
		
		@GetMapping("/forward/retirement")
		public String pensioneretirement(Model model) {
		
		int file_cat= newEntryService.getRetirementFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
	    
	    String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		Integer deptId = newEntryService.getloginuserDeptId(userId);
		String deptname=newEntryService.getDeptNameById(deptId);
		model.addAttribute("LoginUserId",LoginUserId);
		String UserRole = LoginUserInfo.getUserRole();
	    System.out.println("deptId:"+ deptId);	
	    model.addAttribute("deptId", deptId);
	    model.addAttribute("deptname", deptname);
	    model.addAttribute("userId", userId);
	    
	    List<Map<String, Object>>forwardlist= departmentForwardService.fetchDepatmentForwardEmpfiles(file_cat);
	    	    
	    System.out.println("forwardlist===="+forwardlist);
	    model.addAttribute("forwardlist", forwardlist);
		
		return "modules/pensioner/pensioner/forward/retirement";
		}
		
		@GetMapping("/forward/familypension")
		public String pensionfamily(Model model) {
			int file_cat= newEntryService.getFamilyPensionFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
			
			String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
			
			 System.out.println("deptId:"+ deptId);	
			    model.addAttribute("deptId", deptId);
			    model.addAttribute("deptname", deptname);
			    model.addAttribute("userId", userId);
			    
			    List<Map<String, Object>>forwardlist= departmentForwardService.fetchDepatmentForwardEmpfiles(file_cat);
			    	    
			    System.out.println("forwardlist===="+forwardlist);
			    model.addAttribute("forwardlist", forwardlist);
		
		return "modules/pensioner/pensioner/forward/familypension";
		}
		
		
		@GetMapping("/forward/pendency")
		public String pensionerforward(Model model) {
			int file_cat= newEntryService.getPendencyFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
			String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
			
			 System.out.println("deptId:"+ deptId);	
			    model.addAttribute("deptId", deptId);
			    model.addAttribute("deptname", deptname);
			    model.addAttribute("userId", userId);
			    
			    List<Map<String, Object>>forwardlist= departmentForwardService.fetchDepatmentForwardEmpfiles(file_cat);
			    	    
			    System.out.println("forwardlist===="+forwardlist);
			    model.addAttribute("forwardlist", forwardlist);
		
		return "modules/pensioner/pensioner/forward/pendency";
		}
		
		
		
		
		//dizho
		
		@GetMapping("/received/retirement")
		public String pensionerreceivedretirement(Model model) {
			int file_cat= newEntryService.getRetirementFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>forwardlist= departmentReceivedService.fetchPensionReceivedEmpfiles(file_cat);
		    	    
		    System.out.println("forwardlist===="+forwardlist);
		    model.addAttribute("forwardlist", forwardlist);
		
		return "modules/pensioner/pensioner/received/retirement";
		}
		
		@GetMapping("/received/familypension")
		public String pensionerreceivedfamilypension(Model model) {
			int file_cat= newEntryService.getFamilyPensionFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>forwardlist= departmentReceivedService.fetchPensionReceivedEmpfiles(file_cat);
		    	    
		    System.out.println("forwardlist===="+forwardlist);
		    model.addAttribute("forwardlist", forwardlist);
		
		return "modules/pensioner/pensioner/received/familypension";
		}
		
		
		@GetMapping("/received/pendency")
		public String pensionerreceived(Model model) {
			int file_cat= newEntryService.getPendencyFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>forwardlist= departmentReceivedService.fetchPensionReceivedEmpfiles(file_cat);
		    	    
		    System.out.println("forwardlist===="+forwardlist);
		    model.addAttribute("forwardlist", forwardlist);
		
		return "modules/pensioner/pensioner/received/pendency";
		}
		
		@GetMapping("/received/gis")
		public String pensionerreceivedgis(Model model) {
		int file_cat= newEntryService.getGISFileCategoryId();	    	
		model.addAttribute("filecategory", file_cat);
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		Integer deptId = newEntryService.getloginuserDeptId(userId);
		String deptname=newEntryService.getDeptNameById(deptId);
		model.addAttribute("LoginUserId",LoginUserId);
		String UserRole = LoginUserInfo.getUserRole();
		System.out.println("deptId:"+ deptId);	
		model.addAttribute("deptId", deptId);
		model.addAttribute("deptname", deptname);
		model.addAttribute("userId", userId);
		
		List<Map<String, Object>>gislist= pensionAuditService.fetchDetailsForrecivedGis(file_cat);
		
		System.out.println("auditlist===="+gislist);
		model.addAttribute("auditlist", gislist);
		
		return "modules/pensioner/pensioner/received/gis";
		}
		
		
		
		
		@GetMapping("/received/provisionalretirement")
		public String pensionerprovisionalretirement(Model model) {
		
		int file_cat= newEntryService.getPRFileCategoryId();	    	
		model.addAttribute("filecategory", file_cat);
		
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		Integer deptId = newEntryService.getloginuserDeptId(userId);
		String deptname=newEntryService.getDeptNameById(deptId);
		model.addAttribute("LoginUserId",LoginUserId);
		String UserRole = LoginUserInfo.getUserRole();
		System.out.println("deptId:"+ deptId);	
		model.addAttribute("deptId", deptId);
		model.addAttribute("deptname", deptname);
		model.addAttribute("userId", userId);
		
		List<Map<String, Object>>provisionallist= receivedDeptProvisionalService.fetchDetailsProvisionalPension(file_cat);
		
		System.out.println("provisionallist===="+provisionallist);
		model.addAttribute("provisionallist", provisionallist);
		
		return "modules/pensioner/pensioner/received/provisionalretirement";
		}
		
		
		//sanjay
		
		@GetMapping("/partial/pendency")
		public String pensionerpartial(Model model) {
			int file_cat= newEntryService.getPendencyFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>auditlist= pensionPartiallyCompService.fetchPensionPartialEmpfiles(file_cat);
		    	    
		    System.out.println("auditlist===="+auditlist);
		    model.addAttribute("auditlist", auditlist);
		
		return "modules/pensioner/pensioner/partial/pendency";
		}
		
		@GetMapping("/partial/retirement")
		public String pensionerpartialretirement(Model model) {
			int file_cat= newEntryService.getRetirementFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>auditlist= pensionPartiallyCompService.fetchPensionPartialEmpfiles(file_cat);
		    	    
		    System.out.println("auditlist===="+auditlist);
		    model.addAttribute("auditlist", auditlist);
		
		
		
		
		return "modules/pensioner/pensioner/partial/retirement";
		}
		
		@GetMapping("/partial/familypension")
		public String pensionerpartialfamily(Model model) {
			int file_cat= newEntryService.getFamilyPensionFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			String deptname=newEntryService.getDeptNameById(deptId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
		    System.out.println("deptId:"+ deptId);	
		    model.addAttribute("deptId", deptId);
		    model.addAttribute("deptname", deptname);
		    model.addAttribute("userId", userId);
		    
		    List<Map<String, Object>>auditlist= pensionPartiallyCompService.fetchPensionPartialEmpfiles(file_cat);
		    	    
		    System.out.println("auditlist===="+auditlist);
		    model.addAttribute("auditlist", auditlist);
		
		return "modules/pensioner/pensioner/partial/familypension";
		}
		
		@GetMapping("/reports")
		public String report(Model model) {
			String LoginUserId =  LoginUserInfo.getLoginUserId();
			int userId = Integer.parseInt(LoginUserId);
			model.addAttribute("LoginUserId",LoginUserId);
			String UserRole = LoginUserInfo.getUserRole();
			Integer deptId = newEntryService.getloginuserDeptId(userId);
			model.addAttribute("deptId", deptId);
			
			return "modules/pensioner/pensioner/reports";
		}
		
		
		
		@GetMapping("/dashboard")
		public String pensionerdashboardpage(Model model) {
		String LoginUserId =  LoginUserInfo.getLoginUserId();
		int userId = Integer.parseInt(LoginUserId);
		model.addAttribute("LoginUserId",LoginUserId);
		String UserRole = LoginUserInfo.getUserRole();
		
		return "modules/pensioner/pensioner/dashboard";
		}

}
