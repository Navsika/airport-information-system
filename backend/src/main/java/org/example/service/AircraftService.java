package org.example.service;

import org.example.dto.AircraftDto;
import org.example.entity.Aircraft;
import org.example.mapper.AircraftMapper;
import org.example.repository.AircraftRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AircraftService {
    private final AircraftRepository aircraftRepository;
    private final AircraftMapper aircraftMapper;

    public AircraftService(AircraftRepository aircraftRepository, AircraftMapper aircraftMapper) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftMapper = aircraftMapper;
    }

    @Transactional(readOnly = true)
    public Page<AircraftDto> getAircraftByFilter
            (Integer modelId,
             Integer airlineId,
             Integer manufactureYear,
             LocalDate lastMaintenanceDate,
             Integer flightHours,
             int page,
             int pageSize){
        Pageable pageable = PageRequest.of(page, pageSize);
        return aircraftRepository.findByFilters(modelId,
                airlineId,
                manufactureYear,
                lastMaintenanceDate,
                flightHours, pageable).map(aircraftMapper::toDto);
    }

    @Transactional(readOnly = true)
    public AircraftDto getAircraftByRegistrationNumber(String registrationNumber){
        Aircraft aircraft = aircraftRepository
                .findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new RuntimeException("Самолет с номером "
                        + registrationNumber + " не найден"));
        return aircraftMapper.toDto(aircraft);
    }

    @Transactional(readOnly = true)
    public AircraftDto getAircraftById(Integer id){
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Самолет с ID "
                        + id + " не найден"));
        return aircraftMapper.toDto(aircraft);
    }
    @Transactional
    public AircraftDto createAircraft(AircraftDto aircraftDto){
        if (aircraftRepository.findByRegistrationNumber(aircraftDto.getRegistrationNumber()).isPresent()) {
            throw new IllegalArgumentException("Самолет с таким регистрационным номером уже существует");
        }
        Aircraft aircraft = aircraftMapper.toEntity(aircraftDto);
        Aircraft saved = aircraftRepository.save(aircraft);
        return aircraftMapper.toDto(saved);
    }

    @Transactional
    public AircraftDto updateAircraft(Integer id, AircraftDto aircraftDto){
        Aircraft exist = aircraftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Самолет с ID " + id + " не найден"));
        exist.setLastMaintenanceDate(aircraftDto.getLastMaintenanceDate());
        exist.setAirlineId(aircraftDto.getAirlineId());
        Aircraft updated = aircraftRepository.save(exist);
        return aircraftMapper.toDto(updated);
    }

    @Transactional
    public void addFlightHours(Integer aircraftId, int hours){
        aircraftRepository.addFlightHours(hours, aircraftId);
    }
}
