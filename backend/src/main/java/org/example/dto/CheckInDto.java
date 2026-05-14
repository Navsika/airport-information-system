package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
