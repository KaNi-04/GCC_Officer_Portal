package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsappqaqcMsgService {
	
    public String sendMsg(String msgUrl, String tamilmsgUrl) {
        StringBuilder response = new StringBuilder();

    
        response.append("English Message Response: ").append(sendWhatsAppMessage(msgUrl)).append("\n");
        

        response.append("Tamil Message Response: ").append(sendWhatsAppMessage(tamilmsgUrl));

        return response.toString();
    }

    private String sendWhatsAppMessage(String urlString) {
        String response = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            response = String.valueOf(responseCode);
            System.out.println("Response Code for URL: " + urlString + " is " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


   
    public String sendSMS(String SMS_Url) {
        RestTemplate restTemplate = new RestTemplate();
        String response =restTemplate.getForObject(SMS_Url,String.class);
        return response;
    }
	}
