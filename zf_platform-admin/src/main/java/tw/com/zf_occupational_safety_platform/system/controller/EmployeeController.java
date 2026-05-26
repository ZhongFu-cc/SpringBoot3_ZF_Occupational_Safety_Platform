package tw.com.zf_occupational_safety_platform.system.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.system.manager.EmployeeManager;

/**
 * <p>
 * 用戶表 - 用戶-企業員工 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
@Tag(name = "用戶-企業員工 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/employee")
public class EmployeeController {

	private final EmployeeManager employeeManager;


	// 查詢課程列表
	
	// 觀看課程
	
	// 進行課後測驗
	
	// 查看個人學習歷程 (已完成、未完成、進度%數)





}
