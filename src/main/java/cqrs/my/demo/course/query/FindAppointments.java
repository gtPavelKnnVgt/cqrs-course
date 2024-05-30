package cqrs.my.demo.course.query;

public record FindAppointments(String doctorFirstName, String doctorLastName,
                               int offset, int limit) {
}
