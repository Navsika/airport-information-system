package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirlineDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer airlineId;

    private String iataCode;
    private String airlineName;
    private String country;
}
