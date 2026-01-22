package in.gov.chennaicorporation.gccoffice.m_petition.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import in.gov.chennaicorporation.gccoffice.m_petition.data.ComplaintType;
import in.gov.chennaicorporation.gccoffice.service.Base64Util;
import in.gov.chennaicorporation.gccoffice.service.GetMappingUserDetails;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//import org.jsoup.Jsoup;

import in.gov.chennaicorporation.gccoffice.controller.DateTimeUtil;

@Service
public class Petition {
	
	private final GetMappingUserDetails getMappingUserDetails;
	private final JdbcTemplate jdbcTemplate;
	private final JdbcTemplate jdbcOracleTemplate;
	private final Environment environment;
	private final PetitionMaster petitionMaster;
	

    // Constructor injection
    @Autowired
    public Petition(GetMappingUserDetails getMappingUserDetails, 
                    @Qualifier("mysqlMayorPetitionDataSource") DataSource dataSource, 
                    @Qualifier("oracleDataSource") DataSource oracleDataSource, 
                    Environment environment, 
                    PetitionMaster petitionMaster) {
        this.getMappingUserDetails = getMappingUserDetails;
        this.jdbcTemplate = new JdbcTemplate(dataSource);  // Set jdbcTemplate using injected dataSource
        this.jdbcOracleTemplate = new JdbcTemplate(oracleDataSource);  // Set jdbcOracleTemplate using injected oracleDataSource
        this.environment = environment;
        this.petitionMaster = petitionMaster;
    }
	 
