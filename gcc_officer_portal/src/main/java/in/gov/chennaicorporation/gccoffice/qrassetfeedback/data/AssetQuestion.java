package in.gov.chennaicorporation.gccoffice.qrassetfeedback.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetQuestion {
    private Long aqId;
    private AssetDetails assetDetails;
    private String engQuestion;
    private String tamilQuestion;
    private String qType;
    private boolean isActive;
    private boolean isDelete;
    private Long createdBy;
    private String cDate;

    // Constructors, Getters and Setters

    @JsonCreator
    public AssetQuestion(
            @JsonProperty("aqId") Long aqId,
            @JsonProperty("assetDetails") AssetDetails assetDetails,
            @JsonProperty("engQuestion") String engQuestion,
            @JsonProperty("tamilQuestion") String tamilQuestion,
            @JsonProperty("qType") String qType,
            @JsonProperty("isActive") boolean isActive,
            @JsonProperty("isDelete") boolean isDelete,
            @JsonProperty("createdBy") Long createdBy,
            @JsonProperty("cDate") String cDate
    ) {
        this.aqId = aqId;
        this.assetDetails = assetDetails;
        this.engQuestion = engQuestion;
        this.tamilQuestion = tamilQuestion;
        this.qType = qType;
        this.isActive = isActive;
        this.isDelete = isDelete;
        this.createdBy = createdBy;
        this.cDate = cDate;
    }


    public Long getAqId() {
		return aqId;
	}

	public void setAqId(Long aqId) {
		this.aqId = aqId;
	}

	public AssetDetails getAssetDetails() {
		return assetDetails;
	}

	public void setAssetDetails(AssetDetails assetDetails) {
		this.assetDetails = assetDetails;
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

	public static class AssetDetails {
        private Long assetId;
        private AssetCategory assetCategory;
        private String paramId;
        private Long zoneId;
        private Long divId;
        private String areaName;
        private String locName;
        private String streetName;
        private String assetName;
        private String assetCode;
        private String assetAddress;
        private String assetOpeningTime;
        private String assetClosingTime;
        private String assetLatitude;
        private String assetLongitude;
        private boolean isDelete;
        private boolean isActive;
        private Long createdBy;
        private String cDate;

        // Constructors, Getters and Setters

        public AssetDetails() {
        }

        public Long getAssetId() {
			return assetId;
		}

		public void setAssetId(Long assetId) {
			this.assetId = assetId;
		}

		public AssetCategory getAssetCategory() {
			return assetCategory;
		}

		public void setAssetCategory(AssetCategory assetCategory) {
			this.assetCategory = assetCategory;
		}

		public String getParamId() {
			return paramId;
		}

		public void setParamId(String paramId) {
			this.paramId = paramId;
		}

		public Long getZoneId() {
			return zoneId;
		}

		public void setZoneId(Long zoneId) {
			this.zoneId = zoneId;
		}

		public Long getDivId() {
			return divId;
		}

		public void setDivId(Long divId) {
			this.divId = divId;
		}

		public String getAreaName() {
			return areaName;
		}

		public void setAreaName(String areaName) {
			this.areaName = areaName;
		}

		public String getLocName() {
			return locName;
		}

		public void setLocName(String locName) {
			this.locName = locName;
		}

		public String getStreetName() {
			return streetName;
		}

		public void setStreetName(String streetName) {
			this.streetName = streetName;
		}

		public String getAssetName() {
			return assetName;
		}

		public void setAssetName(String assetName) {
			this.assetName = assetName;
		}

		public String getAssetCode() {
			return assetCode;
		}

		public void setAssetCode(String assetCode) {
			this.assetCode = assetCode;
		}

		public String getAssetAddress() {
			return assetAddress;
		}

		public void setAssetAddress(String assetAddress) {
			this.assetAddress = assetAddress;
		}

		public String getAssetOpeningTime() {
			return assetOpeningTime;
		}

		public void setAssetOpeningTime(String assetOpeningTime) {
			this.assetOpeningTime = assetOpeningTime;
		}

		public String getAssetClosingTime() {
			return assetClosingTime;
		}

		public void setAssetClosingTime(String assetClosingTime) {
			this.assetClosingTime = assetClosingTime;
		}

		public String getAssetLatitude() {
			return assetLatitude;
		}

		public void setAssetLatitude(String assetLatitude) {
			this.assetLatitude = assetLatitude;
		}

		public String getAssetLongitude() {
			return assetLongitude;
		}

		public void setAssetLongitude(String assetLongitude) {
			this.assetLongitude = assetLongitude;
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



		public static class AssetCategory {
            private Long cId;
            private String catName;
            private boolean isDelete;
            private boolean isActive;
            private Long createdBy;
            private String cDate;

            // Constructors, Getters and Setters

            public AssetCategory() {
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
}
