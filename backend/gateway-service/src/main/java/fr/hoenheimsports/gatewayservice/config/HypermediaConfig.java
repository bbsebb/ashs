package fr.hoenheimsports.gatewayservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DelegatingLinkRelationProvider;

@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
public class HypermediaConfig {
    private static final Logger logger = LoggerFactory.getLogger(HypermediaConfig.class);

    public HypermediaConfig() {
        logger.debug("Initialisation de la configuration Hypermedia");
    }

    @Bean
    public LinkRelationProvider linkRelationProvider() {
        logger.debug("Création du bean LinkRelationProvider");
        return new DelegatingLinkRelationProvider();
    }
}
