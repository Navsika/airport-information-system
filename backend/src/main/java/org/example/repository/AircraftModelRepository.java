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
            "(:modelName IS NULL OR LOWER(am.modelName) = LOWER(:modelName)) AND " +
            "(:manufacturer IS NULL OR am.manufacturer = :manufacturer) AND " +
            "(:passengerCapacity IS NULL OR am.passengerCapacity = :passengerCapacity) AND " +
            "(:cargoCapacity IS NULL OR am.cargoCapacity = :cargoCapacity) AND " +
            "(:maxSpeed IS NULL OR am.maxSpeed = :maxSpeed)")
    Page<AircraftModel> findModelByFilters(
            @Param("modelName") String modelName,
            @Param("manufacturer") String manufacturer,
            @Param("passengerCapacity") Short passengerCapacity,
            @Param("cargoCapacity") Integer cargoCapacity,
            @Param("maxSpeed") Short maxSpeed,
            Pageable pageable
    );

    boolean existsByModelNameAndManufacturer(String modelName, String manufacturer);

    Optional<AircraftModel> findByModelName(String modelName);
}
