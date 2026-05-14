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
public class EmployeeDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer employeeId;

    private String firstName;
    private String lastName;
    private String middleName;
    private String category;
    private LocalDate hireDate;
}
