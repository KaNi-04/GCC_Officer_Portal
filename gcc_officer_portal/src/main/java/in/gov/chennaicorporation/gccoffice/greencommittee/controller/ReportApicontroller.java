package in.gov.chennaicorporation.gccoffice.greencommittee.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.greencommittee.service.ReportService;

@RestController
@RequestMapping("/gcc/api/greencommittee/report")
public class ReportApicontroller {

	@Autowired
	private ReportService reportService;

	@GetMapping("/getgreencommitteelist")
	public List<Map<String, Object>> getgreencommitteelist(@RequestParam String meetingid,
			@RequestParam String zones) {

		List<String> refnums = reportService.getrefnums(meetingid, zones);
		System.out.println("refnums=" + refnums);

		List<Map<String, Object>> finalList = new ArrayList<>();

		if (refnums.isEmpty()) {
			return Collections.emptyList();
		}

		for (String ref : refnums) {
			System.out.println("ref=" + ref);
			Map<String, Object> data = reportService.getlistdata(ref);
			if (data != null && !data.isEmpty()) {
				finalList.add(data);
			}
		}

		return finalList;
	}

	@GetMapping("/getabscount")
	public Map<String, Object> getabscount(@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {

		Map<String, Object> response = new HashMap<>();

		try {
			List<Map<String, Object>> data = reportService.getabscount(startDate, endDate);

			response.put("data", data);
			response.put("message", "Zone-wise abstract report");
			response.put("status", "Success");

		} catch (Exception e) {
			e.printStackTrace();

			response.put("data", Collections.emptyList());
			response.put("message", "Failed to fetch abstract report");
			response.put("status", "Error");
		}

		return response;
	}

	@GetMapping("/getWardDetailsByZone")
	public Map<String, Object> getWardDetailsByZone(@RequestParam(required = true) String zone,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {

		Map<String, Object> response = new HashMap<>();

		try {
			List<Map<String, Object>> data = reportService.getWardDetailsByZone(zone, startDate, endDate);

			response.put("data", data);
			response.put("message", "Ward-wise abstract report");
			response.put("status", "Success");

		} catch (Exception e) {
			e.printStackTrace();

			response.put("data", Collections.emptyList());
			response.put("message", "Failed to fetch abstract report");
			response.put("status", "Error");
		}

		return response;
	}

	@GetMapping("/getPendingDetails")
	public Map<String, Object> getPendingDetails(
			@RequestParam String zone,
			@RequestParam(required = false) String ward,
			@RequestParam String type,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {

		Map<String, Object> response = new HashMap<>();

		try {
			List<String> refIds = reportService.getPendingRefIds(zone, ward, type, startDate, endDate);

			List<Map<String, Object>> dataList = new ArrayList<>();

			for (String refId : refIds) {
				Map<String, Object> fullData = reportService.getfulldetailsdata(refId);
				if (fullData != null && !fullData.isEmpty()) {
					dataList.add((Map<String, Object>) fullData.get("data"));
				}
			}

			response.put("data", dataList);
			response.put("message", "Complaint List");
			response.put("status", "Success");

		} catch (Exception e) {
			e.printStackTrace();
			response.put("data", Collections.emptyList());
			response.put("message", "Failed to fetch complaints");
			response.put("status", "Error");
		}

		return response;
	}

}
