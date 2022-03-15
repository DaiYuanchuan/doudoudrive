package com.doudoudrive.common.model.convert;

import com.doudoudrive.common.model.dto.model.OpLogInfo;
import com.doudoudrive.common.model.pojo.LogOp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

}
