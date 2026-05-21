package org.example.repository;

import org.example.dto.ScheduleSearchDto;
import org.example.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query("SELECT new org.example.dto.ScheduleSearchDto(" +
            "s.scheduleId, s.flightNumber, al.airlineName, dep.city, arr.city, " +
            "s.departureTime, s.arrivalTime, s.arrivalDayOffset) " +
            "FROM Schedule s " +
            "JOIN Airline al ON s.airlineId = al.airlineId " +
            "JOIN Airport dep ON s.departureAirport = dep.airportId " +
            "JOIN Airport arr ON s.arrivalAirport = arr.airportId " +
            "WHERE LOWER(dep.city) = LOWER(:depCity) AND LOWER(arr.city) = LOWER(:arrCity)")
    List<ScheduleSearchDto> findByDepartureCityAndArrivalCity(
            @Param("depCity") String depCity,
            @Param("arrCity") String arrCity
    );

    @Query("SELECT new org.example.dto.ScheduleSearchDto(" +
            "s.scheduleId, s.flightNumber, al.airlineName, dep.city, arr.city, " +
            "s.departureTime, s.arrivalTime, s.arrivalDayOffset) " +
            "FROM Schedule s " +
            "JOIN Airline al ON s.airlineId = al.airlineId " +
            "JOIN Airport dep ON s.departureAirport = dep.airportId " +
            "JOIN Airport arr ON s.arrivalAirport = arr.airportId " +
            "ORDER BY s.flightNumber")
    List<ScheduleSearchDto> findAllForSelection();

    Optional<Schedule> findByFlightNumber(String flightNumber);

    boolean existsByFlightNumber(String flightNumber);
}
