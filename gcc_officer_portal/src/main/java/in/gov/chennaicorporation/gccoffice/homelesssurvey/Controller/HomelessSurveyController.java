package in.gov.chennaicorporation.gccoffice.homelesssurvey.Controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import in.gov.chennaicorporation.gccoffice.homelesssurvey.Service.HomelessSurveyService;

@RestController
@RequestMapping("/gcc/api/homeless-survey")
public class HomelessSurveyController {

    @Autowired
    private HomelessSurveyService service;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getHomelessSurveyList(

            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String ward,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        return ResponseEntity.ok(
                service.getHomelessSurveyList(
                        zone,
                        ward,
                        fromDate,
                        toDate));
    }

    @GetMapping("/zones")
    public ResponseEntity<List<Map<String, Object>>> getZones() {

        return ResponseEntity.ok(
                service.getZones());
    }

    @GetMapping("/wards")
    public ResponseEntity<List<Map<String, Object>>> getWards(
            @RequestParam String zoneId) {

        return ResponseEntity.ok(
                service.getWards(zoneId));
    }

    @GetMapping("/nonsurveyed/list")
    @ResponseBody
    public List<Map<String, Object>> getHomelessNonSurveyedList(

            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String ward,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        return service.getHomelessNonSurveyedList(
                zone,
                ward,
                fromDate,
                toDate);
    }

    @GetMapping("/details")
    public ResponseEntity<List<Map<String, Object>>> getSurveyDetails(
            @RequestParam String surveyId) {

        return ResponseEntity.ok(
                service.getHomelessSurveyDetails(surveyId));
    }

    @GetMapping("/document-count")
    public ResponseEntity<Integer> getDocumentCount(
            @RequestParam String surveyId) {

        return ResponseEntity.ok(
                service.getDocumentCount(surveyId));
    }

    @GetMapping("/documents")
    public ResponseEntity<List<Map<String, Object>>> getDocuments(
            @RequestParam String surveyId) {
        System.out.println("surveyId");
        System.out.println(surveyId);

        return ResponseEntity.ok(
                service.getSurveyDocuments(surveyId));
    }

    /*
     * @GetMapping("/download")
     * public ResponseEntity<Resource> downloadFile(
     * 
     * @RequestParam String fileUrl) throws Exception {
     * 
     * URL url = new URL(fileUrl);
     * 
     * InputStream inputStream = url.openStream();
     * 
     * byte[] bytes = inputStream.readAllBytes();
     * 
     * ByteArrayResource resource = new ByteArrayResource(bytes);
     * 
     * String fileName = fileUrl.substring(
     * fileUrl.lastIndexOf("/") + 1);
     * 
     * return ResponseEntity.ok()
     * .header(
     * HttpHeaders.CONTENT_DISPOSITION,
     * "attachment; filename=\"" +
     * fileName + "\"")
     * .contentLength(bytes.length)
     * .body(resource);
     * }
     */
    @GetMapping("/download-all-documents")
    public ResponseEntity<byte[]> downloadAllDocuments(
            @RequestParam String surveyId) throws Exception {

        List<Map<String, Object>> documents = service.getSurveyDocuments(surveyId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ZipOutputStream zipOut = new ZipOutputStream(baos);

        for (Map<String, Object> doc : documents) {

            String fileUrl = String.valueOf(doc.get("file_url"));

            String documentName = String.valueOf(doc.get("document_name"))
                    .replaceAll("\\s+", "_");

            URL url = new URL(fileUrl);

            InputStream is = url.openStream();

            byte[] fileBytes = is.readAllBytes();

            String extension = fileUrl.substring(fileUrl.lastIndexOf("."));

            ZipEntry zipEntry = new ZipEntry(
                    documentName + "_" +
                            surveyId +
                            extension);

            zipOut.putNextEntry(zipEntry);

            zipOut.write(fileBytes);

            zipOut.closeEntry();

            is.close();
        }

        zipOut.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=\""
                                + surveyId
                                + "_Documents.zip\"")
                .header(
                        "Content-Type",
                        "application/zip")
                .body(baos.toByteArray());
    }

