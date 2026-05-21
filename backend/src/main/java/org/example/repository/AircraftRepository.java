package org.example.repository;

import jakarta.transaction.Transactional;
import org.example.entity.Aircraft;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Integer> {
    Optional<Aircraft> findByRegistrationNumber(String registrationNumber);

    @Query("SELECT a FROM Aircraft a WHERE " +
            "a.modelId = COALESCE(:modelId, a.modelId) AND " +
            "a.airlineId = COALESCE(:airlineId, a.airlineId) AND " +
            "a.manufactureYear = COALESCE(:manufactureYear, a.manufactureYear) AND " +
            "a.lastMaintenanceDate = COALESCE(:lastMaintenanceDate, a.lastMaintenanceDate) AND " +
            "a.flightHours = COALESCE(:flightHours, a.flightHours)")
    Page<Aircraft> findByFilters(
            @Param("modelId") Integer modelId,
            @Param("airlineId") Integer airlineId,
            @Param("manufactureYear") Integer manufactureYear,
            @Param("lastMaintenanceDate") LocalDate lastMaintenanceDate,
            @Param("flightHours") Integer flightHours,
            Pageable pageable
    );

    @Query("SELECT a FROM Aircraft a WHERE a.lastMaintenanceDate < :threshold")
    Page<Aircraft> findNeedingMaintenance(
            @Param("threshold") LocalDate threshold,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Aircraft a SET a.flightHours = a.flightHours + :hours WHERE a.aircraftId = :id")
    void addFlightHours(
            @Param ("hours") int hours,
            @Param ("id") Integer id
    );

}

