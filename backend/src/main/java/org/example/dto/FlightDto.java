package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer flightId;

    private Integer scheduleId;
    private OffsetDateTime scheduledDeparture;
    private OffsetDateTime scheduledArrival;
    private OffsetDateTime actualDeparture;
    private OffsetDateTime actualArrival;
    private Integer aircraftId;
    private String gate;
    private String status;
}
