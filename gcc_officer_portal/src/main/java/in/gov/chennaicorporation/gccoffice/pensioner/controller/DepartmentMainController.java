package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.gov.chennaicorporation.gccoffice.pensioner.service.CompletedService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.DepartmentForwardService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.DepartmentReceivedService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.DeptAuditService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.DeptPartiallyCompService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.NewEntryService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.PensionAuditService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.PensionReceivedService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.ReceivedDeptProvisionalService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.ReturnFromAuditService;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/department")
@Controller("gccpensioner")
public class DepartmentMainController {
	
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
	public DeptPartiallyCompService deptPartiallyCompleteService;
	
	
	
	//////////////////////department pages/////////////////////////////////
	
	//yogi
	@GetMapping("/newentry/retirement")
	public String newentryretirement( Model model) {
		
		
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
	    
		return "modules/pensioner/department/newentry/retirement";
	}
	

	@GetMapping("/newentry/familypension")
	public String newentryfamilypension(Model model) {
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
	    
	    int file_cat= newEntryService.getFamilyPensionFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
		
		return "modules/pensioner/department/newentry/familypension";
	}
	
	
	@GetMapping("/newentry/pendency")
	public String newentrypendency(Model model) {
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
	    
	    int file_cat= newEntryService.getPendencyFileCategoryId();    	
	    model.addAttribute("filecategory", file_cat);
		
		return "modules/pensioner/department/newentry/pendency";
	}
	
	
	
	@GetMapping("/newentry/gis")
	public String newentrygis(Model model) {
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
	    
	    int file_cat= newEntryService.getGISFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
		
		return "modules/pensioner/department/newentry/gis";
	}
	
	
	@GetMapping("/newentry/provisionalretirement")
	public String newentryprovisional(Model model) {
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
	    
	    int file_cat= newEntryService.getPRFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
		
		return "modules/pensioner/department/newentry/provisionalretirement";
	}
	
	
	@GetMapping("/newentry/contributorypensionscheme")
	public String newentrycontributory(Model model) {
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
	    
	    int file_cat= newEntryService.getCPSFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
		
		return "modules/pensioner/department/newentry/contributorypensionscheme";
	}
	
	
   //sanjay//////////////AUDIT LIST PAGES//////////////////
	
	@GetMapping("/audit/retirement")
	public String auditretirement(Model model) {
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
	    int file_cat= newEntryService.getRetirementFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
	    
	    List<Map<String, Object>>auditlist=deptAuditService.fetchEmployeeDetailsByTable(deptId,file_cat);
	    System.out.println("auditlist===="+auditlist);
	    model.addAttribute("auditlist", auditlist);
		
		return "modules/pensioner/department/audit/retirement";
	}
	

	@GetMapping("/audit/familypension")
	public String auditfamilypension(Model model) {
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
	    int file_cat= newEntryService.getFamilyPensionFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
	    
	    List<Map<String, Object>>auditlist=deptAuditService.fetchEmployeeDetailsByTable(deptId,file_cat);
	    System.out.println("auditlist===="+auditlist);
	    model.addAttribute("auditlist", auditlist);
		
		
		return "modules/pensioner/department/audit/familypension";
	}
	
	
	@GetMapping("/audit/pendency")
	public String auditpendency(Model model) {
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
	    int file_cat= newEntryService.getPendencyFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
	    
	    List<Map<String, Object>>auditlist=deptAuditService.fetchEmployeeDetailsByTable(deptId,file_cat);
	    System.out.println("auditlist===="+auditlist);
	    model.addAttribute("auditlist", auditlist);
		
		return "modules/pensioner/department/audit/pendency";
	}
	
	
	///////////////////PARTIAL LIST PAGES//////////////////////
	
	@GetMapping("/partial/retirement")
	public String partialretirement(Model model) {
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
	    int file_cat= newEntryService.getRetirementFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
	    
	    List<Map<String, Object>>auditlist=deptPartiallyCompleteService.fetchDepatmentPartialEmpfiles(deptId,file_cat);
	    System.out.println("auditlist===="+auditlist);
	    model.addAttribute("auditlist", auditlist);
		
		return "modules/pensioner/department/partial/retirement";
	}
	
