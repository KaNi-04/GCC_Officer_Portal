package in.gov.chennaicorporation.gccoffice.pensioner.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@Service
@Transactional
public class PensionReceivedService {

	@Autowired
    private JdbcTemplate jdbcTemplate;
 
 
   private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    @Autowired
    private AppConfig appconfig;

    public PensionReceivedService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Autowired
	 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
    
    @Autowired
    public GetFileStatusService getFileStatus;
    
    public List<Map<String, Object>> fetchReceivedDepartmentEmpfiles(Integer deptId,Integer file_cat) {
    	String query = "SELECT * " +
                "FROM pensioner_details " +
                "WHERE dept_id=? AND file_category=? AND (file_status=5 OR file_status=6) AND is_closed=0";     
        
        return jdbcTemplate.queryForList(query,deptId,file_cat);
    }
    
    
    public List<Map<String, Object>> fetchZonebenefitsById(String tempId) {
    	String query = "SELECT * " +
                "FROM zone_benefits " +
                "WHERE temp_id=?";


    	return jdbcTemplate.queryForList(query,tempId);
	}
    
    public List<Map<String, Object>> fetchPensionbenefitsById(String tempId) {
    	String query = "SELECT * " +
                "FROM pension_benefits " +
                "WHERE temp_id=?";


    	return jdbcTemplate.queryForList(query,tempId);
	}
    
