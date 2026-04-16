package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirportDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer airportId;

    private String iataCode;
    private String airportName;
    private String city;
    private String country;
}
