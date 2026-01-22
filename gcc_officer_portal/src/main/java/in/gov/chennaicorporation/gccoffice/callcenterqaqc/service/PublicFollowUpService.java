package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PublicFollowUpService {
	
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
	                   "WHERE call_category = 'PUBLICFOLLOWUP' AND agent_id = ? AND is_processed = FALSE";
	    
	    return jdbcTemplate.queryForList(query, agent_id);
	}
	
	public List<Map<String, Object>> getFollowUpCatergory()
	{
		String query = "SELECT call_status " +  
                "FROM gcc_1913_qaqc.call_status WHERE call_category_id = 4 and isactive='1'";
		
		return jdbcTemplate.queryForList(query);
	}
	
	public List<Map<String, Object>> getFollowupSubmitDetails(int userId) {
    	
	   	 String query = "SELECT complaint_number, complaint_date,complaint_person_name, complaint_mobilenumber,official_name,official_mobilenum, " +
	               "complaint_type, call_status, remarks " +
	               "FROM gcc_1913_qaqc.qaqc_upload_data " +
	               "WHERE call_category = 'PUBLICFOLLOWUP' AND agent_id = ? AND is_processed = '1'";
	   	 
			return jdbcTemplate.queryForList(query,userId);
			  
		}

}