	@Transactional(readOnly = true)
	public List getComplaintTypeList() {
		return petitionMaster.getComplaintType();
	}
	/*
	@Transactional(readOnly = true)
	public static List<ComplaintType> convertToComplaintTypes(List<Map<String, Object>> complaintTypeMaps) {
        return complaintTypeMaps.stream()
                .map(ComplaintType::new)
                .collect(Collectors.toList());
    }
	*/
	@Transactional(readOnly = true)
	public static List<ComplaintType> convertToComplaintTypes(List<Map<String, Object>> complaintTypeMaps) {
	    List<ComplaintType> complaintTypes = new ArrayList<ComplaintType>();
	    
	    if (complaintTypeMaps != null) {
	        for (Map<String, Object> map : complaintTypeMaps) {
	            ComplaintType ct = new ComplaintType(map);
	            complaintTypes.add(ct);
	        }
	    }
	    
	    return complaintTypes;
	}
	@Transactional(readOnly = true)
	// Method to map complaint type IDs to their respective complaint types
	public static String mapComplaintTypes(List<ComplaintType> complaintTypes, String complaint) {
        String[] complaintIds = complaint.split(",");
        StringBuilder complaintTxtBuilder = new StringBuilder();

        for (String complaintId : complaintIds) {
            int id = Integer.parseInt(complaintId);
            for (ComplaintType type : complaintTypes) {
                if (type.getComplaintTypeId() == id) {
                    if (complaintTxtBuilder.length() > 0) {
                        complaintTxtBuilder.append(",");
                    }
                    complaintTxtBuilder.append(type.getComplaintType());
                    break;
                }
            }
        }

        return complaintTxtBuilder.toString();
    }
	
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getPetitionList(String month, String year) {
	    try {
	        // Construct the date pattern using the provided month and year
	        String datePattern = year + "-" + month + "-%"; // Example: "2025-02-%"
	        
	        // Corrected SQL query using the proper date format
	        String sqlQuery = "SELECT p.*, pt.name as petition_type_name FROM petition p "
	        		+ "LEFT JOIN petition_master.petition_types pt ON pt.petition_type_id = p.petition_type "
	        		+ "WHERE (p.`isactive`=1 AND p.`isdelete`=0) AND DATE(p.cdate) LIKE ?";
	        
	        
	        // Execute the query with the date pattern as parameter
	        List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, datePattern);
	        
	        // Encode the SQL query for logging or tracking
	        Base64Util.encodeBase64(sqlQuery);
	        
	        // Iterate over the list of results and encode the 'petitionNo'
	        for (Map<String, Object> map : result) {
	            String petitionNo = (String) map.get("petition_no");
	            
	            // Encode the 'petitionNo' value using Base64Util
	            String petitionNoEncoded = Base64Util.encodeBase64(petitionNo);
	            
	            // Add the encoded value as 'petitionNoEncode' to the map
	            map.put("petitionNoEncode", petitionNoEncoded);
	        }
	        
	        return result;
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return null; // Return null or handle error appropriately
	    }
	}
	
	@Transactional(readOnly = true)
    public List<Map<String,Object>> getUnmapPetitionList() {
        try {
            String sqlQuery = "SELECT "
            		+ "    p.* "
            		+ "FROM "
            		+ "    `petition` p "
            		+ "WHERE "
            		+ "    p.`isactive` = 1 AND p.`isdelete` = 0 "
            		+ "    AND NOT EXISTS ("
            		+ "        SELECT 1 "
            		+ "        FROM `petition_mapping` pm "
            		+ "        WHERE (p.`petition_id` = pm.`petition_id`) AND (pm.`isactive` = 1 AND pm.`isdelete` = 0 ) "
            		+ "    )";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery);
            
            // Iterate over the list of maps
            for (Map<String, Object> map : result) {
                // Get the value of the 'petitionNo' field from the map
                String petitionNo = (String) map.get("petition_no");
                
                // Encode the 'petitionNo' value using Base64Util
                String petitionNoEncoded = Base64Util.encodeBase64(petitionNo);
                
                // Add the encoded value as 'petitionNoEncode' to the map
                map.put("petitionNoEncode", petitionNoEncoded);
            }
            
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null; // Return null or handle error appropriately
        }
    }
	
	@Transactional(readOnly = true)
    public List<Map<String,Object>> getPetitionDetails(String petitionNo) {
        try {
            String sqlQuery = "SELECT * FROM petition WHERE petition_no = ? AND (`isactive`=1 AND `isdelete`=0) LIMIT 1";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, petitionNo);
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null; // Return null or handle error appropriately
        }
    }
	
	@Transactional
	public String savePetition(
			String petitionType,
			String createdBy,
			String petitionerName,
			String petitionerMobile,
			String petitionerComplaint,
			MultipartFile page1,
			MultipartFile page2,
			MultipartFile page3,
			MultipartFile page4) {
		
		//System.out.println("I am from Service Page "+petitionerName);
		
		//String complaintNature = "";
		int lastInsertId = 0;
		String  sqlQuery = "INSERT INTO `petition` "
                + "(`petition_type`,`created_by`, `petitioner_name`, `petitioner_mobile`, "
                + "`petitioner_complaint`, `petitioner_complaint_txt`) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
		
		try {
			// Complaint Type List
			List complaintTypelist = getComplaintTypeList();
			// Convert the list of maps to a list of ComplaintType objects
	        List<ComplaintType> complaintTypes = convertToComplaintTypes(complaintTypelist);

			// Map complaint types
	        String petitionerComplaint_txt = mapComplaintTypes(complaintTypes, petitionerComplaint);
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			// Use JdbcTemplate's update method to execute the query with parameters
			jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
	            ps.setString(1, petitionType);
	            ps.setString(2, createdBy);
	            ps.setString(3, petitionerName);
	            ps.setString(4, petitionerMobile);
	            ps.setString(5, petitionerComplaint);
	            ps.setString(6, petitionerComplaint_txt);
	            return ps;
	        }, keyHolder);
			
			// Get the last insert ID
	        lastInsertId = (int) keyHolder.getKey().longValue();

	        // Get the value of mp_ackno from application.properties
	        String ackno = environment.getProperty("mp_ackno");
	        String ackno_prefix = environment.getProperty("mp_ackno_prefix");

	        // Convert ackno to int if needed
	        int acknoInt = Integer.parseInt(ackno);

	        // Calculate the petition number
	        int petitionNo = (int) (lastInsertId + acknoInt);

	        // Construct the petition number string
	        String petitionNoString = ackno_prefix + petitionNo;

	        // Update the petition number in the database
	        updatePetitionNumber(lastInsertId, petitionNoString);
	        
	        // Add Petition Scan File in the database
	        addPetitionFile(createdBy,lastInsertId, petitionNoString, page1, page2, page3, page4);
	        
            return petitionNoString; // If no exception, query executed successfully
        } catch (DataAccessException e) {
            // Handle exception (e.g., log error)
            e.printStackTrace();
            return "error"; // Query execution failed
        }
	}
	
	@Transactional
	public boolean updatePetitionNumber(int petitionId,String petitionNoString) {
		String  sqlQuery = "UPDATE `petition` SET `petition_no`='"+petitionNoString+"' WHERE `petition_id`='"+petitionId+"'";
		jdbcTemplate.update(sqlQuery);
		return true;
	}
	
	@Transactional
	public String addPetitionFile(String createdBy, 
			int petitionId,
    		String petitionNoString,
    		MultipartFile page1,
			MultipartFile page2,
			MultipartFile page3,
			MultipartFile page4) {
		
		String fileName="";
        for (int i = 1; i <= 4; i++) {
            MultipartFile file = null;
            switch (i) {
                case 1:
                    file = page1;
                    fileName = "Scan1";
                    break;
                case 2:
                    file = page2;
                    fileName = "Scan2";
                    break;
                case 3:
                    file = page3;
                    fileName = "Scan3";
                    break;
                case 4:
                    file = page4;
                    fileName = "Scan4";
                    break;
            }
            if (file != null && !file.isEmpty()) {
            	try {
                    // Get the bytes of the file
                    byte[] bytes = file.getBytes();
                    
                    // Set the file path where you want to save it
                    String uploadDirectory = environment.getProperty("file.upload.directory");
                    String serviceFolderName = environment.getProperty("mp_foldername");
                    
                    var year = DateTimeUtil.getCurrentYear();
            		var month = DateTimeUtil.getCurrentMonth();

            		uploadDirectory = uploadDirectory + serviceFolderName;// + year + "/" + month;
            		
                    //uploadDirectory = uploadDirectory + serviceFolderName;
                    
                    // File name 
                    fileName = petitionId + "_" + fileName + "_" + petitionNoString + "_" + file.getOriginalFilename();
                    
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
                    String  sqlQuery = "INSERT INTO `pentition_files`(`created_by`, `file_name`, `file_size`, `file_type`, `petition_no`, `petition_id`) "
                    		+ "VALUES ('"+createdBy+"','"+fileName+"','"+fileSize+"','"+fileType+"','"+petitionNoString+"','"+petitionId+"')";
            		jdbcTemplate.update(sqlQuery);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Failed to save file " + file.getOriginalFilename();
                }
            }
            
        }
        return "success";
	}
	
	@Transactional
	public boolean updatePetition(
			String petitionNo,
			String petitionType,
			String createdBy,
			String petitionerName,
			String petitionerMobile,
			String petitionerAddress,
			String petitionerPincode,
			String petitionerComplaint,
			String complaintNature) {
		
		try {
			// Complaint Type List
			List complaintTypelist = getComplaintTypeList();
			// Convert the list of maps to a list of ComplaintType objects
	        List<ComplaintType> complaintTypes = convertToComplaintTypes(complaintTypelist);

			// Map complaint types
	        String petitionerComplaint_txt = mapComplaintTypes(complaintTypes, petitionerComplaint);
			
	        String  sqlQuery = "UPDATE `petition` SET "
	                + "`petition_type`='"+petitionType+"', `created_by`='"+createdBy+"', `petitioner_name`='"+petitionerName+"', `petitioner_mobile`='"+petitionerMobile+"', "
	                + "`petitioner_address`='"+petitionerAddress+"', `petitioner_pincode`='"+petitionerPincode+"',"
	                + "`petitioner_complaint`='"+petitionerComplaint+"', `petitioner_complaint_txt`='"+petitionerComplaint_txt+"', `complaint_nature`='"+complaintNature+"' "
	                + "WHERE (`petition_no`='"+petitionNo+"') LIMIT 1";
	        System.out.println(sqlQuery);
			// Use JdbcTemplate's update method to execute the query with parameters
			jdbcTemplate.update(sqlQuery);
			
			// Add Petition Scan File in the database
	        //addPetitionFile(createdBy,lastInsertId, petitionNoString, page1, page2, page3, page4);
	        
            return true; // If no exception, query executed successfully
        } catch (DataAccessException e) {
            // Handle exception (e.g., log error)
            e.printStackTrace();
            return false; // Query execution failed
        }
	}
	
	@Transactional
	public boolean updatePetitionStatus(String petitionId,String petitionNo) {
		String status_value = "close";
		List<Map<String, Object>> petitionMappingDetails = getPetitionMappingDetails(petitionNo, petitionId);

		for (Map<String, Object> mapping : petitionMappingDetails) {
		    // Access values from the map using keys
		    Object status = mapping.get("status");
		    // Check if status is "close"
		    if (!"close".equals(status)) {
		    	status_value = "partial";
		    }
		    
		}
		
		String sqlQuery = "UPDATE `petition` SET `status`='"+status_value+"' WHERE `petition_no`='"+petitionNo+"' AND `petition_id`='"+petitionId+"' LIMIT 1";
		jdbcTemplate.update(sqlQuery);
		System.out.println(sqlQuery);
		return true;
	}
	
	@Transactional(readOnly = true)
    public List<Map<String,Object>> getPetitionMappingDetails(String petitionNo, String petitionId) {
        try {
            String sqlQuery = "SELECT * FROM petition_mapping WHERE (`petition_id`= ? AND petition_no = ?) AND (`isactive`=1 AND `isdelete`=0)";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, petitionId, petitionNo);
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null; // Return null or handle error appropriately
        }
    }
	
	@Transactional
	public boolean unmapComplient(String petitionId, String petitionNo) {
		    String sqlQuery = "UPDATE `petition_mapping` SET `isactive`='0',`isdelete`='1' WHERE `petition_id`='"+petitionId+"' AND `petition_no`='"+petitionNo+"'";
		    jdbcTemplate.update(sqlQuery);
	    return true;
	}
	
	@Transactional
	public boolean mapComplientToOfficer(
	        String createdBy, String petitionId, String petitionNo, String complaintId, String officerId) {
		    String sqlQuery = "INSERT INTO `petition_mapping`"
		            + "(`created_by`, `off_id`, `petition_no`, `complaint_typeid`, `petition_id`) "
		            + "VALUES (?, ?, ?, ?, ?)";
		    jdbcTemplate.update(sqlQuery, createdBy, officerId, petitionNo, complaintId, petitionId);
	    return true;
	}
	
	@Transactional
	public boolean updateMapComplientStatus(String mapId,String status) {
		    String sqlQuery = "UPDATE `petition_mapping` SET `status`=? WHERE `mapid`=? LIMIT 1";
		    jdbcTemplate.update(sqlQuery, status, mapId);
		    System.out.println(sqlQuery);
	    return true;
	}
	
	@Transactional(readOnly = true)
    public List<Map<String,Object>> getComplaintMappingDetails(String petitionNo, String petitionId, String complaintTypeId) {
        try {
            String sqlQuery = "SELECT * FROM petition_mapping WHERE (`petition_id`= ? AND petition_no = ?) AND `complaint_typeid`= ?  AND (`isactive`=1 AND `isdelete`=0) LIMIT 1";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, petitionId, petitionNo,complaintTypeId);
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null; // Return null or handle error appropriately
        }
    }
	
	@Transactional(readOnly = true)
    public List<Map<String,Object>> getComplaintList(String loginId, String month, String year) {
		// Construct the date pattern using the provided month and year
        String datePattern = year + "-" + month + "-%"; // Example: "2025-02-%"
        
        try {
        	String sqlQuery = "SELECT "
            		+ "    p.`petition_id`, p.`cdate`, p.`created_by`, p.`isactive`, p.`isdelete`, p.`petition_no`, "
            		+ "    p.`petitioner_address`, p.`petitioner_complaint`, p.`petitioner_complaint_txt`, "
            		+ "    p.`petitioner_mobile`, p.`petitioner_name`, p.`complaint_nature`, p.`petitioner_pincode`, "
            		+ "    pm.`mapid`, pm.`complaint_typeid`, pm.`off_id`, pm.`status`, om.`off_nm`, ctype.`complaint_type` "
            		+ " FROM  "
            		+ "    `petition` p, `petition_mapping` pm, `petition_master`.`officer_master` om, `petition_master`.`complaint_types` ctype "
            		+ " WHERE "
            		+ "    (p.`isactive` = 1 AND p.`isdelete` = 0) "
            		+ "	   AND (pm.`isactive` = 1 AND pm.`isdelete` = 0) "
            		+ "	   AND (om.`isactive` = 1 AND om.`isdelete` = 0) "
            		+ "	   AND (om.`off_id` = pm.`off_id`) "
            		+ "	   AND (ctype.`complaint_typeid` = pm.`complaint_typeid`) "
            		+ "    AND (p.`petition_id` = pm.`petition_id`) "
            		+ "    AND DATE(p.cdate) LIKE ?"
            		+ "    ";
        	
        	
        	//if (officerMaster != null && !officerMaster.isEmpty() && loginId!='256') {
        	if (!loginId.equals("491") && !loginId.equals("1") ) {
        		
        		String officerMaster  = getMappingUserDetails.getOfficerMaster(loginId);
        	    sqlQuery = sqlQuery + " AND (pm.off_id IN("+officerMaster+"))";
        	}
        	
        	System.out.println(sqlQuery);
        	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery,datePattern);
        	
        	// Iterate over the list of maps
            for (Map<String, Object> map : result) {
                // Get the value of the 'petitionNo' field from the map
                String petitionNo = (String) map.get("petition_no");
                
                // Encode the 'petitionNo' value using Base64Util
                String petitionNoEncoded = Base64Util.encodeBase64(petitionNo);
                
                // Add the encoded value as 'petitionNoEncode' to the map
                map.put("petitionNoEncode", petitionNoEncoded);
            }
            
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null; // Return null or handle error appropriately
        }
    }
	
	@Transactional(readOnly = true)
    public List<Map<String,Object>> getComplaintListByPetitionId(String petitionId) {
        try {
        	String sqlQuery = "SELECT "
            		+ " p.`petition_id`, p.`cdate`, p.`created_by`, p.`isactive`, p.`isdelete`, p.`petition_no`, "
            		+ " p.`petitioner_address`, p.`petitioner_complaint`, p.`petitioner_complaint_txt`, "
            		+ " p.`petitioner_mobile`, p.`petitioner_name`, p.`complaint_nature`, p.`petitioner_pincode`, p.`status` petition_status, "
            		+ " pm.`mapid`, pm.`complaint_typeid`, pm.`off_id`, pm.`status` complaint_status, pm.`cdate` mappedOn, om.`off_nm`, ctype.`complaint_type` "
            		+ " FROM "
            		+ " 	`mayor_petition`.`petition` p,`mayor_petition`.`petition_mapping` pm, `petition_master`.`officer_master` om, `petition_master`.`complaint_types` ctype "
            		+ " WHERE "
            		+ "		(p.`petition_id` = '"+petitionId+"' AND p.`isactive` = 1 AND p.`isdelete` = 0) "
            		+ "		AND (pm.`isactive` = 1 AND pm.`isdelete` = 0) "
            		+ "		AND (om.`isactive` = 1 AND om.`isdelete` = 0) "
            		+ "		AND (om.`off_id` = pm.`off_id`) "
            		+ "		AND (ctype.`complaint_typeid` = pm.`complaint_typeid`) "
            		+ " 	AND (p.`petition_id` = pm.`petition_id`)"
            		+ " ";
        	System.out.println(sqlQuery);
        	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery);
        	
        	// Iterate over the list of maps
            for (Map<String, Object> map : result) {
                // Get the value of the 'petitionNo' field from the map
                String petitionNo = (String) map.get("petition_no");
                
                // Encode the 'petitionNo' value using Base64Util
                String petitionNoEncoded = Base64Util.encodeBase64(petitionNo);
                
                // Add the encoded value as 'petitionNoEncode' to the map
                map.put("petitionNoEncode", petitionNoEncoded);
            }
            
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null; // Return null or handle error appropriately
        }
    }
	
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getComplaintDetails(String mapid) {
	    try {
	        String sqlQuery = "SELECT "
	                + "    p.`petition_id`, p.`cdate`, p.`created_by`, p.`isactive`, p.`isdelete`, p.`petition_no`, "
	                + "    p.`petitioner_address`, p.`petitioner_complaint`, p.`petitioner_complaint_txt`, "
	                + "    p.`petitioner_mobile`, p.`petitioner_name`, p.`complaint_nature`, p.`petitioner_pincode`, "
	                + "    pm.`mapid`, pm.`complaint_typeid`, pm.`off_id`, pm.`status`, om.`off_nm`, ctype.`complaint_type`, "
	                + "    COALESCE(pf.`mtm_pet_file_id`, 'No file available') AS mtm_pet_file_id, "
	                + "    COALESCE(pf.`cdate`, '') AS file_cdate, "
	                + "    COALESCE(pf.`created_by`, 'N/A') AS file_created_by, "
	                + "    COALESCE(pf.`file_name`, 'No file available') AS file_name, "
	                + "    COALESCE(pf.`file_size`, 0) AS file_size, "
	                + "    COALESCE(pf.`file_type`, 'Unknown') AS file_type, "
	                + "    COALESCE(pf.`isactive`, 0) AS file_isactive, "
	                + "    COALESCE(pf.`isdelete`, 0) AS file_isdelete "
	                + " FROM "
	                + "    `petition` p "
	                + " JOIN `petition_mapping` pm ON p.`petition_id` = pm.`petition_id` "
	                + " JOIN `petition_master`.`officer_master` om ON om.`off_id` = pm.`off_id` "
	                + " JOIN `petition_master`.`complaint_types` ctype ON ctype.`complaint_typeid` = pm.`complaint_typeid` "
	                + " LEFT JOIN `pentition_files` pf ON p.`petition_id` = pf.`petition_id` "
	                + " WHERE "
	                + "    pm.`mapid` = '" + mapid + "' "
	                + "    AND p.`isactive` = 1 AND p.`isdelete` = 0 "
	                + "    AND pm.`isactive` = 1 AND pm.`isdelete` = 0 "
	                + "    AND om.`isactive` = 1 AND om.`isdelete` = 0 ";

	        List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery);

	        // Iterate over the list of maps
	        for (Map<String, Object> map : result) {
	            // Get the value of the 'petitionNo' field from the map
	            String petitionNo = (String) map.get("petition_no");

	            // Encode the 'petitionNo' value using Base64Util
	            String petitionNoEncoded = Base64Util.encodeBase64(petitionNo);

	            // Add the encoded value as 'petitionNoEncode' to the map
	            map.put("petitionNoEncode", petitionNoEncoded);
	        }

	        return result;
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return null; // Return null or handle error appropriately
	    }
	}
	
	@Transactional(readOnly = true)
    public List<Map<String,Object>> getComplaintActionDetails(String mapId) {
        try {
            String sqlQuery = "SELECT * FROM `petition_action_taken` WHERE (`mapid`= ? AND `isactive`=1 AND `isdelete`=0) LIMIT 1";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, mapId);
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null; // Return null or handle error appropriately
        }
    }
	
	@Transactional
	public String saveComplanitStatus(
			String createdBy,
			String mapId,
			String complaintStatus,
			String complaintActionText,
			MultipartFile actionPage1,
			String petitionId,
			String petitionNo) {
		
		//System.out.println("I am from Service Page "+petitionerName);
		
		//String complaintNature = "";
		int lastInsertId = 0;
		String  sqlQuery = "INSERT INTO `petition_action_taken` "
                + "(`created_by`, `off_id`, `mapid`, `status`, "
                + "`comments`) "
                + "VALUES (?, ?, ?, ?, ?)";
		
		try {
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			// Use JdbcTemplate's update method to execute the query with parameters
			jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
	            ps.setString(1, createdBy);
	            ps.setString(2, createdBy);
	            ps.setString(3, mapId);
	            ps.setString(4, complaintStatus);
	            ps.setString(5, complaintActionText);
	            return ps;
	        }, keyHolder);
			
			// Get the last insert ID
	        lastInsertId = (int)keyHolder.getKey().longValue();
	        String actionID = "error";
	        if(lastInsertId>0) {
	        	// Update Complaint Status
	        	updateMapComplientStatus(mapId,"close");
	        	// Update Petition Status
	        	updatePetitionStatus(petitionId,petitionNo);
	        	// Add Action Scan File in the database
		        addActionFile(mapId,lastInsertId, actionPage1);
		        actionID = ""+lastInsertId;
	        }
	        
            return actionID; // If no exception, query executed successfully
        } catch (DataAccessException e) {
            // Handle exception (e.g., log error)
            e.printStackTrace();
            return "error"; // Query execution failed
        }
	}
	
	@Transactional
	public String addActionFile( String mapId, int solmapid,
    		MultipartFile page1) {
		
        MultipartFile file = page1;
        String fileName = "Scan1_"+solmapid;
                   
            if (file != null && !file.isEmpty()) {
            	try {
                    // Get the bytes of the file
                    byte[] bytes = file.getBytes();
                    
                    // Set the file path where you want to save it
                    String uploadDirectory = environment.getProperty("file.upload.directory");
                    String serviceFolderName = environment.getProperty("mp_foldername");
                    String serviceActionFolderName = environment.getProperty("mp_action_Foldername");
                    
                    //uploadDirectory = uploadDirectory + serviceFolderName +"/"+ serviceActionFolderName;
                    
                    var year = DateTimeUtil.getCurrentYear();
            		var month = DateTimeUtil.getCurrentMonth();

            		uploadDirectory = uploadDirectory + serviceFolderName + "/" + serviceActionFolderName;// + year + "/" + month;
                    
                    // File name 
                    fileName = mapId + "_" + fileName + "_" + file.getOriginalFilename();
                    
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
                    String  sqlQuery = "UPDATE `petition_action_taken` SET "
                    					+ "`file_name`='"+fileName+"', "
                    					+ "`file_size`='"+fileSize+"', "
                    					+ "`file_type`='"+fileType+"' "
                    					+ "WHERE `solmapid`='"+solmapid+"' LIMIT 1";
            		jdbcTemplate.update(sqlQuery);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Failed to save file " + file.getOriginalFilename();
                }
            }
        return "success";
	}
	
	
	//Report List
		@Transactional(readOnly = true)
		public List<Map<String, Object>> getReportList(Date fromDate, Date toDate, String petitionType) {
		    try {
		        StringBuilder sql = new StringBuilder(
		            "SELECT p.petition_no, p.petitioner_name, pt.name AS petition_type, \r\n" +
		            "p.petitioner_mobile, p.complaint_nature, zh.off_nm AS officer_name, \r\n" +
		            "zh.off_id AS officer_id, pm.status \r\n" +
		            "FROM mayor_petition.petition_mapping pm \r\n" +
		            "LEFT JOIN mayor_petition.petition p \r\n" +
		            "ON p.petition_id = pm.petition_id \r\n" +
		            "LEFT JOIN petition_master.officer_master_zh zh \r\n" +
		            "ON zh.off_id = pm.off_id \r\n" +
		            "LEFT JOIN petition_master.petition_types pt \r\n" +
		            "ON pt.petition_type_id = p.petition_type \r\n"
		        );

		        List<Object> params = new ArrayList<>();
		        List<String> conditions = new ArrayList<>();

		        if (fromDate != null && toDate != null) {
		            // Ensure toDate includes full day
		            Calendar cal = Calendar.getInstance();
		            cal.setTime(toDate);
		            cal.set(Calendar.HOUR_OF_DAY, 23);
		            cal.set(Calendar.MINUTE, 59);
		            cal.set(Calendar.SECOND, 59);
		            cal.set(Calendar.MILLISECOND, 999);
		            Date toDateEnd = cal.getTime();

		            conditions.add("date(pm.cdate) BETWEEN ? AND ?");
		            params.add(fromDate);
		            params.add(toDateEnd);
		        }

		        if (petitionType != null && !petitionType.trim().isEmpty()) {
		            conditions.add("pt.petition_type_id = ?");
		            params.add(petitionType);
		        }

		        if (!conditions.isEmpty()) {
		            sql.append("WHERE ").append(String.join(" AND ", conditions));
		        }

		        List<Map<String, Object>> result = jdbcTemplate.queryForList(
		            sql.toString(), params.toArray()
		        );

		        for (Map<String, Object> map : result) {
		            String petitionNo = (String) map.get("petition_no");
		            if (petitionNo != null) {
		                map.put("petitionNoEncode", Base64Util.encodeBase64(petitionNo));
		            }
		        }

		        return result;
		    } catch (DataAccessException e) {
		        e.printStackTrace();
		        return Collections.emptyList();
		    }
		}
		
	// Import Old data
	/*
	public String HtmlDecoder(String tamilTxt) {
        // HTML-encoded string
        String encodedString = ""+tamilTxt;

        // Decode the HTML-encoded string using Jsoup
        String decodedString = Jsoup.parse(encodedString).text();
        
        // Output the decoded Tamil text
        System.out.println(decodedString);
        
        return decodedString;
    }
	
	@Transactional
	public String importPetition(
			String oldPetionid,
			String petitionType,
			String createdBy,
			String petitionerName,
			String petitionerMobile,
			String petitionerComplaint,
			String page1,
			String page2,
			String page3,
			String page4,
			String cdate,
			String officerId,
			String address,
			String complainttxt,
			String pin) {
		
		//System.out.println("I am from Service Page "+petitionerName);
		
		//String complaintNature = "";
		int lastInsertId = 0;
		String  sqlQuery = "INSERT INTO `petition` "
                + "(`petition_type`,`created_by`, `petitioner_name`, `petitioner_mobile`, "
                + "`petitioner_complaint`, `petitioner_complaint_txt`,`cdate`,`petitioner_address`,`complaint_nature`,`petitioner_pincode`) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			// Complaint Type List
			List complaintTypelist = getComplaintTypeList();
			// Convert the list of maps to a list of ComplaintType objects
	        List<ComplaintType> complaintTypes = convertToComplaintTypes(complaintTypelist);

			// Map complaint types
	        String petitionerComplaint_txt = mapComplaintTypes(complaintTypes, petitionerComplaint);
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			// Use JdbcTemplate's update method to execute the query with parameters
			jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
	            ps.setString(1, petitionType);
	            ps.setString(2, createdBy);
	            ps.setString(3, petitionerName);
	            ps.setString(4, petitionerMobile);
	            ps.setString(5, petitionerComplaint);
	            ps.setString(6, petitionerComplaint_txt);
	            ps.setString(7, cdate);
	            ps.setString(8, address);
	            ps.setString(9, complainttxt);
	            ps.setString(10, pin);
	            return ps;
	        }, keyHolder);
			
			// Get the last insert ID
	        lastInsertId = (int) keyHolder.getKey().longValue();

	        // Get the value of mp_ackno from application.properties
	        String ackno = environment.getProperty("mp_ackno");
	        String ackno_prefix = environment.getProperty("mp_ackno_prefix");

	        // Convert ackno to int if needed
	        int acknoInt = Integer.parseInt(ackno);

	        // Calculate the petition number
	        int petitionNo = (int) (lastInsertId + acknoInt);

	        // Construct the petition number string
	        String petitionNoString = ackno_prefix + petitionNo;

	        // Update the petition number in the database
	        updatePetitionNumber(lastInsertId, petitionNoString);
	        
	        // Add Petition Scan File in the database
	        //addPetitionFile(createdBy,lastInsertId, petitionNoString, page1, page2, page3, page4);
	     // Set the file path where you want to save it
            String uploadDirectory = environment.getProperty("file.upload.directory");
            String serviceFolderName = environment.getProperty("mp_foldername");
            
            var year = DateTimeUtil.getCurrentYear();
    		var month = DateTimeUtil.getCurrentMonth();

    		uploadDirectory = uploadDirectory + serviceFolderName;// + year + "/" + month;
    		
	        String fileName = page1;//uploadDirectory + "/" + page1;
	        String fileSize = "10000";
	        String fileType = "application/pdf";
	        
	        // Save the file path to a database
            String  sqlFileQuery = "INSERT INTO `pentition_files`(`created_by`, `file_name`, `file_size`, `file_type`, `petition_no`, `petition_id`) "
            		+ "VALUES ('"+createdBy+"','"+fileName+"','"+fileSize+"','"+fileType+"','"+petitionNoString+"','"+lastInsertId+"')";
    		jdbcTemplate.update(sqlFileQuery);
    		
    		String lastInsertIdtxt = ""+lastInsertId;
    		
    		// map Complient Mapping
    		if(mapComplientToOfficer(
    				createdBy, lastInsertIdtxt, petitionNoString, petitionerComplaint, officerId
    				)) {
    			System.out.println("Complient Created : "+ lastInsertIdtxt);
    			
    			String sqlQueryMap = "SELECT * FROM petition_mapping WHERE (`petition_id`= ? AND petition_no = ?) AND `complaint_typeid`= ? AND (`isactive`=1 AND `isdelete`=0) LIMIT 1";
    			List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQueryMap, lastInsertId, petitionNoString, petitionerComplaint);
    			
    			//System.out.println(sqlQueryMap);
    			// Check if result is not empty
    			if (result.size() > 0) {
    			    // Retrieve mapId from the first result
    			    String mapId = String.valueOf(result.get(0).get("mapid"));
    			    
    			    // Save the reply with the retrieved mapId
    			    saveReplay(oldPetionid, mapId, createdBy, lastInsertIdtxt, petitionNoString);
    			}
    		}
    		
            return petitionNoString; // If no exception, query executed successfully
            
        } catch (Exception e) {
            // Handle exception (e.g., log error)
            e.printStackTrace();
            return "error"; // Query execution failed
        }
	}
	
	public String saveReplay(String oldPetionid, String mapId, String createdBy, String petitionId, String petitionNo) {
		try {
			
			String sqlQuery2 = "SELECT mr.PET_ID, mr.FLE_UPD, mr.REPLY_DT, mr.REPLY "
					+ "FROM FAS.MAYOR_REPLY mr "
					+ "WHERE mr.PET_ID = ? "
					+ "AND ROWNUM <= 1";
			List<Map<String, Object>> petData = jdbcOracleTemplate.queryForList(sqlQuery2,oldPetionid);
			
			int lastInsertId = 0;
			for (Map<String, Object> pet : petData) {
				
				String complaintActionText = HtmlDecoder((String) pet.get("REPLY"));
				String status = "close";
				String fileName = (String) pet.get("FLE_UPD");
				//String cdate = (String) pet.get("REPLY_DT");
				String cdate ="";
				Object replyDateObj = pet.get("REPLY_DT");

				if (replyDateObj != null) {
				    if (replyDateObj instanceof Timestamp) {
				        Timestamp timestamp = (Timestamp) replyDateObj;
				        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				         cdate = dateFormat.format(timestamp);
				        // Now you can use cdate
				    } else {
				        // Handle other types or null case
				         cdate = replyDateObj.toString();
				    }
				} else {
				    // Handle the case where REPLY_DT is null
				     cdate = "";
				}
				
				String cdatetxt = cdate;
				System.out.println(cdate);
				
				String  sqlQuery = "INSERT INTO `petition_action_taken` "
		                + "(`created_by`, `off_id`, `mapid`, `status`, "
		                + "`comments`, `cdate`) "
		                + "VALUES (?, ?, ?, ?, ?, ?)";
				
				KeyHolder keyHolder = new GeneratedKeyHolder();
				// Use JdbcTemplate's update method to execute the query with parameters
				jdbcTemplate.update(connection -> {
		            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
		            ps.setString(1, createdBy);
		            ps.setString(2, createdBy);
		            ps.setString(3, mapId);
		            ps.setString(4, status);
		            ps.setString(5, complaintActionText);
		            ps.setString(6, cdatetxt);
		            return ps;
		        }, keyHolder);
				
				// Get the last insert ID
		        lastInsertId = (int)keyHolder.getKey().longValue();
		        String actionID = "error";
		        if(lastInsertId>0) {
		        	// Update Complaint Status
		        	updateMapComplientStatus(mapId,"close");
		        	// Update Petition Status
		        	updatePetitionStatus(petitionId,petitionNo);
		        	// Add Action Scan File in the database
			        //addActionFile(mapId,lastInsertId, actionPage1);
		        	
		        	String uploadDirectory = environment.getProperty("file.upload.directory");
		            String serviceFolderName = environment.getProperty("mp_foldername");
                    String serviceActionFolderName = environment.getProperty("mp_action_Foldername");
                    
		            var year = DateTimeUtil.getCurrentYear();
		    		var month = DateTimeUtil.getCurrentMonth();

		    		uploadDirectory = uploadDirectory + serviceFolderName + serviceActionFolderName;// + year + "/" + month;
		    		
			        //fileName = uploadDirectory + "/" + fileName;
			        String fileSize = "10000";
			        String fileType = "application/pdf";
			        
			        actionID = ""+lastInsertId;
			        
                    // Save the file path to a database
                    String  sqlQueryFile = "UPDATE `petition_action_taken` SET "
                    					+ "`file_name`='"+fileName+"', "
                    					+ "`file_size`='"+fileSize+"', "
                    					+ "`file_type`='"+fileType+"' "
                    					+ "WHERE `solmapid`='"+actionID+"' LIMIT 1";
            		jdbcTemplate.update(sqlQueryFile);
            		
			        
		        }
			}
			return "Success";
		}
		catch (DataAccessException e) {
            e.printStackTrace();
            return "Fail " + e.getMessage();
        }
	}
	
	public String saveOldData() {
        //try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            //Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
			String oldPetionid = "";
            String petitionType = "";
            String petDate = "";
			String createdBy = "";
			String petitionerName = "";
			String petitionerMobile = "";
			String petitionerComplaint = "50";
			String page1 = null;
			String page2 = null;
			String page3 = null;
			String page4 = null;
			String officerId="";
			String complaint_nature="";
			String petitioner_address="";
			String petitioner_pincode="";

			String SqlQuery = "SELECT PET_ID, "
	                + "PET_DT, "
	                + "CASE "
	                + "    WHEN PET_TYPE = 'P' THEN '1' "
	                + "    WHEN PET_TYPE = 'O' THEN '6' "
	                + "    WHEN PET_TYPE = 'N' THEN '5' "
	                + "    ELSE PET_TYPE "  // Keep the original PET_TYPE if none of the conditions match
	                + "END AS PET_TYPE, "
	                + "PET_NM, "
	                + "PET_DET, "
	                + "COMP_TYPE, "
	                + "FLE, "
	                + "HQ_DEPT_ID, "
	                + "OTHR_DEPT,  PET_ADD1 ,PET_ADD2 , PET_ADD3, PET_PIN "
	                + "FROM FAS.MAYOR_PETITION "
	                + "WHERE PET_DT > TO_DATE('2022-09-01', 'YYYY-MM-DD') "
	                + "AND PET_ID LIKE 'M%'";
			List<Map<String, Object>> petData = jdbcOracleTemplate.queryForList(SqlQuery);
			
			// Set the file path where you want to save it
            String uploadDirectory = environment.getProperty("file.upload.directory");
            String serviceFolderName = environment.getProperty("mp_foldername");
            uploadDirectory = uploadDirectory + serviceFolderName;
            int countData=0;
			// Loop through the database data if necessary
	        for (Map<String, Object> pet : petData) {
	        	oldPetionid = (String) pet.get("PET_ID");
	        	countData++;
	            petitionType = (String) pet.get("PET_TYPE");
	            petDate = pet.get("PET_DT").toString(); // Convert date to string
	            createdBy = "1";
	            petitionerName = HtmlDecoder((String) pet.get("PET_NM"));
	            petitionerMobile = "";
	            complaint_nature = HtmlDecoder((String) pet.get("PET_DET"));
	            petitioner_address = (String) pet.get("PET_ADD1") + " " + pet.get("PET_ADD2") + " "+ pet.get("PET_ADD3");
	            petitioner_address = HtmlDecoder(petitioner_address);
	            petitioner_pincode = (String) pet.get("PET_PIN");
	            //petitionerComplaint = ((String) pet.get("COMP_TYPE"));
	            page1 = (String) pet.get("FLE");
	            //officerId = (String) pet.get("OTHR_DEPT");
	         // Retrieve the BigDecimal value and convert it to String
	            BigDecimal officerIdBigDecimal = (BigDecimal) pet.get("OTHR_DEPT");
	            if (officerIdBigDecimal != null) {
	                officerId = officerIdBigDecimal.toString(); // Convert BigDecimal to String
	            } else {
	                officerId = ""; // In case the value is null
	            }

	            BigDecimal petitionerComplaintnum = (BigDecimal) pet.get("COMP_TYPE");
	            if (petitionerComplaintnum != null) {
	            	petitionerComplaint = petitionerComplaintnum.toString(); // Convert BigDecimal to String
	            } else {
	            	petitionerComplaint = ""; // In case the value is null
	            }
	            System.out.println("OnGoing : "+countData + " == " + oldPetionid);
	            // Handle each record from the database
	            importPetition(oldPetionid, petitionType, createdBy, petitionerName,
	                    petitionerMobile, petitionerComplaint,
	                    page1, page2, page3, page4, petDate,officerId,petitioner_address, complaint_nature, petitioner_pincode);
	                    
	            System.out.println("Completed : "+countData + " == " + oldPetionid);
	        }
	        
	        return "Total Data Completed " + countData;
       // } catch (Exception e) {
         //   throw new RuntimeException("Failed to process Excel file: " + e.getMessage());
       // }
       
    }
     * 
        */
}
