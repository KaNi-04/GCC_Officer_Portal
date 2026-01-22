package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.StatusCountService;

@RequestMapping("gcc/api/callcenterqaqc")
@RestController
public class StatusCountController {

    @Autowired
	private StatusCountService statusCountService;


    @Autowired
    public StatusCountController(StatusCountService statusCountService) {
        this.statusCountService = statusCountService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(
            @RequestParam(value = "startDate", required = false, defaultValue = "") String fromDate,
            @RequestParam(value = "endDate", required = false, defaultValue = "") String toDate,
            @RequestParam(value = "dropdown1", required = false, defaultValue = "") String compType,
            @RequestParam(value = "dropdown2", required = false, defaultValue = "") String zoneName,
            @RequestParam(value = "dropdown3", required = false,defaultValue = "") String compMode,
            @RequestParam(value = "dropdown4", required = false, defaultValue = "") String compGroup,
            @RequestParam(value = "regiondropdown", required = false, defaultValue = "") String region) {
    	

        Map<String, Object> counts = statusCountService.fetchComplaintCounts(fromDate, toDate, compType, zoneName, compMode, compGroup,region);
        //System.out.println("counts:" + counts);
        //System.out.println("group:" + compGroup);
        return ResponseEntity.ok(counts);
    }
        

    @GetMapping("/getComplaintTypesByGroup")
    public ResponseEntity<List<String>> getComplaintTypesByGroup(@RequestParam String group) {
        List<String> complaintTypes = statusCountService.fetchComplaintTypes(group);
        return ResponseEntity.ok(complaintTypes);
    }


    @GetMapping("/getComplaintGroup")
    public ResponseEntity<List<String>> getComplaintGroup() {
        List<String> complaintGroup= statusCountService.fetchComplaintGroup();
        //System.out.println("types:"+complaintTypes);
        return ResponseEntity.ok(complaintGroup);
    }
    
    
    @GetMapping("/getModes")
    public ResponseEntity<List<String>> getModes() {
        List<String> modes= statusCountService.fetchModes();
        //System.out.println("types:"+modes);
        return ResponseEntity.ok(modes);
    }
    
    
    @GetMapping("/getZones")
    public ResponseEntity<List<String>> getZones() {
        List<String> zones= statusCountService.fetchZones();
        //System.out.println("types:"+zones);
        return ResponseEntity.ok(zones);
    }
    
    
    
    
    

}
