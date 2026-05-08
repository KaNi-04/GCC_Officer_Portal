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

}
