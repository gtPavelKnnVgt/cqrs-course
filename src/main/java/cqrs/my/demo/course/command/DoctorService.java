package cqrs.my.demo.course.command;

import cqrs.my.demo.course.db.tables.records.AppointmentRecord;
import cqrs.my.demo.course.events.AppointmentCreatedDomainEvent;
import cqrs.my.demo.course.events.AppointmentCreatedEventHandler;
import cqrs.my.demo.course.events.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static cqrs.my.demo.course.db.Tables.*;


@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DSLContext ctx;
    private final EventPublisher eventPublisher;
    private final AppointmentCreatedEventHandler appointmentCreatedObserver;

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

        return appointment;
    }
}
