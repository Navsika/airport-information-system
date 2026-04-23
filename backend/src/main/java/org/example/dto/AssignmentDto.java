package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class AssignmentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer assignmentId;

    private Integer flightId;
    private Integer employeeId;
    private String employeeRole;
}
