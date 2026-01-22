package in.gov.chennaicorporation.gccoffice.cdwaste.service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SMSService {
    public String sendMsg(String msgUrl) {
        System.out.println("Whatsapp URL :-"+msgUrl);

        String response ="";
        try{
            HttpURLConnection connection =null;

            URL url = new URL(msgUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            response=responseCode+"";
            System.out.println("Response Code: " + responseCode);

        }catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }
    
    public String sendSMS(String SMS_Url) {
    	System.out.println("SMS URL:- "+ SMS_Url);
        RestTemplate restTemplate = new RestTemplate();
        String response =restTemplate.getForObject(SMS_Url,String.class);
        return response;
    }
}
