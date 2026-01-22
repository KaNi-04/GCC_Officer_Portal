package in.gov.chennaicorporation.gccoffice.taxcollection.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "collection_request")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler"})
public class TaxCollectionRequestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "request_id",nullable = false)
    private Integer request_id;
	
	@Column(name = "request_no",nullable = true)
    private String requestNo;
	
	@Column(name = "name",nullable = false)
    private String name;

	@Column(name = "mobile",nullable = false)
    private String mobile;
	
	@Column(name = "ptax",nullable = false)
    private String ptax;
	
	@Column(name = "usertype",nullable = false)
    private String usertype;
    
	@Column(name = "availability_date",nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private String availabilitydate;
	
	@Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'open'")
	private String status;
	
    @Column(name = "isdelete",nullable = false, columnDefinition = "Boolean default false")
    private Boolean isDelete;
    
    @Column(name = "isactive",nullable = false, columnDefinition = "Boolean default true")
    private Boolean isActive;
    
    @Column(name = "created_by",nullable = false, columnDefinition = "Integer Default 0")
    private Integer createdBy;
    
    @Column(name = "cdate",nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private String cDate;
    
  
	public TaxCollectionRequestEntity() {
		
	}


	public TaxCollectionRequestEntity(Integer request_id, String requestNo, String name, String mobile, String ptax,
			String usertype, String availabilitydate, String status, Boolean isDelete, Boolean isActive,
			Integer createdBy, String cDate) {
		super();
		this.request_id = request_id;
		this.requestNo = requestNo;
		this.name = name;
		this.mobile = mobile;
		this.ptax = ptax;
		this.usertype = usertype;
		this.availabilitydate = availabilitydate;
		this.status = status;
		this.isDelete = isDelete;
		this.isActive = isActive;
		this.createdBy = createdBy;
		this.cDate = cDate;
	}


	public Integer getRequest_id() {
		return request_id;
	}


	public void setRequest_id(Integer request_id) {
		this.request_id = request_id;
	}


	public String getRequestNo() {
		return requestNo;
	}


	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getPtax() {
		return ptax;
	}


	public void setPtax(String ptax) {
		this.ptax = ptax;
	}


	public String getUsertype() {
		return usertype;
	}


	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}


	public String getAvailabilitydate() {
		return availabilitydate;
	}


	public void setAvailabilitydate(String availabilitydate) {
		this.availabilitydate = availabilitydate;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
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
