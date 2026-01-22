package in.gov.chennaicorporation.gccoffice.garbagecollection.modeldata;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
public class RequestStatus {
	
	private Integer statusId;
	private Integer requestId;
    private String status;
    private String comments;
    private Boolean isDelete;
    private Boolean isActive;
    private Integer createdBy;
    private String cDate;

    @JsonCreator
    public RequestStatus(@JsonProperty("request_status_id") Integer statusId,
                      @JsonProperty("request_id") Integer requestId,
                      @JsonProperty("status") String status,
                      @JsonProperty("comments") String comments,
                      @JsonProperty("isdelete") Boolean isDelete,
                      @JsonProperty("isactive") Boolean isActive,
                      @JsonProperty("created_by") Integer createdBy,
                      @JsonProperty("cdate") String cDate) {
        this.statusId = statusId;
        this.requestId = requestId;
        this.status = status;
        this.comments = comments;
        this.isDelete = isDelete;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.cDate = cDate;
    }

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
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
