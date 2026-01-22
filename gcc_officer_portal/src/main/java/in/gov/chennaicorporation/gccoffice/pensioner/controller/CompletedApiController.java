package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.pensioner.service.CompletedService;

@RequestMapping("gcc/api/department/completed")
@RestController
public class CompletedApiController {

    @Autowired
    private CompletedService completedService;

    @Autowired
    public CompletedApiController(CompletedService completedService) {
        this.completedService = completedService;
    }

    // Combined method to handle both cases (with and without emp_no parameter)
    @GetMapping("/cpslist")
    public ResponseEntity<?> getCPSList(@RequestParam(value = "emp_no", required = false) String empNo,
    		@RequestParam(required = false)Integer file_cat,
    		@RequestParam(required = false)Integer deptId) {
        try {
            List<Map<String, Object>> cpsList;

            // If emp_no is provided, fetch filtered records
            if (empNo != null && !empNo.isEmpty()) {
                cpsList = completedService.getCPSListByEmpNo(empNo);
            } else {
                // If emp_no is not provided, fetch all records
                cpsList = completedService.getCPSList(file_cat,deptId);
            }

            // Return response based on the data
            if (cpsList.isEmpty()) {
                return ResponseEntity.noContent().build(); // No records found
            }

            return ResponseEntity.ok(cpsList); // Return the records

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error occurred while fetching completed service records.");
        }
    }
}
