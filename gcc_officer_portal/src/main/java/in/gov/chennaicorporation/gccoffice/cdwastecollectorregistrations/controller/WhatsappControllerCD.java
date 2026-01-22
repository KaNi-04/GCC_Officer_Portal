package in.gov.chennaicorporation.gccoffice.cdwastecollectorregistrations.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.cdwastecollectorregistrations.service.WhatsappMsgService;



@RestController("whatsappconCD")
@RequestMapping("/gcc/api/cd/whatsapp")
public class WhatsappControllerCD {

    @Autowired
    private WhatsappMsgService whatsappMsgService;

    @PostMapping("/send-whatsapp-message")
    public ResponseEntity<String> sendWhatsAppMessage(@RequestParam String msgUrl) {
        String response = whatsappMsgService.sendMsg(msgUrl);
        return ResponseEntity.ok(response);
    }

}

