package in.gov.chennaicorporation.gccoffice.vendor.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;



@Service
public class StreetVendorService {
	
	
	 @Autowired
	    @Qualifier("vendorDateTimeUtil")
	    private DateTimeUtil dateTimeUtil;
	
	
	 private final Environment environment;
	 
	 private String fileBaseUrl;
	
	private JdbcTemplate jdbcTemplate;
	
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlGccStreetVendorDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	 private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	    private static final int STRING_LENGTH = 15;
	    private static final Random RANDOM = new SecureRandom();

	
	 public StreetVendorService(Environment environment) {
	        this.environment = environment;
	        this.fileBaseUrl = environment.getProperty("fileBaseUrl");
	    }
	
	
	 public List<Map<String, Object>> fetchCommunities() {
	        String query = "SELECT id,category_name " +
	                       "FROM social_category_master " +
	                       "WHERE is_active=1 AND is_delete=0 ";
	     
	        
	        return jdbcTemplate.queryForList(query);
	    }
	
	
	
	 public List<Map<String, Object>> fetchMaritalStatus() {
	        String query = "SELECT id,marital_status " +
	                       "FROM marital_status_master " +
	                       "WHERE is_active=1 AND is_delete=0 ";
	     
	        
	        return jdbcTemplate.queryForList(query);
	    }
	
	 

	 public List<Map<String, Object>> fetchEducationStatus() {
	        String query = "SELECT id,education " +
	                       "FROM education_master " +
	                       "WHERE is_active=1 AND is_delete=0 ";
	     
	        
	        return jdbcTemplate.queryForList(query);
	    }
	 
	 public List<Map<String, Object>> fetchvendingCategory() {
	        String query = "SELECT id,vending_category_name " +
	                       "FROM vending_category_master " +
	                       "WHERE is_active=1 AND is_delete=0 ";
	     
	        
	        return jdbcTemplate.queryForList(query);
	    }
	 
