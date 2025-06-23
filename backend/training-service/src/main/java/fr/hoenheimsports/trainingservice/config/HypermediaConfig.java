package fr.hoenheimsports.trainingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;

@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
public class HypermediaConfig {
    @Bean
    public HalConfiguration globalPolicy() {
        return new HalConfiguration() //
                .withRenderSingleLinksFor(LinkRelation.of("trainingSessionsList"), HalConfiguration.RenderSingleLinks.AS_ARRAY)
                .withRenderSingleLinksFor(LinkRelation.of("coachesList"), HalConfiguration.RenderSingleLinks.AS_ARRAY)
                .withRenderSingleLinksFor(LinkRelation.of("teamsList"), HalConfiguration.RenderSingleLinks.AS_ARRAY)
                .withRenderSingleLinksFor(LinkRelation.of("hallsList"), HalConfiguration.RenderSingleLinks.AS_ARRAY)
                .withRenderSingleLinksFor(LinkRelation.of("roleCoachesList"), HalConfiguration.RenderSingleLinks.AS_ARRAY);

    }
}

