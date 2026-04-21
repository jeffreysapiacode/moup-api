package io.moup.api.controller;

import io.moup.api.service.UploadService;
import io.moup.api.view.UploadView;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequestMapping("upload")
@AllArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping
    private UploadView upload(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) {
        return UploadView.builder()
                .uploadedOn(Instant.now())
                .content(uploadService.uploadAndSave(title, description, file))
                .build();
    }

}
