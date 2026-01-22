package in.gov.chennaicorporation.gccoffice.pensioner.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@Service
public class FileClosingService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	
	 private RestTemplate restTemplate;
	    private ObjectMapper objectMapper;
	    @Autowired
	    private AppConfig appconfig;

	    public FileClosingService(RestTemplate restTemplate, ObjectMapper objectMapper) {
	        this.restTemplate = restTemplate;
	        this.objectMapper = objectMapper;
	    }
	    
	    @Autowired
		 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
			 this.jdbcTemplate = new JdbcTemplate(dataSource);
		 }
	    
	    @Autowired
	    public GetFileStatusService getFileStatus;
	
	
	  public List<Map<String, Object>> fetchEmployeeByTempId(String tempId) {
		  
		  System.out.println("inside");
		  
		  String sql = "SELECT * " +                 
                  "FROM pensioner_details pd " +
                  "where pd.file_category=1 and pd.file_status <= 8 and pd.temp_id = ? and pd.is_closed=0 " ;
        

			System.out.println("outside");
		  	System.out.println(sql);
		  
	    	return jdbcTemplate.queryForList(sql,tempId);
	    	
	    	
		}

	  
	  public String getTempId(String empCode,Integer deptId,Integer file_cat) {
	        String query =  "SELECT temp_id FROM pensioner_details WHERE emp_no = ? AND file_category = ? AND dept_id = ?";

	        try {
		        return jdbcTemplate.queryForObject(query, String.class,empCode,file_cat,deptId);
		    } catch (EmptyResultDataAccessException e) {
		        return null; // or handle as needed
		    }
	    
	  }
	  
	  
	  public List<Map<String, Object>> fetchEmployeeById(String tempId) {
		  
		  String sql = "SELECT pd.*, zb.gpf, zb.gpf_amount, zb.spfgs, zb.spfgs_amount, zb.dcrg, " +
                  "zb.private_affairs, zb.private_affairs_amount, zb.recovery_emp, " +
                  "pb.file_pending, pb.pension_number, pb.ledger_number, pb.benefit_pension, " +
                  "pb.benefit_pension_amount, pb.benefit_dcrg, pb.benefit_dcrg_amount " +
                  "FROM pensioner_details pd " +
                  "LEFT JOIN zone_benefits zb ON pd.temp_id = zb.temp_id " +
                  "LEFT JOIN pension_benefits pb ON pd.temp_id = pb.temp_id "+
                  "where pd.temp_id = ? ";

   
	    	return jdbcTemplate.queryForList(sql,tempId);
		}
	  
	  
	  public boolean updateEntryDetails(Map<String, Object> requestData) {
		  
		  System.out.println("requestdata"+requestData);
		  
			// TODO Auto-generated method stub
			String empNo = (String) requestData.get("empNo");				
			String tempId = (String) requestData.get("tempId");
			String dateToAudit = (String) requestData.get("dateToAudit");
			String filemovedby =(String)requestData.get("filemovedby");
			
			String serviceDeath =(String)requestData.get("serviceDeath");
			String remarks = (String) requestData.get("remarks");
			String dept_Id = (String) requestData.get("deptId");
		
          
          int deptId = Integer.parseInt(dept_Id);
          int filestatus=getFileStatus.getFileStatus12Id();
          if (filestatus == 0) {
              System.out.println("File status not found. Aborting insertion.");
              return false;
          } 
          // Update file_status in pensioner_details
          String updateQuery = "update pensioner_details set file_status=?,service_death=?,file_moved_date=?,remarks=?,updated_date= CURRENT_TIMESTAMP,is_closed=1 WHERE temp_id = ? LIMIT 1";
          int rowsUpdated = jdbcTemplate.update(updateQuery,filestatus,serviceDeath,dateToAudit, remarks,tempId);
          
          
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
	  
	  
	  // save fileclosing details
	  
	  public boolean saveclosinghistory(List<Map<String, Object>> emp_details,String filemovedby) {
		    try {
		        // Loop through the list of employee details
		        for (Map<String, Object> emp : emp_details) {
		        	
		            // Extract individual fields from the map
		        	String empNo = emp.get("emp_no").toString();
		            String empName = emp.get("emp_name").toString();
		            String deptName = emp.get("dept_name").toString();
		            String designation = emp.get("designation").toString();
		            String serviceDeath=emp.get("service_death").toString();
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
		            String insertQuery ="INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, service_death,file_moved_date, remarks,file_category,file_status,file_entry_type,file_moved_by,dept_id,file_category_name,temp_id) VALUES (?,?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)";

		            // Execute the query
		            int rowsInserted = jdbcTemplate.update(insertQuery, empNo, empName, deptName,designation,serviceDeath,fileMovedDate,remarks,file_category,fileStatus,fileEntryType,filemovedby,dept_id,fileCategoryName,temp_id);
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
	  
	  
	  //closed list 
	  
	  	public List<Map<String, Object>> fetchClosingEmployee() {		  
		  
		  String sql = "SELECT * " +                 
                  "FROM pensioner_details pd " +
                  "where pd.file_category=1 and pd.is_closed=1 ";
        
			  
	    	return jdbcTemplate.queryForList(sql);
	    	
	    	
		}
	  	
	  	
	
		
	  
	  
}
