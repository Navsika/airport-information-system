package org.example.mapper;

import org.example.dto.AirlineDto;
import org.example.entity.Airline;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AirlineMapper {
    Airline toEntity(AirlineDto airlineDto);
    AirlineDto toDto(Airline airline);
}
