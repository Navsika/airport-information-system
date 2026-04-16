package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
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
@Table(name = "aircrafts")
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aircraft_id")
    private Integer aircraftId;
    @Column(name = "registration_number",length = 8, nullable = false, unique = true)
    private String registrationNumber;
    @Column(name = "model_id", nullable = false)
    private Integer modelId;
    @Column(name = "airline_id", nullable = false)
    private Integer airlineId;
    // тут нужно будет проверить
    @Column(name = "manufacture_year", nullable = false)
    private Short manufactureYear;
    @PastOrPresent(message = "Дата последнего техобслуживания не может быть в будущем")
    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;
    @PositiveOrZero(message = "Часы налета не могут быть отрицательными")
    @Column(name = "flight_hours", nullable = false)
    private Integer flightHours;
}
