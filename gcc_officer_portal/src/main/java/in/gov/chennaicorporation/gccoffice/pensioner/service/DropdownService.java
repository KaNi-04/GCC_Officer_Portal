package in.gov.chennaicorporation.gccoffice.pensioner.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DropdownService {
	
	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 
	    @Autowired
		 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
			 this.jdbcTemplate = new JdbcTemplate(dataSource);
		 }

	 public List<Map<String, Object>> getRetrimentTypes() {
	        String sql = "SELECT id,retriment_type FROM type_of_retriment";
	        return jdbcTemplate.queryForList(sql);
	 }
	 
	 public List<Map<String, Object>> getCategoryTypes() {
	        String sql = "SELECT id,category_type FROM type_of_category";
	        return jdbcTemplate.queryForList(sql);
	 }
	 
	 public List<Map<String, Object>> getProvisionalReasonTypes() {
	        String sql = "SELECT id,provisional_reason FROM reason_for_provisional";
	        return jdbcTemplate.queryForList(sql);
	 }

	 
	 
	 public List<Map<String, Object>> getGisEntryTypes() {
	        String sql = "SELECT id,gis_entry FROM gis_entry_type";
	        return jdbcTemplate.queryForList(sql);
	 }
}
