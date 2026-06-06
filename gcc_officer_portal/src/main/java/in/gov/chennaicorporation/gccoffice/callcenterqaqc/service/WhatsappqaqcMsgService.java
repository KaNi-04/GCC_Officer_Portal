package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsappqaqcMsgService {
	
	private final RestTemplate restTemplate;
	
	 @Autowired
	 private JdbcTemplate jdbcTemplate;
	 
	 @Autowired
	 private SocialMediaService socialMediaService;
	 
	 @Autowired
	 private QaqcCallsService qaqcCallsService;
	
	 
	 @Autowired
	 public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	 
	 public WhatsappqaqcMsgService(RestTemplate restTemplate) {
		 this.restTemplate=restTemplate;	       
	    }
	
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

    public Map<String, Object> checkforwhatsappmsg(String complaintNumber, String action) {

        Map<String, Object> response = new HashMap<>();

        boolean result = false;

        if ("COMPLETED".equalsIgnoreCase(action)) {

            String sql =
                    "SELECT call_status " +
                    "FROM qaqc_upload_data_history " +
                    "WHERE complaint_number=? " +
                    "ORDER BY id DESC " +
                    "LIMIT 20";

            List<String> statuses =
                    jdbcTemplate.queryForList(
                            sql,
                            String.class,
                            complaintNumber);

            if (!statuses.isEmpty()
                    && "COMPLETED".equalsIgnoreCase(statuses.get(0))) {

                int unattendedCount = 0;

                for (int i = 1; i < statuses.size(); i++) {

                    if ("UNATTENDED".equalsIgnoreCase(statuses.get(i))) {
                        unattendedCount++;
                    } else {
                        break;
                    }
                }

                result = unattendedCount >= 5;
            }
        }

        else if ("UNATTENDED".equalsIgnoreCase(action)) {

            String sql =
                    "SELECT call_status " +
                    "FROM qaqc_upload_data_history " +
                    "WHERE complaint_number=? " +
                    "ORDER BY id DESC " +
                    "LIMIT 5";

            List<String> statuses =
                    jdbcTemplate.queryForList(
                            sql,
                            String.class,
                            complaintNumber);

            int unattendedCount = 0;

            for (String status : statuses) {

                if ("UNATTENDED".equalsIgnoreCase(status)) {
                    unattendedCount++;
                } else {
                    break;
                }
            }

            result = unattendedCount >= 3;
        }

        response.put("action", action);
        response.put("status", result);

        return response;
    }

	public String getclosefromuser(String complaintNumber) {
		
		try {
			
			Map<String, Object> complaintDetails =socialMediaService.getDetailsByComplaintNumber(complaintNumber);
			String currentStatus =
	                String.valueOf(
	                        complaintDetails.getOrDefault(
	                                "currentStatus",
	                                ""
	                        )
	                );
			
			String Erpname="QAops72";
			
			if ("Action Taken - Level 2".equalsIgnoreCase(currentStatus)) {
								
				boolean statusUpdated=qaqcCallsService.ChangeStatusInErp(complaintNumber, "", "COMPLETED",Erpname);
				if (statusUpdated)
				{
					return complaintNumber+ " PGR Complaint closed Successfully";
				}
				else {
					return "Unable to close "+complaintNumber+" PGR Complaint";
				}
			}
			else {
				return "Unable to close PGR "+complaintNumber+" Complaint,Because the complaint status is "+currentStatus;
			}
			
						
		} catch (Exception e) {
			e.printStackTrace();
			return "Error in closing PGR complaint from user "+complaintNumber;
		}

		
	}
	
	
	public String getreopenfromuser(String complaintNumber) {
		
		try {
			
			Map<String, Object> complaintDetails =socialMediaService.getDetailsByComplaintNumber(complaintNumber);
			String currentStatus =
	                String.valueOf(
	                        complaintDetails.getOrDefault(
	                                "currentStatus",
	                                ""
	                        )
	                );
			
			String Erpname="QAops72";
			
			if ("Action Taken - Level 2".equalsIgnoreCase(currentStatus)) {
				
				boolean statusUpdated=qaqcCallsService.ChangeStatusInErp(complaintNumber, "", "REOPEN",Erpname);
				if (statusUpdated)
				{
					return complaintNumber+ " Complaint Reopened Successfully";
				}
				else {
					return "Unable to Reopen "+complaintNumber+" Complaint";
				}
			}
			else {
				return "Unable to Reopen "+complaintNumber+" Complaint,Because the complaint status is "+currentStatus;
			}
			
						
		} catch (Exception e) {
			e.printStackTrace();
			return "Error in Reopen complaint from user "+complaintNumber;
		}

		
	}
    
    
}
