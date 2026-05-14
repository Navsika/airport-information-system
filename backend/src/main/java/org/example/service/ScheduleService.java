package org.example.service;

import org.example.dto.ScheduleDto;
import org.example.dto.ScheduleSearchDto;
import org.example.entity.Schedule;
import org.example.mapper.ScheduleMapper;
import org.example.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository repository;
    private final ScheduleMapper mapper;

    public ScheduleService(ScheduleRepository repository, ScheduleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ScheduleSearchDto> searchByCities(String depCity, String arrCity){
        if (depCity == null || arrCity == null) return Collections.emptyList();
        return repository.findByDepartureCityAndArrivalCity(depCity, arrCity);
    }

    @Transactional
    public ScheduleDto createSchedule(ScheduleDto dto){
        if (repository.existsByFlightNumber(dto.getFlightNumber())){
            throw new IllegalArgumentException("Шаблон с номером " + dto.getFlightNumber() + " уже существует");
        }
        Schedule schedule = mapper.toEntity(dto);
        return mapper.toDto(repository.save(schedule));
    }

    @Transactional(readOnly = true)
    public ScheduleDto getByFlightNumber(String flightNumber){
        Schedule schedule = repository.findByFlightNumber(flightNumber)
                .orElseThrow(()-> new RuntimeException("Шаблон с номером " + flightNumber + " не найден"));
        return mapper.toDto(schedule);
    }
}
