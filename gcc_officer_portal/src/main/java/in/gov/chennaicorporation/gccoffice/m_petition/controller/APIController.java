package in.gov.chennaicorporation.gccoffice.m_petition.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.m_petition.service.Petition;
import in.gov.chennaicorporation.gccoffice.m_petition.service.PetitionMaster;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("gcc/api/m_petition")
@RestController("mpRest") // Mayor Petition Rest
public class APIController {
	private PetitionMaster petitionMaster;
	private Petition petition;
	
	@Autowired
	public APIController(PetitionMaster petitionMaster,Petition petition) {
		this.petitionMaster = petitionMaster;
		this.petition = petition;
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
	public List getPetitionList(@RequestParam("searchmonth") String month,
			@RequestParam("searchyear") String year) {
		return petition.getPetitionList(month,year);
	}
	
	@GetMapping(value="/getUnmapPetitionList")
	public List getUnmapPetitionList() {
		return petition.getUnmapPetitionList();
	}
	
	@GetMapping(value="/getPetitionDetails")
	public List getPetitionDetails(@RequestParam("petitionNo") String petitionNo) {
		return petition.getPetitionDetails(petitionNo);
	}
	
	@PostMapping(value="/savePetition")
	public String savePetition(
			@RequestParam("createdBy") String createdBy,
			@RequestParam("petitionType") String petitionType,
			@RequestParam("petitionerName") String petitionerName,
			@RequestParam("petitionerMobile") String petitionerMobile,
			@RequestParam("complaintType") String petitionerComplaint,
			@RequestParam("san_page1") MultipartFile page1,
			@RequestParam("san_page2") MultipartFile page2,
			@RequestParam("san_page3") MultipartFile page3,
			@RequestParam("san_page4") MultipartFile page4) {
		
		return petition.savePetition(petitionType, createdBy, petitionerName, petitionerMobile, petitionerComplaint, page1, page2, page3, page4);
	}
	
	@PostMapping(value="/updatePetition")
	public Boolean updatePetition(
			@RequestParam("petitionType") String petitionType,
			@RequestParam("petitionNo") String petitionNo,
			@RequestParam("createdBy") String createdBy,
			@RequestParam("petitionerName") String petitionerName,
			@RequestParam("petitionerMobile") String petitionerMobile,
			@RequestParam("petitionerAddress") String petitionerAddress,
			@RequestParam("petitionerPincode") String petitionerPincode,
			@RequestParam("complaintType") String petitionerComplaint,
			@RequestParam("complaintNature") String complaintNature) {
		
		return petition.updatePetition(petitionNo, petitionType, createdBy, petitionerName, petitionerMobile, petitionerAddress, petitionerPincode, petitionerComplaint, complaintNature);
	}
	
	@PostMapping(value="/mapComplientToOfficer")
	public Boolean updatePetition(
			@RequestParam("petitionId") String petitionId,
			@RequestParam("petitionNo") String petitionNo,
			@RequestParam("createdBy") String createdBy,
			@RequestParam(value = "", required = false) String mapOfficers) {
		
			String  com_mapping = mapOfficers;
			
			// First before Mapping need to unmaps old list
			petition.unmapComplient(petitionId, petitionNo);
			
	        if(com_mapping!=null) {
	            String[] com_mapping_parts = com_mapping.split(",");
	            for (String values : com_mapping_parts) {
	                String[] com_mapping_val = values.split("_");
	                if (com_mapping_val.length > 0) {
	                    String complientId = com_mapping_val[0];
	                    String offcierId = com_mapping_val[1];
	                    petition.mapComplientToOfficer(createdBy, petitionId, petitionNo, complientId, offcierId);
	                }
	            }
	        }
		return true;
	}
	
	
	@GetMapping(value="/getComplaintMappingDetails")
	public List getComplaintMappingDetails(
			@RequestParam("petitionId") String petitionId,
			@RequestParam("petitionNo") String petitionNo,
			@RequestParam("complaintTypeId") String complaintTypeId) {
		return petition.getComplaintMappingDetails(petitionNo,petitionId,complaintTypeId);
	}
	
	@GetMapping(value="/getComplaintList")
	public List getComplaintList(@RequestParam("loginId") String loginId,
			@RequestParam("searchmonth") String month,
			@RequestParam("searchyear") String year
			) {
		
		return petition.getComplaintList(loginId,month,year);
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
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,

            @RequestParam(value = "petitionType", required = false) String petitionType) {

        return petition.getReportList(fromDate, toDate, petitionType);
    }
	
	/*
	// Import Old Data
	@PostMapping("/upload")
    public String uploadExcelFile() {
        try {
        	return petition.saveOldData();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    */
}
