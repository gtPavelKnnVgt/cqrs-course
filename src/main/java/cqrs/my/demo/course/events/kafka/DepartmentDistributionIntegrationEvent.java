package cqrs.my.demo.course.events.kafka;

import java.util.UUID;

public record DepartmentDistributionIntegrationEvent(UUID id, String occuredAt, Long departmentId, String confirmedBy)
        implements IntegrationEvent {
}
