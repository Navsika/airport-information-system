package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QualificationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer qualificationId;

    private Integer pilotId;
    private Integer modelId;
    private LocalDate qualificationDate;
    private LocalDate validUntil;
}
