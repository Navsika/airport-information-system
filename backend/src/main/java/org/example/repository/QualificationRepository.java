package org.example.repository;

import org.example.entity.Qualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, Integer> {
    List<Qualification> findByPilotId(Integer pilotId);
    @Query("SELECT COUNT(q) > 0 FROM Qualification q " +
            "WHERE q.pilotId = :pilotId AND q.modelId = :modelId " +
            "AND q.qualificationDate <= CURRENT_DATE " +
            "AND (q.validUntil IS NULL OR q.validUntil >= CURRENT_DATE)")
    boolean isPilotQualifiedForModel(@Param("pilotId") Integer pilotId,
                                     @Param("modelId") Integer modelId);

    boolean existsByPilotIdAndModelId(Integer pilotId, Integer modelId);
}
