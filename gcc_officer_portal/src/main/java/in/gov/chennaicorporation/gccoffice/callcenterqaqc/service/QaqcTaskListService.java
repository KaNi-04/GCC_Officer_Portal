package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.util.ArrayList;

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
public class QaqcTaskListService {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
// old overalltaskcount wise method
	
//	 public List<Map<String, Object>>  getComplaintAndCallStatusCounts(String startDate, String endDate) {
//	        String sql = "SELECT " +
//	            "t1.total_tasks, " +
//	            "t2.total_calls, " +
//	            "t3.completed_calls, " +
//	            "t2.total_calls - t3.completed_calls AS pending_calls " +
//	            "FROM " +
//	            "( " +
//	            "    SELECT " +
//	            "        COUNT(qaqc_id) AS total_tasks " +
//	            "    FROM " +
//	            "       qaqc_request " +
//	            "    WHERE " +
//	            "        (? IS NULL OR created_date BETWEEN ? AND ?) " +
//	            ") AS t1, " +
//	            "( " +
//	            "    SELECT " +
//	            "        SUM(total_data) AS total_calls " +
//	            "    FROM " +
//	            "       qaqc_request " +
//	            "    WHERE " +
//	            "        (? IS NULL OR created_date BETWEEN ? AND ?) " +
//	            ") AS t2, " +
//	            "( " +
//	            "    SELECT " +
//	            "        SUM(qr.total_data) AS total_data, " +
//	            "        COUNT(CASE WHEN qcl.qaqc_id IS NOT NULL THEN qcl.qaqc_id END) AS completed_calls " +
//	            "    FROM " +
//	            "        qaqc_request qr " +
//	            "    LEFT JOIN " +
//	            "       qaqc_call_logs qcl " +
//	            "    ON " +
//	            "        qr.qaqc_id = qcl.qaqc_id " +
//	            "    WHERE " +
//	            "        (? IS NULL OR qcl.created_date BETWEEN ? AND ?) " +
//	            ") AS t3;";
//
//	        // Prepare the parameters for the query
//	        Object[] params = new Object[] {
//	            startDate, startDate, endDate,
//	            startDate, startDate, endDate,
//	            startDate, startDate, endDate
//	        };
//
//	        // Execute the query and return the results as a List of Maps
//	        return jdbcTemplate.queryForList(sql, params);
//	    }
	 
	 
	 
	 //updated overalltaskcount method
     public List<Map<String, Object>> getComplaintAndCallStatusCounts(String startDate, String endDate) {
	    // Start constructing the SQL query dynamically
	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT ")
	       .append("t1.total_tasks, ")
	       .append("t2.total_calls, ")
	       .append("t3.completed_calls, ")
	       .append("t2.total_calls - t3.completed_calls AS pending_calls, ")
	       .append("t4.no_of_agents ")
	       .append("FROM ")
	       .append("( ")
	       .append("    SELECT ")
	       .append("        COUNT(qaqc_id) AS total_tasks ")
	       .append("    FROM ")
	       .append("        qaqc_request qr ");

	    // Adding dynamic WHERE conditions for date range if provided
	    if (startDate != null && endDate != null) {
	        sql.append("WHERE DATE(qr.created_date) >= ? AND DATE(qr.created_date) <= ? ");
	    }

	    sql.append(") AS t1, ")
	       .append("( ")
	       .append("    SELECT ")
	       .append("        SUM(aa.data_count) AS total_calls ")
	       .append("    FROM ")
	       .append("        agent_assigned aa ")
	       .append("     INNER JOIN ")
	       .append("        qaqc_request qr ")
	       .append("     ON ")
	       .append("        aa.qaqc_id = qr.qaqc_id ");

	    if (startDate != null && endDate != null) {
	        sql.append("WHERE DATE(qr.created_date) >= ? AND DATE(qr.created_date) <= ? ");
	    }

	    sql.append(") AS t2, ")
	       .append("( ")
	       .append("    SELECT ")
	       .append("        SUM(qr.total_data) AS total_data, ")
	       .append("        COUNT(CASE WHEN qcl.call_status IS NOT NULL THEN qcl.qaqc_id END) AS completed_calls ")
	       .append("    FROM ")
	       .append("       qaqc_request qr ")
	       .append("    LEFT JOIN ")
	       .append("        qaqc_upload_data_history qcl ")
	       .append("    ON ")
	       .append("        qr.qaqc_id = qcl.qaqc_id ");

	    if (startDate != null && endDate != null) {
	        sql.append("WHERE DATE(qcl.created_date) >= ? AND DATE(qcl.created_date) <= ? ");
	    }

	    sql.append(") AS t3, ")
	       .append("( ")
	       .append("    SELECT ")
	       .append("        COUNT(DISTINCT aa.agent_id) AS no_of_agents ")
	       .append("    FROM ")
	       .append("        agent_assigned aa ");

	    if (startDate != null && endDate != null) {
	        sql.append("WHERE DATE(aa.created_date) >= ? AND DATE(aa.created_date) <= ? ");
	    }

	    sql.append(") AS t4;");

	    // Prepare the parameters dynamically based on the presence of the date range
	    List<Object> params = new ArrayList<>();
	    if (startDate != null && endDate != null) {
	        params.add(startDate);
	        params.add(endDate);
	        params.add(startDate);
	        params.add(endDate);
	        params.add(startDate);
	        params.add(endDate);
	        params.add(startDate);
	        params.add(endDate);
	    }

	    // Execute the query and return the results as a List of Maps
	    return jdbcTemplate.queryForList(sql.toString(), params.toArray());
	}


	

