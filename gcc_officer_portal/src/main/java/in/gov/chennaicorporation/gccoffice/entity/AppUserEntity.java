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
@Table(name = "appusers")
public class AppUserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",nullable = false)
    private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private UserInfoEntity userid;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usergroup_id", referencedColumnName = "id")
    private AppUserGroupEntity usergroup_id;

	@Column(name = "username",nullable = false)
    private String username;

	@Column(name = "password",nullable = false)
    private String password;

	@Column(name = "roles",nullable = false)
    private String roles;
    
    @Column(name = "isdelete",nullable = false, columnDefinition = "Boolean default false")
    private Boolean isDelete;
    
    @Column(name = "isactive",nullable = false, columnDefinition = "Boolean default true")
    private Boolean isActive;
    
    @Column(name = "created_by",nullable = false, columnDefinition = "Integer Default 0")
    private Integer createdBy;
    
    @Column(name = "cdate",nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private String cDate;
    
  
	public AppUserEntity() {
		
	}

	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public UserInfoEntity getUserid() {
		return userid;
	}


	public void setUserid(UserInfoEntity userid) {
		this.userid = userid;
	}

	
	public AppUserGroupEntity getUsergroup_id() {
		return usergroup_id;
	}


	public void setUsergroup_id(AppUserGroupEntity usergroup_id) {
		this.usergroup_id = usergroup_id;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getRoles() {
		return roles;
	}


	public void setRoles(String roles) {
		this.roles = roles;
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
