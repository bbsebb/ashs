package fr.hoenheimsports.trainingservice.exception;

public class HallAlreadyExistsException extends EntityAlreadyExistsException {
    public HallAlreadyExistsException(String message) {
        super(message);
    }
}
