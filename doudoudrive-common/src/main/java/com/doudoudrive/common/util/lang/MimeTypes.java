package com.doudoudrive.common.util.lang;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.StrUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * <p>根据文件后缀确定文件的mimeType类型的工具类</p>
 * <p>2022-05-22 17:48</p>
 *
 * @author Dan
 **/
@Slf4j
public class MimeTypes {

    /**
     * The default MIME type
     */
    public static final String DEFAULT_MIMETYPE = "application/octet-stream";

    /**
     * 当前实例[单例]
     */
    private static MimeTypes mimetypes = null;

    /**
     * 对mime类型的映射
     */
    private final HashMap<String, String> extensionToMimetypeMap = new HashMap<>();

    /**
     * 需要读取的文件路径
     */
    private static final String FILE_PATH = "/data/mime.types";

    /**
     * 分隔符
     */
    private static final String DELIM = " \t";

    /**
     * 禁止实例化
     */
    private MimeTypes() {
    }

    /**
     * 获取 MimeTypes 实例
     */
    @SneakyThrows
    public synchronized static MimeTypes getInstance() {
        if (mimetypes != null) {
            return mimetypes;
        }

        mimetypes = new MimeTypes();
        try (InputStream is = mimetypes.getClass().getResourceAsStream(FILE_PATH)) {
            if (is != null) {
                // 加载文件 mime.types
                log.debug("Loading mime types from file in the classpath: mime.types");
                mimetypes.loadMimeTypes(is);
            } else {
                // 找不到文件 mime.types
                log.warn("Unable to find 'mime.types' file in classpath");
            }
        }

        return mimetypes;
    }

    /**
     * 自定义需要加载的 mime.types
     *
     * @param is 包含有mime类型的流
     */
    @SneakyThrows
    public void loadMimeTypes(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = br.readLine()) != null) {
            line = line.trim();

            // 忽略注释和空行
            if (line.startsWith(ConstantConfig.SpecialSymbols.COMMENT_SIGN) || line.length() == NumberConstant.INTEGER_ZERO) {
                continue;
            }

            StringTokenizer st = new StringTokenizer(line, DELIM);
            if (st.countTokens() <= NumberConstant.INTEGER_ONE) {
                continue;
            }

            String extension = st.nextToken();
            if (st.hasMoreTokens()) {
                String mimetype = st.nextToken();
                extensionToMimetypeMap.put(extension.toLowerCase(), mimetype);
            }
        }
    }

    /**
     * 根据文件名获取文件 mime 类型
     *
     * @param fileName 文件名
     * @return 返回文件对应的mime类型
     */
    public String getMimeTypes(String fileName) {
        int lastPeriodIndex = fileName.lastIndexOf(ConstantConfig.SpecialSymbols.DOT);
        if (lastPeriodIndex > NumberConstant.INTEGER_ZERO && lastPeriodIndex + NumberConstant.INTEGER_ONE < fileName.length()) {
            String ext = fileName.substring(lastPeriodIndex + NumberConstant.INTEGER_ONE).toLowerCase();
            if (extensionToMimetypeMap.containsKey(ext)) {
                return extensionToMimetypeMap.get(ext);
            }
        }

        return DEFAULT_MIMETYPE;
    }

    /**
     * 向mime类型的映射Map中添加新的值
     *
     * @param key   key
     * @param value value
     */
    public void setMimeTypes(String key, String value) {
        extensionToMimetypeMap.put(key, value);
    }

    /**
     * 获取当前文件对应的 mime 类型
     *
     * @param file {@link File} 需要获取的文件
     * @return 返回文件对应的mime类型
     */
    public String getMimeTypes(File file) {
        if (!file.exists()) {
            return DEFAULT_MIMETYPE;
        }

        // 获取当前文件的扩展名
        String ext = FileTypeUtil.getType(file);
        if (StrUtil.isBlank(ext)) {
            return DEFAULT_MIMETYPE;
        }

        if (extensionToMimetypeMap.containsKey(ext.toLowerCase())) {
            return extensionToMimetypeMap.get(ext.toLowerCase());
        }

        return DEFAULT_MIMETYPE;
    }
}
