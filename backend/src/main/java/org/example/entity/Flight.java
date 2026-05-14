package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_id")
    private Integer flightId;
    @Column(name = "schedule_id", nullable = false)
    private Integer scheduleId;
    @Column(name = "scheduled_departure", nullable = false)
    private OffsetDateTime scheduledDeparture;
    @Column(name = "scheduled_arrival", nullable = false)
    private OffsetDateTime scheduledArrival;
    @Column(name = "actual_departure")
    private OffsetDateTime actualDeparture;
    @Column(name = "actual_arrival")
    private OffsetDateTime actualArrival;
    @Column(name = "aircraft_id")
    private Integer aircraftId;
    @Column(name = "gate", length = 5)
    private String gate;
    @Pattern(regexp = "^(Scheduled|Delayed|Cancelled|Check-in|Boarding|Departed|Arrived)$", message = "Статус должен быть одним из представленных")
    @Column(name = "status", length = 20, nullable = false)
    private String status;
}
