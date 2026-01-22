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
public class DepartmentReceivedService {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
 
 
   private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    @Autowired
    private AppConfig appconfig;

    public DepartmentReceivedService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Autowired
	 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
    
    @Autowired
    public GetFileStatusService getFileStatus;
    
    public List<Map<String, Object>> fetchPensionReceivedEmpfiles(Integer file_cat) {
        String query = "SELECT * " +
                       "FROM pensioner_details " +
                       "WHERE file_category=? AND (file_status=7 OR file_status=8) AND is_closed=0";
     
        
        return jdbcTemplate.queryForList(query,file_cat);
    }
    
    public List<Map<String, Object>> fetchPensionbenefitsById(String tempId) {
    	String query = "SELECT * " +
                "FROM pension_benefits " +
                "WHERE temp_id=?";

 
    	return jdbcTemplate.queryForList(query,tempId);
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
            
            String file_pending = (String) requestData.get("file_pending");
            String pension_number = (String) requestData.get("pension_number");
            String ledger_number = (String) requestData.get("ledger_number");
            String benefit_pension = (String) requestData.get("benefit_pension");
            String benefit_dcrg = requestData.get("benefit_dcrg") != null ? requestData.get("benefit_dcrg").toString() : null;
            String benefit_commutation = (String) requestData.get("benefit_commutation");
            
            String benefit_pension_amount = (String) requestData.get("benefit_pension_amount")!= null ? requestData.get("benefit_pension_amount").toString() : null;
            String benefit_dcrg_amount = requestData.get("benefit_dcrg_amount") != null ? requestData.get("benefit_dcrg_amount").toString() : null;
            String benefit_commutation_amount = (String) requestData.get("benefit_commutation_amount") != null ? requestData.get("benefit_commutation_amount").toString() : null;

            int deptId = Integer.parseInt(dept_Id);
            int filestatus=0;
            String file_entry_type="";

            boolean check = checkTempPresent(tempId);
            
            
            if (check) {
                String updateQuery1 = "UPDATE pension_benefits SET file_pending = ?, pension_number = ?, ledger_number = ?, benefit_pension = ?, benefit_pension_amount = ?, benefit_dcrg = ?, benefit_dcrg_amount = ?, benefit_commutation = ?, benefit_commutation_amount = ?, updated_date = CURRENT_TIMESTAMP WHERE temp_id = ?";
                int rowsUpdated3 = jdbcTemplate.update(updateQuery1, file_pending, pension_number, ledger_number, benefit_pension, benefit_pension_amount, benefit_dcrg, benefit_dcrg_amount, benefit_commutation, benefit_commutation_amount, tempId);
            } else {
                String benefitQuery = "INSERT INTO pension_benefits (emp_no, temp_id, file_pending, pension_number, ledger_number, benefit_pension, benefit_pension_amount, benefit_dcrg, benefit_dcrg_amount, benefit_commutation, benefit_commutation_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                int rowsUpdated2 = jdbcTemplate.update(benefitQuery, empNo, tempId, file_pending, pension_number, ledger_number, benefit_pension, benefit_pension_amount, benefit_dcrg, benefit_dcrg_amount, benefit_commutation, benefit_commutation_amount);
            }

//          if ("yes".equalsIgnoreCase(benefit_dcrg)) {
            
            
          	filestatus = CheckFieldsInDept(tempId) ? (CheckFieldsInPensioner(tempId)  ? getFileStatus.getFileStatus11Id() : getFileStatus.getFileStatus10Id() ) : getFileStatus.getFileStatus9Id(); 
          	
          	if(filestatus == 11) {
          		file_entry_type="FILE_COMPLETED";
          	}
          	else if(filestatus == 10) {
          		file_entry_type="PENSION_PARTIAL";
          	}
          	else {
          		file_entry_type="DEPARTMENT_PARTIAL";
          	}
              
//          }                       
//          else if("no".equalsIgnoreCase(benefit_dcrg) || "no".equalsIgnoreCase(benefit_pension)){
//              	
//              	filestatus = getFileStatus.getFileStatus10Id();
//                  file_entry_type="PENSION_PARTIAL";
//           }
//          else if(benefit_dcrg==null)
//          {
//          	filestatus = getFileStatus.getFileStatus9Id();
//              file_entry_type="DEPARTMENT_PARTIAL";
//          }
            	
           //getFileStatus.getFileStatus2Id();
             
          
            if (filestatus == 0) {
                System.out.println("File status not found. Aborting insertion.");
                return false;
            }
                    

            String updateQuery = "UPDATE pensioner_details SET file_entry_type=?,file_status = ?, file_moved_date = ?, remarks = ?, updated_date = CURRENT_TIMESTAMP WHERE temp_id = ? LIMIT 1";
            int rowsUpdated = jdbcTemplate.update(updateQuery,file_entry_type, filestatus, dateToAudit, remarks, tempId);

            if (rowsUpdated > 0) {
                String historyQuery = "INSERT INTO file_movement_history (emp_no, file_moved_date, file_status, file_moved_by, remarks, temp_id) VALUES (?, ?, ?, ?, ?, ?)";
                int rowsUpdated1 = jdbcTemplate.update(historyQuery, empNo, dateToAudit, filestatus, filemovedby, remarks, tempId);

                if (rowsUpdated1 > 0) {
                   
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

    
    
    private boolean checkTempPresent(String tempId) {
    	String query = "SELECT EXISTS (SELECT 1 FROM pension_benefits WHERE temp_id = ?)";
        
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, Boolean.class, tempId));
	}

	private boolean CheckFieldsInDept(String tempId) {
		
    	// Define the SQL query
		String sql = "SELECT CASE " +
                "WHEN (gpf = 'yes' OR gpf = '90') AND spfgs = 'yes' AND private_affairs = 'yes' AND dcrg = 'yes' THEN true " +
                "ELSE false END AS allYes " +
                "FROM zone_benefits " +
                "WHERE temp_id = ?";
              
    	
        try {
            // Use queryForObject to get a single boolean result
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, tempId);
            return result != null && result; // Return the result if not null
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception if needed
        }
        
        return false; // Return false if an exception occurs
	}
	
