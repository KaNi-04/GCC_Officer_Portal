package in.gov.chennaicorporation.gccoffice.school.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.school.Service.DropdownsService;

@RestController
@RequestMapping("/gcc/api/gccschool/dropdown")
public class DropdownsController {
	@Autowired
	private DropdownsService dropdownsService;

	@Autowired
	public DropdownsController(DropdownsService dropdownsService) {
		this.dropdownsService = dropdownsService;
	}

	// To Get Court Dropdown Data
	@GetMapping("/getCourtData")
	public ResponseEntity<Map<String, Object>> getCourtData() {
		try {
			List<Map<String, Object>> data = dropdownsService.getCourtData();
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

	// To Get Equipmengt Dropdown Data
	@GetMapping("/getEquipmentData")
	public ResponseEntity<Map<String, Object>> getEquipmentData() {
		try {
			List<Map<String, Object>> data = dropdownsService.getEquipmentData();
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

	// To Get Building Material Dropdown Data
	@GetMapping("/getBuildingMaterial")
	public ResponseEntity<Map<String, Object>> getBuildingMaterial() {
		try {
			List<Map<String, Object>> data = dropdownsService.getBuildingMaterial();
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

	// To Get Roof Material Dropdown Data
	@GetMapping("/getRoofMaterial")
	public ResponseEntity<Map<String, Object>> getRoofMaterial() {
		try {
			List<Map<String, Object>> data = dropdownsService.getRoofMaterial();
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

	// To Get Water Source Dropdown Data
	@GetMapping("/getWaterSource")
	public ResponseEntity<Map<String, Object>> getWaterSource() {
		try {
			List<Map<String, Object>> data = dropdownsService.getWaterSource();
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

	// To Get Water Storage Dropdown Data
	@GetMapping("/getWaterStorage")
	public ResponseEntity<Map<String, Object>> getWaterStorage() {
		try {
			List<Map<String, Object>> data = dropdownsService.getWaterStorage();
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

	// To Get Rooms Dropdown Data
	@GetMapping("/getRooms")
	public ResponseEntity<Map<String, Object>> getRooms() {
		try {
			List<Map<String, Object>> data = dropdownsService.getRooms();
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

	@GetMapping("/getFloorsByBuilding")
	public ResponseEntity<Map<String, Object>> getFloorsByBuilding(@RequestParam("building_id") int buildingId) {
		try {
			List<Map<String, Object>> data = dropdownsService.getFloorsByBuilding(buildingId);
			Map<String, Object> response = new HashMap<>();
			if (!data.isEmpty()) {
				response.put("status", "OK");
				response.put("data", data);
			} else {
				response.put("status", "Failed");
				response.put("data", "No floors found for the selected building.");
			}
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Failed to fetch floors."));
		}
	}

	@GetMapping("/getBuildingsBySchoolId")
	public ResponseEntity<Map<String, Object>> getBuildingsBySchoolId(@RequestParam int school_id) {
		try {
			List<Map<String, Object>> data = dropdownsService.getBuildingsBySchoolId(school_id);
			Map<String, Object> response = new HashMap<>();
			if (!data.isEmpty()) {
				response.put("status", "OK");
				response.put("data", data);
			} else {
				response.put("status", "Failed");
				response.put("data", "No buildings found for the selected school.");
			}
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Failed to fetch buildings."));
		}
	}

	@GetMapping("/getDropdownValues")
	public List<Map<String, Object>> getDropdownValues(@RequestParam String user_id) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();

		String schoolId = dropdownsService.getSchoolId(user_id);
		if (schoolId == null || schoolId.trim().isEmpty() || schoolId.equalsIgnoreCase("error")) {
			response.put("Message", "Failure");
			response.put("Data", List.of());
			resultList.add(response);
			return resultList;
		}

		List<Map<String, Object>> udiseData = dropdownsService.getUdiseNumbersBySchoolId(schoolId);
		if (!udiseData.isEmpty()) {
			List<String> udiseList = udiseData.stream().map(m -> (String) m.get("udise")).collect(Collectors.toList());

			response.put("Message", "Success");
			response.put("Data", udiseList);
		} else {
			response.put("Message", "No Data Found");
			response.put("Data", List.of());
		}

		resultList.add(response);
		return resultList;
	}

	@GetMapping("/getCategoryValues")
	public List<Map<String, Object>> getCategoryValues(@RequestParam String user_id) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();

		String schoolId = dropdownsService.getSchoolId(user_id);
		if (schoolId == null || schoolId.trim().isEmpty() || schoolId.equalsIgnoreCase("error")) {
			response.put("Message", "Failure");
			response.put("Data", List.of());
			resultList.add(response);
			return resultList;
		}

		List<Map<String, Object>> categoryData = dropdownsService.getCategoryBySchoolId(schoolId);
		if (!categoryData.isEmpty()) {
			List<String> categoryList = categoryData.stream().map(m -> (String) m.get("category"))
					.collect(Collectors.toList());

			response.put("Message", "Success");
			response.put("Data", categoryList);
		} else {
			response.put("Message", "No Data Found");
			response.put("Data", List.of());
		}

		resultList.add(response);
		return resultList;
	}

	@GetMapping("/getZoneValues")
	public List<Map<String, Object>> getZoneValues(@RequestParam String user_id) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();

		String schoolId = dropdownsService.getSchoolId(user_id);
		if (schoolId == null || schoolId.trim().isEmpty() || schoolId.equalsIgnoreCase("error")) {
			response.put("Message", "Failure");
			response.put("Data", List.of());
			resultList.add(response);
			return resultList;
		}

		List<Map<String, Object>> zoneData = dropdownsService.getZoneBySchoolId(schoolId);
		if (!zoneData.isEmpty()) {
			List<String> zoneList = zoneData.stream().map(m -> (String) m.get("zone")).collect(Collectors.toList());

			response.put("Message", "Success");
			response.put("Data", zoneList);
		} else {
			response.put("Message", "No Data Found");
			response.put("Data", List.of());
		}

		resultList.add(response);
		return resultList;
	}

	@GetMapping("/getwardValues")
	public List<Map<String, Object>> getWardValues(@RequestParam String user_id) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();

		String schoolId = dropdownsService.getSchoolId(user_id);
		if (schoolId == null || schoolId.trim().isEmpty() || schoolId.equalsIgnoreCase("error")) {
			response.put("Message", "Failure");
			response.put("Data", List.of());
			resultList.add(response);
			return resultList;
		}

		List<Map<String, Object>> wardData = dropdownsService.getWardBySchoolId(schoolId);
		if (!wardData.isEmpty()) {
			List<String> wardList = wardData.stream().map(m -> (String) m.get("division")).collect(Collectors.toList());

			response.put("Message", "Success");
			response.put("Data", wardList);
		} else {
			response.put("Message", "No Data Found");
			response.put("Data", List.of());
		}

		resultList.add(response);
		return resultList;
	}

	@GetMapping("/getAllDropdownValues")
	public ResponseEntity<?> getAllDropdownValues(@RequestParam String user_id, @RequestParam String type) {
		Map<String, Object> response = new HashMap<>();
		List<String> dropdownList = Collections.emptyList();

		// First get school_id(s) for this user
		String schoolId = dropdownsService.getSchoolId(user_id);

		if (schoolId.equals("error")) {
			response.put("Message", "Failed to fetch school ID");
			response.put("Data", dropdownList);
			return ResponseEntity.ok(Collections.singletonList(response));
		}

		try {
			List<Map<String, Object>> rawData;

			switch (type.toLowerCase()) {
			case "udise":
				rawData = dropdownsService.getUdiseNumbersBySchoolId(schoolId);
				dropdownList = rawData.stream().map(map -> String.valueOf(map.get("udise")))
						.collect(Collectors.toList());
				break;
			case "category":
				rawData = dropdownsService.getCategoryBySchoolId(schoolId);
				dropdownList = rawData.stream().map(map -> String.valueOf(map.get("category"))).distinct()
						.collect(Collectors.toList());
				break;
			case "zone":
				rawData = dropdownsService.getZoneBySchoolId(schoolId);
				dropdownList = rawData.stream().map(map -> String.valueOf(map.get("zone"))).distinct()
						.collect(Collectors.toList());
				break;
			case "ward":
				rawData = dropdownsService.getWardBySchoolId(schoolId);
				dropdownList = rawData.stream().map(map -> String.valueOf(map.get("division"))).distinct()
						.collect(Collectors.toList());
				break;
			default:
				response.put("Message", "Invalid dropdown type");
				response.put("Data", dropdownList);
				return ResponseEntity.badRequest().body(Collections.singletonList(response));
			}

			response.put("Message", "Success");
			response.put("Data", dropdownList);
			return ResponseEntity.ok(Collections.singletonList(response));
		} catch (Exception e) {
			e.printStackTrace();
			response.put("Message", "Error while processing dropdown values");
			response.put("Data", dropdownList);
			return ResponseEntity.ok(Collections.singletonList(response));
		}
	}

	// To Get Asset Types Data
	@GetMapping("/getAssetType")
	public ResponseEntity<Map<String, Object>> getAssetType() {
		try {
			List<Map<String, Object>> data = dropdownsService.getAssetType();
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

	// To Get Sub Asset Types Data
	@GetMapping("/getSubAssetType")
	public ResponseEntity<Map<String, Object>> getSubAssetType(int assetid) {
		try {
			List<Map<String, Object>> data = dropdownsService.getSubAssetType(assetid);
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

			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Error getting data: " + e.getMessage()));
		}
	}

	@GetMapping("/getSplSubAssetType")
	public ResponseEntity<Map<String, Object>> getSplSubAssetType(int school_id, int assetid) {
		try {
			List<String> assets = dropdownsService.getAssetList(school_id, assetid);
			List<Map<String, Object>> data = dropdownsService.getSplSubAssetType(assets, assetid);
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

			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Error getting data: " + e.getMessage()));
		}
	}
	
	@GetMapping("/getSpecialCategory")
	public ResponseEntity<Map<String, Object>> getSpecialCategory() {
		try {
			List<Map<String, Object>> data = dropdownsService.getSpecialCategory();
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

}
