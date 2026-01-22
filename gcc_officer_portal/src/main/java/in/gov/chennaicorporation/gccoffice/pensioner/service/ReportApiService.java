package in.gov.chennaicorporation.gccoffice.pensioner.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@Service
public class ReportApiService {
	@Autowired
    private JdbcTemplate jdbcTemplate;
 
 
   private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    @Autowired
    private AppConfig appconfig;

    public ReportApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Autowired
	 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
    
    @Autowired
    public GetFileStatusService getFileStatus;

	 public List<Map<String, Object>> getFileCategories() {
	        String sql = "SELECT id,file_name FROM file_category_master";
	        return jdbcTemplate.queryForList(sql);
	 }
	 
//	 String sql = "SELECT fm.file_name, " +
//	            "COALESCE(SUM(CASE WHEN pd.file_status IN (1,3) THEN 1 ELSE 0 END), 0) AS audit_pending, " +
//	            "COALESCE(SUM(CASE WHEN pd.file_status IN (2,5,6,9) THEN 1 ELSE 0 END), 0) AS department_pending, " +
//	            "COALESCE(SUM(CASE WHEN pd.file_status IN (4,7,8,10) THEN 1 ELSE 0 END), 0) AS pension_pending, " +
//	            "COALESCE(SUM(CASE WHEN pd.file_status = 11 THEN 1 ELSE 0 END), 0) AS completed " +
//	            "FROM gcc_pensioner.file_category_master fm " +
//	            "LEFT JOIN gcc_pensioner.pensioner_details pd " +
//	            "ON pd.file_category = fm.id " +
//	            "AND pd.created_date BETWEEN ? AND ? " +
//	            "AND pd.dept_id = ? " +
//	            "AND pd.is_closed = 0 " +  // Ensure only active records are considered
//	            "GROUP BY fm.file_name";
	 
	 
	 public List<Map<String, Object>> getReportData(String startDate, String endDate, String deptId) {
		    String sql = "SELECT fm.id, " +
		                 "       fm.file_name AS file_name,  " +

		                 // Audit Pending Count
		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
		                 "        WHERE pd.file_status IN (1,3) AND pd.dept_id = ? " +
		                 "		  AND pd.file_category=fm.id "+
		                 "        AND pd.is_closed = 0 " +
		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) AS audit_pending, " +

