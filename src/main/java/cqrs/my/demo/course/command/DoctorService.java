package cqrs.my.demo.course.command;

import cqrs.my.demo.course.db.tables.records.AppointmentRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cqrs.my.demo.course.db.Tables.*;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DSLContext ctx;

    @Transactional
    public AppointmentRecord createAppointment(long patientId, long doctorId, long timeSlotId) {
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

        return appointment;
    }
}
