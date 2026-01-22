package in.gov.chennaicorporation.gccoffice.mtm.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PetitionComplaint {
    private int mapId;
    private String petitionNumber;
    private MtmEventsPetitionId mtmEventsPetitionId;
    private MtmEvents mtmEvents;
    private String offID;
    private String status;
    private MtmComplaintTypes mtmComplaintTypes;
    private boolean isDelete;
    private boolean isActive;
    private int createdBy;
    private String cDate;
    private String officer_name;
    private String event_name;
    private Integer officer_petition_count;
    private Integer officer_completed_count;
    private Integer officer_pending_count;

    // Getters and setters for the above fields

    public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public String getPetitionNumber() {
		return petitionNumber;
	}

	public void setPetitionNumber(String petitionNumber) {
		this.petitionNumber = petitionNumber;
	}

	public MtmEventsPetitionId getMtmEventsPetitionId() {
		return mtmEventsPetitionId;
	}

	public void setMtmEventsPetitionId(MtmEventsPetitionId mtmEventsPetitionId) {
		this.mtmEventsPetitionId = mtmEventsPetitionId;
	}

	public MtmEvents getMtmEvents() {
		return mtmEvents;
	}

	public void setMtmEvents(MtmEvents mtmEvents) {
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

	public MtmComplaintTypes getMtmComplaintTypes() {
		return mtmComplaintTypes;
	}

	public void setMtmComplaintTypes(MtmComplaintTypes mtmComplaintTypes) {
		this.mtmComplaintTypes = mtmComplaintTypes;
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

	public String getOfficer_name() {
		return officer_name;
	}

	public void setOfficer_name(String officer_name) {
		this.officer_name = officer_name;
	}

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public Integer getOfficer_petition_count() {
		return officer_petition_count;
	}

	public void setOfficer_petition_count(Integer officer_petition_count) {
		this.officer_petition_count = officer_petition_count;
	}

	public Integer getOfficer_completed_count() {
		return officer_completed_count;
	}

	public void setOfficer_completed_count(Integer officer_completed_count) {
		this.officer_completed_count = officer_completed_count;
	}

	public Integer getOfficer_pending_count() {
		return officer_pending_count;
	}

	public void setOfficer_pending_count(Integer officer_pending_count) {
		this.officer_pending_count = officer_pending_count;
	}

	public static class MtmEventsPetitionId {
        private int petitionId;
        private MtmEvents mtmEvents;
        private String petitionNumber;
        private String petitionerName;
        private long petitionerMobile;
        private String petitionerComplaint;
        private String petitionerAddress;
        private String petitionerPincode;
        private String petitionerNatureOfComplaint;
        private String tableNo;
        private boolean isDelete;
        private boolean isActive;
        private long createdBy;
        private String cDate;
        private String status;
        private String petitionerComplaint_txt;
        
        // Getters and setters for the above fields
		public int getPetitionId() {
			return petitionId;
		}
		public void setPetitionId(int petitionId) {
			this.petitionId = petitionId;
		}
		public MtmEvents getMtmEvents() {
			return mtmEvents;
		}
		public void setMtmEvents(MtmEvents mtmEvents) {
			this.mtmEvents = mtmEvents;
		}
		public String getPetitionNumber() {
			return petitionNumber;
		}
		public void setPetitionNumber(String petitionNumber) {
			this.petitionNumber = petitionNumber;
		}
		public String getPetitionerName() {
			return petitionerName;
		}
		public void setPetitionerName(String petitionerName) {
			this.petitionerName = petitionerName;
		}
		public long getPetitionerMobile() {
			return petitionerMobile;
		}
		public void setPetitionerMobile(long petitionerMobile) {
			this.petitionerMobile = petitionerMobile;
		}
		public String getPetitionerComplaint() {
			return petitionerComplaint;
		}
		public void setPetitionerComplaint(String petitionerComplaint) {
			this.petitionerComplaint = petitionerComplaint;
		}
		public String getPetitionerAddress() {
			return petitionerAddress;
		}
		public void setPetitionerAddress(String petitionerAddress) {
			this.petitionerAddress = petitionerAddress;
		}
		public String getPetitionerPincode() {
			return petitionerPincode;
		}
		public void setPetitionerPincode(String petitionerPincode) {
			this.petitionerPincode = petitionerPincode;
		}
		public String getPetitionerNatureOfComplaint() {
			return petitionerNatureOfComplaint;
		}
		public void setPetitionerNatureOfComplaint(String petitionerNatureOfComplaint) {
			this.petitionerNatureOfComplaint = petitionerNatureOfComplaint;
		}
		public String getTableNo() {
			return tableNo;
		}
		public void setTableNo(String tableNo) {
			this.tableNo = tableNo;
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
		public long getCreatedBy() {
			return createdBy;
		}
		public void setCreatedBy(long createdBy) {
			this.createdBy = createdBy;
		}
		public String getcDate() {
			return cDate;
		}
		public void setcDate(String cDate) {
			this.cDate = cDate;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getPetitionerComplaint_txt() {
			return petitionerComplaint_txt;
		}
		public void setPetitionerComplaint_txt(String petitionerComplaint_txt) {
			this.petitionerComplaint_txt = petitionerComplaint_txt;
		}

        
    }

    public static class MtmEvents {
        private int mtmId;
        private String eventName;
        private String eventAddress;
        private String eventDate;
        private String zone;
        private String ackNo;
        private long startPetId;
        private boolean isDelete;
        private boolean isActive;
        private long createdBy;
        private String cDate;
        private String event_date;
        private Long total_petitions;
        private Long petitions_completed;
        private Long petition_partially_completed;
        private Long petitions_pending;
        private Long total_compliants;
        private Long compliants_completed;
        private Long compliants_pending;
        

        // Getters and setters for the above fields
		public int getMtmId() {
			return mtmId;
		}
		public void setMtmId(int mtmId) {
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
		public long getStartPetId() {
			return startPetId;
		}
		public void setStartPetId(long startPetId) {
			this.startPetId = startPetId;
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
		public long getCreatedBy() {
			return createdBy;
		}
		public void setCreatedBy(long createdBy) {
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

    public static class MtmComplaintTypes {
        private int complaintTypeId;
        private String complaintType;
        private boolean isDelete;
        private boolean isActive;
        private int createdBy;
        private String cDate;

        // Getters and setters for the above fields
		public int getComplaintTypeId() {
			return complaintTypeId;
		}
		public void setComplaintTypeId(int complaintTypeId) {
			this.complaintTypeId = complaintTypeId;
		}
		public String getComplaintType() {
			return complaintType;
		}
		public void setComplaintType(String complaintType) {
			this.complaintType = complaintType;
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