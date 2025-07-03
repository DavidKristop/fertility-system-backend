package com.group3.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.group3.backend.dto.response.ReminderReponse;
import com.group3.backend.model.Reminder;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ReminderMapper {
    @Mapping(target = "sendTo", source = "reminder.sendTo")
    
    public ReminderReponse toResponse(Reminder reminder);
}
