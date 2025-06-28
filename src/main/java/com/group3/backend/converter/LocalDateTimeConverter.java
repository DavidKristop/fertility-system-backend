package com.group3.backend.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, LocalDateTime> {

    private static final ZoneId UTC_PLUS_7 = ZoneId.of("Asia/Bangkok");

    @Override
    public LocalDateTime convertToDatabaseColumn(LocalDateTime attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.atZone(UTC_PLUS_7).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(LocalDateTime dbData) {
        if (dbData == null) {
            return null;
        }
        return dbData.atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC_PLUS_7).toLocalDateTime();
    }
}
