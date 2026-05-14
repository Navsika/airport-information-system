package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.AircraftModelDto;
import org.example.service.AircraftModelService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/airport-info-system/api/models")
public class AircraftModelController {
    private final AircraftModelService aircraftModelService;

    public AircraftModelController(AircraftModelService aircraftModelService) {
        this.aircraftModelService = aircraftModelService;
    }

    @GetMapping
    public ResponseEntity<Page<AircraftModelDto>> getAll(
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) Short passengerCapacity,
            @RequestParam(required = false) Integer cargoCapacity,
            @RequestParam(required = false) Short maxSpeed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(aircraftModelService.getModelsByFilter(modelName, manufacturer,
                passengerCapacity, cargoCapacity, maxSpeed, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftModelDto> getById(@PathVariable Integer id){
        return ResponseEntity.ok(aircraftModelService.getModelById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<AircraftModelDto>> getList() {
        return ResponseEntity.ok(aircraftModelService.getAllForDropdown());
    }

    @PostMapping
    public ResponseEntity<AircraftModelDto> createModel(@RequestBody @Valid AircraftModelDto aircraftModelDto){

        AircraftModelDto created = aircraftModelService.createModel(aircraftModelDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getModelId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

}
