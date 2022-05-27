package com.doudoudrive.file.model.dto.response;

import com.doudoudrive.common.model.dto.model.DiskFileModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>获取文件上传token时响应数据模型</p>
 * <p>2022-05-26 15:53</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadTokenResponseDTO {

    /**
     * 七牛云上传token，在前端进行上传操作时需要此token
     */
    private String token;

    /**
     * 云端预存储的key值，配合token在前端进行上传操作
     */
    private String key;

    /**
     * 用户文件信息，秒传时返回
     */
    private DiskFileModel fileInfo;

}
