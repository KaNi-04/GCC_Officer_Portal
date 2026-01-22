package in.gov.chennaicorporation.gccoffice.m_petition.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetitionMaster {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlPetitionMasterDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	 
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getComplaintType(){
		String SqlQuery = "";
		
		SqlQuery = "SELECT * FROM `complaint_types` WHERE `isactive`=1 AND `isdelete`=0";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
		
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getOfficerList(){
		String SqlQuery = "";
		
		SqlQuery = "SELECT * FROM `officer_master` WHERE `isactive`=1 AND `isdelete`=0";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
		
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getPetitionTypes(){
		String SqlQuery = "";
		
		SqlQuery = "SELECT * FROM `petition_types` WHERE `isactive`=1 AND `isdelete`=0  ORDER BY `orderby` ASC";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
		
	}
}
