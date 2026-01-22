package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class QaqcAgentsService {
	
	private JdbcTemplate jdbcTemplate;

	@Autowired
	 public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	
		
	@Transactional
	public List<Map<String, Object>> getAgentsAllTasksCounts(int agent_id) {
		    String SqlQuery = "SELECT \r\n"		    		
		    		+ "    cr.taskid,\r\n"	
		    		+ "    cr.call_category,\r\n"
		    		+ "    cr.description,\r\n"
		    		+ "    aa.qaqc_id,\r\n"
		    		+ "    aa.agent_id,\r\n"
		    		+ "    aa.created_date,\r\n"
		    		+ "    (SELECT COUNT(data_id) \r\n"
		    		+ "     FROM qaqc_upload_data up \r\n"
		    		+ "     WHERE up.agent_id = aa.agent_id \r\n"
		    		+ "       AND up.qaqc_id = cr.qaqc_id\r\n"
		    		+ "    ) AS calls_assigned,\r\n"
		    		+ "    (SELECT COUNT(CASE WHEN up.call_status IS NULL THEN 1 END) \r\n"
		    		+ "     FROM qaqc_upload_data up \r\n"
		    		+ "     WHERE up.agent_id = aa.agent_id \r\n"
		    		+ "       AND up.qaqc_id = cr.qaqc_id\r\n"
		    		+ "    ) AS pending_count,\r\n"
		    		+ "    (SELECT COUNT(CASE WHEN up.call_status IS NOT NULL THEN 1 END) \r\n"
		    		+ "     FROM qaqc_upload_data up \r\n"
		    		+ "     WHERE up.agent_id = aa.agent_id \r\n"
		    		+ "       AND up.qaqc_id = cr.qaqc_id\r\n"
		    		+ "    ) AS complete_count,\r\n"
		    		+ "    (SELECT COUNT(CASE WHEN up.call_status ='CALL_BACK_LATER' OR up.field_9 = 'UNATTENDED' THEN 1 END) \r\n"
		    		+ "     FROM qaqc_upload_data up \r\n"
		    		+ "     WHERE up.agent_id = aa.agent_id \r\n"
		    		+ "       AND up.qaqc_id = cr.qaqc_id\r\n"
		    		+ "    ) AS followup_count\r\n"
		    		+ "    \r\n"
		    		+ "FROM \r\n"
		    		+ "    agent_assigned aa\r\n"
		    		+ "JOIN \r\n"
		    		+ "    agents_list al ON aa.agent_id = al.agent_id\r\n"
		    		+ "JOIN \r\n"
		    		+ "    qaqc_request cr ON cr.qaqc_id = aa.qaqc_id\r\n"
		    		+ "WHERE \r\n"
		    		+ "    aa.agent_id = ? \r\n"
		    		+ "    AND aa.isactive = '1'";

	    return jdbcTemplate.queryForList(SqlQuery,agent_id);
	}
	
	public List<Map<String, Object>> getdataforagents(int agent_id,int qaqc_id) {
        String sqlQuery = "select *\r\n"
        		+ "from qaqc_upload_data\r\n"
        		+ "where agent_id=? and qaqc_id=?";
        
        return jdbcTemplate.queryForList(sqlQuery,agent_id,qaqc_id);
    }
	
	@Transactional
	public void savecallstatus(String call_status, String remarks,String complaintNumber)
	{
		
		
		String SqlQuery = "UPDATE qaqc_upload_data SET call_status = ?,remarks=?, updated_date = NOW() WHERE (complaint_number = ? )";

	    jdbcTemplate.update(SqlQuery,call_status, remarks,complaintNumber);
	}
	
	@Transactional
	public void savecallstatusinlogs(String call_status, String remarks,String complaintNumber) {
	    
		// Step 1: Fetch details from the source table using the complaintNumber
        String selectQuery = "SELECT * FROM qaqc_upload_data WHERE complaint_number = ?";
        Map<String, Object> details = jdbcTemplate.queryForMap(selectQuery, complaintNumber);

        // Extract the required fields from the result (modify based on your table structure)
        String detailField1 = (String) details.get("qaqc_id");
        String detailField2 = (String) details.get("agent_id"); 
        String detailField3 = (String) details.get("complaint_number");
        String detailField4 = (String) details.get("complaint_person_name");
        String detailField5 = (String) details.get("complaint_mobilenumber");
        String detailField6 = (String) details.get("complaint_type");
        String detailField7 = (String) details.get("complaint_mode");
        String detailField8 = (String) details.get("department");
        String detailField9 = (String) details.get("complaint_group");
        String detailField10 = (String) details.get("official_name");
        String detailField11 = (String) details.get("official_mobilenum");

        // Step 2: Insert the fetched details along with call_status and remarks into qaqc_call_logs
        String insertQuery = "INSERT INTO qaqc_call_logs (qaqc_id, agent_id, complaint_number, complaint_person_name, complaint_mobilenumber,complaint_type,complaint_mode,department,complaint_group,official_name,official_mobilenum) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(insertQuery,detailField1, detailField2,detailField3,detailField4,detailField5,detailField6,detailField7,detailField8,detailField9,detailField10,detailField11,call_status,remarks);
	    
	}
	
	
	public List<Map<String,Object>> getPreviousDayDashboardCounts()
	{
		String sqlQuery = "SELECT \r\n"
	            + "    COUNT(DISTINCT assigned_stats.agent_id) AS TotalAgents, \r\n"
	            + "    SUM(COALESCE(assigned_stats.Assigned, 0)) AS TotalAssigned, \r\n"
	            + "    SUM(COALESCE(completed_stats.TotalCompleted, 0)) AS TotalCompleted, \r\n"
	            + "    SUM(COALESCE(assigned_stats.Assigned, 0) - COALESCE(completed_stats.TotalCompleted, 0)) AS TotalPending \r\n"
	            + "FROM \r\n"
	            + "    (SELECT al.agent_id AS agent_id, SUM(aa.data_count) AS Assigned \r\n"
	            + "     FROM agent_assigned aa \r\n"
	            + "     JOIN agents_list al ON aa.agent_id = al.agent_id \r\n"
	            + "     WHERE DATE(aa.created_date) = DATE(SUBDATE(CURDATE(), 1)) \r\n"
	            + "     GROUP BY al.agent_id) AS assigned_stats \r\n"
	            + "LEFT JOIN \r\n"
	            + "    (SELECT agent_id, COUNT(DISTINCT (complaint_number)) AS TotalCompleted \r\n"
	            + "     FROM qaqc_call_logs \r\n"
	            + "     WHERE DATE(created_date) = DATE(SUBDATE(CURDATE(), 1)) \r\n"
	            + "     GROUP BY agent_id) AS completed_stats \r\n"
	            + "ON assigned_stats.agent_id = completed_stats.agent_id;";
				
        
        return jdbcTemplate.queryForList(sqlQuery);
	}
	
//	public List<Map<String,Object>> getAgentPreviousDayCount()
//	{
//		String sqlQuery = "SELECT DISTINCT \r\n"
//				+ "    assigned_stats.agent_id, \r\n"
//				+ "    assigned_stats.agent_name as agent_name,  -- Use the alias assigned_stats here instead of aa\r\n"
//				+ "    COALESCE(assigned_stats.Assigned, 0) AS Assigned, \r\n"
//				+ "    COALESCE(completed_stats.TotalCompleted, 0) AS TotalCompleted, \r\n"
//				+ "    COALESCE(assigned_stats.Assigned, 0) - COALESCE(completed_stats.TotalCompleted, 0) AS Pending, \r\n"
//				+ "    CASE \r\n"
//				+ "        WHEN COALESCE(assigned_stats.Assigned, 0) = 0 THEN 0 \r\n"
//				+ "        ELSE ROUND(COALESCE(completed_stats.TotalCompleted, 0) * 100.0 / COALESCE(assigned_stats.Assigned, 1)) \r\n"
//				+ "    END AS CompletionPercentage, \r\n"
//				+ "    COALESCE(call_stats.Completed_count, 0) AS Completed_count, \r\n"
//				+ "    COALESCE(call_stats.Reopened_count, 0) AS Reopened_count, \r\n"
//				+ "    COALESCE(call_stats.Followup_count, 0) AS Followup_count, \r\n"
//				+ "    COALESCE(call_stats.Other_count, 0) AS Other_count \r\n"
//				+ "FROM \r\n"
//				+ "    (SELECT al.agent_id AS agent_id, al.agent_name AS agent_name, SUM(aa.data_count) AS Assigned \r\n"
//				+ "     FROM agent_assigned aa \r\n"
//				+ "     JOIN agents_list al ON aa.agent_id = al.agent_id \r\n"
//				+ "     WHERE DATE(aa.created_date) = DATE(SUBDATE(CURDATE(), 1)) \r\n"
//				+ "     GROUP BY al.agent_id,al.agent_name) AS assigned_stats  -- The alias 'assigned_stats' is used here\r\n"
//				+ "LEFT JOIN \r\n"
//				+ "    (SELECT agent_id, COUNT(qaqc_id) AS TotalCompleted \r\n"
//				+ "     FROM qaqc_call_logs \r\n"
//				+ "     WHERE DATE(created_date) = DATE(SUBDATE(CURDATE(), 1)) \r\n"
//				+ "     GROUP BY agent_id) AS completed_stats ON assigned_stats.agent_id = completed_stats.agent_id \r\n"
//				+ "LEFT JOIN \r\n"
//				+ "    (SELECT agent_id, \r\n"
//				+ "        SUM(CASE WHEN call_status IN ('COMPLETED', 'FOLLOWUP_COMPLETED') THEN 1 ELSE 0 END) AS Completed_count, \r\n"
//				+ "        SUM(CASE WHEN call_status = 'REOPEN' THEN 1 ELSE 0 END) AS Reopened_count, \r\n"
//				+ "        SUM(CASE WHEN call_status = 'FOLLOWUP' THEN 1 ELSE 0 END) AS Followup_count, \r\n"
//				+ "        SUM(CASE WHEN call_status NOT IN ('COMPLETED', 'FOLLOWUP_COMPLETED', 'REOPEN', 'FOLLOWUP') THEN 1 ELSE 0 END) AS Other_count \r\n"
//				+ "     FROM qaqc_call_logs \r\n"
//				+ "     WHERE DATE(created_date) = DATE(SUBDATE(CURDATE(), 1)) \r\n"
//				+ "     GROUP BY agent_id) AS call_stats ON assigned_stats.agent_id = call_stats.agent_id;\r\n";
//        
//        return jdbcTemplate.queryForList(sqlQuery);
//	}
	
	
	public List<Map<String,Object>> getAgentPreviousDayCount()
	{
		String sqlQuery = "SELECT DISTINCT \r\n"
				+ "    assigned_stats.agent_id, \r\n"
				+ "    assigned_stats.agent_name as agent_name,  -- Use the alias assigned_stats here instead of aa\r\n"
				+ "    COALESCE(assigned_stats.Assigned, 0) AS Assigned, \r\n"
				+ "    COALESCE(completed_stats.TotalCompleted, 0) AS TotalCompleted, \r\n"
				+ "    COALESCE(assigned_stats.Assigned, 0) - COALESCE(completed_stats.TotalCompleted, 0) AS Pending, \r\n"
				+ "    CASE \r\n"
				+ "        WHEN COALESCE(assigned_stats.Assigned, 0) = 0 THEN 0 \r\n"
				+ "        ELSE ROUND(COALESCE(completed_stats.TotalCompleted, 0) * 100.0 / COALESCE(assigned_stats.Assigned, 1)) \r\n"
				+ "    END AS CompletionPercentage, \r\n"
				+ "    COALESCE(call_stats.Completed_count, 0) AS Completed_count, \r\n"
				+ "    COALESCE(call_stats.Reopened_count, 0) AS Reopened_count, \r\n"
				+ "    COALESCE(call_stats.Followup_count, 0) AS Followup_count, \r\n"
				+ "    COALESCE(call_stats.Other_count, 0) AS Other_count \r\n"
				+ "FROM \r\n"
				+ "    (SELECT al.agent_id AS agent_id, al.agent_name AS agent_name, SUM(aa.data_count) AS Assigned \r\n"
				+ "     FROM agent_assigned aa \r\n"
				+ "     JOIN agents_list al ON aa.agent_id = al.agent_id \r\n"
				+ "     WHERE DATE(aa.created_date) = DATE(SUBDATE(CURDATE(), 1)) \r\n"
				+ "     GROUP BY al.agent_id,al.agent_name) AS assigned_stats  -- The alias 'assigned_stats' is used here\r\n"
				+ "LEFT JOIN \r\n"
				+ "    (SELECT agent_id, count(DISTINCT (complaint_number)) AS TotalCompleted \r\n"
				+ "     FROM qaqc_call_logs \r\n"
				+ "     WHERE DATE(created_date) = DATE(SUBDATE(CURDATE(), 1)) \r\n"
				+ "     GROUP BY agent_id) AS completed_stats ON assigned_stats.agent_id = completed_stats.agent_id \r\n"
				+ "LEFT JOIN \r\n"
				+ "    (SELECT agent_id, \r\n"
				+ "        SUM(CASE WHEN call_status IN ('COMPLETED', 'FOLLOWUP_COMPLETED') THEN 1 ELSE 0 END) AS Completed_count, \r\n"
				+ "        SUM(CASE WHEN call_status = 'REOPEN' THEN 1 ELSE 0 END) AS Reopened_count, \r\n"
				+ "        SUM(CASE WHEN call_status = 'FOLLOWUP' THEN 1 ELSE 0 END) AS Followup_count, \r\n"
				+ "        SUM(CASE WHEN call_status NOT IN ('COMPLETED', 'FOLLOWUP_COMPLETED', 'REOPEN', 'FOLLOWUP') THEN 1 ELSE 0 END) AS Other_count \r\n"
				+ "     FROM qaqc_call_logs \r\n"
				+ "     WHERE DATE(created_date) = DATE(SUBDATE(CURDATE(), 1)) \r\n"
				+ "     GROUP BY agent_id) AS call_stats ON assigned_stats.agent_id = call_stats.agent_id \r\n"
				+ "     ORDER BY agent_name \r\n;";
        
        return jdbcTemplate.queryForList(sqlQuery);
	}
	

	public List<Map<String, Object>> getAgentDashboard(int userId, String startDate, String endDate) {
	    String sql = "SELECT " +
	                 "    COALESCE(SUM(aa.data_count), 0) AS Total_Calls_assigned, " +
	                 "    COALESCE(SUM(CASE WHEN qr.call_category = 'closed' THEN aa.data_count ELSE 0 END), 0) AS Closed_Call_count, " +
	                 "    COALESCE(SUM(CASE WHEN qr.call_category IN ('inprogress', 'redressed', 'reopened','processing') THEN aa.data_count ELSE 0 END), 0) AS Inprogress_Call_count, " +
	                 "    COALESCE(SUM(CASE WHEN qr.call_category = 'officialfollowup' THEN aa.data_count ELSE 0 END), 0) AS Followup_Call_count, " +
	                 "    COALESCE(SUM(CASE WHEN qr.call_category = 'publicfollowup' THEN aa.data_count ELSE 0 END), 0) AS PublicFollowup_Call_count " +
	                 "FROM agent_assigned aa " +
	                 "LEFT JOIN qaqc_request qr ON aa.qaqc_id = qr.qaqc_id " +
	                 "WHERE aa.agent_id = ? " +
	                 "AND DATE(qr.created_date) BETWEEN ? AND ?";
	    
	    return jdbcTemplate.queryForList(sql, userId, startDate, endDate);
	}

	
	public List<Map<String, Object>> getAgentPerformanceData(int agentId, String startDate, String endDate) {
	    String sql = "SELECT \n"
	            + "    COALESCE(total_data_count, 0) AS total_data_count,\n" // Ensure total_data_count is 0 if null
	            + "    COALESCE(completed_count, 0) AS completed_count,\n"  // Ensure completed_count is 0 if null
	            + "    COALESCE((total_data_count - completed_count), 0) AS pending_count,\n" // Ensure pending_count is 0
	            + "    CASE \n"
	            + "        WHEN COALESCE(total_data_count, 0) > 0 THEN ROUND((COALESCE(completed_count, 0) / total_data_count) * 100, 1)\n"
	            + "        ELSE 0\n"
	            + "    END AS completed_percentage\n"
	            + "FROM \n"
	            + "    (\n"
	            + "        SELECT \n"
	            + "            (SELECT COALESCE(SUM(aa.data_count), 0) \n"
	            + "             FROM agent_assigned aa \n"
	            + "             WHERE aa.agent_id = ? AND DATE(aa.created_date) BETWEEN ? AND ?) AS total_data_count,\n"
	            + "            \n"
	            + "            (SELECT COALESCE(COUNT(*), 0) \n"
	            + "             FROM qaqc_call_logs cl \n"
	            + "             WHERE cl.agent_id = ?\n"
	            + "             AND DATE(cl.created_date) BETWEEN ? AND ? \n"
	            + "             AND cl.call_status IS NOT NULL) AS completed_count\n"
	            + "    ) AS combined";

	    return jdbcTemplate.queryForList(sql, agentId, startDate, endDate, agentId, startDate, endDate);
	}


   
   
   public List<Map<String, Object>> getAgentTaskDetails(int userId, String startDate, String endDate) {
	   String sql = "WITH LatestCallLogs AS ( " +
			    "    SELECT " +
			    "        cl.qaqc_id, " +
			    "        cl.agent_id, " +
			    "        cl.complaint_number, " +
			    "        cl.call_status, " +
			    "        cl.created_date, " +
			    "        ROW_NUMBER() OVER (PARTITION BY cl.qaqc_id, cl.agent_id, cl.complaint_number ORDER BY cl.created_date DESC) AS row_num " +
			    "    FROM " +
			    "        qaqc_call_logs cl " +
			    ") " +
			    "SELECT DISTINCT " +
			    "    qr.taskid AS taskid, " +
			    "    qr.call_category AS call_category, " +
			    "    DATE(aa.created_date) AS cdate, " +
			    "    aa.data_count AS assigned, " +
			    "    COUNT(lcl.agent_id) AS callstaken, " +
			    "    (aa.data_count - COUNT(lcl.agent_id)) AS pending, " +
			    "    ROUND((CASE WHEN aa.data_count > 0 THEN (COUNT(lcl.agent_id) / aa.data_count) * 100 ELSE 0 END), 2) AS percentage, " +
			    "    COUNT(CASE WHEN lcl.call_status IN ('COMPLETED', 'FOLLOWUP_COMPLETED') THEN 1 END) AS Completed, " +
			    "    COUNT(CASE WHEN lcl.call_status = 'REOPEN' THEN 1 END) AS Reopened, " +
			    "    COUNT(CASE WHEN lcl.call_status = 'FOLLOWUP' THEN 1 END) AS Followup, " +
			    "    COUNT(CASE WHEN lcl.call_status NOT IN ('COMPLETED', 'FOLLOWUP_COMPLETED', 'REOPEN', 'FOLLOWUP') THEN 1 END) AS Others " +
			    "FROM " +
			    "    qaqc_request qr " +
			    "JOIN " +
			    "    agent_assigned aa ON aa.qaqc_id = qr.qaqc_id " +
			    "JOIN " +
			    "    agents_list al ON al.agent_id = aa.agent_id " +
			    "LEFT JOIN " +
			    "    LatestCallLogs lcl ON qr.qaqc_id = lcl.qaqc_id AND al.agent_id = lcl.agent_id AND lcl.row_num = 1 " +
			    "WHERE " +
			    "    al.agent_id = ? AND aa.data_count != 0 " +
			    "    AND DATE(aa.created_date) BETWEEN ? AND ? " +
			    "GROUP BY " +
			    "    taskid, cdate, assigned, call_category";

			return jdbcTemplate.queryForList(sql, userId, startDate, endDate);
	}
		
		public List<Map<String, Object>> getAgentPerformanceCount(String startDate, String endDate) {
		    String sql = "SELECT " +
		        "t2.total_calls, " +
		        "t3.completed_calls, " +
		        "t2.total_calls - t3.completed_calls AS pending_calls, " +
		        "t4.no_of_agents " +
		        "FROM " +		        
		        "( " +
		        "    SELECT " +
		        "        SUM(aa.data_count) AS total_calls " +
		        "    FROM " +
		        "        agent_assigned aa " +
		        "     INNER JOIN " +
		        "        qaqc_request qr " +
		        "     ON " +
		        "        aa.qaqc_id = qr.qaqc_id " +
		        "    WHERE " +
		        "        (? IS NULL OR date(qr.created_date) BETWEEN ? AND ?) " +
		        ") AS t2, " +
		        "( " +
		        "    SELECT " +
		        "        SUM(qr.total_data) AS total_data, " +
		        "    COUNT(CASE WHEN qcl.call_status IS NOT NULL THEN qcl.qaqc_id END) AS completed_calls" +
		        "    FROM " +
		        "       qaqc_request qr " +
		        "    LEFT JOIN " +
		        "        qaqc_upload_data_history qcl " +
		        "    ON " +
		        "        qr.qaqc_id = qcl.qaqc_id " +
		        "    WHERE " +
		        "        (? IS NULL OR date(qr.created_date) BETWEEN ? AND ?) " +
		        ") AS t3, " +
		        "( " +
		        "    SELECT " +
		        "        COUNT(DISTINCT aa.agent_id) AS no_of_agents " +
		        "    FROM " +
		        "        agent_assigned aa " +
		        "    WHERE " +
		        "        (? IS NULL OR date(aa.created_date) BETWEEN ? AND ?) " +
		        ") AS t4";

		    // Prepare the parameters for the query
		    Object[] params = new Object[] {
		        startDate, startDate, endDate, // For t2
		        startDate, startDate, endDate, // For t3
		        startDate, startDate, endDate  // For t4
		    };

		    // Execute the query and return the results as a List of Maps
		    return jdbcTemplate.queryForList(sql, params);
		}
	
		public List<Map<String, Object>> getAgentPerformanceList(String startDate, String endDate) {
		    // Base query with subqueries for Assigned, Completed, and Pending
		    StringBuilder query = new StringBuilder(
		        "SELECT DISTINCT " +
		        "    assigned_stats.agent_id, " + 
		        "    assigned_stats.agent_name, " +  
		        "    COALESCE(assigned_stats.Assigned, 0) AS Assigned, " +
		        "    COALESCE(completed_stats.TotalCompleted, 0) AS TotalCompleted, " +
		        "    COALESCE(assigned_stats.Assigned, 0) - COALESCE(completed_stats.TotalCompleted, 0) AS Pending, " +
		        "    CASE WHEN COALESCE(assigned_stats.Assigned, 0) = 0 THEN 0 ELSE " +
		        "        ROUND(COALESCE(completed_stats.TotalCompleted, 0) * 100.0 / COALESCE(assigned_stats.Assigned, 1), 2) " +
		        "    END AS CompletionPercentage, " +
		        "    COALESCE(call_stats.Completed_count, 0) AS Completed_count, " +
		        "    COALESCE(call_stats.Reopened_count, 0) AS Reopened_count, " +
		        "    COALESCE(call_stats.Followup_count, 0) AS Followup_count, " +
		        "    COALESCE(call_stats.Other_count, 0) AS Other_count " +
		        "FROM " +
		        "    (SELECT al.agent_id as agent_id, al.agent_name as agent_name,SUM(aa.data_count) AS Assigned " +
		        "     FROM agent_assigned aa JOIN agents_list al ON aa.agent_id=al.agent_id " +
		        "     WHERE (? IS NULL OR DATE(aa.created_date) >= STR_TO_DATE(?, '%Y-%m-%d')) " + // Conditional date filtering
		        "       AND (? IS NULL OR DATE(aa.created_date) <= STR_TO_DATE(?, '%Y-%m-%d')) " +
		        "     GROUP BY al.agent_id,al.agent_name) AS assigned_stats " +  // The alias 'assigned_stats' is used here
		        "LEFT JOIN " +
		        "    (SELECT agent_id, COUNT(call_status) AS TotalCompleted " +
		        "     FROM qaqc_upload_data_history " +
		        "     WHERE (? IS NULL OR DATE(created_date) >= STR_TO_DATE(?, '%Y-%m-%d')) " + // Conditional date filtering
		        "       AND (? IS NULL OR DATE(created_date) <= STR_TO_DATE(?, '%Y-%m-%d')) " +
		        "     GROUP BY agent_id) AS completed_stats ON assigned_stats.agent_id = completed_stats.agent_id " +
		        "LEFT JOIN " +
		        "    (SELECT agent_id, " +
		        "        SUM(CASE WHEN call_status IN ('COMPLETED', 'FOLLOWUP_COMPLETED') THEN 1 ELSE 0 END) AS Completed_count, " +
		        "        SUM(CASE WHEN call_status = 'REOPEN' THEN 1 ELSE 0 END) AS Reopened_count, " +
		        "        SUM(CASE WHEN call_status = 'FOLLOWUP' THEN 1 ELSE 0 END) AS Followup_count, " +
		        "        SUM(CASE WHEN call_status NOT IN ('COMPLETED', 'FOLLOWUP_COMPLETED', 'REOPEN', 'FOLLOWUP') THEN 1 ELSE 0 END) AS Other_count " +
		        "     FROM qaqc_upload_data_history " +
		        "     WHERE (? IS NULL OR DATE(created_date) >= STR_TO_DATE(?, '%Y-%m-%d')) " + // Conditional date filtering
		        "       AND (? IS NULL OR DATE(created_date) <= STR_TO_DATE(?, '%Y-%m-%d')) " +
		        "     GROUP BY agent_id) AS call_stats ON assigned_stats.agent_id = call_stats.agent_id"
		    );

		    // Execute query with date range parameters
		    return jdbcTemplate.queryForList(
		        query.toString(),
		        startDate, startDate, endDate, endDate, // Parameters for assigned_stats
		        startDate, startDate, endDate, endDate, // Parameters for completed_stats
		        startDate, startDate, endDate, endDate  // Parameters for call_stats
		    );
		}
		



		public List<Map<String, Object>> getAgentTaskwiseData(String agentId,String startDate, String endDate) {
		  
		      String query="WITH LatestCallLogs AS (\r\n"
		      		+ "    SELECT \r\n"
		      		+ "        cl.qaqc_id,\r\n"
		      		+ "        cl.agent_id,\r\n"
		      		+ "        cl.complaint_number,\r\n"
		      		+ "        cl.call_status,\r\n"
		      		+ "        cl.created_date,\r\n"
		      		+ "        ROW_NUMBER() OVER (PARTITION BY cl.qaqc_id, cl.agent_id, cl.complaint_number ORDER BY cl.created_date DESC) AS row_num\r\n"
		      		+ "    FROM \r\n"
		      		+ "        qaqc_call_logs cl\r\n"
		      		+ ")\r\n"
		      		+ "SELECT DISTINCT \r\n"
		      		+ "    qr.taskid as taskid,\r\n"
		      		+ "    date(aa.created_date) as cdate,\r\n"
		      		+ "    aa.data_count AS assigned,\r\n"
		      		+ "    COUNT(lcl.agent_id) AS callstaken,\r\n"
		      		+ "    (aa.data_count - COUNT(lcl.agent_id)) AS pending,\r\n"
		      		+ "    ROUND((COUNT(lcl.agent_id) / aa.data_count) * 100, 2) AS percentage,\r\n"
		      		+ "    COUNT(CASE WHEN lcl.call_status IN ('COMPLETED', 'FOLLOWUP_COMPLETED') THEN 1 END) AS Completed,\r\n"
		      		+ "    COUNT(CASE WHEN lcl.call_status = 'REOPEN' THEN 1 END) AS Reopened,\r\n"
		      		+ "    COUNT(CASE WHEN lcl.call_status = 'FOLLOWUP' THEN 1 END) AS Followup,\r\n"
		      		+ "    COUNT(CASE WHEN lcl.call_status NOT IN ('COMPLETED', 'FOLLOWUP_COMPLETED', 'REOPEN', 'FOLLOWUP') THEN 1 END) AS Others\r\n"
		      		+ "FROM \r\n"
		      		+ "    qaqc_request qr\r\n"
		      		+ "JOIN \r\n"
		      		+ "    agent_assigned aa ON aa.qaqc_id = qr.qaqc_id\r\n"
		      		+ "JOIN \r\n"
		      		+ "    agents_list al ON al.agent_id = aa.agent_id\r\n"
		      		+ "LEFT JOIN \r\n"
		      		+ "    LatestCallLogs lcl ON qr.qaqc_id = lcl.qaqc_id AND al.agent_id = lcl.agent_id AND lcl.row_num = 1\r\n"
		      		+ "WHERE \r\n"
		      		+ "    al.agent_id = ? AND DATE(qr.created_date) >= ? AND DATE(qr.created_date) <= ? \r\n"
		      		+ "GROUP BY \r\n"
		      		+ "    taskid, cdate, assigned";  

		   
		        return jdbcTemplate.queryForList(query, agentId,startDate,endDate);
		    
		}
		
		
		public List<Map<String, Object>> getAttendedComplaintStatus(
		        String startDate, String endDate, String complaintNumber, String complaintMobileNumber) {

		    // Base query with JOIN to include agent_name
		    StringBuilder query = new StringBuilder(
		        "SELECT qcl.*,date(qcl.created_date) as updated_date, al.agent_name " +
		        "FROM qaqc_call_logs qcl " +
		        "LEFT JOIN agents_list al ON qcl.agent_id = al.agent_id " +
		        "WHERE 1=1 ");

		    // List to store query parameters
		    List<Object> params = new ArrayList<>();

		    // Add date range condition if provided
		    if (startDate != null && !startDate.isEmpty()) {
		        query.append("AND DATE(qcl.created_date) >= ? ");
		        params.add(startDate);
		    }
		    if (endDate != null && !endDate.isEmpty()) {
		        query.append("AND DATE(qcl.created_date) <= ? ");
		        params.add(endDate);
		    }

		    // Add complaint number condition if provided
		    if (complaintNumber != null && !complaintNumber.isEmpty()) {
		        query.append("AND qcl.complaint_number = ? ");
		        params.add(complaintNumber);
		    }

		    // Add complaint mobile number condition if provided
		    if (complaintMobileNumber != null && !complaintMobileNumber.isEmpty()) {
		        query.append("AND qcl.complaint_mobilenumber = ? ");
		        params.add(complaintMobileNumber);
		    }

		    // Add ORDER BY clause
		    query.append("ORDER BY qcl.created_date DESC ");

		    // Add LIMIT clause only if complaintNumber is provided
		    if (complaintNumber != null && !complaintNumber.isEmpty()) {
		        query.append("LIMIT 1 ");
		    }

		    // Execute the query with dynamic parameters
		    return jdbcTemplate.queryForList(query.toString(), params.toArray());
		}
		
		
		public List<Map<String, Object>> getAttendedComplainthistory(
		        String startDate, String endDate, String complaintNumber, String complaintMobileNumber) {

		    // Base query with JOIN to include agent_name
		    StringBuilder query = new StringBuilder(
		        "SELECT qcl.*, al.agent_name " +
		        "FROM qaqc_call_logs qcl " +
		        "LEFT JOIN agents_list al ON qcl.agent_id = al.agent_id " +
		        "WHERE 1=1 ");

		    // List to store query parameters
		    List<Object> params = new ArrayList<>();

		    // Add date range condition if provided
		    if (startDate != null && !startDate.isEmpty()) {
		        query.append("AND DATE(qcl.created_date) >= ? ");
		        params.add(startDate);
		    }
		    if (endDate != null && !endDate.isEmpty()) {
		        query.append("AND DATE(qcl.created_date) <= ? ");
		        params.add(endDate);
		    }

		    // Add complaint number condition if provided
		    if (complaintNumber != null && !complaintNumber.isEmpty()) {
		        query.append("AND qcl.complaint_number = ? ");
		        params.add(complaintNumber);
		    }

		    // Add complaint mobile number condition if provided
		    if (complaintMobileNumber != null && !complaintMobileNumber.isEmpty()) {
		        query.append("AND qcl.complaint_mobilenumber = ? ");
		        params.add(complaintMobileNumber);
		    }

		    // Group by qcl.id and agent_name to avoid SQL error
		    query.append("GROUP BY qcl.id, al.agent_name ");

		    // Add ordering clause
		    query.append("ORDER BY qcl.created_date DESC");

		    // Execute the query with dynamic parameters
		    return jdbcTemplate.queryForList(query.toString(), params.toArray());
		}
		
		public List<Map<String, Object>> getAgentTaskwiseAssign(String agentId) {
			String query="SELECT * FROM qaqc_upload_data_history where agent_id=? ";  
			return jdbcTemplate.queryForList(query, agentId);
			}

		public List<Map<String, Object>> getAgentName(int userId) {
			String query="SELECT agent_name \r\n"
					+ "FROM agents_list\r\n"
					+ "where agent_id=?";
			return jdbcTemplate.queryForList(query, userId);
		}

}
