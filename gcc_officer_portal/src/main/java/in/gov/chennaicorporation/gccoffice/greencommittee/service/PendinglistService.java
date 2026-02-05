package in.gov.chennaicorporation.gccoffice.greencommittee.service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PendinglistService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Value("${fileBaseUrl}")
	private String fileBaseUrl;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlGreenCommitteeDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
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

	public List<Map<String, Object>> ViewZoneDropdown() {
		String sql = "SELECT zone_name "
	    		+ "FROM zone_master "
	    		+ "WHERE is_active=1 AND is_delete=0";
	    try {
	    	return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
			 return Collections.emptyList();
		}
	}
	
	public List<Map<String, Object>> ViewDropdownEvent() {
	    String sql = "SELECT * FROM create_meeting WHERE isactive=1 AND isdelete=0";
	    return jdbcTemplate.queryForList(sql);
	}

//	public List<String> getrefnums(String date, String zones) {
//
//	    try {
//
//	        StringBuilder sql = new StringBuilder(
//	            "SELECT t.ref_id " +
//	            "FROM ( " +
//	            "   SELECT ref_id, inspection_by, MAX(cdate) AS latest_date " +
//	            "   FROM inspection_data " +
//	            "   WHERE DATE(cdate) <= STR_TO_DATE(?, '%d-%m-%Y') " +
//	            "     AND isactive = 1 "
//	        );
//
//	        List<Object> params = new ArrayList<>();
//	        params.add(date);
//
//	        if (zones != null && !zones.isEmpty()) {
//	            sql.append(" AND zone = ? ");
//	            params.add(zones);
//	        }
//
//	        sql.append(
//	            "   GROUP BY ref_id, inspection_by " +
//	            ") AS t " +
//	            "GROUP BY t.ref_id " +
//	            "HAVING COUNT(DISTINCT t.inspection_by) = 3 " +
//	            "ORDER BY MAX(t.latest_date) DESC"
//	        );
//
//	        return jdbcTemplate.queryForList(sql.toString(), params.toArray(), String.class);
//
//	    } catch (Exception e) {
//
//	        System.err.println("Error in getrefums(): " + e.getMessage());
//	        e.printStackTrace();
//	        return Collections.emptyList();
//	    }
//	}
	
	public List<Map<String, Object>> getrefnums(String date, String zones) {

	    try {

	        StringBuilder sql = new StringBuilder(
	            "SELECT ref_id, " +
	            "       MAX(latest_reinspection_id) AS latest_reinspection_id " +
	            "FROM ( " +
	            "   SELECT ref_id, inspection_by, " +
	            "          MAX(cdate) AS latest_date, " +
	            "          MAX(reinspection_id) AS latest_reinspection_id " +
	            "   FROM inspection_data " +
	            "   WHERE DATE(cdate) <= STR_TO_DATE(?, '%d-%m-%Y') " +
	            "     AND isactive = 1 "
	        );

	        List<Object> params = new ArrayList<>();
	        params.add(date);

	        if (zones != null && !zones.isEmpty()) {
	            sql.append(" AND zone = ? ");
	            params.add(zones);
	        }

	        sql.append(
	            "   GROUP BY ref_id, inspection_by " +
	            ") AS x " +
	            "GROUP BY ref_id " +
	            "HAVING COUNT(DISTINCT inspection_by) = 3 " +
	            "ORDER BY MAX(latest_date) DESC"
	        );

	        return jdbcTemplate.queryForList(sql.toString(), params.toArray());

	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}


	
	public Integer getLatestReinspectionId(String refId) {
	    try {
	        String sql = "SELECT id FROM reinspection WHERE ref_id=? ORDER BY id DESC LIMIT 1";
	        return jdbcTemplate.queryForObject(sql, Integer.class, refId);
	    } catch (Exception e) {
	        return null;
	    }
	}



	public Map<String, Object> getlistdata(String ref) {

	    Map<String, Object> regDetails = getRegDetails(ref);

	    List<Map<String, Object>> compDetails = getComplaintDetails(ref);

	    regDetails.put("comp_details", compDetails);

	    return regDetails;
	}
	
	public Map<String, Object> getRegDetails(String ref) {
	    String sql = "SELECT * FROM reg_details WHERE is_active = 1 AND ref_id = ?";
	    return jdbcTemplate.queryForMap(sql, ref);
	}

	public List<Map<String, Object>> getComplaintDetails(String ref) {

	    String sql = "SELECT cn.name AS nature, SUM(cd.no_of_trees) AS total_trees " +
	                 "FROM complaint_details cd " +
	                 "JOIN comp_nature cn ON cn.id = cd.comp_nature_id " +
	                 "WHERE cd.is_active = 1 AND cd.ref_id = ? " +
	                 "GROUP BY cn.name";

	    return jdbcTemplate.queryForList(sql, ref);
	}
	

	public Map<String, Object> getdetailsbyrefid(String refId) {

	    Map<String, Object> response = new HashMap<>();

	    // 1 — Application details
	    Map<String, Object> app = jdbcTemplate.queryForMap(
	        "SELECT *,DATE_FORMAT(cdate, '%d-%m-%Y %l:%i %p') as r_Cdate FROM reg_details WHERE ref_id=? AND is_active=1",
	        refId
	    );

	    // 2 — Complaint details (grouped)
	    List<Map<String, Object>> complaints = jdbcTemplate.queryForList(
	        "SELECT cn.name AS nature, SUM(cd.no_of_trees) AS count " +
	        "FROM complaint_details cd JOIN comp_nature cn ON cn.id=cd.comp_nature_id " +
	        "WHERE cd.ref_id=? GROUP BY cn.name", refId
	    );

	    // 3 — Complaint images
	    List<String> compImages = jdbcTemplate.queryForList(
	            "SELECT img_path FROM img_uploads WHERE ref_id=? AND is_active=1",
	            new Object[]{refId}, String.class
	        ).stream()
	        .map(path -> fileBaseUrl + "/gccofficialapp/files" + path)
	        .collect(Collectors.toList());

	    app.put("complaints", complaints);
	    app.put("images", compImages);
	    response.put("application", app);

	    // 4 — Inspection details
	    List<Map<String, Object>> inspectionList = new ArrayList<>();

	    String sql =
	    	    "SELECT id.*, um.name AS inspector_name, " +
	    	    "       DATE_FORMAT(id.cdate, '%d-%m-%Y %l:%i %p') AS f_Cdate " +
	    	    "FROM inspection_data id " +
	    	    "LEFT JOIN user_maping um ON um.userid = id.cby " +
	    	    "INNER JOIN ( " +
	    	    "    SELECT inspection_by, MAX(cdate) AS latest_date " +
	    	    "    FROM inspection_data " +
	    	    "    WHERE ref_id = ? AND isactive = 1 " +
	    	    "    GROUP BY inspection_by " +
	    	    ") latest ON id.inspection_by = latest.inspection_by " +
	    	    "         AND id.cdate = latest.latest_date " +
	    	    "WHERE id.ref_id = ? AND id.isactive = 1 " +
	    	    "ORDER BY FIELD(id.inspection_by, 'GCC', 'NGO', 'TNFD')";

	    	List<Map<String, Object>> rows = jdbcTemplate.queryForList(
	    	    sql, refId, refId
	    	);
	    	

	    for (Map<String, Object> row : rows) {

	        Integer inspId = (Integer) row.get("inspection_data_id");

	        // 4a — Recommendation details
	        List<Map<String, Object>> recommend = jdbcTemplate.queryForList(
	            "SELECT rm.name, itd.nooftrees AS count " +
	            "FROM inspection_tree_data itd " +
	            "JOIN inspection_recommend_master rm ON rm.rid = itd.action " +
	            "WHERE itd.inspection_data_id=? AND itd.isactive=1",
	            inspId
	        );

	        row.put("recommendations", recommend);

	        // 4b — Images (thumbnail list)
	        List<String> imgs = new ArrayList<>();
	        if (row.get("file_1") != null) {
	            imgs.add(fileBaseUrl + "/gccofficialapp/files" + row.get("file_1").toString());
	        }
	        if (row.get("file_2") != null) {
	            imgs.add(fileBaseUrl + "/gccofficialapp/files" + row.get("file_2").toString());
	        }
	        if (row.get("file_3") != null) {
	            imgs.add(fileBaseUrl + "/gccofficialapp/files" + row.get("file_3").toString());
	        }

	        row.put("images", imgs);

	        inspectionList.add(row);
	    }

	    response.put("inspections", inspectionList);
	    
	 // 5 — Re-Inspection Details
	    String reSql =
	            "SELECT r.id AS reins_id, r.committee_action, r.remarks AS reins_remarks, " +
	            "       DATE_FORMAT(r.cdate, '%d-%m-%Y %l:%i %p') AS reins_date, " +
	            "       cm.event_name, DATE_FORMAT(cm.event_date, '%d-%m-%Y') AS event_date " +
	            "FROM reinspection r " +
	            "LEFT JOIN create_meeting cm ON cm.id = r.meeting_id " +
	            "WHERE r.ref_id = ? AND r.isactive = 1 " +
	            "ORDER BY r.id DESC";

	    List<Map<String, Object>> reinspectionList = jdbcTemplate.queryForList(reSql, refId);

	    response.put("reinspections", reinspectionList);


	    return response;
	}

	public List<Map<String, Object>> getCommitteeActions() {
		String sql = "SELECT id,name "
	    		+ "FROM committee_recommend_master "
	    		+ "WHERE isactive=1 AND isdelete=0 "
	    		+ "ORDER BY orderby";
	    try {
	    	return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
			 return Collections.emptyList();
		}
	}


	public Map<String, Object> saveCommittee(
	        String refId,
	        String remarks,
	        int meetingid,
	        int userId,
	        List<Map<String, Object>> decisions
	) {

	    Map<String, Object> response = new HashMap<>();

	    try {
	        // 1️⃣ Save committee_data and get generated ID
	        int committeeDataId = saveCommitteeData(refId, remarks, meetingid, userId);

	        List<Integer> insertedTreeIds = saveCommitteeTreeData(decisions, committeeDataId, refId, userId);

	        response.put("committee_data_id", committeeDataId);
	        response.put("tree_ids", insertedTreeIds);
	        response.put("status", "success");
	        response.put("message", "Committee decision saved!");

	    } catch (Exception e) {
	        response.put("status", "error");
	        response.put("message", e.getMessage());
	    }

	    return response;
	}
	
	public int saveCommitteeData(String refId, String remarks, int meetingid, int userId) {
		
	    String remarksValue = (remarks == null || remarks.trim().isEmpty()) ? null : remarks.trim();

	    String sql = "INSERT INTO committee_data (ref_id, meeting_id, remarks, cby) VALUES (?, ?, ?, ?)";

	    KeyHolder keyHolder = new GeneratedKeyHolder();

	    jdbcTemplate.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        ps.setString(1, refId);
	        ps.setInt(2, meetingid);
	        if (remarksValue == null) {
	            ps.setNull(3, Types.VARCHAR);
	        } else {
	            ps.setString(3, remarksValue);
	        }
	        ps.setInt(4, userId);
	        return ps;
	    }, keyHolder);

	    return keyHolder.getKey().intValue(); // generated committee_data.id
	}


	public List<Integer> saveCommitteeTreeData(
	        List<Map<String, Object>> decisions,
	        int committeeDataId,
	        String refId,
	        int userId
	) {

	    String sql = "INSERT INTO committee_tree_data (nooftrees, committee_action, committee_data_id, ref_id, cby) " +
	                 "VALUES (?, ?, ?, ?, ?)";

	    List<Integer> insertedIds = new ArrayList<>();

	    for (Map<String, Object> row : decisions) {

	        int actionId = Integer.parseInt(row.get("id").toString());
	        int count = Integer.parseInt(row.get("count").toString());

	        KeyHolder keyHolder = new GeneratedKeyHolder();

	        jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	            ps.setInt(1, count);
	            ps.setInt(2, actionId);
	            ps.setInt(3, committeeDataId);
	            ps.setString(4, refId);
	            ps.setInt(5, userId);
	            return ps;
	        }, keyHolder);

	        insertedIds.add(keyHolder.getKey().intValue());
	    }

	    return insertedIds;
	}

	public List<Map<String, Object>> getvcsavedrefids() {
		String sql="SELECT ref_id FROM committee_data WHERE isactive=1";
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Collections.emptyList();
					
		}
	}
	
	@Transactional
	public Map<String, Object> saveReInspection(String refId, int action, int meetingId,
            String remarks, int userId) {

			Map<String, Object> response = new HashMap<>();

		try {
		
				// Insert into reinspection table
				String sql = "INSERT INTO reinspection (ref_id, committee_action, meeting_id, remarks, cby) " +
				"VALUES (?, ?, ?, ?, ?)";
				
				KeyHolder keyHolder = new GeneratedKeyHolder();
				
				jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, refId);
				ps.setInt(2, action);
				ps.setInt(3, meetingId);
				ps.setString(4, remarks);
				ps.setInt(5, userId);
				return ps;
				}, keyHolder);
				
				int generatedId = keyHolder.getKey().intValue();
		
				// Update reg_details.reinspection_id
				jdbcTemplate.update("UPDATE reg_details SET reinspection_id=? WHERE ref_id=?",generatedId, refId);
		
				jdbcTemplate.update("UPDATE inspection_data SET isactive=0,isdelete=1 WHERE ref_id=?", refId);

				response.put("status", "success");
				response.put("message", "Committee decision saved!");
				response.put("reinspection_id", generatedId);
		
			}
		catch (Exception e)
		{
				response.put("status", "error");
				response.put("message", e.getMessage());
		}
				
		return response;
		
		}



}
