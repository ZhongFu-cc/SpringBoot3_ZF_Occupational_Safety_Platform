package tw.com.zf_occupational_safety_platform.exception;

public class AccountPasswordWrongException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AccountPasswordWrongException(String message) {
        super(message);
    }

}
