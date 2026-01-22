package in.gov.chennaicorporation.gccoffice.zerohours_petition.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.zerohours_petition.service.Petition;
import in.gov.chennaicorporation.gccoffice.zerohours_petition.service.PetitionMaster;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("gcc/api/zerohours_petition")
@RestController("zeroRest") // Mayor Petition Rest
public class APIController {
	private PetitionMaster petitionMaster;
	private Petition petition;
	
	@Autowired
	public APIController(PetitionMaster petitionMaster,Petition petition) {
		this.petitionMaster = petitionMaster;
		this.petition = petition;
	}
	
	@GetMapping(value="/getEventList")
	public List getEvents() {
		return petition.getEvents();
	}
	
	@GetMapping(value="/getCurrentEventList")
	public List getCurrentEventList() {
		return petition.getCurrentEventList();
	}
	
	@GetMapping(value="/getZoneAndWard")
	public List getZoneAndWard() {
		return petitionMaster.getZoneAndWard();
	}
	
	@GetMapping(value="/getCouncillor")
	public List getCouncillor(@RequestParam("ward") String ward) {
		return petitionMaster.getCouncillor(ward);
	}
	
	@GetMapping(value="/getComplaintTypes")
	public List getComplaintTypeList() {
		return petitionMaster.getComplaintType();
	}
	
	@GetMapping(value="/getOfficerList")
	public List getOfficerList() {
		return petitionMaster.getOfficerList();
	}
	
	@GetMapping(value="/getPetitionTypes")
	public List getPetitionTypes() {
		return petitionMaster.getPetitionTypes();
	}
	
	@GetMapping(value="/getPetitionList")
	public List getPetitionList(@RequestParam("eventId") String eventId) {
		return petition.getPetitionList(eventId);
	}
	
	@GetMapping(value="/getUnmapPetitionList")
	public List getUnmapPetitionList(@RequestParam("eventId") String eventId) {
		return petition.getUnmapPetitionList(eventId);
	}
	
	@GetMapping(value="/getPetitionDetails")
	public List getPetitionDetails(@RequestParam("petitionNo") String petitionNo) {
		return petition.getPetitionDetails(petitionNo);
	}
	
	@PostMapping(value="/savePetition")
	public String savePetition(
			@RequestParam(value = "createdBy", required = false) String createdBy,
			@RequestParam(value = "petitionType", required = false) String petitionType,
			//@RequestParam(value = "petitionerName", required = false) String petitionerName,
			//@RequestParam(value = "petitionerMobile", required = false) String petitionerMobile,
			@RequestParam(value = "complaintType", required = false) String petitionerComplaint,
			@RequestParam(value = "zone", required = false) String zone,
			@RequestParam(value = "ward", required = false) String ward,
			@RequestParam(value = "councillorName", required = false) String councillorName,
			@RequestParam(value = "councillorMobile", required = false) String councillorMobile,
			@RequestParam(value = "complaintNature", required = false) String complaintNature,
			@RequestParam(value = "eventid", required = false) String eventid,
			@RequestParam(value = "san_page1", required = false) MultipartFile page1,
			@RequestParam(value = "san_page2", required = false) MultipartFile page2,
			@RequestParam(value = "san_page3", required = false) MultipartFile page3,
			@RequestParam(value = "san_page4", required = false) MultipartFile page4) {
		
		return petition.savePetition(petitionType, createdBy, zone, ward, councillorName, councillorMobile, petitionerComplaint, complaintNature, eventid,page1, page2, page3, page4);
	}
	
	@PostMapping(value="/updatePetition")
	public Boolean updatePetition(
			@RequestParam(value = "petitionType", required = false) String petitionType,
			@RequestParam(value = "petitionNo", required = false) String petitionNo,
			@RequestParam(value = "createdBy", required = false) String createdBy,
			@RequestParam(value = "petitionerName", required = false) String petitionerName,
			@RequestParam(value = "petitionerMobile", required = false) String petitionerMobile,
			@RequestParam(value = "petitionerAddress", required = false) String petitionerAddress,
			@RequestParam(value = "petitionerPincode", required = false) String petitionerPincode,
			@RequestParam(value = "complaintType", required = false) String petitionerComplaint,
			@RequestParam(value = "complaintNature", required = false) String complaintNature) {
		
		return petition.updatePetition(petitionNo, petitionType, createdBy, petitionerName, petitionerMobile, petitionerAddress, petitionerPincode, petitionerComplaint, complaintNature);
	}
	
