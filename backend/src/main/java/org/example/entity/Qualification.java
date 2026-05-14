package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
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
@Table(name = "qualifications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"pilot_id", "model_id"}))
public class Qualification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qualification_id")
    private Integer qualificationId;
    @Column(name = "pilot_id", nullable = false)
    private Integer pilotId;
    @Column(name = "model_id", nullable = false)
    private Integer modelId;
    @PastOrPresent(message = "Дата получения лицензии не может быть в будущем")
    @Column(name = "qualification_date", nullable = false)
    private LocalDate qualificationDate;
    @Column(name = "valid_until")
    private LocalDate validUntil;
}
