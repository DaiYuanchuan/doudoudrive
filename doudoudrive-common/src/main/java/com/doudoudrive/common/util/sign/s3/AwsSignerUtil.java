package com.doudoudrive.common.util.sign.s3;

import cn.hutool.core.date.DatePattern;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.http.UrlQueryUtil;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.google.common.collect.Maps;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>AWS S3 V4签名计算工具</p>
 * <p>签名规范来自：<a href="https://docs.aws.amazon.com/zh_cn/IAM/latest/UserGuide/create-signed-request.html">AWS API 请求签名</a> </p>
 * <p>2024-04-14 15:29</p>
 *
 * @author Dan
 **/
@Data
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AwsSignerUtil {

    /**
     * 签名过程中所需的UTC时间格式
     */
    private static final DateTimeFormatter UTC_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    /**
     * 匹配一个或多个连续的空白字符的正则表达式
     */
    private static final String REGEX_WHITESPACE = "\\s+";
    /**
     * 自定义的安全字符映射，在进行 URL 编码时，对指定字符进行特殊处理
     */
    private static final Map<Character, String> CUSTOM_SAFE_MAP = new HashMap<>(NumberConstant.INTEGER_TWO) {
        @Serial
        private static final long serialVersionUID = -7152102101684950203L;

        {
            put('/', "/");
        }
    };
    /**
     * Api 端点
     * <p>例如：<a href="https://s3-us-east-1.amazonaws.com">https://s3-us-east-1.amazonaws.com</a></p>
     */
    private String endpoint;
    /**
     * 存储桶名称
     */
    private String bucketName;
    /**
     * 访问密钥
     */
    private String accessKey;
    /**
     * 签名密钥
     */
    private String secretKey;
    /**
     * 区域代码（例如，us-east-1）
     */
    private String region;
    /**
     * 服务名称（例如，s3）
     */
    private String service;
    /**
     * 资源名称
     */
    private String resourceName;
    /**
     * 用于签名的请求头，可以添加额外参数
     */
    private Map<String, String> headers = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_FIVE);
    /**
     * 查询参数，获取资源时可用，可以添加额外参数
     */
    private Map<String, Object> queryParams = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_FIVE);
    /**
     * 请求地址，https://{endpoint}/{bucketName}/{resource name}
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String requestUrl;
    /**
     * 主机地址
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String host;
    /**
     * 用于签名的请求头，按字母顺序排序、以分号分隔的小写请求标头名称列表。列表中的请求头在 CanonicalHeaders 字符串中包含的头相同
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String signedHeader;
    /**
     * 请求头及其值的列表，各个头名称和值用换行符 \n 分隔
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String canonicalHeaders;
    /**
     * URI 编码的查询字符串参数，须按键名称的字母顺序对规范查询字符串中的参数进行排序
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String canonicalQueryString;
    /**
     * 绝对路径组件 URI 的 URI 编码版本，以域名后面的“/”开头，直至字符串结尾处，或者如果包含查询字符串参数，则直至问号字符
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String canonicalUri;
    /**
     * 规范性请求报文主体
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String canonicalRequest;
    /**
     * 在凭证范围内使用的日期和时间。该值是采用 ISO 8601 格式的当前 UTC 时间（例如 20130524T000000Z），用于生成签名
     */
    @Setter(value = AccessLevel.PRIVATE)
    private LocalDateTime requestDateTime;
    /**
     * 凭证范围，将生成的签名限制在指定的区域和服务范围内。该字符串采用以下格式：YYYYMMDD/region/service/aws4_request
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String credentialScope;
    /**
     * 要签名的字符串
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String stringToSign;
    /**
     * 签名结果
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String signature;
    /**
     * 请求中的授权字符串，用于请求头中的 Authorization 字段
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String authorizationHeader;
    /**
     * 请求在查询字符串中使用身份验证参数
     */
    @Setter(value = AccessLevel.PRIVATE)
    private String authorizationParams;

    /**
     * 创建一个 AWS S3 V4 签名计算工具对象
     *
     * @param endpoint     Api 端点
     * @param bucketName   存储桶名称
     * @param accessKey    访问密钥
     * @param secretKey    签名密钥
     * @param resourceName 资源名称
     * @return {@link AwsSignerUtil} AWS S3 V4 签名计算工具
     */
    public static AwsSignerUtil create(String endpoint, String bucketName,
                                       String accessKey, String secretKey, String resourceName) {
        return create(endpoint, bucketName, accessKey, secretKey, ConstantConfig.AwsSigner.REGION_DEFAULT_VALUE,
                ConstantConfig.AwsSigner.SERVICE_DEFAULT_VALUE, resourceName);
    }

    /**
     * 创建一个 AWS S3 V4 签名计算工具对象
     *
     * @param endpoint     Api 端点
     * @param bucketName   存储桶名称
     * @param accessKey    访问密钥
     * @param secretKey    签名密钥
     * @param region       区域代码（例如，us-east-1）
     * @param service      服务名称（例如，s3）
     * @param resourceName 资源名称
     * @return {@link AwsSignerUtil} AWS S3 V4 签名计算工具
     */
    public static AwsSignerUtil create(String endpoint, String bucketName,
                                       String accessKey, String secretKey,
                                       String region, String service, String resourceName) {
        return new AwsSignerUtil().setEndpoint(endpoint)
                .setBucketName(bucketName)
                .setAccessKey(accessKey)
                .setSecretKey(secretKey)
                .setRegion(region)
                .setService(service)
                .setResourceName(resourceName)
                .setRequestDateTime(LocalDateTime.now(ZoneOffset.UTC))
                .credentialScope();
    }

    /**
     * 定义符合 AWS 签名规范的 URL 编码
     *
     * @param src 源字符串
     * @return URL 编码后的字符串
     */
    public static String signerUrlEncode(String src) {
        return UrlQueryUtil.encode(src, StandardCharsets.UTF_8, CUSTOM_SAFE_MAP);
    }

    /**
     * 需要先初始化请求地址
     * 生成当前资源的请求地址，格式为：{endpoint}/{bucketName}/{resource name}?{queryParams}
     *
     * @return 请求地址
     */
    public AwsSignerUtil requestUrl() {
        StringBuilder urlBuilder = new StringBuilder(this.endpoint);

        // 如果 endpoint 不以 / 结尾，则添加 /
        if (!this.endpoint.endsWith(ConstantConfig.SpecialSymbols.SLASH)) {
            urlBuilder.append(ConstantConfig.SpecialSymbols.SLASH);
        }

        // 追加存储桶名称，构建Url的每个部分都需要进行 URL 编码
        urlBuilder.append(signerUrlEncode(this.bucketName));

        if (StringUtils.isNotBlank(this.resourceName)) {
            // 如果资源名称不以 / 开头，则添加 /
            if (!this.resourceName.startsWith(ConstantConfig.SpecialSymbols.SLASH)) {
                urlBuilder.append(ConstantConfig.SpecialSymbols.SLASH);
            }
            urlBuilder.append(signerUrlEncode(this.resourceName));
        }

        // 查询参数非空时，在 URL 后面添加查询参数
        if (CollectionUtil.isNotEmpty(this.queryParams)) {
            urlBuilder.append(ConstantConfig.SpecialSymbols.QUESTION_MARK);

            // 查询字符串参数
            String queryString = UrlQueryUtil.buildUrlQueryParams(this.queryParams, true, false, StandardCharsets.UTF_8, null);
            urlBuilder.append(queryString);
        }

        // 请求地址
        this.requestUrl = urlBuilder.toString();

        // 获取带有端口号的主机地址
        URI uri = URI.create(this.requestUrl);
        String port = uri.getPort() > NumberConstant.INTEGER_MINUS_ONE ? ConstantConfig.SpecialSymbols.ENGLISH_COLON + uri.getPort() : StringUtils.EMPTY;
        this.host = uri.getHost() + port;

        // 绝对路径组件 URI 的 URI 编码版本，以域名后面的“/”开头，直至字符串结尾处，或者如果包含查询字符串参数，则直至问号字符
        String path = StringUtils.isNotBlank(uri.getPath()) ? uri.getPath() : ConstantConfig.SpecialSymbols.SLASH;
        this.canonicalUri = signerUrlEncode(path);
        return this;
    }

    /**
     * 步骤1：创建规范请求<br/>
     * 串联以下由换行符\n分隔的字符串，创建规范请求
     * <ol>
     *     <li>HTTPMethod</li>
     *     <li>CanonicalURI</li>
     *     <li>CanonicalQueryString</li>
     *     <li>CanonicalHeaders</li>
     *     <li>SignedHeaders</li>
     *     <li>HashedPayload</li>
     * </ol>
     * <pre>
     *     HTTPMethod – HTTP 方法，例如 GET、PUT、HEAD 和 DELETE
     *     CanonicalUri：绝对路径组件 URI 的 URI 编码版本，以域名后面的“/”开头，直至字符串结尾处，
     *     或者如果包含查询字符串参数，则直至问号字符（“?”）。如果绝对路径为空，则使用正斜杠字符（/）
     *     CanonicalQueryString – URI 编码的查询字符串参数。您可以单独对每个名称和值进行 URI 编码。您还必须按键名称的字母顺序对规范查询字符串中的参数进行排序
     *     CanonicalHeaders：请求标头及其值的列表。各个标头名称和值对用换行符（“\n”）分隔
     *     SignedHeaders：按字母顺序排序、以分号分隔的小写请求标头名称列表。列表中的请求标头与您在 CanonicalHeaders 字符串中包含的标头相同
     *     HashedPayload – 使用 HTTP 请求正文中的负载作为哈希函数的输入创建的字符串。此字符串使用小写十六进制字符
     * </pre>
     *
     * @param method http请求方法，例如 GET、PUT、HEAD 和 DELETE
     * @param body   请求体
     * @return 规范性请求
     */
    public AwsSignerUtil canonicalRequest(String method, byte[] body) {
        this.headers.put(ConstantConfig.HttpRequest.HOST, this.host);
        this.headers.put(ConstantConfig.AwsSigner.X_AMZ_DATE, this.requestDateTime.format(UTC_PATTERN));

        // 使用 HTTP 请求正文中的负载作为哈希函数的输入创建的字符串，此字符串使用小写十六进制字符，如果请求中不包含有效负载，则按Hex(SHA256Hash(""))生成
        String hashedPayload = CollectionUtil.isEmpty(body) ? ConstantConfig.AwsSigner.EMPTY_BODY_SHA256 : new Digester(DigestAlgorithm.SHA256).digestHex(body);
        this.headers.put(ConstantConfig.AwsSigner.X_AMZ_CONTENT_SHA256, hashedPayload);

        // 获取用于签名的请求头
        this.setCanonicalHeader();

        // URI 编码的查询字符串参数，须按键名称的字母顺序对规范查询字符串中的参数进行排序
        this.canonicalQueryString = UrlQueryUtil.buildUrlQueryParams(this.queryParams, true, true, StandardCharsets.UTF_8, null);

        // 创建规范性请求
        this.canonicalRequest = method + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + this.canonicalUri + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + this.canonicalQueryString + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + this.canonicalHeaders + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + this.signedHeader + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + hashedPayload;
        return this;
    }

    /**
     * 步骤2：创建要签名的字符串<br/>
     * 串联以下由换行符\n分隔的字符串，创建规范请求
     * <ol>
     *     <li>Algorithm</li>
     *     <li>RequestDateTime</li>
     *     <li>CredentialScope</li>
     *     <li>HashedCanonicalRequest</li>
     * </ol>
     * <pre>
     *     Algorithm – 用于创建规范请求的哈希的算法。对于 SHA-256，算法是 AWS4-HMAC-SHA256。
     *     RequestDateTime – 在凭证范围内使用的日期和时间。该值是采用 ISO 8601 格式的当前 UTC 时间（例如 20130524T000000Z）。
     *     CredentialScope – 凭证范围。这会将生成的签名限制在指定的区域和服务范围内。该字符串采用以下格式：YYYYMMDD/region/service/aws4_request。
     *     HashedCanonicalRequest – 规范请求的哈希。该值在上一个步骤中计算。
     * </pre>
     * 示例：<br/>
     * <code>
     *     "AWS4-HMAC-SHA256" + "\n" +
     *     timeStampISO8601Format + "\n" +
     *     Scope + "\n" +
     *     Hex(SHA256Hash(CanonicalRequest))
     * </code>
     *
     * @return 要签名的字符串
     */
    public AwsSignerUtil stringToSign() {
        this.stringToSign = ConstantConfig.AwsSigner.AWS4_HMAC_SHA256 + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + this.requestDateTime.format(UTC_PATTERN) + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + this.credentialScope + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + new Digester(DigestAlgorithm.SHA256).digestHex(this.canonicalRequest);
        return this;
    }

    /**
     * 步骤3：计算签名，这将创建一个仅限于特定区域和服务的签名密钥，作为身份验证信息添加到请求中<br/>
     * 签名结构如下：<br/>
     * <pre>
     *     DateKey = HMAC-SHA256("AWS4"+"SecretAccessKey", "YYYYMMDD");
     *     DateRegionKey = HMAC-SHA256(DateKey, "aws-region");
     *     DateRegionServiceKey = HMAC-SHA256(DateRegionKey, "aws-service");
     *     SigningKey = HMAC-SHA256(DateRegionServiceKey, "aws4_request");
     * </pre>
     *
     * @return 签名
     */
    public AwsSignerUtil signature() {
        // 生成签名密钥，算法版本 + secretKey
        String secretKey = ConstantConfig.AwsSigner.AWS4 + this.secretKey;

        // 当前的日期，格式为：YYYYMMDD
        String date = this.requestDateTime.format(DatePattern.PURE_DATE_FORMATTER);

        // 计算签名的密钥
        byte[] dateKey = new HMac(HmacAlgorithm.HmacSHA256, secretKey.getBytes(StandardCharsets.UTF_8)).digest(date);
        byte[] dateRegionKey = new HMac(HmacAlgorithm.HmacSHA256, dateKey).digest(this.region);
        byte[] dateRegionServiceKey = new HMac(HmacAlgorithm.HmacSHA256, dateRegionKey).digest(this.service);
        byte[] signingKey = new HMac(HmacAlgorithm.HmacSHA256, dateRegionServiceKey).digest(ConstantConfig.AwsSigner.TERMINATOR);

        // 将签名从二进制转换为十六进制表示形式，使用小写字符
        this.signature = new HMac(HmacAlgorithm.HmacSHA256, signingKey).digestHex(this.stringToSign).toLowerCase(Locale.US);
        return this;
    }

    /**
     * 步骤4：生成授权字符串，用于请求头中的 Authorization 字段<br/>
     * 必须是连续的字符串，算法和 Credential 之间没有逗号，但是必须使用逗号分隔其他元素
     * 示例：
     * <pre>
     *     Authorization: AWS4-HMAC-SHA256
     *     Credential=AKIAIOSFODNN7EXAMPLE/20220830/us-east-1/ec2/aws4_request,
     *     SignedHeaders=host;x-amz-date,
     *     Signature=calculated-signature
     * </pre>
     *
     * @return 返回所有的请求头信息
     */
    public Map<String, String> buildHeader() {
        // 访问凭证，格式：Credential=accessKey/credentialScope
        String credential = ConstantConfig.AwsSigner.CREDENTIAL + ConstantConfig.SpecialSymbols.EQUALS
                + this.accessKey + ConstantConfig.SpecialSymbols.SLASH + this.credentialScope;

        // 用于签名的请求头字段
        String signedHeaders = ConstantConfig.AwsSigner.SIGNED_HEADERS + ConstantConfig.SpecialSymbols.EQUALS + this.signedHeader;

        // 生成授权字符串
        this.authorizationHeader = ConstantConfig.AwsSigner.AWS4_HMAC_SHA256 + StringUtils.SPACE
                + credential + ConstantConfig.SpecialSymbols.COMMA + StringUtils.SPACE
                + signedHeaders + ConstantConfig.SpecialSymbols.COMMA + StringUtils.SPACE
                + ConstantConfig.AwsSigner.SIGNATURE + ConstantConfig.SpecialSymbols.EQUALS + this.signature;

        // 将授权字符串放入请求头中
        this.headers.put(ConstantConfig.HttpRequest.AUTHORIZATION, this.authorizationHeader);
        return this.headers;
    }

    /**
     * 生成请求中的授权参数，用于查询时拼接在资源中的授权信息
     * 示例：
     * <pre>
     *     ://s3.amazonaws.com/examplebucket/test.txt
     *     ?X-Amz-Algorithm=AWS4-HMAC-SHA256
     *     &X-Amz-Credential=AKIAIOSFODNN7EXAMPLE%2F20130524%2Fus-east-1%2Fs3%2Faws4_request
     *     &X-Amz-Date=20130524T000000Z
     *     &X-Amz-Expires=86400
     *     &X-Amz-SignedHeaders=host
     *     &X-Amz-Signature=<signature-value>
     * </pre>
     *
     * @param expiresTime 预签名 URL 的有效时间，对于签名V4，预设网址的最长有效期为7天，以秒表示
     *                    此值是一个整数。可以设置的最小值为 1，并且 最大值为 604800（七天）
     * @return 带有签名的 URL 地址
     */
    public String buildPreSignedUrl(Long expiresTime) {
        // 初始化请求地址
        this.requestUrl();
        this.headers.put(ConstantConfig.HttpRequest.HOST, this.host);

        // 访问凭证，格式：Credential=accessKey/credentialScope
        String credential = this.accessKey + ConstantConfig.SpecialSymbols.SLASH + this.credentialScope;

        // 获取用于签名的请求头
        this.setCanonicalHeader();

        // 构建请求参数所需的签名头
        this.queryParams.put(ConstantConfig.AwsSigner.X_AMZ_ALGORITHM, ConstantConfig.AwsSigner.AWS4_HMAC_SHA256);
        this.queryParams.put(ConstantConfig.AwsSigner.X_AMZ_CREDENTIAL, credential);
        this.queryParams.put(ConstantConfig.AwsSigner.X_AMZ_DATE, this.requestDateTime.format(UTC_PATTERN));
        this.queryParams.put(ConstantConfig.AwsSigner.X_AMZ_EXPIRES, Optional.ofNullable(expiresTime).orElse(TimeUnit.DAYS.toSeconds(NumberConstant.INTEGER_SEVEN)));
        this.queryParams.put(ConstantConfig.AwsSigner.X_AMZ_SIGNED_HEADERS, this.signedHeader);

        // URI 编码的查询字符串参数，须按键名称的字母顺序对规范查询字符串中的参数进行排序
        this.canonicalQueryString = UrlQueryUtil.buildUrlQueryParams(this.queryParams, true, true, StandardCharsets.UTF_8, null);

        // 创建规范性请求
        this.canonicalRequest = ConstantConfig.HttpRequest.GET + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + this.canonicalUri + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + this.canonicalQueryString + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + this.canonicalHeaders + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + this.signedHeader + ConstantConfig.SpecialSymbols.ENTER_LINUX
                + ConstantConfig.AwsSigner.UNSIGNED_PAYLOAD;

        // 创建要签名的字符串、计算签名
        this.stringToSign().signature();

        // 在查询参数中添加签名信息
        this.queryParams.put(ConstantConfig.AwsSigner.X_AMZ_SIGNATURE, this.signature);

        // 判断原链接中是否有问号, 有问号用 & 拼接, 没有问号用 ? 拼接
        boolean hasQuestionMark = StringUtils.contains(this.requestUrl, ConstantConfig.SpecialSymbols.QUESTION_MARK);
        String symbol = hasQuestionMark ? ConstantConfig.SpecialSymbols.AMPERSAND : ConstantConfig.SpecialSymbols.QUESTION_MARK;

        // 返回带有签名的查询参数
        String signParams = ConstantConfig.AwsSigner.X_AMZ_SIGNATURE + ConstantConfig.SpecialSymbols.EQUALS + this.signature;
        return this.requestUrl + symbol + this.canonicalQueryString + ConstantConfig.SpecialSymbols.AMPERSAND + signParams;
    }

    /**
     * 凭证范围，将生成的签名限制在指定的区域和服务范围内
     * 该字符串采用以下格式：YYYYMMDD/region/service/aws4_request
     *
     * @return 凭证范围
     */
    private AwsSignerUtil credentialScope() {
        // 当前的日期，格式为：YYYYMMDD
        String date = this.requestDateTime.format(DatePattern.PURE_DATE_FORMATTER);
        this.credentialScope = date + ConstantConfig.SpecialSymbols.SLASH
                + this.region + ConstantConfig.SpecialSymbols.SLASH
                + this.service + ConstantConfig.SpecialSymbols.SLASH
                + ConstantConfig.AwsSigner.TERMINATOR;
        return this;
    }

    /**
     * 设置规范化的，用于签名的请求头信息
     */
    private void setCanonicalHeader() {
        // 用于签名的请求头，按字母顺序排序、以分号分隔的小写请求标头名称列表。列表中的请求头在 CanonicalHeaders 字符串中包含的头相同
        this.signedHeader = this.headers.keySet().stream()
                .map(String::toLowerCase)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(ConstantConfig.SpecialSymbols.SEMICOLON));

        // 请求头及其值的列表，各个头名称和值用换行符 \n 分隔
        this.canonicalHeaders = this.headers.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
                .map(entry -> entry.getKey().toLowerCase().replaceAll(REGEX_WHITESPACE, StringUtils.SPACE) + ConstantConfig.SpecialSymbols.ENGLISH_COLON
                        + entry.getValue().replaceAll(REGEX_WHITESPACE, StringUtils.SPACE))
                .collect(Collectors.joining(ConstantConfig.SpecialSymbols.ENTER_LINUX)) + ConstantConfig.SpecialSymbols.ENTER_LINUX;
    }
}
