package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AircraftModelDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer modelId;

    private String modelName;
    private String manufacturer;
    private Integer passengerCapacity;
    private Integer cargoCapacity;
    private Integer maxSpeed;
}
