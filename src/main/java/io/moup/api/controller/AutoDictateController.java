package io.moup.api.controller;

import io.moup.api.service.AutoDictateService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("auto-dictate")
@AllArgsConstructor
public class AutoDictateController {

    private final AutoDictateService autoDictateService;

    @PostMapping("upload")
    public void upload(
            @RequestParam("contentUuid") String contentUuid,
            @RequestParam("transcript") MultipartFile transcript) {
        autoDictateService.importTranscript(contentUuid, transcript);
    }
}
