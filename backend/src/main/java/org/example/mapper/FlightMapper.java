package org.example.mapper;

import org.example.dto.FlightDto;
import org.example.entity.Flight;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FlightMapper {
    Flight toEntity (FlightDto flightDto);
    FlightDto toDto(Flight flight);
}
