package org.example.service;

import org.example.dto.FlightDto;
import org.example.dto.FlightListDto;
import org.example.entity.Flight;
import org.example.mapper.FlightMapper;
import org.example.repository.FlightRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FlightService {
    private final FlightRepository repository;
    private final FlightMapper flightMapper;
    private final StatusHistoryService statusHistoryService;

    public FlightService(FlightRepository repository, FlightMapper flightMapper, StatusHistoryService statusHistoryService) {
        this.repository = repository;
        this.flightMapper = flightMapper;
        this.statusHistoryService = statusHistoryService;
    }

    @Transactional(readOnly = true)
    public Page<FlightListDto> getFlightListWithRoute(
            String status, OffsetDateTime dateFrom, OffsetDateTime dateTo,
            Integer departureAirportId, Integer arrivalAirportId,
            int page, int size
    ) {
        return repository.findFlightsWithRoute(
                status, dateFrom, dateTo, departureAirportId, arrivalAirportId, PageRequest.of(page, size, Sort.by("scheduledDeparture").descending()));
    }

    @Transactional(readOnly = true)
    public FlightDto getById(Integer flightId) {
        return repository.findById(flightId)
                .map(flightMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Рейс не найден"));
    }

    @Transactional
    public FlightDto updateFlight(Integer flightId, FlightDto dto, String reasonOfChange) {
        Flight flight = repository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Рейс не найден"));
        String oldStatus = flight.getStatus();
        String newStatus = dto.getStatus();
        if (dto.getStatus() != null) flight.setStatus(dto.getStatus());
        if (dto.getGate() != null) flight.setGate(dto.getGate());
        if (dto.getAircraftId() != null) flight.setAircraftId(dto.getAircraftId());
        if (dto.getActualDeparture() != null) flight.setActualDeparture(dto.getActualDeparture());
        if (dto.getActualArrival() != null) flight.setActualArrival(dto.getActualArrival());

        if (newStatus != null && !newStatus.equals(oldStatus)){
            statusHistoryService.logStatusChange(flightId, oldStatus, newStatus, reasonOfChange);
        }
        Flight saved = repository.save(flight);
        return flightMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<Integer> getAircraftModelIdByFlightId(Integer flightId) {
        return repository.findAircraftModelIdByFlightId(flightId);
    }

    @Transactional
    public Integer getAircraftCapacityByFlightId(Integer flightId){
        return repository.findAircraftCapacityByFlightId(flightId);
    }

    @Transactional(readOnly = true)
    public String getFlightStatus(Integer flightId){
        return repository.findById(flightId)
                .map(Flight::getStatus)
                .orElseThrow(() -> new RuntimeException("Рейс с ID " + flightId + " не найден"));
    }
}