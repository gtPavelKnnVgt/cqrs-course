package cqrs.my.demo.course.query;

import cqrs.my.demo.course.exception.PatientNotValidException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Patient {
    private static final String NAME_REGEX = "^[\\p{L}\\s]+$";
    private static final String NOT_VALID_RECORD_NUMBERS = "^(?!FHWR).*$";

    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String medicalRecordNumber;

    public static Patient registerNewPatient(Long id, String firstName, String lastName, LocalDate birthday, String medicalRecordNumber) {
        if (!isValidName(firstName) || !isValidName(lastName)) {
            throw new PatientNotValidException(firstName, lastName);
        }

        if (!isValidMedicalRecordNumber(medicalRecordNumber)) {
            throw new PatientNotValidException();
        }

        return Patient.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .birthday(birthday)
                .medicalRecordNumber(medicalRecordNumber)
                .build();
    }

    private static boolean isValidName(String name) {
        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private static boolean isValidMedicalRecordNumber(String medicalRecordNumber) {
        Pattern pattern = Pattern.compile(NOT_VALID_RECORD_NUMBERS);
        Matcher matcher = pattern.matcher(medicalRecordNumber);
        return matcher.matches();
    }
}
