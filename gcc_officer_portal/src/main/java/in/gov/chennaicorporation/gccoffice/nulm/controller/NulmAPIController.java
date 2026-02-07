package in.gov.chennaicorporation.gccoffice.nulm.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.nulm.service.NulmService;

@RequestMapping("/gcc/api/nulm")
@RestController("nulmAPI")
public class NulmAPIController {

    private final Environment environment;

    @Autowired
    private NulmService nulmService;

    @Autowired
    private JdbcTemplate nulmJdbcTemplate;

    @Autowired
    public NulmAPIController(NulmService nulmService, JdbcTemplate nulmJdbcTemplate, Environment environment) {
        this.nulmService = nulmService;
        this.nulmJdbcTemplate = nulmJdbcTemplate;
        this.environment = environment;
    }

    @Autowired
    public void setDataSource(@Qualifier("mysqlNulmDataSource") DataSource dataSource) {
        this.nulmJdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping("/getYear")
    public ResponseEntity<List<Map<String, Object>>> getYear() {
        List<Map<String, Object>> yearList = nulmService.getYearList();
        return ResponseEntity.ok(yearList);
    }

    @GetMapping("/getMonth")
    public ResponseEntity<List<Map<String, Object>>> getMonth(int year) {
        // System.out.println("year: "+year);
        List<Map<String, Object>> monthList = nulmService.getMonthList(year);
        return ResponseEntity.ok(monthList);
    }

    @GetMapping("/getGender")
    public ResponseEntity<List<Map<String, Object>>> getGender() {

        List<Map<String, Object>> genderList = nulmService.getGenderList();
        return ResponseEntity.ok(genderList);
    }

    @GetMapping("/getDivisionList")
    public ResponseEntity<List<Map<String, Object>>> getDivisionList(@RequestParam(required = false) String zone) {
        List<Map<String, Object>> divisionList = nulmService.getDivisionList(zone);
        return ResponseEntity.ok(divisionList);
    }

    @PostMapping("/saveGroup")
    public ResponseEntity<String> saveSchemeGroup(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> groupData = new HashMap<>(payload);
            nulmService.saveSchemeGroup(groupData);
            return ResponseEntity.ok("Self Help Group Saved Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving Scheme Group: " + e.getMessage());
        }
    }

    /*
     * @PostMapping("/saveOrder")
     * public ResponseEntity<String> saveOrderDetails(@RequestBody Map<String,
     * Object> payload) {
     * try {
     * Map<String, Object> orderData = new HashMap<>(payload);
     * nulmService.saveOrderDetails(orderData);
     * return ResponseEntity.ok("Order saved successfully.");
     * } catch (IllegalArgumentException e) {
     * return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
     * } catch (Exception e) {
     * return ResponseEntity.status(500).body("Error saving order: " +
     * e.getMessage());
     * }
     * }
     */
    @PostMapping("/saveStaff")
    public ResponseEntity<String> saveStaffDetails(@RequestBody Map<String, Object> payload) {
        try {
            nulmService.saveStaffDetails(payload);
            return ResponseEntity.ok("Staff details saved successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving staff details: " + e.getMessage());
        }
    }

