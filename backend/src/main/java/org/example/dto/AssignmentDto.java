package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class AssignmentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer assignmentId;

    private Integer flightId;
    private Integer employeeId;
    private String employeeRole;
}
