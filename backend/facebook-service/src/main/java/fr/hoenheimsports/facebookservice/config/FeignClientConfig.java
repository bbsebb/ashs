package fr.hoenheimsports.facebookservice.config;

import feign.codec.ErrorDecoder;
import fr.hoenheimsports.facebookservice.feignClient.FeignErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {


    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
