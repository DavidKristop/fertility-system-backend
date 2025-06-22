package com.group3.backend.mapper;

import com.group3.backend.dto.response.Schedule.ScheduleResponse;
import com.group3.backend.model.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    @Mapping(source = "scheduleServices", target = "services")
    @Mapping(source = "scheduleResult", target = "scheduleResult")
    ScheduleResponse toResponse(Schedule schedule);

    
}
