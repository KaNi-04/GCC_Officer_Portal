package in.gov.chennaicorporation.gccoffice.childrensurvey.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("ChildrenSurveyController")
@RequestMapping("/gcc/childrensurvey")
public class MainController {

    @GetMapping("/surveylist")
    public String surveylist() {
        return "modules/childrensurvey/surveylist";
    }

    @GetMapping("/childlist")
    public String childlist() {
        return "modules/childrensurvey/childlist";
    }

    @GetMapping("/surveyorsreports")
    public String surveyorsreports() {
        return "modules/childrensurvey/surveyorsreports";
    }

    @GetMapping("/mastersubmenu/educationmaster")
    public String educationmaster() {
        return "modules/childrensurvey/educationmaster";
    }

    @GetMapping("/mastersubmenu/locationmaster")
    public String locationmaster() {
        return "modules/childrensurvey/locationmaster";
    }

    @GetMapping("/mastersubmenu/gendermaster")
    public String gendermaster() {
        return "modules/childrensurvey/gendermaster";
    }

    @GetMapping("/mastersubmenu/castemaster")
    public String castemaster() {
        return "modules/childrensurvey/castemaster";
    }

    @GetMapping("/mastersubmenu/livingmaster")
    public String livingmaster() {
        return "modules/childrensurvey/livingmaster";
    }

    // documentation
    @GetMapping("/mastersubmenu/documentcorrectionmaster")
    public String documentcorrectionmaster() {
        return "modules/childrensurvey/documentcorrectionmaster";
    }

    @GetMapping("/mastersubmenu/dropoutreasonmaster")
    public String dropoutreasonmaster() {
        return "modules/childrensurvey/dropoutreasonmaster";
    }

    @GetMapping("/mastersubmenu/incomemaster")
    public String incomemaster() {
        return "modules/childrensurvey/incomemaster";
    }

    @GetMapping("/mastersubmenu/religionmaster")
    public String religionmaster() {
        return "modules/childrensurvey/religionmaster";
    }

    @GetMapping("/mastersubmenu/interestfieldmaster")
    public String interestfieldmaster() {
        return "modules/childrensurvey/interestfieldmaster";
    }

    @GetMapping("/mastersubmenu/ownershipmaster")
    public String ownermaster() {
        return "modules/childrensurvey/ownermaster";
    }

    @GetMapping("/mastersubmenu/vulnerabilitymaster")
    public String vulnerabilitymaster() {
        return "modules/childrensurvey/vulnerabilitymaster";
    }

    @GetMapping("/mastersubmenu/whereaboutmaster")
    public String whereaboutmaster() {
        return "modules/childrensurvey/whereaboutmaster";
    }

    @GetMapping("/mastersubmenu/documentreasonmaster")
    public String documentreasonmaster() {
        return "modules/childrensurvey/documentreasonmaster";
    }

}
