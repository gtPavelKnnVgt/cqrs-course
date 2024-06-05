package cqrs.my.demo.course.query;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cqrs.my.demo.course.db.Tables.*;
import static cqrs.my.demo.course.db.tables.Appointment.APPOINTMENT;
import static cqrs.my.demo.course.db.tables.Doctor.DOCTOR;
import static org.jooq.impl.DSL.row;

@Repository
@RequiredArgsConstructor
class QueryRepository {
    private final DSLContext ctx;

    List<Appointment> findAppointments(String doctorFirstName, String doctorLastName, int offset, int limit) {
        return ctx.select(APPOINTMENT.ID, APPOINTMENT.DAY_OF_WEEK,
                        row(PATIENT.ID,
                                PATIENT.FIRST_NAME,
                                PATIENT.LAST_NAME,
                                PATIENT.BIRTHDAY,
                                PATIENT.MEDICAL_RECORD_NUMBER)
                                .mapping(Patient::registerNewPatient).as("patient"),
                        row(TIMESLOT.ID,
                                TIMESLOT.START_TIME,
                                TIMESLOT.END_TIME)
                                .mapping(TimeSlot::new).as("time_slot"),
                        row(DOCTOR.ID,
                                DOCTOR.FIRST_NAME,
                                DOCTOR.LAST_NAME,
                                DOCTOR.SPECIALIZATION,
                                DOCTOR.LICENSE_NUMBER,
                                row(DEPARTMENT.ID,
                                        DEPARTMENT.NAME,
                                        DEPARTMENT.DESCRIPTION).mapping(Department::new),
                                row(TIMESLOT.ID,
                                        TIMESLOT.START_TIME,
                                        TIMESLOT.END_TIME).mapping(TimeSlot::new))
                                .mapping(Doctor::assignDoctorToDepartment).as("doctor"))
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
                .fetch(new AppointmentRecordMapper());
    }
}
