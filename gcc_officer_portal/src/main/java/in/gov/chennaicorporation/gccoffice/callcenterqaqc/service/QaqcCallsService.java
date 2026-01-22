package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Transactional
public class QaqcCallsService {
	 
	 @Autowired
	 private JdbcTemplate jdbcTemplate;
	 
	 @Autowired
	 public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	 
	 public List<Map<String, Object>> getClosedComplaints(int agent_id) {
		    String query = "SELECT data_id, complaint_number, complaint_date,complaint_person_name, complaint_mobilenumber, complaint_type,official_name, official_mobilenum,complaint_mode, department,call_category " +
		                   "FROM gcc_1913_qaqc.qaqc_upload_data " +
		                   "WHERE call_category = 'CLOSED' " +
		                   "  AND is_processed = FALSE " + // Fetch only unprocessed entries
		                   "  AND agent_id = ?";
		    return jdbcTemplate.queryForList(query, agent_id);
		}

	 
	    public List<Map<String, Object>> getCallCategories() 
	    {
	    	String query = "SELECT call_status " +
                    "FROM gcc_1913_qaqc.call_status WHERE call_category_id ='1' and isactive='1'";
	        return jdbcTemplate.queryForList(query);	    	
	    }
	    
	    
	    @Transactional
	    public int uploadComplaintDetailsInLogs(Map<String, Object> logDetails, String action,  String complaintNumber, String remainderDate) {
	    	//System.out.println("Complaint Details: " + logDetails);
		    // Extract other field values from complaintDetails
		    int qaqcid = (int) logDetails.get("qaqc_id");
		    String ccategory = logDetails.get("call_category").toString();
		    String complaintDate = logDetails.get("complaint_date").toString();
		    String personName = logDetails.get("complaint_person_name").toString();
		    String personMobNo = logDetails.get("complaint_mobilenumber").toString();
		    String cType = logDetails.get("complaint_type").toString();
		    String cMode = logDetails.get("complaint_mode").toString();
		    String department = logDetails.get("department").toString();
		    String cGroup = logDetails.get("complaint_group").toString();
		    String officialName = logDetails.get("official_name").toString();
		    String officialMobileNum = logDetails.get("official_mobilenum").toString();
		    String callStatus = logDetails.get("call_status").toString();
		    String remarks = logDetails.get("remarks").toString();
		    int agentId = (int) logDetails.get("agent_id");
		    int updated_agent=(int)logDetails.get("updated_agent");
		    boolean isProcessed = (boolean)logDetails.get("is_processed");
		    LocalDateTime currentDate = LocalDateTime.now();

		    // Determine which table to insert into based on the action
		    if (action.equals("FOLLOWUP")) {
		        String query = "INSERT INTO gcc_1913_qaqc.qaqc_followup_call_logs (qaqc_id, call_category, agent_id, complaint_number,complaint_date, complaint_person_name, "
		                     + "complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, remainder_date,official_mobilenum, call_status, remarks,created_date,updated_agent) "
		                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

		        int rowsAffected = jdbcTemplate.update(query, qaqcid, ccategory, agentId, complaintNumber, complaintDate,personName,
		                personMobNo, cType, cMode, department, cGroup, officialName,remainderDate, officialMobileNum,callStatus,
		                 remarks,currentDate,updated_agent);
		        //System.out.println("Rows Inserted in qaqc_followup_call_logs : " + rowsAffected); 
		        
		        String query1 = "INSERT INTO gcc_1913_qaqc.qaqc_call_logs (qaqc_id, call_category, agent_id, complaint_number,complaint_date, complaint_person_name, "
	                    + "complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name,remainder_date, official_mobilenum, call_status, remarks, is_processed,updated_agent) "
	                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

	       int rowsAffected1 = jdbcTemplate.update(query1, qaqcid, ccategory, agentId, complaintNumber, complaintDate,personName,
	               personMobNo, cType, cMode, department, cGroup, officialName,remainderDate, officialMobileNum, callStatus, remarks,
	               isProcessed,updated_agent);
	       //System.out.println("Rows Inserted in qaqc_call_logs : " + rowsAffected1); 

	       
		        return rowsAffected + rowsAffected1;
		        
		    } else {
		        String query = "INSERT INTO gcc_1913_qaqc.qaqc_call_logs (qaqc_id, call_category, agent_id, complaint_number, complaint_date,complaint_person_name, "
		                     + "complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, call_status, remarks, is_processed,updated_agent) "
		                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

		        int rowsAffected = jdbcTemplate.update(query, qaqcid, ccategory, agentId, complaintNumber,complaintDate, personName,
		                personMobNo, cType, cMode, department, cGroup, officialName, officialMobileNum, callStatus, remarks,
		                isProcessed,updated_agent);
		        
		        //System.out.println("Rows Inserted in qaqc_call_logs: " + rowsAffected);

		        return rowsAffected;
		    }
		}
	    
