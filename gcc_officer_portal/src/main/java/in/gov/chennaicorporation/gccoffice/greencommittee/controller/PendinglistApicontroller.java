package in.gov.chennaicorporation.gccoffice.greencommittee.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.greencommittee.service.PendinglistService;

@RestController
@RequestMapping("/gcc/api/greencommittee/pendinglist")
public class PendinglistApicontroller {

	@Autowired
	private PendinglistService pendinglistService;
	
	
	@GetMapping("/getzones")
    public List<Map<String, Object>> ViewZoneDropdown() {
       
    	return pendinglistService.ViewZoneDropdown();
           	        
    }
	
	 @GetMapping("/getwardbyzone")
	    public List<Map<String, Object>> ViewWardDropdown(@RequestParam String zone) {
	       
	    	return pendinglistService.ViewWardDropdown(zone);
	           	        
	 }
	 
	 @GetMapping("/dropdown-Event")
	    public ResponseEntity<List<Map<String, Object>>> ViewDropdownEvent() {
	        try {
	            List<Map<String, Object>> result = pendinglistService.ViewDropdownEvent();
	            return ResponseEntity.ok(result);
	        } catch (Exception e) {
	            Map<String, Object> error = new HashMap<>();
	            error.put("error", "Unable to fetch event list");
	            error.put("details", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body(Collections.singletonList(error));
	        }
	    }
	 
//	 @GetMapping("/getgreencommitteelist")
//	 public List<Map<String, Object>> getgreencommitteelist(@RequestParam String date,
//	                                                        @RequestParam String zones) {
//
//	     List<String> refnums = pendinglistService.getrefnums(date, zones);
//	     //System.out.println("refnums="+refnums);
//	     List<Map<String, Object>> savedids=pendinglistService.getvcsavedrefids();
//	     //System.err.println("savedids="+savedids);
//	     // Convert to List<String>
//	     List<String> savedRefIds = savedids.stream()
//	             .map(m -> m.get("ref_id").toString())
//	             .toList();
//
//	     //System.out.println("savedRefIds=" + savedRefIds);
//	     
//	     List<Map<String, Object>> finalList = new ArrayList<>();
//
//	     if (refnums.isEmpty()) {
//	         return Collections.emptyList();
//	     }
//
//	     // REMOVE already saved ref_ids
//	     List<String> filteredRefIds = refnums.stream()
//	             .filter(ref -> !savedRefIds.contains(ref))
//	             .toList();
//	     
//	     //System.out.println("filteredRefIds="+filteredRefIds);
//
//	     for (String ref : filteredRefIds) {
//	         Map<String, Object> data = pendinglistService.getlistdata(ref);
//	         if (data != null && !data.isEmpty()) {
//	             finalList.add(data);
//	         }
//	     }
//
//	     return finalList;
//	 }
	 
	 
	 @GetMapping("/getgreencommitteelist")
	 public List<Map<String, Object>> getgreencommitteelist(
	         @RequestParam String date, @RequestParam String zones) {

	     List<Map<String,Object>> refnums = pendinglistService.getrefnums(date, zones);
	     System.out.println("refnums="+refnums);
	     List<Map<String,Object>> savedIds = pendinglistService.getvcsavedrefids();
	     System.out.println("savedIds="+savedIds);
	     // Convert savedIds → List<String>
	     List<String> savedRefList = new ArrayList<>();
	     for (Map<String,Object> m : savedIds) {
	         savedRefList.add(m.get("ref_id").toString());
	     }

	     List<Map<String,Object>> finalList = new ArrayList<>();

	     for (Map<String,Object> row : refnums) {

	         String refId = row.get("ref_id").toString();
	         Integer inspReinsId = row.get("latest_reinspection_id") != null ?
	                 Integer.parseInt(row.get("latest_reinspection_id").toString())
	                 : null;

	         // Skip if already committee decided
	         if (savedRefList.contains(refId)) continue;

	         // Get reinspection latest id
	         Integer dbLatest = pendinglistService.getLatestReinspectionId(refId);

	         // Rule: if inspection reinspection ID is NULL but in DB exists → skip
	         if (inspReinsId == null && dbLatest != null) continue;

	         // Rule: if BOTH exist AND not equal → skip
	         if (inspReinsId != null && dbLatest != null && !inspReinsId.equals(dbLatest))
	             continue;

	         // 👍 Valid, add final data
	         Map<String,Object> data = pendinglistService.getlistdata(refId);
	         if (data != null && !data.isEmpty()) {
	        	 
	        	 System.out.println("inside="+refId);
	             finalList.add(data);
	         }
	     }

	     return finalList;
	 }

	 
	 @GetMapping("/getdetailsbyrefid")
	 public Map<String, Object> getdetailsbyrefid(@RequestParam String refid) {
	     return pendinglistService.getdetailsbyrefid(refid);
	 }
	 
	 @GetMapping("/getCommitteeActions")
	 public List<Map<String, Object>> getCommitteeActions() {
	       
	    	return pendinglistService.getCommitteeActions();
	           	        
	    }
	 
	 @PostMapping("/savecommittee")
	 public Map<String, Object> saveCommittee(@RequestBody Map<String, Object> payload) {

	     String refId = (String) payload.get("ref_id");
	     String remarks = (String) payload.get("remarks");
	     int userId = Integer.parseInt(payload.get("userId").toString());
	     int meetingid = Integer.parseInt(payload.get("meetingid").toString());

	     List<Map<String, Object>> decisions = (List<Map<String, Object>>) payload.get("decisions");

	     return pendinglistService.saveCommittee(refId, remarks, meetingid, userId, decisions);
	 }

	 @PostMapping("/savereinspection")
	 public Map<String, Object> saveReInspection(@RequestBody Map<String, Object> payload) {

	     String refId = payload.get("ref_id").toString();
	     int committeeAction = Integer.parseInt(payload.get("committee_action").toString());
	     int meetingId = Integer.parseInt(payload.get("meeting_id").toString());
	     String remarks = payload.get("remarks") != null ? payload.get("remarks").toString() : null;
	     int userId = Integer.parseInt(payload.get("userId").toString());

	     return pendinglistService.saveReInspection(refId, committeeAction, meetingId, remarks, userId);
	 }

	 
}
