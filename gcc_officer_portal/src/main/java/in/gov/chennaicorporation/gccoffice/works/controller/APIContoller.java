package in.gov.chennaicorporation.gccoffice.works.controller;

import in.gov.chennaicorporation.gccoffice.works.service.ERPEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.works.service.EstimateService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gcc/works/api/estimates")

public class APIContoller {

    @Autowired
    private EstimateService estimateService;

    @Autowired
    private ERPEstimateService erpEstimateService;

    // to save ERP works
    /*@PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveEstimate(@RequestParam("estno") String estimateNo,
                                          @RequestParam("estdate") String estimateDate,
                                          @RequestParam("zone") String zone,
                                          @RequestParam("ward") String ward,
                                          @RequestParam("projectname") String projectName,
                                          @RequestParam("location") String location,
                                          @RequestParam("department") String department,
                                          @RequestParam("contractorname") String contractorName,
                                          @RequestParam("contractorperiod") String contractorPeriod,
                                          @RequestParam("fundsource") String fundSource,
                                          @RequestParam("scheme") String scheme,
                                          @RequestParam("category") String category,
                                          @RequestParam("subcategory") String subCategory,
                                          @RequestParam("estamount") String estAmount,
                                          @RequestParam("techsanctiondate") String techSancDate,
                                          @RequestParam("adminsanctiondate") String adminSancDate,
                                          @RequestParam("tendercalldate") String tenderCallDate,
                                          @RequestParam("tenderfinalizeddate") String tenderFinDate,
                                          @RequestParam("loadate") String loaDate,
                                          @RequestParam("agreementdate") String agreementDate,
                                          @RequestParam("workorderdate") String workOrderDate,
                                          @RequestParam("workcommenceddate") String workCommDate,
                                          @RequestParam("adminsanctionfile") MultipartFile adminSanctionFile,
                                          @RequestParam("loafile") MultipartFile loaFile,
                                          @RequestParam("agreementfile") MultipartFile agreementFile,
                                          @RequestParam("workorderfile") MultipartFile workOrderFile,
                                          @RequestParam("iconicProject") Integer iconicProjectInt) {
        try {
            String adminSanFilePath = estimateService.saveFile(adminSanctionFile, "admin");
            String loaFilePath = estimateService.saveFile(loaFile, "loa");
            String agreementFilePath = estimateService.saveFile(agreementFile, "agreement");
            String workOrderFilePath = estimateService.saveFile(workOrderFile, "workorder");

            Boolean iconicProject = (iconicProjectInt != null && iconicProjectInt == 1);

            // Convert date formats
            String newestDate = convertDateFormat(estimateDate);
            String newTechSancDate = convertDateFormat(techSancDate);
            String newAdminSancDate = convertDateFormat(adminSancDate);
            String newTenderCallDate = convertDateFormat(tenderCallDate);
            String newTenderFinDate = convertDateFormat(tenderFinDate);
            String newLoaDate = convertDateFormat(loaDate);
            String newAgreementDate = convertDateFormat(agreementDate);
            String newWorkOrderDate = convertDateFormat(workOrderDate);
            String newWorkCommDate = convertDateFormat(workCommDate);

            // Call saveEstimate method and pass the Iconic Project field
            int result = estimateService.saveEstimate(estimateNo, newestDate, zone, ward, projectName, iconicProject, location,
                    department, contractorName, contractorPeriod, fundSource, scheme, category, subCategory, estAmount,
                    newTechSancDate, newAdminSancDate, adminSanFilePath, newTenderCallDate, newTenderFinDate, newLoaDate, loaFilePath,
                    newAgreementDate, agreementFilePath, newWorkOrderDate, workOrderFilePath, newWorkCommDate);

            if (result > 0) {
                return ResponseEntity.ok("Estimate saved successfully");
            } else {
                return ResponseEntity.status(500).body("Failed to save estimate");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving estimate: " + e.getMessage());
        }
    }*/

    // Handle the form submission
   
