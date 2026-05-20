package io.moup.api.service;

import io.moup.api.entity.Content;
import io.moup.api.entity.Word;
import io.moup.api.mapper.ContentMapper;
import io.moup.api.repository.ContentRepository;
import io.moup.api.util.FilenameUtils;
import io.moup.api.view.ContentView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.mp4parser.IsoFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.Year;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class ContentService {

    @Value("${app.environment.production}")
    private Boolean production;

    @Value("${app.http.production-transfer-host}")
    private String productionTransferHost;

    @Value("${app.allowed-file-types.audio}")
    private String[] allowedFileTypesAudio;

    @Value("${app.allowed-file-types.video}")
    private String[] allowedFileTypesVideo;

    private static final String BASE_DIRECTORY = "./content/";

    private final ContentRepository contentRepository;
    private final WordService wordService;
    private final ContentMapper mapper;
    private final RestTemplate restTemplate;

    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public ContentService(ContentRepository contentRepository, WordService wordService, ContentMapper mapper, RestTemplate restTemplate) {
        this.contentRepository = contentRepository;
        this.wordService = wordService;
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    @CacheEvict(value = "content", allEntries = true)
    @Transactional
    public ContentView uploadAndSave(String title, String description, MultipartFile file, MultipartFile thumbnail, MultipartFile transcript) throws Exception {
        Instant uploadedOn = Instant.now();
        String ext = org.apache.commons.io.FilenameUtils.getExtension(file.getOriginalFilename());
        String baseFilename = FilenameUtils.formatFilename(title);
        String fullFileName = baseFilename + "." + ext;
        String filePath = BASE_DIRECTORY + fullFileName;
        File mediaFile = new File(filePath);
        // Check if the file already exists and throw an error is it does. Tell the user to re-upload instead.

        FileUtils.copyInputStreamToFile(file.getInputStream(), mediaFile);
        double durationInSeconds;
        try (IsoFile audioFile = new IsoFile(filePath)) {
            durationInSeconds = (double)
                    audioFile.getMovieBox().getMovieHeaderBox().getDuration() /
                    audioFile.getMovieBox().getMovieHeaderBox().getTimescale();
        }
        writeID3Tags(title, description, mediaFile);
        Content content = contentRepository.save(Content.builder()
                .title(title)
                .description(description)
                .duration(durationInSeconds)
                .uploadedOn(uploadedOn)
                .filename(fullFileName)
                .mmx(generateMmx())
                .active(Boolean.TRUE)
                .downloadCount(0L)
                .playCount(0L)
                .build());
        wordService.importTranscript(content.getUuid(), transcript);
        return mapper.contentToContentView(content);
    }

    @Transactional(readOnly = true)
    public void reupload(String uuid, MultipartFile file) throws IOException {
        Content content = get(uuid);
        String filePath = BASE_DIRECTORY + content.getFilename();
        File mediaFile = new File(filePath);
        FileUtils.copyInputStreamToFile(file.getInputStream(), mediaFile);
        writeID3Tags(content.getTitle(), content.getDescription(), mediaFile);
    }

    @Transactional
    public void transfer(String uuid) {
        if (production) {
            throw new RuntimeException("Cannot transfer while in production.");
        }
        Content content = get(uuid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(new File("content/" + content.getFilename())));
        body.add("title", content.getTitle());
        body.add("description", content.getDescription());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<ContentView> response = restTemplate.postForEntity(productionTransferHost + "/content/upload", requestEntity, ContentView.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            // Import words for transcript
            log.info("Content upload service returned 200. Continuing to upload words...");
            List<Word> words = wordService.getAllByContentUuid(uuid);
            restTemplate.postForObject(productionTransferHost + "/auto-dictate/receive-transfer?contentUuid=" + response.getBody().getUuid(), words, Void.class);
        } else {
            throw new RuntimeException("Something went wrong. Received status code: " + response.getStatusCode().value());
        }
    }

    public void saveAlbumArt(MultipartFile file) throws IOException {
        String filePath = "./content/album-artwork.png";
        File mp4File = new File(filePath);
        FileUtils.copyInputStreamToFile(file.getInputStream(), mp4File);
    }

    @Transactional
    public void incrementPlayCount(String uuid) {
        Content content = get(uuid);
        content.setPlayCount(content.getPlayCount() + 1);
    }

    @Transactional
    public void incrementDownloadCount(String filename) {
        Content content = findByFilename(filename);
        content.setDownloadCount(content.getDownloadCount() + 1);
    }

    @Transactional(readOnly = true)
    public Content get(String uuid) {
        return contentRepository.findById(uuid)
                .orElseThrow(()-> new RuntimeException("Could not find content with uuid " + uuid));
    }

    @Transactional(readOnly = true)
    public List<Content> getList() {
        return contentRepository.findAll(Sort.by("uploadedOn").descending());
    }

    @Transactional
    public void delete(String uuid) throws IOException {
        Content content = get(uuid);
        contentRepository.deleteById(uuid);
        wordService.deleteByContentUuid(uuid);
        // Delete file(s)
        Files.delete(Paths.get(BASE_DIRECTORY + content.getFilename()));
    }

    private Content findByFilename(String filename) {
        return contentRepository.findByFilename(filename)
                .orElseThrow(() -> new RuntimeException("Content with filename not found: " + filename));
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

    private String generateMmx() {
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }
}
