package org.example.service;

import org.example.dto.FlightPassengerDto;
import org.example.dto.PassengerDto;
import org.example.entity.Passenger;
import org.example.mapper.PassengerMapper;
import org.example.repository.PassengerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PassengerService {
    private final PassengerRepository repository;
    private final PassengerMapper mapper;

    public PassengerService(PassengerRepository repository, PassengerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public PassengerDto getByPassportNumber(String passportNumber) {
        return repository.findByPassportNumber(passportNumber)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Пассажир с паспортом " + passportNumber + " не найден"));
    }

    @Transactional
    public PassengerDto createPassenger(PassengerDto dto) {
        if (repository.existsByPassportNumber(dto.getPassportNumber())) {
            throw new IllegalArgumentException("Пассажир с таким номером паспорта уже зарегистрирован");
        }
        Passenger passenger = mapper.toEntity(dto);
        return mapper.toDto(repository.save(passenger));
    }

    @Transactional(readOnly = true)
    public List<FlightPassengerDto> getPassengersByFlightId(Integer flightId) {
        return repository.findPassengersByFlightId(flightId);
    }

    @Transactional(readOnly = true)
    public PassengerDto getById(Integer id) {
        Passenger passenger = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Пассажир с ID " + id + " не найден"));
        return mapper.toDto(passenger);
    }   
}