	    public void updateComplaintDetails(String complaintNumber,int dataId, String action, String remarks,int updated_agent)
		  { 
	    	String query = "UPDATE gcc_1913_qaqc.qaqc_upload_data " +
		  "SET call_status = ?, " + "    remarks = ?,updated_agent=?, " +
		  "    updated_date = CURRENT_TIMESTAMP, " + "    is_processed = TRUE " + 
		  "WHERE data_id = ? "  +"  AND complaint_number = ? " +"  AND call_category = 'CLOSED'";
		  
		  int rowsUpdated = jdbcTemplate.update(query, action, remarks,updated_agent, dataId,complaintNumber);
		  
		  //System.out.println("Rows Updated in upload data: " + rowsUpdated); 
		  }
	    
	    public Map<String, Object> getComplaintDetails(int dataId) {
			String query = "Select * from gcc_1913_qaqc.qaqc_upload_data where data_id = ?";
			return jdbcTemplate.queryForMap(query, dataId);
		}

		public Map<String, Object> getErpnameById(Integer agentId) {
			String query = "Select erp_username from agents_list where agent_id = ?";
			return jdbcTemplate.queryForMap(query, agentId);
		}

//		public boolean ChangeStatusInErp(String complaintNumber, String remarks, String action,String Erpname) {
//			String urlString = "";
//			
//			if(remarks.isEmpty()) {
//				remarks="N/A";
//			}
//			
//			if("COMPLETED".equals(action) || "WRONG_ASSIGN".equals(action)) {
//				
//				urlString="https://erp.chennaicorporation.gov.in/pgr/external/mobileservice?serviceId=UpdateComplaint&ComplaintId="+complaintNumber+"&Message="+remarks+"&UserType=Official&Status=11&erp_username="+Erpname;
//				System.out.println(urlString);
//				//urlencode
//				try {					
//					
//					URL url = new URL(urlString);
//					URLConnection urlcon=url.openConnection();    
//					InputStream stream=urlcon.getInputStream(); 
//					 StringBuilder responseBuilder = new StringBuilder();
//					int i;    
//					while((i=stream.read())!=-1){    
//					responseBuilder.append((char) i);
//					 }  
//					stream.close();
//
//		            // Parse the response
//		            String response = responseBuilder.toString();
//		            System.out.println("Raw Response from ERP if Completed: " + response); // Debug: Print the raw response
//
//		            // Check ResultStatus in the response
//		            if (response.contains("\"ResultStatus\": true")) {
//		            	System.out.println("ResultStatus is true in the response."); // Debug: Positive ResultStatus
//		                return true;
//		            }
//		            else {
//		                System.out.println("ResultStatus is false or not present in the response."); // Debug: Negative or missing ResultStatus
//		                return false;
//		            }
//				} catch (Exception e) {
//					e.printStackTrace();
//					 return false;
//				}
//			}
//			
//			if("REOPEN".equals(action)) {
//				urlString="https://erp.chennaicorporation.gov.in/pgr/external/mobileservice?serviceId=UpdateComplaint&ComplaintId="+complaintNumber+"&Message="+remarks+"&UserType=Official&Status=9&Comp_Image=64baseString&erp_username="+Erpname;
//				System.out.println(urlString);
//				try {
//					URL url = new URL(urlString);
//					URLConnection urlcon=url.openConnection();    
//					InputStream stream=urlcon.getInputStream(); 
//					 StringBuilder responseBuilder = new StringBuilder();
//					int i;    
//					while((i=stream.read())!=-1){    
//					responseBuilder.append((char) i);
//					}  
//					stream.close();
//
//		            // Parse the response
//		            String response = responseBuilder.toString();
//		            System.out.println("Raw Response from ERP if Reopen: " + response);
//		            
//		            // Check ResultStatus in the response
//		            if (response.contains("\"ResultStatus\": true")) {
//		            	System.out.println("ResultStatus is true in the response."); // Debug: Positive ResultStatus
//		                return true;
//		            }
//		            else {
//		                System.out.println("ResultStatus is false or not present in the response."); // Debug: Negative or missing ResultStatus
//		                return false;
//		            }
//				} catch (Exception e) {
//					e.printStackTrace();
//					 return false;
//				}
//			}
//			return false;
//						
//		}
		
//		public boolean ChangeStatusInErp(String complaintNumber, String remarks, String action, String Erpname) {
//		    String urlString = "";
//
//		    if (remarks.isEmpty()) {
//		        remarks = "N/A";
//		    }
//
//		    try {
//		        // Encode parameters
//		    	String encodedComplaintNumber = URLEncoder.encode(complaintNumber, StandardCharsets.UTF_8.toString()).replace("+", "%20");
//				  String encodedRemarks = URLEncoder.encode(remarks, StandardCharsets.UTF_8.toString()).replace("+", "%20");
//				  String encodedErpname = URLEncoder.encode(Erpname, StandardCharsets.UTF_8.toString()).replace("+", "%20");
//
//		        if ("COMPLETED".equals(action) || "WRONG_ASSIGN".equals(action)) {
//		            urlString = "https://erp.chennaicorporation.gov.in/pgr/external/mobileservice?"
//		                    + "serviceId=UpdateComplaint"
//		                    + "&ComplaintId=" + encodedComplaintNumber
//		                    + "&Message=" + encodedRemarks
//		                    + "&UserType=Official"
//		                    + "&Status=11"
//		                    + "&erp_username=" + encodedErpname;
//
//		            System.out.println(urlString);
//
//		            return sendRequest(urlString);
//		        }
//
//		        if ("REOPEN".equals(action)) {
//		            urlString = "https://erp.chennaicorporation.gov.in/pgr/external/mobileservice?"
//		                    + "serviceId=UpdateComplaint"
//		                    + "&ComplaintId=" + encodedComplaintNumber
//		                    + "&Message=" + encodedRemarks
//		                    + "&UserType=Official"
//		                    + "&Status=9"
//		                    + "&Comp_Image=64baseString"
//		                    + "&erp_username=" + encodedErpname;
//
//		            System.out.println(urlString);
//
//		            return sendRequest(urlString);
//		        }
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		        return false;
//		    }
//
//		    return false;
//		}
		