    // To save ERP details in erp_works table
    @PostMapping(value = "/saveERPForm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveERPEstimate(@RequestParam("erpestno") String estimateNo,
                                             @RequestParam("erpestdate") String estimateDate,
                                             @RequestParam("erpzone") String zone,
                                             @RequestParam("erpward") String ward,
                                             @RequestParam("erpprojectname") String projectName,
                                             @RequestParam("erplocation") String location,
                                             @RequestParam("erpdepartment") String department,
                                             @RequestParam(value = "iconicProject", required = false, defaultValue = "0") int iconicProject,  // ✅ Receive int directly
                                             @RequestParam("erpcontractorname") String contractorName,
                                             @RequestParam("erpcontractorperiod") String contractorPeriod,
                                             @RequestParam("erpfundsource") String fundSource,
                                             @RequestParam("erpscheme") String scheme,
                                             @RequestParam("erpcategory") String category,
                                             @RequestParam("erpsubcategory") String subCategory,
                                             @RequestParam("erpestamount") String estAmount,
                                             @RequestParam("erptechsanctiondate") String techSancDate,
                                             @RequestParam("erpadminsanctiondate") String adminSancDate,
                                             @RequestParam("erptendercalldate") String tenderCallDate,
                                             @RequestParam("erptenderfinalizeddate") String tenderFinDate,
                                             @RequestParam("erploadate") String loaDate,
                                             @RequestParam("erpagreementdate") String agreementDate,
                                             @RequestParam("erpworkorderdate") String workOrderDate,
                                             @RequestParam("erpworkcommenceddate") String workCommDate,
                                             @RequestParam("erploafile") MultipartFile loaFile,
                                             @RequestParam("erpagreementfile") MultipartFile agreementFile,
                                             @RequestParam("erpworkorderfile") MultipartFile workOrderFile,
                                             @RequestParam("erpadminsanctionfile") MultipartFile adminSanFile) {
        try {
            String adminSanFilePath = estimateService.saveFile(adminSanFile, "admin","erp");
            String loaFilePath = estimateService.saveFile(loaFile, "loa","erp");
            String agreementFilePath = estimateService.saveFile(agreementFile, "agreement","erp");
            String workOrderFilePath = estimateService.saveFile(workOrderFile, "workorder","erp");

            // Debugging: Check what value is received from frontend
            System.out.println("Received Iconic Project Value: " + iconicProject);

            // Convert date formats
            String newestDate = convertDateFormat(estimateDate);
            String newTechSancDate = convertDateFormat(techSancDate);
            String newAdminSancDate = convertDateFormat(adminSancDate);
            String newTenderCallDate = convertDateFormat(tenderCallDate);
            String newTenderFinDate = convertDateFormat(tenderFinDate);
            String newLoaDate = convertDateFormat(loaDate);
            String newAgreementDate = convertDateFormat(agreementDate);
            String newWorkOrderDate = convertDateFormat(workOrderDate);
            String newWorkCommDate = convertDateFormat(workCommDate);

            // Call saveEstimate method and pass Iconic Project as int
            int result = estimateService.saveERPEstimate(estimateNo, newestDate, zone, ward, projectName, iconicProject, location,
                    department, contractorName, contractorPeriod, fundSource, scheme, category,
                    subCategory, estAmount, newTechSancDate, newAdminSancDate, adminSanFilePath,
                    newTenderCallDate, newTenderFinDate, newLoaDate, loaFilePath, newAgreementDate,
                    agreementFilePath, newWorkOrderDate, workOrderFilePath, newWorkCommDate);

            if (result > 0) {
                return ResponseEntity.ok("Estimate saved successfully");
            } else {
                return ResponseEntity.status(500).body("Failed to save estimate");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving estimate: " + e.getMessage());
        }
    }

    public static String convertDateFormat(String dateStr) {
        try {
            DateTimeFormatter oldFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter newFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate date = LocalDate.parse(dateStr, oldFormat);
            return date.format(newFormat);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + dateStr);
            return null;
        }
    }

    @GetMapping("/fetchERPdetails")
    public ResponseEntity<Map<String, Object>> getERPEstimateDetails(@RequestParam String estimateNo) {

        if (estimateNo == null || estimateNo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Estimate number cannot be empty"));
        }

        Map<String, Object> details = estimateService.getERPEstimateDetails(estimateNo);

        System.out.println("iconic....."+details.get("iconic_project"));
        details.put("status", "NonErp");

        return ResponseEntity.ok(details);
    }

    @GetMapping("/fetchnonerpdetails")
    public ResponseEntity<Map<String, Object>> getNonERPEstimateDetails(@RequestParam String estimateNo) {

        if (estimateNo == null || estimateNo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Estimate number cannot be empty"));
        }

        Map<String, Object> details = estimateService.getNonERPEstimateDetails(estimateNo);

        details.put("status", "NonErp");

        System.out.println("iconic....."+details.get("iconic_project"));

        return ResponseEntity.ok( details);
    }


    @GetMapping("/getSchemeList")
    public List<Map<String, Object>> getSchemeList() {
        return estimateService.getSchemeList();
    }

    @GetMapping("/getCategoryList")
    public List<Map<String, Object>> getCategoryList() {
        return estimateService.getCategoryList();
    }

    @GetMapping("/getSubCategoryList")
    public List<Map<String, Object>> getSubCategoryList() {
        return estimateService.getSubCategoryList();
    }

    @GetMapping("/getDepartmentList")
    public List<Map<String, Object>> getDepartmentList() {
        return estimateService.getDepartmentList();
    }

    @GetMapping("/getSourceofFundList")
    public List<Map<String, Object>> getSourceofFundList() {
        return estimateService.getSourceofFundList();
    }

