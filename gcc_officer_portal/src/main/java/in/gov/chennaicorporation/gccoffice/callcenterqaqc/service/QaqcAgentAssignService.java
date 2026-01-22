package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QaqcAgentAssignService {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
    private JdbcTemplate jdbcTemplate2;
	
	@Autowired
	public void setDataSource2(@Qualifier("mysql1913CampaignDataSource") DataSource dataSource) {
		this.jdbcTemplate2 = new JdbcTemplate(dataSource);
	}
		
	@Transactional
	public Integer getCampaignPendingCountForAgent(int agentId) {
	    String countQuery = "SELECT COUNT(ud.data_id) FROM gcc_1913_campaign.upload_data ud\r\n"
	    		+ "JOIN gcc_1913_campaign.campaign_request cr ON cr.campaign_id=ud.campaign_id\r\n"
	    		+ "WHERE cr.campaign_status='ONGOING' AND ud.agent_id = ? AND ud.call_status IS NULL";
	    return jdbcTemplate2.queryForObject(countQuery, Integer.class, agentId);
	}
	
	@Transactional
	public List<Map<String, Object>> getAvailableOutboundAgents()
	{
		String sqlQuery = "select agent_id,agent_name\r\n"
				+ "from gcc_1913_qaqc.agents_list\r\n"
				+ "where calling_type='OUTBOUND' and isactive='1' and isdelete='0'";
		
		return jdbcTemplate.queryForList(sqlQuery);
	}
	
	@Transactional
	public Integer getTodayCountForAgent(int agentId) {
	    String countQuery = "SELECT coalesce(sum(aa.data_count),0) as todaycounts \r\n"
	    		+ "from gcc_1913_qaqc.agent_assigned aa \r\n"
	    		+ "join gcc_1913_qaqc.agents_list al on al.agent_id=aa.agent_id \r\n"
	    		+ "where date(aa.created_date)=curdate() and al.agent_id=?";
	    return jdbcTemplate.queryForObject(countQuery, Integer.class, agentId);
	}
	
	
	
//	@Transactional
//	public List<Map<String, Object>> getAvailableOutboundAgents()
//	{
//		String sqlQuery = "SELECT al.agent_id as agent_id,al.agent_name as agent_name,coalesce(sum(aa.data_count),0) as todaycounts\r\n"
//				+ "from agent_assigned aa\r\n"
//				+ "join agents_list al on al.agent_id=aa.agent_id\r\n"
//				+ "where date(aa.created_date)=curdate() and al.calling_type='OUTBOUND' and al.isactive='1' and al.isdelete='0'\r\n"
//				+ "group by al.agent_id,al.agent_name";
//		
//		return jdbcTemplate.queryForList(sqlQuery);
//	}
	
	
	@Transactional // Ensures atomicity of operations
    public Map<String, Object> assignTasksTransactionally(Map<String, Object> requestData) {
		Map<String, Object> response = new HashMap<>();
        // Extract input data
        String description = (String) requestData.get("description");
        String fromDate = (String) requestData.get("startDate");
        String toDate = (String) requestData.get("endDate");
        String status = (String) requestData.get("closeName");
           
        String group = (String) requestData.get("group");
        String type = (String) requestData.get("type");
        String mode = (String) requestData.get("mode");
        String  zone= (String) requestData.get("zone");
        String  region= (String) requestData.get("region");
        
        int closedCount = Integer.parseInt(requestData.get("closedCount").toString());
        //System.out.println("closedCount====="+closedCount);
        List<Map<String, Object>> agents = (List<Map<String, Object>>) requestData.get("agents");
        //System.out.println("agents====="+agents);
        // Step 1: Save task details
        LocalDate fromLocalDate = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE);
        LocalDate toLocalDate = LocalDate.parse(toDate, DateTimeFormatter.ISO_DATE);
        String formattedFromDate = fromLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String formattedToDate = toLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime indiaTime = now.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
        String taskId = "#" + indiaTime.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        //System.out.println("taskId==="+taskId);
//        if ("closed".equalsIgnoreCase(status)) {
//            status = "Action Taken Level 2";
//        }
                 
        int rowsInserted = savetaskid(taskId, formattedFromDate, formattedToDate, description, status, closedCount,group,type,mode,zone,region);

        if (rowsInserted <= 0) {
            throw new RuntimeException("Failed to create task");
        }

        // Step 2: Get the last task
        List<Map<String, Object>> lastTask = getLastTask();
        if (lastTask.isEmpty()) {
            throw new RuntimeException("Failed to fetch last task");
        }
        int qaqcId = (int) lastTask.get(0).get("qaqc_id");
        
        
        List<Map<String, Object>> assignedIDList = new ArrayList<>();
        
        //System.out.println("Before shuffle: " + agents);
        
        // Shuffle the agents list
        Collections.shuffle(agents);
        
        //System.out.println("After shuffle: " + agents);
        // Step 3: Save agents' task counts
        for (Map<String, Object> agent : agents) {
            Integer agentId = Integer.valueOf(agent.get("agentId").toString());
            Integer dataCounts = Integer.valueOf(agent.get("callsPerAgent").toString());
            assignedIDList.add( saveInAgentAssigned(agentId, qaqcId, dataCounts));
        }
        //System.out.println("outside assignedIDList===="+assignedIDList);
        

        // Step 4: Save upload data
        if (!"officialfollowup".equalsIgnoreCase(status) && !"publicfollowup".equalsIgnoreCase(status))
        {
			String url="https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=ms_dashboard_details&From_date="+formattedFromDate+"&To_date="+formattedToDate+"&jsonResp=Yes&Status="+status+"&isQcuser=Yes"+"&ComplaintType="+type+"&ComplaintGroupId="+group+"&compmode="+mode+"&Zoneid="+zone+"&RegionId="+region;
			System.out.println("url=="+url);
	        boolean isSaved = processAndSaveDetails(url, qaqcId, agents, status,assignedIDList, mode);
	        if (!isSaved) {
	            throw new RuntimeException("Failed to process upload data");
	        }
        }       
        else
        {   
        	boolean isSaved=processAndSaveFollowupDetails(qaqcId, agents, status,formattedFromDate);
        	if (!isSaved) {
	            throw new RuntimeException("Failed to process upload data");
	        }
        }
        
       
        response.put("status", "success");
        response.put("message", "Data Transferred Successfully");
        return response;
    }
	
	@Transactional
	public List<Map<String, Object>> getfollowupdata(String fromdate, String status) {
	    try {
	        // Parse input date from dd/MM/yyyy to yyyy-MM-dd
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LocalDate formattedDate = LocalDate.parse(fromdate, inputFormatter);

	        // Format the date into yyyy-MM-dd
	        String formattedFromDate = formattedDate.format(outputFormatter);
	        if (!"officialfollowup".equalsIgnoreCase(status))
	        {
		        //String sqlQuery = "SELECT * FROM gcc_1913_qaqc.qaqc_followup_call_logs WHERE remainder_date = ? AND call_status='FOLLOWUP' AND call_category IN ('CLOSED','PUBLICFOLLOWUP')";
	        	String sqlQuery = "SELECT * FROM gcc_1913_qaqc.qaqc_followup_call_logs WHERE remainder_date = ? AND call_status='FOLLOWUP' AND call_category IN ('CLOSED','PUBLICFOLLOWUP') AND isactive=1";
	        	return jdbcTemplate.queryForList(sqlQuery, formattedFromDate);
	        }
	        else
	        {
	        	//String sqlQuery = "SELECT * FROM gcc_1913_qaqc.qaqc_followup_call_logs WHERE remainder_date = ? AND call_status='FOLLOWUP' AND call_category NOT IN ('CLOSED','PUBLICFOLLOWUP')";
	        	String sqlQuery = "SELECT * FROM gcc_1913_qaqc.qaqc_followup_call_logs WHERE remainder_date = ? AND call_status='FOLLOWUP' AND call_category NOT IN ('CLOSED','PUBLICFOLLOWUP') AND isactive=1";
	        	return jdbcTemplate.queryForList(sqlQuery, formattedFromDate);
	        }

	    } catch (DateTimeParseException e) {
	        throw new IllegalArgumentException("Invalid date format. Expected dd/MM/yyyy.", e);
	    }
	}

	@Transactional
	public boolean processAndSaveFollowupDetails(int qaqc_id, List<Map<String, Object>> agents, String status,
			String fromdate) {
		try {

			List<Map<String, Object>> detail = getfollowupdata(fromdate, status);
			

			int index = 0;
			
			
			for (Map<String, Object> agent : agents) {
				int remainingDataCountForAgent = Integer.parseInt(agent.get("callsPerAgent").toString());
				// System.out.println(remainingDataCountForAgent);
				int currentAgentId = Integer.parseInt(agent.get("agentId").toString());
				// System.out.println(agent.get("agentName").toString());

				for (int i = 0; i < remainingDataCountForAgent && index < detail.size(); i++, index++) {
					// System.out.println(i +" "+index);
					// Save the current complaint details with the current agent_id
					Integer isactive_id=(Integer) detail.get(index).get("id");
					
					String complaintNumber = (String) detail.get(index).get("complaint_number");
					String complaintDate = (String) detail.get(index).get("complaint_date");
					String complanitPersonName = (String) detail.get(index).get("complaint_person_name");
					String complaintPersonMobileNum = (String) detail.get(index).get("complaint_mobilenumber");
					String complaintType = (String) detail.get(index).get("complaint_type");
					String complaintMode = (String) detail.get(index).get("complaint_mode");
					String department = (String) detail.get(index).get("department");
					String complaintGroup = (String) detail.get(index).get("complaint_group");
					String officerName = (String) detail.get(index).get("official_name");
					String officerMobileNumber = (String) detail.get(index).get("official_mobilenum");

					String status3 = status.toUpperCase();

					// Insert into the database
					String insertQuery = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data (qaqc_id, call_category, complaint_number, complaint_date,complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					// jdbcTemplate.update(insertQuery, qaqc_id,status3
					// ,complaintNumber,complaintDate, complanitPersonName,
					// complaintPersonMobileNum, complaintType, complaintMode, department,
					// complaintGroup, officerName, officerMobileNumber, currentAgentId);
					KeyHolder keyHolder = new GeneratedKeyHolder();

					jdbcTemplate.update(connection -> {
						PreparedStatement ps = connection.prepareStatement(insertQuery,
								Statement.RETURN_GENERATED_KEYS);
						ps.setInt(1, qaqc_id);
						ps.setString(2, status3);
						ps.setString(3, complaintNumber);
						ps.setString(4, complaintDate);
						ps.setString(5, complanitPersonName);
						ps.setString(6, complaintPersonMobileNum);
						ps.setString(7, complaintType);
						ps.setString(8, complaintMode);
						ps.setString(9, department);
						ps.setString(10, complaintGroup);
						ps.setString(11, officerName);
						ps.setString(12, officerMobileNumber);
						ps.setInt(13, currentAgentId);
						return ps;
					}, keyHolder);
					Number generatedDataId = keyHolder.getKey();

					if (generatedDataId != null) {
						long dataId = generatedDataId.longValue();
						// System.out.println("followdataId=="+dataId);
						// Use the dataId in the next insert for qaqc_upload_data_history
						String sql = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data_history "
								+ "(qaqc_id, call_category, complaint_number, complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id, data_id) "
								+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

						jdbcTemplate.update(sql, qaqc_id, status3, complaintNumber, complaintDate, complanitPersonName,
								complaintPersonMobileNum, complaintType, complaintMode, department, complaintGroup,
								officerName, officerMobileNumber, currentAgentId, dataId);
					}
					
					//System.out.println("isactive_id===="+isactive_id);
					
					String upsql2 = "UPDATE gcc_1913_qaqc.qaqc_followup_call_logs set isactive=0 where id=?";
					jdbcTemplate.update(upsql2, isactive_id);

				}
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	@Transactional
	public boolean processAndSaveFollowupDetails(int qaqc_id, List<Map<String, Object>> agents,String status,String fromdate) {
	    try {

	    	List<Map<String,Object>> detail=getfollowupdata(fromdate, status);
	    	
	    	int index = 0;

	    	for(Map<String, Object> agent: agents) {
	    			int remainingDataCountForAgent = Integer.parseInt(agent.get("callsPerAgent").toString());
	    			//System.out.println(remainingDataCountForAgent);
	    			int currentAgentId = Integer.parseInt(agent.get("agentId").toString());
	    			//System.out.println(agent.get("agentName").toString());

	    	for (int i = 0; i < remainingDataCountForAgent && index < detail.size(); i++, index++) {
	    		//System.out.println(i +" "+index);
	            // Save the current complaint details with the current agent_id
	            String complaintNumber = (String) detail.get(index).get("complaint_number");
	            String complaintDate = (String) detail.get(index).get("complaint_date");
	            String complanitPersonName = (String) detail.get(index).get("complaint_person_name");
	            String complaintPersonMobileNum = (String) detail.get(index).get("complaint_mobilenumber");
	            String complaintType = (String) detail.get(index).get("complaint_type");
	            String complaintMode = (String) detail.get(index).get("complaint_mode");
	            String department = (String) detail.get(index).get("department");
	            String complaintGroup = (String) detail.get(index).get("complaint_group");
	            String officerName = (String) detail.get(index).get("official_name");
	            String officerMobileNumber = (String) detail.get(index).get("official_mobilenum");
	            
	            
	            String status3=status.toUpperCase();

	            // Insert into the database
	            String insertQuery = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data (qaqc_id, call_category, complaint_number, complaint_date,complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	            //jdbcTemplate.update(insertQuery, qaqc_id,status3 ,complaintNumber,complaintDate, complanitPersonName, complaintPersonMobileNum, complaintType, complaintMode, department, complaintGroup, officerName, officerMobileNumber, currentAgentId);
	            KeyHolder keyHolder = new GeneratedKeyHolder();

	            jdbcTemplate.update(connection -> {
	                PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
	                ps.setInt(1, qaqc_id);
	                ps.setString(2, status3);
	                ps.setString(3, complaintNumber);
	                ps.setString(4, complaintDate);
	                ps.setString(5, complanitPersonName);
	                ps.setString(6, complaintPersonMobileNum);
	                ps.setString(7, complaintType);
	                ps.setString(8, complaintMode);
	                ps.setString(9, department);
	                ps.setString(10, complaintGroup);
	                ps.setString(11, officerName);
	                ps.setString(12, officerMobileNumber);
	                ps.setInt(13, currentAgentId);
	                return ps;
	            }, keyHolder);
	            Number generatedDataId = keyHolder.getKey();
	            
	            if (generatedDataId != null) {
	                long dataId = generatedDataId.longValue();
	                //System.out.println("followdataId=="+dataId);
	                // Use the dataId in the next insert for qaqc_upload_data_history
	                String sql = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data_history " +
	                        "(qaqc_id, call_category, complaint_number, complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id, data_id) " +
	                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	                jdbcTemplate.update(sql, qaqc_id, status3, complaintNumber, complaintDate, complanitPersonName, 
	                                    complaintPersonMobileNum, complaintType, complaintMode, department, 
	                                    complaintGroup, officerName, officerMobileNumber, currentAgentId, dataId);
	            }
	            
	            }
	        }
	        return true;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	 */
	/*
	@Transactional
	public boolean processAndSaveDetails(String url, int qaqc_id, List<Map<String, Object>> agents,String status,List<Map<String, Object>> assignedIDList, String mode) {
	    try {
	        	    	
	    	RestTemplate restTemplate = new RestTemplate();
	    	
			// Fetch response as String
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			String rawResponse = response.getBody();

			// Preprocess response to remove invalid characters if needed
			if (rawResponse != null) {
//				rawResponse = rawResponse.replaceAll("\"\"", "\""); // Replace double-double quotes
//				rawResponse = rawResponse.replaceAll("(?<![,{])\"R", "\"R"); // Example: Fix missing commas
				
			}

			// Log raw response
			//System.out.println("Raw Response: " + rawResponse);

			// Parse JSON
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> responseMap = objectMapper.readValue(rawResponse, Map.class);

			// Target the specific field where double-double quotes might appear
			String targetField = "Address"; // Replace with the actual field name
			if (responseMap.containsKey(targetField)) {
				String fieldValue = (String) responseMap.get(targetField);
				if (fieldValue != null && fieldValue.contains("\"\"")) {
					// Clean double-double quotes in the field value
					fieldValue = fieldValue.replaceAll("\"\"", "\"");
					responseMap.put(targetField, fieldValue); // Update the field with cleaned value
				}
			}

	        // Extract "Details" array from the response
	        List<Map<String, Object>> details = (List<Map<String, Object>>) responseMap.get("Details");
	        //System.out.println(details);
	        if (details == null) {
	            System.err.println("No 'Details' found in the response.");
	            return false; // Or handle as per your requirement
	        }
	        if(mode.isEmpty() || mode.isBlank() || mode==null ) {
	        	details=details.stream()
	        			.filter(map-> !map.get("Complaint Mode").equals("SOCIAL MEDIA")).collect(Collectors.toList());
	        }
	        
	        // Apply filtering based on the status value
	        if ("withdraw".equalsIgnoreCase(status)) {
	            // Keep only records where "Current Status" is "Completed"
	            details = details.stream()
	                    .filter(entry -> "Completed".equals(entry.get("Current Status")))
	                    .collect(Collectors.toList());
	        } else {
	            // Remove records where "Current Status" is "Completed"
	            details = details.stream()
	                    .filter(entry -> !"Completed".equals(entry.get("Current Status")))
	                    .collect(Collectors.toList());
	        }
	        
	        int index = 0;
	        
	        List<Map<String, Object>> followupData= getFollowupData();
	            		
    		List<Map<String, Object>> countDetails = new ArrayList<>();

	    	for(Map<String, Object> agent: agents) {
	    			int remainingDataCountForAgent = Integer.parseInt(agent.get("callsPerAgent").toString());
	    			//System.out.println(remainingDataCountForAgent);
	    			int currentAgentId = Integer.parseInt(agent.get("agentId").toString());
	    			//System.out.println(agent.get("agentName").toString());
	    			
	    	Map<String, Object> countResult = new HashMap<>();
	    	int assignedCount = 0;

	    	for (int i = 0; i < remainingDataCountForAgent && index < details.size(); i++, index++) {
	    		
	    		System.out.println("CHECK = "+index+" "+details.get(index).get("Complaint Mode"));
	    		//System.out.println(i +" "+index);
	    		
	    		 String complaintNumber1 = (String) details.get(index).get("Complaint Number");
	    		 //System.out.println("complaintNumber = "+complaintNumber1);
	    		 
	    		 Map<String, Object> result = followupData.stream()
	    		            .filter(map -> complaintNumber1.equals(map.get("complaint_number")))
	    		            .findFirst().orElse(null);
	    		 
	    		 String compnum = "";
	    		 String callstatus ="";
		    	 Date calldate = null ;
	    		 if(result!= null) {
	    		  compnum = (String) result.get("complaint_number");
	    		  callstatus = (String) result.get("call_status");
		    		calldate = (Date) result.get("remainder_date");
	    		 }
		    		
	    		 
	    		if(!compnum.equals("") && compnum.equals(complaintNumber1)) {
	    			//System.out.println("1 if complaintNumber = "+complaintNumber);
	    			Date currentDate = new Date();
	    		if(!callstatus.equalsIgnoreCase("followup") && calldate.before(currentDate)) {
	    			//System.out.println("2 if complaintNumber = "+complaintNumber1);
	    			 String complaintDate = (String) details.get(index).get("Complaint Opened Date");
	    			 String complanitPersonName = (String) details.get(index).get("Complaint Person Name");
	    			 String complaintPersonMobileNum = (String) details.get(index).get("Complaint Person Number");
	    			 String complaintType = (String) details.get(index).get("Complaint Type");
	    			 String complaintMode = (String) details.get(index).get("Complaint Mode");
	    			 String department = (String) details.get(index).get("Department");
	    			 String complaintGroup = (String) details.get(index).get("Complaint Group");
	    			 String officerName = (String) details.get(index).get("Assign To");
	    			 String officerMobileNumber = (String) details.get(index).get("Official Contact Number");
	    			 String status1 = status.toUpperCase();
	    			 
	    			 
		    		 
		    		// Insert into the database
			    		String insertQuery = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data (qaqc_id, call_category, complaint_number,complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			    		//jdbcTemplate.update(insertQuery, qaqc_id, status1, complaintNumber1,complaintDate, complanitPersonName, complaintPersonMobileNum, complaintType, complaintMode, department, complaintGroup, officerName, officerMobileNumber, currentAgentId);
			    		
			    		KeyHolder keyHolder = new GeneratedKeyHolder();

		    			 jdbcTemplate.update(connection -> {
		    			     PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
		    			     ps.setInt(1, qaqc_id);
		    			     ps.setString(2, status);
		    			     ps.setString(3, complaintNumber1);
		    			     ps.setString(4, complaintDate);
		    			     ps.setString(5, complanitPersonName);
		    			     ps.setString(6, complaintPersonMobileNum);
		    			     ps.setString(7, complaintType);
		    			     ps.setString(8, complaintMode);
		    			     ps.setString(9, department);
		    			     ps.setString(10, complaintGroup);
		    			     ps.setString(11, officerName);
		    			     ps.setString(12, officerMobileNumber);
		    			     ps.setInt(13, currentAgentId);		    			    
		    			     return ps;
		    			 }, keyHolder);
		    			// Retrieve the generated data_id
		    			 Number generatedDataId = keyHolder.getKey();
		    			 if (generatedDataId != null) {
		    				    long dataId = generatedDataId.longValue();
		    				    
		    				    //System.out.println("dataId if=="+dataId);
		    				    // Use the dataId in the next insert for qaqc_upload_data_history
		    				    String sql = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data_history " +
		    				            "(qaqc_id, call_category, complaint_number, complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id, data_id) " +
		    				            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		    				    jdbcTemplate.update(sql, qaqc_id, status1, complaintNumber1, complaintDate, complanitPersonName, 
		    				                        complaintPersonMobileNum, complaintType, complaintMode, department, 
		    				                        complaintGroup, officerName, officerMobileNumber, currentAgentId, dataId);
		    				    
		    				}
			    	
			    		
		    			 assignedCount++;

	    		}
	    		
	    		} else {
	    			//System.out.println("else complaintNumber = "+complaintNumber1);
	    			String complaintDate2 = (String) details.get(index).get("Complaint Opened Date");
	    			String complanitPersonName2 = (String) details.get(index).get("Complaint Person Name");
	    			String complaintPersonMobileNum2 = (String) details.get(index).get("Complaint Person Number");
	    			String complaintType2 = (String) details.get(index).get("Complaint Type");
	    			String complaintMode2 = (String) details.get(index).get("Complaint Mode");
	    			String department2 = (String) details.get(index).get("Department");
	    			String complaintGroup2 = (String) details.get(index).get("Complaint Group");
	    			String officerName2 = (String) details.get(index).get("Assign To");
	    			String officerMobileNumber2 = (String) details.get(index).get("Official Contact Number");
	    			String status2 = status.toUpperCase();
		    		 
		    		// Insert into the database
			    		String insertQuery = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data (qaqc_id, call_category, complaint_number,complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			    		//jdbcTemplate.update(insertQuery, qaqc_id, status2, complaintNumber1,complaintDate2, complanitPersonName2, complaintPersonMobileNum2, complaintType2, complaintMode2, department2, complaintGroup2, officerName2, officerMobileNumber2, currentAgentId);
			    		KeyHolder keyHolder = new GeneratedKeyHolder();
			    		jdbcTemplate.update(connection -> {
			    		    PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			    		    ps.setInt(1, qaqc_id);
			    		    ps.setString(2, status2);
			    		    ps.setString(3, complaintNumber1);
			    		    ps.setString(4, complaintDate2);
			    		    ps.setString(5, complanitPersonName2);
			    		    ps.setString(6, complaintPersonMobileNum2);
			    		    ps.setString(7, complaintType2);
			    		    ps.setString(8, complaintMode2);
			    		    ps.setString(9, department2);
			    		    ps.setString(10, complaintGroup2);
			    		    ps.setString(11, officerName2);
			    		    ps.setString(12, officerMobileNumber2);
			    		    ps.setInt(13, currentAgentId);
			    		    return ps;
			    		}, keyHolder);
			    		Number generatedDataId = keyHolder.getKey();
			    		
			    		if (generatedDataId != null) {
			    		    long dataId2 = generatedDataId.longValue();
			    		    //System.out.println("dataId2 else=="+dataId2);
			    		    // Use the dataId in the next insert for qaqc_upload_data_history
			    		    String sql = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data_history " +
			    		            "(qaqc_id, call_category, complaint_number, complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id, data_id) " +
			    		            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			    		    jdbcTemplate.update(sql, qaqc_id, status2, complaintNumber1, complaintDate2, complanitPersonName2, 
			    		                        complaintPersonMobileNum2, complaintType2, complaintMode2, department2, 
			    		                        complaintGroup2, officerName2, officerMobileNumber2, currentAgentId, dataId2);
			    		}
			    		
				            
			    		assignedCount++;
	    		}
	    	}
	    	
	    	countResult.put("assignedCount", assignedCount);
    		countResult.put("qaqc_id", qaqc_id);
    		countResult.put("agent_id", currentAgentId);
    		countDetails.add(countResult);
	    }
	    	for(Map<String, Object> assgn : assignedIDList) {
	    		
	    		for(Map<String, Object> count : countDetails) {
	    			
	    			if(((int)assgn.get("agent_id"))==((int)count.get("agent_id"))) {
	    				int assignedid = (int)assgn.get("assigned_id");	
	    				int agentid = (int)assgn.get("agent_id");	
	    				int assignedCount = (int)count.get("assignedCount");	
	    			String sql = "UPDATE agent_assigned SET data_count = ? WHERE assigned_id = ? and agent_id = ?";
	    			jdbcTemplate.update(sql, assignedCount, assignedid, agentid);
	    			}
	    		}
	    	}

	        return true;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	 }
	*/
	
	@Transactional
	public boolean processAndSaveDetails(String url, int qaqc_id, List<Map<String, Object>> agents, String status,
			List<Map<String, Object>> assignedIDList, String mode) {
		try {

			RestTemplate restTemplate = new RestTemplate();

			// Fetch response as String
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			String rawResponse = response.getBody();

			// Preprocess response to remove invalid characters if needed
			if (rawResponse != null) {
//				rawResponse = rawResponse.replaceAll("\"\"", "\""); // Replace double-double quotes
//				rawResponse = rawResponse.replaceAll("(?<![,{])\"R", "\"R"); // Example: Fix missing commas

			}

			// Log raw response
			// System.out.println("Raw Response: " + rawResponse);

			// Parse JSON
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> responseMap = objectMapper.readValue(rawResponse, Map.class);

			// Target the specific field where double-double quotes might appear
			String targetField = "Address"; // Replace with the actual field name
			if (responseMap.containsKey(targetField)) {
				String fieldValue = (String) responseMap.get(targetField);
				if (fieldValue != null && fieldValue.contains("\"\"")) {
					// Clean double-double quotes in the field value
					fieldValue = fieldValue.replaceAll("\"\"", "\"");
					responseMap.put(targetField, fieldValue); // Update the field with cleaned value
				}
			}

			
			// Extract "Details" array from the response
			List<Map<String, Object>> details = (List<Map<String, Object>>) responseMap.get("Details");
			/*
			// System.out.println(details);
			if (details == null) {
				System.err.println("No 'Details' found in the response.");
				return false; // Or handle as per your requirement
			}
			
			if (mode.isEmpty() || mode.isBlank() || mode == null) {
				details = details.stream().filter(map -> !map.get("Complaint Mode").equals("SOCIAL MEDIA"))
						.collect(Collectors.toList());
			}

			// Apply filtering based on the status value
			if ("withdraw".equalsIgnoreCase(status)) {
				// Keep only records where "Current Status" is "Completed"
				//details = details.stream().filter(entry -> "Completed".equals(entry.get("Current Status"))).toList(); 
				details = details.stream()
					    .filter(entry -> "Completed".equals(entry.get("Current Status")))
					    .collect(Collectors.toList());
			} else {
				// Remove records where "Current Status" is "Completed"
				//details = details.stream().filter(entry -> !"Completed".equals(entry.get("Current Status"))).toList();
				details = details.stream()
					    .filter(entry -> !"Completed".equals(entry.get("Current Status")))
					    .collect(Collectors.toList());
			}
			*/
			
			// Filter out SOCIAL MEDIA if mode is empty, blank, or null
			if (mode == null || mode.isEmpty() || mode.trim().isEmpty()) {
			    List<Map<String, Object>> filteredDetails = new ArrayList<Map<String, Object>>();
			    for (Map<String, Object> map : details) {
			        Object complaintMode = map.get("Complaint Mode");
			        if (complaintMode == null || !"SOCIAL MEDIA".equals(complaintMode.toString())) {
			            filteredDetails.add(map);
			        }
			    }
			    details = filteredDetails;
			}

			// Apply filtering based on the status value
			if ("withdraw".equalsIgnoreCase(status)) {
			    // Keep only records where "Current Status" is "Completed"
			    List<Map<String, Object>> filteredDetails = new ArrayList<Map<String, Object>>();
			    for (Map<String, Object> entry : details) {
			        Object currentStatus = entry.get("Current Status");
			        if ("Completed".equals(currentStatus != null ? currentStatus.toString() : null)) {
			            filteredDetails.add(entry);
			        }
			    }
			    details = filteredDetails;
			} else {
			    // Remove records where "Current Status" is "Completed"
			    List<Map<String, Object>> filteredDetails = new ArrayList<Map<String, Object>>();
			    for (Map<String, Object> entry : details) {
			        Object currentStatus = entry.get("Current Status");
			        if (!"Completed".equals(currentStatus != null ? currentStatus.toString() : null)) {
			            filteredDetails.add(entry);
			        }
			    }
			    details = filteredDetails;
			}
			int index = 0;

			List<Map<String, Object>> followupData = getFollowupData();
			System.out.println("followupData=" + followupData);

			List<Map<String, Object>> countDetails = new ArrayList<>();

			for (Map<String, Object> agent : agents) {
				int remainingDataCountForAgent = Integer.parseInt(agent.get("callsPerAgent").toString());
				// System.out.println(remainingDataCountForAgent);
				int currentAgentId = Integer.parseInt(agent.get("agentId").toString());
				// System.out.println(agent.get("agentName").toString());

				Map<String, Object> countResult = new HashMap<>();
				int assignedCount = 0;

				for (int i = 0; i < remainingDataCountForAgent && index < details.size(); i++, index++) {

					//System.out.println("CHECK = " + index + " " + details.get(index).get("Complaint Mode"));
					// System.out.println(i +" "+index);

					String complaintNumber1 = (String) details.get(index).get("Complaint Number");
					// System.out.println("complaintNumber = "+complaintNumber1);
/*
					Map<String, Object> result = followupData.stream()
							.filter(map -> complaintNumber1.equals(map.get("complaint_number"))).findFirst()
							.orElse(null);
*/
					Map<String, Object> result = null;

					if (followupData != null) {
					    for (Map<String, Object> map : followupData) {
					        Object complaintNumberValue = map.get("complaint_number");
					        if (complaintNumber1 != null && complaintNumber1.equals(complaintNumberValue != null ? complaintNumberValue.toString() : null)) {
					            result = map;
					            break; // Stop at the first match
					        }
					    }
					}
					
					String compnum = "";
					String callstatus = "";
					String callcat = "";
					Integer isactive_id = 0;
					Date calldate = null;
					if (result != null) {
						compnum = (String) result.get("complaint_number");
						callstatus = (String) result.get("call_status");
						calldate = (Date) result.get("remainder_date");
						callcat = (String) result.get("call_category");						
						isactive_id = (Integer) result.get("id");
						
					}

					if (!compnum.equals("") && compnum.equals(complaintNumber1)) {
						//System.out.println("1 if complaintNumber = " + complaintNumber1);
						Date currentDate = new Date();

						if (status.equalsIgnoreCase("CLOSED") || status.equalsIgnoreCase("PUBLICFOLLOWUP")) {

							if ((callcat.equalsIgnoreCase("CLOSED") || callcat.equalsIgnoreCase("PUBLICFOLLOWUP"))
									&& calldate.before(currentDate)) {							

								//System.out.println("2 if complaintNumber = " + complaintNumber1);
								String complaintDate = (String) details.get(index).get("Complaint Opened Date");
								String complanitPersonName = (String) details.get(index).get("Complaint Person Name");
								String complaintPersonMobileNum = (String) details.get(index)
										.get("Complaint Person Number");
								String complaintType = (String) details.get(index).get("Complaint Type");
								String complaintMode = (String) details.get(index).get("Complaint Mode");
								String department = (String) details.get(index).get("Department");
								String complaintGroup = (String) details.get(index).get("Complaint Group");
								String officerName = (String) details.get(index).get("Assign To");
								String officerMobileNumber = (String) details.get(index).get("Official Contact Number");
								String status1 = status.toUpperCase();

								// Insert into the database
								String insertQuery = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data (qaqc_id, call_category, complaint_number,complaint_date, "
										+ "complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, "
										+ "official_mobilenum, agent_id) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
								// jdbcTemplate.update(insertQuery, qaqc_id, status1,
								// complaintNumber1,complaintDate, complanitPersonName,
								// complaintPersonMobileNum, complaintType, complaintMode, department,
								// complaintGroup, officerName, officerMobileNumber, currentAgentId);

								KeyHolder keyHolder = new GeneratedKeyHolder();

								jdbcTemplate.update(connection -> {
									PreparedStatement ps = connection.prepareStatement(insertQuery,
											Statement.RETURN_GENERATED_KEYS);
									ps.setInt(1, qaqc_id);
									ps.setString(2, status);
									ps.setString(3, complaintNumber1);
									ps.setString(4, complaintDate);
									ps.setString(5, complanitPersonName);
									ps.setString(6, complaintPersonMobileNum);
									ps.setString(7, complaintType);
									ps.setString(8, complaintMode);
									ps.setString(9, department);
									ps.setString(10, complaintGroup);
									ps.setString(11, officerName);
									ps.setString(12, officerMobileNumber);
									ps.setInt(13, currentAgentId);
									return ps;
								}, keyHolder);
								// Retrieve the generated data_id
								Number generatedDataId = keyHolder.getKey();
								if (generatedDataId != null) {
									long dataId = generatedDataId.longValue();

									// System.out.println("dataId if=="+dataId);
									// Use the dataId in the next insert for qaqc_upload_data_history
									String sql = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data_history "
											+ "(qaqc_id, call_category, complaint_number, complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id, data_id) "
											+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

									jdbcTemplate.update(sql, qaqc_id, status1, complaintNumber1, complaintDate,
											complanitPersonName, complaintPersonMobileNum, complaintType, complaintMode,
											department, complaintGroup, officerName, officerMobileNumber,
											currentAgentId, dataId);

								}

								assignedCount++;

								String upsql = "UPDATE gcc_1913_qaqc.qaqc_followup_call_logs set isactive=0 where id=?";
								jdbcTemplate.update(upsql, isactive_id);

							}

							if (callcat.equalsIgnoreCase("REDRESSED") || callcat.equalsIgnoreCase("OFFICIALFOLLOWUP")) {

								//System.out.println("3 if complaintNumber = " + complaintNumber1);
								String complaintDate = (String) details.get(index).get("Complaint Opened Date");
								String complanitPersonName = (String) details.get(index).get("Complaint Person Name");
								String complaintPersonMobileNum = (String) details.get(index)
										.get("Complaint Person Number");
								String complaintType = (String) details.get(index).get("Complaint Type");
								String complaintMode = (String) details.get(index).get("Complaint Mode");
								String department = (String) details.get(index).get("Department");
								String complaintGroup = (String) details.get(index).get("Complaint Group");
								String officerName = (String) details.get(index).get("Assign To");
								String officerMobileNumber = (String) details.get(index).get("Official Contact Number");
								String status1 = status.toUpperCase();

								// Insert into the database
								String insertQuery = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data (qaqc_id, call_category, complaint_number,complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
								// jdbcTemplate.update(insertQuery, qaqc_id, status1,
								// complaintNumber1,complaintDate, complanitPersonName,
								// complaintPersonMobileNum, complaintType, complaintMode, department,
								// complaintGroup, officerName, officerMobileNumber, currentAgentId);

								KeyHolder keyHolder = new GeneratedKeyHolder();

								jdbcTemplate.update(connection -> {
									PreparedStatement ps = connection.prepareStatement(insertQuery,
											Statement.RETURN_GENERATED_KEYS);
									ps.setInt(1, qaqc_id);
									ps.setString(2, status);
									ps.setString(3, complaintNumber1);
									ps.setString(4, complaintDate);
									ps.setString(5, complanitPersonName);
									ps.setString(6, complaintPersonMobileNum);
									ps.setString(7, complaintType);
									ps.setString(8, complaintMode);
									ps.setString(9, department);
									ps.setString(10, complaintGroup);
									ps.setString(11, officerName);
									ps.setString(12, officerMobileNumber);
									ps.setInt(13, currentAgentId);
									return ps;
								}, keyHolder);
								// Retrieve the generated data_id
								Number generatedDataId = keyHolder.getKey();
								if (generatedDataId != null) {
									long dataId = generatedDataId.longValue();

									// System.out.println("dataId if=="+dataId);
									// Use the dataId in the next insert for qaqc_upload_data_history
									String sql = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data_history "
											+ "(qaqc_id, call_category, complaint_number, complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id, data_id) "
											+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

									jdbcTemplate.update(sql, qaqc_id, status1, complaintNumber1, complaintDate,
											complanitPersonName, complaintPersonMobileNum, complaintType, complaintMode,
											department, complaintGroup, officerName, officerMobileNumber,
											currentAgentId, dataId);

								}

								assignedCount++;

								String upsql1 = "UPDATE gcc_1913_qaqc.qaqc_followup_call_logs set isactive=0 where id=?";
								jdbcTemplate.update(upsql1, isactive_id);
							}

						}

						else if (status.equalsIgnoreCase("REDRESSED") || status.equalsIgnoreCase("OFFICIALFOLLOWUP")) {

							if ((callcat.equalsIgnoreCase("REDRESSED") || callcat.equalsIgnoreCase("OFFICIALFOLLOWUP"))
									&& calldate.before(currentDate)) {

								//System.out.println("2 if complaintNumber = " + complaintNumber1);
								String complaintDate = (String) details.get(index).get("Complaint Opened Date");
								String complanitPersonName = (String) details.get(index).get("Complaint Person Name");
								String complaintPersonMobileNum = (String) details.get(index)
										.get("Complaint Person Number");
								String complaintType = (String) details.get(index).get("Complaint Type");
								String complaintMode = (String) details.get(index).get("Complaint Mode");
								String department = (String) details.get(index).get("Department");
								String complaintGroup = (String) details.get(index).get("Complaint Group");
								String officerName = (String) details.get(index).get("Assign To");
								String officerMobileNumber = (String) details.get(index).get("Official Contact Number");
								String status1 = status.toUpperCase();

								// Insert into the database
								String insertQuery = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data (qaqc_id, call_category, complaint_number,complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
								// jdbcTemplate.update(insertQuery, qaqc_id, status1,
								// complaintNumber1,complaintDate, complanitPersonName,
								// complaintPersonMobileNum, complaintType, complaintMode, department,
								// complaintGroup, officerName, officerMobileNumber, currentAgentId);

								KeyHolder keyHolder = new GeneratedKeyHolder();

								jdbcTemplate.update(connection -> {
									PreparedStatement ps = connection.prepareStatement(insertQuery,
											Statement.RETURN_GENERATED_KEYS);
									ps.setInt(1, qaqc_id);
									ps.setString(2, status);
									ps.setString(3, complaintNumber1);
									ps.setString(4, complaintDate);
									ps.setString(5, complanitPersonName);
									ps.setString(6, complaintPersonMobileNum);
									ps.setString(7, complaintType);
									ps.setString(8, complaintMode);
									ps.setString(9, department);
									ps.setString(10, complaintGroup);
									ps.setString(11, officerName);
									ps.setString(12, officerMobileNumber);
									ps.setInt(13, currentAgentId);
									return ps;
								}, keyHolder);
								// Retrieve the generated data_id
								Number generatedDataId = keyHolder.getKey();
								if (generatedDataId != null) {
									long dataId = generatedDataId.longValue();

									// System.out.println("dataId if=="+dataId);
									// Use the dataId in the next insert for qaqc_upload_data_history
									String sql = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data_history "
											+ "(qaqc_id, call_category, complaint_number, complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id, data_id) "
											+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

									jdbcTemplate.update(sql, qaqc_id, status1, complaintNumber1, complaintDate,
											complanitPersonName, complaintPersonMobileNum, complaintType, complaintMode,
											department, complaintGroup, officerName, officerMobileNumber,
											currentAgentId, dataId);

								}

								assignedCount++;

								String upsql2 = "UPDATE gcc_1913_qaqc.qaqc_followup_call_logs set isactive=0 where id=?";
								jdbcTemplate.update(upsql2, isactive_id);

							}

						}

					} else {
						//System.out.println("else complaintNumber = " + complaintNumber1);
						String complaintDate2 = (String) details.get(index).get("Complaint Opened Date");
						String complanitPersonName2 = (String) details.get(index).get("Complaint Person Name");
						String complaintPersonMobileNum2 = (String) details.get(index).get("Complaint Person Number");
						String complaintType2 = (String) details.get(index).get("Complaint Type");
						String complaintMode2 = (String) details.get(index).get("Complaint Mode");
						String department2 = (String) details.get(index).get("Department");
						String complaintGroup2 = (String) details.get(index).get("Complaint Group");
						String officerName2 = (String) details.get(index).get("Assign To");
						String officerMobileNumber2 = (String) details.get(index).get("Official Contact Number");
						String status2 = status.toUpperCase();

						// Insert into the database
						String insertQuery = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data (qaqc_id, call_category, complaint_number,complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
						// jdbcTemplate.update(insertQuery, qaqc_id, status2,
						// complaintNumber1,complaintDate2, complanitPersonName2,
						// complaintPersonMobileNum2, complaintType2, complaintMode2, department2,
						// complaintGroup2, officerName2, officerMobileNumber2, currentAgentId);
						KeyHolder keyHolder = new GeneratedKeyHolder();
						jdbcTemplate.update(connection -> {
							PreparedStatement ps = connection.prepareStatement(insertQuery,
									Statement.RETURN_GENERATED_KEYS);
							ps.setInt(1, qaqc_id);
							ps.setString(2, status2);
							ps.setString(3, complaintNumber1);
							ps.setString(4, complaintDate2);
							ps.setString(5, complanitPersonName2);
							ps.setString(6, complaintPersonMobileNum2);
							ps.setString(7, complaintType2);
							ps.setString(8, complaintMode2);
							ps.setString(9, department2);
							ps.setString(10, complaintGroup2);
							ps.setString(11, officerName2);
							ps.setString(12, officerMobileNumber2);
							ps.setInt(13, currentAgentId);
							return ps;
						}, keyHolder);
						Number generatedDataId = keyHolder.getKey();

						if (generatedDataId != null) {
							long dataId2 = generatedDataId.longValue();
							// System.out.println("dataId2 else=="+dataId2);
							// Use the dataId in the next insert for qaqc_upload_data_history
							String sql = "INSERT INTO gcc_1913_qaqc.qaqc_upload_data_history "
									+ "(qaqc_id, call_category, complaint_number, complaint_date, complaint_person_name, complaint_mobilenumber, complaint_type, complaint_mode, department, complaint_group, official_name, official_mobilenum, agent_id, data_id) "
									+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

							jdbcTemplate.update(sql, qaqc_id, status2, complaintNumber1, complaintDate2,
									complanitPersonName2, complaintPersonMobileNum2, complaintType2, complaintMode2,
									department2, complaintGroup2, officerName2, officerMobileNumber2, currentAgentId,
									dataId2);
						}

						assignedCount++;
					}
				}

				countResult.put("assignedCount", assignedCount);
				countResult.put("qaqc_id", qaqc_id);
				countResult.put("agent_id", currentAgentId);
				countDetails.add(countResult);
			}
			for (Map<String, Object> assgn : assignedIDList) {

				for (Map<String, Object> count : countDetails) {

					if (((int) assgn.get("agent_id")) == ((int) count.get("agent_id"))) {
						int assignedid = (int) assgn.get("assigned_id");
						int agentid = (int) assgn.get("agent_id");
						int assignedCount = (int) count.get("assignedCount");
						String sql = "UPDATE agent_assigned SET data_count = ? WHERE assigned_id = ? and agent_id = ?";
						jdbcTemplate.update(sql, assignedCount, assignedid, agentid);
					}
				}
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private List<Map<String, Object>> getFollowupData() {
		
		 //String sqlQuery ="select complaint_number, remainder_date, call_status from qaqc_followup_call_logs";
		 String sqlQuery = "select id, call_category,complaint_number, remainder_date, call_status from qaqc_followup_call_logs where isactive=1";
		  	  
		 return jdbcTemplate.queryForList(sqlQuery); 
	}

	
//	@Transactional
//	public int saveInAgentAssigned(int agent_id, int qaqc_id,int data_counts) {
//	    String insertSql = "INSERT INTO gcc_1913_qaqc.agent_assigned (agent_id, qaqc_id,data_count) VALUES (?, ?,?)";
//	    int rowsInserted = jdbcTemplate.update(insertSql, agent_id, qaqc_id,data_counts);
//
//	    return rowsInserted;
//	}
	
	@Transactional
	public Map<String, Object> saveInAgentAssigned(int agent_id, int qaqc_id, int data_counts) {
	    String insertSql = "INSERT INTO gcc_1913_qaqc.agent_assigned (agent_id, qaqc_id, data_count) VALUES (?, ?, ?)";
	    KeyHolder keyHolder = new GeneratedKeyHolder();

	    int rowsInserted = jdbcTemplate.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
	        ps.setInt(1, agent_id);
	        ps.setInt(2, qaqc_id);
	        ps.setInt(3, data_counts);
	        return ps;
	    }, keyHolder);

	    if (rowsInserted > 0 && keyHolder.getKey() != null) {
	        Map<String, Object> result = new HashMap<>();
	        result.put("assigned_id", keyHolder.getKey().intValue());
	        result.put("agent_id", agent_id);
	        return result;
	    } else {
	        throw new RuntimeException("Failed to insert data into agent_assigned table.");
	    }
	}
	@Transactional
	 public int savetaskid(String taskid,String fromdate,String todate,String description,String status, int closedCount,String group, String type, String mode, String zone,String region) {
		
		// Ensure default values for nullable parameters
	    if (group == null || group.isEmpty()) {
	        group = "ALL";
	    }
	    if (type == null || type.isEmpty()) {
	        type = "ALL";
	    }
	    if (mode == null || mode.isEmpty()) {
	        mode = "ALL";
	    }
	    if (zone == null || zone.isEmpty()) {
	        zone = "ALL";
	    }
	    if (region == null || region.isEmpty()) {
	    	region = "ALL";
	    }
		
	        String sql = "INSERT INTO gcc_1913_qaqc.qaqc_request (taskid,fromdate,todate,description,call_category,total_data,complaint_group,complaint_type,complaint_mode,complaint_zone,complaint_region) " +
	                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	        int rowsInserted = jdbcTemplate.update(sql,taskid,fromdate,todate,description,status,closedCount,group,type,mode,zone,region);
	        return rowsInserted;
	    }
	@Transactional
    public List<Map<String, Object>> getLastTask()
    {
  	  String sqlQuery ="SELECT qaqc_id\r\n"
  	  		+ "FROM gcc_1913_qaqc.qaqc_request\r\n"
  	  		+ "ORDER BY qaqc_id DESC\r\n"
  	  		+ "LIMIT 1";
  	  
  	  return jdbcTemplate.queryForList(sqlQuery); 
    }

	public int deleteUploadDataTable() {
	    String sql = "DELETE FROM gcc_1913_qaqc.qaqc_upload_data WHERE 1=1";
	    int rowsAffected= jdbcTemplate.update(sql);
	    return rowsAffected;
	}
	
}
