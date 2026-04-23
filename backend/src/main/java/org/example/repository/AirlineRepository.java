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
            "(:iataCode IS NULL OR LOWER(a.iataCode) = LOWER(:iataCode)) AND " +
            "(:airlineName IS NULL OR LOWER(a.airlineName) LIKE LOWER(CONCAT('%', :airlineName, '%'))) AND " +
            "(:country IS NULL OR a.country = :country)")
    Page<Airline> findByFilters(
            @Param("iataCode") String iataCode,
            @Param("airlineName") String airlineName,
            @Param("country") String country,
            Pageable pageable
    );

    boolean existsByIataCode(String iataCode);

    boolean existsByAirlineName(String airlineName);
}
