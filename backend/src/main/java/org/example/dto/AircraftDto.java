package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AircraftDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer aircraftId;
    private String registrationNumber;
    private Integer modelId;
    private Integer airlineId;
    private Short manufactureYear;
    private LocalDate lastMaintenanceDate;
    private Integer flightHours;
}
