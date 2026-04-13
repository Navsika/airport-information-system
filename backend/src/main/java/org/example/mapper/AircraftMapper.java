package org.example.mapper;

import org.example.dto.AircraftDto;
import org.example.dto.AirportDto;
import org.example.entity.Aircraft;
import org.example.entity.Airport;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AircraftMapper {
    Aircraft toEntity(AircraftDto aircraftDto);
    AircraftDto toDto(Aircraft aircraft);
}