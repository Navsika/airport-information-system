package org.example.controller;

import jakarta.servlet.Servlet;
import jakarta.validation.Valid;
import org.example.dto.TicketDto;
import org.example.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/airport-info-system/api/flights/{flightId}/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketDto>> getTicketsByFlight(@PathVariable Integer flightId) {
        return ResponseEntity.ok(ticketService.getByFlightId(flightId));
    }

    @PostMapping
    public ResponseEntity<TicketDto> sellTicket(
            @PathVariable Integer flightId,
            @RequestBody @Valid TicketDto dto
       ){
        TicketDto created = ticketService.sellTicket(flightId, dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getTicketId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }


}
