package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer ticketId;

    private String ticketNumber;
    private Integer passengerId;
    private Integer flightId;
    private String ticketClass;
    private String seatNumber;
    private LocalDate purchaseDate;
}
