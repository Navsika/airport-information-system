package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.EmployeeDto;
import org.example.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/airport-info-system/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) LocalDate hireDateFrom,
            @RequestParam(required = false) LocalDate hireDateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(employeeService.getEmployeesByFilter(category, lastName, hireDateFrom, hireDateTo, page, size));
    }

    @GetMapping("/list")
    public ResponseEntity<List<EmployeeDto>> getListForDropdown() {
        return ResponseEntity.ok(employeeService.getAllForDropdown());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }


    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody @Valid EmployeeDto dto) {
        EmployeeDto created = employeeService.createEmployee(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getEmployeeId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Integer id, @RequestBody @Valid EmployeeDto dto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }
}
