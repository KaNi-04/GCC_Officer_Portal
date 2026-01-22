package in.gov.chennaicorporation.gccoffice.callcenterqaqc.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
//import java.util.stream.Stream;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.gov.chennaicorporation.gccoffice.callcenterqaqc.service.QaqcCallsService;
import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("gcc/api/callcenterqaqc/agent/qaqccalls")
@RestController
public class QaqcCallsController {

	private final RestTemplate restTemplate;
	private final AppConfig appConfig;

	@Autowired
	public QaqcCallsController(RestTemplate restTemplate, AppConfig appConfig) {
		this.restTemplate = restTemplate;
		this.appConfig = appConfig;
	}

	@Autowired
	private QaqcCallsService qaqcCallsService;

	@PostMapping("/update")
	@ResponseBody
	public ResponseEntity<String> updateCallDetails(@RequestParam("complaintNumber") String complaintNumber,
			@RequestParam("dataId") int dataId, @RequestParam("action") String action,
			@RequestParam("userId") String userId,
			@RequestParam(value = "remainderDate", required = false, defaultValue = "-") String remainderDate,
			@RequestParam("remarks") String remarks) {
		try {
//			System.out.println("Updating Complaint Number: " + complaintNumber);
//			System.out.println("Updating Data ID: " + dataId);
//			System.out.println("Action: " + action);
//			System.out.println("Remarks: " + remarks);
			int updated_agent = Integer.parseInt(userId);
//			System.out.println("updated_agent: " + updated_agent);

		
//			  Map<String,Object> followUpComplaint =
//			  qaqcCallsService.checkComplaintDetail(complaintNumber);
//			  
//			  try { 
//				  int followupid = (int) followUpComplaint.get("id");
//			  // update complaintstaus if exist in followup call logs table 
//				  if (followUpComplaint != null &&!followUpComplaint.isEmpty())
//				  {
//					  qaqcCallsService.updateFollowupCallLogs(complaintNumber, action, remarks,followupid);
//				  }
//			  
//			  }
//			  catch(Exception e) 
//			  { 
//				  System.out.println("Exception:"+e);
//			 }
			  
			  //to insert incorrect office mobile number details in table
			  if(action.equals("MOBILE_NUM")) {
			  qaqcCallsService.saveMobileNumDetails(complaintNumber, remarks); }
			  
			  // Get complaint details 
			  Map<String, Object> complaintDetails =qaqcCallsService.getComplaintDetails(dataId);
			  
			  Integer agentId = (Integer) complaintDetails.get("agent_id");
			  Map<String,Object> erp_username=qaqcCallsService.getErpnameById(agentId); 
			  String Erpname=(String) erp_username.get("erp_username");
			  
			  // Handle COMPLETED and REOPEN actions
			  			
			  if ("COMPLETED".equals(action) || "REOPEN".equals(action) ||"WRONG_ASSIGN".equals(action))
			  { 
				  boolean statusUpdated =qaqcCallsService.ChangeStatusInErp(complaintNumber, remarks, action,Erpname);
				  System.out.println("statusUpdated="+statusUpdated); 
					if (!statusUpdated)
					{
						return ResponseEntity.ok("error");
					}
			  }
			  
			  
			  // Execute common update methods
			  qaqcCallsService.updateComplaintDetails(complaintNumber, dataId, action,remarks,updated_agent); 
			  qaqcCallsService.updateComplaintHistoryDetails(complaintNumber,dataId, action, remarks,updated_agent, remainderDate); 
			  Map<String, Object> logDetails =qaqcCallsService.getComplaintDetails(dataId);
			  qaqcCallsService.uploadComplaintDetailsInLogs(logDetails, action,complaintNumber, remainderDate);
			 

			return ResponseEntity.ok("Success");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
		}
	}