    @PostMapping("/assignAppointment")
    public ResponseEntity<String> assignOrderToEnrollments(@RequestBody Map<String, Object> requestBody) {
        int orderId = (int) requestBody.get("orderId");
        String inchargePhoneno = ((String) requestBody.get("incharge_phoneno"));
        String designation = (String) requestBody.get("designation");
        List<Integer> enrollmentIds = (List<Integer>) requestBody.get("enrollmentIds");

        // Step 1: Retrieve the selected order
        String getOrderSql = "SELECT * FROM order_details WHERE order_id = ?";
        Map<String, Object> orderDetails;

        try {
            orderDetails = nulmJdbcTemplate.queryForMap(getOrderSql, orderId);
        } catch (EmptyResultDataAccessException e) {
            // Handle the case where no order is found
            return new ResponseEntity<>("No order found with order_id: " + orderId, HttpStatus.NOT_FOUND);
        }

        if (orderDetails != null) {
            // Step 2: Build the SQL for updating enrollments with dynamic IN clause
            StringBuilder updateSql = new StringBuilder(
                    "UPDATE enrollment_table SET order_id = ?, appointed = 'Yes', facial_attendance = 'No', incharge_phoneno = ?, designation = ?, appointed_date = CURRENT_DATE WHERE enrollment_id IN (");

            // Add placeholders for the IN clause based on the size of the enrollmentIds
            // list
            for (int i = 0; i < enrollmentIds.size(); i++) {
                updateSql.append("?");
                if (i < enrollmentIds.size() - 1) {
                    updateSql.append(", ");
                }
            }
            updateSql.append(")");

            // Combine the orderId, inchargePhoneno, designation, and the list of
            // enrollmentIds
            List<Object> parameters = new ArrayList<>();
            parameters.add(orderId);
            parameters.add(inchargePhoneno);
            parameters.add(designation);
            parameters.addAll(enrollmentIds);

            // Execute the update query
            try {
                nulmJdbcTemplate.update(updateSql.toString(), parameters.toArray());
                return new ResponseEntity<>("Order assigned to enrollments and appointed successfully.", HttpStatus.OK);
            } catch (DataAccessException e) {
                // Handle any data access exceptions
                return new ResponseEntity<>("Error updating enrollments: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>("Unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/assignDropout")
    public ResponseEntity<String> assignDropout(@RequestBody Map<String, Object> requestBody) {
        int orderId = (int) requestBody.get("orderId");
        List<Integer> enrollmentIds = (List<Integer>) requestBody.get("enrollmentIds");

        // Step 1: Retrieve the selected order
        String getOrderSql = "SELECT * FROM order_details WHERE order_id = ?";
        Map<String, Object> orderDetails;

        try {
            orderDetails = nulmJdbcTemplate.queryForMap(getOrderSql, orderId);
        } catch (EmptyResultDataAccessException e) {
            // Handle the case where no order is found
            return new ResponseEntity<>("No order found with order_id: " + orderId, HttpStatus.NOT_FOUND);
        }

        if (orderDetails != null) {
            // Step 2: Build the SQL for updating enrollments with dynamic IN clause
            StringBuilder updateSql = new StringBuilder(
                    "UPDATE enrollment_table SET appointed = 'Drop', dropout = 'Yes', dropout_date = CURRENT_DATE WHERE enrollment_id IN (");

            // Add placeholders for the IN clause based on the size of the enrollmentIds
            // list
            for (int i = 0; i < enrollmentIds.size(); i++) {
                updateSql.append("?");
                if (i < enrollmentIds.size() - 1) {
                    updateSql.append(", ");
                }
            }
            updateSql.append(") AND order_id = ?"); // Match the order_id in the WHERE clause

            // Combine the list of enrollmentIds and the orderId
            List<Object> parameters = new ArrayList<>(enrollmentIds);
            parameters.add(orderId); // Add orderId at the end of the parameters list

            // Execute the update query
            try {
                int rowsAffected = nulmJdbcTemplate.update(updateSql.toString(), parameters.toArray());
                if (rowsAffected > 0) {
                    return new ResponseEntity<>("Order assigned to enrollments has been Dropout successfully.",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("No matching records found to update.", HttpStatus.NOT_FOUND);
                }
            } catch (DataAccessException e) {
                // Handle any data access exceptions
                return new ResponseEntity<>("Error updating enrollments: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>("Unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/getAllSchemeGroupNames")
    public ResponseEntity<List<Map<String, Object>>> getAllSchemeGroupNames() {
        try {
            List<Map<String, Object>> schemeGroupNames = nulmService.getAllSchemeGroupNames();
            return ResponseEntity.ok(schemeGroupNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/getAllOrderNumber")
    public ResponseEntity<List<String>> getAllOrderNumber() {
        try {
            List<String> schemeGroupNames = nulmService.getAllOrderNumber();
            return ResponseEntity.ok(schemeGroupNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/getAllSchemeGroups")
    public ResponseEntity<List<Map<String, Object>>> getAllSchemeGroups() {
        try {
            List<Map<String, Object>> groups = nulmService.getAllSchemeGroups();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /////// get all list of orders////////////////////////////
    @GetMapping("/getAllOrders")
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        try {
            List<Map<String, Object>> groups = nulmService.getAllOrders();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getAllStaffNames")
    public ResponseEntity<List<String>> getAllStaffNames() {
        try {
            List<String> schemeGroupNames = nulmService.getStaffName();
            return ResponseEntity.ok(schemeGroupNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/appointedCounts")
    public ResponseEntity<Map<String, Integer>> getAppointedCounts(
            @RequestParam(required = false) Integer groupId) {

        try {
            // Validate that groupId is provided
            if (groupId == null) {
                return ResponseEntity.badRequest().body(null); // You can also return a specific error message
            }

            // Call the service method to get the counts
            Map<String, Integer> counts = nulmService.getAppointedCounts(groupId);

            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // You can also return the error
                                                                                       // message
        }
    }

    @PostMapping("/markAbsent")
    public ResponseEntity<String> markAbsent(@RequestParam int employeeId) {
        int result = nulmService.markAbsentEntry(employeeId);

        if (result > 0) {
            return ResponseEntity.ok("Employee marked as absent successfully.");
        } else {
            return ResponseEntity.status(304)
                    .body("No update made. Employee may already be marked as absent or has attended.");
        }
    }

    @PostMapping("/markOverDuty")
    public ResponseEntity<String> markOverDuty(@RequestParam int employeeId) {
        int result = nulmService.markOverDuty(employeeId);

        if (result > 0) {
            return ResponseEntity.ok("Over Duty marked successfully.");
        } else {
            return ResponseEntity.status(304).body("No update made. Employee ID may not exist.");
        }
    }

    @PostMapping("/markAttendance")
    public ResponseEntity<String> markAttendance(
            @RequestParam int employeeId,
            @RequestParam String markAttendance) {
        try {
            String result = nulmService.markAttendance(employeeId, markAttendance);
            if ("Checked in successfully".equals(result) || "Checked out successfully".equals(result)) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to mark attendance");
        }
    }

    @GetMapping("/getAllOrderDetails")
    public ResponseEntity<List<Map<String, Object>>> getAllOrderDetails() {
        try {
            List<Map<String, Object>> orderDetails = nulmService.getAllOrderDetails();
            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/enrollmentCountsbyorderId")
    public ResponseEntity<Map<String, Integer>> getEnrollmentCounts(@RequestParam int orderId) {
        Map<String, Integer> counts = nulmService.getAppointedCounts(orderId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/dropOutCounts")
    public ResponseEntity<Map<String, Integer>> getDropCounts(@RequestParam int orderId) {
        Map<String, Integer> counts = nulmService.getDropCounts(orderId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/enrollmentCountsbygroupId")
    public ResponseEntity<List<Map<String, Object>>> getEnrollmentbygroupId(@RequestParam int groupId) {
        List<Map<String, Object>> enrollmentDetails = nulmService.getEnrollmentDetails(groupId);
        return ResponseEntity.ok(enrollmentDetails);
    }

    @GetMapping("/appointmentCountsbygroupId")
    public ResponseEntity<List<Map<String, Object>>> getAppointedDetails(@RequestParam int groupId) {
        List<Map<String, Object>> enrollmentDetails = nulmService.getAppointedDetails(groupId);
        return ResponseEntity.ok(enrollmentDetails);
    }

    @GetMapping("/getDepartment")
    public ResponseEntity<List<Map<String, Object>>> getDepartment() {
        List<Map<String, Object>> orderNumbers = nulmService.getDepartment();
        return ResponseEntity.ok(orderNumbers);
    }

    @GetMapping("/getEnrollmentdeatilsByID/{enrollmentId}")
    public ResponseEntity<List<Map<String, Object>>> getEnrollmentsById(
            @PathVariable("enrollmentId") int enrollmentId) {
        List<Map<String, Object>> enrollments = nulmService.getEnrollmentsById(enrollmentId);

        if (enrollments.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no enrollments are found
        }

        return ResponseEntity.ok(enrollments); // Return 200 OK with the list of enrollments
    }

    @GetMapping("/selfHelpGroups")
    public ResponseEntity<List<Map<String, Object>>> getSelfHelpGroups() {
        List<Map<String, Object>> selfHelpGroups = nulmService.getSelfHelpGroups();
        return ResponseEntity.ok(selfHelpGroups);
    }

    @GetMapping("/orderNumbers")
    public ResponseEntity<List<Map<String, Object>>> getOrderNumbers() {
        List<Map<String, Object>> orderNumbers = nulmService.getOrderNumbers();
        return ResponseEntity.ok(orderNumbers);
    }

    @PostMapping("/updateEnrollment/{id}")
    public ResponseEntity<String> updateEnrollment(
            @PathVariable("id") int id,
            @RequestParam String dateOfBirth,
            @RequestParam String gender,
            @RequestParam int groupId, // New parameter
            @RequestParam int orderId // New parameter
    ) {
        int rowsAffected = nulmService.updateEnrollment(id, dateOfBirth, gender, groupId, orderId);

        if (rowsAffected > 0) {
            return new ResponseEntity<>("Enrollment updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Enrollment update failed", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/saveOrder")
    public ResponseEntity<String> saveOrderDetails(@RequestParam String order_number,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date order_date,
            @RequestParam int no_of_staffs,
            @RequestParam String order_description,
            @RequestParam MultipartFile ordercopyurl,
            @RequestParam String order_generated_by,
            @RequestParam String category,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date validity_date) {
        String order_copy_url = "";
        try {
            // Fetch the upload directory and folder name from environment
            String uploadDirectory = environment.getProperty("file.upload.directory");
            String folderName = environment.getProperty("nulm_foldername");
            String fileName = "OrderCopy_" + ordercopyurl.getOriginalFilename();

            // Construct the file path
            String filepath = uploadDirectory + folderName + "/" + fileName;
            Path path = Paths.get(filepath);

            // Ensure the directory exists
            Files.createDirectories(path.getParent());

            // Write the file to the specified path
            Files.write(path, ordercopyurl.getBytes());

            // Set the order_copy_url to the relative file path for storing in DB
            order_copy_url = folderName + "/" + fileName;

            // Save the order details using the service method
            String savedOrderNumber = nulmService.saveOrderDetails(
                    order_number,
                    order_date,
                    no_of_staffs,
                    order_description,
                    order_copy_url,
                    order_generated_by,
                    category,
                    validity_date);

            return ResponseEntity.ok("Order saved successfully with order number: " + savedOrderNumber);

        } catch (Exception e) {
            // Handle error scenarios
            return ResponseEntity.status(500).body("Error saving order: " + e.getMessage());
        }
    }
    /*
     * @GetMapping("/staffInititeadList")
     * public ResponseEntity<List<Map<String, Object>>>
     * getAttendanceWithInitiatedSalaryByGroupId(@RequestParam("inchargeName")
     * String inchargeName,
     * 
     * @RequestParam("month") String month) {
     * List<Map<String, Object>> staffList =
     * nulmService.getAttendanceWithInitiatedSalary(inchargeName, month);
     * return ResponseEntity.ok(staffList);
     * }
     * 
     * @GetMapping("/staffInititeadList")
     * public ResponseEntity<List<Map<String, Object>>>
     * getAttendanceWithInitiatedSalaryByGroupId(
     * 
     * @RequestParam(value = "inchargeId", required = false) String inchargeId,
     * 
     * @RequestParam(value = "month", required = false) String month,
     * 
     * @RequestParam(value = "inchargeName", required = false) String inchargeName,
     * 
     * @RequestParam(value = "zone", required = false) Integer zone) {
     * 
     * List<Map<String, Object>> staffList =
     * nulmService.getAttendanceWithInitiatedSalary(inchargeId, month, inchargeName,
     * zone);
     * return ResponseEntity.ok(staffList);
     * }
     */

    @GetMapping("/staffInititeadList")
    public ResponseEntity<List<Map<String, Object>>> getAttendanceWithInitiatedSalaryByGroupId(
            @RequestParam(value = "inchargeId", required = false) Integer inchargeId,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "incharge_name", required = false) String inchargeName,
            @RequestParam(value = "enrollmentIds", required = false) List<Integer> enrollmentIds) {

        List<Map<String, Object>> staffList = nulmService.getAttendanceWithInitiatedSalary(inchargeId, month, year,
                inchargeName, enrollmentIds);
        return ResponseEntity.ok(staffList);
    }

    /*
     * @PostMapping("/salaryDetailUpdate")
     * public ResponseEntity<String> addSalaryDetails(@RequestBody List<Map<String,
     * Object>> requestList) {
     * System.out.println("Received POST request with data: " + requestList);
     * try {
     * for (Map<String, Object> request : requestList) {
     * int totalDaysPresent =
     * Integer.parseInt(request.get("total_days_present").toString());
     * int totalDaysOd = Integer.parseInt(request.get("total_days_od").toString());
     * int totalDaysSalary =
     * Integer.parseInt(request.get("total_days_salary").toString());
     * int totalDaysAbsent =
     * Integer.parseInt(request.get("total_days_absent").toString());
     * String month = request.get("month").toString();
     * int year = Integer.parseInt(request.get("year").toString());
     * int salaryAmount = Integer.parseInt(request.get("salary_amount").toString());
     * int enrollmentId = Integer.parseInt(request.get("enrollment_id").toString());
     * int groupId = Integer.parseInt(request.get("group_id").toString());
     * int wageId = Integer.parseInt(request.get("wage_id").toString());
     * 
     * // Check if the month and year are valid for salary initiation (past month)
     * if (isFutureOrCurrentMonth(month, year)) {
     * return ResponseEntity.status(HttpStatus.BAD_REQUEST)
     * .body("Salary cannot be initiated for the current or future months. Please select a past month."
     * );
     * }
     * 
     * String salaryStatus = "Initiated";
     * 
     * // Call the service method to insert the salary details
     * int rowsAffected = nulmService.addSalaryDetails(totalDaysPresent,
     * totalDaysOd, totalDaysSalary, totalDaysAbsent, month, year, salaryAmount,
     * salaryStatus, enrollmentId, groupId, wageId);
     * 
     * if (rowsAffected <= 0) {
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
     * .body("Failed to add salary details for enrollment ID: " + enrollmentId);
     * }
     * }
     * return ResponseEntity.status(HttpStatus.CREATED)
     * .body("Salary details added successfully for all selected entries.");
     * } catch (Exception e) {
     * e.printStackTrace(); // Log the exception for debugging
     * return ResponseEntity.status(HttpStatus.BAD_REQUEST)
     * .body("Invalid request data: " + e.getMessage());
     * }
     * }
     */
    @PostMapping("/salaryDetailUpdate")
    public ResponseEntity<String> addSalaryDetails(@RequestBody List<Map<String, Object>> requestList) {
        System.out.println("Received POST request with data: " + requestList);
        try {
            for (Map<String, Object> request : requestList) {
                System.out.println("enrollment_id = " + request.get("enrollment_id").toString());
                int totalDaysPresent = Integer.parseInt(request.get("total_days_present").toString());
                int totalDaysOd = Integer.parseInt(request.get("total_days_od").toString());
                int totalDaysSalary = Integer.parseInt(request.get("total_days_salary").toString());
                int totalDaysAbsent = Integer.parseInt(request.get("total_days_absent").toString());
                String month = request.get("month").toString();
                int year = Integer.parseInt(request.get("year").toString());
                int salaryAmount = Integer.parseInt(request.get("salary_amount").toString());
                int enrollmentId = Integer.parseInt(request.get("enrollment_id").toString());
                int groupId = Integer.parseInt(request.get("group_id").toString());
                int wageId = Integer.parseInt(request.get("wage_id").toString());
                int inchargeID = 0;// Integer.parseInt(""+request.get("incharge_id"));

                // Check if the month and year are valid for salary initiation (past month)
                if (isFutureOrCurrentMonth(month, year)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Salary cannot be initiated for the current or future months. Please select a past month.");
                }

                String salaryStatus = "Initiated";

                boolean checkexist = nulmService.checkSalaryDetails(enrollmentId, month, year);

                System.out.println("checkexist:   " + checkexist);

                if (!checkexist) {
                    // Call the service method to insert the salary details
                    int rowsAffected = nulmService.addSalaryDetails(totalDaysPresent, totalDaysOd, totalDaysSalary,
                            totalDaysAbsent, month, year, salaryAmount, salaryStatus, enrollmentId, groupId, wageId,
                            inchargeID);
                    if (rowsAffected <= 0) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to add salary details for enrollment ID: " + enrollmentId);
                    }
                } else {
                    int rowsAffected = nulmService.updateSalaryDetails(totalDaysPresent, totalDaysOd, totalDaysSalary,
                            totalDaysAbsent, month, year, salaryAmount, salaryStatus, enrollmentId, groupId, wageId,
                            inchargeID);
                    if (rowsAffected <= 0) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to update salary details for enrollment ID: " + enrollmentId);
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Salary details added successfully for all selected entries.");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request data: " + e.getMessage());
        }
    }

    // Helper method to check if the month is future or current
    private boolean isFutureOrCurrentMonth(String month, int year) {
        // Get the current month and year
        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();

        // Convert the string month from the request to Month enum
        Month requestMonth = Month.valueOf(month.toUpperCase()); // Ensure that month is passed in capitalized format
                                                                 // like "JANUARY", "FEBRUARY", etc.

        // Compare the request month and year with the current date
        if (year > currentYear) {
            return true; // Future year
        } else if (year == currentYear && requestMonth.compareTo(currentMonth) >= 0) {
            return true; // Current or future month in the current year
        }
        return false; // Past month
    }

    // @PostMapping("/salaryDetailUpdate")
    // public ResponseEntity<String> addSalaryDetails(@RequestBody List<Map<String,
    // Object>> requestList) {
    // System.out.println("Received POST request with data: " + requestList);
    // try {
    // for (Map<String, Object> request : requestList) {
    // int totalDaysPresent =
    // Integer.parseInt(request.get("total_days_present").toString());
    // int totalDaysOd = Integer.parseInt(request.get("total_days_od").toString());
    // int totalDaysSalary =
    // Integer.parseInt(request.get("total_days_salary").toString());
    // int totalDaysAbsent =
    // Integer.parseInt(request.get("total_days_absent").toString());
    // String month = request.get("month").toString();
    // int year = Integer.parseInt(request.get("year").toString());
    // int salaryAmount = Integer.parseInt(request.get("salary_amount").toString());
    // int enrollmentId = Integer.parseInt(request.get("enrollment_id").toString());
    // int groupId = Integer.parseInt(request.get("group_id").toString());
    // int wageId = Integer.parseInt(request.get("wage_id").toString());
    //
    // String salaryStatus = "Initiated";
    //
    // // Call the service method to insert the salary details
    // int rowsAffected = nulmService.addSalaryDetails(totalDaysPresent,
    // totalDaysOd, totalDaysSalary, totalDaysAbsent, month, year, salaryAmount,
    // salaryStatus, enrollmentId, groupId, wageId);
    //
    // if (rowsAffected <= 0) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed
    // to add salary details for enrollment ID: " + enrollmentId);
    // }
    // }
    // return ResponseEntity.status(HttpStatus.CREATED).body("Salary details added
    // successfully for all selected entries.");
    // } catch (Exception e) {
    // e.printStackTrace(); // Log the exception for debugging
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request
    // data: " + e.getMessage());
    // }
    // }

    @PostMapping("/update-salary-status")
    public ResponseEntity<String> updateSalaryStatus(
            @RequestParam List<Integer> enrollmentIds,
            @RequestParam String month,
            @RequestParam int year) {
        System.out.println("Enrollment IDs: " + enrollmentIds);
        System.out.println("Month: " + month);
        System.out.println("Year: " + year);
        nulmService.updateSalaryStatusToApproved(enrollmentIds, month, year);
        return ResponseEntity.ok("Salary status updated to 'Approved' for the provided enrollments.");
    }

    /*************** API For Mobile Application *********************************/
    /*
     * @GetMapping("/attendanceWithSalary")
     * public ResponseEntity<List<Map<String, Object>>>
     * getAttendanceWithSalaryByInchargeId(
     * 
     * @RequestParam(value = "month", required = false) String month,
     * 
     * @RequestParam(value = "year", required = false) Integer year,
     * 
     * @RequestParam(value = "incharge_id", required = false) Integer inchargeId) {
     * 
     * try {
     * // Call the service method to fetch the data
     * List<Map<String, Object>> attendanceWithSalary =
     * nulmService.getAttendanceWithSalaryByInchargeId(month, year, inchargeId);
     * 
     * // If no data is found, return a NOT FOUND response
     * if (attendanceWithSalary.isEmpty()) {
     * return ResponseEntity.status(404).body(List.of(Map.of("error",
     * "No attendance records found for the specified parameters.")));
     * }
     * 
     * // Return the data in the response
     * return ResponseEntity.ok(attendanceWithSalary);
     * } catch (Exception ex) {
     * // Return a 500 Internal Server Error with the error message
     * return ResponseEntity.status(500).body(List.of(Map.of("error",
     * "An error occurred while fetching attendance data: " + ex.getMessage())));
     * }
     * }
     */

    @GetMapping("/attendanceWithSalary")
    public ResponseEntity<List<Map<String, Object>>> getAttendanceWithSalaryByInchargeId(
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "incharge_id", required = false) Integer inchargeId) {

        try {

            //List<Map<String, Object>> attendanceWithSalary = new ArrayList<>();
            //Integer check = nulmService.checkInchargeID(inchargeId);
           // if (check == 1) {
                // Call the service method to fetch the data
               List<Map<String, Object>> attendanceWithSalary = nulmService.getAttendanceWithSalaryByInchargeId(month, year, inchargeId);
            //}
            // If no data is found, return a NOT FOUND response total_days_salary,
            // total_salary and total_days_absent
            if (attendanceWithSalary.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(List.of(Map.of("error", "No attendance records found for the specified parameters.")));
            }

            for (Map<String, Object> aa : attendanceWithSalary) {
                long days = ((Number) aa.get("total_days")).longValue();
                long present = ((Number) aa.get("total_days_present")).longValue();
                long od = ((Number) aa.get("total_days_od")).longValue();
                long salaryPerDay = ((Number) aa.get("salary_per_day")).longValue();

                long totalPresent = present + od;
                long totalAbsent = days - totalPresent;
                long totalSalary = totalPresent * salaryPerDay;

                aa.put("total_salary", totalSalary);
                aa.put("total_days_absent", totalAbsent);
                aa.put("total_days_salary", totalPresent);
            }

            // Return the data in the response
            return ResponseEntity.ok(attendanceWithSalary);
        } catch (Exception ex) {
            // Return a 500 Internal Server Error with the error message
            return ResponseEntity.status(500).body(
                    List.of(Map.of("error", "An error occurred while fetching attendance data: " + ex.getMessage())));
        }
    }

    @PostMapping("/salaryDetailInitiate")
    public ResponseEntity<String> addInitiatedSalaryDetails(
            @RequestParam("total_days_present") List<Integer> totalDaysPresentList,
            @RequestParam("total_days_od") List<Integer> totalDaysOdList,
            @RequestParam("total_days_salary") List<Integer> totalDaysSalaryList,
            @RequestParam("total_days_absent") List<Integer> totalDaysAbsentList,
            @RequestParam("month") List<String> monthList,
            @RequestParam("year") List<Integer> yearList,
            @RequestParam("salary_amount") List<Integer> salaryAmountList,
            @RequestParam("enrollment_id") List<Integer> enrollmentIdList,
            @RequestParam("group_id") List<Integer> groupIdList,
            @RequestParam("wage_id") List<Integer> wageIdList) {
        // System.out.println("Received POST request with form-data");

        try {
            // Iterate through each list to process the form data
            for (int i = 0; i < totalDaysPresentList.size(); i++) {
                int totalDaysPresent = totalDaysPresentList.get(i);
                int totalDaysOd = totalDaysOdList.get(i);
                int total_days_salary = totalDaysSalaryList.get(i);
                int totalDaysAbsent = totalDaysAbsentList.get(i);
                String month = monthList.get(i);
                int year = yearList.get(i);
                int salaryAmount = salaryAmountList.get(i);
                int enrollmentId = enrollmentIdList.get(i);
                int groupId = groupIdList.get(i);
                int wageId = wageIdList.get(i);

                String salaryStatus = "Initiated";

                // Call the service method to insert the salary details
                int rowsAffected = nulmService.addSalaryDetails(totalDaysPresent, totalDaysOd, total_days_salary,
                        totalDaysAbsent, month, year, salaryAmount, salaryStatus, enrollmentId, groupId, wageId);

                if (rowsAffected <= 0) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to add salary details for enrollment ID: " + enrollmentId);
                }
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Salary details added successfully for all selected entries.");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data: " + e.getMessage());
        }
    }

    // @GetMapping("/filtered-details")
    // public List<Map<String, Object>> getFilteredSalaryDetails(
    // @RequestParam(required = false) String month,
    // @RequestParam(required = false) Integer year,
    // @RequestParam(required = false) String salaryStatus) {
    //
    // // Call the service method to get the filtered salary details
    // return nulmService.getFilteredSalaryDetails(month, year, salaryStatus);
    // }
}
