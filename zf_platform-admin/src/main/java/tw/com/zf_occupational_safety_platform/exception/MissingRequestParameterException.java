package tw.com.zf_occupational_safety_platform.exception;

/**
 * 自定義遺失必要參數異常<br>
 * 通常用於補充 Valid 校驗的異常資訊不足
 */
public class MissingRequestParameterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingRequestParameterException(String message) {
		super(message);
	}

}