		                 // Department Pending Count
		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
		                 "        WHERE pd.file_status IN (2,5,6,9) AND pd.dept_id = ? " +
		                 "		  AND pd.file_category=fm.id "+
		                 "        AND pd.is_closed = 0 " +
		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) AS department_pending, " +

		                 // Pension Pending Count
		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
		                 "        WHERE pd.file_status IN (4,7,8,10) AND pd.dept_id = ? " +
		                 "		  AND pd.file_category=fm.id "+
		                 "        AND pd.is_closed = 0 " +
		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) AS pension_pending, " +

		                 // Completed Count (SUM of two tables)
		                 "       (COALESCE((SELECT COUNT(*) FROM pensioner_details pd " +
		                 "        WHERE pd.file_status = 11 AND pd.dept_id = ? " +
		                 "		  AND pd.file_category=fm.id "+
		                 "        AND pd.is_closed = 0 " +
		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)), 0) " +
		                 "       + " +
		                 "       COALESCE((SELECT COUNT(*) FROM cps_pensioner_details cps " +
		                 "        WHERE cps.file_status = 11 AND cps.dept_id = ? " +
		                 "		  AND cps.file_category=fm.id "+
		                 "        AND DATE(cps.created_date) BETWEEN DATE(?) AND DATE(?)), 0)) AS completed, " +

		                 // Total Files Count (SUM of two tables)
		                 "       (COALESCE((SELECT COUNT(*) FROM pensioner_details pd " +
		                 "        WHERE pd.dept_id = ? " +
		                 "		  AND pd.file_category=fm.id "+
		                 "        AND pd.is_closed = 0 " +
		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)), 0) " +
		                 "       + " +
		                 "       COALESCE((SELECT COUNT(*) FROM cps_pensioner_details cps " +
		                 "        WHERE cps.dept_id = ? " +
		                 "		  AND cps.file_category=fm.id "+
		                 "        AND DATE(cps.created_date) BETWEEN DATE(?) AND DATE(?)), 0)) AS total_files " +

		                 "FROM file_category_master fm " +
		                 "GROUP BY fm.id, fm.file_name " + // Correct placement of GROUP BY
		                 "ORDER BY fm.id"; // ORDER BY after GROUP BY

		    List<Object> params = new ArrayList<>();
		    
		    // Adding parameters in the correct order for each subquery
		    for (int i = 0; i < 7; i++) {  // 6 subqueries that use deptId, startDate, endDate
		        params.add(deptId);
		        params.add(startDate);
		        params.add(endDate);
		    }

		    return jdbcTemplate.queryForList(sql, params.toArray());
		}



	 
	 public List<Map<String, Object>> getReportDetails(String startDate, String endDate, Integer deptId, Integer fileCatId, List<String> fileStatus) {
		    String sql = "";
		    List<Object> params = new ArrayList<>();

		    // Construct fileStatus condition
		    String fileStatusCondition = (fileStatus != null && !fileStatus.isEmpty()) 
		        ? " AND pd.file_status IN (" + String.join(",", Collections.nCopies(fileStatus.size(), "?")) + ")"
		        : ""; // Skip condition if fileStatus is empty or null

		    if (fileCatId == 6) {
		        sql = "SELECT pd.emp_no, pd.emp_name, pd.designation, pd.type_of_retirement, pd.reason_for_pending, pd.retirement_date, " +
		              "fm.file_status AS fst, pd.file_category_name, pd.remarks, DATE(pd.created_date) AS created_date " +
		              "FROM cps_pensioner_details pd " +
		              "INNER JOIN file_status_master fm ON pd.file_status = fm.id " +
		              "WHERE DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?) " + // First date filter
		              "AND pd.dept_id = ? " + // Next deptId
		              "AND pd.file_category = ? " + // Next fileCatId
		              fileStatusCondition; // Last fileStatus condition

		    } else {
		        sql = "SELECT pd.*, fm.file_status AS fst " +
		              "FROM pensioner_details pd " +
		              "INNER JOIN file_status_master fm ON pd.file_status = fm.id " +
		              "WHERE DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?) " + // First date filter
		              "AND pd.dept_id = ? " + // Next deptId
		              "AND pd.file_category = ? " + // Next fileCatId
		              fileStatusCondition; // Last fileStatus condition
		    }

		    // Add date range parameters first
		    params.add(startDate);
		    params.add(endDate);
		    
		    // Add deptId and fileCatId parameters
		    params.add(deptId);
		    params.add(fileCatId);
		    
		    // Add fileStatus parameters if present
		    if (fileStatus != null && !fileStatus.isEmpty()) {
		        params.addAll(fileStatus);
		    }

		    System.out.println("Executing SQL Query: " + sql);
		    System.out.println("Query Parameters: " + params);

		    return jdbcTemplate.queryForList(sql, params.toArray());
		}


	     	

	 
	 
	 //pensioner side reorts methods
	 
	 public List<Map<String, Object>> getstatus() {
	        String sql = "select id , file_status from file_status_master";
	        return jdbcTemplate.queryForList(sql);
	 }
	 
	 public List<Map<String, Object>> getdepartment() {
	        String sql = "select dept_id ,dept_name from department_master WHERE dept_id <> 2000";
	        return jdbcTemplate.queryForList(sql);
	 }
	 

