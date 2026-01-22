package in.gov.chennaicorporation.gccoffice.petregistration.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestList {
    private int uniqueid;
    private long mobilenum;
    private String petname;
    private String species;
    private String breed;
    private String gender;
    private String registerdate;
    private int status;
    private String rejectreason;
    private String zone;
    private String statusdate;
    private String updatedby;
    
    
    // Constructor
    public RequestList() {
		
	}

    @JsonCreator
	public RequestList(@JsonProperty("uniqueid") int uniqueid, 
			@JsonProperty("mobilenum") long mobilenum, 
			@JsonProperty("petname") String petname, 
			@JsonProperty("species") String species, 
			@JsonProperty("breed") String breed, 
			@JsonProperty("gender") String gender,
			@JsonProperty("registerdate") String registerdate, 
			@JsonProperty("status") int status, 
			@JsonProperty("rejectreason") String rejectreason, 
			@JsonProperty("zone") String zone, 
			@JsonProperty("statusdate") String statusdate, 
			@JsonProperty("updatedby") String updatedby) {
		
		this.uniqueid = uniqueid;
		this.mobilenum = mobilenum;
		this.petname = petname;
		this.species = species;
		this.breed = breed;
		this.gender = gender;
		this.registerdate = registerdate;
		this.status = status;
		this.rejectreason = rejectreason;
		this.zone = zone;
		this.statusdate = statusdate;
		this.updatedby = updatedby;
	}
    
    // Getters and setters for each field
	public int getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(int uniqueid) {
        this.uniqueid = uniqueid;
    }

    public long getMobilenum() {
        return mobilenum;
    }

    public void setMobilenum(long mobilenum) {
        this.mobilenum = mobilenum;
    }

    public String getPetname() {
        return petname;
    }

    public void setPetname(String petname) {
        this.petname = petname;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegisterdate() {
        return registerdate;
    }

    public void setRegisterdate(String registerdate) {
        this.registerdate = registerdate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRejectreason() {
        return rejectreason;
    }

    public void setRejectreason(String rejectreason) {
        this.rejectreason = rejectreason;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getStatusdate() {
        return statusdate;
    }

    public void setStatusdate(String statusdate) {
        this.statusdate = statusdate;
    }

    public String getUpdatedby() {
        return updatedby;
    }

    public void setUpdatedby(String updatedby) {
        this.updatedby = updatedby;
    }
}
