package in.gov.chennaicorporation.gccoffice.flagpole.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.cdwaste.service.OfficerService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.flagpole.service.RdoService;

@RestController
@RequestMapping("/gcc/api/flagpoleregistration")
public class FlagPoleApiController {

        @Autowired
        private RdoService rdoService;

        private final RestTemplate restTemplate;
        private final AppConfig appConfig;

        @Autowired
        public FlagPoleApiController(RestTemplate restTemplate, AppConfig appConfig) {
                this.restTemplate = restTemplate;
                this.appConfig = appConfig;
        }

        // old
        // @GetMapping("/getAllDetails")
        // public ResponseEntity<?> getAllRequestDetails(
        // @RequestParam String userLogin,
        // @RequestParam(required = false) String startDate,
        // @RequestParam(required = false) String endDate) {

        // try {
        // List<Map<String, Object>> result =
        // rdoService.getAllRequestDetailsByUserLogin(
        // userLogin, startDate, endDate);

        // return ResponseEntity.ok(
        // Map.of(
        // "status", "success",
        // "count", result.size(),
        // "data", result));

        // } catch (Exception e) {
        // e.printStackTrace();
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        // .body(
        // Map.of(
        // "status", "error",
        // "message", "Failed to fetch data",
        // "error", e.getMessage()));
        // }
        // }

        // new
        @GetMapping("/getAllDetails")
        public ResponseEntity<?> getAllRequestDetails(
                        @RequestParam String userLogin,
                        @RequestParam(required = false) String startDate,
                        @RequestParam(required = false) String endDate) {

                try {
                        List<Map<String, Object>> result = rdoService.getAllRequestDetailsByUserLogin(
                                        userLogin, startDate, endDate);

                        // 🔥 Attach HISTORY per refid
                        for (Map<String, Object> record : result) {
                                String refid = (String) record.get("refid");

                                // List<Map<String, Object>> history =
                                // rdoService.getRequestHistoryByRefId(refid);
                                List<Map<String, Object>> history = rdoService.getOfficerFeedbackHistoryByRefId(refid,
                                                userLogin);

                                record.put("history", history);
                        }

                        return ResponseEntity.ok(
                                        Map.of(
                                                        "status", "success",
                                                        "count", result.size(),
                                                        "data", result));

                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of(
                                                        "status", "error",
                                                        "message", "Failed to fetch data",
                                                        "error", e.getMessage()));
                }
        }

        // @PostMapping("/rdo/updateDecision")
        // public ResponseEntity<?> updateRdoDecision(
        // @RequestParam String refid,
        // @RequestParam String status,
        // @RequestParam String remarks,
        // @RequestParam String approvedBy // 🔥 FROM JS
        // ) {

        // int count = rdoService.updateRdoDecision(
        // refid,
        // status,
        // remarks,
        // approvedBy);

        // if (count > 0) {
        // return ResponseEntity.ok(Map.of(
        // "status", "SUCCESS",
        // "message", "Decision updated successfully"));
        // } else {
        // return ResponseEntity.badRequest().body(Map.of(
        // "status", "FAILED",
        // "message", "Unable to update decision"));
        // }
        // }

        @PostMapping("/rdo/updateDecision")
        public ResponseEntity<?> updateRdoDecision(
                        @RequestParam String refid,
                        @RequestParam String status,
                        @RequestParam String remarks,
                        @RequestParam String approvedBy) {

                int count = rdoService.updateRdoDecision(refid, status, remarks, approvedBy);

                if (count > 0) {
                        return ResponseEntity.ok(Map.of(
                                        "status", "SUCCESS",
                                        "message", "Decision updated successfully"));
                } else {
                        return ResponseEntity.badRequest().body(Map.of(
                                        "status", "FAILED",
                                        "message", "Unable to update decision"));
                }
        }

        // old
        // @GetMapping("/getApprovedAndRejectedDetails")
        // public ResponseEntity<?> getApprovedRequestDetails(
        // @RequestParam String userLogin,
        // @RequestParam(required = false) String startDate,
        // @RequestParam(required = false) String endDate,
        // @RequestParam(required = false) String status) {

        // try {
        // List<Map<String, Object>> result =
        // rdoService.getRdoApprovedRequestDetailsByUserLogin(
        // userLogin, startDate, endDate, status);

        // return ResponseEntity.ok(
        // Map.of(
        // "status", "success",
        // "count", result.size(),
        // "data", result));

        // } catch (Exception e) {
        // e.printStackTrace();
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        // .body(
        // Map.of(
        // "status", "error",
        // "message", "Failed to fetch data",
        // "error", e.getMessage()));
        // }
        // }

        // new
        // @GetMapping("/getApprovedAndRejectedDetails")
        // public ResponseEntity<?> getApprovedRequestDetails(
        // @RequestParam String userLogin,
        // @RequestParam(required = false) String startDate,
        // @RequestParam(required = false) String endDate,
        // @RequestParam(required = false) String status) {

        // try {
        // List<Map<String, Object>> result =
        // rdoService.getRdoApprovedRequestDetailsByUserLogin(
        // userLogin, startDate, endDate, status);

        // // 🔥 Attach HISTORY per refid
        // for (Map<String, Object> record : result) {
        // String refid = (String) record.get("refid");

        // List<Map<String, Object>> history =
        // rdoService.getRequestHistoryByRefId(refid);

        // record.put("history", history);
        // }

        // return ResponseEntity.ok(
        // Map.of(
        // "status", "success",
        // "count", result.size(),
        // "data", result));

        // } catch (Exception e) {
        // e.printStackTrace();
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        // .body(Map.of(
        // "status", "error",
        // "message", "Failed to fetch data",
        // "error", e.getMessage()));
        // }
        // }

        @GetMapping("/getApprovedAndRejectedDetails")
        public ResponseEntity<?> getApprovedRequestDetails(
                        @RequestParam String userLogin,
                        @RequestParam(required = false) String startDate,
                        @RequestParam(required = false) String endDate) {

                try {
                        List<Map<String, Object>> result = rdoService.getRdoApprovedRequestDetailsByUserLogin(
                                        userLogin, startDate, endDate, null);

                        for (Map<String, Object> record : result) {
                                String refid = (String) record.get("refid");

                                // 🔥 NEW: officer feedback history
                                List<Map<String, Object>> history = rdoService.getOfficerFeedbackHistoryByRefId(refid,
                                                userLogin);

                                record.put("history", history);
                        }

                        return ResponseEntity.ok(Map.of(
                                        "status", "success",
                                        "count", result.size(),
                                        "data", result));

                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of(
                                                        "status", "error",
                                                        "message", "Failed to fetch data",
                                                        "error", e.getMessage()));
                }
        }

}
