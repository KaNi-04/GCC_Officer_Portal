package in.gov.chennaicorporation.gccoffice.pensioner.scheduler;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import in.gov.chennaicorporation.gccoffice.pensioner.service.PendencyAutoConversionService;
import in.gov.chennaicorporation.gccoffice.pensioner.service.ReceivedDeptProvisionalService;

@Component
public class PendencyAutoConversionScheduler {
	

	 @Autowired
	    private PendencyAutoConversionService pendencyAutoConversionService;
	    
	    @Autowired
		public ReceivedDeptProvisionalService ReceivedDeptProvisionalservice;

	    /**
	     * Scheduled method that runs every day 3.00am and 4.00 am to process auto-conversion tasks.
	     */
	    
	    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Kolkata")// Runs every 3.00 am 
	    public void executeAutoConversion() {
	        try {
	            System.out.println(" Running scheduled auto-conversion task... at 3.00 am");

	            // Step 1: Fetch temp IDs that need processing
	            List<Map<String, Object>> tempIds = pendencyAutoConversionService.getConvertingTempIds();
	            
	            if (tempIds == null || tempIds.isEmpty()) {
	                System.out.println(" No records found for auto-conversion.");
	                return;
	            }

	            System.out.println(" Found " + tempIds.size() + " records for processing.");

	            // Step 2: Process each tempId
	            for (Map<String, Object> tempIdMap : tempIds) {
	                String tempId = tempIdMap.get("temp_id") != null ? tempIdMap.get("temp_id").toString() : null;

	                if (tempId == null || tempId.trim().isEmpty()) {
	                    System.out.println(" Skipping empty temp_id.");
	                    continue;
	                }

	                System.out.println(" Processing tempId: " + tempId);

	                // Step 3: Call service method to process auto-conversion
	                boolean success = pendencyAutoConversionService.processAutoConversionForTempId(tempId);

	                if (success) {
	                    System.out.println(" Auto-conversion completed for tempId: " + tempId);
	                } else {
	                    System.out.println(" Auto-conversion failed for tempId: " + tempId);
	                }
	            }
	        } catch (Exception e) {
	            System.err.println(" Error during auto-conversion task: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	    
	    
	    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Kolkata") // Runs every day 4.00am 
	    public void scheduledPendencyUpdate() {
	        System.out.println("Scheduled Task Started 4.00 am: Updating Pensioner Pending Duration");
	        
	        Map<String, Object> result = GetRecordsUpdatePendencyDuration();
	        
	        System.out.println("Scheduled Task Completed: " + result);
	        
	        // Fetch the pending pensioners count after the update
	        int pendingCount = pendencyAutoConversionService.getPendingPensionersCount();
	        System.out.println("Total pending pensioners after update: " + pendingCount);
	    }

	    public Map<String, Object> GetRecordsUpdatePendencyDuration() {
	        Map<String, Object> response = new HashMap<>();

	        // Lists to store tempIds based on update success or failure
	        List<String> successfulUpdates = new ArrayList<>();
	        List<String> failedUpdates = new ArrayList<>();

	        List<Map<String, Object>> details = pendencyAutoConversionService.getPendencyUpdatingDuration();
	        
	        List<Map<String, Object>> prdetails = pendencyAutoConversionService.getProvisionalNoPendingDuration();
	        if(!prdetails.isEmpty()) {
	        	
	        	for (Map<String, Object> prdetail : prdetails) {
	        		String tempId1 = prdetail.get("temp_id") != null ? String.valueOf(prdetail.get("temp_id")) : null;
	                String retirementDateStr1 = prdetail.get("retirement_date") != null ? String.valueOf(prdetail.get("retirement_date")) : null;
	                
	                try {
	                    // Convert retirement_date to LocalDate
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                    LocalDate startDate1 = LocalDate.parse(retirementDateStr1, formatter);
	                    LocalDate endDate1 = LocalDate.now();
	                    
	                    System.out.printf("Processing Temp ID: %s | Start Date: %s | End Date: %s%n", tempId1, startDate1, endDate1);
	                    
	                    String updateDuration1 = pendencyAutoConversionService.calculatePendingDuration(startDate1, endDate1);
	                    System.out.println("Updated Duration: " + updateDuration1);
	                    
	                    boolean isUpdated1 = pendencyAutoConversionService.updatePendingDurationforPR(updateDuration1, tempId1);
	                    
	                    if (isUpdated1) {
	                        successfulUpdates.add(tempId1);
	                        String filemovedby=pendencyAutoConversionService.getFileMovedByTempId(tempId1);
	                        List<Map<String, Object>> empDetails=pendencyAutoConversionService.fetchEmployeeDetails(tempId1);
	                        boolean historySaved = ReceivedDeptProvisionalservice.savehistory(empDetails, filemovedby);
	                        if (!historySaved) {
	                            System.out.println("Failed to save prno history for tempId: " + tempId1);
	                        }
	                    } else {
	                        failedUpdates.add(tempId1);
	                    }

	                }
	                catch (Exception e) {
	                    System.err.println("Error parsing retirement_date for tempId " + tempId1 + ": " + e.getMessage());
	                }
	        	}
	        	
	        }
	        if(!details.isEmpty()) {

	        for (Map<String, Object> tempIdMap : details) {
	            String tempId = tempIdMap.get("temp_id") != null ? String.valueOf(tempIdMap.get("temp_id")) : null;
	            String retirementDateStr = tempIdMap.get("retirement_date") != null ? String.valueOf(tempIdMap.get("retirement_date")) : null;
	            String serviceDeathDate = tempIdMap.get("service_death") != null ? String.valueOf(tempIdMap.get("service_death")) : null;
	         // Corrected parsing for file_category
	            int fileCat = tempIdMap.get("file_category") != null ? Integer.parseInt(tempIdMap.get("file_category").toString()) : null;
	            
	            if(fileCat==3 || fileCat==5) {
	            	
	            	if (retirementDateStr != null && !retirementDateStr.trim().isEmpty()) {
	                    try {
	                        // Convert retirement_date to LocalDate
	                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                        LocalDate startDate = LocalDate.parse(retirementDateStr, formatter);
	                        LocalDate endDate = LocalDate.now(); // Current date

	                        System.out.printf("Processing Temp ID: %s | Start Date: %s | End Date: %s%n", tempId, startDate, endDate);

	                        // Calculate duration
	                        String updateDuration = pendencyAutoConversionService.calculatePendingDuration(startDate, endDate);
	                        System.out.println("Updated Duration: " + updateDuration);

	                        // Update the record in the database
	                        boolean isUpdated = pendencyAutoConversionService.updatePendingDuration(updateDuration, tempId);

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
	            
	            
	            if(fileCat==2) {
	            	
	            	if (serviceDeathDate != null && !serviceDeathDate.trim().isEmpty()) {
	                    try {
	                        // Convert retirement_date to LocalDate
	                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                        LocalDate startDate = LocalDate.parse(serviceDeathDate, formatter);
	                        LocalDate endDate = LocalDate.now(); // Current date

	                        System.out.printf("Processing Temp ID: %s | Start Date: %s | End Date: %s%n", tempId, startDate, endDate);

	                        // Calculate duration
	                        String updateDuration = pendencyAutoConversionService.calculatePendingDuration(startDate, endDate);
	                        System.out.println("Updated Duration: " + updateDuration);

	                        // Update the record in the database
	                        boolean isUpdated = pendencyAutoConversionService.updatePendingDuration(updateDuration, tempId);

	                        // Add tempId to the respective list
	                        if (isUpdated) {
	                            successfulUpdates.add(tempId);
	                        } else {
	                            failedUpdates.add(tempId);
	                        }

	                    } catch (Exception e) {
	                        System.err.println("Error parsing service death date for tempId " + tempId + ": " + e.getMessage());
	                    }
	                } else {
	                    System.out.println("Skipping: service death date is null or empty for tempId: " + tempId);
	                }
	            }
	                       
	        }
	        response.put("status", "Success");
	        response.put("message", "Pendency duration updated");
	        response.put("total_records_processed", details.size());
	        response.put("successful_updates_count", successfulUpdates.size());
	        response.put("failed_updates_count", failedUpdates.size());
	        response.put("successful_updates", successfulUpdates);
	        response.put("failed_updates", failedUpdates);
	        
	        }

	        response.put("status", "No Data");
	        response.put("message", "No Updation in Pending Duration");

	        return response;
	    }

    
}
