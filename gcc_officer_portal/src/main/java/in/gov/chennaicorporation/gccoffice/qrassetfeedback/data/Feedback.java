package in.gov.chennaicorporation.gccoffice.qrassetfeedback.data;

public class Feedback {
    private int fbId;
    private AssetDetails assetDetails;
    private String paramName;
    private String mobileNo;
    private int ratingVal;
    private Object file;
    private int createdBy;
    private String cDate;
    private boolean delete;
    private boolean active;
   
    public Feedback() {
    	super();
	}

	public int getFbId() {
        return fbId;
    }

    public void setFbId(int fbId) {
        this.fbId = fbId;
    }

    public AssetDetails getAssetDetails() {
        return assetDetails;
    }

    public void setAssetDetails(AssetDetails assetDetails) {
        this.assetDetails = assetDetails;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public int getRatingVal() {
        return ratingVal;
    }

    public void setRatingVal(int ratingVal) {
        this.ratingVal = ratingVal;
    }

    public Object getFile() {
        return file;
    }

    public void setFile(Object file) {
        this.file = file;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCDate() {
        return cDate;
    }

    public void setCDate(String cDate) {
        this.cDate = cDate;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public class AssetDetails {
        private int assetId;
        private AssetCategory assetCategory;
        private String paramId;
        private int zoneId;
        private int divId;
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
        private int createdBy;
        private String cDate;
        
        
		public AssetDetails() {
			super();
		}
		
		public int getAssetId() {
			return assetId;
		}
		public void setAssetId(int assetId) {
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
		public int getZoneId() {
			return zoneId;
		}
		public void setZoneId(int zoneId) {
			this.zoneId = zoneId;
		}
		public int getDivId() {
			return divId;
		}
		public void setDivId(int divId) {
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
		public int getCreatedBy() {
			return createdBy;
		}
		public void setCreatedBy(int createdBy) {
			this.createdBy = createdBy;
		}
		public String getcDate() {
			return cDate;
		}
		public void setcDate(String cDate) {
			this.cDate = cDate;
		}
		// Asset Category
		public class AssetCategory {
	        private int cId;
	        private String catName;
	        private boolean isDelete;
	        private boolean isActive;
	        private int createdBy;
	        private String cDate;
	        
	        
			public AssetCategory() {
				super();
			}
			public int getcId() {
				return cId;
			}
			public void setcId(int cId) {
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
			public int getCreatedBy() {
				return createdBy;
			}
			public void setCreatedBy(int createdBy) {
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
