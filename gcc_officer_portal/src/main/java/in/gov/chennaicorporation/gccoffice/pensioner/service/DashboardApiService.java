package in.gov.chennaicorporation.gccoffice.pensioner.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class DashboardApiService {
	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 
	    @Autowired
		 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
			 this.jdbcTemplate = new JdbcTemplate(dataSource);
		 }
	    
	    @Autowired
	    public GetFileStatusService getFileStatus;
	 
	 public List<Map<String, Object>> getTempEmpDetails(String tempId) {
		  System.out.println("service");
		  System.out.println(tempId);
		 
	        String sql = "SELECT MH.file_moved_date,sm.file_status,MH.remarks FROM file_movement_history MH"
	        		+ " inner join file_status_master sm on MH.file_status= sm.id where temp_id=?   order by MH.file_moved_date desc ";

	        return jdbcTemplate.queryForList(sql, tempId);
	    }
	        
	        
	 
	 public List<Map<String, Object>> getEmpDetails(String empNo) {
		  System.out.println("service");
		  System.out.println(empNo);
		 
	        String sql = "SELECT dm.dept_name,fm.file_name, sm.file_status ,pd.temp_id " +
	                     "FROM pensioner_details pd " +
	                     "INNER JOIN file_category_master fm ON pd.file_category = fm.id " +
	                     "INNER JOIN file_status_master sm ON sm.id = pd.file_status " +
	                     "inner join department_master dm on dm.id=pd.dept_id "+
	                    
	                     "WHERE pd.emp_no = ? " +
	                     
	                     "UNION " +
	                     "SELECT dm.dept_name,fm.file_name, sm.file_status ,cps.temp_id " +
	                     "FROM  cps_pensioner_details cps " +
	                     "INNER JOIN file_category_master fm ON cps.file_category = fm.id " +
	                     "INNER JOIN file_status_master sm ON sm.id = cps.file_status " +
	                     "inner join department_master dm on dm.id=cps.dept_id "+
	                     
	                     "WHERE cps.emp_no = ?";

	        return jdbcTemplate.queryForList(sql, empNo, empNo);
	    }
	 
	 public List<Map<String, Object>> getPensionEmpDetails(String empNo) {
		  System.out.println("service");
		  System.out.println(empNo);
		 
	        String sql = "SELECT dm.dept_name,fm.file_name, sm.file_status ,pd.temp_id " +
	                     "FROM pensioner_details pd " +
	                     "INNER JOIN file_category_master fm ON pd.file_category = fm.id " +
	                     "INNER JOIN file_status_master sm ON sm.id = pd.file_status " +
	                     "inner join department_master dm on dm.id=pd.dept_id "+
	                     "WHERE pd.emp_no = ? " +
	                     "UNION " +
	                     "SELECT dm.dept_name,fm.file_name, sm.file_status ,cps.temp_id  " +
	                     "FROM cps_pensioner_details cps " +
	                     "INNER JOIN file_category_master fm ON cps.file_category = fm.id " +
	                     "INNER JOIN file_status_master sm ON sm.id = cps.file_status " +
	                     "inner join department_master dm on dm.id=cps.dept_id "+
	                     "WHERE cps.emp_no = ?";

	        return jdbcTemplate.queryForList(sql, empNo, empNo);
	    }

	 
	 
	/* public List<Map<String, Object>> getEmployeeDetailss(String fileName) {
			
			
		 String sql = "SELECT emp_no FROM gcc_pensioner.pensioner_details pd " +
	             "INNER JOIN gcc_pensioner.file_category_master cm ON pd.file_category = cm.id " +
	             "WHERE cm.file_name = ?";
	return jdbcTemplate .queryForList(sql, new Object[]{fileName});
	    }*/
	/* public List<Map<String, Object>> getStageWiseCounts() {
		 String sql = 
				    "SELECT category, SUM(category_count) AS category_count FROM (" +
				    "    SELECT " +
				    "        CASE " +
				    "            WHEN sm.id IN (1, 3) THEN 'Audit Pending' " +
				    "            WHEN sm.id IN (2, 5, 6, 9) THEN 'Department Pending' " +
				    "            WHEN sm.id IN (4, 7, 8, 10) THEN 'Pension Pending' " +
				    "            WHEN sm.id = 11 THEN 'Completed' " +
				    
				    "        END AS category, " +
				    "        COUNT(*) AS category_count " +
				    "    FROM gcc_pensioner.pensioner_details pd " +
				    "    INNER JOIN gcc_pensioner.file_status_master sm ON pd.file_status = sm.id " +
				    "    GROUP BY category " +

				    "    UNION ALL " +

				    "    SELECT " +
				    "        CASE " +
				    "            WHEN sm.id IN (1, 3) THEN 'Audit Pending' " +
				    "            WHEN sm.id IN (2, 5, 6, 9) THEN 'Department Pending' " +
				    "            WHEN sm.id IN (4, 7, 8, 10) THEN 'Pension Pending' " +
				    "            WHEN sm.id = 11 THEN 'Completed' " +
				   
				    "        END AS category, " +
				    "        COUNT(*) AS category_count " +
				    "    FROM gcc_pensioner.cps_pensioner_details cps " +
				    "    INNER JOIN gcc_pensioner.file_status_master sm ON cps.file_status = sm.id " +
				    "    GROUP BY category " +
				    ") AS combined_results " +
				    "GROUP BY category;";

				return jdbcTemplate.queryForList(sql);

	    }*/
	
	 public List<Map<String, Object>> getEmployeeDetails(String FileId) {
	     String query;

	     switch (FileId) {
	         case "1":
	         case "2":
	         case "3":
	         case "4":
	         case "5":
	             query = "SELECT pd.emp_no " +
	                     "FROM  pensioner_details pd " +
	                     "INNER JOIN file_category_master cm " +
	                     "ON pd.file_category = cm.id " +
	                     "WHERE cm.id = ?";
	             break;

	         case "6":
	             query = "SELECT cps.emp_no " +
	                     "FROM cps_pensioner_details cps " +
	                     "INNER JOIN file_category_master cm " +
	                     "ON cps.file_category = cm.id " +
	                     "WHERE cm.id = ?";
	             break;

	         default:
	             return Collections.emptyList();
	     }

	     return jdbcTemplate.queryForList(query, FileId);
	 }


	 public List<Map<String, Object>> fetchCategoryCounts() {
		 String sql = "SELECT file_name, id, COALESCE(SUM(category_count), 0) AS category_count " +
	                "FROM ( " +
	                "    SELECT cm.file_name, cm.id,COUNT(pd.file_category) AS category_count " +
	                "    FROM file_category_master cm " +
	                "    LEFT JOIN pensioner_details pd " +
	                "    ON pd.file_category = cm.id " +
	                " AND pd.is_closed='0' " +
	               
	               
	                "     GROUP BY cm.file_name,cm.id" +
	                "    UNION ALL " +
	                "    SELECT cm.file_name, cm.id,COUNT(cps.file_category) AS category_count  " +
	                "    FROM file_category_master cm " +
	                "    LEFT JOIN cps_pensioner_details cps " +
	                "    ON cps.file_category = cm.id " +
	             
	               
	                "    GROUP BY cm.file_name ,cm.id " +
	                ") combined_counts " +
	                "GROUP BY file_name,id";

	        return jdbcTemplate.queryForList(sql);
	    } 
		
	  /*  
	    public List<Map<String, Object>> fetchCategorysearchCounts(String startdate, String enddate) {
	    	String sql = "SELECT cm.file_name, COALESCE(SUM(category_count), 0) AS category_count " +
	                "FROM ( " +
	                "    SELECT cm.file_name, COUNT(pd.file_category) AS category_count " +
	                "    FROM gcc_pensioner.file_category_master cm " +
	                "    LEFT JOIN gcc_pensioner.pensioner_details pd " +
	                "    ON pd.file_category = cm.id " +
	                "    AND (? IS NULL OR (? = ? AND DATE(pd.created_date) = ?) OR (DATE(pd.created_date) >= ? AND DATE(pd.created_date) <= ?)) " +
	              
	                "    GROUP BY cm.file_name " +
	                "    UNION ALL " +
	                "    SELECT cm.file_name, COUNT(cps.file_category) AS category_count " +
	                "    FROM gcc_pensioner.file_category_master cm " +
	                "    LEFT JOIN gcc_pensioner.cps_pensioner_details cps " +
	                "    ON cps.file_category = cm.id " +
	                "    AND (? IS NULL OR (? = ? AND DATE(cps.created_date) = ?) OR (DATE(cps.created_date) >= ? AND DATE(cps.created_date) <= ?)) " +
	              
	                "    GROUP BY cm.file_name " +
	                ") combined_counts " +
	                "RIGHT JOIN gcc_pensioner.file_category_master cm " +
	                "ON combined_counts.file_name = cm.file_name " +
	            
	                "GROUP BY cm.file_name";

	return jdbcTemplate.queryForList(sql, new Object[]{
	   startdate, startdate, enddate, startdate, // For `pd.created_date` exact match or range
	   startdate, enddate,                       // For `pd.created_date` range
	   startdate, startdate, enddate, startdate, // For `cps.created_date` exact match or range
	   startdate, enddate                        // For `cps.created_date` range
	});
	
	
	
	
	  



	    }//department*/
	    public List<Map<String, Object>> getDepartment() {
	        String sql = "SELECT dept_id,dept_name FROM department_master WHERE dept_id <> 2000";
	        return jdbcTemplate.queryForList(sql);
	 }

		   
	    public List<Map<String, Object>> DepartmenfetchCategoryCounts(String deptId) {
	        String sql = "SELECT cm.file_name, cm.id, " +
	                     "       COALESCE(pd.category_count, 0) + COALESCE(cps.category_count, 0) AS category_count " +
	                     "FROM file_category_master cm " +
	                     "LEFT JOIN ( " +
	                     "    SELECT file_category, COUNT(*) AS category_count " +
	                     "    FROM pensioner_details " +
	                     "    WHERE (? IS NULL OR dept_id = ?) " +
	                     " AND is_closed='0' " +
	                     "    GROUP BY file_category " +
	                     ") pd ON cm.id = pd.file_category " +
	                     "LEFT JOIN ( " +
	                     "    SELECT file_category, COUNT(*) AS category_count " +
	                     "    FROM cps_pensioner_details " +
	                     "    WHERE (? IS NULL OR dept_id = ?) " +
	                     "    GROUP BY file_category " +
	                     ") cps ON cm.id = cps.file_category";

	        return jdbcTemplate.queryForList(sql, deptId, deptId, deptId, deptId);
	    

		    }   public List<Map<String, Object>> fetchchartCategoryCounts(String deptId, String startDate, String endDate) {
		        String sql = "SELECT cm.file_name, cm.id, " +
	                     "       COALESCE(pd.category_count, 0) + COALESCE(cps.category_count, 0) AS category_count " +
	                     "FROM file_category_master cm " +
	                     "LEFT JOIN ( " +
	                     "    SELECT file_category, COUNT(*) AS category_count " +
	                     "    FROM pensioner_details " +
	                     "    WHERE (? IS NULL OR dept_id = ?) " +
	                     "    AND (? IS NULL OR date(created_date) >= ?) " +
	                     "    AND (? IS NULL OR date(created_date) <= ?) " +
	                     " AND is_closed='0' " +
	                     "    GROUP BY file_category " +
	                     ") pd ON cm.id = pd.file_category " +
	                     "LEFT JOIN ( " +
	                     "    SELECT file_category, COUNT(*) AS category_count " +
	                     "    FROM gcc_pensioner.cps_pensioner_details " +
	                     "    WHERE (? IS NULL OR dept_id = ?) " +
	                     "    AND (? IS NULL OR date(created_date) >= ?) " +
	                     "    AND (? IS NULL OR date(created_date) <= ?) " +
	                     "    GROUP BY file_category " +
	                     ") cps ON cm.id = cps.file_category";

	        return jdbcTemplate.queryForList(
	                sql, deptId, deptId, startDate, startDate, endDate, endDate,
	                deptId, deptId, startDate, startDate, endDate, endDate
	        );
	    }
		    public List<Map<String, Object>> getStageWiseCounts(String deptId, String startDate, String endDate) {
		        String sql =
		            "WITH categories AS (" +
		            "    SELECT 'Audit Pending' AS category UNION ALL " +
		            "    SELECT 'Department Pending' UNION ALL " +
		            "    SELECT 'Pension Pending' UNION ALL " +
		            "    SELECT 'Completed' " +
		            ") " +
		            "SELECT c.category, COALESCE(SUM(cr.category_count), 0) AS category_count " +
		            "FROM categories c " +
		            "LEFT JOIN ( " +
		            "    SELECT category, SUM(category_count) AS category_count FROM ( " +
		            "        SELECT " +
		            "            CASE " +
		            "                WHEN sm.id IN (1, 3) THEN 'Audit Pending' " +
		            "                WHEN sm.id IN (2, 5, 6, 9) THEN 'Department Pending' " +
		            "                WHEN sm.id IN (4, 7, 8, 10) THEN 'Pension Pending' " +
		            "                WHEN sm.id = 11 THEN 'Completed' " +
		            "            END AS category, " +
		            "            COUNT(*) AS category_count " +
		            "        FROM gcc_pensioner.pensioner_details pd " +
		            "        INNER JOIN gcc_pensioner.file_status_master sm ON pd.file_status = sm.id " +
		            "        WHERE (? IS NULL OR pd.dept_id = ?) " +
		            "        AND (? IS NULL OR DATE(pd.created_date) >= ?) " +
		            "        AND (? IS NULL OR DATE(pd.created_date) <= ?) " +
		            "  AND pd.is_closed='0' " +
		            "        GROUP BY category " +

		            "        UNION ALL " +

		            "        SELECT " +
		            "            CASE " +
		            "                WHEN sm.id IN (1, 3) THEN 'Audit Pending' " +
		            "                WHEN sm.id IN (2, 5, 6, 9) THEN 'Department Pending' " +
		            "                WHEN sm.id IN (4, 7, 8, 10) THEN 'Pension Pending' " +
		            "                WHEN sm.id = 11 THEN 'Completed' " +
		            "            END AS category, " +
		            "            COUNT(*) AS category_count " +
		            "        FROM gcc_pensioner.cps_pensioner_details cps " +
		            "        INNER JOIN gcc_pensioner.file_status_master sm ON cps.file_status = sm.id " +
		            "        WHERE (? IS NULL OR cps.dept_id = ?) " +
		            "        AND (? IS NULL OR DATE(cps.created_date) >= ?) " +
		            "        AND (? IS NULL OR DATE(cps.created_date) <= ?) " +
		            "        GROUP BY category " +
		            "    ) AS combined_results " +
		            "    GROUP BY category " +
		            ") cr ON c.category = cr.category " +
		            "GROUP BY c.category;";

		        return jdbcTemplate.queryForList(
		            sql, deptId, deptId, startDate, startDate, endDate, endDate,
		            deptId, deptId, startDate, startDate, endDate, endDate
		        );
		    }

		    

		    //simple
		    public List<Map<String, Object>> fetchCategorysearchCounts(String startdate, String enddate,String deptId) {
		    	String sql = "SELECT cm.file_name, COALESCE(SUM(category_count), 0) AS category_count " +
		    		    "FROM gcc_pensioner.file_category_master cm " +
		    		    "LEFT JOIN ( " +
		    		    "    SELECT pd.file_category, COUNT(pd.file_category) AS category_count " +
		    		    "    FROM gcc_pensioner.pensioner_details pd " +
		    		    "    WHERE (? IS NULL OR DATE(pd.created_date) = ? OR (DATE(pd.created_date) BETWEEN ? AND ?)) " +
		    		    "    AND (? IS NULL OR pd.dept_id = ?) " +
		    		    " AND pd.is_closed='0' " +
		    		    "    GROUP BY pd.file_category " +
		    		    
		    		    "    UNION ALL " +
		    		    "    SELECT cps.file_category, COUNT(cps.file_category) AS category_count " +
		    		    "    FROM gcc_pensioner.cps_pensioner_details cps " +
		    		    "    WHERE (? IS NULL OR DATE(cps.created_date) = ? OR (DATE(cps.created_date) BETWEEN ? AND ?)) " +
		    		    "    AND (? IS NULL OR cps.dept_id = ?) " +
		    		    "    GROUP BY cps.file_category " +
		    		    ") combined_counts ON cm.id = combined_counts.file_category " +
		    		    "GROUP BY cm.file_name";
	
		    		return jdbcTemplate.queryForList(sql, new Object[]{
		    		   startdate, startdate, startdate, enddate, deptId, deptId,  // For `pd.created_date` and `dept_id`
		    		   startdate, startdate, startdate, enddate, deptId, deptId   // For `cps.created_date` and `dept_id`
		    		});
	
		    }
	}