	//old taskwise querymethod
	
//	public List<Map<String, Object>> getTaskwiseData(String startDate, String endDate) {
//	    // Base query
//	    StringBuilder query = new StringBuilder(
//	        "SELECT " +
//	        "    qr.taskid, " +
//	        "    MAX(qr.total_data) AS Assigned, " +
//	        "    MAX(qr.created_date) AS TaskCreatedDate, " +
//	        "    COUNT(CASE WHEN acl.qaqc_id IS NOT NULL THEN qr.qaqc_id END) AS TotalCompleted, " +
//	        "     MAX(qr.total_data) - COUNT(CASE WHEN acl.qaqc_id IS NOT NULL THEN qr.qaqc_id END) AS Pending, " + // Pending calculated as Assigned - TotalCompleted
//	        "    COUNT(CASE WHEN acl.call_status = 'COMPLETED' THEN 1 END) AS Completed, " +
//	        "    COUNT(CASE WHEN acl.call_status = 'REOPENED' THEN 1 END) AS Reopened, " +
//	        "    COUNT(CASE WHEN acl.call_status = 'FOLLOWUP' THEN 1 END) AS Followup " +
//	        "FROM qaqc_request qr " +
//	        "LEFT JOIN qaqc_call_logs acl " +
//	        "ON qr.qaqc_id = acl.qaqc_id "
//	    );
//
//	    // Adding dynamic WHERE conditions for date range
//	    if (startDate != null && endDate != null) {
//	        query.append("WHERE qr.created_date BETWEEN ? AND ? ");
//	    }
//
//	    // Grouping by taskid
//	    query.append("GROUP BY qr.taskid");
//
//	    // Query Execution
//	    if (startDate != null && endDate != null) {
//	        return jdbcTemplate.queryForList(query.toString(), startDate, endDate);
//	    } else {
//	        return jdbcTemplate.queryForList(query.toString());
//	    }
//	}

	 
	 //updated taskwisedata query method
	 
