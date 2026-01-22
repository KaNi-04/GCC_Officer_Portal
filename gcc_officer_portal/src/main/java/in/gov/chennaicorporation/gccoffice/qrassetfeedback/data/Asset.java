package in.gov.chennaicorporation.gccoffice.qrassetfeedback.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Asset {
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

    // Constructors
    // Getters and Setters
    // Other methods if needed

    @JsonCreator
    public Asset(
            @JsonProperty("assetId") Long assetId,
            @JsonProperty("assetCategory") AssetCategory assetCategory,
            @JsonProperty("paramId") String paramId,
            @JsonProperty("zoneId") Long zoneId,
            @JsonProperty("divId") Long divId,
            @JsonProperty("areaName") String areaName,
            @JsonProperty("locName") String locName,
            @JsonProperty("streetName") String streetName,
            @JsonProperty("assetName") String assetName,
            @JsonProperty("assetCode") String assetCode,
            @JsonProperty("assetAddress") String assetAddress,
            @JsonProperty("assetOpeningTime") String assetOpeningTime,
            @JsonProperty("assetClosingTime") String assetClosingTime,
            @JsonProperty("assetLatitude") String assetLatitude,
            @JsonProperty("assetLongitude") String assetLongitude,
            @JsonProperty("isDelete") boolean isDelete,
            @JsonProperty("isActive") boolean isActive,
            @JsonProperty("createdBy") Long createdBy,
            @JsonProperty("cDate") String cDate
    ) {
        this.assetId = assetId;
        this.assetCategory = assetCategory;
        this.paramId = paramId;
        this.zoneId = zoneId;
        this.divId = divId;
        this.areaName = areaName;
        this.locName = locName;
        this.streetName = streetName;
        this.assetName = assetName;
        this.assetCode = assetCode;
        this.assetAddress = assetAddress;
        this.assetOpeningTime = assetOpeningTime;
        this.assetClosingTime = assetClosingTime;
        this.assetLatitude = assetLatitude;
        this.assetLongitude = assetLongitude;
        this.isDelete = isDelete;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.cDate = cDate;
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
