package org.example.mapper;

import org.example.dto.TicketDto;
import org.example.entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    Ticket toEntity(TicketDto ticketDto);
    TicketDto toDto (Ticket ticket);
}
