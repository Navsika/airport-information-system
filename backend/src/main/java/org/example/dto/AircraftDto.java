package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
