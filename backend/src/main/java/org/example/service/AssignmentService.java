package org.example.service;

import org.example.dto.AssignmentDto;
import org.example.entity.Assignment;
import org.example.mapper.AssignmentMapper;
import org.example.repository.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
@Service
public class AssignmentService {
    private static final int MAX_FLIGHT_ATTENDANTS = 3;
    private static final Set<String> SINGLE_CREW_ROLES = Set.of(
            "Commander",
            "Co-pilot",
            "Senior Flight Attendant"
    );

    private final AssignmentRepository repository;
    private final FlightService flightService;
    private final QualificationService qualificationService;
    private final AssignmentMapper mapper;

    public AssignmentService(AssignmentRepository repository, FlightService flightService, QualificationService qualificationService, AssignmentMapper mapper) {
        this.repository = repository;
        this.flightService = flightService;
        this.qualificationService = qualificationService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<AssignmentDto> getCrewByFlightId(Integer flightId){
        return repository.findByFlightId(flightId)
                .stream().map(mapper::toDto)
                .toList();
    }

    @Transactional
    public AssignmentDto assignCrew(AssignmentDto dto){
        if (repository.existsByFlightIdAndEmployeeId(dto.getFlightId(), dto.getEmployeeId())) {
            throw new IllegalArgumentException("Данный сотрудник уже назначен на данный рейс");
        }
        if (SINGLE_CREW_ROLES.contains(dto.getEmployeeRole())
                && repository.existsByFlightIdAndEmployeeRole(dto.getFlightId(), dto.getEmployeeRole())) {
            throw new IllegalArgumentException("На рейс уже назначен сотрудник с ролью " + dto.getEmployeeRole());
        }
        if ("Flight Attendant".equals(dto.getEmployeeRole())
                && repository.countByFlightIdAndEmployeeRole(dto.getFlightId(), "Flight Attendant") >= MAX_FLIGHT_ATTENDANTS) {
            throw new IllegalArgumentException("На рейс нельзя назначить больше " + MAX_FLIGHT_ATTENDANTS + " бортпроводников");
        }
        if (repository.hasOverlappingAssignment(dto.getEmployeeId(), dto.getFlightId())){
            throw new IllegalArgumentException("Сотрудник не может быть назначен на данный рейс: занят на другом рейсе");
        }

        if ("Commander".equals(dto.getEmployeeRole()) || "Co-pilot".equals(dto.getEmployeeRole())) {
            Integer modelId = flightService.getAircraftModelIdByFlightId(dto.getFlightId())
                    .orElseThrow(() -> new RuntimeException("На рейс не назначен самолет"));

            if (!qualificationService.checkPilotQualification(dto.getEmployeeId(), modelId)) {
                throw new IllegalArgumentException(
                        "Пилот не имеет действующего допуска на модель воздушного судна, назначенного на этот рейс"
                );
            }
        }
        Assignment assignment = mapper.toEntity(dto);
        Assignment saved = repository.save(assignment);
        return mapper.toDto(saved);
    }

    @Transactional
    public void removeCrew(Integer assignmentId){
        repository.deleteById(assignmentId);
    }
}
