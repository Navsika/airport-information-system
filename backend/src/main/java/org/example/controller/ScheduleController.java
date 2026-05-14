package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.ScheduleDto;
import org.example.dto.ScheduleSearchDto;
import org.example.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/airport-info-system/api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<ScheduleSearchDto>> searchByCities(
            @RequestParam String departureCity,
            @RequestParam String arrivalCity) {
        return ResponseEntity.ok(scheduleService.searchByCities(departureCity, arrivalCity));
    }

    @PostMapping
    public ResponseEntity<ScheduleDto> create(@RequestBody @Valid ScheduleDto dto) {
        ScheduleDto created = scheduleService.createSchedule(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getScheduleId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/search/{flightNumber}")
    public ResponseEntity<ScheduleDto> getByNumber(@PathVariable String flightNumber){
        return ResponseEntity.ok(scheduleService.getByFlightNumber(flightNumber));
    }
}
