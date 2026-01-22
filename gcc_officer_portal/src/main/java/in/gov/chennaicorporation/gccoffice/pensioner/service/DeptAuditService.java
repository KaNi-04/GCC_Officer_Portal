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
public class DeptAuditService {
	

	
	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 
	 
	   private RestTemplate restTemplate;
	    private ObjectMapper objectMapper;
	    @Autowired
	    private AppConfig appconfig;

	    public DeptAuditService(RestTemplate restTemplate, ObjectMapper objectMapper) {
	        this.restTemplate = restTemplate;
	        this.objectMapper = objectMapper;
	    }
	    
	    @Autowired
		 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
			 this.jdbcTemplate = new JdbcTemplate(dataSource);
		 }
	    
	    @Autowired
	    public GetFileStatusService getFileStatus;
	 

	    public List<Map<String, Object>> fetchEmployeeDetailsByTable(Integer deptId,Integer file_cat) {
	        String query = "SELECT * " +
	                       "FROM pensioner_details " +
	                       "WHERE dept_id=? AND file_category=? AND (file_status=1 OR file_status=3) AND is_closed=0";
	     
	        
	        return jdbcTemplate.queryForList(query,deptId,file_cat);
	    }
	    
	    public List<Map<String, Object>> fetchEmployeeById(String tempId) {
	    	String query = "SELECT * " +
                    "FROM pensioner_details " +
                    "WHERE temp_id=?";
  
     
	    	return jdbcTemplate.queryForList(query,tempId);
		}
	    
	    
	    
	    public List<Map<String, Object>> fetchEmployeeByIdCps(String tempId) {
	    	String query = "SELECT * " +
                    "FROM cps_pensioner_details " +
                    "WHERE temp_id=?";
  
     
	    	return jdbcTemplate.queryForList(query,tempId);
		}
	    
	    public List<Map<String, Object>> fetchDetailsWithZoneBenefitsById(String tempId) {
	        String query = "SELECT pd.*, zb.gpf, zb.gpf_amount, zb.spfgs, zb.spfgs_amount, zb.dcrg, " +
	                       "zb.private_affairs, zb.private_affairs_amount, zb.recovery_emp " +
	                       "FROM pensioner_details pd " +
	                       "LEFT JOIN zone_benefits zb ON pd.temp_id = zb.temp_id " +
	                       "WHERE pd.temp_id = ?";
	        
	        return jdbcTemplate.queryForList(query, tempId);
	    }//
	    
	    public List<Map<String, Object>> fetchFilePendingBenefits(String tempId) {
	    	
	        String query = "SELECT pd.*, zb.gpf, zb.gpf_amount, zb.spfgs, zb.spfgs_amount, zb.dcrg, " +
	        			   "zb.private_affairs, zb.private_affairs_amount, zb.recovery_emp,pb.file_pending " +
	                       "FROM pensioner_details pd " +
	                       "LEFT JOIN zone_benefits zb ON pd.temp_id = zb.temp_id " +
	                       "LEFT JOIN pension_benefits pb ON pd.temp_id = pb.temp_id " +
	                       "WHERE pd.temp_id = ?";
	        
	        return jdbcTemplate.queryForList(query, tempId);
	    }
	    
	    
	    public List<Map<String, Object>> fetchPartialCompleted(String tempId) {
	    	
	        String query = "SELECT pd.*, zb.gpf, zb.gpf_amount, zb.spfgs, zb.spfgs_amount, zb.dcrg, " +
	        			   "zb.private_affairs, zb.private_affairs_amount, zb.recovery_emp,pb.file_pending," +
	        			   "pb.pension_number,pb.ledger_number,pb.benefit_pension,pb.benefit_pension_amount,pb.benefit_dcrg,pb.benefit_dcrg_amount,pb.benefit_commutation,pb.benefit_commutation_amount " +
	                       "FROM pensioner_details pd " +
	                       "LEFT JOIN zone_benefits zb ON pd.temp_id = zb.temp_id " +
	                       "LEFT JOIN pension_benefits pb ON pd.temp_id = pb.temp_id " +
	                       "WHERE pd.temp_id = ?";
	        
	        return jdbcTemplate.queryForList(query, tempId);
	    }

	    
	    
	    
	    public List<Map<String, Object>> fetchFileMovementHistory(String tempId) {
	        // Query to fetch file movement history
	        String query = "SELECT fh.file_moved_date AS fileMovedDateHistory, " +
	                       "fm.file_status AS fileStatusHistory, " +
	                       "fh.remarks AS remarksHistory " +
	                       "FROM file_movement_history fh " +
	                       "JOIN file_status_master fm ON fh.file_status = fm.id " +
	                       "WHERE fh.temp_id = ?";
	        
	        return jdbcTemplate.queryForList(query,tempId);
	    }
	    
	    
	    @Transactional
		public boolean updateEntryDetails(Map<String, Object> requestData) {
			// TODO Auto-generated method stub
			String empNo = (String) requestData.get("empNo");				
			String tempId = (String) requestData.get("tempId");
        	String dateToAudit = (String) requestData.get("dateToAudit");
            String filemovedby =(String)requestData.get("filemovedby");
            String remarks = (String) requestData.get("remarks");
            String dept_Id = (String) requestData.get("deptId");
            
            
            int deptId = Integer.parseInt(dept_Id);
            int filestatus = getFileStatus.getFileStatus2Id();
            if (filestatus == 0) {
                System.out.println("File status not found. Aborting insertion.");
                return false;
            }
            String file_entry_type="AUDIT_RETURNED";
            
            // Update file_status in pensioner_details
            String updateQuery = "UPDATE pensioner_details SET file_status = ?, file_entry_type =?,file_moved_date=?,remarks=?,updated_date= CURRENT_TIMESTAMP WHERE temp_id = ? LIMIT 1";
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
		public boolean savehistory(List<Map<String, Object>> emp_details,String filemovedby) {
		    try {
		        // Loop through the list of employee details
		        for (Map<String, Object> emp : emp_details) {
		            // Extract individual fields from the map
		            String empNo = emp.get("emp_no").toString();
		            String empName = emp.get("emp_name").toString();
		            String deptName = emp.get("dept_name").toString();
		            String designation = emp.get("designation").toString();
		            String fileCategoryName = emp.get("file_category_name").toString();
		            String retirement_date = emp.get("retirement_date").toString();
		            String retirement_class = emp.get("retirement_class").toString();
		            String type_of_retirement = emp.get("type_of_retirement").toString();		            
		            String fileMovedDate = emp.get("file_moved_date").toString();
		            String remarks = emp.get("remarks").toString();		            
		            int fileStatus = Integer.parseInt(emp.get("file_status").toString());		            
		            String fileEntryType = emp.get("file_entry_type").toString();		            
		            int file_category = Integer.parseInt(emp.get("file_category").toString());
		            int dept_id = Integer.parseInt(emp.get("dept_id").toString());
		            String temp_id = emp.get("temp_id").toString();

		            // Prepare the insert query
		            String insertQuery ="INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, type_of_retirement,retirement_date ,retirement_class,file_moved_date, remarks,file_category,file_status,file_entry_type,file_moved_by,dept_id,file_category_name,temp_id) VALUES (?,?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?)";

		            // Execute the query
		            int rowsInserted = jdbcTemplate.update(insertQuery, empNo, empName, deptName,designation,type_of_retirement,retirement_date,retirement_class,fileMovedDate,remarks,file_category,fileStatus,fileEntryType,filemovedby,dept_id,fileCategoryName,temp_id);
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

		
      //save the updatedfamilypension details
	    @Transactional
		public boolean saveFamilyPensionHistory(List<Map<String, Object>> emp_details,String filemovedby) {
		    try {
		        // Loop through the list of employee details
		        for (Map<String, Object> emp : emp_details) {
		            // Extract individual fields from the map
		            String empNo = emp.get("emp_no").toString();
		            String empName = emp.get("emp_name").toString();
		            String deptName = emp.get("dept_name").toString();
		            String designation = emp.get("designation").toString();
		            String fileCategoryName = emp.get("file_category_name").toString();
		            String pendingDuration=emp.get("pending_duration").toString();
		            String pendingReason=emp.get("reason_for_pending") != null ? emp.get("reason_for_pending").toString() : null;
		            String serviceDeath = emp.get("service_death").toString();
		            String relationshipDeceased = emp.get("relationship_deceased").toString();
		            String sanctionedGis = emp.get("sanctioned_gis_amount").toString();		            
		            String fileMovedDate = emp.get("file_moved_date").toString();
		            String remarks = emp.get("remarks").toString();		            
		            int fileStatus = Integer.parseInt(emp.get("file_status").toString());		            
		            String fileEntryType = emp.get("file_entry_type").toString();		            
		            int file_category = Integer.parseInt(emp.get("file_category").toString());
		            int dept_id = Integer.parseInt(emp.get("dept_id").toString());
		            String temp_id = emp.get("temp_id").toString();

		            // Prepare the insert query
		            String insertQuery ="INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, service_death,relationship_deceased ,sanctioned_gis_amount,file_moved_date, remarks,file_category,file_status,file_entry_type,file_moved_by,dept_id,file_category_name,temp_id,pending_duration,reason_for_pending) VALUES (?,?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)";

		            // Execute the query
		            int rowsInserted = jdbcTemplate.update(insertQuery, empNo, empName, deptName,designation,serviceDeath,relationshipDeceased,sanctionedGis,fileMovedDate,remarks,file_category,fileStatus,fileEntryType,filemovedby,dept_id,fileCategoryName,temp_id,pendingDuration,pendingReason);
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
		
		
		//save the pendency details
	    @Transactional
		public boolean savePendencyHistory(List<Map<String, Object>> emp_details,String filemovedby) {
		    try {
		        // Loop through the list of employee details
		        for (Map<String, Object> emp : emp_details) {
		            // Extract individual fields from the map
		            String empNo = emp.get("emp_no").toString();
		            String empName = emp.get("emp_name").toString();
		            String deptName = emp.get("dept_name").toString();
		            String designation = emp.get("designation").toString();
		            String retirementClass = emp.get("retirement_class").toString();
		            String retirementDate=emp.get("retirement_date").toString();
		            //String fileCategoryName = emp.get("file_category_name").toString();
		            String pendingDuration=emp.get("pending_duration").toString();
		            String pendingReason=emp.get("reason_for_pending") != null ? emp.get("reason_for_pending").toString() : null;
		            
		            String typeOfCategory=emp.get("file_category_name").toString();
		            String typeOfRetirement=emp.get("type_of_retirement").toString();
		            
		            String fileMovedDate = emp.get("file_moved_date").toString();
		            String remarks = emp.get("remarks").toString();		            
		            int fileStatus = Integer.parseInt(emp.get("file_status").toString());		            
		            String fileEntryType = emp.get("file_entry_type").toString();		            
		            int file_category = Integer.parseInt(emp.get("file_category").toString());
		            int dept_id = Integer.parseInt(emp.get("dept_id").toString());
		            String temp_id = emp.get("temp_id").toString();

		            // Prepare the insert query
		            String insertQuery ="INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation,retirement_class,retirement_date,file_category_name,type_of_retirement,file_moved_date, remarks,file_category,file_status,file_entry_type,file_moved_by,dept_id,temp_id,pending_duration,reason_for_pending) VALUES (?,?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)";

		            // Execute the query
		            int rowsInserted = jdbcTemplate.update(insertQuery, empNo, empName, deptName,designation,retirementClass,retirementDate,typeOfCategory,typeOfRetirement,fileMovedDate,remarks,file_category,fileStatus,fileEntryType,filemovedby,dept_id,temp_id,pendingDuration,pendingReason);
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
