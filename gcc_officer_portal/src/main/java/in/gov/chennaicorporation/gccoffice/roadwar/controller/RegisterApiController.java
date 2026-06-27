package in.gov.chennaicorporation.gccoffice.roadwar.controller;

import java.util.Collections;
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

import in.gov.chennaicorporation.gccoffice.roadwar.service.RegisterService;

@RestController
@RequestMapping("/gcc/api/roadwarweb/register")
public class RegisterApiController {

	
	@Autowired
	private RegisterService registerService;
	
	
	@GetMapping("/getzones")
	public Map<String, Object> ViewZoneDropdown() {

	    Map<String, Object> response = new HashMap<>();

	    List<Map<String, Object>> zones = registerService.ViewZoneDropdown();

	    if (!zones.isEmpty()) {
	        response.put("status", true);
	        response.put("message", "Zone list fetched successfully");
	        response.put("data", zones);
	    } else {
	        response.put("status", false);
	        response.put("message", "No zones found");
	        response.put("data", Collections.emptyList());
	    }

	    return response;
	}
	
	@GetMapping("/getwardbyzone")
	public Map<String, Object> ViewWardDropdown(@RequestParam String zone) {

	    Map<String, Object> response = new HashMap<>();

	    List<Map<String, Object>> wards = registerService.ViewWardDropdown(zone);

	    if (!wards.isEmpty()) {
	        response.put("status", true);
	        response.put("message", "Ward list fetched successfully");
	        response.put("data", wards);
	    } else {
	        response.put("status", false);
	        response.put("message", "No wards found");
	        response.put("data", Collections.emptyList());
	    }

	    return response;
	}
	
	
	
	@GetMapping("/getRoadNamesByWard")
	public Map<String, Object> getRoadNamesByWard(@RequestParam String ward) {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> roads = registerService.getRoadNamesByWard(ward);
		if (!roads.isEmpty()) {
			response.put("status", true);
			response.put("message", "Road names fetched successfully");
			response.put("data", roads);
		} else {
			response.put("status", false);
			response.put("message", "No roads found for ward: " + ward);
			response.put("data", Collections.emptyList());
		}
		return response;
	}
	
	
	@GetMapping("/getRoadType")
	public Map<String, Object> getRoadType() {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> roadTypes = registerService.getRoadType();
		if (!roadTypes.isEmpty()) {
			response.put("status", true);
			response.put("message", "Road types fetched successfully");
			response.put("data", roadTypes);
		} else {
			response.put("status", false);
			response.put("message", "No road types found");
			response.put("data", Collections.emptyList());
		}
		return response;
	}
	
	
	@GetMapping("/getRoadTypeMaterial")
	public Map<String, Object> getRoadTypeMaterial() {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> roadTypes = registerService.getRoadTypeMaterial();
		if (!roadTypes.isEmpty()) {
			response.put("status", true);
			response.put("message", "Roadtype Material fetched successfully");
			response.put("data", roadTypes);
		} else {
			response.put("status", false);
			response.put("message", "No roadtype material found");
			response.put("data", Collections.emptyList());
		}
		return response;
	}
	
	@GetMapping("/getFootpathType")
	public Map<String, Object> getFootpathType() {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> roadTypes = registerService.getFootpathType();
		if (!roadTypes.isEmpty()) {
			response.put("status", true);
			response.put("message", "Footpath Types fetched successfully");
			response.put("data", roadTypes);
		} else {
			response.put("status", false);
			response.put("message", "No Footpath Types found");
			response.put("data", Collections.emptyList());
		}
		return response;
	}	
	
	@GetMapping("/getRoadSide")
	public Map<String, Object> getRoadSide() {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> roadTypes = registerService.getRoadSide();
		if (!roadTypes.isEmpty()) {
			response.put("status", true);
			response.put("message", "Road sides fetched successfully");
			response.put("data", roadTypes);
		} else {
			response.put("status", false);
			response.put("message", "No Road sides found");
			response.put("data", Collections.emptyList());
		}
		return response;
	}
	
	@GetMapping("/getMonthList")
	public Map<String, Object> getMonthList() {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> months = registerService.getMonthList();
		if (!months.isEmpty()) {
			response.put("status", true);
			response.put("message", "Month list fetched successfully");
			response.put("data", months);
		} else {
			response.put("status", false);
			response.put("message", "No months found");
			response.put("data", Collections.emptyList());
		}
		return response;
	}
	
	
	
	@GetMapping("/getYearList")
	public Map<String, Object> getYearList() {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> years = registerService.getYearList();
		if (!years.isEmpty()) {
			response.put("status", true);
			response.put("message", "Year list fetched successfully");
			response.put("data", years);
		} else {
			response.put("status", false);
			response.put("message", "No Year found");
			response.put("data", Collections.emptyList());
		}
		return response;
	}
	
	
	@PostMapping("/saveRoadWarDetails")
	public Map<String, Object> saveRoadWarDetails(@RequestBody Map<String, Object> payload) {
		return registerService.saveRoadWarDetails(payload);
	}
	
	
	@GetMapping("/getAllRoadWarDetails")
	public ResponseEntity<?> getAllRoadWarDetails(
			@RequestParam(required = false) String userid,
	        @RequestParam(required = false) String zone,
	        @RequestParam(required = false) String ward,
	        @RequestParam(required = false) String roadId) {

	    try {

	        List<Map<String, Object>> response =
	        		registerService.getAllRoadWarDetails(userid,zone, ward, roadId);

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error",
	                        "Failed to fetch data : " + e.getMessage()));
	    }
	}
	
	
	
	@GetMapping("/getRoadWarDetailsById")
	public ResponseEntity<?> getRoadWarDetailsById(
	        @RequestParam String refid) {

	    try {

	        Map<String, Object> response =
	                registerService.getRoadWarDetailsById(refid);

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {

	        Map<String, Object> error = new HashMap<>();
	        error.put("status", false);
	        error.put("message", "Failed to fetch data : " + e.getMessage());

	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(error);
	    }
	}
	
	@GetMapping("/checkRoadExists")
	public Map<String, Object> checkRoadExists(@RequestParam("roadId") Integer roadId) {
	    return registerService.checkRoadExists(roadId);
	}
	
	@PostMapping("/updateRoadWarDetails")
	public Map<String, Object> updateRoadWarDetails(@RequestBody Map<String, Object> payload) {
		return registerService.updateRoadWarDetails(payload);
	}
	
	@PostMapping("/deleteRoadWarDetails")
	public Map<String, Object> deleteRoadWarDetails(
	        @RequestParam String refId,@RequestParam String updatedby) {

	    return registerService.deleteRoadWarDetails(refId,updatedby);
	}
	
}
