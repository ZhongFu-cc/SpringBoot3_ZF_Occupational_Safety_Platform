package tw.com.zf_occupational_safety_platform.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.FormManager;
import tw.com.zf_occupational_safety_platform.service.FormService;

/**
 * <p>
 * 課程主表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
@Tag(name = "課程主表 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/course")
public class CourseController {

	
	
}
