package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.PassengerDto;
import org.example.service.PassengerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/airport-info-system/api/passengers")
public class PassengerController {
    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping("/search")
    public ResponseEntity<PassengerDto> searchByPassport(@RequestParam String passportNumber) {
        return ResponseEntity.ok(passengerService.getByPassportNumber(passportNumber));
    }

    @GetMapping("/list")
    public ResponseEntity<List<PassengerDto>> getList() {
        return ResponseEntity.ok(passengerService.getAllForDropdown());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(passengerService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PassengerDto> createPassenger(@RequestBody @Valid PassengerDto dto) {
        PassengerDto created = passengerService.createPassenger(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getPassengerId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
