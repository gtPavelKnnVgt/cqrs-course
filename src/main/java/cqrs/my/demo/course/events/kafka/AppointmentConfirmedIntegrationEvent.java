package cqrs.my.demo.course.events.kafka;

import java.util.UUID;

public record AppointmentConfirmedIntegrationEvent(UUID id, String occuredAt, String administratorFio) implements IntegrationEvent {
}
