package in.gov.chennaicorporation.gccoffice.callcenter.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {
	
	
//	private JdbcTemplate jdbcTemplate;
//	
//	
//	@Autowired
//	public void setDataSource(@Qualifier("BiometricDataSource") DataSource dataSource) {
//		this.jdbcTemplate = new JdbcTemplate(dataSource);
//	}
//		
//	
//	public List<Map<String, Object>> getAttendanceReport(String fromDate, String toDate) {
//	    String reportQuery;
//
//	    if (fromDate != null && toDate != null) {
//	        // Query with date filter if dates are provided
//	        reportQuery = "SELECT d.zone, d.division, b.user_id, b.user_name, f.departmentname, e.designation, " +
//	                      "c.device_name, CAST(a.officepunch AS DATE) AS punch_date, MIN(a.OFFICEPUNCH) AS PUNCHIN, " +
//	                      "CASE WHEN MAX(a.OFFICEPUNCH) = MIN(a.OFFICEPUNCH) THEN NULL ELSE MAX(a.OFFICEPUNCH) END AS PUNCHOUT " +
//	                      "FROM machinerawpunch a, tbl_realtime_userinfo b, tbl_fkdevice_status c, zone_div_mast d, " +
//	                      "tblemployee e, tbldepartment f " +
//	                      "WHERE a.paycode = b.user_id AND a.deviceid = c.device_id AND c.device_name = d.devname " +
//	                      "AND a.paycode = e.paycode AND e.departmentcode = f.departmentcode " +
//	                      "AND CAST(a.officepunch AS DATE) BETWEEN ? AND ? " +
//	                      "AND a.paycode LIKE '%19130%' " +
//	                      "GROUP BY d.zone, d.division, b.user_id, b.user_name, f.departmentname, e.designation, " +
//	                      "c.device_name, CAST(a.officepunch AS DATE) " +
//	                      "ORDER BY d.zone, d.division, b.user_id, CAST(a.officepunch AS DATE)";
//	        
//	        // Execute query with date parameters
//	        return jdbcTemplate.queryForList(reportQuery, fromDate, toDate);
//	        
//	    } else {
//	        // Query without date filter
//	        reportQuery = "SELECT d.zone, d.division, b.user_id, b.user_name, f.departmentname, e.designation, " +
//	                      "c.device_name, CAST(a.officepunch AS DATE) AS punch_date, MIN(a.OFFICEPUNCH) AS PUNCHIN, " +
//	                      "CASE WHEN MAX(a.OFFICEPUNCH) = MIN(a.OFFICEPUNCH) THEN NULL ELSE MAX(a.OFFICEPUNCH) END AS PUNCHOUT " +
//	                      "FROM machinerawpunch a, tbl_realtime_userinfo b, tbl_fkdevice_status c, zone_div_mast d, " +
//	                      "tblemployee e, tbldepartment f " +
//	                      "WHERE a.paycode = b.user_id AND a.deviceid = c.device_id AND c.device_name = d.devname " +
//	                      "AND a.paycode = e.paycode AND e.departmentcode = f.departmentcode " +
//	                      "AND a.paycode LIKE '%19130%' " +
//	                      "GROUP BY d.zone, d.division, b.user_id, b.user_name, f.departmentname, e.designation, " +
//	                      "c.device_name, CAST(a.officepunch AS DATE) " +
//	                      "ORDER BY d.zone, d.division, b.user_id, CAST(a.officepunch AS DATE)";
//	        
//	        // Execute query without date parameters
//	        return jdbcTemplate.queryForList(reportQuery);
//	    }
//	}
//
//	
//	
////	public List<Map<String, Object>> getAttendanceReport(String fromDate, String toDate) {
////        System.out.println("Service Layer: Generating attendance report");
////
////        String reportQuery = "SELECT d.zone, d.division, b.user_id, b.user_name, f.departmentname, e.designation, c.device_name, " +
////                             "CAST(a.officepunch AS DATE) AS punch_date, MIN(a.OFFICEPUNCH) AS PUNCHIN, " +
////                             "CASE WHEN MAX(a.OFFICEPUNCH) = MIN(a.OFFICEPUNCH) THEN NULL ELSE MAX(a.OFFICEPUNCH) END AS PUNCHOUT " +
////                             "FROM machinerawpunch a, tbl_realtime_userinfo b, tbl_fkdevice_status c, zone_div_mast d, " +
////                             "tblemployee e, tbldepartment f " +
////                             "WHERE a.paycode = b.user_id AND a.deviceid = c.device_id AND c.device_name = d.devname " +
////                             "AND a.paycode = e.paycode AND e.departmentcode = f.departmentcode ";
////
////        // Dynamic date filter
////        if (fromDate != null && toDate != null) {
////            reportQuery += "AND CAST(a.officepunch AS DATE) BETWEEN ? AND ? ";
////        } else if (fromDate != null) {
////            reportQuery += "AND CAST(a.officepunch AS DATE) = ? ";
////        } else {
////            throw new IllegalArgumentException("fromDate or toDate must be provided");
////        }
////
////        reportQuery += "AND a.paycode LIKE '%19130%' " +
////                       "GROUP BY d.zone, d.division, b.user_id, b.user_name, f.departmentname, e.designation, c.device_name, " +
////                       "CAST(a.officepunch AS DATE) " +
////                       "ORDER BY d.zone, d.division, b.user_id, CAST(a.officepunch AS DATE)";
////
////        // Execute query based on dates
////        if (fromDate != null && toDate != null) {
////            return jdbcTemplate.queryForList(reportQuery, fromDate, toDate);
////        } else {
////            return jdbcTemplate.queryForList(reportQuery, fromDate);
////        }
////    }
//	
////	public List<Map<String, Object>> getAttendanceReport() {
////		 System.out.println("check 2");
////  		String reportQuery ="SELECT d.zone,d.division,b.user_id,b.user_name,f.departmentname,e.designation,c.device_name,CAST(a.officepunch AS DATE) as punch_date,MIN(a.OFFICEPUNCH) AS PUNCHIN,\r\n"
////  				+ "CASE WHEN MAX(a.OFFICEPUNCH) = MIN(a.OFFICEPUNCH) THEN NULL ELSE MAX(a.OFFICEPUNCH) END AS PUNCHOUT FROM machinerawpunch a,tbl_realtime_userinfo b,tbl_fkdevice_status c,\r\n"
////  				+ "zone_div_mast d,tblemployee e,tbldepartment f WHERE a.paycode = b.user_id AND a.deviceid = c.device_id AND c.device_name = d.devname AND a.paycode = e.paycode AND \r\n"
////  				+ "e.departmentcode = f.departmentcode AND CAST(officepunch AS DATE) BETWEEN '2024-10-01' AND '2024-10-10'\r\n"
////  				+ "AND a.paycode LIKE '%19130%' \r\n"
////  				+ "GROUP BY d.zone,d.division,b.user_id,b.user_name,f.departmentname,e.designation,c.device_name,CAST(a.officepunch AS DATE)\r\n"
////  				+ "ORDER BY d.zone,d.division,b.user_id,CAST(a.officepunch AS DATE)";
////  		 System.out.println("check 3"); 
////  	     List<Map<String,Object>> reports = jdbcTemplate.queryForList(reportQuery);
////  	   System.out.println("check 4");
////  		 return reports;
////  	}
	
}
