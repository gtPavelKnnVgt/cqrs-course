package cqrs.my.demo.course.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppointmentCreatedEventHandler implements DomainEventObserver<AppointmentCreatedDomainEvent> {
    @Override
    public void onEvent(AppointmentCreatedDomainEvent event) {
      log.info("Received appointment created event: {}", event);
    }

    public void subscribe(EventPublisher eventPublisher) {
        eventPublisher.addObserver(this);
    }

    public void unsubscribe(EventPublisher eventPublisher) {
        eventPublisher.removeObserver(this);
    }
}
