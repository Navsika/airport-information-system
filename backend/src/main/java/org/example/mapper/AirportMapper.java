package org.example.mapper;

import org.example.dto.AirportDto;
import org.example.entity.Airport;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AirportMapper {
    Airport toEntity(AirportDto airportDto);
    AirportDto toDto(Airport airport);
}
