package org.example.repository;

import org.example.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByFlightId(Integer flightId);
    long countByFlightId(Integer flightId);

    boolean existsByPassengerIdAndFlightId(Integer passengerId, Integer flightId);

    boolean existsByFlightIdAndSeatNumber(Integer flightId, String seatNumber);
}
