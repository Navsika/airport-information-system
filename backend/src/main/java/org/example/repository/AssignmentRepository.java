package org.example.repository;

import org.example.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByFlightId(Integer flightId);

    @Query(value = "SELECT COUNT(a) > 0 FROM assignments a " +
            "JOIN flights f ON a.flight_id = f.flight_id " +
            "WHERE a.employee_id = :empId " +
            "AND f.flight_id != :flightId " +
            "AND f.status NOT IN ('Cancelled', 'Arrived') " +
            "AND f.scheduled_departure < (SELECT scheduled_arrival FROM flights WHERE flight_id = :flightId) " +
            "AND f.scheduled_arrival > (SELECT scheduled_departure FROM flights WHERE flight_id = :flightId)",
            nativeQuery = true)
    boolean hasOverlappingAssignment(@Param("empId") Integer employeeId,
                                     @Param("flightId") Integer flightId);

    boolean existsByFlightIdAndEmployeeId(Integer flightId, Integer employeeId);

    boolean existsByFlightIdAndEmployeeRole(Integer flightId, String employeeRole);

    long countByFlightIdAndEmployeeRole(Integer flightId, String employeeRole);
}
