package com.doudoudrive.common.model.dto.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>七牛云文件上传策略数据模型</p>
 * <p>2022-05-26 17:37</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUploadModel {

    /**
     * 上传成功后，七牛云向业务服务器发送 POST 请求的 URL
     * <pre>
     *     必须是公网上可以正常进行 POST 请求并能响应 HTTP/1.1 200 OK 的有效 URL。
     *     另外，为了给客户端有一致的体验，我们要求 callbackUrl 返回包 Content-Type 为 “application/json”，即返回的内容必须是合法的 JSON 文本。
     *     出于高可用的考虑，本字段允许设置多个 callbackUrl（用英文符号 ; 分隔），在前一个 callbackUrl 请求失败的时候会依次重试下一个 callbackUrl。
     *     一个典型例子是：http://<ip1>/callback;http://<ip2>/callback，并同时指定下面的 callbackHost 字段。
     *     在 callbackUrl 中使用 ip 的好处是减少对 dns 解析的依赖，可改善回调的性能和稳定性。指定 callbackUrl，必须指定 callbackbody，且值不能为空。
     * </pre>
     */
    private String callbackUrl;

    /**
     * 上传成功后，七牛云向业务服务器发送 Content-Type: application/x-www-form-urlencoded 的 POST 请求。
     * 业务服务器可以通过直接读取请求的 query 来获得该字段，支持魔法变量和自定义变量。
     * callbackBody 要求是合法的 url query string。
     * 例如key=$(key)&hash=$(etag)&w=$(imageInfo.width)&h=$(imageInfo.height)。
     * 如果callbackBodyType指定为application/json，则callbackBody应为json格式，
     * 例如:{“key”:"$(key)",“hash”:"$(etag)",“w”:"$(imageInfo.width)",“h”:"$(imageInfo.height)"}。
     */
    private String callbackBody;

    /**
     * 上传成功后，七牛云向业务服务器发送回调通知 callbackBody 的 Content-Type。
     * 默认为 application/x-www-form-urlencoded，也可设置为 application/json。
     */
    private String callbackBodyType;

    /**
     * 限定上传文件大小最大值，单位Byte。
     * 超过限制上传文件大小的最大值会被判为上传失败，返回 413 状态码。
     */
    @JSONField(name = "fsizeLimit")
    private Long sizeLimit;

    /**
     * 文件存储类型。
     * 0 为标准存储（默认），1 为低频存储，2 为归档存储，3 为深度归档存储。
     */
    private Integer fileType;

    /**
     * 指定上传的目标资源空间 Bucket 和资源键 Key（最大为 750 字节）
     */
    private String scope;

    /**
     * 上传凭证有效截止时间。Unix时间戳，单位为秒。
     * 该截止时间为上传完成后，在七牛空间生成文件的校验时间，而非上传的开始时间，一般建议设置为上传开始时间 + 3600s
     */
    private Long deadline;

}
