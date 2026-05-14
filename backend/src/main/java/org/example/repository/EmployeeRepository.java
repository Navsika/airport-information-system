package org.example.repository;

import org.example.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @Query("SELECT e FROM Employee e WHERE " +
            "(:category IS NULL OR e.category = :category) AND " +
            "(:lastName IS NULL OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:hireDateFrom IS NULL OR e.hireDate >= :hireDateFrom) AND " +
            "(:hireDateTo IS NULL OR e.hireDate <= :hireDateTo)")
    Page<Employee> findByFilters(
            @Param("category") String category,
            @Param("lastName") String lastName,
            @Param("hireDateFrom") LocalDate hireDateFrom,
            @Param("hireDateTo") LocalDate hireDateTo,
            Pageable pageable
    );
}
