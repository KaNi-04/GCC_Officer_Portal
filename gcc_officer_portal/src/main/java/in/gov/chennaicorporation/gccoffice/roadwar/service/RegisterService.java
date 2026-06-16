package in.gov.chennaicorporation.gccoffice.roadwar.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private  Environment environment;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlRoadWarDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	public List<Map<String, Object>> ViewZoneDropdown() {
		String sql = "SELECT zone_name "
	    		+ "FROM zone_master "
	    		+ "WHERE is_active=1 AND is_delete=0";
	    try {
	    	return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
			 return Collections.emptyList();
		}
	}
	
	public List<Map<String, Object>> ViewWardDropdown(String zone) {
		String sql = "SELECT ward_name "
	    		+ "FROM ward_master "
	    		+ "WHERE is_active=1 AND is_delete=0 AND zone_id=?";
	    try {
	    	return jdbcTemplate.queryForList(sql,zone);
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
			 return Collections.emptyList();
		}
	}

	
	
}
