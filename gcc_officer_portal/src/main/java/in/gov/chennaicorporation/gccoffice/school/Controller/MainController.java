package in.gov.chennaicorporation.gccoffice.school.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@Controller("schoolcontroller")
@RequestMapping("/gcc/school")
public class MainController {

	@GetMapping("/otherdetails")
	public String otherdetails(Model model) {

		return "modules/school/otherdetails";
	}

	@GetMapping("/schooldetails")
	public String viewschool(Model model) {
		return "modules/school/schooldetails";
	}

	@GetMapping("/schoolmodule")
	public String viewmodule(Model model) {
		String loginUserId = LoginUserInfo.getLoginUserId();
		model.addAttribute("user_id", loginUserId);
		System.out.println("loginUserId..........." + loginUserId);
		return "modules/school/dashboard";
	}

	@GetMapping("/staffdetails")
	public String staffdetails(Model model) {
		return "modules/school/staffdetails";
	}

	@GetMapping("/roomassets")
	public String roomassets(Model model) {

		return "modules/school/roomassets";
	}

	@GetMapping("/assetmanagement")
	public String assetmanagement(Model model) {

		return "modules/school/assetmanagement";
	}


	@GetMapping("/studentdetails")
	public String studentdetails(Model model) {

		return "modules/school/studentdetails";
	}

	@GetMapping("/building")
	public String buildings(Model model) {

		return "modules/school/building";
	}

	@GetMapping("/room")
	public String roomlist(Model model) {

		return "modules/school/room";
	}

	@GetMapping("/playground")
	public String playground(Model model) {

		return "modules/school/playground";
	}

	@GetMapping("/drinkingwater")
	public String drinkingwater(Model model) {

		return "modules/school/drinkingwater";
	}

	@GetMapping("/toilets")
	public String toilet(Model model) {

		return "modules/school/toilets";
	}

//	@GetMapping("/electricalassets")
//	public String electricalassets(Model model) {
//
//		return "modules/school/electricalassets";
//	}
//
//	@GetMapping("/furnitureassets")
//	public String furnitureassets(Model model) {
//
//		return "modules/school/furnitureassets";
//	}

	@GetMapping("/kitchen")
	public String kitchen(Model model) {

		return "modules/school/kitchen";
	}

	@GetMapping("/addelectricalasset")
	public String addelectricalasset(Model model) {

		return "modules/school/addelectricalasset";
	}

	@GetMapping("/addfurnitureasset")
	public String addfurnitureasset(Model model) {

		return "modules/school/addfurnitureasset";
	}
	@GetMapping("/reports")
	public String reportpage(Model model) {
	 
		return "modules/school/reports";
	}
	
	@GetMapping("/studentregistration")
	public String studentRegistrationPage() {
		
		return "modules/school/studentregister";
	}
	
	@GetMapping("/aeoschoolentry/aeolist")
	public String viewaeolist(Model model) {
	 
		return "modules/school/reportfolder/aeolist";
	}
	

	@GetMapping("/aeoschoolentry/aeoallschools")
	public String viewaeoallschools(Model model) {
	 
		return "modules/school/reportfolder/aeoallschools";
	}
}