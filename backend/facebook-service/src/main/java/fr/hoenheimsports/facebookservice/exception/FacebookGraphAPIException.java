package fr.hoenheimsports.facebookservice.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class FacebookGraphAPIException extends ErrorResponseException {

    public FacebookGraphAPIException(HttpStatusCode status, ProblemDetail body, Throwable cause) {
        super(status, body, cause);
    }

}
