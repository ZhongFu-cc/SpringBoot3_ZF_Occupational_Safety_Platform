package tw.com.zf_occupational_safety_platform.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.CourseChapterManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseChapterVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.utils.R;

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

	private final CourseChapterService courseChapterService;
	private final CourseChapterManager courseChapterManager;

	/**
	 * 根據ID 查詢 課程單元
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 查詢 課程單元")
	@GetMapping("{id}")
	public R<CourseChapter> getCourseChapter(@PathVariable("id") @Schema(type = "string") Long id) {
		CourseChapter courseCategory = courseChapterService.get(id);
		return R.ok(courseCategory);
	}

	@GetMapping("course")
	@Operation(summary = "根據 課程ID 查詢 課程單元樹(父子結構)")
	public R<List<CourseChapterVO>> getCourseChapterTree(
			@RequestParam("courseId") @Schema(type = "string") Long courseId) {
		List<CourseChapterVO> treeList = courseChapterManager.findTreeList(courseId);
		return R.ok(treeList);
	}

	/**
	 * 查詢 課程單元分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	//	@GetMapping("pagination")
	//	@Parameters({
	//			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	//	@Operation(summary = "查詢 課程單元分頁對象")
	//	public R<IPage<CourseChapter>> findCourseChapterPage(@RequestParam Integer page, @RequestParam Integer size,
	//			@RequestParam(required = false) String queryText) {
	//
	//		Page<CourseChapter> pageInfo = new Page<>(page, size);
	//		IPage<CourseChapter> courseCategoryPage = courseCategoryService.findPageByQuery(pageInfo, queryText);
	//		return R.ok(courseCategoryPage);
	//	}

	/**
	 * 新增 課程單元
	 * 
	 * @param addCourseChapterDTO
	 * @return
	 */
	@Operation(summary = "新增 課程單元")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PostMapping
	public R<CourseChapter> saveCourseChapter(@RequestBody @Valid AddCourseChapterDTO addCourseChapterDTO) {
		CourseChapter courseChapter = courseChapterManager.createCourseChapter(addCourseChapterDTO);
		return R.ok(courseChapter);
	}

	/**
	 * 更新 課程單元
	 * 
	 * @param putCourseChapterDTO
	 * @return
	 */
	@Operation(summary = "更新 課程單元")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PutMapping
	public R<Void> updateCourseChapter(@RequestBody @Valid PutCourseChapterDTO putCourseChapterDTO) {
		courseChapterManager.updateCourseChapter(putCourseChapterDTO);
		return R.ok();
	}

	/**
	 * 根據ID 刪除 課程單元
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 刪除 課程單元 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("super-admin")
	@DeleteMapping("{id}")
	public R<Void> removeCourseChapter(@PathVariable @Schema(type = "string") Long id) {
		courseChapterManager.removeCourseChapter(id);
		return R.ok();
	}

}
