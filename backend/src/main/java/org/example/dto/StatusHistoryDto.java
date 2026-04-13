package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusHistoryDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer statusHistoryId;

    private Integer flightId;
    private String status;
    private LocalDateTime changeTime;
    private String reason;
}
