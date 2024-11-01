package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>Minio响应的时间字段适配器</p>
 * <p>2024-04-27 22:15</p>
 *
 * @author Dan
 **/
public class MinioResponseDateAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final DateTimeFormatter MINIO_RESPONSE_DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public LocalDateTime unmarshal(String value) {
        return LocalDateTime.parse(value, MINIO_RESPONSE_DATE_FORMAT);
    }

    @Override
    public String marshal(LocalDateTime value) {
        return MINIO_RESPONSE_DATE_FORMAT.format(value);
    }
}
