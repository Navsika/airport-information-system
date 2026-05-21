package org.example.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FlightCreateDto {
    @NotNull(message = "Шаблон расписания обязателен")
    private Integer scheduleId;

    @NotNull(message = "Дата рейса обязательна")
    @FutureOrPresent(message = "Нельзя создать рейс на прошедшую дату")
    private LocalDate flightDate;

    private Integer aircraftId;
    private String gate;
}
