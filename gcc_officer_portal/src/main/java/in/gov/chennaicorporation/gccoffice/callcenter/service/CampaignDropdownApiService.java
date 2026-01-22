package in.gov.chennaicorporation.gccoffice.callcenter.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CampaignDropdownApiService {

	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 
	    @Autowired
		 public void setDataSource(@Qualifier("mysql1913CampaignDataSource") DataSource dataSource) {
			 this.jdbcTemplate = new JdbcTemplate(dataSource);
		 }
	    
	    public List<Map<String,Object>> getAnswerTypes(){
	    	String sql = "SELECT answer_type_id,type_name FROM answer_type";
	    	return jdbcTemplate.queryForList(sql);
	    	
	    }
}
