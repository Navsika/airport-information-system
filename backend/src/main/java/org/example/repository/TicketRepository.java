package org.example.repository;

import org.example.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByFlightId(Integer flightId);
    long countByFlightId(Integer flightId);
    @Query("SELECT m.passengerCapacity FROM Flight f " +
            "JOIN Aircraft a ON f.aircraftId = a.aircraftId " +
            "JOIN AircraftsModel m ON a.modelId = m.modelId " +
            "WHERE f.flightId = :flightId")
    Optional<Integer> findAircraftCapacityByFlightId(@Param("flightId") Integer flightId);

    boolean existsByPassengerIdAndFlightId(Integer passengerId);

    boolean existsByFlightIdAndSeatNumber(Integer flightId, String seatNumber);
}
