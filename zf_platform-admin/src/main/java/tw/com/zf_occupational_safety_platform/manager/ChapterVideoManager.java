package tw.com.zf_occupational_safety_platform.manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.helper.S3Helper;
import tw.com.zf_occupational_safety_platform.service.ChapterVideoService;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.system.service.SysChunkFileService;

@Component
@RequiredArgsConstructor
public class ChapterVideoManager {

	// 「預設」存储桶名称
	@Value("${spring.cloud.aws.s3.bucketName}") // 注意：这里的 Value key 可能需要对应您的配置
	private String bucketName;

	private final CourseChapterService courseChapterService;
	private final ChapterVideoService chapterVideoService;
	private final SysChunkFileService sysChunkFileService;
	private final S3Helper s3Helper;

}
