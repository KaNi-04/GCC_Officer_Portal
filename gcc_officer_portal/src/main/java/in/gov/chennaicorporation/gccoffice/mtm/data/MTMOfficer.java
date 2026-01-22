package in.gov.chennaicorporation.gccoffice.mtm.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MTMOfficer {
	private String mapId;
    private String petitionNumber;
    private String mtmEventsPetitionId;
    private String mtmEvents;
    private String offID;
    private String status;
    private String mtmComplaintTypes;
    private String isDelete;
    private String isActive;
    private String createdBy;
    private String cDate;
    private String officerName;
    private String eventName;
    private String officerPetitionCount;
    private String officerCompletedCount;
    private String officerPendingCount;

    @JsonCreator
    public MTMOfficer(
            @JsonProperty("mapId") String mapId,
            @JsonProperty("petitionNumber") String petitionNumber,
            @JsonProperty("mtmEventsPetitionId") String mtmEventsPetitionId,
            @JsonProperty("mtmEvents") String mtmEvents,
            @JsonProperty("offID") String offID,
            @JsonProperty("status") String status,
            @JsonProperty("mtmComplaintTypes") String mtmComplaintTypes,
            @JsonProperty("isDelete") String isDelete,
            @JsonProperty("isActive") String isActive,
            @JsonProperty("createdBy") String createdBy,
            @JsonProperty("cDate") String cDate,
            @JsonProperty("officer_name") String officerName,
            @JsonProperty("event_name") String eventName,
            @JsonProperty("officer_petition_count") String officerPetitionCount,
            @JsonProperty("officer_completed_count") String officerCompletedCount,
            @JsonProperty("officer_pending_count") String officerPendingCount
    ) {
        this.mapId = mapId;
        this.petitionNumber = petitionNumber;
        this.mtmEventsPetitionId = mtmEventsPetitionId;
        this.mtmEvents = mtmEvents;
        this.offID = offID;
        this.status = status;
        this.mtmComplaintTypes = mtmComplaintTypes;
        this.isDelete = isDelete;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.cDate = cDate;
        this.officerName = officerName;
        this.eventName = eventName;
        this.officerPetitionCount = officerPetitionCount;
        this.officerCompletedCount = officerCompletedCount;
        this.officerPendingCount = officerPendingCount;
    }

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getPetitionNumber() {
		return petitionNumber;
	}

	public void setPetitionNumber(String petitionNumber) {
		this.petitionNumber = petitionNumber;
	}

	public String getMtmEventsPetitionId() {
		return mtmEventsPetitionId;
	}

	public void setMtmEventsPetitionId(String mtmEventsPetitionId) {
		this.mtmEventsPetitionId = mtmEventsPetitionId;
	}

	public String getMtmEvents() {
		return mtmEvents;
	}

	public void setMtmEvents(String mtmEvents) {
		this.mtmEvents = mtmEvents;
	}

	public String getOffID() {
		return offID;
	}

	public void setOffID(String offID) {
		this.offID = offID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMtmComplaintTypes() {
		return mtmComplaintTypes;
	}

	public void setMtmComplaintTypes(String mtmComplaintTypes) {
		this.mtmComplaintTypes = mtmComplaintTypes;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getcDate() {
		return cDate;
	}

	public void setcDate(String cDate) {
		this.cDate = cDate;
	}

	public String getOfficerName() {
		return officerName;
	}

	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getOfficerPetitionCount() {
		return officerPetitionCount;
	}

	public void setOfficerPetitionCount(String officerPetitionCount) {
		this.officerPetitionCount = officerPetitionCount;
	}

	public String getOfficerCompletedCount() {
		return officerCompletedCount;
	}

	public void setOfficerCompletedCount(String officerCompletedCount) {
		this.officerCompletedCount = officerCompletedCount;
	}

	public String getOfficerPendingCount() {
		return officerPendingCount;
	}

	public void setOfficerPendingCount(String officerPendingCount) {
		this.officerPendingCount = officerPendingCount;
	}
    
    
}

