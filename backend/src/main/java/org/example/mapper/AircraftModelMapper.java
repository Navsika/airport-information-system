package org.example.mapper;

import org.example.dto.AircraftModelDto;
import org.example.entity.AircraftModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AircraftModelMapper {
    AircraftModel toEntity(AircraftModelDto aircraftModelDto);
    AircraftModelDto toDto(AircraftModel aircraftModel);
}
