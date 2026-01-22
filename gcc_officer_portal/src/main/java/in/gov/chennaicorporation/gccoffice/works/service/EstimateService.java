package in.gov.chennaicorporation.gccoffice.works.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.sql.DataSource;

import java.nio.file.Files;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

import in.gov.chennaicorporation.gccoffice.controller.DateTimeUtil;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
@Service
public class EstimateService {
	@Autowired
    private Environment environment;
	private String fileBaseUrl;
	
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate oraclejdbcTemplate;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int STRING_LENGTH = 15;
    private static final Random RANDOM = new SecureRandom();
    
    @Autowired
    public void setDataSource(
            @Qualifier("mysqlWorksDataSource") DataSource dataSource,
            @Qualifier("oracleDataSource") DataSource oracleDataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.oraclejdbcTemplate = new JdbcTemplate(oracleDataSource);
        this.fileBaseUrl=environment.getProperty("fileBaseUrl");
    }

    public static String generateRandomString() {
        StringBuilder result = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            result.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return result.toString();
    }
	
	public static String generateRandomStringForFile(int String_Lenth) {
        StringBuilder result = new StringBuilder(String_Lenth);
        for (int i = 0; i < STRING_LENGTH; i++) {
            result.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return result.toString();
    }
	
  //save file and get path
    public String saveFile(MultipartFile file, String fileCat, String type_folder) throws IOException {

        String order_copy_url = "";
        type_folder = "works/"+type_folder;
        // Handle file upload if a file is provided
        if (file == null || file.isEmpty()) {
            return order_copy_url;
        }

        // Set the file path where you want to save it
        String uploadDirectory = environment.getProperty("file.upload.directory");
        String serviceFolderName = environment.getProperty("gcc_works");
        var year =DateTimeUtil.getCurrentYear();
        var month =DateTimeUtil.getCurrentMonth();
        //var date =DateTimeUtil.getCurrentDay();

        uploadDirectory = uploadDirectory + serviceFolderName + "/" + type_folder + "/" + year + "/" + month;

        Path directoryPath = Paths.get(uploadDirectory);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        
        String datetimetxt = DateTimeUtil.getCurrentDateTime();
        
        String fileName = fileCat + "_"+ datetimetxt +"_"+generateRandomStringForFile(10) +"_" + file.getOriginalFilename();
        // Construct the file path
        String filepath = uploadDirectory + "/" + fileName;
        String filepath_txt = "/" + serviceFolderName + "/" + type_folder + "/" + year + "/" + month + "/" + fileName;

        Path path = Paths.get(filepath);

        // Ensure the directory exists
        Files.createDirectories(path.getParent());

        // Write the file to the specified path
        Files.write(path, file.getBytes());

        // Set the order_copy_url to the relative file path for storing in DB
        order_copy_url = filepath_txt;

        return order_copy_url;
    }
    
/*
    public int saveEstimate(String estimateNo, String estimateDate, String zone, String ward, String projectName,
                            boolean iconicProject, String location, String department, String contractorName,
                            String contractorPeriod, String fundSource, String scheme, String category,
                            String subCategory, String estAmount, String techSancDate, String adminSancDate,
                            String admSanFile, String tenderCallDate, String tenderFinDate, String loaDate,
                            String loaFile, String agreementDate, String agreementFile, String workOrderDate,
                            String workOrderFile, String workCommDate) {

        String sql = "INSERT INTO gcc_wroks_status.erp_works (estimate_no, estimate_date, zone, ward, project_name, location, " +
                "department, contractor_name, contractor_period, fund_source, scheme, category, sub_category, " +
                "estimatio_amount, technical_sanction_date, admin_sanction_date, admin_sanction_file, tender_called_date, " +
                "tender_finalized_date, loa_date, " +
                "loa_file, agreement_date, agreement_file, work_order_date, work_order_file, work_commenced_date,isiconic) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql, estimateNo, estimateDate, zone, ward, projectName, location, department,
                contractorName, contractorPeriod, fundSource, scheme, category, subCategory, estAmount, techSancDate,
                adminSancDate, admSanFile, tenderCallDate, tenderFinDate, loaDate, loaFile, agreementDate, agreementFile,
                workOrderDate, workOrderFile, workCommDate, iconicProject);
    }
*/
    public String processFormData(
            String estimateNo, String estimateDate, String zone, String ward, Boolean iconicProject,
            String projectName, String location, String department, String contractorName,
            String contractorPeriod, String fundSource, String scheme, String category,
            String subCategory, String estAmount, String techSancDate, String adminSancDate,
            String sanFilePath, String tenderCallDate, String tenderFinDate, String loaDate,
            String loaFilePath, String agreementDate, String agreementFilePath,
            String workOrderDate, String workOrderFilePath, String workCommDate,
            String reason, String remarks, String loginid,
            String projecttype, String govtfund, String specialcat, String filenumber) {

        // 🔍 Validate mandatory fields
        if (estimateNo == null || estimateNo.isEmpty() || estimateDate == null || estimateDate.isEmpty()
                || projectName == null || projectName.isEmpty()) {
            throw new IllegalArgumentException("Mandatory fields are missing!");
        }

        if ("Other".equals(reason)) {
            // ✅ Store "Other" in reason and the entered value in remarks
            if (remarks == null || remarks.trim().isEmpty()) {
                throw new IllegalArgumentException("Remarks cannot be empty when reason is 'Other'");
            }
        } else {
            // ✅ If a predefined reason is selected, store it in reason and make remarks NULL
            remarks = null;
        }
        
       String remarks_txt = remarks;
       
        // ✅ SQL Query
        String sqlQuery = "INSERT INTO gcc_works_status.non_erp_works (estimate_no, estimate_date, zone, ward, isiconic, project_name, location, " +
                     "department, contractor_name, contractor_period, fund_source, scheme, category, sub_category, " +
                     "estimation_amount, technical_sanction_date, admin_sanction_date, admin_sanction_file, tender_called_date, tender_finalized_date, " +
                     "loa_date, loa_file, agreement_date, agreement_file, work_order_date, work_order_file, work_commenced_date, reason, remarks,"
                     + "project_type, govt_funded_project, special_category, filenumber) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                     + "?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        int affectedRows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"estid"});
            ps.setString(1, estimateNo);
            ps.setString(2, estimateDate);
            ps.setString(3, zone);
            ps.setString(4, ward);
            ps.setInt(5, (iconicProject != null && iconicProject) ? 1 : 0); // ✅ Convert Boolean to INT
            ps.setString(6, projectName);
            ps.setString(7, location);
            ps.setString(8, department);
            ps.setString(9, contractorName);
            ps.setString(10, contractorPeriod);
            ps.setString(11, fundSource);
            ps.setString(12, scheme);
            ps.setString(13, category);
            ps.setString(14, subCategory);
            ps.setString(15, estAmount);
            ps.setString(16, techSancDate);
            ps.setString(17, adminSancDate);
            ps.setString(18, sanFilePath);
            ps.setString(19, tenderCallDate);
            ps.setString(20, tenderFinDate);
            ps.setString(21, loaDate);
            ps.setString(22, loaFilePath);
            ps.setString(23, agreementDate);
            ps.setString(24, agreementFilePath);
            ps.setString(25, workOrderDate);
            ps.setString(26, workOrderFilePath);
            ps.setString(27, workCommDate);
            ps.setString(28, reason);
            ps.setString(29, remarks_txt);
            ps.setString(30, projecttype);
            ps.setString(31, govtfund);
            ps.setString(32, specialcat);
            ps.setString(33, filenumber);
            return ps;
        }, keyHolder);
        
        String estNo = "error";
        
        if(affectedRows>0) {
        	int lastInsertId = (int) keyHolder.getKey().longValue();
	        int currentYear = LocalDate.now().getYear();
            int previousYear = currentYear - 1;
            String suffixYear = String.valueOf(currentYear).substring(2);
            String prefix = department.substring(0, 1);
            
            estNo = "M" + prefix + "/" + previousYear + "-" + suffixYear + "/" + lastInsertId;
	        // Update the petition number in the database
	        updateESTno(lastInsertId, estNo);
	        
	     // ✅ Construct SQL Query with Actual Values (For Logging)
	        String formattedSql = String.format(
	            "INSERT INTO gcc_works_status.non_erp_works (estimate_no, estimate_date, zone, ward, isiconic, project_name, location, " +
	            "department, contractor_name, contractor_period, fund_source, scheme, category, sub_category, " +
	            "estimation_amount, technical_sanction_date, admin_sanction_date, admin_sanction_file, tender_called_date, tender_finalized_date, " +
	            "loa_date, loa_file, agreement_date, agreement_file, work_order_date, work_order_file, work_commenced_date, reason, remarks) " +
	            "VALUES ('%s', '%s', '%s', '%s', %d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', " +
	            "'%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
	            estimateNo, estimateDate, zone, ward, (iconicProject != null && iconicProject) ? 1 : 0, projectName, location, department, 
	            contractorName, contractorPeriod, fundSource, scheme, category, subCategory, estAmount, techSancDate, adminSancDate, sanFilePath, 
	            tenderCallDate, tenderFinDate, loaDate, loaFilePath, agreementDate, agreementFilePath, workOrderDate, workOrderFilePath, 
	            workCommDate, reason, remarks_txt
	        );
	        
	        addNonERPLog(""+lastInsertId,loginid, formattedSql);
        }
        // ✅ Return the generated ID
        return estNo;
    }
    
    @Transactional
	public boolean updateESTno(int estid ,String estNo) {
		String  sqlQuery = "UPDATE `non_erp_works` SET `estimate_no`='"+estNo+"' WHERE `estid`='"+estid+"'";
		jdbcTemplate.update(sqlQuery);
		return true;
	}

    @Transactional
	public boolean addNonERPLog(String id,String loginid, String query) {
		String  sqlQuery = "INSERT INTO `non_erp_logs`(`estid`, `updateby`, `query`) VALUES (?,?,?)";
		jdbcTemplate.update(sqlQuery,id,loginid,query);
		return true;
	}
    
    // Method to process the form data
    public int processFormData(String zone, String ward, String projectname, String estimatenumber,
                               String estimatedate, String location, String sourceoffund, String schemename,
                               String category, String subcategory, String estimateamount, String department,
                               String loacopy, String agreementcopy, String workordercopy,
                               String sanctioncopy, String adminsandate, String worksdesc) throws DataAccessException, IOException {

        // Prepare SQL Insert Query
        String sql = "INSERT INTO works_update (zone, ward, project_name, estimate_no, estimate_date, location, " +
                "fund_source, scheme, category, sub_category, est_amount, department, loa_file, agreement_file, " +
                "work_order_file, adm_san_file, admin_sanc_date, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql, zone, ward, projectname, estimatenumber, estimatedate, location, sourceoffund,
                schemename, category, subcategory, estimateamount, department, loacopy, agreementcopy,
                workordercopy, sanctioncopy, adminsandate, worksdesc);
    }

    private String getUserAccess(String userid) {
    	String ward = "0";
    	String department="0";
    	
		String sql = "SELECT `id`, `userid`, `zone`, `ward`, `department` FROM `works_access` WHERE `userid`=? LIMIT 1";
		System.out.println("SELECT `id`, `userid`, `zone`, `ward` FROM `works_access` WHERE `userid`="+userid+" LIMIT 1");
		List<Map<String, Object>> taskresult = jdbcTemplate.queryForList(sql, userid);
		if(!taskresult.isEmpty()) {
			ward = ""+taskresult.get(0).get("ward");
			department = ""+taskresult.get(0).get("department");
		}
		return ward+"_"+department;
	}

    public List<Map<String, Object>> getAllERPWorks(String loginId) {
    	
        String accessData = getUserAccess(loginId);
    	String where="ew.`nonerp`=0 ";
    	
    	// Split the string using "_"
        String[] parts = accessData.split("_");
        
    	if(!parts[0].equals("all")) {
    		where =" AND ew.`ward` IN ("+parts[0]+") ";
    	}
    	if(!parts[1].equals("all")) {
    		if(!where.equals(""))
    		{
    			where = where + " AND ew.`department` IN ("+parts[1]+")";
    		}
    		else {
    			where = where + " AND ew.`department` IN ("+parts[1]+")";
    		}
    	}
    	
    	if(!where.equals("")) {
    		where = " WHERE "+ where;
    	}
    	
        String sql = "SELECT ew.estid, ew.estimate_no, ew.estimate_date, ew.zone, ew.ward, ew.project_name, ew.location, " +
                "                dm.dept_name, ew.contractor_name, ew.contractor_period, fm.name as fund_source, sm.name as scheme, " +
                "                wm.name as category, tm.description as sub_category FROM gcc_works_status.erp_works ew " +
                "                left join gcc_works_status.department_master dm on dm.id = ew.department " +
                "                left join gcc_works_status.workstype_master wm on wm.id = ew.category " +
                "                left join gcc_works_status.typeofwork_master tm on tm.id = ew.sub_category " +
                "                left join gcc_works_status.fundsource_master fm on fm.id = ew.fund_Source " +
                "                left join gcc_works_status.scheme_master sm on sm.id = ew.scheme"
                + " "+where;
        return jdbcTemplate.queryForList(sql);
    }


    public Map<String, Object> getEstimateDetails(String estimateNo) {
        String sql = "SELECT estimate_no,estimate_date,zone,ward, project_name, location, department, contractor_name, contractor_period, fund_source, scheme, category, " +
                "sub_category,est_amount, tech_sanc_date, admin_sanc_date, tender_call_date, tender_fin_date, loa_date, agreement_date, work_order_date, work_comm_date, " +
                "adm_san_file, loa_file, agreement_file, work_order_file, iconic_project, reason, remarks  FROM works_update WHERE estimate_no = ?"; // Replace with your actual query

        // Fetch the details from the database as a Map
        List<Map<String, Object>> details = jdbcTemplate.queryForList(sql, estimateNo);

        if (details.isEmpty()) {
            return new HashMap<>(); // Return an empty map instead of throwing an error
        }

        return details.get(0); // Return the result map
    }

    public List<Map<String, Object>> getSchemeList() {
        String sql = "SELECT * FROM gcc_works_status.scheme_master WHERE isactive=1 order by NAME asc";
        // Fetch the details from the database as a Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        return result; // Return the result map
    }

    public List<Map<String, Object>> getCategoryList() {
        String sql = "SELECT *  FROM gcc_works_status.workstype_master order by NAME asc";
        // Fetch the details from the database as a Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        return result; // Return the result map
    }

    public List<Map<String, Object>> getSubCategoryList() {
        String sql = "SELECT *  FROM gcc_works_status.typeofwork_master order by DESCRIPTION asc";
        // Fetch the details from the database as a Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        return result; // Return the result map
    }

    public List<Map<String, Object>> getDepartmentList() {
        String sql = "SELECT *  FROM gcc_works_status.department_master WHERE isactive=1 order by DEPT_NAME asc";
        // Fetch the details from the database as a Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        return result; // Return the result map
    }

    public List<Map<String, Object>> getSourceofFundList() {
        String sql = "SELECT *  FROM gcc_works_status.fundsource_master WHERE isactive=1 order by NAME asc";
        // Fetch the details from the database as a Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        return result; // Return the result map
    }

    public int updateEstimateDetails(Map<String, Object> updatedDetails, String sanFilePath, String loaFilePath,
                                     String agreementFilePath, String workOrderFilePath) throws IOException {

    	// ✅ Base SQL query
        StringBuilder sql = new StringBuilder("UPDATE gcc_works_status.erp_works SET "
                + "estimate_date = ?, zone = ?, ward = ?, project_name = ?, location = ?, "
                + "isiconic = ?, department = ?, contractor_name = ?, contractor_period = ?, "
                + "fund_source = ?, scheme = ?, category = ?, sub_category = ?, estimation_amount = ?, "
                + "technical_sanction_date = ?, admin_sanction_date = ?, tender_called_date = ?, tender_finalized_date = ?, "
                + "loa_date = ?, agreement_date = ?, work_order_date = ?, work_commenced_date = ? ");

        // ✅ Dynamically append file updates if new files are provided
        List<Object> params = new ArrayList<>(Arrays.asList(
                updatedDetails.get("estimate_date"),
                updatedDetails.get("zone"),
                updatedDetails.get("ward"),
                updatedDetails.get("project_name"),
                updatedDetails.get("location"),
                updatedDetails.get("iconic_project"),  // ✅ Ensure iconic_project is updated
                updatedDetails.get("department"),
                updatedDetails.get("contractor_name"),
                updatedDetails.get("contractor_period"),
                updatedDetails.get("fund_source"),
                updatedDetails.get("scheme"),
                updatedDetails.get("category"),
                updatedDetails.get("sub_category"),
                updatedDetails.get("est_amount"),
                updatedDetails.get("tech_sanc_date"),
                updatedDetails.get("admin_sanc_date"),
                updatedDetails.get("tender_call_date"),
                updatedDetails.get("tender_fin_date"),
                updatedDetails.get("loa_date"),
                updatedDetails.get("agreement_date"),
                updatedDetails.get("work_order_date"),
                updatedDetails.get("work_comm_date")
        ));

        if (!sanFilePath.isEmpty()) {
            sql.append(", admin_sanction_file = ?");
            params.add(sanFilePath);
        }
        if (!loaFilePath.isEmpty()) {
            sql.append(", loa_file = ?");
            params.add(loaFilePath);
        }
        if (!agreementFilePath.isEmpty()) {
            sql.append(", agreement_file = ?");
            params.add(agreementFilePath);
        }
        if (!workOrderFilePath.isEmpty()) {
            sql.append(", work_order_file = ?");
            params.add(workOrderFilePath);
        }

        if (updatedDetails.get("reason") != null && !updatedDetails.get("reason").toString().isEmpty()) {
            sql.append(", reason = ?");
            params.add(updatedDetails.get("reason"));
        }

        if (updatedDetails.get("remarks") != null && !updatedDetails.get("remarks").toString().isEmpty()) {
            sql.append(", remarks = ?");
            params.add(updatedDetails.get("remarks"));
        }

        // ✅ Only update iconic_project if it's a Non-ERP estimate
        if (updatedDetails.containsKey("iconic_project")) {
            sql.append(", isiconic = ?");
            params.add(updatedDetails.get("iconic_project"));
        }

        // ✅ Add WHERE clause
        sql.append(" WHERE estimate_no = ?");
        params.add(updatedDetails.get("estimate_no"));

        // ✅ Execute update
        return jdbcTemplate.update(sql.toString(), params.toArray());
    }

    public String getSequence() {
        String sql = "select sequence from gcc_works_status.nonerp_sequence";
        return jdbcTemplate.queryForObject(sql, String.class);
    }

    public int updateSequence(String seq) {
        String sql = "update gcc_works_status.nonerp_sequence set sequence = ? where id = 1";
        return jdbcTemplate.update(sql, seq);
    }

    public List<Map<String, Object>> getFilteredWorksList(String zone, String ward, String department, String fundsource,
                                                          LocalDate fromDate, LocalDate toDate, String filterType, String estno) {

        StringBuilder sql = new StringBuilder("SELECT * FROM works_update");
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (zone != null && !zone.isEmpty()) {
            conditions.add("zone = ?");
            params.add(zone);
        }
        if (ward != null && !ward.isEmpty()) {
            conditions.add("ward = ?");
            params.add(ward);
        }
        if (department != null && !department.isEmpty()) {
            conditions.add("department = ?");
            params.add(department);
        }
        if (fundsource != null && !fundsource.isEmpty()) {
            conditions.add("fund_source = ?");
            params.add(fundsource);
        }
        if (fromDate != null) {
            conditions.add("estimate_date >= ?");
            params.add(fromDate);
        }
        if (toDate != null) {
            conditions.add("estimate_date <= ?");
            params.add(toDate);
        }

        if (filterType != null && filterType.equals("erpWorks")) {
            conditions.add("estimate_no not like'M%'");
        }

        if (filterType != null && filterType.equals("nonErpWorks")) {
            conditions.add("estimate_no like'M%'");
        }

        if (estno != null && !estno.isEmpty()) {
            conditions.add("estimate_no = ?");
            params.add(estno);
        }

        // Append conditions only if there are any
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        System.out.println("sql:  " + sql);

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    //model service

    public List<Map<String, Object>> fetchEstimateByCode(String estimateCode) {
        String query = "SELECT * FROM gcc_works_status.erp_works WHERE estimate_no=?";
        return jdbcTemplate.queryForList(query, estimateCode);
    }


    // save details service
    @Transactional
    public List<Map<String, Object>> saveWorkDetailsbycode(List<Map<String, Object>> workDetails) {
        String sql = "INSERT INTO works_details (estimate_number, category, sub_category, road_name, road_type, work_type_1, work_type_2, work_type_3, length, width, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (Map<String, Object> work : workDetails) {
            jdbcTemplate.update(sql,
                    work.get("estimate_number"),
                    work.get("category"),
                    work.get("sub_category"),
                    work.get("road_name"),
                    work.get("road_type"),
                    work.get("work_type_1"),
                    work.get("work_type_2"),
                    work.get("work_type_3"),
                    work.get("length"),
                    work.get("width"),
                    work.get("remarks")
            );
        }

        return workDetails;
    }

    public List<String> getAllWorksDetails() {
        String sql = "SELECT distinct  estimate_no FROM gcc_works_status.erp_works";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<Map<String, Object>> getAllReasons() {
        String sql = "SELECT id, reasons FROM gcc_works_status.reason_master";
        return jdbcTemplate.queryForList(sql);
    }

    public String convertDateFormat(String date) {
        if (date == null || date.isEmpty() || date.equals("_--_")) {
            return null; // Return null for empty or invalid date values
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            System.err.println("Date conversion error for: " + date + " - " + e.getMessage());
            return null; // Return null to avoid inserting invalid dates
        }
    }

    public int saveERPEstimate(String estimateNo, String estimateDate, String zone, String ward, String projectName,
                            int iconicProject, String location, String department, String contractorName,
                            String contractorPeriod, String fundSource, String scheme, String category,
                            String subCategory, String estAmount, String techSancDate, String adminSancDate,
                            String admSanFile, String tenderCallDate, String tenderFinDate, String loaDate,
                            String loaFile, String agreementDate, String agreementFile, String workOrderDate,
                            String workOrderFile, String workCommDate) {

        String sql = "INSERT INTO gcc_works_status.erp_works (estimate_no, estimate_date, zone, ward, project_name, location, " +
                "department, contractor_name, contractor_period, fund_source, scheme, category, sub_category, " +
                "estimation_amount, technical_sanction_date, admin_sanction_date, admin_sanction_file, tender_called_date, tender_finalized_date, loa_date, " +
                "loa_file, agreement_date, agreement_file, work_order_date, work_order_file, work_commenced_date, isiconic) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql, estimateNo, estimateDate, zone, ward, projectName, location, department,
                contractorName, contractorPeriod, fundSource, scheme, category, subCategory, estAmount,
                techSancDate, adminSancDate, admSanFile, tenderCallDate, tenderFinDate, loaDate, loaFile,
                agreementDate, agreementFile, workOrderDate, workOrderFile, workCommDate, iconicProject);
    }

    public List<Map<String, Object>> getestimateworkdetails(String estNo) {
        String sql = "SELECT * FROM works_details where estimate_number =?";
        return jdbcTemplate.queryForList(sql, estNo);
    }

    public List<Map<String, Object>> getQuestionsBySubCatId(int subCatId) {
        String sql = "SELECT qm.id, qm.question_name, qm.input_type_id, qm.sub_cat_id, qm.field_order, " +
                "im.name AS input_type_name, am.id AS answer_id, am.answer_name, am.haschild AS a_haschild, " +
                " eg.code AS sub_cat_code, eg.id AS sub_cat_id " +
                "FROM gcc_works_status.question_master qm " +
                "LEFT JOIN gcc_works_status.input_type_master im ON qm.input_type_id = im.id " +
                "LEFT JOIN gcc_works_status.ans_master am ON am.ques_id = qm.id  " +
                "JOIN gcc_works_status.typeofwork_master eg ON eg.id = qm.sub_cat_id " +
                "WHERE qm.sub_cat_id = ? AND qm.isactive=1 and qm.field_order !=0 order by qm.field_order ";

        return jdbcTemplate.queryForList(sql, subCatId);
    }



    public Integer getSubCategoryIdByName(String subCategoryName) {
        String sql = "SELECT id FROM egw_typeofwork WHERE code = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, subCategoryName);

    }

    // save answer method


    public void saveAnswers(List<Map<String, Object>> answers) {
        if (answers == null || answers.isEmpty()) {
            System.err.println("No answers provided.");
            return;
        }

        // Extract estimate number
        String estimateNumber = (String) answers.get(0).getOrDefault("estimateNumber", "");

        if (estimateNumber == null || estimateNumber.isEmpty()) {
            System.err.println("Estimate number is missing.");
            return;
        }

        try {
            // Get estid using estimate number
            String query = "SELECT estid FROM gcc_works_status.erp_works WHERE estimate_no = ?";
            Integer estid = jdbcTemplate.queryForObject(query, Integer.class, estimateNumber);
            System.out.println("estid: " + estid);

            if (estid == null) {
                System.err.println("Estimate ID not found for estimate number: " + estimateNumber);
                return;
            }

            // SQL for inserting data
            String sql = "INSERT INTO gcc_works_status.erp_works_task (estid, estimate_no, sub_cat_id, question_id, answer_text, ans_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            // Insert answers
            for (Map<String, Object> answer : answers) {
                try {
                    Integer questionId = convertToInteger(answer.get("questionId"));
                    String answerText = (String) answer.getOrDefault("answerText", "");
                    Integer answerId = convertToInteger(answer.get("answerId"));
                    Integer subCatId = convertToInteger(answer.get("subCatId"));

                    jdbcTemplate.update(sql, estid, estimateNumber, subCatId, questionId, answerText, answerId);
                } catch (Exception e) {
                    System.err.println("Error saving answer for questionId: " + answer.get("questionId") + " - " + e.getMessage());
                }
            }

            System.out.println("Answers saved successfully.");
        } catch (Exception e) {
            System.err.println("Error fetching estid for estimate number: " + estimateNumber + " - " + e.getMessage());
        }
    }

    private Integer convertToInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                System.err.println("Invalid integer value: " + value);
                return null;
            }
        }
        System.err.println("Unsupported data type: " + value.getClass().getName());
        return null;
    }


    public Map<String, Object> getQuestionDetailsById(int answerId) {

        String sql = "SELECT qm.id, qm.question_name, qm.haschild, qm.input_type_id, qm.sub_cat_id, qm.field_order, "
                + "im.name AS input_type_name "
                + "FROM gcc_works_status.question_master qm "
                + "LEFT JOIN gcc_works_status.input_type_master im ON qm.input_type_id = im.id "
                + "LEFT JOIN gcc_works_status.ans_master am ON am.id = qm.haschild "
                + "WHERE am.id = ? AND am.haschild = 1 AND qm.isactive=1" ;

        System.out.println("sql..1"+sql);

        return jdbcTemplate.queryForMap(sql, answerId);
    }

    public List<Map<String, Object>> getAnsDetailsByQId(String quesId) {

        String sql = "select im.name ,qm.input_type_id,qm.id as QId,am.answer_name,am.id as AId, am.haschild "
                + " from ans_master am "
                + "  Left join gcc_works_status.question_master qm ON am.ques_id=qm.id "
                + "  Left join gcc_works_status.input_type_master im on qm.input_type_id=im.id "
                + "  where qm.id =? and qm.isactive=1" ;

        System.out.println("sql..2"+sql);

        return jdbcTemplate.queryForList(sql, quesId);
    }

    // fetch the saved data


    public List<Map<String, Object>> getSavedData(String estimateNumber) {

        String sql = "SELECT  ewt.*, qm.question_name, am.answer_name, im.name, qm.field_order, "
                + " CASE WHEN ewt.ans_id = am.id THEN 1 ELSE 0 END AS is_saved_answer "
                + " FROM gcc_works_status.erp_works_task ewt "
                + " LEFT JOIN gcc_works_status.question_master qm ON qm.id = ewt.question_id "
                + " LEFT JOIN gcc_works_status.ans_master am ON am.ques_id = qm.id "
                + " LEFT JOIN gcc_works_status.input_type_master im ON im.id = qm.input_type_id "
                + " WHERE ewt.estimate_no = ?  group by ewt.id,am.answer_name,am.id order by qm.field_order ";


        return jdbcTemplate.queryForList(sql, estimateNumber);
    }


   /* public List<Map<String, Object>> getSavedData(String estimateNumber) {

        String sql = "SELECT DISTINCT wt.*, qm.question_name, am.answer_name, im.name, qm.field_order, "
                + " CASE WHEN wt.ans_id = am.id THEN 1 ELSE 0 END AS is_saved_answer "
                + " FROM works_task wt "
                + " LEFT JOIN question_master qm ON qm.id = wt.question_id "
                + " LEFT JOIN ans_master am ON am.ques_id = qm.id "
                + " LEFT JOIN input_type_master im ON im.id = qm.input_type_id "
                + " WHERE wt.estimate_no = ?  group by wt.id,am.answer_name,am.id,wt.group_id order by qm.field_order";

        return jdbcTemplate.queryForList(sql, estimateNumber);
    }*/

    public List<Map<String, Object>> getAllNonERPWorks(String loginId) {
    	String accessData = getUserAccess(loginId);
    	String where="";
    	
    	// Split the string using "_"
        String[] parts = accessData.split("_");
        
    	if(!parts[0].equals("all")) {
    		where =" new.`ward` IN ("+parts[0]+") ";
    	}
    	if(!parts[1].equals("all")) {
    		if(!where.equals(""))
    		{
    			where = where + "AND new.`department` IN ("+parts[1]+")";
    		}
    		else {
    			where = where + "new.`department` IN ("+parts[1]+")";
    		}
    	}
    	
    	if(!where.equals("")) {
    		where = " WHERE "+ where;
    	}
    	
        String sql = "SELECT new.estid, new.estimate_no, new.estimate_date, new.zone, new.ward, new.project_name, new.location, new.filenumber, new.estimation_amount, " +
                "                dm.dept_name, new.contractor_name, new.contractor_period, fm.name as fund_source, sm.name as scheme, " +
                "                wm.name as category, tm.description as sub_category FROM gcc_works_status.non_erp_works new " +
                "                left join gcc_works_status.department_master dm on dm.id = new.department " +
                "                left join gcc_works_status.workstype_master wm on wm.id = new.category " +
                "                left join gcc_works_status.typeofwork_master tm on tm.id = new.sub_category " +
                "                left join gcc_works_status.fundsource_master fm on fm.id = new.fund_Source " +
                "                left join gcc_works_status.scheme_master sm on sm.id = new.scheme"
                + " "+ where;
        return jdbcTemplate.queryForList(sql);
    }


    public Map<String, Object> getERPEstimateDetails(String estimateNo) {

        String sql = "select ew.estimate_no, ew.estimate_date, ew.zone, ew.ward, ew.project_name, ew.location, ew.department, ew.contractor_name, " +
                "ew.contractor_period, ew.estimation_amount as est_amount, ew.technical_sanction_date as tech_sanc_date, ew.admin_sanction_date as admin_sanc_date," +
                "ew.tender_called_date as tender_call_date, ew.tender_called_date as tender_fin_date, ew.loa_date, ew.agreement_date, ew.work_order_date, " +
                "ew.work_commenced_date as work_comm_date, ew.admin_sanction_file as adm_san_file, ew.loa_file, ew.agreement_file, ew.work_order_file, " +
                "ew.isiconic as iconic_project, dm.dept_name, ew.fund_source as fundsource, fm.name as fund_source, " +
                "ew.scheme as schemeid, sm.name as scheme,  ew.category as categoryid, wm.name as category, ew.sub_category as subcategoryid, " +
                "tm.description as sub_category " +
                "from gcc_works_status.erp_works ew " +
                "                left join gcc_works_status.department_master dm on dm.id = ew.department " +
                "                left join gcc_works_status.workstype_master wm on wm.id = ew.category " +
                "                left join gcc_works_status.typeofwork_master tm on tm.id = ew.sub_category " +
                "                left join gcc_works_status.fundsource_master fm on fm.id = ew.fund_Source " +
                "                left join gcc_works_status.scheme_master sm on sm.id = ew.scheme where ew.estimate_no = ? LIMIT 100";

        // Fetch the details from the database as a Map
        List<Map<String, Object>> details = jdbcTemplate.queryForList(sql, estimateNo);

        if (details.isEmpty()) {
            return new HashMap<>(); // Return an empty map instead of throwing an error
        }

        return details.get(0); // Return the result map
    }

    public Map<String, Object> getNonERPEstimateDetails(String estimateNo) {

        String sql = "select ew.estimate_no, ew.estimate_date, ew.zone, ew.ward, ew.project_name, ew.location, ew.department, ew.contractor_name, " +
                "ew.contractor_period, ew.estimation_amount as est_amount, ew.technical_sanction_date as tech_sanc_date, ew.admin_sanction_date as admin_sanc_date," +
                "ew.tender_called_date as tender_call_date, ew.tender_called_date as tender_fin_date, ew.loa_date, ew.agreement_date, ew.work_order_date, " +
                "ew.work_commenced_date as work_comm_date, ew.admin_sanction_file as adm_san_file, ew.loa_file, ew.agreement_file, ew.work_order_file, " +
                "ew.isiconic as iconic_project, dm.dept_name, ew.fund_source as fundsource, fm.name as fund_source, " +
                "ew.scheme as schemeid, sm.name as scheme,  ew.category as categoryid, wm.name as category, ew.sub_category as subcategoryid, ew.reason, ew.remarks, " +
                "tm.description as sub_category, ew.`project_type`, ew.`govt_funded_project`, ew.`special_category`, ew.`filenumber` " +
                "from gcc_works_status.non_erp_works ew " +
                "                left join gcc_works_status.department_master dm on dm.id = ew.department " +
                "                left join gcc_works_status.workstype_master wm on wm.id = ew.category " +
                "                left join gcc_works_status.typeofwork_master tm on tm.id = ew.sub_category " +
                "                left join gcc_works_status.fundsource_master fm on fm.id = ew.fund_Source " +
                "                left join gcc_works_status.scheme_master sm on sm.id = ew.scheme where ew.estimate_no = ? ";

        // Fetch the details from the database as a Map
        List<Map<String, Object>> details = jdbcTemplate.queryForList(sql, estimateNo);

        if (details.isEmpty()) {
            return new HashMap<>(); // Return an empty map instead of throwing an error
        }

        return details.get(0); // Return the result map
    }

    public int updateNonERPEstimateDetails(Map<String, Object> updatedDetails, String sanFilePath, String loaFilePath,
                                     String agreementFilePath, String workOrderFilePath) throws IOException {

    	// ✅ Base SQL query
        StringBuilder sql = new StringBuilder("UPDATE gcc_works_status.non_erp_works SET "
                + "estimate_date = ?, zone = ?, ward = ?, project_name = ?, location = ?, "
                + "isiconic = ?, department = ?, contractor_name = ?, contractor_period = ?, "
                + "fund_source = ?, scheme = ?, category = ?, sub_category = ?, estimation_amount = ?, "
                + "technical_sanction_date = ?, admin_sanction_date = ?, tender_called_date = ?, tender_finalized_date = ?, "
                + "loa_date = ?, agreement_date = ?, work_order_date = ?, work_commenced_date = ?, "
                + "project_type=?, govt_funded_project=?, special_category=?, filenumber=? ");

        // ✅ Dynamically append file updates if new files are provided
        List<Object> params = new ArrayList<>(Arrays.asList(
                updatedDetails.get("estimate_date"),
                updatedDetails.get("zone"),
                updatedDetails.get("ward"),
                updatedDetails.get("project_name"),
                updatedDetails.get("location"),
                updatedDetails.get("iconic_project"),  // ✅ Ensure iconic_project is updated
                updatedDetails.get("department"),
                updatedDetails.get("contractor_name"),
                updatedDetails.get("contractor_period"),
                updatedDetails.get("fund_source"),
                updatedDetails.get("scheme"),
                updatedDetails.get("category"),
                updatedDetails.get("sub_category"),
                updatedDetails.get("est_amount"),
                updatedDetails.get("tech_sanc_date"),
                updatedDetails.get("admin_sanc_date"),
                updatedDetails.get("tender_call_date"),
                updatedDetails.get("tender_fin_date"),
                updatedDetails.get("loa_date"),
                updatedDetails.get("agreement_date"),
                updatedDetails.get("work_order_date"),
                updatedDetails.get("work_comm_date"),
                updatedDetails.get("projecttype"),
                updatedDetails.get("govtfund"),
                updatedDetails.get("specialcat"),
                updatedDetails.get("filenumber")
        ));

        if (!sanFilePath.isEmpty()) {
            sql.append(", admin_sanction_file = ?");
            params.add(sanFilePath);
        }
        if (!loaFilePath.isEmpty()) {
            sql.append(", loa_file = ?");
            params.add(loaFilePath);
        }
        if (!agreementFilePath.isEmpty()) {
            sql.append(", agreement_file = ?");
            params.add(agreementFilePath);
        }
        if (!workOrderFilePath.isEmpty()) {
            sql.append(", work_order_file = ?");
            params.add(workOrderFilePath);
        }

        if (updatedDetails.get("reason") != null && !updatedDetails.get("reason").toString().isEmpty()) {
            sql.append(", reason = ?");
            params.add(updatedDetails.get("reason"));
        }

        if (updatedDetails.get("remarks") != null && !updatedDetails.get("remarks").toString().isEmpty()) {
            sql.append(", remarks = ?");
            params.add(updatedDetails.get("remarks"));
        }

        // ✅ Only update iconic_project if it's a Non-ERP estimate
        if (updatedDetails.containsKey("iconic_project")) {
            sql.append(", isiconic = ?");
            params.add(updatedDetails.get("iconic_project"));
        }

        // ✅ Add WHERE clause
        sql.append(" WHERE estimate_no = ?");
        params.add(updatedDetails.get("estimate_no"));

        // ✅ Execute update
        return jdbcTemplate.update(sql.toString(), params.toArray());
    }
    
    ///// Balaji
    
    public List<Map<String, Object>> getestimateworkdetailsById(String estid) {
        String sql = "SELECT * FROM works_data_erp where `id`=? LIMIT 1";
        return jdbcTemplate.queryForList(sql, estid);
    }
    
    public int saveEstimateWorkDetails(
    		String loginid,
    		String estid,
    		String isiconic,
    		MultipartFile admin_sanction_file, 
    		MultipartFile loa_file, 
    		MultipartFile agreement_file,
    		MultipartFile work_order_file,
    		String techSanctionDate,
    		String adminSanctionDate,
    		String tenderCallDate,
    		String tenderFinalizedDate,
    		String loaDate,
    		String agreementDate,
    		String workOrderDate,
    		String workCommencedDate,
    		String projecttype,
    		String govtfund,
    		String specialcat
    		) {
        // Fetch the estimate details
        List<Map<String, Object>> estimateDetails = getestimateworkdetailsById(estid);

        if (estimateDetails.isEmpty()) {
            System.out.println("No records found for ID: " + estid);
            return 0;
        }
        
        try {
        // File Upload and save URL
        String admin_sanction_file_url = saveFile(admin_sanction_file, "admin_sanction", "erp");
        String loa_file_url = saveFile(loa_file, "loa", "erp");
        String agreement_file_url = saveFile(agreement_file, "agreement", "erp");
        String work_order_file_url = saveFile(work_order_file, "work_order", "erp");
        
        // Extract the first record
        Map<String, Object> data = estimateDetails.get(0);
       
        // Call saveEstimate with the extracted values
        return saveEstimate(
	            String.valueOf(data.get("ESTIMATE_NO")),
	            String.valueOf(data.get("ESTIMATE_DATE")),
	            String.valueOf(data.get("ZONE")),
	            String.valueOf(data.get("WARD")),
	            String.valueOf(data.get("PROJECT_NAME")),
	            isiconic,
	            String.valueOf(data.get("LOCATION")),
	            String.valueOf(data.get("DEPARTMENT_NAME")),
	            String.valueOf(data.get("CONTRACTOR_NAME")),
	            String.valueOf(data.get("CONTRACTOR_PERIOD")),
	            String.valueOf(data.get("FUND_SOURCE")),
	            String.valueOf(data.get("SCHEME")),
	            String.valueOf(data.get("CATEGORY")),
	            String.valueOf(data.get("SUB_CATEGORY")),
	            String.valueOf(data.get("ESTIMATION_AMOUNT")),
	            techSanctionDate,
	            //String.valueOf(data.get("TECHNICAL_SANCTION_DATE")),
	            adminSanctionDate,
	            //String.valueOf(data.get("ADMIN_SANCTION_DATE")),
	            admin_sanction_file_url,
	            tenderCallDate,
	            //String.valueOf(data.get("TENDER_CALLED_DATE")),
	            tenderFinalizedDate,
	            //String.valueOf(data.get("TENDER_FINALIZED_DATE")),
	            loaDate,
	            //String.valueOf(data.get("LOA_DATE")),
	            loa_file_url,
	            agreementDate,
	            //String.valueOf(data.get("AGREEMENT_DATE")),
	            agreement_file_url,
	            workOrderDate, 
	            //String.valueOf(data.get("WORK_ORDER_DATE")),
	            work_order_file_url,
	            workCommencedDate,
	            //String.valueOf(data.get("WORK_COMMENCED_DATE")),
	            String.valueOf(data.get("id")),
	            loginid,
	            projecttype,
	            govtfund,
	            specialcat,
	            String.valueOf(data.get("EST_VAL_WITH_OH"))
	        );
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
            System.out.println("Error uploading files: " + e.getMessage());
            return 0;
        }
    }
    
    public int saveEstimate(String estimateNo, String estimateDate, String zone, String ward, String projectName,
                            String iconicProject, String location, String department, String contractorName,
                            String contractorPeriod, String fundSource, String scheme, String category,
                            String subCategory, String estAmount, String techSancDate, String adminSancDate,
                            String admSanFile, String tenderCallDate, String tenderFinDate, String loaDate,
                            String loaFile, String agreementDate, String agreementFile, String workOrderDate,
                            String workOrderFile, String workCommDate, String works_data_erp_id, String loginid,
                            String projecttype,String govtfund,String specialcat, String estAmountWithOH) {

    	String departmentId = getDepartmentId(department);
    	String categoryId = getCategoryId(category);
    	String subCategoryId = getSubCategoryId(subCategory);
    	String fundSourceId = getFundSourceId(fundSource);
    	String schemeId = getSchemeId(scheme);
    	
    	techSancDate = normalizeDate(techSancDate);
    	agreementDate = normalizeDate(agreementDate);
    	adminSancDate = normalizeDate(adminSancDate);
    	tenderCallDate = normalizeDate(tenderCallDate);
    	tenderFinDate = normalizeDate(tenderFinDate);
    	loaDate = normalizeDate(loaDate);
    	workOrderDate = normalizeDate(workOrderDate);
    	workCommDate = normalizeDate(workCommDate);
    	
        String sql = "INSERT INTO `gcc_works_status`.`erp_works` (estimate_no, estimate_date, zone, ward, project_name, location, " +
                "department, contractor_name, contractor_period, fund_source, scheme, category, sub_category, " +
                "estimation_amount, technical_sanction_date, admin_sanction_date, admin_sanction_file, tender_called_date, " +
                "tender_finalized_date, loa_date, " +
                "loa_file, agreement_date, agreement_file, work_order_date, work_order_file, work_commenced_date,isiconic,works_data_erp_id, " +
                "project_type, govt_funded_project, special_category, est_val_with_oh) " +
                "VALUES "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + " ?, ?, ?, ?, ?, ?, ?, ?, "
                + " ?, ?, ?, ?)";

        int result = jdbcTemplate.update(sql, 
        		estimateNo, estimateDate, zone, ward, projectName, 
        		location, departmentId, contractorName, contractorPeriod, fundSourceId, 
        		schemeId, categoryId, subCategoryId, estAmount, techSancDate,
                adminSancDate, admSanFile, tenderCallDate, tenderFinDate, loaDate, 
                loaFile, agreementDate, agreementFile, workOrderDate, workOrderFile, 
                workCommDate, iconicProject, works_data_erp_id,
                projecttype, govtfund, specialcat, estAmountWithOH);
        
        updateWorksDataErpStatus(works_data_erp_id);
        
        runSqlScript();
        
        String logsql = "INSERT INTO `gcc_works_status`.`erp_works` (estimate_no, estimate_date, zone, ward, project_name, location, " +
                "department, contractor_name, contractor_period, fund_source, scheme, category, sub_category, " +
                "estimation_amount, technical_sanction_date, admin_sanction_date, admin_sanction_file, tender_called_date, " +
                "tender_finalized_date, loa_date, " +
                "loa_file, agreement_date, agreement_file, work_order_date, work_order_file, work_commenced_date,isiconic,works_data_erp_id," +
                "project_type, govt_funded_project, special_category, est_val_with_oh) " +
                "VALUES ('" + estimateNo + "', '" + estimateDate + "', '" + zone + "', '" + ward + "', '" + projectName + "', '" + 
                location + "', '" + departmentId + "', '" + contractorName + "', '" + contractorPeriod + "', '" + fundSourceId + "', '" +
                schemeId + "', '" + categoryId + "', '" + subCategoryId + "', '" + estAmount + "', '" + techSancDate + "', '" + 
                adminSancDate + "', '" + admSanFile + "', '" + tenderCallDate + "', '" + tenderFinDate + "', '" + loaDate + "', '" + 
                loaFile + "', '" + agreementDate + "', '" + agreementFile + "', '" + workOrderDate + "', '" + workOrderFile + "', '" + 
                workCommDate + "', '" + iconicProject + "', '" + works_data_erp_id + "','" +
                projecttype + "', '" + govtfund + "', '" + specialcat + "', '"+ estAmountWithOH +"')";

        addERPLog(works_data_erp_id,loginid,logsql);
        
        return result;
    }
    @Transactional
    public void runSqlScript() {
	 // 1. Update zone and ward
	    jdbcTemplate.execute("UPDATE `erp_works` "
	    		+ "SET  "
	    		+ "  `zone` = LPAD(`zone`, 2, '0'), "
	    		+ "  `ward` = LPAD(`ward`, 3, '0')");
	
	    // 2. Insert into erp_works_task
	    jdbcTemplate.execute("INSERT INTO `erp_works_task` (`estid`, `estimate_no`, `sub_cat_id`, `question_id`, `ans_id`, `remarks`)  "
	    		+ "	 SELECT DISTINCT  "
	    		+ "	     estid,  "
	    		+ "	     estimate_no, "
	    		+ "	     sub_category,  "
	    		+ "	     1 AS question_id,  "
	    		+ "	     project_name AS ans_id,  "
	    		+ "	     NULL AS remarks "
	    		+ "	 FROM `erp_works`  ew "
	    		+ "	 WHERE  NOT EXISTS ( "
	    		+ "	        SELECT 1  "
	    		+ "	        FROM erp_works_task ews  "
	    		+ "	        WHERE ews.estid = ew.estid "
	    		+ "	    )");
	
	    // 3. Insert stages for '4RBT'
	    jdbcTemplate.execute("INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`) "
	    		+ "SELECT  "
	    		+ "    ew.estid, "
	    		+ "    twtm.code, "
	    		+ "    twtm.taskname, "
	    		+ "    twtm.orderby, "
	    		+ "    twtm.remarks "
	    		+ "FROM ( "
	    		+ "    SELECT DISTINCT estid  "
	    		+ "    FROM `erp_works` ew "
	    		+ "    WHERE (project_name LIKE \"%BM%\"  "
	    		+ "           OR project_name LIKE \"%BC%\"  "
	    		+ "           OR project_name LIKE \"%BT%\" "
	    		+ "		   OR project_name LIKE \"%B.T%\") "
	    		+ "      AND sub_category = 4  "
	    		+ "      AND category IN (8, 9, 14, 15) "
	    		+ "	  AND NOT EXISTS ( "
	    		+ "	        SELECT 1  "
	    		+ "	        FROM erp_works_stages ews  "
	    		+ "	        WHERE ews.estid = ew.estid "
	    		+ "	    ) "
	    		+ ") ew "
	    		+ "CROSS JOIN ( "
	    		+ "    SELECT *  "
	    		+ "    FROM typeofwork_task_master  "
	    		+ "    WHERE code = '4RBT' "
	    		+ ") twtm");
	
	    // Add similar jdbcTemplate.execute("..."); for each SQL block in your script
	
	    // 4. Insert stages for '4FBT'
	    jdbcTemplate.execute("INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`) "
	    		+ "SELECT  "
	    		+ "    ew.estid, "
	    		+ "    twtm.code, "
	    		+ "    twtm.taskname, "
	    		+ "    twtm.orderby, "
	    		+ "    twtm.remarks "
	    		+ "FROM ( "
	    		+ "    SELECT DISTINCT estid  "
	    		+ "    FROM `erp_works` ew "
	    		+ "    WHERE (project_name LIKE \"%BM%\"  "
	    		+ "           OR project_name LIKE \"%BC%\"  "
	    		+ "           OR project_name LIKE \"%BT%\" "
	    		+ "		   OR project_name LIKE \"%B.T%\") "
	    		+ "      AND sub_category = 4  "
	    		+ "      AND category IN (1,4,7,11) "
	    		+ "	  AND NOT EXISTS ( "
	    		+ "	        SELECT 1  "
	    		+ "	        FROM erp_works_stages ews  "
	    		+ "	        WHERE ews.estid = ew.estid "
	    		+ "	    ) "
	    		+ ") ew "
	    		+ "CROSS JOIN ( "
	    		+ "    SELECT *  "
	    		+ "    FROM typeofwork_task_master  "
	    		+ "    WHERE code = '4FBT' "
	    		+ ") twtm");
	
	    // 5. Insert stages for '4RCC'
	    jdbcTemplate.execute("INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`) "
	    		+ "SELECT  "
	    		+ "    ew.estid, "
	    		+ "    twtm.code, "
	    		+ "    twtm.taskname, "
	    		+ "    twtm.orderby, "
	    		+ "    twtm.remarks "
	    		+ "FROM ( "
	    		+ "    SELECT DISTINCT estid  "
	    		+ "    FROM `erp_works` ew "
	    		+ "    WHERE ( "
	    		+ "	project_name LIKE \"%CC%\" OR  "
	    		+ "	project_name LIKE \"%concrete%\" OR  "
	    		+ "	project_name LIKE \"%Interlocking%\" OR  "
	    		+ "	project_name LIKE \"%Inter locking%\" OR  "
	    		+ "	project_name LIKE \"%ILP%\" OR "
	    		+ "	project_name LIKE \"%ILB%\" OR "
	    		+ "	project_name LIKE \"%inlet%Chamber%\" ) "
	    		+ "      AND sub_category = 4  "
	    		+ "      AND category IN (8, 9, 14, 15) "
	    		+ "	  AND NOT EXISTS ( "
	    		+ "	        SELECT 1  "
	    		+ "	        FROM erp_works_stages ews  "
	    		+ "	        WHERE ews.estid = ew.estid "
	    		+ "	    ) "
	    		+ ") ew "
	    		+ "CROSS JOIN ( "
	    		+ "    SELECT *  "
	    		+ "    FROM typeofwork_task_master  "
	    		+ "    WHERE code = '4RCC' "
	    		+ ") twtm");
	
	    // 6. Insert stages for '4FCC'
	    jdbcTemplate.execute("INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`) "
	    		+ "SELECT  "
	    		+ "    ew.estid, "
	    		+ "    twtm.code, "
	    		+ "    twtm.taskname, "
	    		+ "    twtm.orderby, "
	    		+ "    twtm.remarks "
	    		+ "FROM ( "
	    		+ "    SELECT DISTINCT estid  "
	    		+ "    FROM `erp_works` ew "
	    		+ "    WHERE ( "
	    		+ "	project_name LIKE \"%CC%\" OR  "
	    		+ "	project_name LIKE \"%concrete%\" OR  "
	    		+ "	project_name LIKE \"%Interlocking%\" OR  "
	    		+ "	project_name LIKE \"%Inter locking%\" OR  "
	    		+ "	project_name LIKE \"%ILP%\" OR "
	    		+ "	project_name LIKE \"%ILB%\" OR "
	    		+ "	project_name LIKE \"%inlet%Chamber%\" ) "
	    		+ "      AND sub_category = 4  "
	    		+ "      AND category IN (1,4,7,11) "
	    		+ "	  AND NOT EXISTS ( "
	    		+ "	        SELECT 1  "
	    		+ "	        FROM erp_works_stages ews  "
	    		+ "	        WHERE ews.estid = ew.estid "
	    		+ "	    ) "
	    		+ ") ew "
	    		+ "CROSS JOIN ( "
	    		+ "    SELECT *  "
	    		+ "    FROM typeofwork_task_master  "
	    		+ "    WHERE code = '4FCC' "
	    		+ ") twtm");
	
	    // 7. Insert stages for '0SS' where sub_category = 4
	    jdbcTemplate.execute("INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`) "
	    		+ "   SELECT  "
	    		+ "       ew.estid, "
	    		+ "       twtm.code, "
	    		+ "       twtm.taskname, "
	    		+ "       twtm.orderby, "
	    		+ "       twtm.remarks "
	    		+ "   FROM ( "
	    		+ "       SELECT DISTINCT estid  "
	    		+ "       FROM `erp_works` ew  "
	    		+ "       WHERE sub_category = 4  "
	    		+ "	   	AND category IN (7,8,9,14,15) "
	    		+ "   	  	 AND NOT EXISTS ( "
	    		+ "   	        SELECT 1  "
	    		+ "   	        FROM erp_works_stages ews  "
	    		+ "   	        WHERE ews.estid = ew.estid "
	    		+ "   	    ) "
	    		+ "   ) ew "
	    		+ "   CROSS JOIN ( "
	    		+ "       SELECT *  "
	    		+ "       FROM typeofwork_task_master  "
	    		+ "       WHERE code = '0SS' "
	    		+ "   ) twtm");
	
	    // 8. Insert stages for '0SS' where sub_category <> 4
	    jdbcTemplate.execute("INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`) "
	    		+ "	   SELECT  "
	    		+ "	       ew.estid, "
	    		+ "	       twtm.code, "
	    		+ "	       twtm.taskname, "
	    		+ "	       twtm.orderby, "
	    		+ "	       twtm.remarks "
	    		+ "	   FROM ( "
	    		+ "	       SELECT DISTINCT estid  "
	    		+ "	       FROM `erp_works` ew "
	    		+ "	       WHERE sub_category <> 4  "
	    		+ "	         AND category IN (2,3,5,6,8,9,10,11,12,13,14,15) "
	    		+ "			 AND NOT EXISTS ( "
	    		+ "			 	        SELECT 1  "
	    		+ "			 	        FROM erp_works_stages ews  "
	    		+ "			 	        WHERE ews.estid = ew.estid "
	    		+ "			 	    ) "
	    		+ "	   ) ew "
	    		+ "	   CROSS JOIN ( "
	    		+ "	       SELECT *  "
	    		+ "	       FROM typeofwork_task_master  "
	    		+ "	       WHERE code = '0SS' "
	    		+ "	   ) twtm");
	
	    // 9. Insert stages for sub_category = 15 and category IN (8,10,15)
	    jdbcTemplate.execute("INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`) "
	    		+ "	   SELECT  "
	    		+ "	       ew.estid, "
	    		+ "	       twtm.code, "
	    		+ "	       twtm.taskname, "
	    		+ "	       twtm.orderby, "
	    		+ "	       twtm.remarks "
	    		+ "	   FROM ( "
	    		+ "	       SELECT DISTINCT estid  "
	    		+ "	       FROM `erp_works` ew "
	    		+ "	       WHERE sub_category =15  "
	    		+ "	         AND category IN (8,10,15) "
	    		+ "			 AND NOT EXISTS ( "
	    		+ "			 	        SELECT 1  "
	    		+ "			 	        FROM erp_works_stages ews  "
	    		+ "			 	        WHERE ews.estid = ew.estid "
	    		+ "			 	    ) "
	    		+ "	   ) ew "
	    		+ "	   CROSS JOIN ( "
	    		+ "	       SELECT *  "
	    		+ "	       FROM typeofwork_task_master  "
	    		+ "	       WHERE code = '0SS' "
	    		+ "	   ) twtm");
	
	    // 10. Insert stages for sub_category = 15 and category = 7 (code = 15NB)
	    jdbcTemplate.execute("INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`) "
	    		+ "   SELECT  "
	    		+ "       ew.estid, "
	    		+ "       twtm.code, "
	    		+ "       twtm.taskname, "
	    		+ "       twtm.orderby, "
	    		+ "       twtm.remarks "
	    		+ "   FROM ( "
	    		+ "       SELECT DISTINCT estid  "
	    		+ "       FROM `erp_works` ew "
	    		+ "       WHERE sub_category = 15  "
	    		+ "         AND category IN (7) "
	    		+ "		 AND NOT EXISTS ( "
	    		+ "		 	        SELECT 1  "
	    		+ "		 	        FROM erp_works_stages ews  "
	    		+ "		 	        WHERE ews.estid = ew.estid "
	    		+ "		 	    ) "
	    		+ "   ) ew "
	    		+ "   CROSS JOIN ( "
	    		+ "       SELECT *  "
	    		+ "       FROM typeofwork_task_master  "
	    		+ "       WHERE code = '15NB' "
	    		+ "   ) twtm");
	
	    // 11. Insert stages for sub_category = 7 and category = 7 (code = 7SWD)
	    jdbcTemplate.execute("INSERT INTO `erp_works_stages` (`estid`, `code`, `stagename`, `orderby`, `remarks`) "
	    		+ " SELECT  "
	    		+ "     ew.estid, "
	    		+ "     twtm.code, "
	    		+ "     twtm.taskname, "
	    		+ "     twtm.orderby, "
	    		+ "     twtm.remarks "
	    		+ " FROM ( "
	    		+ "     SELECT DISTINCT estid  "
	    		+ "     FROM `erp_works` ew "
	    		+ "     WHERE sub_category = 7  "
	    		+ "       AND category IN (7) "
	    		+ "	 AND NOT EXISTS ( "
	    		+ "	 	        SELECT 1  "
	    		+ "	 	        FROM erp_works_stages ews  "
	    		+ "	 	        WHERE ews.estid = ew.estid "
	    		+ "	 	    ) "
	    		+ " ) ew "
	    		+ " CROSS JOIN ( "
	    		+ "     SELECT *  "
	    		+ "     FROM typeofwork_task_master  "
	    		+ "     WHERE code = '7SWD' "
	    		+ " ) twtm");
	}
   
    /*public void runSqlScript() {
        try {
            // Load SQL content from classpath safely
            Resource resource = new ClassPathResource("work_sync.sql");

            try (InputStream inputStream = resource.getInputStream();
                 Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                
                scanner.useDelimiter("\\A");
                String sql = scanner.hasNext() ? scanner.next() : "";

                // Execute the SQL
                jdbcTemplate.execute(sql);
            }
        } catch (Exception e) {
            e.printStackTrace(); // or log with Logger
        }
    }*/
    
    @Transactional
	public boolean addERPLog(String id,String loginid, String query) {
		String  sqlQuery = "INSERT INTO `erp_logs`(`estid`, `updateby`, `query`) VALUES (?,?,?)";
		jdbcTemplate.update(sqlQuery,id,loginid,query);
		return true;
	}
   
    @Transactional
	public boolean updateWorksDataErpStatus(String id) {
		String  sqlQuery = "UPDATE `works_data_erp` SET `isuserupdate`=1 WHERE `id`=?";
		jdbcTemplate.update(sqlQuery,id);
		return true;
	}
    
    public int updateEstimateWorkDetails(
    		String loginid,
    		String estid,
    		String isiconic,
    		MultipartFile admin_sanction_file, 
    		MultipartFile loa_file, 
    		MultipartFile agreement_file,
    		MultipartFile work_order_file,
    		String techSanctionDate,
    		String adminSanctionDate,
    		String tenderCallDate,
    		String tenderFinalizedDate,
    		String loaDate,
    		String agreementDate,
    		String workOrderDate,
    		String workCommencedDate,
    		String projecttype,
    		String govtfund,
    		String specialcat
    		) {
        // Fetch the estimate details
        //List<Map<String, Object>> estimateDetails = getestimateworkdetailsById(estid);
/*
        if (estimateDetails.isEmpty()) {
            System.out.println("No records found for ID: " + estid);
            return 0;
        }
  */      
        try {
        // File Upload and save URL
        String admin_sanction_file_url = saveFile(admin_sanction_file, "admin_sanction", "erp");
        String loa_file_url = saveFile(loa_file, "loa", "erp");
        String agreement_file_url = saveFile(agreement_file, "agreement", "erp");
        String work_order_file_url = saveFile(work_order_file, "work_order", "erp");
        
        // Extract the first record
        //Map<String, Object> data = estimateDetails.get(0);
       
        // Call saveEstimate with the extracted values
        return updateEstimate(
        		estid,
        		isiconic,
	            techSanctionDate,
	            //String.valueOf(data.get("TECHNICAL_SANCTION_DATE")),
	            adminSanctionDate,
	            //String.valueOf(data.get("ADMIN_SANCTION_DATE")),
	            admin_sanction_file_url,
	            tenderCallDate,
	            //String.valueOf(data.get("TENDER_CALLED_DATE")),
	            tenderFinalizedDate,
	            //String.valueOf(data.get("TENDER_FINALIZED_DATE")),
	            loaDate,
	            //String.valueOf(data.get("LOA_DATE")),
	            loa_file_url,
	            agreementDate,
	            //String.valueOf(data.get("AGREEMENT_DATE")),
	            agreement_file_url,
	            workOrderDate, 
	            //String.valueOf(data.get("WORK_ORDER_DATE")),
	            work_order_file_url,
	            workCommencedDate,
	            //String.valueOf(data.get("WORK_COMMENCED_DATE")),
	            loginid,
	            projecttype,
	            govtfund,
	            specialcat
	        );
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
            System.out.println("Error uploading files: " + e.getMessage());
            return 0;
        }
    }
    
    private String normalizeDate(String date) {
        return (date == null || date.isBlank()) ? null : date;
    }
    
    public int updateEstimate(String estid,
            String iconicProject, String techSancDate, String adminSancDate,
            String admSanFile, String tenderCallDate, String tenderFinDate, String loaDate,
            String loaFile, String agreementDate, String agreementFile, String workOrderDate,
            String workOrderFile, String workCommDate, String loginid,
            String projecttype, String govtfund, String specialcat) {

			// Normalize blank date fields to null
	    	techSancDate = normalizeDate(techSancDate);
	    	agreementDate = normalizeDate(agreementDate);
	    	adminSancDate = normalizeDate(adminSancDate);
	    	tenderCallDate = normalizeDate(tenderCallDate);
	    	tenderFinDate = normalizeDate(tenderFinDate);
	    	loaDate = normalizeDate(loaDate);
	    	workOrderDate = normalizeDate(workOrderDate);
	    	workCommDate = normalizeDate(workCommDate);
			
			// Dynamically build file update portion of SQL
			StringBuilder fileQuery = new StringBuilder();
			List<Object> params = new ArrayList<>();
			
			// Base SQL
			String sql = "UPDATE `gcc_works_status`.`erp_works` SET "
			 + "technical_sanction_date=?, admin_sanction_date=?, tender_called_date=?, "
			 + "tender_finalized_date=?, loa_date=?, agreement_date=?, work_order_date=?, work_commenced_date=?, "
			 + "isiconic=?, project_type=?, govt_funded_project=?, special_category=?";
			
			// Add base parameters
			Collections.addAll(params, techSancDate, adminSancDate, tenderCallDate, tenderFinDate,
			loaDate, agreementDate, workOrderDate, workCommDate,
			iconicProject, projecttype, govtfund, specialcat);
			
			// Add file fields conditionally
			if (admSanFile != null && !admSanFile.isBlank()) {
			fileQuery.append(", admin_sanction_file=?");
			params.add(admSanFile);
			}
			if (loaFile != null && !loaFile.isBlank()) {
			fileQuery.append(", loa_file=?");
			params.add(loaFile);
			}
			if (agreementFile != null && !agreementFile.isBlank()) {
			fileQuery.append(", agreement_file=?");
			params.add(agreementFile);
			}
			if (workOrderFile != null && !workOrderFile.isBlank()) {
			fileQuery.append(", work_order_file=?");
			params.add(workOrderFile);
			}
			
			// Complete SQL and add estid
			sql += fileQuery.toString() + " WHERE estid=? LIMIT 1";
			params.add(estid);
			
			// Execute update
			int result = jdbcTemplate.update(sql, params.toArray());
			
			String logsql ="";
			// Log the update
			addERPLog(estid, loginid, logsql);
			
			return result;
	}
    
    public List<Map<String, Object>> getUserEstWorkDetailsById(String estid) {
        String sql = "SELECT ew.`estid`, ew.`estimate_no`, ew.`estimate_date`, ew.`zone`, ew.`ward`, "
        		+ "ew.`project_name`, ew.`location`, ew.`contractor_name`, ew.`contractor_period`, "
        		+ "fm.`name` AS `fund_source`, sm.`name` AS `scheme`, "
        		+ "ew.`estimation_amount`, ew.`est_val_with_oh`, ew.`technical_sanction_date`, ew.`admin_sanction_date`, "
        		+ " ew.`tender_called_date`, ew.`tender_finalized_date`, ew.`loa_date`, "
        		+ "ew.`agreement_date`, ew.`work_order_date`,  "
        		+ " CONCAT('" + fileBaseUrl + "/gcc/files', ew.`admin_sanction_file`) AS admin_sanction_file, "
        		+ " CONCAT('" + fileBaseUrl + "/gcc/files',  ew.`agreement_file`) AS agreement_file, "
        		+ " CONCAT('" + fileBaseUrl + "/gcc/files', ew.`loa_file`) AS loa_file, "
        		+ " CONCAT('" + fileBaseUrl + "/gcc/files', ew.`work_order_file`) AS work_order_file, "
        		+ "ew.`work_commenced_date`, ew.`update_status`, ew.`isiconic`, "
        		+ "ew.`works_data_erp_id`, ew.`percentage`, ew.`project_type`, ew.`govt_funded_project`, ew.`special_category`, "
        		+ "dm.dept_name as `department`, wm.name as `category`, "
        		+ "trim(tm.description) as `sub_category` "
        		+ "FROM erp_works ew "
        		+ "LEFT JOIN department_master dm ON dm.id = ew.department "
        		+ "LEFT JOIN workstype_master wm ON wm.id = ew.category "
        		+ "LEFT JOIN typeofwork_master tm ON  tm.id = ew.sub_category "
        		+ "LEFT JOIN scheme_master sm ON  sm.id = ew.scheme "
        		+ "LEFT JOIN fundsource_master fm ON  fm.id = ew.fund_source "
        		+ "WHERE ew.`estid`=? AND ew.`isactive`=1 AND ew.`isdelete`=0 LIMIT 1";
        return jdbcTemplate.queryForList(sql, estid);
    }
    
    public List<Map<String, Object>> getUserEstWorkStageDetailsByEstid(String estid) {
        String sql = "SELECT * FROM `erp_works_stages` WHERE `estid`=? AND `isactive`=1 AND `isdelete`=0";
        return jdbcTemplate.queryForList(sql, estid);
    }
    
    @Transactional
	public String getDepartmentId(String name) {
    	String id="0";
    	String sqlQuery ="SELECT `id` FROM `department_master` WHERE `dept_name`=?";
    	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, name);
    	if(!result.isEmpty()) {
    		id = ""+result.get(0).get("id");
    	}
    	return id;
    }
    
    @Transactional
	public String getCategoryId(String name) {
    	String id="0";
    	String sqlQuery ="SELECT `id` FROM `workstype_master` WHERE `name`=?";
    	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, name);
    	if(!result.isEmpty()) {
    		id = ""+result.get(0).get("id");
    	}
    	return id;
    }
    
    @Transactional
	public String getSubCategoryId(String name) {
    	String id="0";
    	String sqlQuery ="SELECT `id` FROM `typeofwork_master` WHERE `description`=?";
    	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, name);
    	if(!result.isEmpty()) {
    		id = ""+result.get(0).get("id");
    	}
    	return id;
    }
    
    @Transactional
	public String getFundSourceId(String name) {
    	String id="0";
    	String sqlQuery ="SELECT `id` FROM `fundsource_master` WHERE `name`=?";
    	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, name);
    	if(!result.isEmpty()) {
    		id = ""+result.get(0).get("id");
    	}
    	return id;
    }
    
    @Transactional
	public String getSchemeId(String name) {
    	String id="0";
    	String sqlQuery ="SELECT `id` FROM `scheme_master` WHERE `name`=?";
    	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, name);
    	if(!result.isEmpty()) {
    		id = ""+result.get(0).get("id");
    	}
    	return id;
    }
    
    public List<Map<String, Object>> getProjectList() {
        String sql = "SELECT id as pid, name  FROM `project_list` WHERE isactive=1 order by name asc";
        // Fetch the details from the database as a Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        return result; // Return the result map
    }
    
    public List<Map<String, Object>> getProjectUpdatedList(String loginid) {
        String sql = "SELECT "
        		+ "    pl.type, "
        		+ "    pl.department, "
        		+ "    pl.name, "
        		+ "    pfd.feedback AS status, "
        		+ "    pfd.file, "
        		+ "    pfd.cdate AS lastupdatedate "
        		+ "FROM "
        		+ "    project_feedback_data pfd "
        		+ "LEFT JOIN "
        		+ "    project_list pl ON pfd.pid = pl.id AND pl.isactive = 1 "
        		+ "INNER JOIN ( "
        		+ "    SELECT "
        		+ "        pid, MAX(cdate) AS max_cdate "
        		+ "    FROM "
        		+ "        project_feedback_data "
        		+ "    WHERE "
        		+ "        isactive = 1 AND inby = ? "
        		+ "    GROUP BY "
        		+ "        pid "
        		+ ") latest ON latest.pid = pfd.pid AND latest.max_cdate = pfd.cdate "
        		+ "WHERE "
        		+ "    pfd.isactive = 1 AND pfd.inby = ? ORDER BY pl.name";
        // Fetch the details from the database as a Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql,loginid, loginid);

        return result; // Return the result map
    } 
    
    // Method to process the form data
    public int saveProjectData(String pid, String feedback, String file, String inby) throws DataAccessException, IOException {

        // Prepare SQL Insert Query
        String sql = "INSERT INTO project_feedback_data (pid, feedback, file, inby) VALUES (?, ?, ?, ?)";

        return jdbcTemplate.update(sql, pid, feedback, file, inby);
    }
}
