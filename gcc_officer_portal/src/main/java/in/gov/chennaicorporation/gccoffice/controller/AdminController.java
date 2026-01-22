package in.gov.chennaicorporation.gccoffice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//import in.gov.chennaicorporation.gccoffice.entity.AppModuleAccessEntity;
import in.gov.chennaicorporation.gccoffice.entity.AppModuleEntity;
import in.gov.chennaicorporation.gccoffice.entity.AppUserEntity;
import in.gov.chennaicorporation.gccoffice.entity.AppUserGroupEntity;
import in.gov.chennaicorporation.gccoffice.entity.MenuItemEntity;
import in.gov.chennaicorporation.gccoffice.entity.SubMenuItemEntity;
import in.gov.chennaicorporation.gccoffice.repository.AppModuleRepository;
import in.gov.chennaicorporation.gccoffice.repository.AppUserGroupRepository;
import in.gov.chennaicorporation.gccoffice.repository.AppUserRepository;
import in.gov.chennaicorporation.gccoffice.repository.MenuItemRepository;
import in.gov.chennaicorporation.gccoffice.repository.SubMenuItemRepository;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/admin")
@Controller("gccAdminController")
public class AdminController {
   
    
    private final AppUserRepository appUserRepository;
    private final AppUserGroupRepository appUserGroupRepository;
    
    private final AppModuleRepository appModuleRepository;
    private final MenuItemRepository menuItemRepository;
    private final SubMenuItemRepository subMenuItemRepository;
    
    
    
    @Autowired
    public AdminController(
    		AppUserRepository appUserRepository,
    		AppUserGroupRepository appUserGroupRepository,
    		AppModuleRepository appModuleRepository, 
    		MenuItemRepository menuItemRepository, 
    		SubMenuItemRepository subMenuItemRepository) {
    	
    	this.appUserRepository = appUserRepository;
    	this.appUserGroupRepository = appUserGroupRepository;
    	
    	this.appModuleRepository = appModuleRepository;
		this.menuItemRepository = menuItemRepository;
		this.subMenuItemRepository = subMenuItemRepository;
    	
    }
   
    @GetMapping({"", "/", "/index"})
	public String main(Model model) {
    	String userId = LoginUserInfo.getLoginUserId();
        if (userId != null && !userId.isEmpty()) {
            System.out.println("String UserID: " + userId);
        }
		return "modules/admin/index";
	}
    
    @GetMapping("/user-list")
	public String userList(Model model) {
    	model.addAttribute("userlist",getUser());
		return "modules/admin/user-list";
	}
    
    @GetMapping("/user-group-list")
	public String userGroupList(Model model) {
    	model.addAttribute("userGroupList",getUserGroup());
		return "modules/admin/user-group-list";
	}
    
    @GetMapping("/user-group-access")
	public String userGroupAccess(Model model) {
    	String accessTableHTML = getAccessTreeHTML();
    	model.addAttribute("userGroupList",getUserGroup());
    	model.addAttribute("accessHTML",accessTableHTML);
		return "modules/admin/user-group-access";
	}
    
    

    //////////////////////////////////////// (Functions) ////////////////////////////////////////
    
    public List<AppUserEntity> getUser() {
    	List<AppUserEntity> appUserEntities;
    	appUserEntities = appUserRepository.showAll();
    	return appUserEntities; 
    }
    
    public List<AppUserGroupEntity> getUserGroup() {
    	List<AppUserGroupEntity> appUserGroupEntities;
    	appUserGroupEntities = appUserGroupRepository.findAll();
    	return appUserGroupEntities; 
    }
    
    public String getAccessTreeHTML() {

    	//List<AppModuleAccessEntity> getAppModuleAccessEntities;
    	List<AppModuleEntity> getAppModuleEntities;
    	
    	getAppModuleEntities = appModuleRepository.showAll();
    	
    	String accessHTML = "";
    	String accessTableHTML = "";
    	//String MenuHTML = "";
    	//String subMenuHTML = "";
    	for (AppModuleEntity moduleName : getAppModuleEntities) {
    		
    		accessHTML = accessHTML+"<li><input id=\"m"+moduleName.getId()+"\" type=\"checkbox\" data-type=\"parent\" value=\"\"/>&nbsp;<label for=\"m"+moduleName.getId()+"\" style=\"background-color:#000;color:#fff;\">&nbsp;&nbsp;<i class=\""+moduleName.getIcon()+"\"></i> "+moduleName.getName()+"&nbsp;&nbsp;</label>";
    	
    		List<MenuItemEntity> getMenuItems;
    		getMenuItems = menuItemRepository.getMenuItemByModuleId(moduleName.getId());
    		
    		String subMenuHTML = "";
    		for (MenuItemEntity menuItem : getMenuItems) {
    			
    			if(menuItem.getHasSub()) {
    			
					List<SubMenuItemEntity> getMenuSubItems = subMenuItemRepository.getSubMenuItemByMenuItemId(menuItem.getId());
            		String subSubMenuHTML = "";
            		for (SubMenuItemEntity subMenuItem : getMenuSubItems) {
            			subSubMenuHTML = subSubMenuHTML+"<li><input id=\"sm"+subMenuItem.getId()+"\" type=\"checkbox\" data-type=\"sub-child\"/> <label for=\"sm"+subMenuItem.getId()+"\"><i class=\""+subMenuItem.getIcon()+"\"></i> "+subMenuItem.getName()+"</label></li>";
            		}
            		if(subSubMenuHTML.equals(""))
            		{
            			subMenuHTML = subMenuHTML+"<li><input id=\"mm"+menuItem.getId()+"\" type=\"checkbox\" data-type=\"child\" /> <label for=\"mm"+menuItem.getId()+"\"><i class=\""+menuItem.getIcon()+"\"></i> "+menuItem.getName()+"</label></li>";
            		}
            		else {
            			subMenuHTML = subMenuHTML+"<li><input id=\"mm"+menuItem.getId()+"\" type=\"checkbox\" data-type=\"child\"/> <label for=\"mm"+menuItem.getId()+"\"><i class=\""+menuItem.getIcon()+"\"></i> "+menuItem.getName()+"</label> <ul>"+subSubMenuHTML+"</ul></li>";
            		}
    					
    			}
    			else {
    				subMenuHTML = subMenuHTML+"<li><input id=\"mm"+menuItem.getId()+"\" type=\"checkbox\" data-type=\"child\"/> <label for=\"mm"+menuItem.getId()+"\"><i class=\""+menuItem.getIcon()+"\"></i> "+menuItem.getName()+"</label></li>";
    			}
    		}
    		
    		accessHTML = accessHTML+"<ul>"+subMenuHTML+"</ul></li>";
    		subMenuHTML = "";
    	}
    	
    	accessTableHTML = "<ul id=\"tree\" class=\"checktree\">"+accessHTML+"</ul>";
    	
    	return accessTableHTML;
    }
}
