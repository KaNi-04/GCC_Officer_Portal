package in.gov.chennaicorporation.gccoffice.greencommittee.service;

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
import org.springframework.stereotype.Service;

@Service
public class MonitoringService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Value("${fileBaseUrl}")
	private String fileBaseUrl;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlGreenCommitteeDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public List<String> getrefnums(String meetingid, String zones) {
		try {

	        StringBuilder sql = new StringBuilder(
	            "   SELECT cd.ref_id " +
	            "   FROM committee_data cd "
	            + " LEFT JOIN reg_details rd ON rd.ref_id=cd.ref_id " +
	            "   WHERE cd.isactive = 1 "
	            
	        );

	        List<Object> params = new ArrayList<>();
	        
	        if (meetingid != null && !meetingid.isEmpty()) {
	            sql.append(" AND cd.meeting_id = ? ");
	            params.add(meetingid);
	        }

	        if (zones != null && !zones.isEmpty()) {
	            sql.append(" AND rd.zone = ? ");
	            params.add(zones);
	        }

	        return jdbcTemplate.queryForList(sql.toString(), params.toArray(), String.class);

	    } catch (Exception e) {

	        System.err.println("Error in getrefums(): " + e.getMessage());
	        e.printStackTrace();
	        return Collections.emptyList();
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
	    
	    
	    //List<Map<String, Object>> committeeList = new ArrayList<>();

	    Map<String, Object> c1 = jdbcTemplate.queryForMap(
		        "SELECT cd.*,DATE_FORMAT(cd.cdate, '%d-%m-%Y %l:%i %p') as c_Cdate,cm.event_name as meeting_name,cm.event_date as meeting_date  "
		        + " FROM committee_data cd "
		        + " LEFT JOIN create_meeting cm ON cm.id=cd.meeting_id "
		        + " WHERE cd.ref_id=? AND cd.isactive=1 ",
		        refId
		    );
	    
	    List<Map<String, Object>> com_complaints = jdbcTemplate.queryForList(
		        "SELECT crm.name AS nature, SUM(ctd.nooftrees) AS count " +
		        "FROM committee_tree_data ctd JOIN committee_recommend_master crm ON crm.id=ctd.committee_action " +
		        "WHERE ctd.ref_id=? GROUP BY crm.name", refId
		    );
	    
	    c1.put("com_complaints",com_complaints );
	    response.put("committee", c1);

	    return response;
	}
	
	
}
