package in.gov.chennaicorporation.gccoffice.vtrack.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import antlr.collections.impl.Vector;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;
import in.gov.chennaicorporation.gccoffice.vtrack.service.VehicleService;

@RestController
@RequestMapping("/gcc/api/vtrack")
public class VehicleApiController {
    @Autowired
    public VehicleService vehicleService;

    @GetMapping("/getTemplateDetails")
    public ResponseEntity<List<Map<String, Object>>> getTemplateDetails() {
        try {
            List<Map<String, Object>> templateNames = vehicleService.getAllTemplateNames();

            if (templateNames == null || templateNames.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
            }

            return ResponseEntity.ok(templateNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PostMapping("/saveMessageDetails")
    public String saveMessageDetails(@RequestParam("date") String date, @RequestParam("image") MultipartFile image,
            @RequestParam("templateType") String tempType) throws IOException {

        String img_path = vehicleService.fileUpload("vtrack", tempType, image);

        int result = vehicleService.saveMessageDetails(date, tempType, img_path);

        if (result > 0) {
            return "success";
        }
        return "Failed";

    }

    @GetMapping({ "/sendmessage" })
    public String sendmessage(
            @RequestParam(value = "msgid", required = true) String msgid,
            @RequestParam(value = "datetxt", required = false) String datetxt,
            @RequestParam(value = "fileurl", required = false) String fileurl) {

        String LoginUserId = LoginUserInfo.getLoginUserId();

        return vehicleService.sendMessage(msgid, datetxt, fileurl);
    }

    @PostMapping("/disablemessage")
    public String disablemessage(@RequestParam(value = "msgId", required = true) String msgid) {
        String status = vehicleService.disablemessage(msgid);

        if (status.equals("success")) {
            return "success";
        } else {
            return "Failed to update";
        }
    }

    @PostMapping("/enablemessage")
    public String enablemessage(@RequestParam(value = "msgId", required = true) String msgid) {
        String status = vehicleService.enableMessage(msgid);

        if (status.equals("success")) {
            return "success";
        } else {
            return "Failed to update";
        }
    }

}
