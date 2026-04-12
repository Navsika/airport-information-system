package org.example.repository;

import org.example.entity.AircraftModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AircraftModelRepository extends JpaRepository<AircraftModel, Integer> {
}
