package cqrs.my.demo.course.events;

public interface DomainEventObserver<T extends DomainEvent> {
    void onEvent(T event);
}
