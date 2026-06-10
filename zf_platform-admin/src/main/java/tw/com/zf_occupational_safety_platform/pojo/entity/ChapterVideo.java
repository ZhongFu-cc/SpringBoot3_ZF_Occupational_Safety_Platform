package tw.com.zf_occupational_safety_platform.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 章節影片 - 只有content_type 為video的才有
 * </p>
 *
 * @author Joey
 * @since 2026-06-09
 */
@Getter
@Setter
@TableName("chapter_video")
@Schema(name = "ChapterVideo", description = "章節影片 - 只有content_type 為video的才有")
public class ChapterVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主鍵ID")
    @TableId("chapter_video_id")
    private Long chapterVideoId;

    @Schema(description = "課程章節ID")
    @TableField("course_chapter_id")
    private Long courseChapterId;

    @Schema(description = "檔案名稱-可與傳送時不同")
    @TableField("file_name")
    private String fileName;

    @Schema(description = "檔案在S3儲存的路徑")
    @TableField("path")
    private String path;

    @Schema(description = "創建者")
    @TableField("create_by")
    private String createBy;

    @Schema(description = "創建時間")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @Schema(description = "最後修改者")
    @TableField("update_by")
    private String updateBy;

    @Schema(description = "最後修改時間")
    @TableField(value = "update_date", fill = FieldFill.UPDATE)
    private LocalDateTime updateDate;

    @Schema(description = "邏輯刪除,預設為0活耀,1為刪除")
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
}