    public boolean updateEntryDetails(Map<String, Object> requestData) {
        try {
            String empNo = (String) requestData.get("empNo");
            String tempId = (String) requestData.get("tempId");

            String dateToAudit = (String) requestData.get("file_moved_date");
            String filemovedby = (String) requestData.get("filemovedby");
            String remarks = (String) requestData.get("remarks");
            String dept_Id = (String) requestData.get("deptId");

            int deptId = (dept_Id != null && !dept_Id.isEmpty()) ? Integer.parseInt(dept_Id) : 0;
            int filestatus = getFileStatus.getFileStatus7Id();
            if (filestatus == 0) {
                System.out.println("File status not found. Aborting insertion.");
                return false;
            }

            String gpf = (String) requestData.get("gpf");
            String private_affairs = (String) requestData.get("private_affairs");
            String spfgs = (String) requestData.get("spfgs");
            String recovery_emp = (String) requestData.get("recovery_emp");
            String dcrg = (String) requestData.get("dcrg");

            String gpfAmount = requestData.get("gpfAmount") != null ? requestData.get("gpfAmount").toString() : null;
            String private_affairs_amount = requestData.get("private_affairs_amount") != null ? requestData.get("private_affairs_amount").toString() : null;
            String spfgs_amount = requestData.get("spfgs_amount") != null ? requestData.get("spfgs_amount").toString() : null;

            boolean check = checkTempIdInZoneBenefits(tempId);

            // Update file_status in pensioner_details
            String updateQuery = "UPDATE pensioner_details SET file_status = ?, file_moved_date = ?, remarks = ?, updated_date = CURRENT_TIMESTAMP WHERE temp_id = ?";
            int rowsUpdated = jdbcTemplate.update(updateQuery, filestatus, dateToAudit, remarks, tempId);
            System.out.println("Rows updated in pensioner_details: " + rowsUpdated);

            if (rowsUpdated > 0) {
                // Insert into file movement history
                String historyQuery = "INSERT INTO file_movement_history (emp_no, file_moved_date, file_status, file_moved_by, remarks, temp_id) VALUES (?, ?, ?, ?, ?, ?)";
                int rowsUpdated1 = jdbcTemplate.update(historyQuery, empNo, dateToAudit, filestatus, filemovedby, remarks, tempId);
                System.out.println("Rows inserted in file_movement_history: " + rowsUpdated1);

                if (rowsUpdated1 > 0) {
                    if (check) {
                        // Update zone benefits
                        String updateQuery1 = "UPDATE zone_benefits SET gpf = ?, gpf_amount = ?, spfgs = ?, spfgs_amount = ?, dcrg = ?, private_affairs = ?, private_affairs_amount = ?, recovery_emp = ?, updated_date = CURRENT_TIMESTAMP WHERE temp_id = ?";
                        int rowsUpdated3 = jdbcTemplate.update(updateQuery1, gpf, gpfAmount, spfgs, spfgs_amount, dcrg, private_affairs, private_affairs_amount, recovery_emp, tempId);
                        System.out.println("Rows updated in zone_benefits: " + rowsUpdated3);
                    } else {
                        // Insert into zone benefits
                        String benefitQuery = "INSERT INTO zone_benefits (emp_no, temp_id, gpf, gpf_amount, spfgs, spfgs_amount, dcrg, private_affairs, private_affairs_amount, recovery_emp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        int rowsUpdated2 = jdbcTemplate.update(benefitQuery, empNo, tempId, gpf, gpfAmount, spfgs, spfgs_amount, dcrg, private_affairs, private_affairs_amount, recovery_emp);
                        System.out.println("Rows inserted in zone_benefits: " + rowsUpdated2);
                    }
                    return true;
                } else {
                    System.out.println("Failed to insert into file_movement_history");
                    return false;
                }
            } else {
                System.out.println("Failed to update pensioner_details");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Exception in updateEntryDetails: " + e.getMessage());
            return false;
        }
    }

    
    
    public boolean saveHistory(List<Map<String, Object>> empDetails, List<Map<String, Object>> zoneBenefits, String fileMovedBy) {
        try {
            // Batch update for employee details
        	int file_Cat=0;
        	
        	if (!empDetails.isEmpty()) {
                Map<String, Object> firstEmployeeDetails = empDetails.get(0);
                Object fileCatObj = firstEmployeeDetails.get("file_category");

                if (fileCatObj != null) {
                    try {
                    	file_Cat = Integer.parseInt(fileCatObj.toString());
                        System.out.println("Parsed file_category: " + file_Cat);
                    } catch (NumberFormatException e) {
                        System.err.println("Failed to parse file_category: " + fileCatObj);
                    }
                } else {
                    System.out.println("file_category is null in emp_details.");
                }
            } else {
                System.out.println("emp_details is empty.");
            }
        	
        	
        	if(file_Cat==1) {
            String empInsertQuery = 
                "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, type_of_retirement, " +
                "retirement_date, retirement_class, file_moved_date, remarks, file_category, file_status, " +
                "file_entry_type, file_moved_by, dept_id, file_category_name, temp_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                ps.setString(13, fileMovedBy);
                ps.setInt(14, Integer.parseInt(emp.get("dept_id").toString()));
                ps.setString(15, emp.get("file_category_name").toString());
                ps.setString(16, emp.get("temp_id").toString());
            });
            
            System.out.println("rows inserted in pd history for retirement");
            
        	}
        	
        	if(file_Cat==2)
        	{
        		String empInsertQuery1 = 
                        "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, service_death, " +
                        "relationship_deceased, sanctioned_gis_amount, file_moved_date, remarks, file_category, file_status, " +
                        "file_entry_type, file_moved_by, dept_id, file_category_name, temp_id,pending_duration,reason_for_pending) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

                    jdbcTemplate.batchUpdate(empInsertQuery1, empDetails, empDetails.size(), (ps, emp) -> {
                        ps.setString(1, emp.get("emp_no").toString());
                        ps.setString(2, emp.get("emp_name").toString());
                        ps.setString(3, emp.get("dept_name").toString());
                        ps.setString(4, emp.get("designation").toString());
                        ps.setString(5, emp.get("service_death").toString());
                        ps.setString(6, emp.get("relationship_deceased").toString());
                        ps.setString(7, emp.get("sanctioned_gis_amount").toString());
                        ps.setString(8, emp.get("file_moved_date").toString());
                        ps.setString(9, emp.get("remarks").toString());
                        ps.setInt(10, Integer.parseInt(emp.get("file_category").toString()));
                        ps.setInt(11, Integer.parseInt(emp.get("file_status").toString()));
                        ps.setString(12, emp.get("file_entry_type").toString());
                        ps.setString(13, fileMovedBy);
                        ps.setInt(14, Integer.parseInt(emp.get("dept_id").toString()));
                        ps.setString(15, emp.get("file_category_name").toString());
                        ps.setString(16, emp.get("temp_id").toString());
                        ps.setString(17, emp.get("pending_duration").toString());
                        ps.setString(18, emp.get("reason_for_pending") != null ? emp.get("reason_for_pending").toString() : null);
                    });
                    
                    System.out.println("rows inserted in pd history for family Pension");
        	}
        	
        	if(file_Cat==3)
        	{
        		String empInsertQuery2 = 
                        "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation,type_of_retirement, " +
                        "file_moved_date, remarks, file_category, file_status, " +
                        "file_entry_type, file_moved_by, dept_id, file_category_name, temp_id,pending_duration,reason_for_pending,retirement_class,retirement_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

                    jdbcTemplate.batchUpdate(empInsertQuery2, empDetails, empDetails.size(), (ps, emp) -> {
                        ps.setString(1, emp.get("emp_no").toString());
                        ps.setString(2, emp.get("emp_name").toString());
                        ps.setString(3, emp.get("dept_name").toString());
                        ps.setString(4, emp.get("designation").toString());                        
                        ps.setString(5, emp.get("type_of_retirement").toString());
                        ps.setString(6, emp.get("file_moved_date").toString());
                        ps.setString(7, emp.get("remarks").toString());
                        ps.setInt(8, Integer.parseInt(emp.get("file_category").toString()));
                        ps.setInt(9, Integer.parseInt(emp.get("file_status").toString()));
                        ps.setString(10, emp.get("file_entry_type").toString());
                        ps.setString(11, fileMovedBy);
                        ps.setInt(12, Integer.parseInt(emp.get("dept_id").toString()));
                        ps.setString(13, emp.get("file_category_name").toString());
                        ps.setString(14, emp.get("temp_id").toString());
                        ps.setString(15, emp.get("pending_duration").toString());
                        ps.setString(16, emp.get("reason_for_pending") != null ? emp.get("reason_for_pending").toString() : null);
                        ps.setString(17, emp.get("retirement_class").toString());
                        ps.setString(18, emp.get("retirement_date").toString());
                    });
                    
                    System.out.println("rows inserted in pd history for pendency");
        	}

            // Batch update for zone benefits
            String zoneInsertQuery = 
                "INSERT INTO zone_benefits_history (emp_no, temp_id, gpf, gpf_amount, spfgs, spfgs_amount, dcrg, " +
                "private_affairs, private_affairs_amount, recovery_emp, file_moved_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.batchUpdate(zoneInsertQuery, zoneBenefits, zoneBenefits.size(), (ps, zone) -> {
                ps.setString(1, zone.get("emp_no").toString());
                ps.setString(2, zone.get("temp_id").toString());
                ps.setString(3, zone.get("gpf").toString());
                ps.setString(4, zone.get("gpf_amount") != null ? zone.get("gpf_amount").toString() : null);
                ps.setString(5, zone.get("spfgs").toString());
                ps.setString(6, zone.get("spfgs_amount") != null ? zone.get("spfgs_amount").toString() : null);
                ps.setString(7, zone.get("dcrg").toString());
                ps.setString(8, zone.get("private_affairs").toString());
                ps.setString(9, zone.get("private_affairs_amount") != null ? zone.get("private_affairs_amount").toString() : null);
                ps.setString(10, zone.get("recovery_emp") != null ? zone.get("recovery_emp").toString() : null);
                ps.setString(11, fileMovedBy);
            });
            
            System.out.println("rows inserted in zone history for family Pension");

            return true; // All insertions succeeded
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false in case of any exception
        }
    }
    
   /* 
    */


