package fr.hoenheimsports.contactservice.exception;

/**
 * Custom exception that represents errors occurring during the email sending process.
 * This exception is used to indicate and encapsulate issues related to email functionality.
 */
public class EmailException extends RuntimeException {

    /**
     * Constructs a new EmailException with the specified cause.
     *
     * @param cause the underlying cause of the exception, typically an instance of {@link Throwable}.
     */
    public EmailException(Throwable cause) {
        super(cause);
    }
}
