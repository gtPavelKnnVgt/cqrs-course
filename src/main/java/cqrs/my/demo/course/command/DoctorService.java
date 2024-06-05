package cqrs.my.demo.course.command;

import cqrs.my.demo.course.db.tables.records.AppointmentRecord;
import cqrs.my.demo.course.events.domain.AppointmentCreatedDomainEvent;
import cqrs.my.demo.course.events.domain.AppointmentCreatedEventHandler;
import cqrs.my.demo.course.events.domain.EventPublisher;
import cqrs.my.demo.course.query.*;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static cqrs.my.demo.course.db.Tables.*;
import static cqrs.my.demo.course.db.tables.Department.DEPARTMENT;
import static cqrs.my.demo.course.db.tables.Timeslot.TIMESLOT;


@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DSLContext ctx;
    private final EventPublisher eventPublisher;
    private final AppointmentCreatedEventHandler appointmentCreatedObserver;

    @Transactional
    public AppointmentRecord createAppointment(long patientId, long doctorId, long timeSlotId, long departmentId) {
        eventPublisher.addObserver(appointmentCreatedObserver);

        AppointmentDataPreparation result = prepareDataForAppointmentCreation(patientId, doctorId, timeSlotId, departmentId);
        var dayOfWeek = result.timeSlot().startTime().getDayOfWeek().name();

        Appointment preparedAppointment = Appointment.registerNewAppointment(UUID.randomUUID().getMostSignificantBits(),
                result.patient(), result.doctor(), result.timeSlot(), dayOfWeek);

        var event = new AppointmentCreatedDomainEvent(
                UUID.randomUUID(),
                Instant.now(),
                String.format("%s %s", preparedAppointment.getPatient().getFirstName(), preparedAppointment.getPatient().getLastName()),
                String.format("%s %s", preparedAppointment.getDoctor().getFirstName(), preparedAppointment.getDoctor().getLastName()),
                dayOfWeek,
                preparedAppointment.getTimeSlot().startTime(),
                preparedAppointment.getTimeSlot().endTime());

        if (preparedAppointment.getEvents() == null) {
            preparedAppointment.setEvents(new ArrayList<>());
        }
        preparedAppointment.getEvents().add(event);

        var appointment = ctx.newRecord(APPOINTMENT);
        appointment.setPatientId(patientId);
        appointment.setDoctorId(doctorId);
        appointment.setDayOfWeek(dayOfWeek);
        appointment.setTimeslotId(timeSlotId);
        appointment.store();

        eventPublisher.publish(event);

        return appointment;
    }

    private AppointmentDataPreparation prepareDataForAppointmentCreation(long patientId, long doctorId, long timeSlotId, long departmentId) {
        var patient = Optional.ofNullable(ctx
                        .selectFrom(PATIENT)
                        .where(PATIENT.ID.eq(patientId))
                        .fetchOne(new PatientRecordMapper()))
                .orElseThrow(() -> new IllegalArgumentException("Patient does not exist"));

        var department = Optional.ofNullable(ctx.selectFrom(DEPARTMENT)
                        .where(DEPARTMENT.ID.eq(departmentId))
                        .fetchOneInto(Department.class))
                .orElseThrow(() -> new IllegalArgumentException("Department does not exist"));

        var timeSlot = Optional.ofNullable(
                        ctx
                                .selectFrom(TIMESLOT)
                                .where(TIMESLOT.ID.eq(timeSlotId))
                                .fetchOneInto(TimeSlot.class))
                .orElseThrow(() -> new IllegalArgumentException("TimeSlot does not exist"));

        var doctor = Optional.ofNullable(
                        ctx
                                .selectFrom(DOCTOR)
                                .where(DOCTOR.ID.eq(doctorId))
                                .fetchOne(new DoctorRecordMapper(department, timeSlot)))
                .orElseThrow(() -> new IllegalArgumentException("Doctor does not exist"));
        return new AppointmentDataPreparation(patient, timeSlot, doctor);
    }
}
