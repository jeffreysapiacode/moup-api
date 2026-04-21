package io.moup.api.controller;

import io.moup.api.view.ContentView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("stream")
public class StreamController {

    @GetMapping
    public ContentView test() {
        return ContentView.builder()
                .title("How To Get To Heaven")
                .description("Here is a description of how to get to Heaven.")
                .build();
    }

}
