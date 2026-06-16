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
				  System.out.println(returnedAction+" Whatsapp Message Triggered for "+complaintNumber);
				  return true;
			 } 
        	else {
        		System.out.println(returnedAction+ " Whatsapp Message Failed for "+complaintNumber);
				  return false;
        	}
        }

        return false;
    }
    
    private String sendWhatsappMsg(String pname,String cat,String mob_no, String complaintNumber,String returnedAction) {
		String urlString = "";
				
		try {
				
			
			String placeholders =
	                URLEncoder.encode(
	                        pname + "|~|" + cat + "|~|" + complaintNumber,
	                        StandardCharsets.UTF_8.toString());

        String buttonPlaceholder =
                URLEncoder.encode(
                        complaintNumber+ "|~|" +complaintNumber,
                        StandardCharsets.UTF_8.toString());

        String mobile =
                URLEncoder.encode(
                        mob_no,
                        StandardCharsets.UTF_8.toString());
			
			
			if("COMPLETED".equalsIgnoreCase(returnedAction)) {
       		 
				urlString="https://sendapiv1.pinbot.ai/pinwa/sendMessage?apikey=5c995535-6244-11f0-98fc-02c8a5e042bd&from=919445061913&to="+mobile+"&templateid=3304034&type=template&placeholders="+placeholders;
				
        	}
        	
        	if("UNATTENDED".equalsIgnoreCase(returnedAction)) {
        	       	        		
	        
        		urlString="https://sendapiv1.pinbot.ai/pinwa/sendMessage?apikey=5c995535-6244-11f0-98fc-02c8a5e042bd&from=919445061913&to="+mobile+"&templateid=3304038&type=template&placeholders="+placeholders+"&button_placeholder="+buttonPlaceholder;
	        
        	}
			
	         
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
			System.out.println("Response Code for Whatsapp msg URL: " + urlString + " is " + responseCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("response=" + response);
		return response;
	}
    
//    @GetMapping("/getclosefromuser")
//    public String getclosefromuser(@RequestParam String complaintNumber) {
//    	
//    	return whatsappqaqcMsgService.getclosefromuser(complaintNumber);
//    	   	
//    }
    
    @GetMapping(value = "/getclosefromuser", produces = "text/html")
    public String getclosefromuser(@RequestParam String complaintNumber) {

        String result = whatsappqaqcMsgService.getclosefromuser(complaintNumber);

        boolean success = result.contains("Successfully");
        String color = success ? "#28a745" : "#dc3545";

        String logoUrl = "https://gccservices.in/gcc/assets/images/logo.png"; 

        //background: #f5f5f5;
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>GCC PGR Complaint Status</title>
                <style>
                    body{
                        font-family: Arial, sans-serif;
        				background: #1d495a;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        margin: 0;
                    }

                    .card{
                        width: 600px;
                        background: #fff;
                        border-radius: 12px;
                        box-shadow: 0 4px 15px rgba(0,0,0,0.15);
                        overflow: hidden;
                    }

                    .card-header{
                        display: flex;
                        align-items: center;
                        padding: 15px 20px;
                        border-bottom: 1px solid #ddd;
                        background: #f8f9fa;
                    }

                    .card-header img{
                        width: 60px;
                        height: 60px;
                        object-fit: contain;
                    }

                    .card-header h2{
                        flex: 1;
                        text-align: center;
                        margin: 0;
                        color: #003366;
                    }

                    .card-body{
                        padding: 30px;
                        text-align: center;
                        min-height: 120px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }

                    .card-body h3{
					    white-space: pre-line;
					    margin: 0;
					    color: %s;
					    line-height: 1.8;
					}

                    .card-footer{
                        padding: 12px 20px;
                        border-top: 1px solid #ddd;
                        text-align: right;
                        font-weight: bold;
                        color: #666;
                        background: #f8f9fa;
                    }
                </style>
            </head>
            <body>
                <div class="card">

                    <div class="card-header">
                        <img src="%s" alt="GCC Logo">
                        <h2>Thanks For Your Response !!!</h2>
                    </div>

                    <div class="card-body">
                        <h3>%s</h3>
                    </div>

                    <div class="card-footer">
                        - GCC
                    </div>

                </div>
            </body>
            </html>
            """.formatted(color, logoUrl, result);
    }
    

    @GetMapping(value="/getreopenfromuser", produces = "text/html")
    public String getreopenfromuser(@RequestParam String complaintNumber) {
    	
    	String result= whatsappqaqcMsgService.getreopenfromuser(complaintNumber);
    	
    	 boolean success = result.contains("Successfully");
         String color = success ? "#28a745" : "#dc3545";

         String logoUrl = "https://gccservices.in/gcc/assets/images/logo.png"; 

         //background: #f5f5f5;
         
         return """
             <!DOCTYPE html>
             <html>
             <head>
                 <meta charset="UTF-8">
                 <title>GCC PGR Complaint Status</title>
                 <style>
                     body{
                         font-family: Arial, sans-serif;
         				background: #1d495a;
                         display: flex;
                         justify-content: center;
                         align-items: center;
                         height: 100vh;
                         margin: 0;
                     }

                     .card{
                         width: 600px;
                         background: #fff;
                         border-radius: 12px;
                         box-shadow: 0 4px 15px rgba(0,0,0,0.15);
                         overflow: hidden;
                     }

                     .card-header{
                         display: flex;
                         align-items: center;
                         padding: 15px 20px;
                         border-bottom: 1px solid #ddd;
                         background: #f8f9fa;
                     }

                     .card-header img{
                         width: 60px;
                         height: 60px;
                         object-fit: contain;
                     }

                     .card-header h2{
                         flex: 1;
                         text-align: center;
                         margin: 0;
                         color: #003366;
                     }

                     .card-body{
                         padding: 30px;
                         text-align: center;
                         min-height: 120px;
                         display: flex;
                         align-items: center;
                         justify-content: center;
                     }

                     .card-body h3{
 					    white-space: pre-line;
 					    margin: 0;
 					    color: %s;
 					    line-height: 1.8;
 					}

                     .card-footer{
                         padding: 12px 20px;
                         border-top: 1px solid #ddd;
                         text-align: right;
                         font-weight: bold;
                         color: #666;
                         background: #f8f9fa;
                     }
                 </style>
             </head>
             <body>
                 <div class="card">

                     <div class="card-header">
                         <img src="%s" alt="GCC Logo">
                         <h2>Thanks For Your Response !!!</h2>
                     </div>

                     <div class="card-body">
                         <h3>%s</h3>
                     </div>

                     <div class="card-footer">
                         - GCC
                     </div>

                 </div>
             </body>
             </html>
             """.formatted(color, logoUrl, result);
    	   	
    }

}

