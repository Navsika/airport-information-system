package org.example.mapper;

import org.example.dto.PassengerDto;
import org.example.entity.Passenger;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PassengerMapper {
    Passenger toEntity(PassengerDto passengerDto);
    PassengerDto toDto(Passenger passenger);
}
