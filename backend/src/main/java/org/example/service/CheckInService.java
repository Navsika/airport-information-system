package org.example.service;

import org.example.dto.CheckInDto;
import org.example.entity.Flight;
import org.example.entity.CheckIns;
import org.example.mapper.CheckInMapper;
import org.example.repository.CheckInRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class CheckInService {
    private final CheckInRepository repository;
    private final CheckInMapper mapper;
    private final TicketService ticketService;
    private final FlightService flightService;

    public CheckInService(CheckInRepository repository, CheckInMapper mapper, TicketService ticketService, FlightService flightService) {
        this.repository = repository;
        this.mapper = mapper;
        this.ticketService = ticketService;
        this.flightService = flightService;
    }

    @Transactional
    public CheckInDto createCheckIn(CheckInDto dto){
        if (repository.existsByTicketId(dto.getTicketId())){
            throw new IllegalArgumentException("Этот билет уже зарегистрирован");
        }
        Integer flightId = ticketService.findFlightIdByTicketId(dto.getTicketId());
        Flight flight = flightService.getFlightEntity(flightId);
        String flightStatus = flight.getStatus();
        if (!Set.of("Check-in").contains(flightStatus)) {
            throw new IllegalArgumentException("Регистрация доступна только в статусе Check-in (текущий статус: " + flightStatus + ")");
        }
        if (dto.getBaggageCount() == null || dto.getTotalBaggageWeight() == null ||
                dto.getBaggageCount() < 0 || dto.getTotalBaggageWeight().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Количество и вес багажа не могут быть отрицательными");
        }

        CheckIns checkIn = mapper.toEntity(dto);
        CheckIns saved = repository.save(checkIn);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public CheckInDto getByTicketId(Integer ticketId) {
        return repository.findByTicketId(ticketId)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Регистрация для билета " + ticketId + " не найдена"));
    }

    @Transactional(readOnly = true)
    public List<CheckInDto> getCheckedInByFlightId(Integer flightId) {
        return repository.findByFlightId(flightId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public void cancelCheckIn(Integer ticketId){
        String flightStatus = ticketService.getFlightStatusByTicketId(ticketId);
        if ("Departed".equals(flightStatus) || "Arrived".equals(flightStatus) || "Cancelled".equals(flightStatus)) {
            throw new IllegalArgumentException("Рейс уже вылетел, невозможно отменить регистрацию");
        }
        repository.deleteByTicketId(ticketId);
    }
}
