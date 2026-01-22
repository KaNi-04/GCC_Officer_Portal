package in.gov.chennaicorporation.gccoffice.qrassetfeedback.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetParameter {
    private Long paramId;
    private AssetCategoryInfo assetCategory;
    private String paramName;
    private boolean isDelete;
    private boolean isActive;
    private Long createdBy;
    private String cDate;

    // Constructor
    public AssetParameter() {
        
    }
    @JsonCreator
    public AssetParameter(@JsonProperty("paramId") Long paramId,
                         @JsonProperty("catName") String catName,
                         @JsonProperty("isDelete") boolean isDelete,
                         @JsonProperty("isActive") boolean isActive,
                         @JsonProperty("createdBy") Long createdBy,
                         @JsonProperty("cDate") String cDate) {
        this.paramId = paramId;
        this.paramName = catName;
        this.isDelete = isDelete;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.cDate = cDate;
    }
    
 // Define the inner class for assetCategory
    public static class AssetCategoryInfo {
        private Long cId;
        private String catName;
        private boolean isDelete;
        private boolean isActive;
        private Long createdBy;
        private String cDate;
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

	public Long getParamId() {
		return paramId;
	}
	public void setParamId(Long paramId) {
		this.paramId = paramId;
	}
	public AssetCategoryInfo getAssetCategory() {
		return assetCategory;
	}
	public void setAssetCategory(AssetCategoryInfo assetCategory) {
		this.assetCategory = assetCategory;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
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
