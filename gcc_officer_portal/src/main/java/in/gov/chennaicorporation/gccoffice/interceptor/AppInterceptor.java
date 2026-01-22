package in.gov.chennaicorporation.gccoffice.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import in.gov.chennaicorporation.gccoffice.entity.AppModuleAccessEntity;
import in.gov.chennaicorporation.gccoffice.entity.AppModuleEntity;
import in.gov.chennaicorporation.gccoffice.entity.MenuItemAccessEntity;
import in.gov.chennaicorporation.gccoffice.entity.MenuItemEntity;
import in.gov.chennaicorporation.gccoffice.entity.SubMenuItemAccessEntity;
import in.gov.chennaicorporation.gccoffice.entity.SubMenuItemEntity;
import in.gov.chennaicorporation.gccoffice.repository.AppModuleAccessRepository;
import in.gov.chennaicorporation.gccoffice.repository.AppModuleRepository;
import in.gov.chennaicorporation.gccoffice.repository.MenuItemAccessRepository;
import in.gov.chennaicorporation.gccoffice.repository.MenuItemRepository;
import in.gov.chennaicorporation.gccoffice.repository.SubMenuItemAccessRepository;
import in.gov.chennaicorporation.gccoffice.repository.SubMenuItemRepository;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppInterceptor implements HandlerInterceptor {
	
	List<AppModuleAccessEntity> getAppModuleAccessEntities;
	List<MenuItemAccessEntity> getMenuItemAccessEntities;
	List<SubMenuItemAccessEntity> getSubMenuItemAccessEntities;
	
	private AppModuleAccessRepository appModuleAccessRepository;
	private MenuItemAccessRepository menuItemAccessRepository;
	private SubMenuItemAccessRepository subMenuItemAccessRepository;
	
	List<AppModuleEntity> getAppModuleEntities;
	List<MenuItemEntity> getMenuItemEntities;
	List<SubMenuItemEntity> getSubMenuItemEntities;
	
	private AppModuleRepository appModuleRepository;
	private MenuItemRepository menuItemRepository;
	private SubMenuItemRepository subMenuItemRepository;
	
	private final Environment environment;


    @Autowired
    public AppInterceptor(AppModuleAccessRepository appModuleAccessRepository,
    		MenuItemAccessRepository menuItemAccessRepository, 
    		SubMenuItemAccessRepository subMenuItemAccessRepository,
    		AppModuleRepository appModuleRepository,
    		MenuItemRepository menuItemRepository,
    		SubMenuItemRepository subMenuItemRepository,
    		Environment environment) {
    	
        this.appModuleAccessRepository = appModuleAccessRepository;
        this.menuItemAccessRepository = menuItemAccessRepository;
        this.subMenuItemAccessRepository = subMenuItemAccessRepository;
        
        this.appModuleRepository = appModuleRepository;
        this.menuItemRepository = menuItemRepository;
        this.subMenuItemRepository = subMenuItemRepository;
        
        this.environment = environment;
    }
    
    private boolean isLoginPage(HttpServletRequest request) {
        RequestMatcher loginPageMatcher = new AntPathRequestMatcher("/gcc/login");
        return loginPageMatcher.matches(request);
    }

    private boolean isErrorPage(HttpServletRequest request) {
        RequestMatcher errorPageMatcher = new AntPathRequestMatcher("/gcc/error");
        return errorPageMatcher.matches(request);
    }
    
    private boolean isfavicon(HttpServletRequest request) {
        RequestMatcher loginPageMatcher = new AntPathRequestMatcher("/favicon.ico");
        return loginPageMatcher.matches(request);
    }
    
    public static String[] Split(String request,String patten) {
        String path = request;
        // Split the string by "/"
        String[] parts = path.split(patten);
        /*
        // Display the parts
        for (String part : parts) {
            System.out.println(part);
        }
        */
        return parts;
    }
    
    private boolean hasModuleAccess(String moduleName) {
    	getAppModuleEntities = appModuleRepository.findByModulePath(moduleName);
    	//System.out.println("DataTable Size for the url ("+moduleName+"): "+getAppModuleEntities.size());
    	
    	String moduleid = null;
		
    	if(getAppModuleEntities.size()>0) {
    		for ( AppModuleEntity appModuleEntity : getAppModuleEntities) {
    			moduleid = appModuleEntity.getId().toString();
    		}
    		if(moduleid != null) {
    			String userGroupId = LoginUserInfo.getUserGroupId();
    			System.out.println("hasModuleAccess : Query input`s :\n userGroupId : "+userGroupId+"\n moduleid : "+moduleid);
    			getAppModuleAccessEntities = appModuleAccessRepository.findByAll(userGroupId,moduleid);
    			//System.out.println("After Check in(hasModuleAccess):"+getAppModuleAccessEntities.size());
    			if(getAppModuleAccessEntities.size()>0) {
    				return true;
    			}
    			else {
    				System.out.println("hasModuleAccess : getAppModuleAccessEntities.size = " + getAppModuleAccessEntities.size());
    			}
    			
    		}
    	}
    	return false;
    }
    
    private boolean hasMenuAccess(String url,String moduleName,String menuName) {
    	
    	if(!hasModuleAccess(moduleName)) {return false; }
    	
    	getMenuItemEntities = menuItemRepository.findByUrl(url);
    	//System.out.println("DataTable Size for the url ("+url+"): "+getMenuItemEntities.size());
    	
    	String moduleid = null;
    	String menuid = null;
		
    	if(getMenuItemEntities.size()>0) {
    		for (MenuItemEntity MenuEntity : getMenuItemEntities) {
    			moduleid = MenuEntity.getModuleId().getId().toString();
    			menuid = MenuEntity.getId().toString();
    		}
    		if(moduleid != null && menuid != null) {
    			String userGroupId = LoginUserInfo.getUserGroupId();
    			System.out.println("hasMenuAccess : Query input`s :\n userGroupId : "+userGroupId+"\n moduleid : "+moduleid+"\n menuid : "+menuid);
    			getMenuItemAccessEntities = menuItemAccessRepository.findByAll(userGroupId,moduleid,menuid);
    			if(getMenuItemAccessEntities.size()>0) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    private boolean hasSubMenuAccess(String url,String moduleName,String menuName,String subMenuName) {
    	
    	if(!hasModuleAccess(moduleName)) {return false; }
    	
    	getSubMenuItemEntities = subMenuItemRepository.findByUrl(url);
    	//System.out.println("DataTable Size for the url ("+url+"): "+getSubMenuItemEntities.size());
    	
    	String moduleid = null;
    	String menuid = null;
    	String submenuid = null;
		
    	if(getSubMenuItemEntities.size()>0) {
    		for (SubMenuItemEntity subMenuEntity : getSubMenuItemEntities) {
    			moduleid = subMenuEntity.getModuleId().getId().toString();
    			menuid = subMenuEntity.getMenuItemId().getId().toString();
    			submenuid = subMenuEntity.getId().toString();
    		}
    		if(moduleid != null && menuid != null && submenuid != null) {
    			String userGroupId = LoginUserInfo.getUserGroupId();
    			System.out.println("hasSubMenuAccess 1: Query input`s :\n userGroupId : "+userGroupId+"\n moduleid : "+moduleid+"\n menuid : "+menuid+"\n submenuid : "+submenuid);
    			getSubMenuItemAccessEntities = subMenuItemAccessRepository.findByAll(userGroupId,moduleid,menuid,submenuid);
    			if(getSubMenuItemAccessEntities.size()>0) {
    				// Check Parent Menu Access (Menu Access)
    				if(moduleid != null && menuid != null) {
    	    			System.out.println("hasSubMenuAccess 2: Query input`s :\n userGroupId : "+userGroupId+"\n moduleid : "+moduleid+"\n menuid : "+menuid);
    	    			getMenuItemAccessEntities = menuItemAccessRepository.findByAll(userGroupId,moduleid,menuid);
    	    			if(getMenuItemAccessEntities.size()>0) {
    	    				return true;
    	    			}
    	    		}
    			}
    		}
    	}
    	return false;
    }
    
    private boolean hasAccess(String path) {
    	
    	String userRole = LoginUserInfo.getUserRole();
    	
    	if (userRole != null && userRole.equals("ADMIN")) {
    		return true;
    	}
    	
    	/*
    	if(LoginUserInfo.getUserRole().equals("ADMIN")) {
    		return true;
    	}
    	*/
    	String url = removeLeadingSlash(path);
    	
    	url = removeStartingString(url); // Remove parent App url "gcc"
    	
    	//System.out.println("Give URL : "+path+" After remove leading slash : "+url);
    	
    	// If default home page 
    	if(path.equals("/")) {
    		return true;
    	}
    	
    	//continue for access check 
    	String[] parts = Split(url, "/");
    	
    	if(parts.length<=0) {return true;}
    	
    	//System.out.println("Part Length:"+ parts.length);
    	
    	if(parts.length>1) {
    		int j=0;
    		for (String part : parts) {
                System.out.println(j+") URL SPLIT: "+part);
                j++;
            }
    		String moduleName = "";
    		String menuName = "";
    		String subMenuName = "";
    		
    		if(parts.length==1) { // Check Only Module Access
    			moduleName = parts[0];
    			return hasModuleAccess(moduleName);
    		}
    		else if(parts.length==2) { // Check Module & Menu Access
    			moduleName = parts[0];
        		menuName = parts[1];
        		subMenuName = "";
    			return hasMenuAccess(url,moduleName,menuName);
    		}
    		else if(parts.length==3) { // Check Module , Menu & Sub-Menu Access
    			moduleName = parts[0];
        		menuName = parts[1];
        		subMenuName = parts[2];
    			return hasSubMenuAccess(url,moduleName,menuName,subMenuName);
    		}
    	}
    	
    	return true;
    }
    
    public static String removeLeadingSlash(String input) {
        if (input.startsWith("/")) {
            return input.substring(1);
        }
        return input;
    }
    
    public static String removeStartingString(String input) {
        if (input.startsWith("gcc")) {
            return removeLeadingSlash(input.substring(3));
        }
        return input;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	 String assetsBaseUrl = environment.getProperty("assets.base-url");
         request.setAttribute("assetsBaseUrl", assetsBaseUrl);
         
    	if (!isLoginPage(request) && !isErrorPage(request) && !isfavicon(request)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
            	String fullRequestURI = request.getRequestURI();
                String contextPath = request.getContextPath();
                String path = fullRequestURI.substring(contextPath.length());
                if(!hasAccess(path)) {
                	//Object principal = authentication.getPrincipal();
                	//UserDetails userDetails = (UserDetails) principal;
                	
                	//System.out.println("1) Access Deny For --- " + userDetails.getAuthorities().toString());
                	// Redirect to the error page
                    //response.sendRedirect(request.getContextPath() + "/error");
                    request.getRequestDispatcher("/error").forward(request, response);
                }
            }
        }

        // Continue with the request processing
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Check if the current request matches the login or error page URLs
        if (!isLoginPage(request) && !isErrorPage(request) && !isfavicon(request)) {
            if (modelAndView != null) {
            	String assetsBaseUrl = environment.getProperty("assets.base-url");
                request.setAttribute("assetsBaseUrl", assetsBaseUrl);
            	modelAndView.addObject("assetsBaseUrl", assetsBaseUrl);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof UserDetails) {
                        UserDetails userDetails = (UserDetails) principal;
                        modelAndView.addObject("currentUserDetails", userDetails);
                        System.out.println(userDetails);
                    }
                }
            }
        }
    }
}
