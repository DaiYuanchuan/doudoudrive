package com.doudoudrive.common.util.minio;

import com.alibaba.fastjson2.JSONObject;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.minio.*;
import com.doudoudrive.common.util.http.HttpConnection;
import com.doudoudrive.common.util.http.HttpRequest;
import com.doudoudrive.common.util.http.HttpResponse;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.sign.s3.AwsSignerUtil;
import com.doudoudrive.common.util.xml.JaxbUtil;
import com.doudoudrive.common.util.xml.XmlUtil;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.DigestUtils;
import org.springframework.util.MimeTypeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>MinIO 对象存储相关工具方法</p>
 * <p>2024-04-22 23:01</p>
 *
 * @author Dan
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MinioTemplate {

    /**
     * 当前 Minio 实例对象
     */
    private static volatile MinioTemplate instance;
    private MinioConfig config;

    /**
     * 私有化构造方法
     *
     * @param config Minio 配置信息
     */
    private MinioTemplate(MinioConfig config) {
        this.config = config;
    }

    // ---------------------------------------------------------------- Bucket start

    /**
     * 获取 MinioTemplate 实例对象
     *
     * @param config Minio 配置信息
     * @return MinioTemplate
     */
    public static MinioTemplate getInstance(MinioConfig config) {
        if (instance == null) {
            synchronized (HttpConnection.class) {
                if (instance == null) {
                    instance = new MinioTemplate(config);
                }
            }
        }
        return instance;
    }

    /**
     * 列出所有的桶
     *
     * @return {@link ListAllMyBucketsResult}
     */
    public ListAllMyBucketsResult listBuckets() {
        // 构建请求签名，此接口不需要带入存储桶名称字段
        AwsSignerUtil signer = this.generateSigner(null, ConstantConfig.HttpRequest.GET);

        // 发送请求
        try (HttpResponse execute = HttpRequest.get(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .execute()) {
            if (execute.isOk()) {
                // 解析并且返回结果
                return JaxbUtil.xmlToBean(execute.body(), ListAllMyBucketsResult.class, Boolean.TRUE);
            }
        } catch (Exception e) {
            log.error("Minio listBuckets error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 检查指定的桶是否存在
     *
     * @return 指定的桶是否存在，存在返回 true，否则返回 false
     */
    public Boolean bucketExists() {
        return this.bucketExists(config.getBucket());
    }

    /**
     * 检查指定的桶是否存在
     *
     * @param bucket 桶名称
     * @return 指定的桶是否存在，存在返回 true，否则返回 false
     */
    public Boolean bucketExists(String bucket) {
        // 构建请求签名，此接口不需要带入存储桶名称字段
        AwsSignerUtil signer = this.generateSigner(bucket, ConstantConfig.HttpRequest.HEAD);
        // 发送请求
        try (HttpResponse execute = HttpRequest.head(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .execute()) {
            // 存储桶存则响应状态码为 200，否则为 404
            return execute.isOk();
        } catch (Exception e) {
            log.error("Minio bucketExists error: {}", e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 创建一个存储桶
     *
     * @return 创建成功返回 true，否则返回 false
     */
    public Boolean createBucket() {
        return this.createBucket(config.getBucket());
    }

    /**
     * 创建一个存储桶
     *
     * @param bucket 需要创建的存储桶名称
     * @return 创建成功返回 true，否则返回 false
     */
    public Boolean createBucket(String bucket) {
        // 构建请求签名，此接口不需要带入存储桶名称字段
        AwsSignerUtil signer = this.generateSigner(bucket, ConstantConfig.HttpRequest.PUT);
        // 发送请求
        try (HttpResponse execute = HttpRequest.put(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .execute()) {
            return execute.isOk();
        } catch (Exception e) {
            log.error("Minio createBucket error: {}", e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 删除一个存储桶，存储桶内必须是空的，否则删除失败
     *
     * @return 删除成功返回 true，否则返回 false
     */
    public Boolean removeBucket() {
        return this.removeBucket(config.getBucket());
    }

    /**
     * 删除一个存储桶，存储桶内必须是空的，否则删除失败
     *
     * @param bucket 需要删除的存储桶名称
     * @return 删除成功返回 true，否则返回 false
     */
    public Boolean removeBucket(String bucket) {
        // 构建请求签名，此接口不需要带入存储桶名称字段
        AwsSignerUtil signer = this.generateSigner(bucket, ConstantConfig.HttpRequest.DELETE);
        // 发送请求
        try (HttpResponse execute = HttpRequest.delete(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .execute()) {
            return execute.isOk();
        } catch (Exception e) {
            log.error("Minio removeBucket error: {}", e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 清空存储桶中的所有对象
     */
    public void clearBucket() {
        this.clearBucket(config.getBucket());
    }

    /**
     * 清空存储桶中的所有对象
     *
     * @param bucket 存储桶名称
     */
    public void clearBucket(String bucket) {
        while (true) {
            // 列出当前存储桶中的全部数据
            ListObjectsV2 listObjects = this.listObjects(ListObjects.builder()
                    .bucket(bucket)
                    .build(), ListObjectsV2.class);
            // 如果存储桶中没有数据，则直接返回
            if (listObjects == null || CollectionUtil.isEmpty(listObjects.getContents())) {
                return;
            }

            // 获取存储桶中的全部对象的 key
            List<DeleteObject> deleteObjects = listObjects.getContents().stream().map(content -> DeleteObject.builder()
                    .key(content.getKey())
                    .build()).toList();
            if (CollectionUtil.isEmpty(deleteObjects)) {
                return;
            }

            // 删除存储桶中的全部对象
            removeObject(bucket, Boolean.TRUE, DeletesRequest.builder().objectList(deleteObjects).build());
        }
    }

    /**
     * API_GetBucketLocation: 获取存储桶的所在区域
     *
     * @return 存储桶所在区域，失败则返回默认区域 us-east-1
     */
    public String getBucketLocation() {
        return this.getBucketLocation(config.getBucket());
    }

    // ---------------------------------------------------------------- Bucket end

    // ---------------------------------------------------------------- Object start

    /**
     * API_GetBucketLocation: 获取存储桶的所在区域
     *
     * @param bucket 存储桶名称
     * @return 存储桶所在区域，失败则返回默认区域 us-east-1
     */
    public String getBucketLocation(String bucket) {
        // 定义查询参数
        Map<String, Object> queryParams = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
        queryParams.put(ConstantConfig.AwsSigner.GetBucketLocation.LOCATION, StringUtils.EMPTY);

        // 构建请求签名，此接口不需要带入存储桶名称字段
        AwsSignerUtil signer = this.generateSigner(bucket, queryParams);
        // 发送请求
        try (HttpResponse execute = HttpRequest.get(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .execute()) {
            if (execute.isOk()) {
                // 解析返回结果
                Document document = XmlUtil.parseXml(execute.body());
                Element element = XmlUtil.getRootElement(document);
                NodeList nodeList = element.getChildNodes();
                Node node = nodeList.item(NumberConstant.INTEGER_ZERO);
                return node == null ? ConstantConfig.AwsSigner.GetBucketLocation.DEFAULT_LOCATION : node.getTextContent();
            }
        } catch (Exception e) {
            log.error("Minio getBucketLocation error: {}", e.getMessage(), e);
        }
        return ConstantConfig.AwsSigner.GetBucketLocation.DEFAULT_LOCATION;
    }

    /**
     * API_CreateMultipartUpload：开启分片上传并返回上传一个上传 ID
     * 此上传 ID 是 用于关联特定分片上传中的所有分片的唯一标识符，参考：API_UploadPart
     *
     * @param bucket       存储桶名称
     * @param resourceName 资源名称
     * @param contentType  资源类型，为空则默认使用 octet-stream
     * @return {@link CreateMultipartUploadResult} 返回创建结果，包含存储桶名称、资源名称、上传 ID
     */
    public CreateMultipartUploadResult createMultipartUpload(String bucket, String resourceName, String contentType) {
        if (StringUtils.isBlank(contentType)) {
            // 请求头默认使用octet-stream
            contentType = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
        }

        // 定义请求头
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
        headers.put(HttpHeaders.CONTENT_TYPE, contentType);

        // 定义查询参数
        Map<String, Object> queryParams = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
        queryParams.put(ConstantConfig.AwsSigner.CreateMultipartUpload.UPLOADS, StringUtils.EMPTY);

        // 构建请求签名
        AwsSignerUtil signer = this.generateSigner(bucket, resourceName, ConstantConfig.HttpRequest.POST, null, queryParams, headers);
        // 发送请求
        try (HttpResponse execute = HttpRequest.post(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .execute()) {
            if (execute.isOk()) {
                return JaxbUtil.xmlToBean(execute.body(), CreateMultipartUploadResult.class, Boolean.TRUE);
            }
        } catch (Exception e) {
            log.error("Minio createMultipartUpload error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * API_UploadPart：上传一个分片数据
     * <pre>
     *     分片大小最小为 5MB，最大为 5GB
     *     分片上传的最大数量为10000，允许的最大对象大小为5TiB
     *     上传的分片可以不按顺序上传，但是在完成分片上传之前，必须上传所有分片
     *     上传的分片保存在服务器的.minio.sys/multipart目录下
     *     直到完成分片上传，或者分片上传被取消才会被删除
     *     默认在24h后自动删除未完成的分段上传
     *     通过：mc admin config get play/ api 查询配置信息
     *     mc admin config set play/ api stale_uploads_expiry=24h 修改配置信息
     * </pre>
     *
     * @param bucket       存储桶名称
     * @param resourceName 资源名称
     * @param uploadId     上传 ID
     * @param partNumber   分片编号
     * @param body         分片数据
     * @return 返回请求头中的 ETag 值，获取失败则返回空字符串
     */
    public String uploadPart(String bucket, String resourceName, final String uploadId, final long partNumber, byte[] body) {
        // 定义请求头
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
        headers.put(ConstantConfig.AwsSigner.CONTENT_MD5, this.md5Digest(body));

        // 定义查询参数
        Map<String, Object> queryParams = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_TWO);
        queryParams.put(ConstantConfig.AwsSigner.UploadPart.UPLOAD_ID, uploadId);
        queryParams.put(ConstantConfig.AwsSigner.UploadPart.PART_NUMBER, partNumber);

        // 构建请求签名
        AwsSignerUtil signer = this.generateSigner(bucket, resourceName, ConstantConfig.HttpRequest.PUT, body, queryParams, headers);
        // 发送请求
        try (HttpResponse execute = HttpRequest.put(signer.getRequestUrl())
                .setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .body(body)
                .execute()) {
            // 获取请求头中的 ETag 值
            String etag = execute.header(ConstantConfig.AwsSigner.UploadPart.ETAG);
            if (StringUtils.isBlank(etag)) {
                return StringUtils.EMPTY;
            }
            return etag.replaceAll(ConstantConfig.SpecialSymbols.DOUBLE_QUOTATION_MARKS, StringUtils.EMPTY);
        } catch (Exception e) {
            log.error("Minio uploadPart error: {}", e.getMessage(), e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * API_CompleteMultipartUpload: 完成分片上传，合并所有分片
     *
     * @param bucket       存储桶名称
     * @param resourceName 资源名称
     * @param uploadId     上传 ID
     * @param parts        分片列表
     */
    public CompleteMultipartUploadResult completeMultipartUpload(String bucket, String resourceName, final String uploadId, List<Part> parts) {
        if (CollectionUtil.isEmpty(parts)) {
            return null;
        }

        // 分片按照编号排序
        parts.sort(Comparator.comparing(Part::getPartNumber));

        // 对象参数转换为xml
        String xml = JaxbUtil.beanToXml(CompleteMultipartUpload.builder().partList(parts).build());
        if (StringUtils.isBlank(xml)) {
            return null;
        }

        // 构建请求body
        byte[] body = xml.getBytes(StandardCharsets.UTF_8);

        // 定义查询参数
        Map<String, Object> queryParams = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
        queryParams.put(ConstantConfig.AwsSigner.UploadPart.UPLOAD_ID, uploadId);

        // 定义请求头
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_TWO);
        headers.put(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_XML_VALUE);
        headers.put(ConstantConfig.AwsSigner.CONTENT_MD5, this.md5Digest(body));

        // 构建请求签名
        AwsSignerUtil signer = this.generateSigner(bucket, resourceName, ConstantConfig.HttpRequest.POST, body, queryParams, headers);
        // 发送请求
        try (HttpResponse execute = HttpRequest.post(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .body(body)
                .execute()) {
            if (execute.isOk()) {
                return JaxbUtil.xmlToBean(execute.body(), CompleteMultipartUploadResult.class, Boolean.TRUE);
            }
        } catch (Exception e) {
            log.error("Minio completeMultipartUpload error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * API_AbortMultipartUpload: 终止上传、删除所有已上传的分片
     * <p>请求语法：</p>
     * <pre><code>
     *     DELETE /Key+?uploadId=UploadId HTTP/1.1
     *     Host: Bucket.s3.amazonaws.com
     *     x-amz-request-payer: RequestPayer
     *     x-amz-expected-bucket-owner: ExpectedBucketOwner
     * </code></pre>
     *
     * @param bucket       存储桶名称
     * @param resourceName 资源名称
     * @param uploadId     上传 ID
     * @return 终止成功返回 true，否则返回 false
     */
    public Boolean abortMultipartUpload(String bucket, String resourceName, final String uploadId) {
        // 定义查询参数
        Map<String, Object> queryParams = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
        queryParams.put(ConstantConfig.AwsSigner.UploadPart.UPLOAD_ID, uploadId);

        // 构建请求签名
        AwsSignerUtil signer = this.generateSigner(bucket, resourceName, ConstantConfig.HttpRequest.DELETE, queryParams);
        // 发送请求
        try (HttpResponse execute = HttpRequest.delete(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .execute()) {
            return execute.isOk();
        } catch (Exception e) {
            log.error("Minio abortMultipartUpload error: {}", e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * API_ListParts: 列出指定上传 ID 的所有分片
     * 该请求默认返回 1000 个上传的分片信息
     * <p>请求语法：</p>
     * <pre><code>
     *     GET /Key+?max-parts=MaxParts&part-number-marker=PartNumberMarker&uploadId=UploadId HTTP/1.1
     *     Host: Bucket.s3.amazonaws.com
     *     x-amz-request-payer: RequestPayer
     *     x-amz-expected-bucket-owner: ExpectedBucketOwner
     *     x-amz-server-side-encryption-customer-algorithm: SSECustomerAlgorithm
     *     x-amz-server-side-encryption-customer-key: SSECustomerKey
     *     x-amz-server-side-encryption-customer-key-MD5: SSECustomerKeyMD5
     * </code></pre>
     *
     * @param bucket           存储桶名称
     * @param resourceName     资源名称
     * @param uploadId         上传 ID
     * @param maxParts         设置要返回的最大分片数量
     * @param partNumberMarker 分片编号标记，指定列出应在其后开始的部分，可以用来进行分页查询
     */
    public ListPartsResult listParts(String bucket, String resourceName, final String uploadId, Integer maxParts, Integer partNumberMarker) {
        if (maxParts == null || maxParts < NumberConstant.INTEGER_ONE) {
            // 默认返回 1000 个上传的分片信息
            maxParts = NumberConstant.INTEGER_ONE_THOUSAND;
        }

        // 定义查询参数
        Map<String, Object> queryParams = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
        queryParams.put(ConstantConfig.AwsSigner.ListParts.UPLOAD_ID, uploadId);
        queryParams.put(ConstantConfig.AwsSigner.ListParts.MAX_PARTS, maxParts);
        queryParams.put(ConstantConfig.AwsSigner.ListParts.PART_NUMBER_MARKER, partNumberMarker);

        // 构建请求签名
        AwsSignerUtil signer = this.generateSigner(bucket, resourceName, ConstantConfig.HttpRequest.GET, queryParams);
        // 发送请求
        try (HttpResponse execute = HttpRequest.get(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .execute()) {
            if (execute.isOk()) {
                return JaxbUtil.xmlToBean(execute.body(), ListPartsResult.class, Boolean.TRUE);
            }
        } catch (Exception e) {
            log.error("Minio listParts error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * API_CopyObject: 复制一个对象
     * 使用此 API 的单个操作中最大为 5 GB，如果要复制大于 5 GB 的对象，请使用分片复制(API_UploadPartCopy)
     */
    public CopyObjectResult copyObject(CopyObjectRequest request) {
        // 定义请求头
        Map<String, Object> requestHeaders = JSONObject.from(request);
        if (StringUtils.isNotBlank(request.getTargetContentType())) {
            requestHeaders.put(HttpHeaders.CONTENT_TYPE, request.getTargetContentType());
        }

        if (StringUtils.isNotBlank(request.getTargetContentEncoding())) {
            requestHeaders.put(HttpHeaders.CONTENT_ENCODING, request.getTargetContentEncoding());
        }

        Map<String, String> header = requestHeaders.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, value -> String.valueOf(value.getValue())));

        // 构建请求签名
        AwsSignerUtil signer = this.generateSigner(request.getTargetBucket(), request.getTargetKey(), ConstantConfig.HttpRequest.PUT, null, null, header);
        // 发送请求
        try (HttpResponse execute = HttpRequest.put(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .setSocketTimeout(NumberConstant.INTEGER_TEN_THOUSAND)
                .execute()) {
            if (execute.isOk()) {
                return JaxbUtil.xmlToBean(execute.body(), CopyObjectResult.class, Boolean.TRUE);
            }
        } catch (Exception e) {
            log.error("Minio copyObject error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * API_PutObject: 上传一个对象、将对象添加到存储桶中
     * 适用于小文件上传，大于 5M 的文件上传请使用分片上传
     *
     * @param bucket       存储桶名称
     * @param resourceName 资源名称
     * @param contentType  资源Mime类型
     * @param body         资源数据
     * @return {@link PutObjectResult} 上传结果
     */
    public PutObjectResult putObject(String bucket, String resourceName, String contentType, byte[] body) {
        if (StringUtils.isBlank(contentType)) {
            // 请求头默认使用octet-stream
            contentType = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
        }

        // 定义请求头
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_TWO);
        headers.put(HttpHeaders.CONTENT_TYPE, contentType);
        headers.put(ConstantConfig.AwsSigner.CONTENT_MD5, this.md5Digest(body));

        // 构建请求签名
        AwsSignerUtil signer = this.generateSigner(bucket, resourceName, ConstantConfig.HttpRequest.PUT, body, null, headers);
        // 发送请求
        try (HttpResponse execute = HttpRequest.put(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .body(body)
                .execute()) {
            if (execute.isOk()) {
                return PutObjectResult.builder()
                        .etag(execute.header(ConstantConfig.AwsSigner.PutObject.ETAG))
                        .versionId(execute.header(ConstantConfig.AwsSigner.PutObject.VERSION_ID))
                        .build();
            }
        } catch (Exception e) {
            log.error("Minio putObject error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除指定存储桶中的一个对象：API_DeleteObject
     *
     * @param bucket               存储桶名称
     * @param bypassGovernanceMode 指示是否绕过对象存储桶中的管理保留策略，若要使用此标头，必须具有权限：s3:BypassGovernanceRetention
     * @param args                 {@link DeleteObject} 删除对象参数
     */
    public void removeObject(String bucket, boolean bypassGovernanceMode, DeleteObject args) {
        Map<String, Object> queryParams = JSONObject.from(args);
        if (bypassGovernanceMode) {
            queryParams.put(ConstantConfig.AwsSigner.X_AMZ_BYPASS_GOVERNANCE_RETENTION, Boolean.TRUE);
        }

        // 构建请求签名
        AwsSignerUtil signer = this.generateSigner(bucket, args.getKey(), ConstantConfig.HttpRequest.DELETE, queryParams);
        HttpResponse execute = null;
        // 发送请求
        try {
            execute = HttpRequest.delete(signer.getRequestUrl())
                    .setHeader(signer.buildHeader(), Boolean.FALSE)
                    .charset(StandardCharsets.UTF_8)
                    .execute();
        } catch (Exception e) {
            log.error("Minio removeObject error: {}", e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(execute);
        }
    }

    /**
     * 删除指定存储桶中的多个对象：API_DeleteObjects
     * 该请求最多可以包含要删除的 1000 个key的列表
     *
     * @param bucket               存储桶名称
     * @param bypassGovernanceMode 指示是否绕过对象存储桶中的管理保留策略，若要使用此标头，必须具有权限：s3:BypassGovernanceRetention
     * @param args                 {@link DeleteObject} 删除对象参数
     */
    public void removeObject(String bucket, boolean bypassGovernanceMode, DeletesRequest args) {
        if (args == null || CollectionUtil.isEmpty(args.getObjectList()) || args.getObjectList().size() > NumberConstant.INTEGER_ONE_THOUSAND) {
            throw new IllegalArgumentException("The number of keys to be deleted must be greater than 0 and less than or equal to 1000");
        }

        // 对象参数转换为xml
        String xml = JaxbUtil.beanToXml(args);
        if (StringUtils.isBlank(xml)) {
            return;
        }

        // 构建请求body
        byte[] body = xml.getBytes(StandardCharsets.UTF_8);

        // 定义查询参数
        Map<String, Object> queryParams = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
        queryParams.put(ConstantConfig.HttpRequest.DELETE.toLowerCase(), Boolean.TRUE);

        // 定义请求头
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_THREE);
        headers.put(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_XML_VALUE);
        headers.put(ConstantConfig.AwsSigner.CONTENT_MD5, this.md5Digest(body));
        if (bypassGovernanceMode) {
            headers.put(ConstantConfig.AwsSigner.X_AMZ_BYPASS_GOVERNANCE_RETENTION, Boolean.TRUE.toString());
        }

        // 构建请求签名
        AwsSignerUtil signer = this.generateSigner(bucket, null, ConstantConfig.HttpRequest.POST, body, queryParams, headers);
        // 发送请求
        HttpResponse execute = null;
        try {
            execute = HttpRequest.post(signer.getRequestUrl())
                    .setHeader(signer.buildHeader(), Boolean.FALSE)
                    .charset(StandardCharsets.UTF_8)
                    .body(body)
                    .execute();
        } catch (Exception e) {
            log.error("Minio removeObject error: {}", e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(execute);
        }
    }

    /**
     * 列出存储桶中的部分或全部（最多 1000 个）对象
     * 本接口同时支持 ListObjects 和 ListObjectsV2 两种接口
     *
     * @param args  {@link ListObjects} 查询参数
     * @param clazz 定义返回结果的类型，支持 {@link ListObjectsV1} 和 {@link ListObjectsV2}
     * @param <T>   返回结果类型
     * @return 返回查询结果
     */
    public <T extends ListObjectsResult> T listObjects(ListObjects args, Class<T> clazz) {
        Optional.ofNullable(args).orElseThrow(() -> new IllegalArgumentException("ListObjects args is null"));

        // 如果未指定存储桶，则使用配置的默认存储桶
        if (StringUtils.isBlank(args.getBucket())) {
            args.setBucket(config.getBucket());
        }

        // 构建请求签名
        AwsSignerUtil signer = this.generateSigner(args.getBucket(), JSONObject.from(args));
        // 发送请求
        try (HttpResponse execute = HttpRequest.get(signer.getRequestUrl())
                .setHeader(signer.buildHeader(), Boolean.FALSE)
                .charset(StandardCharsets.UTF_8)
                .execute()) {
            if (execute.isOk()) {
                return JaxbUtil.xmlToBean(execute.body(), clazz, Boolean.TRUE);
            }
        } catch (Exception e) {
            log.error("Minio listObjects error: {}", e.getMessage(), e);
        }
        return null;
    }

    // ---------------------------------------------------------------- Object end

    /**
     * 获取预签名对象 URL，可以用于访问私有对象
     *
     * @param bucket       存储桶名称
     * @param resourceName 资源名称
     * @param expiresTime  预签名 URL 的有效时间，对于签名V4，预设网址的最长有效期为7天，以秒表示
     *                     此值是一个整数。可以设置的最小值为 1，并且 最大值为 604800（七天）
     * @return 返回预签名 URL
     */
    public String getPreSignedObjUrl(String bucket, String resourceName, Long expiresTime) {
        // 构建请求签名，生成预签名 URL
        return AwsSignerUtil.create(config.getEndpoint(), bucket, config.getAccessKey(), config.getSecretKey(), resourceName)
                .setRegion(config.getRegion()).setService(config.getServer()).buildPreSignedUrl(expiresTime);
    }

    /**
     * 生成签名对象，指定请求方法，存储桶名称
     * <pre>
     *     无body、无资源名称
     * </pre>
     *
     * @param bucket 指定桶名称
     * @param method 请求方法
     * @return {@link AwsSignerUtil}
     */
    private AwsSignerUtil generateSigner(String bucket, String method) {
        return this.generateSigner(bucket, null, method, null);
    }

    /**
     * 生成签名对象，指定请求方法、存储桶名称、查询参数
     *
     * @param bucket      指定桶名称
     * @param queryParams 查询参数
     * @return {@link AwsSignerUtil}
     */
    private AwsSignerUtil generateSigner(String bucket, Map<String, Object> queryParams) {
        return this.generateSigner(bucket, null, ConstantConfig.HttpRequest.GET, queryParams);
    }

    /**
     * 生成签名对象，指定请求方法、存储桶名称、资源名称
     *
     * @param bucket       指定桶名称
     * @param resourceName 资源名称
     * @param method       请求方法
     * @param queryParams  查询参数
     * @return {@link AwsSignerUtil}
     */
    private AwsSignerUtil generateSigner(String bucket, String resourceName, String method, Map<String, Object> queryParams) {
        return this.generateSigner(bucket, resourceName, method, null, queryParams, null);
    }

    /**
     * 生成签名对象，指定请求方法、存储桶名称、资源名称、请求体
     *
     * @param bucket       指定桶名称
     * @param resourceName 资源名称
     * @param method       请求方法
     * @param body         请求体
     * @return {@link AwsSignerUtil}
     */
    private AwsSignerUtil generateSigner(String bucket, String resourceName, String method, byte[] body, Map<String, Object> queryParams, Map<String, String> headers) {
        return AwsSignerUtil.create(config.getEndpoint(), bucket, config.getAccessKey(), config.getSecretKey(), resourceName)
                .setRegion(config.getRegion()).setService(config.getServer())
                .setQueryParams(Optional.ofNullable(queryParams).orElse(Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ZERO)))
                .setHeaders(Optional.ofNullable(headers).orElse(Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ZERO)))
                .requestUrl()
                .canonicalRequest(method, body)
                .stringToSign()
                .signature();
    }

    /**
     * 获取内容的 MD5 值
     *
     * @param body 请求体
     * @return MD5 值
     */
    private String md5Digest(byte[] body) {
        if (CollectionUtil.isEmpty(body)) {
            return ConstantConfig.AwsSigner.EMPTY_CONTENT_MD5;
        }
        byte[] md5 = DigestUtils.md5Digest(body);
        return Base64.getEncoder().encodeToString(md5);
    }
}
