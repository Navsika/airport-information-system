package org.example.repository;

import org.example.dto.FlightListDto;
import org.example.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer>, JpaSpecificationExecutor<Flight> {

    @Query("SELECT new org.example.dto.FlightListDto(" +
            "f.flightId, s.flightNumber, al.airlineName, " +
            "dep.airportName, arr.airportName, " +
            "f.scheduledDeparture, f.scheduledArrival, " +
            "f.actualDeparture, f.actualArrival, " +
            "f.status, f.gate, ac.registrationNumber, am.passengerCapacity) " +
            "FROM Flight f " +
            "JOIN Schedule s ON f.scheduleId = s.scheduleId " +
            "JOIN Airline al ON s.airlineId = al.airlineId " +
            "JOIN Airport dep ON s.departureAirport = dep.airportId " +
            "JOIN Airport arr ON s.arrivalAirport = arr.airportId " +
            "LEFT JOIN Aircraft ac ON f.aircraftId = ac.aircraftId " +
            "LEFT JOIN AircraftModel am ON ac.modelId = am.modelId " +
            "WHERE f.flightId IN :flightIds " +
            "ORDER BY f.scheduledDeparture DESC")
    List<FlightListDto> findFlightsWithRouteByIds(@Param("flightIds") List<Integer> flightIds);

    @Query("SELECT a.modelId FROM Flight f JOIN Aircraft a ON f.aircraftId = a.aircraftId WHERE f.flightId = :flightId")
    Optional<Integer> findAircraftModelIdByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT m.passengerCapacity FROM Flight f " +
            "JOIN Aircraft a ON f.aircraftId = a.aircraftId " +
            "JOIN AircraftModel m ON a.modelId = m.modelId " +
            "WHERE f.flightId = :flightId")
    Integer  findAircraftCapacityByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT m.cargoCapacity FROM Flight f " +
            "JOIN Aircraft a ON f.aircraftId = a.aircraftId " +
            "JOIN AircraftModel m ON a.modelId = m.modelId " +
            "WHERE f.flightId = :flightId")
    Integer findAircraftCargoCapacityByFlightId(@Param("flightId") Integer flightId);

    boolean existsByScheduleIdAndScheduledDeparture(Integer scheduleId, OffsetDateTime scheduledDeparture);

    @Query("SELECT COUNT(f) FROM Flight f " +
            "WHERE f.aircraftId = :aircraftId " +
            "AND f.status <> 'Cancelled' " +
            "AND f.scheduledDeparture < :scheduledArrival " +
            "AND f.scheduledArrival > :scheduledDeparture")
    long countAircraftOverlaps(
            @Param("aircraftId") Integer aircraftId,
            @Param("scheduledDeparture") OffsetDateTime scheduledDeparture,
            @Param("scheduledArrival") OffsetDateTime scheduledArrival
    );
}
