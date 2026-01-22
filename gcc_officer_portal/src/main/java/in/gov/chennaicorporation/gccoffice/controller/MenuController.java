package in.gov.chennaicorporation.gccoffice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

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

@ControllerAdvice
public class MenuController {

    private final AppModuleRepository appModuleRepository;
    private final MenuItemRepository menuItemRepository;
    private final SubMenuItemRepository subMenuItemRepository;
    
    private final AppModuleAccessRepository appModuleAccessRepository;
    private final MenuItemAccessRepository menuItemAccessRepository;
    private final SubMenuItemAccessRepository subMenuItemAccessRepository;

    @Autowired
    public MenuController(AppModuleRepository appModuleRepository, 
    		MenuItemRepository menuItemRepository, 
    		SubMenuItemRepository subMenuItemRepository,
    		AppModuleAccessRepository appModuleAccessRepository,
    		MenuItemAccessRepository menuItemAccessRepository,
    		SubMenuItemAccessRepository subMenuItemAccessRepository) {
    	
    	this.appModuleRepository = appModuleRepository;
		this.menuItemRepository = menuItemRepository;
		this.subMenuItemRepository = subMenuItemRepository;
		
		this.appModuleAccessRepository = appModuleAccessRepository;
		this.menuItemAccessRepository = menuItemAccessRepository;
		this.subMenuItemAccessRepository = subMenuItemAccessRepository;
    }
    
