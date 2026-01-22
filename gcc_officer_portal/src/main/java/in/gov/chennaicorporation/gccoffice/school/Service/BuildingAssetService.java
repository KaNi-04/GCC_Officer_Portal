package in.gov.chennaicorporation.gccoffice.school.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class BuildingAssetService {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public void setDataSourcemysql(@Qualifier("mysqlSchoolDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate= new NamedParameterJdbcTemplate(dataSource);
	}

	@Transactional
	public int saveBuildings(String building_name, int total_floor, int building_material, int roof_material,
			 int school_id) {
		String sql = "INSERT INTO buildings (building_name, total_floor, building_material, roof_material, school_id) VALUES (?, ?, ?, ?, ?)";
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, building_name);
				ps.setInt(2, total_floor);
				ps.setInt(3, building_material);
				ps.setInt(4, roof_material);
				ps.setInt(5, school_id);
				return ps;
			}, keyHolder);
			Number generatedDataId = keyHolder.getKey();
			int buildingID = generatedDataId.intValue();
			return buildingID;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return 0;
		}

	}

	@Transactional
	public Map<String, Object> getFloor(int i) {
		String sql = "SELECT * FROM floor_master WHERE execution_order = ? ";
		return jdbcTemplate.queryForMap(sql, i);
	}

	@Transactional
	public void saveFloor(String floor_name, int building_id, int floor_master_id, int school_id) {
		String sql = "INSERT INTO floor_list (floor_name, building_id, floor_master_id, school_id) VALUES (?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, floor_name, building_id, floor_master_id, school_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	
//	@Transactional
//	public List<Map<String, Object>> getFloorDetails(int building_id) {
//		String sql = "SELECT fl.*,rd.room_type\r\n"
//				+ "FROM floor_list fl\r\n"
//				+ "left JOIN room_details rd ON rd.floors_id=fl.floors_id\r\n"
//				+ "WHERE fl.building_id = ? ORDER BY fl.floors_id";
//		System.out.println(sql);
//		return jdbcTemplate.queryForList(sql, building_id);
//		
//		
//	}

	@Transactional
	public List<Map<String, Object>> getFloorDetails(int building_id) {
		String sql = "SELECT fl.floors_id, fl.floor_name, fl.building_id, fl.is_active, fl.is_delete, \r\n"
				+ "                 fl.floor_master_id, fl.status, \r\n"
				+ "                 COALESCE(GROUP_CONCAT( DISTINCT rm.room_type ORDER BY rm.room_master_id SEPARATOR ', '), '') AS room_types \r\n"
				+ "                 FROM floor_list fl \r\n"
				+ "                 LEFT JOIN room_details rd ON rd.floors_id = fl.floors_id \r\n"
				+ "                 LEFT JOIN room_master rm ON rm.room_master_id = rd.room_type \r\n"
				+ "                 WHERE fl.building_id = ? \r\n"
				+ "                 GROUP BY fl.floors_id, fl.floor_name, fl.building_id, fl.is_active, fl.is_delete, \r\n"
				+ "                 fl.floor_master_id, fl.status \r\n" + "                 ORDER BY fl.floors_id";

		return jdbcTemplate.queryForList(sql, building_id);

	}

	public int getLastBuildingOrder(@RequestParam int schoolId) {
		String sql = "SELECT COALESCE(MAX(building_order), 0) FROM buildings WHERE school_id = ?";
		int lastBuildingOrder = jdbcTemplate.queryForObject(sql, Integer.class, schoolId);
		return lastBuildingOrder;
	}

	@Transactional
	public List<Map<String, Object>> getSavedBuildings(int school_id) {
		String sql = "SELECT b.*,bm.content as building_material_name,rm.content as roof_material_name\r\n"
				+ "FROM buildings b\r\n" + "JOIN building_material_master bm ON bm.bm_id=b.building_material\r\n"
				+ "JOIN roof_material_master rm ON rm.rm_id=b.roof_material\r\n"
				+ "WHERE b.school_id = ? and b.is_delete=0 and b.is_active=1  ORDER BY b.buildng_id";
		return jdbcTemplate.queryForList(sql, school_id);
	}
/*
	@Transactional
	public void saveRoomDetails(int room_type, String room_num, int building_id, int floors_id, int school_id) {
		String sql = "INSERT INTO room_details (room_type, room_num, building_id, floors_id, school_id) VALUES (?, ?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, room_type, room_num, building_id, floors_id, school_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}
*/
	@Transactional
	public String roomDetails(int room_type, String room_num, int building_id, int floors_id,
			String anganwadiUniqueNumber, Boolean  anganwadi_check, int school_id) {
		String response = "";
		if(anganwadiUniqueNumber!=null) {
			int is_avbl = checkAnganwadi(school_id, anganwadiUniqueNumber);
			if(is_avbl==0) {
				saveRoomDetails(room_type, room_num, building_id, floors_id,anganwadiUniqueNumber, anganwadi_check, school_id);
				response = "Saved sucessfully";
			} else if(is_avbl==1) {
				response = "Duplicate";
			} else {
				response = "Error";
			}
		} else {
			saveRoomDetails(room_type, room_num, building_id, floors_id,anganwadiUniqueNumber, anganwadi_check, school_id);
			response = "Saved sucessfully";
		}
		return response;
	}
	
	private int checkAnganwadi(int school_id, String anganwadiUniqueNumber) {
	    String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM room_details WHERE school_id = ? and anganwadi_unique_number = ?) THEN 1 ELSE 0 END AS result";
	    try {
	        return  jdbcTemplate.queryForObject(sql, Integer.class, school_id, anganwadiUniqueNumber);
	        
	    } catch (Exception e) {
	        e.printStackTrace(); 
	        return 5;
	    }
	}

	public void saveRoomDetails(int room_type, String room_num, int building_id, int floors_id, String anganwadiUniqueNumber, Boolean  anganwadi_check, int school_id) {
		String sql = "INSERT INTO room_details (room_type, room_num, building_id, floors_id,anganwadi_unique_number, anganwadi_check, school_id) VALUES (?,?, ?, ?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, room_type, room_num, building_id, floors_id,anganwadiUniqueNumber, anganwadi_check, school_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}
	public Map<String, Object> getSimpleBuildingById(int buildingId) {
		String sql = "SELECT b.*, rm.content as roofmaterial ,bm.content as buildingmaterial FROM buildings b join building_material_master bm on\r\n"
				+ " bm.execution_order=b.building_material inner join roof_material_master rm on rm.execution_order=b.roof_material  WHERE buildng_id = ?";
		try {
			return jdbcTemplate.queryForMap(sql, buildingId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Map<String, Object>> getFloorsByBuildingId(int buildingId) {
		String sql = "SELECT fl.floors_id as floors_id,fl.floor_name as floor_name,fm.execution_order as execution_order  FROM floor_list fl left join floor_master fm on fm.floor_master_id=fl.floor_master_id\r\n"
				+ "                     WHERE fl.building_id = ? AND fl.is_active = 1 AND fl.is_delete = 0\r\n"
				+ "                     ORDER BY 1 DESc";

		return jdbcTemplate.queryForList(sql, buildingId);
	}

	@Transactional
	public void updateBuilding(int building_id, String building_name, int total_floor, String building_material,
			String roof_material, int school_id) {
		String sql = "UPDATE buildings SET building_name = ?, total_floor = ?, building_material = ?, roof_material = ?, school_id = ? WHERE buildng_id = ? ";
		try {
			jdbcTemplate.update(sql, building_name, total_floor, building_material, roof_material, school_id,
					building_id);

		} catch (DataAccessException e) {
			e.printStackTrace();

		}

	}

	// add floor
	@Transactional
	public String addFloor(int building_id, int exe_order) {

		try {
			exe_order = exe_order + 1;
			Map<String, Object> data = getfloorName(exe_order);
			String name = (String) data.get("content");
			int id = (int) data.get("floor_master_id");
			addFloordetails(name, building_id, id);
			return "Sucess";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}

	private void addFloordetails(String name, int building_id, int id) {
		String sql = "INSERT INTO floor_list (floor_name, building_id, floor_master_id) VALUES (?, ?, ?)";
		try {
			jdbcTemplate.update(sql, name, building_id, id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private Map<String, Object> getfloorName(int exe_order) {
		String sql = "SELECT * FROM floor_master WHERE execution_order = ?";
		try {
			return jdbcTemplate.queryForMap(sql, exe_order);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	// delete floor
	@Transactional
	public String editBuildings(int building_id, int floor_id) {
		try {
			List<Integer> data = getassetFloorID(floor_id);
			for(Integer it_details_id : data) {
				unmapItAssetMaster(it_details_id);
			}
			editItAsset(floor_id, building_id);
			
			List<Map<String, Object>> electrical_asset= getElectricalAssetMasterByFloorId(floor_id);
			List<Map<String, Object>> furniture_asset= getFurnitureAssetMasterByFloorId(floor_id);
			
			for(Map<String, Object> ele_data : electrical_asset) {
				int school_id = (Integer) ele_data.get("school_id");
				int sub_asset_id = (Integer) ele_data.get("sub_asset_id");
				int asset_qty = (Integer) ele_data.get("asset_qty");
				String assetType="electricalasset";
				Map<String, Object> assetMaster = getAssetMasterBySchlId(sub_asset_id, school_id, assetType);
				int mapped = assetMaster.get("mapped_qty") != null ? (Integer) assetMaster.get("mapped_qty") : 0;
				int unMapped = assetMaster.get("unmapped_qty") != null ? (Integer) assetMaster.get("unmapped_qty") : 0;
				int map = mapped - asset_qty;
				int unMap = unMapped+asset_qty;
				updateAssetmaster(map, unMap, school_id, sub_asset_id, assetType);
			}
			
			editElectricalAsset(floor_id, building_id);
			
			for(Map<String, Object> fur_data : furniture_asset) {
				int school_id = (Integer) fur_data.get("school_id");
				int sub_asset_id = (Integer) fur_data.get("sub_asset_id");
				int asset_qty = (Integer) fur_data.get("asset_qty");
				String assetType="furnitureasset";
				Map<String, Object> assetMaster = getAssetMasterBySchlId(sub_asset_id, school_id, assetType);
				int mapped = assetMaster.get("mapped_qty") != null ? (Integer) assetMaster.get("mapped_qty") : 0;
				int unMapped = assetMaster.get("unmapped_qty") != null ? (Integer) assetMaster.get("unmapped_qty") : 0;
				int map = mapped-asset_qty;
				int unMap = unMapped+asset_qty;
				updateAssetmaster(map, unMap, school_id, sub_asset_id, assetType);
			}
			
			editFurnitureAsset(floor_id, building_id);
			
			editRoom(floor_id, building_id);
	
			editFloor(floor_id, building_id);
		
			return "sucess";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

	}


	public List<Integer> getassetFloorID(int floor_id) {
		List<Integer> assetIds = new ArrayList<>();
		try {
			String sql = "SELECT it_details_id FROM it_mapping WHERE floor_id = ?";
			assetIds = jdbcTemplate.queryForList(sql, Integer.class, floor_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assetIds;
		
	}

	private void editElectricalAsset(int floor_id, int building_id) {
		String sql = "UPDATE electrical_mapping SET is_active = false, is_delete = true WHERE floor_id = ? And building_id=?";
		try {
			jdbcTemplate.update(sql, floor_id, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private void editFurnitureAsset(int floor_id, int building_id) {
		String sql = "UPDATE furniture_mapping SET is_active = false, is_delete = true WHERE floor_id = ? And building_id=?";
		try {
			jdbcTemplate.update(sql, floor_id, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private void editItAsset(int floor_id, int building_id) {
		String sql = "UPDATE it_mapping SET is_active = false, is_delete = true WHERE floor_id = ? And building_id=?";
		try {
			jdbcTemplate.update(sql, floor_id, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private void editRoom(int floor_id, int building_id) {
		String sql = "UPDATE room_details SET is_active = false, is_delete = true WHERE floors_id = ? And building_id=?";
		try {
			jdbcTemplate.update(sql, floor_id, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private void editFloor(int floor_id, int building_id) {
		String sql = "UPDATE floor_list SET is_active = false, is_delete = true WHERE floors_id = ? And building_id=?";
		try {
			jdbcTemplate.update(sql, floor_id, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	@Transactional
	public void unmapAssets(int room_id, boolean is_delete) {
		String sql = "UPDATE it_mapping SET is_delete = ?,is_active=0 WHERE room_id = ?";
		String sql1 = "UPDATE furniture_mapping SET is_delete = ?,is_active=0 WHERE room_id = ?";
		String sql2 = "UPDATE electrical_mapping SET is_delete = ?,is_active=0 WHERE room_id = ?";
		try {
			jdbcTemplate.update(sql, is_delete, room_id);
			jdbcTemplate.update(sql1, is_delete, room_id);
			jdbcTemplate.update(sql2, is_delete, room_id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Transactional
	public void deleteRoom(int room_id, boolean is_delete) {
		String sql = "UPDATE room_details SET is_delete = ?,is_active=0 WHERE room_id = ? ";
		try {
			jdbcTemplate.update(sql, is_delete, room_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	@Transactional
	public String executeDelete(int building_id, boolean is_delete) {
		try {
			deleteItAsset(building_id, is_delete);
			List<Integer> data = getassetBuildingID(building_id);
			for(Integer it_details_id : data) {
				unmapItAssetMaster(it_details_id);
			}
			
			List<Map<String, Object>> electrical_asset= getElectricalAssetMasterByBuildingId(building_id);
			List<Map<String, Object>> furniture_asset= getFurnitureAssetMasterByBuildingId(building_id);
			
			for(Map<String, Object> ele_data : electrical_asset) {
				int school_id = (Integer) ele_data.get("school_id");
				int sub_asset_id = (Integer) ele_data.get("sub_asset_id");
				int asset_qty = (Integer) ele_data.get("asset_qty");
				String assetType="electricalasset";
				Map<String, Object> assetMaster = getAssetMasterBySchlId(sub_asset_id, school_id, assetType);
				int mapped = assetMaster.get("mapped_qty") != null ? (Integer) assetMaster.get("mapped_qty") : 0;
				int unMapped = assetMaster.get("unmapped_qty") != null ? (Integer) assetMaster.get("unmapped_qty") : 0;
				int map = mapped-asset_qty;
				int unMap = unMapped+asset_qty;
				updateAssetmaster(map, unMap, school_id, sub_asset_id, assetType);
			}
			
			deleteElectricalAsset(building_id, is_delete);
			
			for(Map<String, Object> fur_data : furniture_asset) {
				int school_id = (Integer) fur_data.get("school_id");
				int sub_asset_id = (Integer) fur_data.get("sub_asset_id");
				int asset_qty = (Integer) fur_data.get("asset_qty");
				String assetType="furnitureasset";
				Map<String, Object> assetMaster = getAssetMasterBySchlId(sub_asset_id, school_id, assetType);
				int mapped = assetMaster.get("mapped_qty") != null ? (Integer) assetMaster.get("mapped_qty") : 0;
				int unMapped = assetMaster.get("unmapped_qty") != null ? (Integer) assetMaster.get("unmapped_qty") : 0;
				int map = mapped-asset_qty;
				int unMap = unMapped+asset_qty;
				updateAssetmaster(map, unMap, school_id, sub_asset_id, assetType);
			}
			
			deleteFurnitureAsset(building_id, is_delete);
			
			deleteRoomById(building_id, is_delete);
			deleteFloor(building_id, is_delete);
			deleteBuilding(building_id, is_delete);
			return "Building Deleted Sucessfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	public List<Integer> getassetBuildingID(int building_id) {
		List<Integer> assetIds = new ArrayList<>();
		try {
			String sql = "SELECT it_details_id FROM it_mapping WHERE building_id = ?";
			assetIds = jdbcTemplate.queryForList(sql, Integer.class, building_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assetIds;
	}

	private void deleteBuilding(int building_id, boolean is_delete) {
		String sql = "UPDATE buildings SET is_active=false,is_delete = ? WHERE buildng_id = ?;";
		try {
			jdbcTemplate.update(sql, is_delete, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private void deleteFloor(int building_id, boolean is_delete) {
		String sql = "UPDATE floor_list SET is_active=false,is_delete = ? WHERE building_id = ?;";
		try {
			jdbcTemplate.update(sql, is_delete, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private void deleteRoomById(int building_id, boolean is_delete) {
		String sql = "UPDATE room_details SET is_active=false,is_delete = ? WHERE building_id = ?;";
		try {
			jdbcTemplate.update(sql, is_delete, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private void deleteElectricalAsset(int building_id, boolean is_delete) {
		String sql = "UPDATE electrical_mapping SET is_active=false,is_delete = ? WHERE building_id = ?;";
		try {
			jdbcTemplate.update(sql, is_delete, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private void deleteFurnitureAsset(int building_id, boolean is_delete) {
		String sql = "UPDATE furniture_mapping SET is_active=false,is_delete = ? WHERE building_id = ? ;";
		try {
			jdbcTemplate.update(sql, is_delete, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private void deleteItAsset(int building_id, boolean is_delete) {
		String sql = "UPDATE it_mapping SET is_active=false,is_delete = ? WHERE building_id = ?;";
		try {
			jdbcTemplate.update(sql, is_delete, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	public Map<String, List<Map<String, Object>>> getAllMaterials() {
		String buildingMaterialSql = "SELECT execution_order, content FROM building_material_master ORDER BY execution_order";
		String roofMaterialSql = "SELECT execution_order, content FROM roof_material_master ORDER BY execution_order";

		List<Map<String, Object>> buildingMaterials = jdbcTemplate.queryForList(buildingMaterialSql);
		List<Map<String, Object>> roofMaterials = jdbcTemplate.queryForList(roofMaterialSql);

		Map<String, List<Map<String, Object>>> result = new HashMap<>();
		result.put("buildingMaterials", buildingMaterials);
		result.put("roofMaterials", roofMaterials);

		return result;
	}

	@Transactional
	public List<Map<String, Object>> getRoomDetailsBySchoolId(int schoolId) {
		String sql = "SELECT rd.room_id, " + "       rd.room_num AS room_name, " + "       bd.building_name, "
				+ "       rd.building_id, " + "       fl.floor_name AS floor, " + // ✅ FIXED HERE
				"       rm.room_type, " + "       rd.school_id, " + "       rm.room_master_id, "
				+ "       fl.floors_id " + "FROM room_details rd "
				+ "JOIN buildings bd ON bd.buildng_id = rd.building_id "
				+ "JOIN floor_list fl ON fl.floors_id = rd.floors_id " + // ✅ REMOVED floor_master JOIN
				"JOIN room_master rm ON rd.room_type = rm.room_master_id "
				+ "WHERE rd.school_id = ? AND rd.is_active = 1 AND rd.is_delete = 0";

		return jdbcTemplate.queryForList(sql, schoolId);
	}

	@Transactional
	public Map<String, Object> getRoomDetailsById(int roomId) {
		String sql = "SELECT rd.room_id,rd.room_num AS room_name,bd.building_name, "
				+ "fl.floor_name AS floor, rm.room_type, rd.school_id, rm.room_master_id,rd.anganwadi_unique_number, "
				+ "fl.floors_id " + "FROM room_details rd "
				+ "JOIN buildings bd ON bd.buildng_id = rd.building_id "
				+ "JOIN floor_list fl ON fl.floors_id = rd.floors_id " + 
				"JOIN room_master rm ON rd.room_type = rm.room_master_id "
				+ "WHERE rd.room_id = ? AND rd.is_active = 1 AND rd.is_delete = 0";

		return jdbcTemplate.queryForMap(sql, roomId);
	}

	public String editRoomsById(int room_type, String room_num, int building_id, int floors_id, int school_id,
			String anganwadiUniqueNumber, Boolean anganwadi_check,int room_id) {
		
		String response = "";
		if(anganwadiUniqueNumber!=null) {
			int is_avbl = checkAnganwadiEdit(school_id, anganwadiUniqueNumber,room_id);
			if(is_avbl==0) {
				editRoom(room_type, room_num, building_id, floors_id, school_id,anganwadiUniqueNumber,anganwadi_check,room_id);				
				response = "Rooms Edited Sucessfully";
				
			} else if(is_avbl==1) {				
				response = "Duplicate";
				
			} else {				
				response = "Error";
				
			}
		} else {
			editRoom(room_type, room_num, building_id, floors_id, school_id,anganwadiUniqueNumber, anganwadi_check,room_id);
			response = "Rooms Edited Sucessfully";
		}
		return response;
	}
	
	private int checkAnganwadiEdit(int school_id, String anganwadiUniqueNumber,int room_id) {
	    String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM room_details WHERE school_id = ? and anganwadi_unique_number = ? and room_id !=? ) THEN 1 ELSE 0 END AS result";
	    try {
	        return  jdbcTemplate.queryForObject(sql, Integer.class, school_id, anganwadiUniqueNumber,room_id);
	        
	    } catch (Exception e) {
	        e.printStackTrace(); 
	        return 5;
	    }
	}

	@Transactional
	public void editRoom(int room_type, String room_num, int building_id, int floors_id, int school_id,String anganwadiUniqueNumber,Boolean anganwadi_check, int room_id) {
		String sql = "UPDATE room_details SET room_type = ?, room_num = ?, building_id = ?, floors_id = ?, school_id = ?, anganwadi_unique_number=?,anganwadi_check=? WHERE room_id = ? ";
		try {
			jdbcTemplate.update(sql, room_type, room_num, building_id, floors_id, school_id,anganwadiUniqueNumber, anganwadi_check, room_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	/*
	 * @Transactional public void deleteRoom(int room_id, boolean is_delete) {
	 * String sql = "UPDATE room_details SET is_delete = ? WHERE room_id = ? "; try
	 * { jdbcTemplate.update(sql, is_delete, room_id); }catch (DataAccessException
	 * e) { e.printStackTrace(); }
	 * 
	 * }
	 */

	@Transactional
	public List<Map<String, Object>> getItAssetMapping(int roomId, String assetType) {
		String sql = "";
		if (assetType.equalsIgnoreCase("itasset")) {
			sql = "SELECT md.*,m.it_map_id \r\n" + "FROM it_mapping m\r\n"
					+ "JOIN it_master_details md ON m.it_details_id = md.it_details_id\r\n"
					+ "WHERE m.room_id = ? and m.is_active='1' and m.is_delete='0'\r\n";
		} else if (assetType.equalsIgnoreCase("furnitureasset")) {
			sql = "  select sm.asset_name as assetname,fm.* from asset_sub_master sm inner join \r\n"
					+ "  furniture_mapping fm on  sm.sub_asset_id = fm.sub_asset_id  where fm.is_active='1' and fm.is_delete='0' and fm.room_id = ?";
		} else if (assetType.equalsIgnoreCase("electricalasset")) {
			sql = "select sm.asset_name as assetname,em.* from asset_sub_master sm inner join \r\n"
					+ "  electrical_mapping em on  sm.sub_asset_id = em.sub_asset_id  where em.is_active='1' and em.is_delete='0' and em.room_id = ?";
		}

		try {
			return jdbcTemplate.queryForList(sql, roomId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	@Transactional
	public String deleteAssetMapping(int it_map_id, boolean is_delete) {
		String sql = "UPDATE it_mapping SET is_delete = ? WHERE it_map_id = ? ";

		try {
			jdbcTemplate.update(sql, is_delete, it_map_id);
			return "Asset Deleted";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	@Transactional
	public String updateFurturQty(int id, int qty, String assetType) {

		String sql = "";
		if (assetType.equalsIgnoreCase("furnitureasset")) {
			sql = "UPDATE furniture_mapping SET  asset_qty = ? WHERE furniture_map_id = ?";
		} else if (assetType.equalsIgnoreCase("electricalasset")) {
			sql = "UPDATE electrical_mapping SET  asset_qty = ? WHERE electrical_map_id = ?";
		}

		try {
			jdbcTemplate.update(sql, qty, id);
			return "sucess";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	@Transactional
	public List<Map<String, Object>> getSchoolAssetForManagement(int school_id) {
		String sql = "SELECT md.* FROM school_list m INNER JOIN it_master_details md ON m.udise COLLATE utf8mb4_general_ci = md.udise "
				+ "WHERE m.id = ? and md.is_online = '0'";
		try {
			return jdbcTemplate.queryForList(sql, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Transactional
	public List<Map<String, Object>> getSchoolAsset(int school_id, String type) {
		String sql = "SELECT md.* FROM school_list m INNER JOIN it_master_details md ON m.udise COLLATE utf8mb4_general_ci = md.udise\r\n"
				+ "WHERE m.id = ? and md.is_mapped = 0 and md.hardware_type LIKE '%" + type + "%'";
		try {
			return jdbcTemplate.queryForList(sql, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Transactional
	public String addAsset(String assetCode, String name, String source, String serialNum, String vendorDetail,
			String invoice_num, String udise, boolean is_online) {
		String sql = "INSERT INTO it_master_details (asset_name, source_of_asset, serial_no, hardware_type, vendor_name, udise, is_online, invoice_num) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, assetCode, source, serialNum, name, vendorDetail, udise, is_online, invoice_num);
			return "Sucess";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "Error";
		}

	}

	@Transactional
	public Map<String, Object> getAssets(int it_details_id) {

		String sql = "SELECT * FROM it_master_details WHERE it_details_id = ? ";
		return jdbcTemplate.queryForMap(sql, it_details_id);
	}

	@Transactional
	public String editAsset(String assetCode, String name, String source, String serialNum, String vendorDetail,
			String invoice_num, int it_details_id) {
		String sql = "UPDATE it_master_details SET asset_name = ?, source_of_asset = ?, serial_no = ?, hardware_type = ?, vendor_name = ?, invoice_num = ? WHERE it_details_id = ? ";
		try {
			jdbcTemplate.update(sql, assetCode, source, serialNum, name, vendorDetail, invoice_num, it_details_id);
			return "Success";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "Error";
		}
	}

	@Transactional
	public void mapAssets(int room_id, int floor_id, int school_id, int it_details_id, int building_id) {

		saveAssetMapping(room_id, floor_id, school_id, it_details_id, building_id);
		updateMapping(it_details_id);

	}

	private void updateMapping(int it_details_id) {
		String sql = "UPDATE it_master_details SET is_mapped = true WHERE it_details_id = ?";
		try {
			jdbcTemplate.update(sql, it_details_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	private void saveAssetMapping(int room_id, int floor_id, int school_id, int it_details_id, int building_id) {
		String sql = "INSERT INTO it_mapping (room_id, floor_id, school_id, it_details_id, building_id) VALUES (?, ?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, room_id, floor_id, school_id, it_details_id, building_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	@Transactional
	public void addFurAsset(int sub_asset_id, int asset_qty, int room_id, int building_id, int floor_id, int school_id,
			String assetType) {
		String sql = "";
		if (assetType.equalsIgnoreCase("furnitureasset")) {
			sql = "INSERT INTO furniture_mapping (sub_asset_id, asset_qty, room_id,building_id, floor_id, school_id) VALUES (?,?, ?, ?, ?, ?)";
		} else if (assetType.equalsIgnoreCase("electricalasset")) {
			sql = "INSERT INTO electrical_mapping (sub_asset_id, asset_qty, room_id,building_id, floor_id, school_id) VALUES (?,?, ?, ?, ?, ?)";
		}
		try {
			jdbcTemplate.update(sql, sub_asset_id, asset_qty, room_id, building_id, floor_id, school_id);

		} catch (DataAccessException e) {
			e.printStackTrace();

		}
	}
//intial
	// checking for view
	public List<Map<String, Object>> getStudentStrengthBySchoolId(@RequestParam int schoolId) {
	    String sql = "SELECT total_boys, total_girls, others, total_students,special_child " +
	                 "FROM student_strength " +
	                 "WHERE school_id = ? " +
	                 "ORDER BY student_id DESC LIMIT 1";

	    return jdbcTemplate.queryForList(sql, schoolId);
	}

//initial
	/*public List<Map<String, Object>> getotherdetailsBySchoolId(@RequestParam int schoolId) {
		String sql = "select*from school_buildup_area where school_id=?" ;

		return jdbcTemplate.queryForList(sql, schoolId);
	}*/
	
	public List<Map<String, Object>> getotherdetailsBySchoolId(@RequestParam int schoolId) {
		//String sql = "select*from school_buildup_area where school_id=? ORDER BY buildup_id DESC LIMIT 1" ;

		String sql = "SELECT buildup_id, land_area, buildup_area, " +
	             "CAST(is_compound AS SIGNED) AS is_compound, " +
	             "is_nameboard AS is_nameboard, " +
	             "is_pasystem  AS is_pasystem, " +
	             "is_bell  AS is_bell, " +
	             "is_generator  AS is_generator, " +
	             "school_id " +
	             "FROM school_buildup_area WHERE school_id = ? ORDER BY buildup_id DESC LIMIT 1";

		
		return jdbcTemplate.queryForList(sql, schoolId);
	}
//initial
	/*public List<Map<String, Object>> getStaffStrengthBySchoolId(@RequestParam int schoolId) {
		String sql = "SELECT * FROM staff_strength_1 WHERE school_id = ?";

		return jdbcTemplate.queryForList(sql, schoolId);
	}
	public List<Map<String, Object>> getStaffStrengthBySchoolId(@RequestParam int schoolId) {
		String sql = "SELECT * FROM staff_strength_1 WHERE school_id = ? ORDER BY staff_id DESC LIMIT 1";

		return jdbcTemplate.queryForList(sql, schoolId);
	}
*/
	@Transactional
	public Map<String, Object> getIt_Details(int it_map_id) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "SELECT * FROM it_mapping WHERE it_map_id = ? ";
			result = jdbcTemplate.queryForMap(sql, it_map_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

//	@Transactional
//	public String unmapAsset(int it_details_id) {
//		String sql = "UPDATE it_master_details SET is_mapped = false WHERE it_details_id = ? ";
//
//		try {
//			jdbcTemplate.update(sql, it_details_id);
//			return "Asset Unmapped";
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "error";
//		}
//	}

	@Transactional
	public List<Integer> getassetRoomID(int room_id) {
		List<Integer> assetIds = new ArrayList<>();
		try {
			String sql = "SELECT it_details_id FROM it_mapping WHERE room_id = ?";
			assetIds = jdbcTemplate.queryForList(sql, Integer.class, room_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assetIds;
	}

	@Transactional
	public void unmapItAssetMaster(Integer it_details_id) {
		String sql = "UPDATE it_master_details SET is_mapped = false WHERE it_details_id = ? ";

		try {
			jdbcTemplate.update(sql, it_details_id);

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	@Transactional
	public int  checkBuilding(String building_name, int school_id) {
		
		String sql = "SELECT CASE \r\n"
				+ "           WHEN COUNT(*) > 0 THEN 1 \r\n"
				+ "           ELSE 0 \r\n"
				+ "       END AS is_existing\r\n"
				+ "FROM buildings\r\n"
				+ "WHERE school_id = ? AND building_name = ?";
		
		try {
			return jdbcTemplate.queryForObject(sql, Integer.class, school_id, building_name);

		} catch (Exception e) {
			e.printStackTrace();
			return 5;
              
		}
		
		
	}

	@Transactional
	public int getAssetCount(String udise, boolean is_online) {
		String sql = "SELECT COUNT(*) FROM it_master_details WHERE is_online = ? and udise = ?;";
		
		try {
			return jdbcTemplate.queryForObject(sql, Integer.class, is_online, udise);

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
              
		}
	}
	@Transactional
	public String assetUpdation(int asset_qty, String assetType, int sub_asset_id, int school_id, int room_id,
			int building_id, int floor_id) {
		int check =	mapAssetMaster(asset_qty, assetType, sub_asset_id, school_id);
		if(check ==1) {
			Map<String, Object> data = ckeckAssetQty(room_id, sub_asset_id, assetType);
			
			int asset_Map_Id = (Integer) data.get("asset_Map_Id") !=null ? (Integer) data.get("asset_Map_Id") : 0;
			int qty = (Integer) data.get("asset_qty") !=null ? (Integer) data.get("asset_qty") : 0;
			int total = qty+asset_qty;
			
			if(asset_Map_Id!=0) {
			updateAssetQty(asset_Map_Id, assetType, total);
			}else {
			addFurAsset(sub_asset_id, asset_qty, room_id,building_id, floor_id, school_id, assetType );
			}
			return "Sucess";
		} else {
			return "Add Sufficient Quantity";
		}
	}
	
	private void updateAssetQty(int asset_Map_Id, String assetType, int total) {
		String sql="";
		if (assetType.equalsIgnoreCase("furnitureasset")) {
			sql = "UPDATE furniture_mapping SET asset_qty = ? WHERE furniture_map_id = ?";
		} else if (assetType.equalsIgnoreCase("electricalasset")) {
			sql = "UPDATE electrical_mapping SET asset_qty = ? WHERE electrical_map_id = ?";
		} 
		jdbcTemplate.update(sql, total, asset_Map_Id);
		
	}

	public Map<String, Object> ckeckAssetQty(int room_id, int sub_asset_id, String assetType) {
	    String sql = "";
	    if ("furnitureasset".equalsIgnoreCase(assetType)) {
	        sql = "SELECT furniture_map_id as asset_Map_Id, asset_qty FROM furniture_mapping WHERE room_id = ? AND sub_asset_id = ?";
	    } else if ("electricalasset".equalsIgnoreCase(assetType)) {
	        sql = "SELECT electrical_map_id as asset_Map_Id, asset_qty FROM electrical_mapping WHERE room_id = ? AND sub_asset_id = ?";
	    }

	    try {
	    	return jdbcTemplate.queryForMap(sql, room_id, sub_asset_id);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Map.of();
	    }
	}

	@Transactional
	public int mapAssetMaster(int asset_qty, String assetType, int sub_asset_id, int school_id) {
		Map<String, Object> assetMaster = getAssetMasterBySchlId(sub_asset_id, school_id, assetType);
		
		int mapped = assetMaster.get("mapped_qty") != null ? (Integer) assetMaster.get("mapped_qty") : 0;
		int unMapped = assetMaster.get("unmapped_qty") != null ? (Integer) assetMaster.get("unmapped_qty") : 0;
		if(unMapped>=asset_qty) {
		int map = mapped+asset_qty;
		int unMap = unMapped-asset_qty;
		return updateAssetmaster(map, unMap, school_id, sub_asset_id, assetType);
		}
		return 0;
		
	}

	@Transactional
	public int updateAssetmaster(int map, int unMap, int school_id, int sub_asset_id, String assetType) {
		String sql="";
		if (assetType.equalsIgnoreCase("furnitureasset")) {
			sql = "UPDATE furniture_master SET mapped_qty = ?, unmapped_qty = ? WHERE school_id = ? and sub_asset_id = ?";
		} else if (assetType.equalsIgnoreCase("electricalasset")) {
			sql = "UPDATE electrical_master SET mapped_qty = ?, unmapped_qty = ? WHERE school_id = ? and sub_asset_id = ?";
		} 
		try {
			jdbcTemplate.update(sql, map, unMap, school_id, sub_asset_id);
			return 1;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return 0;
		}
		
	}	
	


	@Transactional
	public Map<String, Object> getAssetMasterBySchlId(int sub_asset_id, int school_id, String assetType) {
		String sql="";
		if (assetType.equalsIgnoreCase("furnitureasset")) {
		 sql = "SELECT * from furniture_master where school_id = ? and sub_asset_id = ?";
		} else if (assetType.equalsIgnoreCase("electricalasset")) {
			sql = "SELECT * from electrical_master where school_id = ? and sub_asset_id = ?";
		}
		try {
		 List<Map<String, Object>> data =jdbcTemplate.queryForList(sql, school_id, sub_asset_id);
			if(data!=null && !data.isEmpty()) {
				return data.get(0);
			}
			return Map.of();
		} catch (Exception e) {
			e.printStackTrace();
			return Map.of();
		}
	}

	@Transactional
	public List<Map<String, Object>> getElectricalAssetMasterByRoomId(int room_id) {
		try {
		String sql = "select sub_asset_id, school_id, asset_qty from electrical_mapping where room_id=? and is_active = 1 and is_delete = 0";

		return jdbcTemplate.queryForList(sql, room_id);
		} catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}

	@Transactional
	public List<Map<String, Object>> getFurnitureAssetMasterByRoomId(int room_id) {
		try {
			String sql = "select sub_asset_id, school_id, asset_qty from furniture_mapping where room_id=? and is_active = 1 and is_delete = 0";

			return jdbcTemplate.queryForList(sql, room_id);
			} catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
	}
	
	@Transactional
	private List<Map<String, Object>> getElectricalAssetMasterByFloorId(int floor_id) {
		try {
			String sql = "select sub_asset_id, school_id, asset_qty from electrical_mapping where floor_id=? and is_active = 1 and is_delete = 0";

			return jdbcTemplate.queryForList(sql, floor_id);
			} catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
	}
	@Transactional
	private List<Map<String, Object>> getFurnitureAssetMasterByFloorId(int floor_id) {
		try {
			String sql = "select sub_asset_id, school_id, asset_qty from furniture_mapping where floor_id=? and is_active = 1 and is_delete = 0";

			return jdbcTemplate.queryForList(sql, floor_id);
			} catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
	}
	
	@Transactional
	private List<Map<String, Object>> getElectricalAssetMasterByBuildingId(int building_id) {
		try {
			String sql = "select sub_asset_id, school_id, asset_qty from electrical_mapping where building_id=? and is_active = 1 and is_delete = 0";

			return jdbcTemplate.queryForList(sql, building_id);
			} catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
	}
	
	@Transactional
	private List<Map<String, Object>> getFurnitureAssetMasterByBuildingId(int building_id) {
		try {
			String sql = "select sub_asset_id, school_id, asset_qty from furniture_mapping where building_id=? and is_active = 1 and is_delete = 0";

			return jdbcTemplate.queryForList(sql, building_id);
			} catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
	}

	@Transactional
	public String updateAssetExecution(List<Map<String, Object>> assets) {
		try {
			for(Map<String, Object> asset : assets) {
				int id = Integer.parseInt(asset.get("id").toString());
			
				int qty = Integer.parseInt(asset.get("qty").toString());
				String assetType = (String) asset.get("assetType");
				int school_id = Integer.parseInt(asset.get("schid").toString());
				int sub_asset_id = Integer.parseInt(asset.get("subid").toString());
				int flag = Integer.parseInt(asset.get("flag").toString());
				int originalQty = Integer.parseInt(asset.get("originalQty").toString());
				
				Map<String, Object> assetMaster = getAssetMasterBySchlId(sub_asset_id, school_id, assetType);
				int mapped = assetMaster.get("mapped_qty") != null ? (Integer) assetMaster.get("mapped_qty") : 0;
				int unMapped = assetMaster.get("unmapped_qty") != null ? (Integer) assetMaster.get("unmapped_qty") : 0;
				int master = assetMaster.get("master_qty") != null ? (Integer) assetMaster.get("master_qty") : 0;
				
				int map = 0;
				int unmap = 0;
				
				if(flag == -1) {
					int diff = originalQty-qty;
					map=mapped-diff;
					unmap = unMapped+diff;
					if(unmap<=master) {
						int result = updateAssetmaster(map, unmap, school_id, sub_asset_id, assetType);
					} else {
						return "Add Sufficient Master Quantity";								
						}
					
				} else if (flag == 0) {
					
				}else if(flag == 1) {
					int diff = qty-originalQty;
					map=mapped+diff;
					unmap = unMapped-diff;
					if(map<=master) {
						int result = updateAssetmaster(map, unmap, school_id, sub_asset_id, assetType);
					} else {
						return "Add Sufficient Master Quantity";								
						}
				}
				String data = updateFurturQty(id, qty, assetType);
				
			}
			return "Sucess";
			} catch (Exception e) {
				e.printStackTrace();
				return "Error";
			}
	}
	
	public List<Map<String, Object>> getStaffStrengthBySchoolId(@RequestParam int schoolId) {
		String sql = "SELECT * FROM staff_strength_1 WHERE school_id = ? ORDER BY staff_id DESC LIMIT 1";

		return jdbcTemplate.queryForList(sql, schoolId);
	}
	
	public List<Map<String, Object>> getSpecialCategoryBySchoolId(int schoolId) {
		String sql = "SELECT scd.*,scm.special_category as sc_names FROM  special_category_details scd\r\n"
				+ "INNER JOIN special_category_master scm ON scm.special_category_id=scd.special_category_id\r\n"
				+ "where scd.school_id=? AND scd.is_active=1 AND scd.is_delete=0";

		return jdbcTemplate.queryForList(sql, schoolId);
	}
	

}
