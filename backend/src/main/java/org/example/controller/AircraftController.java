package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.AircraftDto;
import org.example.service.AircraftService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
@RestController
@RequestMapping("/airport-info-system/api/aircrafts")
public class AircraftController {
    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping
    public ResponseEntity<Page<AircraftDto>> getAircrafts(
            @RequestParam(required = false) Integer modelId,
            @RequestParam(required = false) Integer airlineId,
            @RequestParam(required = false) Integer manufactureYear,
            @RequestParam(required = false) LocalDate lastMaintenanceDate,
            @RequestParam(required = false) Integer flightHours,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AircraftDto> result =  aircraftService.getAircraftByFilter(modelId,
                airlineId,
                manufactureYear,
                lastMaintenanceDate,
                flightHours,
                page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftDto> getById(@PathVariable Integer id){
        return ResponseEntity.ok(aircraftService.getAircraftById(id));
    }

    @GetMapping("/registration/{registrationNumber}")
    public ResponseEntity<AircraftDto> getByRegistrationNumber(@PathVariable String registrationNumber){
        return ResponseEntity.ok(aircraftService.getAircraftByRegistrationNumber(registrationNumber));
    }

    @PostMapping
    public ResponseEntity<AircraftDto> createAircraft(@RequestBody @Valid AircraftDto aircraftDto){
        AircraftDto created = aircraftService.createAircraft(aircraftDto);
        URI location = URI.create("/api/aircrafts" + created.getAircraftId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftDto> updateAircraft(
            @PathVariable Integer id,
            @RequestBody @Valid AircraftDto aircraftDto) {
        return ResponseEntity.ok(aircraftService.updateAircraft(id, aircraftDto));
    }
}
