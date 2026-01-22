package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.pensioner.service.NewEntryService;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;


@RequestMapping("/gcc/departmentfileclosing")
@Controller("gccpensionerfileclosing")


public class FileClosingMainController {
	
	
	@Autowired
	
	private NewEntryService newEntryService;
	
	
	@GetMapping("/fileclosing")
	public String searchEmployee(Model model) {
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
		
		
		return "modules/pensioner/department/fileclosing";
	}
	
	@GetMapping("/closedlist")
	public String closedEmployeeList(Model model) {
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
		
		
		return "modules/pensioner/department/closedList";
	}

}
