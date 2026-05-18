package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.FlightDto;
import org.example.dto.FlightListDto;
import org.example.dto.FlightPassengerDto;
import org.example.dto.FlightStatusUpdateDto;
import org.example.dto.StatusHistoryDto;
import org.example.service.FlightService;
import org.example.service.PassengerService;
import org.example.service.StatusHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/airport-info-system/api/flights")
public class FlightController {
    private final FlightService flightService;
    private final PassengerService passengerService;
    private final StatusHistoryService statusHistoryService;

    public FlightController(FlightService flightService, PassengerService passengerService, StatusHistoryService statusHistoryService) {
        this.flightService = flightService;
        this.passengerService = passengerService;
        this.statusHistoryService = statusHistoryService;
    }

    @GetMapping
    public ResponseEntity<Page<FlightListDto>> getFlights(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) OffsetDateTime dateFrom,
            @RequestParam(required = false) OffsetDateTime dateTo,
            @RequestParam(required = false) Integer departureAirportId,
            @RequestParam(required = false) Integer arrivalAirportId,
            @RequestParam(required = false) String aircraftRegNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        return ResponseEntity.ok(flightService.getFlightListWithRoute(
                status, dateFrom, dateTo, departureAirportId, arrivalAirportId, aircraftRegNumber, page, size
        ));
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDto> getById(@PathVariable Integer flightId) {
        return ResponseEntity.ok(flightService.getById(flightId));
    }

    @PatchMapping("/{flightId}")
    public ResponseEntity<FlightDto> updateFlight(
            @PathVariable Integer flightId,
            @RequestBody @Valid FlightDto dto,
            @RequestParam(required = false) String reasonOfChange
    ) {

        FlightDto updated = flightService.updateFlight(flightId, dto, reasonOfChange);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{flightId}/status")
    public ResponseEntity<FlightDto> updateStatus(
            @PathVariable Integer flightId,
            @RequestBody @Valid FlightStatusUpdateDto dto
    ) {
        return ResponseEntity.ok(flightService.updateFlightStatus(
                flightId,
                dto.getStatus(),
                dto.getReasonOfChange()
        ));
    }

    @GetMapping("/{flightId}/passengers")
    public ResponseEntity<List<FlightPassengerDto>> getPassengers(@PathVariable Integer flightId) {
        return ResponseEntity.ok(passengerService.getPassengersByFlightId(flightId));
    }

    @GetMapping("/{flightId}/status-history")
    public ResponseEntity<List<StatusHistoryDto>> getStatusHistory(@PathVariable Integer flightId){
        return ResponseEntity.ok(statusHistoryService.getHistoryByFlightId(flightId));
    }
}
