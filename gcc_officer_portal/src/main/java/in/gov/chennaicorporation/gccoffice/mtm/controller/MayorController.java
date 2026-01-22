package in.gov.chennaicorporation.gccoffice.mtm.controller;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;
import in.gov.chennaicorporation.gccoffice.mtm.data.MTMOfficer;
import in.gov.chennaicorporation.gccoffice.mtm.data.MtmEvent;
import in.gov.chennaicorporation.gccoffice.mtm.data.Petition;
import in.gov.chennaicorporation.gccoffice.mtm.data.PetitionComplaint;
import in.gov.chennaicorporation.gccoffice.service.LoginUserInfo;

@RequestMapping("/gcc/mtm/mayor")
@Controller("mtmMayorController")
public class MayorController {
	
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	private final LoginUserInfo loginUserInfo;
	private final String basePath = "modules/mtm/mayor";
	
	@Autowired
    public MayorController(RestTemplate restTemplate, AppConfig appConfig, LoginUserInfo loginUserInfo) {
        this.restTemplate = restTemplate;
        this.loginUserInfo = loginUserInfo;
        this.appConfig = appConfig;
    }
	
	@GetMapping({"", "/", "/index", "/dashboard"})
	public String home(Model model) {
		return basePath+"/index";
	}
	
	@GetMapping({"/petition-list"})
	public String showPetitionList(Model model,HttpSession session) {
		
		String eventid = "0";
		
		if(session.getAttribute("eventid")!= null && !session.getAttribute("eventid").equals("0")){
			eventid = session.getAttribute("eventid").toString();
		}
		String baseUrl = appConfig.mtm + "/api/mayor/petition-list";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("mtmid",eventid);
    	
    	Petition[] petitionList = restTemplate.getForObject(builder.toUriString(), Petition[].class);
    	model.addAttribute("petitionlist",petitionList);
    	
    	baseUrl = appConfig.mtm + "/api/event-list";
    	MtmEvent[] mtmEventsList = restTemplate.getForObject(baseUrl, MtmEvent[].class);
    	model.addAttribute("mtmEventsList",mtmEventsList);
    	/*
    	baseUrl = appConfig.mtm + "/api/complainttypes";
    	MtmComplaintTypes[] mtmComplaintTypesList = restTemplate.getForObject(baseUrl, MtmComplaintTypes[].class);
    	model.addAttribute("complaintTypesList",mtmComplaintTypesList);
    	
    	//System.out.println("Length: "+mtmComplaintTypesList.length);	
    	 */
		return basePath+"/petition-list";
	}
	
	@GetMapping({"/completed-petition-list"})
	public String showCompletedPetitionList(Model model,HttpSession session) {
		
		String eventid = "0";
		
		if(session.getAttribute("eventid")!= null && !session.getAttribute("eventid").equals("0")){
			eventid = session.getAttribute("eventid").toString();
		}
		String baseUrl = appConfig.mtm + "/api/mayor/completed-petition-list";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("mtmid",eventid);
    	
    	Petition[] petitionList = restTemplate.getForObject(builder.toUriString(), Petition[].class);
    	model.addAttribute("petitionlist",petitionList);
    	
    	baseUrl = appConfig.mtm + "/api/event-list";
    	MtmEvent[] mtmEventsList = restTemplate.getForObject(baseUrl, MtmEvent[].class);
    	model.addAttribute("mtmEventsList",mtmEventsList);
    	/*
    	baseUrl = appConfig.mtm + "/api/complainttypes";
    	MtmComplaintTypes[] mtmComplaintTypesList = restTemplate.getForObject(baseUrl, MtmComplaintTypes[].class);
    	model.addAttribute("complaintTypesList",mtmComplaintTypesList);
    	
    	//System.out.println("Length: "+mtmComplaintTypesList.length);
    	 */
		return basePath+"/completed-petition-list";
	}
	
