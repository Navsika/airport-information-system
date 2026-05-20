package org.example.repository;

import org.example.entity.Flight;
import org.example.entity.Aircraft;
import org.example.entity.Schedule;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.OffsetDateTime;

public final class FlightSpecifications {
    private FlightSpecifications() {
    }

    public static Specification<Flight> withFilters(
            String status,
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo,
            Integer departureAirportId,
            Integer arrivalAirportId,
            String aircraftRegNumber
    ) {
        return Specification
                .where(hasStatus(status))
                .and(departureAfterOrAt(dateFrom))
                .and(departureBeforeOrAt(dateTo))
                .and(hasDepartureAirport(departureAirportId))
                .and(hasArrivalAirport(arrivalAirportId))
                .and(hasAircraftRegNumber(aircraftRegNumber));
    }

    private static Specification<Flight> hasStatus(String status) {
        return (root, query, criteriaBuilder) ->
                status == null || status.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<Flight> departureAfterOrAt(OffsetDateTime dateFrom) {
        return (root, query, criteriaBuilder) ->
                dateFrom == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("scheduledDeparture"), dateFrom);
    }

    private static Specification<Flight> departureBeforeOrAt(OffsetDateTime dateTo) {
        return (root, query, criteriaBuilder) ->
                dateTo == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(root.get("scheduledDeparture"), dateTo);
    }

    private static Specification<Flight> hasDepartureAirport(Integer departureAirportId) {
        return (root, query, criteriaBuilder) -> {
            if (departureAirportId == null) {
                return criteriaBuilder.conjunction();
            }
            Subquery<Integer> scheduleIds = query.subquery(Integer.class);
            Root<Schedule> schedule = scheduleIds.from(Schedule.class);
            scheduleIds.select(schedule.get("scheduleId"))
                    .where(criteriaBuilder.equal(schedule.get("departureAirport"), departureAirportId));
            return root.get("scheduleId").in(scheduleIds);
        };
    }

    private static Specification<Flight> hasArrivalAirport(Integer arrivalAirportId) {
        return (root, query, criteriaBuilder) -> {
            if (arrivalAirportId == null) {
                return criteriaBuilder.conjunction();
            }
            Subquery<Integer> scheduleIds = query.subquery(Integer.class);
            Root<Schedule> schedule = scheduleIds.from(Schedule.class);
            scheduleIds.select(schedule.get("scheduleId"))
                    .where(criteriaBuilder.equal(schedule.get("arrivalAirport"), arrivalAirportId));
            return root.get("scheduleId").in(scheduleIds);
        };
    }

    private static Specification<Flight> hasAircraftRegNumber(String aircraftRegNumber) {
        return (root, query, criteriaBuilder) -> {
            if (aircraftRegNumber == null || aircraftRegNumber.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Subquery<Integer> aircraftIds = query.subquery(Integer.class);
            Root<Aircraft> aircraft = aircraftIds.from(Aircraft.class);
            aircraftIds.select(aircraft.get("aircraftId"))
                    .where(criteriaBuilder.like(
                            criteriaBuilder.lower(aircraft.get("registrationNumber")),
                            "%" + aircraftRegNumber.toLowerCase() + "%"
                    ));
            return root.get("aircraftId").in(aircraftIds);
        };
    }
}
