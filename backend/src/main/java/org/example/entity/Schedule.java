package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;
    @Column(name = "flight_number",length = 10, nullable = false, unique = true)
    private String flightNumber;
    @Column(name = "airline_id", nullable = false)
    private Integer airlineId;
    @Column(name = "departure_airport", nullable = false)
    private Integer departureAirport;
    @Column(name = "arrival_airport", nullable = false)
    private Integer arrivalAirport;
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;
    @Positive(message = "Количество дней не может быть отрицательным")
    @Column(name = "arrival_day_offset", nullable = false)
    private Short arrivalDayOffset;
}
