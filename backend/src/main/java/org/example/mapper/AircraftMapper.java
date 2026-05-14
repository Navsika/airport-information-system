package org.example.mapper;

import org.example.dto.AircraftDto;
import org.example.entity.Aircraft;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AircraftMapper {
    Aircraft toEntity(AircraftDto aircraftDto);
    AircraftDto toDto(Aircraft aircraft);
}