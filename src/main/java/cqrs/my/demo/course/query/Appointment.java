package cqrs.my.demo.course.query;

public record Appointment(Long id, String dayOfWeek, Patient patient, Doctor doctor, TimeSlot timeSlot) {
}
