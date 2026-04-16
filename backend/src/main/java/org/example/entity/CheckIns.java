package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "check_ins")
public class CheckIns {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "check_in_id")
    private Integer checkInId;
    @Column(name = "ticket_id", nullable = false, unique = true)
    private Integer ticketId;
    @Column(name = "check_in_time", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime checkInTime;
    @Column(name = "counter_number", length = 5)
    private String counterNumber;
    @Column(name = "terminal", length = 3, nullable = false)
    private String terminal;
    @PositiveOrZero(message = "Количество багажа не может быть отрицательным")
    @Column(name = "baggage_count", nullable = false)
    private Short baggageCount;
    @PositiveOrZero(message = "Вес багажа не может быть отрицательным")
    @Column(name = "total_baggage_weight", nullable = false)
    private BigDecimal totalBaggageWeight;
}
