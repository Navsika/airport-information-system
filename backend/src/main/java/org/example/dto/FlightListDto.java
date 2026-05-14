package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class FlightListDto {
    private Integer flightId;
    private String flightNumber;
    private String airlineName;
    private String departureAirport;
    private String arrivalAirport;
    private OffsetDateTime scheduledDeparture;
    private OffsetDateTime scheduledArrival;
    private OffsetDateTime actualDeparture;
    private OffsetDateTime actualArrival;
    private String status;
    private String gate;
    private String aircraftRegNumber;
}