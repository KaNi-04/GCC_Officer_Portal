package in.gov.chennaicorporation.gccoffice.pensioner.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import in.gov.chennaicorporation.gccoffice.pensioner.service.DropdownService;

@RequestMapping("gcc/api")
@Controller
public class DropdownApiController {
	
	@Autowired
	private DropdownService dropdownService;
	
	@GetMapping("/getRetrimentTypes")
	@ResponseBody
	public List<Map<String, Object>> getRetrimentTypes() {
	    return dropdownService.getRetrimentTypes();
	}
	
	@GetMapping("/getCategoryTypes")
	@ResponseBody
	public List<Map<String, Object>> getCategoryTypes() {
	    return dropdownService.getCategoryTypes();
	}
	
	@GetMapping("/getProvisionalReasonTypes")
	@ResponseBody
	public List<Map<String, Object>> getProvisionalReasonTypes() {
	    return dropdownService.getProvisionalReasonTypes();
	}
	
	
	@GetMapping("/getGisEntryTypes")
	@ResponseBody
	public List<Map<String, Object>> getGisEntryTypes() {
	    return dropdownService.getGisEntryTypes();
	}

}
