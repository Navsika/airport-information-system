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
    //запрос для того, чтобы найти самолет по номеру при нажатии на кнопку изменить
    // он должен находить по идее да? а как он узнает что за регистрационный номер,
    // если мы не будем показывать
    @Query("SELECT a FROM Aircraft a WHERE " +
            "(:modelId IS NULL OR a.modelId = :modelId) AND" +
            " (:airlineId IS NULL OR a.airlineId = :airlineId) AND " +
            " (:manufactureYear IS NULL OR a.manufactureYear =: manufactureYear AND " +
            " (:lastMaintenanceDate IS NULL OR a.lastMaintenanceDate = :lastMaintenanceDate) AND" +
            " (:flightHours IS NULL OR a.flightHours = :flightHours)")
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
            @Param("threshold") LocalDate threshold
    );
    //запрос для получения самолетов которым нужно тех обсулживание
    @Modifying
    @Transactional
    @Query("UPDATE Aircraft a SET a.flightHours = a.flightHours + :hours WHERE a.aircraftId = :id")
    void addFlightHours(
            @Param ("hours") int hours,
            @Param ("id") Integer id
    );

}

