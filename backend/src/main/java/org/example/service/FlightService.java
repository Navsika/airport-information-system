package org.example.service;

import org.example.dto.FlightCreateDto;
import org.example.dto.FlightDto;
import org.example.dto.FlightListDto;
import org.example.dto.FlightStatsDto;
import org.example.entity.Flight;
import org.example.entity.Schedule;
import org.example.mapper.FlightMapper;
import org.example.repository.AircraftRepository;
import org.example.repository.CheckInRepository;
import org.example.repository.FlightRepository;
import org.example.repository.FlightSpecifications;
import org.example.repository.ScheduleRepository;
import org.example.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class FlightService {
    private static final Map<String, Set<String>> ALLOWED_STATUS_TRANSITIONS = Map.of(
            "Scheduled", Set.of("Check-in", "Delayed", "Cancelled"),
            "Delayed", Set.of("Check-in", "Cancelled"),
            "Check-in", Set.of("Boarding", "Cancelled"),
            "Boarding", Set.of("Departed", "Cancelled"),
            "Departed", Set.of("Arrived"),
            "Arrived", Set.of(),
            "Cancelled", Set.of()
    );

    private final FlightRepository repository;
    private final ScheduleRepository scheduleRepository;
    private final AircraftRepository aircraftRepository;
    private final TicketRepository ticketRepository;
    private final CheckInRepository checkInRepository;
    private final FlightMapper flightMapper;
    private final StatusHistoryService statusHistoryService;

    public FlightService(FlightRepository repository, ScheduleRepository scheduleRepository, AircraftRepository aircraftRepository, TicketRepository ticketRepository, CheckInRepository checkInRepository, FlightMapper flightMapper, StatusHistoryService statusHistoryService) {
        this.repository = repository;
        this.scheduleRepository = scheduleRepository;
        this.aircraftRepository = aircraftRepository;
        this.ticketRepository = ticketRepository;
        this.checkInRepository = checkInRepository;
        this.flightMapper = flightMapper;
        this.statusHistoryService = statusHistoryService;
    }

    @Transactional(readOnly = true)
    public Page<FlightListDto> getFlightListWithRoute(
            String status, OffsetDateTime dateFrom, OffsetDateTime dateTo,
            Integer departureAirportId, Integer arrivalAirportId,
            String aircraftRegNumber, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduledDeparture").descending());
        Page<Flight> flightPage = repository.findAll(
                FlightSpecifications.withFilters(status, dateFrom, dateTo, departureAirportId, arrivalAirportId, aircraftRegNumber),
                pageable
        );
        List<Integer> flightIds = flightPage.getContent().stream()
                .map(Flight::getFlightId)
                .toList();
        if (flightIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, flightPage.getTotalElements());
        }
        List<FlightListDto> flightsWithRoute = repository.findFlightsWithRouteByIds(flightIds);
        return new PageImpl<>(flightsWithRoute, pageable, flightPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public FlightDto getById(Integer flightId) {
        return repository.findById(flightId)
                .map(flightMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Рейс не найден"));
    }

    @Transactional
    public FlightDto createFlight(FlightCreateDto dto) {
        Schedule schedule = scheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Шаблон расписания не найден"));

        OffsetDateTime scheduledDeparture = toFlightDateTime(dto.getFlightDate(), schedule.getDepartureTime());
        OffsetDateTime scheduledArrival = toFlightDateTime(
                dto.getFlightDate().plusDays(schedule.getArrivalDayOffset()),
                schedule.getArrivalTime()
        );

        if (!scheduledArrival.isAfter(scheduledDeparture)) {
            throw new IllegalArgumentException("Плановое время прибытия должно быть позже вылета");
        }
        if (repository.existsByScheduleIdAndScheduledDeparture(schedule.getScheduleId(), scheduledDeparture)) {
            throw new IllegalArgumentException("Рейс по этому шаблону на выбранную дату уже создан");
        }
        if (dto.getAircraftId() != null) {
            if (!aircraftRepository.existsById(dto.getAircraftId())) {
                throw new RuntimeException("Самолет не найден");
            }
            if (repository.countAircraftOverlaps(dto.getAircraftId(), scheduledDeparture, scheduledArrival) > 0) {
                throw new IllegalArgumentException("Самолет уже назначен на другой рейс в это время");
            }
        }

        Flight flight = new Flight();
        flight.setScheduleId(schedule.getScheduleId());
        flight.setScheduledDeparture(scheduledDeparture);
        flight.setScheduledArrival(scheduledArrival);
        flight.setAircraftId(dto.getAircraftId());
        flight.setGate(normalizeGate(dto.getGate()));
        flight.setStatus("Scheduled");

        Flight saved = repository.save(flight);
        statusHistoryService.logStatusChange(saved.getFlightId(), null, "Scheduled", "Рейс создан");
        return flightMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public FlightStatsDto getFlightStats(Integer flightId) {
        Flight flight = repository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Рейс не найден"));
        long soldTickets = ticketRepository.countByFlightId(flightId);
        long checkedInPassengers = checkInRepository.countByFlightId(flightId);
        int showUpPercent = soldTickets == 0 ? 0 : (int) Math.round((checkedInPassengers * 100.0) / soldTickets);
        BigDecimal baggageWeight = Optional.ofNullable(checkInRepository.sumBaggageWeightByFlightId(flightId))
                .orElse(BigDecimal.ZERO);
        Integer capacity = flight.getAircraftId() == null ? null : repository.findAircraftCapacityByFlightId(flightId);
        Integer baggageLimit = flight.getAircraftId() == null ? null : repository.findAircraftCargoCapacityByFlightId(flightId);
        int baggagePercent = baggageLimit == null || baggageLimit == 0
                ? 0
                : (int) Math.round((baggageWeight.doubleValue() * 100.0) / baggageLimit);

        return new FlightStatsDto(
                soldTickets,
                capacity,
                checkedInPassengers,
                showUpPercent,
                baggageWeight.doubleValue(),
                baggageLimit,
                baggagePercent
        );
    }

    @Transactional
    public FlightDto updateFlight(Integer flightId, FlightDto dto, String reasonOfChange) {
        Flight flight = repository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Рейс не найден"));
        String oldStatus = flight.getStatus();
        String newStatus = dto.getStatus();
        if (newStatus != null) {
            validateStatusTransition(oldStatus, newStatus);
            flight.setStatus(newStatus);
        }
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

    @Transactional
    public FlightDto updateFlightStatus(Integer flightId, String newStatus, String reasonOfChange) {
        Flight flight = repository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Рейс не найден"));
        String oldStatus = flight.getStatus();
        validateStatusTransition(oldStatus, newStatus);
        if (!newStatus.equals(oldStatus)) {
            flight.setStatus(newStatus);
            statusHistoryService.logStatusChange(flightId, oldStatus, newStatus, reasonOfChange);
        }
        return flightMapper.toDto(repository.save(flight));
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

    @Transactional(readOnly = true)
    public Flight getFlightEntity(Integer flightId) {
        return repository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Рейс с ID " + flightId + " не найден"));
    }

    private void validateStatusTransition(String oldStatus, String newStatus) {
        if (oldStatus == null || oldStatus.equals(newStatus)) {
            return;
        }
        Set<String> allowedNextStatuses = ALLOWED_STATUS_TRANSITIONS.get(oldStatus);
        if (allowedNextStatuses == null) {
            throw new IllegalArgumentException("Неизвестный текущий статус рейса: " + oldStatus);
        }
        if (!allowedNextStatuses.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Недопустимый переход статуса рейса: " + oldStatus + " -> " + newStatus
            );
        }
    }

    private OffsetDateTime toFlightDateTime(LocalDate date, java.time.LocalTime time) {
        ZoneOffset offset = OffsetDateTime.now().getOffset();
        return OffsetDateTime.of(date, time, offset);
    }

    private String normalizeGate(String gate) {
        if (gate == null || gate.isBlank()) {
            return null;
        }
        return gate.trim().toUpperCase();
    }
}