    @PostMapping(value = "/updateEstimate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateEstimate(@RequestParam("estno") String estimateNo,
                                                 @RequestParam("estdate") String estimateDate, @RequestParam("zone") String zone,
                                                 @RequestParam("ward") String ward, @RequestParam("projectname") String projectName,
                                                 @RequestParam("location") String location, @RequestParam("department") String department,
                                                 @RequestParam(value = "nonErpIconicProject", required = false, defaultValue = "0") Integer iconicProject,
                                                 @RequestParam("contractorname") String contractorName,
                                                 @RequestParam("contractorperiod") String contractorPeriod, @RequestParam("fundsource") String fundSource,
                                                 @RequestParam("scheme") String scheme, @RequestParam("category") String category,
                                                 @RequestParam("subcategory") String subCategory, @RequestParam("estamount") String estAmount,
                                                 @RequestParam("techsanctiondate") String techSancDate,
                                                 @RequestParam("adminsanctiondate") String adminSancDate,
                                                 @RequestParam("tendercalldate") String tenderCallDate,
                                                 @RequestParam("tenderfinalizeddate") String tenderFinDate, @RequestParam("loadate") String loaDate,
                                                 @RequestParam("agreementdate") String agreementDate, @RequestParam("workorderdate") String workOrderDate,
                                                 @RequestParam("workcommenceddate") String workCommDate, @RequestParam("loafile") MultipartFile loaFile,
                                                 @RequestParam("agreementfile") MultipartFile agreementFile,
                                                 @RequestParam("workorderfile") MultipartFile workOrderFile,
                                                 @RequestParam("adminsanctionfile") MultipartFile adminSanFile,
                                                 @RequestParam(value = "reason", required = false) String reason,
                                                 @RequestParam(value = "remarks", required = false) String remarks,
                                                 @RequestParam(value = "projecttype", required = false) String projecttype,
                                         		@RequestParam(value = "govtfund", required = false) String govtfund,
                                         		@RequestParam(value = "specialcat", required = false) String specialcat,
                                         		@RequestParam(value = "filenumber", required = false) String filenumber) {
        try {

            // ✅ Check if it is a Non-ERP estimate
            boolean isNonERP = estimateNo.startsWith("M");

            // ✅ Ensure iconic_project is updated only for Non-ERP estimates
            if (!isNonERP) {
                iconicProject = null; // Don't update for ERP
            }

            System.out.println("adminsacfile:" + adminSanFile);
            System.out.println("loa:" + loaFile);
            System.out.println("agreementfile:" + agreementFile);
            System.out.println("workfile : " + workOrderFile);
            System.out.println("iconicProject..."+iconicProject);

            // Create a mutable HashMap to store parameters
            Map<String, Object> updatedDetails = new HashMap<>();
            updatedDetails.put("estimate_no", estimateNo);

            // Directly convert estimateDate here
            updatedDetails.put("estimate_date", convertDateFormat(estimateDate));
            updatedDetails.put("zone", zone);
            updatedDetails.put("ward", ward);
            updatedDetails.put("project_name", projectName);
            updatedDetails.put("location", location);
            updatedDetails.put("iconic_project", iconicProject);
            updatedDetails.put("department", department);
            updatedDetails.put("contractor_name", contractorName);
            updatedDetails.put("contractor_period", contractorPeriod);
            updatedDetails.put("fund_source", fundSource);
            updatedDetails.put("scheme", scheme);
            updatedDetails.put("category", category);
            updatedDetails.put("sub_category", subCategory);
            updatedDetails.put("est_amount", estAmount);
            updatedDetails.put("tech_sanc_date", convertDateFormat(techSancDate));
            updatedDetails.put("admin_sanc_date", convertDateFormat(adminSancDate));
            updatedDetails.put("tender_call_date", convertDateFormat(tenderCallDate));
            updatedDetails.put("tender_fin_date", convertDateFormat(tenderFinDate));
            updatedDetails.put("loa_date", convertDateFormat(loaDate));
            updatedDetails.put("agreement_date", convertDateFormat(agreementDate));
            updatedDetails.put("work_order_date", convertDateFormat(workOrderDate));
            updatedDetails.put("work_comm_date", convertDateFormat(workCommDate));
            updatedDetails.put("reason", reason);
            updatedDetails.put("remarks", remarks);
            updatedDetails.put("projecttype", projecttype);
            updatedDetails.put("govtfund", govtfund);
            updatedDetails.put("specialcat", specialcat);
            updatedDetails.put("filenumber", filenumber);

            // ✅ Only update iconic_project for Non-ERP
            if (isNonERP) {
                updatedDetails.put("iconic_project", iconicProject);
            }

            String SanFilePath = estimateService.saveFile(adminSanFile, "sanction","nonerp");
            String loaFilePath = estimateService.saveFile(loaFile, "loa","nonerp");
            String agreementFilePath = estimateService.saveFile(agreementFile, "agreement","nonerp");
            String workOrderFilePath = estimateService.saveFile(workOrderFile, "workorder","nonerp");

            // Call the service layer to update the estimate details
            int isUpdated = estimateService.updateNonERPEstimateDetails(updatedDetails, SanFilePath, loaFilePath,
                    agreementFilePath, workOrderFilePath);

            if (isUpdated > 0) {
                return ResponseEntity.ok("Estimate details updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update estimate.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating estimate: " + e.getMessage());
        }
    }

    // to update ERP works Detail from erp_works table
    @PostMapping(value = "/updateERPEstimate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateEstimatelist(@RequestParam("erpestno") String estimateNo,
                                                     @RequestParam("erpestdate") String estimateDate, @RequestParam("erpzone") String zone,
                                                     @RequestParam("erpward") String ward, @RequestParam("erpprojectname") String projectName,
                                                     @RequestParam("erplocation") String location, @RequestParam("erpdepartment") String department,
                                                     @RequestParam(value = "iconicProject", required = false, defaultValue = "No") String iconicProjectStr,
                                                     @RequestParam("erpcontractorname") String contractorName,
                                                     @RequestParam("erpcontractorperiod") String contractorPeriod, @RequestParam("erpfundsource") String fundSource,
                                                     @RequestParam("erpscheme") String scheme, @RequestParam("erpcategory") String category,
                                                     @RequestParam("erpsubcategory") String subCategory, @RequestParam("erpestamount") String estAmount,
                                                     @RequestParam("erptechsanctiondate") String techSancDate,
                                                     @RequestParam("erpadminsanctiondate") String adminSancDate,
                                                     @RequestParam("erptendercalldate") String tenderCallDate,
                                                     @RequestParam("erptenderfinalizeddate") String tenderFinDate, @RequestParam("erploadate") String loaDate,
                                                     @RequestParam("erpagreementdate") String agreementDate, @RequestParam("erpworkorderdate") String workOrderDate,
                                                     @RequestParam("erpworkcommenceddate") String workCommDate, @RequestParam("erploafile") MultipartFile loaFile,
                                                     @RequestParam("erpagreementfile") MultipartFile agreementFile,
                                                     @RequestParam("erpworkorderfile") MultipartFile workOrderFile,
                                                     @RequestParam("erpadminsanctionfile") MultipartFile adminSanFile) {
        try {
            System.out.println("controller called");
            boolean iconicProject = "Yes".equalsIgnoreCase(iconicProjectStr);
            System.out.println("adminsacfile:" + adminSanFile);
            System.out.println("loa:" + loaFile);
            System.out.println("agreementfile:" + agreementFile);
            System.out.println("workfile : " + workOrderFile);


            // Create a mutable HashMap to store parameters
            Map<String, Object> updatedDetails = new HashMap<>();

            updatedDetails.put("estimate_no", estimateNo);

            // Directly convert estimateDate here
            updatedDetails.put("estimate_date", convertDateFormat(estimateDate));
            updatedDetails.put("zone", zone);
            updatedDetails.put("ward", ward);
            updatedDetails.put("project_name", projectName);
            updatedDetails.put("location", location);
            updatedDetails.put("iconic_project", iconicProject);
            updatedDetails.put("department", department);
            updatedDetails.put("contractor_name", contractorName);
            updatedDetails.put("contractor_period", contractorPeriod);
            updatedDetails.put("fund_source", fundSource);
            updatedDetails.put("scheme", scheme);
            updatedDetails.put("category", category);
            updatedDetails.put("sub_category", subCategory);
            updatedDetails.put("est_amount", estAmount);
            updatedDetails.put("tech_sanc_date", convertDateFormat(techSancDate));
            updatedDetails.put("admin_sanc_date", convertDateFormat(adminSancDate));
            updatedDetails.put("tender_call_date", convertDateFormat(tenderCallDate));
            updatedDetails.put("tender_fin_date", convertDateFormat(tenderFinDate));
            updatedDetails.put("loa_date", convertDateFormat(loaDate));
            updatedDetails.put("agreement_date", convertDateFormat(agreementDate));
            updatedDetails.put("work_order_date", convertDateFormat(workOrderDate));
            updatedDetails.put("work_comm_date", convertDateFormat(workCommDate));

            String SanFilePath = estimateService.saveFile(adminSanFile, "sanction","erp");
            String loaFilePath = estimateService.saveFile(loaFile, "loa","erp");
            String agreementFilePath = estimateService.saveFile(agreementFile, "agreement","erp");
            String workOrderFilePath = estimateService.saveFile(workOrderFile, "workorder","erp");

            // Call the service layer to update the estimate details
            int isUpdated = estimateService.updateEstimateDetails(updatedDetails, SanFilePath, loaFilePath,
                    agreementFilePath, workOrderFilePath);

            if (isUpdated > 0) {
                return ResponseEntity.ok("Estimate details updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update estimate.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating estimate: " + e.getMessage());
        }
    }

    @GetMapping("/getFilteredWorks")
    @ResponseBody
    public List<Map<String, Object>> getFilteredWorks(
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String ward,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String fundsource,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) String estno) {
        System.out.println("estno: " + estno);

        return estimateService.getFilteredWorksList(zone, ward, department, fundsource, fromDate, toDate, filterType, estno);
    }
    
    
    //ak
    @GetMapping("/getFilteredERpWorksList")
    @ResponseBody
    public List<Map<String, Object>> getFilteredERpWorksList(
    		@RequestParam(required = false) String loginId,
            @RequestParam(required = false) String finYear,
            @RequestParam(required = false) String oValue) {
        //System.out.println("estno: " + estno);
        //System.out.println("check");

        // Fetch all filtered works
        List<Map<String, Object>> filteredWorks = erpEstimateService.getFilteredERpWorksList(loginId,finYear,oValue);

        return filteredWorks;
    }
    
    @GetMapping("/getFilteredERpWorksListByLogin")
    @ResponseBody
    public List<Map<String, Object>> getFilteredERpWorksListByLogin(
    		@RequestParam(required = false) String loginId,
            @RequestParam(required = false) String finYear,
            @RequestParam(required = false) String oValue) {
        //System.out.println("estno: " + estno);
        //System.out.println("check");

        // Fetch all filtered works
        List<Map<String, Object>> filteredWorks = erpEstimateService.getFilteredERpWorksListByLogin(loginId,finYear,oValue);

        return filteredWorks;
    }
    
    @GetMapping("/getFilteredNonERpWorksListByLogin")
    @ResponseBody
    public List<Map<String, Object>> getFilteredNonERpWorksListByLogin(
    		@RequestParam(required = false) String loginId,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String ward,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String fundsource,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) String estno) {
        // Fetch all filtered works
        List<Map<String, Object>> filteredWorks = estimateService.getAllNonERPWorks(loginId);

        return filteredWorks;
    }
    
