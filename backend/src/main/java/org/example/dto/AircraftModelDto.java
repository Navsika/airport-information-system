package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AircraftModelDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer modelId;

    private String modelName;
    private String manufacturer;
    private Short passengerCapacity;
    private Integer cargoCapacity;
    private Short maxSpeed;
}
