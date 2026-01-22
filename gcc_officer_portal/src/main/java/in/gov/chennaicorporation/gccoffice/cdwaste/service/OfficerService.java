package in.gov.chennaicorporation.gccoffice.cdwaste.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class OfficerService {
	
    @Autowired
	private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private  RestTemplate restTemplate;
    
    @Autowired 
    private SMSService smsService;
	
  @Autowired
	public void setDataSourcemysql(@Qualifier("mysqlCDManagerSystemDataSource") DataSource dataSource) {		
		this.jdbcTemplate = new JdbcTemplate(dataSource);	
		}
 

  @Transactional
  public void saveRequestDetails(Map<String, Object> payload) {
	    double actualWeight = Double.parseDouble(payload.get("actualWeight").toString());
	    String paymentType = payload.get("paymentType").toString();
		/*
		 * double processingCharge =
		 * Double.parseDouble(payload.get("processingCharge").toString()); double
		 * wasteCharge = Double.parseDouble(payload.get("wasteCharge").toString());
		 */
	    
	    double processingCharge = payload.get("processingCharge") != null
	    	    ? Double.parseDouble(payload.get("processingCharge").toString())
	    	    : 0.0;

	    	double wasteCharge = payload.get("wasteCharge") != null
	    	    ? Double.parseDouble(payload.get("wasteCharge").toString())
	    	    : 0.0;
	    double totalCharge = Double.parseDouble(payload.get("totalCharge").toString());
	    String paymentMobNo = payload.get("paymentMobileNo") != null ? payload.get("paymentMobileNo").toString() : null;
	    String refId = payload.get("refId") != null ? payload.get("refId").toString() : null;

	    // ✅ Check if record exists
	    String checkSql = "SELECT COUNT(*) FROM waste_collectors_request WHERE refid = ?";
	    Integer count = jdbcTemplate.queryForObject(checkSql, new Object[]{refId}, Integer.class);

	    if (count != null && count > 0) {
	        // ✅ Update the existing record
	        String updateSql = "UPDATE waste_collectors_request SET actual_weight = ?, payment_type = ?, processing_charge = ?, waste_collection_charge = ?, total_charge = ?, payment_mobile_no = ? WHERE refid = ?";
	        jdbcTemplate.update(updateSql, actualWeight, paymentType, processingCharge, wasteCharge, totalCharge, paymentMobNo, refId);

	        // ✅ Send WhatsApp message if payment type is 'generateLink' and mobile number is provided
	        if ("generateLink".equalsIgnoreCase(paymentType) && paymentMobNo != null && !paymentMobNo.isBlank()) {
	            sendWhatsappMessage(refId, actualWeight, totalCharge, paymentMobNo);
	        }
	    } else {
	        throw new RuntimeException("No request found for Ref ID: " + refId);
	    }
	}


  
  public void sendWhatsappMessage(String refId, double actualWeight, double totalCharge, String mobileNumber) {
	    try {
	        String message = String.format(
	            "Your request (Request ID: *%s*) has been processed. Please proceed with the payment.\n\nActual Weight: *%.0f kg*\nAmount Payable: *%.0f*",
	            refId, actualWeight, totalCharge
	        );

	        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
	        String encodedHeader = URLEncoder.encode("C&D Waste Collection – Payment Request", StandardCharsets.UTF_8);
	        String encodedFooter = URLEncoder.encode("Greater Chennai Corporation", StandardCharsets.UTF_8);

	        String redirectUrlParam = URLEncoder.encode("dumpregistration/paymentform?requestId=" + refId, StandardCharsets.UTF_8);

	        String apiUrl = "https://media.smsgupshup.com/GatewayAPI/rest?userid=2000233507" +
	                        "&password=h2YjFNcJ" +
	                        "&send_to=" + mobileNumber +
	                        "&v=1.1&format=json&msg_type=TEXT&method=SENDMESSAGE" +
	                        "&msg=" + encodedMessage +
	                        "&isTemplate=true" +
	                        "&header=" + encodedHeader +
	                        "&footer=" + encodedFooter +
	                        "&buttonUrlParam=" + redirectUrlParam;

	        // Optional: log URL before hitting for debugging
	        System.out.println("WhatsApp API URL: " + apiUrl);

	        // Make API call
	     //   restTemplate.getForObject(apiUrl, String.class);
	        smsService.sendMsg(apiUrl);
	    } catch (Exception e) {
	        e.printStackTrace();
	        // Optionally log or alert failure
	    }
	}


  public List<Map<String, Object>> getAllRequestDetails(LocalDate fromDate, LocalDate toDate, String wastequantity, String zone) {
      StringBuilder query = new StringBuilder(
          "SELECT wcr.id, wcr.name, wcr.mobile_no, wcr.area, wcr.zone, qm.quantity, " +
          "wtm.waste_type, dl.location, wcr.actual_weight, wcr.payment_status,wcr.waste_collection_charge, " +
          "wcr.processing_charge, wcr.total_charge, DATE_FORMAT(wcr.cdate, '%d-%m-%Y') AS request_date, wcr.refid " +
          "FROM waste_collectors_request wcr " +
          "JOIN dumping_location dl ON wcr.dump_location_id = dl.id " +
          "JOIN waste_type_master wtm ON wcr.waste_type_id = wtm.id " +
          "JOIN quantity_master qm ON wcr.waste_quantity_id = qm.id " +
          "WHERE wcr.cdate BETWEEN ? AND ?");

      LocalDateTime fromDateTime = fromDate.atStartOfDay();
      LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

      List<Object> params = new ArrayList<>();
      params.add(fromDateTime);
      params.add(toDateTime);

      if (wastequantity != null && !wastequantity.isEmpty()) {
          query.append(" AND qm.quantity = ?");
          params.add(wastequantity);
      }

      if (zone != null && !zone.isEmpty()) {
          query.append(" AND wcr.zone = ?");
          params.add(zone);
      }

      return jdbcTemplate.queryForList(query.toString(), params.toArray());
  }
  
  
  
  
  public List<Map<String, Object>> getAllwastequantity() {
      String sql = " SELECT id,quantity FROM quantity_master where is_active = 1 ";
      return jdbcTemplate.queryForList(sql);
  }

  
  public List<Map<String, Object>> getZonesList() {
		String sql = " SELECT id,zone_name from zones where is_active = 1 ";
		return jdbcTemplate.queryForList(sql);
	}

}