	@GetMapping("/getComplaintDetails")
	public Map<String, Object> getComplaintDetails(@RequestParam String complaintNumber) {

		Map<String, Object> resultMap = new HashMap<>();

		try {
			String url = "https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=getComplaintByID&ComplaintId="
					+ complaintNumber + "&imgUrlonly=yes&isQcuser=Yes&jsonResp=Yes";
			System.out.println("Request URL: " + url);

			ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				String responseBody = responseEntity.getBody();
				//System.out.println("Response Body: " + responseBody);

				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(responseBody);

				JsonNode complaintDetails = rootNode.path(0).path("ComplaintListHistory").path(0);

				String registerDate = complaintDetails.path("complaintopendate").asText("N/A");
				String ClosedDate = complaintDetails.path("complaintcloseddate").asText("N/A");
				String ReopenDate = complaintDetails.path("REOPENDDATE").asText("N/A");
				String FinalcloseDate = complaintDetails.path("FINALCLOSEDDATE").asText("N/A");
				
				//System.out.println("registerdate=====" + registerDate);

				String formattedDate = "N/A"; // Default value if parsing fails

				if (!registerDate.equals("N/A") && !registerDate.isEmpty()) {
					try {
						// Parse the input date string
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
						Date date = inputFormat.parse(registerDate);

						// Format the date to dd-MM-yyyy
						SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
						formattedDate = outputFormat.format(date);
					} catch (Exception e) {
						e.printStackTrace(); // Log exception for debugging
					}
				}

				/*
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		        
		        LocalDateTime maxDate = Stream.of(registerDate, ClosedDate, ReopenDate, FinalcloseDate)
		                .filter(date -> date != null && !date.equalsIgnoreCase("null"))
		                .map(date -> {
		                    try {
		                        return LocalDateTime.parse(date, inputFormatter);
		                    } catch (DateTimeParseException e) {
		                        return null; // Skip invalid date formats
		                    }
		                })
		                .filter(Objects::nonNull)
		                .max(LocalDateTime::compareTo) // Find the greatest date
		                .orElse(null); // Default to null if no valid dates are found
		        
		        String greatestDateFormatted = (maxDate != null) ? maxDate.format(outputFormatter) : "N/A";
		        */
		        
				SimpleDateFormat inputFormatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
				SimpleDateFormat outputFormatter = new SimpleDateFormat("dd-MM-yyyy");

				Date maxDate = null;

				// Put all date strings into array
				String[] dates = { registerDate, ClosedDate, ReopenDate, FinalcloseDate };

				for (int i = 0; i < dates.length; i++) {

				    String dateStr = dates[i];

				    if (dateStr == null || "null".equalsIgnoreCase(dateStr)) {
				        continue;
				    }

				    try {
				        Date parsedDate = inputFormatter.parse(dateStr);

				        if (maxDate == null || parsedDate.after(maxDate)) {
				            maxDate = parsedDate;
				        }

				    } catch (ParseException e) {
				        // Ignore invalid date formats
				    }
				}

				String greatestDateFormatted =
				        (maxDate != null) ? outputFormatter.format(maxDate) : "N/A";

				if (!complaintDetails.isMissingNode()) {
					// Existing fields
					resultMap.put("complaintid", complaintDetails.path("complaintid").asText("N/A"));
					resultMap.put("complaintdate", formattedDate);
					resultMap.put("complainantName", complaintDetails.path("CustomerName").asText("N/A"));
					resultMap.put("mobileNumber", complaintDetails.path("CustomerMobile").asText("N/A"));
					resultMap.put("complaintType", complaintDetails.path("complainttype").asText("N/A"));
					resultMap.put("description", complaintDetails.path("complaintdetails").asText("N/A"));
					resultMap.put("currentStatus", complaintDetails.path("complaintcurrentstatus").asText("N/A"));
					resultMap.put("OfficialMobile", complaintDetails.path("OfficialMobile").asText("N/A"));
					resultMap.put("Street", complaintDetails.path("Street").asText("N/A"));
					resultMap.put("Area", complaintDetails.path("Area").asText("N/A"));
					resultMap.put("Landmark", complaintDetails.path("Landmark").asText("N/A"));
					resultMap.put("Location", complaintDetails.path("Location").asText("N/A"));
					resultMap.put("Zone", complaintDetails.path("Zone").asText("N/A"));
					resultMap.put("Division", complaintDetails.path("Division").asText("N/A"));
					resultMap.put("gender", complaintDetails.path("GENDER").asText("N/A"));
					resultMap.put("deptname", complaintDetails.path("DEPT_NAME").asText("N/A"));
					resultMap.put("compmode", complaintDetails.path("COMP_MODE").asText("N/A"));
					resultMap.put("greatestDate", greatestDateFormatted);

					// Comments section
					JsonNode comments = complaintDetails.path("Comments");
					if (!comments.isMissingNode()) {
						List<Map<String, Object>> commentList = new ArrayList<>();
						for (JsonNode commentNode : comments) {
							Map<String, Object> commentData = new HashMap<>();

							// Extracting comment details
							commentData.put("user", commentNode.path("user").asText("N/A"));
							commentData.put("message", commentNode.path("message").asText("N/A"));
							commentData.put("comment_date", commentNode.path("comment_date").asText("N/A"));

							// Handling media link (Image) as part of userimages array for each comment
							JsonNode userImages = commentNode.path("userimages");
							if (!userImages.isMissingNode() && userImages.isArray() && userImages.size() > 0) {
								String mediaLink = userImages.get(0).path("Comp_Image").asText(null);
								// If media link is valid, add it to the comment data
								if (mediaLink != null && !mediaLink.equals("null") && !mediaLink.isEmpty()) {
									commentData.put("Comp_Image", mediaLink);
								} else {
									commentData.put("Comp_Image", "No image available");
								}
							} else {
								commentData.put("Comp_Image", "No image available");
							}

							// Add the comment data to the list
							commentList.add(commentData);
						}
						resultMap.put("comments", commentList);
					}

					// Handling media link (Image) as part of userimages array
					String mediaLink = complaintDetails.path("mediaLink").asText(null);
					List<Map<String, String>> userImagesList = new ArrayList<>();
					Map<String, String> imageMap = new HashMap<>();
					imageMap.put("Comp_Image", mediaLink != null && !mediaLink.equals("null") ? mediaLink : null);
					userImagesList.add(imageMap);
					resultMap.put("userimages", userImagesList);

					//System.out.println("Image URL from API: " + mediaLink);
				}
			} else {
				resultMap.put("error", "Failed to retrieve complaint details.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("error", "Error processing request");
		}

		return resultMap;
	}

	@GetMapping("/getComplaintDetailsByMobileNumber")
	public Map<String, Object> getComplaintDetailsByMobileNumber(@RequestParam String complaintMobileNumber,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam String complaintYear) {

//		System.out.println("Computed Start Date: " + startDate);
//		System.out.println("Computed End Date: " + endDate);
//		System.out.println("Computed Year: " + complaintYear);
//		System.out.println("Computed MobileNumber: " + complaintMobileNumber);

		Map<String, Object> resultMap = new HashMap<>();
		List<Map<String, Object>> complaintsList = new ArrayList<>();

		try {
			// Determine current year's starting date and today's date
			LocalDate currentDate = LocalDate.now();
			LocalDate yearStartDate = currentDate.withDayOfYear(1);

			// Set default dates if startDate or endDate are empty
			if (startDate == null || startDate.isEmpty()) {
				startDate = yearStartDate.toString(); // Set to current year's start date
			}
			if (endDate == null || endDate.isEmpty()) {
				endDate = currentDate.toString(); // Set to today's date
			}

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			startDate = LocalDate.parse(startDate, inputFormatter).format(outputFormatter);
			endDate = LocalDate.parse(endDate, inputFormatter).format(outputFormatter);

			// Debug logs (optional)
//			System.out.println("Formatted Start Date: " + startDate);
//			System.out.println("Formatted End Date: " + endDate);

			String url = "https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=getComplaintListWithImg&From_date="
					+ startDate + "&To_date=" + endDate
					+ "&jsonResp=Yes&UserType=Public&Token=&PageIndex=null&Status=null&repYear=" + complaintYear
					+ "&MobileNo=" + complaintMobileNumber;

			System.out.println("Request URL: " + url);

			ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				String responseBody = responseEntity.getBody();
				// System.out.println("Response Body: " + responseBody);

				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(responseBody);

				JsonNode complaintListHistory = rootNode.path(0).path("ComplaintListHistory");

				if (complaintListHistory.isArray()) {
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
					for (JsonNode complaintDetails : complaintListHistory) {
						Map<String, Object> complaintMap = new HashMap<>();

						String registerDate = complaintDetails.path("complaintopendate").asText("N/A");
		                Date parsedDate = null;

		                if (!registerDate.equals("N/A") && !registerDate.isEmpty() && !"null".equalsIgnoreCase(registerDate)) {
		                    try {
		                        parsedDate = inputFormat.parse(registerDate);
		                        String formattedDate = outputFormat.format(parsedDate);
		                        complaintMap.put("complaintdate", formattedDate);
		                    } catch (Exception e) {
		                        e.printStackTrace();
		                        complaintMap.put("complaintdate", "N/A");
		                    }
		                } else {
		                    complaintMap.put("complaintdate", "N/A");
		                }

						// Add complaint details to the map
						complaintMap.put("complaintid", complaintDetails.path("complaintid").asText("N/A"));
						complaintMap.put("complainantName", complaintDetails.path("CustomerName").asText("N/A"));
						complaintMap.put("mobileNumber", complaintDetails.path("CustomerMobile").asText("N/A"));
						complaintMap.put("complaintType", complaintDetails.path("complainttype").asText("N/A"));
						complaintMap.put("currentStatus",complaintDetails.path("complaintcurrentstatus").asText("N/A"));
						complaintMap.put("OfficialMobile", complaintDetails.path("OfficialMobile").asText("N/A"));
						complaintMap.put("Street", complaintDetails.path("Street").asText("N/A"));
						complaintMap.put("Area", complaintDetails.path("Area").asText("N/A"));
						complaintMap.put("Landmark", complaintDetails.path("Landmark").asText("N/A"));
						complaintMap.put("Location", complaintDetails.path("Location").asText("N/A"));
						complaintMap.put("Zone", complaintDetails.path("Zone").asText("N/A"));
						complaintMap.put("Division", complaintDetails.path("Division").asText("N/A"));
						String closedDate = complaintDetails.path("complaintcloseddate").asText("N/A");

						// Check if the value is explicitly "null" (as a string)
						if ("null".equalsIgnoreCase(closedDate)) {
							closedDate = "N/A";
						}

						complaintMap.put("complaintcloseddate", closedDate);
						complaintMap.put("parsedComplaintDate", parsedDate);

						// Add the complaint to the list
						complaintsList.add(complaintMap);
					}
					
					// Sort complaintsList by parsedComplaintDate in descending order
		            complaintsList.sort((a, b) -> {
		                Date dateA = (Date) a.get("parsedComplaintDate");
		                Date dateB = (Date) b.get("parsedComplaintDate");
		                if (dateA == null && dateB == null) return 0;
		                if (dateA == null) return 1;
		                if (dateB == null) return -1;
		                return dateB.compareTo(dateA);
		            });
		            
		            for (Map<String, Object> complaint : complaintsList) {
		                complaint.remove("parsedComplaintDate");
		            }
				}
				resultMap.put("status", "success");
				resultMap.put("complaints", complaintsList);
			} else {
				resultMap.put("status", "error");
				resultMap.put("message", "Failed to retrieve complaint details.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", "error");
			resultMap.put("message", "Error processing request");
		}

		return resultMap;
	}

	
	@GetMapping("/getqcsubmittedcalls")
	public List<Map<String, Object>> getSubmittedList(@RequestParam int userId)
	{
		return qaqcCallsService.getSubmitDetails(userId);
	}
	
	/*
	 * System.out.println("Computed Start Date: " + startDate);
	 * System.out.println("Computed End Date: " + endDate);
	 * System.out.println("Computed Year: " + complaintYear);
	 * System.out.println("Computed MobileNumber: " + complaintMobileNumber);
	 * 
	 * String url
	 * ="https://erp.chennaicorporation.gov.in/pgr/newmobileservice?serviceId=getComplaintListWithImg&From_date="
	 * +startDate+"&To_date="+endDate+
	 * "&jsonResp=Yes&UserType=Public&Token=&PageIndex=null&Status=null&repYear="+
	 * complaintYear+"&MobileNo="+complaintMobileNumber;
	 * 
	 * System.out.println("Request URL: " + url);
	 * 
	 */

}
