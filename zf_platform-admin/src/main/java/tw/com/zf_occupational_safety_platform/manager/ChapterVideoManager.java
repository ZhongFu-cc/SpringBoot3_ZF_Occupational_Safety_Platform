package tw.com.zf_occupational_safety_platform.manager;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.zf_occupational_safety_platform.exception.CourseException;
import tw.com.zf_occupational_safety_platform.helper.S3Helper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.UpdateChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.UploadChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterVideo;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.service.ChapterVideoService;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.ChunkResponseVO;
import tw.com.zf_occupational_safety_platform.system.service.SysChunkFileService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChapterVideoManager {

	// 「預設」存储桶名称
	@Value("${spring.cloud.aws.s3.bucketName}") // 注意：这里的 Value key 可能需要对应您的配置
	private String bucketName;

	// Redisson Keys 儲存 chapterVideo
	private static final String CHAPTER_VIDEO_KEY_PREFIX = "chapter:video:";

	private static final String VIDEO_BASE_PATH = "chapter_video/";

	@Qualifier("businessRedissonClient")
	private final RedissonClient redissonClient;
	private final CourseChapterService courseChapterService;
	private final ChapterVideoService chapterVideoService;
	private final SysChunkFileService sysChunkFileService;
	private final S3Helper s3Helper;

	/**
	 * --------------------------- 投稿者-二階段稿件操作---------------------------------
	 */

	/**
	 * 
	 * 初次上傳slide，大檔案切割成分片，最後重新組裝
	 * 
	 * @param uploadChapterVideoDTO
	 * @param file
	 * @return
	 */
	public ChunkResponseVO uploadChunk(@Valid UploadChapterVideoDTO uploadChapterVideoDTO, MultipartFile file) {
		// 1.courseChapterId 找到要上傳哪個課程章節的影片
		CourseChapter courseChapter = courseChapterService.get(uploadChapterVideoDTO.getCourseChapterId());

		if (courseChapter == null) {
			throw new CourseException("沒有此課程章節");
		}

		// 2.組裝合併後檔案的路徑, 目前在 稿件/第二階段/投稿類別/
		String mergedBasePath = VIDEO_BASE_PATH + courseChapter.getTitle() + "/";

		// 3.上傳分片,會回傳分片上傳狀態，並在最後一個分片上傳完成時進行合併
		ChunkResponseVO chunkResponseVO = sysChunkFileService.uploadChunkS3(file, mergedBasePath,
				uploadChapterVideoDTO.getChunkUploadDTO());

		// 4.當FilePath 不等於 null 時, 代表整個檔案都 merge 完成，具有可查看的Path路徑
		String filePath = chunkResponseVO.getFilePath();
		if (filePath != null) {
			String fileName = uploadChapterVideoDTO.getChunkUploadDTO().getFileName();
			String sha256 = chunkResponseVO.getCurrentFileSha256();
			String chapterVideoKey = CHAPTER_VIDEO_KEY_PREFIX + sha256;
			RBucket<String> bucket = redissonClient.getBucket(chapterVideoKey);

			// 4-1. 先查 Redis
			String existingId = bucket.get();
			if (existingId != null) {
				return chunkResponseVO;
			}

			// 4-2. Redis 沒命中，先查 DB
			ChapterVideo existFile = chapterVideoService.getByChapter(courseChapter.getCourseChapterId());

			if (existFile != null) {
				// DB 已存在，更新 Redis
				bucket.set(existFile.getChapterVideoId().toString(), 30, TimeUnit.SECONDS);
				return chunkResponseVO;
			}

			// 4-3. DB 也沒有，獲取鎖並進行插入
			RLock lock = redissonClient.getLock("db-insert-lock:" + sha256);
			boolean isLock = false;
			try {
				isLock = lock.tryLock(10, 30, TimeUnit.SECONDS);
				if (isLock) {
					// 4-3-1. 鎖內再次競態檢查
					existingId = bucket.get();
					if (existingId != null) {
						return chunkResponseVO;
					}

					//  4-3-2. 插入 DB
					ChapterVideo chapterVideo = new ChapterVideo();
					chapterVideo.setFileName(fileName);
					chapterVideo.setPath(filePath);
					chapterVideoService.save(chapterVideo);

					//  4-3-3. 更新 Redis
					bucket.set(chapterVideo.getChapterVideoId().toString(), 30, TimeUnit.SECONDS);
				}

			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			} finally {
				if (isLock) {
					lock.unlock();
				}
			}
		}

		return chunkResponseVO;
	}

	/**
	 * 更新章節課程影片，大檔案切割成分片，最後重新組裝
	 * 
	 * @param putSlideUploadDTO
	 * @param file
	 * @return
	 */
	public ChunkResponseVO updateChunk(@Valid UpdateChapterVideoDTO putSlideUploadDTO, MultipartFile file) {
		// 1.先查詢chapterVideo確定有這個資料，如果查不到，報錯
		ChapterVideo chapterVideo = chapterVideoService.get(putSlideUploadDTO.getChapterVideoId());

		if (chapterVideo == null) {
			throw new CourseException("沒有此課程章節影片");
		}

		CourseChapter courseChapter = courseChapterService.get(chapterVideo.getCourseChapterId());
		// 2.組裝合併後檔案的路徑, 目前在 稿件/第二階段/投稿類別/
		String mergedBasePath = VIDEO_BASE_PATH + courseChapter.getTitle() + "/";

		// 3.上傳分片,會回傳分片上傳狀態，並在最後一個分片上傳完成時進行合併
		ChunkResponseVO chunkResponseVO = sysChunkFileService.uploadChunkS3(file, mergedBasePath,
				putSlideUploadDTO.getChunkUploadDTO());

		// 5.當FilePath 不等於 null 時, 代表整個檔案都 merge 完成，具有可查看的Path路徑
		// 所以可以更新到chapterVideo 中
		if (chunkResponseVO.getFilePath() != null) {

			// 刪除舊檔案 和 DB 紀錄
			String oldS3Key = s3Helper.extractS3PathInDbUrl(bucketName, chapterVideo.getPath());
			String currentS3Key = s3Helper.extractS3PathInDbUrl(bucketName, chunkResponseVO.getFilePath());
			System.out.println("oldS3Key: " + oldS3Key);
			System.out.println("此次更新Chunk File Path: " + currentS3Key);

			// 當檔名不一樣時要刪除舊檔案，檔名相同S3會直接覆蓋
			if (!oldS3Key.equals(currentS3Key)) {
				System.out.println("檔名不一致,移除檔案");
				s3Helper.removeFileIfPresent(bucketName, oldS3Key);
				// 檔名不一樣時，刪除分片上傳紀錄，一樣則不要刪,避免sysChunk紀錄混亂
				sysChunkFileService.deleteSysChunkFileByPath(oldS3Key);

			}

			// 設定檔案路徑，組裝 bucketName 和 Path 進資料庫當作真實路徑
			chapterVideo.setPath(chunkResponseVO.getFilePath());
			// 設定檔案名稱
			chapterVideo.setFileName(putSlideUploadDTO.getChunkUploadDTO().getFileName());
			// 更新資料庫
			chapterVideoService.updateById(chapterVideo);

		}

		return chunkResponseVO;
	}

	/**
	 * 刪除課程章節影片
	 * 
	 * @param chapterVideoId
	 */
	public void removeFile(Long chapterVideoId) {
		// 1.先查詢chapterVideo確定有這個資料，如果查不到，報錯
		ChapterVideo chapterVideo = chapterVideoService.get(chapterVideoId);

		if (chapterVideo == null) {
			throw new CourseException("沒有此課程章節影片");
		}

		// 2.刪除檔案
		s3Helper.removeFileIfPresent(bucketName, chapterVideo.getPath());

		// 3.刪除資料
		chapterVideoService.remove(chapterVideoId);

		// 4.移除chapter 內的 path
		courseChapterService.clearVideoUrl(chapterVideo.getCourseChapterId());

	}

}