    /*@GetMapping("/fetchdetailsWorklist")
    public ResponseEntity<Map<String, Object>> fetchdetailsWorklist(@RequestParam String estimateNo) {
    	System.out.println("abs_est_number");

        if (estimateNo == null || estimateNo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Estimate number cannot be empty"));
        }

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> details = new HashMap<>();
        details = estimateService.getEstimateDetails(estimateNo);


        if (estimateNo.startsWith("M")) {
            if (details == null) {
                details = new HashMap<>();
            }
            // fetch from local DB
            details.put("status", "NonErp");
        } else {
            response.put("status", "Erp");
            Map<String, Object> erpWorksDetails = erpEstimateService.getERPWorksDetailslist(estimateNo); // fetch from ERP

            if (erpWorksDetails != null && !erpWorksDetails.isEmpty()) {
            
                response.put("estimate_no", erpWorksDetails.getOrDefault("abs_est_number", ""));
                response.put("estimate_date", erpWorksDetails.getOrDefault("asdate", ""));
                response.put("zone", erpWorksDetails.getOrDefault("zone", ""));
               
                response.put("ward", erpWorksDetails.getOrDefault("wardname", ""));
                response.put("department", erpWorksDetails.getOrDefault("zonename", ""));
                
                
                response.put("project_name", erpWorksDetails.getOrDefault("nameofwork", ""));
                response.put("location", erpWorksDetails.getOrDefault("location", ""));
                response.put("contractor_period", erpWorksDetails.getOrDefault("agreementperiod", ""));
                response.put("scheme", erpWorksDetails.getOrDefault("schemename", ""));
                response.put("category", erpWorksDetails.getOrDefault("workcategory", ""));
                response.put("sub_category", erpWorksDetails.getOrDefault("typeofwork", ""));
                response.put("est_amount", erpWorksDetails.getOrDefault("valueofwork", ""));
            }

            if (details != null && !details.isEmpty()) {
                response.put("tech_sanc_date", details.get("tech_sanc_date"));
                response.put("admin_sanc_date", details.get("admin_sanc_date"));
                response.put("tender_fin_date", details.get("tender_fin_date"));
                response.put("tender_call_date", details.get("tender_call_date"));
                response.put("loa_date", details.get("loa_date"));
                response.put("agreement_date", details.get("agreement_date"));
                response.put("work_order_date", details.get("work_order_date"));
                response.put("work_comm_date", details.get("work_comm_date"));
                response.put("adm_san_file", details.get("adm_san_file"));
                response.put("loa_file", details.get("loa_file"));
                response.put("agreement_file", details.get("agreement_file"));
                response.put("work_order_file", details.get("work_order_file"));
            }
        }


        if(!details.isEmpty()){
            System.out.println("status2: "+details.get("status"));
        } else{
            System.out.println("status1: "+response.get("status"));
        }


        return ResponseEntity.ok(!details.isEmpty() ? details : response);
    }*/
    
    
    @GetMapping("/getERPWorksDetailsList")
    public ResponseEntity<Map<String, Object>> getERPWorksDetailsList(@RequestParam("estimateNo") String estimateNo) {
        
        System.out.println("ERP estNo: " + estimateNo);

        Map<String, Object> erpWorksDetails = erpEstimateService.getERPWorksDetails(estimateNo); // Fetch details from ERP

        if (erpWorksDetails.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message", "No Data Found"));
        }

        System.out.println("erpWorksDetails.. "+erpWorksDetails);
        return ResponseEntity.ok(erpWorksDetails);
    }


