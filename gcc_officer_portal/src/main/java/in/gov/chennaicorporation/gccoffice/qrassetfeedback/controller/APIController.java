package in.gov.chennaicorporation.gccoffice.qrassetfeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.gov.chennaicorporation.gccoffice.configuration.AppConfig;

@RequestMapping("/gcc/api/qrfeedback")
@RestController("qrassetfeedbackcontroller")
public class APIController {
	private final RestTemplate restTemplate;
	private final AppConfig appConfig;
	String bodyMessage = "";
	
	@Autowired
    public APIController(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }
	@GetMapping(value = "/getFeedbackAssetdetails")
	public String getFeedbackAssetdetails(@RequestParam String fbId) {
		String feedbackId = fbId;
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.qrAssetFeedback+"/api/feedback/{feedbackId}",String.class,feedbackId);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	@GetMapping(value = "/getFeedbackData")
	public String getFeedbackData(@RequestParam String fbId) {
		String feedbackId = fbId;
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.qrAssetFeedback+"/api/feedback/data/{feedbackId}",String.class,feedbackId);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	@GetMapping(value = "/dashboard-summary")
	public String getDashboardSummary() {
		System.out.print("hi dashboard-summary");
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.qrAssetFeedback+"/api/dashboard-summary",String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
	}
	
	@PostMapping(value = "/category/checkbyname")
	public int checkByName(@RequestParam("catName") String catName) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("catName", catName);
	    
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	    
	    ResponseEntity<Integer> response = restTemplate.postForEntity(appConfig.qrAssetFeedback + "/api/category/checkbyname", request, Integer.class);
	    
	    return response.getBody();
	}
	
	@PostMapping(value = "/category/edit/checkbyname")
	public int editCheckByName(@RequestParam("catName") String catName,@RequestParam("cId") String cId) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("catName", catName);
	    params.add("cId", cId);
	    
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	    
	    ResponseEntity<Integer> response = restTemplate.postForEntity(appConfig.qrAssetFeedback + "/api/category/edit/checkbyname", request, Integer.class);
	    
	    return response.getBody();
	}
	
	@PostMapping(value = "/category/{id}")
	public String getCategoryById(@PathVariable("id") int id) {
		ResponseEntity<String> response = restTemplate.getForEntity(appConfig.qrAssetFeedback+"/api/category/"+id,String.class);
        bodyMessage = response.getBody().trim();
	    return bodyMessage;
	}
	
	@PostMapping(value = "/saveCategory")
	public int saveCategory(@RequestParam("catName") String catName, @RequestParam("isActive") String isActive) {
		
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("catName", catName);
	    params.add("isActive", isActive);
	    
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	    
	    ResponseEntity<Integer> response = restTemplate.postForEntity(appConfig.qrAssetFeedback + "/api/category/save", request, Integer.class);
	    
	    return response.getBody();
	}
	
	@PostMapping(value = "/updateCategory")
	public int updateCategory(@RequestParam("catName") String catName, @RequestParam("isActive") String isActive, @RequestParam("cId") String cId) {
		//System.out.println(cId);
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("catName", catName);
	    params.add("isActive", isActive);
	    
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	    
	    ResponseEntity<Integer> response = restTemplate.postForEntity(appConfig.qrAssetFeedback + "/api/category/"+cId+"/update", request, Integer.class);
	    
	    return response.getBody();
	}
	
	@GetMapping("/category/{id}/delete")
    public String deleteCategory(@PathVariable("id") int id) {
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.qrAssetFeedback+"/api/category/{id}/delete",String.class,id);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
    }
	
	
	@PostMapping(value = "/category/question/{id}")
	public String getCategoryQuestionById(@PathVariable("id") int id) {
		ResponseEntity<String> response = restTemplate.getForEntity(appConfig.qrAssetFeedback+"/api/category/question/"+id,String.class);
        bodyMessage = response.getBody().trim();
	    return bodyMessage;
	}
	
	@GetMapping("/category/question/{id}/delete")
    public String deleteCategoryQuestion(@PathVariable("id") int id) {
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.qrAssetFeedback+"/api/category/question/{id}/delete",String.class,id);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
    }
	
	@PostMapping(value = "/saveCategoryQuestion")
	public int saveCategoryQuestion(@RequestParam("assetCategory") String assetCategory,@RequestParam("engQuestion") String engQuestion,@RequestParam("tamilQuestion") String tamilQuestion, @RequestParam("isActive") String isActive) {
		
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("assetCategory", assetCategory);
	    params.add("engQuestion", engQuestion);
	    params.add("tamilQuestion", tamilQuestion);
	    params.add("isActive", isActive);
	    
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	    
	    ResponseEntity<Integer> response = restTemplate.postForEntity(appConfig.qrAssetFeedback + "/api/category/question/save", request, Integer.class);
	    
	    return response.getBody();
	}
	
	@PostMapping(value = "/updateCategoryQuestion")
	public int updateCategoryQuestion(@RequestParam("cmqId") String cmqId,@RequestParam("assetCategory") String assetCategory,@RequestParam("engQuestion") String engQuestion,@RequestParam("tamilQuestion") String tamilQuestion, @RequestParam("isActive") String isActive) {
		    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("assetCategory", assetCategory);
	    params.add("engQuestion", engQuestion);
	    params.add("tamilQuestion", tamilQuestion);
	    params.add("isActive", isActive);
	    
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	    
	    ResponseEntity<Integer> response = restTemplate.postForEntity(appConfig.qrAssetFeedback + "/api/category/question/"+cmqId+"/update", request, Integer.class);
	    
	    return response.getBody();
	}
	
	@GetMapping(value = "/getComplaintCategory")
	public String getComplaintCategory() {
        ResponseEntity<String> response = restTemplate.getForEntity(appConfig.qrAssetFeedback+"/api/getComplaintCategory",String.class);
        bodyMessage = response.getBody().trim();
        return bodyMessage;
    }
	@PostMapping(value = "/getComplaintCategoryById")
    public String getComplaintCategoryById(@RequestParam String comGroupId) {
		
        
        HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("comGroupId", comGroupId);
	    
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	    
	    ResponseEntity<String> response = restTemplate.postForEntity(appConfig.qrAssetFeedback+"/api/getComplaintCategoryById",request,String.class);
        bodyMessage = response.getBody().trim();
	    
	    return bodyMessage;
	}
}
