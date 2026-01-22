package in.gov.chennaicorporation.gccoffice.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import in.gov.chennaicorporation.gccoffice.modeldata.ErrorResponse;

@Controller
public class CustomErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";
    private static final Map<Integer, String> PAGE_NAME_MAP = new HashMap<>();
    
    public String getPageName(Integer statusCode) {
        if (statusCode != null) {
            return PAGE_NAME_MAP.getOrDefault(statusCode, "errorPage");
        } else {
            return "errorPage";
        }
    }
    
    // Handle errors for web applications, return HTML error pages
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String handleWebError(HttpServletRequest request, Model model) {
        // Retrieve error information from the request
       // HttpStatus status = HttpStatus.valueOf((Integer) request.getAttribute("javax.servlet.error.status_code"));
        //Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");

     // Retrieve error information from the request
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
        	// Add the error message to the model
            model.addAttribute("errorStatus", "Error 403");
            model.addAttribute("errorError", "403 - Access Denied");
            model.addAttribute("errorMessage", "Oops, You don`t have permission to access this page.");
            // Handle the case where the status code is null
            // You may choose to set default values or redirect to a generic error page
            return "error-403";
        }
        
        HttpStatus status = (statusCode != null) ? HttpStatus.valueOf(statusCode) : HttpStatus.INTERNAL_SERVER_ERROR;
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");

        // Add custom error handling logic for web applications here
        String errorPage = "error";
        Integer errorStatus = 404;
        String errorError = "Internal Server Error";
        String errorMessage = "An error occurred"; // Default error message
        
        String pageName = new CustomErrorController().getPageName(status.value());
        
        // Print error information to the console
        if (status != null) {
        	errorStatus = status.value();
            errorError = status.getReasonPhrase();
            
            System.err.println("HTTP Status: " + status +"\n"
            +"Status Code: "+status.value()+"\n"+ "Status Error: "
            +status.getReasonPhrase()
            +"Page Name: "+pageName);
            
            // Check if the status code
            if (status == HttpStatus.NOT_FOUND) {
                errorPage = "error-404"; // 400 Error Page
            }
            else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
                errorPage = "error-500"; // 500 Error page
            }
            else {
            	errorPage = "error"; //// Other Error page
            }
        }
        if (throwable != null) {
        	// Retrieve the exception message
            errorMessage = throwable.getMessage();
            throwable.printStackTrace();
        }
        
        // Add the error message to the model
        model.addAttribute("errorStatus", errorStatus);
        model.addAttribute("errorError", errorError);
        model.addAttribute("errorMessage", errorMessage);
        

        return errorPage; // Return the name of your custom HTML error page (without the .html extension)
    }

    // Handle errors for RESTful APIs, return JSON error responses
    @RequestMapping(value = ERROR_PATH, produces = "application/json")
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleApiError(HttpServletRequest request) {
        // Retrieve error information from the request
        HttpStatus status = HttpStatus.valueOf((Integer) request.getAttribute("javax.servlet.error.status_code"));
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");

        // Create an error response
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage("An error occurred");

        if (throwable != null) {
            errorResponse.setException(throwable.getMessage());
        }

        // Return a ResponseEntity with the custom JSON error response and HTTP status
        return ResponseEntity.status(status).body(errorResponse);
    }

    
    @GetMapping(ERROR_PATH)
    public String handleError() {
        return "error-404"; // Return the name of your custom error page (without the .html extension)
    }
}
