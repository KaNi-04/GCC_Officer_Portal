package in.gov.chennaicorporation.gccoffice.homelesssurvey.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("HomelessSurveyController")
@RequestMapping("/gcc/homelesssurvey")
public class MainController {

    @GetMapping("/homelesslist/surveyedlist")
    public String surveyedlist() {
        return "modules/homelesssurvey/homelesslist";
    }

    @GetMapping("/homelesslist/nonsurveyedlist")
    public String nonsurveyedlist() {
        return "modules/homelesssurvey/nonsurveyedhomelist";
    }

    @GetMapping("/surveylist")

    public String surveylist() {
        return "modules/homelesssurvey/surveyorlist";
    }

    @GetMapping("/surveyorsreports")

    public String surveyorsreports() {
        return "modules/homelesssurvey/surveyorsreports";
    }

    @GetMapping("/homelesslist/gender")
    public String gender() {
        return "modules/homelesssurvey/gendermaster";
    }

    @GetMapping("/homelesslist/relationtype")
    public String relationtype() {
        return "modules/homelesssurvey/relationtype";
    }

    @GetMapping("/homelesslist/currentlyliving")
    public String currentlyliving() {
        return "modules/homelesssurvey/currentlyliving";
    }

    @GetMapping("/homelesslist/martial")
    public String martialstatus() {
        return "modules/homelesssurvey/martial";
    }

    @GetMapping("/homelesslist/disabilitytype")
    public String disabilitytype() {
        return "modules/homelesssurvey/disability";
    }

    @GetMapping("/homelesslist/diable")
    public String disabiltycertificate() {
        return "modules/homelesssurvey/disabiltycertificate";
    }

    @GetMapping("/homelesslist/healthcondition")
    public String healthcondition() {
        return "modules/homelesssurvey/healthcondition";
    }

    @GetMapping("/homelesslist/education")
    public String education() {
        return "modules/homelesssurvey/education";
    }

    @GetMapping("/homelesslist/discontinued")
    public String discontinued() {
        return "modules/homelesssurvey/discontinued";
    }

    @GetMapping("/homelesslist/employment")
    public String employmenttype() {
        return "modules/homelesssurvey/employmenttype";
    }

    @GetMapping("/homelesslist/occupation")
    public String occupation() {
        return "modules/homelesssurvey/occupation";
    }

    @GetMapping("/homelesslist/notemployed")
    public String notemployed() {
        return "modules/homelesssurvey/notemployed";
    }

    @GetMapping("/homelesslist/homeless")
    public String homelessreason() {
        return "modules/homelesssurvey/homelessreason";
    }

    @GetMapping("/homelesslist/sleepingspace")
    public String sleepingspace() {
        return "modules/homelesssurvey/sleepingspace";
    }

    @GetMapping("/homelesslist/moving")
    public String movingreason() {
        return "modules/homelesssurvey/movingreason";
    }

    @GetMapping("/homelesslist/comeoutshelter")
    public String comeoutshelter() {
        return "modules/homelesssurvey/comeoutshelter";
    }

    @GetMapping("/homelesslist/gccshelter")
    public String gccshelter() {
        return "modules/homelesssurvey/gccshelter";
    }

    @GetMapping("/homelesslist/birthcertificate")
    public String birthcertficatenoreason() {
        return "modules/homelesssurvey/birthcertficatenoreason";
    }

    @GetMapping("/homelesslist/communitycertificate")
    public String communitycertificate() {
        return "modules/homelesssurvey/communitynoreason";
    }

    @GetMapping("/homelesslist/votercertificate")
    public String voterid() {
        return "modules/homelesssurvey/voterid";
    }

    @GetMapping("/homelesslist/aadharcertificate")
    public String aadhar() {
        return "modules/homelesssurvey/aadhar";
    }

    @GetMapping("/homelesslist/smartcertificate")
    public String smartcard() {
        return "modules/homelesssurvey/smart";
    }

    @GetMapping("/homelesslist/drivercertificate")
    public String driverlicense() {
        return "modules/homelesssurvey/driver";
    }

    @GetMapping("/homelesslist/cmrcertificate")
    public String cminsurance() {
        return "modules/homelesssurvey/cminsurance";
    }

    @GetMapping("/homelesslist/bankpassbook")
    public String bankpassbook() {
        return "modules/homelesssurvey/bankpassbook";
    }

    @GetMapping("/homelesslist/religion")
    public String religion() {
        return "modules/homelesssurvey/religion";
    }

    @GetMapping("/homelesslist/caste")
    public String caste() {
        return "modules/homelesssurvey/caste";
    }

}
