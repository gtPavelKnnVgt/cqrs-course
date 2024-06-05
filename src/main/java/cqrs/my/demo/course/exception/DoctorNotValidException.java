package cqrs.my.demo.course.exception;

public class DoctorNotValidException extends RuntimeException {
    private static final String ERROR_MESSAGE = "first name %s or last name %s is not valid.";
    private static final String ERROR_MESSAGE_WITH_DAY_OF_WEEK = "invalid day of week %s";

    public DoctorNotValidException(String firstName, String lastName) {
        super(String.format(ERROR_MESSAGE, firstName, lastName));
    }

    public DoctorNotValidException(String dayOfWeek) {
        super(String.format(ERROR_MESSAGE_WITH_DAY_OF_WEEK, dayOfWeek));
    }
}
