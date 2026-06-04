package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.vendor.service.DateTimeUtil;

@Service
@Transactional
public class SocialMediaService {
	
	private final RestTemplate restTemplate;
	
	 @Autowired
	 private JdbcTemplate jdbcTemplate;
	
	 
	 @Autowired
	 public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	 
	 @Autowired
	    @Qualifier("vendorDateTimeUtil")
	    private DateTimeUtil dateTimeUtil;
	 
	 private final Environment environment;
	 
	 private String fileBaseUrl;
	 
	 public SocialMediaService(Environment environment,RestTemplate restTemplate) {
		 this.restTemplate=restTemplate;
	        this.environment = environment;	       
	        this.fileBaseUrl = environment.getProperty("fileBaseUrl");
	    }
	 
	 @Transactional
	    public int uploadComplaintDetailsInLogs(Map<String, Object> details,String action,String remarks,int updated_agent,String remainderDate) {
	    	//System.out.println("Complaint Details: " + details);
		    // Extract other field values from complaintDetails
		    String complaintNumber = details.get("complaintid").toString();
		    String complaintDate = details.get("complaintdate").toString();
		    String personName = details.get("complainantName").toString();
		    String personMobNo = details.get("mobileNumber").toString();
		    String cType = details.get("complaintType").toString();
		    String cMode = details.get("compmode").toString();
		    String department = details.get("deptname").toString();
		    String officialName = details.get("Officialname").toString();
		    String officialMobileNum = details.get("OfficialMobile").toString();
		    String callStatus = action;
		    String remarksfield = remarks;
		    String zone = details.get("Zone").toString();
		    int updated_by=updated_agent;
		    String cur_status = details.get("currentStatus").toString();
		    
		    String finalremainderDate=null;
		    if(!remainderDate.isEmpty()) {
		    	finalremainderDate=remainderDate;
		    }
		    
		    LocalDateTime currentDate = LocalDateTime.now();
		    
		        String query = "INSERT INTO gcc_1913_qaqc.social_media_completed (complaint_number,zone,complaint_date, complaint_person_name,remarks, "
		                     + "complaint_mobilenumber, complaint_type, complaint_mode, department, official_name,official_mobilenum, call_status,created_date,updated_by,cur_status,remainder_date) "
		                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";

		        int rowsAffected = jdbcTemplate.update(query, complaintNumber,zone, complaintDate,personName,remarksfield,
		                personMobNo, cType, cMode, department, officialName, officialMobileNum,callStatus,
		                 currentDate,updated_by,cur_status,finalremainderDate);
		        //System.out.println("Rows Inserted in table : " + rowsAffected); 
		        	       
		        return rowsAffected;
		        		    
		}

	 public List<Map<String, Object>> getdetails(String formattedFromDate, String formattedToDate, String status, String zone) {
		    StringBuilder query = new StringBuilder("SELECT *,DATE_FORMAT(remainder_date, '%d/%m/%Y') as formatted_fdate FROM gcc_1913_qaqc.social_media_completed WHERE DATE(created_date) BETWEEN ? AND ? ");
		    List<Object> params = new ArrayList<>();
		    params.add(formattedFromDate);
		    params.add(formattedToDate);

		    if (zone != null && !zone.isEmpty()) {
		        query.append(" AND zone = ?");
		        params.add(zone);
		    }

		    if (status != null && !status.equals("ALL")) {
		        query.append(" AND call_status = ?");
		        params.add(status);
		    }

		    // Execute the query
		    List<Map<String, Object>> results = jdbcTemplate.queryForList(query.toString(), params.toArray());


		    return results.isEmpty() ? null : results;
		}

	public String getErpusernamebyId(String userId) {
		
		String query="SELECT erp_username FROM gcc_1913_qaqc.agents_list "
				+ "where calling_type='SOCIALMEDIA' and agent_id=?";
		
		//System.out.println("query====="+query);
		
		//String erpUsername = jdbcTemplate.queryForObject(query,new Object[]{userId},String.class);
		String erpUsername = jdbcTemplate.queryForObject(query,String.class,userId);
		
	     return erpUsername;
	}
	
	public String get1913AdminErpusernamebyId(String userId) {
			
			String query="SELECT erp_username FROM gcc_1913_qaqc.agents_list "
					+ "where agent_id=?";
			
			//System.out.println("query====="+query);
			
			//String erpUsername = jdbcTemplate.queryForObject(query,new Object[]{userId},String.class);
			String erpUsername = jdbcTemplate.queryForObject(query,String.class,userId);
			
		     return erpUsername;
		}