	@PostMapping(value="/mapComplientToOfficer")
	public Boolean updatePetition(
			@RequestParam("petitionId") String petitionId,
			@RequestParam("petitionNo") String petitionNo,
			@RequestParam("createdBy") String createdBy,
			@RequestParam(value = "", required = false) String mapOfficers) {
		
			String  zerohours_mapping = mapOfficers;
			
			// First before Mapping need to unmaps old list
			petition.unmapComplient(petitionId, petitionNo);
			
	        if(zerohours_mapping!=null) {
	            String[] zerohours_mapping_parts = zerohours_mapping.split(",");
	            for (String values : zerohours_mapping_parts) {
	                String[] zerohours_mapping_val = values.split("_");
	                if (zerohours_mapping_val.length > 0) {
	                    String complientId = zerohours_mapping_val[0];
	                    String offcierId = zerohours_mapping_val[1];
	                    petition.mapComplientToOfficer(createdBy, petitionId, petitionNo, complientId, offcierId);
	                }
	            }
	        }
		return true;
	}
	
	@PostMapping(value="/mapPetitionToOfficer")
	public Boolean mapPetition(
			@RequestParam("petitionId") String petitionId,
			@RequestParam("petitionNo") String petitionNo,
			@RequestParam("createdBy") String createdBy,
			@RequestParam(value = "", required = false) String offidto) {
		
			String  zerohours_mapping = offidto;
			
			// First before Mapping need to unmaps old list
			//petition.unmapComplient(petitionId, petitionNo);
			System.out.println("zerohours_mapping: "+zerohours_mapping);
	        if(zerohours_mapping!=null) {
	            
	                    String complientId = "0";
	                    String offcierId = zerohours_mapping;
	                    petition.mapComplientToOfficer(createdBy, petitionId, petitionNo, complientId, offcierId);
	               
	        }
		return true;
	}
	
	@GetMapping(value="/unmapPetition")
	public String getComplaintMappingDetails(
			@RequestParam("mapid") String mapid) {
		return petition.unmapPetition(mapid);
	}
	
	@GetMapping(value="/getComplaintMappingDetails")
	public List getComplaintMappingDetails(
			@RequestParam("petitionId") String petitionId,
			@RequestParam("petitionNo") String petitionNo,
			@RequestParam("complaintTypeId") String complaintTypeId) {
		return petition.getComplaintMappingDetails(petitionNo,petitionId,complaintTypeId);
	}
	
	@GetMapping(value="/getPetitionMappingDetails2")
	public List getComplaintMappingDetails2(
			@RequestParam("petitionId") String petitionId) {
		return petition.getPetitionMappingDetails2(petitionId);
	}
	
	@GetMapping(value="/getComplaintList")
	public List getComplaintList(@RequestParam("eventId") String eventId,@RequestParam("loginId") String loginId) {
		return petition.getComplaintList(eventId,loginId);
	}
	
	@GetMapping(value="/getComplaintListByPetitionId")
	public List getComplaintListByPetitionId(@RequestParam("petitionId") String petitionId) {
		return petition.getComplaintListByPetitionId(petitionId);
	}
	
	@GetMapping(value="/getComplaintDetails")
	public List<?> getComplaintDetails(@RequestParam("mapId") String mapId) {
		return petition.getComplaintDetails(mapId);
	}
	
	@GetMapping(value="/getComplaintActionDetails")
	public List<?> getComplaintActionDetails(@RequestParam("mapId") String mapId) {
		return petition.getComplaintActionDetails(mapId);
	}
	
	@PostMapping(value="/saveComplanitStatus")
	public String saveComplanitStatus(
			@RequestParam("createdBy") String createdBy,
			@RequestParam("mapId") String mapId,
			@RequestParam("complaintStatus") String complaintStatus,
			@RequestParam("complaintActionText") String complaintActionText,
			@RequestParam("action_san_page1") MultipartFile actionPage1,
			@RequestParam("petitionId") String petitionId,
			@RequestParam("petitionNo") String petitionNo) {
		
		return petition.saveComplanitStatus(createdBy, mapId, complaintStatus, complaintActionText, actionPage1, petitionId, petitionNo);
	}
	
	@GetMapping("/getReportList")
	public List<Map<String, Object>> getReportList(
	        @RequestParam(value = "fromDate", required = false) 
	        @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,

	        @RequestParam(value = "toDate", required = false) 
	        @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {

	    return petition.getReportList(fromDate, toDate);
	}
}
