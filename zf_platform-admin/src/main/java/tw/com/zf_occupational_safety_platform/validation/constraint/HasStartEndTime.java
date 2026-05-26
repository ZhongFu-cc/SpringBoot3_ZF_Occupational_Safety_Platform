package tw.com.zf_occupational_safety_platform.validation.constraint;

import java.time.LocalDateTime;

public interface HasStartEndTime {

	public LocalDateTime getStartTime();
	public LocalDateTime getEndTime();

}
