package cqrs.my.demo.course.query;

import java.time.LocalDateTime;

public record TimeSlot(Long id, LocalDateTime startTime, LocalDateTime endTime) {
}
