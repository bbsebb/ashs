package fr.hoenheimsports.gatewayservice;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class APiRootController {
    @GetMapping("/api")
    public Mono<RepresentationModel<?>> aggregateRoot(ServerWebExchange exchange) {
        String baseUrl = exchange.getRequest().getURI().getScheme() + "://" +
                exchange.getRequest().getURI().getHost() +
                (exchange.getRequest().getURI().getPort() != -1 ? ":" + exchange.getRequest().getURI().getPort() : "");
        RepresentationModel<?> root = new RepresentationModel<>();

        root.add(Link.of(baseUrl + "/training-service" + "/api/teams").withRel("teams"));
        root.add(Link.of(baseUrl + "/training-service" + "/api/coaches").withRel("coaches"));
        root.add(Link.of(baseUrl + "/training-service" + "/api/halls").withRel("halls"));
        root.add(Link.of(baseUrl + "/training-service" + "/api/training-sessions").withRel("training-sessions"));
        root.add(Link.of(baseUrl + "/contact-service" + "/api").withRel("contact"));
        root.add(Link.of(baseUrl + "/instagram-service" + "/api").withRel("instagram"));

        return Mono.just(root);
    }
}

