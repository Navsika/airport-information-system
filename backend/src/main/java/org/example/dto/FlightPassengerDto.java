package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FlightPassengerDto {
    private String lastName;
    private String firstName;
    private String passportNumber;
    private String seatNumber;
    private String ticketClass;
    private boolean isCheckedIn;
    private String passengerFlightStatus;
}
