package in.gov.chennaicorporation.gccoffice.nulm.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gov.chennaicorporation.gccoffice.nulm.repository.NulmRepository;

@Service
public class NulmService {

	@Autowired
	private NulmRepository nulmRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public NulmService(NulmRepository nulmRepository, JdbcTemplate jdbcTemplate) {
		this.nulmRepository = nulmRepository;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Autowired
	public void setDataSource(@Qualifier("mysqlNulmDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void saveOrderDetails(Map<String, Object> orderData) {
		nulmRepository.saveOrderDetails(orderData);
	}

	public void saveSchemeGroup(Map<String, Object> groupData) {
		nulmRepository.saveSchemeGroup(groupData);
	}

	public void saveStaffDetails(Map<String, Object> staffData) {
		nulmRepository.saveStaffDetails(staffData);
	}

	public List<Map<String, Object>> getYearList() {
		String sql = "SELECT DISTINCT year(coalesce(indatetime,oddatetime,leavedatetime)) as year FROM attendance ";
		return jdbcTemplate.queryForList(sql);
	}

	public List<Map<String, Object>> getMonthList(int year) {
		String sql = "SELECT DISTINCT MONTHNAME(coalesce(indatetime,oddatetime,leavedatetime)) as month FROM attendance where "
				+ "year(coalesce(indatetime,oddatetime,leavedatetime)) = ? ";
		return jdbcTemplate.queryForList(sql, year);
	}

	public List<Map<String, Object>> getGenderList() {
		String sql = "SELECT DISTINCT(gender) as gender FROM enrollment_table ";
		return jdbcTemplate.queryForList(sql);
	}

	public List<Map<String, Object>> getDivisionList(String zone) {
		String sql = "SELECT DISTINCT(division) as division FROM enrollment_table where zone = ? ";
		return jdbcTemplate.queryForList(sql, zone);
	}

	public void assignOrderToEnrollments(int orderId, List<Integer> enrollmentIds) {
		// Step 1: Retrieve the selected order
		String getOrderSql = "SELECT * FROM order_details WHERE order_id = ?";
		Map<String, Object> orderDetails;
		try {
			orderDetails = jdbcTemplate.queryForMap(getOrderSql, orderId);
		} catch (EmptyResultDataAccessException e) {
			// Handle the case where no order is found
			System.err.println("No order found with order_id: " + orderId);
			return; // Exit the method if the order is not found
		}

		if (orderDetails != null) {
			// Step 2: Build the SQL for updating enrollments with dynamic IN clause
			StringBuilder updateSql = new StringBuilder(
					"UPDATE enrollment_table SET order_id = ?, appointed = 'Yes', facial_attendance = 'No', appointed_date = CURRENT_DATE WHERE enrollment_id IN (");

			// Add placeholders for the IN clause based on the size of the enrollmentIds
			// list
			for (int i = 0; i < enrollmentIds.size(); i++) {
				updateSql.append("?");
				if (i < enrollmentIds.size() - 1) {
					updateSql.append(", ");
				}
			}
			updateSql.append(")");

			// Combine the orderId with the list of enrollmentIds
			List<Object> parameters = new ArrayList<>();
			parameters.add(orderId);
			parameters.addAll(enrollmentIds);

			// Execute the update query
			try {
				jdbcTemplate.update(updateSql.toString(), parameters.toArray());
			} catch (DataAccessException e) {
				// Handle any data access exceptions
				System.err.println("Error updating enrollments: " + e.getMessage());
			}
		}
	}

	public Map<String, Integer> getAppointedCounts(int orderId) {
		// Query to get counts from the enrollment_table
		String enrollmentQuery = "SELECT " +
				"COUNT(CASE WHEN appointed = 'Yes' THEN 1 END) AS total_yes, " +
				"COUNT(CASE WHEN appointed = 'No' THEN 1 END) AS total_no " +
				"FROM enrollment_table " +
				"WHERE order_id = ?";

		// Query to get total_count from order_details
		String orderDetailsQuery = "SELECT no_of_staffs AS total_count " +
				"FROM order_details " +
				"WHERE order_id = ?";

		// Execute the first query
		Map<String, Integer> counts = jdbcTemplate.queryForObject(enrollmentQuery, new Object[] { orderId },
				(rs, rowNum) -> {
					Map<String, Integer> result = new HashMap<>();
					result.put("total_yes", rs.getInt("total_yes"));
					result.put("total_no", rs.getInt("total_no"));
					return result;
				});

		// Execute the second query
		Integer totalCount = jdbcTemplate.queryForObject(orderDetailsQuery, new Object[] { orderId }, Integer.class);

		// Add the total_count to the result map
		counts.put("total_count", totalCount);

		// Calculate the output value: total_count - total_yes
		int outputValue = totalCount - counts.get("total_yes");

		// Add the output value to the result map
		counts.put("output_value", outputValue);

		return counts;
	}

	public Map<String, Integer> getDropCounts(int orderId) {
		// Query to get counts from the enrollment_table
		String enrollmentQuery = "SELECT " +
				"COUNT(CASE WHEN appointed = 'Yes' THEN 1 END) AS total_yes, " +
				"COUNT(CASE WHEN dropout = 'Yes' THEN 1 END) AS total_drop, " +
				"COUNT(CASE WHEN appointed IS NULL THEN 1 END) AS total_null " +
				"FROM enrollment_table " +
				"WHERE order_id = ?";

		// Query to get total_count from order_details
		String orderDetailsQuery = "SELECT no_of_staffs AS total_count " +
				"FROM order_details " +
				"WHERE order_id = ?";

		// Execute the first query
		Map<String, Integer> counts = jdbcTemplate.queryForObject(enrollmentQuery, new Object[] { orderId },
				(rs, rowNum) -> {
					Map<String, Integer> result = new HashMap<>();
					result.put("total_yes", rs.getInt("total_yes"));
					result.put("total_drop", rs.getInt("total_drop"));
					result.put("total_null", rs.getInt("total_null"));
					return result;
				});

		// Execute the second query
		Integer totalCount = jdbcTemplate.queryForObject(orderDetailsQuery, new Object[] { orderId }, Integer.class);

		// Add the total_count to the result map
		counts.put("total_count", totalCount);

		return counts;
	}

	public List<Map<String, Object>> getAllSchemeGroupNames() {
		return nulmRepository.getAllSchemeGroupNames();
	}

	public List<Map<String, Object>> getEnrollmentDetails(int groupId) {
		String sql = "SELECT * FROM enrollment_table WHERE group_id = ? AND (appointed IS NULL OR appointed = 'NO')";
		return jdbcTemplate.queryForList(sql, groupId);
	}

	public List<Map<String, Object>> getAppointedDetails(int groupId) {
		String sql = "SELECT * FROM enrollment_table WHERE group_id = ? AND (appointed IS NULL OR appointed = 'Yes')";
		return jdbcTemplate.queryForList(sql, groupId);
	}

	public int markAbsentEntry(int employeeId) {
		// Query to check if the employee has already been marked absent for today
		String checkAbsentQuery = "SELECT absent FROM attendance WHERE employee_id = ? AND attendance_date = CURDATE()";

		List<Integer> absentResults = jdbcTemplate.query(
				checkAbsentQuery,
				ps -> ps.setInt(1, employeeId),
				(rs, rowNum) -> rs.getInt("absent"));

		// If no record found for today, mark as absent
		if (absentResults.isEmpty()) {
			// Insert a new row to mark the employee as absent for today
			String insertAbsentQuery = "INSERT INTO attendance (employee_id, absent, mark_attendance, attendance_date) VALUES (?, 1, 'ABSENT', CURDATE())";
			return jdbcTemplate.update(insertAbsentQuery, employeeId);
		}

		// If already marked absent for today, return 0 indicating no action taken
		return 0;
	}

	public int markOverDuty(int employeeId) {
		// Check if the employee ID exists in the attendance table
		String checkEmployeeQuery = "SELECT od FROM attendance WHERE employee_id = ?";

		List<Integer> odResults = jdbcTemplate.query(
				checkEmployeeQuery,
				ps -> ps.setInt(1, employeeId),
				(rs, rowNum) -> rs.getInt("od"));

		// If the employee ID exists, update the OD field to 1
		if (!odResults.isEmpty()) {
			String updateOdQuery = "UPDATE attendance SET od = 1, mark_attendance = 'OD' WHERE employee_id = ?";
			return jdbcTemplate.update(updateOdQuery, employeeId);
		}

		// If the employee ID doesn't exist, return 0 to indicate no update
		return 0;
	}

	public List<Map<String, Object>> getAllOrderDetails() {
		return nulmRepository.getAllOrderDetails();
	}

	public String markAttendance(int employeeId, String markAttendance) {
		// Get today's date
		LocalDate today = LocalDate.now();

		// Query to check the current status of check_in and check_out for the employee
		// on the current date
		String checkAttendanceQuery = "SELECT check_in, check_out FROM attendance WHERE employee_id = ? AND attendance_date = ?";

		List<Map<String, Object>> attendanceResults = jdbcTemplate.query(
				checkAttendanceQuery,
				ps -> {
					ps.setInt(1, employeeId);
					ps.setObject(2, today);
				},
				(rs, rowNum) -> {
					Map<String, Object> result = new HashMap<>();
					result.put("check_in", rs.getInt("check_in"));
					result.put("check_out", rs.getInt("check_out"));
					return result;
				});

		// If the employee has not checked in today
		if (attendanceResults.isEmpty()) {
			if ("IN".equalsIgnoreCase(markAttendance)) {
				// Mark check-in (entry phase)
				String insertAttendanceQuery = "INSERT INTO attendance (employee_id, mark_attendance, check_in, in_time, attendance_date) VALUES (?, 'IN', 1, NOW(), ?)";
				jdbcTemplate.update(insertAttendanceQuery, employeeId, today);
				return "Checked in successfully";
			} else {
				// Trying to check out without a check-in for today
				return "Failed to mark attendance - Cannot check out without checking in first";
			}
		}

		// Retrieve the first (and only) result since attendance is checked by employee
		// ID and date
		Map<String, Object> result = attendanceResults.get(0);
		Integer checkInStatus = (Integer) result.get("check_in");
		Integer checkOutStatus = (Integer) result.get("check_out");

		// If the markAttendance is 'OUT' and check-in is already marked as 1
		if ("OUT".equalsIgnoreCase(markAttendance) && checkInStatus == 1
				&& (checkOutStatus == null || checkOutStatus == 0)) {
			// Mark check-out (exit phase)
			String updateCheckOutQuery = "UPDATE attendance SET check_out = 1, out_time = NOW(), mark_attendance = 'OUT' WHERE employee_id = ? AND attendance_date = ?";
			int rowsUpdated = jdbcTemplate.update(updateCheckOutQuery, employeeId, today);
			if (rowsUpdated > 0) {
				return "Checked out successfully";
			} else {
				return "Failed to mark attendance - Could not update check-out status";
			}
		}

		// If trying to check in again when already checked in
		if ("IN".equalsIgnoreCase(markAttendance) && checkInStatus == 1) {
			return "Failed to mark attendance - Already checked in today";
		}

		// If trying to check out but not meeting the conditions
		return "Failed to mark attendance - Invalid conditions for check-out";
	}

	public List<String> getAllOrderNumber() {
		return nulmRepository.getAllOrderNumber();
	}

	public List<Map<String, Object>> getAllSchemeGroups() {
		return nulmRepository.getAllSchemeGroups();
	}

	public List<Map<String, Object>> getAllOrders() {
		return nulmRepository.getAllOrders();
	}

	public List<String> getStaffName() {
		return nulmRepository.getStaffName();
	}

	public List<Map<String, Object>> getEnrollments(String department, String division) {
		StringBuilder sql = new StringBuilder("SELECT e.*, o.order_number, s.group_name " +
				"FROM enrollment_table e " +
				"LEFT JOIN order_details o ON e.order_id = o.order_id " +
				"LEFT JOIN suyaudavi_kuzhu s ON e.group_id = s.group_id " +
				"WHERE e.appointed = '1' AND e.isactive = '1' AND e.isdelete = '0'");

		if (department != null && !department.isEmpty()) {
			sql.append(" AND e.department = '").append(department).append("'");
		}
		if (division != null && !division.isEmpty()) {
			sql.append(" AND e.division = '").append(division).append("'");
		}

		return jdbcTemplate.queryForList(sql.toString());
	}

	public List<Map<String, Object>> getDepartment() {
		String sql = "SELECT DISTINCT department FROM enrollment_table WHERE isactive = 1 AND isdelete = 0";
		return jdbcTemplate.queryForList(sql);
	}

	public List<Map<String, Object>> getDivision() {
		String sql = "SELECT DISTINCT division FROM enrollment_table WHERE isactive = 1 AND isdelete = 0";
		return jdbcTemplate.queryForList(sql);
	}

	public List<Map<String, Object>> getEnrollmentsById(int enrollmentId) {
		String sql = "SELECT * FROM enrollment_table " +
				"WHERE enrollment_id = ? " +
				"AND isactive = 1 " +
				"AND isdelete = 0 " +
				"AND (appointed = 1 OR appointed IS NULL) " +
				"AND (dropout = 0 OR dropout IS NULL) ";

		try {
			// Query the database for enrollment details based on the specified conditions
			// and enrollment_id
			return jdbcTemplate.queryForList(sql, new Object[] { enrollmentId });
		} catch (Exception e) {
			// Return an empty list if no enrollments are found
			return List.of();
		}
	}

	public List<Map<String, Object>> getSelfHelpGroups() {
		String sql = "SELECT group_id AS groupId, group_name AS groupName FROM suyaudavi_kuzhu WHERE isactive = 1 AND isdelete = 0 order by group_name asc";
		return jdbcTemplate.queryForList(sql);
	}

	public List<Map<String, Object>> getOrderNumbers() {
		String sql = "SELECT order_id AS orderId, order_number AS orderNumber FROM order_details WHERE isactive = 1 AND isdelete = 0";
		return jdbcTemplate.queryForList(sql);
	}

	public int updateEnrollment(
			int enrollmentId,
			String dateOfBirth,
			String gender,
			int groupId, // New parameter
			int orderId // New parameter
	) {
		String sql = "UPDATE enrollment_table SET date_of_birth = ?, gender = ?, group_id = ?, order_id = ? WHERE enrollment_id = ?";

		return jdbcTemplate.update(sql, dateOfBirth, gender, groupId, orderId, enrollmentId);
	}

	/*
	 * public String saveOrderDetails(String order_number, Date order_date, int
	 * no_of_staffs, String order_description, String order_copy_url, String
	 * order_generated_by, String category, Date validity_date) {
	 * String sql =
	 * "INSERT INTO order_details (order_number, order_date, no_of_staffs, order_description, order_copy_url, order_generated_by, category, created_date, validity_date, order_status, isactive, isdelete) "
	 * +
	 * "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CASE WHEN ? >= ? THEN 'Active' ELSE 'Expired' END, 1, 0)"
	 * ;
	 * 
	 * try {
	 * // Parameters array to be passed to the update method
	 * Object[] params = new Object[] {
	 * order_number,
	 * order_date,
	 * no_of_staffs,
	 * order_description,
	 * order_copy_url,
	 * order_generated_by,
	 * category,
	 * validity_date,
	 * validity_date, // For the CASE WHEN condition
	 * new Date() // Current date for comparison with validity_date
	 * };
	 * 
	 * // Execute the update
	 * jdbcTemplate.update(sql, params);
	 * 
	 * return order_number;
	 * } catch (Exception e) {
	 * e.printStackTrace();
	 * return "Error in Saving Order Details, give proper input";
	 * }
	 * }
	 */

	public String saveOrderDetails(String order_number, Date order_date, int no_of_staffs, String order_description,
			String order_copy_url, String order_generated_by, String category, Date validity_date) {
		String sql = "INSERT INTO order_details (order_number, order_date, no_of_staffs, order_description, order_copy_url, order_generated_by, category, created_date, validity_date, order_status, isactive, isdelete) "
				+
				"VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CASE WHEN ? >= ? THEN 'Active' ELSE 'Expired' END, 1, 0)";

		try {
			// Parameters array to be passed to the update method
			Object[] params = new Object[] {
					order_number,
					order_date,
					no_of_staffs,
					order_description,
					order_copy_url,
					order_generated_by,
					category,
					validity_date,
					validity_date, // For the CASE WHEN condition
					order_date // Comparison with order_date instead of the current date
			};

			// Log the SQL query for debugging
			System.out.println(
					"INSERT INTO order_details (order_number, order_date, no_of_staffs, order_description, order_copy_url, order_generated_by, category, created_date, validity_date, order_status, isactive, isdelete) "
							+
							"VALUES ('" + order_number + "', '" + order_date + "', '" + no_of_staffs + "', '"
							+ order_description + "', '" + order_copy_url + "', '" + order_generated_by + "', '"
							+ category + "', CURRENT_TIMESTAMP, " +
							validity_date + ", CASE WHEN " + validity_date + " >= " + order_date
							+ " THEN 'Active' ELSE 'Expired' END, 1, 0)");

			// Execute the update
			jdbcTemplate.update(sql, params);

			return order_number;
		} catch (Exception e) {
			e.printStackTrace();
			return "Error in Saving Order Details, give proper input";
		}
	}

	public int addSalaryDetails(int totalDaysPresent, int totalDaysOd, int totalDaysSalary, int totalDaysAbsent,
			String month, int year, int salaryAmount,
			String salaryStatus, int enrollmentId, int groupId, int wageId) {
		String sql = "INSERT INTO salary_details (total_days_present, total_days_od, total_days_salary, total_days_absent, month, year, salary_amount, salary_status, enrollment_id, group_id, wage_id) "
				+
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		return jdbcTemplate.update(sql, totalDaysPresent, totalDaysOd, totalDaysSalary, totalDaysAbsent, month, year,
				salaryAmount, salaryStatus, enrollmentId, groupId, wageId);
	}

	public List<Map<String, Object>> getAttendanceWithSalary(String month, Integer year) {
		StringBuilder sql = new StringBuilder("SELECT " +
				"e.enrollment_id, " +
				"e.name, " +
				"e.incharge_id, " +
				"YEAR(coalesce((a.indatetime),(a.oddatetime),(a.leavedatetime))) AS year, " +
				"MONTHNAME(coalesce((a.indatetime),(a.oddatetime),(a.leavedatetime))) AS month, " +
				"COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
				"COUNT(DISTINCT DATE(a.leavedatetime)) AS total_days_absent, " +
				"COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END) AS total_days_od, " +
				"COUNT(DISTINCT DATE(a.indatetime)) + " +
				"COUNT(DISTINCT DATE(a.leavedatetime)) + " +
				"COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END) AS total_days, " +
				"COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END) AS total_days_salary, "
				+
				"(SELECT sw.wage_id FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS wage_id, "
				+
				"(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS salary_per_day, "
				+
				"((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END)) * "
				+
				"(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1)) AS total_salary, "
				+
				"sd.salary_status, " +
				"sk.group_id, " +
				"sk.group_name " +
				"FROM enrollment_table e " +
				"LEFT JOIN attendance a ON e.enrollment_id = a.enrollment_id " + // Changed JOIN to LEFT JOIN to include
																					// all enrollments
				"LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
				"AND YEAR(a.indatetime and a.oddatetime and a.leavedatetime) = sd.year " +
				"AND MONTHNAME(a.indatetime and a.oddatetime and a.leavedatetime) = sd.month " +
				"LEFT JOIN suyaudavi_kuzhu sk ON e.group_id = sk.group_id " +
				"WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) ");

		// Add month condition if month is provided
		if (month != null && !month.isEmpty()) {
			sql.append("AND MONTHNAME(coalesce((a.indatetime),(a.leavedatetime),(a.oddatetime)))= ? ");
		}

		// Add year condition if year is provided
		if (year != null) {
			sql.append("AND YEAR(coalesce((a.indatetime),(a.leavedatetime),(a.oddatetime))) = ? ");
		}

		sql.append("GROUP BY e.enrollment_id, YEAR(coalesce((a.indatetime),(a.oddatetime),(a.leavedatetime))), "
				+ "MONTHNAME(coalesce((a.indatetime),(a.oddatetime),(a.leavedatetime))), sd.salary_status ");

		// Create a list of parameters to pass to the query
		List<Object> params = new ArrayList<>();
		if (month != null && !month.isEmpty()) {
			params.add(month);
		}
		if (year != null) {
			params.add(year);
		}

		// Execute the query with the provided parameters
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		// List<Map<String, Object>> results =
		// jdbcTemplate.queryForList(sql.toString());
		return results;
	}
	/*
	 * public List<Map<String, Object>> getAttendanceWithSalaryByInchargeId(String
	 * month, Integer year, Integer incharge_id) {
	 * String sql =
	 * "SELECT e.enrollment_id, e.emp_id, COALESCE(e.name, '') AS name, YEAR(a.indatetime) AS year, "
	 * + "MONTHNAME(a.indatetime) AS month, COUNT(a.inby) AS total_days_present, "
	 * + "COUNT(a.leaveby) AS total_days_absent, COUNT(a.odby) AS total_days_od, "
	 * + "(COUNT(a.inby) + COUNT(a.leaveby) + COUNT(a.odby)) AS total_days, "
	 * + "(COUNT(a.inby) + COUNT(a.odby)) AS total_days_salary, "
	 * +
	 * "COALESCE((SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1), 0) AS salary_per_day, "
	 * +
	 * "COALESCE(((COUNT(a.inby) + COUNT(a.odby)) * (SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1)), 0) AS total_salary, "
	 * +
	 * "COALESCE(sd.salary_status, '') AS salary_status, COALESCE(sk.group_id, '') AS group_id, "
	 * + "COALESCE(sk.group_name, '') AS group_name "
	 * + "FROM enrollment_table e "
	 * + "JOIN attendance a ON e.enrollment_id = a.enrollment_id "
	 * + "LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id "
	 * + "AND YEAR(a.indatetime) = sd.year AND MONTHNAME(a.indatetime) = sd.month "
	 * + "LEFT JOIN suyaudavi_kuzhu sk ON e.group_id = sk.group_id "
	 * +
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * + "AND e.incharge_id = '" + incharge_id + "' "
	 * + "AND MONTHNAME(a.indatetime) = '" + month + "' "
	 * + "AND YEAR(a.indatetime) = '" + year + "' "
	 * +
	 * "GROUP BY e.enrollment_id, e.name, YEAR(a.indatetime), MONTHNAME(a.indatetime), "
	 * + "sd.salary_status, sk.group_id, sk.group_name";
	 * 
	 * System.out.println(sql);
	 * 
	 * // Execute the query with the provided parameters
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
	 * return results;
	 * }
	 * 
	 * public List<Map<String, Object>> getAttendanceWithSalary(String month,
	 * Integer year) {
	 * StringBuilder sql = new StringBuilder("SELECT " +
	 * "e.enrollment_id, " +
	 * "e.name, " +
	 * "YEAR(a.indatetime) AS year, " +
	 * "MONTHNAME(a.indatetime) AS month, " +
	 * "COUNT(a.inby) AS total_days_present, " +
	 * "COUNT(a.leaveby) AS total_days_absent, " +
	 * "COUNT(a.odby) AS total_days_od, " +
	 * "(COUNT(a.inby) + COUNT(a.leaveby) + COUNT(a.odby)) AS total_days, " +
	 * "(COUNT(a.inby) + COUNT(a.odby)) AS total_days_salary, " +
	 * "(SELECT sw.wage_id FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS wage_id, "
	 * +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS salary_per_day, "
	 * +
	 * "((COUNT(a.inby) + COUNT(a.odby)) * " +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1)) AS total_salary, "
	 * +
	 * "sd.salary_status, " +
	 * "sk.group_id, " +
	 * "sk.group_name " +
	 * "FROM enrollment_table e " +
	 * "JOIN attendance a ON e.enrollment_id = a.enrollment_id " +
	 * "LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
	 * "AND YEAR(a.indatetime) = sd.year " +
	 * "AND MONTHNAME(a.indatetime) = sd.month " +
	 * "LEFT JOIN suyaudavi_kuzhu sk ON e.group_id = sk.group_id " +
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * );
	 * 
	 * // Add month condition if month is provided
	 * if (month != null && !month.isEmpty()) {
	 * sql.append("AND MONTHNAME(a.indatetime) = ? ");
	 * }
	 * 
	 * // Add year condition if year is provided
	 * if (year != null) {
	 * sql.append("AND YEAR(a.indatetime) = ? ");
	 * }
	 * 
	 * sql.
	 * append("GROUP BY e.enrollment_id, e.name, YEAR(a.indatetime), MONTHNAME(a.indatetime), sd.salary_status, sk.group_id, sk.group_name"
	 * );
	 * 
	 * // Create a list of parameters to pass to the query
	 * List<Object> params = new ArrayList<>();
	 * if (month != null && !month.isEmpty()) {
	 * params.add(month);
	 * }
	 * if (year != null) {
	 * params.add(year);
	 * }
	 * 
	 * // Execute the query with the provided parameters
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(),
	 * params.toArray());
	 * return results;
	 * }
	 * 
	 * public List<Map<String, Object>> getSalaryReport(String month, int year,
	 * String salaryStatus,String loginId) {
	 * 
	 * // Retrieve zone access for the user
	 * String ZoneWhare;
	 * String zoneAccessSQL =
	 * "SELECT zone FROM gcc_apps.login_mapping_user WHERE appuser_id = ?";
	 * List<Map<String, Object>> getAccess =
	 * jdbcTemplate.queryForList(zoneAccessSQL, loginId);
	 * System.out.println(zoneAccessSQL +"->"+ loginId);
	 * // Extract the zones from getAccess
	 * List<String> zones = getAccess.stream()
	 * .map(access -> (String) access.get("zone"))
	 * .filter(Objects::nonNull)
	 * .collect(Collectors.toList());
	 * 
	 * // If the user has zone acc ess, add it to the WHERE clause
	 * if (!zones.isEmpty()) {
	 * String zoneList = zones.stream()
	 * .map(zone -> "'" + zone + "'")
	 * .collect(Collectors.joining(", "));
	 * ZoneWhare = " AND e.zone IN ("+zoneList+")";
	 * // System.out.println("AND e.zone IN ("+zoneList+")");
	 * }
	 * else {
	 * ZoneWhare = " AND e.zone IN (0)";
	 * }
	 * 
	 * String sql = "SELECT " +
	 * "e.enrollment_id, " +
	 * "e.emp_id, " +
	 * "e.name, " +
	 * "e.incharge_name, " + // Fetch incharge_name from enrollment_table
	 * "e.incharge_designation, " + // Fetch incharge_designation from
	 * enrollment_table
	 * "YEAR(a.indatetime) AS year, " +
	 * "MONTHNAME(a.indatetime) AS month, " + // Use MONTHNAME to get month in words
	 * "COUNT(a.inby) AS total_days_present, " +
	 * "COUNT(a.leaveby) AS total_days_absent, " + // Changed to total_days_absent
	 * "COUNT(a.odby) AS total_days_od, " +
	 * "(COUNT(a.inby) + COUNT(a.odby)) AS total_days_salary, " +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS salary_per_day, "
	 * + // Fetch salary_per_day from staff_wages table
	 * "((COUNT(a.inby) + COUNT(a.odby)) * " +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1)) AS total_salary, "
	 * + // Calculate total salary
	 * "sd.salary_status, " +
	 * "sk.group_id, " + // Fetch group_id from suyaudavi_kuzhu table
	 * "sk.group_name " + // Fetch group_name from suyaudavi_kuzhu table
	 * "FROM enrollment_table e " +
	 * "JOIN attendance a ON e.enrollment_id = a.enrollment_id " +
	 * "LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
	 * "AND YEAR(a.indatetime) = sd.year " + // Match year
	 * "AND MONTHNAME(a.indatetime) = sd.month " + // Match month name
	 * "LEFT JOIN suyaudavi_kuzhu sk ON e.group_id = sk.group_id " + // Join with
	 * suyaudavi_kuzhu table on group_id
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * + // Removed unauthorizedleaveby
	 * "AND MONTHNAME(a.indatetime) = ? " + // Filter by month
	 * "AND YEAR(a.indatetime) = ? "+ // Filter by year
	 * "AND (sd.salary_status = 'Initiated' OR sd.salary_status = 'Approved') " + //
	 * Filter by year
	 * (salaryStatus != null && !salaryStatus.isEmpty() ?
	 * "AND sd.salary_status = ? " : "") + // Optional filter by salary status
	 * ZoneWhare
	 * +" GROUP BY e.enrollment_id, e.name, e.incharge_name, e.incharge_designation, YEAR(a.indatetime), MONTHNAME(a.indatetime), salary_per_day, sd.salary_status, sk.group_id, sk.group_name"
	 * ; // Group by new fields
	 * 
	 * // Prepare the query parameters based on provided inputs
	 * List<Object> params = new ArrayList<>();
	 * params.add(month);
	 * params.add(year);
	 * if (salaryStatus != null && !salaryStatus.isEmpty()) {
	 * params.add(salaryStatus);
	 * }
	 * 
	 * // Execute the query with dynamic parameters
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql,
	 * params.toArray());
	 * return results;
	 * }
	 */

	/*
	 * public List<Map<String, Object>> getSalaryReport(String month, int year,
	 * String salaryStatus,String loginId) {
	 * 
	 * // Retrieve zone access for the user
	 * String ZoneWhare;
	 * String zoneAccessSQL =
	 * "SELECT zone FROM gcc_apps.login_mapping_user WHERE appuser_id = ?";
	 * List<Map<String, Object>> getAccess =
	 * jdbcTemplate.queryForList(zoneAccessSQL, loginId);
	 * System.out.println(zoneAccessSQL +"->"+ loginId);
	 * // Extract the zones from getAccess
	 * List<String> zones = getAccess.stream()
	 * .map(access -> (String) access.get("zone"))
	 * .filter(Objects::nonNull)
	 * .collect(Collectors.toList());
	 * 
	 * // If the user has zone acc ess, add it to the WHERE clause
	 * if (!zones.isEmpty()) {
	 * String zoneList = zones.stream()
	 * .map(zone -> "'" + zone + "'")
	 * .collect(Collectors.joining(", "));
	 * ZoneWhare = " AND e.zone IN ("+zoneList+")";
	 * // System.out.println("AND e.zone IN ("+zoneList+")");
	 * }
	 * else {
	 * ZoneWhare = " AND e.zone IN (0)";
	 * }
	 * 
	 * String sql = "SELECT " +
	 * "e.enrollment_id, " +
	 * "e.emp_id, " +
	 * "e.name, " +
	 * "e.incharge_name, " + // Fetch incharge_name from enrollment_table
	 * "e.incharge_designation, " + // Fetch incharge_designation from
	 * enrollment_table
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS iyear, " +
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS imonth, "
	 * + // Use MONTHNAME to get month in words
	 * "COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
	 * "COUNT(DISTINCT DATE(a.leavedatetime)) AS total_days_absent, " + // Changed
	 * to total_days_absent
	 * "COUNT(DISTINCT DATE(a.oddatetime)) AS total_days_od, " +
	 * "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) AS total_days_salary, "
	 * +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS salary_per_day, "
	 * + // Fetch salary_per_day from staff_wages table
	 * "((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) * "
	 * +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1)) AS total_salary, "
	 * + // Calculate total salary
	 * "sd.salary_status, " +
	 * "sk.group_id, " + // Fetch group_id from suyaudavi_kuzhu table
	 * "sk.group_name " + // Fetch group_name from suyaudavi_kuzhu table
	 * "FROM enrollment_table e " +
	 * "JOIN attendance a ON e.enrollment_id = a.enrollment_id " +
	 * "LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
	 * "AND YEAR(a.indatetime) = sd.year " + // Match year
	 * "AND MONTHNAME(a.indatetime) = sd.month " + // Match month name
	 * "LEFT JOIN suyaudavi_kuzhu sk ON e.group_id = sk.group_id " + // Join with
	 * suyaudavi_kuzhu table on group_id
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * + // Removed unauthorizedleaveby
	 * "AND MONTHNAME(a.indatetime) = ? " + // Filter by month
	 * "AND YEAR(a.indatetime) = ? "+ // Filter by year
	 * "AND (sd.salary_status = 'Initiated' OR sd.salary_status = 'Approved') " + //
	 * Filter by year
	 * (salaryStatus != null && !salaryStatus.isEmpty() ?
	 * "AND sd.salary_status = ? " : "") + // Optional filter by salary status
	 * ZoneWhare
	 * +" GROUP BY e.enrollment_id, e.name, e.incharge_name, e.incharge_designation, iyear, imonth, salary_per_day, sd.salary_status, sk.group_id, sk.group_name"
	 * ; // Group by new fields
	 * 
	 * // Prepare the query parameters based on provided inputs
	 * List<Object> params = new ArrayList<>();
	 * params.add(month);
	 * params.add(year);
	 * if (salaryStatus != null && !salaryStatus.isEmpty()) {
	 * params.add(salaryStatus);
	 * }
	 * 
	 * // Execute the query with dynamic parameters
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql,
	 * params.toArray());
	 * return results;
	 * }
	 */
	public List<Map<String, Object>> getSalaryReport(String month, Integer year, String salaryStatus, String groupName,
			String loginId) {

		// Retrieve zone access for the user
		String ZoneWhare = "";
		String zoneAccessSQL = "SELECT zone FROM gcc_apps.login_mapping_user WHERE appuser_id = ?";
		List<Map<String, Object>> getAccess = jdbcTemplate.queryForList(zoneAccessSQL, loginId);
		System.out.println(zoneAccessSQL + "->" + loginId);
		/*
		 * // Extract the zones from getAccess
		 * List<String> zones = getAccess.stream()
		 * .map(access -> (String) access.get("zone"))
		 * .filter(Objects::nonNull)
		 * .collect(Collectors.toList());
		 */
		// Extract the zones from getAccess
		List<String> zones = new ArrayList<String>();

		if (getAccess != null) {
			for (Map<String, Object> access : getAccess) {
				Object zoneObj = access.get("zone");
				if (zoneObj != null) {
					zones.add(zoneObj.toString());
				}
			}
		}
		/*
		 * // If the user has zone access, add it to the WHERE clause
		 * if (!zones.isEmpty()) {
		 * String zoneList = zones.stream()
		 * .map(zone -> "'" + zone + "'")
		 * .collect(Collectors.joining(", "));
		 * ZoneWhare = " AND en.zone IN ("+zoneList+")";
		 * }
		 * else {
		 * ZoneWhare = " AND en.zone IN (0)";
		 * }
		 */
		// If the user has zone access, add it to the WHERE clause
		// String ZoneWhare = "";
		if (zones != null && !zones.isEmpty()) {
			StringBuilder zoneListBuilder = new StringBuilder();
			for (int i = 0; i < zones.size(); i++) {
				zoneListBuilder.append("'").append(zones.get(i)).append("'");
				if (i < zones.size() - 1) {
					zoneListBuilder.append(", ");
				}
			}
			ZoneWhare = " AND en.zone IN (" + zoneListBuilder.toString() + ")";
		} else {
			ZoneWhare = " AND en.zone IN (0)";
		}

		String sql = "select en.emp_id, en.name, en.incharge_name, en.designation, en.incharge_designation, sk.group_name, s.total_days_present, s.total_days_od, "
				+ "s.total_days_absent, s.total_days_salary, s.month as imonth,s.year as iyear, s.salary_status, s.salary_amount as total_salary, "
				+ "DAY(LAST_DAY(STR_TO_DATE(CONCAT(s.year, '-', s.month, '-01'), '%Y-%M-%d'))) AS total_days from salary_details s "
				+ "left join enrollment_table en on s.enrollment_id = en.enrollment_id "
				+ "left join suyaudavi_kuzhu sk on s.group_id = sk.group_id "
				+ "where s.month = ? and s.year =? "
				+ "AND (s.salary_status = 'Initiated' OR s.salary_status = 'Approved') "
				+ (salaryStatus != null && !salaryStatus.isEmpty() ? "AND s.salary_status = ? " : "")
				+ (groupName != null && !groupName.isEmpty() ? "AND sk.group_name = ? " : "")
				+ ZoneWhare;

		// Prepare the query parameters based on provided inputs
		List<Object> params = new ArrayList<>();
		params.add(month);
		params.add(year);
		if (salaryStatus != null && !salaryStatus.isEmpty()) {
			params.add(salaryStatus);
		}
		if (groupName != null && !groupName.isEmpty()) {
			params.add(groupName);
		}

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		return results;
	}
	/*
	 * public List<Map<String, Object>> getSalaryReport(String month, Integer year,
	 * String salaryStatus, String groupName) {
	 * String sql =
	 * "select en.emp_id, en.name, en.incharge_name, en.incharge_designation, sk.group_name, s.total_days_present, s.total_days_od, "
	 * +
	 * "s.total_days_absent, s.total_days_salary, s.month as imonth,s.year as iyear, s.salary_status, s.salary_amount as total_salary, "
	 * +
	 * "DAY(LAST_DAY(STR_TO_DATE(CONCAT(s.year, '-', s.month, '-01'), '%Y-%M-%d'))) AS total_days from salary_details s "
	 * + "left join enrollment_table en on s.enrollment_id = en.enrollment_id "
	 * + "left join suyaudavi_kuzhu sk on s.group_id = sk.group_id "
	 * + "where s.month = ? and s.year =? "
	 * + "AND (s.salary_status = 'Initiated' OR s.salary_status = 'Approved') "
	 * + (salaryStatus != null && !salaryStatus.isEmpty() ?
	 * "AND s.salary_status = ? " : "")
	 * + (groupName != null && !groupName.isEmpty() ? "AND sk.group_name = ? " :
	 * "");
	 * 
	 * // Prepare the query parameters based on provided inputs
	 * List<Object> params = new ArrayList<>();
	 * params.add(month);
	 * params.add(year);
	 * if (salaryStatus != null && !salaryStatus.isEmpty()) {
	 * params.add(salaryStatus);
	 * }
	 * if (groupName != null && !groupName.isEmpty()) {
	 * params.add(groupName);
	 * }
	 * 
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(),
	 * params.toArray());
	 * return results;
	 * }
	 */
	/*
	 * public List<Map<String, Object>> getSalaryReport(String month, int year,
	 * String salaryStatus ,String groupName, String loginId) {
	 * // Retrieve zone access for the user
	 * String ZoneWhere;
	 * String zoneAccessSQL =
	 * "SELECT zone FROM gcc_apps.login_mapping_user WHERE appuser_id = ?";
	 * List<Map<String, Object>> getAccess =
	 * jdbcTemplate.queryForList(zoneAccessSQL, loginId);
	 * System.out.println(zoneAccessSQL +"->"+ loginId);
	 * // Extract the zones from getAccess
	 * List<String> zones = getAccess.stream()
	 * .map(access -> (String) access.get("zone"))
	 * .filter(Objects::nonNull)
	 * .collect(Collectors.toList());
	 * 
	 * // If the user has zone acc ess, add it to the WHERE clause
	 * if (!zones.isEmpty()) {
	 * String zoneList = zones.stream()
	 * .map(zone -> "'" + zone + "'")
	 * .collect(Collectors.joining(", "));
	 * ZoneWhere = " AND e.zone IN ("+zoneList+")";
	 * // System.out.println("AND e.zone IN ("+zoneList+")");
	 * }
	 * else {
	 * ZoneWhere = " AND e.zone IN (0)";
	 * }
	 * 
	 * String sql = "SELECT " +
	 * "e.enrollment_id, " +
	 * "e.emp_id, " +
	 * "e.name, " +
	 * "e.incharge_name, " + // Fetch incharge_name from enrollment_table
	 * "e.incharge_designation, " + // Fetch incharge_designation from
	 * enrollment_table
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS iyear, " +
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS imonth, "
	 * + // Use MONTHNAME to get month in words
	 * "COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
	 * "COUNT(DISTINCT DATE(a.oddatetime)) AS total_days_od, " + // Count of OD days
	 * "MAX(DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)))) AS total_days, "
	 * + // Calculate total days in the month
	 * "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) AS total_days_salary, "
	 * + // Calculate total present and OD days
	 * "(MAX(DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)))) - "
	 * + // Calculate total days absent
	 * "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime)))) AS total_days_absent, "
	 * + // Calculate total days absent
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS salary_per_day, "
	 * + // Fetch salary_per_day from staff_wages table
	 * "((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) * "
	 * +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1)) AS total_salary, "
	 * + // Calculate total salary
	 * "sd.salary_status, " +
	 * "sk.group_id, " + // Fetch group_id from suyaudavi_kuzhu table
	 * "sk.group_name " + // Fetch group_name from suyaudavi_kuzhu table
	 * "FROM enrollment_table e " +
	 * "JOIN attendance a ON e.enrollment_id = a.enrollment_id " +
	 * "LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
	 * "AND YEAR(a.indatetime) = sd.year " + // Match year
	 * "AND MONTHNAME(a.indatetime) = sd.month " + // Match month name
	 * "LEFT JOIN suyaudavi_kuzhu sk ON e.group_id = sk.group_id " + // Join with
	 * suyaudavi_kuzhu table on group_id
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * + // Removed unauthorizedleaveby
	 * "AND MONTHNAME(a.indatetime) = ? " + // Filter by month
	 * "AND YEAR(a.indatetime) = ? " + // Filter by year
	 * "AND (sd.salary_status = 'Initiated' OR sd.salary_status = 'Approved') " + //
	 * Filter by salary status
	 * (salaryStatus != null && !salaryStatus.isEmpty() ?
	 * "AND sd.salary_status = ? " : "") +
	 * (groupName != null && !groupName.isEmpty() ? "AND sk.group_name = ? " :
	 * "")+// Optional filter by salary status
	 * ZoneWhere
	 * +" GROUP BY e.enrollment_id, e.name, e.incharge_name, e.incharge_designation, iyear, imonth, salary_per_day, sd.salary_status, sk.group_id, sk.group_name"
	 * ; // Group by new fields
	 * 
	 * // Prepare the query parameters based on provided inputs
	 * List<Object> params = new ArrayList<>();
	 * params.add(month);
	 * params.add(year);
	 * if (salaryStatus != null && !salaryStatus.isEmpty()) {
	 * params.add(salaryStatus);
	 * }
	 * if (groupName != null && !groupName.isEmpty()) {
	 * params.add(groupName);
	 * }
	 * 
	 * // Execute the query with dynamic parameters
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql,
	 * params.toArray());
	 * return results;
	 * }
	 */

	/*
	 * public List<Map<String, Object>> getAttendanceWithInitiatedSalary(String
	 * inchargeName, String month) {
	 * String sql = "SELECT " +
	 * "e.enrollment_id, " +
	 * "e.emp_id, " +
	 * "e.name, " +
	 * "YEAR(a.indatetime) AS year, " +
	 * "MONTHNAME(a.indatetime) AS month, " + // Get month in words
	 * "COUNT(a.inby) AS total_days_present, " +
	 * "COUNT(a.leaveby) AS total_days_absent, " + // Changed to total_days_absent
	 * "COUNT(a.odby) AS total_days_od, " +
	 * "(COUNT(a.inby) + COUNT(a.odby)) AS total_days_salary, " +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS salary_per_day, "
	 * + // Fetch salary_per_day from staff_wages table
	 * "((COUNT(a.inby) + COUNT(a.odby)) * " +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1)) AS total_salary, "
	 * + // Calculate total salary
	 * "sd.salary_status " +
	 * "FROM enrollment_table e " +
	 * "JOIN attendance a ON e.enrollment_id = a.enrollment_id " + // Join with
	 * attendance table
	 * "LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " + //
	 * Left join with salary_details
	 * "AND YEAR(a.indatetime) = sd.year " + // Match year
	 * "AND MONTHNAME(a.indatetime) = sd.month " + // Match month name
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * + // Removed unauthorizedleaveby
	 * "AND e.incharge_name = ? " + // Filter by incharge_name
	 * "AND MONTHNAME(a.indatetime) = ? " + // Filter by month
	 * "AND sd.salary_status IN ('Initiated', 'Approved') " + // Filter by salary
	 * status 'Initiated' or 'Approved'
	 * "GROUP BY e.enrollment_id, e.name, YEAR(a.indatetime), MONTHNAME(a.indatetime), salary_per_day, sd.salary_status"
	 * ; // Group by required fields
	 * 
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql,
	 * inchargeName, month);
	 * return results;
	 * }
	 */
	/*
	 * public List<Map<String, Object>> getAttendanceWithInitiatedSalary(String
	 * inchargeName, String month) {
	 * String sql = "SELECT " +
	 * "e.enrollment_id, " +
	 * "e.emp_id, " +
	 * "e.name, " +
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS year, " +
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS month, "
	 * +
	 * "COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
	 * "COUNT(DISTINCT DATE(a.leavedatetime)) AS total_days_absent, " +
	 * "COUNT(DISTINCT DATE(a.oddatetime)) AS total_days_od, " +
	 * "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) AS total_days_salary, "
	 * +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS salary_per_day, "
	 * +
	 * "((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) * "
	 * +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1)) AS total_salary, "
	 * +
	 * "sd.salary_status " +
	 * "FROM enrollment_table e " +
	 * "JOIN attendance a ON e.enrollment_id = a.enrollment_id " +
	 * "LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * +
	 * "AND e.incharge_name = ? " +
	 * "AND sd.salary_status IN ('Initiated', 'Approved') " +
	 * "AND (MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) = ? OR ? IS NULL) "
	 * + // Ensure month matches
	 * "GROUP BY e.enrollment_id, e.emp_id, e.name, " +
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " + // Grouping
	 * by COALESCE year
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), sd.salary_status"
	 * ; // Grouping by COALESCE month
	 * 
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql,
	 * inchargeName, month, month);
	 * return results;
	 * }
	 */
	/*
	 * public List<Map<String, Object>> getAttendanceWithInitiatedSalary(String
	 * inchargeId, String month) {
	 * String sql = "SELECT " +
	 * "e.enrollment_id, " +
	 * "e.emp_id, " +
	 * "e.name, " +
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS year, " +
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS month, "
	 * +
	 * "COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
	 * "COUNT(DISTINCT DATE(a.oddatetime)) AS total_days_od, " +
	 * "MAX(DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)))) AS total_days, "
	 * + // Total days in the month
	 * "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) AS total_days_salary, "
	 * +
	 * "(MAX(DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)))) - "
	 * + // Total days in the month
	 * "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime)))) AS total_days_absent, "
	 * + // Calculate total days absent
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS salary_per_day, "
	 * +
	 * "((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) * "
	 * +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1)) AS total_salary, "
	 * +
	 * "sd.salary_status " +
	 * "FROM enrollment_table e " +
	 * "JOIN attendance a ON e.enrollment_id = a.enrollment_id " +
	 * "LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * +
	 * "AND e.incharge_id = ? " +
	 * "AND sd.salary_status IN ('Initiated', 'Approved') " +
	 * "AND sd.month = ? " + // Filtering based on salary_details month
	 * "AND MONTHNAME(a.indatetime) = ? " + // Additional filtering on attendance
	 * "GROUP BY e.enrollment_id, e.emp_id, e.name, " +
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), sd.salary_status"
	 * ;
	 * 
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql,
	 * inchargeId, month, month);
	 * return results;
	 * }
	 */
	/*
	 * public List<Map<String, Object>> getAttendanceWithInitiatedSalary(String
	 * inchargeId, String month, String inchargeName, Integer zone) {
	 * StringBuilder sql = new StringBuilder("SELECT " +
	 * "e.group_id, " +
	 * "(SELECT group_name FROM suyaudavi_kuzhu WHERE suyaudavi_kuzhu.group_id = e.group_id LIMIT 1) AS group_name, "
	 * +
	 * "a.enrollment_id, " +
	 * "e.emp_id, " +
	 * "e.name, " +
	 * "e.incharge_id, " +
	 * "(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1) AS salary_per_day, "
	 * +
	 * "(SELECT wage_id FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1) AS wage_id, "
	 * +
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS year, " +
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS month, "
	 * +
	 * "DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))) AS total_days, "
	 * +
	 * "COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
	 * "COUNT(DISTINCT DATE(a.oddatetime)) AS total_days_od, " +
	 * "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) AS total_days_salary, "
	 * +
	 * "((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) * "
	 * +
	 * "(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1)) AS total_salary, "
	 * +
	 * "(MAX(DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)))) - "
	 * +
	 * "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime)))) AS total_days_absent, "
	 * +
	 * "IFNULL(s.salary_status, '') AS salary_status " +
	 * "FROM attendance AS a " +
	 * "INNER JOIN enrollment_table AS e ON a.enrollment_id = e.enrollment_id " +
	 * "LEFT JOIN salary_details AS s ON a.enrollment_id = s.enrollment_id " +
	 * "AND s.month = MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) "
	 * +
	 * "AND s.year = YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) " +
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * );
	 * 
	 * // Append condition for incharge_id
	 * sql.append("AND (? IS NULL OR e.incharge_id = ?) ");
	 * 
	 * // Append condition for incharge_name
	 * sql.append("AND (? IS NULL OR e.incharge_name = ?) ");
	 * 
	 * // Append condition for zone
	 * sql.append("AND (? IS NULL OR e.zone = ?) ");
	 * 
	 * // Append condition for month if provided
	 * if (month != null && !month.isEmpty()) {
	 * sql.
	 * append("AND MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) = ? "
	 * );
	 * }
	 * 
	 * // Add condition to filter by salary_status (only 'Initiated' or 'Approved')
	 * sql.append("AND IFNULL(s.salary_status, '') IN ('Initiated', 'Approved') ");
	 * 
	 * // Group by necessary columns
	 * sql.append("GROUP BY a.enrollment_id, e.group_id, " +
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
	 * "(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1), "
	 * +
	 * "(SELECT wage_id FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1), "
	 * +
	 * "(SELECT group_name FROM suyaudavi_kuzhu WHERE suyaudavi_kuzhu.group_id = e.group_id LIMIT 1), "
	 * +
	 * "DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))), " +
	 * "s.salary_status");
	 * 
	 * // Create a list of parameters
	 * List<Object> params = new ArrayList<>();
	 * params.add(inchargeId);
	 * params.add(inchargeId);
	 * params.add(inchargeName);
	 * params.add(inchargeName);
	 * params.add(zone);
	 * params.add(zone); // Added twice for the conditional check in the SQL
	 * if (month != null && !month.isEmpty()) {
	 * params.add(month);
	 * }
	 * 
	 * // Log the SQL and parameters (optional for debugging)
	 * System.out.println("Generated SQL: " + sql.toString());
	 * System.out.println("Parameters: " + params);
	 * 
	 * // Execute the query using JdbcTemplate
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(),
	 * params.toArray());
	 * return results;
	 * }
	 * 
	 */
	/*
	 * public List<Map<String, Object>> getAttendanceWithInitiatedSalary(String
	 * inchargeId, String month, String inchargeName, int zone) {
	 * StringBuilder sql = new StringBuilder("SELECT " +
	 * "e.group_id, " +
	 * "(SELECT group_name FROM suyaudavi_kuzhu WHERE suyaudavi_kuzhu.group_id = e.group_id LIMIT 1) AS group_name, "
	 * +
	 * "a.enrollment_id, " +
	 * "e.emp_id, " +
	 * "e.name, " +
	 * "e.incharge_id, " +
	 * "(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1) AS salary_per_day, "
	 * +
	 * "(SELECT wage_id FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1) AS wage_id, "
	 * +
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS year, " +
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS month, "
	 * +
	 * "DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))) AS total_days, "
	 * +
	 * "COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
	 * "COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END) AS total_days_od, "
	 * +
	 * "COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END) AS total_days_salary, "
	 * +
	 * "((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END)) * "
	 * +
	 * "(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1)) AS total_salary, "
	 * +
	 * "(MAX(DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)))) - "
	 * +
	 * "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END))) AS total_days_absent, "
	 * +
	 * "IFNULL(s.salary_status, '') AS salary_status " +
	 * "FROM attendance AS a " +
	 * "INNER JOIN enrollment_table AS e ON a.enrollment_id = e.enrollment_id " +
	 * "LEFT JOIN salary_details AS s ON a.enrollment_id = s.enrollment_id " +
	 * "AND s.month = MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) "
	 * +
	 * "AND s.year = YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) " +
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * );
	 * 
	 * // Append condition for incharge_id
	 * sql.append("AND (? IS NULL OR e.incharge_id = ?) ");
	 * 
	 * // Append condition for incharge_name
	 * sql.append("AND (? IS NULL OR e.incharge_name = ?) ");
	 * 
	 * // Append condition for month if provided
	 * if (month != null && !month.isEmpty()) {
	 * sql.
	 * append("AND MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) = ? "
	 * );
	 * }
	 * 
	 * // Add condition to filter by salary_status (only 'Initiated' or 'Approved')
	 * sql.append("AND IFNULL(s.salary_status, '') IN ('Initiated', 'Approved') ");
	 * 
	 * // Group by necessary columns
	 * sql.append("GROUP BY a.enrollment_id, e.group_id, " +
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
	 * "(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1), "
	 * +
	 * "(SELECT wage_id FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1), "
	 * +
	 * "(SELECT group_name FROM suyaudavi_kuzhu WHERE suyaudavi_kuzhu.group_id = e.group_id LIMIT 1), "
	 * +
	 * "DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))), " +
	 * "s.salary_status");
	 * 
	 * // Create a list of parameters
	 * List<Object> params = new ArrayList<>();
	 * params.add(inchargeId);
	 * params.add(inchargeId); // Added twice for the conditional check in the SQL
	 * params.add(inchargeName);
	 * params.add(inchargeName); // Added twice for the conditional check in the SQL
	 * if (month != null && !month.isEmpty()) {
	 * params.add(month);
	 * }
	 * 
	 * // Log the SQL and parameters (optional for debugging)
	 * System.out.println("Generated SQL: " + sql.toString());
	 * System.out.println("Parameters: " + params);
	 * 
	 * // Execute the query using JdbcTemplate
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(),
	 * params.toArray());
	 * return results;
	 * }
	 */

	public List<Map<String, Object>> getAttendanceWithInitiatedSalary(
			int inchargeId, String month, String year, String inchargeName, List<Integer> enrollmentIds) {

		String sql = "SELECT " +
				"e.group_id, " +
				"(SELECT group_name FROM suyaudavi_kuzhu WHERE suyaudavi_kuzhu.group_id = e.group_id LIMIT 1) AS group_name, "
				+
				"a.enrollment_id, e.emp_id, e.name, e.incharge_id, " +
				"(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1) AS salary_per_day, "
				+
				"(SELECT wage_id FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1) AS wage_id, " +
				"YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS year, " +
				"MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS month, " +
				"DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))) AS total_days, " +
				"COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
				"COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END) AS total_days_od, " +
				"COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END) AS total_days_salary, "
				+
				"((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END)) * "
				+
				"(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1)) AS total_salary, "
				+
				"(MAX(DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)))) - " +
				"(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT CASE WHEN a.indatetime IS NULL THEN DATE(a.oddatetime) END))) AS total_days_absent, "
				+
				"IFNULL(s.salary_status, '') AS salary_status " +
				"FROM attendance AS a " +
				"INNER JOIN enrollment_table AS e ON a.enrollment_id = e.enrollment_id " +
				"LEFT JOIN salary_details AS s ON a.enrollment_id = s.enrollment_id " +
				"AND s.month = MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) " +
				"AND s.year = YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) " +
				"WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) " +
				"AND (? IS NULL OR e.incharge_id = ?) " +
				"AND (? IS NULL OR e.incharge_name = ?) ";

		if (month != null && !month.isEmpty()) {
			sql += "AND MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) = ? ";
		}
		if (year != null && !year.isEmpty()) {
			sql += "AND YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) = ? ";
		}

		// Assuming enrollmentIds is a List<String> or List<Integer>
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < enrollmentIds.size(); i++) {
			placeholders.append("?");
			if (i < enrollmentIds.size() - 1) {
				placeholders.append(", ");
			}
		}
		String inClause = placeholders.toString() + ") ";

		sql += "AND IFNULL(s.salary_status, '') IN ('Initiated', 'Approved') " +
				"AND e.enrollment_id IN (" +
				inClause +
				"GROUP BY a.enrollment_id, e.group_id, " +
				"YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
				"MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
				"(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1), " +
				"(SELECT wage_id FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1), " +
				"(SELECT group_name FROM suyaudavi_kuzhu WHERE suyaudavi_kuzhu.group_id = e.group_id LIMIT 1), " +
				"DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))), " +
				"s.salary_status";

		List<Object> params = new ArrayList<>();
		params.add(inchargeId);
		params.add(inchargeId);
		params.add(inchargeName);
		params.add(inchargeName);

		if (month != null && !month.isEmpty()) {
			params.add(month);
		}
		if (year != null && !year.isEmpty()) {
			params.add(year);
		}

		params.addAll(enrollmentIds);

		System.out.println("SQL = " + sql);
		System.out.println("Parameter = " + params);

		return jdbcTemplate.queryForList(sql, params.toArray());
	}

	@Transactional
	public void updateSalaryStatusToApproved(List<Integer> enrollmentIds, String month, int year) {
		String sql = "UPDATE salary_details SET salary_status = 'Approved' WHERE enrollment_id = ? AND month = ? AND year = ?";

		for (Integer enrollmentId : enrollmentIds) {
			// Pass the individual enrollmentId instead of the entire list
			jdbcTemplate.update(sql, enrollmentId, month, year);
		}
	}
	/*
	 * public List<Map<String, Object>> getFilteredSalaryDetails(String month,
	 * Integer year, String salaryStatus) {
	 * StringBuilder sql = new StringBuilder("SELECT " +
	 * "e.incharge_name, " +
	 * "GROUP_CONCAT(e.enrollment_id) AS enrollment_ids, " +
	 * "COUNT(e.enrollment_id) AS total_staff_count, " +
	 * "sd.month, " +
	 * "sd.year, " +
	 * "sd.salary_status, " +
	 * "COUNT(CASE WHEN sd.salary_status = 'Initiated' THEN 1 END) AS total_initiated_count, "
	 * +
	 * "COUNT(CASE WHEN sd.salary_status = 'Approved' THEN 1 END) AS total_approved_count, "
	 * +
	 * "SUM(CASE WHEN sd.salary_status IN ('Initiated', 'Approved') THEN sd.salary_amount ELSE 0 END) AS total_salary_amount "
	 * +
	 * "FROM enrollment_table e " +
	 * "JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
	 * "WHERE 1=1 "); // Placeholder for dynamic conditions
	 * 
	 * if (month != null && !month.isEmpty()) {
	 * sql.append(" AND sd.month = '").append(month).append("'");
	 * }
	 * if (year != null) {
	 * sql.append(" AND sd.year = ").append(year);
	 * }
	 * if (salaryStatus != null && !salaryStatus.isEmpty()) {
	 * sql.append(" AND sd.salary_status = '").append(salaryStatus).append("'");
	 * }
	 * 
	 * sql.append(" GROUP BY e.incharge_name, sd.month, sd.year, sd.salary_status "
	 * );
	 * sql.append(" ORDER BY sd.year DESC, sd.month DESC");
	 * 
	 * try {
	 * List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
	 * for (Map<String, Object> row : result) {
	 * String enrollmentIdsString = (String) row.get("enrollment_ids");
	 * if (enrollmentIdsString != null) {
	 * List<Integer> enrollmentIds = Arrays.stream(enrollmentIdsString.split(","))
	 * .map(String::trim)
	 * .filter(id -> !id.isEmpty())
	 * .map(Integer::parseInt)
	 * .collect(Collectors.toList());
	 * row.put("enrollment_ids", enrollmentIds);
	 * }
	 * }
	 * return result;
	 * } catch (Exception e) {
	 * e.printStackTrace();
	 * return Collections.emptyList();
	 * }
	 * }
	 */
	/*
	 * public List<Map<String, Object>> getFilteredSalaryDetails(String month,
	 * Integer year, String salaryStatus, String department, String loginId) {
	 * StringBuilder sql = new StringBuilder("SELECT " +
	 * "e.incharge_name, " +
	 * "GROUP_CONCAT(e.enrollment_id) AS enrollment_ids, " +
	 * "COUNT(e.enrollment_id) AS total_staff_count, " +
	 * "sd.month, " +
	 * "sd.year, " +
	 * "sd.salary_status, " +
	 * "COUNT(CASE WHEN sd.salary_status = 'Initiated' THEN 1 END) AS total_initiated_count, "
	 * +
	 * "COUNT(CASE WHEN sd.salary_status = 'Approved' THEN 1 END) AS total_approved_count, "
	 * +
	 * "SUM(CASE WHEN sd.salary_status IN ('Initiated', 'Approved') THEN sd.salary_amount ELSE 0 END) AS total_salary_amount, "
	 * +
	 * "e.department " + // Select the department as well
	 * "FROM enrollment_table e " +
	 * "JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
	 * "WHERE 1=1 "); // Placeholder for dynamic conditions
	 * 
	 * // Add filters for month, year, salary status, and department
	 * if (month != null && !month.isEmpty()) {
	 * sql.append(" AND sd.month = '").append(month).append("'");
	 * }
	 * if (year != null) {
	 * sql.append(" AND sd.year = ").append(year);
	 * }
	 * if (salaryStatus != null && !salaryStatus.isEmpty()) {
	 * sql.append(" AND sd.salary_status = '").append(salaryStatus).append("'");
	 * }
	 * if (department != null && !department.isEmpty()) {
	 * sql.append(" AND e.department = '").append(department).append("'");
	 * }
	 * 
	 * // Retrieve zone access for the user
	 * String zoneAccessSQL =
	 * "SELECT zone FROM gcc_apps.login_mapping_user WHERE appuser_id = ?";
	 * List<Map<String, Object>> getAccess =
	 * jdbcTemplate.queryForList(zoneAccessSQL, loginId);
	 * System.out.println(zoneAccessSQL +"->"+ loginId);
	 * // Extract the zones from getAccess
	 * List<String> zones = getAccess.stream()
	 * .map(access -> (String) access.get("zone"))
	 * .filter(Objects::nonNull)
	 * .collect(Collectors.toList());
	 * 
	 * // If the user has zone acc ess, add it to the WHERE clause
	 * if (!zones.isEmpty()) {
	 * String zoneList = zones.stream()
	 * .map(zone -> "'" + zone + "'")
	 * .collect(Collectors.joining(", "));
	 * sql.append(" AND e.zone IN (").append(zoneList).append(")");
	 * // System.out.println("AND e.zone IN ("+zoneList+")");
	 * }
	 * else {
	 * sql.append(" AND e.zone IN (").append("0").append(")");
	 * }
	 * 
	 * sql.
	 * append(" GROUP BY e.incharge_name, sd.month, sd.year, sd.salary_status, e.department "
	 * );
	 * sql.append(" ORDER BY sd.year DESC, sd.month DESC");
	 * 
	 * System.out.println(sql);
	 * 
	 * try {
	 * List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
	 * for (Map<String, Object> row : result) {
	 * String enrollmentIdsString = (String) row.get("enrollment_ids");
	 * if (enrollmentIdsString != null) {
	 * List<Integer> enrollmentIds = Arrays.stream(enrollmentIdsString.split(","))
	 * .map(String::trim)
	 * .filter(id -> !id.isEmpty())
	 * .map(Integer::parseInt)
	 * .collect(Collectors.toList());
	 * row.put("enrollment_ids", enrollmentIds);
	 * }
	 * }
	 * return result;
	 * } catch (Exception e) {
	 * e.printStackTrace();
	 * return Collections.emptyList();
	 * }
	 * }
	 */

	public List<Map<String, Object>> getFilteredSalaryDetails(String month, Integer year, String salaryStatus,
			String loginId) {
		StringBuilder sql = new StringBuilder("SELECT " +
				"e.incharge_name, " +
				"e.incharge_id, " +
				"e.zone, " +
				"GROUP_CONCAT(e.enrollment_id) AS enrollment_ids, " +
				"COUNT(e.enrollment_id) AS total_staff_count, " +
				"sd.month, " +
				"sd.year, " +
				"sd.salary_status, " +
				"COUNT(CASE WHEN sd.salary_status = 'Initiated' THEN 1 END) AS total_initiated_count, " +
				"COUNT(CASE WHEN sd.salary_status = 'Approved' THEN 1 END) AS total_approved_count, " +
				"SUM(CASE WHEN sd.salary_status IN ('Initiated', 'Approved') THEN sd.salary_amount ELSE 0 END) AS total_salary_amount, "
				+
				"e.department " + // Select the department as well
				"FROM enrollment_table e " +
				"JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
				"WHERE 1=1 "); // Placeholder for dynamic conditions

		// Add filters for month, year, salary status, and department
		if (month != null && !month.isEmpty()) {
			sql.append(" AND sd.month = '").append(month).append("'");
		}
		if (year != null) {
			sql.append(" AND sd.year = ").append(year);
		}
		if (salaryStatus != null && !salaryStatus.isEmpty()) {
			sql.append(" AND sd.salary_status = '").append(salaryStatus).append("'");
		}

		// Retrieve zone access for the user
		String zoneAccessSQL = "SELECT zone FROM gcc_apps.login_mapping_user WHERE appuser_id = ?";
		List<Map<String, Object>> getAccess = jdbcTemplate.queryForList(zoneAccessSQL, loginId);
		System.out.println(zoneAccessSQL + "->" + loginId);
		// Extract the zones from getAccess
		/*
		 * List<String> zones = getAccess.stream()
		 * .map(access -> (String) access.get("zone"))
		 * .filter(Objects::nonNull)
		 * .collect(Collectors.toList());
		 * 
		 * // If the user has zone acc ess, add it to the WHERE clause
		 * if (!zones.isEmpty()) {
		 * String zoneList = zones.stream()
		 * .map(zone -> "'" + zone + "'")
		 * .collect(Collectors.joining(", "));
		 * sql.append(" AND e.zone IN (").append(zoneList).append(")");
		 * // System.out.println("AND e.zone IN ("+zoneList+")");
		 * }
		 * else {
		 * sql.append(" AND e.zone IN (").append("0").append(")");
		 * }
		 */
		// Extract zones from getAccess
		List<String> zones = new ArrayList<String>();
		for (Map<String, Object> access : getAccess) {
			Object zoneObj = access.get("zone");
			if (zoneObj != null) {
				zones.add(zoneObj.toString());
			}
		}

		// If the user has zone access, add it to the WHERE clause
		if (!zones.isEmpty()) {
			StringBuilder zoneList = new StringBuilder();
			for (int i = 0; i < zones.size(); i++) {
				zoneList.append("'").append(zones.get(i)).append("'");
				if (i < zones.size() - 1) {
					zoneList.append(", ");
				}
			}
			sql.append(" AND e.zone IN (").append(zoneList.toString()).append(")");
		} else {
			sql.append(" AND e.zone IN (0)");
		}
		sql.append(
				" GROUP BY e.incharge_name, e.incharge_id, e.zone, sd.month, sd.year, sd.salary_status, e.department ");
		sql.append(" ORDER BY sd.year DESC, sd.month DESC");
		/*
		 * try {
		 * List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
		 * for (Map<String, Object> row : result) {
		 * String enrollmentIdsString = (String) row.get("enrollment_ids");
		 * if (enrollmentIdsString != null) {
		 * List<Integer> enrollmentIds = Arrays.stream(enrollmentIdsString.split(","))
		 * .map(String::trim)
		 * .filter(id -> !id.isEmpty())
		 * .map(Integer::parseInt)
		 * .collect(Collectors.toList());
		 * row.put("enrollment_ids", enrollmentIds);
		 * }
		 * }
		 * return result;
		 * } catch (Exception e) {
		 * e.printStackTrace();
		 * return Collections.emptyList();
		 * }
		 */

		try {
			List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());

			for (Map<String, Object> row : result) {
				String enrollmentIdsString = (String) row.get("enrollment_ids");
				if (enrollmentIdsString != null) {
					String[] parts = enrollmentIdsString.split(",");
					List<Integer> enrollmentIds = new ArrayList<Integer>();
					for (int i = 0; i < parts.length; i++) {
						String part = parts[i].trim();
						if (!part.isEmpty()) {
							try {
								enrollmentIds.add(Integer.parseInt(part));
							} catch (NumberFormatException nfe) {
								// Ignore or log invalid integers
								nfe.printStackTrace();
							}
						}
					}
					row.put("enrollment_ids", enrollmentIds);
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}

	}

	public Integer checkInchargeID(Integer inchargeId) {

		String sql = "SELECT EXISTS( " +
				"SELECT 1 FROM additional_incharge " +
				"WHERE incharge_id = ? AND is_active = 1 AND is_delete = 0)";

		Integer result = jdbcTemplate.queryForObject(sql, Integer.class, inchargeId);

		return result;
	}

	public List<Map<String, Object>> getAttendanceWithSalaryByInchargeId(String month, Integer year,
			Integer incharge_id) {
		StringBuilder sql = new StringBuilder("SELECT " +
				"e.group_id, " +
				"(SELECT group_name FROM suyaudavi_kuzhu WHERE suyaudavi_kuzhu.group_id = e.group_id LIMIT 1) AS group_name, "
				+
				"a.enrollment_id, " +
				"e.emp_id, " +
				"e.name, " +
				"e.incharge_id, " +
				"e.incharge_name, " +
				"(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1) AS salary_per_day, "
				+
				"(SELECT wage_id FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1) AS wage_id, " +
				"YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS year, " +
				"MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS month, " +
				"DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))) AS total_days, " +
				"COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
				// Subquery to get total_days_od
				"(SELECT COUNT(DISTINCT DATE(a2.oddatetime)) " +
				" FROM attendance AS a2 " +
				" WHERE MONTHNAME(a2.oddatetime) = ? " + // Use placeholder for month
				" AND YEAR(a2.oddatetime) = ? " + // Use placeholder for year
				" AND a2.enrollment_id = a.enrollment_id " +
				" AND DATE(a2.oddatetime) NOT IN ( " +
				"   SELECT DISTINCT DATE(a3.indatetime) " +
				"   FROM attendance AS a3 " +
				"   WHERE MONTHNAME(a3.indatetime) = ? " + // Use placeholder for month
				"   AND YEAR(a3.indatetime) = ? " + // Use placeholder for year
				"   AND a3.enrollment_id = a.enrollment_id " +
				")) AS total_days_od, " +
				"IFNULL(s.salary_status, '') AS salary_status " +
				"FROM attendance AS a " +
				"INNER JOIN enrollment_table AS e ON a.enrollment_id = e.enrollment_id " +
				"LEFT JOIN salary_details AS s ON a.enrollment_id = s.enrollment_id " +
				"AND s.month = MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) " +
				"AND s.year = YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) " +
				"WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) ");

		// Append condition for incharge_id
		sql.append("AND (? IS NULL OR e.incharge_id = ?) ");

		// Append condition for month if provided
		if (month != null && !month.isEmpty()) {
			sql.append("AND MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) = ? ");
		}

		// Append condition for year if provided
		if (year != null) {
			sql.append("AND YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) = ? ");
		}

		// Group by necessary columns
		sql.append("GROUP BY a.enrollment_id, e.group_id, " +
				"YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
				"MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
				"(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1), " +
				"(SELECT wage_id FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC LIMIT 1), " +
				"(SELECT group_name FROM suyaudavi_kuzhu WHERE suyaudavi_kuzhu.group_id = e.group_id LIMIT 1), " +
				"DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))), " +
				"s.salary_status");

		// Create a list of parameters
		List<Object> params = new ArrayList<>();
		params.add(month); // Add month for total_days_od
		params.add(year); // Add year for total_days_od
		params.add(month); // Add month for the inner subquery
		params.add(year); // Add year for the inner subquery
		params.add(incharge_id);
		params.add(incharge_id); // Added twice for the conditional check in the SQL
		if (month != null && !month.isEmpty()) {
			params.add(month);
		}
		if (year != null) {
			params.add(year);
		}

		// Log the SQL and parameters (optional for debugging)
		System.out.println("Generated SQL: " + sql.toString());
		System.out.println("Parameters: " + params);

		// Execute the query using JdbcTemplate
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		return results;
	}

	public List<Map<String, Object>> getZoneWiseReport(String month, Integer year, String salaryStatus,
			String groupName, String zone, String division) {
		String sql = "select en.zone,en.division, en.emp_id, en.name, en.incharge_name, en.incharge_designation, sk.group_name, s.total_days_present, s.total_days_od, "
				+ "s.total_days_absent, s.total_days_salary, s.month as imonth,s.year as iyear, s.salary_status, s.salary_amount as total_salary, "
				+ "DAY(LAST_DAY(STR_TO_DATE(CONCAT(s.year, '-', s.month, '-01'), '%Y-%M-%d'))) AS total_days from salary_details s "
				+ "left join enrollment_table en on s.enrollment_id = en.enrollment_id "
				+ "left join suyaudavi_kuzhu sk on s.group_id = sk.group_id "
				+ "where s.month = ? and s.year =? "
				+ "AND (s.salary_status = 'Initiated' OR s.salary_status = 'Approved') "
				+ (salaryStatus != null && !salaryStatus.isEmpty() ? "AND s.salary_status = ? " : "")
				+ (groupName != null && !groupName.isEmpty() ? "AND sk.group_name = ? " : "")
				+ (zone != null && !zone.isEmpty() ? "AND en.zone = ? " : "")
				+ (division != null && !division.isEmpty() ? "AND en.division = ? " : "");

		// Prepare the query parameters based on provided inputs
		List<Object> params = new ArrayList<>();
		params.add(month);
		params.add(year);
		if (salaryStatus != null && !salaryStatus.isEmpty()) {
			params.add(salaryStatus);
		}
		if (groupName != null && !groupName.isEmpty()) {
			params.add(groupName);
		}
		if (zone != null && !zone.isEmpty()) {
			params.add(zone);
		}
		if (division != null && !division.isEmpty()) {
			params.add(division);
		}

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		return results;
	}

	public List<Map<String, Object>> getAttendanceReport(String fromdate, String todate, String groupName, String zone,
			String division) {
		String sql = "select date_format(coalesce(att.indatetime, att.oddatetime, att.leavedatetime),'%d-%m-%Y') as attendance_date, et.emp_id, et.name, "
				+ " CASE WHEN att.indatetime IS NOT NULL THEN 'Present' WHEN att.leavedatetime IS NOT NULL THEN 'Absent' "
				+ " WHEN att.oddatetime IS NOT NULL THEN 'On Duty' ELSE 'No Record' END AS attendance_status, "
				+ " et.incharge_name,et.incharge_phoneno, et.incharge_designation, et.zone, et.division, et.department, sk.group_name from attendance att "
				+ " left join enrollment_table et on att.enrollment_id = et.enrollment_id "
				+ " left join suyaudavi_kuzhu sk on et.group_id = sk.group_id where "
				+ " (DATE(att.indatetime) BETWEEN STR_TO_DATE('" + fromdate + "', '%Y-%m-%d') AND STR_TO_DATE('"
				+ todate + "', '%Y-%m-%d') "
				+ " OR DATE(att.oddatetime) BETWEEN STR_TO_DATE('" + fromdate + "', '%Y-%m-%d') AND STR_TO_DATE('"
				+ todate + "', '%Y-%m-%d')"
				+ " OR DATE(att.leavedatetime) BETWEEN STR_TO_DATE('" + fromdate + "', '%Y-%m-%d') AND STR_TO_DATE('"
				+ todate + "', '%Y-%m-%d')) "
				+ (zone != null && !zone.isEmpty() ? " and et.zone = ? " : "")
				+ (division != null && !division.isEmpty() ? " and et.division = ? " : "")
				+ (groupName != null && !groupName.isEmpty() ? " and sk.group_name = ? " : "");

		// Prepare the query parameters based on provided inputs
		List<Object> params = new ArrayList<>();

		if (groupName != null && !groupName.isEmpty()) {
			params.add(groupName);
		}
		if (zone != null && !zone.isEmpty()) {
			params.add(zone);
		}
		if (division != null && !division.isEmpty()) {
			params.add(division);
		}

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		return results;
	}

	/**************************************
	 * Updated code working 08-10-2024
	 ******************************************/
	// public List<Map<String, Object>> getAttendanceWithSalaryByInchargeId(String
	// month, Integer year, Integer incharge_id) {
	// StringBuilder sql = new StringBuilder("SELECT " +
	// "e.group_id, " +
	// "(SELECT group_name FROM suyaudavi_kuzhu WHERE suyaudavi_kuzhu.group_id =
	// e.group_id LIMIT 1) AS group_name, " +
	// "a.enrollment_id, " +
	// "e.emp_id, " +
	// "e.name, " +
	// "e.incharge_id, " +
	// "e.incharge_name, " +
	// "(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id
	// DESC LIMIT 1) AS salary_per_day, " +
	// "(SELECT wage_id FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC
	// LIMIT 1) AS wage_id, " +
	// "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS year, " +
	// "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS month, "
	// +
	// "DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))) AS
	// total_days, " +
	// "COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
	// "COUNT(DISTINCT DATE(a.oddatetime)) AS total_days_od, " +
	// "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) AS
	// total_days_salary, " +
	// "((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) *
	// " +
	// "(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id
	// DESC LIMIT 1)) AS total_salary, " +
	// "(MAX(DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)))) -
	// " +
	// "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))))
	// AS total_days_absent, " +
	// "IFNULL(s.salary_status, '') AS salary_status " +
	// "FROM attendance AS a " +
	// "INNER JOIN enrollment_table AS e ON a.enrollment_id = e.enrollment_id " +
	// "LEFT JOIN salary_details AS s ON a.enrollment_id = s.enrollment_id " +
	// "AND s.month = MONTHNAME(COALESCE(a.indatetime, a.leavedatetime,
	// a.oddatetime)) " +
	// "AND s.year = YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) " +
	// "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL)
	// ");
	//
	// // Append condition for incharge_id
	// sql.append("AND (? IS NULL OR e.incharge_id = ?) ");
	//
	// // Append condition for month if provided
	// if (month != null && !month.isEmpty()) {
	// sql.append("AND MONTHNAME(COALESCE(a.indatetime, a.leavedatetime,
	// a.oddatetime)) = ? ");
	// }
	//
	// // Append condition for year if provided
	// if (year != null) {
	// sql.append("AND YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) =
	// ? ");
	// }
	//
	// // Group by necessary columns
	// sql.append("GROUP BY a.enrollment_id, e.group_id, " +
	// "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
	// "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
	// "(SELECT salary_per_day FROM staff_wages WHERE isactive = 1 ORDER BY wage_id
	// DESC LIMIT 1), " +
	// "(SELECT wage_id FROM staff_wages WHERE isactive = 1 ORDER BY wage_id DESC
	// LIMIT 1), " +
	// "(SELECT group_name FROM suyaudavi_kuzhu WHERE suyaudavi_kuzhu.group_id =
	// e.group_id LIMIT 1), " +
	// "DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))), " +
	// "s.salary_status");
	//
	// // Create a list of parameters
	// List<Object> params = new ArrayList<>();
	// params.add(incharge_id);
	// params.add(incharge_id); // Added twice for the conditional check in the SQL
	// if (month != null && !month.isEmpty()) {
	// params.add(month);
	// }
	// if (year != null) {
	// params.add(year);
	// }
	//
	// // Log the SQL and parameters (optional for debugging)
	// System.out.println("Generated SQL: " + sql.toString());
	// System.out.println("Parameters: " + params);
	//
	// // Execute the query using JdbcTemplate
	// List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(),
	// params.toArray());
	// return results;
	// }

	// public List<Map<String, Object>> getAttendanceWithSalaryByInchargeId(String
	// month, Integer year, Integer incharge_id) {
	// StringBuilder sql = new StringBuilder("SELECT " +
	// "e.enrollment_id, " +
	// "e.emp_id, " +
	// "e.name, " +
	// "e.incharge_id, " +
	// "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS year, " +
	// "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS month, "
	// +
	// "COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
	// "COUNT(DISTINCT DATE(a.oddatetime)) AS total_days_od, " +
	// "MAX(DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)))) AS
	// total_days, " +
	// "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) AS
	// total_days_salary, " +
	// "(MAX(DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)))) -
	// " +
	// "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))))
	// AS total_days_absent, " +
	// "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY
	// sw.effect_from_date DESC LIMIT 1) AS salary_per_day, " +
	// "((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) *
	// " +
	// "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY
	// sw.effect_from_date DESC LIMIT 1)) AS total_salary, " +
	// "IFNULL(sd.salary_status, '') AS salary_status, " + // Modified salary_status
	// to return an empty string
	// "sk.group_id, " +
	// "sk.group_name " +
	// "FROM enrollment_table e " +
	// "JOIN attendance a ON e.enrollment_id = a.enrollment_id " +
	// "LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
	// "LEFT JOIN suyaudavi_kuzhu sk ON e.group_id = sk.group_id " +
	// "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL)
	// ");
	//
	// // Check if incharge_id is present
	// sql.append("AND (? IS NULL OR e.incharge_id = ?) ");
	//
	// // Check if month is provided
	// if (month != null && !month.isEmpty()) {
	// sql.append("AND MONTHNAME(COALESCE(a.indatetime, a.leavedatetime,
	// a.oddatetime)) = ? ");
	// }
	//
	// // Check if year is provided
	// if (year != null) {
	// sql.append("AND YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) =
	// ? ");
	// }
	//
	// // Updated GROUP BY to include all non-aggregated columns
	// sql.append("GROUP BY e.enrollment_id, e.emp_id, e.name, e.incharge_id, " +
	// "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
	// "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), " +
	// "sd.salary_status, sk.group_id, sk.group_name");
	//
	// // Create a list of parameters
	// List<Object> params = new ArrayList<>();
	// params.add(incharge_id);
	// params.add(incharge_id); // Added twice for the conditional check in the SQL
	// if (month != null && !month.isEmpty()) {
	// params.add(month);
	// }
	// if (year != null) {
	// params.add(year);
	// }
	//
	// // Log SQL and parameters
	// System.out.println("Generated SQL: " + sql.toString());
	// System.out.println("Parameters: " + params);
	//
	// // Execute the query
	// List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(),
	// params.toArray());
	// return results;
	// }
	/*
	 * public List<Map<String, Object>> getAttendanceWithSalaryByInchargeId(String
	 * month, Integer year, Integer incharge_id) {
	 * 
	 * StringBuilder sql = new StringBuilder("SELECT " +
	 * "e.enrollment_id, " +
	 * "e.emp_id, " +
	 * "e.name, " +
	 * "e.incharge_id, " +
	 * "YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS year, " +
	 * "MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) AS month, "
	 * +
	 * "COUNT(DISTINCT DATE(a.indatetime)) AS total_days_present, " +
	 * "COUNT(DISTINCT DATE(a.leavedatetime)) AS total_days_absent, " +
	 * "COUNT(DISTINCT DATE(a.oddatetime)) AS total_days_od, " +
	 * "DAY(LAST_DAY(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime))) AS total_days, "
	 * +
	 * "(COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) AS total_days_salary, "
	 * +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1) AS salary_per_day, "
	 * +
	 * "((COUNT(DISTINCT DATE(a.indatetime)) + COUNT(DISTINCT DATE(a.oddatetime))) * "
	 * +
	 * "(SELECT sw.salary_per_day FROM staff_wages sw WHERE sw.isactive = 1 ORDER BY sw.effect_from_date DESC LIMIT 1)) AS total_salary, "
	 * +
	 * "COALESCE(sd.salary_status, '') AS salary_status, COALESCE(sk.group_id, '') AS group_id, "
	 * +
	 * "COALESCE(sk.group_name, '') AS group_name " +
	 * "FROM enrollment_table e " +
	 * "JOIN attendance a ON e.enrollment_id = a.enrollment_id " +
	 * "LEFT JOIN salary_details sd ON e.enrollment_id = sd.enrollment_id " +
	 * "LEFT JOIN suyaudavi_kuzhu sk ON e.group_id = sk.group_id " +
	 * "WHERE (a.inby IS NOT NULL OR a.leaveby IS NOT NULL OR a.odby IS NOT NULL) "
	 * );
	 * 
	 * // Check if incharge_id is present
	 * sql.append("AND (? IS NULL OR e.incharge_id = ?) ");
	 * 
	 * // Check if month is provided
	 * if (month != null && !month.isEmpty()) {
	 * sql.
	 * append("AND MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) = ? "
	 * );
	 * }
	 * 
	 * // Check if year is provided
	 * if (year != null) {
	 * sql.
	 * append("AND YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)) = ? "
	 * );
	 * }
	 * 
	 * sql.
	 * append("GROUP BY e.enrollment_id, e.name, YEAR(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), MONTHNAME(COALESCE(a.indatetime, a.leavedatetime, a.oddatetime)), total_days, sd.salary_status, sk.group_id, sk.group_name"
	 * );
	 * 
	 * // Create a list of parameters
	 * List<Object> params = new ArrayList<>();
	 * params.add(incharge_id);
	 * params.add(incharge_id); // Added twice for the conditional check in the SQL
	 * if (month != null && !month.isEmpty()) {
	 * params.add(month);
	 * }
	 * if (year != null) {
	 * params.add(year);
	 * }
	 * 
	 * // Log SQL and parameters
	 * System.out.println("Generated SQL: " + sql.toString());
	 * System.out.println("Parameters: " + params);
	 * 
	 * // Execute the query
	 * List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(),
	 * params.toArray());
	 * return results;
	 * }
	 */

	public List<Map<String, Object>> getConsolidatedAttendanceReport(String fromdate, String todate, String groupName,
			String zone, String division) { // newly added
		String sql = "select \n" +
				"et.emp_id, et.enrollment_id, et.name, " +
				"MAX(DAY(LAST_DAY(COALESCE(att.indatetime, att.leavedatetime, att.oddatetime)))) AS total_days, " +
				"count(distinct date(att.indatetime)) as totaldayspresent, " +
				"count(distinct case when att.indatetime is null then date(att.oddatetime) end) as totaldaysod, " +
				"count(distinct date(att.leavedatetime)) as totaldaysleave, " +
				"MAX(DAY(LAST_DAY(COALESCE(att.indatetime, att.leavedatetime, att.oddatetime)))) - " +
				"((count(distinct date(att.indatetime)) + " +
				"count(distinct case when att.indatetime is null then date(att.oddatetime) end) + " +
				"count(distinct date(att.leavedatetime)))) as totaldaysabsent, " +
				"et.incharge_name, et.incharge_phoneno, et.incharge_designation, et.zone, " +
				"et.division, et.department, sk.group_name from attendance att "
				+ " left join enrollment_table et on att.enrollment_id = et.enrollment_id "
				+ " left join suyaudavi_kuzhu sk on et.group_id = sk.group_id where "
				+ " (DATE(att.indatetime) BETWEEN STR_TO_DATE('" + fromdate + "', '%Y-%m-%d') AND STR_TO_DATE('"
				+ todate + "', '%Y-%m-%d') "
				+ " OR DATE(att.oddatetime) BETWEEN STR_TO_DATE('" + fromdate + "', '%Y-%m-%d') AND STR_TO_DATE('"
				+ todate + "', '%Y-%m-%d')"
				+ " OR DATE(att.leavedatetime) BETWEEN STR_TO_DATE('" + fromdate + "', '%Y-%m-%d') AND STR_TO_DATE('"
				+ todate + "', '%Y-%m-%d')) "
				+ (zone != null && !zone.isEmpty() ? " and et.zone = ? " : "")
				+ (division != null && !division.isEmpty() ? " and et.division = ? " : "")
				+ (groupName != null && !groupName.isEmpty() ? " and sk.group_name = ? " : "")
				+ " group by et.emp_id, et.enrollment_id, et.name, et.incharge_name, et.incharge_phoneno, et.incharge_designation, et.zone, "
				+ " et.division, et.department, sk.group_name";

		// Prepare the query parameters based on provided inputs
		List<Object> params = new ArrayList<>();
		// params.add(fromdate);
		// params.add(todate);
		if (groupName != null && !groupName.isEmpty()) {
			params.add(groupName);
		}
		if (zone != null && !zone.isEmpty()) {
			params.add(zone);
		}
		if (division != null && !division.isEmpty()) {
			params.add(division);
		}

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		return results;
	}

	public Map<String, List<String>> fetchAttendanceDates(String enrollmentId, String fromdate, String todate,
			String type) { // newly added
		Map<String, List<String>> attendanceData = new HashMap<>();

		if (type.equals("present")) {
			String presentQuery = "SELECT DISTINCT DATE_FORMAT(DATE(indatetime), '%d-%m-%Y') AS PresentDates " +
					"FROM attendance WHERE enrollment_id = ? AND DATE(indatetime) BETWEEN STR_TO_DATE(? , '%Y-%m-%d') AND "
					+
					"STR_TO_DATE(? , '%Y-%m-%d')";
			attendanceData.put("presentDates",
					jdbcTemplate.queryForList(presentQuery, String.class, enrollmentId, fromdate, todate));

		} else if (type.equals("od")) {
			String odQuery = "SELECT DISTINCT DATE_FORMAT(DATE(oddatetime), '%d-%m-%Y') AS OdDates " +
					"FROM attendance WHERE enrollment_id = ? AND DATE(oddatetime) BETWEEN STR_TO_DATE(?, '%Y-%m-%d') AND STR_TO_DATE(?, '%Y-%m-%d')";
			attendanceData.put("odDates",
					jdbcTemplate.queryForList(odQuery, String.class, enrollmentId, fromdate, todate));
		} else if (type.equals("leave")) {
			String leaveQuery = "SELECT DISTINCT DATE_FORMAT(DATE(leavedatetime), '%d-%m-%Y') AS LeaveDates " +
					"FROM attendance WHERE enrollment_id = ? AND DATE(leavedatetime) BETWEEN STR_TO_DATE(?, '%Y-%m-%d') AND STR_TO_DATE(?, '%Y-%m-%d')";
			attendanceData.put("leaveDates",
					jdbcTemplate.queryForList(leaveQuery, String.class, enrollmentId, fromdate, todate));
		} else if (type.equals("absent")) {
			String absentQuery = "";

		}

		return attendanceData;
	}

	public List<Map<String, Object>> getSelfHelpGroupDetails() { // newly added
		String sql = "select * from suyaudavi_kuzhu where isactive = 1 and isdelete =0 order by group_name asc";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
		return results;
	}

	public List<Map<String, Object>> getSalaryNotInitiatedReport(String month, Integer year, String groupName) {

		String sql = "SELECT e.emp_id, e.name, e.designation, e.incharge_name, e.incharge_designation, e.incharge_phoneno, sk.group_name "
				+
				"FROM enrollment_table e " +
				"left join suyaudavi_kuzhu sk on e.group_id = sk.group_id " +
				"WHERE NOT EXISTS ( SELECT 1  " +
				"    FROM salary_details s " +
				"    WHERE e.enrollment_id = s.enrollment_id " +
				"    AND s.month = ? " +
				"    AND s.year = ? )"
				+ (groupName != null && !groupName.isEmpty() ? " and sk.group_name = ? " : "");

		List<Object> params = new ArrayList<>();

		params.add(month);
		params.add(year);

		if (groupName != null && !groupName.isEmpty()) {
			params.add(groupName);
		}

		return jdbcTemplate.queryForList(sql, params.toArray());
	}

	public boolean checkSalaryDetails(int enrollmentId, String month, int year) {
		String sql = "select enrollment_id from salary_details where enrollment_id =? and month = ? and year = ?";
		// boolean result = jdbcTemplate.update(sql, enrollmentId, month, year) == 1;
		List<Integer> results = jdbcTemplate.queryForList(sql, Integer.class, enrollmentId, month, year);
		return !results.isEmpty();
		// return result;
	}

	public int addSalaryDetails(
			int totalDaysPresent, int totalDaysOd, int totalDaysSalary, int totalDaysAbsent,
			String month, int year, int salaryAmount,
			String salaryStatus, int enrollmentId, int groupId, int wageId, int inchargeID) {
		String sql = "INSERT INTO salary_details (total_days_present, total_days_od, total_days_salary, total_days_absent, month, year, "
				+ "salary_amount, salary_status, enrollment_id, group_id, wage_id, incharge_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		return jdbcTemplate.update(sql, totalDaysPresent, totalDaysOd, totalDaysSalary, totalDaysAbsent, month, year,
				salaryAmount, salaryStatus, enrollmentId, groupId, wageId, inchargeID);
	}

	public int updateSalaryDetails(
			int totalDaysPresent, int totalDaysOd, int totalDaysSalary,
			int totalDaysAbsent, String month, int year, int salaryAmount,
			String salaryStatus, int enrollmentId, int groupId,
			int wageId, int inchargeID) {
		String sql = "update salary_details set total_days_present =?, total_days_od=?, total_days_salary = ? , total_days_absent = ?, month = ?, year = ?, "
				+ "salary_amount = ?, salary_status = ?, enrollment_id = ?, group_id= ?, wage_id= ?, incharge_id = ? where enrollment_id =? and month = ? and year = ?) ";

		return jdbcTemplate.update(sql, totalDaysPresent, totalDaysOd, totalDaysSalary, totalDaysAbsent, month, year,
				salaryAmount, salaryStatus, enrollmentId, groupId, wageId, inchargeID, enrollmentId, month, year);
	}
}