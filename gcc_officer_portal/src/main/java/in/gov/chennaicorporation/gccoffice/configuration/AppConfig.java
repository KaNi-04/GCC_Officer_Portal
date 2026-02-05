package in.gov.chennaicorporation.gccoffice.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig {
	
	@Autowired
    private Environment env;
	
	public String qrAssetFeedback = "http://117.232.67.158:8063";
	public String mtm = "http://117.232.67.158:8060";
	public String petRegistration = "http://117.232.67.158:8061";
	public String garbageCollection = "http://117.232.67.158:8067"; //117.232.67.158
	public String cdwastecollectors = "https://gccservices.in";
	public String qaqcurl="https://erp.chennaicorporation.gov.in/pgr/newmobileservice?";
	public String attendanceReport = "http://117.232.67.158:8076";
	public String dumpregister = "https://gccservices.in";
	public String mobileservice="https://gccservices.in";
	public String gccappsMysqlPassword = "";
	public String domesticurl="https://gccservices.in/gccofficialapp/api/domesticwaste";
	
	public String otpUrl = "https://tmegov.onex-aura.com/api/sms?";

	public String getOtpUrl() {
		return otpUrl;
	}

	public void setOtpUrl(String otpUrl) {
		this.otpUrl = otpUrl;
	}
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
   
}