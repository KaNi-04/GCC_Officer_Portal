package in.gov.chennaicorporation.gccoffice.controller;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RequestMapping("/gcc/api/mobile")
@RestController
public class APIMobileCotroller {
	private JdbcTemplate jdbcTemplate;
	
	private AppModuleRepository appModuleRepository;
    private MenuItemRepository menuItemRepository;
    private SubMenuItemRepository subMenuItemRepository;
    
    private AppModuleAccessRepository appModuleAccessRepository;
    private MenuItemAccessRepository menuItemAccessRepository;
    private SubMenuItemAccessRepository subMenuItemAccessRepository;
    
	@Autowired
	public void setDataSource(@Qualifier("mysqlAppDataSource") DataSource dataSource,
			AppModuleRepository appModuleRepository,
			MenuItemRepository menuItemRepository,
			SubMenuItemRepository subMenuItemRepository,
			AppModuleAccessRepository appModuleAccessRepository,
			MenuItemAccessRepository menuItemAccessRepository,
			SubMenuItemAccessRepository subMenuItemAccessRepository) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.appModuleRepository =appModuleRepository;
		this.menuItemRepository =menuItemRepository;
		this.subMenuItemRepository =subMenuItemRepository;
		this.appModuleAccessRepository =appModuleAccessRepository;
		this.menuItemAccessRepository =menuItemAccessRepository;
		this.subMenuItemAccessRepository =subMenuItemAccessRepository;
		
	}
	@GetMapping(value="/getMenu")
	public List getMenu(@RequestParam String groupId){
		String SqlQuery = "";
		
    	List<AppModuleAccessEntity> getAppModuleAccessEntities;
    	List<AppModuleEntity> getAppModuleEntities;
    	
    		getAppModuleAccessEntities = appModuleAccessRepository.findByUserGroupID(groupId);
    		
    		String[] checkModuleId = new String[getAppModuleAccessEntities.size()];
        	int i=0;
        	for (AppModuleAccessEntity accessEntity : getAppModuleAccessEntities) {
        		checkModuleId[i]=accessEntity.getModuleId().getId().toString();
        		i++;
        	}
        	getAppModuleEntities = appModuleRepository.showAllById(checkModuleId);
    	
		return getAppModuleEntities;
	}
	
	@GetMapping(value="/getSubMenu")
	public List getSubMenu(@RequestParam String groupId, @RequestParam Integer menuId ){
		String SqlQuery = "";
		
		List<MenuItemEntity> getMenuItems;
    	
    	List<MenuItemAccessEntity> getMenuItemAccess = menuItemAccessRepository.findByUserGroupID(groupId, menuId.toString());
		
		String[] checkMenuId = new String[getMenuItemAccess.size()];
    	int j=0;
    	for (MenuItemAccessEntity menuAccessEntity : getMenuItemAccess) {
    		checkMenuId[j]=menuAccessEntity.getMenuItemId().getId().toString();
    		j++;
    	}
		getMenuItems = menuItemRepository.getMenuItemByModuleIdAccess(menuId,checkMenuId);
    	
		return getMenuItems;
	}
	
	@GetMapping(value="/getSubSubMenu")
	public List getSubSubMenu(@RequestParam String groupId, @RequestParam Integer menuId, @RequestParam Integer submenuId ){
		String SqlQuery = "";
		
		List<SubMenuItemAccessEntity> getSubMenuItemAccess = subMenuItemAccessRepository.findByUserGroupIDAccess(groupId, menuId.toString(), submenuId.toString());
		
		String[] checkSubMenuId = new String[getSubMenuItemAccess.size()];
		int k=0;
    	for (SubMenuItemAccessEntity subMenuItemAccessEntity : getSubMenuItemAccess) {
    		checkSubMenuId[k]=subMenuItemAccessEntity.getSubMenuItemId().getId().toString();
    		k++;
    	}
    	List<SubMenuItemEntity> getMenuSubItems = subMenuItemRepository.getSubMenuItemByMenuItemIdAccess(submenuId,checkSubMenuId);
    	
		return getMenuSubItems;
	}
}
