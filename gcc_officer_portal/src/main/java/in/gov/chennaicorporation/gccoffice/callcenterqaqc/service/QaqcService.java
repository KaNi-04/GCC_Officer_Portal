package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List; 
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class QaqcService {
	

	    private JdbcTemplate jdbcTemplate;
	 
		 @Autowired
		 public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
			 this.jdbcTemplate = new JdbcTemplate(dataSource);
		 }
	 

	    
	    public List<Map<String, Object>> getFollowUpDetailsByStatus(String callStatus, LocalDate fromDate, LocalDate toDate) {
	        // Start the SQL query
	        String sql = "SELECT " +
	                     "f.id, " +
	                     "f.qaqc_id, " +
	                     "f.call_category, " +
	                     "f.complaint_number, " +
	                     "f.agent_id, " +
	                     "f.complaint_person_name, " +
	                     "f.complaint_mobilenumber, " +
	                     "f.complaint_type, " +
	                     "f.complaint_mode, " +
	                     "f.department, " +
	                     "f.complaint_group, " +
	                     "f.official_name, " +
	                     "f.official_mobilenum, " +
	                     "f.call_status, " +
	                     "f.remainder_date, " +
	                     "f.remarks, " +
	                     "f.created_date, " +
	                     "r.taskid " +
	                     "FROM qaqc_followup_call_logs f " +
	                     "LEFT JOIN qaqc_request r ON f.qaqc_id = r.qaqc_id ";

	        // List to hold the query parameters
	        List<Object> params = new ArrayList<>();
	        boolean hasCondition = false;

	        // Filter by callStatus if provided
	        if (callStatus != null && !callStatus.isEmpty()) {
	            sql += " WHERE f.call_status = ?";
	            params.add(callStatus);
	            hasCondition = true;
	        }

	        // Filter by date range (fromDate to toDate) if both are provided
	        if (fromDate != null && toDate != null) {
	            if (hasCondition) {
	                sql += " AND f.remainder_date BETWEEN ? AND ?";
	            } else {
	                sql += " WHERE f.remainder_date BETWEEN ? AND ?";
	                hasCondition = true;
	            }
	            params.add(fromDate);
	            params.add(toDate);
	        }
	        // If only fromDate is provided
	        else if (fromDate != null) {
	            if (hasCondition) {
	                sql += " AND f.remainder_date >= ?";
	            } else {
	                sql += " WHERE f.remainder_date >= ?";
	                hasCondition = true;
	            }
	            params.add(fromDate);
	        }
	        // If only toDate is provided (include records with remainder_date greater than toDate)
	        else if (toDate != null) {
	            if (hasCondition) {
	                sql += " AND (f.remainder_date <= ? OR f.remainder_date > ?)";
	            } else {
	                sql += " WHERE (f.remainder_date <= ? OR f.remainder_date > ?)";
	                hasCondition = true;
	            }
	            params.add(toDate);  // <= toDate condition
	            params.add(toDate);  // > toDate condition
	        }

	        // Final debugging: print the query and parameters
	        //System.out.println("Final SQL Query: " + sql);
	        //System.out.println("Query Parameters: " + params);

	        // Execute the query with the dynamically added parameters
	        return jdbcTemplate.queryForList(sql, params.toArray());
	    }

