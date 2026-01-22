package in.gov.chennaicorporation.gccoffice.mtm.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetParameterQuestions.AssetCategory;
import in.gov.chennaicorporation.gccoffice.qrassetfeedback.data.AssetParameterQuestions.AssetParameter;

public class MtmComplaintTypes {
	private Long complaintTypeId;
	private String complaintType;
    private Boolean isDelete;
    private Boolean isActive;
    private Long createdBy;
    private String cDate;
     
    @JsonCreator
    public MtmComplaintTypes(
            @JsonProperty("complaintTypeId") Long complaintTypeId,
            @JsonProperty("complaintType") String complaintType,
            @JsonProperty("isDelete") Boolean isDelete,
            @JsonProperty("isActive") Boolean isActive,
            @JsonProperty("createdBy") Long createdBy,
            @JsonProperty("cDate") String cDate
    ) {
        this.complaintTypeId = complaintTypeId;
        this.complaintType = complaintType;
        this.isDelete = isDelete;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.cDate = cDate;
    }

	public Long getComplaintTypeId() {
		return complaintTypeId;
	}

	public void setComplaintTypeId(Long complaintTypeId) {
		this.complaintTypeId = complaintTypeId;
	}

	public String getComplaintType() {
		return complaintType;
	}

	public void setComplaintType(String complaintType) {
		this.complaintType = complaintType;
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

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public String getcDate() {
		return cDate;
	}

	public void setcDate(String cDate) {
		this.cDate = cDate;
	}
	
    
}