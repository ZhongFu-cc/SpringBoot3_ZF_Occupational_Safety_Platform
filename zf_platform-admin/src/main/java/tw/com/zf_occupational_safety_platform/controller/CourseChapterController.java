package tw.com.zf_occupational_safety_platform.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 課程章節表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
@Tag(name = "課程章節 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/course-chapter")
public class CourseChapterController {

}
