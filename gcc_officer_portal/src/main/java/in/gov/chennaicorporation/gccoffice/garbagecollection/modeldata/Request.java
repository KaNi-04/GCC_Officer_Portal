package in.gov.chennaicorporation.gccoffice.garbagecollection.modeldata;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
public class Request {
	
	private Integer requestId;
    private String name;
    private String mobile;
    private String email;
    private String address;
    private String landmark;
    private String zone;
    private String division;
    private String area;
    private String locality;
    private String street;
    private String latitude;
    private String longitude;
    private String requestType;
    private String garbageCategory;
    private String status;
    private String statusId;
    private Boolean isDelete;
    private Boolean isActive;
    private Integer createdBy;
    private String cDate;

    @JsonCreator
    public Request(@JsonProperty("request_id") Integer requestId,
                      @JsonProperty("name") String name,
                      @JsonProperty("mobile") String mobile,
                      @JsonProperty("email") String email,
                      @JsonProperty("address") String address,
                      @JsonProperty("landmark") String landmark,
                      @JsonProperty("zone") String zone,
                      @JsonProperty("division") String division,
                      @JsonProperty("area") String area,
                      @JsonProperty("locality") String locality,
                      @JsonProperty("street") String street,
                      @JsonProperty("latitude") String latitude,
                      @JsonProperty("longitude") String longitude,
                      @JsonProperty("request_type") String requestType,
                      @JsonProperty("garbage_category") String garbageCategory,
                      @JsonProperty("status") String status,
                      @JsonProperty("statusid") String statusId,
                      @JsonProperty("isdelete") Boolean isDelete,
                      @JsonProperty("isactive") Boolean isActive,
                      @JsonProperty("created_by") Integer createdBy,
                      @JsonProperty("cdate") String cDate) {
        this.requestId = requestId;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.landmark = landmark;
        this.zone = zone;
        this.division = division;
        this.area = area;
        this.locality = locality;
        this.street = street;
        this.latitude = latitude;
        this.longitude = longitude;
        this.requestType = requestType;
        this.garbageCategory = garbageCategory;
        this.status = status;
        this.statusId = statusId;
        this.garbageCategory = garbageCategory;
        this.isDelete = isDelete;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.cDate = cDate;
    }

	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getGarbageCategory() {
		return garbageCategory;
	}

	public void setGarbageCategory(String garbageCategory) {
		this.garbageCategory = garbageCategory;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
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
