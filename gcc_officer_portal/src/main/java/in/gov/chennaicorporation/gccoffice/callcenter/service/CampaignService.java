package in.gov.chennaicorporation.gccoffice.callcenter.service;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CampaignService {

    private JdbcTemplate jdbcTemplate;
	
  @Autowired
	public void setDataSourcemysql(@Qualifier("mysql1913CampaignDataSource") DataSource dataSource) {		
		this.jdbcTemplate = new JdbcTemplate(dataSource);	
		}
  
  @Autowired
	public void setDataSource2(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
  private JdbcTemplate jdbcTemplate2;

   
	//Method to save file and return its URL
	  public String saveFile(MultipartFile file) {
	      String fileName = file.getOriginalFilename();
	      String filePath = "/uploads/" + fileName;  // Path to save the file
	      try {
	          File destinationFile = new File(filePath);
	          file.transferTo(destinationFile);  // Save the file locally
	      } catch (IOException e) {
	          throw new RuntimeException("Failed to store file " + fileName, e);
	      }
	      return filePath;  // Return the file path (URL) for saving to the database
	  }

  
	//Method to save campaign details into the database
	public void saveCampaignDetails(Map<String, Object> campaignDetails) {
	   String sql = "UPDATE gcc_1913_campaign.campaign_request " +
	                "SET start_date = :start_date, " +
	                "end_date = :end_date, " +
	                "calls_per_agent = :calls_per_agent, " +
	                "description = :description " +
	                "WHERE campaign_id = :campaign_id";
	
	   // Using NamedParameterJdbcTemplate for mapping campaignDetails into query
	   NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	   namedJdbcTemplate.update(sql, campaignDetails);
	}

  
  // Method to save campaign details into the database
  public String saveCampaignDetailsExcel(Map<String, Object> campaignDetailsExcel) {
	  
	  campaignDetailsExcel.putIfAbsent("campaign_status", "PENDING");
	  
	  String sql = "INSERT INTO gcc_1913_campaign.campaign_request (campaign_name, category_id, excel_url,campaign_status) " +
                   "VALUES (:campaign_name, :category_id, :excel_url ,:campaign_status)";

      // Using NamedParameterJdbcTemplate for mapping campaignDetails into query
      NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
      namedJdbcTemplate.update(sql, campaignDetailsExcel);
      
      // Return the category to use in the next step
      return campaignDetailsExcel.get("category_id").toString();

  }
  
  
  // campaign_status change as ongoing in campaign_request table
  @Transactional
  public void updateCampaignStatus(int campaignId, int categoryId) {
      String checkSql = "SELECT COUNT(*) FROM gcc_1913_campaign.agent_assign "
                      + "WHERE campaign_id = ? AND category_id = ? AND status = 'ASSIGNED'";

      int assignedCount = jdbcTemplate.queryForObject(checkSql, Integer.class, campaignId, categoryId);

      if (assignedCount > 0) {
          String updateSql = "UPDATE gcc_1913_campaign.campaign_request "
                           + "SET campaign_status = 'ONGOING' WHERE campaign_id = ? AND category_id = ?";
          jdbcTemplate.update(updateSql, campaignId, categoryId);

          System.out.println("Campaign status updated to ONGOING for campaign ID: " + campaignId);
      } else {
          System.out.println("No agents assigned yet. Campaign status remains PENDING.");
      }
  }

  
	//Validated uploaded excel column count with field column in our DB
	public List<String> getUploadDataFieldColumns() {
	   String query = "SELECT column_name FROM information_schema.columns "
	                + "WHERE table_schema = 'gcc_1913_campaign' "
	                + "AND table_name = 'upload_data' "
	                + "AND column_name LIKE 'field_%'";
	   return jdbcTemplate.queryForList(query, String.class);
	}




  public String saveQuestionsAnswers(List<Map<String, Object>> questionsData) {
	    for (Map<String, Object> question : questionsData) {
	        String questionText = (String) question.get("question");
	        String answerType = (String) question.get("answerType");

	        // Validate and parse categoryId and campaignId
	        Integer categoryId = Integer.parseInt(question.get("categoryId").toString());
	        Integer campaignId = Integer.parseInt(question.get("campaignId").toString());

	        List<String> answers = (List<String>) question.get("answers");

	        // Insert question and get generated question_id
	        String questionInsertQuery = "INSERT INTO gcc_1913_campaign.campaign_questions (category_id, campaign_id, questions, answer_type_id) VALUES (?, ?, ?, ?)";

	        KeyHolder keyHolder = new GeneratedKeyHolder();
	        jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(questionInsertQuery, Statement.RETURN_GENERATED_KEYS);
	            ps.setInt(1, categoryId);
	            ps.setInt(2, campaignId);
	            ps.setString(3, questionText);
	            ps.setString(4, answerType);
	            return ps;
	        }, keyHolder);

	        Integer questionId = keyHolder.getKey().intValue();

	        // Insert answers if available
	        if (answers != null && !answers.isEmpty()) {
	            String answerInsertQuery = "INSERT INTO gcc_1913_campaign.campaign_answers (category_id, campaign_id, question_id, campaign_answer) VALUES (?, ?, ?, ?)";
	            for (String answer : answers) {
	                jdbcTemplate.update(answerInsertQuery, categoryId, campaignId, questionId, answer);
	            }
	        }
	    }
	    return "Questions and Answers saved successfully!";
	}

  


  public void saveQuestionsAndAnswers(String category, String campaignId, String question, String answerType, List<String> answers) {
	    // Convert category and answerType to integers once to avoid redundant parsing
	    int categoryId = Integer.parseInt(category);
	    int answerTypeId = Integer.parseInt(answerType);
	    int campaignIdd = Integer.parseInt(campaignId);
	    
	    // Query to insert question into campaign_questions and retrieve generated question_id
	    String query1 = "INSERT INTO gcc_1913_campaign.campaign_questions (category_id, campaign_id, questions, answer_type_id) VALUES (?, ?, ?, ?)";
	    
	    KeyHolder keyHolder = new GeneratedKeyHolder();
	    
	    jdbcTemplate.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
	        ps.setInt(1, categoryId);
	        ps.setInt(2, campaignIdd);
	        ps.setString(3, question);
	        ps.setInt(4, answerTypeId);
	        return ps;
	    }, keyHolder);
	    
	    int questionId = keyHolder.getKey().intValue();
	    
	    // If answerType is NOT "Text" (ID = 1), save answers
	    if (answerTypeId != 1 && answers != null && !answers.isEmpty()) {
	        String query2 = "INSERT INTO gcc_1913_campaign.campaign_answers (category_id, campaign_id, question_id, campaign_answer) VALUES (?, ?, ?, ?)";
	        
	        for (String answer : answers) {
	            jdbcTemplate.update(query2, categoryId, campaignId, questionId, answer);
	        }
	    }

	    System.out.println("Inserted Question ID: " + questionId + " with Answers: " + (answers != null ? answers : "None"));
	}

  


  
