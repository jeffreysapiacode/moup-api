package io.moup.api.controller;

import io.moup.api.view.ContentView;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("stream")
@AllArgsConstructor
public class StreamController {

    private static final String FORMAT="classpath:content/%s.m4a";
    private ResourceLoader resourceLoader;

    @GetMapping(value = "{filename}", produces = "audio/mp4")
    public Mono<Resource> stream(@PathVariable String filename) {
        return Mono.fromSupplier(()-> (Resource) resourceLoader.
                getResource(String.format(FORMAT,filename)))   ;
    }

}
