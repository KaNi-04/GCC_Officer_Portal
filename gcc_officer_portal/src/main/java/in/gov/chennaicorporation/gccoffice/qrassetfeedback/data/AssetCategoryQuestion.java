package in.gov.chennaicorporation.gccoffice.qrassetfeedback.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetCategoryQuestion {
	private Long cmqId;
    private AssetCategoryInfo assetCategory;
    private String engQuestion;
    private String tamilQuestion;
    private Long comGroupId;
    private Long comSubGroupId;
    private String comSubGroupName;
    private String qType;
    private boolean isActive;
    private boolean isDelete;
    private Long createdBy;
    private String cDate;

    // Constructors
    // Getters and Setters
    // Other methods if needed

    @JsonCreator
    public AssetCategoryQuestion(
            @JsonProperty("cmqId") Long cmqId,
            @JsonProperty("assetCategory") AssetCategoryInfo assetCategory,
            @JsonProperty("engQuestion") String engQuestion,
            @JsonProperty("tamilQuestion") String tamilQuestion,
            @JsonProperty("comGroupId") Long comGroupId,
            @JsonProperty("comSubGroupId") Long comSubGroupId,
            @JsonProperty("comSubGroupName") String comSubGroupName,
            @JsonProperty("qType") String qType,
            @JsonProperty("isActive") boolean isActive,
            @JsonProperty("isDelete") boolean isDelete,
            @JsonProperty("createdBy") Long createdBy,
            @JsonProperty("cDate") String cDate
    ) {
        this.cmqId = cmqId;
        this.assetCategory = assetCategory;
        this.engQuestion = engQuestion;
        this.tamilQuestion = tamilQuestion;
        this.comGroupId = comGroupId;
        this.comSubGroupId = comSubGroupId;
        this.comSubGroupName = comSubGroupName;
        this.qType = qType;
        this.isActive = isActive;
        this.isDelete = isDelete;
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

	public Long getCmqId() {
		return cmqId;
	}

	public void setCmqId(Long cmqId) {
		this.cmqId = cmqId;
	}

	public AssetCategoryInfo getAssetCategory() {
		return assetCategory;
	}

	public void setAssetCategory(AssetCategoryInfo assetCategory) {
		this.assetCategory = assetCategory;
	}

	public String getEngQuestion() {
		return engQuestion;
	}

	public void setEngQuestion(String engQuestion) {
		this.engQuestion = engQuestion;
	}

	public String getTamilQuestion() {
		return tamilQuestion;
	}

	public void setTamilQuestion(String tamilQuestion) {
		this.tamilQuestion = tamilQuestion;
	}

	public Long getComGroupId() {
		return comGroupId;
	}

	public void setComGroupId(Long comGroupId) {
		this.comGroupId = comGroupId;
	}

	public Long getComSubGroupId() {
		return comSubGroupId;
	}

	public void setComSubGroupId(Long comSubGroupId) {
		this.comSubGroupId = comSubGroupId;
	}

	public String getComSubGroupName() {
		return comSubGroupName;
	}

	public void setComSubGroupName(String comSubGroupName) {
		this.comSubGroupName = comSubGroupName;
	}

	public String getqType() {
		return qType;
	}

	public void setqType(String qType) {
		this.qType = qType;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
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
