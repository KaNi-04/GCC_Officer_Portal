package in.gov.chennaicorporation.gccoffice.school.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.school.Service.SchoolPropertyService;

@RestController
@CrossOrigin()
@RequestMapping("/gcc/api/gccschool/schoolproperty")
public class SchoolPropertyController {

	@Autowired
	private SchoolPropertyService schoolPropertyService;

	@Autowired
	public SchoolPropertyController(SchoolPropertyService schoolPropertyService) {
		this.schoolPropertyService = schoolPropertyService;
	}

	// To Get School Data
	@GetMapping("/getSchoolData")
	public ResponseEntity<Map<String, Object>> getSchoolData(@RequestParam String udise) {
		try {
			List<Map<String, Object>> data = schoolPropertyService.getSchoolData(udise);
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

//   For Other Details	
	@PostMapping(value = "/saveSchoolBuildupArea")
	public String saveSchoolBuildupArea(@RequestParam("land_area") Double land_area,
			@RequestParam("buildup_area") Double buildup_area, @RequestParam("is_compound") int is_compound,
			@RequestParam("is_nameboard") Boolean is_nameboard, @RequestParam("is_pasystem") Boolean is_pasystem,
			@RequestParam("is_bell") Boolean is_bell, @RequestParam("is_generator") Boolean is_generator,
			@RequestParam("school_id") int school_id) {

		return schoolPropertyService.saveSchoolBuildupArea(land_area, buildup_area, is_compound, is_nameboard,
				is_pasystem, is_bell, is_generator, school_id);
	}

//  For Student Strength	
	@PostMapping(value = "/saveStudentStrength")
	public String saveStudentStrength(@RequestParam("total_students") int total_students,
			@RequestParam("total_girls") int total_girls, @RequestParam("total_boys") int total_boys,
			@RequestParam("others") int others, @RequestParam("special_child") int special_child,@RequestParam("school_id") int school_id) {

		return schoolPropertyService.saveStudentStrength(total_students, total_girls, total_boys, others,special_child, school_id);
	}

//  For Staff Strength	
//	@PostMapping(value="/saveStaffStrength")
//	public String saveStaffStrength(
//			@RequestParam("tot_staff") int tot_staff,
//			@RequestParam("sg_staff") int sg_staff,
//			@RequestParam("bt_staff") int bt_staff,
//			@RequestParam("pg_staff") int pg_staff,
//			@RequestParam("temp_staff") int temp_staff,
//			@RequestParam("ngo_staff") int ngo_staff,
//			@RequestParam("kg_staff") int kg_staff,
//			@RequestParam("pt_staff") int pt_staff,
//			@RequestParam("part_staff") int part_staff,
//			@RequestParam("qa_staff") int qa_staff,
//			@RequestParam("kg_ayha") int kg_ayha,
//			@RequestParam("watchman") int watchman,
//			@RequestParam("comp_asst") int comp_asst,
//			@RequestParam("scavengers") int scavengers,
//			@RequestParam("maint_workers") int maint_workers,
//			@RequestParam("school_id") int school_id) {
//
//		return schoolPropertyService.saveStaffStrength(tot_staff, sg_staff, bt_staff,pg_staff, temp_staff, ngo_staff, kg_staff, pt_staff,part_staff, qa_staff, kg_ayha, watchman, comp_asst, scavengers, maint_workers, school_id);
//	}

//  For Toilets	
	@PostMapping(value = "/saveToilets")
	public String saveToilets(@RequestParam(value = "total_toiletBlocks", required = false) Integer total_toiletBlocks,
			@RequestParam("total_male_urinals") int total_male_urinals,
			@RequestParam("total_female_urinals") int total_female_urinals,
			@RequestParam("staff_male_toilets") int staff_male_toilets,
			@RequestParam("staff_female_toilets") int staff_female_toilets,
			@RequestParam("total_wc_male") int total_wc_male, @RequestParam("total_wc_female") int total_wc_female,
			@RequestParam("total_diffAbled_toilet") int total_diffAbled_toilet,
			@RequestParam("is_napkins") Boolean is_napkins, @RequestParam("school_id") int school_id) {

		return schoolPropertyService.saveToilets(total_toiletBlocks, total_male_urinals, total_female_urinals,staff_male_toilets,staff_female_toilets,total_wc_male, total_wc_female,
				total_diffAbled_toilet, is_napkins, school_id);
	}

//  For Kitchen	
	@PostMapping(value = "/saveKitchen")
	public ResponseEntity<Map<String, String>> saveKitchen(@RequestBody Map<String, Object> payload) {
		Map<String, String> response = new HashMap<>();
		try {
			Boolean is_cmbfs = payload.get("is_cmbfs") != null ? Boolean.parseBoolean(payload.get("is_cmbfs").toString()) : null;
			Boolean is_cmbfsIndividual = payload.get("is_cmbfsIndividual") != null ? Boolean.parseBoolean(payload.get("is_cmbfsIndividual").toString()) : null;
			Boolean is_noonmeal = payload.get("is_noonmeal") != null ? Boolean.parseBoolean(payload.get("is_noonmeal").toString()) : null;
			Boolean is_noonIndividual = payload.get("is_noonIndividual") != null ? Boolean.parseBoolean(payload.get("is_noonIndividual").toString()) : null;
			Boolean is_vegGarden = payload.get("is_vegGarden") != null ? Boolean.parseBoolean(payload.get("is_vegGarden").toString()) : null;
			Boolean is_anganwadi = payload.get("is_anganwadi") != null ? Boolean.parseBoolean(payload.get("is_anganwadi").toString()) : null;

		List<Map<String, Object>> anganwadi = (List<Map<String, Object>>) payload.get("anganwadi");
		
		//System.out.println("anganwadi="+ anganwadi);
 
		int school_id = Integer.parseInt(payload.get("school_id").toString());
		

		int kitchen_id =  schoolPropertyService.saveKitchen(is_cmbfs, is_cmbfsIndividual, is_noonmeal, is_noonIndividual,
				is_vegGarden, is_anganwadi, school_id);
		
		if(is_anganwadi) {
			for(Map<String, Object> data:anganwadi) {
				//System.out.println("hi");
				String anganwadi_unique = data.get("anganwadi_unique")!= null ? data.get("anganwadi_unique").toString() : null;											
				Boolean is_anganwadiIndividual = data.get("is_anganwadiIndividual") != null ? Boolean.parseBoolean(data.get("is_anganwadiIndividual").toString()) : null;
				//System.out.printf(anganwadi_unique,is_anganwadiIndividual);
				schoolPropertyService.saveAnganwadi(anganwadi_unique, is_anganwadiIndividual, kitchen_id, school_id);
				schoolPropertyService.updateAnganwadiCheck(anganwadi_unique, school_id);
			}
		}
		
		response.put("status", "OK");
		response.put("message", "Kitchen Details saved successfully");
		return ResponseEntity.ok(response);
		
		}catch (Exception e) {
			e.printStackTrace();
			response.put("status", "ERROR");
			response.put("message", "Failed to save Kitchen details");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

//  For Playground	
	/*@PostMapping(value = "/savePlayground")
	public ResponseEntity<Map<String, String>> savePlayground(@RequestBody Map<String, Object> payload) {
		Map<String, String> response = new HashMap<>();
		try {
			Boolean is_playground = (Boolean) payload.get("is_playground");
			Double playground_area = Double.valueOf(payload.get("playground_area").toString());
			Boolean is_court = (Boolean) payload.get("is_court");
			int school_id = Integer.parseInt(payload.get("school_id").toString());
			// Boolean is_active = (Boolean) payload.get("is_active");

			List<String> equipmentidRaw = (List<String>) payload.get("equipmentid");
			List<String> courtidRaw = (List<String>) payload.get("courtid");

			// Convert string IDs to integers
//			List<Integer> equipmentid = equipmentidRaw.stream().map(Integer::parseInt).toList();
//			List<Integer> courtid = courtidRaw.stream().map(Integer::parseInt).toList();

			int playground_id = schoolPropertyService.savePlayground(is_playground, playground_area, is_court,
					school_id);

			for (String equipment_id : equipmentidRaw) {
				schoolPropertyService.saveEquipmentDetails(equipment_id, school_id, playground_id);
			}

			for (String court_id : courtidRaw) {
				schoolPropertyService.saveCourtDetails(court_id, school_id, playground_id);
			}

			response.put("status", "OK");
			response.put("message", "Playground Details saved successfully");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "ERROR");
			response.put("message", "Failed to save playground details");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}*/
	@PostMapping("/savePlayground")
	public ResponseEntity<Map<String, String>> savePlayground(@RequestBody Map<String, Object> payload) {
	    Map<String, String> response = new HashMap<>();
	    try {
	        Boolean is_playground = (Boolean) payload.get("is_playground");
	        Double playground_area = Double.valueOf(payload.get("playground_area").toString());
	        Boolean is_court = (Boolean) payload.get("is_court");
	        int school_id = Integer.parseInt(payload.get("school_id").toString());

	        List<String> equipmentidRaw = (List<String>) payload.get("equipmentid");
	        List<String> courtidRaw = (List<String>) payload.get("courtid");

	        int playground_id = schoolPropertyService.savePlayground(is_playground, playground_area, is_court, school_id);

	        boolean insertedAny = false;

	        for (String equipment_id : equipmentidRaw) {
	            if (schoolPropertyService.saveEquipmentDetails(equipment_id, school_id, playground_id)) {
	                insertedAny = true;
	            }
	        }

	        for (String court_id : courtidRaw) {
	            if (schoolPropertyService.saveCourtDetails(court_id, school_id, playground_id)) {
	                insertedAny = true;
	            }
	        }

	        if (insertedAny) {
	            response.put("status", "OK");
	            response.put("message", "Playground and related details saved successfully");
	        } else {
	            response.put("status", "SKIPPED");
	            response.put("message", "No new data inserted; records already exist");
	        }

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "ERROR");
	        response.put("message", "Failed to save playground details");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

//initial
	// For Drinking Water
	/*@PostMapping(value = "/saveDrinkingWater")
	public ResponseEntity<Map<String, Object>> saveDrinkingWater(@RequestBody Map<String, Object> requestData) {
		try {
			// Extract data from the map
			List<Map<String, Object>> water = (List<Map<String, Object>>) requestData.get("water");
			Boolean is_purifier = "1".equals(requestData.get("is_purifier"));
			Double purifier_capacity = requestData.get("purifier_capacity") != null
					? Double.valueOf(requestData.get("purifier_capacity").toString())
					: null;
			Boolean is_borewell = "1".equals(requestData.get("is_borewell"));
			Integer borewell_count = requestData.get("borewell_count") != null
					? Integer.valueOf(requestData.get("borewell_count").toString())
					: null;
			Boolean is_storage = "1".equals(requestData.get("is_storage"));
			List<Map<String, Object>> storage = (List<Map<String, Object>>) requestData.get("storage");
			int school_id = Integer.parseInt(requestData.get("school_id").toString());

			Map<String, Object> response = new HashMap<>();

			int drinking_water_id = schoolPropertyService.saveDrinkingWater(is_purifier, purifier_capacity, is_borewell,
					borewell_count, is_storage, school_id);
			for (Map<String, Object> waterSupply : water) {
				int source_water_id = (int) waterSupply.get("source_water_id");
				String supply_type = (String) waterSupply.get("supply_type");
				schoolPropertyService.saveWaterSupply(source_water_id, supply_type, drinking_water_id, school_id);
			}
			if (!storage.isEmpty()) {
				for (Map<String, Object> waterStorage : storage) {
					double capacity = Double.valueOf(waterStorage.get("capacity").toString());
					int storage_water_id = (int) waterStorage.get("storage_water_id");
					schoolPropertyService.saveWaterStorage(capacity, storage_water_id, drinking_water_id, school_id);
				}
			}

			response.put("status", "OK");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Error saving data: " + e.getMessage()));
		}
	}*/
	@PostMapping("/saveDrinkingWater")
	public ResponseEntity<Map<String, Object>> saveDrinkingWater(@RequestBody Map<String, Object> requestData) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        int schoolId = Integer.parseInt(requestData.get("school_id").toString());

	        // Check if data already exists for the school
	        boolean exists = schoolPropertyService.existsDrinkingWaterData(schoolId);

	        if (exists) {
	            // If data exists, respond with SKIPPED status and message
	            response.put("status", "SKIPPED");
	            response.put("message", "Data already exists for this school. Insert skipped.");
	            return ResponseEntity.ok(response);
	        }

	        // If no existing data, proceed with your save logic
	        List<Map<String, Object>> water = (List<Map<String, Object>>) requestData.get("water");
	        Boolean is_purifier = "1".equals(requestData.get("is_purifier"));
	        Double purifier_capacity = requestData.get("purifier_capacity") != null
	                ? Double.valueOf(requestData.get("purifier_capacity").toString())
	                : null;
	        Boolean is_borewell = "1".equals(requestData.get("is_borewell"));
	        Integer borewell_count = requestData.get("borewell_count") != null
	                ? Integer.valueOf(requestData.get("borewell_count").toString())
	                : null;
	        
	        int drinkingwater_tap = requestData.get("drinkingwater_tap") != null
	                ? Integer.valueOf(requestData.get("drinkingwater_tap").toString())
	                : null;
	        int handwash_tap = requestData.get("handwash_tap") != null
	                ? Integer.valueOf(requestData.get("handwash_tap").toString())
	                : null;

	        Boolean is_storage = "1".equals(requestData.get("is_storage"));
	        List<Map<String, Object>> storage = (List<Map<String, Object>>) requestData.get("storage");
/*
	        int drinking_water_id = schoolPropertyService.saveDrinkingWater(is_purifier, purifier_capacity, is_borewell,
	                borewell_count, is_storage, schoolId);
	      */
	        int drinking_water_id = schoolPropertyService.saveDrinkingWater(is_purifier, purifier_capacity, is_borewell,
	                borewell_count, is_storage, schoolId,drinkingwater_tap,handwash_tap);
	        
	        for (Map<String, Object> waterSupply : water) {
	            int source_water_id = (int) waterSupply.get("source_water_id");
	            String supply_type = (String) waterSupply.get("supply_type");
	            schoolPropertyService.saveWaterSupply(source_water_id, supply_type, drinking_water_id, schoolId);
	        }
	        if (!storage.isEmpty()) {
	            for (Map<String, Object> waterStorage : storage) {
	                double capacity = Double.valueOf(waterStorage.get("capacity").toString());
	                int storage_water_id = (int) waterStorage.get("storage_water_id");
	                schoolPropertyService.saveWaterStorage(capacity, storage_water_id, drinking_water_id, schoolId);
	            }
	        }

	        response.put("status", "OK");
	        response.put("message", "Drinking water details submitted successfully!");
	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "ERROR");
	        response.put("message", "Error saving data: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	//initial

	/*@PostMapping("/saveStaffStrength")
	public String saveStaffStrength(@RequestParam int sgRegTeacher, @RequestParam int sgTempTeacher,
			@RequestParam int sgPtTeacher, @RequestParam int sgNgoTeacher, @RequestParam int btRegTeacher,
			@RequestParam int btTempTeacher, @RequestParam int btPtTeacher, @RequestParam int btNgoTeacher,
			@RequestParam int pgRegTeacher, @RequestParam int pgTempTeacher, @RequestParam int pgPtTeacher,
			@RequestParam int pgNgoTeacher, @RequestParam int kgRegTeacher, @RequestParam int kgTempTeacher,
			@RequestParam int petRegTeacher, @RequestParam int petTempTeacher, @RequestParam int petGrade1Teacher,
			@RequestParam int petGrade2Teacher, @RequestParam int totOa, @RequestParam int kgAyah,
			@RequestParam int compAss, @RequestParam int watchmen, @RequestParam int scavengers,
			@RequestParam int maintenanceWork, @RequestParam int schoolId) {

		// Call service method
		schoolPropertyService.saveStaffStrength(sgRegTeacher, sgTempTeacher, sgPtTeacher, sgNgoTeacher, btRegTeacher,
				btTempTeacher, btPtTeacher, btNgoTeacher, pgRegTeacher, pgTempTeacher, pgPtTeacher, pgNgoTeacher,
				kgRegTeacher, kgTempTeacher, petRegTeacher, petTempTeacher, petGrade1Teacher, petGrade2Teacher, totOa,
				kgAyah, compAss, watchmen, scavengers, maintenanceWork, schoolId);

		return "Staff strength details saved successfully!";
	}*/
	@PostMapping("/saveStaffStrength")
	public String saveStaffStrength(@RequestBody Map<String, Object> payload) {
		
		int sgRegTeacher = Integer.parseInt(payload.get("sgRegTeacher").toString());
		int sgTempTeacher = Integer.parseInt(payload.get("sgTempTeacher").toString());
		int sgPtTeacher = Integer.parseInt(payload.get("sgPtTeacher").toString());
		int sgNgoTeacher = Integer.parseInt(payload.get("sgNgoTeacher").toString());
		int btRegTeacher = Integer.parseInt(payload.get("btRegTeacher").toString());
		int btTempTeacher = Integer.parseInt(payload.get("btTempTeacher").toString());
		int btPtTeacher = Integer.parseInt(payload.get("btPtTeacher").toString());
		int btNgoTeacher = Integer.parseInt(payload.get("btNgoTeacher").toString());
		int pgRegTeacher = Integer.parseInt(payload.get("pgRegTeacher").toString());
		int pgTempTeacher = Integer.parseInt(payload.get("pgTempTeacher").toString());
		int pgPtTeacher = Integer.parseInt(payload.get("pgPtTeacher").toString());
		int pgNgoTeacher = Integer.parseInt(payload.get("pgNgoTeacher").toString());
		int kgRegTeacher = Integer.parseInt(payload.get("kgRegTeacher").toString());
		int kgTempTeacher = Integer.parseInt(payload.get("kgTempTeacher").toString());
		int petRegTeacher = Integer.parseInt(payload.get("petRegTeacher").toString());
		int petTempTeacher = Integer.parseInt(payload.get("petTempTeacher").toString());
		int petGrade1Teacher = Integer.parseInt(payload.get("petGrade1Teacher").toString());
		int petGrade2Teacher = Integer.parseInt(payload.get("petGrade2Teacher").toString());
		
		int petPtTeacher = Integer.parseInt(payload.get("petPtTeacher").toString());
		Boolean is_hm = (Boolean) payload.get("hmAvailability");
		int stRegTeacher = Integer.parseInt(payload.get("stRegTeacher").toString());
		int stPtTeacher = Integer.parseInt(payload.get("stPtTeacher").toString());
		
		int totOa = Integer.parseInt(payload.get("totOa").toString());
		int kgAyah = Integer.parseInt(payload.get("kgAyah").toString());
		int compAss = Integer.parseInt(payload.get("compAss").toString());
		String watchmen = payload.get("watchmen").toString();
		int scavengers = Integer.parseInt(payload.get("scavengers").toString());
		int maintenanceWork = Integer.parseInt(payload.get("maintenanceWork").toString());
		int schoolId = Integer.parseInt(payload.get("schoolId").toString());
		
		List<String> specialCategoryId = (List<String>) payload.get("specialCategoryId");
     
		return schoolPropertyService.saveStaffStrength(sgRegTeacher, sgTempTeacher, sgPtTeacher, sgNgoTeacher, btRegTeacher,
				btTempTeacher, btPtTeacher, btNgoTeacher, pgRegTeacher, pgTempTeacher, pgPtTeacher, pgNgoTeacher,
				kgRegTeacher, kgTempTeacher, petRegTeacher, petTempTeacher, petGrade1Teacher, petGrade2Teacher, petPtTeacher,
				is_hm, stRegTeacher, stPtTeacher, totOa, kgAyah, compAss, watchmen, scavengers, maintenanceWork, schoolId, specialCategoryId);


	}


	// For login
	@GetMapping(value = "/getLoginDetails")
	public List<Map<String, Object>> getLoginDetails(@RequestParam String user_id) {
		List<Map<String, Object>> schoolData = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();
		String schoolId = schoolPropertyService.getSchoolId(user_id);
		if (schoolId == null || schoolId.trim().isEmpty() || schoolId.equalsIgnoreCase("error")) {
			response.put("Message", "Failure");
			response.put("Data", List.of());
			schoolData.add(response);
			return schoolData;
		}
		List<Map<String, Object>> sch = schoolPropertyService.getSchoolData1(schoolId);
		if (!sch.isEmpty()) {
			response.put("Message", "Success");
			response.put("Data", sch);
			schoolData.add(response);
			return schoolData;
		} else {
			response.put("Message", "No Data Found");
			response.put("Data", List.of());
			schoolData.add(response);
			return schoolData;
		}
	}

	@GetMapping("/getSchoolDetails")
	public ResponseEntity<Map<String, Object>> getSchoolDetails(@RequestParam Long schoolId) {
		try {
			Map<String, Object> data = schoolPropertyService.getSchoolDetails(schoolId);
			if (data != null && !data.isEmpty()) {
				return ResponseEntity.ok(data);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("status", "Failed", "message", "No school found with given ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", e.getMessage()));
		}
	}

	@GetMapping("/filterSchools")
	public ResponseEntity<?> filterSchools(@RequestParam String user_id, @RequestParam(required = false) String udise,
			@RequestParam(required = false) String category, @RequestParam(required = false) String zone,
			@RequestParam(required = false) String ward) {

		List<Map<String, Object>> filteredSchools = schoolPropertyService.filterSchools(user_id, udise, category, zone,
				ward);

		Map<String, Object> result = new HashMap<>();
		result.put("Message", "Success");
		result.put("Data", filteredSchools);

		return ResponseEntity.ok(Collections.singletonList(result));
	}

	@PostMapping("/updateOtherDetails")
	public String updateOtherDetails(@RequestParam("land_area") Double land_area,
			@RequestParam("buildup_area") Double buildup_area, @RequestParam("is_compound") int is_compound,
			@RequestParam("is_nameboard") Boolean is_nameboard, @RequestParam("is_pasystem") Boolean is_pasystem,
			@RequestParam("is_bell") Boolean is_bell, @RequestParam("is_generator") Boolean is_generator,
			@RequestParam("school_id") int school_id) {
		try {
			return schoolPropertyService.updateOtherDetails(land_area, buildup_area, is_compound, is_nameboard,
					is_pasystem, is_bell, is_generator, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}

	@PostMapping(value = "/updateStudentDetails")
	public String updateStudentDetails(@RequestParam("total_students") int total_students,
			@RequestParam("total_girls") int total_girls, @RequestParam("total_boys") int total_boys,
			@RequestParam("others") int others,@RequestParam("special_child") int special_child , @RequestParam("school_id") int school_id) {
		
		try {
			return schoolPropertyService.updateStudentDetails(total_students, total_girls, total_boys, others,special_child,
					school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}

	@GetMapping("/checkPlaygroundDetails")
	public ResponseEntity<?> checkPlaygroundDetails(@RequestParam int school_id) {

		Map<String, Object> playgroundDetails = schoolPropertyService.checkPlaygroundDetails(school_id);
		List<Map<String, Object>> courtDetails = schoolPropertyService.checkCourtDetails(school_id);
		List<Map<String, Object>> equipmentDetails = schoolPropertyService.checkEquipmentDetails(school_id);

		Map<String, Object> result = new HashMap<>();
		result.put("Message", "Success");
		result.put("playgroundDetails", playgroundDetails);
		result.put("CourtDetails", courtDetails);
		result.put("EquipmentDetails", equipmentDetails);

		return ResponseEntity.ok(Collections.singletonList(result));
	}

	/*
	 * @PostMapping(value = "/updatePlayground") public ResponseEntity<String>
	 * updatePlayground(@RequestBody Map<String, Object> payload) { try { Boolean
	 * is_playground = (Boolean) payload.get("is_playground"); Double
	 * playground_area = Double.valueOf(payload.get("playground_area").toString());
	 * Boolean is_court = (Boolean) payload.get("is_court"); int school_id =
	 * Integer.parseInt(payload.get("school_id").toString()); int playground_id =
	 * Integer.parseInt(payload.get("playground_id").toString());
	 * 
	 * List<Map<String, Object>> equipments = (List<Map<String, Object>>)
	 * payload.get("equipments"); List<Map<String, Object>> courts =
	 * (List<Map<String, Object>>) payload.get("courts");
	 * 
	 * String result = schoolPropertyService.updatePlaygroundDetails( is_playground,
	 * playground_area, is_court, equipments, courts, playground_id, school_id );
	 * return ResponseEntity.ok(result);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return
	 * ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error"); } }
	 */
//	@PostMapping(value = "/updatePlayground")
//	public ResponseEntity<Map<String, String>> updatePlayground(@RequestBody Map<String, Object> payload) {
//		Map<String, String> response = new HashMap<>();
//		try {
//			Boolean is_playground = (Boolean) payload.get("is_playground");
//			Double playground_area = Double.valueOf(payload.get("playground_area").toString());
//			Boolean is_court = (Boolean) payload.get("is_court");
//			int school_id = Integer.parseInt(payload.get("school_id").toString());
//			//Boolean is_active = (Boolean) payload.get("is_active");
//			int playground_id = Integer.parseInt(payload.get("playground_id").toString());
//			
//
//			List<String> equipmentidRaw = (List<String>) payload.get("equipments");
//			List<String> courtidRaw = (List<String>) payload.get("courts");
//
//			// Convert string IDs to integers
//			List<Integer> equipmentid = equipmentidRaw.stream().map(Integer::parseInt).toList();
//			List<Integer> courtid = courtidRaw.stream().map(Integer::parseInt).toList();
//			schoolPropertyService.deactiveCourts(school_id);
//			schoolPropertyService.deactiveEquipments(school_id);
//			schoolPropertyService.updatePlayground(is_playground, playground_area, is_court, school_id, playground_id);
//			//int playground_id = schoolPropertyService.savePlayground(is_playground, playground_area, is_court,
//			//		school_id);
//
//			for (Integer equipment_id : equipmentid) {
//				schoolPropertyService.saveEquipmentDetails(equipment_id, school_id, playground_id);
//			}
//
//			for (Integer court_id : courtid) {
//				schoolPropertyService.saveCourtDetails(court_id, school_id, playground_id);
//			}
//
//			response.put("status", "OK");
//			response.put("message", "Playground Details updated successfully");
//			return ResponseEntity.ok(response);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.put("status", "ERROR");
//			response.put("message", "Failed to updated playground details");
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//	}

	@PostMapping(value = "/updatePlayground")
	public ResponseEntity<Map<String, String>> updatePlayground(@RequestBody Map<String, Object> payload) {
		Map<String, String> response = new HashMap<>();
		try {
			Boolean is_playground = (Boolean) payload.get("is_playground");
			Double playground_area = Double.valueOf(payload.get("playground_area").toString());
			Boolean is_court = (Boolean) payload.get("is_court");
			int school_id = Integer.parseInt(payload.get("school_id").toString());
			int playground_id = Integer.parseInt(payload.get("playground_id").toString());

			List<Map<String, Object>> equipmentList = (List<Map<String, Object>>) payload.get("equipments");
			List<Map<String, Object>> courtList = (List<Map<String, Object>>) payload.get("courts");

			String status = schoolPropertyService.updatePlaygroundDetails(is_playground, playground_area, is_court,
					equipmentList, courtList, playground_id, school_id);

			response.put("status", status.equals("Success") ? "OK" : "ERROR");
			response.put("message",
					status.equals("Success") ? "Playground updated successfully" : "Failed to update playground");

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "ERROR");
			response.put("message", "Exception occurred during update");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping(value = "/updateKitchenDetails")
	public ResponseEntity<Map<String, String>> updateKitchenDetails(@RequestBody Map<String, Object> payload) {
		Map<String, String> response = new HashMap<>();
		try {
			
			Map<String, Object> kitchenEdit = (Map<String, Object>) payload.get("kitchenEdit");
			System.err.println("kitchenEdit="+kitchenEdit);
			List<Map<String, Object>> oldAnganwadiEdit = (List<Map<String, Object>>) payload.get("oldAnganwadiEdit");
			List<Map<String, Object>> newAnganwadiEdit = (List<Map<String, Object>>) payload.get("newAnganwadiEdit");
			
			Boolean is_cmbfs = kitchenEdit.get("is_cmbfs") != null ? Boolean.parseBoolean(kitchenEdit.get("is_cmbfs").toString()) : null;
			Boolean is_cmbfsIndividual = kitchenEdit.get("is_cmbfsIndividual") != null ? Boolean.parseBoolean(kitchenEdit.get("is_cmbfsIndividual").toString()) : null;
			Boolean is_noonmeal = kitchenEdit.get("is_noonmeal") != null ? Boolean.parseBoolean(kitchenEdit.get("is_noonmeal").toString()) : null;
			Boolean is_noonIndividual = kitchenEdit.get("is_noonIndividual") != null ? Boolean.parseBoolean(kitchenEdit.get("is_noonIndividual").toString()) : null;
			Boolean is_vegGarden = kitchenEdit.get("is_vegGarden") != null ? Boolean.parseBoolean(kitchenEdit.get("is_vegGarden").toString()) : null;
			Boolean is_anganwadi = kitchenEdit.get("is_anganwadi") != null ? Boolean.parseBoolean(kitchenEdit.get("is_anganwadi").toString()) : null;
			
			int school_id = kitchenEdit.get("school_id") != null ?Integer.parseInt(kitchenEdit.get("school_id").toString()) : null;
			
			
			schoolPropertyService.updateKitchenDetails(is_cmbfs, is_cmbfsIndividual, is_noonmeal, is_noonIndividual, is_vegGarden, is_anganwadi,school_id);
			
			if(is_anganwadi) {
			
				if(oldAnganwadiEdit != null) {
					
					for(Map<String, Object> oldlist:oldAnganwadiEdit) {
						
						String anganwadi_unique = oldlist.get("anganwadi_unique")!= null ? oldlist.get("anganwadi_unique").toString() : null;											
						Boolean is_anganwadiIndividual = oldlist.get("is_anganwadiIndividual") != null ? Boolean.parseBoolean(oldlist.get("is_anganwadiIndividual").toString()) : null;
						schoolPropertyService.saveOldAnganwadi(anganwadi_unique, is_anganwadiIndividual, school_id);
					}
					
				}
				
			if(newAnganwadiEdit != null) {
				
				int kitchen_id=schoolPropertyService.getKitchenIdbySchId(school_id);
				
				for(Map<String, Object> newlist:newAnganwadiEdit) {
					
					String new_anganwadi_unique = newlist.get("anganwadi_unique")!= null ? newlist.get("anganwadi_unique").toString() : null;											
					Boolean new_is_anganwadiIndividual = newlist.get("is_anganwadiIndividual") != null ? Boolean.parseBoolean(newlist.get("is_anganwadiIndividual").toString()) : null;
					schoolPropertyService.saveNewAnganwadi(new_anganwadi_unique, new_is_anganwadiIndividual,kitchen_id,school_id);
					schoolPropertyService.updateAnganwadiCheck(new_anganwadi_unique, school_id);
				}
				
			}
			
			
		}
			
			response.put("status", "OK");
			response.put("message", "Kitchen Details Updated successfully");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "ERROR");
			response.put("message", "Failed to save Kitchen details");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

	}

	@GetMapping("/checkKitchenDetails")
	public ResponseEntity<?> checkKitchenDetails(@RequestParam int school_id) {

		Map<String, Object> kitchenDetails = schoolPropertyService.checkKitchenDetails(school_id);
		List<Map<String, Object>> oldAnganwadiDetails = schoolPropertyService.checkOldAnganwadiDetails(school_id);
		List<Map<String, Object>> newAnganwadiDetails = schoolPropertyService.checkNewAnganwadiDetails(school_id);

		Map<String, Object> result = new HashMap<>();
		result.put("Message", "Success");
		result.put("kitchenDetails", kitchenDetails);
		result.put("anganwadiDetails", oldAnganwadiDetails);
		result.put("newAnganwadiDetails", newAnganwadiDetails);

		return ResponseEntity.ok(Collections.singletonList(result));
	}

	@PostMapping("/updateStaffDetails")
	public String updateStaffDetails(@RequestBody Map<String, Object> payload) {
		
		int sgRegTeacher = Integer.parseInt(payload.get("modal_sgRegTeacher").toString());
		int sgTempTeacher = Integer.parseInt(payload.get("modal_sgTempTeacher").toString());
		int sgPtTeacher = Integer.parseInt(payload.get("modal_sgPtTeacher").toString());
		int sgNgoTeacher = Integer.parseInt(payload.get("modal_sgNgoTeacher").toString());
		int btRegTeacher = Integer.parseInt(payload.get("modal_btRegTeacher").toString());
		int btTempTeacher = Integer.parseInt(payload.get("modal_btTempTeacher").toString());
		int btPtTeacher = Integer.parseInt(payload.get("modal_btPtTeacher").toString());
		int btNgoTeacher = Integer.parseInt(payload.get("modal_btNgoTeacher").toString());
		int pgRegTeacher = Integer.parseInt(payload.get("modal_pgRegTeacher").toString());
		int pgTempTeacher = Integer.parseInt(payload.get("modal_pgTempTeacher").toString());
		int pgPtTeacher = Integer.parseInt(payload.get("modal_pgPtTeacher").toString());
		int pgNgoTeacher = Integer.parseInt(payload.get("modal_pgNgoTeacher").toString());
		int kgRegTeacher = Integer.parseInt(payload.get("modal_kgRegTeacher").toString());
		int kgTempTeacher = Integer.parseInt(payload.get("modal_kgTempTeacher").toString());
		int petRegTeacher = Integer.parseInt(payload.get("modal_petRegTeacher").toString());
		int petTempTeacher = Integer.parseInt(payload.get("modal_petTempTeacher").toString());
		int petGrade1Teacher = Integer.parseInt(payload.get("modal_petg1Teacher").toString());
		int petGrade2Teacher = Integer.parseInt(payload.get("modal_petg2Teacher").toString());
		
		int petPtTeacher = Integer.parseInt(payload.get("modal_petptTeacher").toString());
		Boolean is_hm = (Boolean) payload.get("is_hmAvailability");
		int stRegTeacher = Integer.parseInt(payload.get("modal_stRegTeacher").toString());
		int stPtTeacher = Integer.parseInt(payload.get("modal_stPtTeacher").toString());
		
		int totOa = Integer.parseInt(payload.get("modal_totOa").toString());
		int kgAyah = Integer.parseInt(payload.get("modal_kgAyah").toString());
		int compAss = Integer.parseInt(payload.get("modal_compAss").toString());
		String watchmen = payload.get("modal_watchmen").toString();
		int scavengers = Integer.parseInt(payload.get("modal_scavengers").toString());
		int maintenanceWork = Integer.parseInt(payload.get("modal_maintenanceWork").toString());
		int schoolId = Integer.parseInt(payload.get("schoolId").toString());
		
		List<String> specialCategoryId = (List<String>) payload.get("specialCategoryId");
     
		return schoolPropertyService.updateStaffStrength(sgRegTeacher, sgTempTeacher, sgPtTeacher, sgNgoTeacher, btRegTeacher,
				btTempTeacher, btPtTeacher, btNgoTeacher, pgRegTeacher, pgTempTeacher, pgPtTeacher, pgNgoTeacher,
				kgRegTeacher, kgTempTeacher, petRegTeacher, petTempTeacher, petGrade1Teacher, petGrade2Teacher, petPtTeacher,
				is_hm, stRegTeacher, stPtTeacher, totOa, kgAyah, compAss, watchmen, scavengers, maintenanceWork, schoolId, specialCategoryId);


	}
	/*
	 * @GetMapping("/checkWaterDetails") public ResponseEntity<?> checkWaterDetails(
	 * 
	 * @RequestParam int school_id) {
	 * 
	 * Map<String, Object> drinkingWater =
	 * schoolPropertyService.checkWaterDetails(school_id); List<Map<String, Object>>
	 * waterSource = schoolPropertyService.checkWaterSource(school_id);
	 * List<Map<String, Object>> waterStorage =
	 * schoolPropertyService.checkWaterStotage(school_id);
	 * 
	 * Map<String, Object> result = new HashMap<>(); result.put("Message",
	 * "Success"); result.put("DrinkingWater", drinkingWater);
	 * result.put("WaterSource", waterSource); result.put("WaterStorage",
	 * waterStorage);
	 * 
	 * return ResponseEntity.ok(Collections.singletonList(result)); }
	 */
//	@PostMapping(value = "/updateDrinkingWater")
//	public String updateDrinkingWater(@RequestParam("is_purifier") Boolean is_purifier, @RequestParam("purifier_capacity") Double purifier_capacity, 
//			                       @RequestParam("is_borewell") Boolean is_borewell, @RequestParam("borewell_count") int borewell_count,  @RequestParam("is_storage") Boolean is_storage,
//			                       @RequestParam("drinking_water_id") int drinking_water_id, @RequestParam("school_id") int school_id, @RequestParam("supply") List<Map<String, Object>> supply,
//			                       @RequestParam("storage") List<Map<String, Object>> storage) {
//		try {
//
//			return schoolPropertyService.updateDrinkingWater(is_purifier, purifier_capacity, is_borewell, borewell_count, is_storage, drinking_water_id, school_id, supply, storage);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "Error";
//		}
//
//	}

	@PostMapping(value = "/updateDrinkingWater")
	public ResponseEntity<Map<String, Object>> updateDrinkingWater(@RequestBody Map<String, Object> requestData) {
		try {
			System.out.println("Received update payload: " + requestData);

			// Extract data from the map
			// List<Map<String, Object>> water = (List<Map<String, Object>>)
			// requestData.get("water");
			List<Map<String, Object>> water = (List<Map<String, Object>>) requestData.getOrDefault("supply",
					new ArrayList<>());
			List<Map<String, Object>> storage = (List<Map<String, Object>>) requestData.getOrDefault("storage",
					new ArrayList<>());

			Boolean is_purifier = (Boolean) requestData.get("is_purifier");
			Double purifier_capacity = requestData.get("purifier_capacity") != null
					? Double.valueOf(requestData.get("purifier_capacity").toString())
					: 0;
			Boolean is_borewell = (Boolean) requestData.get("is_borewell");
			Integer borewell_count = requestData.get("borewell_count") != null
					? Integer.valueOf(requestData.get("borewell_count").toString())
					: 0;
			
			int drinkingwater_tap = requestData.get("drinkingwater_tap") != null
					? Integer.valueOf(requestData.get("drinkingwater_tap").toString())
					: 0;
			int  handwash_tap= requestData.get("handwash_tap") != null
					? Integer.valueOf(requestData.get("handwash_tap").toString())
					: 0;

			Boolean is_storage = (Boolean) requestData.get("is_storage");
			// List<Map<String, Object>> storage = (List<Map<String, Object>>)
			// requestData.get("storage");
			int school_id = Integer.parseInt(requestData.get("school_id").toString());
			int drinking_water_id = Integer.parseInt(requestData.get("drinking_water_id").toString());

			Map<String, Object> response = new HashMap<>();
/*
			schoolPropertyService.updateDrinkWater(is_purifier, purifier_capacity, is_borewell, borewell_count,
					is_storage, school_id, drinking_water_id);
		*/	
			schoolPropertyService.updateDrinkWater(is_purifier, purifier_capacity, is_borewell, borewell_count,
					is_storage, school_id, drinking_water_id,drinkingwater_tap,handwash_tap);
			
			schoolPropertyService.deactiveWaterSupply(school_id, drinking_water_id);
			schoolPropertyService.deactiveWaterStorage(school_id, drinking_water_id);
			for (Map<String, Object> waterSupply : water) {
				int source_water_id = (int) waterSupply.get("source_water_id");
				String supply_type = (String) waterSupply.get("supply_type");
				schoolPropertyService.saveWaterSupply(source_water_id, supply_type, drinking_water_id, school_id);
			}
			if (!storage.isEmpty()) {
				for (Map<String, Object> waterStorage : storage) {
					double capacity = Double.valueOf(waterStorage.get("capacity").toString());
					int storage_water_id = (int) waterStorage.get("storage_water_id");
					schoolPropertyService.saveWaterStorage(capacity, storage_water_id, drinking_water_id, school_id);
				}
			}

			response.put("status", "OK");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Error saving data: " + e.getMessage()));
		}
	}

	@GetMapping("/checkWaterDetails")
	public ResponseEntity<Map<String, Object>> checkWaterDetails(@RequestParam int school_id) {
		System.out.println("CHECK 11");
		try {
			System.out.println("CHECK 1");
			Map<String, Object> drinkingWater = schoolPropertyService.checkWaterDetails(school_id);
			List<Map<String, Object>> waterSource = schoolPropertyService.checkWaterSource(school_id);
			List<Map<String, Object>> waterStorage = schoolPropertyService.checkWaterStotage(school_id);
			System.out.println("WATER SOURCE = " + waterSource);

			Map<String, Object> response = new HashMap<>();
			response.put("DrinkingWater", drinkingWater);
			response.put("WaterSource", waterSource);
			response.put("WaterStorage", waterStorage);

			// ✅ Set status based on content presence
			if (drinkingWater != null && !drinkingWater.isEmpty()) {
				response.put("status", "OK");
			} else {
				response.put("status", "NEW");
			}

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "Error", "message", "Could not load drinking water data"));
		}
	}

	@GetMapping("/checkToiletDetails")
	public ResponseEntity<?> checkToiletDetails(@RequestParam int school_id) {

		List<Map<String, Object>> toiletDetails = schoolPropertyService.checkToiletDetails(school_id);

		Map<String, Object> result = new HashMap<>();
		result.put("Message", "Success");
		result.put("Data", toiletDetails);

		return ResponseEntity.ok(Collections.singletonList(result));
	}

	@PostMapping(value = "/updateToiletDetails")
	public String updateToiletDetails(@RequestParam(value = "total_toiletBlocks", required = false) Integer total_toiletBlocks,
			@RequestParam("total_male_urinals") int total_male_urinals,
			@RequestParam("total_female_urinals") int total_female_urinals,
			@RequestParam("staff_male_toilets") int staff_male_toilets,
			@RequestParam("staff_female_toilets") int staff_female_toilets,
			@RequestParam("total_wc_male") int total_wc_male, @RequestParam("total_wc_female") int total_wc_female,
			@RequestParam("total_diffAbled_toilet") int total_diffAbled_toilet,
			@RequestParam("is_napkins") Boolean is_napkins, @RequestParam("school_id") int school_id) {
		try {

			return schoolPropertyService.updateToiletDetails(total_toiletBlocks, total_male_urinals,total_female_urinals,staff_male_toilets,staff_female_toilets, total_wc_male,
					total_wc_female, total_diffAbled_toilet, is_napkins, school_id);

		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}

	@PostMapping(value = "/saveAssetMaster")
	public ResponseEntity<Map<String, String>> saveAssetMaster(@RequestBody Map<String, Object> payload) {
		Map<String, String> response = new HashMap<>();
		String status = "";
		try {

			int school_id = Integer.parseInt(payload.get("school_id").toString());
			int asset_id = Integer.parseInt(payload.get("asset_id").toString());

			List<Map<String, Object>> equipmentList = (List<Map<String, Object>>) payload.get("data");

			for (Map<String, Object> asset : equipmentList) {
				int sub_asset_id = Integer.parseInt(asset.get("sub_asset_id").toString());
				int master_qty = Integer.parseInt(asset.get("master_qty").toString());

				status = schoolPropertyService.saveAssetMaster(sub_asset_id, master_qty, school_id, asset_id);

			}

			response.put("status", status.equals("Success") ? "OK" : "ERROR");
			response.put("message",
					status.equals("Success") ? "Asset Master Saved successfully" : "Failed to Save Asset Master");

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "ERROR");
			response.put("message", "Exception occurred during update");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/getAssetMaster")
	public ResponseEntity<?> getAssetMaster(@RequestParam int school_id, @RequestParam int asset_id) {

		List<Map<String, Object>> data = schoolPropertyService.getAssetMaster(school_id, asset_id);
		// System.out.println("data=="+data);
		Map<String, Object> result = new HashMap<>();
		result.put("status", "Success");
		result.put("data", data);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/getAssetMasterById")
	public ResponseEntity<?> getAssetMasterById(@RequestParam int assetMasterId, @RequestParam int asset_id) {

		Map<String, Object> data = schoolPropertyService.getAssetMasterById(assetMasterId, asset_id);

		Map<String, Object> result = new HashMap<>();
		result.put("status", "Success");
		result.put("data", data);

		return ResponseEntity.ok(result);
	}

	@PostMapping(value = "/updateAssetMaster")
	public String updateStudentDetails(@RequestParam("master_qty") int master_qty,
			@RequestParam("assetMasterId") int assetMasterId, @RequestParam("asset_id") int asset_id) {

		try {
			Map<String, Object> assetMaster = schoolPropertyService.getAssetMasterById(assetMasterId, asset_id);
			int qty = (Integer) assetMaster.get("master_qty");
			int unmapped_asset = (Integer) assetMaster.get("unmapped_qty");
			int mapped_asset = qty - unmapped_asset;
			int master = 0;
			int unmapped = 0;
			
			if(master_qty>=mapped_asset) {
				 master = master_qty;				 
				 unmapped = master_qty - mapped_asset;
				 return schoolPropertyService.updateAssetMaster(assetMasterId, master, asset_id, unmapped);
			}
			else {
				return "lesser than";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}

	@GetMapping("/getAnganwadiDetails")
	public ResponseEntity<Map<String, Object>> getAnganwadiDetailsBySchId(@RequestParam int school_id) {
		try {
			List<Map<String, Object>> data = schoolPropertyService.getAnganwadiDetailsBySchId(school_id);
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
}