     public List<Map<String, Object>> getTaskwiseData(String startDate, String endDate) {
    	    // Base query with subqueries for Assigned and Pending calculations
    	    StringBuilder query = new StringBuilder(
    	        "SELECT " +
    	        "    qr.qaqc_id, " +
    	        "    qr.taskid, " +
    	        "    qr.call_category, " +
    	        "    qr.description, " +
    	        "    (" +
    	        "        SELECT SUM(aa.data_count) " +
    	        "        FROM agent_assigned aa " +
    	        "        WHERE aa.qaqc_id = qr.qaqc_id" +
    	        "    ) AS Assigned, " +
    	        "    MAX(DATE(qr.created_date)) AS TaskCreatedDate, " +
    	        "    COUNT(CASE WHEN acl.call_status IS NOT NULL THEN acl.qaqc_id END) AS TotalCompleted, " +
    	        "    (" +
    	        "        SELECT SUM(aa.data_count) " +
    	        "        FROM agent_assigned aa " +
    	        "        WHERE aa.qaqc_id = qr.qaqc_id" +
    	        "    ) - COUNT(CASE WHEN acl.call_status IS NOT NULL THEN acl.qaqc_id END) AS Pending, " +
    	        "    COUNT(CASE WHEN acl.call_status IN ('COMPLETED', 'FOLLOWUP_COMPLETED') THEN 1 END) AS Completed, " +
    	        "    COUNT(CASE WHEN acl.call_status = 'REOPEN' THEN 1 END) AS Reopened, " +
    	        "    COUNT(CASE WHEN acl.call_status = 'FOLLOWUP' THEN 1 END) AS Followup, " +
    	        "    COUNT(CASE WHEN acl.call_status NOT IN ('COMPLETED', 'FOLLOWUP_COMPLETED', 'REOPEN', 'FOLLOWUP') THEN 1 ELSE NULL END) AS Others " +
    	        "FROM qaqc_request qr " +
    	        "LEFT JOIN qaqc_upload_data_history acl " +
    	        "ON qr.qaqc_id = acl.qaqc_id "
    	    );

    	    // Adding dynamic WHERE conditions for date range if provided
    	    if (startDate != null && endDate != null) {
    	        query.append("WHERE DATE(qr.created_date) >= ? AND DATE(qr.created_date) <= ? ");
    	    }

    	    // Grouping by taskid and qaqc_id to calculate Assigned and Pending
    	    query.append("GROUP BY qr.taskid, qr.qaqc_id ");

    	    // Adding ORDER BY clause for descending order of created_date
    	    query.append("ORDER BY TaskCreatedDate DESC, qr.qaqc_id DESC");

    	    // Query execution with parameters for start and end dates if available
    	    if (startDate != null && endDate != null) {
    	        return jdbcTemplate.queryForList(query.toString(), startDate, endDate);
    	    } else {
    	        return jdbcTemplate.queryForList(query.toString());
    	    }
    	}



	

     public List<Map<String, Object>> getAgentwiseData(String qaqc_id) {
         
         String query= "SELECT DISTINCT \r\n"
         		+ "    al.agent_id,\r\n"
         		+ "    al.agent_name AS agentName,\r\n"
         		+ "    qr.taskid AS taskId,\r\n"
         		+ "    qr.fromdate AS fromdate,\r\n"
         		+ "    qr.todate AS todate,\r\n"
         		+ "    qr.description AS description,\r\n"
         		+ "    qr.call_category AS call_category,\r\n"
         		+ "    qr.complaint_region AS complaint_region,\r\n"
         		+ "    qr.complaint_group AS complaint_group,\r\n"
         		+ "    qr.complaint_type AS complaint_type,\r\n"
         		+ "    qr.complaint_mode AS complaint_mode,\r\n"
         		+ "    qr.complaint_zone AS complaint_zone,\r\n"
         		+ "    DATE(qr.created_date) AS TaskCreatedDate,\r\n"
         		+ "    aa.data_count AS assigned,\r\n"
         		+ "    COUNT(lcl.call_status) AS callstaken,\r\n"
         		+ "    (aa.data_count - COUNT(lcl.call_status)) AS pending,\r\n"
         		+ "    ROUND((COUNT(lcl.call_status) / aa.data_count) * 100, 2) AS percentage,\r\n"
         		+ "    COUNT(CASE WHEN lcl.call_status IN ('COMPLETED', 'FOLLOWUP_COMPLETED') THEN 1 END) AS Completed,\r\n"
         		+ "    COUNT(CASE WHEN lcl.call_status = 'REOPEN' THEN 1 END) AS Reopened,\r\n"
         		+ "    COUNT(CASE WHEN lcl.call_status = 'FOLLOWUP' THEN 1 END) AS Followup,\r\n"
         		+ "    COUNT(CASE WHEN lcl.call_status NOT IN ('COMPLETED', 'FOLLOWUP_COMPLETED', 'REOPEN', 'FOLLOWUP') THEN 1 END) AS Others\r\n"
         		+ "FROM qaqc_request qr\r\n"
         		+ "JOIN agent_assigned aa ON aa.qaqc_id = qr.qaqc_id\r\n"
         		+ "JOIN agents_list al ON al.agent_id = aa.agent_id\r\n"
         		+ "LEFT JOIN qaqc_upload_data_history lcl ON qr.qaqc_id = lcl.qaqc_id AND al.agent_id = lcl.agent_id \r\n"
         		+ "WHERE qr.qaqc_id = ?\r\n"
         		+ "GROUP BY al.agent_id, al.agent_name, aa.data_count";
     
      return jdbcTemplate.queryForList(query, qaqc_id);
  }

	
}