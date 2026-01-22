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
@Table(name = "menuitem_access")
public class MenuItemAccessEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",nullable = false)
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usergroup_id", referencedColumnName = "id")
    private AppUserGroupEntity userGroupId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuitem_id", referencedColumnName = "id")
    private MenuItemEntity menuItemId;
	
	@ManyToOne
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private AppModuleEntity moduleId;
	
    @Column(name = "isdelete",nullable = false, columnDefinition = "Boolean default false")
    private Boolean isDelete;
    
    @Column(name = "isactive",nullable = false, columnDefinition = "Boolean default true")
    private Boolean isActive;
    
    @Column(name = "created_by",nullable = false, columnDefinition = "Integer Default 0")
    private Integer createdBy;
    
    @Column(name = "cdate",nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private String cDate;
    
  
	public MenuItemAccessEntity() {
		
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


	public MenuItemEntity getMenuItemId() {
		return menuItemId;
	}


	public void setMenuItemId(MenuItemEntity menuItemId) {
		this.menuItemId = menuItemId;
	}

	
	public AppModuleEntity getModuleId() {
		return moduleId;
	}


	public void setModuleId(AppModuleEntity moduleId) {
		this.moduleId = moduleId;
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
