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
public class DepartmentForwardService {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
 
 
   private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    @Autowired
    private AppConfig appconfig;

    public DepartmentForwardService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Autowired
	 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
    
    @Autowired
    public GetFileStatusService getFileStatus;
    
    public List<Map<String, Object>> fetchDepatmentForwardEmpfiles(Integer file_cat) {
        String query = "SELECT * " +
                       "FROM pensioner_details " +
                       "WHERE file_status=4 AND file_category=? AND is_closed=0";
     
        
        return jdbcTemplate.queryForList(query,file_cat);
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
        int filestatus = getFileStatus.getFileStatus5Id();
        if (filestatus == 0) {
            System.out.println("File status not found. Aborting insertion.");
            return false;
        } 
        
        String updateQuery = "UPDATE pensioner_details SET file_status = ?,file_moved_date=?,remarks=?,updated_date= CURRENT_TIMESTAMP WHERE temp_id = ? LIMIT 1";
        int rowsUpdated = jdbcTemplate.update(updateQuery,filestatus,dateToAudit, remarks,tempId);
        
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
    
  //save the updatedfamilypensionReturn
    @Transactional
		public boolean saveFamilyPensionForwardHistory(List<Map<String, Object>> emp_details,String filemovedby) {
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
		            String sericeDeath = emp.get("service_death").toString();
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
		            int rowsInserted = jdbcTemplate.update(insertQuery, empNo, empName, deptName,designation,sericeDeath,relationshipDeceased,sanctionedGis,fileMovedDate,remarks,file_category,fileStatus,fileEntryType,filemovedby,dept_id,fileCategoryName,temp_id,pendingDuration,pendingReason);
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
		
		//update save pendency history
    @Transactional
  		public boolean savePendencyForwardHistory(List<Map<String, Object>> emp_details,String filemovedby) {
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
