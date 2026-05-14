package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
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
@Table(name = "tickets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"flight_id", "seat_number"}))
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;
    @Column(name = "ticket_number", length = 20, nullable = false, unique = true)
    private String ticketNumber;
    @Column(name = "passenger_id")
    private Integer passengerId;
    @Column(name = "flight_id", nullable = false)
    private Integer flightId;
    @Pattern(regexp = "^(Business|Economy)$", message = "Класс обслуживания на борту: Бизнес, Эконом")
    @Column(name = "ticket_class", length = 10, nullable = false)
    private String ticketClass;
    @Column(name = "seat_number", length = 10, nullable = false)
    private String seatNumber;
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
}
