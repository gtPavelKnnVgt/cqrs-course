package cqrs.my.demo.course.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cqrs.my.demo.course.events.domain.DomainEvent;
import lombok.*;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Appointment {
    private final static String INCORRECT_MED_RECORD = "1L";

    @JsonIgnore
    @Setter
    private List<DomainEvent> events;

    private Long id;
    private String appointmentDayOfWeek;
    private Patient patient;
    private Doctor doctor;
    private TimeSlot timeSlot;

    public static Appointment registerNewAppointment(Long id, Patient patient, Doctor doctor, TimeSlot timeSlot, String dayOfWeek) {
        if (patient.getMedicalRecordNumber().startsWith(INCORRECT_MED_RECORD)) {
            throw new IllegalArgumentException("medical record number starts from invalid symbols!");
        }
        Set<String> validDaysOfWeek = Arrays.stream(DayOfWeek.values()).map(DayOfWeek::name).collect(Collectors.toSet());
        if (!validDaysOfWeek.contains(dayOfWeek)) {
            throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);
        }
        return Appointment.builder()
                .id(id)
                .doctor(doctor)
                .patient(patient)
                .timeSlot(timeSlot)
                .appointmentDayOfWeek(dayOfWeek)
                .build();
    }
}
