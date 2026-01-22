package in.gov.chennaicorporation.gccoffice.controller;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.entity.AppModuleAccessEntity;
import in.gov.chennaicorporation.gccoffice.entity.AppModuleEntity;
import in.gov.chennaicorporation.gccoffice.entity.AppUserEntity;
import in.gov.chennaicorporation.gccoffice.entity.MenuItemAccessEntity;
import in.gov.chennaicorporation.gccoffice.entity.MenuItemEntity;
import in.gov.chennaicorporation.gccoffice.entity.SubMenuItemAccessEntity;
import in.gov.chennaicorporation.gccoffice.entity.SubMenuItemEntity;
import in.gov.chennaicorporation.gccoffice.entity.UserActivityLog;
import in.gov.chennaicorporation.gccoffice.repository.AppModuleAccessRepository;
import in.gov.chennaicorporation.gccoffice.repository.AppModuleRepository;
import in.gov.chennaicorporation.gccoffice.repository.AppUserRepository;
import in.gov.chennaicorporation.gccoffice.repository.MenuItemAccessRepository;
import in.gov.chennaicorporation.gccoffice.repository.MenuItemRepository;
import in.gov.chennaicorporation.gccoffice.repository.SubMenuItemAccessRepository;
import in.gov.chennaicorporation.gccoffice.repository.SubMenuItemRepository;
import in.gov.chennaicorporation.gccoffice.repository.UserActivityLogRepository;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequestMapping("/gcc/api")
@RestController
public class APIController { 
	
	List<AppUserEntity> getAppUserEntities;
	
	@Autowired
    private HttpServletRequest request;
	
	@Autowired
	private AppUserRepository appUserRepository;
	
	@Autowired
	private LoginUserInfo loginUserInfo;
	
	List<UserActivityLog> getActivityLogs;
	@Autowired
	private UserActivityLogRepository userActivityLogRepository;
	
	List<AppModuleAccessEntity> getAppModuleAccessEntities;
	List<MenuItemAccessEntity> getMenuItemAccessEntities;
	List<SubMenuItemAccessEntity> getSubMenuItemAccessEntities;
	
	@Autowired
	private AppModuleAccessRepository appModuleAccessRepository;
	@Autowired
	private MenuItemAccessRepository menuItemAccessRepository;
	@Autowired
	private SubMenuItemAccessRepository subMenuItemAccessRepository;
	
	List<AppModuleEntity> getAppModuleEntities;
	List<MenuItemEntity> getMenuItemEntities;
	List<SubMenuItemEntity> getSubMenuItemEntities;
	
	@Autowired
	private AppModuleRepository appModuleRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
	private SubMenuItemRepository subMenuItemRepository;
	
    @Autowired
    public APIController(
    		AppModuleRepository appModuleRepository, 
    		MenuItemRepository menuItemRepository, 
    		SubMenuItemRepository subMenuItemRepository,
    		AppModuleAccessRepository appModuleAccessRepository,
    		MenuItemAccessRepository menuItemAccessRepository,
    		SubMenuItemAccessRepository subMenuItemAccessRepository,
    		UserActivityLogRepository userActivityLogRepository) {
    	
    		this.appModuleRepository = appModuleRepository;
		this.menuItemRepository = menuItemRepository;
		this.subMenuItemRepository = subMenuItemRepository;
		
		this.appModuleAccessRepository = appModuleAccessRepository;
		this.menuItemAccessRepository = menuItemAccessRepository;
		this.subMenuItemAccessRepository = subMenuItemAccessRepository;
		this.userActivityLogRepository = userActivityLogRepository;
    	
    }
    
    // Encode to Base64
 	@GetMapping(value="/encodeBase64")
     public static String encodeBase64(@RequestParam String string) {
 		String value=string; 
 		byte[] encodedBytes = Base64.getEncoder().encode(value.getBytes());
         return new String(encodedBytes);
     }

     // Decode from Base64
 	@GetMapping(value="/decodeBase64")
     public static String decodeBase64(@RequestParam String string) {
 		 String encodedValue=string;
         byte[] decodedBytes = Base64.getDecoder().decode(encodedValue);
         return new String(decodedBytes);
     }
 	
