package in.gov.chennaicorporation.gccoffice.flagpole.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WhatsappServiceFlagPole {

    @Autowired
    private SmsServiceFlagPole smsService;

    // c & d waste template after user register
    public void sendOfficerApprovedWhatsappMessage(String refId, String mobileNumber) {
        try {

            String apiUrl = "https://media.smsgupshup.com/GatewayAPI/rest?userid=2000233507&password=h2YjFNcJ&send_to="
                    + mobileNumber
                    + "&v=1.1&format=json&msg_type=IMAGE&method=SENDMEDIAMESSAGE&caption=Welcome+to+Greater+Chennai+Corporation%2C+%0A%0AYour+Registered+ID+is+%2A${registerId}%2A+.%0AYour+C%26D+waste+registration+has+been+approved+successfully.+%0A%0AFor+downloading+your+ID+card%2C+kindly+click+on+the+button+below+%F0%9F%91%87&media_url=https%3A%2F%2Fres.cloudinary.com%2Fdpub5muar%2Fimage%2Fupload%2Fv1729597847%2Fq2ihjs99puhhwkjzajyo.png&isTemplate=true&buttonUrlParam=userdetail%3Fregister_id%3D"
                    + refId;

            System.out.println("WhatsApp API URL: " + apiUrl); // Optional debug
            smsService.sendMsg(apiUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
