package fr.hoenheimsports.gatewayservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class APiRootController {
    private static final Logger logger = LoggerFactory.getLogger(APiRootController.class);

    @GetMapping("/api")
    public Mono<RepresentationModel<?>> aggregateRoot(ServerWebExchange exchange) {
        logger.info("Réception d'une requête pour l'endpoint racine de l'API");
        String baseUrl = exchange.getRequest().getURI().getScheme() + "://" +
                exchange.getRequest().getURI().getHost() +
                (exchange.getRequest().getURI().getPort() != -1 ? ":" + exchange.getRequest().getURI().getPort() : "");
        logger.debug("URL de base construite: {}", baseUrl);

        RepresentationModel<?> root = new RepresentationModel<>();
        logger.debug("Ajout des liens aux différents services");

        root.add(Link.of(baseUrl + "/training-service" + "/api/teams").withRel("teams"));
        root.add(Link.of(baseUrl + "/training-service" + "/api/coaches").withRel("coaches"));
        root.add(Link.of(baseUrl + "/training-service" + "/api/halls").withRel("halls"));
        root.add(Link.of(baseUrl + "/training-service" + "/api/training-sessions").withRel("training-sessions"));
        root.add(Link.of(baseUrl + "/contact-service" + "/api").withRel("contact"));
        root.add(Link.of(baseUrl + "/facebook-service" + "/api/feeds").withRel("facebook"));

        logger.debug("Modèle racine créé avec {} liens", root.getLinks().toList().size());
        logger.info("Retour du modèle racine de l'API");
        return Mono.just(root);
    }
}
