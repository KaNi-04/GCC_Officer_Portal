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
                    "WHERE complaint_number=? AND call_category='CLOSED' " +
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
                    "WHERE complaint_number=? AND call_category='CLOSED' " +
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
    
    
	public String getErpusername() {
			
			String query="SELECT erp_username FROM agents_list "
					+ "where calling_type='WHATSAPP_MSG'";
						
			return jdbcTemplate.queryForObject(query,String.class);		    
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
			
			
			
			String Erpname=getErpusername();
			
			System.out.println("Erpname="+Erpname);
			
			if ("Action Taken - Level 2".equalsIgnoreCase(currentStatus)) {
								
				boolean statusUpdated=qaqcCallsService.ChangeStatusInErp(complaintNumber, "COMPLAINT CLOSED BY WHATSAPP MESSAGE", "COMPLETED",Erpname);
				if (statusUpdated)
				{
					return "Your "+complaintNumber+ " PGR Complaint has been closed Successfully.\n If you need further assistance,\n Please contact GCC 1913 Helpline.";
				}
				else {
					return "We are unable to process your request at the moment.\n Please try again later.";
				}
			}			
			else {
				if("FINAL_CLOSURE".equalsIgnoreCase(currentStatus)){
					
					return "Your " + complaintNumber + " complaint has already been closed.\n\n"
						     + "If you need further assistance, please contact GCC 1913 Helpline.";
					
				}
				else if("REOPENED".equalsIgnoreCase(currentStatus)) {
					return "Your "+complaintNumber+" complaint has already been reopened. Once it is resolved, we will reach you by GCC’s 1913 helpline. If you need further assistance, please contact GCC 1913 Helpline.";
				}
				else {
					return "Your "+complaintNumber+" complaint is Under Progress.\n Once it is resolved, \n we will reach you by GCC’s 1913 helpline.";
				}
			}
			
						
		} catch (Exception e) {
			e.printStackTrace();
			return "We are unable to process your request at the moment.\n Please try again later.";
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
			
			String Erpname=getErpusername();
			
			System.out.println("Erpname="+Erpname);
			
			if ("Action Taken - Level 2".equalsIgnoreCase(currentStatus)) {
				
				boolean statusUpdated=qaqcCallsService.ChangeStatusInErp(complaintNumber, "COMPLAINT REOPENED BY WHATSAPP MESSAGE", "REOPEN",Erpname);
				if (statusUpdated)
				{
					 return "Your "+complaintNumber+ " PGR complaint has been reopened Successfully.\n Once it is resolved , we will reach you by GCC’s 1913 helpline.If you need further assistance , please contact GCC 1913 Helpline.";
				}
				else {
					return "We are unable to process your request at the moment.\n Please try again later.";
				}
			}
			else {

				if("FINAL_CLOSURE".equalsIgnoreCase(currentStatus)){
					
					return "Your " + complaintNumber + " complaint has already been closed.\n\n"
						     + "If you need further assistance, please contact GCC 1913 Helpline.";
				}
				else if("REOPENED".equalsIgnoreCase(currentStatus)) {
					return "Your "+complaintNumber+" complaint has already been reopened. Once it is resolved, we will reach you by GCC’s 1913 helpline. If you have any further assistance, please contact GCC 1913 Helpline.";
				}
				else {
					return "Your "+complaintNumber+" complaint is Under Progress.\n Once it is resolved, \n we will reach you by GCC’s 1913 helpline.";
				}
			}
			
						
		} catch (Exception e) {
			e.printStackTrace();
			return "We are unable to process your request at the moment.\n Please try again later.";
		}

		
	}
    
    
}
