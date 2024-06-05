package cqrs.my.demo.course.query;

import cqrs.my.demo.course.exception.DoctorNotValidException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Doctor {
    private static final String NAME_REGEX = "^[\\p{L}\\s]+$";

    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String licenseNumber;
    private Department department;
    private TimeSlot timeSlot;

    public static Doctor assignDoctorToDepartment(Long id, String firstName, String lastName,
                                                  String specialization, String licenseNumber, Department department,
                                                  TimeSlot workingTimeSlot) {
        if (!isValidName(firstName) || !isValidName(lastName)) {
            throw new DoctorNotValidException(firstName, lastName);
        }
        Set<String> doctorSpecs = Arrays.stream(DoctorSpecialization.values()).map(DoctorSpecialization::name).collect(Collectors.toSet());
        if (!doctorSpecs.contains(specialization)) {
            throw new IllegalArgumentException("Specialization " + specialization + " is not valid!");
        }
        LocalDateTime invalidStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(6, 0));
        LocalDateTime invalidEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 0));
        if (invalidStart.isEqual(workingTimeSlot.startTime()) || invalidStart.isAfter(workingTimeSlot.startTime())
                || invalidEnd.isBefore(workingTimeSlot.endTime()) || invalidEnd.isEqual(workingTimeSlot.endTime())) {
            throw new IllegalArgumentException("working slot is too early or too late!");
        }
        return Doctor.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .licenseNumber(licenseNumber)
                .department(department)
                .timeSlot(workingTimeSlot)
                .build();
    }

    private static boolean isValidName(String name) {
        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
}
