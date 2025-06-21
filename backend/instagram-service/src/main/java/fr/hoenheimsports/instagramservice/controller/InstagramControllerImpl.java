package fr.hoenheimsports.instagramservice.controller;

import fr.hoenheimsports.instagramservice.service.InstagramService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InstagramControllerImpl {
    private final InstagramService instagramService;

    public InstagramControllerImpl(InstagramService instagramService) {
        this.instagramService = instagramService;
    }

    @GetMapping()
    public String getMe() {
        return this.instagramService.getMe();
    }
}