    //show the model details by using estimate number

    @GetMapping("/getestimatedetails")
    public List<Map<String, Object>> getEmployeedetailsbyCode(
            @RequestParam String estimateCode) {

        List<Map<String, Object>> estimateDetails = estimateService.fetchEstimateByCode(estimateCode);
        System.out.println("estimateDetails==="+estimateDetails);
        return estimateDetails;
    }

    //save the work details mapping

    @PostMapping("/saveworkdetails")
    public ResponseEntity<?> saveWorkDetails(@RequestBody List<Map<String, Object>> workDetails) {
        List<Map<String, Object>> savedDetails = estimateService.saveWorkDetailsbycode(workDetails);
        return ResponseEntity.ok(savedDetails);
    }

    @GetMapping("/reasonlist")
    public ResponseEntity<List<Map<String, Object>>> getAllReasons() {
        List<Map<String, Object>> reasons = estimateService.getAllReasons();
        return ResponseEntity.ok(reasons);
    }

    @GetMapping("/getestimateworkdetails")
    public List<Map<String, Object>> getestimateworkdetails(
            @RequestParam String estimateCode) {

        List<Map<String, Object>> estimateDetails = estimateService.getestimateworkdetails(estimateCode);
        System.out.println("estimateDetails==="+estimateDetails);
        return estimateDetails;
    }

