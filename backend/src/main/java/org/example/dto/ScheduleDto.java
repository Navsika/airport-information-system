package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Short arrivalDayOffset;
}
