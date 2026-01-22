package in.gov.chennaicorporation.gccoffice.callcenter.service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CampaignReportService {
	

    private JdbcTemplate jdbcTemplate;
	
  @Autowired
	public void setDataSourcemysql(@Qualifier("mysql1913CampaignDataSource") DataSource dataSource) {		
		this.jdbcTemplate = new JdbcTemplate(dataSource);	
		}
  
  @Autowired
	public void setDataSource2(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	

	public List<Map<String, Object>> getCampaignData(String startDate, String endDate) {
        StringBuilder query = new StringBuilder(
            "SELECT " +
            "    cr.campaign_id, " +
            "    cr.category_id, " +
            "    cr.campaign_name, " +
            "    cc.category_name, " +
            "    cr.start_date, " +
            "    cr.end_date, " +
            "    COUNT(ud.campaign_id) AS total_count, " +
            "    COUNT(ud.agent_id) AS assigned_count, " +
            "    COUNT(ud.campaign_id) - COUNT(ud.agent_id) AS not_assigned_count, " +
            "    COUNT(CASE WHEN ud.call_status = 'INFORMATION GIVEN' THEN 1 ELSE NULL END) AS informationgiven_count, " +
            "    COUNT(CASE WHEN ud.call_status = 'CALL BACK LATER' THEN 1 ELSE NULL END) AS callback_count, " +
            "    COUNT(CASE WHEN ud.call_status = 'UNATTENDED' THEN 1 ELSE NULL END) AS unattended_count, " +
            "    COUNT(CASE WHEN ud.call_status = 'SWITCH OFF' THEN 1 ELSE NULL END) AS switchoff_count, " +
            "    COUNT(CASE WHEN ud.call_status = 'WRONG NUMBER' THEN 1 ELSE NULL END) AS wrongnumber_count " +
            "FROM gcc_1913_campaign.campaign_request cr " +
            "JOIN gcc_1913_campaign.upload_data ud ON cr.campaign_id = ud.campaign_id " +
            "JOIN gcc_1913_campaign.category cc ON cr.category_id = cc.category_id " +
            "WHERE cr.campaign_status IN ('ONGOING', 'CLOSED') " +
            "AND ud.agent_id IS NOT NULL "
        );

        // Apply date filter dynamically if dates are provided
        if (startDate != null && endDate != null) {
            query.append("AND DATE(cr.created_date) BETWEEN ? AND ? ");
        }

        query.append("GROUP BY cr.campaign_id ");
        query.append("ORDER BY cr.campaign_id DESC");

        // Execute query with or without parameters
        if (startDate != null && endDate != null) {
            return jdbcTemplate.queryForList(query.toString(), startDate, endDate);
        } else {
            return jdbcTemplate.queryForList(query.toString());
        }
    }
	
	   public List<Map<String, Object>> getAgentwiseData(String campaignId) {
	        String query = "SELECT aa.agent_id, al.agent_name, \r\n"
	        		+ "    COUNT(ud.agent_id) AS assigned_count, \r\n"
	        		+ "    COUNT(CASE WHEN ud.is_processed = 1 THEN 1 ELSE NULL END) AS completed_count, \r\n"
	        		+ "    COUNT(ud.agent_id) - COUNT(CASE WHEN ud.is_processed = 1 THEN 1 ELSE NULL END) AS pending_count, \r\n"
	        		+ "    COUNT(CASE WHEN ud.call_status = 'INFORMATION GIVEN' THEN 1 ELSE NULL END) AS informationgiven_count, \r\n"
	        		+ "    COUNT(CASE WHEN ud.call_status = 'CALL BACK LATER' THEN 1 ELSE NULL END) AS callback_count, \r\n"
	        		+ "    COUNT(CASE WHEN ud.call_status = 'UNATTENDED' THEN 1 ELSE NULL END) AS unattended_count, \r\n"
	        		+ "    COUNT(CASE WHEN ud.call_status = 'SWITCH OFF' THEN 1 ELSE NULL END) AS switchoff_count, \r\n"
	        		+ "    COUNT(CASE WHEN ud.call_status = 'WRONG NUMBER' THEN 1 ELSE NULL END) AS wrongnumber_count, \r\n"
	        		+ "    CASE \r\n"
	        		+ "        WHEN COUNT(ud.agent_id) = 0 THEN 0 \r\n"
	        		+ "        ELSE FLOOR((COUNT(CASE WHEN ud.is_processed = 1 THEN 1 ELSE NULL END) * 100) / COUNT(ud.agent_id))\r\n"
	        		+ "    END AS completed_percentage\r\n"
	        		+ "FROM gcc_1913_campaign.agent_assign aa \r\n"
	        		+ "JOIN gcc_1913_qaqc.agents_list al ON al.agent_id = aa.agent_id \r\n"
	        		+ "JOIN gcc_1913_campaign.upload_data ud ON ud.agent_id = aa.agent_id AND ud.campaign_id = aa.campaign_id\r\n"
	        		+ "WHERE aa.campaign_id = ? AND aa.isactive = 1 AND aa.isdelete = 0 AND ud.agent_id IS NOT NULL \r\n"
	        		+ "GROUP BY aa.agent_id, al.agent_name \r\n"
	        		+ "ORDER BY aa.agent_id";
	        
	        return jdbcTemplate.queryForList(query, campaignId);
	    }
	   
	   //report for campaign details
	   public List<Map<String, Object>> getAgentReportCampaignId(String campaignId) {
	        String query = "SELECT cr.category_id,cr.campaign_id, cc.category_name, \r\n"
	        		+ "	cr.start_date, cr.end_date, \r\n"
	        		+ "    cr.description, date (cr.created_date) as created_date  \r\n"
	        		+ "    FROM gcc_1913_campaign.category cc \r\n"
	        		+ "    join gcc_1913_campaign.campaign_request cr on cr.category_id = cc.category_id \r\n"
	        		+ "    where cr.campaign_id = ?;";
	        
	        return jdbcTemplate.queryForList(query, campaignId);
	    }
	   
	   
	   
	   //get campaignname 
	   public String getCampaignName(int campaignId) {
	        String query = "SELECT campaign_name FROM gcc_1913_campaign.campaign_request WHERE campaign_id = ?";
	        
	        return jdbcTemplate.queryForObject(query, String.class, campaignId);
	    }
	   
	   
	   
	   //question and answers counts with percentage in report page method

		  public List<Map<String, Object>> getQuestionAnswers(int campaignId) {
			  String sql = "SELECT cq.question_id, cq.questions, ca.campaign_answer,"
			  		+ " COUNT(qa.campaign_answer) AS answer_count,"
			  		+ "         FLOOR("
					+ "             IFNULL((COUNT(qa.campaign_answer) / NULLIF(total_info.total_information_given, 0)) * 100, 0)"
					+ "         ) AS answer_percentage"
					 + "    FROM "
					+ "         gcc_1913_campaign.campaign_questions cq"
					+ "     JOIN "
					 + "        gcc_1913_campaign.campaign_answers ca "
					 + "        ON cq.question_id = ca.question_id AND cq.campaign_id = ca.campaign_id"
					 + "    LEFT JOIN "
					 + "        gcc_1913_campaign.questions_answers qa "
					 + "        ON cq.question_id = qa.question_id "
					 + "        AND ca.campaign_answer = qa.campaign_answer "
					 + "        AND cq.campaign_id = qa.campaign_id"
					  + "   LEFT JOIN ("
					  + "       SELECT campaign_id, COUNT(*) AS total_information_given"
					  + "       FROM gcc_1913_campaign.upload_data "
					  + "       WHERE call_status = 'INFORMATION GIVEN'"
					  + "       GROUP BY campaign_id"
					  + "   ) AS total_info ON total_info.campaign_id = cq.campaign_id"
					  + "   WHERE "
					   + "      cq.campaign_id = ? "
					   + "      AND cq.isactive = 1 "
					   + "      AND cq.isdelete = 0"
					   + "  GROUP BY "
					   + "      cq.question_id, cq.questions, ca.campaign_answer, total_info.total_information_given"
					   + "  ORDER BY "
					   + "      cq.question_id";

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
		  
		  //
		  
		  
		  

//		  public void generateCSV(int categoryId, int campaignId, PrintWriter writer) {
//			    List<Map<String, Object>> resultList = new ArrayList<>();
//
//			    // Fetch ordered field column names
//			    String fetchColumnsSQL = "SELECT COLUMN_NAME " +
//			            "FROM INFORMATION_SCHEMA.COLUMNS " +
//			            "WHERE TABLE_SCHEMA = 'gcc_1913_campaign' " +
//			            "AND TABLE_NAME = 'upload_data' " +
//			            "AND COLUMN_NAME LIKE 'field_%' " +
//			            "ORDER BY CAST(SUBSTRING(COLUMN_NAME, 7) AS UNSIGNED)";
//
//			    List<String> fieldColumns = jdbcTemplate.queryForList(fetchColumnsSQL, String.class);
//
//			    if (fieldColumns.isEmpty()) {
//			        writer.println("No matching columns found");
//			        return;
//			    }
//
//			    // Fetch ordered fields data based on campaign_id, including data_id, agent_id, and call_status
//			    String query1 = "SELECT data_id, " + String.join(", ", fieldColumns) + ", agent_id, call_status,remarks " +
//			                    "FROM gcc_1913_campaign.upload_data WHERE campaign_id = ?";
//			    
//			    List<Map<String, Object>> orderedFieldsList = jdbcTemplate.queryForList(query1, campaignId);
//
//			    if (orderedFieldsList.isEmpty()) {
//			        writer.println("No data found for campaign_id: " + campaignId);
//			        return;
//			    }
//
//			    // Fetch category columns
//			    String query2 = "SELECT column_name FROM gcc_1913_campaign.category_columns WHERE category_id = ? AND isactive=1";
//			    List<String> categoryColumns = jdbcTemplate.queryForList(query2, String.class, categoryId);
//
//			    if (categoryColumns.isEmpty()) {
//			        writer.println("No category columns found for category_id: " + categoryId);
//			        return;
//			    }
//
//			    int categoryCount = categoryColumns.size();
//
//			    // Fetch Agent Names from agent_id
//			    String fetchAgentSQL = "SELECT agent_id, agent_name FROM gcc_1913_qaqc.agents_list";
//			    Map<Integer, String> agentMap = jdbcTemplate.query(fetchAgentSQL, rs -> {
//			        Map<Integer, String> map = new HashMap<>();
//			        while (rs.next()) {
//			            map.put(rs.getInt("agent_id"), rs.getString("agent_name"));
//			        }
//			        return map;
//			    });
//
//			    // Fetch Questions for given campaign_id
//			    String fetchQuestionsQuery = "SELECT question_id, questions FROM gcc_1913_campaign.campaign_questions WHERE campaign_id = ?";
//			    Map<Integer, String> questionsMap = jdbcTemplate.query(fetchQuestionsQuery, rs -> {
//			        Map<Integer, String> map = new LinkedHashMap<>();
//			        while (rs.next()) {
//			            map.put(rs.getInt("question_id"), rs.getString("questions"));
//			        }
//			        return map;
//			    }, campaignId);
//
//			    // Fetch Answers mapped to questions and grouped by data_id
//			    String fetchAnswersQuery = "SELECT data_id, question_id, campaign_answer FROM gcc_1913_campaign.questions_answers " +
//			                               "WHERE campaign_id = ?";
//			    Map<Integer, Map<Integer, String>> answersMap = jdbcTemplate.query(fetchAnswersQuery, rs -> {
//			        Map<Integer, Map<Integer, String>> map = new HashMap<>();
//			        while (rs.next()) {
//			            int dataId = rs.getInt("data_id");
//			            int questionId = rs.getInt("question_id");
//			            String answer = rs.getString("campaign_answer");
//
//			            map.computeIfAbsent(dataId, k -> new HashMap<>()).put(questionId, answer);
//			        }
//			        return map;
//			    }, campaignId);
//
//			    // Process each row and collect data
//			    for (Map<String, Object> orderedFields : orderedFieldsList) {
//			        List<Object> nonNullValues = new ArrayList<>();
//			        for (String field : fieldColumns) {
//			            Object value = orderedFields.get(field);
//			            if (value != null) {
//			                nonNullValues.add(value);
//			                if (nonNullValues.size() == categoryCount) {
//			                    break; // Stop after collecting required count
//			                }
//			            }
//			        }
//
//			        if (nonNullValues.size() < categoryCount) {
//			            continue; // Skip if not enough non-null values
//			        }
//
//			        Integer dataId = orderedFields.get("data_id") != null ? ((Number) orderedFields.get("data_id")).intValue() : null;
//
//			        // Map category columns to non-null ordered field values
//			        Map<String, Object> finalMapping = new LinkedHashMap<>();
//
//			        // Add category columns
//			        for (int i = 0; i < categoryCount; i++) {
//			            finalMapping.put(categoryColumns.get(i), nonNullValues.get(i));
//			        }
//
//			        // Add Questions and Corresponding Answers for this specific data_id
//			        Map<Integer, String> currentAnswers = answersMap.getOrDefault(dataId, new HashMap<>());
//			        for (Map.Entry<Integer, String> entry : questionsMap.entrySet()) {
//			            Integer questionId = entry.getKey();
//			            String questionText = entry.getValue();
//			            String answer = currentAnswers.getOrDefault(questionId, "-"); // Default to "-"
//
//			            finalMapping.put(questionText, answer);
//			        }
//			        
//			        //Integer agentId = ((Number) orderedFields.get("agent_id")).intValue();
//			        
//			        Integer agentId = orderedFields.get("agent_id") != null ? ((Number) orderedFields.get("agent_id")).intValue() : null;
//			        String agentName = agentMap.getOrDefault(agentId, "Not Assigned");
//			        finalMapping.put("Agent Name", agentName);
//
//			        String callstatus = orderedFields.get("call_status") != null ? orderedFields.get("call_status").toString() : "-";
//			        finalMapping.put("Call Status", callstatus);
//
//			        // Add Remarks
//			        String remarks = orderedFields.get("remarks") != null ? orderedFields.get("remarks").toString() : "-";
//
//			        finalMapping.put("Remarks", remarks);
//
//			        resultList.add(finalMapping);
//			    }
//
//			    // Write CSV data
//			    if (resultList.isEmpty()) {
//			        writer.println("No Data Available");
//			        return;
//			    }
//
//			    // Extract headers from the first row
//			    String headers = String.join(",", resultList.get(0).keySet());
//			    writer.println(headers);
//
//			    // Write data rows
//			    for (Map<String, Object> rowData : resultList) {
//			        List<String> values = rowData.values().stream()
//			                .map(value -> value != null ? value.toString() : "")
//			                .collect(Collectors.toList());
//			        writer.println(String.join(",", values));
//			    }
//			}
		  
		  
		// Method to escape CSV values
		  private String escapeCsvValue(String value) {
		      if (value == null) return "";
		      
		      // If the value contains commas, double quotes, or newlines, wrap it in quotes and escape inner quotes
		      if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
		          value = "\"" + value.replace("\"", "\"\"") + "\"";
		      }
		      return value;
		  }

		  public void generateCSV(int categoryId, int campaignId, PrintWriter writer) {
		      List<Map<String, Object>> resultList = new ArrayList<>();

		      // Fetch ordered field column names
		      String fetchColumnsSQL = "SELECT COLUMN_NAME " +
		              "FROM INFORMATION_SCHEMA.COLUMNS " +
		              "WHERE TABLE_SCHEMA = 'gcc_1913_campaign' " +
		              "AND TABLE_NAME = 'upload_data' " +
		              "AND COLUMN_NAME LIKE 'field_%' " +
		              "ORDER BY CAST(SUBSTRING(COLUMN_NAME, 7) AS UNSIGNED)";

		      List<String> fieldColumns = jdbcTemplate.queryForList(fetchColumnsSQL, String.class);

		      if (fieldColumns.isEmpty()) {
		          writer.println("No matching columns found");
		          return;
		      }

		      // Fetch ordered fields data based on campaign_id, including data_id, agent_id, and call_status
		      String query1 = "SELECT data_id, " + String.join(", ", fieldColumns) + ", agent_id, call_status,remarks,DATE(updated_date) as updated_date " +
                      "FROM gcc_1913_campaign.upload_data WHERE campaign_id = ?";
		      
		      List<Map<String, Object>> orderedFieldsList = jdbcTemplate.queryForList(query1, campaignId);

		      if (orderedFieldsList.isEmpty()) {
		          writer.println("No data found for campaign_id: " + campaignId);
		          return;
		      }

		      // Fetch category columns
		      String query2 = "SELECT column_name FROM gcc_1913_campaign.category_columns WHERE category_id = ? AND isactive=1";
		      List<String> categoryColumns = jdbcTemplate.queryForList(query2, String.class, categoryId);

		      if (categoryColumns.isEmpty()) {
		          writer.println("No category columns found for category_id: " + categoryId);
		          return;
		      }

		      int categoryCount = categoryColumns.size();

		      // Fetch Agent Names from agent_id
		      String fetchAgentSQL = "SELECT agent_id, agent_name FROM gcc_1913_qaqc.agents_list";
		      Map<Integer, String> agentMap = jdbcTemplate.query(fetchAgentSQL, rs -> {
		          Map<Integer, String> map = new HashMap<>();
		          while (rs.next()) {
		              map.put(rs.getInt("agent_id"), rs.getString("agent_name"));
		          }
		          return map;
		      });

		      // Fetch Questions for given campaign_id
		      String fetchQuestionsQuery = "SELECT question_id, questions FROM gcc_1913_campaign.campaign_questions WHERE campaign_id = ?";
		      Map<Integer, String> questionsMap = jdbcTemplate.query(fetchQuestionsQuery, rs -> {
		          Map<Integer, String> map = new LinkedHashMap<>();
		          while (rs.next()) {
		              map.put(rs.getInt("question_id"), rs.getString("questions"));
		          }
		          return map;
		      }, campaignId);

		      // Fetch Answers mapped to questions and grouped by data_id
		      String fetchAnswersQuery = "SELECT data_id, question_id, campaign_answer FROM gcc_1913_campaign.questions_answers " +
		                                 "WHERE campaign_id = ?";
		      Map<Integer, Map<Integer, String>> answersMap = jdbcTemplate.query(fetchAnswersQuery, rs -> {
		          Map<Integer, Map<Integer, String>> map = new HashMap<>();
		          while (rs.next()) {
		              int dataId = rs.getInt("data_id");
		              int questionId = rs.getInt("question_id");
		              String answer = rs.getString("campaign_answer");

		              map.computeIfAbsent(dataId, k -> new HashMap<>()).put(questionId, answer);
		          }
		          return map;
		      }, campaignId);

		      // Process each row and collect data
		      for (Map<String, Object> orderedFields : orderedFieldsList) {
		          List<Object> nonNullValues = new ArrayList<>();
		          for (String field : fieldColumns) {
		              Object value = orderedFields.get(field);
		              if (value != null) {
		                  nonNullValues.add(value);
		                  if (nonNullValues.size() == categoryCount) {
		                      break; // Stop after collecting required count
		                  }
		              }
		          }

		          if (nonNullValues.size() < categoryCount) {
		              continue; // Skip if not enough non-null values
		          }

		          Integer dataId = orderedFields.get("data_id") != null ? ((Number) orderedFields.get("data_id")).intValue() : null;

		          // Map category columns to non-null ordered field values
		          Map<String, Object> finalMapping = new LinkedHashMap<>();

		          // Add category columns
		          for (int i = 0; i < categoryCount; i++) {
		              finalMapping.put(categoryColumns.get(i), nonNullValues.get(i));
		          }

		          // Add Questions and Corresponding Answers for this specific data_id
		          Map<Integer, String> currentAnswers = answersMap.getOrDefault(dataId, new HashMap<>());
		          for (Map.Entry<Integer, String> entry : questionsMap.entrySet()) {
		              Integer questionId = entry.getKey();
		              String questionText = entry.getValue();
		              String answer = currentAnswers.getOrDefault(questionId, "-"); // Default to "-"

		              finalMapping.put(questionText, answer);
		          }

		          Integer agentId = orderedFields.get("agent_id") != null ? ((Number) orderedFields.get("agent_id")).intValue() : null;
		          String agentName = agentMap.getOrDefault(agentId, "Not Assigned");
		          finalMapping.put("Agent Name", agentName);

		          String callstatus = orderedFields.get("call_status") != null ? orderedFields.get("call_status").toString() : "-";
		          finalMapping.put("Call Status", callstatus);

		          // Add Remarks
		          String remarks = orderedFields.get("remarks") != null ? orderedFields.get("remarks").toString() : "-";
		          finalMapping.put("Remarks", remarks);

		          String up_date = orderedFields.get("updated_date") != null ? orderedFields.get("updated_date").toString() : "-";
		          finalMapping.put("Updated_Date", up_date);
		          
		          resultList.add(finalMapping);
		      }

		      // Write CSV data
		      if (resultList.isEmpty()) {
		          writer.println("No Data Available");
		          return;
		      }
/*
		      // Extract headers from the first row and escape them
		      String headers = resultList.get(0).keySet().stream()
		          .map(this::escapeCsvValue)  // Escape each header value
		          .collect(Collectors.joining(","));
		      writer.println(headers);

		      // Write data rows
		      for (Map<String, Object> rowData : resultList) {
		          List<String> values = rowData.values().stream()
		                  .map(value -> escapeCsvValue(value != null ? value.toString() : ""))
		                  .collect(Collectors.toList());
		          writer.println(String.join(",", values));
		      }
		      */
		   // Extract headers from the first row and escape them
		      Map<String, Object> firstRow = resultList.get(0);

		      StringBuilder headerBuilder = new StringBuilder();
		      int headerIndex = 0;

		      for (String key : firstRow.keySet()) {
		          if (headerIndex > 0) {
		              headerBuilder.append(",");
		          }
		          headerBuilder.append(escapeCsvValue(key));
		          headerIndex++;
		      }

		      writer.println(headerBuilder.toString());

		      // Write data rows
		      for (Map<String, Object> rowData : resultList) {

		          StringBuilder rowBuilder = new StringBuilder();
		          int valueIndex = 0;

		          for (Object value : rowData.values()) {
		              if (valueIndex > 0) {
		                  rowBuilder.append(",");
		              }
		              rowBuilder.append(
		                  escapeCsvValue(value != null ? value.toString() : "")
		              );
		              valueIndex++;
		          }

		          writer.println(rowBuilder.toString());
		      }
		      
		  }

	   
}


