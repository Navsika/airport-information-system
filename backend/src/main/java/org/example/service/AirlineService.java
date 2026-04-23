package org.example.service;

import org.example.dto.AirlineDto;
import org.example.entity.Airline;
import org.example.mapper.AirlineMapper;
import org.example.repository.AirlineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AirlineService {
    private final AirlineRepository repository;
    private final AirlineMapper mapper;

    public AirlineService(AirlineRepository repository, AirlineMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<AirlineDto> getAirlinesByFilter(String iataCode, String airlineName, String country,
                                                int page, int size){
        return repository.findByFilters(iataCode, airlineName, country, PageRequest.of(page, size))
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<AirlineDto> getAllAirlinesForDropdown() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AirlineDto getById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Авиакомпания с ID " + id + " не найдена"));
    }

    @Transactional
    public AirlineDto createAirline(AirlineDto dto) {
        if (repository.existsByIataCode(dto.getIataCode())) {
            throw new IllegalArgumentException("Авиакомпания с IATA-кодом " + dto.getIataCode() + " уже существует");
        }
        if (repository.existsByAirlineName(dto.getAirlineName())) {
            throw new IllegalArgumentException("Авиакомпания с названием " + dto.getAirlineName() + " уже существует");
        }
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Transactional
    public AirlineDto updateAirline(Integer id, AirlineDto dto) {
        Airline existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Авиакомпания не найдена"));

        if (!existing.getIataCode().equals(dto.getIataCode()) && repository.existsByIataCode(dto.getIataCode())) {
            throw new IllegalArgumentException("Код " + dto.getIataCode() + " уже занят");
        }
        if (!existing.getAirlineName().equals(dto.getAirlineName()) && repository.existsByAirlineName(dto.getAirlineName())) {
            throw new IllegalArgumentException("Название " + dto.getAirlineName() + " уже занято");
        }

        existing.setIataCode(dto.getIataCode());
        existing.setAirlineName(dto.getAirlineName());
        existing.setCountry(dto.getCountry());
        return mapper.toDto(repository.save(existing));
    }
}
