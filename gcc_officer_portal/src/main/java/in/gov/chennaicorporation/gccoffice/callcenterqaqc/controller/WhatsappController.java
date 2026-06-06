package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.WhatsappqaqcMsgService;

@RestController("callcenterqaqcwhatsapp")
@RequestMapping("/gcc/api/qaqc/whatsapp")
public class WhatsappController {

    @Autowired
    private WhatsappqaqcMsgService whatsappqaqcMsgService;

    @PostMapping("/send-whatsapp-message")
    public ResponseEntity<String> sendWhatsAppMessage(@RequestParam String msgUrl,@RequestParam String tamilmsgUrl) {
        String response = whatsappqaqcMsgService.sendMsg(msgUrl,tamilmsgUrl);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/checkforwhatsappmsg")
    public boolean checkforwhatsappmsg(
            @RequestParam String complaintNumber,
            @RequestParam String action,
            @RequestParam String complaintTypeName,
            @RequestParam String mobnumber,
            @RequestParam String pname) {

        Map<String, Object> ismsg =
                whatsappqaqcMsgService.checkforwhatsappmsg(
                        complaintNumber,
                        action);

        String returnedAction =
                String.valueOf(ismsg.get("action"));

        boolean status =
                Boolean.TRUE.equals(ismsg.get("status"));

        System.out.println("Action = " + returnedAction);
        System.out.println("Status = " + status);

        if (status) {
        	
        	String result = sendWhatsappMsg(pname, complaintTypeName, mobnumber, complaintNumber,returnedAction);
        	if(result.equals("200")) {      		 
				  System.out.println("Whatsapp Message Triggered for "+complaintNumber);
				  return true;
			 } 
        	else {
        		System.out.println("Whatsapp Message Failed for "+complaintNumber);
				  return false;
        	}
        }

        return false;
    }
    
    private String sendWhatsappMsg(String pname,String cat,String mob_no, String complaintNumber,String returnedAction) {
		String urlString = "";
				
		try {
					
			
			if("COMPLETED".equalsIgnoreCase(returnedAction)) {
       		 
        	}
        	
        	if("UNATTENDED".equalsIgnoreCase(returnedAction)) {
        		
        	}
			
			  String placeholders =
		                URLEncoder.encode(
		                        pname + "|~|" + cat + "|~|" + complaintNumber,
		                        StandardCharsets.UTF_8.toString());

		        String buttonPlaceholder =
		                URLEncoder.encode(
		                        complaintNumber,
		                        StandardCharsets.UTF_8.toString());

		        String mobile =
		                URLEncoder.encode(
		                        mob_no,
		                        StandardCharsets.UTF_8.toString());
		        
			
			urlString="";
	         
			String res = hitURL(urlString);
			return res;
			
		} catch (Exception e) {
			e.printStackTrace();
	        return "";
		}
		
         
	}
    
    private String hitURL(String urlString) {
		String response = "";
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			response = String.valueOf(responseCode);
			//System.out.println("Response Code for URL: " + urlString + " is " + responseCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("response=" + response);
		return response;
	}
    
    @GetMapping("/getclosefromuser")
    public String getclosefromuser(@RequestParam String complaintNumber) {
    	
    	return whatsappqaqcMsgService.getclosefromuser(complaintNumber);
    	   	
    }
    
    @GetMapping("/getreopenfromuser")
    public String getreopenfromuser(@RequestParam String complaintNumber) {
    	
    	return whatsappqaqcMsgService.getreopenfromuser(complaintNumber);
    	   	
    }

}

