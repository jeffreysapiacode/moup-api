package io.moup.api.service;

import io.moup.api.entity.Content;
import io.moup.api.mapper.ContentMapper;
import io.moup.api.repository.ContentRepository;
import io.moup.api.util.FilenameUtils;
import io.moup.api.view.ContentView;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.mp4parser.IsoFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

@Service
@AllArgsConstructor
public class UploadService {

    private final ContentMapper mapper;
    private final ContentRepository contentRepository;

    public ContentView uploadAndSave(String title, String description, MultipartFile file) {
        IsoFile audioFile;
        // TODO - Add ability to have date in the filename
        String filename = "./content/" + FilenameUtils.formatFilename(title) + org.apache.commons.io.FilenameUtils.getExtension(file.getOriginalFilename());
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(filename));
            audioFile = new IsoFile(filename);
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
                .filename(FilenameUtils.formatFilename(title))
                .build());
        return mapper.contentToContentView(content);
    }

}
