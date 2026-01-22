package in.gov.chennaicorporation.gccoffice.zerohours_petition.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.Statement;
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

import in.gov.chennaicorporation.gccoffice.com_petition.data.ComplaintType;
import in.gov.chennaicorporation.gccoffice.service.Base64Util;
import in.gov.chennaicorporation.gccoffice.service.GetMappingUserDetails;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@Service("zeroHoursPetition")
public class Petition {
	private JdbcTemplate jdbcTemplate;
	private final Environment environment;
	private PetitionMaster petitionMaster;
	private GetMappingUserDetails getMappingUserDetails;

	@Autowired
	public Petition(Environment environment,PetitionMaster petitionMaster,GetMappingUserDetails getMappingUserDetails) {
		this.environment = environment;
		this.petitionMaster = petitionMaster;
		this.getMappingUserDetails = getMappingUserDetails;
	}

	
	@Autowired
	public void setDataSource(@Qualifier("mysqlZeroHoursDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	 
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getEvents(){
		String SqlQuery = "";
		
		SqlQuery = "SELECT * FROM `event_master` WHERE `isactive`=1 AND `isdelete`=0 ORDER BY date(`date`) DESC";
		//System.out.println(SqlQuery);
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getCurrentEventList(){
		String SqlQuery = "";
		
		//SqlQuery = "SELECT * FROM event_master WHERE date(`date`)= date(NOW())";
		SqlQuery = "SELECT * FROM event_master WHERE `isactive`=1 AND `isdelete`=0 ORDER BY date(`date`) DESC";
		//System.out.println(SqlQuery);
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public List getComplaintTypeList() {
		return petitionMaster.getComplaintType();
	}
	
	@Transactional(readOnly = true)
	public static List<ComplaintType> convertToComplaintTypes(List<Map<String, Object>> complaintTypeMaps) {
        return complaintTypeMaps.stream()
                .map(ComplaintType::new)
                .collect(Collectors.toList());
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
    public List<Map<String,Object>> getPetitionList(String eventid) {
        try {
            //String sqlQuery = "SELECT * FROM petition WHERE (`isactive`=1 AND `isdelete`=0)";
            String sqlQuery = "SELECT p.* , pt.name petition_type_name FROM petition p, petition_master.petition_types pt "
            		+ "WHERE (p.`isactive`=1 AND p.`isdelete`=0) AND `eventid`='"+eventid+"' AND p.petition_type = pt.petition_type_id ";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery);
            //Base64Util.encodeBase64(sqlQuery);
            
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
    public List<Map<String,Object>> getUnmapPetitionList(String eventid) {
        try {
            String sqlQuery = "SELECT "
            		+ "    p.* "
            		+ "FROM "
            		+ "    `petition` p "
            		+ "WHERE "
            		+ "    (p.`isactive` = 1 AND p.`isdelete` = 0) AND `eventid`='"+eventid+"' ";
            		//+ "    AND NOT EXISTS ("
            		//+ "        SELECT 1 "
            		//+ "        FROM `petition_mapping` pm "
            		//+ "        WHERE (p.`petition_id` = pm.`petition_id`) AND (pm.`isactive` = 1 AND pm.`isdelete` = 0 ) "
            		//+ "    )";
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
            //System.out.println("SELECT * FROM petition WHERE petition_no = '"+petitionNo+"' AND (`isactive`=1 AND `isdelete`=0) LIMIT 1");
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
			String zone,
			String ward,
			String petitionerName,
			String petitionerMobile,
			String petitionerComplaint,
			String complaintNature,
			String eventid,
			MultipartFile page1,
			MultipartFile page2,
			MultipartFile page3,
			MultipartFile page4) {
		
		//System.out.println("I am from Service Page "+petitionerName);
		
		//String complaintNature = "";
		int lastInsertId = 0;
		String  sqlQuery = "INSERT INTO `petition` "
		         + "(`petition_type`, `created_by`, `petitioner_name`, `petitioner_mobile`, "
		         + "`petitioner_complaint`, `petitioner_complaint_txt`, `eventid`, `zone`, `ward`,`complaint_nature`) "
		         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			// Complaint Type List
			//List complaintTypelist = getComplaintTypeList();
			// Convert the list of maps to a list of ComplaintType objects
	        //List<ComplaintType> complaintTypes = convertToComplaintTypes(complaintTypelist);

			// Map complaint types
	        //String petitionerComplaint_txt = mapComplaintTypes(complaintTypes, petitionerComplaint);
			String petitionerComplaint_txt="";
			KeyHolder keyHolder = new GeneratedKeyHolder();
			// Use JdbcTemplate's update method to execute the query with parameters
			jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
	            ps.setString(1, "1");
	            ps.setString(2, createdBy);
	            ps.setString(3, petitionerName);
	            ps.setString(4, petitionerMobile);
	            ps.setString(5, petitionerComplaint);
	            ps.setString(6, petitionerComplaint_txt);
	            ps.setString(7, eventid);
	            ps.setString(8, zone);
	            ps.setString(9, ward);
	            ps.setString(10, complaintNature);
	            return ps;
	        }, keyHolder);
			
			// Get the last insert ID
	        lastInsertId = (int) keyHolder.getKey().longValue();

	        // Get the value of mp_ackno from application.properties
	        String ackno = environment.getProperty("zh_ackno");
	        String ackno_prefix = environment.getProperty("zh_ackno_prefix");

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
                    String serviceFolderName = environment.getProperty("zh_foldername");
                    uploadDirectory = uploadDirectory + serviceFolderName;
                    
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
			/*
	        String  sqlQuery = "UPDATE `petition` SET "
	                //+ "`petition_type`='"+petitionType+"', "
	                		+ "`created_by`='"+createdBy+"', `petitioner_name`='"+petitionerName+"', `petitioner_mobile`='"+petitionerMobile+"', "
	                //+ "`petitioner_address`='"+petitionerAddress+"', `petitioner_pincode`='"+petitionerPincode+"',"
	                + "`petitioner_complaint`='"+petitionerComplaint+"', `petitioner_complaint_txt`='"+petitionerComplaint_txt+"', `complaint_nature`='"+complaintNature+"' "
	                + "WHERE (`petition_no`='"+petitionNo+"') LIMIT 1";
	        System.out.println(sqlQuery);
			// Use JdbcTemplate's update method to execute the query with parameters
			jdbcTemplate.update(sqlQuery);
			*/
			String sqlQuery = "UPDATE `petition` SET "
	                + "`petition_type` = ?, `created_by` = ?, `petitioner_name` = ?, `petitioner_mobile` = ?, "
	                + "`petitioner_address` = ?, `petitioner_pincode` = ?, `petitioner_complaint` = ?, "
	                + "`petitioner_complaint_txt` = ?, `complaint_nature` = ? "
	                + "WHERE `petition_no` = ? LIMIT 1";

	        int rowsUpdated = jdbcTemplate.update(sqlQuery, 
	        	    petitionType, 
	        	    createdBy, 
	        	    petitionerName, 
	        	    petitionerMobile, 
	        	    petitionerAddress, 
	        	    petitionerPincode, 
	        	    petitionerComplaint, 
	        	    petitionerComplaint_txt,  // Safely includes single quotes
	        	    complaintNature, 
	        	    petitionNo
	        	);
			
			// Add Petition Scan File in the database
	        // addPetitionFile(createdBy,lastInsertId, petitionNoString, page1, page2, page3, page4);
	        
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
	
	@Transactional(readOnly = true)
    public List<Map<String,Object>> getPetitionMappingDetails2(String petitionId) {
        try {
            String sqlQuery = "SELECT pm.mapid,pm.petition_no,pm.petition_id,pm.status,om.* FROM zerohours_petition.petition_mapping as pm,petition_master.officer_master_zh as om WHERE (pm.`petition_id`= ?) AND (pm.`isactive`=1 AND pm.`isdelete`=0) AND pm.off_id=om.off_id";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, petitionId);
            System.out.println(sqlQuery + petitionId);
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
	public String unmapPetition(String mapid) {
		    String sqlQuery = "UPDATE `petition_mapping` SET `isactive`='0',`isdelete`='1' WHERE `mapid`='"+mapid+"'";
		    jdbcTemplate.update(sqlQuery);
	    return "success";
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
    public List<Map<String,Object>> getComplaintList(String eventId, String loginId) {
		String sqlQuery = "";
        try {
        	sqlQuery = "SELECT "
            		+ "    p.`petition_id`, p.`cdate`, p.`created_by`, p.`isactive`, p.`isdelete`, p.`petition_no`, "
            		+ "    p.`petitioner_address`, p.`petitioner_complaint`, p.`petitioner_complaint_txt`, "
            		+ "    p.`petitioner_mobile`, p.`petitioner_name`, p.`complaint_nature`, p.`petitioner_pincode`, "
            		+ "    pm.`mapid`, pm.`complaint_typeid`, pm.`off_id`, pm.`status`, om.`off_nm` "
            		+ " FROM  "
            		+ "    `petition` p, `petition_mapping` pm, `petition_master`.`officer_master_zh` om"
            		+ " WHERE "
            		+ "    (p.`isactive` = 1 AND p.`isdelete` = 0) "
            		+ "	   AND (pm.`isactive` = 1 AND pm.`isdelete` = 0) "
            		+ "	   AND (om.`isactive` = 1 AND om.`isdelete` = 0) "
            		+ "	   AND (om.`off_id` = pm.`off_id`) "
            		//+ "	   AND (ctype.`complaint_typeid` = pm.`complaint_typeid`) "
            		+ "    AND (p.`petition_id` = pm.`petition_id`)"
            		+ " AND p.eventid='"+eventId+"' ";
        	
        	String officerMaster  = getMappingUserDetails.getOfficerMaster(loginId);
    		
    		//if (officerMaster != null && !officerMaster.isEmpty() && loginId!='256') {
        	if (!loginId.equals("256") && !loginId.equals("1")) {
        	    sqlQuery = sqlQuery + " AND (pm.off_id IN("+officerMaster+"))";
        	}
    		
        	System.out.println(sqlQuery + loginId);
        	
        	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery);
        	
        	// Iterate over the list of maps
            for (Map<String, Object> map : result) {
                // Get the value of the 'petitionNo' field from the map
                String petitionNo = (String) map.get("petition_no");
                
                // Encode the 'petitionNo' value using Base64Util
                String petitionNoEncoded = Base64Util.encodeBase64(petitionNo);
                //System.out.println(petitionNo + " : "+petitionNoEncoded + " : "+Base64Util.encodeBase64(petitionNo));
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
            		+ " 	`zerohours_petition`.`petition` p,`zerohours_petition`.`petition_mapping` pm, `petition_master`.`officer_master_zh` om, `petition_master`.`complaint_types` ctype "
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
    public List<Map<String,Object>> getComplaintDetails(String mapid) {
        try {
        	String sqlQuery = "SELECT "
            		+ "    p.`petition_id`, p.`cdate`, p.`created_by`, p.`isactive`, p.`isdelete`, p.`petition_no`, "
            		+ "    p.`petitioner_address`, p.`petitioner_complaint`, p.`petitioner_complaint_txt`, "
            		+ "    p.`petitioner_mobile`, p.`petitioner_name`, p.`complaint_nature`, p.`petitioner_pincode`, "
            		+ "    pm.`mapid`, pm.`complaint_typeid`, pm.`off_id`, pm.`status`, om.`off_nm` "
            		+ " FROM "
            		+ "    `petition` p,`petition_mapping` pm, `petition_master`.`officer_master_zh` om"
            		+ " WHERE "
            		+ "	   pm.`mapid`='"+mapid+"' " 
            		+ "    AND (p.`isactive` = 1 AND p.`isdelete` = 0) "
            		+ "	   AND (pm.`isactive` = 1 AND pm.`isdelete` = 0) "
            		+ "	   AND (om.`isactive` = 1 AND om.`isdelete` = 0) "
            		+ "	   AND (om.`off_id` = pm.`off_id`) "
            		//+ "	   AND (ctype.`complaint_typeid` = pm.`complaint_typeid`) "
            		+ "    AND (p.`petition_id` = pm.`petition_id`)"
            		+ "    ";
        	//System.out.println(sqlQuery);
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
		String selectQuery ="Select solmapid,count(`solmapid`) as total FROM `petition_action_taken` WHERE mapid=?";
		List<Map<String, Object>> result = jdbcTemplate.queryForList(selectQuery, mapId);
		int total = (result.isEmpty()) ? 0 : ((Number) result.get(0).get("total")).intValue();

	    String sqlQuery;
	    boolean isUpdate = total > 0; // If total > 0, we will update, otherwise insert
	    System.out.println("selectQuery: "+selectQuery);
	    System.out.println("isUpdate: "+isUpdate +" mapId: "+mapId);
	    if (isUpdate) {
	    	lastInsertId = (int) result.get(0).get("solmapid");
	        // Update existing record
	        sqlQuery = "UPDATE `petition_action_taken` SET `created_by` = ?,  `off_id` = ?,`mapid`=?, `status` = ?, `comments` = ? WHERE `mapid` = '"+mapId+"'";
	    } else {
	        // Insert new record
	        sqlQuery = "INSERT INTO `petition_action_taken` "
	                + "(`created_by`, `off_id`, `mapid`, `status`, `comments`) "
	                + "VALUES (?, ?, ?, ?, ?)";
	    }
		
		try {
		    KeyHolder keyHolder = new GeneratedKeyHolder();
		    int rowsAffected = jdbcTemplate.update(connection -> {
		        PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
		        ps.setString(1, createdBy);
		        ps.setString(2, createdBy);
		        ps.setString(3, mapId);
		        ps.setString(4, complaintStatus);
		        ps.setString(5, complaintActionText);
		        return ps;
		    }, keyHolder);
		    
		    if (rowsAffected > 0) {
		    	if (!isUpdate) {
			        Number key = keyHolder.getKey();
			        if (key != null) {
			            lastInsertId = key.intValue();
			            // Continue with further processing
			        } else {
			        	
			            // Handle the case when the key is null
			            System.out.println("Insertion succeeded but no key was generated.");
			            return String.valueOf(rowsAffected); // Or some other appropriate response
			        }
		    	}
		    } else {
		        System.out.println("Insertion failed, no rows affected.");
		        return "error";
		    }

		    // Further logic to update status and add files
		    if (lastInsertId > 0) {
		        updateMapComplientStatus(mapId, "close");
		        updatePetitionStatus(petitionId, petitionNo);
		        addActionFile(mapId, lastInsertId, actionPage1);
		        return String.valueOf(lastInsertId);
		    }

		} catch (DataAccessException e) {
		    e.printStackTrace();
		    return "error";
		}
		return "";
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
                    String serviceFolderName = environment.getProperty("zh_foldername");
                    String serviceActionFolderName = environment.getProperty("zh_action_Foldername");
                    
                    uploadDirectory = uploadDirectory + serviceFolderName +"/"+ serviceActionFolderName;
                    
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
		public List<Map<String, Object>> getReportList(Date fromDate, Date toDate) {
		    try {
		        StringBuilder sql = new StringBuilder(
		            "SELECT p.petition_no, p.zone, p.ward, p.petitioner_name, " +
		            "p.petitioner_mobile, p.complaint_nature, zh.off_nm AS officer_name, " +
		            "zh.off_id AS officer_id, pm.status " +
		            "FROM zerohours_petition.petition_mapping pm " +
		            "LEFT JOIN zerohours_petition.petition p " +
		            "ON p.petition_id = pm.petition_id " +
		            "LEFT JOIN petition_master.officer_master_zh zh " +
		            "ON zh.off_id = pm.off_id "
		        );

		        List<Object> params = new ArrayList<>();

		        if (fromDate != null && toDate != null) {
		            // Ensure toDate includes full day
		            Calendar cal = Calendar.getInstance();
		            cal.setTime(toDate);
		            cal.set(Calendar.HOUR_OF_DAY, 23);
		            cal.set(Calendar.MINUTE, 59);
		            cal.set(Calendar.SECOND, 59);
		            cal.set(Calendar.MILLISECOND, 999);
		            Date toDateEnd = cal.getTime();

		            sql.append("WHERE date(pm.cdate) BETWEEN ? AND ? ");
		            params.add(fromDate);
		            params.add(toDateEnd);
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
}
