package in.gov.chennaicorporation.gccoffice.taxcollection.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "collection_request_status")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler"})
public class TaxCollectionRequestStatusEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "request_status_id",nullable = false)
    private Integer request_status_id;
	
	@ManyToOne
    @JoinColumn(name="request_id",nullable = false)
    private TaxCollectionRequestEntity taxCollectionRequestEntity;
	
	@Column(name = "status",nullable = false)
    private String status ;

	@Column(name = "comments",nullable = false)
    private String comments;
	
	@Column(name = "created_by",nullable = false, columnDefinition = "Integer Default 0")
    private Integer createdBy;
    
    @Column(name = "cdate",nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private String cDate;
	
    @Column(name = "isdelete",nullable = false, columnDefinition = "Boolean default false")
    private Boolean isDelete;
    
    @Column(name = "isactive",nullable = false, columnDefinition = "Boolean default true")
    private Boolean isActive;
  
	public TaxCollectionRequestStatusEntity() {
		
	}

	public TaxCollectionRequestStatusEntity(Integer request_status_id,
			TaxCollectionRequestEntity taxCollectionRequestEntity, String status, String comments, Integer createdBy,
			String cDate, Boolean isDelete, Boolean isActive) {
		super();
		this.request_status_id = request_status_id;
		this.taxCollectionRequestEntity = taxCollectionRequestEntity;
		this.status = status;
		this.comments = comments;
		this.createdBy = createdBy;
		this.cDate = cDate;
		this.isDelete = isDelete;
		this.isActive = isActive;
	}

	public Integer getRequest_status_id() {
		return request_status_id;
	}

	public void setRequest_status_id(Integer request_status_id) {
		this.request_status_id = request_status_id;
	}

	public TaxCollectionRequestEntity getTaxCollectionRequestEntity() {
		return taxCollectionRequestEntity;
	}

	public void setTaxCollectionRequestEntity(TaxCollectionRequestEntity taxCollectionRequestEntity) {
		this.taxCollectionRequestEntity = taxCollectionRequestEntity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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
}