    /*
     * @GetMapping("/download-document")
     * public ResponseEntity<byte[]> downloadDocument(
     * 
     * @RequestParam String fileUrl,
     * 
     * @RequestParam String fileName) {
     * 
     * try {
     * 
     * URL url = new URL(fileUrl);
     * 
     * InputStream is = url.openStream();
     * 
     * byte[] fileBytes = is.readAllBytes();
     * 
     * String extension = fileUrl.substring(
     * fileUrl.lastIndexOf("."));
     * 
     * return ResponseEntity.ok()
     * .header(
     * "Content-Disposition",
     * "attachment; filename=\"" + fileName + extension + "\"")
     * .header(
     * "Content-Type",
     * "application/octet-stream")
     * .body(fileBytes);
     * 
     * } catch (Exception e) {
     * 
     * throw new RuntimeException(e);
     * }
     * }
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getCategories() {

        return ResponseEntity.ok(
                service.getCategories());
    }

    @GetMapping("/getSurveyorsList")
    @ResponseBody
    public List<Map<String, Object>> getSurveyorsList(

            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String ward,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        return service.getSurveyorsList(
                zone,
                ward,
                fromDate,
                toDate);
    }

    @GetMapping("/surveyor-details")
    public ResponseEntity<List<Map<String, Object>>> getSurveyorDetails(

            @RequestParam String loginId,
            @RequestParam String zone,
            @RequestParam String ward,
            @RequestParam String surveyDate) {

        return ResponseEntity.ok(
                service.getSurveyorWiseSurveyList(
                        loginId,
                        zone,
                        ward,
                        surveyDate));
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

    @GetMapping("/relationtypemasterlist")
    public List<Map<String, Object>> getRelationTypeList() {

        return service.getRelationTypeList();
    }

    @PostMapping("/relationtypesave")
    public String saveRelationType(
            @RequestBody Map<String, Object> req) {

        service.saveRelationType(req);

        return "SUCCESS";
    }

    @PostMapping("/relationtype-status")
    public String updateRelationTypeStatus(
            @RequestBody Map<String, Object> req) {

        service.updateRelationTypeStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/currentlylivingmasterlist")
    public List<Map<String, Object>> getCurrentlyLivingList() {

        return service.getCurrentlyLivingList();
    }

    @PostMapping("/currentlylivingsave")
    public String saveCurrentlyLiving(
            @RequestBody Map<String, Object> req) {

        service.saveCurrentlyLiving(req);

        return "SUCCESS";
    }

    @PostMapping("/currentlyliving-status")
    public String updateCurrentlyLivingStatus(
            @RequestBody Map<String, Object> req) {

        service.updateCurrentlyLivingStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/martialmasterlist")
    public List<Map<String, Object>> getMartialList() {

        return service.getMartialList();
    }

    @PostMapping("/martialsave")
    public String saveMartial(
            @RequestBody Map<String, Object> req) {

        service.saveMartial(req);

        return "SUCCESS";
    }

    @PostMapping("/martial-status")
    public String updateMartialStatus(
            @RequestBody Map<String, Object> req) {

        service.updateMartialStatus(req);

        return "SUCCESS";
    }

    @GetMapping("/disabilitytype")
    public List<Map<String, Object>> getDisabilityTypeList() {
        return service.getDisabilityTypeList();
    }

    @PostMapping("/disabilitytypesave")
    public String saveDisabilityType(
            @RequestBody Map<String, Object> req) {
        service.saveDisabilityType(req);
        return "SUCCESS";
    }

    @PostMapping("/disabilitytype-status")
    public String updateDisabilityTypeStatus(
            @RequestBody Map<String, Object> req) {
        service.updateDisabilityTypeStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/disabiltycertificatelist")
    public List<Map<String, Object>> getDisabilityCertificateMasterList() {
        return service.getDisabilityCertificateMasterList();
    }

    @PostMapping("/disabiltycertificategersave")
    public String saveDisabilityCertificateMaster(
            @RequestBody Map<String, Object> req) {
        service.saveDisabilityCertificateMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/disabiltycertificate-status")
    public String updateDisabilityCertificateMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateDisabilityCertificateMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/healthconditionlist")
    public List<Map<String, Object>> getHealthConditionList() {
        return service.getHealthConditionList();
    }

    @PostMapping("/healthconditionsave")
    public String saveHealthCondition(
            @RequestBody Map<String, Object> req) {
        service.saveHealthCondition(req);
        return "SUCCESS";
    }

    @PostMapping("/healthcondition-status")
    public String updateHealthConditionStatus(
            @RequestBody Map<String, Object> req) {
        service.updateHealthConditionStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/educationmasterlist")
    public List<Map<String, Object>> getEducationMasterList() {
        return service.getEducationMasterList();
    }

    @PostMapping("/educationsave")
    public String saveEducationMaster(
            @RequestBody Map<String, Object> req) {
        service.saveEducationMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/education-status")
    public String updateEducationMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateEducationMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/discontinuedlist")
    public List<Map<String, Object>> getDiscontinuedList() {
        return service.getDiscontinuedList();
    }

    @PostMapping("/discontinuedsave")
    public String saveDiscontinuedMaster(
            @RequestBody Map<String, Object> req) {
        service.saveDiscontinuedMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/discontinued-status")
    public String updateDiscontinuedMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateDiscontinuedMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/employmenttypelist")
    public List<Map<String, Object>> getEmploymentTypeList() {
        return service.getEmploymentTypeList();
    }

    @PostMapping("/employmenttypesave")
    public String saveEmploymentType(
            @RequestBody Map<String, Object> req) {
        service.saveEmploymentType(req);
        return "SUCCESS";
    }

    @PostMapping("/employmenttype-status")
    public String updateEmploymentTypeStatus(
            @RequestBody Map<String, Object> req) {
        service.updateEmploymentTypeStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/occupationlist")
    public List<Map<String, Object>> getOccupationList() {
        return service.getOccupationList();
    }

    @PostMapping("/occupationsave")
    public String saveOccupation(
            @RequestBody Map<String, Object> req) {
        service.saveOccupation(req);
        return "SUCCESS";
    }

    @PostMapping("/occupation-status")
    public String updateOccupationStatus(
            @RequestBody Map<String, Object> req) {
        service.updateOccupationStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/notemployedlist")
    public List<Map<String, Object>> getNotEmployedList() {
        return service.getNotEmployedList();
    }

    @PostMapping("/notemployedsave")
    public String saveNotEmployed(
            @RequestBody Map<String, Object> req) {
        service.saveNotEmployed(req);
        return "SUCCESS";
    }

    @PostMapping("/notemployed-status")
    public String updateNotEmployedStatus(
            @RequestBody Map<String, Object> req) {
        service.updateNotEmployedStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/homelessreasonmasterlist")
    public List<Map<String, Object>> getReasonHomelessList() {
        return service.getReasonHomelessList();
    }

    @PostMapping("/homelessreasonmasterlist")
    public String saveReasonHomeless(
            @RequestBody Map<String, Object> req) {
        service.saveHomelessReason(req);
        return "SUCCESS";
    }

    @PostMapping("/homelessreasonmaster-status")
    public String updateHomelessReasonStatus(
            @RequestBody Map<String, Object> req) {
        service.updateHomelessReasonStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/sleepingspacemasterlist")
    public List<Map<String, Object>> getSleepingSpaceList() {
        return service.getSleepingSpaceList();
    }

    @PostMapping("/sleepingspacesave")
    public String saveSleepingSpace(
            @RequestBody Map<String, Object> req) {
        service.saveSleepingSpace(req);
        return "SUCCESS";
    }

    @PostMapping("/sleepingspace-status")
    public String updateSleepingSpaceStatus(
            @RequestBody Map<String, Object> req) {
        service.updateSleepingSpaceStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/movingreasonmasterlist")
    public List<Map<String, Object>> getMovingReasonList() {
        return service.getMovingReasonList();
    }

    @PostMapping("/movingreasonmasterdatasave")
    public String saveMovingReason(
            @RequestBody Map<String, Object> req) {
        service.saveMovingReason(req);
        return "SUCCESS";
    }

    @PostMapping("/movingreasonmasterdata-status")
    public String updateMovingReasonStatus(
            @RequestBody Map<String, Object> req) {
        service.updateMovingReasonStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/comeoutsheltermasterlist")
    public List<Map<String, Object>> getComeOutShelterList() {
        return service.getComeOutShelterList();
    }

    @PostMapping("/comeoutsheltersave")
    public String saveComeOutShelter(
            @RequestBody Map<String, Object> req) {
        service.saveComeOutShelter(req);
        return "SUCCESS";
    }

    @PostMapping("/comeoutshelter-status")
    public String updateComeOutShelterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateComeOutShelterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/gccsheltermasterlist")
    public List<Map<String, Object>> getGCCShelterMasterList() {
        return service.getGCCShelterMasterList();
    }

    @PostMapping("/gccsheltermasterdatasave")
    public String saveGCCShelterMaster(
            @RequestBody Map<String, Object> req) {
        service.saveGCCShelterMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/gccsheltermasterdata-status")
    public String updateGCCShelterMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateGCCShelterMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/birthcertificatmasterlist")
    public List<Map<String, Object>> getBirthCertificateNoReasonMasterList() {
        return service.getBirthCertificateNoReasonMasterList();
    }

    @PostMapping("/birthcertificatmasterdatasave")
    public String saveBirthCertificateNoReasonMaster(
            @RequestBody Map<String, Object> req) {
        service.saveBirthCertificateNoReasonMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/birthcertificatmasterdata-status")
    public String updateBirthCertificateNoReasonMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateBirthCertificateNoReasonMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/communitymasterlist")
    public List<Map<String, Object>> getCommunityMasterList() {
        return service.getCommunityMasterList();
    }

    @PostMapping("/communitycertmasterdatasave")
    public String saveCommunityMaster(
            @RequestBody Map<String, Object> req) {
        service.saveCommunityMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/communitycertmasterdata-status")
    public String updateCommunityMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateCommunityMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/voteridmasterlist")
    public List<Map<String, Object>> getVoterIdMasterList() {
        return service.getVoterIdMasterList();
    }

    @PostMapping("/voteridmastersave")
    public String saveVoterIdMaster(
            @RequestBody Map<String, Object> req) {
        service.saveVoterIdMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/voteridmaster-status")
    public String updateVoterIdMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateVoterIdMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/aadharnoreasonlst")
    public List<Map<String, Object>> getAadharCertificateNoReasonMasterList() {
        return service.getAadharCertificateNoReasonMasterList();
    }

    @PostMapping("/aadharnoreasonsave")
    public String saveAadharCertificateNoReasonMaster(
            @RequestBody Map<String, Object> req) {
        service.saveAadharCertificateNoReasonMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/aadharnoreason-status")
    public String updateAadharCertificateNoReasonMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateAadharCertificateNoReasonMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/smartcardnoreasonmasterlist")
    public List<Map<String, Object>> getSmartCardNoReasonMasterList() {
        return service.getSmartCardNoReasonMasterList();
    }

    @PostMapping("/smartcardnoreasonsave")
    public String saveSmartCardNoReasonMaster(
            @RequestBody Map<String, Object> req) {
        service.saveSmartCardNoReasonMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/smartcardnoreasonstatus")
    public String updateSmartCardNoReasonMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateSmartCardNoReasonMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/driverlicensemasterlist")
    public List<Map<String, Object>> getDriverLicenseNoReasonMasterList() {
        return service.getDriverLicenseNoReasonMasterList();
    }

    @PostMapping("/driverlicensesave")
    public String saveDriverLicenseNoReasonMaster(
            @RequestBody Map<String, Object> req) {
        service.saveDriverLicenseNoReasonMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/driverlicense-status")
    public String updateDriverLicenseNoReasonMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateDriverLicenseNoReasonMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/cminsurancemasterlist")
    public List<Map<String, Object>> getCmInsuranceNoReasonMasterList() {
        return service.getCmInsuranceNoReasonMasterList();
    }

    @PostMapping("/cminsurancesave")
    public String saveCmInsuranceNoReasonMaster(
            @RequestBody Map<String, Object> req) {
        service.saveCmInsuranceNoReasonMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/cminsurance-status")
    public String updateCmInsuranceNoReasonMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateCmInsuranceNoReasonMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/bankpassbookreasonmasterlist")
    public List<Map<String, Object>> getBankPassbookNoReasonMasterList() {
        return service.getBankPassbookNoReasonMasterList();
    }

    @PostMapping("/bankpassbookreasonmaster-save")
    public String saveBankPassbookNoReasonMaster(
            @RequestBody Map<String, Object> req) {
        service.saveBankPassbookNoReasonMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/bankpassbookreasonmasterstatus")
    public String updateBankPassbookNoReasonMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateBankPassbookNoReasonMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/religionmasterlist")
    public List<Map<String, Object>> getReligionMasterList() {
        return service.getReligionMasterList();
    }

    @PostMapping("/religionmasterdatasave")
    public String saveReligionMaster(
            @RequestBody Map<String, Object> req) {
        service.saveReligionMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/religionmasterdata-status")
    public String updateReligionMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateReligionMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/castemasterlist")
    public List<Map<String, Object>> getCasteMasterList() {
        return service.getCasteMasterList();
    }

    @PostMapping("/castemasterdatasave")
    public String saveCasteMaster(
            @RequestBody Map<String, Object> req) {
        service.saveCasteMaster(req);
        return "SUCCESS";
    }

    @PostMapping("/castemasterdata-status")
    public String updateCasteMasterStatus(
            @RequestBody Map<String, Object> req) {
        service.updateCasteMasterStatus(req);
        return "SUCCESS";
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {

        return ResponseEntity.ok(
                service.getDashboard());
    }

    @GetMapping("/zone-wise")
    public ResponseEntity<List<Map<String, Object>>> getZoneWiseCount() {

        return ResponseEntity.ok(
                service.getZoneWiseCount());
    }

    @GetMapping("/ward-wise")
    public ResponseEntity<List<Map<String, Object>>> getWardWiseCount(
            @RequestParam String zone) {

        return ResponseEntity.ok(
                service.getWardWiseCount(zone));
    }

}
