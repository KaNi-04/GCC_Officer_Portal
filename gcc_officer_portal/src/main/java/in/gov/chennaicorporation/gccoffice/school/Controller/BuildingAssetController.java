package in.gov.chennaicorporation.gccoffice.school.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.school.Service.BuildingAssetService;
import in.gov.chennaicorporation.gccoffice.school.Service.SchoolPropertyService;

@RestController
@RequestMapping("/gcc/api/gccschool/buildingasset")
public class BuildingAssetController {

	private BuildingAssetService buildingAssetService;

	@Autowired
	public BuildingAssetController(BuildingAssetService buildingAssetService) {
		this.buildingAssetService = buildingAssetService;
	}

	// For Buildings
	@PostMapping(value = "/saveBuildings")
	public String saveBuildings(@RequestBody List<Map<String, Object>> buildingList) {
		try {
			for (Map<String, Object> building : buildingList) {
				String building_name = (String) building.get("building_name");
				int total_floor = Integer.parseInt(building.get("total_floor").toString());
				int building_material = Integer.parseInt(building.get("building_material").toString());
				int roof_material = Integer.parseInt(building.get("roof_material").toString());
				// int building_order =
				// Integer.parseInt(building.get("building_order").toString());
				int school_id = Integer.parseInt(building.get("school_id").toString());
				int check_building = buildingAssetService.checkBuilding(building_name, school_id);
				if (check_building == 1) {
					return "Building Name Alredy Exist";
				} else if (check_building == 5) {
					return "error";
				}
				int building_id = buildingAssetService.saveBuildings(building_name, total_floor, building_material,
						roof_material, school_id);
				System.out.println("Hi");
				for (int i = 1; i <= total_floor; i++) {
					Map<String, Object> floor = buildingAssetService.getFloor(i);

					String floor_name = (String) floor.get("content");
					int floor_master_id = Integer.parseInt(floor.get("floor_master_id").toString());
					buildingAssetService.saveFloor(floor_name, building_id, floor_master_id, school_id);
					System.out.println("Saved Floor Master ID: " + floor_master_id);
				}
			}
			return "Building Saved sucessfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	// To Get Saved Buildings
	@GetMapping("/getSavedBuildings")
	public ResponseEntity<Map<String, Object>> getSavedBuildings(int school_id) {
		try {
			List<Map<String, Object>> data = buildingAssetService.getSavedBuildings(school_id);
			int lastId = buildingAssetService.getLastBuildingOrder(school_id);
			Map<String, Object> response = new HashMap<>();
			if (data.size() != 0) {
				response.put("status", "OK");
				response.put("data", data);
				response.put("lastId", lastId);
			} else {
				response.put("status", "Failed");
				response.put("data", "No Data Found");
				response.put("lastId", lastId);
			}
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			// Log the exception details for debugging
			e.printStackTrace();

			// Provide a user-friendly error message
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Error getting data: " + e.getMessage()));
		}
	}

	// To Get floor Data by buildings
	@GetMapping("/getFloorDetails")
	public ResponseEntity<Map<String, Object>> getFloorDetails(int building_id) {
		try {
			List<Map<String, Object>> data = buildingAssetService.getFloorDetails(building_id);
			Map<String, Object> response = new HashMap<>();
			if (data.size() != 0) {
				response.put("status", "OK");
				response.put("data", data);
			} else {
				response.put("status", "Failed");
				response.put("data", "No Data Found");
			}

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			// Log the exception details for debugging
			e.printStackTrace();

			// Provide a user-friendly error message
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Error getting data: " + e.getMessage()));
		}
	}

	// For Room mapping --add room
	/*
	 * @PostMapping(value = "/saveRoom") public String saveRoom(@RequestBody
	 * Map<String, Object> rooms) { try { int building_id = (int)
	 * rooms.get("building_id"); int floors_id = (int) rooms.get("floors_id"); int
	 * room_type = Integer.parseInt(rooms.get("room_type").toString());
	 * 
	 * String room_num = (String) rooms.get("room_num"); int school_id = (int)
	 * rooms.get("school_id"); buildingAssetService.saveRoomDetails(room_type,
	 * room_num, building_id, floors_id, school_id); return
	 * "Rooms Mapped Sucessfully"; } catch (Exception e) { e.printStackTrace();
	 * return "error"; } }
	 */

	// vishnu save
	@PostMapping(value = "/saveRoom")
	public String saveRoom(@RequestBody Map<String, Object> rooms) {
		try {
			int building_id = Integer.parseInt(rooms.get("building_id").toString());
			int floors_id = Integer.parseInt(rooms.get("floors_id").toString());
			int room_type = Integer.parseInt(rooms.get("room_type").toString());
			String room_num = rooms.get("room_num").toString();
			int school_id = Integer.parseInt(rooms.get("school_id").toString());
			
			String anganwadiUniqueNumber = rooms.get("anganwadiUniqueNumber") != null 
				    ? rooms.get("anganwadiUniqueNumber").toString().trim() 
				    : null;
			Boolean anganwadi_check = rooms.get("anganwadi_check") != null ? Boolean.parseBoolean(rooms.get("anganwadi_check").toString()) : null;

			return buildingAssetService.roomDetails(room_type, room_num, building_id, floors_id,anganwadiUniqueNumber, anganwadi_check, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	// For Edit Room mapping
//			@PostMapping(value = "/editRoom")
//			public String editRoom(@RequestBody Map<String, Object> rooms) {
//				try {
//					int room_id = (int) rooms.get("room_id");
//					int building_id = (int) rooms.get("building_id");
//					int floors_id = (int) rooms.get("floors_id");
//					int room_type = (int) rooms.get("room_type");
//					String room_num = (String) rooms.get("room_num");
//					int school_id = (int) rooms.get("school_id");
//					buildingAssetService.editRoom(room_type, room_num, building_id, floors_id, school_id, room_id);
//					return "Rooms Edited Sucessfully";
//				} catch (Exception e) {
//					e.printStackTrace();
//					return "error";
//				}
//			}

	// vishnu edit room
	@PostMapping(value = "/editRoom")
	public String editRoom(@RequestBody Map<String, Object> rooms) {
		try {
			if (rooms.get("room_id") == null || rooms.get("building_id") == null || rooms.get("floors_id") == null
					|| rooms.get("room_type") == null || rooms.get("room_num") == null
					|| rooms.get("school_id") == null) {

				return "Missing required fields";
			}

			int room_id = Integer.parseInt(rooms.get("room_id").toString());
			int building_id = Integer.parseInt(rooms.get("building_id").toString());
			int floors_id = Integer.parseInt(rooms.get("floors_id").toString());
			int room_type = Integer.parseInt(rooms.get("room_type").toString());
			String room_num = rooms.get("room_num").toString();
			int school_id = Integer.parseInt(rooms.get("school_id").toString());
			String anganwadiUniqueNumber = rooms.get("anganwadiUniqueNumber") != null 
				    ? rooms.get("anganwadiUniqueNumber").toString().trim() 
				    : null;
			
			Boolean anganwadi_check = rooms.get("anganwadi_check") != null ? Boolean.parseBoolean(rooms.get("anganwadi_check").toString()) : null;

			return buildingAssetService.editRoomsById(room_type, room_num, building_id, floors_id, school_id,anganwadiUniqueNumber,anganwadi_check, room_id);
			
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	// For Delete Room mapping
	/*
	 * @PostMapping(value = "/deleteRoom") public String deleteRoom(@RequestParam
	 * int room_id, @RequestParam boolean is_delete) { try { // // from assets //
	 * List<Map<String, Object>> assets = buildingAssetService.getAssets(room_id);
	 * // if(!assets.isEmpty()) { // for(Map<String, Object> asset : assets) { // //
	 * } // } buildingAssetService.deleteRoom(room_id, is_delete); return
	 * "Rooms Edited Sucessfully"; } catch (Exception e) { e.printStackTrace();
	 * return "error"; } }
	 */
//vishnu

	@GetMapping("/getBuildingbyid")
	public ResponseEntity<Map<String, Object>> getSimpleBuilding(@RequestParam int building_id) {
		try {
			Map<String, Object> building = buildingAssetService.getSimpleBuildingById(building_id);
			if (building != null) {
				return ResponseEntity.ok(Map.of("status", "OK", "data", building));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("status", "Failed", "message", "Building not found"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Something went wrong"));
		}
	}

	@GetMapping("/getAllMaterials")
	public ResponseEntity<Map<String, Object>> getAllMaterials() {
		try {
			Map<String, List<Map<String, Object>>> materials = buildingAssetService.getAllMaterials();

			return ResponseEntity.ok(Map.of("status", "OK", "buildingMaterials", materials.get("buildingMaterials"),
					"roofMaterials", materials.get("roofMaterials")));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Failed to load material data"));
		}
	}

	@GetMapping("/getFloorsByBuildingId")
	public ResponseEntity<Map<String, Object>> getFloorsByBuildingId(@RequestParam int buildingId) {
		try {
			List<Map<String, Object>> floors = buildingAssetService.getFloorsByBuildingId(buildingId);

			return ResponseEntity.ok(Map.of("status", "OK", "floors", floors));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Could not load floor list"));
		}
	}

	@PostMapping(value = "/deleteBuilding")
	public String deleteBuilding(@RequestParam int building_id, @RequestParam boolean is_delete) {

		return buildingAssetService.executeDelete(building_id, is_delete);

	}

	@PostMapping(value = "/deleteRoom")
	public String deleteRoom(@RequestParam int room_id, @RequestParam boolean is_delete) {
		try {
			// from assets
			List<Integer> asset_ID = buildingAssetService.getassetRoomID(room_id);
			List<Map<String, Object>> electrical_asset = buildingAssetService.getElectricalAssetMasterByRoomId(room_id);
			List<Map<String, Object>> furniture_asset = buildingAssetService.getFurnitureAssetMasterByRoomId(room_id);
			for (Integer it_details_id : asset_ID) {
				buildingAssetService.unmapItAssetMaster(it_details_id);
			}
			for (Map<String, Object> ele_data : electrical_asset) {
				int school_id = (Integer) ele_data.get("school_id");
				int sub_asset_id = (Integer) ele_data.get("sub_asset_id");
				int asset_qty = (Integer) ele_data.get("asset_qty");
				String assetType = "electricalasset";
				Map<String, Object> assetMaster = buildingAssetService.getAssetMasterBySchlId(sub_asset_id, school_id,
						assetType);
				int mapped = assetMaster.get("mapped_qty") != null ? (Integer) assetMaster.get("mapped_qty") : 0;
				int unMapped = assetMaster.get("unmapped_qty") != null ? (Integer) assetMaster.get("unmapped_qty") : 0;
				int map = mapped - asset_qty;
				int unMap = unMapped + asset_qty;
				buildingAssetService.updateAssetmaster(map, unMap, school_id, sub_asset_id, assetType);
			}
			for (Map<String, Object> fur_data : furniture_asset) {
				int school_id = (Integer) fur_data.get("school_id");
				int sub_asset_id = (Integer) fur_data.get("sub_asset_id");
				int asset_qty = (Integer) fur_data.get("asset_qty");
				String assetType = "furnitureasset";
				Map<String, Object> assetMaster = buildingAssetService.getAssetMasterBySchlId(sub_asset_id, school_id,
						assetType);
				int mapped = assetMaster.get("mapped_qty") != null ? (Integer) assetMaster.get("mapped_qty") : 0;
				int unMapped = assetMaster.get("unmapped_qty") != null ? (Integer) assetMaster.get("unmapped_qty") : 0;
				int map = mapped - asset_qty;
				int unMap = unMapped + asset_qty;
				buildingAssetService.updateAssetmaster(map, unMap, school_id, sub_asset_id, assetType);
			}
			buildingAssetService.unmapAssets(room_id, is_delete);
			buildingAssetService.deleteRoom(room_id, is_delete);
			return "Rooms Edited Sucessfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	@PostMapping(value = "/editBuildings")
	public String editBuilding(@RequestParam int building_id, @RequestParam int floor_id) {

		return buildingAssetService.editBuildings(building_id, floor_id);
	}

	@PostMapping(value = "/addFloor")
	public String addFloor(@RequestParam int building_id, @RequestParam int exe_order) {
		return buildingAssetService.addFloor(building_id, exe_order);
	}

	@PostMapping(value = "/updateBuilding")
	public String updateBuilding(@RequestParam int building_id, @RequestParam String building_name,
			@RequestParam int total_floor, @RequestParam String building_material, @RequestParam String roof_material,
			@RequestParam int school_id) {
		try {
			buildingAssetService.updateBuilding(building_id, building_name, total_floor, building_material,
					roof_material, school_id);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}

	@GetMapping("/getRoomDetails")
	public ResponseEntity<Map<String, Object>> getRoomDetails(@RequestParam int school_id) {
		try {
			List<Map<String, Object>> data = buildingAssetService.getRoomDetailsBySchoolId(school_id);
			Map<String, Object> response = new HashMap<>();

			if (data != null && !data.isEmpty()) {
				response.put("status", "OK");
				response.put("data", data);
			} else {
				response.put("status", "Failed");
				response.put("data", "No Data Found");
			}

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Error fetching room details: " + e.getMessage()));
		}
	}

	@GetMapping("/getRoomById")
	public ResponseEntity<Map<String, Object>> getRoomById(@RequestParam int room_id) {
		try {
			Map<String, Object> room = buildingAssetService.getRoomDetailsById(room_id);
			return ResponseEntity.ok(Map.of("status", "OK", "data", room));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "ERROR", "data", "Room not found"));
		}
	}

	@GetMapping(value = "/getItAssetMapping")
	public ResponseEntity<Map<String, Object>> getItAssetMapping(int room_id, String assetType) {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> data = buildingAssetService.getItAssetMapping(room_id, assetType);
		response.put("Status", "OK");
		response.put("data", data);
		return ResponseEntity.ok(response);

	}

	@PostMapping(value = "/deleteAssetMapping")
	public ResponseEntity<Map<String, Object>> deleteAssetMapping(int it_map_id, boolean is_delete) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> it_Details = buildingAssetService.getIt_Details(it_map_id);
		int it_details_id = (Integer) it_Details.get("it_details_id");
		String data = buildingAssetService.deleteAssetMapping(it_map_id, is_delete);
		buildingAssetService.unmapItAssetMaster(it_details_id);
		response.put("status", "Sucess");
		response.put("data", data);
		return ResponseEntity.ok(response);

	}

	// For update assets
	@PostMapping(value = "/updateassets")
	public String updateassets(@RequestBody List<Map<String, Object>> assets) {
		return buildingAssetService.updateAssetExecution(assets);
	}

	@GetMapping(value = "/getSchoolAsset")
	public ResponseEntity<Map<String, Object>> getSchoolAsset(@RequestParam int school_id, @RequestParam String type) {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> data = buildingAssetService.getSchoolAsset(school_id, type);
		if (!data.isEmpty()) {
			response.put("status", "Success");
			response.put("data", data);
		} else {
			response.put("status", "Error");
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/mapAssets")
	public String mapAssets(@RequestBody List<Map<String, Object>> assets) {
		try {
			for (Map<String, Object> asset : assets) {
				int room_id = Integer.parseInt(asset.get("room_id").toString());
				int floor_id = Integer.parseInt(asset.get("floor_id").toString());
				int school_id = Integer.parseInt(asset.get("school_id").toString());
				int it_details_id = Integer.parseInt(asset.get("it_details_id").toString());
				int building_id = Integer.parseInt(asset.get("building_id").toString());
				buildingAssetService.mapAssets(room_id, floor_id, school_id, it_details_id, building_id);
			}
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}

	}

	@PostMapping(value = "/addFurAsset")
	public String addFurAsset(@RequestParam int sub_asset_id, @RequestParam int asset_qty, @RequestParam int room_id,
			@RequestParam int building_id, @RequestParam int floor_id, @RequestParam int school_id,
			@RequestParam String assetType) {

		return buildingAssetService.assetUpdation(asset_qty, assetType, sub_asset_id, school_id, room_id, building_id,
				floor_id);
	}

	// Asset Management

	@GetMapping(value = "/getAssetManagement")
	public ResponseEntity<Map<String, Object>> getSchoolAssetForManagement(@RequestParam int school_id) {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> data = buildingAssetService.getSchoolAssetForManagement(school_id);
		if (!data.isEmpty()) {
			response.put("status", "Sucess");
			response.put("data", data);
		} else {
			response.put("status", "Error");
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/addAsset")
	public String addAsset(@RequestParam String code, @RequestParam String name, @RequestParam String source,
			@RequestParam String serialNum, @RequestParam String vendorDetail, @RequestParam String invoice_num,
			@RequestParam String udise, @RequestParam boolean is_online, @RequestParam int school_id) {
		try {
			int count = buildingAssetService.getAssetCount(udise, is_online);
			String assetCode = "";
			if (count >= 0) {
				String series = String.format("%04d", count + 1);
				String school = String.format("%03d", school_id);
				assetCode = code + school + series;

			} else {
				return "Error";
			}
			return buildingAssetService.addAsset(assetCode, name, source, serialNum, vendorDetail, invoice_num, udise,
					is_online);

		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}

	}

	@GetMapping(value = "/getAssets")
	public ResponseEntity<Map<String, Object>> getAssets(@RequestParam int it_details_id) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = buildingAssetService.getAssets(it_details_id);
		if (!data.isEmpty()) {
			response.put("status", "Success");
			response.put("data", data);
		} else {
			response.put("Status", "Error");
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/editAsset")
	public String editAsset(@RequestParam String assetCode, @RequestParam String name, @RequestParam String source,
			@RequestParam String serialNum, @RequestParam String vendorDetail, @RequestParam String invoice_num,
			@RequestParam int it_details_id) {
		try {

			return buildingAssetService.editAsset(assetCode, name, source, serialNum, vendorDetail, invoice_num,
					it_details_id);

		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}

	}

	@GetMapping("/getStudentStrengthBySchoolId")
	public ResponseEntity<Map<String, Object>> getStudentStrengthBySchoolId(@RequestParam int schoolId) {
		try {
			List<Map<String, Object>> studentStrength = buildingAssetService.getStudentStrengthBySchoolId(schoolId);

			if (!studentStrength.isEmpty()) {
				return ResponseEntity.ok(Map.of("status", "OK", "studentStrength", studentStrength));
			} else {
				return ResponseEntity.ok(Map.of("status", "NEW", "studentStrength", studentStrength));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Could not load student strength data"));
		}
	}

	@GetMapping("/getotherdetailsBySchoolId")
	public ResponseEntity<Map<String, Object>> getotherdetailsBySchoolId(@RequestParam int schoolId) {
		try {
			List<Map<String, Object>> getotherdetailsBySchool = buildingAssetService
					.getotherdetailsBySchoolId(schoolId);

			if (!getotherdetailsBySchool.isEmpty()) {
				return ResponseEntity.ok(Map.of("status", "OK", "otherdetailsBySchool", getotherdetailsBySchool));
			} else {
				return ResponseEntity.ok(Map.of("status", "NEW", "otherdetailsBySchool", getotherdetailsBySchool));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Could not load student strength data"));
		}
	}

	@GetMapping("/getStaffStrengthBySchoolId")
	public ResponseEntity<Map<String, Object>> getStaffStrengthBySchoolId(@RequestParam int schoolId) {
		try {
			List<Map<String, Object>> staffStrength = buildingAssetService.getStaffStrengthBySchoolId(schoolId);
			List<Map<String, Object>> special_category = buildingAssetService.getSpecialCategoryBySchoolId(schoolId);

			if (!staffStrength.isEmpty()) {
				return ResponseEntity.ok(Map.of("status", "OK", "staffStrength", staffStrength,"special_category",special_category));
			} else {
				return ResponseEntity.ok(Map.of("status", "NEW", "staffStrength", staffStrength,"special_category",special_category));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Could not load student strength data"));
		}
	}

}
