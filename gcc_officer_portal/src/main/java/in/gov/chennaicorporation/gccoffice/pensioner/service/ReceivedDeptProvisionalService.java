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

@Service
@Transactional
public class ReceivedDeptProvisionalService {
	
	
	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 
	 
	   private RestTemplate restTemplate;
	    private ObjectMapper objectMapper;
	    
	    public ReceivedDeptProvisionalService(RestTemplate restTemplate, ObjectMapper objectMapper) {
	        this.restTemplate = restTemplate;
	        this.objectMapper = objectMapper;
	    }
	
	    @Autowired
		 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
			 this.jdbcTemplate = new JdbcTemplate(dataSource);
		 }
	    
	    @Autowired
	    public GetFileStatusService getFileStatus;
	    
	    
	    
    public List<Map<String, Object>> fetchDetailsProvisionalPension(Integer file_cat) {
	    	String query = "SELECT * " +
                    "FROM pensioner_details " +
                    "WHERE file_category=? AND file_status=7";
	        return jdbcTemplate.queryForList(query,file_cat);
	    }
    
    public List<Map<String, Object>> fetchDetailsProvisionalDepartment(Integer file_cat) {
    	String query = "SELECT * " +
                "FROM pensioner_details " +
                "WHERE file_category=? AND file_status=6";
        return jdbcTemplate.queryForList(query,file_cat);
    }
    

    
    
    //updated entry details
    public boolean updateEntryDetailsCompleted(Map<String, Object> requestData) {
		// TODO Auto-generated method stub
		String empNo = (String) requestData.get("empNo");				
		String tempId = (String) requestData.get("tempId");
    	String dateToAudit = (String) requestData.get("dateToAudit");
        String filemovedby =(String)requestData.get("filemovedby");
        String remarks = (String) requestData.get("remarks");
        String dept_Id = (String) requestData.get("deptId");
        String pramount = (String) requestData.get("pramount");
        String prpercentage = (String) requestData.get("prpercentage");
        
        
        int deptId = Integer.parseInt(dept_Id);
        int filestatus = 11;
        String file_entry_type="FILE_COMPLETED";
        
        // Update file_status in pensioner_details
        String updateQuery = "UPDATE pensioner_details SET file_status = ?,file_moved_date=?,remarks=?,pr_amount=?,pr_percentage=?,file_entry_type=?,updated_date= CURRENT_TIMESTAMP WHERE temp_id = ? LIMIT 1";
        int rowsUpdated = jdbcTemplate.update(updateQuery,filestatus,dateToAudit, remarks,pramount,prpercentage,file_entry_type,tempId);
        
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
    
    
    
    
    // return to department
    public boolean updateEntryDetailsReturntoDepartment(Map<String, Object> requestData) {
		// TODO Auto-generated method stub
		String empNo = (String) requestData.get("empNo");				
		String tempId = (String) requestData.get("tempId");
    	String dateToAudit = (String) requestData.get("dateToAudit");
        String filemovedby =(String)requestData.get("filemovedby");
        String remarks = (String) requestData.get("remarks");
        String dept_Id = (String) requestData.get("deptId");
//        String pramount = (String) requestData.get("pramount");
//        String prpercentage = (String) requestData.get("prpercentage");
        
        
        int deptId = Integer.parseInt(dept_Id);
        int filestatus = 6;
        String file_entry_type="FILE_RETURNED";
        
        // Update file_status in pensioner_details
        String updateQuery = "UPDATE pensioner_details SET file_status = ?, file_entry_type =?,file_moved_date=?,remarks=?,updated_date= CURRENT_TIMESTAMP WHERE temp_id = ? LIMIT 1";
        int rowsUpdated = jdbcTemplate.update(updateQuery,filestatus,file_entry_type,dateToAudit,remarks,tempId);
        
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
    
    
    
    
    // sent to pension
    public boolean updateEntryDetailsSenttoPension(Map<String, Object> requestData) {
		// TODO Auto-generated method stub
		String empNo = (String) requestData.get("empNo");				
		String tempId = (String) requestData.get("tempId");
    	String dateToAudit = (String) requestData.get("dateToAudit");
        String filemovedby =(String)requestData.get("filemovedby");
        String remarks = (String) requestData.get("remarks");
        String dept_Id = (String) requestData.get("deptId");
        String ReasonofPension = (String) requestData.get("ReasonofPension");
        String pendingDuration = (String) requestData.get("pendingDuration");
        String reasonofPending = (String) requestData.get("reasonofPending");
        String Dp = (String) requestData.get("Dp");
        String otherReason = (String) requestData.get("otherReason");
        
//        String pramount = (String) requestData.get("pramount");
//        String prpercentage = (String) requestData.get("prpercentage");
        
        
        int deptId = Integer.parseInt(dept_Id);
        int filestatus = 7;
        String file_entry_type="FILE_RETURNED";
        
        // Update file_status in pensioner_details
        String updateQuery = "UPDATE pensioner_details SET file_status = ?, file_entry_type =?,file_moved_date=?,remarks=?,reason_for_provisional_pension=?,pending_duration=?,reason_for_pending=?,dp=?,others_provisional_pension=?,updated_date= CURRENT_TIMESTAMP WHERE temp_id = ? LIMIT 1";
        int rowsUpdated = jdbcTemplate.update(updateQuery,filestatus,file_entry_type,dateToAudit,remarks,ReasonofPension,pendingDuration,reasonofPending,Dp,otherReason ,tempId);
        
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
    
    
    
    

    
    
    public boolean savehistory(List<Map<String, Object>> emp_details, String filemovedby) {
        try {
            // Loop through the list of employee details
            for (Map<String, Object> emp : emp_details) {
                // Safely extract individual fields from the map with default values
                String empNo = emp.get("emp_no").toString();
                String empName = emp.get("emp_name").toString();
                String deptName = emp.get("dept_name").toString();
                String designation = emp.get("designation").toString();
                String fileCategoryName = emp.get("file_category_name").toString();
                String retirement_class = emp.get("retirement_class").toString();
                String type_of_retirement = emp.get("type_of_retirement").toString();
                String retirementDate = emp.get("retirement_date").toString();
                String ReasonofPension = emp.get("reason_for_provisional_pension").toString();
                String pendingDuration = emp.get("pending_duration").toString();
                String reasonofPending = emp.get("reason_for_pending") != null ? emp.get("reason_for_pending").toString() : null;
                //String Dp = emp.get("dp").toString();
                String Dp = emp.get("dp") != null ? emp.get("dp").toString() : "hello";
                String otherReason = emp.get("others_provisional_pension") != null ? emp.get("others_provisional_pension").toString() : null;
                String fileMovedDate = emp.get("file_moved_date").toString();
                String remarks = emp.get("remarks").toString();
                String pramount = emp.get("pr_amount") != null && !emp.get("pr_amount").toString().trim().isEmpty() ? emp.get("pr_amount").toString() : null; 
                String prpercentage = emp.get("pr_percentage") != null && !emp.get("pr_percentage").toString().trim().isEmpty() ? emp.get("pr_percentage").toString() : null; 
                int fileStatus = Integer.parseInt(emp.get("file_status").toString()); // Default to 0
                String fileEntryType = emp.get("file_entry_type").toString();
                int file_category =  Integer.parseInt(emp.get("file_category").toString()); // Default to 0
                int dept_id = Integer.parseInt(emp.get("dept_id").toString()); // Default to 0
                String temp_id = emp.get("temp_id").toString();

                // Prepare the insert query
                String insertQuery = "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, type_of_retirement,retirement_date ,reason_for_provisional_pension, pending_duration, dp, others_provisional_pension, reason_for_pending, retirement_class, file_moved_date, remarks, pr_amount, pr_percentage, file_category, file_status, file_entry_type, file_moved_by, dept_id, file_category_name, temp_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

                // Execute the query
                int rowsInserted = jdbcTemplate.update(insertQuery, empNo, empName, deptName, designation, type_of_retirement,retirementDate ,ReasonofPension, pendingDuration, Dp, otherReason, reasonofPending, retirement_class, fileMovedDate, remarks, pramount, prpercentage, file_category, fileStatus, fileEntryType, filemovedby, dept_id, fileCategoryName, temp_id);
                System.out.println("Rows inserted in pdh: " + rowsInserted);

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
