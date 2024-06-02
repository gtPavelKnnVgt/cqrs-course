package cqrs.my.demo.course.events.domain;

public interface DomainEventObserver<T extends DomainEvent> {
    void onEvent(T event);
}
