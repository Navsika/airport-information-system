package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "status_histories")
public class StatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_history_id")
    private Integer statusHistoryId;
    @Column(name = "flight_id", nullable = false)
    private Integer flightId;
    @Pattern(regexp = "^(Scheduled|Delayed|Cancelled|Check-in|Boarding|Departed|Arrived)$",
            message = "Статус должен быть одним из допустимых значений")
    @Column(name = "status", length = 20, nullable = false)
    private String status;
    @Column(name = "change_time", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime changeTime;
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
}
