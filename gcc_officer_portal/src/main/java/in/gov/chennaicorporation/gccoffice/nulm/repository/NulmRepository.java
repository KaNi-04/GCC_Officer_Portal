package in.gov.chennaicorporation.gccoffice.nulm.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class NulmRepository {
	
	@Autowired
	private JdbcTemplate nulmJdbcTemplate;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	 
	@Autowired
	public NulmRepository(JdbcTemplate nulmJdbcTemplate,JdbcTemplate jdbcTemplate) {
		this.nulmJdbcTemplate = nulmJdbcTemplate;
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlNulmDataSource") DataSource dataSource) {
		this.nulmJdbcTemplate = new JdbcTemplate(dataSource);
	}
		
/////////////////////////// Order Repository ///////////////////////////////
public void saveOrderDetails(Map<String, Object> orderData) {
String sql = "INSERT INTO order_details (order_number, order_date, no_of_staffs, order_description, order_copy_url, order_generated_by, created_date, validity_date, order_status, isactive, isdelete) " +
		"VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, " +
		"CASE WHEN ? >= ? THEN 'Active' ELSE 'Expired' END, 1, 0)";
nulmJdbcTemplate.update(sql,
		  orderData.get("orderNumber"),
          orderData.get("orderDate"),
          orderData.get("noOfStaffs"),
          orderData.get("orderDescription"),
          orderData.get("orderCopyUrl"),
          orderData.get("orderGeneratedBy"),
          orderData.get("validityDate"),
          orderData.get("validityDate"),
          orderData.get("orderDate"));
}

/////To get all the list of orders/////////////////////////////////
public List<Map<String, Object>> getAllOrders() {
    String sql = "SELECT od.*, " +
                 "SUM(CASE WHEN ed.appointed = '1' THEN 1 ELSE 0 END) AS appointed_yes_count " +
                 "FROM order_details od " +
                 "LEFT JOIN enrollment_table ed ON od.order_id = ed.order_id " +
                 "GROUP BY od.order_id, od.order_number, od.order_date, od.order_generated_by, od.order_description, od.no_of_staffs, od.category, od.validity_date, od.order_copy_url";

    // Fetch the list of orders with the appointed counts
    List<Map<String, Object>> orderLists = nulmJdbcTemplate.queryForList(sql);

    // Add the calculated value (appointed_yes_count - no_of_staffs) to each order map
    for (Map<String, Object> order : orderLists) {
        int appointedYesCount = ((Number) order.get("appointed_yes_count")).intValue();
        int noOfStaffs = ((Number) order.get("no_of_staffs")).intValue();

        // Calculate the output value
        int outputValue = noOfStaffs-appointedYesCount;

        // Add the output value to the order map
        order.put("output_value", outputValue);
    }

    return orderLists;
}
//public List<Map<String, Object>> getAllOrders() {
//    String sql = "SELECT od.*, " +
//                 "SUM(CASE WHEN ed.appointed = 'Yes' THEN 1 ELSE 0 END) AS appointed_yes_count, " +
//                 "SUM(CASE WHEN ed.appointed = 'No' THEN 1 ELSE 0 END) AS appointed_no_count " +
//                 "FROM order_details od " +
//                 "LEFT JOIN enrollment_table ed ON od.order_id = ed.order_id " +
//                 "GROUP BY od.order_id, od.order_number, od.order_date, od.order_generated_by, od.order_description, od.no_of_staffs, od.validity_date";
//
//    List<Map<String, Object>> orderLists = nulmJdbcTemplate.queryForList(sql);
//    return orderLists;
//}

public List<Map<String, Object>> getAllOrderDetails() {
    String sql = "SELECT order_id, order_number FROM order_details";
    
    // Use queryForList to fetch the data
    return nulmJdbcTemplate.queryForList(sql);
}

public List<String> getAllOrderNumber() {
    String sql = "SELECT order_number FROM order_details";
    
    // Use query with a RowMapper to map each row to a String
    return nulmJdbcTemplate.query(sql, new RowMapper<String>() {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("order_number");
        }
    });
}


/////////////////////////// Scheme Group Repository ///////////////////////////////
public void saveSchemeGroup(Map<String, Object> groupData) {
String sql = "INSERT INTO suyaudavi_kuzhu (group_name, incharge_name, incharge_phoneno, bank_name, bank_branch, ifsc_code, bank_accountno, created_date, isactive, isdelete) " +
"VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 1, 0)";
nulmJdbcTemplate.update(sql, 
		groupData.get("groupName"),
		groupData.get("inchargeName"),
		groupData.get("inchargePhoneNo"),
		groupData.get("bankName"),
		groupData.get("bankBranch"),
		groupData.get("ifscCode"),
		groupData.get("bankAccountno"));
}

public List<Map<String, Object>> getAllSchemeGroupNames() {
    String sql = "SELECT group_id,group_name FROM suyaudavi_kuzhu";
    
    // Use query with a RowMapper to map each row to a String
    return nulmJdbcTemplate.queryForList(sql);
}

public List<Map<String, Object>> getAllSchemeGroups() {
    String sql = "SELECT k.group_name, k.incharge_name, k.incharge_phoneno, k.created_date, " +
                 "SUM(CASE WHEN e.appointed = '1' THEN 1 ELSE 0 END) AS appointed_count, " +
                 "SUM(CASE WHEN e.appointed = '0' AND e.isactive = 1 THEN 1 ELSE 0 END) AS available_count, " +
                 "SUM(CASE WHEN e.isactive = 1 AND e.dropout = '1' THEN 1 ELSE 0 END) AS dropout_count " +
                 "FROM suyaudavi_kuzhu k " +
                 "LEFT JOIN enrollment_table e ON k.group_id = e.group_id " +
                 "GROUP BY k.group_name, k.incharge_name, k.incharge_phoneno, k.created_date";
    
    return nulmJdbcTemplate.queryForList(sql);
}

