package io.moup.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("stream")
@AllArgsConstructor
public class StreamController {

    private static final String FORMAT="file:content/%s";
    private ResourceLoader resourceLoader;

    @GetMapping(value = "{filename}", produces = "audio/mp4")
    public Mono<Resource> stream(@PathVariable String filename) throws InterruptedException {
        return Mono.fromSupplier(()-> resourceLoader.
                getResource(String.format(FORMAT,filename)))   ;
    }

}
