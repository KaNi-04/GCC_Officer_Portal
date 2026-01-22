package in.gov.chennaicorporation.gccoffice.pensioner.service;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import java.util.Comparator;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@Service
public class NewEntryService {
	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    @Autowired
    private AppConfig appconfig;

    public NewEntryService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Autowired
	 public void setDataSource(@Qualifier("mysqlPensionerDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
    
    @Autowired
    public GetFileStatusService getFileStatus;
	
//  old retiment list getting method
    
//    public Map<String, Object> fetchEmplyeeDetails(String fromDate, String toDate, Integer deptId) {
//        Map<String, Object> response = new HashMap<>();
//
//        String apiUrl = appconfig.qaqcurl + "serviceId=common_api"
//                + "&jsonResp=Yes"
//                + "&subService=getEmpPenDetDuration"
//                + "&fromDate=" + fromDate
//                + "&toDate=" + toDate
//                + "&deptId=" + deptId;
//
//        System.out.println("url: " + apiUrl);
//        System.out.println("deptId:" + deptId);
//        try {
//            String rawResponse = restTemplate.getForObject(apiUrl, String.class);
//            System.out.println("Raw Response: " + rawResponse); // Debugging step
//
//            if (isValidJson(rawResponse)) {
//                JsonNode jsonResponse = objectMapper.readTree(rawResponse);
//
//                // Extract the "ListResult" array
//                JsonNode details = jsonResponse.path("ListResult");
//
//                if (details.isArray() && details.size() > 0) {
//                    List<Map<String, String>> list = new ArrayList<>();
//                    for (JsonNode detail : details) {
//                        Map<String, String> listResult = new HashMap<>();
//
//                        listResult.put("RETIREMENT_DATE", getNonNullValue(detail.path("RETIREMENT_DATE")));
//                        listResult.put("DEATH_DATE", getNonNullValue(detail.path("DEATH_DATE")));
//                        listResult.put("COMMUNITY_NAME", getNonNullValue(detail.path("COMMUNITY_NAME")));
//                        listResult.put("GROUP_TYPE", getNonNullValue(detail.path("GROUP_TYPE")));
//                        listResult.put("ID_DEPT", getNonNullValue(detail.path("ID_DEPT")));
//                        listResult.put("EMPLOYEE_CODE", getNonNullValue(detail.path("EMPLOYEE_CODE")));
//                        listResult.put("FUNCTIONARY", getNonNullValue(detail.path("FUNCTIONARY")));
//                        listResult.put("IS_HANDICAPPED", getNonNullValue(detail.path("IS_HANDICAPPED")));
//                        listResult.put("GENDER", getNonNullValue(detail.path("GENDER")));
//                        listResult.put("DATE_OF_BIRTH", getNonNullValue(detail.path("DATE_OF_BIRTH")));
//                        listResult.put("DATE_OF_APPOINTMENT", getNonNullValue(detail.path("DATE_OF_APPOINTMENT")));
//                        listResult.put("DESIGNATION", getNonNullValue(detail.path("DESIGNATION")));
//                        listResult.put("EMPLOYEE_NAME", getNonNullValue(detail.path("EMPLOYEE_NAME")));
//                        listResult.put("DEPARTMENT", getNonNullValue(detail.path("DEPARTMENT")));
//                        list.add(listResult);
//                    }
//                 // Sort the list by RETIREMENT_DATE in ascending order
//                    list.sort(Comparator.comparing(map -> parseDate(((Map<String, String>) map).get("RETIREMENT_DATE"))));
//                                                   
//                    response.put("list", list);
//
//                } else {
//                    response.put("list", Collections.emptyList());
//                }
//            } else {
//                System.err.println("Invalid JSON Response: " + rawResponse);
//                response.put("list", Collections.emptyList());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.put("list", Collections.emptyList());
//        }
//        return response;
//    }
    
    
    // new retirement list getting method with condition
    public Map<String, Object> fetchEmplyeeDetails(String fromDate, String toDate, Integer deptId) {
        Map<String, Object> response = new HashMap<>();

        String apiUrl = appconfig.qaqcurl + "serviceId=common_api"
                + "&jsonResp=Yes"
                + "&subService=getEmpPenDetDuration"
                + "&fromDate=" + fromDate
                + "&toDate=" + toDate
                + "&deptId=" + deptId;

        System.out.println("url: " + apiUrl);
        System.out.println("deptId:" + deptId);

        try {
            String rawResponse = restTemplate.getForObject(apiUrl, String.class);
            System.out.println("Raw Response: " + rawResponse); // Debugging step

            if (isValidJson(rawResponse)) {
                JsonNode jsonResponse = objectMapper.readTree(rawResponse);
                JsonNode details = jsonResponse.path("ListResult");

                if (details.isArray() && details.size() > 0) {
                    List<Map<String, String>> list = new ArrayList<>();
                    LocalDate currentDate = LocalDate.now(); // Get today's date

                    for (JsonNode detail : details) {
                        String retirementDateStr = getNonNullValue(detail.path("RETIREMENT_DATE"));
                        LocalDate retirementDate = parseDate(retirementDateStr);

                        // Only add to the list if RETIREMENT_DATE is today or in the future
                        if (retirementDate != null && !retirementDate.isBefore(currentDate)) {
                            Map<String, String> listResult = new HashMap<>();
                            listResult.put("RETIREMENT_DATE", retirementDateStr);
                            listResult.put("DEATH_DATE", getNonNullValue(detail.path("DEATH_DATE")));
                            listResult.put("COMMUNITY_NAME", getNonNullValue(detail.path("COMMUNITY_NAME")));
                            listResult.put("GROUP_TYPE", getNonNullValue(detail.path("GROUP_TYPE")));
                            listResult.put("ID_DEPT", getNonNullValue(detail.path("ID_DEPT")));
                            listResult.put("EMPLOYEE_CODE", getNonNullValue(detail.path("EMPLOYEE_CODE")));
                            listResult.put("FUNCTIONARY", getNonNullValue(detail.path("FUNCTIONARY")));
                            listResult.put("IS_HANDICAPPED", getNonNullValue(detail.path("IS_HANDICAPPED")));
                            listResult.put("GENDER", getNonNullValue(detail.path("GENDER")));
                            listResult.put("DATE_OF_BIRTH", getNonNullValue(detail.path("DATE_OF_BIRTH")));
                            listResult.put("DATE_OF_APPOINTMENT", getNonNullValue(detail.path("DATE_OF_APPOINTMENT")));
                            listResult.put("DESIGNATION", getNonNullValue(detail.path("DESIGNATION")));
                            listResult.put("EMPLOYEE_NAME", getNonNullValue(detail.path("EMPLOYEE_NAME")));
                            listResult.put("DEPARTMENT", getNonNullValue(detail.path("DEPARTMENT")));
                            list.add(listResult);
                        }
                    }

                    // Sort the list by RETIREMENT_DATE in ascending order
                    list.sort(Comparator.comparing(map -> parseDate(map.get("RETIREMENT_DATE"))));

                    response.put("list", list);
                } else {
                    response.put("list", Collections.emptyList());
                }
            } else {
                System.err.println("Invalid JSON Response: " + rawResponse);
                response.put("list", Collections.emptyList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("list", Collections.emptyList());
        }
        return response;
    }

    
    
    public Map<String, Object> fetchEmplyeeDetailsbyCode(String empCode) {
        Map<String, Object> response = new HashMap<>();

        String apiUrl = appconfig.qaqcurl + "serviceId=common_api"
                + "&jsonResp=Yes"
                + "&subService=getEmpWorkDet"
                + "&empCode=" + empCode;

        System.out.println("url: " + apiUrl);
        System.out.println("empCode:" + empCode);
        try {
            String rawResponse = restTemplate.getForObject(apiUrl, String.class);
            System.out.println("Raw Response: " + rawResponse); // Debugging step

            if (isValidJson(rawResponse)) {
                JsonNode jsonResponse = objectMapper.readTree(rawResponse);

                // Extract the "ListResult" array
                JsonNode details = jsonResponse.path("ListResult");

                if (details.isArray() && details.size() > 0) {
                    List<Map<String, String>> list = new ArrayList<>();
                    for (JsonNode detail : details) {
                        Map<String, String> listResult = new HashMap<>();
                        listResult.put("RETIREMENT_DATE", formatToISODate(getNonNullValue(detail.path("RETIREMENT_DATE"))));
                        listResult.put("TO_DATE", parseDate(getNonNullValue(detail.path("TO_DATE"))).toString());
                        listResult.put("DEATH_DATE", getNonNullValue(detail.path("DEATH_DATE")));
                        listResult.put("COMMUNITY_NAME", getNonNullValue(detail.path("COMMUNITY_NAME")));
                        listResult.put("GROUP_TYPE", getNonNullValue(detail.path("GROUP_TYPE")));
                        listResult.put("ID_DEPT", getNonNullValue(detail.path("ID_DEPT")));
                        listResult.put("EMPLOYEE_CODE", getNonNullValue(detail.path("EMPLOYEE_CODE")));
                        listResult.put("FUNCTIONARY", getNonNullValue(detail.path("FUNCTIONARY")));
                        listResult.put("IS_HANDICAPPED", getNonNullValue(detail.path("IS_HANDICAPPED")));
                        listResult.put("GENDER", getNonNullValue(detail.path("GENDER")));
                        listResult.put("DATE_OF_BIRTH", getNonNullValue(detail.path("DATE_OF_BIRTH")));
                        listResult.put("DATE_OF_APPOINTMENT", getNonNullValue(detail.path("DATE_OF_APPOINTMENT")));
                        listResult.put("DESIGNATION", getNonNullValue(detail.path("DESIGNATION")));
                        listResult.put("EMPLOYEE_NAME", getNonNullValue(detail.path("EMPLOYEE_NAME")));
                        listResult.put("DEPARTMENT", getNonNullValue(detail.path("DEPARTMENT")));
                        list.add(listResult);
                    }
                 // Sort the list by RETIREMENT_DATE in ascending order
                    list.sort(Comparator.comparing(map -> parseDate(((Map<String, String>) map).get("RETIREMENT_DATE"))));
                    response.put("list", list);
                    System.out.println("listresult: "+list);

                } else {
                    response.put("list", Collections.emptyList());
                }
            } else {
                System.err.println("Invalid JSON Response: " + rawResponse);
                response.put("list", Collections.emptyList());
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("list", Collections.emptyList());
        }
        return response;
    }
    
    
 // Utility method to convert DD-MM-YYYY to YYYY-MM-DD
    private String formatToISODate(String date) {
        if (date == null || date.isEmpty()) {
            return "";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            System.err.println("Date parsing error: " + e.getMessage());
            return date; // Return original date if parsing fails
        }
    }
    
    
    //getting deptId from login 
	public Integer getloginuserDeptId(int userId) {
	    String query = "select emp_dept_id from login_details where gcc_login_id = ?";
	    try {
	        // Use queryForObject to return a single value
	        return jdbcTemplate.queryForObject(query, Integer.class, userId);
	    } catch (EmptyResultDataAccessException e) {
	        // Log the issue and handle the case when no result is found
	        System.out.println("No department found for user ID: " + userId);
	        return null; // Or return a default value if required
	    }
	}

	
	private LocalDate parseDate(String date) {
	    if (date == null || date.isEmpty()) {
	        return LocalDate.MIN; // Use a minimum value for invalid or empty dates
	    }
	    try {
	        // Remove time part if present
	        if (date.contains(" ")) {
	            date = date.split(" ")[0]; // Extract only the date part before space
	        }

	        DateTimeFormatter formatter;
	        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) { // yyyy-MM-dd
	            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        } else if (date.matches("\\d{2}-\\d{2}-\\d{4}")) { // dd-MM-yyyy
	            formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	        } else {
	            System.err.println("Unknown date format: " + date);
	            return LocalDate.MIN;
	        }
	        return LocalDate.parse(date, formatter);
	    } catch (DateTimeParseException e) {
	        e.printStackTrace();
	        return LocalDate.MIN; // Fallback for invalid dates
	    }
	}
    
    
//    private LocalDate parseDate(String date) {
//        if (date == null || date.isEmpty()) {
//            return LocalDate.MIN; // Use a minimum value for invalid or empty dates
//        }
//        try {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Adjust format as needed
//            return LocalDate.parse(date, formatter);
//        } catch (DateTimeParseException e) {
//            e.printStackTrace();
//            return LocalDate.MIN; // Fallback for invalid dates
//        }
//    }

    
    private boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
	
    
    private String getNonNullValue(JsonNode node) {
        //String value = node.isMissingNode() || node.isNull() || node.asText().isEmpty() ? "N/A" : node.asText();
        String value = node.isMissingNode() || node.isNull() || node.asText().isEmpty() || node.asText().isBlank() ? "N/A" : node.asText();
        //2024-565EAT
        return "null".equals(value) ? "N/A" : value; // Check for string "null"
    }

    public String getDeptNameById(Integer deptId) {
        String query = "SELECT dept_name FROM department_master WHERE dept_id=? AND isactive=1 AND isdelete=0";

        try {
            // Query execution and returning the department name
            //return jdbcTemplate.queryForObject(query, new Object[]{deptId}, String.class);
            
            return jdbcTemplate.queryForObject(query, String.class,deptId);
        } catch (Exception e) {
            // Handle exception if deptId is not found or query fails
            e.printStackTrace();
            return null;
        }
    }
    
    

    @Transactional
        public boolean saveRetirementEmployeeDetails(Map<String, Object> requestData) {
            try {
            	
            	String empNo = (String) requestData.get("empNo");
                String empName = (String) requestData.get("empName");
                String deptName = (String) requestData.get("deptName");
                String designation = (String) requestData.get("designation");
                String typeOfRetirement = (String) requestData.get("typeOfRetirement");
                String retirementdate = (String) requestData.get("retirementdate");
                String retirementclass = (String) requestData.get("retirementclass");
                String dateToAudit = (String) requestData.get("dateToAudit");
                String filemovedby =(String)requestData.get("filemovedby");
                String remarks = (String) requestData.get("remarks");
                String dept_Id = (String) requestData.get("deptId");
                String file_category = (String) requestData.get("filecategory");
                
                // Safely parse integer values
                int deptId;
                int filecategory;
                try {
                    deptId = Integer.parseInt(dept_Id);
                    filecategory = Integer.parseInt(file_category);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format for deptId or filecategory.");
                    return false;
                }
                
                int filestatus = getFileStatus.getFileStatus1Id();
            	if (filestatus == 0) {
                    System.out.println("File status not found. Aborting insertion.");
                    return false;
                }
            	
            	// Execute a query to fetch the required String based on filecategory
                String additionalDataQuery = "SELECT file_name FROM file_category_master WHERE id = ?";
                String fc_name = jdbcTemplate.queryForObject(additionalDataQuery,String.class,filecategory);
            	System.out.println("fc_name==="+fc_name);
            	
            	
            	String fileentrytype ="NEW";
            	
            	System.out.println("filemovedby====="+filemovedby); 
            	
            	 // Generate the variable (PR + current date + current time)
                String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
                String temp_id = "PR" + currentTimestamp;
            	
                String query = "INSERT INTO pensioner_details (emp_no, emp_name, dept_name, designation, type_of_retirement,retirement_date ,retirement_class,file_moved_date, remarks,file_category,file_status,file_entry_type,dept_id,file_category_name,temp_id) VALUES (?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?)";
                int rowsInserted = jdbcTemplate.update(query, empNo, empName, deptName, designation, typeOfRetirement,retirementdate ,retirementclass,dateToAudit, remarks,filecategory,filestatus,fileentrytype,deptId,fc_name,temp_id);
                
                String query1 = "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, type_of_retirement,retirement_date ,retirement_class,file_moved_date, remarks,file_category,file_status,file_entry_type,file_moved_by,dept_id,file_category_name,temp_id) VALUES (?,?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?)";
                int rowsInsertedHistory = jdbcTemplate.update(query1, empNo, empName, deptName, designation, typeOfRetirement,retirementdate ,retirementclass,dateToAudit, remarks,filecategory,filestatus,fileentrytype,filemovedby,deptId,fc_name,temp_id);
                
                String query2 = "INSERT INTO file_movement_history (emp_no,file_moved_date,file_status,file_moved_by,remarks,temp_id) VALUES (?, ?, ?, ?, ?,?)";
                int rowsInsertedFileMovement = jdbcTemplate.update(query2, empNo,dateToAudit,filestatus,filemovedby,remarks,temp_id);
                
                
                return rowsInserted > 0 && rowsInsertedHistory > 0 && rowsInsertedFileMovement > 0;
                
            } catch (Exception e) {
                e.printStackTrace();
                return false; // Return false if an exception occurs
            }
        }

    
//    public List<String> getRegisteredEmps(Integer deptId) {
//        String query = "SELECT DISTINCT emp_no FROM pensioner_details WHERE dept_id=? AND file_category <> 5 " +
//                       "UNION ALL " +
//                       "SELECT DISTINCT emp_no FROM cps_pensioner_details WHERE dept_id=?";
//        
//        List<String> emps = jdbcTemplate.queryForList(query, String.class, deptId, deptId);
//        System.out.println("emps====" + emps);
//
//        return emps; 
//    }
    
    
    public List<String> getRegisteredEmps(Integer deptId) {
        String query = "SELECT DISTINCT emp_no FROM pensioner_details " +
                       "WHERE dept_id=? AND (file_category <> 5 OR  (file_category = 5 AND file_status != 11)) " +
                       "UNION ALL " +
                       "SELECT DISTINCT emp_no FROM cps_pensioner_details WHERE dept_id=?";
        
        List<String> emps = jdbcTemplate.queryForList(query, String.class, deptId, deptId);
        System.out.println("emps====" + emps);

        return emps; 
    }

	
	public Integer getRetirementFileCategoryId() {
	    String query = "SELECT id FROM file_category_master WHERE file_name = 'Retirement'";
	    try {
	        return jdbcTemplate.queryForObject(query, Integer.class);
	    } catch (EmptyResultDataAccessException e) {
	        return null; // or handle as needed
	    }
	}
	
	public Integer getFamilyPensionFileCategoryId() {
	    String query = "SELECT id FROM file_category_master WHERE file_name = 'Family Pension'";
	    try {
	        return jdbcTemplate.queryForObject(query, Integer.class);
	    } catch (EmptyResultDataAccessException e) {
	        return null; // or handle as needed
	    }
	}
	
	public Integer getPendencyFileCategoryId() {
	    String query = "SELECT id FROM file_category_master WHERE file_name = 'Pendency'";
	    try {
	        return jdbcTemplate.queryForObject(query, Integer.class);
	    } catch (EmptyResultDataAccessException e) {
	        return null; // or handle as needed
	    }
	}
	
	public Integer getGISFileCategoryId() {
	    String query = "SELECT id FROM file_category_master WHERE file_name = 'GIS'";
	    try {
	        return jdbcTemplate.queryForObject(query, Integer.class);
	    } catch (EmptyResultDataAccessException e) {
	        return null; // or handle as needed
	    }
	}
	
	public Integer getPRFileCategoryId() {
	    String query = "SELECT id FROM file_category_master WHERE file_name = 'Provisional Pension'";
	    try {
	        return jdbcTemplate.queryForObject(query, Integer.class);
	    } catch (EmptyResultDataAccessException e) {
	        return null; // or handle as needed
	    }
	}
	
	public Integer getCPSFileCategoryId() {
	    String query = "SELECT id FROM file_category_master WHERE file_name = 'Contributory Pension Scheme'";
	    try {
	        return jdbcTemplate.queryForObject(query, Integer.class);
	    } catch (EmptyResultDataAccessException e) {
	        return null; // or handle as needed
	    }
	}
	
	
	@Transactional
	public boolean saveFamilyPensionDetails(Map<String, Object> requestData) {
        try {
        	
        	// Extract data from the request body
            String empNo = (String) requestData.get("empNo");
            String empName = (String) requestData.get("empName");
            String deptName = (String) requestData.get("deptName");
            String designation = (String) requestData.get("designation");
            String sanctionedgis = (String) requestData.get("sanctionedgis");
            String serviceDeath =(String) requestData.get("serviceDeath");
            String deceased =(String) requestData.get("deceased");
            String dateToAudit = (String) requestData.get("dateToAudit");
            String remarks = (String) requestData.get("remarks");
            String filemovedby =(String)requestData.get("filemovedby");
            
            String pendingDuration = (String) requestData.get("pendingDuration");
	        String reasonPending = (("yes".equals(pendingDuration) || "no".equals(pendingDuration)) 
	                                 && requestData.get("reasonPending") != null) ? requestData.get("reasonPending").toString() : null;
	        
            String dept_Id = (String) requestData.get("deptId");                    
            int deptId = Integer.parseInt(dept_Id);
        	
            String file_category = (String) requestData.get("filecategory");
            int filecategory = Integer.parseInt(file_category);
            	        	
        	// Execute a query to fetch the required String based on filecategory
            String additionalDataQuery = "SELECT file_name FROM file_category_master WHERE id = ?";
            String fc_name = jdbcTemplate.queryForObject(additionalDataQuery, String.class,filecategory);
        	System.out.println("fc_name==="+fc_name);
        	
        	
        	int filestatus = getFileStatus.getFileStatus1Id();
        	if (filestatus == 0) {
                System.out.println("File status not found. Aborting insertion.");
                return false;
            }
        	String fileentrytype ="NEW";
        	int rowsInserted=0;
        	
        	// Generate the variable (PR + current date + current time)
            String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
            String temp_id = "PF" + currentTimestamp;
        	
            String query = "INSERT INTO pensioner_details (emp_no, emp_name, dept_name, designation, sanctioned_gis_amount,service_death,relationship_deceased,file_moved_date, remarks,dept_id,file_category,file_status,file_entry_type,file_category_name,temp_id,pending_duration,reason_for_pending) VALUES (?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)";
             rowsInserted = jdbcTemplate.update(query, empNo, empName, deptName, designation, sanctionedgis,serviceDeath ,deceased,dateToAudit, remarks,deptId,filecategory,filestatus,fileentrytype,fc_name,temp_id, pendingDuration,reasonPending);
            
            String query1 = "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, sanctioned_gis_amount,service_death,relationship_deceased,file_moved_date, remarks,dept_id,file_category,file_status,file_entry_type,file_moved_by,file_category_name,temp_id,pending_duration,reason_for_pending) VALUES (?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?)";
            rowsInserted = jdbcTemplate.update(query1, empNo, empName, deptName, designation, sanctionedgis,serviceDeath ,deceased,dateToAudit, remarks,deptId,filecategory,filestatus,fileentrytype,filemovedby,fc_name,temp_id, pendingDuration,reasonPending);
            
            String query2 = "INSERT INTO file_movement_history (emp_no,file_moved_date,file_status,file_moved_by,remarks,temp_id) VALUES (?, ?, ?, ?, ?,?)";
            rowsInserted = jdbcTemplate.update(query2, empNo,dateToAudit,filestatus,filemovedby,remarks,temp_id);
           
            
            return rowsInserted > 0;
            		
            
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }

	 
	///gis saving method service
	@Transactional
	 public boolean savenewEntryDetailsGis(Map<String, Object> requestData) {
	         try {
	         	
	         	
	              String empNo = (String) requestData.get("empNo");
	              String empName = (String) requestData.get("empName");
	              String deptName = (String) requestData.get("deptName");
	              String designation = (String) requestData.get("designation");           
	              String dateToPension = (String) requestData.get("dateToPension");
	              String filemovedby =(String)requestData.get("filemovedby");
	              String remarks = (String) requestData.get("remarks");
	              String serviceDeath =(String) requestData.get("serviceDeath");
	              String deceased =(String) requestData.get("deceased");
	              String gisentry = requestData.get("gisentry") != null ?(String) requestData.get("gisentry") : null;
	              String gisAdvanceamount =requestData.get("gisAdvanceamount") != null ?(String) requestData.get("gisAdvanceamount") : null;
	              String gisAdvance =  requestData.get("gisAdvance") != null ? (String) requestData.get("gisAdvance") : null;
	              String dept_Id = (String) requestData.get("deptId");            
	              int deptId = Integer.parseInt(dept_Id);
	 	        	
	              	String file_category = (String) requestData.get("filecategory");
		            int filecategory = Integer.parseInt(file_category);
	 	        	// Execute a query to fetch the required String based on filecategory
	 	            String additionalDataQuery = "SELECT file_name FROM file_category_master WHERE id = ?";
	 	            String fc_name = jdbcTemplate.queryForObject(additionalDataQuery, String.class,filecategory);
	 	        	System.out.println("fc_name==="+fc_name);
	 	        	
	 	        	int filestatus = getFileStatus.getFileStatus7Id();
	 	        	if (filestatus == 0) {
	                    System.out.println("File status not found. Aborting insertion.");
	                    return false;
	                }
	 	        	String fileentrytype ="NEW";
	         	
	         	System.out.println("filemovedby====="+filemovedby);
	         	
	         // Generate the variable (PR + current date + current time)
	            String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
	            String temp_id = "PG" + currentTimestamp;
	         	
	             String query = "INSERT INTO pensioner_details (emp_no, emp_name, dept_name, designation,file_moved_date, remarks,service_death,relationship_deceased,gis_entry,gis_advance_amount,gis_advance ,file_category,file_status,file_entry_type,dept_id,file_category_name,temp_id) VALUES (?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)";
	             int rowsInserted = jdbcTemplate.update(query, empNo, empName, deptName, designation, dateToPension, remarks,serviceDeath,deceased,gisentry,gisAdvanceamount,gisAdvance,filecategory,filestatus,fileentrytype,deptId,fc_name,temp_id);
	             
	             String query1 = "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation,file_moved_date, remarks,service_death,relationship_deceased,gis_entry,gis_advance_amount,gis_advance ,file_category,file_status,file_entry_type,file_moved_by,dept_id,file_category_name,temp_id) VALUES (?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?)";
	             int rowsInsertedHistory = jdbcTemplate.update(query1, empNo, empName, deptName, designation,dateToPension, remarks,serviceDeath,deceased,gisentry,gisAdvanceamount,gisAdvance,filecategory,filestatus,fileentrytype,filemovedby,deptId,fc_name,temp_id);
	             
	             String query2 = "INSERT INTO file_movement_history (emp_no,file_moved_date,file_status,file_moved_by,remarks,temp_id) VALUES (?, ?, ?, ?, ?,?)";
	             int rowsInsertedFileMovement = jdbcTemplate.update(query2, empNo,dateToPension,filestatus,filemovedby,remarks,temp_id);
//	             
	             
	             return rowsInserted > 0 && rowsInsertedHistory > 0 && rowsInsertedFileMovement > 0;
	             
	         } catch (Exception e) {
	             e.printStackTrace();
	             return false; // Return false if an exception occurs
	         }
	     }
	 
	 

	//provisional retirement method service	
	@Transactional
	   public boolean savenewEntryDetailsProvisionalretirement(Map<String, Object> requestData) {
	        try {
	        	
	             String empNo = (String) requestData.get("empNo");
	             String empName = (String) requestData.get("empName");
	             String deptName = (String) requestData.get("deptName");
	             String designation = (String) requestData.get("designation");
	             String typeOfRetirement = (String) requestData.get("typeOfRetirement");
	            
	             String ReasonofPension = (String) requestData.get("ReasonofPension");
	             String retirementClass = (String) requestData.get("retirementClass");
	             String retirementDate = (String) requestData.get("retirementDate");
	             String pendingDuration = (String) requestData.get("pendingDuration");
	             String reasonofPending = (String) requestData.get("reasonofPending");
	             String dp = (String) requestData.get("dp");
	             String otherReason = (String) requestData.get("otherReason");
	             String dateToAudit = (String) requestData.get("dateToAudit");
	             String filemovedby =(String)requestData.get("filemovedby");
	             String remarks = (String) requestData.get("remarks");
	             String dept_Id = (String) requestData.get("deptId");            
	             int deptId = Integer.parseInt(dept_Id);
		        	
	             String file_category = (String) requestData.get("filecategory");
		            int filecategory = Integer.parseInt(file_category);
		        	// Execute a query to fetch the required String based on filecategory
		            String additionalDataQuery = "SELECT file_name FROM file_category_master WHERE id = ?";
		            String fc_name = jdbcTemplate.queryForObject(additionalDataQuery, String.class,filecategory);
		        	System.out.println("fc_name==="+fc_name);
		        	
		        	int filestatus = getFileStatus.getFileStatus7Id();
		        	if (filestatus == 0) {
	                    System.out.println("File status not found. Aborting insertion.");
	                    return false;
	                }
		        	String fileentrytype ="NEW";
	        	
	        	System.out.println("filemovedby====="+filemovedby);
	        	
	        	// Generate the variable (PR + current date + current time)
	            String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
	            String temp_id = "PPR" + currentTimestamp;
	        	        	
	            String query = "INSERT INTO pensioner_details (emp_no, emp_name, dept_name, designation,type_of_retirement,retirement_class,retirement_date , reason_for_provisional_pension,file_moved_date, pending_duration,reason_for_pending,dp,remarks,file_category,file_status,file_entry_type,dept_id,file_category_name,others_provisional_pension,temp_id) VALUES (?,?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?)";
	            int rowsInserted = jdbcTemplate.update(query, empNo, empName, deptName, designation, typeOfRetirement,retirementClass ,retirementDate , ReasonofPension,dateToAudit,pendingDuration,reasonofPending,dp,remarks,filecategory,filestatus,fileentrytype,deptId,fc_name,otherReason,temp_id);
	            
	            String query1 = "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation,type_of_retirement,retirement_class, retirement_date,reason_for_provisional_pension ,file_moved_date, remarks,pending_duration,reason_for_pending,dp ,file_category,file_status,file_entry_type,file_moved_by,dept_id,file_category_name,others_provisional_pension,temp_id) VALUES (?,?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?)";
	            int rowsInsertedHistory = jdbcTemplate.update(query1, empNo, empName, deptName, designation,typeOfRetirement,retirementClass,retirementDate ,ReasonofPension,dateToAudit,remarks,pendingDuration,reasonofPending,dp,filecategory,filestatus,fileentrytype,filemovedby,deptId,fc_name,otherReason,temp_id);
	            
	            String query2 = "INSERT INTO file_movement_history (emp_no,file_moved_date,file_status,file_moved_by,remarks,temp_id) VALUES (?, ?, ?, ?, ?,?)";
	            int rowsInsertedFileMovement = jdbcTemplate.update(query2, empNo,dateToAudit,filestatus,filemovedby,remarks,temp_id);
//	            
	            
	            return rowsInserted > 0 && rowsInsertedHistory > 0 && rowsInsertedFileMovement > 0;
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false; // Return false if an exception occurs
	        }
	    }
    
	 //cps saving method:
	  @Transactional
	    public boolean savenewEntryDetailspensionscheme(Map<String, Object> requestData) {
		    try {

		        // Safely extract input values from requestData
		        String empNo = requestData.get("empNo") != null ? requestData.get("empNo").toString() : null;
		        String empName = requestData.get("empName") != null ? requestData.get("empName").toString() : null;
		        String deptName = requestData.get("deptName") != null ? requestData.get("deptName").toString() : null;
		        String designation = requestData.get("designation") != null ? requestData.get("designation").toString() : null;
		        String classes = requestData.get("classes") != null ? requestData.get("classes").toString() : null;
		        String fileMovedBy = requestData.get("fileMovedBy") != null ? requestData.get("fileMovedBy").toString() : null;
		        String remarks = requestData.get("remarks") != null ? requestData.get("remarks").toString() : null;
		        String retirementType = requestData.get("retirementType") != null ? requestData.get("retirementType").toString() : null;
		        String retirementdate = requestData.get("retirementdate") !=null ? requestData.get("retirementdate").toString() : null;

		        String uac = (String) requestData.get("uac");
		        String uacDate = "yes".equals(uac) && requestData.get("uacDate") != null ? requestData.get("uacDate").toString() : null;

		        String pendingDuration = (String) requestData.get("pendingDuration");
		        String reasonPending = "Yes".equals(pendingDuration) && requestData.get("reasonPending") != null ? requestData.get("reasonPending").toString() : null;
		        
		        String cps =(String) requestData.get("cps");
		        String cpsAmount = "yes".equals(cps) && requestData.get("cpsAmount") != null ? requestData.get("cpsAmount").toString() : null;

		        String elOrLpa = (String)requestData.get("elOrLpa");
		        String elOrLpaAmount = "yes".equals(elOrLpa) && requestData.get("elOrLpaAmount") != null ? requestData.get("elOrLpaAmount").toString() : null;

		        String spfgs = (String) requestData.get("spfgs");
		        String spfgsAmount = "yes".equals(spfgs) && requestData.get("spfgsAmount") != null ? requestData.get("spfgsAmount").toString() : null;

		        int deptId = requestData.get("deptId") != null ? Integer.parseInt(requestData.get("deptId").toString()) : 0;

		        // Default static values
		        String file_category = (String) requestData.get("filecategory");
	            int filecategory = Integer.parseInt(file_category);
		        int fileStatus = getFileStatus.getFileStatus11Id();
		        if (fileStatus == 0) {
                    System.out.println("File status not found. Aborting insertion.");
                    return false;
                }
		        String fileEntryType = "FILE_COMPLETED";

		        // Fetch file_category_name dynamically
		        String fileCategoryQuery = "SELECT file_name FROM file_category_master WHERE id = ?";
		        String fileCategoryName = jdbcTemplate.queryForObject(fileCategoryQuery, String.class,filecategory);
		        
		     // Generate the variable (PR + current date + current time)
	            String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
	            String temp_id = "PC" + currentTimestamp;

		        // Insert into cps_pensioner_details
		        String queryDetails = "INSERT INTO cps_pensioner_details " +
		                "(emp_no, emp_name, dept_name, designation, file_category, retirement_class, retirement_date,type_of_retirement, uac, uac_date, " +
		                "pending_duration,reason_for_pending ,cps, cps_amount, el_or_lpa, el_or_lpa_amount, spfgs, spfgs_amount, remarks, " +
		                "file_status, file_entry_type, dept_id, file_category_name,temp_id) " +
		                "VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
		        int rowsInsertedDetails = jdbcTemplate.update(queryDetails,
		                empNo, empName, deptName, designation, filecategory, classes, retirementdate,retirementType, uac, uacDate,
		                pendingDuration,reasonPending ,cps, cpsAmount, elOrLpa, elOrLpaAmount, spfgs, spfgsAmount, remarks,
		                fileStatus, fileEntryType, deptId, fileCategoryName,temp_id);

		        // Insert into cps_pensioner_details_history
		        String queryHistory = "INSERT INTO cps_pensioner_details_history " +
		                "(emp_no, emp_name, dept_name, designation, file_category, retirement_class, retirement_date,type_of_retirement, uac, uac_date, " +
		                "pending_duration,reason_for_pending ,cps, cps_amount, el_or_lpa, el_or_lpa_amount, spfgs, spfgs_amount, remarks, " +
		                "file_status, file_entry_type, file_moved_by, dept_id, file_category_name,temp_id) " +
		                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
		        int rowsInsertedHistory = jdbcTemplate.update(queryHistory,
		                empNo, empName, deptName, designation, filecategory, classes, retirementdate,retirementType, uac, uacDate,
		                pendingDuration, reasonPending,cps, cpsAmount, elOrLpa, elOrLpaAmount, spfgs, spfgsAmount, remarks,
		                fileStatus, fileEntryType, fileMovedBy, deptId, fileCategoryName,temp_id);

		    	 // Current date and time
		        LocalDateTime fileMovedDate = LocalDateTime.now();
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		        String formattedFileMovedDate = fileMovedDate.format(formatter);
		       
		        // Insert into file_movement_history
		        String queryFileMovement = "INSERT INTO file_movement_history (emp_no, file_status,file_moved_date, file_moved_by, remarks,temp_id) " +
		                "VALUES (?, ?, ?, ?, ?,?)";
		        int rowsInsertedFileMovement = jdbcTemplate.update(queryFileMovement, empNo,  fileStatus, formattedFileMovedDate, fileMovedBy, remarks,temp_id);

		        // Return true if all inserts were successful
		        return rowsInsertedDetails > 0 && rowsInsertedHistory > 0 && rowsInsertedFileMovement > 0;

		    } catch (Exception e) {
		        e.printStackTrace();
		        return false; // Return false if an exception occurs
		    }
		}
	    
	    
	    //pendency saving method
	  @Transactional
	    public boolean savePendencyDetails(Map<String, Object> requestData) {
	        try {
	        	
	        	String empNo = (String) requestData.get("empNo");
	            String empName = (String) requestData.get("empName");
	            String deptName = (String) requestData.get("deptName");
	            String designation = (String) requestData.get("designation");
	            String retirementclass = (String) requestData.get("retirementclass");
	            String typeOfRetirement = (String) requestData.get("typeOfRetirement");
	            String retirementDate = (String) requestData.get("retirementDate");
	            //String pendencyCategory=(String)requestData.get("pendencyCategory");
	            String pendencyCategory="Retirement";
	           
	            String dateToAudit = (String) requestData.get("dateToAudit");
	            String remarks = (String) requestData.get("remarks");
	            String filemovedby =(String)requestData.get("filemovedby");
	            
	            String pendingDuration = (String) requestData.get("pendingDuration");
		       		        
		        String reasonPending =requestData.get("reasonPending") != null ? requestData.get("reasonPending").toString() : null;
		       
	            String dept_Id = (String) requestData.get("deptId");            
	            System.out.println("dept_Id = "+dept_Id);
	            int deptId = Integer.parseInt(dept_Id);
	        		        	
	        	String file_category = (String) requestData.get("filecategory");
	            int filecategory = Integer.parseInt(file_category);
	            int filestatus = getFileStatus.getFileStatus1Id();
	            if (filestatus == 0) {
                    System.out.println("File status not found. Aborting insertion.");
                    return false;
                }
	        	String fileentrytype ="NEW";
	        	int rowsInserted = 0;
	        	
	        	// Generate the variable (PR + current date + current time)
	            String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
	            String temp_id = "PP" + currentTimestamp;
	        	
	        	
	            String query = "INSERT INTO pensioner_details (emp_no, emp_name, dept_name, designation, retirement_class,retirement_date,pending_duration,reason_for_pending,file_moved_date,dept_id,remarks,file_category,file_status,file_entry_type,file_category_name,type_of_retirement,temp_id) VALUES (?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)";
	             rowsInserted = jdbcTemplate.update(query, empNo, empName, deptName, designation, retirementclass,retirementDate,pendingDuration,reasonPending,dateToAudit, deptId,remarks,filecategory,filestatus,fileentrytype,pendencyCategory,typeOfRetirement,temp_id);
	             
	             String query1 = "INSERT INTO pensioner_details_history (emp_no, emp_name, dept_name, designation, retirement_class,retirement_date,pending_duration,reason_for_pending,file_moved_date,dept_id,remarks,file_category,file_status,file_entry_type,file_category_name,type_of_retirement,file_moved_by,temp_id) VALUES (?,?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?)";
	             rowsInserted = jdbcTemplate.update(query1, empNo, empName, deptName, designation, retirementclass,retirementDate,pendingDuration,reasonPending,dateToAudit, deptId,remarks,filecategory,filestatus,fileentrytype,pendencyCategory,typeOfRetirement,filemovedby,temp_id);
	             
	             String query2 = "INSERT INTO file_movement_history (emp_no,file_moved_date,file_status,file_moved_by,remarks,temp_id) VALUES (?,?, ?, ?, ?, ?)";
	            rowsInserted = jdbcTemplate.update(query2, empNo,dateToAudit,filestatus,filemovedby,remarks,temp_id);
	          
      
	            return rowsInserted > 0;

	           
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false; // Return false if an exception occurs
	        }
	    }
	    
	  public boolean doesEmployeeExistRPR(String empCode) {
	        String sql = "SELECT COUNT(*) FROM ( " +
	                     "  SELECT emp_no FROM pensioner_details WHERE emp_no = ? AND file_category IN (1,2,3,4,5) " +
	                     "  UNION ALL " +
	                     "  SELECT emp_no FROM cps_pensioner_details WHERE emp_no = ? AND file_category IN (6) " +
	                     ") AS combined";
	        // Note: Pass empCode twice since there are two placeholders.
	        Integer count = jdbcTemplate.queryForObject(sql,  Integer.class,empCode,empCode);
	        return count != null && count > 0;
	    }
	    
	    
//	    public boolean doesEmployeeExist(String empCode) {
//	    	
//	        String sql = "SELECT COUNT(*) FROM ( " +
//	                     "  SELECT emp_no  FROM pensioner_details WHERE emp_no = ? AND file_category IN (1,3,5) "+
//	                    		 "  UNION ALL " +
//	    	                     "  SELECT emp_no FROM cps_pensioner_details WHERE emp_no = ? AND file_category IN (6) " +
//	    	                     ") AS combined";
//	        // Note: Pass empCode twice since there are two placeholders.
//	        Integer count = jdbcTemplate.queryForObject(sql,Integer.class,empCode,empCode);
//	        System.out.println("count<<<<<<<<<<<"+count);
//	        return count != null && count > 0;
//	    }
//	    
//	    
//	    public boolean doesEmployeeExistgis(String empCode) {
//	        String sql = "SELECT COUNT(*) FROM ( " +
//	                     "  SELECT emp_no FROM pensioner_details WHERE emp_no = ? AND file_category IN (1,2,3,5,6) " +
//	                     "  UNION ALL " +
//	                     "  SELECT emp_no FROM cps_pensioner_details WHERE emp_no = ? AND file_category IN (6) " +
//	                     ") AS combined";
//	        // Note: Pass empCode twice since there are two placeholders.
//	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,empCode,empCode);
//	        return count != null && count > 0;
//	    }
//	    
//	    
//        public boolean doesEmployeeExistfp(String empCode) {
//	    	
//	        String sql = "SELECT COUNT(*) FROM ( " +
//	        
//	                     "  SELECT emp_no FROM pensioner_details WHERE emp_no = ? AND file_category IN (1,2,3,5) AND is_closed IN (0)" +
//	                     "  UNION ALL " +
//	                     
//	                     "  SELECT emp_no FROM cps_pensioner_details WHERE emp_no = ? AND file_category IN (6) " +
//	                     ") AS combined";
//	        
//	        // Note: Pass empCode twice since there are two placeholders.
//	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,empCode,empCode);
//	        return count != null && count > 0;
//	    }
	    
	  
	  public boolean doesEmployeeExist(String empCode) {
	    	
	        String sql = "SELECT COUNT(*) FROM ( " +
	                     "  SELECT emp_no  FROM pensioner_details WHERE emp_no = ? AND file_category IN (3,4,5) "+
	                    		 "  UNION ALL " +
	                    		 "  SELECT emp_no  FROM pensioner_details WHERE emp_no = ? AND file_category IN (1) AND is_closed=0 "+
	                    		 "  UNION ALL " +
	    	                     "  SELECT emp_no FROM cps_pensioner_details WHERE emp_no = ? AND file_category IN (6) " +
	    	                     ") AS combined";
	        // Note: Pass empCode twice since there are two placeholders.
	        Integer count = jdbcTemplate.queryForObject(sql,Integer.class,empCode,empCode,empCode);
	        System.out.println("count<<<<<<<<<<<"+count);
	        return count != null && count > 0;
	    }
	    
	    
	    public boolean doesEmployeeExistgis(String empCode) {
	        String sql = "SELECT COUNT(*) FROM ( " +
	                     "  SELECT emp_no FROM pensioner_details WHERE emp_no = ? AND file_category IN (1,2,3,5) " +
	                     "  UNION ALL " +
	                     "  SELECT emp_no FROM cps_pensioner_details WHERE emp_no = ? AND file_category IN (6) " +
	                     ") AS combined";
	        // Note: Pass empCode twice since there are two placeholders.
	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,empCode,empCode);
	        return count != null && count > 0;
	    }
	    
	    
      public boolean doesEmployeeExistfp(String empCode) {
	    	
	        String sql = "SELECT COUNT(*) FROM ( " +
	        
	                     "  SELECT emp_no FROM pensioner_details WHERE emp_no = ? AND file_category IN (1,2,3,5) AND is_closed IN (0)" +
	                     "  UNION ALL " +
	                                         
	                     "  SELECT emp_no FROM cps_pensioner_details WHERE emp_no = ? AND file_category IN (6) " +
	                     ") AS combined";
	        
	        // Note: Pass empCode twice since there are two placeholders.
	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,empCode,empCode);
	        return count != null && count > 0;
	    }

}