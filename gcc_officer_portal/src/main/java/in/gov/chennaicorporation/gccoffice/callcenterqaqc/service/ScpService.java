package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ScpService {

	private JdbcTemplate jdbcTemplate;
	 
	 @Autowired
	 public void setDataSource(@Qualifier("mysqlScpDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	 
	 
	// Method to enrich report data with questions_part
	 public List<Map<String, Object>> enrichScpReportWithQuestions(List<Map<String, Object>> reportData) {

		    // Step 1: Fetch all questions
		    String questionSql = "SELECT id AS question_id, questions AS question, answer_type, isactive, isdelete FROM questions WHERE isactive=1 AND isdelete=0";
		    List<Map<String, Object>> questionList = jdbcTemplate.queryForList(questionSql);

		    // Step 2: Fetch all answers (master answers)
		    String answerSql = "SELECT question_id, answer FROM answers WHERE isactive=1 AND isdelete=0";
		    List<Map<String, Object>> answerList = jdbcTemplate.queryForList(answerSql);

		    // Convert answerList to Map<question_id, List<String>> for easy mapping
		    Map<Integer, List<String>> answerMasterMap = new HashMap<>();
		    for (Map<String, Object> ans : answerList) {
		        Integer qid = (Integer) ans.get("question_id");
		        String answerVal = (String) ans.get("answer");
		        answerMasterMap.computeIfAbsent(qid, k -> new ArrayList<>()).add(answerVal);
		    }
/*
		    // Step 3: Fetch all scp_report entries for these scp_ids
		    List<Long> scpIds = reportData.stream()
		            .map(d -> ((Number) d.get("id")).longValue())
		            .collect(Collectors.toList());

		 // Step 4: Get existing scp_ids from scp_report
		    Set<Long> existingScpIds = new HashSet<>();
		    if (!scpIds.isEmpty()) {
		        String inClause = scpIds.stream().map(id -> "?").collect(Collectors.joining(","));
		        String sql = "SELECT DISTINCT scp_id FROM scp_report WHERE isactive=1 AND isdelete=0 AND scp_id IN (" + inClause + ")";
		        List<Map<String, Object>> existingList = jdbcTemplate.queryForList(sql, scpIds.toArray());
		        for (Map<String, Object> row : existingList) {
		            existingScpIds.add(((Number) row.get("scp_id")).longValue());
		        }
		    }

		 // Step 5: Filter out already answered scp_ids
		    List<Map<String, Object>> filteredReportData = reportData.stream()
		            .filter(d -> !existingScpIds.contains(((Number) d.get("id")).longValue()))
		            .collect(Collectors.toList());
*/
		 // Step 3: Fetch all scp_report entries for these scp_ids
		    List<Long> scpIds = new ArrayList<Long>();
		    for (Map<String, Object> d : reportData) {
		        Object idObj = d.get("id");
		        if (idObj instanceof Number) {
		            scpIds.add(((Number) idObj).longValue());
		        }
		    }

		    // Step 4: Get existing scp_ids from scp_report
		    Set<Long> existingScpIds = new HashSet<Long>();
		    if (!scpIds.isEmpty()) {
		        // Build the IN clause placeholders
		        StringBuilder inClause = new StringBuilder();
		        for (int i = 0; i < scpIds.size(); i++) {
		            if (i > 0) inClause.append(",");
		            inClause.append("?");
		        }

		        String sql = "SELECT DISTINCT scp_id FROM scp_report WHERE isactive=1 AND isdelete=0 AND scp_id IN (" 
		                     + inClause.toString() + ")";
		        List<Map<String, Object>> existingList = jdbcTemplate.queryForList(sql, scpIds.toArray());
		        for (Map<String, Object> row : existingList) {
		            Object scpIdObj = row.get("scp_id");
		            if (scpIdObj instanceof Number) {
		                existingScpIds.add(((Number) scpIdObj).longValue());
		            }
		        }
		    }

		    // Step 5: Filter out already answered scp_ids
		    List<Map<String, Object>> filteredReportData = new ArrayList<Map<String, Object>>();
		    for (Map<String, Object> d : reportData) {
		        Object idObj = d.get("id");
		        if (idObj instanceof Number) {
		            Long id = ((Number) idObj).longValue();
		            if (!existingScpIds.contains(id)) {
		                filteredReportData.add(d);
		            }
		        }
		    }
		    
		    // Step 6: Attach questions only to the filtered list
		    for (Map<String, Object> report : filteredReportData) {
		        Long scpId = ((Number) report.get("id")).longValue();
		        List<Map<String, Object>> questionsPart = new ArrayList<>();

		        for (Map<String, Object> question : questionList) {
		            Integer qid = (Integer) question.get("question_id");

		            Map<String, Object> questionPart = new HashMap<>();
		            questionPart.put("question_id", qid);
		            questionPart.put("question", question.get("question"));
		            questionPart.put("answer_type", question.get("answer_type"));
		            questionPart.put("answer_master", answerMasterMap.getOrDefault(qid, Collections.emptyList()));

		            // Empty answers since these scp_ids don’t exist yet
		            Map<String, Object> answersMap = new HashMap<>();
		            answersMap.put("scp_id", scpId);
		            answersMap.put("question_id", qid);
		            answersMap.put("answer", null);
		            answersMap.put("agent_id", null);

		            questionPart.put("answers", answersMap);
		            questionsPart.add(questionPart);
		        }

		        report.put("questions_part", questionsPart);
		    }

		    return filteredReportData;
		}
	 
	 
//	 public boolean saveAnswers(List<Map<String, Object>> answers) {
//		    if (answers == null || answers.isEmpty()) return false;
//
//		    String sql = "INSERT INTO scp_report (scp_id, question_id, answer, agent_id, zone, ward, streetid, street_name, last_feedback_date) "
//		               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//		    int[][] result = jdbcTemplate.batchUpdate(sql, answers, answers.size(), (ps, answer) -> {
//
//		        // Convert safely to String or Integer
//		        String scpIdStr = getSafeString(answer.get("scp_id"));
//		        String questionIdStr = getSafeString(answer.get("question_id"));
//		        String answerStr = getSafeString(answer.get("answer"));
//		        String agentIdStr = getSafeString(answer.get("agent_id"));
//		        String zone = getSafeString(answer.get("zone"));
//		        String ward = getSafeString(answer.get("ward"));
//		        String streetId = getSafeString(answer.get("street_id"));
//		        String streetName = getSafeString(answer.get("street_name"));
//		        String lastFeedback = getSafeString(answer.get("lastfeedback"));
//
//		        // Parse integers only if not null
//		        Integer scpId = (scpIdStr != null) ? Integer.parseInt(scpIdStr) : null;
//		        Integer questionId = (questionIdStr != null) ? Integer.parseInt(questionIdStr) : null;
//		        Integer agentId = (agentIdStr != null) ? Integer.parseInt(agentIdStr) : null;
//
//		        // Set parameters
//		        ps.setObject(1, scpId);
//		        ps.setObject(2, questionId);
//		        ps.setObject(3, answerStr);
//		        ps.setObject(4, agentId);
//		        ps.setObject(5, zone);
//		        ps.setObject(6, ward);
//		        ps.setObject(7, streetId);
//		        ps.setObject(8, streetName);
//		        ps.setObject(9, lastFeedback);
//		    });
//
//		    return result.length > 0;
//		}
	 
	 public String saveAnswers(List<Map<String, Object>> answers) {
		    if (answers == null || answers.isEmpty()) return "error";
/*
		    // Step 1: Extract unique scp_ids from the list
		    List<Integer> scpIds = answers.stream()
		            .map(a -> {
		                String idStr = getSafeString(a.get("scp_id"));
		                return (idStr != null) ? Integer.parseInt(idStr) : null;
		            })
		            .filter(Objects::nonNull)
		            .distinct()
		            .toList();
*/
		 // Step 1: Extract unique scp_ids from the list
		    List<Integer> scpIds = new ArrayList<Integer>();
		    Set<Integer> uniqueIds = new HashSet<Integer>();

		    for (Map<String, Object> a : answers) {
		        Object scpIdObj = a.get("scp_id");
		        String idStr = getSafeString(scpIdObj);
		        if (idStr != null) {
		            try {
		                Integer id = Integer.parseInt(idStr);
		                if (!uniqueIds.contains(id)) {
		                    uniqueIds.add(id);
		                    scpIds.add(id);
		                }
		            } catch (NumberFormatException e) {
		                // Skip invalid numbers
		            }
		        }
		    }
		    
		    // Step 2: Check if any of these scp_ids already exist in DB
		    String checkSql = "SELECT COUNT(*) FROM scp_report WHERE scp_id IN (:ids)";
		    Map<String, Object> params = new HashMap<>();
		    params.put("ids", scpIds);

		    NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
		    Integer count = namedJdbc.queryForObject(checkSql, params, Integer.class);

		    if (count != null && count > 0) {
		        // Duplicate found — stop processing
		        return "duplicate";
		    }

		    // Step 3: Insert new answers
		    String insertSql = "INSERT INTO scp_report (scp_id, question_id, answer, agent_id, zone, ward, streetid, street_name, last_feedback_date) "
		                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		    int[][] result = jdbcTemplate.batchUpdate(insertSql, answers, answers.size(), (ps, answer) -> {
		        String scpIdStr = getSafeString(answer.get("scp_id"));
		        String questionIdStr = getSafeString(answer.get("question_id"));
		        String answerStr = getSafeString(answer.get("answer"));
		        String agentIdStr = getSafeString(answer.get("agent_id"));
		        String zone = getSafeString(answer.get("zone"));
		        String ward = getSafeString(answer.get("ward"));
		        String streetId = getSafeString(answer.get("street_id"));
		        String streetName = getSafeString(answer.get("street_name"));
		        String lastFeedback = getSafeString(answer.get("lastfeedback"));

		        Integer scpId = (scpIdStr != null) ? Integer.parseInt(scpIdStr) : null;
		        Integer questionId = (questionIdStr != null) ? Integer.parseInt(questionIdStr) : null;
		        Integer agentId = (agentIdStr != null) ? Integer.parseInt(agentIdStr) : null;

		        ps.setObject(1, scpId);
		        ps.setObject(2, questionId);
		        ps.setObject(3, answerStr);
		        ps.setObject(4, agentId);
		        ps.setObject(5, zone);
		        ps.setObject(6, ward);
		        ps.setObject(7, streetId);
		        ps.setObject(8, streetName);
		        ps.setObject(9, lastFeedback);
		    });

		    return (result.length > 0) ? "success" : "error";
		}


		// Helper method
		private String getSafeString(Object val) {
		    if (val == null) return null;
		    String s = val.toString().trim();
		    return s.isEmpty() ? null : s;
		}


		public List<Map<String, Object>> getZoneWiseReportData() {
	        String sql = "SELECT "
	        		+ "sr.zone,"
	        		+ " COALESCE(COUNT(DISTINCT sr.scp_id), 0) AS Completed,"
	        		+ " SUM(CASE WHEN sr.answer = 'Yes' THEN 1 ELSE 0 END) AS YesCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'No' THEN 1 ELSE 0 END) AS NoCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Available' THEN 1 ELSE 0 END) AS AvailableCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Not Available' THEN 1 ELSE 0 END) AS NotAvailableCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Image Not Clear' THEN 1 ELSE 0 END) AS ImageNotClearCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Yes,it is in good condition' THEN 1 ELSE 0 END) AS LidGoodYesCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'No,it is not in good condition' THEN 1 ELSE 0 END) AS LidGoodNoCount"
	        		+ " FROM scp_report sr"
	        		+ " WHERE sr.isactive = 1 AND sr.isdelete = 0"
	        		+ " GROUP BY sr.zone"
	        		+ " ORDER BY sr.zone";

	        return jdbcTemplate.queryForList(sql);
	    }
		
		public List<Map<String, Object>> getWardWiseReportData(String zone) {
	        String sql = "SELECT "
	        		+ "sr.zone,"
	        		+ "sr.ward,"
	        		+ " COALESCE(COUNT(DISTINCT sr.scp_id), 0) AS Completed,"
	        		+ " SUM(CASE WHEN sr.answer = 'Yes' THEN 1 ELSE 0 END) AS YesCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'No' THEN 1 ELSE 0 END) AS NoCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Available' THEN 1 ELSE 0 END) AS AvailableCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Not Available' THEN 1 ELSE 0 END) AS NotAvailableCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Image Not Clear' THEN 1 ELSE 0 END) AS ImageNotClearCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Yes,it is in good condition' THEN 1 ELSE 0 END) AS LidGoodYesCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'No,it is not in good condition' THEN 1 ELSE 0 END) AS LidGoodNoCount"
	        		+ " FROM scp_report sr"
	        		+ " WHERE sr.isactive = 1 AND sr.isdelete = 0 AND sr.zone= ?"
	        		+ " GROUP BY sr.zone,sr.ward"
	        		+ " ORDER BY sr.zone,sr.ward";

	        return jdbcTemplate.queryForList(sql,zone);
	    }
		
		public List<Map<String, Object>> getStreetWiseReportData(String zone,String ward) {
	        String sql = "SELECT "
	        		+ "sr.zone,"
	        		+ "sr.ward,"
	        		+ "sr.streetid,"
	        		+ " COALESCE(COUNT(DISTINCT sr.scp_id), 0) AS Completed,"
	        		+ " SUM(CASE WHEN sr.answer = 'Yes' THEN 1 ELSE 0 END) AS YesCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'No' THEN 1 ELSE 0 END) AS NoCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Available' THEN 1 ELSE 0 END) AS AvailableCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Not Available' THEN 1 ELSE 0 END) AS NotAvailableCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Image Not Clear' THEN 1 ELSE 0 END) AS ImageNotClearCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Yes,it is in good condition' THEN 1 ELSE 0 END) AS LidGoodYesCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'No,it is not in good condition' THEN 1 ELSE 0 END) AS LidGoodNoCount"
	        		+ " FROM scp_report sr"
	        		+ " WHERE sr.isactive = 1 AND sr.isdelete = 0 AND sr.zone=? AND sr.ward=?"
	        		+ " GROUP BY sr.streetid, sr.street_name,sr.zone,sr.ward"
	        		+ " ORDER BY sr.zone,sr.ward,sr.streetid";

	        return jdbcTemplate.queryForList(sql,zone,ward);
	    }


		public List<Map<String,Object>> getdetailreport(String startDate, String endDate) {
		
			
			String sql = "SELECT "
	        		+ "sr.zone,"
	        		+ "sr.ward,"
	        		+ "sr.street_name,"
	        		+ " COALESCE(COUNT(DISTINCT sr.scp_id), 0) AS Completed,"
	        		+ " SUM(CASE WHEN sr.answer = 'Yes' THEN 1 ELSE 0 END) AS YesCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'No' THEN 1 ELSE 0 END) AS NoCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Available' THEN 1 ELSE 0 END) AS AvailableCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Not Available' THEN 1 ELSE 0 END) AS NotAvailableCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Image Not Clear' THEN 1 ELSE 0 END) AS ImageNotClearCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'Yes,it is in good condition' THEN 1 ELSE 0 END) AS LidGoodYesCount,"
	        		+ " SUM(CASE WHEN sr.answer = 'No,it is not in good condition' THEN 1 ELSE 0 END) AS LidGoodNoCount"	        		
	        		+ " FROM scp_report sr"
	        		+ " WHERE sr.isactive = 1 AND sr.isdelete = 0 AND DATE(sr.created_date) BETWEEN ? AND ?"
	        		+ " GROUP BY sr.streetid, sr.street_name,sr.zone,sr.ward"
	        		+ " ORDER BY sr.zone,sr.ward,sr.streetid";

	        return jdbcTemplate.queryForList(sql,startDate,endDate);			
			
		}
		
		
	 
	
}
