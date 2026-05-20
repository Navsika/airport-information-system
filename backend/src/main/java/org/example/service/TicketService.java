package org.example.service;

import org.example.dto.TicketDto;
import org.example.entity.Ticket;
import org.example.mapper.TicketMapper;
import org.example.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TicketService {
    private final TicketRepository repository;
    private final TicketMapper mapper;
    private final FlightService flightService;

    public TicketService(TicketRepository repository, TicketMapper mapper, FlightService flightService) {
        this.repository = repository;
        this.mapper = mapper;
        this.flightService = flightService;
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getByFlightId(Integer flightId){
        return repository.findByFlightId(flightId)
                .stream().map(mapper::toDto)
                .toList();
    }

    @Transactional
    public TicketDto sellTicket(Integer flightId, TicketDto dto){
        Integer capacity = flightService.getAircraftCapacityByFlightId(flightId);
        long sold = repository.countByFlightId(flightId);
        if (sold >= capacity){
            throw new IllegalArgumentException("Все места на рейсе проданы");
        }

        if (dto.getPassengerId() != null && repository.existsByPassengerIdAndFlightId(dto.getPassengerId(), flightId)){
            throw new IllegalArgumentException("Пассажир уже имеет билет на данный рейс");
        }

        if (repository.existsByFlightIdAndSeatNumber(flightId, dto.getSeatNumber())){
            throw new IllegalArgumentException("Место " + dto.getSeatNumber() + " уже продано");
        }

        String seatNum = dto.getSeatNumber();
        try {
            int row = Integer.parseInt(seatNum.replaceAll("[^0-9]", ""));
            if (row < 1 || row > 30){
                throw new IllegalArgumentException("Выход за пределы количества рядов");
            }
        } catch (NumberFormatException e){
            throw new IllegalArgumentException("Некорректный формат места: " + seatNum);
        }

        Ticket ticket = mapper.toEntity(dto);
        ticket.setFlightId(flightId);
        ticket.setTicketNumber(generateTicketNumber(flightId));
        ticket.setPurchaseDate(LocalDate.now());

        Ticket save = repository.save(ticket);
        return mapper.toDto(save);
    }

    @Transactional(readOnly = true)
    public Integer findFlightIdByTicketId(Integer ticketId){
        Ticket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Билета с таким ID не существует"));
        return ticket.getFlightId();
    }

    @Transactional(readOnly = true)
    public String getFlightStatusByTicketId(Integer ticketId) {
        Ticket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Билет с ID " + ticketId + " не найден"));
        return flightService.getFlightStatus(ticket.getFlightId());
    }

    private String generateTicketNumber(Integer flightId) {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        long uniquePart = System.nanoTime() % 100_000;
        return String.format("T%d-%s-%05d", flightId, datePart, uniquePart);
    }
}