    // Newly added for dynamic questions and answers
    @GetMapping("/questions")
    public ResponseEntity<List<Map<String, Object>>> getQuestionsBySubCatId(@RequestParam("subCatId") int subCatId) {
        List<Map<String, Object>> questions = estimateService.getQuestionsBySubCatId(subCatId);
        return ResponseEntity.ok(questions);
    }


    //get the subcategory id by name

    @GetMapping("/subcategory/id")
    public ResponseEntity<?> getSubCategoryId(@RequestParam("name") String name) {
        Integer subCatId = estimateService.getSubCategoryIdByName(name);
        if (subCatId != null) {
            return ResponseEntity.ok(Map.of("subCatId", subCatId));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sub-Category not found");
        }
    }

    // added by sanjay

    @GetMapping("/answerIdd")
    public ResponseEntity<List<Map<String, Object>>> getAnswerByIdd(@RequestParam("answerId") int answerId) {
        System.out.println("answerID.." + answerId);

        // Initialize the list properly
        List<Map<String,Object>> newList = new ArrayList<>();

        Map<String, Object> questionDetails = estimateService.getQuestionDetailsById(answerId);

        if (questionDetails != null && questionDetails.get("id") != null) {
            String quesId = questionDetails.get("id").toString();
            System.out.println("quesId: " + quesId);

            List<Map<String,Object>> answerDetails = estimateService.getAnsDetailsByQId(quesId);

            newList.add(questionDetails);
            if (answerDetails != null) {
                newList.addAll(answerDetails);
            }
        }

        return ResponseEntity.ok(newList);
    }

    @PostMapping("/saveErplist")
    public ResponseEntity<String> saveAnswers(@RequestBody List<Map<String, Object>> answers) {
        estimateService.saveAnswers(answers);

        System.out.println("answers"+answers);

        return ResponseEntity.ok("Answers saved successfully!");
    }

    @GetMapping("/fetchErpData")
    public ResponseEntity<List<Map<String, Object>>> fetchEstimateData(@RequestParam String estimateNumber) {
        System.out.println("I'm in fetch controller...");
        List<Map<String, Object>> savedData = estimateService.getSavedData(estimateNumber);
        System.out.println("savedData..."+savedData );
        return ResponseEntity.ok(savedData);
    }

    
    //// Balaji
    @GetMapping("/getestimateworkdetailsById")
    public List<Map<String, Object>> getestimateworkdetailsById(
            @RequestParam String estid) {

        List<Map<String, Object>> estimateDetails = estimateService.getestimateworkdetailsById(estid);
        System.out.println("estid==="+estid);
        return estimateDetails;
    }
    
    @PostMapping("/saveEstimateWorkDetails")
    public ResponseEntity<String> saveAnswers(
    		@RequestParam(value = "loginid", required = false) String loginid,
    		@RequestParam(value = "estid", required = true) String estid,
    		@RequestParam(value = "techSanctionDate", required = false) String techSanctionDate,
    		@RequestParam(value = "adminSanctionDate", required = false) String adminSanctionDate,
    		@RequestParam(value = "tenderCallDate", required = false) String tenderCallDate,
    		@RequestParam(value = "tenderFinalizedDate", required = false) String tenderFinalizedDate,
    		@RequestParam(value = "loaDate", required = false) String loaDate,
    		@RequestParam(value = "agreementDate", required = false) String agreementDate,
    		@RequestParam(value = "workOrderDate", required = false) String workOrderDate,
    		@RequestParam(value = "workCommencedDate", required = false) String workCommencedDate,
    		@RequestParam(value = "isiconic", required = true) String isiconic,
    		@RequestParam(value = "admin_sanction_file", required = true) MultipartFile admin_sanction_file,
    		@RequestParam(value = "loa_file", required = false) MultipartFile loa_file,
    		@RequestParam(value = "agreement_file", required = false) MultipartFile agreement_file,
    		@RequestParam(value = "work_order_file", required = false) MultipartFile work_order_file,
    		@RequestParam(value = "projecttype", required = false) String projecttype,
    		@RequestParam(value = "govtfund", required = false) String govtfund,
    		@RequestParam(value = "specialcat", required = false) String specialcat
    		) {
        estimateService.saveEstimateWorkDetails(loginid, estid, isiconic, admin_sanction_file, loa_file, agreement_file, work_order_file,
        		techSanctionDate,adminSanctionDate,tenderCallDate,tenderFinalizedDate,
        		loaDate,agreementDate,workOrderDate,workCommencedDate,
        		projecttype,govtfund,specialcat);
        return ResponseEntity.ok("success");
    }
    
