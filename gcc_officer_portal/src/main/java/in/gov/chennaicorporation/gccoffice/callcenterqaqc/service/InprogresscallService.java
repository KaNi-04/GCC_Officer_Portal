package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

@Service
public class InprogresscallService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
	 public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }

    
    public List<Map<String, Object>> getActionsCatergory()
	{
		String query = "SELECT call_status " +  
                "FROM gcc_1913_qaqc.call_status WHERE call_category_id = 3 and isactive='1'";
		
		return jdbcTemplate.queryForList(query);
	}
    
//    public List<Map<String, Object>> getInProgressCalls(int agent_id) {
//        String sql = "SELECT complaint_number, " +
//                     "       official_name , " +
//                     "       official_mobilenum , " +
//                     "       complaint_type, " +
//                     "       call_category,  " +
//                     "       complaint_mode,  " +
//                     "       department  " +
//                     
//                     "FROM gcc_1913_qaqc.qaqc_upload_data " +
//                     "WHERE call_category ='inprogress' and agent_id= ?";
//
//		
//        return jdbcTemplate.queryForList(sql, agent_id);        
//    }
    
    public List<Map<String, Object>> getInProgressCalls(int agent_id) {
        String query = "SELECT data_id, complaint_number, complaint_date,complaint_person_name, complaint_mobilenumber, " +
                       "complaint_type, official_name, official_mobilenum, call_category, complaint_mode, department " +
                       "FROM gcc_1913_qaqc.qaqc_upload_data " +
                       "WHERE (call_category = 'PROCESSING' OR call_category = 'REOPENED' OR call_category = 'REDRESSED' OR call_category = 'INPROGRESS') " +
                       "  AND is_processed = FALSE " +
                       "  AND agent_id = ?";
        return jdbcTemplate.queryForList(query, agent_id);
    }
    
    public void updateInprogressComplaintDetails(String complaintNumber,int dataId, String action, String remarks,int updated_agent)
	  { 
  	String query = "UPDATE gcc_1913_qaqc.qaqc_upload_data " +
	  "SET call_status = ?, " + "    remarks = ?,updated_agent=?, " +
	  "    updated_date = CURRENT_TIMESTAMP, " + "    is_processed = TRUE " + 
	  "WHERE data_id = ? "  +"  AND complaint_number = ?";
	  
	  int rowsUpdated = jdbcTemplate.update(query, action, remarks,updated_agent, dataId,complaintNumber);
	  
	  //System.out.println("Rows Updated in upload data: " + rowsUpdated); 
	  }

    public void saveMobileNumDetails(String complaintNumber, String remarks) {
        String query = "insert into officer_mobileno (complaint_number,remarks) values(?,?)";
        int rowsUpdated = jdbcTemplate.update(query, complaintNumber, remarks);
        //System.out.println("Rows Updated: " + rowsUpdated);
    }
    
    public List<Map<String, Object>> getInprogressSubmitDetails(int userId) {
    	
   	 String query = "SELECT complaint_number, complaint_date,complaint_person_name, complaint_mobilenumber,official_name,official_mobilenum, " +
               "complaint_type, call_status, remarks " +
               "FROM gcc_1913_qaqc.qaqc_upload_data " +
               "WHERE (call_category = 'PROCESSING' OR call_category = 'REOPENED' OR call_category = 'REDRESSED' OR call_category = 'INPROGRESS') AND agent_id = ? AND is_processed = '1'";
   	 
		return jdbcTemplate.queryForList(query,userId);
		  
	}


	public void updateComplaintHistoryDetails(String complaintNumber, int dataId, String action, String remarks,int updated_agent,
				String remainderDate) {
			
			String query = "UPDATE gcc_1913_qaqc.qaqc_upload_data_history \r\n"
					+ "SET call_status = ?, remarks = ?, updated_date = CURRENT_TIMESTAMP, remainder_date = ?,updated_agent=?\r\n"
					+ "WHERE data_id = ?   AND complaint_number = ?";
					  
			int rowsUpdated = jdbcTemplate.update(query, action, remarks, remainderDate,updated_agent, dataId, complaintNumber);
					  
			//System.out.println("Rows Updated in upload data history: " + rowsUpdated); 
			
		}
 
}