	@GetMapping(value="/getAccessTree")
	public String getAccessTreeHTML(@RequestParam String groupId,@RequestParam String groupName) {

    	getAppModuleEntities = appModuleRepository.showAll();
    	
    	String accessHTML = "";
    	String accessTableHTML = "";
    	//String MenuHTML = "";
    	String isSelected = "";
    	//String subMenuHTML = "";
    	
    	String groupNameHTML = "<span class=\"badge badge-pill badge-primary mb-1\">"+groupName+"</span>";
    	for (AppModuleEntity moduleName : getAppModuleEntities) {
    		
    		// ID to check
    		Integer desiredId = moduleName.getId();
    		
    		// Check if the ID exists in the list of entities
    		getAppModuleAccessEntities = appModuleAccessRepository.findByAll(groupId,desiredId.toString());
    		// Check Module Id Access
    		if (getAppModuleAccessEntities != null && !getAppModuleAccessEntities.isEmpty()) { isSelected = "checked"; } else { isSelected = ""; }
    		
    		accessHTML = accessHTML+"<li><input id=\"m"+moduleName.getId()+"\" type=\"checkbox\" data-type=\"parent\" value=\"m_"+desiredId+"\" "+isSelected+"/>&nbsp;<label for=\"m"+moduleName.getId()+"\" style=\"background-color:#000;color:#fff;\">&nbsp;&nbsp;<i class=\""+moduleName.getIcon()+"\"></i> "+moduleName.getName()+"&nbsp;&nbsp;</label>";
    	
    		
    		getMenuItemEntities = menuItemRepository.getMenuItemByModuleId(moduleName.getId());
    		
    		String subMenuHTML = "";
    		for (MenuItemEntity menuItem : getMenuItemEntities) {
    			
    			String mmid = desiredId+"_"+menuItem.getId();
    			// Check if the ID exists in the list of entities
        		getMenuItemAccessEntities = menuItemAccessRepository.findByAll(groupId, desiredId.toString(),menuItem.getId().toString());
        		// Check Module Id Access
        		if (getMenuItemAccessEntities != null && !getMenuItemAccessEntities.isEmpty()) { isSelected = "checked"; } else { isSelected = ""; }
    			
    			if(menuItem.getHasSub()) {
    			
					getSubMenuItemEntities = subMenuItemRepository.getSubMenuItemByMenuItemId(menuItem.getId());
            		String subSubMenuHTML = "";
            		for (SubMenuItemEntity subMenuItem : getSubMenuItemEntities) {
            			String isSelected2 = "";
            			String smid = desiredId+"_"+menuItem.getId()+"_"+subMenuItem.getId();
            			// Check if the ID exists in the list of entities
                		getSubMenuItemAccessEntities = subMenuItemAccessRepository.findByAll(groupId, desiredId.toString(),menuItem.getId().toString(),subMenuItem.getId().toString());
                		// Check Module Id Access
                		if (getSubMenuItemAccessEntities != null && !getSubMenuItemAccessEntities.isEmpty()) { isSelected2 = "checked"; } else { isSelected2 = ""; }
            			
            			subSubMenuHTML = subSubMenuHTML+"<li><input id=\"sm"+subMenuItem.getId()+"\" type=\"checkbox\" data-type=\"sub-child\" value=\"sm_"+smid+"\" "+isSelected2+"/>&nbsp;<label for=\"sm"+subMenuItem.getId()+"\"><i class=\""+subMenuItem.getIcon()+"\"></i> "+subMenuItem.getName()+"</label></li>";
            		}
            		if(subSubMenuHTML.equals(""))
            		{
            			subMenuHTML = subMenuHTML+"<li><input id=\"mm"+menuItem.getId()+"\" type=\"checkbox\" data-type=\"child\" value=\"mm_"+mmid+"\" "+isSelected+"/>&nbsp;<label for=\"mm"+menuItem.getId()+"\"><i class=\""+menuItem.getIcon()+"\"></i> "+menuItem.getName()+"</label></li>";
            		}
            		else {
            			subMenuHTML = subMenuHTML+"<li><input id=\"mm"+menuItem.getId()+"\" type=\"checkbox\" data-type=\"child\" value=\"mm_"+mmid+"\" "+isSelected+"/>&nbsp;<label for=\"mm"+menuItem.getId()+"\">&nbsp;<i class=\""+menuItem.getIcon()+"\"></i> "+menuItem.getName()+"</label> <ul>"+subSubMenuHTML+"</ul></li>";
            		}
    					
    			}
    			else {
    				subMenuHTML = subMenuHTML+"<li><input id=\"mm"+menuItem.getId()+"\" type=\"checkbox\" data-type=\"child\" value=\"mm_"+mmid+"\" "+isSelected+"/>&nbsp;<label for=\"mm"+menuItem.getId()+"\"><i class=\""+menuItem.getIcon()+"\"></i> "+menuItem.getName()+"</label></li>";
    			}
    		}
    		
    		accessHTML = accessHTML+"<ul>"+subMenuHTML+"</ul></li>";
    		subMenuHTML = "";
    		
    	}
    	
    	accessTableHTML = groupNameHTML+"<ul id=\"tree\" class=\"checktree\" style=\"padding-left:25px;\" >"+accessHTML+"</ul>";
    	
    	return accessTableHTML;
    }
	
