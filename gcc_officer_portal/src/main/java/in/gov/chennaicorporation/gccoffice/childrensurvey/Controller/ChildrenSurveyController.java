package in.gov.chennaicorporation.gccoffice.childrensurvey.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.childrensurvey.Service.ChildrenSurveyService;

@RestController
@RequestMapping("/gcc/api/children-survey")
public class ChildrenSurveyController {

    @Autowired
    private ChildrenSurveyService service;

    @GetMapping("/gender")
    public List<Map<String, Object>> getGender() {
        return service.getGender();
    }

    @GetMapping("/location")
    public List<Map<String, Object>> getLocation() {
        return service.getLocation();
    }

    @GetMapping("/education")
    public List<Map<String, Object>> getEducation() {
        return service.getEducation();
    }

    @GetMapping("/list")
    public List<Map<String, Object>> getSurveyList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String age,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        return service.getSurveyList(name, gender, age, area, education, fromDate, toDate);
    }

    @GetMapping("/details")
    public Map<String, Object> getSurveyDetails(
            @RequestParam String surveyId) {
        return service.getSurveyDetails(surveyId);
    }

    @GetMapping("/getSurveyorsList")
    @ResponseBody
    public List<Map<String, Object>> getSurveyorsList(

            @RequestParam(required = false) String name,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        return service.getSurveyorsList(
                name,
                area,
                fromDate,
                toDate);
    }

    @GetMapping("/survey-breakup")
    @ResponseBody
    public List<Map<String, Object>> getSurveyBreakup(

            @RequestParam(required = false) String loginId,
            @RequestParam(required = false) String locationId,
            @RequestParam(required = false) String surveyDate) {

        return service.getSurveyBreakup(loginId, locationId, surveyDate);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {

        return service.getDashboardData();
    }

}
