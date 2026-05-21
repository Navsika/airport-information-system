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
    private final FlightService flightService;

    public PassengerService(PassengerRepository repository, PassengerMapper mapper, FlightService flightService) {
        this.repository = repository;
        this.mapper = mapper;
        this.flightService = flightService;
    }

    @Transactional(readOnly = true)
    public PassengerDto getByPassportNumber(String passportNumber) {
        return repository.findByPassportNumber(passportNumber)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Пассажир с паспортом " + passportNumber + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<PassengerDto> getAllForDropdown() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PassengerDto> searchByName(String lastName, String firstName) {
        return repository.findByNameFilters(normalizeTextFilter(lastName), normalizeTextFilter(firstName)).stream()
                .map(mapper::toDto)
                .toList();
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
        String flightStatus = flightService.getFlightStatus(flightId);
        return repository.findPassengersByFlightId(flightId).stream()
                .map(passenger -> withActualFlightStatus(passenger, flightStatus))
                .toList();
    }

    private FlightPassengerDto withActualFlightStatus(FlightPassengerDto passenger, String flightStatus) {
        String passengerStatus = resolvePassengerFlightStatus(passenger.isCheckedIn(), flightStatus);
        return new FlightPassengerDto(
                passenger.getLastName(),
                passenger.getFirstName(),
                passenger.getPassportNumber(),
                passenger.getSeatNumber(),
                passenger.getTicketClass(),
                passenger.isCheckedIn(),
                passengerStatus
        );
    }

    private String resolvePassengerFlightStatus(boolean checkedIn, String flightStatus) {
        if (checkedIn) {
            return "CHECKED_IN";
        }
        if ("Departed".equals(flightStatus) || "Arrived".equals(flightStatus)) {
            return "NO_SHOW";
        }
        if ("Cancelled".equals(flightStatus)) {
            return "CANCELLED";
        }
        return "PENDING";
    }

    @Transactional(readOnly = true)
    public PassengerDto getById(Integer id) {
        Passenger passenger = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Пассажир с ID " + id + " не найден"));
        return mapper.toDto(passenger);
    }

    private String normalizeTextFilter(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
