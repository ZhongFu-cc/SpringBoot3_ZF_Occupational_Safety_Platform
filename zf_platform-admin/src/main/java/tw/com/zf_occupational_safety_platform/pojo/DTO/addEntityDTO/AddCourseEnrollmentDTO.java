package tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AddCourseEnrollmentDTO {

	@Schema(description = "報名用戶ID")
	@TableField("sys_user_id")
	private Long sysUserId;

	@Schema(description = "報名課程ID")
	@TableField("course_id")
	private Long courseId;

}
