package org.example.mapper;

import org.example.dto.AssignmentDto;
import org.example.entity.Assignment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    Assignment toEntity(AssignmentDto assignmentDto);
    AssignmentDto toDto(Assignment assignment);
}
