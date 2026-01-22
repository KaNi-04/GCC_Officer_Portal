package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.naming.java.javaURLContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ComplaintService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	 public void setDataSource(@Qualifier("mysql1913QAQCDataSource") DataSource dataSource) {
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	
	private final String baseUrl = "https://erp.chennaicorporation.gov.in/pgr/newmobileservice";

	public String fetchComplaintCategories() {
		RestTemplate restTemplate = new RestTemplate();
		String url = baseUrl + "?serviceId=getComplCategory&ComplaintGroupId=null";
		return restTemplate.getForObject(url, String.class);
	}

	public String fetchComplaintSubTypes(String groupId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = baseUrl + "?serviceId=getComplCategory&ComplaintGroupId=" + groupId;
		return restTemplate.getForObject(url, String.class);
	}

	public List<Map<String, Object>> fetchComplaintDetailsById(String complaintId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = baseUrl + "?serviceId=getComplaintByID&ComplaintId=" + complaintId;
		String response = restTemplate.getForObject(url, String.class);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Map<String, Object>> fetchComplaintDetailsByMobile(String mobileNo) {
		RestTemplate restTemplate = new RestTemplate();
		String url = baseUrl
				+ "?serviceId=getComplaintListWithImg&From_date=01/01/2024&To_date=20/08/2024&jsonResp=Yes&UserType=Public&MobileNo="
				+ mobileNo + "&PageIndex=null&Status=null&repYear=2024";
		String response = restTemplate.getForObject(url, String.class);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int saveInLogs(String mobileNo, String complaintNo,String userId) {
		
		 int agent_id;
		    try {
		        agent_id = Integer.parseInt(userId);
		    } catch (NumberFormatException e) {
		        return 0;
		    }

		    String sql = "INSERT INTO pgr_complaint_reg (user_mobile_num, complaint_number, agent_id) VALUES (?, ?, ?)";
		    return jdbcTemplate.update(sql, mobileNo, complaintNo, agent_id);
	}



}
