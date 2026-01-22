package in.gov.chennaicorporation.gccoffice.mtm.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MtmEvent {
    private Long mtmId;
    private String eventName;
    private String eventAddress;
    private String eventDate;
    private String zone;
    private String ackNo;
    private Long startPetId;
    private Boolean isDelete;
    private Boolean isActive;
    private Long createdBy;
    private String cDate;
    private String event_date;
    private Long total_petitions;
    private Long petitions_completed;
    private Long petition_partially_completed;
    private Long petitions_pending;
    private Long total_compliants;
    private Long compliants_completed;
    private Long compliants_pending;

    // Constructors, getters, and setters

    @JsonCreator
    public MtmEvent(
            @JsonProperty("mtmId") Long mtmId,
            @JsonProperty("eventName") String eventName,
            @JsonProperty("eventAddress") String eventAddress,
            @JsonProperty("eventDate") String eventDate,
            @JsonProperty("zone") String zone,
            @JsonProperty("ackNo") String ackNo,
            @JsonProperty("startPetId") Long startPetId,
            @JsonProperty("isDelete") Boolean isDelete,
            @JsonProperty("isActive") Boolean isActive,
            @JsonProperty("createdBy") Long createdBy,
            @JsonProperty("cDate") String cDate,
            @JsonProperty("event_date") String event_date,
            @JsonProperty("total_petitions") Long total_petitions,
            @JsonProperty("petitions_completed") Long petitions_completed,
            @JsonProperty("petition_partially_completed") Long petition_partially_completed,
            @JsonProperty("petitions_pending") Long petitions_pending,
            @JsonProperty("total_compliants") Long total_compliants,
            @JsonProperty("compliants_completed") Long compliants_completed,
            @JsonProperty("compliants_pending") Long compliants_pending
    ) {
        this.mtmId = mtmId;
        this.eventName = eventName;
        this.eventAddress = eventAddress;
        this.eventDate = eventDate;
        this.zone = zone;
        this.ackNo = ackNo;
        this.startPetId = startPetId;
        this.isDelete = isDelete;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.cDate = cDate;
        this.event_date = event_date;
        this.total_petitions = total_petitions;
        this.petitions_completed = petitions_completed;
        this.petition_partially_completed = petition_partially_completed;
        this.petitions_pending = petitions_pending;
        this.total_compliants = total_compliants;
        this.compliants_completed = compliants_completed;
        this.compliants_pending = compliants_pending;
    }

	public Long getMtmId() {
		return mtmId;
	}

	public void setMtmId(Long mtmId) {
		this.mtmId = mtmId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventAddress() {
		return eventAddress;
	}

	public void setEventAddress(String eventAddress) {
		this.eventAddress = eventAddress;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getAckNo() {
		return ackNo;
	}

	public void setAckNo(String ackNo) {
		this.ackNo = ackNo;
	}

	public Long getStartPetId() {
		return startPetId;
	}

	public void setStartPetId(Long startPetId) {
		this.startPetId = startPetId;
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

	public String getEvent_date() {
		return event_date;
	}

	public void setEvent_date(String event_date) {
		this.event_date = event_date;
	}

	public Long getTotal_petitions() {
		return total_petitions;
	}

	public void setTotal_petitions(Long total_petitions) {
		this.total_petitions = total_petitions;
	}

	public Long getPetitions_completed() {
		return petitions_completed;
	}

	public void setPetitions_completed(Long petitions_completed) {
		this.petitions_completed = petitions_completed;
	}

	public Long getPetition_partially_completed() {
		return petition_partially_completed;
	}

	public void setPetition_partially_completed(Long petition_partially_completed) {
		this.petition_partially_completed = petition_partially_completed;
	}

	public Long getPetitions_pending() {
		return petitions_pending;
	}

	public void setPetitions_pending(Long petitions_pending) {
		this.petitions_pending = petitions_pending;
	}

	public Long getTotal_compliants() {
		return total_compliants;
	}

	public void setTotal_compliants(Long total_compliants) {
		this.total_compliants = total_compliants;
	}

	public Long getCompliants_completed() {
		return compliants_completed;
	}

	public void setCompliants_completed(Long compliants_completed) {
		this.compliants_completed = compliants_completed;
	}

	public Long getCompliants_pending() {
		return compliants_pending;
	}

	public void setCompliants_pending(Long compliants_pending) {
		this.compliants_pending = compliants_pending;
	}

    
}

