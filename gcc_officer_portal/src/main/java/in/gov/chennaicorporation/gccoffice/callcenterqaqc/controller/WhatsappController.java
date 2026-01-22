package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.WhatsappqaqcMsgService;

@RestController("callcenterqaqcwhatsapp")
@RequestMapping("/gcc/qaqc/api/whatsapp")
public class WhatsappController {

    @Autowired
    private WhatsappqaqcMsgService whatsappqaqcMsgService;

    @PostMapping("/send-whatsapp-message")
    public ResponseEntity<String> sendWhatsAppMessage(@RequestParam String msgUrl,@RequestParam String tamilmsgUrl) {
        String response = whatsappqaqcMsgService.sendMsg(msgUrl,tamilmsgUrl);
        return ResponseEntity.ok(response);
    }

}