	@PostMapping("/saveGroupAccessData")
	public String saveGroupAccessData(@RequestBody Map<String, Object> requestBody) {
	    @SuppressWarnings("unchecked")
		List<String> checkedData = (List<String>) requestBody.get("checkedData");
	    String groupId = (String) requestBody.get("groupId");
	    String dataType[]= {"","", "module","menu","submenu"};
	    if (groupId != null) {
	        System.out.println("groupid value: " + groupId);
	        
	        // Set isActive = false based on groupId
	        appModuleAccessRepository.updateActiveStatus(false,groupId);
	        menuItemAccessRepository.updateActiveStatus(false,groupId);
	        subMenuItemAccessRepository.updateActiveStatus(false,groupId);
	        
	        // Now update as per input data
	        if (checkedData != null) {
		        for (String data : checkedData) {
		            System.out.println(data);
		            
		            // m_x -> Module Value (moduleId) -> 2
		            // mm_x_x -> Menu Value (moduleId,menuId) ->3
		            // sm_x_x_x -> SubMenu Value (moduleId, menuId, submenuId) ->4
		            
		            String[] dataValue = data.split("_");
		            Integer dataLength = dataValue.length;
		            System.out.println(dataLength +" ==> "+dataType[dataLength]);
		            
		            if(dataValue.length==2) { // Module
		            	// Check if the ID exists in the list of entities
		            	getAppModuleAccessEntities = appModuleAccessRepository.findByAll(groupId, dataValue[1]);
		            	if (getAppModuleAccessEntities != null && !getAppModuleAccessEntities.isEmpty()) { 
		            		appModuleAccessRepository.update(true,groupId,dataValue[1]);
		            	} 
		            	else {
		            		appModuleAccessRepository.insert(groupId,dataValue[1]);
		            	}
		            }
		            else if(dataValue.length==3) { // Menu
		            	// Check if the ID exists in the list of entities
		        		getMenuItemAccessEntities = menuItemAccessRepository.findByAll(groupId, dataValue[1], dataValue[2]);
		            	if (getMenuItemAccessEntities != null && !getMenuItemAccessEntities.isEmpty()) { 
		            		menuItemAccessRepository.update(true,groupId,dataValue[1],dataValue[2]);
		            	} 
		            	else {
		            		menuItemAccessRepository.insert(groupId,dataValue[1],dataValue[2]);
		            	}
		            }
		            else if(dataValue.length==4) { // SubMenu
		            	// Check if the ID exists in the list of entities
		            	getSubMenuItemAccessEntities = subMenuItemAccessRepository.findByAll(groupId, dataValue[1], dataValue[2], dataValue[3]);
		            	if (getSubMenuItemAccessEntities != null && !getSubMenuItemAccessEntities.isEmpty()) { 
		            		subMenuItemAccessRepository.update(true,groupId,dataValue[1],dataValue[2],dataValue[3]);
		            	} 
		            	else {
		            		subMenuItemAccessRepository.insert(groupId,dataValue[1],dataValue[2],dataValue[3]);
		            	}
		            }
		            
		        }
		    }
	        
	    }

	    return "";
	}
	
