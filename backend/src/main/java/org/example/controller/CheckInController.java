package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.CheckInDto;
import org.example.service.CheckInService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("//airport-info-system/api")
public class CheckInController {
    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/tickets/{ticketId}/check-in")
    public ResponseEntity<CheckInDto> registerTicket(@PathVariable Integer ticketId,
                                                     @RequestBody @Valid CheckInDto dto){
        dto.setTicketId(ticketId);
        CheckInDto created = checkInService.createCheckIn(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getCheckInId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/tickets/{ticketId}/check-in")
    public ResponseEntity<CheckInDto> getCheckInByTicket(@PathVariable Integer ticketId) {
        return ResponseEntity.ok(checkInService.getByTicketId(ticketId));
    }

    @GetMapping("/flights/{flightId}/check-ins")
    public ResponseEntity<List<CheckInDto>> getCheckedInPassengers(@PathVariable Integer flightId) {
        return ResponseEntity.ok(checkInService.getCheckedInByFlightId(flightId));
    }

    @DeleteMapping("/tickets/{ticketId}/check-in")
    public ResponseEntity<Void> cancelCheckIn(@PathVariable Integer ticketId) {
        checkInService.cancelCheckIn(ticketId);
        return ResponseEntity.noContent().build();
    }
}
