package in.gov.chennaicorporation.gccoffice.flagpole.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.flagpole.service.FlagpoleSMSService;
import in.gov.chennaicorporation.gccoffice.flagpole.service.RdoService;

@RestController
@RequestMapping("/gcc/api/flagpoleregistration")
public class FlagPoleApiController {

        @Autowired
        private RdoService rdoService;

        @Autowired
        private FlagpoleSMSService flagpoleSMSService;

        private final RestTemplate restTemplate;
        private final AppConfig appConfig;

        @Autowired
        public FlagPoleApiController(RestTemplate restTemplate, AppConfig appConfig) {
                this.restTemplate = restTemplate;
                this.appConfig = appConfig;
        }

        // new
        // @GetMapping("/getAllDetails")
        // public ResponseEntity<?> getAllRequestDetails(
        // @RequestParam String userLogin,
        // @RequestParam(required = false) String startDate,
        // @RequestParam(required = false) String endDate) {

        // try {
        // List<Map<String, Object>> result =
        // rdoService.getAllRequestDetailsByUserLogin(
        // userLogin, startDate, endDate);

        // // 🔥 Attach HISTORY per refid
        // for (Map<String, Object> record : result) {
        // String refid = (String) record.get("refid");

        // // List<Map<String, Object>> history =
        // // rdoService.getRequestHistoryByRefId(refid);
        // List<Map<String, Object>> history =
        // rdoService.getOfficerFeedbackHistoryByRefId(refid,
        // userLogin);

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

        // @GetMapping("/getAllDetails")
        // public ResponseEntity<?> getAllRequestDetails(
        // @RequestParam String gccUserId,
        // @RequestParam(required = false) String startDate,
        // @RequestParam(required = false) String endDate) {

        // try {

        // // 🔥 Resolve username ONCE (backend trust)
        // String username = rdoService.getUsernameByUserId(gccUserId);
        // if (username == null) {
        // throw new RuntimeException("Invalid gccUserId : " + gccUserId);
        // }

        // List<Map<String, Object>> result =
        // rdoService.getAllRequestDetailsByUserLogin(
        // gccUserId, startDate, endDate);

        // // 🔥 Attach HISTORY per refid (dept-wise)
        // for (Map<String, Object> record : result) {
        // String refid = (String) record.get("refid");

        // List<Map<String, Object>> history =
        // rdoService.getOfficerFeedbackHistoryByRefId(
        // refid, username);

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

        @GetMapping("/getAllDetails")
        public ResponseEntity<?> getAllRequestDetails(
                        @RequestParam String gccUserId,
                        @RequestParam(required = false) String startDate,
                        @RequestParam(required = false) String endDate) {

                try {

                        /* 🔥 Normalize input (username OR id → gccuserid) */
                        gccUserId = rdoService.resolveGccUserId(gccUserId);

                        List<Map<String, Object>> result = rdoService.getAllRequestDetailsByUserLogin(gccUserId,
                                        startDate, endDate);

                        /* 🔥 Attach HISTORY per refid */
                        for (Map<String, Object> record : result) {

                                String refid = (String) record.get("refid");

                                List<Map<String, Object>> history = rdoService.getOfficerFeedbackHistoryByRefId(refid,
                                                gccUserId);

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

        // old
        // @PostMapping("/rdo/updateDecision")
        // public ResponseEntity<?> updateRdoDecision(
        // @RequestParam String refid,
        // @RequestParam String status,
        // @RequestParam String remarks,
        // @RequestParam String approvedBy) {

        // int count = rdoService.updateRdoDecision(refid, status, remarks, approvedBy);

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

        // @PostMapping("/rdo/updateDecision")
        // public ResponseEntity<?> updateRdoDecision(
        // @RequestParam String refid,
        // @RequestParam String status,
        // @RequestParam String remarks,
        // @RequestParam String gccUserId) {

        // try {

        // /* 🔥 Normalize username OR id → gccuserid */
        // gccUserId = rdoService.resolveGccUserId(gccUserId);

        // int count = rdoService.updateRdoDecision(
        // refid, status, remarks, gccUserId);

        // if (count > 0) {
        // return ResponseEntity.ok(Map.of(
        // "status", "SUCCESS",
        // "message", "Decision updated successfully"));
        // } else {
        // return ResponseEntity.badRequest().body(Map.of(
        // "status", "FAILED",
        // "message", "Unable to update decision"));
        // }

        // } catch (Exception e) {
        // e.printStackTrace();
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        // .body(Map.of(
        // "status", "ERROR",
        // "message", e.getMessage()));
        // }
        // }

        @PostMapping("/officer/updateDecision")
        public ResponseEntity<?> updateOfficerDecision(
                        @RequestParam String refid,
                        @RequestParam String status,
                        @RequestParam String remarks,
                        @RequestParam String gccUserId,
                        @RequestParam String username) {

                try {

                        /* 🔥 Normalize username OR id → gccuserid */
                        gccUserId = rdoService.resolveGccUserId(gccUserId);

                        int count = rdoService.updateOfficerDecision(
                                        refid,
                                        status,
                                        remarks,
                                        gccUserId,
                                        username);

                        if (count > 0) {
                                return ResponseEntity.ok(Map.of(
                                                "status", "SUCCESS",
                                                "message", "Decision updated successfully"));
                        } else {
                                return ResponseEntity.badRequest().body(Map.of(
                                                "status", "FAILED",
                                                "message", "Unable to update decision"));
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of(
                                                        "status", "ERROR",
                                                        "message", e.getMessage()));
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

        // @GetMapping("/getApprovedAndRejectedDetails")
        // public ResponseEntity<?> getApprovedRequestDetails(
        // @RequestParam String userLogin,
        // @RequestParam(required = false) String startDate,
        // @RequestParam(required = false) String endDate) {

        // try {
        // List<Map<String, Object>> result =
        // rdoService.getRdoApprovedRequestDetailsByUserLogin(
        // userLogin, startDate, endDate, null);

        // for (Map<String, Object> record : result) {
        // String refid = (String) record.get("refid");

        // // 🔥 NEW: officer feedback history
        // List<Map<String, Object>> history =
        // rdoService.getOfficerFeedbackHistoryByRefId(refid,
        // userLogin);

        // record.put("history", history);
        // }

        // return ResponseEntity.ok(Map.of(
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
                        @RequestParam String gccUserId,
                        @RequestParam(required = false) String startDate,
                        @RequestParam(required = false) String endDate) {

                try {

                        /* 🔥 Normalize username OR id → gccuserid */
                        gccUserId = rdoService.resolveGccUserId(gccUserId);

                        /* 🔥 Fetch approved & rejected records */
                        List<Map<String, Object>> result = rdoService.getRdoApprovedRequestDetailsByUserLogin(
                                        gccUserId, startDate, endDate, null);

                        /* 🔥 Attach officer feedback history per refid */
                        for (Map<String, Object> record : result) {

                                String refid = (String) record.get("refid");

                                List<Map<String, Object>> history = rdoService.getOfficerFeedbackHistoryByRefId(refid,
                                                gccUserId);

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
                                                        "message", "Failed to fetch approved/rejected data",
                                                        "error", e.getMessage()));
                }
        }

        @GetMapping("/sendRejectMessage")
        public void sendRejectMessage(@RequestParam String mobileNo, @RequestParam String refid) {
                flagpoleSMSService.userrequestRejectedsms(mobileNo, refid);
        }

}
