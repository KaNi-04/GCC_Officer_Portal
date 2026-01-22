package in.gov.chennaicorporation.gccoffice.school.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StudentInfoService {
	private JdbcTemplate jdbcTemplate;
	
	private String fileBaseUrl;

	@Autowired
	public void setDataSourcemysql(@Qualifier("mysqlSchoolDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.fileBaseUrl=environment.getProperty("fileBaseUrl");
	}
	
	 @Autowired
	 private Environment environment;
	 
	 @Transactional
	 public ResponseEntity<?> getStandards(String udise) {
		 Map<String, Object> result = new HashMap<>();
		 String standard =  getStandardsCount(udise);
		 List<String> standards = Arrays.asList(standard.split(","));
		 List<Map<String, Object>> updateSTD = new ArrayList<>();
		 for (String std:standards) {
			 Map<String, Object> data = new HashMap<>();
			 String clas = std;
			 String count = getStudentCount(udise, clas);
			 data.put("Designation", clas);
			 data.put("Student_Count", count);
			 updateSTD.add(data);
		 }
		 if(!updateSTD.isEmpty()) {
				result.put("Message", "Success");
				result.put("Data", updateSTD);
				return ResponseEntity.ok(result);
		 } else {
				result.put("Message", "Error");
				result.put("Error", "Failed to fetch class List");
				return ResponseEntity.ok(result);
		 }
		 
	 }
	 
	 @Transactional
		public ResponseEntity<?> getSections() {
			Map<String, Object> result = new HashMap<>();
			String sql = "SELECT id,section FROM class_sections_master WHERE is_active=1 AND is_delete=0 ORDER BY id";
			try {
				List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
				result.put("Message", "Success");
				result.put("Data", data);
				return ResponseEntity.ok(result);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("Message", "Error");
				result.put("Error", "Failed to fetch medium List: " + e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
			}
		}
	 
	 @Transactional
	private String getStudentCount(String udise, String clas) {
		String sql = "SELECT count(student_id) FROM student_info where udise = ? AND std = ? AND is_active = 1 AND is_delete = 0 ";
		try {
			String data = jdbcTemplate.queryForObject(sql, new Object[]{udise, clas}, String.class);
		return data;
		}catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	@Transactional
	public String getStandardsCount(String udise) {
		
		String sql = "SELECT CONCAT_WS(',', "
				+ "    IF(prekg = 1, 'PreKg', NULL), "
				+ "    IF(lkg = 1, 'LKG', NULL), "
				+ "    IF(ukg = 1, 'UKG', NULL), "
				+ "    IF(class1 = 1, 'Class1', NULL), "
				+ "    IF(class2 = 1, 'Class2', NULL), "
				+ "    IF(class3 = 1, 'Class3', NULL), "
				+ "    IF(class4 = 1, 'Class4', NULL), "
				+ "    IF(class5 = 1, 'Class5', NULL), "
				+ "    IF(class6 = 1, 'Class6', NULL), "
				+ "    IF(class7 = 1, 'Class7', NULL), "
				+ "    IF(class8 = 1, 'Class8', NULL), "
				+ "    IF(class9 = 1, 'Class9', NULL), "
				+ "    IF(class10 = 1, 'Class10', NULL), "
				+ "    IF(class11 = 1, 'Class11', NULL), "
				+ "    IF(class12 = 1, 'Class12', NULL) "
				+ "  ) AS active_classes "
				+ " FROM school_class_master "
				+ " WHERE udise = ? ";

		try {
			String data = jdbcTemplate.queryForObject(sql, String.class, udise);
			return data;
		} catch (Exception e) {
			e.printStackTrace();

			//return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
			return null;
		}
		
	}

//	@Transactional
//	public ResponseEntity<?> getStandards(String udise) {
//		Map<String, Object> result = new HashMap<>();
//		String sql = "SELECT Designation, Student_Count FROM ( "
//				+ "SELECT 'PreKG' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'PreKG' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (PreKg_Boys > 0 OR PreKg_Girls > 0 OR PreKg_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'LKG' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count " 
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'LKG' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (LKG_Boys > 0 OR LKG_Girls > 0 OR LKG_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'UKG' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'UKG' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (UKG_Boys > 0 OR UKG_Girls > 0 OR UKG_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class1' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class1' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class1_Boys > 0 OR Class1_Girls > 0 OR Class1_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class2' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class2' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class2_Boys > 0 OR Class2_Girls > 0 OR Class2_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class3' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class3' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class3_Boys > 0 OR Class3_Girls > 0 OR Class3_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class4' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class4' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class4_Boys > 0 OR Class4_Girls > 0 OR Class4_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class5' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class5' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class5_Boys > 0 OR Class5_Girls > 0 OR Class5_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class6' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class6' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class6_Boys > 0 OR Class6_Girls > 0 OR Class6_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class7' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class7' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class7_Boys > 0 OR Class7_Girls > 0 OR Class7_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class8' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class8' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class8_Boys > 0 OR Class8_Girls > 0 OR Class8_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class9' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class9' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class9_Boys > 0 OR Class9_Girls > 0 OR Class9_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class10' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class10' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class10_Boys > 0 OR Class10_Girls > 0 OR Class10_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class11' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count "
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class11' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class11_Boys > 0 OR Class11_Girls > 0 OR Class11_Transgen > 0) AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " UNION ALL "
//				+ " SELECT 'Class12' AS Designation, "
//				+ " COUNT(si.student_id) AS Student_Count " 
//				+ " FROM student_enrollment_master sem "
//				+ " LEFT JOIN student_info si "
//				+ " ON sem.UDISE = si.udise AND si.std = 'Class12' "
//				+ " WHERE sem.UDISE = ? "
//				+ " AND (Class12_Boys > 0 OR Class12_Girls > 0 OR Class12_Transgen > 0)AND si.is_active = 1 AND si.is_delete = 0 "
//				+ " ) AS result WHERE Student_Count > 0";
//
//		try {
//			List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise);
//			result.put("Message", "Success");
//			result.put("Data", data);
//			return ResponseEntity.ok(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.put("Message", "Error");
//			result.put("Error", "Failed to fetch class List: " + e.getMessage());
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
//		}
//		
//	}
	 
//		@Transactional
//		public ResponseEntity<?> getStandards(String udise) {
//			Map<String, Object> result = new HashMap<>();
//			String sql = "SELECT 'PreKG' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (PreKg_Boys > 0 OR PreKg_Girls > 0 OR PreKg_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'LKG' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (LKG_Boys > 0 OR LKG_Girls > 0 OR LKG_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'UKG' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (UKG_Boys > 0 OR UKG_Girls > 0 OR UKG_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class1' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class1_Boys > 0 OR Class1_Girls > 0 OR Class1_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class2' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class2_Boys > 0 OR Class2_Girls > 0 OR Class2_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class3' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class3_Boys > 0 OR Class3_Girls > 0 OR Class3_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class4' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class4_Boys > 0 OR Class4_Girls > 0 OR Class4_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class5' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class5_Boys > 0 OR Class5_Girls > 0 OR Class5_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class6' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class6_Boys > 0 OR Class6_Girls > 0 OR Class6_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class7' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class7_Boys > 0 OR Class7_Girls > 0 OR Class7_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class8' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class8_Boys > 0 OR Class8_Girls > 0 OR Class8_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class9' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class9_Boys > 0 OR Class9_Girls > 0 OR Class9_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class10' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class10_Boys > 0 OR Class10_Girls > 0 OR Class10_Transgen > 0) "
//					+ " UNION "
//					+ " SELECT 'Class11' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class11_Boys > 0 OR Class11_Girls > 0 OR Class11_Transgen > 0) "
//					+ " UNION " 
//					+ " SELECT 'Class12' AS Designation "
//					+ " FROM student_enrollment_master "
//					+ " WHERE UDISE = ? "
//					+ "  AND (Class12_Boys > 0 OR Class12_Girls > 0 OR Class12_Transgen > 0)";
//	
//			try {
//				List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise, udise);
//				result.put("Message", "Success");
//				result.put("Data", data);
//				return ResponseEntity.ok(result);
//			} catch (Exception e) {
//				e.printStackTrace();
//				result.put("Message", "Error");
//				result.put("Error", "Failed to fetch class List: " + e.getMessage());
//				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
//			}
//			
//		}
	@Transactional
	public ResponseEntity<?> checkStudent(String udise, String student_name, String emis_no, String gender, String std,
			String dob, String age, String parent_type,String section,String medium, String father_name, String mother_name, String guardian_name,
			String father_mobile, String mother_mobile, String guardian_mobile, String father_aadhar,
			String mother_aadhar, String guardian_aadhar, String student_aadhar, MultipartFile father_aadhar_file,
			MultipartFile mother_aadhar_file, MultipartFile guardian_aadhar_file, MultipartFile student_aadhar_file) {
		Map<String, Object> result = new HashMap<>();
		int is_avbl = checkEmis(emis_no);
		if(is_avbl==0) {
			String data = saveStudent(udise, student_name, emis_no, gender, std, dob, age, parent_type,section,medium, father_name, mother_name, guardian_name, father_mobile, mother_mobile, guardian_mobile, father_aadhar, mother_aadhar, guardian_aadhar, student_aadhar, father_aadhar_file, mother_aadhar_file, guardian_aadhar_file, student_aadhar_file);
			if (data.equalsIgnoreCase("sucess")) {
			result.put("Message", "Saved Sucessfully");
			result.put("Data", "Success");
			} else {
				result.put("Message", "Error");
				result.put("Error", "Failed to save");
			}
			
		} else if(is_avbl==1){
			result.put("Message", "Duplicate");
			result.put("Error", "EMIS No already registered");
			
		}else {
			result.put("Message", "Error");
			result.put("Error", "Failed to save");
		}
			return ResponseEntity.ok(result);
			
	}

	private int checkEmis(String emis_no) {
	    String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM student_info WHERE emis_no = ? and is_active=1 and is_delete=0) THEN 1 ELSE 0 END AS result";
	    try {
	        return  jdbcTemplate.queryForObject(sql, Integer.class, emis_no);
	        
	    } catch (Exception e) {
	        e.printStackTrace(); 
	        return 5;
	    }
	}
	@Transactional
	public String saveStudent(String udise, String student_name, String emis_no, String gender, String std,
			String dob, String age, String parent_type,String section,String medium, String father_name, String mother_name, String guardian_name, String father_mobile, String mother_mobile, String guardian_mobile,
			String father_aadhar, String mother_aadhar, String guardian_aadhar, String student_aadhar, MultipartFile father_aadhar_file,
			MultipartFile mother_aadhar_file, MultipartFile guardian_aadhar_file, MultipartFile student_aadhar_file) {
		
		String sql = "INSERT INTO student_info (udise, student_name, emis_no, gender, std, dob, age, parent_type,section,mediums, father_name, mother_name, guardian_name, father_mobile, mother_mobile, guardian_mobile, father_aadhar, mother_aadhar, guardian_aadhar, student_aadhar, father_aadhar_file, mother_aadhar_file, guardian_aadhar_file, student_aadhar_file) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
		 try {
			 String fatherUrl = null;
			 String motherUrl = null;
			 String guardianUrl = null;
			 String studentUrl = null;
			 
			 if(student_aadhar.isEmpty()) {
				 student_aadhar=null;
			 }
			 
			 String sec=section.toUpperCase();
			 
			 if(father_aadhar_file!=null && !father_aadhar_file.isEmpty()) {
				 fatherUrl = saveFile(father_aadhar_file);
			 } 
			 if(mother_aadhar_file!=null && !mother_aadhar_file.isEmpty()) {
				 motherUrl = saveFile(mother_aadhar_file); 
			 } 
			 if(guardian_aadhar_file!=null && !guardian_aadhar_file.isEmpty()) {
				 guardianUrl = saveFile(guardian_aadhar_file);
			 } 
			 if(student_aadhar_file!=null && !student_aadhar_file.isEmpty()) {
				 studentUrl = saveFile(student_aadhar_file); 
			 }
			 
			
			 
//			 System.out.println("----- Student Info -----");
//			    System.out.println("UDISE: " + udise);
//			    System.out.println("Student Name: " + student_name);
//			    System.out.println("EMIS No: " + emis_no);
//			    System.out.println("Gender: " + gender);
//			    System.out.println("Class: " + std);
//			    System.out.println("DOB: " + dob);
//			    System.out.println("Age: " + age);
//			    System.out.println("Parent Type: " + parent_type);
//
//			    System.out.println("----- Parent Details -----");
//			    System.out.println("Father Name: " + father_name);
//			    System.out.println("Mother Name: " + mother_name);
//			    System.out.println("Guardian Name: " + guardian_name);
//
//			    System.out.println("Father Mobile: " + father_mobile);
//			    System.out.println("Mother Mobile: " + mother_mobile);
//			    System.out.println("Guardian Mobile: " + guardian_mobile);
//
//			    System.out.println("Father Aadhar: " + father_aadhar);
//			    System.out.println("Mother Aadhar: " + mother_aadhar);
//			    System.out.println("Guardian Aadhar: " + guardian_aadhar);
//			    System.out.println("Student Aadhar: " + student_aadhar);
//			    
//			    System.out.println("----- Uploaded Files -----");
//			    System.out.println("Father Aadhar File: " + (father_aadhar_file != null ? father_aadhar_file.getOriginalFilename() : "null"));
//			    System.out.println("Mother Aadhar File: " + (mother_aadhar_file != null ? mother_aadhar_file.getOriginalFilename() : "null"));
//			    System.out.println("Guardian Aadhar File: " + (guardian_aadhar_file != null ? guardian_aadhar_file.getOriginalFilename() : "null"));
//			    System.out.println("Student Aadhar File: " + (student_aadhar_file != null ? student_aadhar_file.getOriginalFilename() : "null"));

			 
			jdbcTemplate.update(sql, udise, student_name, emis_no, gender, std, dob, age, parent_type,sec,medium, father_name, mother_name, guardian_name, father_mobile, mother_mobile, guardian_mobile, father_aadhar, mother_aadhar, guardian_aadhar, student_aadhar, fatherUrl, motherUrl, guardianUrl, studentUrl);
			return "sucess";
		 } catch (IOException e) {
			e.printStackTrace();
			return "error";
		}
		
	}
	
//	 private String saveFile(MultipartFile file) throws IOException {
//	    	
//	    	String uploadDirectory = environment.getProperty("file.upload.directory");
//	        
//	        String folderName = environment.getProperty("education.foldername");
//	        	        
//	        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + file.getOriginalFilename();
//
//	        String folderPath = uploadDirectory + "/" + folderName;
//	       
//	        Path dirPath = Paths.get(folderPath);
//	        if (!Files.exists(dirPath)) {
//	            Files.createDirectories(dirPath);
//	        }
//	               
//	        Path filePath = dirPath.resolve(fileName);
//	        
//	        Files.write(filePath, file.getBytes());
//	        
//	        return filePath.toAbsolutePath().toString();
//	    }
	
	private String saveFile(MultipartFile file) throws IOException {
	    String uploadDirectory = environment.getProperty("file.upload.directory");
	    String folderName = environment.getProperty("education.foldername");

	    String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + file.getOriginalFilename();

	    String FilePath = "/"+ folderName +"/"+fileName;
	    // Create the directory path
	    Path dirPath = Paths.get(uploadDirectory, folderName);
	    if (!Files.exists(dirPath)) {
	        Files.createDirectories(dirPath);
	    }

	    // Create the full file path
	    Path filePath = dirPath.resolve(fileName);

	    // Save the file
	    Files.write(filePath, file.getBytes());
	    
	    return FilePath;

	    // Return the full file path as a string
	    //return filePath.toString().replace("\\", "/");
	}


	 @Transactional
	public ResponseEntity<?> getStudentsClass(String udise, String std) {
		Map<String, Object> result = new HashMap<>();
		String sql = "SELECT student_id, student_name, emis_no FROM student_info WHERE udise = ? AND std = ? AND is_active=1 AND is_delete=0";
		try {
			List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, udise, std);
			result.put("Message", "Success");
			result.put("Data", data);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("Message", "Error");
			result.put("Error", "Failed to fetch students in class List: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
		
	}

	 @Transactional
	public ResponseEntity<?> getStudentsDetails(String studentId) {
		 Map<String, Object> result = new HashMap<>();
		 String sql = "SELECT *, " +
	             "CONCAT('" + fileBaseUrl + "/gcc/files', father_aadhar_file) AS view_father_aadhar_file, " +
	             "CONCAT('" + fileBaseUrl + "/gcc/files', mother_aadhar_file) AS view_mother_aadhar_file, " +
	             "CONCAT('" + fileBaseUrl + "/gcc/files', guardian_aadhar_file) AS view_guardian_aadhar_file, " +
	             "CONCAT('" + fileBaseUrl + "/gcc/files', student_aadhar_file) AS view_student_aadhar_file " +
	             " FROM student_info WHERE student_id = ?";
			try {
				List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, studentId);
				result.put("Message", "Success");
				result.put("Data", data);
				return ResponseEntity.ok(result);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("Message", "Error");
				result.put("Error", "Failed to fetch student detail: " + e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
			}
	}
	 
	 @Transactional
		public ResponseEntity<?> getMedium() {
			Map<String, Object> result = new HashMap<>();
			String sql = "SELECT * FROM medium_master ORDER BY execution_order ASC";
			try {
				List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
				result.put("Message", "Success");
				result.put("Data", data);
				return ResponseEntity.ok(result);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("Message", "Error");
				result.put("Error", "Failed to fetch medium List: " + e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
			}
		}
	 
	 private String logEntry(String student_id, String user_id, String action_type) {
			String sql="INSERT INTO student_info_log (student_id, udise, student_name, emis_no, gender, mediums, std, section, dob, age, parent_type, father_name, mother_name, guardian_name, father_mobile, mother_mobile, guardian_mobile, father_aadhar, mother_aadhar, guardian_aadhar, student_aadhar, father_aadhar_file, mother_aadhar_file, guardian_aadhar_file, student_aadhar_file, user_id, action_type) "
					+ " SELECT student_id, udise, student_name, emis_no, gender, mediums, std, section, dob, age, parent_type, father_name, mother_name, guardian_name, father_mobile, mother_mobile, guardian_mobile, father_aadhar, mother_aadhar, guardian_aadhar, student_aadhar, father_aadhar_file, mother_aadhar_file, guardian_aadhar_file, student_aadhar_file, ?, ? FROM student_info WHERE student_id = ?";
			
			try {
				jdbcTemplate.update(sql, user_id, action_type, student_id);
				return "sucess";
				
			} catch (DataAccessException e) {
				e.printStackTrace();
				return "failure";
				
			}
			
		}
	 
	 @Transactional
		public ResponseEntity<?> deleteProcess(String student_id, String user_id, String action_type) {
	 Map<String, Object> result = new HashMap<>();
			 
			 String data = logEntry(student_id, user_id, action_type); 
			 String data1 = "";
			 if(data.equalsIgnoreCase("sucess")) {
				data1 = deleteStudent(student_id); 
			 } else {
				 result.put("Message", "Error");
				 result.put("Data", "Failed");
				 return ResponseEntity.ok(result);
			 }
			 if(data1.equalsIgnoreCase("sucess")) {
				 result.put("Message", "Success");
					result.put("Data", "Deleted sucessfully");
			 }else {
				 result.put("Message", "Error");
				 result.put("Data", "Failed to Delete");
			 }
			 return ResponseEntity.ok(result);
		}
		
		private String deleteStudent(String student_id) {
			String sql="UPDATE student_info SET is_delete = 1, is_active = 0 WHERE student_id = ? ";
		
			
			try {
				jdbcTemplate.update(sql, student_id);
				return "sucess";
			} catch (DataAccessException e) {
				e.printStackTrace();
				return "failure";
			}
		}


		@Transactional
		public ResponseEntity<?> updateProcess(String student_id, String user_id, String action_type, String udise,
				String student_name, String emis_no, String gender, String mediums, String std, String section, String dob,
				String age, String parent_type, String father_name, String mother_name, String guardian_name,
				String father_mobile, String mother_mobile, String guardian_mobile, String father_aadhar,
				String mother_aadhar, String guardian_aadhar, String student_aadhar, MultipartFile father_aadhar_file,
				MultipartFile mother_aadhar_file, MultipartFile guardian_aadhar_file, MultipartFile student_aadhar_file, String raw_father_aadhar, String raw_mother_aadhar, String raw_guardian_aadhar, String raw_student_aadhar) {
			 Map<String, Object> result = new HashMap<>();
			 			
				 String data = logEntry(student_id, user_id, action_type); 
				 String data1 = "";
				 if(data.equalsIgnoreCase("sucess")) {
					data1 = updateStudent(student_id, udise, student_name, emis_no, gender, mediums, std, section, dob, age, parent_type, father_name, mother_name, guardian_name, father_mobile, mother_mobile, guardian_mobile, father_aadhar, mother_aadhar, guardian_aadhar, student_aadhar, father_aadhar_file, mother_aadhar_file, guardian_aadhar_file, student_aadhar_file, raw_father_aadhar, raw_mother_aadhar, raw_guardian_aadhar, raw_student_aadhar); 
				 }
				 else {
					 result.put("Message", "Error");
					 result.put("Data", "Failed");
					 return ResponseEntity.ok(result);
				 }
				 
				 if(data1.equalsIgnoreCase("sucess")) {
					 result.put("Message", "Success");
						result.put("Data", "Updated successfully");
				 }else {
					 result.put("Message", "Error");
					 result.put("Data", "Failed to update");
				 }

			 return ResponseEntity.ok(result);
		}
		 
		private String updateStudent(String student_id, String udise, String student_name, String emis_no, String gender,
				String mediums, String std, String section, String dob, String age, String parent_type, String father_name,
				String mother_name, String guardian_name, String father_mobile, String mother_mobile,
				String guardian_mobile, String father_aadhar, String mother_aadhar, String guardian_aadhar,
				String student_aadhar, MultipartFile father_aadhar_file, MultipartFile mother_aadhar_file,
				MultipartFile guardian_aadhar_file, MultipartFile student_aadhar_file, String raw_father_aadhar, String raw_mother_aadhar, String raw_guardian_aadhar, String raw_student_aadhar) {
			    
			    String sql = "";
			    
			    String fatherUrl = null;
				 String motherUrl = null;
				 String guardianUrl = null;
				 String studentUrl = null;

				 
				 try {
					 
					 //// for parent
					 if("Parent".equalsIgnoreCase(parent_type)) {
						 
						 if(raw_father_aadhar!=null && !raw_father_aadhar.isEmpty() ) {
							 fatherUrl = raw_father_aadhar;
							 if(father_aadhar_file!=null && !father_aadhar_file.isEmpty()) {
								 fatherUrl = saveFile(father_aadhar_file);
							 }							 
							 
						 }
						 else {
							 if(father_aadhar_file!=null && !father_aadhar_file.isEmpty()) {
								 fatherUrl = saveFile(father_aadhar_file);
							 }
							 else {
								 fatherUrl = null;
							 }
							 
						 }
					
						 if(raw_mother_aadhar!=null && !raw_mother_aadhar.isEmpty() ) {
							 
							 motherUrl = raw_mother_aadhar;
							 if(mother_aadhar_file!=null && !mother_aadhar_file.isEmpty()) {
								
								 motherUrl = saveFile(mother_aadhar_file); 
							 } 
							
						 }
						 else {
							 
							 if(mother_aadhar_file!=null && !mother_aadhar_file.isEmpty()) {
								 motherUrl = saveFile(mother_aadhar_file); 
							 }
							 else {
								 motherUrl =null;
							 }
							
						 }
						 
					 }
					 
					 else {
						 if (father_aadhar_file != null && !father_aadhar_file.isEmpty()) {
							    fatherUrl = saveFile(father_aadhar_file);
							} else {
							    //fatherUrl = (raw_father_aadhar != null) ? raw_father_aadhar : null;
							    fatherUrl = (raw_father_aadhar != null && !raw_father_aadhar.isEmpty()) ? raw_father_aadhar : null;
							}
						 
						 if (mother_aadhar_file != null && !mother_aadhar_file.isEmpty()) {
							 	motherUrl = saveFile(mother_aadhar_file);
							} else {
								//motherUrl = (raw_mother_aadhar != null) ? raw_mother_aadhar : null;
								motherUrl = (raw_mother_aadhar != null && !raw_mother_aadhar.isEmpty()) ? raw_mother_aadhar : null;
							}
					 }
					 
					 
					 
					 ////
					 guardianUrl = raw_guardian_aadhar;
					 if(guardian_aadhar_file!=null && !guardian_aadhar_file.isEmpty()) {
						 guardianUrl = saveFile(guardian_aadhar_file);
					 } 
					
					 ///
					 studentUrl = raw_student_aadhar;
					 if(student_aadhar_file!=null && !student_aadhar_file.isEmpty()) {
						 studentUrl = saveFile(student_aadhar_file); 
					 }
					 
					 //father_mobile mother_mobile
					 if (father_mobile != null && father_mobile.trim().isEmpty()) {
						 father_mobile = null;
						}
					 if (mother_mobile != null && mother_mobile.trim().isEmpty()) {
						 mother_mobile = null;
						}
					 
					// father_aadhar mother_aadhar  student_aadhar
					 if (father_aadhar != null && father_aadhar.trim().isEmpty()) {
						 father_aadhar = null;
						}
					 if (mother_aadhar != null && mother_aadhar.trim().isEmpty()) {
						 mother_aadhar = null;
						}
					 
					 if (student_aadhar != null && student_aadhar.trim().isEmpty()) {
						    student_aadhar = null;
						}
					 
					 ////////
					 if(parent_type.equalsIgnoreCase("Parent")) {
						 sql="UPDATE student_info SET udise = ?, student_name = ?, emis_no = ?, gender = ?, mediums = ?, std = ?, section = ?, dob = ?, age = ?, parent_type = ?, father_name = ?, mother_name = ?, father_mobile = ?, mother_mobile = ?, father_aadhar = ?, mother_aadhar = ?, student_aadhar = ?, father_aadhar_file = ?, mother_aadhar_file = ?, student_aadhar_file = ?  WHERE student_id = ? ";
						 jdbcTemplate.update(sql, udise, student_name, emis_no, gender, mediums, std, section, dob, age, parent_type, father_name, mother_name, father_mobile, mother_mobile, father_aadhar, mother_aadhar, student_aadhar, fatherUrl, motherUrl, studentUrl, student_id);
					 } else if(parent_type.equalsIgnoreCase("SingleParent_Father")) {
						 sql="UPDATE student_info SET udise = ?, student_name = ?, emis_no = ?, gender = ?, mediums = ?, std = ?, section = ?, dob = ?, age = ?, parent_type = ?, father_name = ?, father_mobile = ?, father_aadhar = ?, student_aadhar = ?, father_aadhar_file = ?, student_aadhar_file = ?  WHERE student_id = ? ";
						 jdbcTemplate.update(sql, udise, student_name, emis_no, gender, mediums, std, section, dob, age, parent_type, father_name, father_mobile, father_aadhar, student_aadhar, fatherUrl, studentUrl, student_id);
					 } else if(parent_type.equalsIgnoreCase("SingleParent_Mother")) {
						 sql="UPDATE student_info SET udise = ?, student_name = ?, emis_no = ?, gender = ?, mediums = ?, std = ?, section = ?, dob = ?, age = ?, parent_type = ?, mother_name = ?, mother_mobile = ?, mother_aadhar = ?, student_aadhar = ?, mother_aadhar_file = ?, student_aadhar_file = ?  WHERE student_id = ? ";
						 jdbcTemplate.update(sql, udise, student_name, emis_no, gender, mediums, std, section, dob, age, parent_type, mother_name, mother_mobile, mother_aadhar, student_aadhar, motherUrl, studentUrl, student_id);
					 } else if(parent_type.equalsIgnoreCase("Guardian")) {
						 sql="UPDATE student_info SET udise = ?, student_name = ?, emis_no = ?, gender = ?, mediums = ?, std = ?, section = ?, dob = ?, age = ?, parent_type = ?, father_name = ?, mother_name = ?, guardian_name = ?, father_mobile = ?, mother_mobile = ?, guardian_mobile = ?, father_aadhar = ?, mother_aadhar = ?, guardian_aadhar = ?, student_aadhar = ?, father_aadhar_file = ?, mother_aadhar_file = ?, guardian_aadhar_file = ?, student_aadhar_file = ?  WHERE student_id = ? ";
						 jdbcTemplate.update(sql, udise, student_name, emis_no, gender, mediums, std, section, dob, age, parent_type, father_name, mother_name, guardian_name, father_mobile, mother_mobile, guardian_mobile, father_aadhar, mother_aadhar, guardian_aadhar, student_aadhar, fatherUrl, motherUrl, guardianUrl, studentUrl, student_id);
					 }
					 return "sucess";
				 }catch (Exception e) {
					 e.printStackTrace();
						return "failure";
				}
		}
		
		@Transactional
		public ResponseEntity<?> checkStudent(String udise, String schoolId) {
		    Map<String, Object> result = new HashMap<>();
		    try {
		        String stu_strength = studentStrength(schoolId);
		        String aadhar_strength = aadharStrength(udise);

		        if (!stu_strength.equalsIgnoreCase("error") && !aadhar_strength.equalsIgnoreCase("error")) {
		            int allowable_strength = Integer.parseInt(stu_strength) - Integer.parseInt(aadhar_strength);
		            result.put("Student_Strength", Integer.parseInt(stu_strength));
		            result.put("Aadhar_strength", Integer.parseInt(aadhar_strength));
		            result.put("Allowable_strength", allowable_strength);
		            result.put("Message", "Sucessfully student count fetched");
		            result.put("Status", "Sucess");
		        } else if (stu_strength.equalsIgnoreCase("error") || aadhar_strength.equalsIgnoreCase("error")) {
		            result.put("Status", "Error");
		            result.put("Message", "Error in fetching students count");
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        result.put("Status", "Error");
		        result.put("Message", "Error in fetching students count");
		    }
		    return ResponseEntity.ok(result);
		}
		

		private String aadharStrength(String udise) {
			String sql = "SELECT count(*) FROM student_info WHERE udise = ? AND is_active = 1 AND is_delete = 0";
		    String strength = "0"; 

		    try {
		        String result = jdbcTemplate.queryForObject(sql, String.class, udise);
		        if (result != null && !result.isEmpty()) {
		            strength = result;
		        }
		    }
		    catch (Exception e) {
		       System.out.println("Error fetching student strength: " + e.getMessage());
		        strength = "error";
		    }

		    return strength;
		}
		private String studentStrength(String schoolId) {
		    String sql = "SELECT total_students FROM student_strength WHERE school_id = ?";
		    String strength = "0"; 

		    try {
		        String result = jdbcTemplate.queryForObject(sql, String.class, schoolId);
		        if (result != null && !result.isEmpty()) {
		            strength = result;
		        }
		    }
		 	    catch (Exception e) {
		        System.out.println("Error fetching student strength: " + e.getMessage());
		        strength = "error";
		    }

		    return strength;
		}
		
}
