package fr.hoenheimsports.trainingservice.exception;

public class TeamAlreadyExistsException extends EntityAlreadyExistsException {
    public TeamAlreadyExistsException(String message) {
        super(message);
    }
}
