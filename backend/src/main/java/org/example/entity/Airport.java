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
@Table(name = "airports")
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airport_id")
    private Integer airportId;
    @Column(name = "iata_code",length = 4, nullable = false, unique = true)
    private String iataCode;
    @Column(name = "airport_name", length = 150, nullable = false, unique = true)
    private String airportName;
    @Column(name = "city", length = 50, nullable = false)
    private String city;
    @Column(name = "country", length = 50, nullable = false)
    private String country;
}
