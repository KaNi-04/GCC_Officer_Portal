package in.gov.chennaicorporation.gccoffice.roadwar.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}
