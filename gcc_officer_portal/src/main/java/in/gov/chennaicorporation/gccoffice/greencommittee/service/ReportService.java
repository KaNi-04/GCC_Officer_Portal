package in.gov.chennaicorporation.gccoffice.greencommittee.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	

	@Autowired
	public void setDataSource(@Qualifier("mysqlGreenCommitteeDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public List<String> getrefnums(String meetingid, String zones) {
	    try {

	        StringBuilder sql = new StringBuilder(
	            " SELECT DISTINCT ref_id FROM ( " +

	            "   SELECT cd.ref_id " +
	            "   FROM committee_data cd " +
	            "   LEFT JOIN reg_details rd ON rd.ref_id = cd.ref_id " +
	            "   WHERE cd.isactive = 1 " +

	            "   UNION " +

	            "   SELECT ri.ref_id " +
	            "   FROM reinspection ri " +
	            "   INNER JOIN ( " +
	            "       SELECT ref_id, MAX(cdate) AS max_cdate " +
	            "       FROM reinspection " +
	            "       WHERE isactive = 1 " +
	            "       GROUP BY ref_id " +
	            "   ) latest " +
	            "       ON latest.ref_id = ri.ref_id " +
	            "      AND latest.max_cdate = ri.cdate " +
	            "   LEFT JOIN reg_details rd2 ON rd2.ref_id = ri.ref_id " +
	            "   WHERE ri.isactive = 1 " +

	            " ) t WHERE 1=1 "
	        );

	        List<Object> params = new ArrayList<>();

	        /* Meeting filter */
	        if (meetingid != null && !meetingid.isEmpty()) {
	            sql.append(
	                " AND ref_id IN ( " +
	                "   SELECT ref_id FROM committee_data WHERE meeting_id = ? " +
	                "   UNION " +
	                "   SELECT ref_id FROM reinspection WHERE meeting_id = ? " +
	                " ) "
	            );
	            params.add(meetingid);
	            params.add(meetingid);
	        }

	        /* Zone filter */
	        if (zones != null && !zones.isEmpty()) {
	            sql.append(
	                " AND ref_id IN ( " +
	                "   SELECT ref_id FROM reg_details WHERE zone = ? " +
	                " ) "
	            );
	            params.add(zones);
	        }

	        return jdbcTemplate.queryForList(
	            sql.toString(),
	            params.toArray(),
	            String.class
	        );

	    } catch (Exception e) {
	        System.err.println("Error in getrefnums(): " + e.getMessage());
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}

	
	public Map<String, Object> getlistdata(String ref) {

	    Map<String, Object> regDetails = getRegDetails(ref);

	    List<Map<String, Object>> compDetails = getComplaintDetails(ref);

	    regDetails.put("comp_details", compDetails);
	    
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
	    	    sql, ref, ref
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

	        inspectionList.add(row);
	    }

	    regDetails.put("inspections", inspectionList);
	    
	    
	    String reSql =
	            "SELECT r.id AS reins_id, r.committee_action, r.remarks AS reins_remarks, " +
	            "       DATE_FORMAT(r.cdate, '%d-%m-%Y %l:%i %p') AS reins_date, " +
	            "       cm.event_name, DATE_FORMAT(cm.event_date, '%d-%m-%Y') AS event_date, crm.name as ins_name " +
	            "FROM reinspection r " +
	            "LEFT JOIN create_meeting cm ON cm.id = r.meeting_id " +
	            " LEFT JOIN committee_recommend_master crm ON crm.id=r.committee_action " +
	            "WHERE r.ref_id = ? AND r.isactive = 1 " +
	            "ORDER BY r.id DESC";

	    List<Map<String, Object>> reinspectionList = jdbcTemplate.queryForList(reSql, ref);

	    regDetails.put("reinspections", reinspectionList);
	    
	    Map<String, Object> c1 = null;

	    List<Map<String, Object>> committeeList = jdbcTemplate.queryForList(
	        "SELECT cd.*, DATE_FORMAT(cd.cdate, '%d-%m-%Y %l:%i %p') AS c_Cdate, " +
	        "cm.event_name AS meeting_name, cm.event_date AS meeting_date " +
	        "FROM committee_data cd " +
	        "LEFT JOIN create_meeting cm ON cm.id = cd.meeting_id " +
	        "WHERE cd.ref_id = ? AND cd.isactive = 1",
	        ref
	    );

	    if (!committeeList.isEmpty()) {
	        c1 = committeeList.get(0);

	        List<Map<String, Object>> com_complaints = jdbcTemplate.queryForList(
	            "SELECT crm.name AS nature, SUM(ctd.nooftrees) AS count " +
	            "FROM committee_tree_data ctd " +
	            "JOIN committee_recommend_master crm ON crm.id = ctd.committee_action " +
	            "WHERE ctd.ref_id = ? " +
	            "GROUP BY crm.name",
	            ref
	        );

	        c1.put("com_complaints", com_complaints);
	    }

	    // IMPORTANT: always put committee key (even if null)
	    regDetails.put("committee", c1);


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
}