	@GetMapping({"/partial-petition-list"})
	public String showPartialPetitionList(Model model,HttpSession session) {
		
		String eventid = "0";
		
		if(session.getAttribute("eventid")!= null && !session.getAttribute("eventid").equals("0")){
			eventid = session.getAttribute("eventid").toString();
		}
		String baseUrl = appConfig.mtm + "/api/mayor/partial-petition-list";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("mtmid",eventid);
    	
    	Petition[] petitionList = restTemplate.getForObject(builder.toUriString(), Petition[].class);
    	model.addAttribute("petitionlist",petitionList);
    	
    	baseUrl = appConfig.mtm + "/api/event-list";
    	MtmEvent[] mtmEventsList = restTemplate.getForObject(baseUrl, MtmEvent[].class);
    	model.addAttribute("mtmEventsList",mtmEventsList);
    	/*
    	baseUrl = appConfig.mtm + "/api/complainttypes";
    	MtmComplaintTypes[] mtmComplaintTypesList = restTemplate.getForObject(baseUrl, MtmComplaintTypes[].class);
    	model.addAttribute("complaintTypesList",mtmComplaintTypesList);
    	
    	//System.out.println("Length: "+mtmComplaintTypesList.length);	
    	 
    	 */
		return basePath+"/partial-petition-list";
	}
	
	@GetMapping({"/pending-petition-list"})
	public String showPendingPetitionList(Model model,HttpSession session) {
		
		String eventid = "0";
		
		if(session.getAttribute("eventid")!= null && !session.getAttribute("eventid").equals("0")){
			eventid = session.getAttribute("eventid").toString();
		}
		String baseUrl = appConfig.mtm + "/api/mayor/pending-petition-list";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("mtmid",eventid);
    	
    	Petition[] petitionList = restTemplate.getForObject(builder.toUriString(), Petition[].class);
    	model.addAttribute("petitionlist",petitionList);
    	
    	baseUrl = appConfig.mtm + "/api/event-list";
    	MtmEvent[] mtmEventsList = restTemplate.getForObject(baseUrl, MtmEvent[].class);
    	model.addAttribute("mtmEventsList",mtmEventsList);
    	/*
    	baseUrl = appConfig.mtm + "/api/complainttypes";
    	MtmComplaintTypes[] mtmComplaintTypesList = restTemplate.getForObject(baseUrl, MtmComplaintTypes[].class);
    	model.addAttribute("complaintTypesList",mtmComplaintTypesList);
    	
    	//System.out.println("Length: "+mtmComplaintTypesList.length);
    	 * 	
    	 */
		return basePath+"/pending-petition-list";
	}
	
	@GetMapping({"/complaint-list"})
	public String showComplaintList(Model model,HttpSession session) {
		
		String eventid = "0";
		
		if(session.getAttribute("eventid")!= null && !session.getAttribute("eventid").equals("0")){
			eventid = session.getAttribute("eventid").toString();
		}
		String baseUrl = appConfig.mtm + "/api/mayor/complaint-list";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("mtmid",eventid);
    	PetitionComplaint[] petitionList = restTemplate.getForObject(builder.toUriString(), PetitionComplaint[].class);
    	model.addAttribute("petitionlist",petitionList);
    	
    	baseUrl = appConfig.mtm + "/api/event-list";
    	MtmEvent[] mtmEventsList = restTemplate.getForObject(baseUrl, MtmEvent[].class);
    	model.addAttribute("mtmEventsList",mtmEventsList);
    	/*
    	baseUrl = appConfig.mtm + "/api/complainttypes";
    	MtmComplaintTypes[] mtmComplaintTypesList = restTemplate.getForObject(baseUrl, MtmComplaintTypes[].class);
    	model.addAttribute("complaintTypesList",mtmComplaintTypesList);
    	*/
    	
		return basePath+"/complaint-list";
	}
	
