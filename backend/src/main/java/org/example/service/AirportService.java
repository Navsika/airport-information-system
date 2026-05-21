package org.example.service;

import org.example.dto.AirportDto;
import org.example.entity.Airport;
import org.example.mapper.AirportMapper;
import org.example.repository.AirportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class AirportService {
    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;

    public AirportService(AirportRepository airportRepository, AirportMapper airportMapper) {
        this.airportRepository = airportRepository;
        this.airportMapper = airportMapper;
    }

    @Transactional(readOnly = true)
    public Page<AirportDto> getAirportsByFilter(String country, String city, String iataCode, int page, int size){
        return airportRepository.findByFilters(
                        normalizeTextFilter(country),
                        normalizeTextFilter(city),
                        normalizeTextFilter(iataCode),
                        PageRequest.of(page, size))
                .map(airportMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<AirportDto> getAllForDropdown(){
        return airportRepository.findAll()
                .stream().map(airportMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AirportDto getById(Integer id){
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Аэропорт с ID " + id + " не найден"));
        return airportMapper.toDto(airport);
    }

    @Transactional
    public AirportDto createAirport(AirportDto dto){
        if (airportRepository.existsByIataCode(dto.getIataCode())) {
            throw new IllegalArgumentException("Аэропорт с IATA-кодом " + dto.getIataCode() + " уже существует");
        }
        if (airportRepository.existsByAirportName(dto.getAirportName())) {
            throw new IllegalArgumentException("Аэропорт '" + dto.getAirportName() + "' уже существует");
        }
        return airportMapper.toDto(airportRepository.save(airportMapper.toEntity(dto)));
    }

    @Transactional
    public AirportDto updateAirport(Integer id, AirportDto dto){
        Airport exist = airportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Аэропорт с ID " + id + " не найден"));

        if (!exist.getAirportName().equals(dto.getAirportName())) {
            if (airportRepository.existsByAirportName(dto.getAirportName())) {
                throw new IllegalArgumentException("Аэропорт с названием '" + dto.getAirportName() + "' уже существует");
            }
            exist.setAirportName(dto.getAirportName());
        }
        Airport updated = airportRepository.save(exist);
        return airportMapper.toDto(updated);
    }

    private String normalizeTextFilter(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
