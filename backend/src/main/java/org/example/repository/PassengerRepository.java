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
            "CASE WHEN c.checkInId IS NOT NULL THEN true ELSE false END) " +
            "FROM Ticket t " +
            "JOIN t.passenger p " +
            "LEFT JOIN CheckIn c ON t.ticketId = c.ticket.ticketId " +
            "WHERE t.flight.flightId = :flightId " +
            "ORDER BY p.lastName ASC")
    List<FlightPassengerDto> findPassengersByFlightId(@Param("flightId") Integer flightId);

    Optional<Passenger> findByPassportNumber(String passportNumber);

    boolean existsByPassportNumber(String passportNumber);
}
