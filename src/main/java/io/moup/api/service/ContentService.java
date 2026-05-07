package io.moup.api.service;

import io.moup.api.entity.Content;
import io.moup.api.mapper.ContentMapper;
import io.moup.api.repository.ContentRepository;
import io.moup.api.util.FilenameUtils;
import io.moup.api.view.ContentView;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.mp4parser.IsoFile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.Year;
import java.util.List;

@Service
@AllArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final AutoDictateService autoDictateService;
    private final ContentMapper mapper;

    @Transactional(readOnly = true)
    public List<Content> getList() {
        return contentRepository.findAll(Sort.by("uploadedOn").descending());
    }

    public ContentView uploadAndSave(String title, String description, MultipartFile file, MultipartFile transcript) {
        IsoFile audioFile;
        Instant uploadedOn = Instant.now();
        String ext = org.apache.commons.io.FilenameUtils.getExtension(file.getOriginalFilename());
        String filename =  FilenameUtils.formatFilename(title) + "." + ext;
        String filePath = "./content/" + filename;
        File mp4File = new File(filePath);
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), mp4File);
            audioFile = new IsoFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not upload file: " + e.getMessage());
        }
        double lengthInSeconds = (double)
                audioFile.getMovieBox().getMovieHeaderBox().getDuration() /
                audioFile.getMovieBox().getMovieHeaderBox().getTimescale();
        // Write ID3 tags
        writeID3Tags(title, description, mp4File);
        Content content = contentRepository.save(Content.builder()
                .title(title)
                .description(description)
                .duration(lengthInSeconds)
                .uploadedOn(uploadedOn)
                .filename(filename)
                .build());
        autoDictateService.importTranscript(content.getUuid(), transcript);
        return mapper.contentToContentView(content);
    }

    private void writeID3Tags(String title, String description, File mp4File) {
        try {
            AudioFile audioFile = AudioFileIO.read(mp4File);
            Tag tag = audioFile.getTag();
            tag.setField(FieldKey.ARTIST, "How To Get To Heaven");
            tag.setField(FieldKey.ALBUM_ARTIST, "How To Get To Heaven");
            tag.setField(FieldKey.ALBUM, "How To Get To Heaven");
            tag.setField(FieldKey.TITLE, title);
            tag.setField(FieldKey.CUSTOM1, description);
            tag.setField(FieldKey.YEAR, String.valueOf(Year.now().getValue()));
            tag.setField(FieldKey.GENRE, "Talk");

            Artwork artwork = Artwork.createArtworkFromFile(new File("./content/album-artwork.png"));
            tag.deleteArtworkField();
            tag.setField(artwork);
            audioFile.setTag(tag);
            AudioFileIO.write(audioFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
