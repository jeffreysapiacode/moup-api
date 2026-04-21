package io.moup.api.service;

import io.moup.api.entity.Content;
import io.moup.api.repository.ContentRepository;
import io.moup.api.view.ContentView;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.mp4parser.IsoFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Instant;

@Service
@AllArgsConstructor
public class UploadService {

    private final ContentRepository contentRepository;

    public ContentView uploadAndSave(String title, String description, MultipartFile file) {
        IsoFile isoFile;
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File("./content/test.m4a"));
            isoFile = new IsoFile("./content/test.m4a");
        } catch (Exception e) {
            throw new RuntimeException("Could not upload file: " + e.getMessage());
        }
        double lengthInSeconds = (double)
                isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        Content content = contentRepository.save(Content.builder()
                .title(title)
                .description(description)
                .duration(lengthInSeconds)
                .uploadedOn(Instant.now())
                .build());
        return ContentView.builder()
                .uuid(content.getUuid())
                .title(content.getTitle())
                .description(content.getDescription())
                .duration(content.getDuration())
                .uploadedOn(content.getUploadedOn())
                .build();
    }

}
