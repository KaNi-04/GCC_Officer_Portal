package in.gov.chennaicorporation.gccoffice.pensioner.service;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@Service
public class CompletedService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    @Autowired
    private AppConfig appconfig;

    public CompletedService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Autowired
	 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
    
    @Autowired
    public GetFileStatusService getFileStatus;
    
    public List<Map<String, Object>> getCPSList(Integer file_cat,Integer deptId) {
        String query = "SELECT * FROM cps_pensioner_details where file_category = ? and dept_id=? and file_status=11 ";

        try {
            return jdbcTemplate.queryForList(query,file_cat,deptId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    ///////////////////CPS completed list method///////////
    
    public List<Map<String, Object>> getCPSListPensionside(Integer file_cat) {
        String query = "SELECT * FROM cps_pensioner_details where file_category = ? and file_status=11 ";

        try {
            return jdbcTemplate.queryForList(query,file_cat);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    
    public List<Map<String, Object>> getCPSListByEmpNo(String empNo) {
        String query = "SELECT * FROM cps_pensioner_details WHERE emp_no = ?";

        try {
            return jdbcTemplate.queryForList(query, empNo);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    
    ////////////////////////////

    
    
    
    ////////Retirement completed list method///////////////////
    public List<Map<String, Object>> fetchDetailsCompletedRetirement(Integer file_cat,Integer deptId) {
    	String query = "SELECT * " +
                "FROM pensioner_details " +
                "WHERE file_category=? AND dept_id=? and file_status=11 AND is_closed=0";
        return jdbcTemplate.queryForList(query,file_cat,deptId);
    }
    
    
    
    public List<Map<String, Object>> fetchDetailsCompletedRetirementPensionside(Integer file_cat) {
    	String query = "SELECT * " +
                "FROM pensioner_details " +
                "WHERE file_category=? and file_status=11 AND is_closed=0";
        return jdbcTemplate.queryForList(query,file_cat);
    }
    /////////////////////////

    
    
    ///////////////////Family pension completed list method//////////////
    public List<Map<String, Object>> fetchDetailsCompletedFamilyPension(Integer file_cat,Integer deptId) {
    	String query = "SELECT * " +
                "FROM pensioner_details " +
                "WHERE file_category=? AND dept_id=? and file_status=11";
        return jdbcTemplate.queryForList(query,file_cat,deptId);
    }
    
    
    
    public List<Map<String, Object>> fetchDetailsCompletedFamilyPensionPensionSide(Integer file_cat) {
    	String query = "SELECT * " +
                "FROM pensioner_details " +
                "WHERE file_category=? and file_status=11";
        return jdbcTemplate.queryForList(query,file_cat);
    }
    //////////////////////////

    
    
    ///////////////Pendency completed method////////////////
    public List<Map<String, Object>> fetchDetailsCompletedPendency(Integer file_cat,Integer deptId) {
    	String query = "SELECT * " +
                "FROM pensioner_details " +
                "WHERE file_category=? AND dept_id=? and file_status=11";
        return jdbcTemplate.queryForList(query,file_cat,deptId);
    }
    
    
    
    public List<Map<String, Object>> fetchDetailsCompletedPendencyPensionSide(Integer file_cat) {
    	String query = "SELECT * " +
                "FROM pensioner_details " +
                "WHERE file_category=? and file_status=11";
        return jdbcTemplate.queryForList(query,file_cat);
    }
    //////////////////////
    
    
    
    ////////////Gis completed list method
	
	public List<Map<String, Object>> fetchDetailsForrecivedGiscompleted(Integer file_cat,Integer deptId) {
		 String query = "SELECT * FROM pensioner_details WHERE file_category = ? AND dept_id=? AND file_status = 11";
	        
	        return jdbcTemplate.queryForList(query,file_cat,deptId);
	    }
	
	
	public List<Map<String, Object>> fetchDetailsForrecivedGiscompletedPensionside(Integer file_cat) {
		 String query = "SELECT * FROM pensioner_details WHERE file_category = ? AND file_status = 11";
	        
	        return jdbcTemplate.queryForList(query,file_cat);
	    }
	
	
	
	////////////////Provisional retirement completed list method
	
    public List<Map<String, Object>> fetchDetailsProvisionalDepartmentCompleted(Integer file_cat,Integer deptId) {
    	String query = "SELECT * " +
                "FROM pensioner_details " +
                "WHERE file_category=? AND dept_id=? and file_status=11";
        return jdbcTemplate.queryForList(query,file_cat,deptId);
    }
    
    
    
    public List<Map<String, Object>> fetchDetailsProvisionalDepartmentPensionside(Integer file_cat) {
    	String query = "SELECT * " +
                "FROM pensioner_details " +
                "WHERE file_category=? and file_status=11";
        return jdbcTemplate.queryForList(query,file_cat);
    }
}
