package in.gov.chennaicorporation.gccoffice.pensioner.service;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class GetFileStatusService {
	
	@Autowired
	public JdbcTemplate jdbcTemplate;

	 @Autowired
	 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	 
	 public Integer getFileStatus1Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'Sent to Audit' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus2Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'Returned From Audit' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus3Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'Rectify and Sent to Audit' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus4Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'Audit Certified' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus5Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'File Forwarded to Department' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus6Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'Returned to Department' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus7Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'File Forwarded to Pension' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus8Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'Pension Pending' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus9Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'File in Department to complete pending values' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus10Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'File in Pension to complete pending values' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus11Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'File Completed' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
	 
	 public Integer getFileStatus12Id() {
	        try {
	            String query = "SELECT id FROM file_status_master WHERE file_status = 'Retirement File Closed' AND isactive = 1 AND isdelete = 0 LIMIT 1";
	            return jdbcTemplate.queryForObject(query, Integer.class);
	        } catch (EmptyResultDataAccessException e) {
	            System.out.println("No matching file_status found, returning default value (0).");
	            return 0; // Default value in case no record is found
	        }
	    }
}
