package cqrs.my.demo.course.command;

import cqrs.my.demo.course.db.tables.records.AppointmentRecord;
import cqrs.my.demo.course.events.domain.AppointmentCreatedDomainEvent;
import cqrs.my.demo.course.events.domain.AppointmentCreatedEventHandler;
import cqrs.my.demo.course.events.domain.EventPublisher;
import cqrs.my.demo.course.events.kafka.AppointmentConfirmedIntegrationEvent;
import cqrs.my.demo.course.events.kafka.KafkaIntegrationEventProducer;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cqrs.my.demo.course.db.Tables.*;


@Service
@RequiredArgsConstructor
public class DoctorService {
    private static final String ADMINISTRATION_FIO = "Админ Админов";

    private final DSLContext ctx;
    private final EventPublisher eventPublisher;
    private final AppointmentCreatedEventHandler appointmentCreatedObserver;
    private final KafkaIntegrationEventProducer kafkaProducer;

    @Transactional
    public AppointmentRecord createAppointment(long patientId, long doctorId, long timeSlotId) {
        eventPublisher.addObserver(appointmentCreatedObserver);
        var patient = ctx
                .selectFrom(PATIENT)
                .where(PATIENT.ID.eq(patientId))
                .fetchOptional()
                .orElseThrow(() -> new IllegalArgumentException("Patient does not exist"));

        var doctor = ctx
                .selectFrom(DOCTOR)
                .where(DOCTOR.ID.eq(doctorId))
                .fetchOptional()
                .orElseThrow(() -> new IllegalArgumentException("Doctor does not exist"));

        var timeSlot = ctx
                .selectFrom(TIMESLOT)
                .where(TIMESLOT.ID.eq(timeSlotId))
                .fetchOptional()
                .orElseThrow(() -> new IllegalArgumentException("TimeSlot does not exist"));

        var appointment = ctx.newRecord(APPOINTMENT);
        appointment.setPatientId(patientId);
        appointment.setDoctorId(doctorId);
        appointment.setTimeslotId(timeSlotId);
        appointment.setDayOfWeek(timeSlot.getStartTime().getDayOfWeek().name());

        appointment.store();

        var event = new AppointmentCreatedDomainEvent(
                UUID.randomUUID(),
                Instant.now(),
                String.format("%s %s", patient.getFirstName(), patient.getLastName()),
                String.format("%s %s", doctor.getFirstName(), doctor.getLastName()),
                appointment.getDayOfWeek(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime());
        eventPublisher.publish(event);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        var integrationEvent = new AppointmentConfirmedIntegrationEvent(
                UUID.randomUUID(),
                Instant.now(),
                ADMINISTRATION_FIO
        );
        scheduler.schedule(() -> kafkaProducer.send(integrationEvent), 10, TimeUnit.SECONDS);

        return appointment;
    }
}
