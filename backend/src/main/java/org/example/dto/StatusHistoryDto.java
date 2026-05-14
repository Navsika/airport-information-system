package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusHistoryDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer statusHistoryId;

    private Integer flightId;
    private String status;
    private LocalDateTime changeTime;
    private String reason;
}
