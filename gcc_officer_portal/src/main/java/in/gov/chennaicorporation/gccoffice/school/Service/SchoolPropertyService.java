package in.gov.chennaicorporation.gccoffice.school.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchoolPropertyService {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSourcemysql(@Qualifier("mysqlSchoolDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		
	}

	@Transactional
	public List<Map<String, Object>> getSchoolData(String udise) {
		String sql = "SELECT * FROM school_list WHERE udise = ?";
		return jdbcTemplate.queryForList(sql, udise);
	}
//initial
	/*@Transactional
	public String saveSchoolBuildupArea(Double land_area, Double buildup_area, Boolean is_compound,
			Boolean is_nameboard, Boolean is_pasystem, Boolean is_bell, Boolean is_generator, int school_id) {
		String sql = "INSERT INTO school_buildup_area (land_area, buildup_area, is_compound, is_nameboard, is_pasystem, is_bell, is_generator, school_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, land_area, buildup_area, is_compound, is_nameboard, is_pasystem, is_bell,
					is_generator, school_id);
			return "Other Details saved sucessfully";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "error";
		}

	}*/
	@Transactional
	public String saveSchoolBuildupArea(Double land_area, Double buildup_area, int is_compound,
	        Boolean is_nameboard, Boolean is_pasystem, Boolean is_bell, Boolean is_generator, int school_id) {

	    // Step 1: Check if record exists for the given school_id
	    String checkSql = "SELECT COUNT(*) FROM school_buildup_area WHERE school_id = ?";
	    try {
	        int count = jdbcTemplate.queryForObject(checkSql, new Object[]{school_id}, Integer.class);

	        if (count > 0) {
	            return "Build-up Area details for this school already exist.";
	        }

	        // Step 2: Insert only if no existing record found
	        String insertSql = "INSERT INTO school_buildup_area (land_area, buildup_area, is_compound, is_nameboard, is_pasystem, is_bell, is_generator, school_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	        jdbcTemplate.update(insertSql, land_area, buildup_area, is_compound, is_nameboard, is_pasystem, is_bell, is_generator, school_id);

	        return "Other Details saved successfully";

	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return "Error saving build-up area details.";
	    }
	}

//initial live
	/*@Transactional
	public String saveStudentStrength(int total_students, int total_girls, int total_boys, int others, int school_id) {
		String sql = "INSERT INTO student_strength (total_students, total_girls, total_boys,others,school_id) VALUES (?,?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, total_students, total_girls, total_boys, others, school_id);
			return "Student Strength saved sucessfully";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "error";
		}
	}*/
	@Transactional
	public String saveStudentStrength(int total_students, int total_girls, int total_boys, int others,int special_child, int school_id) {
	    // Check if a record already exists for the given school_id
	    String checkSql = "SELECT COUNT(*) FROM student_strength WHERE school_id = ?";
	    try {
	        int count = jdbcTemplate.queryForObject(checkSql, new Object[]{school_id}, Integer.class);
	        if (count > 0) {
	            return "Student Strength for this school is already recorded.";
	        }

	        // Proceed with insert if no record found
	        String insertSql = "INSERT INTO student_strength (total_students, total_girls, total_boys, others, special_child,school_id) VALUES (?,?, ?, ?, ?, ?)";
	        jdbcTemplate.update(insertSql, total_students, total_girls, total_boys, others,special_child, school_id);
	        return "Student Strength saved successfully";

	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return "Error saving student strength.";
	    }
	}


//	@Transactional
//	public String saveStaffStrength(int tot_staff, int sg_staff, int bt_staff, int pg_staff, int temp_staff,
//			int ngo_staff, int kg_staff, int pt_staff, int part_staff, int qa_staff, int kg_ayha, int watchman,
//			int comp_asst, int scavengers, int maint_workers, int school_id) {
//		String sql = "INSERT INTO staff_strength (tot_staff, sg_staff, bt_staff, pg_staff,temp_staff, ngo_staff, kg_staff, pt_staff,part_staff, qa_staff, kg_ayha, watchman, comp_asst, scavengers, maint_workers, school_id) VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//		try {
//			jdbcTemplate.update(sql, tot_staff, sg_staff, bt_staff, pg_staff, temp_staff, ngo_staff, kg_staff, pt_staff,
//					part_staff, qa_staff, kg_ayha, watchman, comp_asst, scavengers, maint_workers, school_id);
//			return "Staff Strength saved sucessfully";
//		} catch (DataAccessException e) {
//			e.printStackTrace();
//			return "error";
//		}
//	}

	
	//initial
	/*@Transactional
	public String saveToilets(int total_toiletBlocks, int total_male_urinals, int total_wc_male, int total_wc_female,
			int total_diffAbled_toilet, Boolean is_napkins, int school_id) {
		String sql = "INSERT INTO toilet (total_toiletBlocks, total_male_urinals, total_wc_male, total_wc_female, total_diffAbled_toilet, is_napkins, school_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, total_toiletBlocks, total_male_urinals, total_wc_male, total_wc_female,
					total_diffAbled_toilet, is_napkins, school_id);
			return "Toilet Details saved sucessfully";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "error";
		}
	}*/
	@Transactional
	public String saveToilets(Integer total_toiletBlocks, int total_male_urinals,int total_female_urinals,int staff_male_toilets,int staff_female_toilets, int total_wc_male, int total_wc_female,
	                          int total_diffAbled_toilet, Boolean is_napkins, int school_id) {
	    String checkSql = "SELECT COUNT(*) FROM toilet WHERE school_id = ?";
	    int count = jdbcTemplate.queryForObject(checkSql, Integer.class, school_id);

	    if (count > 0) {
	        return "Toilet data already exists for this school. Insertion not allowed.";
	    }

	    String insertSql = "INSERT INTO toilet (total_toiletBlocks, total_male_urinals, total_wc_male, total_wc_female, total_diffAbled_toilet, is_napkins, school_id,total_female_urinals,staff_male_toilet,staff_female_toilet) VALUES (?, ?, ?, ?, ?, ?, ?,?,?,?)";

	    try {
	        jdbcTemplate.update(insertSql, total_toiletBlocks, total_male_urinals, total_wc_male, total_wc_female,
	                total_diffAbled_toilet, is_napkins, school_id,total_female_urinals,staff_male_toilets,staff_female_toilets);
	        return "Toilet Details saved successfully";
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return "Error while saving toilet details";
	    }
	}
//initial
	/*@Transactional
	public String saveKitchen(Boolean is_cmbfs, Boolean is_cmbfsIndividual, Boolean is_noonmeal,
			Boolean is_noonIndividual, Boolean is_vegGarden, Boolean is_anganwadi, String anganwadi_unique,
			Boolean is_anganwadiIndividual, int school_id) {
		String sql = "INSERT INTO kitchen (is_cmbfs, is_cmbfsIndividual, is_noonmeal, is_noonIndividual, is_vegGarden, is_anganwadi, anganwadi_unique, is_anganwadiIndividual, school_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, is_cmbfs, is_cmbfsIndividual, is_noonmeal, is_noonIndividual, is_vegGarden,
					is_anganwadi, anganwadi_unique, is_anganwadiIndividual, school_id);
			return "Kitchen Details saved sucessfully";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "error";
		}
	}*/

@Transactional
	public int saveKitchen(Boolean is_cmbfs, Boolean is_cmbfsIndividual, Boolean is_noonmeal, Boolean is_noonIndividual,
			Boolean is_vegGarden, Boolean is_anganwadi, int school_id) {
		String sql = "INSERT INTO kitchen (is_cmbfs, is_cmbfsIndividual, is_noonmeal, is_noonIndividual, is_vegGarden, is_anganwadi, school_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setObject(1, is_cmbfs, Types.BOOLEAN);
	            ps.setObject(2, is_cmbfsIndividual, Types.BOOLEAN);
	            ps.setObject(3, is_noonmeal, Types.BOOLEAN);
	            ps.setObject(4, is_noonIndividual, Types.BOOLEAN);
	            ps.setObject(5, is_vegGarden, Types.BOOLEAN);
	            ps.setObject(6, is_anganwadi, Types.BOOLEAN);
				ps.setInt(7, school_id);
				return ps;
			}, keyHolder);
			Number generatedDataId = keyHolder.getKey();
			int playgoundID = generatedDataId.intValue();
			return playgoundID;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Transactional
	public void saveAnganwadi(String anganwadi_unique, Boolean is_anganwadiIndividual, int kitchen_id, int school_id) {
		String sql = "INSERT INTO anganwadi_details (anganwadi_unique, is_anganwadiIndividual, kitchen_id, school_id) VALUES (?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, anganwadi_unique, is_anganwadiIndividual, kitchen_id, school_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
	}

	@Transactional
	public void updateAnganwadiCheck(String anganwadi_unique, int school_id) {
		String sql ="UPDATE room_details SET anganwadi_check = 1 WHERE anganwadi_unique_number = ? AND school_id = ?";
		
		try {
			jdbcTemplate.update(sql, anganwadi_unique, school_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

//initial
	/*@Transactional
	public int savePlayground(Boolean is_playground, Double playground_area, Boolean is_court, int school_id) {
		String sql = "INSERT INTO playground (is_playground, playground_area, is_court, school_id) VALUES (?, ?, ?, ?)";
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setBoolean(1, is_playground);
				ps.setDouble(2, playground_area);
				ps.setBoolean(3, is_court);
				ps.setInt(4, school_id);
				return ps;
			}, keyHolder);
			Number generatedDataId = keyHolder.getKey();
			int playgoundID = generatedDataId.intValue();
			return playgoundID;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return 0;
		}

	}*/
	@Transactional
	public int savePlayground(Boolean is_playground, Double playground_area, Boolean is_court, int school_id) {
	    String checkSql = "SELECT playground_id FROM playground WHERE school_id = ?";
	    List<Integer> existing = jdbcTemplate.queryForList(checkSql, Integer.class, school_id);

	    if (!existing.isEmpty()) {
	        // Return existing ID without inserting new row
	        return existing.get(0);
	    }

	    // Insert new record
	    String insertSql = "INSERT INTO playground (is_playground, playground_area, is_court, school_id) VALUES (?, ?, ?, ?)";
	    KeyHolder keyHolder = new GeneratedKeyHolder();
	    jdbcTemplate.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
	        ps.setBoolean(1, is_playground);
	        ps.setDouble(2, playground_area);
	        ps.setBoolean(3, is_court);
	        ps.setInt(4, school_id);
	        return ps;
	    }, keyHolder);

	    Number generatedId = keyHolder.getKey();
	    return generatedId != null ? generatedId.intValue() : 0;
	}



	//initiall
	/*@Transactional
	public void saveEquipmentDetails(String equipment_id, int school_id, int playground_id) {
		String sql = "INSERT INTO equipment_details (equipment_id, school_id, playground_id) VALUES (?, ?, ?)";
		try {
			jdbcTemplate.update(sql, equipment_id, school_id, playground_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}*/
	@Transactional
	public boolean saveEquipmentDetails(String equipment_id, int school_id, int playground_id) {
	    String checkSql = "SELECT COUNT(*) FROM equipment_details WHERE equipment_id = ? AND school_id = ? AND playground_id = ?";
	    Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, equipment_id, school_id, playground_id);
	    if (count != null && count < 0) {
	        return false;
	    }

	    String insertSql = "INSERT INTO equipment_details (equipment_id, school_id, playground_id) VALUES (?, ?, ?)";
	    jdbcTemplate.update(insertSql, equipment_id, school_id, playground_id);
	    return true;
	}

	
	
	@Transactional
	public void updatePlayground(Boolean is_playground, Double playground_area, Boolean is_court, int school_id, int playground_id) {
	    String sql = "UPDATE playground SET is_playground = ?, playground_area = ?, is_court = ? WHERE playground_id = ? AND school_id = ?";
	    try {
	        int rows = jdbcTemplate.update(sql, is_playground, playground_area, is_court, playground_id, school_id);
	        System.out.println("✅ Rows affected (playground): " + rows);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	    }
	}

//initial
	/*@Transactional
	public void saveCourtDetails(String court_id, int school_id, int playground_id) {
		String sql = "INSERT INTO courts_details (court_id, school_id, playground_id) VALUES (?, ?, ?)";
		try {
			jdbcTemplate.update(sql, court_id, school_id, playground_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}*/
	@Transactional
	public boolean saveCourtDetails(String court_id, int school_id, int playground_id) {
	    String checkSql = "SELECT COUNT(*) FROM courts_details WHERE court_id = ? AND school_id = ? AND playground_id = ?";
	    Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, court_id, school_id, playground_id);
	    if (count != null && count > 0) {
	        return false;
	    }

	    String insertSql = "INSERT INTO courts_details (court_id, school_id, playground_id) VALUES (?, ?, ?)";
	    jdbcTemplate.update(insertSql, court_id, school_id, playground_id);
	    return true;
	}
	
	public boolean existsDrinkingWaterData(int schoolId) {
	    String drinkingWaterSql = "SELECT COUNT(*) FROM drinking_water WHERE school_id = ?";
	    String waterSupplySql = "SELECT COUNT(*) FROM water_supply WHERE school_id = ?";
	    String waterStorageSql = "SELECT COUNT(*) FROM water_storage WHERE school_id = ?";

	    int drinkingWaterCount = jdbcTemplate.queryForObject(drinkingWaterSql, Integer.class, schoolId);
	    int waterSupplyCount = jdbcTemplate.queryForObject(waterSupplySql, Integer.class, schoolId);
	    int waterStorageCount = jdbcTemplate.queryForObject(waterStorageSql, Integer.class, schoolId);

	    return drinkingWaterCount > 0 || waterSupplyCount > 0 || waterStorageCount > 0;
	}


	

	@Transactional
	public int saveDrinkingWater(Boolean is_purifier, Double purifier_capacity, Boolean is_borewell,
			Integer borewell_count, Boolean is_storage, int school_id,int drinkingwater_tap,int handwash_tap) {
		String sql = "INSERT INTO drinking_water (is_purifier, purifier_capacity, is_borewell, borewell_count, is_storage, school_id,drinkingwater_tap,handwash_tap) VALUES (?, ?, ?, ?, ?, ?,?,?)";
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setBoolean(1, is_purifier);
				// Handle possible null values
				if (purifier_capacity != null) {
					ps.setDouble(2, purifier_capacity);
				} else {
					ps.setNull(2, Types.DOUBLE);
				}

				ps.setBoolean(3, is_borewell);
				if (borewell_count != null) {
					ps.setInt(4, borewell_count);
				} else {
					ps.setNull(4, Types.INTEGER);
				}
				ps.setBoolean(5, is_storage);
				ps.setInt(6, school_id);
				ps.setInt(7, drinkingwater_tap);
				ps.setInt(8, handwash_tap);
				return ps;
			}, keyHolder);
			Number generatedDataId = keyHolder.getKey();
			int drinking_water_id = generatedDataId.intValue();
			return drinking_water_id;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return 0;
		}

	}

	@Transactional
	public void saveWaterSupply(int source_water_id, String supply_type, int drinking_water_id, int school_id) {
		String sql = "INSERT INTO water_supply (source_water_id, supply_type, drinking_water_id, school_id) VALUES (?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, source_water_id, supply_type, drinking_water_id, school_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}

	@Transactional
	public void saveWaterStorage(double capacity, int storage_water_id, int drinking_water_id, int school_id) {
		String sql = "INSERT INTO water_storage (capacity, storage_water_id, drinking_water_id, school_id) VALUES (?, ?, ?, ?)";
		try {
			jdbcTemplate.update(sql, capacity, storage_water_id, drinking_water_id, school_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

	}
//initial
	/*@Transactional
	public void saveStaffStrength(int sgRegTeacher, int sgTempTeacher, int sgPtTeacher, int sgNgoTeacher,
			int btRegTeacher, int btTempTeacher, int btPtTeacher, int btNgoTeacher, int pgRegTeacher, int pgTempTeacher,
			int pgPtTeacher, int pgNgoTeacher, int kgRegTeacher, int kgTempTeacher, int petRegTeacher, int petTempTeacher, int petGrade1Teacher, int petGrade2Teacher, int totOa, int kgAyah, int compAss,
			int watchmen, int scavengers, int maintenanceWork, int schoolId) {
		String sql = "INSERT INTO staff_strength_1 ("
				+ "sg_reg_teacher, sg_temp_teacher, sg_pt_teacher, sg_ngo_teacher, "
				+ "bt_reg_teacher, bt_temp_teacher, bt_pt_teacher, bt_ngo_teacher, "
				+ "pg_reg_teacher, pg_temp_teacher, pg_pt_teacher, pg_ngo_teacher, "
				+ "kg_reg_teacher, kg_temp_teacher, pet_reg_teacher, pet_temp_teacher, pet_grade1_teacher, pet_grade2_teacher, tot_oa, kg_ayah, comp_ass, "
				+ "watchmen, scavengers, maintenance_work, school_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.update(sql, sgRegTeacher, sgTempTeacher, sgPtTeacher, sgNgoTeacher, btRegTeacher, btTempTeacher,
				btPtTeacher, btNgoTeacher, pgRegTeacher, pgTempTeacher, pgPtTeacher, pgNgoTeacher, kgRegTeacher, kgTempTeacher,
				petRegTeacher, petTempTeacher, petGrade1Teacher, petGrade2Teacher, totOa, kgAyah, compAss, watchmen, scavengers, maintenanceWork, schoolId);
	}*/
	@Transactional
	public String saveStaffStrength(int sgRegTeacher, int sgTempTeacher, int sgPtTeacher, int sgNgoTeacher,
			int btRegTeacher, int btTempTeacher, int btPtTeacher, int btNgoTeacher, int pgRegTeacher, int pgTempTeacher,
			int pgPtTeacher, int pgNgoTeacher, int kgRegTeacher, int kgTempTeacher, int petRegTeacher,
			int petTempTeacher, int petGrade1Teacher, int petGrade2Teacher, int petPtTeacher, Boolean is_hm,
			int stRegTeacher, int stPtTeacher, int totOa, int kgAyah, int compAss, String watchmen, int scavengers,
			int maintenanceWork, int schoolId, List<String> specialCategoryId) {
		try {
		int staff_id = saveStaffCounts(sgRegTeacher, sgTempTeacher, sgPtTeacher, sgNgoTeacher, btRegTeacher,
				btTempTeacher, btPtTeacher, btNgoTeacher, pgRegTeacher, pgTempTeacher, pgPtTeacher, pgNgoTeacher,
				kgRegTeacher, kgTempTeacher, petRegTeacher, petTempTeacher, petGrade1Teacher, petGrade2Teacher, petPtTeacher,
				is_hm, stRegTeacher, stPtTeacher, totOa, kgAyah, compAss, watchmen, scavengers, maintenanceWork, schoolId);
		
		if(!specialCategoryId.isEmpty()) {
			for(String special_category_id : specialCategoryId) {
				saveSpecialCategoryDetails(special_category_id, schoolId, staff_id);
			}
		}
		
		
		return "Staff Details Saved Sucessfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
		
	}
	

	
	private void saveSpecialCategoryDetails(String special_category_id, int schoolId, int staff_id) {
				
		String sql = "INSERT INTO special_category_details (special_category_id, school_id, staff_id) VALUES (?, ?, ?)";
		try {
			jdbcTemplate.update(sql, special_category_id, schoolId, staff_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	public int saveStaffCounts(int sgRegTeacher, int sgTempTeacher, int sgPtTeacher, int sgNgoTeacher,
			int btRegTeacher, int btTempTeacher, int btPtTeacher, int btNgoTeacher, int pgRegTeacher, int pgTempTeacher,
			int pgPtTeacher, int pgNgoTeacher, int kgRegTeacher, int kgTempTeacher, int petRegTeacher, int petTempTeacher, int petGrade1Teacher, int petGrade2Teacher, 
			int petPtTeacher,Boolean is_hm, int stRegTeacher, int stPtTeacher, 
			int totOa, int kgAyah, int compAss, String watchmen, int scavengers, int maintenanceWork, int schoolId) {
		String sql = "INSERT INTO staff_strength_1 ( "
				+ " sg_reg_teacher, sg_temp_teacher, sg_pt_teacher, sg_ngo_teacher, "
				+ " bt_reg_teacher, bt_temp_teacher, bt_pt_teacher, bt_ngo_teacher, "
				+ " pg_reg_teacher, pg_temp_teacher, pg_pt_teacher, pg_ngo_teacher, "
				+ " kg_reg_teacher, kg_temp_teacher, "
				+ " pet_reg_teacher, pet_temp_teacher, pet_grade1_teacher, pet_grade2_teacher, pet_pt_teacher, "
				+ " is_hm, st_reg_teacher, st_pt_teacher, tot_oa, kg_ayah, comp_ass, "
				+ " watchmen, scavengers, maintenance_work, school_id) "
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, sgRegTeacher);
				ps.setInt(2, sgTempTeacher);
				ps.setInt(3, sgPtTeacher);
				ps.setInt(4, sgNgoTeacher);
				ps.setInt(5, btRegTeacher);
				ps.setInt(6, btTempTeacher);
				ps.setInt(7, btPtTeacher);
				ps.setInt(8, btNgoTeacher);
				ps.setInt(9, pgRegTeacher);
				ps.setInt(10, pgTempTeacher);
				ps.setInt(11, pgPtTeacher);
				ps.setInt(12, pgNgoTeacher);
				ps.setInt(13, kgRegTeacher);
				ps.setInt(14, kgTempTeacher);
				ps.setInt(15, petRegTeacher);
				ps.setInt(16, petTempTeacher);
				ps.setInt(17, petGrade1Teacher);
				ps.setInt(18, petGrade2Teacher);
				ps.setInt(19, petPtTeacher);
				ps.setBoolean(20, is_hm);
				ps.setInt(21, stRegTeacher);
				ps.setInt(22, stPtTeacher);
				ps.setInt(23, totOa);
				ps.setInt(24, kgAyah);
				ps.setInt(25, compAss);
				ps.setString(26, watchmen);
				ps.setInt(27, scavengers);
				ps.setInt(28, maintenanceWork);
				ps.setInt(29, schoolId);
				return ps;
			}, keyHolder);
			Number generatedDataId = keyHolder.getKey();
			int playgoundID = generatedDataId.intValue();
			return playgoundID;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return 0;
		}
	}


	@Transactional
	public String getSchoolId(String user_id) {
		String sql = "SELECT school_id FROM login_details WHERE user_id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, String.class, user_id);
		} catch (DataAccessException e) {

			return "error";
		}
	}

	public List<Map<String, Object>> getSchoolData1(String schoolId) {
		String sql = "SELECT * FROM school_list WHERE id In(" + schoolId + ")";
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return List.of();
		}
	}
	

	@Transactional
	public Map<String, Object> getSchoolDetails(Long schoolId) {
		String sql = "SELECT * FROM school_list WHERE id = ?";
		return jdbcTemplate.queryForMap(sql, schoolId);
	}

	public List<Map<String, Object>> filterSchools(String userId, String udise, String category, String zone, String division) {
	    StringBuilder sql = new StringBuilder(
	        "SELECT s.id, s.udise, s.school_name, s.category, s.zone, s.division, s.address " +
	        "FROM school_list s " +
	        "JOIN login_details l ON FIND_IN_SET(s.id, l.school_id) > 0 " +
	        "WHERE l.user_id = ?"
	    );

	    List<Object> params = new ArrayList<>();
	    params.add(userId); // required

	    if (udise != null && !udise.isEmpty()) {
	        sql.append(" AND s.udise = ?");
	        params.add(udise);
	    }
	    if (category != null && !category.isEmpty()) {
	        sql.append(" AND s.category = ?");
	        params.add(category);
	    }
	    if (zone != null && !zone.isEmpty()) {
	        sql.append(" AND s.zone = ?");
	        params.add(zone);
	    }
	    if (division != null && !division.isEmpty()) {
	        sql.append(" AND s.division = ?");
	        params.add(division);
	    }

	    return jdbcTemplate.queryForList(sql.toString(), params.toArray());
	}
	
	@Transactional
	public String updateOtherDetails(Double land_area, Double buildup_area, int is_compound, Boolean is_nameboard,
			Boolean is_pasystem, Boolean is_bell, Boolean is_generator, int school_id) {
		String sql = "UPDATE school_buildup_area SET land_area = ?, buildup_area = ?, is_compound = ?, is_nameboard = ?, is_pasystem = ?, is_bell = ?, is_generator = ? WHERE school_id = ? ";
		try {
			jdbcTemplate.update(sql, land_area, buildup_area, is_compound, is_nameboard, is_pasystem, is_bell,
					is_generator, school_id);
			return "Success";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "Error";
		}
	}
	@Transactional
	public String updateStudentDetails(int total_students, int total_girls, int total_boys, int others, int special_child,int school_id) {
		String sql = "UPDATE student_strength SET total_students = ?, total_girls = ?, total_boys = ?, others = ?, special_child=? WHERE school_id = ? ";
		try {
			jdbcTemplate.update(sql, total_students, total_girls, total_boys, others,special_child, school_id);
			return "Success";
		}catch (DataAccessException e) {
			e.printStackTrace();
			return "Error";
		}	
	}
	//initial
	/*@Transactional
	public Map<String, Object> checkPlaygroundDetails(int school_id) {
		String sql = "SELECT * FROM playground WHERE school_id = ? ";
		try {
			return jdbcTemplate.queryForMap(sql, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}*/
	@Transactional
	public Map<String, Object> checkPlaygroundDetails(int school_id) {
		String sql = "SELECT * FROM playground WHERE school_id = ? ORDER BY playground_id DESC LIMIT 1 ";
		try {
			return jdbcTemplate.queryForMap(sql, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}
	private void updateCourtDetails(int court_id, int courts_detais_id) {
		String sql = "UPDATE courts_details SET court_id = ? WHERE courts_detais_id = ? ";
		try {
			jdbcTemplate.update(sql, court_id, courts_detais_id);
		
		}catch (DataAccessException e) {
			e.printStackTrace();
			
		}	
		
	}
//	@Transactional
//	public List<Map<String, Object>> checkCourtDetails(int school_id) {
//	String sql = "select cm.court_type,cd.Courts_detais_id "
//	+ "FROM "
//	+ "playground p\r\n"
//	+ "INNER JOIN "
//	+ "    courts_details cd ON p.playground_id = cd.playground_id\r\n"
//	+ "INNER JOIN "
//	+ "    courts_master cm ON cd.court_id = cm.court_id\r\n"
//	+ "    WHERE "
//	+ "    p.school_id = ?";
//	try {
//	return jdbcTemplate.queryForList(sql, school_id);
//	} catch (Exception e) {
//	e.printStackTrace();
//	return List.of();
//	}
//	}
	//initial
	/*@Transactional
	public List<Map<String, Object>> checkCourtDetails(int school_id) {
	    String sql = """
	        SELECT cm.court_type, cd.Courts_detais_id, cd.court_id
	        FROM playground p
	        INNER JOIN courts_details cd ON p.playground_id = cd.playground_id
	        INNER JOIN courts_master cm ON cd.court_id = cm.court_id
	        WHERE p.school_id = ?  and cd.is_active = 1
	    """;
	    try {
	        return jdbcTemplate.queryForList(sql, school_id);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return List.of();
	    }
	}*/
	
	@Transactional
	public List<Map<String, Object>> checkCourtDetails(int schoolId) {
	    String sql = "" +
	        "SELECT \n" +
	        "    cm.court_type, \n" +
	        "    cd.Courts_detais_id, \n" +
	        "    cd.court_id, \n" +
	        "    p.* \n" +
	        "FROM gcc_school.playground p \n" +
	        "INNER JOIN gcc_school.courts_details cd ON p.playground_id = cd.playground_id \n" +
	        "INNER JOIN gcc_school.courts_master cm ON cd.court_id = cm.court_id \n" +
	        "WHERE p.school_id = ? \n" +
	        "  AND cd.is_active = 1 \n" +
	        "  AND p.playground_id = ( \n" +
	        "      SELECT MAX(playground_id) \n" +
	        "      FROM gcc_school.playground \n" +
	        "      WHERE school_id = ? \n" +
	        "  )";

	    try {
	        return jdbcTemplate.queryForList(sql, schoolId, schoolId);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return List.of();
	    }
	}


	@Transactional
//	public List<Map<String, Object>> checkEquipmentDetails(int school_id) {
//	String sql = "select em.equipment_type,ed.equipment_detais_id "
//	+ "FROM "
//	+ "playground p\r\n"
//	+ "INNER JOIN "
//	+ "    equipment_details ed ON p.playground_id = ed.playground_id\r\n"
//	+ "INNER JOIN "
//	+ "    equipment_master em ON ed.equipment_id = em.equipment_id\r\n"
//	+ "    WHERE "
//	+ "    p.school_id = ?";
//	try {
//	return jdbcTemplate.queryForList(sql, school_id);
//	} catch (Exception e) {
//	e.printStackTrace();
//	return List.of();
//	}
//	}
	//ajith
	//initial
	/*public List<Map<String, Object>> checkEquipmentDetails(int school_id) {
	    String sql = """
	        SELECT em.equipment_id, em.equipment_type, ed.equipment_detais_id
	        FROM playground p
	        INNER JOIN equipment_details ed ON p.playground_id = ed.playground_id
	        INNER JOIN equipment_master em ON ed.equipment_id = em.equipment_id
	        WHERE p.school_id = ? and ed.is_active = 1 """;
	    try {
	        return jdbcTemplate.queryForList(sql, school_id);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return List.of();
	    }
	}*/
	public List<Map<String, Object>> checkEquipmentDetails(int school_id) {
	    String sql = "" +
	        "SELECT em.equipment_id, em.equipment_type, ed.equipment_detais_id \n" +
	        "FROM playground p \n" +
	        "INNER JOIN equipment_details ed ON p.playground_id = ed.playground_id \n" +
	        "INNER JOIN equipment_master em ON ed.equipment_id = em.equipment_id \n" +
	        "WHERE p.school_id = ? \n" +
	        "  AND ed.is_active = 1 \n" +
	        "  AND p.playground_id = ( \n" +
	        "      SELECT MAX(playground_id) \n" +
	        "      FROM playground \n" +
	        "      WHERE school_id = ? \n" +
	        "  )";

	    try {
	        return jdbcTemplate.queryForList(sql, school_id, school_id);  // pass school_id twice
	    } catch (Exception e) {
	        e.printStackTrace();
	        return List.of();
	    }
	}

private void updateEquipmentDetails(Integer equipment_id, int equipment_detais_id) {
		
		String sql = "UPDATE equipment_details SET equipment_id = ? WHERE equipment_detais_id = ? ";
		try {
			jdbcTemplate.update(sql, equipment_id, equipment_detais_id);
		
		}catch (DataAccessException e) {
			e.printStackTrace();
			
		}	
	}

	/*private void updatePlayground(Boolean is_playground, Double playground_area, Boolean is_court, int school_id, int playground_id) {
		String sql = "UPDATE playground SET is_playground = ?, playground_area = ?, is_court = ? WHERE playground_id = ? and school_id = ? ";
		try {
			jdbcTemplate.update(sql, is_playground, playground_area, is_court, playground_id, school_id);

		
		}catch (DataAccessException e) {
			e.printStackTrace();
			
		}	
		
	}*/
	
	
	
	
	
	
//	@Transactional
//	public String updatePlaygroundDetails(Boolean is_playground, Double playground_area, Boolean is_court,
//	        List<Map<String, Object>> equipments, List<Map<String, Object>> courts, int playground_id, int school_id) {
//	    try {
//	        // Update playground main record
//	        updatePlayground(is_playground, playground_area, is_court, school_id, playground_id);
//
//	        // Update equipment details with null-safe checks
//	        for (Map<String, Object> equipment : equipments) {
//	            Object equipDetIdObj = equipment.get("equipment_detais_id");
//	            Object equipIdObj = equipment.get("equipment_id");
//
//	            if (equipDetIdObj != null && equipIdObj != null) {
//	                int equipment_detais_id = Integer.parseInt(equipDetIdObj.toString());
//	                int equipment_id = Integer.parseInt(equipIdObj.toString());
//	                updateEquipmentDetails(equipment_id, equipment_detais_id);
//	            } else {
//	                System.out.println("⚠️ Skipping equipment update — missing equipment_id or equipment_detais_id: " + equipment);
//	            }
//	        }
//	        if(is_court.booleanValue()) {
//		        // Update court details with null-safe checks
//		        for (Map<String, Object> court : courts) {
//		            Object courtDetIdObj = court.get("Courts_detais_id");
//		            Object courtIdObj = court.get("court_id");
//	
//		            if (courtDetIdObj != null && courtIdObj != null) {
//		                int Courts_detais_id = Integer.parseInt(courtDetIdObj.toString());
//		                int court_id = Integer.parseInt(courtIdObj.toString());
//		                updateCourtDetails(court_id, Courts_detais_id);
//		            } else {
//		                System.out.println("⚠️ Skipping court update — missing court_id or Courts_detais_id: " + court);
//		            }
//		        }
//	        }
//	        else {
//	        	//inactiveCourtDetails(school_id);
//	        }
//	        return "Success";
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return "Error";
//	    }
//	}
@Transactional
public String updatePlaygroundDetails(Boolean is_playground, Double playground_area, Boolean is_court,
        List<Map<String, Object>> equipments, List<Map<String, Object>> courts, int playground_id, int school_id) {
    try {
        // Step 1: Update main playground table
        updatePlayground(is_playground, playground_area, is_court, school_id, playground_id);

        // Step 2: Deactivate old entries
        deactiveCourts(school_id);
        deactiveEquipments(school_id);

        // Step 3: Re-insert new equipment selections
        for (Map<String, Object> equipment : equipments) {
            Object equipmentIdObj = equipment.get("equipment_id");
            if (equipmentIdObj != null) {
                String equipment_id = equipmentIdObj.toString();
                saveEquipmentDetails(equipment_id, school_id, playground_id);
            } else {
                System.out.println("⚠️ Skipping equipment insert — missing equipment_id: " + equipment);
            }
        }

        // Step 4: Re-insert new court selections (only if court is enabled)
        if (Boolean.TRUE.equals(is_court)) {
            for (Map<String, Object> court : courts) {
                Object courtIdObj = court.get("court_id");
                if (courtIdObj != null) {
                    String court_id = courtIdObj.toString();
                    saveCourtDetails(court_id, school_id, playground_id);
                } else {
                    System.out.println("⚠️ Skipping court insert — missing court_id: " + court);
                }
            }
        }

        return "Success";

    } catch (Exception e) {
        e.printStackTrace();
        return "Error";
    }
}

	@Transactional
	public void deactiveCourts(int school_id) {
		String sql = "UPDATE courts_details SET is_active = false WHERE school_id = ? ";
		try {
			jdbcTemplate.update(sql, school_id);
		
		}catch (DataAccessException e) {
			e.printStackTrace();
			
		}	
	}

	@Transactional
	public void deactiveEquipments(int school_id) {
		String sql = "UPDATE equipment_details SET is_active = false WHERE school_id = ? ";
		try {
			jdbcTemplate.update(sql, school_id);
		
		}catch (DataAccessException e) {
			e.printStackTrace();
			
		}	
		
	}

	public void updateKitchenDetails(Boolean is_cmbfs, Boolean is_cmbfsIndividual, Boolean is_noonmeal,
			Boolean is_noonIndividual, Boolean is_vegGarden, Boolean is_anganwadi,int school_id) {
    System.out.println("inside");
		String sql = "UPDATE kitchen SET is_cmbfs = ?, is_cmbfsIndividual = ?, is_noonmeal = ?, is_noonIndividual = ?, is_vegGarden = ?, is_anganwadi = ? WHERE school_id = ? ";
			
		try {
			jdbcTemplate.update(sql, is_cmbfs,is_cmbfsIndividual,is_noonmeal,is_noonIndividual,is_vegGarden,is_anganwadi, school_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	public void saveOldAnganwadi(String anganwadi_unique, Boolean is_anganwadiIndividual, int school_id) {
		
		String sql = "UPDATE anganwadi_details SET is_anganwadiIndividual = ? WHERE school_id = ? AND anganwadi_unique=?";

		try {
			jdbcTemplate.update(sql,is_anganwadiIndividual,school_id,anganwadi_unique);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	public int getKitchenIdbySchId(int school_id) {

		String sql = "SELECT kitchen_id FROM kitchen WHERE school_id=? Limit 1";

		try {
	        return jdbcTemplate.queryForObject(sql, new Object[]{school_id}, Integer.class);
	    } catch (EmptyResultDataAccessException e) {
	        // No kitchen found for this school_id
	        return 0;
	    }
		
	}
	
	public void saveNewAnganwadi(String new_anganwadi_unique, Boolean new_is_anganwadiIndividual, int kitchen_id,
			int school_id) {
		
		String sql = "INSERT INTO anganwadi_details (anganwadi_unique, is_anganwadiIndividual, kitchen_id, school_id) VALUES (?, ?, ?, ?)";
		
		try {
			jdbcTemplate.update(sql, new_anganwadi_unique, new_is_anganwadiIndividual, kitchen_id, school_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}
	//initial
	/*@Transactional
	public Map<String, Object> checkKitchenDetails(int school_id) {
		String sql = "SELECT * FROM kitchen WHERE school_id = ?";
		try {
			return jdbcTemplate.queryForMap(sql, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}*/
	@Transactional
	public Map<String, Object> checkKitchenDetails(int school_id) {
		String sql = "SELECT * FROM kitchen WHERE school_id = ? ORDER BY kitchen_id DESC LIMIT 1";
		try {
			return jdbcTemplate.queryForMap(sql, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}
	@Transactional
	public List<Map<String, Object>> checkOldAnganwadiDetails(int school_id) {
		String sql = "SELECT * FROM anganwadi_details WHERE school_id = ? AND is_active = 1 AND is_delete = 0";
		try {
			return jdbcTemplate.queryForList(sql, school_id);
		} catch (Exception e) {
			 e.printStackTrace();
		        return List.of();
		}
	}
	@Transactional
	public List<Map<String, Object>> checkNewAnganwadiDetails(int school_id) {
		String sql = "SELECT * FROM room_details WHERE school_id = ? AND anganwadi_check = 0 AND is_active = 1 AND is_delete = 0";
		try {
			return jdbcTemplate.queryForList(sql, school_id);
		} catch (Exception e) {
			 e.printStackTrace();
		        return List.of();
		}
	}
	/*@Transactional
	public String updateStaffDetails(int sgRegTeacher, int sgTempTeacher, int sgPtTeacher, int sgNgoTeacher,
			                         int btRegTeacher, int btTempTeacher, int btPtTeacher, int btNgoTeacher, 
			                         int pgRegTeacher, int pgTempTeacher, int pgPtTeacher, int pgNgoTeacher, 
			                         int kgTeacher, int petTeacher, int totOa, int kgAyah, int compAss,
			                         int watchmen, int scavengers, int maintenanceWork, int school_id) {
		String sql = "UPDATE staff_strength_1 SET sg_reg_teacher = ?, sg_temp_teacher = ?, sg_pt_teacher = ?, sg_ngo_teacher = ?,"
				+ " bt_reg_teacher = ?, bt_temp_teacher = ?, bt_pt_teacher = ?, bt_ngo_teacher = ?, "
				+ "pg_reg_teacher = ?, pg_temp_teacher = ?, pg_pt_teacher = ?, pg_ngo_teacher = ?,"
				+ "kg_teacher = ?, pet_teacher = ?, tot_oa = ?, kg_ayah = ?, comp_ass = ?, watchmen = ?,"
				+ " scavengers = ?, maintenance_work = ?WHERE school_id = ? ";
		try {
			jdbcTemplate.update(sql, sgRegTeacher, sgTempTeacher, sgPtTeacher, sgNgoTeacher,
					            btRegTeacher, btTempTeacher, btPtTeacher, btNgoTeacher,
					            pgRegTeacher, pgTempTeacher, pgPtTeacher, pgNgoTeacher,
					            kgTeacher, petTeacher, totOa, kgAyah, compAss, 
					            watchmen, scavengers, maintenanceWork, school_id);
			return "Success";
		}catch (DataAccessException e) {
			e.printStackTrace();
			return "Error";
		}	
	}*/
	@Transactional
	public String updateStaffDetails(int sgRegTeacher, int sgTempTeacher, int sgPtTeacher, int sgNgoTeacher,
	                                 int btRegTeacher, int btTempTeacher, int btPtTeacher, int btNgoTeacher, 
	                                 int pgRegTeacher, int pgTempTeacher, int pgPtTeacher, int pgNgoTeacher, 
	                                 int kgRegTeacher, int kgTempTeacher, int petRegTeacher, int petTempTeacher, int petGrade1Teacher, int petGrade2Teacher, int totOa, int kgAyah, int compAss,
	                                 int watchmen, int scavengers, int maintenanceWork, int school_id) {

	    String sql = "UPDATE staff_strength_1 SET sg_reg_teacher = ?, sg_temp_teacher = ?, sg_pt_teacher = ?, sg_ngo_teacher = ?," +
	                 " bt_reg_teacher = ?, bt_temp_teacher = ?, bt_pt_teacher = ?, bt_ngo_teacher = ?, " +
	                 " pg_reg_teacher = ?, pg_temp_teacher = ?, pg_pt_teacher = ?, pg_ngo_teacher = ?," +
	                 " kg_reg_teacher = ?, kg_temp_teacher = ?, pet_reg_teacher = ?, pet_temp_teacher = ?, pet_grade1_teacher = ?, pet_grade2_teacher = ?, tot_oa = ?, kg_ayah = ?, comp_ass = ?, watchmen = ?," +
	                 " scavengers = ?, maintenance_work = ? WHERE school_id = ?";

	    try {
	        int rowsAffected = jdbcTemplate.update(sql, sgRegTeacher, sgTempTeacher, sgPtTeacher, sgNgoTeacher,
	                                                     btRegTeacher, btTempTeacher, btPtTeacher, btNgoTeacher,
	                                                     pgRegTeacher, pgTempTeacher, pgPtTeacher, pgNgoTeacher,
	                                                     kgRegTeacher, kgTempTeacher, petRegTeacher, petTempTeacher, petGrade1Teacher, petGrade2Teacher, totOa, kgAyah, compAss,
	                                                     watchmen, scavengers, maintenanceWork, school_id);

	        if (rowsAffected == 0) {
	            return "No data found for given school_id"; 
	        } else {
	            return "Success";
	        }
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return "Error";
	    }
	}
	//initial
	/*@Transactional
	public Map<String, Object> checkWaterDetails(int school_id) {
		String sql = "select drinking_water_id, is_purifier, borewell_count, is_borewell, is_storage, purifier_capacity from drinking_water where school_id = ?";
	    try {
	        return jdbcTemplate.queryForMap(sql, school_id);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyMap(); 
	    }
	}*/
	@Transactional
	public Map<String, Object> checkWaterDetails(int school_id) {
		String sql = "select drinking_water_id, is_purifier, borewell_count, is_borewell, is_storage, purifier_capacity from drinking_water where school_id = ? ORDER BY drinking_water_id DESC LIMIT 1";
	    try {
	        return jdbcTemplate.queryForMap(sql, school_id);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyMap(); 
	    }
	}
	//initial
	/*@Transactional
	public List<Map<String, Object>> checkWaterSource(int school_id) {
		String sql = "select ws.water_supply_id, ws.source_water_id, ws.supply_type, sw.water_type "
				+ "FROM "
				+ "drinking_water dw\r\n"
				+ "INNER JOIN "
				+ "    water_supply ws ON dw.drinking_water_id = ws.drinking_water_id\r\n"
				+ "INNER JOIN "
				+ "    source_water sw ON ws.source_water_id = sw.source_water_id\r\n"
				+ "    WHERE "
				+ "    dw.school_id = ? and ws.is_active = 1";
	    try {
	        return jdbcTemplate.queryForList(sql, school_id);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return List.of();
	    }
	}*/
	@Transactional
	public List<Map<String, Object>> checkWaterSource(int school_id) {
		String sql = "select ws.water_supply_id, ws.source_water_id, ws.supply_type, sw.water_type "
				+ "FROM "
				+ "drinking_water dw\r\n"
				+ "INNER JOIN "
				+ "    water_supply ws ON dw.drinking_water_id = ws.drinking_water_id\r\n"
				+ "INNER JOIN "
				+ "    source_water sw ON ws.source_water_id = sw.source_water_id\r\n"
				+ "    WHERE "
				+ "    dw.school_id = ? and ws.is_active = 1 ORDER BY water_supply_id";// DESC LIMIT 1";
	    try {
	        return jdbcTemplate.queryForList(sql, school_id);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return List.of();
	    }
	}
//	@Transactional
//	public String updateDrinkingWater(Boolean is_purifier, Double purifier_capacity, Boolean is_borewell,
//			int borewell_count, Boolean is_storage, int drinking_water_id, int school_id,
//			List<Map<String, Object>> supply, List<Map<String, Object>> storage) {
//		try {
//			updateDrinkWater(is_purifier, purifier_capacity, is_borewell, borewell_count, is_storage, school_id, drinking_water_id);
//			for (Map<String, Object> supplies : supply) {
//				int water_supply_id = Integer.parseInt(supplies.get("water_supply_id").toString());
//				int source_water_id = Integer.parseInt(supplies.get("source_water_id").toString());
//				String supply_type = (String) supplies.get("supply_type");
//				updateWaterSupplyDetails(water_supply_id, source_water_id, supply_type);
//			}
//
//			for (Map<String, Object> store : storage) {
//				int water_storage_id = Integer.parseInt(store.get("water_storage_id").toString());
//				int storage_water_id = Integer.parseInt(store.get("storage_water_id").toString());
//				Double capacity = (Double) store.get("capacity");
//				updateWaterStorageDetails(water_storage_id, storage_water_id, capacity);
//			}
//			return "Sucess";
//			} catch (Exception e) {
//				e.printStackTrace();
//				return "Error";
//			}
//	}
	
	//Ajithh
	@Transactional
	public String updateDrinkingWater(Boolean is_purifier, Double purifier_capacity, Boolean is_borewell,
	                                  int borewell_count, Boolean is_storage, int drinking_water_id, int school_id,
	                                  List<Map<String, Object>> supply, List<Map<String, Object>> storage) {
	    try {
	        // Step 1: Update main table
	        //updateDrinkWater(is_purifier, purifier_capacity, is_borewell, borewell_count, is_storage, school_id, drinking_water_id);

	        // Step 2: UPSERT for water supply
	        for (Map<String, Object> item : supply) {
	            int source_water_id = Integer.parseInt(item.get("source_water_id").toString());
	            String supply_type = item.get("supply_type").toString();

	            // Check if record exists
	            String checkSql = "SELECT COUNT(*) FROM water_supply WHERE drinking_water_id = ? AND source_water_id = ?";
	            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, drinking_water_id, source_water_id);

	            if (count != null && count > 0) {
	                // Update existing
	            	jdbcTemplate.update("UPDATE water_supply SET supply_type = ?, is_active = 1 WHERE drinking_water_id = ? AND source_water_id = ?",
	            		    supply_type, drinking_water_id, source_water_id);

	                       
	            } else {
	                // Insert new
	                jdbcTemplate.update("INSERT INTO water_supply (drinking_water_id, source_water_id, supply_type, school_id) VALUES (?, ?, ?, ?)",
	                        drinking_water_id, source_water_id, supply_type, school_id);
	            }
	        }

	        // Step 3: UPSERT for water storage
	        for (Map<String, Object> item : storage) {
	            int storage_water_id = Integer.parseInt(item.get("storage_water_id").toString());
	            double capacity = Double.parseDouble(item.get("capacity").toString());

	            String checkSql = "SELECT COUNT(*) FROM water_storage WHERE drinking_water_id = ? AND storage_water_id = ?";
	            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, drinking_water_id, storage_water_id);

	            if (count != null && count > 0) {
	            	jdbcTemplate.update("UPDATE water_storage SET capacity = ?, is_active = 1 WHERE drinking_water_id = ? AND storage_water_id = ?",
	            		    capacity, drinking_water_id, storage_water_id);

	            } else {
	                jdbcTemplate.update("INSERT INTO water_storage (drinking_water_id, storage_water_id, capacity, school_id) VALUES (?, ?, ?, ?)",
	                        drinking_water_id, storage_water_id, capacity, school_id);
	            }
	        }

	        return "Sucess";

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Error";
	    }
	}



//	private void updateWaterStorageDetails(int water_storage_id, int storage_water_id, Double capacity) {
//		String sql = "UPDATE water_storage SET capacity = ?, storage_water_id = ? WHERE water_storage_id = ? ";
//		try {
//			jdbcTemplate.update(sql, capacity, storage_water_id, water_storage_id);
//		
//		}catch (DataAccessException e) {
//			e.printStackTrace();
//			
//		}	
//		
//	}
//
//	private void updateWaterSupplyDetails(int water_supply_id, int source_water_id, String supply_type) {
//		String sql = "UPDATE water_supply SET source_water_id = ?, supply_type = ? WHERE water_supply_id = ? ";
//		try {
//			jdbcTemplate.update(sql, source_water_id, supply_type, water_supply_id);
//		
//		}catch (DataAccessException e) {
//			e.printStackTrace();
//			
//		}	
//		
//	}
//
//	private void updateDrinkWater(Boolean is_purifier, Double purifier_capacity, Boolean is_borewell,
//			int borewell_count, Boolean is_storage, int school_id, int drinking_water_id) {
//		
//		String sql = "UPDATE drinking_water SET is_purifier = ?, purifier_capacity = ?, is_borewell = ?, borewell_count = ?, is_storage = ? WHERE drinking_water_id = ? and school_id = ? ";
//		try {
//			jdbcTemplate.update(sql, is_purifier, purifier_capacity, is_borewell, borewell_count, is_storage, drinking_water_id, school_id);
//		
//		}catch (DataAccessException e) {
//			e.printStackTrace();
//			
//		}	
//	}
	//ajith
	@Transactional
	public void updateDrinkWater(Boolean is_purifier, Double purifier_capacity, Boolean is_borewell,
			int borewell_count, Boolean is_storage, int school_id, int drinking_water_id,int drinkingwater_tap,int handwash_tap) {
		
		
		String sql = "UPDATE drinking_water SET is_purifier = ?, purifier_capacity = ?, is_borewell = ?, borewell_count = ?, is_storage = ?,drinkingwater_tap=?,handwash_tap=? WHERE drinking_water_id = ? and school_id = ? ";
		try {
			jdbcTemplate.update(sql, is_purifier, purifier_capacity, is_borewell, borewell_count, is_storage,drinkingwater_tap, handwash_tap,drinking_water_id, school_id);
		
		}catch (DataAccessException e) {
			e.printStackTrace();
			
		}	
	}
	
	@Transactional
	public void deactiveWaterSupply(int school_id, int drinking_water_id) {
		String sql = "UPDATE water_supply SET is_active = false WHERE school_id = ? and drinking_water_id = ? ";
		try {
			jdbcTemplate.update(sql, school_id, drinking_water_id);
		
		}catch (DataAccessException e) {
			e.printStackTrace();
			
		}	
		
	}

	@Transactional
	public void deactiveWaterStorage(int school_id, int drinking_water_id) {
		String sql = "UPDATE water_storage SET is_active = false WHERE school_id = ? and drinking_water_id = ? ";
		try {
			jdbcTemplate.update(sql, school_id, drinking_water_id);
		
		}catch (DataAccessException e) {
			e.printStackTrace();
			
		}	
		
	}
	
	private void updateWaterSupplyDetails(int water_supply_id, int source_water_id, String supply_type) {
		String sql = "UPDATE water_supply SET source_water_id = ?, supply_type = ? WHERE water_supply_id = ?";
		try {
			jdbcTemplate.update(sql, source_water_id, supply_type, water_supply_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	private void updateWaterStorageDetails(int water_storage_id, int storage_water_id, Double capacity) {
		String sql = "UPDATE water_storage SET capacity = ?, storage_water_id = ? WHERE water_storage_id = ?";
		try {
			jdbcTemplate.update(sql, capacity, storage_water_id, water_storage_id);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	//initial
	
	/*@Transactional
	public List<Map<String, Object>> checkWaterStotage(int school_id) {
		String sql = " select  wst.water_storage_id, wst.capacity, wstm.storage_type\r\n"
				+ "    FROM "
				+ "    drinking_water dw\r\n"
				+ "    INNER JOIN\r\n"
				+ "    water_storage wst ON dw.drinking_water_id = wst.drinking_water_id   "
				+ "INNER JOIN\r\n"
				+ "    water_storage_master wstm ON wst.storage_water_id = wstm.storage_water_id    "
				+ "WHERE "
				+ "    dw.school_id = ?  and wst.is_active = 1";
	    try {
	        return jdbcTemplate.queryForList(sql, school_id);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return List.of();
	    }
	}*/
	@Transactional
	public List<Map<String, Object>> checkWaterStotage(int school_id) {
		String sql = " select  wst.water_storage_id, wst.capacity, wstm.storage_type\r\n"
				+ "    FROM "
				+ "    drinking_water dw\r\n"
				+ "    INNER JOIN\r\n"
				+ "    water_storage wst ON dw.drinking_water_id = wst.drinking_water_id   "
				+ "INNER JOIN\r\n"
				+ "    water_storage_master wstm ON wst.storage_water_id = wstm.storage_water_id    "
				+ "WHERE "
				+ "    dw.school_id = ?  and wst.is_active = 1 ORDER BY water_storage_id"; //DESC LIMIT 1";
	    try {
	        return jdbcTemplate.queryForList(sql, school_id);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return List.of();
	    }
	}
//initial
	/*@Transactional
	public List<Map<String, Object>> checkToiletDetails(int school_id) {
		String sql = "SELECT * FROM toilet WHERE school_id = ?";
		try {
			return jdbcTemplate.queryForList(sql, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}*/
	@Transactional
	public List<Map<String, Object>> checkToiletDetails(int school_id) {
		String sql = "SELECT * FROM toilet WHERE school_id = ? ORDER BY toilet_id DESC LIMIT 1";
		try {
			return jdbcTemplate.queryForList(sql, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}
	
	@Transactional
	public String updateToiletDetails(Integer total_toiletBlocks, int total_male_urinals, int total_female_urinals,int staff_male_toilets,int staff_female_toilets,int total_wc_male,
			int total_wc_female, int total_diffAbled_toilet, Boolean is_napkins, int school_id) {
		String sql = "UPDATE toilet SET total_toiletBlocks = ?, total_male_urinals = ?,total_female_urinals=?,staff_male_toilet=?,staff_female_toilet=?, total_wc_male = ?, total_wc_female = ?, total_diffAbled_toilet = ?, is_napkins = ? WHERE school_id = ? ";
		try {
			jdbcTemplate.update(sql, total_toiletBlocks, total_male_urinals,total_female_urinals,staff_male_toilets,staff_female_toilets, total_wc_male, total_wc_female,
					total_diffAbled_toilet, is_napkins, school_id);
			return "Success";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "Error";
		}
	}

	@Transactional
	public String saveAssetMaster(int sub_asset_id, int master_qty, int school_id, int asset_id) {
		String sql ="";
		if(asset_id==2) {
	       sql = "INSERT INTO electrical_master (sub_asset_id, master_qty, unmapped_qty, school_id) VALUES (?, ?, ?, ?)";
		} else if(asset_id==3) {
			 sql = "INSERT INTO furniture_master (sub_asset_id, master_qty, unmapped_qty, school_id) VALUES (?, ?, ?, ?)";
		}
		try {
			jdbcTemplate.update(sql, sub_asset_id, master_qty, master_qty, school_id);
			return "Success";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "Error";
		}
	}
	
	@Transactional
	public List<Map<String, Object>> getAssetMaster(int school_id, int asset_id) {
		String sql="";
		if(asset_id==2) {
		 sql = "SELECT asm.asset_name, em.electrical_master_id, em.master_qty, em.mapped_qty, em.unmapped_qty, em.scrap_qty from electrical_master em "
				+ "INNER JOIN asset_sub_master asm ON em.sub_asset_id = asm.sub_asset_id\r\n"
				+ " WHERE em.school_id = ?";
		} else if(asset_id==3) {
			sql = "SELECT asm.asset_name, fm.furniture_master_id, fm.master_qty, fm.mapped_qty, fm.unmapped_qty, fm.scrap_qty from furniture_master fm "
					+ "INNER JOIN asset_sub_master asm ON fm.sub_asset_id = asm.sub_asset_id\r\n"
					+ " WHERE fm.school_id = ?";
		}
		try {
			return jdbcTemplate.queryForList(sql, school_id);
		} catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}
	
	@Transactional
	public Map<String, Object> getAssetMasterById(int assetMasterId, int asset_id) {
		String sql="";
		if(asset_id==2) {
		 sql = "SELECT em.*,asm.asset_name from electrical_master em INNER JOIN asset_sub_master asm ON em.sub_asset_id = asm.sub_asset_id where em.electrical_master_id = ?";
		} else if(asset_id==3) {
			sql = "SELECT fm.*,asm.asset_name from furniture_master fm  INNER JOIN asset_sub_master asm ON fm.sub_asset_id = asm.sub_asset_id where fm.furniture_master_id = ?";
		}
		try {
			return jdbcTemplate.queryForMap(sql, assetMasterId);
		} catch (Exception e) {
			e.printStackTrace();
			return Map.of();
		}
	}
	
	@Transactional
	public String updateAssetMaster(int assetMasterId, int master, int asset_id, int unmapped) {
		String sql ="";
		if (asset_id == 2) {
		 sql = "UPDATE electrical_master SET master_qty = ?, unmapped_qty = ? WHERE electrical_master_id = ? ";
		} else if(asset_id==3) {
			sql = "UPDATE furniture_master SET master_qty = ?, unmapped_qty = ? WHERE furniture_master_id = ? ";
		}
		 try {
			jdbcTemplate.update(sql, master, unmapped, assetMasterId);
			return "Success";
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "Error";
		}
	}
	
	public List<Map<String, Object>> getAnganwadiDetailsBySchId(int school_id) {
		
		String sql = "SELECT rd.room_id,rd.room_num AS room_name,bd.building_name, "
				+ "rd.building_id,fl.floor_name AS floor, "
				+ "rm.room_type,rd.school_id,rm.room_master_id,rd.anganwadi_unique_number, "
				+ "fl.floors_id FROM room_details rd "
				+ "JOIN buildings bd ON bd.buildng_id = rd.building_id "
				+ "JOIN floor_list fl ON fl.floors_id = rd.floors_id "
				+ "JOIN room_master rm ON rd.room_type = rm.room_master_id "
				+ "WHERE rd.school_id = ? AND rd.is_active = 1 AND rd.is_delete = 0 AND rd.room_type='32' AND rd.anganwadi_check = 0";

		return jdbcTemplate.queryForList(sql, school_id);
	}
	public String updateStaffStrength(int sgRegTeacher, int sgTempTeacher, int sgPtTeacher, int sgNgoTeacher,
			int btRegTeacher, int btTempTeacher, int btPtTeacher, int btNgoTeacher, int pgRegTeacher, int pgTempTeacher,
			int pgPtTeacher, int pgNgoTeacher, int kgRegTeacher, int kgTempTeacher, int petRegTeacher,
			int petTempTeacher, int petGrade1Teacher, int petGrade2Teacher, int petPtTeacher, Boolean is_hm,
			int stRegTeacher, int stPtTeacher, int totOa, int kgAyah, int compAss, String watchmen, int scavengers,
			int maintenanceWork, int schoolId, List<String> specialCategoryId) {

		try {
			 updateStaffCounts(sgRegTeacher, sgTempTeacher, sgPtTeacher, sgNgoTeacher, btRegTeacher,
					btTempTeacher, btPtTeacher, btNgoTeacher, pgRegTeacher, pgTempTeacher, pgPtTeacher, pgNgoTeacher,
					kgRegTeacher, kgTempTeacher, petRegTeacher, petTempTeacher, petGrade1Teacher, petGrade2Teacher, petPtTeacher,
					is_hm, stRegTeacher, stPtTeacher, totOa, kgAyah, compAss, watchmen, scavengers, maintenanceWork, schoolId);
			 
		  if(specialCategoryId.isEmpty()) {
			  deactivateSpecialCategory(schoolId); 
		  }
			
		  if(!specialCategoryId.isEmpty()) {
			 
			int staffId=getstaffId(schoolId);
			if(staffId!=0) {
				    deactivateSpecialCategory(schoolId);
					for(String special_category_id : specialCategoryId) {
						saveSpecialCategoryDetails(special_category_id, schoolId, staffId);
					}
				}
				
			}
		  
			
			return "Staff Details Updated Sucessfully";
			} catch (Exception e) {
				e.printStackTrace();
				return "Error";
			}
	}
	
	private int getstaffId(int schoolId) {
		
		String sql = "SELECT staff_id FROM staff_strength_1 WHERE school_id = ? ORDER BY staff_id DESC LIMIT 1";
		try {
	        return jdbcTemplate.queryForObject(sql, new Object[]{schoolId}, Integer.class);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return 0; // or any default/fallback value
	    }	
	}

	@Transactional
	public void deactivateSpecialCategory(int schoolId) {
		
		String sql = "UPDATE special_category_details SET is_active = 0 AND is_delete = 1 WHERE school_id = ? ";
		try {
			jdbcTemplate.update(sql, schoolId);		
		}catch (DataAccessException e) {
			e.printStackTrace();			
		}	
		
	}

	private void updateStaffCounts(int sgRegTeacher, int sgTempTeacher, int sgPtTeacher, int sgNgoTeacher,
			int btRegTeacher, int btTempTeacher, int btPtTeacher, int btNgoTeacher, int pgRegTeacher, int pgTempTeacher,
			int pgPtTeacher, int pgNgoTeacher, int kgRegTeacher, int kgTempTeacher, int petRegTeacher,
			int petTempTeacher, int petGrade1Teacher, int petGrade2Teacher, int petPtTeacher, Boolean is_hm,
			int stRegTeacher, int stPtTeacher, int totOa, int kgAyah, int compAss, String watchmen, int scavengers,
			int maintenanceWork, int schoolId) {
		
		String sql = "UPDATE staff_strength_1 SET "
			    + "sg_reg_teacher = ?, sg_temp_teacher = ?, sg_pt_teacher = ?, sg_ngo_teacher = ?, "
			    + "bt_reg_teacher = ?, bt_temp_teacher = ?, bt_pt_teacher = ?, bt_ngo_teacher = ?, "
			    + "pg_reg_teacher = ?, pg_temp_teacher = ?, pg_pt_teacher = ?, pg_ngo_teacher = ?, "
			    + "kg_reg_teacher = ?, kg_temp_teacher = ?, "
			    + "pet_reg_teacher = ?, pet_temp_teacher = ?, pet_grade1_teacher = ?, pet_grade2_teacher = ?, pet_pt_teacher = ?, "
			    + "is_hm = ?, st_reg_teacher = ?, st_pt_teacher = ?, tot_oa = ?, kg_ayah = ?, comp_ass = ?, "
			    + "watchmen = ?, scavengers = ?, maintenance_work = ? "
			    + "WHERE school_id = ?";

		
		try {
						
			 jdbcTemplate.update(sql, sgRegTeacher,  sgTempTeacher,  sgPtTeacher,  sgNgoTeacher,
					 btRegTeacher,  btTempTeacher,  btPtTeacher,  btNgoTeacher,  pgRegTeacher,  pgTempTeacher,
					 pgPtTeacher,  pgNgoTeacher,  kgRegTeacher,  kgTempTeacher,  petRegTeacher,
					 petTempTeacher,  petGrade1Teacher,  petGrade2Teacher,  petPtTeacher, is_hm,
					 stRegTeacher,  stPtTeacher,  totOa,  kgAyah,  compAss,  watchmen,  scavengers,
					 maintenanceWork,  schoolId);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

}
