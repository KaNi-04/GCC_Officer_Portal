package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SocialMediaService {
	
	 @Autowired
	 private JdbcTemplate jdbcTemplate;
	 
	 @Autowired
	 public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	 
	 @Transactional
	    public int uploadComplaintDetailsInLogs(Map<String, Object> details,String action,String remarks,int updated_agent) {
	    	//System.out.println("Complaint Details: " + details);
		    // Extract other field values from complaintDetails
		    String complaintNumber = details.get("complaintid").toString();
		    String complaintDate = details.get("complaintdate").toString();
		    String personName = details.get("complainantName").toString();
		    String personMobNo = details.get("mobileNumber").toString();
		    String cType = details.get("complaintType").toString();
		    String cMode = details.get("compmode").toString();
		    String department = details.get("deptname").toString();
		    String officialName = details.get("Officialname").toString();
		    String officialMobileNum = details.get("OfficialMobile").toString();
		    String callStatus = action;
		    String remarksfield = remarks;
		    String zone = details.get("Zone").toString();
		    int updated_by=updated_agent;
		    
		    LocalDateTime currentDate = LocalDateTime.now();
		    
		        String query = "INSERT INTO gcc_1913_qaqc.social_media_completed (complaint_number,zone,complaint_date, complaint_person_name,remarks, "
		                     + "complaint_mobilenumber, complaint_type, complaint_mode, department, official_name,official_mobilenum, call_status,created_date,updated_by) "
		                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

		        int rowsAffected = jdbcTemplate.update(query, complaintNumber,zone, complaintDate,personName,remarksfield,
		                personMobNo, cType, cMode, department, officialName, officialMobileNum,callStatus,
		                 currentDate,updated_by);
		        //System.out.println("Rows Inserted in table : " + rowsAffected); 
		        	       
		        return rowsAffected;
		        		    
		}

	 public List<Map<String, Object>> getdetails(String formattedFromDate, String formattedToDate, String status, String zone) {
		    StringBuilder query = new StringBuilder("SELECT * FROM gcc_1913_qaqc.social_media_completed WHERE DATE(created_date) BETWEEN ? AND ? ");
		    List<Object> params = new ArrayList<>();
		    params.add(formattedFromDate);
		    params.add(formattedToDate);

		    if (zone != null && !zone.isEmpty()) {
		        query.append(" AND zone = ?");
		        params.add(zone);
		    }

		    if (status != null && !status.equals("ALL")) {
		        query.append(" AND call_status = ?");
		        params.add(status);
		    }

		    // Log the query and parameters for debugging
		    //System.out.println("Executing query: " + query.toString());
		    //System.out.println("With parameters: " + params);

		    // Execute the query
		    List<Map<String, Object>> results = jdbcTemplate.queryForList(query.toString(), params.toArray());

		    // Log the results for debugging
		    //System.out.println("Query results: " + results);

		    return results.isEmpty() ? null : results;
		}

	public String getErpusernamebyId(String userId) {
		
		String query="SELECT erp_username FROM gcc_1913_qaqc.agents_list\r\n"
				+ "where calling_type='SOCIALMEDIA' and agent_id=?";
		
		//System.out.println("query====="+query);
		
		//String erpUsername = jdbcTemplate.queryForObject(query,new Object[]{userId},String.class);
		String erpUsername = jdbcTemplate.queryForObject(query,String.class,userId);
		
	     return erpUsername;
	}



}