	@GetMapping(value="/getUserProfileBasic")
	public String getUserProfileBasic(@RequestParam String userId) {
		//String userHtml ="";
		String userProfileHtml ="";
		//String userProfileTimelineHtml ="";
		getAppUserEntities = appUserRepository.showById(userId);
		for (AppUserEntity appUserEntity : getAppUserEntities) {
			
			userProfileHtml = ""
					+ "		"
					+ "			<div class='card mb-1'>"
					+ "         	<div class='card-body'>"
					+ "                <div class='text-center'>"
					+ "                    <img src='/assets/images/users/user.jpg' class='rounded' width='150'>\n"
					+ "                    <h4 class='card-title mt-10'>"+appUserEntity.getUserid().getName()+"</h4>\n"
					+ "                    <p class='text-muted'>Front End Developer</p>\n"
					+ "             	</div>\n"
					+ "				</div>"
					+ "				<hr class='mb-0 mt-0'> \n"
					+ " 			<div class='card-body'> \n"
					+ "             	<small class='text-muted d-block'>Email address </small>\n"
					+ "             	<h8>"+appUserEntity.getUserid().getEmail()+"</h8> \n"
					+ "             	<small class='text-muted d-block pt-10'>Phone</small>\n"
					+ "             	<h8>"+appUserEntity.getUserid().getPhone()+"</h8> \n"
					+ "             	<small class='text-muted d-block pt-10'>Role</small>\n"
					+ "             	<h8>"+appUserEntity.getRoles()+"</h8>\n"
					+ "             	<small class='text-muted d-block pt-10'>Access Group</small>\n"
					+ "             	<h8>"+appUserEntity.getUsergroup_id().getName()+"</h8>\n"
					+ "         	</div>\n"
					+ "			</div>\n"
					+ "		";

		}

		return userProfileHtml;
	}
	
	@GetMapping(value="/getUserProfile")
	public String getUserProfile(@RequestParam String userId) {
		String userHtml ="";
		String userProfileHtml ="";
		String userProfileTimelineHtml ="";
		String userIp = getUserIP();
		getAppUserEntities = appUserRepository.showById(userId);
		for (AppUserEntity appUserEntity : getAppUserEntities) {
			
			userProfileHtml = ""
					+ "		<div class='col-lg-4 col-md-5'>"
					+ "			<div class='card mb-1'>"
					+ "         	<div class='card-body'>"
					+ "                <div class='text-center'>"
					+ "                    <img src='/assets/images/users/user.jpg' class='rounded' width='150'>\n"
					+ "                    <h4 class='card-title mt-10'>"+appUserEntity.getUserid().getName()+"</h4>\n"
					+ "                    <p class='text-muted'>Front End Developer</p>\n"
					+ "                    <p class='text-muted'>"+userIp+"</p>\n"
					+ "             	</div>\n"
					+ "				</div>"
					+ "				<hr class='mb-0 mt-0'> \n"
					+ " 			<div class='card-body'> \n"
					+ "             	<small class='text-muted d-block'>Email address </small>\n"
					+ "             	<h8>"+appUserEntity.getUserid().getEmail()+"</h8> \n"
					+ "             	<small class='text-muted d-block pt-10'>Phone</small>\n"
					+ "             	<h8>"+appUserEntity.getUserid().getPhone()+"</h8> \n"
					+ "             	<small class='text-muted d-block pt-10'>Role</small>\n"
					+ "             	<h8>"+appUserEntity.getRoles()+"</h8>\n"
					+ "             	<small class='text-muted d-block pt-10'>Access Group</small>\n"
					+ "             	<h8>"+appUserEntity.getUsergroup_id().getName()+"</h8>\n"
					+ "         	</div>\n"
					+ "			</div>\n"
					+ "		</div>";
			
			userProfileTimelineHtml = ""
					+ "<div class='col-lg-8 col-md-7'>"
					+ "	<div class='card'>"
					+ "		<ul class='nav nav-pills custom-pills' id='pills-tab' role='tablist'>"
					+ "			<li class='nav-item'>"
					+ "				<a class='nav-link active' id='pills-timeline-tab' data-toggle='pill' href='#current-month' role='tab' aria-controls='pills-timeline' aria-selected='true'>Timeline</a>"
					+ "			</li>"
					+ "			<li class='nav-item'>"
					+ "				<a class='nav-link' id='pills-profile-tab' data-toggle='pill' href='#last-month' role='tab' aria-controls='pills-profile' aria-selected='false'>Profile</a>"
					+ "			</li>"
					+ "		</ul>"
					+ "		<div class='tab-content' id='pills-tabContent'>"
					+ "			<div class='tab-pane fade active show' id='current-month' role='tabpanel' aria-labelledby='pills-timeline-tab'>"
					+ 				userTimeLine(appUserEntity.getUserid().getUserid().toString(),appUserEntity.getUserid().getName(),"user.jpg")
					+ "			</div>"
					+ "		</div>"
					+ "	</div>"
					+ "</div>";
			
			userHtml ="<div class='row'>" + userProfileHtml + userProfileTimelineHtml + "</div>";

		}

		return userHtml;
	}
	
	
	public String userTimeLine(String userid, String userName, String img) {
		
		
		String userTimeLineHTML = "";
		
		getActivityLogs = userActivityLogRepository.findByUserIdOrderByTimestampDesc(userid);
		for (UserActivityLog userActivityLog : getActivityLogs) {
			userTimeLineHTML += ""
					+ "		<div class='sl-item'>"
					+ "			<div class='sl-left'> "
					+ "				<img src='/assets/images/users/"+img+"' alt='user' class='rounded-circle'> "
					+ "			</div>"
					+ "			<div class='sl-right'>"
					+ "				<div>"
					+ "					<a href='javascript:void(0)' class='link'>"+userName+"</a> "
					+ "					<span class='sl-date'>"+dateFormat(userActivityLog.getTimestamp().toString())+"</span>"
					+ "					<blockquote class='mt-10'>"
					+ "						"+userActivityLog.getActivityType()
					+ "					</blockquote>"
					+ "				</div>"
					+ "			</div>"
					+ "		</div><hr>";
		}
		userTimeLineHTML = ""
				+ "<div class='card-body'>"
				+ "	<div class='profiletimeline mt-0'>"
				+ 		userTimeLineHTML
				+ "	</div>"
				+ "</div>";
		
		return userTimeLineHTML;
	}
	

