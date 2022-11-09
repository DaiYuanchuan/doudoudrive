package com.doudoudrive.file.model.dto.request;

import com.doudoudrive.common.model.dto.model.FileNestedModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>文件复制(保存到我的)时的请求数据模型</p>
 * <p>2022-10-16 23:28</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileCopyRequestDTO {

    /**
     * 分享的短链接标识
     */
    @NotBlank(message = "你打开的链接有误，请重试")
    @Size(max = 35, message = "你打开的链接有误，请重试")
    private String shareId;

    /**
     * 提取码(为空时表示不需要提取码)
     */
    @Size(max = 6, message = "请输入6位数字或字母")
    private String sharePwd;

    /**
     * 需要复制到的目标文件夹标识
     */
    @NotBlank(message = "请选择需要保存到的文件夹")
    @Size(max = 35, message = "未找到指定文件夹")
    private String targetFolderId;

    /**
     * 文件复制时的嵌套数据模型
     */
    @Valid
    @NotEmpty(message = "请选择需要保存的文件")
    @Size(max = 120, message = "请不要一次性操作太多数据~")
    private List<FileNestedModel> fileInfo;

}
