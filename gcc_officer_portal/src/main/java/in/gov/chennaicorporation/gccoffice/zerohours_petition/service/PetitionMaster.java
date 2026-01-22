package in.gov.chennaicorporation.gccoffice.zerohours_petition.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ZeroPetitionMaster")
public class PetitionMaster {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlPetitionMasterDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getZoneAndWard(){
		String SqlQuery = "";
		
		SqlQuery = "SELECT "
				+ "    CAST(zone AS UNSIGNED) AS zone, "
				+ "    GROUP_CONCAT(CAST(divid AS UNSIGNED) ORDER BY CAST(divid AS UNSIGNED) ASC) AS wards "
				+ "FROM "
				+ "    councillor "
				+ "GROUP BY "
				+ "    CAST(zone AS UNSIGNED) "
				+ "ORDER BY "
				+ "    CAST(zone AS UNSIGNED) ASC";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
		
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getCouncillor(String ward){
		String SqlQuery = "";
		
		SqlQuery = "SELECT * FROM councillor WHERE `divid`='"+ward+"' LIMIT 1";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
		
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
		
		SqlQuery = "SELECT * FROM `officer_master_zh` WHERE `isactive`=1 AND `isdelete`=0";
		
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
