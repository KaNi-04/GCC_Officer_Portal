package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.HashMap;
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
	    public int uploadComplaintDetailsInLogs(Map<String, Object> details,String action,String remarks,int updated_agent,String remainderDate) {
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
		    String cur_status = details.get("currentStatus").toString();
		    
		    String finalremainderDate=null;
		    if(!remainderDate.isEmpty()) {
		    	finalremainderDate=remainderDate;
		    }
		    
		    LocalDateTime currentDate = LocalDateTime.now();
		    
		        String query = "INSERT INTO gcc_1913_qaqc.social_media_completed (complaint_number,zone,complaint_date, complaint_person_name,remarks, "
		                     + "complaint_mobilenumber, complaint_type, complaint_mode, department, official_name,official_mobilenum, call_status,created_date,updated_by,cur_status,remainder_date) "
		                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";

		        int rowsAffected = jdbcTemplate.update(query, complaintNumber,zone, complaintDate,personName,remarksfield,
		                personMobNo, cType, cMode, department, officialName, officialMobileNum,callStatus,
		                 currentDate,updated_by,cur_status,finalremainderDate);
		        //System.out.println("Rows Inserted in table : " + rowsAffected); 
		        	       
		        return rowsAffected;
		        		    
		}

	 public List<Map<String, Object>> getdetails(String formattedFromDate, String formattedToDate, String status, String zone) {
		    StringBuilder query = new StringBuilder("SELECT *,DATE_FORMAT(remainder_date, '%d/%m/%Y') as formatted_fdate FROM gcc_1913_qaqc.social_media_completed WHERE DATE(created_date) BETWEEN ? AND ? ");
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

		    // Execute the query
		    List<Map<String, Object>> results = jdbcTemplate.queryForList(query.toString(), params.toArray());


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

	public List<Map<String, Object>> getA1dropdowns() {
		String sql="SELECT * FROM call_status WHERE isactive=1 AND isdelete=0 AND call_category_id=5";
		try {
	        return jdbcTemplate.queryForList(sql);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return new ArrayList<>(); // return empty list on error
	    }
	}

	public List<Map<String, Object>> getA2dropdowns() {
		String sql="SELECT * FROM call_status WHERE isactive=1 AND isdelete=0 AND call_category_id=6";
		try {
	        return jdbcTemplate.queryForList(sql);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return new ArrayList<>(); // return empty list on error
	    }
	}

	public List<Map<String, Object>> getfiltreddetails(List<Map<String, Object>> details) {

	    List<Map<String, Object>> followupData = getFollowupData();

	    if (followupData == null || followupData.isEmpty()) {
	        return details;
	    }

	    LocalDate currentDate = LocalDate.now();

	    // Store complaint_number -> followup row
	    Map<String, Map<String, Object>> followupMap = new HashMap<>();

	    for (Map<String, Object> followup : followupData) {

	        String complaintNumber = String.valueOf(followup.get("complaint_number"));

	        followupMap.put(complaintNumber, followup);
	    }

	    List<Map<String, Object>> filteredDetails = new ArrayList<>();

	    for (Map<String, Object> detail : details) {

	        String complaintNumber =String.valueOf(detail.get("Complaint Number"));

	        // If complaint not in followup table
	        if (!followupMap.containsKey(complaintNumber)) {

	            filteredDetails.add(detail);
	            continue;
	        }

	        Map<String, Object> followupRow =followupMap.get(complaintNumber);

	        String remainderDateStr =String.valueOf(followupRow.get("remainder_date"));

	        LocalDate remainderDate = LocalDate.parse(remainderDateStr);

	        // remainder_date <= current date
	        if (!remainderDate.isAfter(currentDate)) {

	            // UPDATE is_processed = 0
	            Integer id = (Integer) followupRow.get("id");

	            updateFollowupProcessed(id);

	            filteredDetails.add(detail);
	        }

	        // ELSE:
	        // remainder_date > currentDate
	        // skip / eliminate from frontend
	    }

	    return filteredDetails;
	}
	
	private void updateFollowupProcessed(Integer id) {

	    String sql =
	            "UPDATE social_media_completed " +
	            "SET is_processed = 0 " +
	            "WHERE id = ?";

	    jdbcTemplate.update(sql, id);
	}

	private List<Map<String, Object>> getFollowupData() {
		String sqlQuery = "select id, complaint_number,call_status, remainder_date from social_media_completed where is_processed=1 AND call_status='FOLLOWUP'";
	  	  
		 return jdbcTemplate.queryForList(sqlQuery); 
	}



}
