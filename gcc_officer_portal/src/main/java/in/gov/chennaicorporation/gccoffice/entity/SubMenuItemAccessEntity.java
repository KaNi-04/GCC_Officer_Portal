package in.gov.chennaicorporation.gccoffice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "submenuitem_access")
public class SubMenuItemAccessEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",nullable = false)
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usergroup_id", referencedColumnName = "id")
    private AppUserGroupEntity userGroupId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submenuitem_id", referencedColumnName = "id")
    private SubMenuItemEntity subMenuItemId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private AppModuleEntity moduleId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuitem_id", referencedColumnName = "id")
    private MenuItemEntity menuItemId;
	
    @Column(name = "isdelete",nullable = false, columnDefinition = "Boolean default false")
    private Boolean isDelete;
    
    @Column(name = "isactive",nullable = false, columnDefinition = "Boolean default true")
    private Boolean isActive;
    
    @Column(name = "created_by",nullable = false, columnDefinition = "Integer Default 0")
    private Integer createdBy;
    
    @Column(name = "cdate",nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private String cDate;
    
  
	public SubMenuItemAccessEntity() {
		
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public AppUserGroupEntity getUserGroupId() {
		return userGroupId;
	}


	public void setUserGroupId(AppUserGroupEntity userGroupId) {
		this.userGroupId = userGroupId;
	}


	public SubMenuItemEntity getSubMenuItemId() {
		return subMenuItemId;
	}


	public void setSubMenuItemId(SubMenuItemEntity subMenuItemId) {
		this.subMenuItemId = subMenuItemId;
	}
	
	

	public AppModuleEntity getModuleId() {
		return moduleId;
	}


	public void setModuleId(AppModuleEntity moduleId) {
		this.moduleId = moduleId;
	}


	public MenuItemEntity getMenuItemId() {
		return menuItemId;
	}


	public void setMenuItemId(MenuItemEntity menuItemId) {
		this.menuItemId = menuItemId;
	}


	public Boolean getIsDelete() {
		return isDelete;
	}


	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}


	public Boolean getIsActive() {
		return isActive;
	}


	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}


	public Integer getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}


	public String getcDate() {
		return cDate;
	}


	public void setcDate(String cDate) {
		this.cDate = cDate;
	}
	
}