//public List<String> getAllSchemeGroupNames() {
//    String sql = "SELECT group_name FROM suyaudavi_kuzhu";
//    
//    // Use query with a RowMapper to map each row to a String
//    return nulmJdbcTemplate.query(sql, new RowMapper<String>() {
//        @Override
//        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
//            return rs.getString("group_name");
//        }
//    });
//}



/////////////////////////// Staff Detail Repository ///////////////////////////////
private RowMapper<Integer> groupIdRowMapper = new RowMapper<Integer>() {
    @Override
    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("group_id");
    }
};

// Method to retrieve group_id based on scheme_group_name
public Integer getGroupIdBySchemeGroupName(String groupName) {
    String sql = "SELECT group_id FROM suyaudavi_kuzhu WHERE group_name = ?";
    
    // Correct use of queryForObject with RowMapper and parameter
    return jdbcTemplate.queryForObject(sql, groupIdRowMapper, groupName);
}


public void saveStaffDetails(Map<String, Object> staffData) {
    String sql = "INSERT INTO enrollment_table (name, employee_id, phone_number, gender, date_of_birth, education_qualification, address, additional_skills, aadhar_card_url, ration_card_url, bank_passbook_url, staff_photo_url, created_date, group_id, appointed, facial_attendance, isactive, isdelete) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, 'No', 'No', 1, 0)";

nulmJdbcTemplate.update(sql, 
          staffData.get("name"),
          staffData.get("employeeId"),
          staffData.get("phoneNumber"),
          staffData.get("gender"),
          staffData.get("dateOfBirth"),
          staffData.get("educationQualification"),
          staffData.get("address"), // Ensure address is included
          staffData.get("additionalSkills"),
          staffData.get("aadharCardUrl"),
          staffData.get("rationCardUrl"),
          staffData.get("bankPassbookUrl"),
          staffData.get("staffPhotoUrl"),
          staffData.get("groupId"));
}


public List<String> getStaffName() {
    String sql = "SELECT name FROM enrollment_table ";
    
    // Use query with a RowMapper to map each row to a String
    return nulmJdbcTemplate.query(sql, new RowMapper<String>() {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("name");
        }
    });
}


//////////////////Attendance Repository/////////////////////////////////////////////////////////////////////////////




////////////////Appointment Repository/////////////////////////////////////////////////////////////////
//Existing RowMappers
//private RowMapper<Integer> groupIdsRowMapper = new RowMapper<Integer>() {
// @Override
// public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
//     return rs.getInt("group_id");
// }
//};
//
//private RowMapper<Integer> enrollmentIdRowMapper = new RowMapper<Integer>() {
// @Override
// public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
//     return rs.getInt("enrollment_id");
// }
//};
//
//private RowMapper<Integer> orderIdRowMapper = new RowMapper<Integer>() {
// @Override
// public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
//     return rs.getInt("order_id");
// }
//};
//
////Method to retrieve group_id based on scheme_group_name
//public Integer getGroupIdsBySchemeGroupName(String schemeGroupName) {
// String sql = "SELECT group_id FROM scheme_group WHERE scheme_group_name = ?";
// return jdbcTemplate.queryForObject(sql, groupIdsRowMapper, schemeGroupName);
//}
//
////Method to retrieve staff_id based on staff_name
//public Integer getEnrollmentIdByStaffName(String name) {
// String sql = "SELECT enrollment_id FROM enrollment_table WHERE name = ?";
// return jdbcTemplate.queryForObject(sql, enrollmentIdRowMapper, name);
//}
//
////Method to retrieve order_id based on both order_number and order_title
//public Integer getOrderIdByOrderNumberAndTitle(int orderNumber, String orderTitle) {
// String sql = "SELECT order_id FROM order_details WHERE order_number = ? AND order_title = ?";
// return jdbcTemplate.queryForObject(sql, orderIdRowMapper, orderNumber, orderTitle);
//}
//
////Method to save appointment
//public void saveAppointment(Map<String, Object> appointmentData) {
// String schemeGroupName = (String) appointmentData.get("schemeGroupName");
// Integer groupId = getGroupIdsBySchemeGroupName(schemeGroupName);
//
// String name = (String) appointmentData.get("name");
// Integer staffId = getEnrollmentIdByStaffName(name);
//
// int orderNumber = (int) appointmentData.get("orderNumber");
// String orderTitle = (String) appointmentData.get("orderTitle");
// Integer orderId = getOrderIdByOrderNumberAndTitle(orderNumber, orderTitle);
//
// String sql = "INSERT INTO appointment (zone, ward, supervisor_name, department, date_of_appointment, created_at, group_id, staff_id, order_id, is_active, is_delete) " +
//              "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, 1, 0)";
// nulmJdbcTemplate.update(sql, 
//        appointmentData.get("zone"),
//        appointmentData.get("ward"),
//        appointmentData.get("supervisorName"),
//        appointmentData.get("department"),
//        appointmentData.get("dateOfAppointment"),
//        groupId,
//        staffId,
//        orderId);
//}

}
