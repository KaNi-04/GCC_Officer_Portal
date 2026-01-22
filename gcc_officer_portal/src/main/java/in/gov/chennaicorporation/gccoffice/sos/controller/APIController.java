package in.gov.chennaicorporation.gccoffice.sos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.sos.service.SOSService;

@RequestMapping("gcc/api/sos")
@RestController("sosRest") 
public class APIController {
	private SOSService sosService;
	
	@Autowired
	public APIController(SOSService sosService) {
		this.sosService = sosService;
	}
	
	@GetMapping(value="/getZoneAndWard")
	public List getZoneAndWard() {
		return sosService.getZoneAndWard();
	}
	
	@GetMapping(value="/getRescueList")
	public List getRescueList(@RequestParam(value = "", required = false) String status) {
		return sosService.getRescueList(status);
	}
	
	@GetMapping(value="/getRescueUpdateData")
	public List getRequestDataById(@RequestParam(value = "", required = false) String rescueid) {
		return sosService.getRequestDataById(rescueid);
	}
	
	@PostMapping(value="/saveRequest")
	public List<?> saveRequest(
			@RequestParam(value="contact_name", required = false) String contact_name,
			@RequestParam(value="contact_number", required = false) String contact_number,
			@RequestParam(value="latitude", required = false) String latitude,
			@RequestParam(value="longitude", required = false) String longitude,
			@RequestParam(value="zone", required = false) String zone,
			@RequestParam(value="ward", required = false) String ward,
			@RequestParam(value="streetid", required = false) String streetid,
			@RequestParam(value="streetname", required = false) String streetname,
			@RequestParam(value="location_details", required = false) String location_details,
			@RequestParam(value="request_type", required = false) String request_type,
			@RequestParam(value="no_of_count", required = false) String no_of_count,
			@RequestParam(value="if_any", required = false) String if_any,
			@RequestParam(value="land_mark", required = false) String land_mark,
			@RequestParam(value="remarks", required = false) String remarks,
			@RequestParam(value="loginId", required = false) String loginId,
			@RequestParam(value="mode", required = false) String mode
			){
		return sosService.saveRequest(contact_name, contact_number, 
				latitude, longitude, zone, ward, streetid, streetname, 
				location_details, request_type, no_of_count, if_any, 
				land_mark, remarks, loginId,mode);
	}
	
}
