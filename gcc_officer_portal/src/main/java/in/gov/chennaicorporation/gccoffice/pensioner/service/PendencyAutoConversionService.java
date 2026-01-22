package in.gov.chennaicorporation.gccoffice.pensioner.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@Service
@Transactional
public class PendencyAutoConversionService {

	 @Autowired
	    private JdbcTemplate jdbcTemplate;

	    @Autowired
	    private AppConfig appConfig;

	    @Autowired
	    public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	    }

	    /**
	     * Main method to process auto-conversion logic.
	     * - Fetch records where retirement date has crossed.
	     * - Update pensioner details.
	     * - Fetch employee details.
	     * - Save data into pensioner_details_history and file_movement_history.
	     */
	    @Transactional
	    public boolean processAutoConversion() {
	        try {
	            // Step 1: Fetch temp IDs that need processing
	            List<Map<String, Object>> tempList = getConvertingTempIds();
	            if (tempList.isEmpty()) {
	                System.out.println("No records found for auto-conversion.");
	                return false;
	            }

	            for (Map<String, Object> tempIdMap : tempList) {
	                String tempId = tempIdMap.get("temp_id") != null ? String.valueOf(tempIdMap.get("temp_id")) : null;
	                if (tempId == null || tempId.trim().isEmpty()) {
	                    System.out.println("Skipping null/empty temp_id.");
	                    continue;
	                }

	                System.out.println("Processing tempId: " + tempId);

	                // Step 2: Get file_moved_by value
	                String fileMovedBy = getFileMovedByTempId(tempId);
	                if (fileMovedBy == null) {
	                    System.out.println("Skipping tempId " + tempId + " as fileMovedBy is NULL.");
	                    continue;
	                }

	                // Step 3: Update pensioner_details table
	                boolean updated = updateEntryDetails(tempId);
	                if (!updated) {
	                    System.out.println("Failed to update pensioner_details for tempId: " + tempId);
	                    continue;
	                }

	                // Step 4: Fetch employee details
	                List<Map<String, Object>> empDetails = fetchEmployeeDetails(tempId);
	                if (empDetails.isEmpty()) {
	                    System.out.println("No employee details found for tempId: " + tempId);
	                    continue;
	                }

	                // Step 5: Save history data
	                boolean historySaved = saveHistory(empDetails, fileMovedBy);
	                if (!historySaved) {
	                    System.out.println("Failed to save history for tempId: " + tempId);
	                }
	            }

	            System.out.println("Auto-conversion process completed.");
	            return true;
	        } catch (Exception e) {
	            System.err.println("Exception in processAutoConversion: " + e.getMessage());
	            e.printStackTrace();
	            return false;
	        }
	    }

	    public List<Map<String, Object>> getConvertingTempIds() {
	        String sql = "SELECT temp_id FROM pensioner_details WHERE file_category=1 AND file_status <=8 AND retirement_date <= CURDATE() - 1";
	        return jdbcTemplate.queryForList(sql);
	    }

	    private boolean updateEntryDetails(String tempId) {
	        try {
	            LocalDate datefield = LocalDate.now();
	            String formattedDate = datefield.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	            String remarks = "Auto_Conversion";
	            int fileCategory = 3;
	            String pendingDuration = "yes";
	            String reasonForPending = "Retirement Date Crossed while file in Processing";

	            String updateQuery = "UPDATE pensioner_details SET reason_for_pending=?, pending_duration=?, file_category=?, file_moved_date=?, remarks=?, updated_date=CURRENT_TIMESTAMP WHERE temp_id=?";
	            int rowsUpdated = jdbcTemplate.update(updateQuery, reasonForPending, pendingDuration, fileCategory, formattedDate, remarks, tempId);

	            System.out.println("Updated pensioner_details for tempId: " + tempId + " - Rows affected: " + rowsUpdated);
	            return rowsUpdated > 0;
	        } catch (Exception e) {
	            System.err.println("Exception in updateEntryDetails: " + e.getMessage());
	            return false;
	        }
	    }

	    public List<Map<String, Object>> fetchEmployeeDetails(String tempId) {
	        String sql = "SELECT * FROM pensioner_details WHERE temp_id=?";
	        return jdbcTemplate.queryForList(sql, tempId);
	    }

	    
	    @Transactional
	    public boolean saveHistory(List<Map<String, Object>> empDetails, String fileMovedBy) {
	        try {
	            String empInsertQuery = "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, type_of_retirement, " +
	                    "retirement_date, retirement_class, file_moved_date, remarks, file_category, file_status, " +
	                    "file_entry_type, file_moved_by, dept_id, file_category_name, temp_id, pending_duration, reason_for_pending) " +
	                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	            jdbcTemplate.batchUpdate(empInsertQuery, empDetails, empDetails.size(), (ps, emp) -> {
	                ps.setString(1, emp.get("emp_no").toString());
	                ps.setString(2, emp.get("emp_name").toString());
	                ps.setString(3, emp.get("dept_name").toString());
	                ps.setString(4, emp.get("designation").toString());
	                ps.setString(5, emp.get("type_of_retirement").toString());
	                ps.setString(6, emp.get("retirement_date").toString());
	                ps.setString(7, emp.get("retirement_class").toString());
	                ps.setString(8, emp.get("file_moved_date").toString());
	                ps.setString(9, emp.get("remarks").toString());
	                ps.setInt(10, Integer.parseInt(emp.get("file_category").toString()));
	                ps.setInt(11, Integer.parseInt(emp.get("file_status").toString()));
	                ps.setString(12, emp.get("file_entry_type").toString());
	                ps.setString(13, fileMovedBy != null ? fileMovedBy : "");
	                ps.setInt(14, Integer.parseInt(emp.get("dept_id").toString()));
	                ps.setString(15, emp.get("file_category_name").toString());
	                ps.setString(16, emp.get("temp_id").toString());
	                ps.setString(17, emp.get("pending_duration") != null ? emp.get("pending_duration").toString() : null);
	                ps.setString(18, emp.get("reason_for_pending") != null ? emp.get("reason_for_pending").toString() : null);
	            });

	            String historyQuery = "INSERT INTO file_movement_history (emp_no, file_moved_date, file_status, file_moved_by, remarks, temp_id) VALUES (?, ?, ?, ?, ?, ?)";

	            jdbcTemplate.batchUpdate(historyQuery, empDetails, empDetails.size(), (ps, his) -> {
	                ps.setString(1, his.get("emp_no").toString());
	                ps.setString(2, his.get("file_moved_date").toString());
	                ps.setString(3, his.get("file_status").toString());
	                ps.setString(4, fileMovedBy != null ? fileMovedBy : "");
	                ps.setString(5, his.get("remarks").toString());
	                ps.setString(6, his.get("temp_id").toString());
	            });

	            System.out.println("Successfully saved history for " + empDetails.size() + " records.");
	            return true;
	        } catch (Exception e) {
	            System.err.println("Exception in saveHistory: " + e.getMessage());
	            return false;
	        }
	    }

	    public String getFileMovedByTempId(String tempId) {
	        String sql = "SELECT file_moved_by FROM pensioner_details_history WHERE temp_id=? ORDER BY created_date DESC LIMIT 1";
	        return jdbcTemplate.queryForObject(sql, String.class, tempId);
	    }

	    
		public boolean processAutoConversionForTempId(String tempId) {
	    try {
	        System.out.println("Running auto-conversion for tempId: " + tempId);

	        // Step 1: Get fileMovedBy
	        String fileMovedBy = getFileMovedByTempId(tempId);
	        if (fileMovedBy == null) {
	            System.out.println("Skipping tempId " + tempId + " as fileMovedBy is NULL.");
	            return false;
	        }

	        // Step 2: Update pensioner details
	        boolean updated = updateEntryDetails(tempId);
	        if (!updated) {
	            System.out.println("Failed to update pensioner_details for tempId: " + tempId);
	            return false;
	        }

	        // Step 3: Fetch employee details
	        List<Map<String, Object>> empDetails = fetchEmployeeDetails(tempId);
	        if (empDetails.isEmpty()) {
	            System.out.println("No employee details found for tempId: " + tempId);
	            return false;
	        }

	        // Step 4: Save history
	        boolean historySaved = saveHistory(empDetails, fileMovedBy);
	        if (!historySaved) {
	            System.out.println("Failed to save history for tempId: " + tempId);
	            return false;
	        }

	        System.out.println("Successfully processed tempId: " + tempId);
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error processing tempId: " + tempId + " - " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}
		
		public List<Map<String, Object>> getPendencyUpdatingDuration(){
			
			String sqlQuery = "SELECT * FROM pensioner_details WHERE file_category IN (2,3,5) AND (pending_duration = 'yes' OR pending_duration = 'Yes') AND file_status < 11";
		    List<Map<String, Object>> details = jdbcTemplate.queryForList(sqlQuery);
		    
		    return details;
	    
		}
		
		public List<Map<String, Object>> getProvisionalNoPendingDuration(){
			
			String sqlQuery = "SELECT * FROM pensioner_details WHERE file_category IN (5) AND (pending_duration = 'no' OR pending_duration = 'No') AND file_status < 11 AND retirement_date < curdate()";
		    List<Map<String, Object>> prdetails = jdbcTemplate.queryForList(sqlQuery);
		    
		    return prdetails;
	    
		}
		
		public String calculatePendingDuration(LocalDate startDate, LocalDate endDate) {
	        Period period = Period.between(startDate, endDate);

	        int years = period.getYears();
	        int months = period.getMonths();
	        int days = period.getDays();
	        
	        if (years == 0 && months == 0 && days == 0) {
	            return "0 days"; // or "Same day"
	        }

	        return (years > 0 ? years + " year " : "") +
	               (months > 0 ? months + " month " : "") +
	               (days > 0 ? days + " day" : "");
	    }

	    public boolean updatePendingDuration(String updateDuration, String tempId) {
	        String sqlQuery = "UPDATE pensioner_details SET reason_for_pending=? WHERE temp_id=?";
	        int rowsUpdated = jdbcTemplate.update(sqlQuery, updateDuration, tempId);
	        System.out.println("Temp ID: " + tempId + " | Rows Updated: " + rowsUpdated);
	        return rowsUpdated > 0;
	    }
	    
	    public boolean updatePendingDurationforPR(String updateDuration1, String tempId1) {
	        String sqlQuery = "UPDATE pensioner_details SET pending_duration='Yes',reason_for_pending=? WHERE temp_id=?";
	        int rowsUpdated = jdbcTemplate.update(sqlQuery, updateDuration1, tempId1);
	        System.out.println("Updating for PR No Temp ID: " + tempId1 + " | Rows Updated: " + rowsUpdated);
	        return rowsUpdated > 0;
	    }

	    // New method to get the count of pending pensioners
	    public int getPendingPensionersCount() {
	        String sqlQuery = "SELECT COUNT(*) FROM pensioner_details WHERE file_category=3 AND pending_duration='yes' AND file_status < 11";
	        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
	        return count != null ? count : 0;
	    }
	

}
