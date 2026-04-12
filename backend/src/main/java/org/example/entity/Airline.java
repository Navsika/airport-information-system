package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "airlines")
public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airline_id")
    private Integer airlineId;
    @Column(name = "iata_code",length = 3, nullable = false, unique = true)
    private String iataCode;
    @Column(name = "airline_name", length = 100, nullable = false, unique = true)
    private String airlineName;
    @Column(name = "country", length = 50, nullable = false)
    private String country;
}
