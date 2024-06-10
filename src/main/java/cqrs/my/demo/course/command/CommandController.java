package cqrs.my.demo.course.command;

import cqrs.my.demo.course.events.kafka.AppointmentConfirmedIntegrationEvent;
import cqrs.my.demo.course.events.kafka.DepartmentDistributionIntegrationEvent;
import cqrs.my.demo.course.events.kafka.KafkaIntegrationEventProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("api/v1/appointments")
@RequiredArgsConstructor
public class CommandController {
    private final static String APPOINTMENT_ADDED_TOPIC_NAME = "INTEGRATION_EVENTS_APPOINTMENT_ADDED";
    private final static String DEPARTMENT_DISTRIBUTED_TOPIC_NAME = "INTEGRATION_EVENTS_DEPARTMENT_DISTRIBUTED";
    private static final String ADMINISTRATION_FIO = "Админ Админов";

    private final CommandHandler commandHandler;
    private final KafkaIntegrationEventProducer kafkaProducer;

    @PostMapping
    ResponseEntity<?> createAppointment(@RequestBody @Valid DoctorCommand.CreateAppointment createAppointment) {
        var appointmentId = commandHandler.handle(createAppointment).orElse(null);

        sendAppointmentConfirmedIntegrationEvent();
        sendDepartmentDistributionAuditIntegrationEvent(createAppointment.departmentId());

        return created(fromCurrentRequest().path("/appointments/{id}").buildAndExpand(appointmentId).toUri()).build();
    }

    private void sendAppointmentConfirmedIntegrationEvent() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        var integrationEvent = new AppointmentConfirmedIntegrationEvent(
                UUID.randomUUID(),
                String.valueOf(Instant.now().getEpochSecond()),
                ADMINISTRATION_FIO
        );
        scheduler.schedule(() -> kafkaProducer.send(integrationEvent, APPOINTMENT_ADDED_TOPIC_NAME), 10, TimeUnit.SECONDS);
    }

    private void sendDepartmentDistributionAuditIntegrationEvent(long departmentId) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        var integrationEvent = new DepartmentDistributionIntegrationEvent(
                UUID.randomUUID(),
                String.valueOf(Instant.now().getEpochSecond()),
                departmentId,
                ADMINISTRATION_FIO
        );
        scheduler.schedule(() -> kafkaProducer.send(integrationEvent, DEPARTMENT_DISTRIBUTED_TOPIC_NAME), 20, TimeUnit.SECONDS);
    }
}
