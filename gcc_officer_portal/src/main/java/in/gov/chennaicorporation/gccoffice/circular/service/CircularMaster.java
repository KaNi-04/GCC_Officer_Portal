package in.gov.chennaicorporation.gccoffice.circular.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.m_petition.data.ComplaintType;

@Service("CircularMaster")
public class CircularMaster {
	private JdbcTemplate jdbcTemplate;
	private Environment environment;
	private JdbcTemplate appjdbcTemplate;
	
	@Autowired
	public void setDataSource(Environment environment,
			@Qualifier("mysqlCircularDataSource") DataSource dataSource,
			@Qualifier("mysqlAppDataSource") DataSource appdataSource) {
		this.environment = environment;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.appjdbcTemplate = new JdbcTemplate(appdataSource);
	}
	 
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getCircularType(){
		String SqlQuery = "";
		
		SqlQuery = "SELECT * FROM `circular_types` WHERE `isactive`=1 AND `isdelete`=0";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getDepartment(){
		String SqlQuery = "";
		
		SqlQuery = "SELECT * FROM `departments` WHERE `isactive`=1 AND `isdelete`=0 Order By name";
		
		List<Map<String, Object>> result = appjdbcTemplate.queryForList(SqlQuery);
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getCircular(){
		String SqlQuery = "";
		
		SqlQuery = "SELECT cd.*, cf.*, CASE WHEN cf.cid IS NOT NULL THEN 'true' ELSE 'false' END AS file_exits, gcc_apps.departments.name as department_name, circular_types.name as circular_type_name FROM circular_data cd LEFT JOIN circular_files cf ON cd.cid = cf.cid, gcc_apps.departments, circular_types where gcc_apps.departments.did=department AND circular_types.tid=circular_type ORDER BY cd.cid Desc";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
	}
	
	@Transactional
	public String saveCircular(
			String createdBy,
			String circularCategory,
			String circularType,
			String department,
			String Date,
			String subject,
			MultipartFile scan_file1) {
		
		//System.out.println("I am from Service Page "+petitionerName);
		
		//String complaintNature = "";
		int lastInsertId = 0;
		String  sqlQuery = "INSERT INTO `circular_data` "
                + "(`createdBy`,`category`, `circular_type`, `department`, "
                + "`Date`, `subject`) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
		
		try {
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			// Use JdbcTemplate's update method to execute the query with parameters
			jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
	            ps.setString(1, createdBy);
	            ps.setString(2, circularCategory);
	            ps.setString(3, circularType);
	            ps.setString(4, department);
	            ps.setString(5, Date);
	            ps.setString(6, subject);
	            return ps;
	        }, keyHolder);
			
			// Get the last insert ID
	        lastInsertId = (int) keyHolder.getKey().longValue();
	        String lastInsertId_txt = String.valueOf(lastInsertId);
	        
	        // Add Petition Scan File in the database
	        addCircularFile(createdBy, lastInsertId, scan_file1);
	        
            return lastInsertId_txt; // If no exception, query executed successfully
            
        } catch (DataAccessException e) {
            // Handle exception (e.g., log error)
        	String sqlQuery2 = "UPDATE `circular_data` SET `isactive`=0, `isdelete`=1 WHERE `cid`="+lastInsertId+" LIMIT 1";
    		jdbcTemplate.update(sqlQuery2);
            e.printStackTrace();
            return "error"; // Query execution failed
        }
	}
	
	@Transactional
	public String addCircularFile(String createdBy, 
			int cid,
    		MultipartFile page1) {
		
		String fileName="";
        for (int i = 1; i <= 4; i++) {
            MultipartFile file = null;
            switch (i) {
                case 1:
                    file = page1;
                    fileName = "Scan1";
                    break;
            }
            if (file != null && !file.isEmpty()) {
            	try {
                    // Get the bytes of the file
                    byte[] bytes = file.getBytes();
                    
                    // Set the file path where you want to save it
                    String uploadDirectory = environment.getProperty("file.upload.directory");
                    String serviceFolderName = environment.getProperty("cir_foldername");
                    uploadDirectory = uploadDirectory + serviceFolderName;
                    
                    // File name 
                    fileName = cid + "_" + fileName + "_" + file.getOriginalFilename();
                    
                    //File Size
                    Long fileSize = file.getSize();
                    
                    // File Type
                    String fileType = file.getContentType();
                    
                    // Create directory if it doesn't exist
                    Path directoryPath = Paths.get(uploadDirectory);
                    if (!Files.exists(directoryPath)) {
                        Files.createDirectories(directoryPath);
                    }
                    
                    String filePath = uploadDirectory + "/" + fileName;

                    // Create a new Path object
                    Path path = Paths.get(filePath);

                    // Write the bytes to the file
                    Files.write(path, bytes);
                    
                    // Save the file path to a database
                    String  sqlQuery = "INSERT INTO `circular_files` ( `cid`, `created_by`, `file_name`, `file_size`, `file_type`) "
                    		+ "VALUES ('"+cid+"', '"+createdBy+"','"+fileName+"','"+fileSize+"','"+fileType+"')";
            		jdbcTemplate.update(sqlQuery);
                    
                } catch (IOException e) {
                	e.printStackTrace();
                    return "Failed to save file " + file.getOriginalFilename();
                }
            }
            
        }
        return "success";
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getReport(){
	    String SqlQuery = "";
	    String dynamic_query ="";
	    SqlQuery = "SELECT * FROM `circular_types` WHERE `isactive`=1 AND `isdelete`=0 ORDER BY `tid`";
	    
	    List<Map<String, Object>> circular_types_result = jdbcTemplate.queryForList(SqlQuery);
	    
	    for(Map<String, Object> type : circular_types_result) {
	        String typeId = type.get("tid").toString();
	        String typeName = type.get("name").toString();
	        dynamic_query += "SUM(CASE WHEN ct.tid = '" + typeId + "' THEN 1 ELSE 0 END) AS `dynamic_" + typeName + "`, ";
	    }
	    
	    // Remove the trailing comma and space from dynamic_query
	    dynamic_query = dynamic_query.substring(0, dynamic_query.length() - 2);
	    
	    // Construct the SQL query with the dynamic part
	    SqlQuery = "SELECT "
	            + " d.name, "
	            + "SUM(CASE WHEN ct.tid != '0' THEN 1 ELSE 0 END) as total, "
	            + dynamic_query + " "
	            + "FROM "
	            + "gcc_apps.departments d "
	            + "LEFT JOIN circular.circular_data cd ON cd.department = d.did "
	            + "LEFT JOIN circular.circular_types ct ON cd.circular_type = ct.tid "
	            + "GROUP BY d.name";
	    System.out.println(SqlQuery);
	    List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
	    
	    return result;
	}
	
	
}
