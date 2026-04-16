package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckInDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer checkInId;

    private Integer ticketId;
    private LocalDateTime checkInTime;
    private String counterNumber;
    private String terminal;
    private Short baggageCount;
    private BigDecimal totalBaggageWeight;
}