	 public List<Map<String, Object>> fetchtamilvendingCategory() {
	        String query = "SELECT id,tamil_vending_category_name " +
	                       "FROM tamil_vending_category_master " +
	                       "WHERE is_active=1 AND is_delete=0 ";
	     
	        
	        return jdbcTemplate.queryForList(query);
	    }
	 
	 
//	 public List<Map<String, Object>> fetchZones() {
//	        String query = "SELECT id,zone_name " +
//	                       "FROM zone_master " +
//	                       "WHERE is_active=1 AND is_delete=0 ";
//	     
//	        
//	        return jdbcTemplate.queryForList(query);
//	    }
	 
//	 public List<Map<String, Object>> fetchWardsByZone(int zoneId) {
//	        String sql = "SELECT id, ward_name FROM ward_master WHERE zone_id = ? ";
//	        return jdbcTemplate.queryForList(sql, zoneId);
//	    }
	
	 
//	 public String saveStreetVendorDetails(String latitude,String longitude,String zone, String ward, String landmark, String vending_district,
//				int vending_pincode, int vending_category, String vending_space, boolean pm_svanidhi_loan,
//				boolean bank_acc_status, String bankPassbookPath, String streetVendorPhotoPath, String name,
//				String f_h_name, String mobile_number, String dob, String gender, int social_category, boolean diff_abled,
//				int education_status, int marital_status, int no_of_fam_mem, boolean are_any_fam_mem_invol_str,
//				String presentAddress, String present_district, int present_pincode, String rationCardPath,
//				 String aadharFrontPhotoPath, String aadharBackPhotoPath,String aadharNo) {
//
//		 
//		 
//			String sql = "INSERT INTO vendor_details ("
//					+ "latitude,longitude,zone, ward, vending_address,vending_district, vending_pincode, vending_category, vending_space, pm_svanidhi_loan, "
//					+ "bank_acc_status, bank_passbook, street_vendor_photo, name, f_h_name, mob_no, "
//					+ "dob,gender,social_category, diff_abled, education_status, marital_status,no_of_fam_mem, "
//					+ "are_any_fam_mem_invol_str_ven,present_address, present_district, present_pincode, ration_card_photo, aadhar_front_photo, "
//					+ "aadhar_back_photo,aadhar_no"
//					+ ") VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
//
//			KeyHolder keyHolder = new GeneratedKeyHolder();
//
//			try {
//				int result = jdbcTemplate.update(connection -> {
//					PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
//					ps.setString(1, latitude);
//					ps.setString(2, longitude);
//					ps.setString(3, zone);
//					ps.setString(4, ward);
//					ps.setString(5, landmark);
//					ps.setString(6, vending_district);
//					ps.setInt(7, vending_pincode);
//					ps.setInt(8, vending_category);
//					ps.setString(9, vending_space);
//					ps.setBoolean(10, pm_svanidhi_loan);
//					ps.setBoolean(11, bank_acc_status);
//					ps.setString(12, bankPassbookPath);
//					ps.setString(13, streetVendorPhotoPath);
//					ps.setString(14, name);
//					ps.setString(15, f_h_name);
//					ps.setString(16, mobile_number);
//					ps.setString(17, dob);
//					ps.setString(18, gender);
//					ps.setInt(19, social_category);
//					ps.setBoolean(20, diff_abled);
//					ps.setInt(21, education_status);
//					ps.setInt(22, marital_status);
//					ps.setInt(23, no_of_fam_mem);
//					ps.setBoolean(24, are_any_fam_mem_invol_str);
//					ps.setString(25, presentAddress);
//					ps.setString(26, present_district);
//					ps.setInt(27, present_pincode);
//		            ps.setString(28, rationCardPath);
//		            ps.setString(29, aadharFrontPhotoPath);
//		            ps.setString(30, aadharBackPhotoPath);
//		            ps.setString(31, aadharNo);
//					return ps;
//				}, keyHolder);
//				//return keyHolder.getKey().intValue();
//				if (result > 0) {
//		            int generatedId = keyHolder.getKey().intValue();
//		            String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//		            String refId = prefix + generatedId;
//
//		            String updateSql = " UPDATE vendor_details SET vendor_req_id = ? WHERE id = ? ";
//		            jdbcTemplate.update(updateSql,refId, generatedId);
//		            return refId;
//		        }
//		        //return "error";
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return "error";
//		}
	 
	 
	 public String saveStreetVendorDetails(String latitude,String longitude,String zone, String ward, String landmark, String vending_district,
				Integer vending_pincode, Integer vending_category, String vending_space, Boolean pm_svanidhi_loan,
				Boolean bank_acc_status, String bankPassbookPath, String streetVendorPhotoPath, String name,
				String f_h_name, String mobile_number, String dob, String gender, Integer social_category, Boolean diff_abled,
				Integer education_status, Integer marital_status, Integer no_of_fam_mem, Boolean are_any_fam_mem_invol_str,
				String presentAddress, String present_district, Integer present_pincode, String rationCardPath,
				 String aadharFrontPhotoPath, String aadharBackPhotoPath,String aadharNo,String userId,String tamil_name,String tamil_f_h_name,String tamil_landmark,
				 String tamil_gender,String tamil_present_district,@RequestParam Integer tamilvending_category) {

		 	int isWeb=1;
		 	String latitudeVal = (latitude != null && !latitude.trim().isEmpty()) ? latitude : null;
		 	String longitudeVal = (longitude != null && !longitude.trim().isEmpty()) ? longitude : null;
		 	String zoneVal = (zone != null && !zone.trim().isEmpty()) ? zone : null;
		 	String wardVal = (ward != null && !ward.trim().isEmpty()) ? ward : null;
		 	String dobVal = (dob != null && !dob.trim().isEmpty()) ? dob : null;
		 	String vendingAddressVal = (landmark != null && !landmark.trim().isEmpty()) ? landmark : null;
		 
		 	
		 	
			String sql = "INSERT INTO vendor_details ("
					+ "latitude,longitude,zone, ward, vending_address,vending_district, vending_pincode, vending_category, vending_space, pm_svanidhi_loan, "
					+ "bank_acc_status, bank_passbook, street_vendor_photo, name, f_h_name, mob_no, "
					+ "dob,gender,social_category, diff_abled, education_status, marital_status,no_of_fam_mem, "
					+ "are_any_fam_mem_invol_str_ven,present_address, present_district, present_pincode, ration_card_photo, aadhar_front_photo, "
					+ "aadhar_back_photo,aadhar_no,is_web,cby,tamil_name,tamil_f_h_name,tamil_landmark,tamil_gender,tamil_present_district,tamil_vending_category "
					+ ") VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?)";

			KeyHolder keyHolder = new GeneratedKeyHolder();

			try {
				int result = jdbcTemplate.update(connection -> {
					PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
					ps.setString(1, latitudeVal);
					ps.setString(2, longitudeVal);
					ps.setString(3, zoneVal);
					ps.setString(4, wardVal);
					ps.setString(5, vendingAddressVal);
					ps.setString(6, vending_district);
					if (vending_pincode != null) ps.setInt(7, vending_pincode);
					else ps.setNull(7, java.sql.Types.INTEGER);

					if (vending_category != null) ps.setInt(8, vending_category);
					else ps.setNull(8, java.sql.Types.INTEGER);
					ps.setString(9, vending_space);
					if (pm_svanidhi_loan != null) ps.setBoolean(10, pm_svanidhi_loan);
					else ps.setNull(10, java.sql.Types.BOOLEAN);

					if (bank_acc_status != null) ps.setBoolean(11, bank_acc_status);
					else ps.setNull(11, java.sql.Types.BOOLEAN);
					ps.setString(12, bankPassbookPath);
					ps.setString(13, streetVendorPhotoPath);
					ps.setString(14, name);
					ps.setString(15, f_h_name);
					ps.setString(16, mobile_number);
					ps.setString(17, dobVal); 
					ps.setString(18, gender);
					if (social_category != null) ps.setInt(19, social_category);
					else ps.setNull(19, java.sql.Types.INTEGER);
					if (diff_abled != null) ps.setBoolean(20, diff_abled);
					else ps.setNull(20, java.sql.Types.BOOLEAN);
					if (education_status != null) ps.setInt(21, education_status);
					else ps.setNull(21, java.sql.Types.INTEGER);

					if (marital_status != null) ps.setInt(22, marital_status);
					else ps.setNull(22, java.sql.Types.INTEGER);

					if (no_of_fam_mem != null) ps.setInt(23, no_of_fam_mem);
					else ps.setNull(23, java.sql.Types.INTEGER);
					if (are_any_fam_mem_invol_str != null) ps.setBoolean(24, are_any_fam_mem_invol_str);
					else ps.setNull(24, java.sql.Types.BOOLEAN);
					ps.setString(25, presentAddress);
					ps.setString(26, present_district);
					if (present_pincode != null) ps.setInt(27, present_pincode);
					else ps.setNull(27, java.sql.Types.INTEGER);
		            ps.setString(28, rationCardPath);
		            ps.setString(29, aadharFrontPhotoPath);
		            ps.setString(30, aadharBackPhotoPath);
		            ps.setString(31, aadharNo);
		            ps.setInt(32, isWeb);
		            ps.setString(33, userId);
		            ps.setString(34, tamil_name);
		            ps.setString(35, tamil_f_h_name);
		            ps.setString(36, tamil_landmark);
		            ps.setString(37, tamil_gender);
		            ps.setString(38, tamil_present_district);
		            if (tamilvending_category != null) ps.setInt(39, tamilvending_category);
					else ps.setNull(39, java.sql.Types.INTEGER);
					return ps;
				}, keyHolder);
				//return keyHolder.getKey().intValue();
				if (result > 0) {
		            int generatedId = keyHolder.getKey().intValue();
		            String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		            String refId = prefix + generatedId;

		            String updateSql = " UPDATE vendor_details SET vendor_req_id = ? WHERE id = ? ";
		            jdbcTemplate.update(updateSql,refId, generatedId);
		            return refId;
		        }
		        //return "error";
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "error";
		}
	
	 
	 
	 
	 public String fileUpload(MultipartFile file, String name) {

	        // Set the file path where you want to save it
	        String uploadDirectory = environment.getProperty("file.upload.directory.mobile");
	        String serviceFolderName = environment.getProperty("street_vendor_foldername");
	        var year = DateTimeUtil.getCurrentYear();
	        var month = DateTimeUtil.getCurrentMonth();

	        uploadDirectory = uploadDirectory + serviceFolderName + "/" + year + "/" + month;

	        try {
	            // Create directory if it doesn't exist
	            Path directoryPath = Paths.get(uploadDirectory);
	            if (!Files.exists(directoryPath)) {
	                Files.createDirectories(directoryPath);
	            }

	            // Datetime string
	            String datetimetxt = DateTimeUtil.getCurrentDateTime();
	           
	            // File name
	            String fileName = name + "_" + datetimetxt + "_" + file.getOriginalFilename();
	            fileName = fileName.replaceAll("\\s+", ""); // Remove space on filename

	            String filePath = uploadDirectory + "/" + fileName;

	            String filepath_txt = "/" + serviceFolderName + "/" + year + "/" + month + "/" + fileName;

	            // Create a new Path object
	            Path path = Paths.get(filePath);

	            // Get the bytes of the file
	            byte[] bytes = file.getBytes();

				/*
				 * // Compress the image BufferedImage image = ImageIO.read(new
				 * ByteArrayInputStream(bytes)); byte[] compressedBytes = compressImage(image,
				 * 0.5f); // Compress with 50% quality
				 * 
				 * // Write the bytes to the file 
				 * Files.write(path, compressedBytes);
				 */
	            
	            Files.write(path, bytes);

	            //System.out.println(filePath);
	            return filepath_txt;

	        } catch (IOException e) {
	            e.printStackTrace();
	            return "Failed to save file " + file.getOriginalFilename();
	        }
	    }
	 
	 
	 private byte[] compressImage(BufferedImage image, float quality) throws IOException {
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);

	        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
	        writer.setOutput(imageOutputStream);

	        ImageWriteParam param = writer.getDefaultWriteParam();
	        if (param.canWriteCompressed()) {
	            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	            param.setCompressionQuality(quality);
	        }

	        writer.write(null, new javax.imageio.IIOImage(image, null, null), param);

	        writer.dispose();
	        imageOutputStream.close();

	        return byteArrayOutputStream.toByteArray();
	    }

	 
	 public static String generateRandomString() {
	        StringBuilder result = new StringBuilder(STRING_LENGTH);
	        for (int i = 0; i < STRING_LENGTH; i++) {
	            result.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
	        }
	        return result.toString();
	    }


	 
	 
