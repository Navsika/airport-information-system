package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
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
@Table(name = "passengers")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passenger_id")
    private Integer passengerId;
    @Column(name = "first_name",length = 50, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;
    @Column(name = "middle_name", length = 50)
    private String middleName;
    @Column(name = "passport_number", length = 10, nullable = false, unique = true)
    private String passportNumber;
    @Future(message = "Дата недействительности паспорта должна быть в будущем")
    @Column(name = "passport_expiry_date", nullable = false)
    private LocalDate passportExpiryDate;
}
