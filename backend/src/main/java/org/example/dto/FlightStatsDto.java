package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FlightStatsDto {
    private long soldTickets;
    private Integer capacity;
    private long checkedInPassengers;
    private int showUpPercent;
    private double baggageWeight;
    private Integer baggageLimit;
    private int baggagePercent;
}