	public String dateFormat(String timestampString) {
       //String timestampString = "2023-12-28T16:21:24";
        LocalDateTime timestamp = LocalDateTime.parse(timestampString, DateTimeFormatter.ISO_DATE_TIME);

        // Define the desired date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

        // Format the timestamp
        String formattedTimestamp = timestamp.format(formatter);

        //System.out.println(formattedTimestamp);
        return formattedTimestamp.toUpperCase();
    }
	
	public String getUserIP() {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
	
	@GetMapping(value="/passwordGenerator")
	public String passwordGenerator(@RequestParam(value = "password", required = false) String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = password;
        String encodedPassword = passwordEncoder.encode(rawPassword);
		return encodedPassword;
	}
	
	@PostMapping(value="/resetPassword")
	public String resetPassword(
			@RequestParam(value = "", required = false) String userid,
			@RequestParam(value = "", required = false) String oldpassword,
			@RequestParam(value = "", required = false) String newpassword
			) {
		
		System.out.println(userid+" : "+oldpassword+" : "+newpassword);
		
		String oldPasswordTxt=oldpassword; //passwordGenerator(oldpassword);
		String newPasswordTxt=newpassword; //passwordGenerator(newpassword);
		String rawPasswordTxt=newpassword;
		
		System.out.println(oldPasswordTxt+" : "+newPasswordTxt+" : "+rawPasswordTxt);
		
        String response = loginUserInfo.resetPassword(userid,oldPasswordTxt,newPasswordTxt,rawPasswordTxt);
		return response;
	}
	
	@PostMapping(value="/userCheck")
	public List<Map<String, Object>> userCheck(
			@RequestParam(value = "", required = false) String usernametxt,
			@RequestParam(value = "", required = false) String passwordtxt
			) {
		
		System.out.println("HRM Service : "+usernametxt+" : "+passwordtxt);
		
        List<Map<String, Object>> response = loginUserInfo.userCheck(usernametxt,passwordtxt);
		return response;
	}
	
	@GetMapping(value="/getDailyKural")
	public List getDailyKural() {
		return loginUserInfo.getDailyContent();
	}
}
