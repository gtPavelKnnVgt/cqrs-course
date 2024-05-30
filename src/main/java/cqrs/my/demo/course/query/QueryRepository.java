package cqrs.my.demo.course.query;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cqrs.my.demo.course.db.Tables.*;
import static cqrs.my.demo.course.db.tables.Appointment.APPOINTMENT;
import static cqrs.my.demo.course.db.tables.Doctor.DOCTOR;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.row;

@Repository
@RequiredArgsConstructor
class QueryRepository {
    private final DSLContext ctx;

    List<Appointment> findAppointments(String doctorFirstName, String doctorLastName, int offset, int limit) {
        return ctx.select(APPOINTMENT.ID, APPOINTMENT.DAY_OF_WEEK,
                        row(APPOINTMENT.patient().ID,
                                APPOINTMENT.patient().FIRST_NAME,
                                APPOINTMENT.patient().LAST_NAME,
                                APPOINTMENT.patient().BIRTHDAY,
                                APPOINTMENT.patient().MEDICAL_RECORD_NUMBER)
                                .mapping(Patient::new),
                        row(APPOINTMENT.doctor().ID,
                                APPOINTMENT.doctor().FIRST_NAME,
                                APPOINTMENT.doctor().LAST_NAME,
                                APPOINTMENT.doctor().SPECIALIZATION,
                                APPOINTMENT.doctor().LICENSE_NUMBER,
                                row(APPOINTMENT.doctor().department().ID,
                                        APPOINTMENT.doctor().department().NAME,
                                        APPOINTMENT.doctor().department().DESCRIPTION).mapping(Department::new))
                                .mapping(Doctor::new),
                        row(APPOINTMENT.timeslot().ID,
                                APPOINTMENT.timeslot().START_TIME,
                                APPOINTMENT.timeslot().END_TIME)
                                .mapping(TimeSlot::new))
                .from(APPOINTMENT)
                .join(PATIENT).on(APPOINTMENT.patient().ID.eq(PATIENT.ID))
                .join(DOCTOR).on(APPOINTMENT.doctor().ID.eq(DOCTOR.ID))
                .join(DEPARTMENT).on(APPOINTMENT.doctor().department().ID.eq(DEPARTMENT.ID))
                .join(TIMESLOT).on(APPOINTMENT.timeslot().ID.eq(TIMESLOT.ID))
                .where(APPOINTMENT.doctor().FIRST_NAME.contains(doctorFirstName)
                        .or(APPOINTMENT.doctor().LAST_NAME.contains(doctorLastName)))
                .orderBy(TIMESLOT.START_TIME)
                .offset(offset)
                .limit(limit)
                .fetch(mapping(Appointment::new));
    }
}
