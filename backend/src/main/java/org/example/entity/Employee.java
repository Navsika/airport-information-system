package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;
    @Column(name = "first_name",length = 50, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;
    @Column(name = "middle_name", length = 50)
    private String middleName;
    @Pattern(regexp = "^(Pilot|FlightAttendant)$", message = "Должность должна быть Pilot или FlightAttendant")
    @Column(name = "category", length = 20, nullable = false)
    private String category;
    @PastOrPresent(message = "Дата найма не может быть в будущем")
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;
}
