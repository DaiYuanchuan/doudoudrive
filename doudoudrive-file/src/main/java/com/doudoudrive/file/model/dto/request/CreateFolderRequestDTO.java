package com.doudoudrive.file.model.dto.request;

import com.doudoudrive.common.constant.NumberConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>文件夹创建时请求数据模型</p>
 * <p>2022-05-21 17:17</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFolderRequestDTO {

    /**
     * 文件夹名称
     */
    @NotBlank(message = "请输入文件夹名称")
    @Size(max = 80, message = "文件夹名称长度错误")
    private String name;

    /**
     * 文件父级标识，0 为根目录标识，默认为 0
     */
    @Size(max = 35, message = "未找到指定文件夹")
    private String parentId;

    /**
     * 获取文件夹名称，文件夹名称去除前后空格
     *
     * @return 文件夹名称
     */
    public String getName() {
        return name.trim();
    }

    /**
     * 获取文件父级标识对象，不存在时默认响应0
     *
     * @return 文件父级标识对象
     */
    public String getParentId() {
        if (StringUtils.isBlank(parentId)) {
            // 0 为根目录
            return NumberConstant.STRING_ZERO;
        }
        return parentId;
    }
}
