package fr.hoenheimsports.instagramservice.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facebook-graph-api", url = "${facebook.api.url}")
public interface FacebookGraphAPI {

    @GetMapping("/me")
    String getMe(@RequestParam("access_token") String accessToken);
}
