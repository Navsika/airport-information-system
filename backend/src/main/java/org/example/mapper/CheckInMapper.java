package org.example.mapper;

import org.example.dto.CheckInDto;
import org.example.entity.CheckIns;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CheckInMapper {
    CheckIns toEntity (CheckInDto checkInDto);
    CheckInDto toDto (CheckIns checkIns);
}