	public List<Map<String, Object>> getA1dropdowns() {
		String sql="SELECT * FROM call_status WHERE isactive=1 AND isdelete=0 AND call_category_id=5";
		try {
	        return jdbcTemplate.queryForList(sql);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return new ArrayList<>(); // return empty list on error
	    }
	}

	public List<Map<String, Object>> getA2dropdowns() {
		String sql="SELECT * FROM call_status WHERE isactive=1 AND isdelete=0 AND call_category_id=6";
		try {
	        return jdbcTemplate.queryForList(sql);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return new ArrayList<>(); // return empty list on error
	    }
	}

	public List<Map<String, Object>> getfiltreddetails(List<Map<String, Object>> details) {

	    List<Map<String, Object>> followupData = getFollowupData();

	    if (followupData == null || followupData.isEmpty()) {
	        return details;
	    }
	    
//	    if(details.isEmpty()) {
//	    	return details;
//	    }

	    LocalDate currentDate = LocalDate.now();

	    // Store complaint_number -> followup row
	    Map<String, Map<String, Object>> followupMap = new HashMap<>();

	    for (Map<String, Object> followup : followupData) {

	        String complaintNumber = String.valueOf(followup.get("complaint_number"));

	        followupMap.put(complaintNumber, followup);
	    }

	    List<Map<String, Object>> filteredDetails = new ArrayList<>();

	    for (Map<String, Object> detail : details) {

	        String complaintNumber =String.valueOf(detail.get("Complaint Number"));

	        // If complaint not in followup table
	        if (!followupMap.containsKey(complaintNumber)) {

	            filteredDetails.add(detail);
	            continue;
	        }

	        Map<String, Object> followupRow =followupMap.get(complaintNumber);

	        String remainderDateStr =String.valueOf(followupRow.get("remainder_date"));

	        LocalDate remainderDate = LocalDate.parse(remainderDateStr);

	        // remainder_date <= current date
	        if (!remainderDate.isAfter(currentDate)) {

	            // UPDATE is_processed = 0
	            Integer id = (Integer) followupRow.get("id");

	            updateFollowupProcessed(id);

	            filteredDetails.add(detail);
	        }

	        // ELSE:
	        // remainder_date > currentDate
	        // skip / eliminate from frontend
	    }

	    return filteredDetails;
	}
	
	private void updateFollowupProcessed(Integer id) {

	    String sql =
	            "UPDATE social_media_completed " +
	            "SET is_processed = 0 " +
	            "WHERE id = ?";

	    jdbcTemplate.update(sql, id);
	}

	private List<Map<String, Object>> getFollowupData() {
		String sqlQuery = "select id, complaint_number,call_status, remainder_date from social_media_completed where is_processed=1 AND call_status='FOLLOWUP'";
	  	  
		 return jdbcTemplate.queryForList(sqlQuery); 
	}

	public Map<String, Object> checkRegisteredComplaint(String complaintNumber) {

	    Map<String, Object> result = new HashMap<>();

	    String sql = "SELECT is_processed "
	    		+ "	        FROM socialmedia_reg_completed "
	    		+ "	        WHERE complaint_number = ? AND isactive=1 AND isdelete=0 "
	    		+ "	        LIMIT 1";

	    List<Map<String, Object>> list =
	            jdbcTemplate.queryForList(sql, complaintNumber);

	    // Case 1
	    if (list.isEmpty()) {
	        result.put("status", "NOT_FOUND");
	        return result;
	    }

	    Boolean isProcessed = (Boolean) list.get(0).get("is_processed");

	    if (!isProcessed) {
	        result.put("status", "PENDING_APPROVAL");
	    } else {
	        result.put("status", "PROCESSED");
	    }

	    return result;
	}

	public Map<String, Object> submitRegClosingComplaint(
	        String complaintNumber,
	        String remarks,
	        MultipartFile resolvedImage,
	        String cby) {

	    Map<String, Object> response = new HashMap<>();

	    try {

	        Integer loginId = Integer.parseInt(cby);

	        String imagePath = fileUpload(resolvedImage, "resolved_img");

	        String sql = "INSERT INTO socialmedia_reg_completed "
	                + "(complaint_number, resolved_img, remarks, cby) "
	                + "VALUES (?, ?, ?, ?)";

	        int rows = jdbcTemplate.update(
	                sql,
	                complaintNumber,
	                imagePath,
	                remarks,
	                loginId
	        );

	        if (rows > 0) {
	            response.put("status", "SUCCESS");
	            response.put("message", "Complaint submitted successfully for admin approval.");
	        } else {
	            response.put("status", "FAILED");
	            response.put("message", "Unable to submit complaint.");
	        }

	    } catch (Exception e) {

	        e.printStackTrace();

	        response.put("status", "error");
	        response.put("message", e.getMessage() != null
	                ? e.getMessage()
	                : "Something went wrong while submitting complaint.");
	    }

	    return response;
	}

