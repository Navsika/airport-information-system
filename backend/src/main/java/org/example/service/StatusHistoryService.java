package org.example.service;

import org.example.dto.StatusHistoryDto;
import org.example.entity.StatusHistory;
import org.example.mapper.StatusHistoryMapper;
import org.example.repository.StatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StatusHistoryService {
    private final StatusHistoryRepository repository;
    private final StatusHistoryMapper mapper;

    public StatusHistoryService(StatusHistoryRepository repository, StatusHistoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<StatusHistoryDto> getHistoryByFlightId(Integer flightId){
        return repository.findByFlightId(flightId)
                .stream().map(mapper::toDto)
                .toList();
    }

    @Transactional
    public void logStatusChange(Integer flightId, String oldStatus, String newStatus, String reason){
        if (oldStatus != null && oldStatus.equals(newStatus)) return;
        StatusHistory history = new StatusHistory();
        history.setFlightId(flightId);
        history.setStatus(newStatus);
        history.setReason(reason);
        repository.save(history);
    }
}