		public boolean ChangeStatusInErp(String complaintNumber, String remarks, String action, String Erpname) {
		    String baseUrl = "https://erp.chennaicorporation.gov.in/pgr/external/mobileservice?";
		    String status = "";
		    
		    if (remarks.isEmpty()) {
		        remarks = "N/A";
		    }

		    try {
		        // Encode parameters
		        String encodedComplaintNumber = URLEncoder.encode(complaintNumber, StandardCharsets.UTF_8.toString()).replace("+", "%20");
		        String encodedRemarks = URLEncoder.encode(remarks, StandardCharsets.UTF_8.toString()).replace("+", "%20");
		        String encodedErpname = URLEncoder.encode(Erpname, StandardCharsets.UTF_8.toString()).replace("+", "%20");

		        // Determine status based on action
		        if ("COMPLETED".equals(action) || "WRONG_ASSIGN".equals(action)) {
		            status = "11";
		        } else if ("REOPEN".equals(action)) {
		            status = "9";
		        } else {
		            return false; // Invalid action, return false
		        }

		        // Construct the URL
		        String urlString = baseUrl
		                + "serviceId=UpdateComplaint"
		                + "&ComplaintId=" + encodedComplaintNumber
		                + "&Message=" + encodedRemarks
		                + "&UserType=Official"
		                + "&Status=" + status
		                + "&Comp_Image="
		                + "&erp_username=" + encodedErpname;

		        System.out.println(urlString);

		        // Send the request
		        return sendRequest(urlString);

		    } catch (Exception e) {
		        e.printStackTrace();
		        return false;
		    }
		}


