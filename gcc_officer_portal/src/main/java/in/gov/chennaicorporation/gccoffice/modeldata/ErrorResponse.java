package in.gov.chennaicorporation.gccoffice.modeldata;

public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String exception;

    // Constructors, getters, and setters for the above fields
    
    public ErrorResponse(int status, String error, String message, String exception) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.exception = exception;
    }

	public ErrorResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

}
