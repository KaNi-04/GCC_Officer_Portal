package in.gov.chennaicorporation.gccoffice.qrassetfeedback.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetParameterQuestions {
    private Long apqId;
    private AssetParameter assetParameter;
    private String engQuestion;
    private String tamilQuestion;
    private String qType;
    private boolean isActive;
    private boolean isDelete;
    private Long createdBy;
    private String cDate;

    // Constructors (JsonCreator)
    @JsonCreator
    public AssetParameterQuestions(
            @JsonProperty("apqId") Long apqId,
            @JsonProperty("assetParameter") AssetParameter assetParameter,
            @JsonProperty("engQuestion") String engQuestion,
            @JsonProperty("tamilQuestion") String tamilQuestion,
            @JsonProperty("qType") String qType,
            @JsonProperty("isActive") boolean isActive,
            @JsonProperty("isDelete") boolean isDelete,
            @JsonProperty("createdBy") Long createdBy,
            @JsonProperty("cDate") String cDate
    ) {
        this.apqId = apqId;
        this.assetParameter = assetParameter;
        this.engQuestion = engQuestion;
        this.tamilQuestion = tamilQuestion;
        this.qType = qType;
        this.isActive = isActive;
        this.isDelete = isDelete;
        this.createdBy = createdBy;
        this.cDate = cDate;
    }

    public Long getApqId() {
		return apqId;
	}

	public void setApqId(Long apqId) {
		this.apqId = apqId;
	}

	public AssetParameter getAssetParameter() {
		return assetParameter;
	}

	public void setAssetParameter(AssetParameter assetParameter) {
		this.assetParameter = assetParameter;
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

	// Define the nested classes for assetParameter and assetCategory
    public static class AssetParameter {
        private Long paramId;
        private AssetCategory assetCategory;
        private String paramName;
        private boolean isDelete;
        private boolean isActive;
        private Long createdBy;
        private String cDate;

        // Constructors (JsonCreator)
        @JsonCreator
        public AssetParameter(
                @JsonProperty("paramId") Long paramId,
                @JsonProperty("assetCategory") AssetCategory assetCategory,
                @JsonProperty("paramName") String paramName,
                @JsonProperty("isDelete") boolean isDelete,
                @JsonProperty("isActive") boolean isActive,
                @JsonProperty("createdBy") Long createdBy,
                @JsonProperty("cDate") String cDate
        ) {
            this.paramId = paramId;
            this.assetCategory = assetCategory;
            this.paramName = paramName;
            this.isDelete = isDelete;
            this.isActive = isActive;
            this.createdBy = createdBy;
            this.cDate = cDate;
        }

		public Long getParamId() {
			return paramId;
		}

		public void setParamId(Long paramId) {
			this.paramId = paramId;
		}

		public AssetCategory getAssetCategory() {
			return assetCategory;
		}

		public void setAssetCategory(AssetCategory assetCategory) {
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

    public static class AssetCategory {
        private Long cId;
        private String catName;
        private boolean isDelete;
        private boolean isActive;
        private Long createdBy;
        private String cDate;

        // Constructors (JsonCreator)
        @JsonCreator
        public AssetCategory(
                @JsonProperty("cId") Long cId,
                @JsonProperty("catName") String catName,
                @JsonProperty("isDelete") boolean isDelete,
                @JsonProperty("isActive") boolean isActive,
                @JsonProperty("createdBy") Long createdBy,
                @JsonProperty("cDate") String cDate
        ) {
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
}