    @PostMapping("/updateEstimateWorkDetails")
    public ResponseEntity<String> saveUpdateAnswers(
    		@RequestParam(value = "loginid", required = false) String loginid,
    		@RequestParam(value = "estid", required = true) String estid,
    		@RequestParam(value = "techSanctionDate", required = false) String techSanctionDate,
    		@RequestParam(value = "adminSanctionDate", required = false) String adminSanctionDate,
    		@RequestParam(value = "tenderCallDate", required = false) String tenderCallDate,
    		@RequestParam(value = "tenderFinalizedDate", required = false) String tenderFinalizedDate,
    		@RequestParam(value = "loaDate", required = false) String loaDate,
    		@RequestParam(value = "agreementDate", required = false) String agreementDate,
    		@RequestParam(value = "workOrderDate", required = false) String workOrderDate,
    		@RequestParam(value = "workCommencedDate", required = false) String workCommencedDate,
    		@RequestParam(value = "isiconic", required = true) String isiconic,
    		@RequestParam(value = "admin_sanction_file", required = false) MultipartFile admin_sanction_file,
    		@RequestParam(value = "loa_file", required = false) MultipartFile loa_file,
    		@RequestParam(value = "agreement_file", required = false) MultipartFile agreement_file,
    		@RequestParam(value = "work_order_file", required = false) MultipartFile work_order_file,
    		@RequestParam(value = "projecttype", required = false) String projecttype,
    		@RequestParam(value = "govtfund", required = false) String govtfund,
    		@RequestParam(value = "specialcat", required = false) String specialcat
    		) {
        estimateService.updateEstimateWorkDetails(loginid, estid, isiconic, admin_sanction_file, loa_file, agreement_file, work_order_file,
        		techSanctionDate,adminSanctionDate,tenderCallDate,tenderFinalizedDate,
        		loaDate,agreementDate,workOrderDate,workCommencedDate,
        		projecttype,govtfund,specialcat);
        return ResponseEntity.ok("success");
    }
    
    @GetMapping("/getUserEstWorkDetailsById")
    public List<Map<String, Object>> getUserEstWorkDetailsById(
            @RequestParam String estid) {

        List<Map<String, Object>> estimateDetails = estimateService.getUserEstWorkDetailsById(estid);
        System.out.println("estid==="+estid);
        return estimateDetails;
    }
    
    @GetMapping("/getUserEstWorkStageDetailsByEstid")
    public List<Map<String, Object>> getUserEstWorkStageDetailsByEstid(
            @RequestParam String estid) {

        List<Map<String, Object>> estimateDetails = estimateService.getUserEstWorkStageDetailsByEstid(estid);
        System.out.println("estid==="+estid);
        return estimateDetails;
    }
    