    @ModelAttribute("menuItemsHTML") // This will add the menuItems to the model for all requests
    public String menuHTML() {
    	
    	if(LoginUserInfo.getLoginUserId() == null || LoginUserInfo.getUserGroupId()==null) {
    		return "";
    	}
    	
    	List<AppModuleAccessEntity> getAppModuleAccessEntities;
    	List<AppModuleEntity> getAppModuleEntities;
    	if(LoginUserInfo.getUserRole().equals("ADMIN")) {
    		getAppModuleEntities = appModuleRepository.showAll();
    	}
    	else {
    		getAppModuleAccessEntities = appModuleAccessRepository.findByUserGroupID(LoginUserInfo.getUserGroupId());
    		
    		String[] checkModuleId = new String[getAppModuleAccessEntities.size()];
        	int i=0;
        	for (AppModuleAccessEntity accessEntity : getAppModuleAccessEntities) {
        		checkModuleId[i]=accessEntity.getModuleId().getId().toString();
        		i++;
        	}
        	getAppModuleEntities = appModuleRepository.showAllById(checkModuleId);
    	}
    	
    	String menuHTML = "";
    	menuHTML = "<div class=\"nav-item\"><a href=\"/gcc\"><i class=\"ik ik-home\"></i><span>Dashboard</span></a></div>";
    	for (AppModuleEntity moduleName : getAppModuleEntities) {
    		
    		menuHTML = menuHTML+"<div class=\"nav-item has-sub\"><a href=\"javascript:void(0)\"><i class=\""+moduleName.getIcon()+"\"></i><span>"+moduleName.getName()+"</span></a><div class=\"submenu-content\">";
    		
    		List<MenuItemEntity> getMenuItems;
    		if(LoginUserInfo.getUserRole().equals("ADMIN")) {
    			getMenuItems = menuItemRepository.getMenuItemByModuleId(moduleName.getId());
    		}
    		else {
    			List<MenuItemAccessEntity> getMenuItemAccess = menuItemAccessRepository.findByUserGroupID(LoginUserInfo.getUserGroupId(), moduleName.getId().toString());
	    		
	    		String[] checkMenuId = new String[getMenuItemAccess.size()];
	        	int j=0;
	        	for (MenuItemAccessEntity menuAccessEntity : getMenuItemAccess) {
	        		checkMenuId[j]=menuAccessEntity.getMenuItemId().getId().toString();
	        		j++;
	        	}
	    		getMenuItems = menuItemRepository.getMenuItemByModuleIdAccess(moduleName.getId(),checkMenuId);
    		}
    		String subMenuHTML = "";
    		for (MenuItemEntity menuItem : getMenuItems) {
    			if(menuItem.getHasSub()) {
    				if(LoginUserInfo.getUserRole().equals("ADMIN")) {
    					List<SubMenuItemEntity> getMenuSubItems = subMenuItemRepository.getSubMenuItemByMenuItemId(menuItem.getId());
                		String subSubMenuHTML = "";
                		for (SubMenuItemEntity subMenuItem : getMenuSubItems) {
                			String target = "";
                			System.err.println(subMenuItem.getExtenalLink());
                			if(subMenuItem.getExtenalLink()) {
                				target = " target=\"_blank\"";
                				subSubMenuHTML = subSubMenuHTML+"<a href=\""+subMenuItem.getUrl()+"\" class=\"menu-item\""+target+"><i class=\""+subMenuItem.getIcon()+"\"></i><span>"+subMenuItem.getName()+"</span></a>";
                			}
                			else {
                				subSubMenuHTML = subSubMenuHTML+"<a href=\"/gcc/"+subMenuItem.getUrl()+"\" class=\"menu-item\""+target+"><i class=\""+subMenuItem.getIcon()+"\"></i><span>"+subMenuItem.getName()+"</span></a>";
                			}
                			
                		}
                		if(subSubMenuHTML.equals(""))
                		{
                			subMenuHTML = subMenuHTML+"<div class=\"nav-item has-sub\"><a href=\"javascript:void(0);\" class=\"menu-item\"><i class=\""+menuItem.getIcon()+"\"></i>"+menuItem.getName()+"</a></div>";
                		}
                		else {
                			subMenuHTML = subMenuHTML+"<div class=\"nav-item has-sub\"><a href=\"javascript:void(0);\" class=\"menu-item\"><i class=\""+menuItem.getIcon()+"\"></i>"+menuItem.getName()+"</a><div class=\"submenu-content\">"+subSubMenuHTML+"</div></div>";
                		}
    				}
    				else {
    					List<SubMenuItemAccessEntity> getSubMenuItemAccess = subMenuItemAccessRepository.findByUserGroupIDAccess(LoginUserInfo.getUserGroupId(), moduleName.getId().toString(), menuItem.getId().toString());
	    	    		
	    	    		String[] checkSubMenuId = new String[getSubMenuItemAccess.size()];
	    	    		if(checkSubMenuId.length>0)
	    	    		{
	    	    			int k=0;
	        	        	for (SubMenuItemAccessEntity subMenuItemAccessEntity : getSubMenuItemAccess) {
	        	        		checkSubMenuId[k]=subMenuItemAccessEntity.getSubMenuItemId().getId().toString();
	        	        		k++;
	        	        	}
	        	        	
	        				List<SubMenuItemEntity> getMenuSubItems = subMenuItemRepository.getSubMenuItemByMenuItemIdAccess(menuItem.getId(),checkSubMenuId);
	                		String subSubMenuHTML = "";
	                		for (SubMenuItemEntity subMenuItem : getMenuSubItems) {
	                			String target = "";
	                			System.err.println(subMenuItem.getExtenalLink());
	                			if(subMenuItem.getExtenalLink()) {
	                				target = " target=\"_blank\"";
	                				subSubMenuHTML = subSubMenuHTML+"<a href=\""+subMenuItem.getUrl()+"\" class=\"menu-item\""+target+"><i class=\""+subMenuItem.getIcon()+"\"></i><span>"+subMenuItem.getName()+"</span></a>";
	                			}
	                			else {
	                				subSubMenuHTML = subSubMenuHTML+"<a href=\"/gcc/"+subMenuItem.getUrl()+"\" class=\"menu-item\""+target+"><i class=\""+subMenuItem.getIcon()+"\"></i><span>"+subMenuItem.getName()+"</span></a>";
	                			}
	                		}
	                		subMenuHTML = subMenuHTML+"<div class=\"nav-item has-sub\"><a href=\"javascript:void(0);\" class=\"menu-item\"><i class=\""+menuItem.getIcon()+"\"></i>"+menuItem.getName()+"</a><div class=\"submenu-content\">"+subSubMenuHTML+"</div></div>";
	    	    		}
	    	    		else {
	    	    			subMenuHTML = subMenuHTML+"<div class=\"nav-item has-sub\"><a href=\"javascript:void(0);\" class=\"menu-item\"><i class=\""+menuItem.getIcon()+"\"></i>"+menuItem.getName()+"</a></div>";
	    	    		}
    				}
    	        	
    			}
    			else {
    				if(menuItem.getOrderby()>0) {
    					subMenuHTML = subMenuHTML+"<a href=\"/gcc/"+menuItem.getUrl()+"\" class=\"menu-item\"><i class=\""+menuItem.getIcon()+"\"></i><span>"+menuItem.getName()+"</span></a>";
    				}
    			}
    		}
    		
    		menuHTML = menuHTML+subMenuHTML+"</div></div>";
    	}
    	
    	return menuHTML;
    }
}