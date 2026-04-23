package org.example.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.AirportDto;
import org.example.service.AirportService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/airport-info-system/api/airports")
public class AirportController {
    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping
    public ResponseEntity<Page<AirportDto>> getAll(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String iataCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(airportService.getAirportsByFilter(country, city, iataCode, page, size));
    }

    @GetMapping("/list")
    public ResponseEntity<List<AirportDto>> getListForDropdown(){
        return ResponseEntity.ok(airportService.getAllForDropdown());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirportDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(airportService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AirportDto> createAirport(@RequestBody @Valid AirportDto dto) {
        AirportDto created = airportService.createAirport(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getAirportId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirportDto> updateAirport(@PathVariable Integer id, @RequestBody @Valid AirportDto dto) {
        return ResponseEntity.ok(airportService.updateAirport(id, dto));
    }
}