 // to save non ERP works
    @PostMapping(value = "/nonerpForm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitForm(
            @RequestParam("estdate") String estimateDate,
            @RequestParam("zone") String zone,
            @RequestParam("ward") String ward,
            @RequestParam("projectname") String projectName,
            @RequestParam("location") String location,
            @RequestParam("department") String department,
            @RequestParam("contractorname") String contractorName,
            @RequestParam("contractorperiod") String contractorPeriod,
            @RequestParam("fundsource") String fundSource,
            @RequestParam("scheme") String scheme,
            @RequestParam("category") String category,
            @RequestParam("subcategory") String subCategory,
            @RequestParam("estamount") String estAmount,
            @RequestParam("techsanctiondate") String techSancDate,
            @RequestParam("adminsanctiondate") String adminSancDate,
            @RequestParam("tendercalldate") String tenderCallDate,
            @RequestParam("tenderfinalizeddate") String tenderFinDate,
            @RequestParam("loadate") String loaDate,
            @RequestParam("agreementdate") String agreementDate,
            @RequestParam("workorderdate") String workOrderDate,
            @RequestParam("workcommenceddate") String workCommDate,
            @RequestParam("loafile") MultipartFile loacopy,
            @RequestParam("agreementfile") MultipartFile agreementcopy,
            @RequestParam("workorderfile") MultipartFile workordercopy,
            @RequestParam("adminsanctionfile") MultipartFile sanctioncopy,
            @RequestParam("iconicProject") Integer iconicProjectInt,
            @RequestParam("reason") String reason,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam(value = "loginid", required = false) String loginid,
            @RequestParam(value = "projecttype", required = false) String projecttype,
    		@RequestParam(value = "govtfund", required = false) String govtfund,
    		@RequestParam(value = "specialcat", required = false) String specialcat,
    		@RequestParam(value = "filenumber", required = false) String filenumber) {  // ✅ Make remarks optional

        Boolean iconicProject = (iconicProjectInt != null && iconicProjectInt == 1);
        Map<String, String> response = new HashMap<>();


        // Ensure remarks is NULL when reason is not "Other"
        if (!"Other".equals(reason)) {
            remarks = null;
        }

        try {
            // ✅ Generate Estimate Number
            int currentYear = LocalDate.now().getYear();
            int previousYear = currentYear - 1;
            String suffixYear = String.valueOf(currentYear).substring(2);
            String prefix = department.substring(0, 1);
            System.out.println("prefix: " + prefix);
            
            int currentMonth= LocalDate.now().getMonthValue();
            if(currentMonth>3) {
            	previousYear = currentYear;
            	int suffixYearint = currentYear + 1;
            	suffixYear = String.valueOf(suffixYearint).substring(2);
            }
            
            // Retrieve sequence from DB
            //String sequenceStr = estimateService.getSequence();
            //int sequence = Integer.parseInt(sequenceStr) + 1;
            //String estNo = "M" + prefix + "/" + previousYear + "-" + suffixYear + "/" + sequence;
            //System.out.println("estNo: " + estNo);
            String estNo = "M" + prefix + "/" + previousYear + "-" + suffixYear + "/";

            // ✅ Save File Paths
            String SanFilePath = estimateService.saveFile(sanctioncopy, "sanction","nonerp");
            String loaFilePath = estimateService.saveFile(loacopy, "loa","nonerp");
            String agreementFilePath = estimateService.saveFile(agreementcopy, "agreement","nonerp");
            String workOrderFilePath = estimateService.saveFile(workordercopy, "workorder","nonerp");

            // ✅ Convert Dates
            String newestDate = estimateService.convertDateFormat(estimateDate);
            String newtechSancDate = estimateService.convertDateFormat(techSancDate);
            String newadminSancDate = estimateService.convertDateFormat(adminSancDate);
            String newtenderCallDate = estimateService.convertDateFormat(tenderCallDate);
            String newtenderFinDate = estimateService.convertDateFormat(tenderFinDate);
            String newloaDate = estimateService.convertDateFormat(loaDate);
            String newagreementDate = estimateService.convertDateFormat(agreementDate);
            String newworkOrderDate = estimateService.convertDateFormat(workOrderDate);
            String newworkCommDate = estimateService.convertDateFormat(workCommDate);

            // ✅ Fix: If reason is "Insufficient Fund", set remarks to NULL
            if (!"Other".equals(reason)) {
                remarks = null;
            }

            // ✅ Save to DB
            String result = estimateService.processFormData(estNo, newestDate, zone, ward, iconicProject, projectName, location,
                    department, contractorName, contractorPeriod, fundSource, scheme, category, subCategory, estAmount,
                    newtechSancDate, newadminSancDate, SanFilePath, newtenderCallDate, newtenderFinDate, newloaDate,
                    loaFilePath, newagreementDate, agreementFilePath, newworkOrderDate, workOrderFilePath, newworkCommDate, reason, remarks, loginid,
                    projecttype, govtfund, specialcat, filenumber);

            if (!result.equals("error")) {
                //estimateService.updateSequence(String.valueOf(result));
                response.put("message", "Estimate saved successfully");
                response.put("estNo", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Failed to save estimate");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Error saving estimate: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    //getProjectList
    @GetMapping("/getProjectList")
    public ResponseEntity<List<Map<String, Object>>> getProjectList() {
        List<Map<String, Object>> reasons = estimateService.getProjectList();
        return ResponseEntity.ok(reasons);
    }
    @GetMapping("/getProjectUpdatedList")
    public ResponseEntity<List<Map<String, Object>>> getProjectUpdatedList(@RequestParam(value = "loginid", required = false) String loginid) {
        List<Map<String, Object>> reasons = estimateService.getProjectUpdatedList(loginid);
        return ResponseEntity.ok(reasons);
    }
    
    
    @PostMapping(value = "/saveProjectStatus", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveProjectStatus(
            @RequestParam("pid") String pid,
            @RequestParam("status") String status,
            @RequestParam("loginid") String loginid,
            @RequestParam(value = "projectfile", required = false) MultipartFile projectfile
            ) {  // ✅ Make remarks optional

        Map<String, String> response = new HashMap<>();

        try {
            
            // ✅ Save File Paths
            String file = estimateService.saveFile(projectfile, pid, "projectStatus");

            // ✅ Save to DB
            int result = estimateService.saveProjectData(pid, status, file, loginid);

            if (result>0) {
                //estimateService.updateSequence(String.valueOf(result));
                response.put("message", "Project status saved successfully");
                //response.put("estNo", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Failed to save project status");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Error saving project status: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
