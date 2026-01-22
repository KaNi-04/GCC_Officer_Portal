package in.gov.chennaicorporation.gccoffice.vendor.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class createventService {
	
private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlVendingCommitteDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	public StreetVendorService streetVendorService;

//	public boolean isFileExists(String originalFilename) {
//	    String sql = "SELECT COUNT(*) FROM create_event " +
//	                 "WHERE file_url LIKE ?";
//	    String filenamePattern = "%" + originalFilename; // Match filename at the end of the path
//	    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filenamePattern);
//	    return count != null && count > 0;
//	}
	
	public String insertEvent(String eventName, LocalDate eventDate, String fileUrl,String cby) {
	    String sql = "INSERT INTO create_event (event_name, event_date, file_url,cby) VALUES (?, ?, ?,?)";
	    try {
	    	jdbcTemplate.update(sql, eventName, eventDate, fileUrl,cby);
	    	return "success";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();			
		}
	    return "error";
	}
	
	//SELECT * FROM vendingcommitte.create_event
	public List<Map<String, Object>> ViewDropdownEvent() {
	    String sql = "SELECT * FROM create_event WHERE isactive=1 AND isdelete=0";
	    return jdbcTemplate.queryForList(sql);
	}
	
	public List<Map<String, Object>> ViewCommitteeDropdownEvent() {
	    String sql = "SELECT req_id,event_name,event_date,DATE_FORMAT(event_date, '%d-%m-%Y') as formated_date "
	    		+ "FROM create_event  "
	    		+ "WHERE isactive=1 AND isdelete=0";
	    try {
	    	return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
			 return Collections.emptyList();
		}
 
	}

	@Transactional
	public ResponseEntity<Map<String, Object>> saveVendorDetails(Map<String, Object> payload) {

	    Map<String, Object> response = new HashMap<>();

	    int vendor_req_id = Integer.parseInt(payload.get("vendor_req_id").toString());
	    String status = payload.get("status").toString();
	    String remarks = payload.get("remarks") == null ? "-" : payload.get("remarks").toString();
	    int event_req_id = Integer.parseInt(payload.get("event_req_id").toString());
	    String vendor_req_number = payload.get("vendor_req_number").toString();
	    String zone = payload.get("zone").toString();
	    String ward = payload.get("ward").toString();
	    String cby = payload.get("cby").toString();
	    String mob_no = payload.get("mob_no").toString();

	    try {

	        /* ============================================================
	         *  CHECK IF vendor is already inside vendor_postponed_list
	         * ============================================================ */
	        String checkPostponedSQL = "SELECT * FROM vendor_postponed_list WHERE vdid=? AND isactive=1";
	        List<Map<String, Object>> existingPostponed =
	                jdbcTemplate.queryForList(checkPostponedSQL, vendor_req_id);

	        /* ============================================================
	         *  BACKUP METHOD: insert into postponed_history_log
	         * ============================================================ */
	        if (!existingPostponed.isEmpty()) {

	            String backupSQL = "INSERT INTO postponed_history_log "
	                    + "(vdid, event_req_id, status, remarks, cby, cdate, updated_by, updated_date, isactive, isdelete) "
	                    + "SELECT vdid, event_req_id, status, remarks, cby, cdate, updated_by, updated_date, isactive, isdelete "
	                    + "FROM vendor_postponed_list WHERE vdid=?";

	            jdbcTemplate.update(backupSQL, vendor_req_id);
	        }

	        /* ============================================================
	         * CASE 1: POSTPONED
	         * ============================================================ */
	        if ("Postponed".equalsIgnoreCase(status)) {

	            if (existingPostponed.isEmpty()) {

	                // INSERT new postponed entry
	                String insertSQL = "INSERT INTO vendor_postponed_list "
	                        + "(vdid, status, remarks, event_req_id, cby) VALUES (?,?,?,?,?)";

	                jdbcTemplate.update(insertSQL, vendor_req_id, status, remarks, event_req_id, cby);

	            } else {

	                // UPDATE postponed entry
	                String updateSQL = "UPDATE vendor_postponed_list "
	                        + "SET status=?, remarks=?, event_req_id=?, updated_by=?, updated_date=NOW() "
	                        + "WHERE vdid=?";

	                jdbcTemplate.update(updateSQL, status, remarks, event_req_id, cby, vendor_req_id);
	            }

	            response.put("status", "success");
	            response.put("message", "Details saved successfully!");

	            return ResponseEntity.ok(response);
	        }

	        /* ============================================================
	         * CASE 2: APPROVED / REJECTED → Insert into vendor_request_list
	         * ============================================================ */

	        String insertRequestSQL = "INSERT INTO vendor_request_list "
	                + "(vdid, status, remarks, event_req_id, cby) VALUES (?, ?, ?, ?, ?)";

	        KeyHolder keyHolder = new GeneratedKeyHolder();

	        jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(insertRequestSQL, Statement.RETURN_GENERATED_KEYS);
	            ps.setInt(1, vendor_req_id);
	            ps.setString(2, status);
	            ps.setString(3, remarks);
	            ps.setInt(4, event_req_id);
	            ps.setString(5, cby);
	            return ps;
	        }, keyHolder);

	        int generatedId = keyHolder.getKey().intValue();

	        /* ============================================================
	         * Assign UID only for APPROVED
	         * ============================================================ */
	        if ("Approved".equalsIgnoreCase(status)) {
	            String currentYear = java.time.Year.now().toString();
	            String uid_no = zone + "_" + ward + "_" + currentYear + "_" + generatedId;

	            jdbcTemplate.update("UPDATE vendor_request_list SET uid_no=? WHERE id=?", uid_no, generatedId);
	        }

	        /* ============================================================
	         * If POSTPONED ENTRY already exists → update it (after backup)
	         * ============================================================ */
	        if (!existingPostponed.isEmpty()) {

	            String updSQL = "UPDATE vendor_postponed_list SET "
	                    + "status=?, remarks=?, event_req_id=?, updated_by=?, updated_date=NOW(),isactive=0,isdelete=1 "
	                    + "WHERE vdid=?";

	            jdbcTemplate.update(updSQL, status, remarks, event_req_id, cby, vendor_req_id);
	        }

	        /* ============================================================
	         * Send WhatsApp notification
	         * ============================================================ */
	        String result = sendWhatsappMsg(mob_no, status, vendor_req_number);

	        response.put("status", "success");
	        response.put("message", result.equals("200")
	                ? "Details submitted successfully!"
	                : "Details submitted, but WhatsApp msg failed.");

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("status", "Error", "message", "Error saving vendor details: " + e.getMessage()));
	    }
	}



	private String sendWhatsappMsg(String mob_no, String status,String vendor_req_number) {
		String urlString="";
		
		if("Approved".equals(status)) {
		 urlString="https://media.smsgupshup.com/GatewayAPI/rest?userid=2000233507&password=h2YjFNcJ&send_to="+mob_no+"&v=1.1&format=json&msg_type=TEXT&method=SENDMESSAGE&msg=You+are+recognized+as+a+roadside+vendor.+Registration+number%3A+"+vendor_req_number+".&isTemplate=true&footer=GCC+-+IT+Cell";
		}
		else {
			urlString="https://media.smsgupshup.com/GatewayAPI/rest?userid=2000233507&password=h2YjFNcJ&send_to="+mob_no+"&v=1.1&format=json&msg_type=TEXT&method=SENDMESSAGE&msg=Your+application+is+rejected+because+your+roadside+vendor+details+are+incorrect.+%0A%0ARegistration+Number%3A+"+vendor_req_number+".";
		}
	
		String res=hitURL(urlString);
		return res;
	}

	private String hitURL(String urlString) {
		String response = "";
		try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            response = String.valueOf(responseCode);
            System.out.println("Response Code for URL: " + urlString + " is " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
		System.out.println("response="+response);
		return response;
	}

	public List<Map<String, Object>> getCommiteeDetails(String event_req_id) {
		 String sql = "SELECT * "
		 		+ "FROM vendor_request_list "
		 		+ "WHERE event_req_id=? ";
		    return jdbcTemplate.queryForList(sql,event_req_id);
	}

	public List<String> removeSavedVendorDetails() {
		
		String sql="SELECT request_no FROM vendor_request_list";
		
		return jdbcTemplate.queryForList(sql,String.class);
	}

//	@Transactional
//	public ResponseEntity<Map<String, Object>> saveBulkVendorDetails(Map<String, Object> payload) {
//
//	    Map<String, Object> response = new HashMap<>();
//
//	    try {
//
//	        String status = payload.get("status").toString();
//	        String remarks = payload.get("remarks").toString();
//	        int eventReqId = Integer.parseInt(payload.get("event_req_id").toString());
//	        String cby = payload.get("cby").toString();
//
//	        List<Map<String, Object>> vendorList =
//	                (List<Map<String, Object>>) payload.get("vendorList");
//
//	        if (vendorList == null || vendorList.isEmpty()) {
//	            return ResponseEntity.badRequest()
//	                .body(Map.of("status", "Error", "message", "No vendor records provided"));
//	        }
//
//	        String insertSQL = "INSERT INTO vendor_request_list " +
//	                "(vdid, status, remarks, event_req_id, uid_no, cby) VALUES (?, ?, ?, ?, '', ?)";
//
//	        try {
//	            jdbcTemplate.batchUpdate(insertSQL, vendorList, vendorList.size(),
//	                (ps, vendor) -> {
//	                    ps.setInt(1, Integer.parseInt(vendor.get("vendor_req_id").toString()));
//	                    ps.setString(2, status);
//	                    ps.setString(3, remarks);
//	                    ps.setInt(4, eventReqId);
//	                    ps.setString(5, cby);
//	                }
//	            );
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(Map.of("status", "Error", "message", "Batch insert failed: " + e.getMessage()));
//	        }
//
//
//
//	        List<Map<String, Object>> insertedIds;
//	        try {
//	            insertedIds = jdbcTemplate.queryForList(
//	                    "SELECT id, vdid FROM vendor_request_list ORDER BY id DESC LIMIT ?",
//	                    vendorList.size());
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(Map.of("status", "Error", "message", "Failed to fetch inserted IDs: " + e.getMessage()));
//	        }
//
//
//	        List<Object[]> updateParams = new ArrayList<>();
//	        int currentYear = java.time.Year.now().getValue();
//
//	        for (int i = 0; i < vendorList.size(); i++) {
//
//	            Map<String, Object> vendor = vendorList.get(i);
//	            Map<String, Object> inserted = insertedIds.get(i);
//
//	            int insertedId = (int) inserted.get("id");
//	            String zone = vendor.get("zone").toString();
//	            String ward = vendor.get("ward").toString();
//
//	            // Assign UID only if Approved
//	            if ("Approved".equalsIgnoreCase(status)) {
//	                String uidNo = zone + "_" + ward + "_" + currentYear + "_" + insertedId;
//	                updateParams.add(new Object[]{uidNo, insertedId});
//	            }
//
//	            // Send WhatsApp for Approved/Rejected
//	            try {
//	                if ("Approved".equalsIgnoreCase(status) || "Rejected".equalsIgnoreCase(status)) {
//	                    sendWhatsappMsg(
//	                        vendor.get("mob_no").toString(),
//	                        status,
//	                        vendor.get("vendor_req_number").toString()
//	                    );
//	                }
//	            } catch (Exception e) {
//	                // WhatsApp failure should NOT stop DB operation
//	                e.printStackTrace();
//	            }
//	        }
//
//	        if (!updateParams.isEmpty()) {
//	            try {
//	                jdbcTemplate.batchUpdate("UPDATE vendor_request_list SET uid_no=? WHERE id=?", updateParams);
//	            } catch (Exception e) {
//	                e.printStackTrace();
//	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                    .body(Map.of("status", "Error", "message", "Failed to update UID numbers: " + e.getMessage()));
//	            }
//	        }
//
//	        response.put("status", "success");
//	        response.put("message", "Details submitted successfully!");
//
//	        return ResponseEntity.ok(response);
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(Map.of("status", "Error", "message", "Unexpected error: " + e.getMessage()));
//	    }
//	}
	
	@Transactional
	public ResponseEntity<Map<String, Object>> saveBulkVendorDetails(Map<String, Object> payload) {

	    Map<String, Object> response = new HashMap<>();

	    try {
	        String status = payload.get("status").toString();
	        String remarks = payload.get("remarks").toString();
	        int eventReqId = Integer.parseInt(payload.get("event_req_id").toString());
	        String cby = payload.get("cby").toString();

	        List<Map<String, Object>> vendorList =
	                (List<Map<String, Object>>) payload.get("vendorList");

	        if (vendorList == null || vendorList.isEmpty()) {
	            return ResponseEntity.badRequest()
	                    .body(Map.of("status", "Error", "message", "No vendor records provided"));
	        }

	        int currentYear = java.time.Year.now().getValue();

	        /* ============================================================
	         * PROCESS EACH VENDOR ONE BY ONE
	         * ============================================================ */
	        for (Map<String, Object> vendor : vendorList) {

	            int vendorReqId = Integer.parseInt(vendor.get("vendor_req_id").toString());
	            String zone = vendor.get("zone").toString();
	            String ward = vendor.get("ward").toString();
	            String mob_no = vendor.get("mob_no").toString();
	            String vendor_req_number = vendor.get("vendor_req_number").toString();

	            /* ============================================================
	             * STEP 1: Check if vendor already exists in postponed list
	             * ============================================================ */
	            String checkSQL = "SELECT * FROM vendor_postponed_list WHERE vdid=? AND isactive=1";
	            List<Map<String, Object>> existingPostponed =
	                    jdbcTemplate.queryForList(checkSQL, vendorReqId);

	            boolean postponedExists = !existingPostponed.isEmpty();

	            /* ============================================================
	             * STEP 2: BACKUP OLD POSTPONED ROW → postponed_history_log
	             * ============================================================ */
	            if (postponedExists) {
	                String backupSQL = "INSERT INTO postponed_history_log "
	                        + "(vdid, event_req_id, status, remarks, cby, cdate, updated_by, updated_date, isactive, isdelete) "
	                        + "SELECT vdid, event_req_id, status, remarks, cby, cdate, updated_by, updated_date, isactive, isdelete "
	                        + "FROM vendor_postponed_list WHERE vdid=?";

	                jdbcTemplate.update(backupSQL, vendorReqId);
	            }

	            /* ============================================================
	             * CASE 1: STATUS = POSTPONED
	             * ============================================================ */
	            if ("Postponed".equalsIgnoreCase(status)) {

	                if (!postponedExists) {
	                    // INSERT NEW POSTPONED ROW
	                    String insertPostSQL =
	                            "INSERT INTO vendor_postponed_list (vdid, status, remarks, event_req_id, cby) " +
	                                    "VALUES (?, ?, ?, ?, ?)";

	                    jdbcTemplate.update(insertPostSQL, vendorReqId, status, remarks, eventReqId, cby);

	                } else {
	                    // UPDATE EXISTING POSTPONED ROW
	                    String updatePostSQL =
	                            "UPDATE vendor_postponed_list SET status=?, remarks=?, event_req_id=?, updated_by=?, updated_date=NOW() " +
	                                    "WHERE vdid=?";

	                    jdbcTemplate.update(updatePostSQL, status, remarks, eventReqId, cby, vendorReqId);
	                }

	                continue; // SKIP REQUEST LIST INSERTION
	            }

	            /* ============================================================
	             * CASE 2: APPROVED / REJECTED → INSERT INTO vendor_request_list
	             * ============================================================ */

	            KeyHolder keyHolder = new GeneratedKeyHolder();

	            String insertReqSQL =
	                    "INSERT INTO vendor_request_list (vdid, status, remarks, event_req_id, cby) " +
	                            "VALUES (?, ?, ?, ?, ?)";

	            jdbcTemplate.update(connection -> {
	                PreparedStatement ps = connection.prepareStatement(insertReqSQL, Statement.RETURN_GENERATED_KEYS);
	                ps.setInt(1, vendorReqId);
	                ps.setString(2, status);
	                ps.setString(3, remarks);
	                ps.setInt(4, eventReqId);
	                ps.setString(5, cby);
	                return ps;
	            }, keyHolder);

	            int generatedId = keyHolder.getKey().intValue();

	            /* ============================================================
	             * UID GENERATION FOR APPROVED
	             * ============================================================ */
	            if ("Approved".equalsIgnoreCase(status)) {

	                String uidNo = zone + "_" + ward + "_" + currentYear + "_" + generatedId;

	                jdbcTemplate.update(
	                        "UPDATE vendor_request_list SET uid_no=? WHERE id=?",
	                        uidNo, generatedId
	                );
	            }

	            /* ============================================================
	             * UPDATE POSTPONED LIST ALSO IF VENDOR EXISTS (AFTER BACKUP)
	             * ============================================================ */
	            if (postponedExists) {
	                String updatePostSQL =
	                        "UPDATE vendor_postponed_list SET status=?, remarks=?, event_req_id=?, updated_by=?, updated_date=NOW(),isactive=0,isdelete=1 " +
	                                "WHERE vdid=?";

	                jdbcTemplate.update(updatePostSQL, status, remarks, eventReqId, cby, vendorReqId);
	            }

	            /* ============================================================
	             * SEND WHATSAPP (NON-BLOCKING)
	             * ============================================================ */
	            try {
	                if ("Approved".equalsIgnoreCase(status) || "Rejected".equalsIgnoreCase(status)) {
	                    sendWhatsappMsg(mob_no, status, vendor_req_number);
	                }
	            } catch (Exception e) {
	                e.printStackTrace(); // Should not stop DB save
	            }
	        }

	        /* ============================================================
	         * SUCCESS RESPONSE
	         * ============================================================ */
	        response.put("status", "success");
	        response.put("message", "Vendor details submitted successfully!");

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("status", "Error", "message", "Unexpected error: " + e.getMessage()));
	    }
	}




	public List<Map<String, Object>> getsavedids() {
		String sql="SELECT vdid FROM vendor_request_list WHERE isactive=1";
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			
			return Collections.emptyList();
		}
		
	}
	
	public List<Map<String, Object>> getPostponedList() {
	    String sql = "SELECT * FROM vendor_postponed_list WHERE isactive=1 AND isdelete=0";
	    try {
	        return jdbcTemplate.queryForList(sql);
	    } catch (Exception e) {
	        return Collections.emptyList();
	    }
	}

	public List<Map<String, Object>> getdataforreport(String meeting, String status,String cby) {

		StringBuilder sql = new StringBuilder(
		        "SELECT * FROM (" +
		        "   SELECT vdid, status, remarks, uid_no, event_req_id, cdate,cby " +
		        "   FROM vendor_request_list " +
		        "   UNION ALL " +
		        "   SELECT vdid, status, remarks, NULL AS uid_no, event_req_id, cdate,cby " +
		        "   FROM vendor_postponed_list " +
		        ") AS all_requests WHERE 1=1"
		    );


	    List<Object> params = new ArrayList<>();

	    try {

	        // Filter by Meeting ID
	        if (meeting != null && !meeting.trim().isEmpty()) {
	            sql.append(" AND event_req_id = ?");
	            params.add(Integer.parseInt(meeting));
	        }

	        // Filter by status (Approved / Rejected / Postponed)
	        if (status != null && !status.trim().isEmpty()) {
	            sql.append(" AND status = ?");
	            params.add(status);
	        }
	        
	        sql.append(" AND cby = ?");
            params.add(Integer.parseInt(cby));

	        return jdbcTemplate.queryForList(sql.toString(), params.toArray());

	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}


	public List<Map<String, Object>> finalvendorDetailsById(String requestId) {

	    // Base vendor details (1 row)
	    List<Map<String, Object>> org_list = streetVendorService.vendorDetailsById(requestId);

	    if (org_list.isEmpty()) {
	        return Collections.emptyList();
	    }

	    Map<String, Object> base = org_list.get(0);

	    // Get full request history (multiple rows)
	    List<Map<String, Object>> historyList = getvendingfinaldata(requestId);

	    // Merge base vendor details into every history record
	    List<Map<String, Object>> output = new ArrayList<>();

	    for (Map<String, Object> h : historyList) {
	        Map<String, Object> merged = new HashMap<>(base);
	        merged.putAll(h);   // add status/history into same row
	        output.add(merged);
	    }

	    return output;
	}



	private List<Map<String, Object>> getvendingfinaldata(String requestId) {

	    String sql =
	        "SELECT all_logs.vdid, " +
	        "       all_logs.vc_status, " +
	        "       all_logs.vc_remarks, " +
	        "       CONCAT_WS('- ', ce.event_name, DATE_FORMAT(ce.event_date, '%d-%m-%Y')) AS event_name, " +
	        "       DATE_FORMAT(all_logs.cdate, '%d-%m-%Y %l:%i %p') AS vc_Inspection_date " +
	        "FROM (" +

	        "   SELECT vrl.vdid, vrl.status AS vc_status, vrl.remarks AS vc_remarks, " +
	        "          vrl.event_req_id, vrl.cdate " +
	        "   FROM vendor_request_list vrl " +
	        "   WHERE vrl.vdid = ? AND vrl.isactive = 1 " +

	        "   UNION ALL " +

	        "   SELECT vpl.vdid, vpl.status AS vc_status, vpl.remarks AS vc_remarks, " +
	        "          vpl.event_req_id, vpl.cdate " +
	        "   FROM vendor_postponed_list vpl " +
	        "   WHERE vpl.vdid = ? AND vpl.isactive = 1 " +

	        "   UNION ALL " +

	        "   SELECT phl.vdid, phl.status AS vc_status, phl.remarks AS vc_remarks, " +
	        "          phl.event_req_id, phl.cdate " +
	        "   FROM postponed_history_log phl " +
	        "   WHERE phl.vdid = ? " +

	        ") AS all_logs " +

	        "LEFT JOIN create_event ce ON ce.req_id = all_logs.event_req_id " +

	        "ORDER BY all_logs.cdate ASC";   

	    try {
	        return jdbcTemplate.queryForList(sql, requestId, requestId, requestId);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}



	public Map<String, Object> getLatestPostponed(String vdid) {

	    String sql = "SELECT status, remarks, DATE_FORMAT(cdate, '%d-%m-%Y %l:%i %p') AS date " +
	                 "FROM vendor_postponed_list WHERE vdid = ? ORDER BY cdate DESC LIMIT 1";

	    List<Map<String,Object>> list = jdbcTemplate.queryForList(sql, vdid);

	    return list.isEmpty() ? null : list.get(0);
	}
	
	public List<Map<String, Object>> getPostponedHistory(String vdid) {

	    String sql = "SELECT status, remarks, DATE_FORMAT(cdate, '%d-%m-%Y %l:%i %p') AS date " +
	                 "FROM postponed_history_log WHERE vdid = ? ORDER BY cdate DESC";

	    return jdbcTemplate.queryForList(sql, vdid);
	}


	public Map<String, Object> checkVendorInVendingTables(Integer vdid) {

	    Map<String, Object> result = new HashMap<>();

	    // 1. Check postponed table
	    String sqlPostponed = "SELECT COUNT(*) FROM vendor_postponed_list WHERE vdid=? AND isactive=1";
	    Integer postponed = jdbcTemplate.queryForObject(sqlPostponed, Integer.class, vdid);
	    if (postponed != null && postponed > 0) {
	        result.put("status", "POSTPONED");
	        return result;
	    }

	    // 2. Check vendor_request_list (latest status)
	    String sqlRequest = 
	        "SELECT status FROM vendor_request_list WHERE vdid=? AND isactive=1 ORDER BY id DESC LIMIT 1";

	    List<Map<String, Object>> reqList = jdbcTemplate.queryForList(sqlRequest, vdid);
	    if (reqList.isEmpty()) {
	        result.put("status", "NO_EVENT");
	        return result;
	    }

	    String status = reqList.get(0).get("status").toString();
	    result.put("status", status);

	    return result;
	}

	

}
