package org.example.repository;

import org.example.entity.CheckIns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIns, Integer> {
    @Query("SELECT c FROM CheckIns c JOIN Ticket t ON c.ticketId = t.ticketId" +
            " WHERE t.flightId = :flightId")
    List<CheckIns> findByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT COUNT(c) FROM CheckIns c JOIN Ticket t ON c.ticketId = t.ticketId" +
            " WHERE t.flightId = :flightId")
    long countByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT COALESCE(SUM(c.totalBaggageWeight), 0) FROM CheckIns c JOIN Ticket t ON c.ticketId = t.ticketId" +
            " WHERE t.flightId = :flightId")
    Double sumBaggageWeightByFlightId(@Param("flightId") Integer flightId);

    boolean existsByTicketId(Integer ticketId);

    Optional<CheckIns> findByTicketId(Integer ticketId);

    void deleteByTicketId(Integer ticketId);
}