/*

	    public Map<String, Long> countFollowUpsByDateAndStatus(LocalDate today, String followupStatus) {
	    	String sqlNotClosed = "SELECT COUNT(*) FROM qaqc_followup_call_logs " +
                    "WHERE call_status = ? AND DATE(remainder_date) = ? AND call_category NOT IN ('CLOSED')";

			String sqlClosed = "SELECT COUNT(*) FROM qaqc_followup_call_logs " +
			                 "WHERE call_status = ? AND DATE(remainder_date) = ? AND call_category IN ('CLOSED')";
			
			long countNotClosed = jdbcTemplate.queryForObject(sqlNotClosed, new Object[]{followupStatus, today}, Long.class);
			long countClosed = jdbcTemplate.queryForObject(sqlClosed, new Object[]{followupStatus, today}, Long.class);
			
			Map<String, Long> result = new HashMap<>();
			result.put("officialfollowup", countNotClosed);
			result.put("publicfollowup", countClosed);
			
			return result;
	    }
	    */
	    public Map<String, Long> countFollowUpsByDateAndStatus(LocalDate today, String followupStatus) {
	    	String sqlNotClosed = "SELECT COUNT(*) FROM qaqc_followup_call_logs " +
                    "WHERE call_status = ? AND DATE(remainder_date) = ? AND call_category NOT IN ('CLOSED','PUBLICFOLLOWUP')";
	    	
	    	String sqlClosed = "SELECT COUNT(*) FROM qaqc_followup_call_logs " +
		"WHERE call_status = ? AND DATE(remainder_date) = ? AND call_category IN ('CLOSED','PUBLICFOLLOWUP')";
			
			long countNotClosed = jdbcTemplate.queryForObject(sqlNotClosed, new Object[]{followupStatus, today}, Long.class);
			long countClosed = jdbcTemplate.queryForObject(sqlClosed, new Object[]{followupStatus, today}, Long.class);
			
			Map<String, Long> result = new HashMap<>();
			result.put("officialfollowup", countNotClosed);
			result.put("publicfollowup", countClosed);
			
			return result;
	    }
	    

	    public List<Map<String, Object>> getUnattendedlist(Integer ucount) {
	        	        
	        String query1="SELECT \r\n"
	        		+ "            MIN(complaint_date) AS complaint_date, \r\n"
	        		+ "            MIN(call_category) AS call_category, \r\n"
	        		+ "            MIN(complaint_person_name) AS complaint_person_name, \r\n"
	        		+ "            MIN(complaint_mobilenumber) AS complaint_mobilenumber, \r\n"
	        		+ "            complaint_number AS complaint_number, \r\n"
	        		+ "            MIN(complaint_group) AS complaint_group, \r\n"
	        		+ "            MIN(complaint_type) AS complaint_type, \r\n"
	        		+ "            MIN(complaint_mode) AS complaint_mode \r\n"
	        		+ "        FROM qaqc_upload_data_history \r\n"
	        		+ "        WHERE unattended_msg = 0 \r\n"
	        		+ "        AND call_status = 'UNATTENDED' \r\n"
	        		+ "        AND call_category IN ('CLOSED', 'PUBLICFOLLOWUP') \r\n"
	        		+ "        GROUP BY complaint_number \r\n"
	        		 + "        HAVING COUNT(*) >= ? \r\n"
	                 + "        LIMIT 100";

	        
	        // Log query and parameters for debugging
	        //System.out.println("Executing query: " + query1);
	        //System.out.println("With parameters: " + ucount);
	        
	        List<Map<String, Object>> results = jdbcTemplate.queryForList(query1, ucount);

	        // Log results for debugging
	        //System.out.println("Query results: " + results);

	        return results.isEmpty() ? Collections.emptyList() : results;
	    }
	    
	    
	    
	    
	    public boolean ChangeStatusInErp(String complaintNumber,String erp_name,String remarks) {
		    String baseUrl = "https://erp.chennaicorporation.gov.in/pgr/external/mobileservice?";
		    String status = "11";
		    		   	    	 	       
		   //String Erpname ="QAops1";
		  
		    try {
		        // Encode parameters
		        String encodedComplaintNumber = URLEncoder.encode(complaintNumber, StandardCharsets.UTF_8.toString()).replace("+", "%20");
		        String encodedRemarks = URLEncoder.encode(remarks, StandardCharsets.UTF_8.toString()).replace("+", "%20");
		        String encodedErpname = URLEncoder.encode(erp_name, StandardCharsets.UTF_8.toString()).replace("+", "%20");

		        //System.out.println("encodedComplaintNumber===="+encodedComplaintNumber);
		        //System.out.println("encodedErpname===="+encodedErpname);
		        //System.out.println("encodedRemarks===="+encodedRemarks);
		        
 		        // Construct the URL
		        String urlString = baseUrl
		                + "serviceId=UpdateComplaint"
		                + "&ComplaintId=" + encodedComplaintNumber
		                + "&Message=" + encodedRemarks
		                + "&UserType=Official"
		                + "&Status=" + status
		                + "&Comp_Image="
		                + "&erp_username=" + encodedErpname;

		        System.out.println("ERP Hitting URL="+urlString);

		        // Send the request
		        return sendRequest(urlString);
		        
		       //return true;
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


		public String getErpusernamebyId(String userId) {
			
			String query="SELECT erp_username FROM gcc_1913_qaqc.agents_list\r\n"
					+ "where calling_type='1913ADMIN' and agent_id=?";
			
			//System.out.println("query"+query);
			
			//String erpUsername = jdbcTemplate.queryForObject(query,new Object[]{userId},String.class);
			String erpUsername = jdbcTemplate.queryForObject(query,String.class,userId);
			
		     return erpUsername;
		}



		public int UpdateInHistoryTable(List<String> completedComplaints) {
		    int totalUpdated = 0;

		    for (String cnum : completedComplaints) {
		        String query = "UPDATE qaqc_upload_data_history SET unattended_msg = 1 WHERE complaint_number = ?";
		        
		        // Update using JdbcTemplate and accumulate the count
		        int rowsUpdated = jdbcTemplate.update(query, cnum);
		        
		        // Accumulate the number of successfully updated rows
		        totalUpdated += rowsUpdated;
		    }

		    return totalUpdated; // Return the total number of updated rows
		}



		public int insertInLogs(List<String> completedComplaints,String remarks,String userId) {
						
			int totalUpdated = 0;
			int rowsUpdated=0;

		    for (String cnum : completedComplaints) {
		    	
		    	List<Map<String,Object>>details=getComplaintsLatestDetails(cnum);
		    	
		    	if(!details.isEmpty()) {
		    		
		    		for (Map<String, Object> detail : details) {		    			   
		    			 int qaqc_id = Integer.parseInt(detail.get("qaqc_id").toString());
		    			 String call_category = "UNATTENDED";
		    			 int agent_id = Integer.parseInt(detail.get("agent_id").toString());

		    			// # id, qaqc_id, call_category, agent_id, complaint_number, complaint_date, complaint_person_name,
		    			// complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, 
		    			// call_status, remainder_date, remarks, created_date, is_processed, updated_agent
		    			 String complaint_number = detail.get("complaint_number").toString();
		    			 String complaint_date = detail.get("complaint_date").toString();
		    			 String complaint_person_name = detail.get("complaint_person_name").toString();
		    			 String complaint_mobilenumber = detail.get("complaint_mobilenumber").toString();
		    			 String complaint_type = detail.get("complaint_type").toString();
		    			 String complaint_mode = detail.get("complaint_mode").toString();
		    			 String department = detail.get("department").toString();
		    			 String complaint_group = detail.get("complaint_group").toString();
		    			 String official_name = detail.get("official_name").toString();
		    			 String official_mobilenum = detail.get("official_mobilenum").toString();
		    			 String call_status = "COMPLETED";
		    			 String upremarks=remarks;	
		    			 int is_processed=1;
		    			 int updated_agent = Integer.parseInt(userId);
		    			 
		    			 String query = "INSERT INTO gcc_1913_qaqc.qaqc_call_logs (qaqc_id, call_category, agent_id, complaint_number, complaint_date,complaint_person_name, "
			                     + "complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, call_status, remarks, is_processed,updated_agent) "
			                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

		    			 rowsUpdated = jdbcTemplate.update(query, qaqc_id, call_category, agent_id, complaint_number,complaint_date, complaint_person_name,
		    					 complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, call_status, upremarks,
		    					 is_processed,updated_agent);
		    			 
				         totalUpdated += rowsUpdated;
		    			 
		    		}	    		
		    		
		    	}
		    	else {
		    		return 0; 
		    	}
		        
		    }

		    return totalUpdated; 
		}


		public  List<Map<String,Object>> getComplaintsLatestDetails(String complaint_number){
			
		    String query = "SELECT * FROM qaqc_upload_data_history WHERE complaint_number = ? ORDER BY created_date DESC LIMIT 1";
			
	        List<Map<String,Object>> getCompNumber = jdbcTemplate.queryForList(query, complaint_number);
	        
	        return getCompNumber;
		}
	    
	    
}
