package cqrs.my.demo.course.command;

import jakarta.validation.constraints.Min;

public interface DoctorCommand {
    record CreateAppointment(@Min(1) long patientId, @Min(1) long doctorId, @Min(1) long timeSlotId,
                             @Min(1) long departmentId) implements DoctorCommand {
    }
}
