package fr.hoenheimsports.facebookservice.feignClient;

import feign.Response;
import feign.codec.ErrorDecoder;
import fr.hoenheimsports.facebookservice.exception.FacebookGraphAPIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Erreur lors de l'appel à l'API Facebook: méthode={}, status={}", methodKey, response.status());
        log.debug("Détails de la réponse en erreur: {}", response.toString());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problemDetail.setTitle("Erreur Facebook Graph API");
        problemDetail.setDetail("Erreur inconnue lors de l'appel à l'API Facebook/Instagram.");
        problemDetail.setType(URI.create("https://developers.facebook.com/docs/graph-api/"));

        log.error("Création d'une exception FacebookGraphAPIException avec status BAD_GATEWAY");
        return new FacebookGraphAPIException(HttpStatus.BAD_GATEWAY, problemDetail, null);
    }
}
