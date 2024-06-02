package cqrs.my.demo.course.events.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentCreatedDomainEvent(UUID id, Instant occuredAt, String patientFio, String doctorFio, String dayOfWeek,
                                            LocalDateTime start, LocalDateTime end) implements DomainEvent {
}
