package in.gov.chennaicorporation.gccoffice.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class LoginUserInfo {
	private JdbcTemplate jdbcAppTemplate;
	private JdbcTemplate jdbcKuralTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlAppDataSource") DataSource sosDataSource,
			@Qualifier("mysqlKuralDataSource") DataSource kuralDataSource) {
		this.jdbcAppTemplate = new JdbcTemplate(sosDataSource);
		this.jdbcKuralTemplate = new JdbcTemplate(kuralDataSource);
	}
	
    public static UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return (UserDetails) principal;
            }
        }
        return null;
    }
    
    public static String getLoginUserId() {
    	UserDetails userDetails = LoginUserInfo.getCurrentUserDetails();
    	 
        if (userDetails instanceof CustomUserDetails) {
        	//System.out.println("User Details : "+userDetails.toString());
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            Map<String, Object> additionalAttributes = customUserDetails.getAdditionalAttributes();
            if (additionalAttributes != null) {
                Object userId = additionalAttributes.get("userid");
                Object groupId = additionalAttributes.get("groupid");
                if (userId != null) {
                    return userId.toString();
                }
            }
            /*
            for (Map.Entry<String, Object> entry : additionalAttributes.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                System.out.println("Key: " + key + ", Value: " + value);
            }*/
        }
    	return null;
    }
    
    public static String getUserGroupId() {
    	UserDetails userDetails = LoginUserInfo.getCurrentUserDetails();
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            Map<String, Object> additionalAttributes = customUserDetails.getAdditionalAttributes();
            if (additionalAttributes != null) {
                Object userGroupId = additionalAttributes.get("usergroupid");
                if (userGroupId != null) {
                    return userGroupId.toString();
                }
            }
        }
    	return null;
    }
    
    public static String getUserRole() {
    	UserDetails userDetails = LoginUserInfo.getCurrentUserDetails();
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            Map<String, Object> additionalAttributes = customUserDetails.getAdditionalAttributes();
            if (additionalAttributes != null) {
                Object userRole = additionalAttributes.get("userrole");
                if (userRole != null) {
                    return userRole.toString();
                }
            }
        }
    	return null;
    }
    
    public String resetPassword(String userid, String oldPassword, String newPassword, String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Query to get the stored encoded password for the given user ID
        String fetchPasswordQuery = "SELECT `password` FROM `appusers` WHERE `id` = ?";
        List<Map<String, Object>> result = jdbcAppTemplate.queryForList(fetchPasswordQuery, new Object[]{userid});

        if (result.isEmpty()) {
            // If no matching user is found, return an error message
            return "User not found.";
        }

        String storedEncodedPassword = (String) result.get(0).get("password");

        // Verify the old password against the stored encoded password
        if (!passwordEncoder.matches(oldPassword, storedEncodedPassword)) {
            // If the old password doesn't match, return an error message
            return "Old password does not match.";
        }

        // Encode the new password using BCrypt
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        // Update the password in the database
        String updateQuery = "UPDATE `appusers` SET `password` = ?, `pwd_txt` = ? WHERE `id` = ?";
        int rowsAffected = jdbcAppTemplate.update(updateQuery, new Object[]{encodedNewPassword, newPassword, userid});

        if (rowsAffected > 0) {
            // If the update is successful, return a success message
            return "success";
        } else {
            // If the update fails, return an error message
            return "Failed to reset the password. Please try again.";
        }
    }
    
    
    public List<Map<String, Object>> userCheck(String username, String password) {
    	
    	Integer statusCode=404;
    	String statusTxt="error";
    	String msgTxt="User not found.";
    	
    	Map<String, Object> response = new HashMap<>();
    	 
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Query to get the stored encoded password for the given user ID
        String fetchPasswordQuery = "SELECT `id`, `roles`, `usergroup_id`, `isactive`, `dname`, `password` as saltref FROM `appusers` WHERE `username` = ? AND (`isactive`=1 AND `isdelete`=0)";
        List<Map<String, Object>> result = jdbcAppTemplate.queryForList(fetchPasswordQuery, new Object[]{username});
        
        if (result.isEmpty()) {
        	System.out.println(fetchPasswordQuery);
            // If no matching user is found, return an error message
        	msgTxt = "User not found.";
        	response.put("Data", Collections.emptyList());
        }
        else {

	        String storedEncodedPassword = (String) result.get(0).get("saltref");
	
	        // Verify the password against the stored encoded password
	        if (!passwordEncoder.matches(password, storedEncodedPassword)) {
	        	msgTxt = "User not found.";
	        	response.put("Data", Collections.emptyList());
	        }
	        else {
	        	statusCode=200;
	        	statusTxt="success";
	        	msgTxt = "Login Successfully";
	        	if (result.isEmpty()) {
	    	    } else {
	    	        response.put("Data", result);
	    	    }
	        }
        }
        response.put("statusCode", statusCode);
	    response.put("statusText", statusTxt);
	    response.put("message", msgTxt);
	    
	    return Collections.singletonList(response);
    }
    
    // Dashboard : தினம் ஒரு திருக்குறள் // thirukkural
    
    public List<Map<String, Object>> getDailyContent() {
        // Query to get all content from the table
        String contentQuery = "SELECT * FROM kural"; // Replace content_table with your actual table name
        List<Map<String, Object>> contentList = jdbcKuralTemplate.queryForList(contentQuery);

        // Total number of records
        int totalRecords = contentList.size();

        if (totalRecords == 0) {
            // If there are no records, return an empty list
            return Collections.emptyList();
        }

        // Calculate the number of days since a specific start date (e.g., October 21, 2024)
        LocalDate startDate = LocalDate.of(2024, 10, 21);
        LocalDate currentDate = LocalDate.now();
        long daysSinceStart = ChronoUnit.DAYS.between(startDate, currentDate);

        // Get the content index for today (using modulo to cycle through the records)
        int contentIndex = (int) (daysSinceStart % totalRecords);

        // Return the content for today as a list
        return Collections.singletonList(contentList.get(contentIndex));
    }
}