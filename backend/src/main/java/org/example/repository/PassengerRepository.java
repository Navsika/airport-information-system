package org.example.repository;

import org.example.dto.FlightPassengerDto;
import org.example.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    @Query("SELECT new org.example.dto.FlightPassengerDto(" +
            "p.lastName, p.firstName, p.passportNumber, t.seatNumber, t.ticketClass, " +
            "CASE WHEN c.checkInId IS NOT NULL THEN true ELSE false END, " +
            "CASE " +
            "WHEN c.checkInId IS NOT NULL THEN 'CHECKED_IN' " +
            "WHEN f.status IN ('Departed', 'Arrived') THEN 'NO_SHOW' " +
            "WHEN f.status = 'Cancelled' THEN 'CANCELLED' " +
            "ELSE 'PENDING' END) " +
            "FROM Ticket t " +
            "JOIN Passenger p ON t.passengerId = p.passengerId " +
            "JOIN Flight f ON t.flightId = f.flightId " +
            "LEFT JOIN CheckIns c ON t.ticketId = c.ticketId " +
            "WHERE t.flightId = :flightId " +
            "ORDER BY p.lastName ASC")
    List<FlightPassengerDto> findPassengersByFlightId(@Param("flightId") Integer flightId);

    Optional<Passenger> findByPassportNumber(String passportNumber);

    @Query("SELECT p FROM Passenger p WHERE " +
            "LOWER(p.lastName) LIKE CONCAT(:lastName, '%') AND " +
            "LOWER(p.firstName) LIKE CONCAT(:firstName, '%') " +
            "ORDER BY p.lastName ASC, p.firstName ASC")
    List<Passenger> findByNameFilters(
            @Param("lastName") String lastName,
            @Param("firstName") String firstName
    );

    boolean existsByPassportNumber(String passportNumber);
}