//	 public List<Map<String, Object>> getReportDatapension(String startDate, String endDate, String fileCategoryId) {
//		    String sql = "SELECT fm.file_name, " +
//		                 "fm.id AS file_category_id, " +
//		                 "COALESCE(dm.dept_name, 'All Departments') AS dept_name, " +
//		                 "COALESCE(SUM(CASE WHEN pd.file_status IN (1,3) OR cps.file_status IN (1,3) THEN 1 ELSE 0 END), 0) AS audit_pending, " +
//		                 "COALESCE(SUM(CASE WHEN pd.file_status IN (2,5,6,9) OR cps.file_status IN (2,5,6,9) THEN 1 ELSE 0 END), 0) AS department_pending, " +
//		                 "COALESCE(SUM(CASE WHEN pd.file_status IN (4,7,8,10) OR cps.file_status IN (4,7,8,10) THEN 1 ELSE 0 END), 0) AS pension_pending, " +
//		                 "COALESCE(SUM(CASE WHEN pd.file_status = 11 OR cps.file_status = 11 THEN 1 ELSE 0 END), 0) AS completed, " +
//		                 "COALESCE(COUNT(pd.id) + COUNT(cps.id), 0) AS total_files " + 
//		                 "FROM gcc_pensioner.file_category_master fm " +  
//		                 "LEFT JOIN gcc_pensioner.cps_pensioner_details cps ON cps.file_category = fm.id " +  
//		                 "LEFT JOIN gcc_pensioner.pensioner_details pd ON pd.file_category = fm.id " +  
//		                 "LEFT JOIN gcc_pensioner.department_master dm ON dm.dept_id = COALESCE(pd.dept_id, cps.dept_id) " +  
//		                 "WHERE (pd.created_date BETWEEN ? AND ? OR cps.created_date BETWEEN ? AND ?) " + 
//		                 (fileCategoryId != null && !fileCategoryId.isEmpty() ? "AND fm.id = ? " : "") +  
//		                 "GROUP BY fm.id, fm.file_name, dm.dept_name " +
//		                 "ORDER BY fm.id, dm.dept_name, fm.file_name";
//
//		    System.out.println("Executing SQL: " + sql);
//
//		    List<Object> params = new ArrayList<>();
//		    params.add(startDate);
//		    params.add(endDate);
//		    params.add(startDate); // Date filter applies to both `pd` and `cps`
//		    params.add(endDate);
//		    
//		    if (fileCategoryId != null && !fileCategoryId.isEmpty()) {
//		        params.add(fileCategoryId);
//		    }
//
//		    return jdbcTemplate.queryForList(sql, params.toArray());
//		}
	 
	 
	 

