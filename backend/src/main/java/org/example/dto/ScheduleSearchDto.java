package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ScheduleSearchDto {
    private Integer scheduleId;
    private String flightNumber;
    private String airlineName;
    private String departureCity;
    private String arrivalCity;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Short arrivalDayOffset;
}