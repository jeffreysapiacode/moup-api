package io.moup.api.service;

import io.moup.api.entity.Content;
import io.moup.api.repository.ContentRepository;
import io.moup.api.util.FileNameUtils;
import io.moup.api.view.ContentView;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.mp4parser.IsoFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

@Service
@AllArgsConstructor
public class UploadService {

    private static final String CONTENT_DIR = "./content/test.m4a";

    private final ContentRepository contentRepository;

    public ContentView uploadAndSave(String title, String description, MultipartFile file) {
        IsoFile audioFile;
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(CONTENT_DIR));
            audioFile = new IsoFile(CONTENT_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Could not upload file: " + e.getMessage());
        }
        double lengthInSeconds = (double)
                audioFile.getMovieBox().getMovieHeaderBox().getDuration() /
                audioFile.getMovieBox().getMovieHeaderBox().getTimescale();
        Content content = contentRepository.save(Content.builder()
                .title(title)
                .description(description)
                .duration(lengthInSeconds)
                .uploadedOn(Instant.now())
                .filename(FileNameUtils.formatFilename(title))
                .build());
        return ContentView.builder()
                .uuid(content.getUuid())
                .title(content.getTitle())
                .filename(content.getFilename())
                .description(content.getDescription())
                .duration(content.getDuration())
                .uploadedOn(content.getUploadedOn())
                .build();
    }

}
