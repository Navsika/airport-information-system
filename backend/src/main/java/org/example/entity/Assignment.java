package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"flight_id", "employee_id"}))
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Integer assignmentId;
    @Column(name = "flight_id", nullable = false)
    private Integer flightId;
    @Column(name = "employee_id")
    private Integer employeeId;
    @Pattern(regexp = "^(Commander|Co-pilot|Senior Flight Attendant|Flight Attendant)$", message = "Роль в команде четко зафиксирована")
    @Column(name = "employee_role", length = 60, nullable = false)
    private String employeeRole;
}
