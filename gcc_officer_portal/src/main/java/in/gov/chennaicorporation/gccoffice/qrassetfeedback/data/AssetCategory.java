package in.gov.chennaicorporation.gccoffice.qrassetfeedback.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetCategory {
    private Long cId;
    private String catName;
    private boolean isDelete;
    private boolean isActive;
    private Long createdBy;
    private String cDate;

    // Constructor
    public AssetCategory() {
        
    }
    @JsonCreator
    public AssetCategory(@JsonProperty("cId") Long cId,
                         @JsonProperty("catName") String catName,
                         @JsonProperty("isDelete") boolean isDelete,
                         @JsonProperty("isActive") boolean isActive,
                         @JsonProperty("createdBy") Long createdBy,
                         @JsonProperty("cDate") String cDate) {
        this.cId = cId;
        this.catName = catName;
        this.isDelete = isDelete;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.cDate = cDate;
    }
    
	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
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
