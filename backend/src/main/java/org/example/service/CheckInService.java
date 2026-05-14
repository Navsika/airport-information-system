package org.example.service;

import org.example.dto.CheckInDto;
import org.example.entity.CheckIns;
import org.example.mapper.CheckInMapper;
import org.example.repository.CheckInRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CheckInService {
    private final CheckInRepository repository;
    private final CheckInMapper mapper;
    private final TicketService ticketService;

    public CheckInService(CheckInRepository repository, CheckInMapper mapper, TicketService ticketService) {
        this.repository = repository;
        this.mapper = mapper;
        this.ticketService = ticketService;
    }

    @Transactional
    public CheckInDto createCheckIn(CheckInDto dto){
        if (repository.existsByTicketId(dto.getTicketId())){
            throw new IllegalArgumentException("Этот билет уже зарегистрирован");
        }
        String flightStatus = ticketService.getFlightStatusByTicketId(dto.getTicketId());
        if ("Departed".equals(flightStatus) || "Arrived".equals(flightStatus) || "Cancelled".equals(flightStatus)) {
            throw new IllegalArgumentException("Регистрация на этот рейс закрыта (статус: " + flightStatus + ")");
        }
        if (dto.getBaggageCount() < 0 || dto.getTotalBaggageWeight().compareTo(BigDecimal.ZERO) < 0) {
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
