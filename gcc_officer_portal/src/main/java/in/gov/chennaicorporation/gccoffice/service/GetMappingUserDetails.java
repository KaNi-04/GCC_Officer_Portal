package in.gov.chennaicorporation.gccoffice.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class GetMappingUserDetails {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlAppDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional(readOnly = true)
    public List<Map<String,Object>> getMappingList(String loginId) {
		try {
        	String sqlQuery = "SELECT * FROM `login_mapping_user` WHERE appuser_id=? LIMIT 1";
        	//System.out.println(sqlQuery+loginId);
        	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery,loginId);
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null; // Return null or handle error appropriately
        }
	}
	
	@Transactional(readOnly = true)
    public String getOfficerMaster(String loginId) {
		System.out.println("SELECT `officer_master_id` FROM `login_mapping_user` WHERE appuser_id = "+loginId+" LIMIT 1");
        try {
            String sqlQuery = "SELECT `officer_master_id` FROM `login_mapping_user` WHERE appuser_id = ? LIMIT 1";
            System.out.println(sqlQuery);
            System.out.println("SELECT `officer_master_id` FROM `login_mapping_user` WHERE appuser_id = "+loginId+" LIMIT 1");
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlQuery, loginId);
            
            if (results.isEmpty()) {
                return null; // No record found
            } else {
                // Assuming there is only one result because of LIMIT 1
                Map<String, Object> row = results.get(0);
                Object officerMasterId = row.get("officer_master_id");
                
                return officerMasterId != null ? officerMasterId.toString() : null;
            }
        } catch (DataAccessException e) {
        	
            e.printStackTrace();
            return null; // Handle error appropriately
        }
    }
	
	@Transactional(readOnly = true)
    public String getERPUserId(String loginId) {
        try {
            String sqlQuery = "SELECT `erpuser_id` FROM `login_mapping_user` WHERE appuser_id = ? LIMIT 1";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlQuery, loginId);
            
            if (results.isEmpty()) {
                return null; // No record found
            } else {
                // Assuming there is only one result because of LIMIT 1
                Map<String, Object> row = results.get(0);
                Object erpUserId = row.get("erpuser_id");
                
                return erpUserId != null ? erpUserId.toString() : null;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null; // Handle error appropriately
        }
    }
}