	 public boolean isMobileRegistered(String mobilenumber) {
	        String sql = "SELECT COUNT(*) FROM vendor_details WHERE mob_no = ?";
	        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{mobilenumber}, Integer.class);
	        return count != null && count > 0;
	    }
	 
	 public Integer findLatestVendorIdByMobile(String mobile) {
		    String sql = "SELECT id FROM vendor_details WHERE mob_no = ? ORDER BY id DESC LIMIT 1";

		    try {
		        return jdbcTemplate.queryForObject(sql, new Object[]{mobile}, Integer.class);
		    } catch (Exception e) {
		        return null;  // mobile not registered
		    }
		}

	 
	 public List<Map<String, Object>> getCommiteeDetailsWeb(String cby) {
			String sql = "SELECT * "
			 		+ "FROM vendor_details "
			 		+ "WHERE is_web=1 AND cby=?";
			return jdbcTemplate.queryForList(sql, cby);
	     
		}


	public List<Map<String, Object>> getwebVendorDetailsByReqId(String requestId) {
		
		String sql="SELECT vd.*,em.education as education_name,mm.marital_status as marital_name,scm.category_name as category_name,vcm.vending_category_name as vending_category_name, "+
				 "CONCAT(vd.present_address,',',vd.present_district) as residental_address, CONCAT(vd.vending_address,',',vending_district) as business_address, "+
	             "CONCAT('" + fileBaseUrl + "/gccofficialapp/files', vd.bank_passbook) AS view_bank_passbook, " +
	             "CONCAT('" + fileBaseUrl + "/gccofficialapp/files', vd.ration_card_photo) AS view_ration_card_photo, " +
	             "CONCAT('" + fileBaseUrl + "/gccofficialapp/files', vd.street_vendor_photo) AS view_street_vendor_photo, " +
	             "CONCAT('" + fileBaseUrl + "/gccofficialapp/files', vd.aadhar_front_photo) AS view_aadhar_front_photo, " +
	             "CONCAT('" + fileBaseUrl + "/gccofficialapp/files', vd.aadhar_back_photo) AS view_aadhar_back_photo "
	             + "FROM vendor_details vd "
	             + "LEFT JOIN social_category_master scm on vd.social_category = scm.id "
	             + "LEFT JOIN vending_category_master vcm ON vd.vending_category=vcm.id "
	             + "LEFT JOIN education_master em on vd.education_status = em.id "
	             + "LEFT JOIN marital_status_master mm on vd.marital_status = mm.id "
	             + "WHERE vd.vendor_req_id = ?";
		
		return jdbcTemplate.queryForList(sql,requestId);
	}


	public String updateStreetVendorDetailsWeb(String latitude, String longitude, String zone, String ward,
			String landmark, String vending_district, Integer vending_pincode, Integer vending_category,
			String vending_space, Boolean pm_svanidhi_loan, Boolean bank_acc_status, String bankPassbookPath,
			String streetVendorPhotoPath, String name, String f_h_name, String mobile_number, String dob, String gender,
			Integer social_category, Boolean diff_abled, Integer education_status, Integer marital_status,
			Integer no_of_fam_mem, Boolean are_any_fam_mem_invol_str, String presentAddress, String present_district,
			Integer present_pincode, String rationCardPath, String aadharFrontPhotoPath, String aadharBackPhotoPath,
			String aadharNo,String tamil_name,String tamil_f_h_name, String tamil_landmark,String tamil_gender,String tamil_present_district,Integer tamilvending_category, String userId, String vendor_req_id) {


		String latitudeVal = (latitude != null && !latitude.trim().isEmpty()) ? latitude : null;
	 	String longitudeVal = (longitude != null && !longitude.trim().isEmpty()) ? longitude : null;
	 	String zoneVal = (zone != null && !zone.trim().isEmpty()) ? zone : null;
	 	String wardVal = (ward != null && !ward.trim().isEmpty()) ? ward : null;
	 	String dobVal = (dob != null && !dob.trim().isEmpty()) ? dob : null;
	 	String vendingAddressVal = (landmark != null && !landmark.trim().isEmpty()) ? landmark : null;
	 	
	 	String sql="UPDATE vendor_details SET "
	 			+ "latitude =?,longitude=?,zone=?, ward=?, vending_address=?,vending_district=?, vending_pincode=?, vending_category=?, vending_space=?, pm_svanidhi_loan=?, "
				+ "bank_acc_status=?, bank_passbook=?, street_vendor_photo=?, name=?, f_h_name=?, mob_no=?, "
				+ "dob=?,gender=?,social_category=?, diff_abled=?, education_status=?, marital_status=?,no_of_fam_mem=?, "
				+ "are_any_fam_mem_invol_str_ven=?,present_address=?, present_district=?, present_pincode=?, ration_card_photo=?, aadhar_front_photo=?, "
				+ "aadhar_back_photo=?,aadhar_no=?,updated_date=NOW(),tamil_name=?,tamil_f_h_name=?,tamil_landmark=?,tamil_gender=?,tamil_present_district=?,tamil_vending_category=?,edit_by=? "
	 			+ " WHERE vendor_req_id = ?  AND editable=1";
	 	
	 	//AND is_web=1
		try {
			int rows=jdbcTemplate.update(sql, latitudeVal, longitudeVal, zoneVal, wardVal, vendingAddressVal, vending_district,
					vending_pincode, vending_category, vending_space, pm_svanidhi_loan, bank_acc_status, bankPassbookPath,
					streetVendorPhotoPath, name, f_h_name, mobile_number, dobVal, gender, social_category, diff_abled,
					education_status, marital_status, no_of_fam_mem, are_any_fam_mem_invol_str, presentAddress,
					present_district, present_pincode, rationCardPath, aadharFrontPhotoPath, aadharBackPhotoPath, aadharNo,
					tamil_name,tamil_f_h_name,tamil_landmark,tamil_gender,tamil_present_district,tamilvending_category,userId,
					vendor_req_id);
			
			if (rows==1) {
				return "Success";
			}
			else {
				return "Error";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "Error";
	}


	public String logEntry(String userId, String vendor_req_id) {
		String sql="INSERT INTO vendor_details_web_log (name,f_h_name,gender,dob,social_category,diff_abled,mob_no,aadhar_no,present_address,present_district,present_pincode,vending_address,vending_district,vending_pincode,vending_category,vending_space,bank_acc_status,bank_passbook,ration_card_photo,street_vendor_photo,aadhar_front_photo,aadhar_back_photo,zone,ward,pm_svanidhi_loan,vendor_req_id,education_status,marital_status,no_of_fam_mem,are_any_fam_mem_invol_str_ven,latitude,longitude,tamil_name,tamil_f_h_name,tamil_landmark,tamil_gender,tamil_present_district,tamil_vending_category,cby) "
				+ " SELECT name,f_h_name,gender,dob,social_category,diff_abled,mob_no,aadhar_no,present_address,present_district,present_pincode,vending_address,vending_district,vending_pincode,vending_category,vending_space,bank_acc_status,bank_passbook,ration_card_photo,street_vendor_photo,aadhar_front_photo,aadhar_back_photo,zone,ward,pm_svanidhi_loan,vendor_req_id,education_status,marital_status,no_of_fam_mem,are_any_fam_mem_invol_str_ven,latitude,longitude,tamil_name,tamil_f_h_name,tamil_landmark,tamil_gender,tamil_present_district,tamil_vending_category,? FROM vendor_details WHERE vendor_req_id=?";
		
		try {
			jdbcTemplate.update(sql, userId,vendor_req_id);
			return "success";
			
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "failure";
			
		}
	}
	
	public String getloginUserZone(int userId) {
	    String query = "select zone from web_login where gcc_user_id = ?";
	    try {
	        // Use queryForObject to return a single value
	        return jdbcTemplate.queryForObject(query, String.class, userId);
	    } catch (EmptyResultDataAccessException e) {
	        // Log the issue and handle the case when no result is found
	        System.out.println("No zone found for user ID: " + userId);
	        return null; // Or return a default value if required
	    }
	}


	public List<Map<String, Object>> vendingDetailsbyZoneWeb(String zone) {
		String sql = "SELECT * "
		 		+ "FROM vendor_details "
		 		+ "WHERE zone=?";
		return jdbcTemplate.queryForList(sql, zone);
	}
	
	public List<Map<String, Object>> getvedingcommitteelist(String date, String zones) {

	    // Convert "01,02,03" → ["01","02","03"] (trim spaces safely)
	    List<String> zoneList = Arrays.stream(zones.split(","))
	                                  .map(String::trim)
	                                  .collect(Collectors.toList());

	    // Create ?,?,? placeholders
	    String zonePlaceholders = String.join(",", Collections.nCopies(zoneList.size(), "?"));

	    String sql = "SELECT vd.id, vd.vendor_req_id, vd.name, vd.mob_no, " +
	            "CONCAT_WS(', ', vd.vending_address, vd.vending_district, vd.vending_pincode) AS vending_address, " +
	            "vd.zone, vd.ward, CONCAT_WS('- ', vd.zone, vd.ward) AS zoneward " +
	            "FROM vendor_details vd " +
	            "JOIN vendor_approval_level_3 va3 ON vd.id = va3.vdid " +
	            "WHERE va3.status='Approved' " +
	            "AND DATE(va3.cdate) <= STR_TO_DATE(?, '%d-%m-%Y') " +
	            "AND vd.zone IN (" + zonePlaceholders + ") "
	            + " ORDER BY vd.zone,vd.ward";

	    List<Object> params = new ArrayList<>();
	    params.add(date);
	    params.addAll(zoneList);

	    try {
	        return jdbcTemplate.queryForList(sql, params.toArray());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}



	public List<Map<String, Object>> vendorDetailsById(String requestId) {
		
		String sql = "SELECT vd.id,vd.vendor_req_id, vd.name, vd.mob_no, vd.dob, vd.gender, vd.diff_abled, vd.f_h_name,  scm.category_name, " +
                "CONCAT_WS(', ', vd.present_address, vd.present_district, vd.present_pincode) AS residental_address, " +
                "CONCAT_WS(', ', vd.vending_address, vd.vending_district, vd.vending_pincode) AS business_address, vd.bank_acc_status, " +
                "ifnull(CONCAT('" + fileBaseUrl + "/gccofficialapp/files',vd.bank_passbook),'') as bank_passbook, " +
                "ifnull(CONCAT('" + fileBaseUrl + "/gccofficialapp/files',vd.ration_card_photo),'') as ration_card_photo, " +
                "ifnull(CONCAT('" + fileBaseUrl + "/gccofficialapp/files',vd.street_vendor_photo),'') as street_vendor_photo, " +
                "ifnull(CONCAT('" + fileBaseUrl + "/gccofficialapp/files',vd.aadhar_front_photo),'') as aadhar_front_photo, " +
                "ifnull(CONCAT('" + fileBaseUrl + "/gccofficialapp/files',vd.aadhar_back_photo),'') as aadhar_back_photo, " +
                "vcm.vending_category_name, vd.vending_space, vd.pm_svanidhi_loan, vd.zone, vd.ward,  " +
                "ifnull(vtym.vending_type_name,'N/A') as vendor_type,ifnull(vstym.vending_sub_type_name,'N/A') as vendor_sub_type, ifnull(va1.vendor_id_card,'N/A') as vendor_id_card,  ifnull(va1.vending_nature,'N/A') as vending_nature,  " +
                "ifnull(vtm.vending_time,'N/A') as vending_time, ifnull(tmm.transaction_mode,'N/A') as transaction_mode, ifnull(va1.swanidi_loan_amount,0) as swanidi_loan_amount,  " +
                "ifnull(CONCAT('" + fileBaseUrl + "/gccofficialapp/files',va1.vendor_space_photo),'') as vendor_space_photo, " +
                "va1.status as LI_Status, va1.remarks as LI_Remarks, DATE_FORMAT(va1.cdate, '%d-%m-%Y %l:%i %p') as LI_Cdate,  " +
                "va2.status as ARO_status, ifnull(va2.remarks,'') as ARO_remarks, DATE_FORMAT(va2.cdate, '%d-%m-%Y %l:%i %p') as ARO_Inspection_date, "+
                "va3.status as EE_status, ifnull(va3.remarks,'') as EE_remarks, DATE_FORMAT(va3.cdate, '%d-%m-%Y %l:%i %p') as EE_Inspection_date "+               
                "FROM vendor_details vd  " +
                "LEFT JOIN vendor_approval_level_3 va3 ON va3.vdid = vd.id " +
                "LEFT JOIN vendor_approval_level_1 va1 on va3.vdid = va1.vdid " +
                "LEFT JOIN vendor_approval_level_2 va2 on va3.vdid = va2.vdid " +                
                "LEFT JOIN education_master em   on  em.id= vd.education_status " +
                "LEFT JOIN marital_status_master msm  on  msm.id= vd.marital_status " +
                "LEFT JOIN social_category_master scm on vd.social_category = scm.id " +
                "LEFT JOIN vending_category_master vcm ON vd.vending_category=vcm.id " +                
                "LEFT JOIN vending_time_master vtm on va1.vendor_timing_id = vtm.id " +
                "LEFT JOIN transaction_mode_master tmm on va1.transaction_mode_id = tmm.id " +
                "LEFT JOIN vending_type_master vtym ON va1.vendor_type = vtym.typeid " +
                "LEFT JOIN vending_sub_type_master vstym ON va1.vendor_sub_type = vstym.subtypeid " +
                "WHERE vd.id = ? ";
		
		return jdbcTemplate.queryForList(sql, requestId);
	}


	public List<Map<String, Object>> getdataforreport(String zone, String ward) {

	    StringBuilder sql = new StringBuilder(
	            "SELECT vd.id, vd.vendor_req_id, vd.zone, vd.ward, vd.name, vd.mob_no " +
	            "FROM vendor_details vd " +
	            "JOIN vendor_approval_level_3 va3 ON va3.vdid = vd.id " +
	            "WHERE vd.isactive = 1"
	    );

	    List<Object> params = new ArrayList<>();

	    // Add zone if not empty
	    if (zone != null && !zone.trim().isEmpty()) {
	        sql.append(" AND vd.zone = ?");
	        params.add(zone);
	    }

	    // Add ward if not empty
	    if (ward != null && !ward.trim().isEmpty()) {
	        sql.append(" AND vd.ward = ?");
	        params.add(ward);
	    }

	    try {
	        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}



	public List<Map<String, Object>> ViewWardDropdown(String zone) {
		String sql = "SELECT ward_name "
	    		+ "FROM ward_master "
	    		+ "WHERE is_active=1 AND is_delete=0 AND zone_id=?";
	    try {
	    	return jdbcTemplate.queryForList(sql,zone);
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
			 return Collections.emptyList();
		}
	}


	
}
