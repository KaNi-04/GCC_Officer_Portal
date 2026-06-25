package in.gov.chennaicorporation.gccoffice.roadwar.service;

import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoginMappingService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlGCCUserDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public String getZoneByUserId(String userid) {

	    String sql = "SELECT zone FROM user_mapping_details WHERE userid=?";

	    try {
	        return jdbcTemplate.queryForObject(sql, String.class, userid);
	    }catch (EmptyResultDataAccessException e) {
	        return "";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "";
	    }
	}
	
	public String getWardByUserId(String userid) {

	    String sql = "SELECT ward FROM user_mapping_details WHERE userid=?";

	    try {
	        return jdbcTemplate.queryForObject(sql, String.class, userid);
	    } catch (EmptyResultDataAccessException e) {
	        return "";
	    }catch (Exception e) {
	        e.printStackTrace();
	        return "";
	    }
	}
}
