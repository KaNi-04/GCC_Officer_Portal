package in.gov.chennaicorporation.gccoffice.callcenter.controller;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.callcenter.service.CampaignDropdownApiService;

@RequestMapping("gcc/api")
@RestController
public class CampaignDropdownApiController {
	

	 @Autowired
	    private CampaignDropdownApiService dropdownApiService;
	 
	 @GetMapping("/getAnswerTypes")
		@ResponseBody
		public List<Map<String, Object>> getAnswerTypes() {
		    return dropdownApiService.getAnswerTypes();
		}
}
