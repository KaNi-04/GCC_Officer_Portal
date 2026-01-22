package in.gov.chennaicorporation.gccoffice.school.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SchoolReportService {
	private JdbcTemplate jdbcTemplate;

	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public void setDataSourcemysql(@Qualifier("mysqlSchoolDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Transactional
	public ResponseEntity<?> getAEOList() {
		Map<String, Object> result = new HashMap<>();
		String sql = "SELECT aeo_details_id, aeo_num, udise FROM aeo_details";

		try {
			List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
			result.put("Message", "Success");
			result.put("data", data);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("Message", "Error");
			result.put("Error", "Failed to fetch AEO List: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	@Transactional
	public ResponseEntity<?> getAEOZone(String udise) {
		Map<String, Object> result = new HashMap<>();

		String sql = "SELECT DISTINCT zone FROM school_list WHERE udise In(" + udise + ") ORDER BY zone ASC";
		try {
			List<String> data = jdbcTemplate.queryForList(sql, String.class);
			result.put("message", "Success");
			result.put("data", data);
			return ResponseEntity.ok(result);
		} catch (DataAccessException e) {
			e.printStackTrace();
			result.put("message", "Error");
			result.put("Error", "Failed to Zone List: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	@Transactional
	public ResponseEntity<?> getAEOWard(String udise, String zone) {
		Map<String, Object> result = new HashMap<>();

		String sql = "SELECT DISTINCT division AS Ward FROM school_list WHERE udise In(" + udise + ") and zone = ? ORDER BY Ward ASC";
		try {
			List<String> data = jdbcTemplate.queryForList(sql, String.class, zone);
			result.put("message", "Success");
			result.put("data", data);
			return ResponseEntity.ok(result);
		} catch (DataAccessException e) {
			e.printStackTrace();
			result.put("message", "Error");
			result.put("Error", "Failed to Ward List: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	@Transactional
	public ResponseEntity<?> getAEOSchoolData(List<String> udise, String zone, String ward) {
		Map<String, Object> response = new HashMap<>();
		
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		if(udise==null || udise.isEmpty()) {
			udise=getUDISE();
		}
		for(String udises:udise) {
		List<String> school = getschoolId(udises, zone, ward);
		if(!school.isEmpty() && school!=null) {
			for(String sch:school) {
				//System.out.println("School_ID = "+sch);
				
				Map<String, Object> result = new HashMap<>();
				Map<String, Object> report = getSchoolReport(sch);
				result.putAll(report);
				String num = (String) report.get("udise");
				Map<String, Object> report1 = getSchoolReport1(sch);
				result.putAll(report1);
				int pending=0;
				for (Map.Entry<String, Object> entry : report1.entrySet()) {
					if(entry.getValue().toString().equalsIgnoreCase("Pending")) {
						
						++pending;
					}
		        }
				 result.put("overall_status", pending==0 ? "Completed" : "Pending");
				 
				 Map<String, Object> report2 = getAEO(num);
				 result.putAll(report2);
				data.add(result);
				
			}
			response.put("message", "Success");
			response.put("data", data);			
		} 
		}
//		System.out.println(data.get(0));
//		System.out.println(data.get(1));
		System.out.println(response.get("message"));
//		System.out.println(response.get("data"));
		return ResponseEntity.ok(response);

	}
	
	private List<String> getUDISE() {
		String sql = "SELECT udise FROM aeo_details";
		try {
			 return jdbcTemplate.queryForList(sql, String.class);

		} catch (Exception e) {
	        e.printStackTrace();
	        return List.of(); 
	    } 
	}

	private Map<String, Object> getAEO(String num) {
		String sql = "SELECT aeo_num FROM aeo_details WHERE FIND_IN_SET(?, udise) > 0";
		try {
			return jdbcTemplate.queryForMap(sql, num);

		} catch (Exception e) {
	        e.printStackTrace();
	        return Map.of(); 
	    } 
	}

	private Map<String, Object> getSchoolReport1(String sch) {
		String sql = "SELECT "
				+ "    CASE "
				+ "        WHEN EXISTS ( "
				+ "            SELECT 1 "
				+ "            FROM school_buildup_area sba "
				+ "            WHERE sba.school_id = ? "
				+ "        ) THEN 'Completed' "
				+ "        ELSE 'Pending' "
				+ "    END AS other_details, "
				+ "    CASE "
				+ "        WHEN EXISTS ( "
				+ "            SELECT 1 "
				+ "            FROM staff_strength_1 ss "
				+ "            WHERE ss.school_id = ? "
				+ "        ) THEN 'Completed' "
				+ "        ELSE 'Pending' "
				+ "    END AS staff_details, "
				+ "    CASE "
				+ "        WHEN EXISTS ( "
				+ "            SELECT 1 "
				+ "            FROM student_strength sts "
				+ "            WHERE sts.school_id = ? "
				+ "        ) THEN 'Completed' "
				+ "        ELSE 'Pending' "
				+ "    END AS students_details, "
				+ "    CASE "
				+ "        WHEN EXISTS ( "
				+ "            SELECT 1 "
				+ "            FROM playground pg "
				+ "            WHERE pg.school_id = ? "
				+ "        ) THEN 'Completed' "
				+ "        ELSE 'Pending' "
				+ "    END AS playground_details, "
				+ "    CASE "
				+ "        WHEN EXISTS ("
				+ "            SELECT 1 "
				+ "            FROM drinking_water dw "
				+ "            WHERE dw.school_id = ? "
				+ "        ) THEN 'Completed' "
				+ "        ELSE 'Pending' "
				+ "    END AS drinkingwater_details, "
				+ "    CASE "
				+ "        WHEN EXISTS ( "
				+ "            SELECT 1 "
				+ "            FROM toilet tot "
				+ "            WHERE tot.school_id = ? "
				+ "        ) THEN 'Completed' "
				+ "        ELSE 'Pending' "
				+ "    END AS toilet_details, "
				+ "    CASE "
				+ "        WHEN EXISTS ( "
				+ "            SELECT 1  "
				+ "            FROM kitchen kit  "
				+ "            WHERE kit.school_id = ? "
				+ "        ) THEN 'Completed' "
				+ "        ELSE 'Pending' "
				+ "    END AS kitchen_details, "
				+ "    CASE  "
				+ "    WHEN NOT EXISTS ( "
				+ "        SELECT 1 FROM buildings WHERE school_id = ? and is_active=1 and is_delete=0 "
				+ "    ) THEN 'Pending' "
				+ "    WHEN ( "
				+ "        SELECT COUNT(*) FROM buildings WHERE school_id = ? and is_active=1 and is_delete=0 "
				+ "    ) = ( "
				+ "        SELECT COUNT(DISTINCT rd.building_id) "
				+ "        FROM room_details rd "
				+ "        JOIN buildings b ON b.buildng_id = rd.building_id "
				+ "        WHERE b.school_id = ? and b.is_active=1 and b.is_delete=0 and rd.is_active=1 and rd.is_delete=0 "
				+ "    ) THEN 'Completed' "
				+ "    ELSE 'Partially Completed' "
				+ "END AS buildings, "
				+ "CASE "
				+ "    WHEN NOT EXISTS ( "
				+ "        SELECT 1 FROM room_details WHERE school_id = ? and is_active=1 and is_delete=0 "
				+ "    ) THEN 'Pending' "
				+ "    WHEN ( "
				+ "        SELECT COUNT(DISTINCT room_id) "
				+ "        FROM room_details "
				+ "        WHERE school_id = ? and is_active=1 and is_delete=0 "
				+ "    ) = ( "
				+ "        SELECT COUNT(DISTINCT rd.room_id) "
				+ "        FROM room_details rd "
				+ "        JOIN it_mapping im ON  im.room_id = rd.room_id "
				+ "        JOIN electrical_mapping em ON  em.room_id = rd.room_id "
				+ "	    JOIN furniture_mapping fm ON  fm.room_id = rd.room_id "
				+ "        WHERE rd.school_id = ? and rd.is_active=1 and rd.is_delete=0 "
				+ "        and im.is_active=1 and im.is_delete=0 and em.is_active=1 and em.is_delete=0 "
				+ "        and fm.is_active=1 and fm.is_delete=0 "
				+ "        AND (im.room_id IS NOT NULL OR em.room_id IS NOT NULL OR fm.room_id IS NOT NULL) "
				+ "    ) THEN 'Completed' "
				+ "    WHEN ( "
				+ "        SELECT COUNT(DISTINCT rd.room_id) "
				+ "        FROM room_details rd "
				+ "        JOIN it_mapping im ON  im.room_id = rd.room_id "
				+ "        JOIN electrical_mapping em ON  em.room_id = rd.room_id "
				+ "        JOIN furniture_mapping fm ON fm.room_id = rd.room_id "
				+ "        WHERE rd.school_id = ? and rd.is_active=1 and rd.is_delete=0 "
				+ "        and im.is_active=1 and im.is_delete=0 and em.is_active=1 and em.is_delete=0 "
				+ "        and fm.is_active=1 and fm.is_delete=0 "
				+ "        AND (im.room_id IS NOT NULL OR em.room_id IS NOT NULL OR fm.room_id IS NOT NULL) "
				+ "    ) > 0 THEN 'Partially Completed' "
				+ "    ELSE 'Pending' "
				+ "END AS school_assets ";
		try {
			return jdbcTemplate.queryForMap(sql, sch, sch, sch, sch, sch, sch, sch, sch, sch, sch, sch, sch, sch, sch);

		} catch (Exception e) {
	        e.printStackTrace();
	        return Map.of(); 
	    } 
	}

	private Map<String, Object> getSchoolReport(String sch) {
		String sql = "SELECT id as school_id, zone, division, udise FROM school_list WHERE id = ?";
		try {
			return jdbcTemplate.queryForMap(sql, sch);

		} catch (Exception e) {
	        e.printStackTrace();
	        return Map.of(); 
	    } 
	}

	private List<String> getschoolId(String udise, String zone, String ward) {
		String sql = "";
		if (zone != null && !zone.isEmpty() && ward != null && !ward.isEmpty()) {
			//System.out.println("check1");
	        sql = "SELECT id FROM school_list WHERE udise In(" + udise + ") and zone = ? and division = ?";
	    } else if (zone != null && !zone.isEmpty() && (ward == null || ward.isEmpty())) {
	        sql = "SELECT id FROM school_list WHERE udise In(" + udise + ") and zone = ?";
	    } else if ((zone == null || zone.isEmpty()) && (ward == null || ward.isEmpty())) {
	        sql = "SELECT id FROM school_list WHERE udise In(" + udise + ")";
	    }
		try {
			 if (sql.contains("zone = ?") && sql.contains("division = ?")) {
		            return jdbcTemplate.queryForList(sql, String.class, zone, ward);
		        } else if (sql.contains("zone = ?")) {
		            return jdbcTemplate.queryForList(sql, String.class, zone);
		        } else {
		            return jdbcTemplate.queryForList(sql, String.class);
		        }

		} catch (DataAccessException e) {
			e.printStackTrace();
			return List.of();
		}
	}
	
	@Transactional
	public ResponseEntity<?> getAEOStudentData(List<String> udise) {
		Map<String, Object> response = new HashMap<>();
		
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		if(udise.isEmpty()) {
			udise = getUDISE();
			System.out.println("udise="+udise);
		}
		for(String udises:udise) {	
			List<Map<String,Object>> school = getschoolsId(udises);
		
			if(!school.isEmpty() && school!=null) {
				for(Map<String,Object> sch:school) {
					Map<String, Object> result = new HashMap<>();
					int id = (Integer)sch.get("id");
					Map<String, Object> report = getTotalStudents(id);
					result.putAll(report);
					
					String num = (String) sch.get("udise");
					
					String sch_name=(String)sch.get("school_name");
					String hm_name=(String)sch.get("hm_name");
					String hm_mob_num=(String)sch.get("hm_phone_number");
					String zone=(String)sch.get("zone");
					String division=(String)sch.get("division");
					
				
					Map<String, Object> report1 = getStudentsReg(num);
					result.putAll(report1);
					long totalStud = (long) report.get("total_students");
					long regStud = (long) report1.get("students_registered");
					long pending =  totalStud - regStud;
					result.put("pending", pending);
					
					result.put("udise", num);
					
					result.put("school_name",sch_name );
					result.put("hm_name",hm_name );
					result.put("hm_mobile", hm_mob_num);
					result.put("zone", zone);
					result.put("division",division );
					
					 
					 Map<String, Object> report2 = getAEO(num);
					 result.putAll(report2);
					 
					data.add(result);
				}
				response.put("Message", "Success");
				response.put("data", data);
				System.out.println("data="+data);
			} else {
				response.put("Message", "No Data");
				response.put("data", "No Data Found");
			}
				
			}
		return ResponseEntity.ok(response);
		}
		
	private Map<String, Object> getStudentsReg(String udises) {
		String sql = "SELECT count(*) as students_registered FROM student_info WHERE udise = ? AND is_active = 1 AND is_delete = 0";
		try {
			return jdbcTemplate.queryForMap(sql, udises);

		} catch (Exception e) {
	        e.printStackTrace();
	        return Map.of(); 
	    } 
	}

	private Map<String, Object> getTotalStudents(int sch) {
		String sql = "SELECT COALESCE((SELECT total_students FROM student_strength WHERE school_id = ?), 0) AS total_students";
		try {
			return jdbcTemplate.queryForMap(sql, sch);

		} catch (Exception e) {
	        e.printStackTrace();
	        return Map.of(); 
	    } 
	}

	

	private List<Map<String,Object>> getschoolsId(String udises) {
		String sql = "SELECT id, udise,school_name,hm_name,hm_phone_number,zone,division FROM school_list WHERE udise In(" + udises + ")";
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return List.of();
		}
	}
	
	////////////////////////////////////////////////
	
	
	@Transactional
	public ResponseEntity<?> getAEOStudentsCount() {
        Map<String, Object> response = new HashMap<>();
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		
		List<Map<String,Object>> aeos = getAeoDetails(); 
		
		for(Map<String,Object> aeo : aeos) {
			
			Map<String, Object> result = new HashMap<>();
			
			String aoeNum = (String) aeo.get("aeo_num");
			result.put("aoe", aoeNum);
			
			String udises = (String) aeo.get("udise");
			
			List<Integer> schoolId = getSchoolId(udises);
			int school_count = schoolId.size();
			result.put("school_count", school_count);
			
			int total_stud = getTotalStudentsCount(schoolId);
			result.put("total_students", total_stud);
			
			int students_registered = getRegisteredStudents(udises);
			result.put("student_registered", students_registered);
			
			int pending = total_stud - students_registered;
			result.put("pending", pending);
			
			data.add(result);
		}
		response.put("Message", "Success");
		response.put("data", data);
		return ResponseEntity.ok(response);
	}

	
	private int getRegisteredStudents(String udises) {

		String[] udiseArray = udises.split(",");

		String placeholders = String.join(",", Collections.nCopies(udiseArray.length, "?"));

		String sql = "SELECT COUNT(student_id) FROM student_info WHERE udise IN (" + placeholders
				+ ") AND is_active = 1 AND is_delete = 0";

		try {

			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, (Object[]) udiseArray);
			return count != null ? count : 0;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return 0;
		}
	}



	private int getTotalStudentsCount(List<Integer> schoolId) {
	    String sql = "SELECT SUM(total_students) FROM student_strength WHERE school_id IN (:schoolId)";
	    try {
	        MapSqlParameterSource parameters = new MapSqlParameterSource();
	        parameters.addValue("schoolId", schoolId);
	        
	        Integer total = namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class);
	        return total != null ? total : 0; 
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return 0; 
	    }
	}


	private List<Integer> getSchoolId(String udises) {
		String sql = "SELECT id FROM school_list WHERE FIND_IN_SET(udise, :udises) > 0";
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("udises", udises);
            return namedParameterJdbcTemplate.queryForList(sql, parameters, Integer.class);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return List.of();
		}
	}

	private List<Map<String, Object>> getAeoDetails() {
		String sql = "SELECT aeo_num, udise FROM aeo_details";
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return List.of();
		}
	}
	
	@Transactional
	public ResponseEntity<?> getRegisteredStud(String udise) {
		Map<String, Object> result = new HashMap<>();
		String sql = "SELECT * "
				+ " FROM student_info "
				+ " WHERE udise = ? "
				+ " AND is_active = 1 "
				+ " AND is_delete = 0 "
				+ " ORDER BY "
				+ " CASE "
				+ " WHEN std = 'PreKG' THEN 1 "
				+ " WHEN std = 'LKG' THEN 2 "
				+ " WHEN std = 'UKG' THEN 3 "
				+ " WHEN std LIKE 'Class1' THEN 4 "
				+ " WHEN std LIKE 'Class2' THEN 5 "
				+ " WHEN std LIKE 'Class3' THEN 6 "
				+ " WHEN std LIKE 'Class4' THEN 7 "
				+ " WHEN std LIKE 'Class5' THEN 8 "
				+ " WHEN std LIKE 'Class6' THEN 9 "
				+ " WHEN std LIKE 'Class7' THEN 10 "
				+ " WHEN std LIKE 'Class8' THEN 11 "
				+ "    WHEN std LIKE 'Class9' THEN 12\r\n"
				+ "    WHEN std LIKE 'Class10' THEN 13\r\n"
				+ "    WHEN std LIKE 'Class11' THEN 14\r\n"
				+ "    WHEN std LIKE 'Class12' THEN 15\r\n"
				+ "    ELSE 100 \r\n"
				+ "  END ";

		try {
			List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, udise);
			result.put("Message", "Success");
			result.put("Data", data);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("Message", "Error");
			result.put("Error", "Failed to fetch AEO List: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}
	
}