//total 5 changes for is_closed=0
/* this is with pendency file category
 * "SELECT file_name, SUM(category_count) AS category_count " +
	                "FROM ( " +
	                "    SELECT cm.file_name, COUNT(pd.file_category) AS category_count " +
	                "    FROM gcc_pensioner.file_category_master cm " +
	                "    LEFT JOIN gcc_pensioner.pensioner_details pd " +
	                "    ON pd.file_category = cm.id " +
	                "    GROUP BY cm.file_name " +
	                "    UNION ALL " +
	                "    SELECT cm.file_name, COUNT(cps.file_category) AS category_count " +
	                "    FROM gcc_pensioner.file_category_master cm " +
	                "    LEFT JOIN gcc_pensioner.cps_pensioner_details cps " +
	                "    ON cps.file_category = cm.id " +
	                "    GROUP BY cm.file_name " +
	                ") combined_counts " +
	                "GROUP BY file_name";--------------------------------------------------------------------------"SELECT cm.file_name, COALESCE(SUM(category_count), 0) AS category_count " +
	                "FROM ( " +
	                "    SELECT cm.file_name, COUNT(pd.file_category) AS category_count " +
	                "    FROM gcc_pensioner.file_category_master cm " +
	                "    LEFT JOIN gcc_pensioner.pensioner_details pd " +
	                "    ON pd.file_category = cm.id " +
	                "    AND (? IS NULL OR (? = ? AND DATE(pd.created_date) = ?) OR (DATE(pd.created_date) >= ? AND DATE(pd.created_date) <= ?)) " +
	                "    GROUP BY cm.file_name " +
	                "    UNION ALL " +
	                "    SELECT cm.file_name, COUNT(cps.file_category) AS category_count " +
	                "    FROM gcc_pensioner.file_category_master cm " +
	                "    LEFT JOIN gcc_pensioner.cps_pensioner_details cps " +
	                "    ON cps.file_category = cm.id " +
	                "    AND (? IS NULL OR (? = ? AND DATE(cps.created_date) = ?) OR (DATE(cps.created_date) >= ? AND DATE(cps.created_date) <= ?)) " +
	                "    GROUP BY cm.file_name " +
	                ") combined_counts " +
	                "RIGHT JOIN gcc_pensioner.file_category_master cm " +
	                "ON combined_counts.file_name = cm.file_name " +
	                "GROUP BY cm.file_name"; */
