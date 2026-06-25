package in.gov.chennaicorporation.gccoffice.roadwar.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RegisterService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private  Environment environment;
		 	
	@Autowired
	public void setDataSource(@Qualifier("mysqlRoadWarDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
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
	
	
	public List<Map<String, Object>> getRoadNamesByWard(String ward) {
		try {
			String encodedWard = URLEncoder.encode("'" + ward + "'", StandardCharsets.UTF_8.toString());
			String url = "https://gisgcc.chennaicorporation.gov.in/server/rest/services/GCCDepts/EDPMobile2025/FeatureServer/0/query"
					+ "?where=new_ward%3D" + encodedWard
					+ "&outFields=road_id%2Croad_name"
					+ "&returnGeometry=false"
					+ "&f=geojson";

			RestTemplate restTemplate = new RestTemplate();
			String responseBody = restTemplate.getForObject(URI.create(url), String.class);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(responseBody);
			JsonNode features = root.path("features");

			List<Map<String, Object>> roadList = new ArrayList<>();
			if (features.isArray()) {
				for (JsonNode feature : features) {
					JsonNode props = feature.path("properties");
					Map<String, Object> roadInfo = new HashMap<>();
					roadInfo.put("road_id",   props.path("road_id").asText());
					roadInfo.put("road_name", props.path("road_name").asText());
					roadList.add(roadInfo);
				}
			}
			return roadList;

		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	
	
	public List<Map<String, Object>> getRoadType() {
		String sql = "Select id,road_type as roadType from road_type_master where is_active = 1 and is_delete = 0";
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	
	public List<Map<String, Object>> getRoadTypeMaterial() {
		String sql = "Select id,roadtype_material from roadtype_material_master where is_active = 1 and is_delete = 0";
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public List<Map<String, Object>> getFootpathType() {
		String sql = "Select id,footpath_type_name from footpath_type_master where is_active = 1 and is_delete = 0";
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	
	public List<Map<String, Object>> getMonthList() {
		String sql = "SELECT id, month_name FROM month_master WHERE is_active = 1 AND is_delete = 0 ORDER BY id";
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	
	public List<Map<String, Object>> getYearList() {
		String sql = "SELECT id, year_name FROM year_master WHERE is_active = 1 AND is_delete = 0 ORDER BY id DESC";
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	
	public Map<String, Object> saveRoadWarDetails(Map<String, Object> payload) {
		Map<String, Object> result = new HashMap<>();
		try {

			String insertRoad = "INSERT INTO road_details_master "
					+ "(zone, ward, road_name, road_type,  road_length, road_avg_width, road_area, "
					+ " footpath_material, footpath_length, footpath_width, footpath_from_location, footpath_to_location, "
					+ " is_swd, swd_from, swd_to, swd_length, swd_size, "
					+ " is_scp, scp_count, is_manhole, manhole_count, manhole_location, "
					+ " is_cpipe, cpipe_count, is_rwh, rwh_count, "
					+ " sewer_length, sewer_size, "
					+ " is_electric_poles, ep_count, is_hml, culvert_bridge_count, details, "
					+ " is_busshelter, busshelter_count, is_centermedian, erp_asset_code, cby,road_id,road_type_material,road_min_width,road_max_width, "
					+ " hml_count,is_streetlight_poles,streetlight_poles_count,is_mml,mml_count,road_from,road_to,is_footpath,footpath_at) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			KeyHolder keyHolder = new GeneratedKeyHolder();

			jdbcTemplate.update((PreparedStatementCreator) con -> {
				var ps = con.prepareStatement(insertRoad, new String[] { "id" });
				ps.setObject(1, payload.get("zone"));
				ps.setObject(2, payload.get("ward"));
				ps.setObject(3, payload.get("road_name"));
				ps.setObject(4, payload.get("road_type"));
//				ps.setObject(5, payload.get("highway_ans"));
				ps.setObject(5, payload.get("road_length"));
				ps.setObject(6, payload.get("road_avg_width"));
				ps.setObject(7, payload.get("road_area"));
				ps.setObject(8, payload.get("footpath_material"));
				ps.setObject(9, payload.get("footpath_length"));
				ps.setObject(10, payload.get("footpath_width"));
				ps.setObject(11, payload.get("footpath_from_location"));
				ps.setObject(12, payload.get("footpath_to_location"));
				ps.setObject(13, payload.get("is_swd"));
				ps.setObject(14, payload.get("swd_from"));
				ps.setObject(15, payload.get("swd_to"));
				ps.setObject(16, payload.get("swd_length"));
				ps.setObject(17, payload.get("swd_size"));
				ps.setObject(18, payload.get("is_scp"));
				ps.setObject(19, payload.get("scp_count"));
				ps.setObject(20, payload.get("is_manhole"));
				ps.setObject(21, payload.get("manhole_count"));
				ps.setObject(22, payload.get("manhole_location"));
				ps.setObject(23, payload.get("is_cpipe"));
				ps.setObject(24, payload.get("cpipe_count"));
				ps.setObject(25, payload.get("is_rwh"));
				ps.setObject(26, payload.get("rwh_count"));
				ps.setObject(27, payload.get("sewer_length"));
				ps.setObject(28, payload.get("sewer_size"));
				ps.setObject(29, payload.get("is_electric_poles"));
				ps.setObject(30, payload.get("ep_count"));
				ps.setObject(31, payload.get("is_hml"));
				ps.setObject(32, payload.get("culvert_bridge_count"));
				ps.setObject(33, payload.get("details"));
				ps.setObject(34, payload.get("is_busshelter"));
				ps.setObject(35, payload.get("busshelter_count"));
				ps.setObject(36, payload.get("is_centermedian"));
				ps.setObject(37, payload.get("erp_asset_code"));
				ps.setObject(38, payload.get("cby"));
				ps.setObject(39, payload.get("road_id"));
				ps.setObject(40, payload.get("road_type_material"));
				ps.setObject(41, payload.get("road_min_width"));
				ps.setObject(42, payload.get("road_max_width"));
				ps.setObject(43, payload.get("hml_count"));
				ps.setObject(44, payload.get("is_streetlight_poles"));
				ps.setObject(45, payload.get("streetlight_poles_count"));
				ps.setObject(46, payload.get("is_mml"));
				ps.setObject(47, payload.get("mml_count"));
				ps.setObject(48, payload.get("roadfrom"));
				ps.setObject(49, payload.get("roadto"));
				ps.setObject(50, payload.get("is_footpath"));
				ps.setObject(51, payload.get("footpathat"));
				return ps;
			}, keyHolder);

			long generatedId = keyHolder.getKey().longValue();
			String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMYYYY"));
			String refId = datePrefix + generatedId;

			jdbcTemplate.update(
					"UPDATE road_details_master SET ref_id = ? WHERE id = ?",
					refId, generatedId);

			List<Map<String, Object>> relayingList = (List<Map<String, Object>>) payload.get("relaying_details");

			if (relayingList != null && !relayingList.isEmpty()) {
				String insertRelay = "INSERT INTO relaying_details "
						+ "(road_ref_id, relay_month, relay_year,road_type_material ,cr_value, road_type, remarks, cby) "
						+ "VALUES (?,?,?,?,?,?,?,?)";

				for (Map<String, Object> relay : relayingList) {
					jdbcTemplate.update(insertRelay,
							refId,
							relay.get("relay_month"),
							relay.get("relay_year"),
							relay.get("road_type_material"),
							relay.get("cr_value"),
							relay.get("road_type"),
							relay.get("remarks"),
							payload.get("cby"));
				}
			}

			result.put("status", true);
			result.put("message", "Road war details saved successfully");
			result.put("ref_id", refId);

		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", false);
			result.put("message", "Failed to save road war details: " + e.getMessage());
		}
		return result;
	}
	
	
	
	public List<Map<String, Object>> getAllRoadWarDetails(
			String userid,
	        String zone,
	        String ward,
	        String roadId) {

	    StringBuilder sql = new StringBuilder(" SELECT rdm.*,rtm.road_type as road_type_name,rtmm.roadtype_material as roadtype_material_name,ftm.footpath_type_name as footpath_material_name  FROM road_details_master rdm " +
	    										" LEFT JOIN road_type_master rtm ON rtm.id= rdm.road_type " +
	    										" LEFT JOIN roadtype_material_master rtmm ON rtmm.id= rdm.road_type_material " +
	    										" LEFT JOIN footpath_type_master ftm ON ftm.id= rdm.footpath_material " +
	    										" WHERE 1=1 ");

	    List<Object> params = new ArrayList<>();

	    
	    if (userid != null && !userid.isEmpty()) {
	        sql.append(" AND rdm.cby = ?");
	        params.add(userid);
	    }
	    
	    if (zone != null && !zone.isEmpty()) {
	        sql.append(" AND rdm.zone = ?");
	        params.add(zone);
	    }

	    if (ward != null && !ward.isEmpty()) {
	        sql.append(" AND rdm.ward = ?");
	        params.add(ward);
	    }

	    if (roadId != null && !roadId.isEmpty()) {
	        sql.append(" AND rdm.road_id = ?");
	        params.add(roadId);
	    }

	    sql.append(" AND rdm.is_active=1 AND rdm.is_delete=0 ");

	    sql.append(" ORDER BY rdm.id DESC");

	    return jdbcTemplate.queryForList(sql.toString(), params.toArray());
	}

	
	public Map<String, Object> getRoadWarDetailsById(String refid) {

	    Map<String, Object> result = new HashMap<>();

	    try {

	        String sql =
	                "SELECT rdm.*, rtm.road_type As road_type_name,rtmm.roadtype_material as roadtype_material,ftm.footpath_type_name as footpath_material_name " +
	                "FROM road_details_master rdm " +
	                "LEFT JOIN road_type_master rtm ON rtm.id = rdm.road_type " +
	                "LEFT JOIN roadtype_material_master rtmm ON rtmm.id = rdm.road_type_material " +
	                "LEFT JOIN footpath_type_master ftm ON ftm.id= rdm.footpath_material " +
	                "WHERE rdm.ref_id = ? AND rdm.is_active=1 AND rdm.is_delete=0  ";

	        Map<String, Object> road =
	                jdbcTemplate.queryForMap(sql, refid);

	        result.putAll(road);

	        List<Map<String, Object>> relayingDetails =
	                jdbcTemplate.queryForList(
	                        " SELECT rd.*,mm.month_name,ym.year_name,rtmm.roadtype_material "
	                        + " FROM relaying_details rd "
	                        + " LEFT JOIN month_master mm on mm.id = rd.relay_month "
	                        + " LEFT JOIN year_master ym on ym.id = rd.relay_year "
	                        + " LEFT JOIN roadtype_material_master rtmm on rtmm.id = rd.road_type_material "
	                        + " WHERE rd.road_ref_id = ? AND rd.is_active=1 AND rd.is_delete=0  ",
	                        refid);

	        result.put("relayingDetails", relayingDetails);
	        result.put("status", true);

	    } catch (Exception e) {

	        result.put("status", false);
	        result.put("message", e.getMessage());

	    }

	    return result;
	}
	
	public Map<String, Object> checkRoadExists(Integer roadId) {

	    Map<String, Object> response = new HashMap<>();

	    try {

	        String sql = " SELECT COUNT(*) FROM road_details_master "
	        		
	                   + " WHERE road_id = ? AND is_active=1 AND is_delete = 0 ";

	        Integer count = jdbcTemplate.queryForObject(
	                sql,
	                Integer.class,
	                roadId
	        );

	        response.put("status", true);
	        response.put("exists", count != null && count > 0);

	    } catch (Exception e) {

	        e.printStackTrace();

	        response.put("status", false);
	        response.put("message", "Error while checking road");

	    }

	    return response;
	}
	
	@Transactional
	public Map<String, Object> updateRoadWarDetails(Map<String, Object> payload) {

	    Map<String, Object> result = new HashMap<>();

	    try {

	        String refId = String.valueOf(payload.get("ref_id"));
	        String updatedBy = String.valueOf(payload.get("updated_by"));
	        
	        String roadId = String.valueOf(payload.get("road_id"));

	        Integer count = jdbcTemplate.queryForObject(
	                "SELECT COUNT(*) " +
	                "FROM road_details_master " +
	                "WHERE road_id = ? " +
	                "AND ref_id <> ? " +
	                "AND is_active = 1 " +
	                "AND is_delete = 0",
	                Integer.class,
	                roadId,
	                refId
	        );

	        if (count != null && count > 0) {

	            result.put("status", false);
	            result.put("message",
	                    "Selected road is already mapped to another Road War record");

	            return result;
	        }


	        //COPY CURRENT RECORD TO LOG TABLE

	        String logSql =
	                "INSERT INTO road_details_master_log (" +
	                "id, zone, ward, road_id, road_name, road_type,road_type_material, highway_ans, " +
	                "road_length, road_avg_width,road_min_width,road_max_width, road_area, " +
	                "road_from,road_to,is_footpath,footpath_at, "+
	                "footpath_material, footpath_length, footpath_width, " +
	                "footpath_from_location, footpath_to_location, " +
	                "is_swd, swd_from, swd_to, swd_length, swd_size, " +
	                "is_scp, scp_count, is_manhole, manhole_count, manhole_location, " +
	                "is_cpipe, cpipe_count, is_rwh, rwh_count, " +
	                "sewer_length, sewer_size, is_electric_poles, ep_count, is_hml,hml_count, " +
	                "is_streetlight_poles,streetlight_poles_count,is_mml,mml_count, "+
	                "culvert_bridge_count, details, is_busshelter, busshelter_count, " +
	                "is_centermedian, erp_asset_code, ref_id, cdate, cby, " +
	                "updated_date, updated_by, is_active, is_delete) " +
	                "SELECT " +
	                "id, zone, ward, road_id, road_name, road_type,road_type_material, highway_ans, " +
	                "road_length, road_avg_width,road_min_width,road_max_width, road_area, " +
	                "road_from,road_to,is_footpath,footpath_at, "+
	                "footpath_material, footpath_length, footpath_width, " +
	                "footpath_from_location, footpath_to_location, " +
	                "is_swd, swd_from, swd_to, swd_length, swd_size, " +
	                "is_scp, scp_count, is_manhole, manhole_count, manhole_location, " +
	                "is_cpipe, cpipe_count, is_rwh, rwh_count, " +
	                "sewer_length, sewer_size, is_electric_poles, ep_count, is_hml,hml_count, " +
	                "is_streetlight_poles,streetlight_poles_count,is_mml,mml_count, "+
	                "culvert_bridge_count, details, is_busshelter, busshelter_count, " +
	                "is_centermedian, erp_asset_code, ref_id, cdate, cby, " +
	                "updated_date, updated_by, is_active, is_delete " +
	                "FROM road_details_master " +
	                "WHERE ref_id=?";

	        jdbcTemplate.update(logSql, refId);


	        // 2. IF SWD = NO CLEAR DEPENDENT VALUES


	        String isSwd = (String) payload.get("is_swd");

	        if ("no".equalsIgnoreCase(isSwd)) {

	            payload.put("swd_from", null);
	            payload.put("swd_to", null);
	            payload.put("swd_length", null);
	            payload.put("swd_size", null);

	            payload.put("is_scp", null);
	            payload.put("scp_count", null);

	            payload.put("is_manhole", null);
	            payload.put("manhole_count", null);
	            payload.put("manhole_location", null);

	            payload.put("is_cpipe", null);
	            payload.put("cpipe_count", null);

	            payload.put("is_rwh", null);
	            payload.put("rwh_count", null);
	        }

	        // 3. UPDATE ROAD MASTER

	        String updateSql =
	                "UPDATE road_details_master SET " +
	                "zone=?, " +
	                "ward=?, " +
	                "road_id=?, " +
	                "road_name=?, " +
	                "road_type=?, " +
	                "road_type_material=?, " +
	                "road_length=?, " +
	                "road_avg_width=?, " +
	                "road_min_width=?, " +
	                "road_max_width=?, " +
	                "road_area=?, " +
	                "road_from=?, "+
	                "road_to=?, "+
	                "is_footpath=?, "+
	                "footpath_at=?, "+
	                "footpath_material=?, " +
	                "footpath_length=?, " +
	                "footpath_width=?, " +
	                "footpath_from_location=?, " +
	                "footpath_to_location=?, " +
	                "is_swd=?, " +
	                "swd_from=?, " +
	                "swd_to=?, " +
	                "swd_length=?, " +
	                "swd_size=?, " +
	                "is_scp=?, " +
	                "scp_count=?, " +
	                "is_manhole=?, " +
	                "manhole_count=?, " +
	                "manhole_location=?, " +
	                "is_cpipe=?, " +
	                "cpipe_count=?, " +
	                "is_rwh=?, " +
	                "rwh_count=?, " +
	                "sewer_length=?, " +
	                "sewer_size=?, " +
	                "is_electric_poles=?, " +
	                "ep_count=?, " +
	                "is_hml=?, " +
	                "hml_count=?, " +
	                "is_streetlight_poles=?, " +
	                "streetlight_poles_count=?, " +
	                "is_mml=?, " +
	                "mml_count=?, " +	                
	                "culvert_bridge_count=?, " +
	                "details=?, " +
	                "is_busshelter=?, " +
	                "busshelter_count=?, " +
	                "is_centermedian=?, " +
	                "updated_date=NOW(), " +
	                "updated_by=? " +
	                "WHERE ref_id=?";

	        jdbcTemplate.update(updateSql,

	                payload.get("zone"),
	                payload.get("ward"),
	                payload.get("road_id"),
	                payload.get("road_name"),
	                payload.get("road_type"),
	                payload.get("road_type_material"),

	                payload.get("road_length"),
	                payload.get("road_avg_width"),
	                payload.get("road_min_width"),
	                payload.get("road_max_width"),
	                payload.get("road_area"),
	                payload.get("road_from"),
	                payload.get("road_to"),
	                payload.get("is_footpath"),
	                payload.get("footpath_at"),
	                
	                payload.get("footpath_material"),
	                payload.get("footpath_length"),
	                payload.get("footpath_width"),

	                payload.get("footpath_from_location"),
	                payload.get("footpath_to_location"),

	                payload.get("is_swd"),
	                payload.get("swd_from"),
	                payload.get("swd_to"),
	                payload.get("swd_length"),
	                payload.get("swd_size"),

	                payload.get("is_scp"),
	                payload.get("scp_count"),

	                payload.get("is_manhole"),
	                payload.get("manhole_count"),
	                payload.get("manhole_location"),

	                payload.get("is_cpipe"),
	                payload.get("cpipe_count"),

	                payload.get("is_rwh"),
	                payload.get("rwh_count"),

	                payload.get("sewer_length"),
	                payload.get("sewer_size"),

	                payload.get("is_electric_poles"),
	                payload.get("ep_count"),

	                payload.get("is_hml"),
	                payload.get("hml_count"),
	                payload.get("is_streetlight_poles"),
	                payload.get("streetlight_poles_count"),
	                payload.get("is_mml"),
	                payload.get("mml_count"),

	                payload.get("culvert_bridge_count"),
	                payload.get("details"),

	                payload.get("is_busshelter"),
	                payload.get("busshelter_count"),

	                payload.get("is_centermedian"),

	                updatedBy,
	                refId
	        );

	        // 4. SOFT DELETE OLD RELAYING RECORDS

	        jdbcTemplate.update(
	                "UPDATE relaying_details " +
	                "SET is_active=0, " +
	                "is_delete=1, " +
	                "updated_date=NOW(), " +
	                "updated_by=? " +
	                "WHERE road_ref_id=? " +
	                "AND is_active=1",
	                updatedBy,
	                refId
	        );

	        // 5. INSERT NEW RELAYING RECORDS

	    	@SuppressWarnings("unchecked")
	        List<Map<String, Object>> relays =
	                (List<Map<String, Object>>) payload.get("relaying_details");

	        String insertRelay =
	                "INSERT INTO relaying_details " +
	                "(road_ref_id, relay_month, relay_year,road_type_material, cr_value, road_type, remarks, cby) " +
	                "VALUES (?,?,?,?,?,?,?,?)";

	        if (relays != null) {

	            for (Map<String, Object> relay : relays) {

	                jdbcTemplate.update(
	                        insertRelay,
	                        refId,
	                        relay.get("relay_month"),
	                        relay.get("relay_year"),
	                        relay.get("road_type_material"),
	                        relay.get("cr_value"),
	                        relay.get("road_type"),
	                        relay.get("remarks"),
	                        updatedBy
	                );
	            }
	        }

	        result.put("status", true);
	        result.put("message", "Road details updated successfully");

	    } catch (Exception e) {

	        e.printStackTrace();

	        result.put("status", false);
	        result.put("message", e.getMessage());
	    }

	    return result;
	}


	public Map<String, Object> deleteRoadWarDetails(String refId, String updatedby) {
		 Map<String, Object> result = new HashMap<>();

		    try {

		        int roadUpdated = jdbcTemplate.update(
		                "UPDATE road_details_master " +
		                "SET is_active=0, " +
		                "is_delete=1, " +
		                "updated_by=?, "+
		                "updated_date=NOW() " +
		                "WHERE ref_id=? " +
		                "AND is_active=1",
		                updatedby,refId
		        );

		        jdbcTemplate.update(
		                "UPDATE relaying_details " +
		                "SET is_active=0, " +
		                "is_delete=1, " +
		                "updated_by=?, "+
		                "updated_date=NOW() " +
		                "WHERE road_ref_id=? " +
		                "AND is_active=1",
		                updatedby,refId
		        );

		        if (roadUpdated > 0) {

		            result.put("status", true);
		            result.put("message",
		                    "Road War details deleted successfully");

		        } else {

		            result.put("status", false);
		            result.put("message","Record not found");
		        }

		    } catch (Exception e) {

		        e.printStackTrace();

		        result.put("status", false);
		        result.put("message",
		                "Failed to delete Road War details");
		    }

		    return result;
	}
	
}