	@GetMapping("/partial/familypension")
	public String partialfamilypension(Model model) {
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
	    int file_cat= newEntryService.getFamilyPensionFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
	    
	    List<Map<String, Object>>auditlist=deptPartiallyCompleteService.fetchDepatmentPartialEmpfiles(deptId,file_cat);
	    System.out.println("auditlist===="+auditlist);
	    model.addAttribute("auditlist", auditlist);
		
		return "modules/pensioner/department/partial/familypension";
	}
	
	@GetMapping("/partial/pendency")
	public String partialpendency(Model model) {
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
	    int file_cat= newEntryService.getPendencyFileCategoryId();
	    model.addAttribute("filecategory", file_cat);
	    
	    List<Map<String, Object>>auditlist=deptPartiallyCompleteService.fetchDepatmentPartialEmpfiles(deptId,file_cat);
	    System.out.println("auditlist===="+auditlist);
	    model.addAttribute("auditlist", auditlist);
		
		return "modules/pensioner/department/partial/pendency";
	}
	
	
	
	//dizho/////////////RETURN LIST PAGES//////////////////
	
	@GetMapping("/return/retirement")
	public String returnretirement(Model model) {
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
	    
	    int file_cat= newEntryService.getRetirementFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
	    
	    List<Map<String, Object>>returnlist=returnFromAuditService.fetchReturnEmpfiles(deptId,file_cat);
	    System.out.println("returnlist===="+returnlist);
	    model.addAttribute("returnlist", returnlist);
		
		return "modules/pensioner/department/return/retirement";
	}
	
	
	@GetMapping("/return/familypension")
	public String returnfamilypension(Model model) {
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
		    
		    int file_cat= newEntryService.getFamilyPensionFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    List<Map<String, Object>>returnlist=returnFromAuditService.fetchReturnEmpfiles(deptId,file_cat);
		    System.out.println("returnlist===="+returnlist);
		    model.addAttribute("returnlist", returnlist);
		
		return "modules/pensioner/department/return/familypension";
	}
	
	
	@GetMapping("/return/pendency")
	public String returnpendency(Model model) {
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
		    
		    int file_cat= newEntryService.getPendencyFileCategoryId();	    	
		    model.addAttribute("filecategory", file_cat);
		    
		    List<Map<String, Object>>returnlist=returnFromAuditService.fetchReturnEmpfiles(deptId,file_cat);
		    System.out.println("returnlist===="+returnlist);
		    model.addAttribute("returnlist", returnlist);
		
		return "modules/pensioner/department/return/pendency";
	}
	
	//////////////////RECEIVED LIST PAGES/////////////////////
	
	
	@GetMapping("/received/retirement")
	public String receivedretirement(Model model) {
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
	    
	    int file_cat= newEntryService.getRetirementFileCategoryId();	    	
	    model.addAttribute("filecategory", file_cat);
	    
	    List<Map<String, Object>>receivedlist=pensionReceivedService.fetchReceivedDepartmentEmpfiles(deptId,file_cat);
	    System.out.println("receivedlist===="+receivedlist);
	    model.addAttribute("receivedlist", receivedlist);
		
		return "modules/pensioner/department/received/retirement";
	}
	
	
	@GetMapping("/received/familypension")
	public String receivedfamilypension(Model model) {
		
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
	    
	    int file_cat= newEntryService.getFamilyPensionFileCategoryId();    	
	    model.addAttribute("filecategory", file_cat);
	    
	    List<Map<String, Object>>receivedlist=pensionReceivedService.fetchReceivedDepartmentEmpfiles(deptId,file_cat);
	    System.out.println("receivedlist===="+receivedlist);
	    model.addAttribute("receivedlist", receivedlist);
		
		return "modules/pensioner/department/received/familypension";
	}
	

	
	@GetMapping("/received/pendency")
	public String receivedpendency(Model model) {
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
	    
	    int file_cat= newEntryService.getPendencyFileCategoryId();    	
	    model.addAttribute("filecategory", file_cat);
	    
	    List<Map<String, Object>>receivedlist=pensionReceivedService.fetchReceivedDepartmentEmpfiles(deptId,file_cat);
	    System.out.println("receivedlist===="+receivedlist);
	    model.addAttribute("receivedlist", receivedlist);
		
		return "modules/pensioner/department/received/pendency";
	}
	
	
	@GetMapping("/received/provisionalretirement")
	public String receivedprovisionalretirement(Model model) {
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
	    
	    List<Map<String, Object>>provisionallist= receivedDeptProvisionalService.fetchDetailsProvisionalDepartment(file_cat);
	    	    
	    System.out.println("provisionallist===="+provisionallist);
	    model.addAttribute("provisionallist", provisionallist);
		
		return "modules/pensioner/department/received/provisionalretirement";
	}
	
	
	////////////////COMPLETED LIST PAGES/////////////////////////////
	
