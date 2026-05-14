package org.example.service;

import org.example.dto.QualificationDto;
import org.example.entity.Qualification;
import org.example.mapper.QualificationMapper;
import org.example.repository.QualificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QualificationService {
    private final QualificationRepository repository;
    private final QualificationMapper mapper;

    public QualificationService(QualificationRepository repository, QualificationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<QualificationDto> getByPilotId(Integer pilotId){
        return repository.findByPilotId(pilotId)
                .stream().map(mapper::toDto)
                .toList();
    }

    @Transactional
    public QualificationDto createQualification(QualificationDto dto){
        if (repository.existsByPilotIdAndModelId(dto.getPilotId(), dto.getModelId())) {
            throw new IllegalArgumentException("У пилота уже есть допуск на эту модель ВС");
        }
        Qualification qualification = mapper.toEntity(dto);
        Qualification saved = repository.save(qualification);
        return mapper.toDto(saved);
    }


    @Transactional
    public QualificationDto updateQualification(Integer id, QualificationDto dto) {
        Qualification exist = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Квалификация не найдена"));
        if (dto.getQualificationDate() != null) {
            exist.setQualificationDate(dto.getQualificationDate());
        }
        if (dto.getValidUntil() != null) {
            exist.setValidUntil(dto.getValidUntil());
        }
        return mapper.toDto(repository.save(exist));
    }

    @Transactional(readOnly = true)
    public boolean checkPilotQualification(Integer pilotId, Integer modelId) {
        return repository.isPilotQualifiedForModel(pilotId, modelId);
    }
}
