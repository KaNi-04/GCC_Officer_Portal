package in.gov.chennaicorporation.gccoffice.callcenter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AgentsService {
	
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
	

		// Utility method to get cell value based on type
		private String getCellValue(Cell cell) {
		    if (cell == null) {
		        return "";
		    }
		    switch (cell.getCellType()) {
		        case STRING:
		            return cell.getStringCellValue();
		        case NUMERIC:
		            // Check if the numeric cell contains a date
		            if (DateUtil.isCellDateFormatted(cell)) {
		                return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
		            }
		            return String.valueOf((long) cell.getNumericCellValue()); // Cast to long to avoid scientific notation
		        case BOOLEAN:
		            return String.valueOf(cell.getBooleanCellValue());
		        case FORMULA:
		            return cell.getCellFormula();
		        default:
		            return "";
		    }
		}

		// Parse integer values (for ID)
		private Integer parseInteger(String value) {
		    try {
		        return Integer.parseInt(value);
		    } catch (NumberFormatException e) {
		        return null; // return null if parsing fails
		    }
		}

		// Parse long values (for Mobile number)
		private Long parseLong(String value) {
		    try {
		        return Long.parseLong(value);
		    } catch (NumberFormatException e) {
		        return null; // return null if parsing fails
		    }
		}

		// Example fetch method
		public List<Map<String, Object>> fetchDataFromExcel(int categoryId,Long campaignId) throws IOException {
		    String excelUrl = getExcelUrlByCampaignId(campaignId);
		    List<Map<String, Object>>data=getExcelData(categoryId,campaignId, excelUrl);
		    return saveExcelData(data);
		}
		
		public String getExcelUrlByCampaignId(Long campaignId) {
	        String sql = "SELECT excel_url\r\n"
	        		+ "FROM gcc_1913_campaign.campaign_request\r\n"
	        		+ "WHERE campaign_id = ?";
	        return jdbcTemplate.queryForObject(sql, String.class, campaignId);
	    }
		
		 public List<Map<String, Object>> getExcelData(int categoryId,Long campaignId,String filePath) throws IOException {
			    List<Map<String, Object>> data = new ArrayList<>();
			    File file = new File(filePath);
			    
			    try (FileInputStream fis = new FileInputStream(file);
			         Workbook workbook = new XSSFWorkbook(fis)) {

			        Sheet sheet = workbook.getSheetAt(0);
			        for (Row row : sheet) {
			            if (row.getRowNum() == 0) continue; // skip header row

			            Map<String, Object> rowData = new HashMap<>();
			            
			            // Add the campaign_id and category_id to each row

		                rowData.put("category_id", categoryId);

		                rowData.put("campaign_id", campaignId);

			            // Convert ID to Integer
			            rowData.put("ID", getCellValue(row.getCell(0)));
			            
			            // Name remains String
			            rowData.put("Name", getCellValue(row.getCell(1)));
			            
			            // Address remains String
			            rowData.put("Address", getCellValue(row.getCell(2)));
			            
			            // Dues remains String (you can convert if necessary)
			            rowData.put("Balance amount",  parseLong(getCellValue(row.getCell(3))));
			            
			            // Convert Mobile number to Long
			            rowData.put("Mobile number", parseLong(getCellValue(row.getCell(4))));
			            
			            rowData.put("Tiny url", getCellValue(row.getCell(5)));

			            data.add(rowData);
			        }
			    }
			    return data;
			}
		 
		 public List<Map<String, Object>> saveExcelData(List<Map<String, Object>> data) {

			    // Prepare SQL query for inserting the specified columns
			    String sql = "INSERT INTO gcc_1913_campaign.upload_data (category_id, campaign_id, field_1, field_2, field_3, field_4, field_5,field_6) " +
			                 "VALUES (?, ?, ?, ?, ?, ?, ?,?)";

			    // Loop through the data list and insert each row into the database
			    for (Map<String, Object> row : data) {
			        jdbcTemplate.update(sql,
			            row.get("category_id"),   // Category ID
			            row.get("campaign_id"),   // Campaign ID
			            row.get("ID"),            // ID (String)
			            row.get("Name"),          // Name (String)
			            row.get("Address"),       // Address (String)
			            row.get("Balance amount"),   // Dues if any (String)
			            row.get("Mobile number"),  // Mobile number (Long)
			            row.get("Tiny url")
			        );
			    }

			    // Return the inserted data (if needed)
			    return data; // Optionally return the inserted data or change the return type as per your use case
			}


		
		//For Assigned tasks
//		@Transactional
//		public List<Map<String, Object>> getAgentsALLCampaigns(int agent_id) {
//			    String SqlQuery = "SELECT \r\n"
//			    		+ "    c.category_name,\r\n"
//			    		+ "    cr.campaign_name,\r\n"
//			    		+ "    cr.calls_per_agent,\r\n"
//			    		+ "    aa.campaign_id,\r\n"
//			    		+ "    aa.agent_id,\r\n"
//			    		+ "    (SELECT COUNT(data_id) \r\n"
//			    		+ "     FROM gcc_1913_campaign.upload_data up \r\n"
//			    		+ "     WHERE up.agent_id = aa.agent_id \r\n"
//			    		+ "       AND up.campaign_id = cr.campaign_id\r\n"
//			    		+ "    ) AS calls_assigned,\r\n"
//			    		+ "    (SELECT COUNT(CASE WHEN up.field_14 IS NULL THEN 1 END) \r\n"
//			    		+ "     FROM gcc_1913_campaign.upload_data up \r\n"
//			    		+ "     WHERE up.agent_id = aa.agent_id \r\n"
//			    		+ "       AND up.campaign_id = cr.campaign_id\r\n"
//			    		+ "    ) AS pending_count,\r\n"
//			    		+ "    (SELECT COUNT(CASE WHEN up.field_14 IS NOT NULL THEN 1 END) \r\n"
//			    		+ "     FROM gcc_1913_campaign.upload_data up \r\n"
//			    		+ "     WHERE up.agent_id = aa.agent_id \r\n"
//			    		+ "       AND up.campaign_id = cr.campaign_id\r\n"
//			    		+ "    ) AS complete_count,\r\n"
//			    		+ "    (SELECT COUNT(CASE WHEN up.field_14 ='CALL_BACK_LATER' OR up.field_14 = 'UNATTENDED' THEN 1 END) \r\n"
//			    		+ "     FROM gcc_1913_campaign.upload_data up \r\n"
//			    		+ "     WHERE up.agent_id = aa.agent_id \r\n"
//			    		+ "       AND up.campaign_id = cr.campaign_id\r\n"
//			    		+ "    ) AS followup_count\r\n"
//			    		+ "    \r\n"
//			    		+ "FROM \r\n"
//			    		+ "    gcc_1913_campaign.agent_assign aa\r\n"
//			    		+ "JOIN \r\n"
//			    		+ "    gcc_1913_qaqc.agents_list al ON aa.agent_id = al.agent_id\r\n"
//			    		+ "JOIN \r\n"
//			    		+ "    gcc_1913_campaign.campaign_request cr ON cr.campaign_id = aa.campaign_id\r\n"
//			    		+ "JOIN \r\n"
//			    		+ "    gcc_1913_campaign.category c ON cr.category_id = c.category_id\r\n"
//			    		+ "WHERE \r\n"
//			    		+ "    aa.agent_id = ? \r\n"
//			    		+ "    AND aa.isactive = '1'\r\n"
//			    		+ "    AND cr.isactive = '0'";
//
//		    return jdbcTemplate.queryForList(SqlQuery,agent_id);
//		}
		
		 
		
		
		
		@Transactional
		public List<Map<String, Object>> getdataByIds(List<Integer> ids) {
		    // Construct the SQL query with placeholders for the number of IDs
		    String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
		    String SqlQuery = "SELECT data_id, field_1 as id, field_2 as name, field_3 as address, field_4 as dues, field_5 as cellnumber,field_6 as viewdetails,field_14 as call_status,field_15 as remarks " +
		                      "FROM gcc_1913_campaign.upload_data " +
		                      "WHERE data_id IN (" + inSql + ")";
		    
		    // Convert the list of ids to an Object array
		    return jdbcTemplate.queryForList(SqlQuery, ids.toArray());
		}
		
		@Transactional
		public void saveCallLogs(String call_status,String remarks ,int data_id,int agent_id)
		{
			String SqlQuery = "UPDATE gcc_1913_campaign.upload_data SET call_status = ?, remarks=?, updated_date = NOW() ,updated_agent_id=?, is_processed=1 WHERE (data_id = ? )";

		    jdbcTemplate.update(SqlQuery,call_status, remarks,agent_id,data_id);
		}
		
		@Transactional
		public int saveInLogsTable(int campaignId, int agentId, int dataId, String call_status,String remarks) {
		    String sqlQuery = "INSERT INTO gcc_1913_campaign.call_logs (campaign_id,agent_id,data_id,call_status,remarks) VALUES (?, ?, ?,?,?)";
		    int rowsAffected = 0;
		    try {
		        rowsAffected = jdbcTemplate.update(sqlQuery, campaignId, agentId, dataId,call_status,remarks);
		        //System.out.println("Inserted into call_logs successfully. Rows affected: " + rowsAffected);
		    } catch (Exception e) {
		        System.err.println("Error inserting into call_logs: " + e.getMessage());
		        // Log the exception or rethrow it based on your error-handling strategy
		    }
		    return rowsAffected;
		}
		
		@Transactional
		public List<Map<String, Object>> getPendingDataByIds(List<Integer> ids) {
		    // Construct the SQL query with placeholders for the number of IDs
		    String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
		    String SqlQuery = "SELECT data_id, field_1 as id, field_2 as name, field_3 as address, field_4 as dues, field_5 as cellnumber,field_6 as viewdetails,field_14 as call_status,field_15 as remarks " +
		                      "FROM gcc_1913_campaign.upload_data " +
		                      "WHERE data_id IN (" + inSql + ") " +
		                      "AND field_14 IN ('UNATTENDED', 'CALL_BACK_LATER')";
		    
		    // Convert the list of ids to an Object array
		    return jdbcTemplate.queryForList(SqlQuery, ids.toArray());
		}
		
		@Transactional
		public  List<Map<String, Object>> getAgentsNameById(List<Integer> agentIds)
		{
			String inSql = String.join(",", Collections.nCopies(agentIds.size(), "?"));
			String SqlQuery ="select agent_name,agent_id from gcc_1913_qaqc.agents_list where agent_id IN (" + inSql + ")";
			
			return jdbcTemplate.queryForList(SqlQuery, agentIds.toArray());
			
		}
		//agents performance
			@Transactional
			public List<Map<String, Object>> getDataDetailsById(List<Integer> dataIds)
			{
				if (dataIds.isEmpty()) {
				    // Handle the case where dataIds is empty to avoid SQL syntax error.
				    throw new IllegalArgumentException("The dataIds list is empty, cannot execute query.");
				}	
				
				String inSql = String.join(",", Collections.nCopies(dataIds.size(), "?"));
				String SqlQuery ="SELECT\r\n"
						+ "	COUNT(*) AS Assigned_count,\r\n"
						+ "    COUNT(CASE WHEN field_14 IS NOT NULL THEN 1 END) AS Complete_count,\r\n"
						+ "    COUNT(CASE WHEN field_14 IS NULL THEN 1 END) AS Pending_count ,\r\n"
						+ "    COUNT(CASE WHEN field_14 = 'INFORMATION_GIVEN' THEN 1  END) AS Information_Given_count,\r\n"
						+ "    COUNT(CASE WHEN field_14 = 'UNATTENDED' THEN 1  END) AS Unattended_count,\r\n"
						+ "    COUNT(CASE WHEN field_14 = 'CALL_BACK_LATER' THEN 1  END) AS Call_back_later_count,\r\n"
						+ "    COUNT(CASE WHEN field_14 = 'SWITCH_OFF' THEN 1  END) AS Switch_off_count,\r\n"
						+ "    COUNT(CASE WHEN field_14 = 'WRONG_NUMBER' THEN 1  END) AS Wrong_number_count,\r\n"
						+ "	   CASE WHEN COUNT(*) = 0 THEN 0 ELSE ROUND((COUNT(CASE WHEN field_14 IS NOT NULL THEN 1 END) * 100.0 / COUNT(*)), 0)END AS Percentage\r\n"
						+ "FROM gcc_1913_campaign.upload_data\r\n"
						+ "WHERE data_id IN (" + inSql + ")";
				
				
				
				 List<Map<String, Object>> results = jdbcTemplate.queryForList(SqlQuery, dataIds.toArray());
				    return results;
			}
		
			@Transactional
			public List<Map<String, Object>> getCompletedDataDetailsById(List<Integer> CdataIds)
			{
				if (CdataIds.isEmpty()) {
				    // Handle the case where dataIds is empty to avoid SQL syntax error.
				    throw new IllegalArgumentException("The CdataIds list is empty, cannot execute query.");
				}
				
				String inSql = String.join(",", Collections.nCopies(CdataIds.size(), "?"));
				String SqlQuery ="SELECT\r\n"
			            + "    data_id,\r\n"  // Add data_id to the result
			            + "    COUNT(*) AS Assigned_count,\r\n"
			            + "    COUNT(CASE WHEN field_14 IS NOT NULL THEN 1 END) AS Complete_count,\r\n"
			            + "    COUNT(CASE WHEN field_14 IS NULL THEN 1 END) AS Pending_count,\r\n"
			            + "    COUNT(CASE WHEN field_14 = 'INFORMATION_GIVEN' THEN 1 END) AS Information_Given_count,\r\n"
			            + "    COUNT(CASE WHEN field_14 = 'UNATTENDED' THEN 1 END) AS Unattended_count,\r\n"
			            + "    COUNT(CASE WHEN field_14 = 'CALL_BACK_LATER' THEN 1 END) AS Call_back_later_count,\r\n"
			            + "    COUNT(CASE WHEN field_14 = 'SWITCH_OFF' THEN 1 END) AS Switch_off_count,\r\n"
			            + "    COUNT(CASE WHEN field_14 = 'WRONG_NUMBER' THEN 1 END) AS Wrong_number_count,\r\n"
			            + "    CASE WHEN COUNT(*) = 0 THEN 0 ELSE ROUND((COUNT(CASE WHEN field_14 IS NOT NULL THEN 1 END) * 100.0 / COUNT(*)), 0) END AS Percentage\r\n"
			            + "FROM gcc_1913_campaign.upload_data\r\n"
			            + "WHERE data_id IN (" + inSql + ")\r\n"
			            + "GROUP BY data_id";  // Group by data_id
				
				return jdbcTemplate.queryForList(SqlQuery, CdataIds.toArray());
			}
		
			@Transactional
			public List<Map<String,Object>> getCompletedDataById(List<Integer> completedDataIds)
			{
				if (completedDataIds.isEmpty()) {
				    // Handle the case where dataIds is empty to avoid SQL syntax error.
				    throw new IllegalArgumentException("The completedDataIds list is empty, cannot execute query.");
				}
				
				String inSql = String.join(",", Collections.nCopies(completedDataIds.size(), "?"));
				String SqlQuery = "SELECT field_1 as id, field_2 as name, field_3 as address, " +
	                      "field_5 as cellnumber, field_14 as call_status, updated_date as updated_date,field_15 as remarks, " +
	                      "(SELECT COUNT(data_id) FROM call_logs WHERE data_id = upload_data.data_id) as call_count " +
	                      "FROM gcc_1913_campaign.upload_data " +
	                      "WHERE data_id IN (" + inSql + ")";
	    
			   
			    return jdbcTemplate.queryForList(SqlQuery, completedDataIds.toArray());
				
			}
			
			
			@Transactional
			public void addAgentsForCampaign(int selectedAgentId, int categoryId, int campaignId) {
			    String checkSql = "SELECT COUNT(*) FROM gcc_1913_campaign.agent_assign "
			                    + "WHERE agent_id = ? AND category_id = ? AND campaign_id = ?";

			    String insertSql = "INSERT INTO gcc_1913_campaign.agent_assign (agent_id, category_id, campaign_id) VALUES (?, ?, ?)";

			    try {
			        // Check if the record already exists
			        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, selectedAgentId, categoryId, campaignId);

			        if (count == 0) {
			            // Insert only if no existing record found
			            int rowsInserted = jdbcTemplate.update(insertSql, selectedAgentId, categoryId, campaignId);

			            if (rowsInserted > 0) {
			                System.out.println("Successfully inserted agent ID " + selectedAgentId + " into campaign ID " + campaignId);
			            } else {
			                System.out.println("No rows inserted for agent ID " + selectedAgentId);
			            }
			        } else {
			            System.out.println("Agent ID " + selectedAgentId + " is already assigned to campaign ID " + campaignId);
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

/*			            if (rowsUpdated > 0) {
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
			public List<Integer> getDataIdForFurtherAssign(int categoryId, int campaignId) {

			    String sql = "SELECT data_id FROM gcc_1913_campaign.upload_data "
			               + "WHERE category_id = ? "
			               + "AND campaign_id = ? "
			               + "AND is_processed = 0 "			               
			               + "ORDER BY data_id";

			    return jdbcTemplate.queryForList(sql, Integer.class, categoryId, campaignId);
			}
			
			@Transactional
			public void resetAgentAssignmentForNextDay(int categoryId, int campaignId) {
			    String sql = "UPDATE gcc_1913_campaign.upload_data "
			               + "SET agent_id = NULL, "
			               + "    call_status = NULL, "
			               + "    remarks = NULL, "
			               + "    updated_agent_id = NULL, "
			               + "    updated_date = NULL, "
			               + "    is_processed = 0 "
			               + "WHERE category_id = ? "
			               + "  AND campaign_id = ? "
			               + "  AND call_status IN ('CALL BACK LATER', 'UNATTENDED')";

			    jdbcTemplate.update(sql, categoryId, campaignId);
			    
			}

			
			
			//////////////////////Ajith code///////////////////
			
			@Transactional
			public List<Map<String, Object>> getFilteredFields(int categoryId, int agentId, int dataId) {
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
				        throw new RuntimeException("No matching columns found");
				    }

				    // Fetch ordered fields data
				    String query1 = "SELECT " + String.join(", ", fieldColumns) + " FROM gcc_1913_campaign.upload_data WHERE data_id = ?";
				    List<Map<String, Object>> orderedFieldsList = jdbcTemplate.queryForList(query1, dataId);

				    if (orderedFieldsList.isEmpty()) {
				        throw new RuntimeException("No data found for data_id: " + dataId);
				    }

				    Map<String, Object> orderedFields = orderedFieldsList.get(0);

				    // Fetch category columns
				    String query2 = "SELECT column_name FROM gcc_1913_campaign.category_columns WHERE category_id = ? and isactive=1 ";
				    List<String> categoryColumns = new ArrayList<>(jdbcTemplate.queryForList(query2, String.class, categoryId));
				    //System.out.println("service===="+categoryColumns);

				    if (categoryColumns.isEmpty()) {
				        throw new RuntimeException("No category columns found for category_id: " + categoryId);
				    }

				    int categoryCount = categoryColumns.size();
				    
				    // Collect first 'categoryCount' non-null values from orderedFields
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
				        throw new RuntimeException("Not enough non-null values to match category columns");
				    }

				    // Map category columns to non-null ordered field values
				    Map<String, Object> finalMapping = new HashMap<>();
				    for (int i = 0; i < categoryCount; i++) {
				        finalMapping.put(categoryColumns.get(i), nonNullValues.get(i));
				    }

				    resultList.add(finalMapping);
				    return resultList;
				}


    @Transactional
	 public List<Map<String, Object>> getCampaignQuestions(int categoryId, int campaignId) {
				    // Fetch questions along with answer type
//				    String questionQuery = "SELECT cq.*, at.type_name " +
//				                           "FROM gcc_1913_campaign.campaign_questions cq " +
//				                           "JOIN gcc_1913_campaign.answer_type at " +
//				                           "ON cq.answer_type_id = at.answer_type_id " +
//				                           
//				                           "WHERE cq.category_id = ? AND cq.campaign_id = ? ";
				    
				    String questionQuery="SELECT cq.questions AS questions, \r\n"
				    		+ "       MIN(cq.question_id) AS question_id, \r\n"
				    		+ "       MIN(cq.category_id) AS category_id, \r\n"
				    		+ "       MIN(cq.campaign_id) AS campaign_id, \r\n"
				    		+ "       MIN(cq.answer_type_id) AS answer_type_id, \r\n"
				    		+ "       MIN(cat.type_name) AS type_name \r\n"
				    		+ "FROM gcc_1913_campaign.campaign_questions cq \r\n"
				    		+ "JOIN gcc_1913_campaign.answer_type cat \r\n"
				    		+ "    ON cq.answer_type_id = cat.answer_type_id \r\n"
				    		+ "WHERE cq.category_id = ? \r\n"
				    		+ "AND cq.campaign_id = ? \r\n"
				    		+ "AND cq.isactive = 1\r\n"
				    		+ "GROUP BY cq.questions\r\n"
				    		+ "ORDER BY MIN(cq.question_id)";

				    List<Map<String, Object>> questions = jdbcTemplate.queryForList(questionQuery, categoryId, campaignId);

				    // Iterate over each question and fetch answers
				    for (Map<String, Object> question : questions) {
				        int questionId = (int) question.get("question_id");

//				        String answerQuery = "SELECT campaign_answer FROM gcc_1913_campaign.campaign_answers " +
//				                             "WHERE question_id = ?";
				        
				        String answerQuery="SELECT distinct(question_id) as qid, campaign_answer, created_date FROM gcc_1913_campaign.campaign_answers\r\n"
				        		+ "WHERE question_id = ?";

				        List<Map<String, Object>> answers = jdbcTemplate.queryForList(answerQuery, questionId);
/*
				        // Extract answer values and add to the question map
				        List<String> answerList = answers.stream()
				                                         .map(a -> (String) a.get("campaign_answer"))
				                                         .collect(Collectors.toList());
*/
				        List<String> answerList = new ArrayList<String>();

				        for (Map<String, Object> a : answers) {
				            Object ansObj = a.get("campaign_answer");
				            if (ansObj != null) {
				                answerList.add(ansObj.toString());
				            }
				        }
				        
				        question.put("answers", answerList);
				    }

				    return questions;
				}

   @Transactional
	public List<Map<String, Object>> FetchAnswer(int campaignId,int agentId,int questionId,int dataId) {
			        String sqlQuery = "SELECT distinct(campaign_answer) FROM gcc_1913_campaign.questions_answers where campaign_id=? and agent_id=? and question_id=? and data_id=?; ";
			        //System.out.println("dropdown l");
			        return jdbcTemplate.queryForList(sqlQuery,campaignId,agentId,questionId,dataId);
			    }

   @Transactional
	 public void saveAnswers(List<Map<String, Object>> answers) {
			        String sql = "INSERT INTO gcc_1913_campaign.questions_answers " +
			                     "(campaign_id, data_id, agent_id, question_id, campaign_answer, created_date) " +
			                     "VALUES (?, ?, ?, ?, ?, NOW())";

			        for (Map<String, Object> answer : answers) {
			            jdbcTemplate.update(sql,
			                answer.get("campaign_id"),
			                answer.get("data_id"),
			                answer.get("agent_id"),
			                answer.get("question_id"),
			                answer.get("campaign_answer")
			            );
			        }
			    }

   @Transactional
	public List<Map<String, Object>> getCallStatus() {
			        String sqlQuery = "SELECT * FROM gcc_1913_campaign.calls_status ";
			        //System.out.println("dropdown l");
			        return jdbcTemplate.queryForList(sqlQuery);
			    }

   @Transactional
	  public List<Map<String, Object>> checkCounts(int campaignId, int dataId) {
			        // Query to get the count of answered questions for a given dataId
			        String answersCountQuery = "SELECT  COUNT(distinct(question_id)) AS answersCount FROM gcc_1913_campaign.questions_answers WHERE data_id = ?";
			        
			        // Query to get the count of required questions for a given campaignId
			        String questionsCountQuery = "SELECT COUNT(distinct(questions)) AS questionsCount FROM gcc_1913_campaign.campaign_questions WHERE campaign_id = ?";

			        // Execute queries 
			        int answersCount = jdbcTemplate.queryForObject(answersCountQuery, Integer.class, dataId);
			        int questionsCount = jdbcTemplate.queryForObject(questionsCountQuery, Integer.class, campaignId);

			        // Construct response as a list of maps
			        return List.of(Map.of(
			            "answersCount", answersCount,
			            "questionsCount", questionsCount
			        ));
			    }
			
   @Transactional
	  public List<Map<String, Object>> getAgentsALLCampaignsList(int agent_id) {
		    String SqlQuery = "SELECT * FROM gcc_1913_campaign.upload_data ud inner join gcc_1913_campaign.campaign_request cr on ud.campaign_id=cr.campaign_id where cr.campaign_status='ONGOING'"+
		    		 " and ud.agent_id = ? and ud.is_processed=0 ";
		    //System.out.println("text");
		    return jdbcTemplate.queryForList(SqlQuery,agent_id);
		}
	 
   @Transactional
	 public List<Map<String, Object>> getAgentsALLCampaignsList1(int agent_id) {
		    String SqlQuery = "SELECT * FROM gcc_1913_campaign.upload_data ud inner join gcc_1913_campaign.campaign_request cr on ud.campaign_id=cr.campaign_id where cr.campaign_status='ONGOING'"+
		    		 " and ud.agent_id = ?  and ud.is_processed=1";
		   // System.out.println("text");
		    return jdbcTemplate.queryForList(SqlQuery,agent_id);
		}
	 
	 
	 
	 @Transactional
		public List<Map<String, Object>> getAgentsALLCampaigns(int agent_id) {
			    String SqlQuery = "SELECT c.category_name,cr.*,\r\n"
			    		+ "(SELECT COUNT(data_id)\r\n"
			    		+ "FROM gcc_1913_campaign.upload_data up\r\n"
			    		+ "WHERE up.agent_id = aa.agent_id \r\n"
			    		+ "AND up.campaign_id = cr.campaign_id\r\n"
			    		+ ") AS calls_assigned,\r\n"
			    		+ "(SELECT COUNT(CASE WHEN up.call_status IS NOT NULL THEN 1 END) \r\n"
			    		+ "FROM gcc_1913_campaign.upload_data up \r\n"
			    		+ "WHERE up.agent_id = aa.agent_id \r\n"
			    		+ "AND up.campaign_id = cr.campaign_id\r\n"
			    		+ ") AS complete_count,\r\n"
			    		+ "(SELECT COUNT(CASE WHEN up.call_status IS NULL THEN 1 END) \r\n"
			    		+ "FROM gcc_1913_campaign.upload_data up \r\n"
			    		+ "WHERE up.agent_id = aa.agent_id \r\n"
			    		+ "AND up.campaign_id = cr.campaign_id\r\n"
			    		+ ") AS pending_count\r\n"
			    		+ "FROM gcc_1913_campaign.agent_assign aa\r\n"
			    		+ "JOIN gcc_1913_campaign.campaign_request cr ON cr.campaign_id=aa.campaign_id\r\n"
			    		+ "JOIN gcc_1913_campaign.category c ON c.category_id=cr.category_id\r\n"
			    		+ "WHERE cr.campaign_status='ONGOING' AND aa.isactive=1 AND cr.isactive=1 AND aa.agent_id=?";

		    return jdbcTemplate.queryForList(SqlQuery,agent_id);
		}
	 
//	 public List<Map<String, Object>> getCallCategories() 
//	    {
//	    	String query = "SELECT id,call_status FROM gcc_1913_campaign.calls_status WHERE isactive=1 AND isdelete=0";
//	        return jdbcTemplate.queryForList(query);	    	
//	    }
	 

}
