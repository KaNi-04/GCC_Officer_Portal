package in.gov.chennaicorporation.gccoffice.pensioner.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@Service
public class DeptPartiallyCompService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    @Autowired
    private AppConfig appconfig;

    public DeptPartiallyCompService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Autowired
	 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
    
    @Autowired
    public GetFileStatusService getFileStatus;
    
    public List<Map<String, Object>> fetchDepatmentPartialEmpfiles(Integer deptId,Integer file_cat) {
        String query = "SELECT * " +
                       "FROM pensioner_details " +
                       "WHERE file_status=9 AND dept_id=? AND file_category=? AND is_closed=0";
     
        
        return jdbcTemplate.queryForList(query,deptId,file_cat);
    }
    
    @Transactional
    public boolean updateEntryCompletedDetails(Map<String, Object> requestData) {
        try {
            String empNo = (String) requestData.get("empNo");
            String tempId = (String) requestData.get("tempId");
            String dateToAudit = (String) requestData.get("dateToAudit");
            String filemovedby = (String) requestData.get("filemovedby");
            String remarks = (String) requestData.get("remarks");
            String dept_Id = (String) requestData.get("deptId");
            
            
            String gpf = (String) requestData.get("gpf");
            String private_affairs = (String) requestData.get("private_affairs");
            String spfgs = (String) requestData.get("spfgs");
            String recovery_emp = requestData.get("recovery_emp") != null ? requestData.get("recovery_emp").toString() : null;
            String dcrg = (String) requestData.get("dcrg");

            String gpfAmount = requestData.get("gpfAmount") != null ? requestData.get("gpfAmount").toString() : null;
            String private_affairs_amount = requestData.get("private_affairs_amount") != null ? requestData.get("private_affairs_amount").toString() : null;
            String spfgs_amount = requestData.get("spfgs_amount") != null ? requestData.get("spfgs_amount").toString() : null;
            
            
            String pension_number = (String) requestData.get("pension_number");
            String ledger_number = (String) requestData.get("ledger_number");
            String benefit_pension = (String) requestData.get("benefit_pension");
            String benefit_dcrg = (String) requestData.get("benefit_dcrg");
            String benefit_commutation = (String) requestData.get("benefit_commutation");
            
            String benefit_pension_amount = requestData.get("benefit_pension_amount")!= null ? requestData.get("benefit_pension_amount").toString() : null;
            String benefit_dcrg_amount = requestData.get("benefit_dcrg_amount") != null ? requestData.get("benefit_dcrg_amount").toString() : null;
            String benefit_commutation_amount = requestData.get("benefit_commutation_amount") != null ? requestData.get("benefit_commutation_amount").toString() : null;
            

            int deptId = Integer.parseInt(dept_Id);
            int filestatus;
            String file_entry_type="";
         
            
            
            String updateQuery1 = "UPDATE zone_benefits SET gpf = ?, gpf_amount = ?, spfgs = ?, spfgs_amount = ?, dcrg = ?, private_affairs = ?, private_affairs_amount = ?, recovery_emp = ?, updated_date = CURRENT_TIMESTAMP WHERE temp_id = ?";
            int rowsUpdated3 = jdbcTemplate.update(updateQuery1, gpf, gpfAmount, spfgs, spfgs_amount, dcrg, private_affairs, private_affairs_amount, recovery_emp, tempId);
            System.out.println("Rows updated in zb: " + rowsUpdated3);

            if (rowsUpdated3 > 0) {
            	
//            	String updateQuery2 = "UPDATE pension_benefits SET  pension_number = ?, ledger_number = ?, benefit_pension = ?, benefit_pension_amount = ?, benefit_dcrg = ?, benefit_dcrg_amount = ?, benefit_commutation = ?, benefit_commutation_amount = ?, updated_date = CURRENT_TIMESTAMP WHERE temp_id = ?";
//                int rowsUpdated2 = jdbcTemplate.update(updateQuery2, pension_number, ledger_number, benefit_pension, benefit_pension_amount, benefit_dcrg, benefit_dcrg_amount, benefit_commutation, benefit_commutation_amount, tempId);
//                System.out.println("Rows updated in pb: " + rowsUpdated2);
                
                filestatus = CheckFieldsforComplete(tempId) ?  getFileStatus.getFileStatus11Id() : CheckFieldsforDept(tempId) ?  getFileStatus.getFileStatus10Id() :  getFileStatus.getFileStatus9Id();
                System.out.println("filestatus inside service="+filestatus);
                
                if(filestatus==11)
                {
                	file_entry_type="FILE_COMPLETED";
                }
                if(filestatus==9) {
                	file_entry_type="DEPARTMENT_PARTIAL";
                }
                if(filestatus==10) {
                	file_entry_type="PENSION_PARTIAL";
                }
                
               
            	if (filestatus == 0) {
                    System.out.println("File status not found. Aborting insertion.");
                    return false;
                }
                              
                              	
                	String updateQuery = "UPDATE pensioner_details SET file_entry_type=?,file_status = ?, file_moved_date = ?, remarks = ?, updated_date = CURRENT_TIMESTAMP WHERE temp_id = ? LIMIT 1";
                    int rowsUpdated = jdbcTemplate.update(updateQuery,file_entry_type, filestatus, dateToAudit, remarks, tempId);
                    System.out.println("Rows updated in pd: " + rowsUpdated);
                    
	                    if (rowsUpdated > 0) {
	                    
	                    	String historyQuery = "INSERT INTO file_movement_history (emp_no, file_moved_date, file_status, file_moved_by, remarks, temp_id) VALUES (?, ?, ?, ?, ?, ?)";
	                        int rowsUpdated1 = jdbcTemplate.update(historyQuery, empNo, dateToAudit, filestatus, filemovedby, remarks, tempId);
	                        System.out.println("Rows updated in fmh: " + rowsUpdated1);
	                        
	                        return true;
	                    }
                
            }
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing department ID: " + e.getMessage());
            return false;
        } catch (DataAccessException e) {
            System.err.println("Database operation failed: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }
    
    @Transactional
    public boolean saveHistory(List<Map<String, Object>> empDetails, List<Map<String, Object>> zoneBenefits, List<Map<String, Object>> pension_details, String fileMovedBy) {
        try {
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
	        
	    	// Batch update for employee details
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
            
         // Batch update for pension benefits
//            String pensionInsertQuery = 
//                    "INSERT INTO pension_benefits_history (emp_no,temp_id,file_pending,pension_number,ledger_number,benefit_pension,benefit_pension_amount,benefit_dcrg,benefit_dcrg_amount,benefit_commutation,benefit_commutation_amount,file_moved_by) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
//            	
//            jdbcTemplate.batchUpdate(pensionInsertQuery, pension_details, pension_details.size(), (ps, pension) -> {
//            	ps.setString(1, pension.get("emp_no").toString());
//                ps.setString(2, pension.get("temp_id").toString());
//                ps.setString(3, pension.get("file_pending").toString());
//                ps.setString(4, pension.get("pension_number").toString());
//                ps.setString(5, pension.get("ledger_number").toString());
//                ps.setString(6, pension.get("benefit_pension").toString());
//                ps.setString(7, pension.get("benefit_pension_amount")  != null ? pension.get("benefit_pension_amount").toString() : null);
//                ps.setString(8, pension.get("benefit_dcrg").toString());
//                ps.setString(9, pension.get("benefit_dcrg_amount") != null ? pension.get("benefit_dcrg_amount").toString() : null);
//                ps.setString(10, pension.get("benefit_commutation").toString());                
//                ps.setString(11, pension.get("benefit_commutation_amount") != null ? pension.get("benefit_commutation_amount").toString() : null);
//                ps.setString(12, fileMovedBy);
//                
//            });

            return true; // All insertions succeeded
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false in case of any exception
        }
    }
    
    private boolean CheckFieldsforComplete(String tempId) {
		
    	// Define the SQL query
        String sql = "SELECT \r\n"
        		+ "    CASE \r\n"
        		+ "       WHEN (zb.gpf = 'yes' OR zb.gpf = '90') AND zb.spfgs = 'yes' AND zb.private_affairs = 'yes' AND zb.dcrg = 'yes' \r\n"
        		+ "         AND pb.benefit_pension = 'yes' AND pb.benefit_dcrg = 'yes'  THEN 1  ELSE 0  \r\n"
        		+ "    END AS allYes \r\n"
        		+ "FROM zone_benefits AS zb\r\n"
        		+ "JOIN pension_benefits AS pb ON zb.temp_id = pb.temp_id  \r\n"
        		+ "WHERE zb.temp_id = ? ";
              
    	
        try {
            // Use queryForObject to get a single boolean result
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, tempId);
            
            System.out.println("result============ "+result);
            
            return result != null && result; // Return the result if not null
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception if needed
        }
        
        return false; // Return false if an exception occurs
	}
    
    private boolean CheckFieldsforDept(String tempId) {
		
    	// Define the SQL query
        String sql = "SELECT \r\n"
        		+ "    CASE \r\n"
        		+ "    WHEN (zb.gpf = 'yes' OR zb.gpf = '90')  AND zb.spfgs = 'yes' AND zb.private_affairs = 'yes' AND zb.dcrg = 'yes' \r\n"
        		+ "    THEN 1  ELSE 0  \r\n"
        		+ "    END AS allYes\r\n"
        		+ "FROM zone_benefits AS zb\r\n"        		
        		+ "WHERE zb.temp_id = ?";
              
    	
        try {
            // Use queryForObject to get a single boolean result
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, tempId);
            
            return result != null && result; // Return the result if not null
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception if needed
        }
        
        return false; // Return false if an exception occurs
	}
}
