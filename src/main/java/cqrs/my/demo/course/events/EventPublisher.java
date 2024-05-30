package cqrs.my.demo.course.events;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventPublisher {
    private final List<DomainEventObserver> observers = new ArrayList<>();

    public void publish(DomainEvent event) {
        observers.forEach(observer -> observer.onEvent(event));
    }

    public void addObserver(DomainEventObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(DomainEventObserver observer) {
        observers.remove(observer);
    }
}
