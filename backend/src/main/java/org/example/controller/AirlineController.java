package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.AirlineDto;
import org.example.service.AirlineService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

public class AirlineController {
    private final AirlineService airlineService;

    public AirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @GetMapping
    public ResponseEntity<Page<AirlineDto>> getAll(
            @RequestParam(required = false) String iataCode,
            @RequestParam(required = false) String airlineName,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(airlineService.getAirlinesByFilter(iataCode, airlineName, country, page, size));
    }

    @GetMapping("/list")
    public ResponseEntity<List<AirlineDto>> getListForDropdown() {
        return ResponseEntity.ok(airlineService.getAllAirlinesForDropdown());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirlineDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(airlineService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AirlineDto> createAirline(@RequestBody @Valid AirlineDto dto) {
        AirlineDto created = airlineService.createAirline(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getAirlineId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirlineDto> updateAirlines(@PathVariable Integer id, @RequestBody @Valid AirlineDto dto) {
        return ResponseEntity.ok(airlineService.updateAirline(id, dto));
    }
}