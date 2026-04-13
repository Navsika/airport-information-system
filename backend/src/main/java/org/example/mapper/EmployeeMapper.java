package org.example.mapper;

import org.example.dto.EmployeeDto;
import org.example.entity.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    Employee toEntity (EmployeeDto employeeDto);
    EmployeeDto toDto (Employee employee);
}
