package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer scheduleId;

    private String flightNumber;
    private Integer airlineId;
    private Integer departureAirport;
    private Integer arrivalAirport;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Integer arrivalDayOffset;
}