//  
//  public Map<String, Object> getCampaignDetailsById(Long campaign_id) {
//      String sql = "SELECT * FROM gcc_1913_campaign.campaign_request WHERE campaign_id = ?";
//      return jdbcTemplate.queryForMap(sql, campaign_id);
//  }
  

  
  
  public void updateCampaign(Long campaign_id, String end_date) {
      String sql = "UPDATE gcc_1913_campaign.campaign_request SET end_date = ? WHERE campaign_id = ?";
      jdbcTemplate.update(sql, end_date,campaign_id);
  }
  

  
  public List<Map<String, Object>> getCampaignDetails() {
	  String sql = "SELECT campaign_id, " +
	             "       campaign_name, " +
	             "       CONCAT(" +
	             "           DATE_FORMAT(start_date, '%d/%m/%Y'), " +
	             "           ' to ', " +
	             "           DATE_FORMAT(end_date, '%d/%m/%Y')" +
	             "       ) AS event_dates, " +
	             "       no_of_agents, " +
	             "       calls_per_agent, " +
	             "      (no_of_agents)*(calls_per_agent) as total_calls, " +
	             "       description " +
	             "FROM gcc_1913_campaign.campaign_request;";

      List<Map<String, Object>> campaigns = jdbcTemplate.queryForList(sql);

      
      return campaigns;
  }
  
  public List<Map<String, Object>> getLastRow()
  {
	  String sqlQuery ="SELECT campaign_id\r\n"
	  		+ "FROM gcc_1913_campaign.campaign_request\r\n"
	  		+ "ORDER BY campaign_id DESC\r\n"
	  		+ "LIMIT 1";
	  
	  return jdbcTemplate.queryForList(sqlQuery); 
  }
  
  
  
  
  public String getLastInsertedCategoryId() {
	    String sqlQuery = "SELECT category_id " +
	                      "FROM gcc_1913_campaign.campaign_request " +
	                      "ORDER BY campaign_id DESC " +
	                      "LIMIT 1";

	    return jdbcTemplate.queryForObject(sqlQuery, String.class); 
	}

  
  public Integer getLastInsertedCampaignId()
  {
	  String sqlQuery ="SELECT campaign_id\r\n"
	  		+ "FROM gcc_1913_campaign.campaign_request\r\n"
	  		+ "ORDER BY campaign_id DESC\r\n"
	  		+ "LIMIT 1";
	  
	  return jdbcTemplate.queryForObject(sqlQuery,Integer.class); 
  }
  
  public List<Map<String, Object>> getDetailsForStart()
  {
	  String sqlQuery ="SELECT cr.campaign_id,cr.category_id,ca.category_name,cr.campaign_name,start_date\r\n"
	  		+ "FROM gcc_1913_campaign.campaign_request cr \r\n"
	  		+ "join gcc_1913_campaign.category ca ON cr.category_id = ca.category_id \r\n"
	  		+ "where cr.campaign_status='ONGOING' AND cr.isactive='1' and cr.isdelete='0' \r\n"
	  		+ "order by cr.campaign_id desc";
		  
		  return jdbcTemplate.queryForList(sqlQuery); 
  }

  public List<Map<String, Object>> getDataforAgents(int campaign_id, int calls_per_agent)
  {
	  List<Map<String, Object>> assignedAgent_list  =getAssignedAgentId(campaign_id);
	  List<Map<String, Object>> data_list  =getDataId(campaign_id);
	  
	  List<Map<String, Object>> task = getTask(assignedAgent_list,data_list,calls_per_agent,campaign_id);
	  
	// Perform the update query here
	  if (task != null && !task.isEmpty()) {
		  assignAgentIds(task);
		    updateAgentStatus(campaign_id);
		}
	    
	  
		return task;
  }
  
	//Method to update agent_id for each data_id in upload_data table
	public void assignAgentIds(List<Map<String, Object>> taskList) {
	   String updateQuery = "UPDATE gcc_1913_campaign.upload_data SET agent_id = ? WHERE data_id = ? AND campaign_id = ?";
	
	   // Loop through each agent's task list
	   for (Map<String, Object> task : taskList) {
	       Integer agentId = (Integer) task.get("agent_id");
	       Integer campaignId = (Integer) task.get("campaign_id"); 
	       List<Integer> dataIds = (List<Integer>) task.get("data_ids");
	
	       // Update each data_id with the respective agent_id
	       for (Integer dataId : dataIds) {
	           jdbcTemplate.update(updateQuery, agentId, dataId, campaignId);
	       }
	   }
	}
		
	//Example update method
	private void updateAgentStatus(int campaign_id) {
	   String updateQuery = "UPDATE gcc_1913_campaign.campaign_request SET isactive = '0', isdelete='1' WHERE campaign_id = ?";
	   jdbcTemplate.update(updateQuery, campaign_id);
	}


  private List<Map<String, Object>> getTask(List<Map<String, Object>> assignedAgent_list, List<Map<String, Object>> data_list,
			int calls_per_agent,int campaign_id) {

	    List<Map<String, Object>> result = new ArrayList<>();
	    int dataIndex = 0;

	    // Loop through each assigned ID
	    for (Map<String, Object> assigned : assignedAgent_list) {
	        // Create a map to store the assigned_id and corresponding data_ids
	        Map<String, Object> assignedTaskMap = new HashMap<>();
	        List<Integer> assignedDataIds = new ArrayList<>();

	        // Get assigned_id
	        Integer agent_id = (Integer) assigned.get("agent_id");

	        // Loop to assign the appropriate number of data IDs (calls_per_agent)
	        for (int i = 0; i < calls_per_agent; i++) {
	            if (dataIndex >= data_list.size()) {
	                // Exit if we run out of data IDs
	                break;
	            }
	            Map<String, Object> data = data_list.get(dataIndex);
	            Integer data_id = (Integer) data.get("data_id");
	            assignedDataIds.add(data_id);
	            dataIndex++;
	        }

	        // Put the agent_id and the corresponding data_ids in the map
	        assignedTaskMap.put("agent_id", agent_id);
	        assignedTaskMap.put("data_ids", assignedDataIds);
	        assignedTaskMap.put("campaign_id", campaign_id);

	        // Add the map to the result list
	        result.add(assignedTaskMap);
	    }

	    return result;
	}


	private List<Map<String, Object>> getDataId(int campaign_id) {
  		
  		String sqlQuery ="SELECT data_id\r\n"
  				+ "from gcc_1913_campaign.upload_data\r\n"
  				+ "where campaign_id=?";
  			  
  			  return jdbcTemplate.queryForList(sqlQuery, new Object[]{campaign_id});
  	}


	private List<Map<String, Object>> getAssignedAgentId(int campaign_id) {
	
  		String sqlQuery ="SELECT agent_id\r\n"
  				+ "FROM gcc_1913_campaign.agent_assign \r\n"
  				+ "where campaign_id=? and isactive='1'";
  			  
  			  return jdbcTemplate.queryForList(sqlQuery, new Object[]{campaign_id});
  	}
	
	public List<Map<String, Object>> getActiveCampaigns()
	{
		String sqlQuery ="SELECT cr.campaign_id,cr.calls_per_agent,cr.category_id,cr.campaign_name,c.category_name,cr.start_date,cr.end_date,cr.description,cr.campaign_status\r\n"
		  		+ "FROM gcc_1913_campaign.campaign_request cr\r\n"
		  		+ "JOIN gcc_1913_campaign.category c ON cr.category_id=c.category_id\r\n"
		  		+ "where cr.isactive='1' and cr.campaign_status  IN ('ONGOING', 'CLOSED') Order by cr.campaign_id desc";
  			  
  			  return jdbcTemplate.queryForList(sqlQuery);
	}


	public List<Map<String, Object>> viewcampaignstatus(int campaign_id) {
		
		String sqlQuery ="SELECT \r\n"
				+ "aa.campaign_id as campaign_id,\r\n"
				+ "    al.agent_id as agent_id,\r\n"
				+ "    al.agent_name as agent_name,\r\n"
				+ "    COUNT(up.agent_id) AS Assigned_count,\r\n"
				+ "    COUNT(CASE WHEN up.field_14 IS NOT NULL THEN 1 END) AS Complete_count,\r\n"
				+ "    COUNT(CASE WHEN up.field_14 IS NULL THEN 1 END) AS Pending_count,\r\n"
				+ "    ROUND(\r\n"
				+ "        (COUNT(CASE WHEN up.field_14 IS NOT NULL THEN 1 END) * 100) / \r\n"
				+ "        COUNT(up.agent_id)\r\n"
				+ "    ) as Percentage,\r\n"
				+ "    COUNT(CASE WHEN up.field_14 = 'INFORMATION_GIVEN' THEN 1  END) AS Information_Given_count,\r\n"
				+ "	COUNT(CASE WHEN up.field_14 = 'UNATTENDED' THEN 1  END) AS Unattended_count,\r\n"
				+ "	COUNT(CASE WHEN up.field_14 = 'CALL_BACK_LATER' THEN 1  END) AS Call_back_later_count,\r\n"
				+ "	COUNT(CASE WHEN up.field_14 = 'SWITCH_OFF' THEN 1  END) AS Switch_off_count,\r\n"
				+ "	COUNT(CASE WHEN up.field_14 = 'WRONG_NUMBER' THEN 1  END) AS Wrong_number_count\r\n"
				+ "FROM gcc_1913_campaign.agent_assign aa\r\n"
				+ "JOIN gcc_1913_qaqc.agents_list al ON al.agent_id = aa.agent_id\r\n"
				+ "JOIN gcc_1913_campaign.upload_data up ON up.agent_id = al.agent_id AND up.campaign_id = ?\r\n"
				+ "WHERE aa.campaign_id = ? AND aa.isactive = '1'\r\n"
				+ "GROUP BY aa.campaign_id,al.agent_id, al.agent_name";
  			  
  			  return jdbcTemplate.queryForList(sqlQuery,campaign_id,campaign_id);
	}

	public List<Map<String, Object>> viewcompletedby(int campaign_id, int agent_id) {
		
		String sqlQuery ="SELECT \r\n"
				+ "up.data_id as data_id,up.field_1 as id, up.field_2 as name, up.field_3 as address,field_5 as cellnumber, field_14 as call_status, \r\n"
				+ "count(cl.data_id) as call_count,updated_date as updated_date,field_15 as remarks\r\n"
				+ "FROM gcc_1913_campaign.agent_assign aa\r\n"
				+ "JOIN gcc_1913_campaign.upload_data up ON up.agent_id = aa.agent_id AND up.campaign_id = ?\r\n"
				+ "JOIN gcc_1913_campaign.call_logs cl ON cl.data_id=up.data_id\r\n"
				+ "WHERE aa.campaign_id = ? AND aa.isactive = '1' AND aa.agent_id=? AND up.field_14 IS NOT NULL\r\n"
				+ "group by up.data_id,up.field_1, up.field_2";
  			  
  			  return jdbcTemplate.queryForList(sqlQuery,campaign_id,campaign_id,agent_id);
	}

	public String getExcelUrlByCampaignId(int lastId) {
	    String sqlQuery = "SELECT excel_url FROM gcc_1913_campaign.campaign_request WHERE campaign_id = ?";
	    return jdbcTemplate.queryForObject(sqlQuery, String.class, lastId);
	}


	
	public void saveUploadData(String category, int campaignId, Map<String, Object> rowData, List<String> expectedHeaders) {
	    int fieldCount = rowData.size();
	    
	    List<String> columnNames = new ArrayList<>();
	    for (int i = 1; i <= fieldCount; i++) {
	        columnNames.add("field_" + i);
	    }

	    String columnNamesString = String.join(", ", columnNames);
	    //String placeholders = columnNames.stream().map(col -> "?").collect(Collectors.joining(", "));

	    StringBuilder placeholdersBuilder = new StringBuilder();

	    for (int i = 0; i < columnNames.size(); i++) {
	        if (i > 0) {
	            placeholdersBuilder.append(", ");
	        }
	        placeholdersBuilder.append("?");
	    }

	    String placeholders = placeholdersBuilder.toString();
	    
	    String sql = "INSERT INTO gcc_1913_campaign.upload_data (category_id, campaign_id, " + columnNamesString + ") VALUES (?, ?, " + placeholders + ")";

	    //System.out.println("SQL Query========= " + sql);

	    List<Object> values = new ArrayList<>();
	    values.add(category);
	    values.add(campaignId);

	    // Ensure rowData values are ordered based on expected headers
	    for (String header : expectedHeaders) {
	        values.add(rowData.getOrDefault(header, ""));  // Ensures the correct mapping
	    }

	    final Logger logger = LoggerFactory.getLogger(CampaignService.class);
	    try {
	        jdbcTemplate.update(sql, values.toArray());
	    } catch (Exception e) {
	        logger.error("Error inserting upload data for campaign {}: {}", campaignId, e.getMessage());
	    }
	}