	@GetMapping("/completed/retirement")
	public String completedretirement(Model model) {
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
	    
	    List<Map<String, Object>>retirementcompletedlist= completedService.fetchDetailsCompletedRetirement(file_cat,deptId);
	    	    
	    System.out.println("provisionallist"+retirementcompletedlist);
	    model.addAttribute("provisionallist", retirementcompletedlist);
		
		return "modules/pensioner/department/completed/retirement";
	}
	
	
	@GetMapping("/completed/familypension")
	public String completedfamilypension(Model model) {
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
	    
	    List<Map<String, Object>>familypensioncompletedlist= completedService.fetchDetailsCompletedFamilyPension(file_cat,deptId);
	    	    
	    System.out.println("provisionallist===="+familypensioncompletedlist);
	    model.addAttribute("provisionallist", familypensioncompletedlist);
		
		return "modules/pensioner/department/completed/familypension";
	}
	
	
	@GetMapping("/completed/pendency")
	public String completedpendency(Model model) {
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
	    
	    List<Map<String, Object>>pendencycompletedlist= completedService.fetchDetailsCompletedPendency(file_cat,deptId);
	    	    
	    System.out.println("provisionallist===="+pendencycompletedlist);
	    model.addAttribute("provisionallist", pendencycompletedlist);
		
		return "modules/pensioner/department/completed/pendency";
	}
	
	
	@GetMapping("/completed/gis")
	public String completedgis(Model model) {
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
	    
	    List<Map<String, Object>>gislist= completedService.fetchDetailsForrecivedGiscompleted(file_cat,deptId);
	    	    
	    System.out.println("auditlist===="+gislist);
	    model.addAttribute("auditlist", gislist);
		
		return "modules/pensioner/department/completed/gis";
	}
	
	@GetMapping("/completed/provisionalretirement")
	public String completedprovisionalretirement(Model model) {
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
	    
	    List<Map<String, Object>>provisionallist= completedService.fetchDetailsProvisionalDepartmentCompleted(file_cat,deptId);
	    	    
	    System.out.println("provisionallist===="+provisionallist);
	    model.addAttribute("provisionallist", provisionallist);
		
		return "modules/pensioner/department/completed/provisionalretirement";
	}
	

	
	@GetMapping("/completed/contributorypensionscheme")
	public String completedContributoryPensionScheme(
	       
	        Model model) {
	    // Get logged-in user details
		
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
	    
	    List<Map<String, Object>>completedCPSList= completedService.getCPSList(file_cat,deptId);
	    	    
	    System.out.println("provisionallist===="+completedCPSList);
	    model.addAttribute("provisionallist", completedCPSList);
	    // Add the list to the model
	    model.addAttribute("completedCPSList", completedCPSList);

	    return "modules/pensioner/department/completed/contributorypensionscheme";
	}
	

	
	//Ajith       ////////////////REPORTS LIST PAGES//////////////////////
	
	@GetMapping("/reports")
	public String reports(Model model) {
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
		
		return "modules/pensioner/department/reports";
	}
	
	
	@GetMapping("/dashboard")
	public String dashboard(Model model) {
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
		
		
		return "modules/pensioner/department/dashboard";
	}
		
	
}

