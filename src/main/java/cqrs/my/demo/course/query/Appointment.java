package cqrs.my.demo.course.query;

record Appointment(Long id, String dayOfWeek, Patient patient, Doctor doctor,TimeSlot timeSlot) {
}
