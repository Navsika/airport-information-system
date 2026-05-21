package org.example.repository;

import org.example.entity.AircraftModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AircraftModelRepository extends JpaRepository<AircraftModel, Integer> {
    @Query("SELECT am FROM AircraftModel am WHERE " +
            "LOWER(am.modelName) LIKE CONCAT(:modelName, '%') AND " +
            "LOWER(am.manufacturer) LIKE CONCAT(:manufacturer, '%') AND " +
            "am.passengerCapacity >= :passengerCapacityMin AND " +
            "am.passengerCapacity <= :passengerCapacityMax AND " +
            "am.cargoCapacity >= :cargoCapacityMin AND " +
            "am.cargoCapacity <= :cargoCapacityMax AND " +
            "am.maxSpeed >= :maxSpeedMin AND " +
            "am.maxSpeed <= :maxSpeedMax")
    Page<AircraftModel> findModelByFilters(
            @Param("modelName") String modelName,
            @Param("manufacturer") String manufacturer,
            @Param("passengerCapacityMin") Short passengerCapacityMin,
            @Param("passengerCapacityMax") Short passengerCapacityMax,
            @Param("cargoCapacityMin") Integer cargoCapacityMin,
            @Param("cargoCapacityMax") Integer cargoCapacityMax,
            @Param("maxSpeedMin") Short maxSpeedMin,
            @Param("maxSpeedMax") Short maxSpeedMax,
            Pageable pageable
    );

    boolean existsByModelNameAndManufacturer(String modelName, String manufacturer);

    Optional<AircraftModel> findByModelName(String modelName);
}
