package in.gov.chennaicorporation.gccoffice.flagpole.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.flagpole.util.MessageUtility;

@Service
public class FlagpoleSMSService {

        @Autowired
        private AppConfig appConfig;

        @Autowired
        private MessageUtility messageUtility;

        private final String key = "pfTEYN6H";

        public String sendBookingSuccesssms(String mobileNo, String requestId) {

                String message = "Your Flag Pole permission request has been submitted successfully. "
                                + "Request ID: " + requestId
                                + ". You will be notified once the status is updated. By GCC";

                // Encode message and fix colon issue (gateway not decoding %3A)
                String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8)
                                .replace("%3A", ":");

                String urlString = appConfig.getOtpUrl()
                                + "from=GCCCRP"
                                + "&key=" + key
                                + "&sender=GCCCRP"
                                + "&to=" + mobileNo
                                + "&body=" + encodedMsg
                                + "&entityid=1401572690000011081"
                                + "&templateid=1407176959297472163";

                System.out.println("SMS URL:- " + urlString);

                String response = messageUtility.sendSMS(urlString);
                System.out.println("SMS RESPONSE:- " + response);

                return response;
        }

        public String userrequestApprovedsms(String mobileNo, String requestId) {

                String message = "Your Flag Pole permission request has been approved. "
                                + "Request ID: " + requestId
                                + ". Please proceed as per the permitted guidelines. By GCC";

                // Encode message and fix colon issue (gateway not decoding %3A)
                String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8)
                                .replace("%3A", ":");

                String urlString = appConfig.getOtpUrl()
                                + "from=GCCCRP"
                                + "&key=" + key
                                + "&sender=GCCCRP"
                                + "&to=" + mobileNo
                                + "&body=" + encodedMsg
                                + "&entityid=1401572690000011081"
                                + "&templateid=1407176959304753543";

                System.out.println("SMS URL:- " + urlString);

                String response = messageUtility.sendSMS(urlString);
                System.out.println("SMS RESPONSE:- " + response);

                return response;
        }

        // public String userrequestRejectedsms(String mobileNo, String requestId) {

        // String message = "Your Flag Pole permission request has been rejected. "
        // + "Request ID: " + requestId + ". "
        // + "Review the details in the portal
        // https://gccservices.in/flagpolesregistration/status\n"
        // + "By GCC";

        // // ONLY encode. Do NOT replace %3A
        // String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8);

        // String urlString = appConfig.getOtpUrl()
        // + "from=GCCCRP"
        // + "&key=" + key
        // + "&sender=GCCCRP"
        // + "&to=" + mobileNo
        // + "&body=" + encodedMsg
        // + "&entityid=1401572690000011081"
        // + "&templateid=1407176959315374790";

        // System.out.println("SMS URL:- " + urlString);

        // String response = messageUtility.sendSMS(urlString);
        // System.out.println("SMS RESPONSE:- " + response);

        // return response;
        // }

        public String userrequestRejectedsms(String mobileNo, String requestId) {

                System.out.println("Rejected Mob Num  SMSSSSSSS = " + mobileNo);
                String url = "https://gccservices.in/flagpolesregistration/status";
                String message = "Your Flag Pole permission request has been rejected. Request ID: " + requestId
                                + ". Review the details in the portal " + url + "\r\nBy GCC";

                String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8);

                System.out.println("encodedMsg:- " + encodedMsg);

                String urlString = appConfig.getOtpUrl()
                                + "from=GCCCRP"
                                + "&key=" + key
                                // + "&sender=GCCCRP"
                                + "&to=" + mobileNo
                                + "&body=" + encodedMsg
                                + "&entityid=1401572690000011081"
                                + "&templateid=1407176959315374790";

                System.out.println("SMS URL:- " + urlString);

                String response = messageUtility.sendSMS(urlString);
                System.out.println("SMS RESPONSE:- " + response);

                return response;
        }

}
