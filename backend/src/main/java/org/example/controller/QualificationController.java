package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.QualificationDto;
import org.example.service.QualificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/airport-info-system/api/employees/{pilotId}/qualifications")
public class QualificationController {
    private final QualificationService qualificationService;

    public QualificationController(QualificationService qualificationService) {
        this.qualificationService = qualificationService;
    }

    @GetMapping
    public ResponseEntity<List<QualificationDto>> getByPilot(@PathVariable Integer pilotId) {
        return ResponseEntity.ok(qualificationService.getByPilotId(pilotId));
    }

    @PostMapping
    public ResponseEntity<QualificationDto> create(
            @PathVariable Integer pilotId,
            @RequestBody @Valid QualificationDto dto) {

        dto.setPilotId(pilotId);
        QualificationDto created = qualificationService.createQualification(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getQualificationId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{qualificationId}")
    public ResponseEntity<QualificationDto> update(
            @PathVariable Integer qualificationId,
            @RequestBody @Valid QualificationDto dto) {

        return ResponseEntity.ok(qualificationService.updateQualification(qualificationId, dto));
    }
}
