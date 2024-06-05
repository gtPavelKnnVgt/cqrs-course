package cqrs.my.demo.course.exception;

public class PatientNotValidException extends RuntimeException {
    private static final String ERROR_MESSAGE = "first name %s or last name %s is not valid.";
    private static final String ERROR_MESSAGE_WITH_BIRTH = "birth date is null. It's not appropriate.";

    public PatientNotValidException(String firstName, String lastName) {
        super(String.format(ERROR_MESSAGE, firstName, lastName));
    }

    public PatientNotValidException() {
        super(ERROR_MESSAGE_WITH_BIRTH);
    }
}
