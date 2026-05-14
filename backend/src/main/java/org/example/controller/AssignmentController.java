package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.AssignmentDto;
import org.example.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/airport-info-system/api/flights/{flightId}/crew")
public class AssignmentController {
    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public ResponseEntity<List<AssignmentDto>> getCrew(@PathVariable Integer flightId){
        return ResponseEntity.ok(assignmentService.getCrewByFlightId(flightId));
    }

    @PostMapping
    public ResponseEntity<AssignmentDto> assignCrew(@PathVariable Integer flightId,
                                                    @RequestBody @Valid AssignmentDto dto){
        dto.setFlightId(flightId);
        AssignmentDto created = assignmentService.assignCrew(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{assignmentId}")
                .buildAndExpand(created.getAssignmentId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> removeCrew(@PathVariable Integer assignmentId){
        assignmentService.removeCrew(assignmentId);
        return ResponseEntity.noContent().build();
    }
}