		private boolean sendRequest(String urlString) {
		    try {
		        URL url = new URL(urlString);
		        URLConnection urlcon = url.openConnection();
		        InputStream stream = urlcon.getInputStream();
		        StringBuilder responseBuilder = new StringBuilder();
		        int i;
		        while ((i = stream.read()) != -1) {
		            responseBuilder.append((char) i);
		        }
		        stream.close();

		        // Parse the response
		        String response = responseBuilder.toString();
		        //System.out.println("Raw Response: " + response);

		        // Check ResultStatus in the response
		        if (response.contains("\"ResultStatus\": true")) {
		            //System.out.println("ResultStatus is true in the response.");
		            return true;
		        } else {
		            System.out.println("ResultStatus is false or not present in the response.");
		            return false;
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        return false;
		    }
		}
		
		
		public void saveMobileNumDetails(String complaintNumber, String remarks) {
	        String query = "insert into officer_mobileno (complaint_number,remarks) values(?,?)";
	        int rowsUpdated = jdbcTemplate.update(query, complaintNumber, remarks);
	        //System.out.println("Rows Updated: " + rowsUpdated);
	    }

	    public Map<String,Object> checkComplaintDetail(String complaintNumber) {
	        String query = "select id,complaint_number from qaqc_followup_call_logs where complaint_number = ?";
	        try {
	            return jdbcTemplate.queryForMap(query, complaintNumber);
	        } catch (Exception e) {
	            System.out.println("Complaint number not found");
	            return null;
	        }
	    }

	    public void updateFollowupCallLogs(String complaintNumber, String action, String remarks, int followupid) {
	    	//System.out.println(complaintNumber+action+remarks);
	    	
	        String query = "update gcc_1913_qaqc.qaqc_followup_call_logs set call_status = ?,remarks = ? where complaint_number= ? and id =?";
	        
	        int rowsUpdated = jdbcTemplate.update(query, action, remarks, complaintNumber, followupid);
	        //System.out.println("Rows Updated in followup: " + rowsUpdated);

	    }
	    
	    public List<Map<String, Object>> getSubmitDetails(int userId) {
	    	
	    	 String query = "SELECT complaint_number, complaint_date,complaint_person_name, complaint_mobilenumber, " +
                    "complaint_type, call_status, remarks " +
                    "FROM gcc_1913_qaqc.qaqc_upload_data " +
                    "WHERE agent_id = ? AND call_category = 'CLOSED' AND is_processed = '1'";
	    	 
			return jdbcTemplate.queryForList(query,userId);
			  
		}

		public void updateComplaintHistoryDetails(String complaintNumber, int dataId, String action, String remarks,int updated_agent,
				String remainderDate) {
		
			String query = "UPDATE gcc_1913_qaqc.qaqc_upload_data_history \r\n"
					+ "SET call_status = ?, remarks = ?, updated_date = CURRENT_TIMESTAMP, remainder_date = ?,updated_agent=?\r\n"
					+ "WHERE data_id = ?   AND complaint_number = ?  AND call_category = 'CLOSED'";
					  
					  int rowsUpdated = jdbcTemplate.update(query, action, remarks, remainderDate,updated_agent, dataId, complaintNumber);
					  
					  //System.out.println("Rows Updated in upload history: " + rowsUpdated); 
			
		}
	    
}

