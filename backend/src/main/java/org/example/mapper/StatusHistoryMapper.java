package org.example.mapper;

import org.example.dto.StatusHistoryDto;
import org.example.entity.StatusHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusHistoryMapper {
    StatusHistory toEntity (StatusHistoryDto statusHistoryDto);
    StatusHistoryDto toDto (StatusHistory statusHistory);
}
