package org.example.repository;

import org.example.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Integer> {
    List<StatusHistory> findByFlightId(Integer flightId);
}
