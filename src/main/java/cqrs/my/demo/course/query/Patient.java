package cqrs.my.demo.course.query;

import java.time.LocalDate;

record Patient(Long id, String firstName, String lastName, LocalDate birthday,
               String medicalRecordNumber) {
}
