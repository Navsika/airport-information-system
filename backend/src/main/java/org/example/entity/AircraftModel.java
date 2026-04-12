package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "aircraft_models")
public class AircraftModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Integer modelId;
    @Column(name = "model_name",length = 30, nullable = false, unique = true)
    private String modelName;
    @Column(name = "manufacturer", length = 50, nullable = false)
    private String manufacturer;
    @Positive(message = "Вместимость пассажиров должна быть больше 0")
    @Column(name = "passenger_capacity", nullable = false)
    private Integer passengerCapacity;
    @Min(value = 0, message = "Грузоподъемность судна не может быть отрицательной")
    @Column(name = "cargo_capacity", nullable = false)
    private Integer cargoCapacity;
    @Positive(message = "Максимальная скорость превышает 0")
    @Column(name = "max_speed", nullable = false)
    private Integer maxSpeed;
}
