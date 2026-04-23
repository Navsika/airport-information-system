package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.AircraftModelDto;
import org.example.service.AircraftModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/airport-info-system/api/models")
public class AircraftModelController {
    private final AircraftModelService aircraftModelService;

    public AircraftModelController(AircraftModelService aircraftModelService) {
        this.aircraftModelService = aircraftModelService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftModelDto> getById(@PathVariable Integer id){
        return ResponseEntity.ok(aircraftModelService.getModelById(id));
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
