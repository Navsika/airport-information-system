package org.example.mapper;

import org.example.dto.AirlineDto;
import org.example.entity.Airline;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirlineMapper {
    Airline toEntity(AirlineDto airlineDto);
    AirlineDto toDto(Airline airline);
}
