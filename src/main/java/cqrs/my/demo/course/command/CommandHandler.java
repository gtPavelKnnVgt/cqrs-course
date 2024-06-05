package cqrs.my.demo.course.command;

import cqrs.my.demo.course.events.kafka.KafkaIntegrationEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final DoctorService doctorService;

    Optional<?> handle(DoctorCommand command) {
        switch (command) {
            case DoctorCommand.CreateAppointment(long patientId, long doctorId, long timeSlotId, long departmentId) -> {
                var appointment = doctorService.createAppointment(patientId, doctorId, timeSlotId, departmentId);
                return Optional.of(appointment.getId());
            }
            default -> throw new IllegalStateException("Unexpected value: " + command);
        }
    }

}
