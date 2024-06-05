package cqrs.my.demo.course.query;

import org.jooq.Record;
import org.jooq.RecordMapper;

import static cqrs.my.demo.course.db.Tables.DOCTOR;


public class DoctorRecordMapper implements RecordMapper<Record, Doctor> {
    private final Department department;
    private final TimeSlot timeslot;

    public DoctorRecordMapper(Department department, TimeSlot timeslot) {
        this.department = department;
        this.timeslot = timeslot;
    }

    @Override
    public Doctor map(Record record) {
        return Doctor.assignDoctorToDepartment(
                record.getValue(DOCTOR.ID),
                record.getValue(DOCTOR.FIRST_NAME),
                record.getValue(DOCTOR.LAST_NAME),
                record.getValue(DOCTOR.SPECIALIZATION),
                record.getValue(DOCTOR.LICENSE_NUMBER),
                department,
                timeslot
        );
    }
}
