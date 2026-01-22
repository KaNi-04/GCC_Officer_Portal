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
@Table(name = "submenuitem")
public class SubMenuItemEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",nullable = false)
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private AppModuleEntity moduleId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuitem_id", referencedColumnName = "id")
    private MenuItemEntity menuItemId;
	
	@Column(name = "name",nullable = false)
    private String name;

	@Column(name = "url",nullable = false)
    private String url;
	
	@Column(name = "icon",nullable = true)
    private String icon;
	
	@Column(name = "orderby",nullable = false, columnDefinition = "INTEGER default 0")
    private Integer orderby;
    
    @Column(name = "isdelete",nullable = false, columnDefinition = "Boolean default false")
    private Boolean isDelete;
    
    @Column(name = "isactive",nullable = false, columnDefinition = "Boolean default true")
    private Boolean isActive;
    
    @Column(name = "created_by",nullable = false, columnDefinition = "Integer Default 0")
    private Integer createdBy;
    
    @Column(name = "cdate",nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private String cDate;
    
    @Column(name = "extenallink",nullable = false, columnDefinition = "Boolean default true")
    private Boolean extenalLink;
  
	public SubMenuItemEntity() {
		
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}


	public Integer getOrderby() {
		return orderby;
	}

	
	public void setOrderby(Integer orderby) {
		this.orderby = orderby;
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


	public Boolean getExtenalLink() {
		return extenalLink;
	}


	public void setExtenalLink(Boolean extenalLink) {
		this.extenalLink = extenalLink;
	}
}
