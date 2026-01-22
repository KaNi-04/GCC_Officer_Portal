package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class QaqcAdminSettingService {

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional
	public int updateProductivity(int productivity_count) {
	    String updateSql = "UPDATE productivity SET productivity_count=? where isactive='1' and isdelete='0'";
	    return jdbcTemplate.update(updateSql,productivity_count);

	}
	
	@Transactional
	public int saveProductivity(int productivity_count) {
	    String insertSql = "INSERT INTO productivity (productivity_count) VALUES (?)";
	    return jdbcTemplate.update(insertSql, productivity_count);

	}
	
	@Transactional
	public Integer getProductivity() { 
	    String insertSql = "select productivity_count\r\n"
	    		+ "from productivity\r\n"
	    		+ "where isactive='1' and isdelete='0'"
	    		+ "LIMIT 1";
	    return jdbcTemplate.queryForObject(insertSql, Integer.class);
	    
	}
	
	@Transactional
	public List<Map<String, Object>> getAdminSettingCounts() {
		
		String sqlQuery = "SELECT " +
			    "COALESCE(p.productivity_count, 0) AS productivity_count, " +
			    "COUNT(al.agent_id) AS total_count, " +
			    "COALESCE(p.inbound_count, 0) AS inbound_count, " +
			    "COALESCE(p.outbound_count, 0) AS outbound_count, " +
			    "COALESCE(p.inbound_count, 0) + COALESCE(p.outbound_count, 0) As assigned, " +
			    "(COUNT(al.agent_id) - (COALESCE(p.inbound_count, 0) + COALESCE(p.outbound_count, 0))) AS notassigned " +
			    "FROM productivity p " +
			    "JOIN agents_list al " +
			    "WHERE p.isactive = '1' AND p.isdelete = '0' AND al.isactive = '1' AND al.isdelete = '0' " +
			    "GROUP BY p.productivity_count, p.inbound_count, p.outbound_count";
	    
	    return jdbcTemplate.queryForList(sqlQuery);
	}

	
	@Transactional
	public int saveCallingTypeInSettings() {
	    // Query to get inbound and outbound counts
	    String selectSql = "SELECT\r\n"
	    		+ "SUM(CASE WHEN calling_type = 'INBOUND' THEN 1 ELSE 0 END) AS inbound, \r\n"
	    		+ "SUM(CASE WHEN calling_type = 'OUTBOUND' THEN 1 ELSE 0 END) AS outbound \r\n"
	    		+ "FROM agents_list \r\n"
	    		+ "WHERE isactive = '1' AND isdelete = '0'";

	    // Fetch the counts
	    Map<String, Object> result = jdbcTemplate.queryForMap(selectSql);
	    int inbound = ((Number) result.get("inbound")).intValue(); // Convert to int
	    int outbound = ((Number) result.get("outbound")).intValue(); // Convert to int

	    //Corrected SQL syntax for updating multiple columns
	    String updateSql = "UPDATE productivity SET inbound_count = ?, outbound_count = ? WHERE isactive = '1' AND isdelete = '0'";
	    return jdbcTemplate.update(updateSql, inbound, outbound);
	}
	
	
	@Transactional
	public int updateCallType(String calling_type,int agent_id) {
	    String updateSql = "UPDATE agents_list SET calling_type=? WHERE agent_id=? AND isactive='1'";
	    
	    return jdbcTemplate.update(updateSql,calling_type,agent_id);
	}
	
	@Transactional
	public List<Map<String, Object>> getAgentsForCallingtype()
	{
		String sqlQuery = "select al.agent_id as agent_id,ap.username as username,al.agent_name as agent_name,al.mobile_number as mobile_number,\r\n"
				+ "al.calling_type as calling_type\r\n"
				+ "from gcc_1913_qaqc.agents_list al\r\n"
				+ "LEFT join gcc_apps.appusers ap on ap.userid=al.agent_id\r\n"
				+ "where al.isactive='1' and al.isdelete='0'";
		
		return jdbcTemplate.queryForList(sqlQuery);
	}

	public int updateAgentName(int agentId, String updatedName) {
	    // Query to check if the name exists for another agent
	    String checkSql = "SELECT COUNT(*) FROM agents_list WHERE agent_name = ? AND agent_id <> ? AND isactive = '1'";
	    
	    int count = jdbcTemplate.queryForObject(checkSql, Integer.class, updatedName, agentId);
	    
	    if (count > 0) {
	        return -1; // Indicate that the name already exists
	    }

	    // Proceed with the update if the name is unique
	    String updateSql = "UPDATE agents_list SET agent_name = ? WHERE agent_id = ? AND isactive = '1'";
	    return jdbcTemplate.update(updateSql, updatedName, agentId);
	}

	
}
