package org.example.repository;

import org.example.entity.Airline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Integer> {
    @Query("SELECT a FROM Airline a WHERE " +
            "LOWER(a.iataCode) LIKE CONCAT(:iataCode, '%') AND " +
            "LOWER(a.airlineName) LIKE CONCAT(:airlineName, '%') AND " +
            "LOWER(a.country) LIKE CONCAT(:country, '%')")
    Page<Airline> findByFilters(
            @Param("iataCode") String iataCode,
            @Param("airlineName") String airlineName,
            @Param("country") String country,
            Pageable pageable
    );

    boolean existsByIataCode(String iataCode);

    boolean existsByAirlineName(String airlineName);
}
