package org.example.service;

import org.example.dto.AircraftModelDto;
import org.example.entity.AircraftModel;
import org.example.mapper.AircraftModelMapper;
import org.example.repository.AircraftModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AircraftModelService {
    final private AircraftModelRepository repository;
    final private AircraftModelMapper mapper;

    public AircraftModelService(AircraftModelRepository repository, AircraftModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<AircraftModelDto> getModelsByFilter(
            String modelName, String manufacturer, Short passengerCapacity,
            Integer cargoCapacity, Short maxSpeed, int page, int size) {

        return repository.findModelByFilters(modelName, manufacturer, passengerCapacity,
                        cargoCapacity, maxSpeed, PageRequest.of(page, size))
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public AircraftModelDto getModelById(Integer id){
        AircraftModel model = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Модель с ID "
                        + id + " не найдена"));
        return mapper.toDto(model);
    }

    @Transactional(readOnly = true)
    public AircraftModelDto getModelByName(String modelName) {
        AircraftModel model = repository.findByModelName(modelName)
                .orElseThrow(() -> new RuntimeException("Модель '" + modelName + "' не найдена"));
        return mapper.toDto(model);
    }

    @Transactional(readOnly = true)
    public List<AircraftModelDto> getAllForDropdown() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public AircraftModelDto createModel(AircraftModelDto aircraftModelDto){
        if (repository.existsByModelNameAndManufacturer(aircraftModelDto.getModelName(), aircraftModelDto.getManufacturer())){
            throw new IllegalArgumentException("Модель " + aircraftModelDto.getModelName() + " от производителя "
                    + aircraftModelDto.getManufacturer() + " уже существует");
        }

        AircraftModel model = mapper.toEntity(aircraftModelDto);
        AircraftModel saved = repository.save(model);
        return mapper.toDto(saved);
    }
}
