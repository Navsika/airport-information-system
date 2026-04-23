package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.FlightDto;
import org.example.dto.FlightListDto;
import org.example.service.FlightService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/airport-info-system/api/flights")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<Page<FlightListDto>> getFlights(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) OffsetDateTime dateFrom,
            @RequestParam(required = false) OffsetDateTime dateTo,
            @RequestParam(required = false) Integer departureAirportId,
            @RequestParam(required = false) Integer arrivalAirportId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        return ResponseEntity.ok(flightService.getFlightListWithRoute(
                status, dateFrom, dateTo, departureAirportId, arrivalAirportId, page, size
        ));
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDto> getById(@PathVariable Integer flightId) {
        return ResponseEntity.ok(flightService.getById(flightId));
    }

    @PatchMapping("/{flightId}")
    public ResponseEntity<FlightDto> update(
            @PathVariable Integer flightId,
            @RequestBody @Valid FlightDto dto) {

        FlightDto updated = flightService.updateFlight(flightId, dto);
        return ResponseEntity.ok(updated);
    }
}