	public String fileUpload(MultipartFile file, String name) {

        // Set the file path where you want to save it
        String uploadDirectory = environment.getProperty("file.upload.directory");
        String serviceFolderName = environment.getProperty("socialmedia_1913_regcomp_foldername");
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


            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            byte[] compressedBytes = compressImage(image, 0.5f); // Compress with 50%quality

            // Write the bytes to the file
            Files.write(path, bytes);
			  
			 // Write the bytes to the file 
			 Files.write(path, compressedBytes);
			 

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
	
	
	public  Map<String, Object> getDetailsByComplaintNumber(String complaintNumber) {
		
		Map<String, Object> resultMap = new HashMap<>();

        try {
            String url = "https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=getComplaintByID&ComplaintId=" 
                + complaintNumber + "&imgUrlonly=yes&isQcuser=Yes&jsonResp=Yes";
            //System.out.println("Request URL: " + url);

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String responseBody = responseEntity.getBody();
                //System.out.println("Response Body: " + responseBody);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(responseBody);

                JsonNode complaintDetails = rootNode.path(0).path("ComplaintListHistory").path(0);
                
                String registerDate=complaintDetails.path("complaintopendate").asText("N/A");
                //System.out.println("registerdate====="+registerDate);
                
                String formattedDate = "N/A"; // Default value if parsing fails

                if (!registerDate.equals("N/A") && !registerDate.isEmpty()) {
                    try {
                        // Parse the input date string
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                        Date date = inputFormat.parse(registerDate);

                        // Format the date to dd-MM-yyyy
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        formattedDate = outputFormat.format(date);
                    } catch (Exception e) {
                        e.printStackTrace(); // Log exception for debugging
                    }
                }

                if (!complaintDetails.isMissingNode()) {
                    // Existing fields
                    resultMap.put("complaintid", complaintDetails.path("complaintid").asText("N/A"));
                    resultMap.put("complaintdate", formattedDate);
                    resultMap.put("complainantName", complaintDetails.path("CustomerName").asText("N/A"));
                    resultMap.put("mobileNumber", complaintDetails.path("CustomerMobile").asText("N/A"));
                    resultMap.put("complaintType", complaintDetails.path("complainttype").asText("N/A"));
                    resultMap.put("description", complaintDetails.path("complaintdetails").asText("N/A"));
                    resultMap.put("currentStatus", complaintDetails.path("complaintcurrentstatus").asText("N/A"));
                    resultMap.put("Street", complaintDetails.path("Street").asText("N/A"));
                    resultMap.put("Area", complaintDetails.path("Area").asText("N/A"));
                    resultMap.put("Landmark", complaintDetails.path("Landmark").asText("N/A"));
                    resultMap.put("Location", complaintDetails.path("Location").asText("N/A"));
                    resultMap.put("Zone", complaintDetails.path("Zone").asText("N/A"));
                    resultMap.put("Division", complaintDetails.path("Division").asText("N/A"));
                    resultMap.put("OfficialMobile", complaintDetails.path("OfficialMobile").asText("N/A"));
                    resultMap.put("gender", complaintDetails.path("GENDER").asText("N/A"));
                    resultMap.put("deptname",complaintDetails.path("DEPT_NAME").asText("N/A"));
                    resultMap.put("compmode", complaintDetails.path("COMP_MODE").asText("N/A"));
                    resultMap.put("Officialname", complaintDetails.path("Officialname").asText("N/A"));

                 
                }
            } else {
                resultMap.put("error", "Failed to retrieve complaint details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("error", "Error processing request");
        }

        return resultMap;
    }

	public Map<String, Object> getFilteredRegComps() {

	    Map<String, Object> response = new HashMap<>();

	    try {

	        String sql =
	                "SELECT src.complaint_number, src.created_date,CONCAT('" + fileBaseUrl + "/gcc/files',src.resolved_img) AS img_full_path,al.agent_name as agent_name,src.remarks as remarks" +
	                " FROM socialmedia_reg_completed src "
	                + " LEFT JOIN agents_list al ON al.agent_id=src.cby " +
	                "WHERE src.is_processed = 0 " +
	                "AND src.isactive = 1 " +
	                "AND src.isdelete = 0";

	        List<Map<String, Object>> dbList = jdbcTemplate.queryForList(sql);

	        List<Map<String, Object>> finalList = new ArrayList<>();

	        for (Map<String, Object> row : dbList) {

	            String complaintNumber =
	                    String.valueOf(row.get("complaint_number"));

	            Map<String, Object> complaintDetails =
	                    getDetailsByComplaintNumber(complaintNumber);

	            String currentStatus =
	                    String.valueOf(
	                            complaintDetails.getOrDefault(
	                                    "currentStatus",
	                                    ""
	                            )
	                    );
	            
	            //System.out.println("currentStatus==="+currentStatus);

	            if ("REGISTERED".equalsIgnoreCase(currentStatus)
	                    || "FORWARDED".equalsIgnoreCase(currentStatus)) {
	            	
	                Map<String, Object> resultRow = new HashMap<>();

	                resultRow.put("complaintnumber", complaintNumber);
	                //resultRow.put("submitted_date", row.get("created_date"));
	                Timestamp ts = (Timestamp) row.get("created_date");

	                SimpleDateFormat sdf =
	                        new SimpleDateFormat("dd/MM/yyyy HH:mm");

	                resultRow.put("submitted_date", sdf.format(ts));
	                
	                String img =  (String) row.get("img_full_path");
	                resultRow.put("img_full_path", img);
	                
	                String agent_name =  (String) row.get("agent_name");
	                resultRow.put("agent_name", agent_name);
	                
	                String remarks =  (String) row.get("remarks");
	                resultRow.put("remarks", remarks);

	                finalList.add(resultRow);

	            } else {

	                String updateSql =
	                        "UPDATE socialmedia_reg_completed " +
	                        "SET updated_by = ?, " +
	                        "updated_date = NOW(), " +
	                        "isactive = 0, " +
	                        "isdelete = 1 " +
	                        "WHERE complaint_number = ?";

	                jdbcTemplate.update(
	                        updateSql,
	                        0,
	                        complaintNumber
	                );
	            }
	        }

	        if (finalList.isEmpty()) {

	            response.put("status", "nodata");
	            response.put("message", "No registered complaints found.");

	        } else {

	            response.put("status", "success");
	            response.put("details", finalList);
	        }

	    } catch (Exception e) {

	        e.printStackTrace();

	        response.put("status", "error");
	        response.put("message", e.getMessage());
	    }

	    return response;
	}

	public Map<String, Object> approveComplaint(String complaintId, String remarks, String imgFullPath, String userid) {
		
		 Map<String, Object> response = new HashMap<>();

		    try {

		        Integer updated_by = Integer.parseInt(userid);

		        String erp_name=get1913AdminErpusernamebyId(userid);
		        
		        boolean statusUpdated =
		                ChangeStatusInErp(
		                        complaintId,
		                        "ActionLevel1",
		                        remarks,
		                        erp_name,
		                        imgFullPath);

		        if (!statusUpdated) {
		            response.put("status", "FAILED");
		            response.put("message", "Unable to move complaint to Action Level 1.");
		            return response;
		        }

		        boolean statusUpdated1 =
		                ChangeStatusInErp(
		                        complaintId,
		                        "Close",
		                        remarks,
		                        erp_name,
		                        "");

		        if (!statusUpdated1) {
		            response.put("status", "FAILED");
		            response.put("message", "Unable to move complaint to Close.");
		            return response;
		        }
		        
				String sql="UPDATE socialmedia_reg_completed "
						+ "SET is_processed = 1, "
						+ "    updated_by = ?, "
						+ "    updated_date = NOW() "
						+ "WHERE complaint_number = ? AND isactive=1 AND isdelete=0";
				
				int rows=jdbcTemplate.update(sql,updated_by,complaintId);

		        if (rows > 0) {
		            response.put("status", "SUCCESS");
		            response.put("message", "Complaint Closed successfully.");
		        } else {
		            response.put("status", "FAILED");
		            response.put("message", "Complaint Closed,Unable to insert in logs.");
		        }

		    } catch (Exception e) {

		        e.printStackTrace();

		        response.put("status", "error");
		        response.put("message", e.getMessage() != null
		                ? e.getMessage()
		                : "Something went wrong while submitting complaint.");
		    }

		    return response;
	}
	
	public boolean ChangeStatusInErp(String complaintNumber,String action ,String remarks,String erp_name,String imgFullPath) {
	    String baseUrl = "https://erp.chennaicorporation.gov.in/pgr/external/mobileservice";
	    String status = "";

	  
	    try {
	      	        
	     // Determine status based on action
	        if ("ActionLevel1".equals(action)) {
	            status = "5";
	        } else if ("Close".equals(action)) {	        	
	            status = "11";
	        } else {
	            return false; // Invalid action, return false
	        }
	        
	        //String img="https://gccservices.in/gccofficialapp/files/app_icon/menu/gcc_survey_icons/Cow.png";
	        
	     // Convert image to Base64
	        String base64Image = "";

	        if (imgFullPath != null && !imgFullPath.trim().isEmpty()) {

	            URL imageUrl = new URL(imgFullPath);

	            try (InputStream inputStream = imageUrl.openStream()) {

	                byte[] imageBytes = inputStream.readAllBytes();

	                base64Image =
	                        Base64.getEncoder().encodeToString(imageBytes);
	            }
	        }
	        
	        String postData =
	                "serviceId=" + URLEncoder.encode("UpdateComplaint", "UTF-8")
	                + "&ComplaintId=" + URLEncoder.encode(complaintNumber, "UTF-8")
	                + "&Message=" + URLEncoder.encode(remarks, "UTF-8")
	                + "&UserType=Official"
	                + "&Status=" + status
	                + "&Comp_Image=" + URLEncoder.encode(base64Image, "UTF-8")
	                + "&erp_username=" + URLEncoder.encode(erp_name, "UTF-8");

	        
	        System.out.println("Complaint = " + complaintNumber);
	        System.out.println("Action = " + action);
	        System.out.println("Base64 Length = " + base64Image.length());
	        System.out.println("Post Data Length = " + postData.length());	
	        
	        return sendRequest(baseUrl, postData);
	       // return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	
	private boolean sendRequest(String urlString, String postData) {

	    try {
	        URL url = new URL(urlString);

	        HttpURLConnection connection =
	                (HttpURLConnection) url.openConnection();
	       

	        connection.setRequestMethod("POST");
	        connection.setDoOutput(true);

	        connection.setRequestProperty(
	                "Content-Type",
	                "application/x-www-form-urlencoded"
	        );

	        //System.out.println("Method = " + connection.getRequestMethod());
	        try (OutputStream os = connection.getOutputStream()) {

	            byte[] input = postData.getBytes(StandardCharsets.UTF_8);

	            os.write(input, 0, input.length);
	        }

	        int responseCode = connection.getResponseCode();

	        System.out.println("Response Code = " + responseCode);
	        System.out.println("Response Message = " + connection.getResponseMessage());

	        InputStream stream;

	        if (responseCode >= 200 && responseCode < 300) {
	            stream = connection.getInputStream();
	        } else {
	            stream = connection.getErrorStream();
	        }

	        StringBuilder responseBuilder = new StringBuilder();

	        if (stream != null) {

	            BufferedReader reader =
	                    new BufferedReader(
	                            new InputStreamReader(stream));

	            String line;

	            while ((line = reader.readLine()) != null) {
	                responseBuilder.append(line);
	            }

	            reader.close();
	        }

	        String response = responseBuilder.toString();

	        System.out.println("ERP Response = " + response);

	        return response.contains("\"ResultStatus\": true");

	    } catch (Exception e) {

	        e.printStackTrace();
	        return false;
	    }
	}

	public Map<String, Object> rejectComplaint(String complaintId, String userid) {
		
		 Map<String, Object> response = new HashMap<>();

		    try {
		    	Integer updated_by = Integer.parseInt(userid);
		    	
		    	String sql="UPDATE socialmedia_reg_completed "
						+ "SET isactive=0,isdelete=1, "
						+ "    updated_by = ?, "
						+ "    updated_date = NOW() "
						+ "WHERE complaint_number = ? AND isactive=1 AND isdelete=0";
				
				int rows=jdbcTemplate.update(sql,updated_by,complaintId);
		    	
		    	
		    	if (rows > 0) {
		            response.put("status", "SUCCESS");
		            response.put("message", "Complaint Rejected successfully.");
		        } else {
		            response.put("status", "FAILED");
		            response.put("message", "Unable to  Reject the Complaint.");
		        }

		    } catch (Exception e) {

		        e.printStackTrace();

		        response.put("status", "error");
		        response.put("message", e.getMessage() != null
		                ? e.getMessage()
		                : "Something went wrong while submitting complaint.");
		    }

		    return response;
	}

}
