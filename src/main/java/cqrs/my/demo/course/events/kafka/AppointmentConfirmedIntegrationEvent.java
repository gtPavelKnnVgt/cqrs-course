package cqrs.my.demo.course.events.kafka;

import java.time.Instant;
import java.util.UUID;

public record AppointmentConfirmedIntegrationEvent(UUID id, Instant occuredAt, String administratorFio) implements IntegrationEvent {
}
