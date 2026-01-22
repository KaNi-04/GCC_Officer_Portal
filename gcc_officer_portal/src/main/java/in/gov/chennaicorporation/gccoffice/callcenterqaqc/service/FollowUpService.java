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

@Service	
public class FollowUpService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	 public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	
	public List<Map<String, Object>> getFollowUpCalls(int agent_id) {
	    String query = "SELECT data_id, complaint_number, complaint_date,complaint_person_name, complaint_mobilenumber, " +
                       "complaint_type, official_name, official_mobilenum, call_category, complaint_mode, department " +	                   
	                   "FROM gcc_1913_qaqc.qaqc_upload_data " +
	                   "WHERE call_category = 'OFFICIALFOLLOWUP' AND agent_id = ? AND is_processed = FALSE";
	    
	    return jdbcTemplate.queryForList(query, agent_id);
	}
	
	public List<Map<String, Object>> getFollowUpCatergory()
	{
		String query = "SELECT call_status " +  
                "FROM gcc_1913_qaqc.call_status WHERE call_category_id = 2 and isactive='1'";
		
		return jdbcTemplate.queryForList(query);
	}

//public List<Map<String, Object>> getAgentCallStatus(int agentId) {
//		
//		String query = "SELECT " +
//                "    (SELECT SUM(aa.data_count) " +
//                "     FROM agent_assigned aa " +
//                "     WHERE aa.agent_id = ?) AS total_data_count, " +
//                "    COUNT(CASE WHEN cl.call_status = 'COMPLETED' THEN 1 END) AS completed_count, " +
//                "    COUNT(CASE WHEN cl.call_status != 'COMPLETED' THEN 1 END) AS pending_count " +
//                "FROM " +
//                "    qaqc_call_logs cl " +
//                "WHERE " +
//                "    cl.agent_id = ?";
//		return jdbcTemplate.queryForList(query,agentId);
//        
//	}
	
	public void updateFollowupComplaintDetails(String complaintNumber,int dataId, String action, String remarks,int updated_agent)
	  { 
	String query = "UPDATE gcc_1913_qaqc.qaqc_upload_data " +
	  "SET call_status = ?, " + "    remarks = ?, updated_agent=?," +
	  "    updated_date = CURRENT_TIMESTAMP, " + "    is_processed = TRUE " + 
	  "WHERE data_id = ? "  +"  AND complaint_number = ?	";
	  
	  int rowsUpdated = jdbcTemplate.update(query, action, remarks,updated_agent, dataId,complaintNumber);
	  
	  //System.out.println("Rows Updated in upload data: " + rowsUpdated); 
	  }
	
	@Transactional
    public int uploadComplaintDetailsInLogs(Map<String, Object> logDetails, String action,  String complaintNumber, String remainderDate) {
    	//System.out.println("Complaint Details: " + logDetails);
	    // Extract other field values from complaintDetails
	    int qaqcid = (int) logDetails.get("qaqc_id");
	    String ccategory = logDetails.get("call_category").toString();
	    String personName = logDetails.get("complaint_person_name").toString();
	    String personMobNo = logDetails.get("complaint_mobilenumber").toString();
	    String complaintDate = logDetails.get("complaint_date").toString();
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
	    	
	    			
	    	String query ="INSERT INTO gcc_1913_qaqc.qaqc_followup_call_logs (qaqc_id, call_category, agent_id, complaint_number, complaint_date,complaint_person_name, "
                    + "complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name,remainder_date, official_mobilenum, call_status, remarks,updated_agent) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
	    	

	    	int rowsAffected = jdbcTemplate.update(query, qaqcid, ccategory, agentId, complaintNumber, complaintDate,personName,
               personMobNo, cType, cMode, department, cGroup, officialName,remainderDate, officialMobileNum, callStatus, remarks,
               updated_agent);
	    	//System.out.println("Rows Updated in qaqc_followup_call_logs : " + rowsAffected); 
	    } 
	    
	    String query1 = "INSERT INTO gcc_1913_qaqc.qaqc_call_logs (qaqc_id, call_category, agent_id, complaint_number,complaint_date, complaint_person_name, "
                + "complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, call_status, remarks, is_processed,updated_agent) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

	    int rowsAffected1 = jdbcTemplate.update(query1, qaqcid, ccategory, agentId, complaintNumber,complaintDate, personName,
           personMobNo, cType, cMode, department, cGroup, officialName, officialMobileNum, callStatus, remarks,
           isProcessed,updated_agent);
   
	    //System.out.println("Rows Updated in qaqc_call_logs: " + rowsAffected1);
	        
	    return rowsAffected1;
	    
	}

	/*
	@Transactional
    public int uploadComplaintDetailsInLogs(Map<String, Object> logDetails, String action,  String complaintNumber, String remainderDate) {
    	//System.out.println("Complaint Details: " + logDetails);
	    // Extract other field values from complaintDetails
	    int qaqcid = (int) logDetails.get("qaqc_id");
	    String ccategory = logDetails.get("call_category").toString();
	    String personName = logDetails.get("complaint_person_name").toString();
	    String personMobNo = logDetails.get("complaint_mobilenumber").toString();
	    String complaintDate = logDetails.get("complaint_date").toString();
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
	    String query = "UPDATE gcc_1913_qaqc.qaqc_followup_call_logs "
	             + "SET qaqc_id = ?, call_category = ?, agent_id = ?, remainder_date = ?, call_status = ?, remarks = ?, updated_agent = ?, updated_date = CURRENT_TIMESTAMP "
	             + "WHERE complaint_number = ? "
	             + "AND id = (SELECT max_id FROM (SELECT MAX(id) AS max_id FROM gcc_1913_qaqc.qaqc_followup_call_logs WHERE complaint_number = ?) AS temp)";

	   int rowsAffected = jdbcTemplate.update(query,qaqcid, ccategory,agentId,remainderDate, callStatus, remarks,updated_agent,complaintNumber,complaintNumber);
       //System.out.println("Rows Updated in qaqc_followup_call_logs : " + rowsAffected); 
	        
	        
	        String query1 = "INSERT INTO gcc_1913_qaqc.qaqc_call_logs (qaqc_id, call_category, agent_id, complaint_number, complaint_date,complaint_person_name, "
                    + "complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name,remainder_date, official_mobilenum, call_status, remarks, is_processed,updated_agent) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

       int rowsAffected1 = jdbcTemplate.update(query1, qaqcid, ccategory, agentId, complaintNumber, complaintDate,personName,
               personMobNo, cType, cMode, department, cGroup, officialName,remainderDate, officialMobileNum, callStatus, remarks,
               isProcessed,updated_agent);
       //System.out.println("Rows Updated in qaqc_call_logs : " + rowsAffected1); 

	        return rowsAffected + rowsAffected1;
	        
	    } else {
	    	
	    	 String query = "UPDATE gcc_1913_qaqc.qaqc_followup_call_logs "
		             + "SET qaqc_id = ?, call_category = ?, agent_id = ?, call_status = ?, remarks = ?, updated_agent = ?, updated_date = CURRENT_TIMESTAMP "
		             + "WHERE complaint_number = ? "
		             + "AND id = (SELECT max_id FROM (SELECT MAX(id) AS max_id FROM gcc_1913_qaqc.qaqc_followup_call_logs WHERE complaint_number = ?) AS temp)";

		   int rowsAffected = jdbcTemplate.update(query,qaqcid, ccategory,agentId, callStatus, remarks,updated_agent,complaintNumber,complaintNumber);
	       //System.out.println("Rows Updated in qaqc_followup_call_logs : " + rowsAffected);
	    	
	        String query1 = "INSERT INTO gcc_1913_qaqc.qaqc_call_logs (qaqc_id, call_category, agent_id, complaint_number,complaint_date, complaint_person_name, "
	                     + "complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, call_status, remarks, is_processed,updated_agent) "
	                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

	        int rowsAffected1 = jdbcTemplate.update(query1, qaqcid, ccategory, agentId, complaintNumber,complaintDate, personName,
	                personMobNo, cType, cMode, department, cGroup, officialName, officialMobileNum, callStatus, remarks,
	                isProcessed,updated_agent);
	        
	        //System.out.println("Rows Updated in qaqc_call_logs: " + rowsAffected1);

	        return rowsAffected + rowsAffected1;
	    }
	}
	*/

	public List<Map<String, Object>> getCallHistory(String complaintNumber)
	{
		 String query = "SELECT complaint_number, agent_id, remainder_date, remarks, created_date " +
	             "FROM gcc_1913_qaqc.qaqc_call_logs " +
	             "WHERE complaint_number = ? and call_status='FOLLOWUP'";
		 return jdbcTemplate.queryForList(query, complaintNumber);
	
	 }
	
	public List<Map<String, Object>> getFollowupSubmitDetails(int userId) {
    	
	   	 String query = "SELECT complaint_number, complaint_date,complaint_person_name, complaint_mobilenumber,official_name,official_mobilenum, " +
	               "complaint_type, call_status, remarks " +
	               "FROM gcc_1913_qaqc.qaqc_upload_data " +
	               "WHERE call_category = 'OFFICIALFOLLOWUP' AND agent_id = ? AND is_processed = '1'";
	   	 
			return jdbcTemplate.queryForList(query,userId);
			  
		}

	public void updateComplaintHistoryDetails(String complaintNumber, int dataId, String action, String remarks,int updated_agent,
				String remainderDate) {
			
			String query = "UPDATE gcc_1913_qaqc.qaqc_upload_data_history \r\n"
					+ "SET call_status = ?, remarks = ?, updated_date = CURRENT_TIMESTAMP, remainder_date = ?,updated_agent=?\r\n"
					+ "WHERE data_id = ?   AND complaint_number = ?";
					  
					  int rowsUpdated = jdbcTemplate.update(query, action, remarks, remainderDate, updated_agent,dataId, complaintNumber);
					  
					  //System.out.println("Rows Updated in upload history: " + rowsUpdated); 
			
		}
}
