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
            "LOWER(e.category) LIKE CONCAT(:category, '%') AND " +
            "LOWER(e.lastName) LIKE CONCAT(:lastName, '%') AND " +
            "e.hireDate >= :hireDateFrom AND " +
            "e.hireDate <= :hireDateTo")
    Page<Employee> findByFilters(
            @Param("category") String category,
            @Param("lastName") String lastName,
            @Param("hireDateFrom") LocalDate hireDateFrom,
            @Param("hireDateTo") LocalDate hireDateTo,
            Pageable pageable
    );
}
