package in.gov.chennaicorporation.gccoffice.school.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DropdownsService {
	private JdbcTemplate jdbcTemplate;
	
//	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	 
	
	@Autowired
	 public void setDataSourcemysql(@Qualifier("mysqlSchoolDataSource") DataSource dataSource) {
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	        this.namedParameterJdbcTemplate= new NamedParameterJdbcTemplate(dataSource);
	    }

	 @Transactional
		public List<Map<String, Object>> getCourtData() {
			String sql = "SELECT * FROM courts_master ";
			return jdbcTemplate.queryForList(sql);
		}

		@Transactional
		public List<Map<String, Object>> getEquipmentData() {
			String sql = "SELECT * FROM equipment_master ";
			return jdbcTemplate.queryForList(sql);
		}
		
		@Transactional
		public List<Map<String, Object>> getBuildingMaterial() {
			String sql = "SELECT * FROM building_material_master ORDER BY execution_order";
			return jdbcTemplate.queryForList(sql);
		}
		
		@Transactional
		public List<Map<String, Object>> getRoofMaterial() {
			String sql = "SELECT * FROM roof_material_master ORDER BY execution_order";
			return jdbcTemplate.queryForList(sql);
		}
		
		@Transactional
		public List<Map<String, Object>> getWaterSource() {
			String sql = "SELECT * FROM source_water ORDER BY execution_order";
			return jdbcTemplate.queryForList(sql);
		}

		@Transactional
		public List<Map<String, Object>> getWaterStorage() {
			String sql = "SELECT * FROM water_storage_master ORDER BY execution_order";
			return jdbcTemplate.queryForList(sql);
		}
		
		@Transactional
		public List<Map<String, Object>> getRooms() {
			String sql = "SELECT * FROM room_master ORDER BY execution_order";
			return jdbcTemplate.queryForList(sql);
		}

		
		
		
		@Transactional
		public List<Map<String, Object>> getFloorsByBuilding(int buildingId) {
		    String sql = "SELECT * FROM floor_list WHERE building_id = ? and is_active = true and is_delete = false";
		    return jdbcTemplate.queryForList(sql, buildingId);
		}
		
		@Transactional
		public List<Map<String, Object>> getBuildingsBySchoolId(int schoolId) {
		    String sql = "SELECT buildng_id, building_name FROM buildings WHERE school_id = ? and is_active = true and is_delete = false";
		    return jdbcTemplate.queryForList(sql, schoolId);
		}

		// Reuse this for school_id retrieval
		@Transactional
		public String getSchoolId(String user_id) {
		    String sql = "SELECT school_id FROM login_details WHERE user_id = ?";
		    try {
		        return jdbcTemplate.queryForObject(sql, String.class, user_id);
		    } catch (DataAccessException e) {
		        return "error";
		    }
		}

		// New method to fetch UDISE numbers for a given school_id
		public List<Map<String, Object>> getUdiseNumbersBySchoolId(String schoolId) {
		    String sql = "SELECT udise FROM school_list WHERE id IN(" + schoolId + ")";
		    try {
		        return jdbcTemplate.queryForList(sql);
		    } catch (DataAccessException e) {
		        e.printStackTrace();
		        return List.of();
		    }
		}
		
		public List<Map<String, Object>> getCategoryBySchoolId(String schoolId) {
		    String sql = "SELECT category FROM school_list WHERE id IN(" + schoolId + ")";
		    try {
		        return jdbcTemplate.queryForList(sql);
		    } catch (DataAccessException e) {
		        e.printStackTrace();
		        return List.of();
		    }
		}

		
		public List<Map<String, Object>> getZoneBySchoolId(String schoolId) {
		    String sql = "SELECT zone FROM school_list WHERE id IN(" + schoolId + ")";
		    try {
		        return jdbcTemplate.queryForList(sql);
		    } catch (DataAccessException e) {
		        e.printStackTrace();
		        return List.of();
		    }
		}

		public List<Map<String, Object>> getWardBySchoolId(String schoolId) {
		    String sql = "SELECT division FROM school_list WHERE id IN(" + schoolId + ")";
		    try {
		        return jdbcTemplate.queryForList(sql);
		    } catch (DataAccessException e) {
		        e.printStackTrace();
		        return List.of();
		    }
		}

		@Transactional
		public List<Map<String, Object>> getAssetType() {
			String sql = "SELECT * FROM asset_master ORDER BY execution_order";
			return jdbcTemplate.queryForList(sql);
		}
		
		@Transactional
		public List<Map<String, Object>> getSubAssetType(int assetid) {
			String sql = "SELECT * FROM asset_sub_master WHERE asset_master_id = ? ORDER BY execution_order";
			return jdbcTemplate.queryForList(sql, assetid);
		}

		@Transactional
		public List<Map<String, Object>> getSplSubAssetType(List<String> assets, int assetid) {
		    String sql;
		    Map<String, Object> params = new HashMap<>();
		    params.put("assetid", assetid);

		    if (assets == null || assets.isEmpty()) {
		       
		        sql = "SELECT * FROM asset_sub_master WHERE asset_master_id = :assetid ORDER BY execution_order";
		        
		    } else {
		        
		        sql = "SELECT * FROM asset_sub_master WHERE asset_master_id = :assetid AND asset_name NOT IN (:assets) ORDER BY execution_order";
		        params.put("assets", assets);
		    }

		    return namedParameterJdbcTemplate.queryForList(sql, params);
		}
		@Transactional
		public List<String> getAssetList(int school_id, int assetid) {
			String sql ="";
			if(assetid==2) {
		 sql = "SELECT asm.asset_name from electrical_master em \r\n"
					+ "INNER JOIN asset_sub_master asm ON em.sub_asset_id = asm.sub_asset_id\r\n"
					+ "WHERE em.school_id = ?";
			} else if(assetid==3) {
				 sql = "SELECT asm.asset_name from furniture_master fm \r\n"
							+ "INNER JOIN asset_sub_master asm ON fm.sub_asset_id = asm.sub_asset_id\r\n"
							+ "WHERE fm.school_id = ?";
			}
			 return jdbcTemplate.queryForList(sql, String.class, school_id);
		}
		
		@Transactional
		public List<Map<String, Object>> getSpecialCategory(){
			String sql = "SELECT * FROM special_category_master WHERE is_active=1 AND is_delete=0 ORDER BY execution_order";
			return jdbcTemplate.queryForList(sql);
		}

}
