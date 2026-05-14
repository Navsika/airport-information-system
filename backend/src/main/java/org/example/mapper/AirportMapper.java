package org.example.mapper;

import org.example.dto.AirportDto;
import org.example.entity.Airport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirportMapper {
    Airport toEntity(AirportDto airportDto);
    AirportDto toDto(Airport airport);
}
