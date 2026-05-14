package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QualificationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer qualificationId;

    private Integer pilotId;
    private Integer modelId;
    private LocalDate qualificationDate;
    private LocalDate validUntil;
}
