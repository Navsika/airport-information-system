package org.example.mapper;

import org.example.dto.QualificationDto;
import org.example.entity.Qualification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QualificationMapper {
    Qualification toEntity (QualificationDto qualificationDto);
    QualificationDto toDto (Qualification qualification);
}
