package in.gov.chennaicorporation.gccoffice.callcenter.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.callcenter.service.CampaignReportService;

@RestController
@RequestMapping("gcc/api/campaignreport")
public class CampaignReportAPIController {
	
	@Autowired
	 private CampaignReportService campaignReportService;
	
    @GetMapping("/getcampaignData")
    public List<Map<String, Object>> getCampaignData(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {
        
        List<Map<String, Object>> campaignData = campaignReportService.getCampaignData(startDate, endDate);
        //System.out.println("Campaign Data: " + campaignData); // Debugging log
        return campaignData;
    }
    
    @GetMapping("/getagentdatacount")
    public List<Map<String, Object>> getAgentwiseData(@RequestParam String campaign_id) {
        List<Map<String, Object>> agentData = campaignReportService.getAgentwiseData(campaign_id);
        //System.out.println("Agent Data: " + agentData);
        return agentData;
    }
    
    @GetMapping("/getReportCampaignid")
    public ResponseEntity<?> getAgentReportCampaignId(@RequestParam String campaign_id) {
         
        List<Map<String, Object>> agentData = campaignReportService.getAgentReportCampaignId(campaign_id);
        //System.out.println("Agent Data: " + agentData);
        return ResponseEntity.ok(agentData);
    }
    
    //question and answers report page api
    @GetMapping("/question-answers")
    public ResponseEntity<?> getQuestionAnswers(@RequestParam int campaign_id) {
        List<Map<String, Object>> response = campaignReportService.getQuestionAnswers(campaign_id);
        return ResponseEntity.ok(response);
    }
//    @GetMapping("/download-excel")
//    public void downloadFilteredCSV(
//            @RequestParam int categoryId,
//            @RequestParam int campaignId,
//            
//            
//            
//            HttpServletResponse response) throws IOException {
//
//        // Set response headers
//        response.setContentType("text/csv");
//        response.setHeader("Content-Disposition", "attachment; filename=filtered_data.csv");
//
//        // Generate CSV and write to response
//        PrintWriter writer = response.getWriter();
//        campaignReportService.generateCSV(categoryId, campaignId, writer);
//        writer.flush();
//        writer.close();
//    }

    
    @GetMapping("/download-excel")
    public void downloadFilteredCSV(
            @RequestParam int categoryId,
            @RequestParam int campaignId,
            HttpServletResponse response) throws IOException {

        String filename = campaignReportService.getCampaignName(campaignId);
        // Format campaign name for file safety (remove spaces/special characters)
        //String safeCampaignName = filename.replaceAll("[^a-zA-Z0-9]", "_");

        // Set response headers
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".csv");

        // Generate CSV and write to response
        PrintWriter writer = response.getWriter();
        campaignReportService.generateCSV(categoryId, campaignId, writer);
        writer.flush();
        writer.close();
    }


}
