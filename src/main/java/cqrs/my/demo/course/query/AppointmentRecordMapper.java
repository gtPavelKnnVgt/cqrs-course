package cqrs.my.demo.course.query;

import org.jooq.Record;
import org.jooq.RecordMapper;

import static cqrs.my.demo.course.db.Tables.*;

public class AppointmentRecordMapper implements RecordMapper<Record, Appointment> {
    @Override
    public Appointment map(Record record) {
        return Appointment.registerNewAppointment(
                record.getValue(APPOINTMENT.ID),
                record.get("patient", Patient.class),
                record.get("doctor", Doctor.class),
                record.get("time_slot", TimeSlot.class),
                record.getValue(APPOINTMENT.DAY_OF_WEEK)
        );
    }
}
