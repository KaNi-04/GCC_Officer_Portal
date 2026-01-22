package in.gov.chennaicorporation.gccoffice.sos.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("sosService")
public class SOSService {
	private JdbcTemplate jdbcTemplate;
	private final Environment environment;
	private String fileBaseUrl;
	
	@Autowired
	public SOSService(Environment environment) {
		this.environment = environment;
		this.fileBaseUrl="https://gccservices.in";
	}
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlGccSOSDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getZoneAndWard(){
		String SqlQuery = "";
		
		SqlQuery = "SELECT LPAD(CAST(zone AS UNSIGNED), 2, '0') AS zone, "
				+ "GROUP_CONCAT(LPAD(CAST(divid AS UNSIGNED), 3, '0') "
				+ "ORDER BY CAST(divid AS UNSIGNED) ASC) AS wards "
				+ "FROM petition_master.councillor GROUP BY CAST(zone AS UNSIGNED) "
				+ "ORDER BY CAST(zone AS UNSIGNED) ASC";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
		
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> getRescueList(String status){
		String SqlQuery = "";
		
		SqlQuery = "SELECT *,CASE "
				+ "           WHEN status = 1 THEN 'Closed' "
				+ "           WHEN status = 0 THEN 'Pending' "
				+ "           ELSE 'Unknown' "
				+ "       END AS statusText FROM `rescue` where isactive=1";
		if(status!="") {
			SqlQuery = SqlQuery + " AND `status`='"+status+"'";
		}
		
		System.out.println(SqlQuery);
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
	}
	
	@Transactional
	public List<Map<String, Object>> getRequestDataById(
			String rescue_id){
		String sqlQuery = "SELECT *,CASE"
				+ "	WHEN status = 0 THEN 'Pending' "
				+ "	WHEN status = 1 THEN 'Closed' "
				+ "    ELSE 'unknown'"
				+ "    END AS status_name "
				+ " FROM `rescue` WHERE `id`='"+rescue_id+"'";
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery);
		Map<String, Object> response = new HashMap<>();
	    if (result.isEmpty()) {
	    	return result;
	    }else {
	    
			String sqlQuery2 = "SELECT *,CONCAT('"+fileBaseUrl+"/gccofficialapp/files', filepath) AS imageUrl "
					+ "FROM `rescue_officer_update` WHERE rescue_id='"+rescue_id+"'";
		    List<Map<String, Object>> result2 = jdbcTemplate.queryForList(sqlQuery2);
		   System.out.println(sqlQuery2);
		    // Add the result2 and result3 to the main result map
		    result.get(0).put("officer_activity_data", result2);
	    }
	    response.put("status", "success");
        response.put("message", "Request Information");
        response.put("Data", result);
        return Collections.singletonList(response);
	}
	
	
	@Transactional
	public List<Map<String, Object>> saveRequest(
			String contact_name,
			String contact_number,
			String latitude,
			String longitude,
			String zone,
			String ward,
			String streetid,
			String streetname,
			String location_details,
			String request_type,
			String no_of_count,
			String if_any,
			String land_mark,
			String remarks,
			String loginId,
			String mode
			) {
		List<Map<String, Object>> result=null;
		Map<String, Object> response = new HashMap<>();
		int lastInsertId = 0;
		
		String sqlQuery = "INSERT INTO `rescue`(`contact_name`, `contact_number`, `latitude`, `longitude`, `zone`, `ward`, "
				+ "`streetid`, `streetname`, `location_details`, `request_type`, `no_of_count`, `if_any`, `land_mark`, "
				+ "`remarks`, `user_id`,`modeofcomplient`) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		try {
            int affectedRows = jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
                    ps.setString(1, contact_name);
                    ps.setString(2, contact_number);
                    ps.setString(3, latitude);
                    ps.setString(4, longitude);
                    ps.setString(5, zone);
                    ps.setString(6, ward);
                    ps.setString(7, streetid);
                    ps.setString(8, streetname);
                    ps.setString(9, location_details);
                    ps.setString(10, request_type);
                    ps.setString(11, no_of_count);
                    ps.setString(12, if_any);
                    ps.setString(13, land_mark);
                    ps.setString(14, remarks);
                    ps.setString(15, loginId);
                    ps.setString(16, mode);
                    return ps;
                }
            }, keyHolder);

            if (affectedRows > 0) {
                Number generatedId = keyHolder.getKey();
                lastInsertId = (generatedId != null) ? generatedId.intValue() : 0;
                response.put("insertId", lastInsertId);
                response.put("status", "success");
                response.put("message", "A new SOS request was inserted successfully!");
                System.out.println("A new SOS request was inserted successfully! Insert ID: " + generatedId);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to insert a new SOS request.");
            }
        } catch (DataAccessException e) {
            System.out.println("Data Access Exception:");
            Throwable rootCause = e.getMostSpecificCause();
            if (rootCause instanceof SQLException) {
                SQLException sqlException = (SQLException) rootCause;
                System.out.println("SQL State: " + sqlException.getSQLState());
                System.out.println("Error Code: " + sqlException.getErrorCode());
                System.out.println("Message: " + sqlException.getMessage());
                response.put("status", "error");
                response.put("message", sqlException.getMessage());
                response.put("sqlState", sqlException.getSQLState());
                response.put("errorCode", sqlException.getErrorCode());
            } else {
                System.out.println("Message: " + rootCause.getMessage());
                response.put("status", "error");
                response.put("message", rootCause.getMessage());
            }
        }

        return Collections.singletonList(response);
    }
}
