package in.gov.chennaicorporation.gccoffice.flagpole.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/flagpole")
@Controller("flagpoleController")
public class MainController {

    @GetMapping("/requestlist")
    public String home(Model model) {

        String LoginUserId = LoginUserInfo.getLoginUserId();
        model.addAttribute("LoginUserId", LoginUserId);

        String officer_name = LoginUserInfo.getCurrentUserDetails().getUsername();
        model.addAttribute("officer_name", officer_name);

        String UserRole = LoginUserInfo.getUserRole(); // rdo_north / rdo_central
        model.addAttribute("UserRole", UserRole);

        return "modules/flagpole/requestlist";
    }

    @GetMapping("/approvedandrejectedlist")
    public String batches(Model model) {
        String LoginUserId = LoginUserInfo.getLoginUserId();
        model.addAttribute("LoginUserId", LoginUserId);
        String officer_name = LoginUserInfo.getCurrentUserDetails().getUsername();
        model.addAttribute("officer_name", officer_name);

        String UserRole = LoginUserInfo.getUserRole();
        model.addAttribute("UserRole", UserRole);
        return "modules/flagpole/approvedandrejectedlist";
    }

    @GetMapping("/reports")
    public String dataentry(Model model) {
        String LoginUserId = LoginUserInfo.getLoginUserId();
        model.addAttribute("LoginUserId", LoginUserId);

        String officer_name = LoginUserInfo.getCurrentUserDetails().getUsername();
        model.addAttribute("officer_name", officer_name);

        String UserRole = LoginUserInfo.getUserRole();
        model.addAttribute("UserRole", UserRole);
        return "modules/flagpole/reports";
    }
}
