package com.doudoudrive.file.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>取消文件分享时的请求数据模型</p>
 * <p>2022-10-07 12:50</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelFileShareRequestDTO {

    /**
     * 需要取消分享的分享链接列表
     */
    @NotEmpty(message = "请选择需要取消的分享链接")
    @Size(max = 120, message = "请不要一次性操作太多数据~")
    private List<String> shareId;

}
