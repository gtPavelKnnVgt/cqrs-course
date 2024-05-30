package cqrs.my.demo.course.query;

import java.time.LocalDateTime;

record TimeSlot(Long id, LocalDateTime startTime, LocalDateTime endTime) {
}