	@GetMapping({"/completed-complaint-list"})
	public String showCompletedComplaintList(Model model,HttpSession session) {
		
		String eventid = "0";
		
		if(session.getAttribute("eventid")!= null && !session.getAttribute("eventid").equals("0")){
			eventid = session.getAttribute("eventid").toString();
		}
		String baseUrl = appConfig.mtm + "/api/mayor/completed-complaint-list";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("mtmid",eventid);
    	PetitionComplaint[] petitionList = restTemplate.getForObject(builder.toUriString(), PetitionComplaint[].class);
    	model.addAttribute("petitionlist",petitionList);
    	
    	baseUrl = appConfig.mtm + "/api/event-list";
    	MtmEvent[] mtmEventsList = restTemplate.getForObject(baseUrl, MtmEvent[].class);
    	model.addAttribute("mtmEventsList",mtmEventsList);
    	/*
    	baseUrl = appConfig.mtm + "/api/complainttypes";
    	MtmComplaintTypes[] mtmComplaintTypesList = restTemplate.getForObject(baseUrl, MtmComplaintTypes[].class);
    	model.addAttribute("complaintTypesList",mtmComplaintTypesList);
    	*/
    	
		return basePath+"/completed-complaint-list";
	}
	
	@GetMapping({"/pending-complaint-list"})
	public String showPendingComplaintList(Model model,HttpSession session) {
		
		String eventid = "0";
		
		if(session.getAttribute("eventid")!= null && !session.getAttribute("eventid").equals("0")){
			eventid = session.getAttribute("eventid").toString();
		}
		String baseUrl = appConfig.mtm + "/api/mayor/pending-complaint-list";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("mtmid",eventid);
    	PetitionComplaint[] petitionList = restTemplate.getForObject(builder.toUriString(), PetitionComplaint[].class);
    	model.addAttribute("petitionlist",petitionList);
    	
    	baseUrl = appConfig.mtm + "/api/event-list";
    	MtmEvent[] mtmEventsList = restTemplate.getForObject(baseUrl, MtmEvent[].class);
    	model.addAttribute("mtmEventsList",mtmEventsList);
    	/*
    	baseUrl = appConfig.mtm + "/api/complainttypes";
    	MtmComplaintTypes[] mtmComplaintTypesList = restTemplate.getForObject(baseUrl, MtmComplaintTypes[].class);
    	model.addAttribute("complaintTypesList",mtmComplaintTypesList);
    	*/
		return basePath+"/completed-complaint-list";
	}
	
	@GetMapping({"/by-event-list"})
	public String showEventListReport(Model model,HttpSession session) {
		
		String eventid = "0";
		
		if(session.getAttribute("eventid")!= null && !session.getAttribute("eventid").equals("0")){
			eventid = session.getAttribute("eventid").toString();
		}
		String baseUrl = appConfig.mtm + "/api/mayor/by-event-list";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("mtmid",eventid);
    	MtmEvent[] mtmEventsTableList = restTemplate.getForObject(builder.toUriString(), MtmEvent[].class);
    	model.addAttribute("mtmEventsTableList",mtmEventsTableList);
    	
    	baseUrl = appConfig.mtm + "/api/event-list";
    	MtmEvent[] mtmEventsList = restTemplate.getForObject(baseUrl, MtmEvent[].class);
    	model.addAttribute("mtmEventsList",mtmEventsList);
    	
		return basePath+"/by-event-list";
	}
	
	@GetMapping({"/by-officer-list"})
	public String showOfficerListReport(Model model,HttpSession session) {
		
		String eventid = "0";
		
		if(session.getAttribute("eventid")!= null && !session.getAttribute("eventid").equals("0")){
			eventid = session.getAttribute("eventid").toString();
		}
		String baseUrl = appConfig.mtm + "/api/mayor/by-officer-list";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
    			.queryParam("mtmid",eventid);
    	MTMOfficer[] officerTableList = restTemplate.getForObject(builder.toUriString(), MTMOfficer[].class);
    	
    	model.addAttribute("officerTableList",officerTableList);
    	
    	baseUrl = appConfig.mtm + "/api/event-list";
    	MtmEvent[] mtmEventsList = restTemplate.getForObject(baseUrl, MtmEvent[].class);
    	model.addAttribute("mtmEventsList",mtmEventsList);
    	
		return basePath+"/by-officer-list";
	}
}