public boolean checkTempIdInZoneBenefits(String tempId) {
        String query = "SELECT EXISTS (SELECT 1 FROM zone_benefits WHERE temp_id = ?)";
        
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, Boolean.class, tempId));
    }
    
    

  //gis
    
    public boolean updateEntryGisDetails(Map<String, Object> requestData) {
		// TODO Auto-generated method stub
		String empNo = (String) requestData.get("empNo");
		String tempId = (String) requestData.get("tempId");
    	String dateToAudit = (String) requestData.get("dateToAudit");
        String filemovedby =(String)requestData.get("filemovedby");
        String remarks = (String) requestData.get("remarks");
        String dept_Id = (String) requestData.get("deptId"); 
        String sanctional_gis_date = (String) requestData.get("sanctional_gis_date");
        String sanctional_gis_amount = (String) requestData.get("sanctional_gis_amount");
        String lic_date = (String) requestData.get("lic_date");
        
        
        int deptId = Integer.parseInt(dept_Id);
        int filestatus = getFileStatus.getFileStatus11Id();
        if (filestatus == 0) {
            System.out.println("File status not found. Aborting insertion.");
            return false;
        }
        String fileentrytype = "FILE_COMPLETED";
        
        String updateQuery = "UPDATE pensioner_details SET file_status = ?, file_moved_date = ?, remarks = ?, sanctional_gis_date = ?, sanctional_gis_amount = ?, lic_date = ?,file_entry_type=? ,updated_date = CURRENT_TIMESTAMP WHERE temp_id = ? LIMIT 1";
        int rowsUpdated = jdbcTemplate.update(updateQuery, filestatus, dateToAudit, remarks, sanctional_gis_date, sanctional_gis_amount, lic_date,fileentrytype ,tempId);

        //int rowsUpdated = jdbcTemplate.update(updateQuery,filestatus,dateToAudit, remarks,sanctional_gis_date,sanctional_gis_amount,lic_date,tempId);
        System.out.println(dateToAudit);    
        System.out.println(sanctional_gis_date);    
        System.out.println(sanctional_gis_amount);
        System.out.println(lic_date);
        System.out.println("Rows inserted in pd:"+rowsUpdated);
		if(rowsUpdated>0)
		{
			// Optional: Insert into file movement history for auditing
            String historyQuery = "INSERT INTO file_movement_history (emp_no, file_moved_date, file_status, file_moved_by, remarks,temp_id) VALUES (?,?, ?, ?, ?, ?)";
            int rowsUpdated1=jdbcTemplate.update(historyQuery, empNo, dateToAudit, filestatus, filemovedby, remarks,tempId);
            System.out.println("Rows inserted in fmh:"+rowsUpdated1);
            
            return true;
		}
		else {
			return false;
		}
					
	}
    
    public boolean savehistoryGis(List<Map<String, Object>> emp_details,String filemovedby) {
	    try {
	        // Loop through the list of employee details
	        for (Map<String, Object> emp : emp_details) {
	            // Extract individual fields from the map
	            String empNo = emp.get("emp_no").toString();
	            String empName = emp.get("emp_name").toString();
	            String deptName = emp.get("dept_name").toString();
	            String designation = emp.get("designation").toString();
	            String fileCategoryName = emp.get("file_category_name").toString();		            
	            String fileMovedDate = emp.get("file_moved_date").toString();
	            String remarks = emp.get("remarks").toString();		            
	            int fileStatus = Integer.parseInt(emp.get("file_status").toString());		            
	            String fileEntryType = emp.get("file_entry_type").toString();		            
	            int file_category = Integer.parseInt(emp.get("file_category").toString());
	            int dept_id = Integer.parseInt(emp.get("dept_id").toString());
	            String sanctional_gis_date=emp.get("sanctional_gis_date").toString();
	            String sanctional_gis_amount=emp.get("sanctional_gis_amount").toString();
	            String lic_date=emp.get("lic_date").toString();
	            String temp_id = emp.get("temp_id").toString();

	            // Prepare the insert query
	            String insertQuery = "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, file_moved_date, remarks, file_category, file_status, file_entry_type, file_moved_by, dept_id, file_category_name, temp_id, sanctional_gis_date, sanctional_gis_amount, lic_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


	            // Execute the query
	            int rowsInserted = jdbcTemplate.update(insertQuery, empNo, empName, deptName,designation,fileMovedDate,remarks,file_category,fileStatus,fileEntryType,filemovedby,dept_id,fileCategoryName,temp_id,sanctional_gis_date,sanctional_gis_amount,lic_date);
	            System.out.println("Rows inserted in pdh:"+rowsInserted);
	            // Check if the insertion was successful
	            if (rowsInserted <= 0) {
	                return false; // Return false if any insertion fails
	            }
	        }

	        return true; // Return true if all insertions succeed
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false; // Return false in case of any exception
	    }
	}

}
