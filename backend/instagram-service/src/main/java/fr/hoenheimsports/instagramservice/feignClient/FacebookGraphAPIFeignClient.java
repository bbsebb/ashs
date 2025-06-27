package fr.hoenheimsports.instagramservice.feignClient;

import fr.hoenheimsports.instagramservice.feignClient.dto.GraphApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facebook-graph-api", url = "${facebook.api.url}")
public interface FacebookGraphAPIFeignClient {

    @GetMapping("/me")
    String getMe(@RequestParam("access_token") String accessToken);

    @GetMapping("/500999826723464/feed")
    GraphApiResponse getFeed(@RequestParam("fields") String fields, @RequestParam("limit") int limit, @RequestParam("access_token") String accessToken);
}
