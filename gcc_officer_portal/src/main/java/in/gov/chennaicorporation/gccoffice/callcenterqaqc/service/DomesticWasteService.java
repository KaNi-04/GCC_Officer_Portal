package in.gov.chennaicorporation.gccoffice.callcenterqaqc.service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DomesticWasteService {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Value("${fileBaseUrl}")
    private String fileBaseUrl;
	
	@Autowired
	public void setDatasource(@Qualifier("mysqlDomesticWasteDataSource") DataSource datasource) {
		this.jdbcTemplate=new JdbcTemplate(datasource);
	}


	
	public Map<String, Object> saveMultipleWasteRequests(String user_name, String mobile, String address,
            String latitude, String longitude,
            String street_name, String street_id, String remarks,
            String itemsJson,String userId,String zone, String ward) {
		
		
		int agent_id=Integer.parseInt(userId);
		int is_app=3;
		String remarks1;
		if (remarks == null || remarks.trim().isEmpty()) {
		    remarks1 = "-";
		} else {
		    remarks1 = remarks;
		}
		
		try {
			
			 ObjectMapper objectMapper = new ObjectMapper();
			    List<Map<String, Object>> items = objectMapper.readValue(itemsJson, new TypeReference<>() {});

			    String sofaSize = null; // declare before lambda
			    for (Map<String, Object> item : items) {
			        if ("2".equals(item.get("item_id").toString())) { // Sofa id = 2
			            Object sizeObj = item.get("sofa_size"); // match your JS payload key
			            if (sizeObj != null && !sizeObj.toString().isEmpty()) {
			                sofaSize = sizeObj.toString();
			            }
			            break; // found sofa, no need to loop further
			        }
			    }
			    
			    final String finalSofaSize = sofaSize;
			
			
				// Step 1: Insert main request (one time)
				String insertMainSql = "INSERT INTO user_request_table " +
				"(request_id, user_name, mobile, address, latitude, longitude, street_name, street_id, remarks,agent_id,is_app,zone,division,sofa_type) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";
			
				KeyHolder keyHolder = new GeneratedKeyHolder();
				jdbcTemplate.update(con -> {
				PreparedStatement ps = con.prepareStatement(insertMainSql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, "TEMP"); // placeholder
				ps.setString(2, user_name);
				ps.setString(3, mobile);
				ps.setString(4, address);
				ps.setString(5, latitude);
				ps.setString(6, longitude);
				ps.setString(7, street_name);
				ps.setString(8, street_id);
				ps.setString(9, remarks1);
				ps.setInt(10, agent_id);
				ps.setInt(11, is_app);
				ps.setString(12, zone);
				ps.setString(13, ward);
				ps.setString(14, finalSofaSize);
				return ps;
				}, keyHolder);
			
				int generatedId = keyHolder.getKey().intValue();
				String requestId = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + generatedId;
				
				// Update request_id in user_request_table
				jdbcTemplate.update("UPDATE user_request_table SET request_id = ? WHERE id = ?", requestId, generatedId);
				
				
				String insertItemSql = "INSERT INTO items (iteam_id, quantity, req_table_id, requedt_id) VALUES (?, ?, ?, ?)";
			
				for (Map<String, Object> item : items) {
				Object itemIdObj = item.get("item_id");
				Object quantityObj = item.get("quantity");
			
				if (itemIdObj == null || quantityObj == null) {
					continue;
				}
			
				int itemId = Integer.parseInt(itemIdObj.toString());
				int quantity = Integer.parseInt(quantityObj.toString());
			
				jdbcTemplate.update(insertItemSql, itemId, quantity, generatedId, requestId);
			}
		
			return Map.of("status", true,"message", "Request saved successfully","requestId", requestId);
		
		}
		catch (Exception e) {
			e.printStackTrace();
			return Map.of("status", false, "message", "Error: " + e.getMessage());
			}
		}
	
	
	

	public List<Map<String, Object>> getAllComplaintsList(String startDate, String endDate, String mobile, String requestId) {
	    StringBuilder sql = new StringBuilder(
	        "SELECT u.*, sm.status_master AS status_name, DATE(u.cdate) AS created_date " +
	        "FROM user_request_table u " +
	        "JOIN status_master sm ON sm.id = u.status_id " +
	        "WHERE u.is_active = 1 AND u.is_delete = 0 AND sm.is_active = 1 AND sm.is_delete = 0 "
	    );

	    List<Object> params = new ArrayList<>();

	    if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
	        sql.append(" AND DATE(u.cdate) BETWEEN ? AND ? ");
	        params.add(startDate);
	        params.add(endDate);
	    }

	    if (mobile != null && !mobile.isEmpty()) {
	        sql.append(" AND u.mobile = ? ");
	        params.add(mobile);
	    }

	    if (requestId != null && !requestId.isEmpty()) {
	        sql.append(" AND u.request_id = ? ");
	        params.add(requestId);
	    }

	    List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

	    for (Map<String, Object> complaint : result) {
	        Number reqTableId = (Number) complaint.get("id");
	        List<Map<String, Object>> itemList = getItemsByRequestId(reqTableId.intValue());
	        complaint.put("items", itemList);
	    }

	    return result;
	}
	
		

		public List<Map<String, Object>> getpendingcomplaints(String startDate, String endDate, String mobile) {
			
			StringBuilder sql = new StringBuilder("SELECT u.*, sm.status_master AS status_name, DATE(u.cdate) AS created_date"
					+ " FROM user_request_table u "
					+ " JOIN status_master sm ON sm.id = u.status"
					+ " WHERE u.is_active = 1 AND u.is_delete = 0 AND u.status IN (1,3) "					
					);
			
			List<Object> params = new ArrayList<>();

		    if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
		        sql.append(" AND DATE(u.cdate) BETWEEN ? AND ? ");
		        params.add(startDate);
		        params.add(endDate);
		    }
		    
		    if (mobile != null && !mobile.isEmpty()) {
		        sql.append(" AND u.mobile = ? ");
		        params.add(mobile);
		    }
			
		    List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

		    for (Map<String, Object> complaint : result) {
		        Number reqTableId = (Number) complaint.get("id");
		        List<Map<String, Object>> itemList = getItemsByRequestId(reqTableId.intValue());
		        complaint.put("items", itemList);
		    }

		    return result;
		}
		
		public List<Map<String, Object>> getItemsByRequestId(int reqTableId) {
		    String itemSql = "SELECT i.iteam_id, im.item_name, i.quantity " +
		                     "FROM items i " +
		                     "LEFT JOIN item_master im ON i.iteam_id = im.id " +
		                     "WHERE i.is_active = 1 AND i.is_delete = 0 AND i.req_table_id = ?";
		    return jdbcTemplate.queryForList(itemSql, reqTableId);
		}



		public List<Map<String, Object>> getDetailsByReqId(String requestId) {
			
			String sql = "SELECT " +
	                "u.id, u.request_id, u.user_name, u.mobile, u.address, u.latitude, u.longitude, " +
	                "u.zone, u.division, u.street_name, u.quantity AS main_quantity, u.remarks, " +
	                "CONCAT('" + fileBaseUrl + "/gccofficialapp/files', u.image_url) AS image_url, " +
	                "DATE_FORMAT(u.cdate, '%d-%m-%Y') AS cdate, " +
	                "u.is_active, u.is_delete, u.street_id, sm.status_master AS status, u.is_app, " +
	                "DATE_FORMAT(u.close_date, '%d-%m-%Y') AS close_date, " +
	                "CONCAT('" + fileBaseUrl + "/gccofficialapp/files', u.officer_upload_url) AS officer_upload_url, " +
	                "itm.iteam_id, itm.quantity, im.item_name " +
	                "FROM user_request_table u " +
	                "LEFT JOIN items itm ON u.id = itm.req_table_id " +
	                "LEFT JOIN item_master im ON itm.iteam_id = im.id AND im.is_active = 1 " +
	                "LEFT JOIN status_master sm ON sm.id = u.status AND sm.is_active = 1 AND sm.is_delete = 0 " +
	                "WHERE u.request_id = ? AND u.is_active = 1";

	        List<Map<String, Object>> rawList = jdbcTemplate.queryForList(sql, requestId);

	        Map<Integer, Map<String, Object>> groupedMap = new LinkedHashMap<>();

	        for (Map<String, Object> row : rawList) {
	            Integer id = (Integer) row.get("id");

	            Map<String, Object> item = null;
	            if (row.get("item_name") != null) {
	                item = new LinkedHashMap<>();
	                item.put("item_name", row.get("item_name"));
	                item.put("quantity", row.get("quantity"));
	            }

	            if (groupedMap.containsKey(id)) {
	                if (item != null) {
	                    ((List<Map<String, Object>>) groupedMap.get(id).get("items")).add(item);
	                }
	            } else {
	                Map<String, Object> newEntry = new LinkedHashMap<>();
	                newEntry.put("id", id);
	                newEntry.put("request_id", row.get("request_id"));
	                newEntry.put("user_name", row.get("user_name"));
	                newEntry.put("mobile", row.get("mobile"));
	                newEntry.put("address", row.get("address"));
	                newEntry.put("latitude", row.get("latitude"));
	                newEntry.put("longitude", row.get("longitude"));
	                newEntry.put("zone", row.get("zone"));
	                newEntry.put("division", row.get("division"));
	                newEntry.put("street_name", row.get("street_name"));
	                newEntry.put("main_quantity", row.get("main_quantity"));
	                newEntry.put("remarks", row.get("remarks"));
	                newEntry.put("image_url", row.get("image_url"));
	                newEntry.put("cdate", row.get("cdate"));
	                newEntry.put("is_active", row.get("is_active"));
	                newEntry.put("is_delete", row.get("is_delete"));
	                newEntry.put("street_id", row.get("street_id"));
	                newEntry.put("status", row.get("status")); // Now this is human-readable
	                newEntry.put("is_app", row.get("is_app"));
	                newEntry.put("close_date", row.get("close_date"));
	                newEntry.put("officer_upload_url", row.get("officer_upload_url"));

	                List<Map<String, Object>> items = new ArrayList<>();
	                if (item != null) {
	                    items.add(item);
	                }
	                newEntry.put("items", items);

	                groupedMap.put(id, newEntry);
	            }
	        }

	        return new ArrayList<>(groupedMap.values());
		}



		public boolean changeStatus(String requestId, String remarks, String userId) {
		    String sql = "UPDATE user_request_table " +
		                 "SET status = 5, " +
		                 "officer_remark = ?, " +
		                 "cancelled_by = ?, " +
		                 "rejecttype = 'Without Location', " +
		                 "reject_latitude = 0.0, " +
		                 "reject_longitude = 0.0 " +
		                 "WHERE request_id = ?";

		    int result = jdbcTemplate.update(sql, remarks, userId, requestId);
		    return result > 0;
		}
		
		
		

}
