package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.AirportDto;

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

    public static Airport toEntity(AirportDto airportDto) {
        Airport airport = new Airport();
        if (airportDto.getAirportId() != null) {
            airport.setAirportId(airportDto.getAirportId());
        }
        //то есть если нам передали id то мы его передаем в entit
        airport.setAirportName(airportDto.getAirportName());
        airport.setIataCode(airportDto.getIataCode());
        airport.setCountry(airportDto.getCountry());
        airport.setCity(airportDto.getCity());
        return airport;
    }

    public AirportDto toDto(){
        AirportDto airportDto = new AirportDto();
        if (this.getAirportId() != null){
            airportDto.setAirportId(this.getAirportId());
        }
        airportDto.setAirportName(this.getAirportName());
        airportDto.setIataCode(this.getIataCode());
        airportDto.setCountry(this.getCountry());
        airportDto.setCity(this.getCity());
        return airportDto;
    }
}


