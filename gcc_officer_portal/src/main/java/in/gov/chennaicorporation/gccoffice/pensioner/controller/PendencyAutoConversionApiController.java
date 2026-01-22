package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import java.time.LocalDate;


import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.pensioner.service.DeptAuditService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.NewEntryService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.PendencyAutoConversionService;

@RequestMapping("gcc/api/pensioner/autoconversion")
@RestController
public class PendencyAutoConversionApiController {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	 @Autowired
	    public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	    }
	
//	@Autowired
//	private DeptAuditService deptAuditService;
//	
//	@Autowired
//	public PendencyAutoConversionService pendencyAutoConversionService;
//	
//	@GetMapping("/updateintopendency")
//	public boolean updateIfRetirementDateCrossed() {
//	    try {
//	        List<Map<String, Object>> covertList = pendencyAutoConversionService.getConvertingTempIds();
//	        System.out.println("covertList===" + covertList);
//
//	        if (!covertList.isEmpty()) {
//	            for (Map<String, Object> tempIdMap : covertList) {
//	                try {
//	                    // Extract temp_id and convert it to a String
//	                    Object tempIdRaw = tempIdMap.get("temp_id");
//	                    String tempId = tempIdRaw != null ? String.valueOf(tempIdRaw) : null;
//
//	                    System.out.println("Converted tempId: " + tempId);
//
//	                    // Ensure tempId is not null before processing
//	                    if (tempId != null && !tempId.trim().isEmpty()) {
//	                    	
//	                    	String fileMovedBy=pendencyAutoConversionService.getFileMovedByTempId(tempId);
//	                    	
//	                    	if(fileMovedBy!=null) {
//		                        boolean isProcessed = pendencyAutoConversionService.updateEntryDetails(tempId);
//	
//		                        if (isProcessed) {
//		                            List<Map<String, Object>> emp_details = deptAuditService.fetchEmployeeById(tempId);
//		                            System.out.println("emp_details: " + emp_details);
//	
//		                            boolean updated = pendencyAutoConversionService.saveHistory(emp_details,fileMovedBy);
//	
//		                            if (!updated) {
//		                                System.out.println("Failed to save history for tempId: " + tempId);
//		                                continue;
//		                            }
//		                        }
//	                    	}
//	                    	else {
//	                    		System.out.println("fileMovedBy is NUll for temp_id="+tempId);
//	                    	}
//	                        
//	                    }
//	                } catch (Exception innerEx) {
//	                    System.err.println("Error processing tempId: " + tempIdMap.get("temp_id"));
//	                    innerEx.printStackTrace();
//	                    continue; // Continue processing the next record
//	                }
//	            }
//	            return true;
//	        } else {
//	            System.out.println("No records found for processing.");
//	            return false;
//	        }
//	    } catch (Exception e) {
//	        System.err.println("Exception in updateIfRetirementDateCrossed: " + e.getMessage());
//	        e.printStackTrace();
//	        return false;
//	    }
//	}
	
	 @Scheduled(cron = "*/40 * * * * ?") // Runs every 40 seconds
	    public void scheduledPendencyUpdate() {
	        System.out.println("Scheduled Task Started: Updating Pensioner Pending Duration 40");
	        
	        Map<String, Object> result = GetRecordsUpdatePendencyDuration();
	        
	        System.out.println("Scheduled Task Completed: " + result);
	        
	        // Fetch the pending pensioners count after the update
	        int pendingCount = getPendingPensionersCount();
	        System.out.println("Total pending pensioners after update: " + pendingCount);
	    }

	    public Map<String, Object> GetRecordsUpdatePendencyDuration() {
	        Map<String, Object> response = new HashMap<>();

	        // Lists to store tempIds based on update success or failure
	        List<String> successfulUpdates = new ArrayList<>();
	        List<String> failedUpdates = new ArrayList<>();

	        String sqlQuery = "SELECT * FROM pensioner_details WHERE file_category=3 AND pending_duration='yes' AND file_status < 11";
	        List<Map<String, Object>> details = jdbcTemplate.queryForList(sqlQuery);

	        for (Map<String, Object> tempIdMap : details) {
	            String tempId = tempIdMap.get("temp_id") != null ? String.valueOf(tempIdMap.get("temp_id")) : null;
	            String retirementDateStr = tempIdMap.get("retirement_date") != null ? String.valueOf(tempIdMap.get("retirement_date")) : null;

	            if (retirementDateStr != null && !retirementDateStr.trim().isEmpty()) {
	                try {
	                    // Convert retirement_date to LocalDate
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                    LocalDate startDate = LocalDate.parse(retirementDateStr, formatter);
	                    LocalDate endDate = LocalDate.now(); // Current date

	                    System.out.printf("Processing Temp ID: %s | Start Date: %s | End Date: %s%n", tempId, startDate, endDate);

	                    // Calculate duration
	                    String updateDuration = calculatePendingDuration(startDate, endDate);
	                    System.out.println("Updated Duration: " + updateDuration);

	                    // Update the record in the database
	                    boolean isUpdated = updatePendingDuration(updateDuration, tempId);

	                    // Add tempId to the respective list
	                    if (isUpdated) {
	                        successfulUpdates.add(tempId);
	                    } else {
	                        failedUpdates.add(tempId);
	                    }

	                } catch (Exception e) {
	                    System.err.println("Error parsing retirement_date for tempId " + tempId + ": " + e.getMessage());
	                }
	            } else {
	                System.out.println("Skipping: Retirement date is null or empty for tempId: " + tempId);
	            }
	        }

	        response.put("status", "Success");
	        response.put("message", "Pendency duration updated");
	        response.put("total_records_processed", details.size());
	        response.put("successful_updates_count", successfulUpdates.size());
	        response.put("failed_updates_count", failedUpdates.size());
	        response.put("successful_updates", successfulUpdates);
	        response.put("failed_updates", failedUpdates);

	        return response;
	    }

	    public String calculatePendingDuration(LocalDate startDate, LocalDate endDate) {
	        Period period = Period.between(startDate, endDate);

	        int years = period.getYears();
	        int months = period.getMonths();
	        int days = period.getDays();

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

	    // New method to get the count of pending pensioners
	    public int getPendingPensionersCount() {
	        String sqlQuery = "SELECT COUNT(*) FROM pensioner_details WHERE file_category=3 AND pending_duration='yes' AND file_status < 11";
	        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
	        return count != null ? count : 0;
	    }



}
