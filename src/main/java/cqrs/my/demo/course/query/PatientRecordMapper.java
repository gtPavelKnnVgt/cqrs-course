package cqrs.my.demo.course.query;

import org.jooq.Record;
import org.jooq.RecordMapper;

import static cqrs.my.demo.course.db.Tables.PATIENT;

public class PatientRecordMapper implements RecordMapper<Record, Patient> {
    @Override
    public Patient map(Record record) {
        return Patient.registerNewPatient(
                record.getValue(PATIENT.ID),
                record.getValue(PATIENT.FIRST_NAME),
                record.getValue(PATIENT.LAST_NAME),
                record.getValue(PATIENT.BIRTHDAY),
                record.getValue(PATIENT.MEDICAL_RECORD_NUMBER)
        );
    }
}