//	 public List<Map<String, Object>> getReportDatapension(String startDate, String endDate, String fileCategoryId) {
//		    String sql = "SELECT dm.dept_id, " +
//		                 "       dm.dept_name AS dept_name,  " +
//
//		                 // Audit Pending Count
//		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
//		                 "        WHERE pd.file_status IN (1,3) AND pd.dept_id = dm.dept_id " +
//		                 "        AND pd.is_closed = 0 " +  // Added condition
//		                 "        AND ( ? IS NULL OR pd.file_category = ? ) " +
//		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) AS audit_pending, " +
//
//		                 // Department Pending Count
//		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
//		                 "        WHERE pd.file_status IN (2,5,6,9) AND pd.dept_id = dm.dept_id " +
//		                 "        AND pd.is_closed = 0 " +  // Added condition
//		                 "        AND ( ? IS NULL OR pd.file_category = ? ) " +
//		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?))  AS department_pending, " +
//
//		                 // Pension Pending Count
//		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
//		                 "        WHERE pd.file_status IN (4,7,8,10) AND pd.dept_id = dm.dept_id " +
//		                 "        AND pd.is_closed = 0 " +  // Added condition
//		                 "        AND ( ? IS NULL OR pd.file_category = ? ) " +
//		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) AS pension_pending, " +
//
//		                 // Completed Count
//		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
//		                 "        WHERE pd.file_status = 11 AND pd.dept_id = dm.dept_id " +
//		                 "        AND pd.is_closed = 0 " +  // Added condition
//		                 "        AND ( ? IS NULL OR pd.file_category = ? ) " +
//		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) " +
//		                 "        + " +
//		                 "       (SELECT COUNT(*) FROM cps_pensioner_details cps " +
//		                 "        WHERE cps.file_status = 11 AND cps.dept_id = dm.dept_id " +		               
//		                 "        AND ( ? IS NULL OR cps.file_category = ? ) " +
//		                 "        AND DATE(cps.created_date) BETWEEN DATE(?) AND DATE(?)) AS completed, " +
//
//		                 // Total Files Count
//		                 "       (SELECT COUNT(*) pensioner_details pd " +
//		                 "        WHERE pd.dept_id = dm.dept_id " +
//		                 "        AND pd.is_closed = 0 " +  // Added condition
//		                 "        AND ( ? IS NULL OR pd.file_category = ? ) " +
//		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) " +
//		                 "        + " +
//		                 "       (SELECT COUNT(*) FROM cps_pensioner_details cps " +
//		                 "        WHERE cps.dept_id = dm.dept_id " +
//		                 "        AND ( ? IS NULL OR cps.file_category = ? ) " +
//		                 "        AND DATE(cps.created_date) BETWEEN DATE(?) AND DATE(?)) AS total_files " +
//
//		                 "FROM department_master dm " +
//		                 "ORDER BY dm.dept_id";
//
//		    System.out.println("Executing SQL: " + sql);
//
//		    List<Object> params = new ArrayList<>();
//
//		    // Adding parameters explicitly without a loop
//		    params.add(fileCategoryId != null && !fileCategoryId.isEmpty() ? fileCategoryId : null); 
//		    params.add(fileCategoryId);
//		    params.add(startDate);
//		    params.add(endDate);
//
//		    params.add(fileCategoryId != null && !fileCategoryId.isEmpty() ? fileCategoryId : null); 
//		    params.add(fileCategoryId);
//		    params.add(startDate);
//		    params.add(endDate);
//
//		    params.add(fileCategoryId != null && !fileCategoryId.isEmpty() ? fileCategoryId : null); 
//		    params.add(fileCategoryId);
//		    params.add(startDate);
//		    params.add(endDate);
//
//		    params.add(fileCategoryId != null && !fileCategoryId.isEmpty() ? fileCategoryId : null); 
//		    params.add(fileCategoryId);
//		    params.add(startDate);
//		    params.add(endDate);
//
//		    params.add(fileCategoryId != null && !fileCategoryId.isEmpty() ? fileCategoryId : null); 
//		    params.add(fileCategoryId);
//		    params.add(startDate);
//		    params.add(endDate);
//
//		    params.add(fileCategoryId != null && !fileCategoryId.isEmpty() ? fileCategoryId : null); 
//		    params.add(fileCategoryId);
//		    params.add(startDate);
//		    params.add(endDate);
//
//		    params.add(fileCategoryId != null && !fileCategoryId.isEmpty() ? fileCategoryId : null); 
//		    params.add(fileCategoryId);
//		    params.add(startDate);
//		    params.add(endDate);
//
//		    return jdbcTemplate.queryForList(sql, params.toArray());
//		}
	 
	 
	 public List<Map<String, Object>> getReportDatapension(String startDate, String endDate, String fileCategoryId) {
		    String sql = "SELECT dm.dept_id, " +
		                 "       dm.dept_name AS dept_name, " +

		                 // Audit Pending Count
		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
		                 "        WHERE pd.file_status IN (1,3) AND pd.dept_id = dm.dept_id " +
		                 "        AND pd.is_closed = 0 " +
		                 "        AND ( ? IS NULL OR pd.file_category = ? ) " +
		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) AS audit_pending, " +

		                 // Department Pending Count
		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
		                 "        WHERE pd.file_status IN (2,5,6,9) AND pd.dept_id = dm.dept_id " +
		                 "        AND pd.is_closed = 0 " +
		                 "        AND ( ? IS NULL OR pd.file_category = ? ) " +
		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?))  AS department_pending, " +

		                 // Pension Pending Count
		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
		                 "        WHERE pd.file_status IN (4,7,8,10) AND pd.dept_id = dm.dept_id " +
		                 "        AND pd.is_closed = 0 " +
		                 "        AND ( ? IS NULL OR pd.file_category = ? ) " +
		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) AS pension_pending, " +

		                 // Completed Count
		                 "       (SELECT COUNT(*) FROM pensioner_details pd " +
		                 "        WHERE pd.file_status = 11 AND pd.dept_id = dm.dept_id " +
		                 "        AND pd.is_closed = 0 " +
		                 "        AND ( ? IS NULL OR pd.file_category = ? ) " +
		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) " +
		                 "        + " +
		                 "       (SELECT COUNT(*) FROM cps_pensioner_details cps " +
		                 "        WHERE cps.file_status = 11 AND cps.dept_id = dm.dept_id " +		               
		                 "        AND ( ? IS NULL OR cps.file_category = ? ) " +
		                 "        AND DATE(cps.created_date) BETWEEN DATE(?) AND DATE(?)) AS completed, " +

		                 // Total Files Count
		                 "       (SELECT COUNT(*) FROM pensioner_details pd " + // Fixed Syntax
		                 "        WHERE pd.dept_id = dm.dept_id " +
		                 "        AND pd.is_closed = 0 " +
		                 "        AND ( ? IS NULL OR pd.file_category = ? ) " +
		                 "        AND DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?)) " +
		                 "        + " +
		                 "       (SELECT COUNT(*) FROM cps_pensioner_details cps " +
		                 "        WHERE cps.dept_id = dm.dept_id " +
		                 "        AND ( ? IS NULL OR cps.file_category = ? ) " +
		                 "        AND DATE(cps.created_date) BETWEEN DATE(?) AND DATE(?)) AS total_files " +

		                 "FROM department_master dm " +
		                 "ORDER BY dm.dept_id";

		    System.out.println("Executing SQL: " + sql);

		    List<Object> params = new ArrayList<>();

		    for (int i = 0; i < 7; i++) {
		        params.add(fileCategoryId != null && !fileCategoryId.isEmpty() ? fileCategoryId : null); 
		        params.add(fileCategoryId);
		        params.add(startDate);
		        params.add(endDate);
		    }

		    return jdbcTemplate.queryForList(sql, params.toArray());
		}


	 
