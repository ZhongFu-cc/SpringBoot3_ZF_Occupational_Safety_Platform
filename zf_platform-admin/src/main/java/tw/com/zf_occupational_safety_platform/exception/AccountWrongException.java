package tw.com.zf_occupational_safety_platform.exception;

public class AccountWrongException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AccountWrongException(String message) {
        super(message);
    }

}