//	public void saveUploadData(String category, int campaignId, Map<String, Object> rowData) {
//	    // Get total number of fields in the row
//	    int fieldCount = rowData.size();
//	    
//	    // Generate dynamic column names as field_1, field_2, ..., field_N
//	    List<String> columnNames = new ArrayList<>();
//	    for (int i = 1; i <= fieldCount; i++) {
//	        columnNames.add("field_" + i);
//	    }
//
//	    // Build dynamic SQL query
//	    String columnNamesString = String.join(", ", columnNames);
//	    String placeholders = columnNames.stream().map(col -> "?").collect(Collectors.joining(", "));
//
//	    String sql = "INSERT INTO upload_data (category_id, campaign_id, " + columnNamesString + ") VALUES (?, ?, " + placeholders + ")";
//
//	    System.out.println("SQL Query========= " + sql);
//
//	    // Prepare values list
//	    List<Object> values = new ArrayList<>();
//	    values.add(category);
//	    values.add(campaignId);
//	    values.addAll(rowData.values());  // Add row data values dynamically
//	    
//
//
//	    final Logger logger = LoggerFactory.getLogger(CampaignService.class);
//	    try {
//	        jdbcTemplate.update(sql, values.toArray());
//	    } catch (Exception e) {
//	        logger.error("Error inserting upload data for campaign {}: {}", campaignId, e.getMessage());
//	    }
//	}


	
	

	//Agent List
	  public List<Map<String, Object>> getAllAgents() {
	      String sql = "SELECT agent_id, agent_name FROM gcc_1913_qaqc.agents_list WHERE calling_type = 'OUTBOUND' AND isactive = '1'";
	      return jdbcTemplate.queryForList(sql);
	  }

	//count based on total, completed and pending
	  public Map<String, Object> getCampaignStatistics(int campaignId) {
	      Map<String, Object> result = new HashMap<>();

	      // Total Count
	      String totalQuery = "SELECT COUNT(*) AS total FROM gcc_1913_campaign.upload_data WHERE campaign_id = ?";
	      int totalCount = jdbcTemplate.queryForObject(totalQuery, Integer.class, campaignId);
	      result.put("totalCount", totalCount);

	      // Completed Count (agent_id is not null and call_status is null)
	      String completedQuery = "SELECT COUNT(*) AS completed FROM gcc_1913_campaign.upload_data ud\r\n"
	      		+ "JOIN gcc_1913_campaign.campaign_request cr ON ud.campaign_id = cr.campaign_id\r\n"
	      		+ "WHERE ud.campaign_id = ? AND cr.campaign_status = 'ONGOING' AND ud.agent_id IS NOT NULL AND\r\n"
	      		+ "( ud.call_status NOT IN ('CALL BACK LATER','UNATTENDED'))";
	      int completedCount = jdbcTemplate.queryForObject(completedQuery, Integer.class, campaignId);
	      result.put("completedCount", completedCount);

	      // Pending Count (agent_id is not null and call_status is not null)
		      String pendingQuery = "SELECT COUNT(*) AS pending FROM gcc_1913_campaign.upload_data ud\r\n"
		      		+ "JOIN gcc_1913_campaign.campaign_request cr ON ud.campaign_id = cr.campaign_id\r\n"
		      		+ "WHERE ud.campaign_id = ? AND ud.agent_id IS NOT NULL \r\n"
		      		+ "AND (ud.call_status IS NULL OR ud.call_status IN ('CALL BACK LATER','UNATTENDED'))";
	      int pendingCount = jdbcTemplate.queryForObject(pendingQuery, Integer.class, campaignId);
	      result.put("pendingCount", pendingCount);

	      return result;
	  }

	
	  
	  public List<Map<String, Object>> getCampaignDetailsById(Long campaign_id) {
		    String sql = "SELECT \r\n"
		            + "    cr.campaign_id, \r\n"
		            + "    cr.campaign_name, \r\n"
		            + "    cr.category_id, \r\n"
		            + "    cr.start_date, \r\n"
		            + "    cr.end_date, \r\n"		            
		            + "    cr.calls_per_agent, \r\n"
		            + "    cr.description, \r\n"
		            + "    cr.created_date, \r\n"
		            + "    cr.excel_url, \r\n"
		            + "    cr.isactive, \r\n"
		            + "    cr.isdelete, \r\n"
		            + "    cr.campaign_status, \r\n"
		            + "    cq.question_id,  \r\n"
		            + "    cq.questions, \r\n"
		            + "    cq.answer_type_id,  \r\n"
		            + "    at.type_name,  \r\n"
		            + "    ca.id AS answer_id, \r\n"
		            + "    ca.campaign_answer\r\n"
		            + "FROM gcc_1913_campaign.campaign_request cr\r\n"
		            + "JOIN gcc_1913_campaign.campaign_questions cq \r\n"
		            + "    ON cr.campaign_id = cq.campaign_id\r\n"
		            + "LEFT JOIN gcc_1913_campaign.answer_type at\r\n"
		            + "    ON cq.answer_type_id = at.answer_type_id  \r\n"
		            + "LEFT JOIN gcc_1913_campaign.campaign_answers ca\r\n"
		            + "    ON cq.question_id = ca.question_id\r\n"
		            + "WHERE cr.campaign_id  = ?";

		    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, campaign_id);

		    // Group answers by question_id
		    Map<Long, Map<String, Object>> groupedData = new LinkedHashMap();

		    for (Map<String, Object> row : results) {
		        Object questionIdObj = row.get("question_id");
		        Long questionId = null;
		        
		        // Convert Integer to Long if needed
		        if (questionIdObj instanceof Integer) {
		            questionId = ((Integer) questionIdObj).longValue();
		        } else if (questionIdObj instanceof Long) {
		            questionId = (Long) questionIdObj;
		        }

		        if (questionId == null) continue;

		        if (!groupedData.containsKey(questionId)) {
		            // Ensure `campaign_answer` is initialized correctly
		            List<String> answers = new ArrayList<>();
		            if (row.get("campaign_answer") != null) {
		                answers.add(row.get("campaign_answer").toString());
		            }
		            row.put("campaign_answer", answers);
		            groupedData.put(questionId, row);
		        } else {
		            // Append the answer to the existing question
		            List<String> answers = (List<String>) groupedData.get(questionId).get("campaign_answer");
		            if (row.get("campaign_answer") != null) {
		                answers.add(row.get("campaign_answer").toString());
		            }
		        }
		    }

		    return new ArrayList<>(groupedData.values());
		}
	  

	  
	  
	  //question and answers counts with percentage in report page method
	  public List<Map<String, Object>> getQuestionAnswers(int campaignId) {
	        String sql = "SELECT cq.question_id, cq.questions, ca.campaign_answer, " +
	                     "COUNT(qa.campaign_answer) AS answer_count, " +
	                     "FLOOR(IFNULL((COUNT(qa.campaign_answer) / " +
	                     "(SELECT COUNT(*) FROM gcc_1913_campaign.upload_data " +
	                     "WHERE campaign_id = cq.campaign_id AND call_status = 'INFORMATION GIVEN') * 100), 0)) AS answer_percentage " +
	                     "FROM campaign_questions cq " +
	                     "JOIN campaign_answers ca ON cq.question_id = ca.question_id AND cq.campaign_id = ca.campaign_id " +
	                     "LEFT JOIN questions_answers qa ON cq.question_id = qa.question_id AND ca.campaign_answer = qa.campaign_answer AND cq.campaign_id = qa.campaign_id " +
	                     "WHERE cq.campaign_id = ? AND cq.isactive = 1 AND cq.isdelete = 0 " +
	                     "GROUP BY cq.question_id, cq.questions, ca.campaign_answer " +
	                     "ORDER BY cq.question_id;";

	        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, campaignId);
	        Map<Integer, Map<String, Object>> responseMap = new LinkedHashMap<>();

	        for (Map<String, Object> row : result) {
	            int questionId = (int) row.get("question_id");
	            if (!responseMap.containsKey(questionId)) {
	                Map<String, Object> questionData = new HashMap<>();
	                questionData.put("question_id", questionId);
	                questionData.put("questions", row.get("questions"));
	                questionData.put("answers", new ArrayList<>());
	                responseMap.put(questionId, questionData);
	            }

	            Map<String, Object> answerData = new HashMap<>();
	            answerData.put("campaign_answer", row.get("campaign_answer"));
	            answerData.put("answer_count", row.get("answer_count"));
	            answerData.put("answer_percentage", row.get("answer_percentage"));

	            ((List<Map<String, Object>>) responseMap.get(questionId).get("answers")).add(answerData);
	        }

	        return new ArrayList<>(responseMap.values());
	    }
	  
	  
	  //////////////dizo//////////////
	  
	  public int closeCampaign(int campaignId) {
	        String sql = "UPDATE gcc_1913_campaign.campaign_request SET campaign_status = 'CLOSED' WHERE campaign_id = ?";
	        int rowsAffected = jdbcTemplate.update(sql, campaignId);

	        if (rowsAffected == 0) {
	            System.out.println("Failed to update. Check if campaignId exists: " + campaignId);
	        } else {
	            System.out.println("Campaign ID " + campaignId + " successfully updated to CLOSED.");
	        }

	        return rowsAffected;
	    }
	  
	    public int autoCloseExpiredCampaigns() {
	        String sql = "UPDATE gcc_1913_campaign.campaign_request " +
	                     "SET campaign_status = 'CLOSED' " +
	                     "WHERE campaign_status = 'ONGOING' AND end_date < CURRENT_DATE";

	        int rowsAffected = jdbcTemplate.update(sql);

	        if (rowsAffected == 0) {
	            System.out.println("No expired campaigns found to close.");
	        } else {
	            System.out.println(rowsAffected + " campaigns successfully updated to CLOSED.");
	        }

	        return rowsAffected;
	    }
	    
	    // Scheduled job to run daily at midnight
		/*
		 * @Scheduled(cron = "0 0 0 * * ?") public void scheduledAutoClose() {
		 * System.out.println("Running scheduled auto-close for expired campaigns...");
		 * autoCloseExpiredCampaigns(); }
		 */
	 


}