//		 public List<Map<String, Object>> getReportDetailspension(String startDate, String endDate, List<String> deptIds, Integer fileCatId, List<String> fileStatus) {
//			    String sql = "";
//			    List<Object> params = new ArrayList<>();
//
//			    boolean selectAll = deptIds.contains("ALL");
//
//			    // ✅ If "ALL" is selected, remove department filtering
//			    String deptCondition = selectAll ? "1=1" : "pd.dept_id IN (" + String.join(",", Collections.nCopies(deptIds.size(), "?")) + ")";
//
//			    // ✅ Handle fileStatus as multiple values in IN clause
//			    String fileStatusCondition = (fileStatus != null && !fileStatus.isEmpty()) 
//			        ? " AND pd.file_status IN (" + String.join(",", Collections.nCopies(fileStatus.size(), "?")) + ")"
//			        : ""; // Skip condition if fileStatus is empty or null
//
//			    if (fileCatId == 6) {
//			        sql = "SELECT pd.emp_no, pd.emp_name, pd.designation, pd.type_of_retirement, pd.reason_for_pending, " +
//			              "fm.file_status AS fst, pd.file_category_name, pd.remarks, DATE(pd.created_date) AS created_date " +
//			              "FROM cps_pensioner_details pd " +
//			              "INNER JOIN file_status_master fm ON pd.file_status = fm.id " +
//			              "WHERE " + deptCondition + " AND pd.file_category = ?" + fileStatusCondition;
//
//			        if (!selectAll) {
//			            params.addAll(deptIds);
//			        }
//			        params.add(fileCatId);
//			        if (fileStatus != null && !fileStatus.isEmpty()) {
//			            params.addAll(fileStatus);
//			        }
//			    }
//			    else {
//			        sql = "SELECT pd.*, fm.file_status as fst FROM pensioner_details pd " +
//			              "INNER JOIN file_status_master fm ON pd.file_status = fm.id " +
//			              "WHERE " + deptCondition + " AND pd.file_category = ?" + fileStatusCondition;
//
//			        if (!selectAll) {
//			            params.addAll(deptIds);
//			        }
//			        params.add(fileCatId);
//			        if (fileStatus != null && !fileStatus.isEmpty()) {
//			            params.addAll(fileStatus);
//			        }
//			    }
//
//			    System.out.println("Executing SQL Query: " + sql);
//			    System.out.println("Query Parameters: " + params);
//
//			    return jdbcTemplate.queryForList(sql, params.toArray());
//			}
	 
	 
	 
