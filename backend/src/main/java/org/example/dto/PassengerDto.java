package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassengerDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer passengerId;

    private String firstName;
    private String lastName;
    private String middleName;
    private String passportNumber;
    private LocalDate passportExpiryDate;
}
