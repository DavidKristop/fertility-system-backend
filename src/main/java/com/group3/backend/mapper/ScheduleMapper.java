package com.group3.backend.mapper;

import com.group3.backend.dto.response.Treatment.ScheduleResponse;
import com.group3.backend.model.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    @Mapping(source = "scheduleServices", target = "services")
    ScheduleResponse toResponse(Schedule schedule);
    
}
