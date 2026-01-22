package in.gov.chennaicorporation.gccoffice.pgr.controller;

import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.pgr.service.DataSyncService;
import in.gov.chennaicorporation.gccoffice.pgr.service.PGRCommon;
import in.gov.chennaicorporation.gccoffice.repository.UserActivityLogRepository;

@RequestMapping("/gcc/api/pgr")
@RestController("pgrRest")
public class APIController {

	private PGRCommon pgrCommon;
	
	@Autowired
    public APIController(PGRCommon pgrCommon) {
    	
		this.pgrCommon = pgrCommon;
	}
	
	@Autowired
    private DataSyncService dataSyncService;
	
	@GetMapping(value="/getDepartments")
	public List getDepartmentList() {
		return pgrCommon.getDeaprtmentList();
	}
	
	@GetMapping(value="/getPendingComplaintCount")
	public List getPendingComplaintCount() {
		return pgrCommon.getPendingComplanitCount();
	}
	
	@GetMapping(value="/getZoneWisePendingComplaintCount")
	public List getZoneWisePendingComplaintCount() {
		return pgrCommon.getZoneWisePendingComplanitCount();
	}
	
	@GetMapping(value="/getZoneWiseReopenComplaintCount")
	public List getZoneWiseReopenComplaintCount() {
		return pgrCommon.getZoneWiseReopenComplanitCount();
	}

	@GetMapping(value="/getComplaintsList")
	public List getComplaintsList() {
		return pgrCommon.getComplaintsList();
	}
	
	@GetMapping(value="/getQaQcList")
	public List getQaQcList() {
		return pgrCommon.getQaQcList();
	}
	
	@GetMapping(value="/getZoneWiseComplaintCountList")
	public List getZoneWiseComplaintList() {
		return pgrCommon.getZoneWiseComplaintCountList();
	}
	
	@GetMapping(value="/getZoneWiseWithoutReopenComplaintList")
	public List getZoneWiseWithoutReopenComplaintList() {
		return pgrCommon.getZoneWiseWithoutReopenComplaintCountList();
	}
	
	@GetMapping(value="/getComplaintDetails")
	public List getComplaintDetails(@RequestParam("complaintNumber") String complaintNumber) {
		return pgrCommon.getComplaintDetails(complaintNumber);
	}
	
	// For Sub Pages
	@GetMapping(value="/getZoneWiseOfficerComplaintCountList")
	public List getZoneWiseOfficerComplaintCountList(@RequestParam("deptid") String deptid, @RequestParam("complainttype") String complainttype) {
		return pgrCommon.getZoneWiseOfficerComplaintCountList(deptid, complainttype);
	}
	
	@GetMapping(value="/getZoneWiseOfficerComplaintList")
	public List getZoneWiseOfficerComplaintList(@RequestParam("deptid") String deptid, @RequestParam("complainttype") String complainttype, @RequestParam("iduser") String iduser) {
		return pgrCommon.getZoneWiseOfficerComplaintList(deptid, complainttype,iduser);
	}
	
	@GetMapping(value="/getZoneWiseOfficerWithoutReopenComplaintCountList")
	public List getZoneWiseOfficerWithoutReopenComplaintCountList(@RequestParam("deptid") String deptid, @RequestParam("complainttype") String complainttype) {
		return pgrCommon.getZoneWiseOfficerWithoutReopenComplaintCountList(deptid, complainttype);
	}
	
	@GetMapping(value="/getZoneWiseOfficerWithoutReopenComplaintList")
	public List getZoneWiseOfficerWithoutReopenComplaintList(@RequestParam("deptid") String deptid, @RequestParam("complainttype") String complainttype, @RequestParam("iduser") String iduser) {
		return pgrCommon.getZoneWiseOfficerWithoutReopenComplaintList(deptid, complainttype,iduser);
	}
	
	@GetMapping(value="/syncERPtoLocal")
	public String syncERPtoLocal() {
		return syncData();
	}
	
	public String syncData() {
		System.out.println("Starting Syning EG_USER Table Data.\n");
		dataSyncService.syncEgUserData();
		System.out.println("EG_USER Table Data synchronization completed.\n");
		
		System.out.println("Starting Syning EG_DEPARTMENT Table Data.\n");
		dataSyncService.syncEgDepartmentData();
		System.out.println("EG_DEPARTMENT Table Data synchronization completed.\n");
		
		System.out.println("Starting Syning EG_BOUNDARY Table Data.\n");
		dataSyncService.syncEgBoundaryData();
		System.out.println("EG_BOUNDARY Table Data synchronization completed.\n");
		
		System.out.println("Starting Syning EGGR_COMPLAINTSTATUS Table Data.\n");
		dataSyncService.syncEggrComplaintStatusData();
		System.out.println("EGGR_COMPLAINTSTATUS Table Data synchronization completed.\n");
		
		System.out.println("Starting Syning EGGR_COMPLAINTTYPES Table Data.\n");
		dataSyncService.syncEggrComplaintTypesData();
		System.out.println("EGGR_COMPLAINTTYPES Table Data synchronization completed.\n");
		
		System.out.println("Starting Syning EGGR_COMPLAINTGROUP Table Data.\n");
		dataSyncService.syncEggrComplaintGroupData();
		System.out.println("EGGR_COMPLAINTGROUP Table Data synchronization completed.\n");
		
		System.out.println("Starting Syning EGGR_COMPLAINTDETAILS Table Data.\n");
		dataSyncService.syncEggrComplaintDetailsData();
		System.out.println("EGGR_COMPLAINTDETAILS Table Data synchronization completed.\n");
		
		System.out.println("Starting Syning EGGR_REDRESSALDETAILS Table Data.\n");
		dataSyncService.syncEggrComplaintDetailsData();
		System.out.println("EGGR_REDRESSALDETAILS Table Data synchronization completed.\n");
		
        return "ERP -> All PGR Data synchronization completed!";
    }

}
