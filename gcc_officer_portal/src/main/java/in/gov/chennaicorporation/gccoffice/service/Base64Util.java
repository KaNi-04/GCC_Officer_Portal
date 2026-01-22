package in.gov.chennaicorporation.gccoffice.service;

import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Base64Util {

    // Encode to Base64
	public static String encodeBase64(String value) {
        if (value != null && !value.isEmpty()) {
            byte[] encodedBytes = Base64.getEncoder().encode(value.getBytes());
            // Print the original value and its encoded version
           // System.out.println(value + " : " + new String(encodedBytes)); 
            return new String(encodedBytes);
        } else {
            return "";
        }
    }

    // Decode from Base64
    public static String decodeBase64(String encodedValue) {
    	if (encodedValue != null && !encodedValue.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(encodedValue);
                return new String(decodedBytes);
            } catch (IllegalArgumentException e) {
                // Handle the exception, e.g., log it or return an error message
                System.err.println("Error decoding Base64 string: " + e.getMessage());
                return "error"; // Or return an error message string
            }
        } else {
            return "";
        }
    }
}
