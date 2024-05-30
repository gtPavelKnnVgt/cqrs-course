package cqrs.my.demo.course.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final DoctorService doctorService;

    Optional<?> handle(DoctorCommand command) {
        switch (command) {
            case DoctorCommand.CreateAppointment(long patientId, long doctorId, long timeSlotId) -> {
                var appointment = doctorService.createAppointment(patientId, doctorId, timeSlotId);
                return Optional.of(appointment.getId());
            }
            default -> throw new IllegalStateException("Unexpected value: " + command);
        }
    }

}
