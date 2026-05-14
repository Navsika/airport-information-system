package org.example.repository;

import org.example.dto.FlightListDto;
import org.example.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    @Query("SELECT new org.example.dto.FlightListDto(" +
            "f.flightId, s.flightNumber, al.airlineName, " +
            "dep.airportName, arr.airportName, " +
            "f.scheduledDeparture, f.scheduledArrival, " +
            "f.actualDeparture, f.actualArrival, " +
            "f.status, f.gate, ac.registrationNumber) " +
            "FROM Flight f " +
            "JOIN Schedule s ON f.scheduleId = s.scheduleId " +
            "JOIN Airline al ON s.airlineId = al.airlineId " +
            "JOIN Airport dep ON s.departureAirport = dep.airportId " +
            "JOIN Airport arr ON s.arrivalAirport = arr.airportId " +
            "LEFT JOIN Aircraft ac ON f.aircraftId = ac.aircraftId " +
            "WHERE (:status IS NULL OR f.status = :status) " +
            "AND (:dateFrom IS NULL OR f.scheduledDeparture >= :dateFrom) " +
            "AND (:dateTo IS NULL OR f.scheduledDeparture <= :dateTo) " +
            "AND (:departureAirportId IS NULL OR s.departureAirport = :departureAirportId) " +
            "AND (:arrivalAirportId IS NULL OR s.arrivalAirport = :arrivalAirportId)")
    Page<FlightListDto> findFlightsWithRoute(
            @Param("status") String status,
            @Param("dateFrom") OffsetDateTime dateFrom,
            @Param("dateTo") OffsetDateTime dateTo,
            @Param("departureAirportId") Integer departureAirportId,
            @Param("arrivalAirportId") Integer arrivalAirportId,
            Pageable pageable
    );

    //для квалификаций
    @Query("SELECT a.modelId FROM Flight f JOIN Aircraft a ON f.aircraftId = a.aircraftId WHERE f.flightId = :flightId")
    Optional<Integer> findAircraftModelIdByFlightId(@Param("flightId") Integer flightId);

    Integer findAircraftCapacityByFlightId(Integer flightId);
}