package in.gov.chennaicorporation.gccoffice.childrensurvey.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/educationmasterlist")
    public List<Map<String, Object>> list() {

        return service.getEducationList();
    }

    @PostMapping("/save")
    public String save(
            @RequestBody Map<String, Object> req) {

        service.saveEducation(req);

        return "SUCCESS";
    }

    @PostMapping("/status")
    public String updateStatus(
            @RequestBody Map<String, Object> req) {

        service.updateStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/locationmasterlist")
    public List<Map<String, Object>> getLocationList() {
        return service.getLocationList();
    }

    @PostMapping("/locationsave")
    public String saveLocation(
            @RequestBody Map<String, Object> req) {

        service.saveLocation(req);

        return "SUCCESS";
    }

    @PostMapping("/location-status")
    public String updateLocationStatus(
            @RequestBody Map<String, Object> req) {

        service.updateLocationStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/gendermasterlist")
    public List<Map<String, Object>> getGenderList() {
        return service.getGenderList();
    }

    @PostMapping("/gendersave")
    public String saveGender(
            @RequestBody Map<String, Object> req) {

        service.saveGender(req);

        return "SUCCESS";
    }

    @PostMapping("/gender-status")
    public String updateGenderStatus(
            @RequestBody Map<String, Object> req) {

        service.updateGenderStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/castemasterlist")
    public List<Map<String, Object>> getCasteList() {
        return service.getCasteList();
    }

    @PostMapping("/savecaste")
    public String saveCaste(
            @RequestBody Map<String, Object> req) {

        service.saveCaste(req);

        return "SUCCESS";
    }

    @PostMapping("/caste-status")
    public String updateCasteStatus(
            @RequestBody Map<String, Object> req) {

        service.updateCasteStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/livingmasterlist")
    public List<Map<String, Object>> getLivingList() {
        return service.getLivingList();
    }

    @PostMapping("/livingsave")
    public String saveLiving(
            @RequestBody Map<String, Object> req) {

        service.saveLiving(req);

        return "SUCCESS";
    }

    @PostMapping("/living-status")
    public String updateLivingStatus(
            @RequestBody Map<String, Object> req) {

        service.updateLivingStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/documentcreationmasterlist")
    public List<Map<String, Object>> getDocumentCreationList() {
        return service.getDocumentCreationList();
    }

    @PostMapping("/documentcreationsave")
    public String saveDocumentCreation(
            @RequestBody Map<String, Object> req) {

        service.saveDocumentCreation(req);

        return "SUCCESS";
    }

    @PostMapping("/documentcreationstatus")
    public String updateDocumentCreationStatus(
            @RequestBody Map<String, Object> req) {

        service.updateDocumentCreationStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/dropoutreasonmasterlist")
    public List<Map<String, Object>> getDropoutReasonList() {
        return service.getDropoutReasonList();
    }

    @PostMapping("/dropoutreasonsave")
    public String saveDropoutReason(
            @RequestBody Map<String, Object> req) {

        service.saveDropoutReason(req);

        return "SUCCESS";
    }

    @PostMapping("/dropoutreasonstatus")
    public String updateDropoutReasonStatus(
            @RequestBody Map<String, Object> req) {

        service.updateDropoutReasonStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/incomemasterlist")
    public List<Map<String, Object>> getIncomeList() {
        return service.getIncomeList();
    }

    @PostMapping("/incomesave")
    public String saveIncome(
            @RequestBody Map<String, Object> req) {

        service.saveIncome(req);

        return "SUCCESS";
    }

    @PostMapping("/incomemasterstatus")
    public String updateIncomeStatus(
            @RequestBody Map<String, Object> req) {

        service.updateIncomeStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/interestfieldmasterlist")
    public List<Map<String, Object>> getInterestFieldList() {
        return service.getInterestFieldList();
    }

    @PostMapping("/interestfieldsave")
    public String saveInterestField(
            @RequestBody Map<String, Object> req) {

        service.saveInterestField(req);

        return "SUCCESS";
    }

    @PostMapping("/interestfieldmaster-status")
    public String updateInterestFieldStatus(
            @RequestBody Map<String, Object> req) {

        service.updateInterestFieldStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/ownermasterlist")
    public List<Map<String, Object>> getOwnerList() {
        return service.getOwnerList();
    }

    @PostMapping("/ownermastersave")
    public String saveOwner(
            @RequestBody Map<String, Object> req) {

        service.saveOwner(req);

        return "SUCCESS";
    }

    @PostMapping("/ownermasterstatus")
    public String updateOwnerStatus(
            @RequestBody Map<String, Object> req) {

        service.updateOwnerStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/religionmasterlist")
    public List<Map<String, Object>> getReligionList() {
        return service.getReligionList();
    }

    @PostMapping("/religionsave")
    public String saveReligion(
            @RequestBody Map<String, Object> req) {

        service.saveReligion(req);

        return "SUCCESS";
    }

    @PostMapping("/religion-status")
    public String updateReligionStatus(
            @RequestBody Map<String, Object> req) {

        service.updateReligionStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/vulnerabilitymasterlist")
    public List<Map<String, Object>> getVulnerabilityList() {
        return service.getVulnerabilityList();
    }

    @PostMapping("/vulnerabilitysave")
    public String saveVulnerability(
            @RequestBody Map<String, Object> req) {

        service.saveVulnerability(req);

        return "SUCCESS";
    }

    @PostMapping("/vulnerabilitymasterstatus")
    public String updateVulnerabilityStatus(
            @RequestBody Map<String, Object> req) {

        service.updateVulnerabilityStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/whereaboutmasterlist")
    public List<Map<String, Object>> getWhereaboutList() {
        return service.getWhereaboutList();
    }

    @PostMapping("/whereaboutsave")
    public String saveWhereabout(
            @RequestBody Map<String, Object> req) {

        service.saveWhereabout(req);

        return "SUCCESS";
    }

    @PostMapping("/whereaboutstatus")
    public String updateWhereaboutStatus(
            @RequestBody Map<String, Object> req) {

        service.updateWhereaboutStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/documentreasonmasterlist")
    public List<Map<String, Object>> getDocumentReasonList() {
        return service.getDocumentReasonList();
    }

    @PostMapping("/documentreasonmaster")
    public String saveDocumentReason(
            @RequestBody Map<String, Object> req) {

        service.saveDocumentReason(req);

        return "SUCCESS";
    }

    @PostMapping("/documentreasonmasterstatus")
    public String updateDocumentReasonStatus(
            @RequestBody Map<String, Object> req) {

        service.updateDocumentReasonStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/nonsurveyed/list")
    public ResponseEntity<List<Map<String, Object>>> getNonSurveyedChildList(

            @RequestParam(required = false) String area,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        return ResponseEntity.ok(
                service.getNonSurveyedChildList(
                        area,
                        fromDate,
                        toDate));
    }
}
