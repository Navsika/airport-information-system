package org.example.service;

import org.example.dto.EmployeeDto;
import org.example.entity.Employee;
import org.example.mapper.EmployeeMapper;
import org.example.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> getEmployeesByFilter(String category, String lastName,
                                                  LocalDate hireDateFrom, LocalDate hireDateTo, int page, int size) {
        return repository.findByFilters(category, lastName, hireDateFrom, hireDateTo, PageRequest.of(page, size))
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllForDropdown() {
        return repository.findAll()
                .stream().map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeDto getById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Сотрудник с ID " + id + " не найден"));
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Transactional
    public EmployeeDto updateEmployee(Integer id, EmployeeDto dto) {
        Employee exist = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        exist.setFirstName(dto.getFirstName());
        exist.setLastName(dto.getLastName());
        exist.setMiddleName(dto.getMiddleName());
        exist.setCategory(dto.getCategory());

        return mapper.toDto(repository.save(exist));
    }
}
