package cqrs.my.demo.course.query;

public record AppointmentDataPreparation(Patient patient, TimeSlot timeSlot, Doctor doctor) {
}