//	 public List<Map<String, Object>> getReportDetailspension(String startDate, String endDate, List<String> deptIds, Integer fileCatId, List<String> fileStatus) {
//		    String sql = "";
//		    List<Object> params = new ArrayList<>();
//
//		    boolean selectAll = deptIds.contains("ALL");
//
//		    // ✅ If "ALL" is selected, remove department filtering
//		    String deptCondition = selectAll ? "1=1" : "pd.dept_id IN (" + String.join(",", Collections.nCopies(deptIds.size(), "?")) + ")";
//
//		    // ✅ Handle fileStatus as multiple values in IN clause
//		    String fileStatusCondition = (fileStatus != null && !fileStatus.isEmpty()) 
//		        ? " AND pd.file_status IN (" + String.join(",", Collections.nCopies(fileStatus.size(), "?")) + ")"
//		        : ""; // Skip condition if fileStatus is empty or null
//
//		    if (fileCatId == 6) {
//		        sql = "SELECT pd.emp_no, pd.emp_name, pd.designation, pd.type_of_retirement, pd.reason_for_pending,pd.retirement_date, " +
//		              "fm.file_status AS fst, pd.file_category_name, pd.remarks, DATE(pd.created_date) AS created_date " +
//		              "FROM cps_pensioner_details pd " +
//		              "INNER JOIN file_status_master fm ON pd.file_status = fm.id " +
//		              "WHERE DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?) " + // First: Date filter
//		              "AND pd.file_category = ? " + // Second: File category filter
//		              "AND " + deptCondition + // Third: Department condition
//		              fileStatusCondition; // Last: File status condition
//
//		    } else {
//		        sql = "SELECT pd.*, fm.file_status as fst " +
//		              "FROM pensioner_details pd " +
//		              "INNER JOIN file_status_master fm ON pd.file_status = fm.id " +
//		              "WHERE DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?) " + // First: Date filter
//		              "AND pd.file_category = ? " + // Second: File category filter
//		              "AND " + deptCondition + // Third: Department condition
//		              fileStatusCondition; // Last: File status condition
//		    }
//
//		    // Add date range parameters first
//		    params.add(startDate);
//		    params.add(endDate);
//		    
//		    // Add file category next
//		    params.add(fileCatId);
//		    
//		    // Add department filter parameters if not "ALL"
//		    if (!selectAll) {
//		        params.addAll(deptIds);
//		    }
//
//		    // Add fileStatus parameters if present
//		    if (fileStatus != null && !fileStatus.isEmpty()) {
//		        params.addAll(fileStatus);
//		    }
//
//		    System.out.println("Executing SQL Query: " + sql);
//		    System.out.println("Query Parameters: " + params);
//
//		    return jdbcTemplate.queryForList(sql, params.toArray());
//		}


	 
	 public List<Map<String, Object>> getReportDetailspension(String startDate, String endDate, List<String> deptIds, Integer fileCatId, List<String> fileStatus) {
		    String sql = "";
		    List<Object> params = new ArrayList<>();

		    // ✅ Ensure deptIds is never empty, otherwise fetch nothing
		    if (deptIds == null || deptIds.isEmpty()) {
		        return new ArrayList<>();
		    }

		    // ✅ Construct IN clause for department filtering
		    String deptCondition = "pd.dept_id IN (" + String.join(",", Collections.nCopies(deptIds.size(), "?")) + ")";

		    // ✅ Handle fileStatus as multiple values in IN clause
		    String fileStatusCondition = (fileStatus != null && !fileStatus.isEmpty()) 
		        ? " AND pd.file_status IN (" + String.join(",", Collections.nCopies(fileStatus.size(), "?")) + ")"
		        : ""; // Skip condition if fileStatus is empty or null

		    if (fileCatId == 6) {
		        sql = "SELECT pd.emp_no, pd.emp_name, pd.designation, pd.type_of_retirement, pd.reason_for_pending, pd.retirement_date, " +
		              "fm.file_status AS fst, pd.file_category_name, pd.remarks, DATE(pd.created_date) AS created_date " +
		              "FROM cps_pensioner_details pd " +
		              "INNER JOIN file_status_master fm ON pd.file_status = fm.id " +
		              "WHERE DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?) " + 
		              "AND pd.file_category = ? " + 
		              "AND " + deptCondition + 
		              fileStatusCondition;

		    } else {
		        sql = "SELECT pd.*, fm.file_status as fst " +
		              "FROM pensioner_details pd " +
		              "INNER JOIN file_status_master fm ON pd.file_status = fm.id " +
		              "WHERE DATE(pd.created_date) BETWEEN DATE(?) AND DATE(?) " + 
		              "AND pd.file_category = ? " + 
		              "AND " + deptCondition + 
		              fileStatusCondition;
		    }

		    // Add date range parameters first
		    params.add(startDate);
		    params.add(endDate);
		    
		    // Add file category next
		    params.add(fileCatId);
		    
		    // Add department filter parameters
		    params.addAll(deptIds);

		    // Add fileStatus parameters if present
		    if (fileStatus != null && !fileStatus.isEmpty()) {
		        params.addAll(fileStatus);
		    }

		    System.out.println("Executing SQL Query: " + sql);
		    System.out.println("Query Parameters: " + params);

		    return jdbcTemplate.queryForList(sql, params.toArray());
		}



	public List<Map<String, Object>> getDepartmentpending(int file_category) {
		
		String sql = "SELECT dept_name, emp_name, emp_no, file_category_name, file_status, file_moved_date, remarks " +
             "FROM pensioner_details WHERE file_status IN (2, 5, 6, 9) AND file_category=?";
		
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, file_category);

		return jdbcTemplate.queryForList(sql, file_category);
	}

	 

}
