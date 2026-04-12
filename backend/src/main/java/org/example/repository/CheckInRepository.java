package org.example.repository;

import org.example.entity.CheckIns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIns, Integer> {
}
