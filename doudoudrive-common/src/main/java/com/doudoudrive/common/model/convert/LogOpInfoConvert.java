package com.doudoudrive.common.model.convert;

import com.doudoudrive.common.model.dto.model.OpLogInfo;
import com.doudoudrive.common.model.dto.model.aliyun.AliCloudCdnLogModel;
import com.doudoudrive.common.model.pojo.LogOp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

/**
 * <p>API操作日志信息的POJO类、DTO、Model等相关的实体信息转换器</p>
 * <p>2022-03-15 11:28</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LogOpInfoConvert {

    /**
     * 将 OpLogInfo(操作信息) 类型转换为 LogOp(API操作日志)
     *
     * @param opLogInfo opLogInfo(操作信息)
     * @return logOp(API操作日志)
     */
    @Mapping(target = "mobile", source = "isMobile")
    LogOp logOpConvert(OpLogInfo opLogInfo);

    @Mappings({
            @Mapping(target = "businessType", constant = "CDN"),
            @Mapping(target = "location", constant = "0-0-内网IP 内网IP"),
            @Mapping(target = "requestUri", expression = "java(cdnLogModel.getUri() + cdnLogModel.getUriParam())"),
            @Mapping(target = "referer", expression = "java(cdnLogModel.getScheme() + \"://\" + cdnLogModel.getDomain() + cdnLogModel.getUri() + cdnLogModel.getUriParam())"),
            @Mapping(target = "title", constant = "LOG"),
            @Mapping(target = "success", expression = "java(Boolean.TRUE)"),
            @Mapping(target = "requestTime", ignore = true),
            @Mapping(target = "responseTime", ignore = true),
            @Mapping(target = "costTime", source = "requestTime")
    })
    LogOp cdnLogModelConvert(AliCloudCdnLogModel cdnLogModel);

}
