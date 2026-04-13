package org.example.mapper;

import org.example.dto.ScheduleDto;
import org.example.entity.Schedule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    Schedule toEntity(ScheduleDto scheduleDto);
    ScheduleDto toDto(Schedule schedule);
}
