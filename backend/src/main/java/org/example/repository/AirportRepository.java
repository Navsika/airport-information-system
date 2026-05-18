package org.example.repository;

import org.example.entity.Airport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Integer> {
    @Query("SELECT a FROM Airport a WHERE " +
            "(:country IS NULL OR LOWER(a.country) LIKE LOWER(CONCAT(:country, '%'))) AND " +
            "(:city IS NULL OR LOWER(a.city) LIKE LOWER(CONCAT(:city, '%'))) AND " +
            "(:iataCode IS NULL OR LOWER(a.iataCode) LIKE LOWER(CONCAT(:iataCode, '%')))")
    Page<Airport> findByFilters(
            @Param("country") String country,
            @Param("city") String city,
            @Param("iataCode") String iataCode,
            Pageable pageable
    );

    boolean existsByIataCode(String iataCode);

    boolean existsByAirportName(String airportName);
}
