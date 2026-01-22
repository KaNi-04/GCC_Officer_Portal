package in.gov.chennaicorporation.gccoffice.callcenter.service;

import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class AgentAssignService
{
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysql1913CampaignDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	public void setDataSource2(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
    private JdbcTemplate jdbcTemplate2;
	

	@Transactional
	public List<Map<String, Object>> getcategory() {
		String sqlQuery = "SELECT category_id,category_name FROM gcc_1913_campaign.category where isactive='1' and isdelete='0'";
		return jdbcTemplate.queryForList(sqlQuery);
	}
	
	@Transactional
	public List<Map<String, Object>> getcampaignbycategory(int id) {
		String sqlQuery = "SELECT cr.campaign_id, cr.campaign_name " +
                "FROM gcc_1913_campaign.campaign_request cr " +
                "JOIN gcc_1913_campaign.category c ON cr.category_id = c.category_id " +
                "WHERE cr.category_id = ? AND campaign_status='PENDING' " +
                "AND cr.isactive = '1' ";
		return jdbcTemplate.queryForList(sqlQuery,id);
	}
	
	@Transactional
	public List<Map<String, Object>> getcampaignbyid(int id) {
		String sqlQuery = "SELECT * FROM gcc_1913_campaign.campaign_request where isactive='1' and campaign_id=?";
		return jdbcTemplate.queryForList(sqlQuery,id);
	}
	
	@Transactional
	public List<Map<String, Object>> getagentsfortask(int id) {
		String sqlQuery = "SELECT al.agent_id,al.agent_name\r\n"
				+ "FROM gcc_1913_qaqc.agents_list al\r\n"
				+ "LEFT JOIN gcc_1913_campaign.agent_assign aa ON al.agent_id = aa.agent_id  AND  aa.isactive = '1' AND aa.campaign_id = ?\r\n"
				+ "WHERE aa.agent_id IS NULL AND al.calling_type='OUTBOUND'AND al.isactive='1' AND al.isdelete='0'";
		return jdbcTemplate.queryForList(sqlQuery,id);
	}
	
	
	@Transactional
	public int addagentsfortask(int agent_id, int category_id, int campaign_id) {
	    String insertSql = "INSERT INTO gcc_1913_campaign.agent_assign (agent_id, category_id, campaign_id) VALUES (?, ?, ?)";
	    int rowsInserted = jdbcTemplate.update(insertSql, agent_id, category_id, campaign_id);

	    return rowsInserted;
	}


	@Transactional
	public void addAgentsForCampaign(int selectedAgentId, int categoryId, int campaignId) {
	    String insertSql = "INSERT INTO gcc_1913_campaign.agent_assign (agent_id, category_id, campaign_id) VALUES (?, ?, ?)";

	    try {
	        
	            int rowsInserted = jdbcTemplate.update(insertSql, selectedAgentId, categoryId, campaignId);

	            if (rowsInserted > 0) {
	                System.out.println("Successfully inserted agent ID in agent_assign== " + selectedAgentId + " into campaign ID " + campaignId);
	            } else {
	                System.out.println("No rows inserted for agent ID " + selectedAgentId);
	            }
	        
	    } catch (DataAccessException | NumberFormatException ex) {
	        System.err.println("Error inserting data: " + ex.getMessage());
	        throw new RuntimeException("Failed to insert agents for campaign. Please try again.", ex);
	    }
	}

	@Transactional
	public void updateAgentForCampaign(int selectedAgentId, int categoryId, int campaignId,int DataId) {
	    String updateSql = "UPDATE gcc_1913_campaign.upload_data SET agent_id = ? WHERE campaign_id = ? AND category_id = ? AND data_id=?";

	    try {
	        
	            int rowsUpdated = jdbcTemplate.update(updateSql, selectedAgentId,campaignId,categoryId,DataId);

/*	            if (rowsUpdated > 0) {
	                System.out.println("Successfully updated agent ID in upload_data== " + selectedAgentId + " for campaign ID " + campaignId);
	            } else {
	                System.out.println("No rows updated for agent ID " + selectedAgentId);
	            }*/
	        
	    } catch (DataAccessException | NumberFormatException ex) {
	        System.err.println("Error updating data: " + ex.getMessage());
	        throw new RuntimeException("Failed to update agents for campaign. Please try again.", ex);
	    }
	}
	
	@Transactional
	public List<Integer> getDataIdForInitialAssign(int categoryId, int campaignId) {

	    String sql = "SELECT data_id FROM gcc_1913_campaign.upload_data "
	                 + "WHERE category_id = ? AND campaign_id = ? "
	                 + "AND agent_id IS NULL AND is_processed = 0 ORDER BY data_id";

	    // Correctly specify the return type for jdbcTemplate.queryForList
	    return jdbcTemplate.queryForList(sql, Integer.class, categoryId, campaignId);
	}


	
	
	@Transactional
	public List<Map<String, Object>> checkcampaigninassignedtable(int id)
	{
		String sqlQuery = "SELECT al.agent_id,al.agent_name,aa.assigned_id\r\n"
				+ "FROM gcc_1913_qaqc.agents_list al\r\n"
				+ "JOIN gcc_1913_campaign.agent_assign aa ON al.agent_id = aa.agent_id\r\n"
				+ "JOIN gcc_1913_campaign.campaign_request c ON aa.campaign_id=c.campaign_id\r\n"
				+ "WHERE aa.campaign_id = ? AND aa.isactive = '1' AND al.calling_type='OUTBOUND'AND al.isactive='1' AND al.isdelete='0'";
		return jdbcTemplate.queryForList(sqlQuery,id);
	}
	
	@Transactional
	public int editagentsfortask(int assigned_id) {
	    String updateSql = "UPDATE gcc_1913_campaign.agent_assign SET isactive='0', isdelete='1',status='NOT_ASSIGNED' WHERE assigned_id=?";
	    int rowsInserted = jdbcTemplate.update(updateSql,assigned_id);

	    return rowsInserted;
	}
	
	
	
	@Transactional
	public List<Map<String, Object>> getcampaignquestionbyid(int id) {
	    String sqlQuery = " SELECT " +
	            "    cr.campaign_id, " +
	            "    cr.campaign_name, " +
	            "    cr.category_id, " +
	            "    cr.start_date, " +
	            "    cr.end_date, " +
	            "    cr.no_of_agents, " +
	            "    cr.calls_per_agent, " +
	            "    cr.description, " +
	            "    cr.created_date, " +
	            "    cr.excel_url, " +
	            "    cr.isactive, " +
	            "    cr.campaign_status, " +
	            "    cq.questions, " +
	            "    cq.question_id, " +
	            "    cq.answer_type_id, " +
	            "    cat.type_name, " +
	            "    ca.campaign_answer " + // Campaign Answer can be NULL
	            "FROM gcc_1913_campaign.campaign_request cr " +
	            "JOIN gcc_1913_campaign.campaign_questions cq " +
	            "    ON cr.campaign_id = cq.campaign_id " +
	            "LEFT JOIN gcc_1913_campaign.campaign_answers ca " + // LEFT JOIN to include questions with no answers
	            "    ON cq.question_id = ca.question_id " +
	            "JOIN gcc_1913_campaign.answer_type cat " +
	            "    ON cat.answer_type_id = cq.answer_type_id " +
	            "WHERE cr.isactive = 1 AND cr.campaign_id = ?";

	    List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(sqlQuery, id);
	    
	    // Map to group answers by question_id
	    Map<Integer, Map<String, Object>> groupedData = new LinkedHashMap();

	    for (Map<String, Object> row : queryResult) {
	        Integer questionId = (Integer) row.get("question_id");
	        String campaignAnswer = (String) row.get("campaign_answer"); // Can be null

	        // Check if this question_id already exists in the map
	        if (!groupedData.containsKey(questionId)) {
	            // Copy the row data (excluding campaign_answer) and initialize answers list
	            Map<String, Object> newRow = new LinkedHashMap<>(row);
	            newRow.remove("campaign_answer");  // Remove individual answer
	            newRow.put("campaign_answers", new ArrayList<String>());  // Initialize answer list
	            
	            groupedData.put(questionId, newRow);
	        }

	        // Only add answers if they are not null
	        if (campaignAnswer != null) {
	            ((List<String>) groupedData.get(questionId).get("campaign_answers")).add(campaignAnswer);
	        }
	    }

	    // Convert grouped data to a list
	    return new ArrayList<>(groupedData.values());
	}


	
}