	private boolean CheckFieldsInPensioner(String tempId) {
		
    	// Define the SQL query
        String sql = "SELECT CASE " +
                     "WHEN benefit_pension = 'yes' AND benefit_dcrg = 'yes' THEN true " +
                     "ELSE false END AS allYes " +
                     "FROM pension_benefits " +
                     "WHERE temp_id = ?";
              
    	
        try {
            // Use queryForObject to get a single boolean result
            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, tempId);
            return result != null && result; // Return the result if not null
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception if needed
        }
        
        return false; // Return false if an exception occurs
	}
	
	@Transactional
	public boolean saveRetirementCompletedhistory(List<Map<String, Object>> emp_details,List<Map<String, Object>> pension_details,String filemovedby) {
		
	    try {
	    	
	    	int file_Cat=0;
        	
        	if (!emp_details.isEmpty()) {
                Map<String, Object> firstEmployeeDetails = emp_details.get(0);
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

            jdbcTemplate.batchUpdate(empInsertQuery, emp_details, emp_details.size(), (ps, emp) -> {
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
                ps.setString(13, filemovedby);
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

                    jdbcTemplate.batchUpdate(empInsertQuery1, emp_details, emp_details.size(), (ps, emp) -> {
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
                        ps.setString(13, filemovedby);
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

                    jdbcTemplate.batchUpdate(empInsertQuery2, emp_details, emp_details.size(), (ps, emp) -> {
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
                        ps.setString(11, filemovedby);
                        ps.setInt(12, Integer.parseInt(emp.get("dept_id").toString()));
                        ps.setString(13, emp.get("file_category_name").toString());
                        ps.setString(14, emp.get("temp_id").toString());
                        ps.setString(15, emp.get("pending_duration").toString());
                        ps.setString(16, emp.get("reason_for_pending") != null ? emp.get("reason_for_pending").toString() : null);
                        ps.setString(17, emp.get("retirement_class").toString());
                        ps.setString(18, emp.get("retirement_date").toString());
                    });
                    
                    System.out.println("rows inserted in pd history for pending");
        	}
            
            String pensionInsertQuery = 
                    "INSERT INTO pension_benefits_history (emp_no,temp_id,file_pending,pension_number,ledger_number,benefit_pension,benefit_pension_amount,benefit_dcrg,benefit_dcrg_amount,benefit_commutation,benefit_commutation_amount,file_moved_by) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            	
            jdbcTemplate.batchUpdate(pensionInsertQuery, pension_details, pension_details.size(), (ps, pension) -> {
            	ps.setString(1, pension.get("emp_no").toString());
                ps.setString(2, pension.get("temp_id").toString());
                ps.setString(3, pension.get("file_pending").toString());
                ps.setString(4, pension.get("pension_number").toString());
                ps.setString(5, pension.get("ledger_number").toString());
                ps.setString(6, pension.get("benefit_pension").toString());
                ps.setString(7, pension.get("benefit_pension_amount")  != null ? pension.get("benefit_pension_amount").toString() : null);
                ps.setString(8, pension.get("benefit_dcrg") != null ? pension.get("benefit_dcrg").toString() : null);
                ps.setString(9, pension.get("benefit_dcrg_amount") != null ? pension.get("benefit_dcrg_amount").toString() : null);
                ps.setString(10, pension.get("benefit_commutation").toString());                
                ps.setString(11, pension.get("benefit_commutation_amount") != null ? pension.get("benefit_commutation_amount").toString() : null);
                ps.setString(12, filemovedby);
                
                
            	
            });
            
            System.out.println("rows inserted in pension history for family Pension");
            
	        return true; // Return true if all insertions succeed
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false; // Return false in case of any exception
	    }
	}
	
	@Transactional
	public boolean updateEntryPendingDetails(Map<String, Object> requestData) {
		// TODO Auto-generated method stub
		String empNo = (String) requestData.get("empNo");
		String tempId = (String) requestData.get("tempId");
    	String dateToAudit = (String) requestData.get("dateToAudit");
        String filemovedby =(String)requestData.get("filemovedby");
        String remarks = (String) requestData.get("remarks");
        String dept_Id = (String) requestData.get("deptId"); 
        
        String file_pending = (String) requestData.get("file_pending");
                
        int deptId = Integer.parseInt(dept_Id);
        int filestatus = getFileStatus.getFileStatus8Id();
    	if (filestatus == 0) {
            System.out.println("File status not found. Aborting insertion.");
            return false;
        }
        
        
        String file_entry_type="FILE_PENDING";
       
        String updateQuery = "UPDATE pensioner_details SET file_status = ?,file_entry_type=?,file_moved_date=?,remarks=?,updated_date= CURRENT_TIMESTAMP WHERE temp_id = ? LIMIT 1";
        int rowsUpdated = jdbcTemplate.update(updateQuery,filestatus,file_entry_type,dateToAudit, remarks,tempId);
        
        System.out.println("Rows inserted in pd:"+rowsUpdated);
		if(rowsUpdated>0)
		{
			// Optional: Insert into file movement history for auditing
            String historyQuery = "INSERT INTO file_movement_history (emp_no, file_moved_date, file_status, file_moved_by, remarks,temp_id) VALUES (?,?, ?, ?, ?, ?)";
            int rowsUpdated1=jdbcTemplate.update(historyQuery, empNo, dateToAudit, filestatus, filemovedby, remarks,tempId);
            System.out.println("Rows inserted in fmh:"+rowsUpdated1);
            
            if(rowsUpdated1>0)
            {
            	// Insert into zone benefits      	
            	String benefitQuery="INSERT INTO pension_benefits (emp_no,temp_id,file_pending) VALUES (?,?,?)";
            	int rowsUpdated2=jdbcTemplate.update(benefitQuery,empNo,tempId,file_pending);
            	System.out.println("Rows inserted in pb1:"+rowsUpdated2);
            	return true;
            }
            else
            {
            	return false;
            }
		}
		else {
			return false;
		}
					
	}
	
	@Transactional
	public boolean saveRetirementPendinghistory(List<Map<String, Object>> emp_details,List<Map<String, Object>> pension_details,String filemovedby) {
	    try {
	    	
	    	int file_Cat=0;
        	
        	if (!emp_details.isEmpty()) {
                Map<String, Object> firstEmployeeDetails = emp_details.get(0);
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

            jdbcTemplate.batchUpdate(empInsertQuery, emp_details, emp_details.size(), (ps, emp) -> {
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
                ps.setString(13, filemovedby);
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

                    jdbcTemplate.batchUpdate(empInsertQuery1, emp_details, emp_details.size(), (ps, emp) -> {
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
                        ps.setString(13, filemovedby);
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
                        "file_entry_type, file_moved_by, dept_id, file_category_name, temp_id,pending_duration,reason_for_pending) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    jdbcTemplate.batchUpdate(empInsertQuery2, emp_details, emp_details.size(), (ps, emp) -> {
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
                        ps.setString(11, filemovedby);
                        ps.setInt(12, Integer.parseInt(emp.get("dept_id").toString()));
                        ps.setString(13, emp.get("file_category_name").toString());
                        ps.setString(14, emp.get("temp_id").toString());
                        ps.setString(15, emp.get("pending_duration").toString());
                        ps.setString(16, emp.get("reason_for_pending") != null ? emp.get("reason_for_pending").toString() : null);
                    });
                    
                    System.out.println("rows inserted in pd history for pending");
        	}
            
            String pensionInsertQuery = 
                    "INSERT INTO pension_benefits_history (emp_no,temp_id,file_pending,file_moved_by) VALUES (?,?,?,?)";
            	
            jdbcTemplate.batchUpdate(pensionInsertQuery, pension_details, pension_details.size(), (ps, pension) -> {
            	ps.setString(1, pension.get("emp_no").toString());
                ps.setString(2, pension.get("temp_id").toString());
                ps.setString(3, pension.get("file_pending").toString());                
                ps.setString(4, filemovedby);
                          	
            });
            System.out.println("rows inserted in pension history for family Pension");
	        return true; // Return true if all insertions succeed
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false; // Return false in case of any exception
	    }
	}
	
	@Transactional
	public boolean updateEntryReturnDetails(Map<String, Object> requestData) {
		// TODO Auto-generated method stub
		String empNo = (String) requestData.get("empNo");
		String tempId = (String) requestData.get("tempId");
    	String dateToAudit = (String) requestData.get("dateToAudit");
        String filemovedby =(String)requestData.get("filemovedby");
        String remarks = (String) requestData.get("remarks");
        String dept_Id = (String) requestData.get("deptId");
        
        String file_entry_type="FILE_RETURNED";
                       
        int deptId = Integer.parseInt(dept_Id);
        int filestatus = getFileStatus.getFileStatus6Id();
    	if (filestatus == 0) {
            System.out.println("File status not found. Aborting insertion.");
            return false;
        }
       
        String updateQuery = "UPDATE pensioner_details SET file_status = ?,file_entry_type=?,file_moved_date=?,remarks=?,updated_date= CURRENT_TIMESTAMP WHERE temp_id = ? LIMIT 1";
        int rowsUpdated = jdbcTemplate.update(updateQuery,filestatus,file_entry_type,dateToAudit, remarks,tempId);
        
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
	
	@Transactional
	public boolean saveRetirementreturnhistory(List<Map<String, Object>> emp_details,String filemovedby) {
	    try {
	        
	    	int file_Cat=0;
        	
        	if (!emp_details.isEmpty()) {
                Map<String, Object> firstEmployeeDetails = emp_details.get(0);
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

            jdbcTemplate.batchUpdate(empInsertQuery, emp_details, emp_details.size(), (ps, emp) -> {
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
                ps.setString(13, filemovedby);
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

                    jdbcTemplate.batchUpdate(empInsertQuery1, emp_details, emp_details.size(), (ps, emp) -> {
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
                        ps.setString(13, filemovedby);
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
                        "file_entry_type, file_moved_by, dept_id, file_category_name, temp_id,pending_duration,reason_for_pending) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    jdbcTemplate.batchUpdate(empInsertQuery2, emp_details, emp_details.size(), (ps, emp) -> {
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
                        ps.setString(11, filemovedby);
                        ps.setInt(12, Integer.parseInt(emp.get("dept_id").toString()));
                        ps.setString(13, emp.get("file_category_name").toString());
                        ps.setString(14, emp.get("temp_id").toString());
                        ps.setString(15, emp.get("pending_duration").toString());
                        ps.setString(16, emp.get("reason_for_pending") != null ? emp.get("reason_for_pending").toString() : null);
                    });
                    
                    System.out.println("rows inserted in pd history for pending");
        	}
            
	        return true; // Return true if all insertions succeed
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false; // Return false in case of any exception
	    }
	}

}
