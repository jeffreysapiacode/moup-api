package io.moup.api.service;

import io.moup.api.entity.Word;
import io.moup.api.model.whisper.Root;
import io.moup.api.model.whisper.Segment;
import io.moup.api.repository.ContentRepository;
import io.moup.api.repository.WordRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AutoDictateService {

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final WordRepository wordRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public void importTranscript(String contentUuid, MultipartFile transcript) {
        if (transcript.isEmpty()) {
            return;
        }
        Root root;
        try {
            if (!contentRepository.existsById(contentUuid)) {
                throw new RuntimeException("Content UUID " + contentUuid + " does not exist.");
            }
            String json = IOUtils.toString(transcript.getInputStream(), StandardCharsets.UTF_8);
            root = OBJECT_MAPPER.readValue(json, Root.class);
        } catch(Exception e) {
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }
        List<Word> wordList = new ArrayList<>();
        for (Segment segment: root.getSegments()) {
            for (io.moup.api.model.whisper.Word word: segment.getWords()) {
                wordList.add(Word.builder()
                        .start(word.getStart())
                        .end(word.getEnd())
                        .word(word.getWord())
                        .contentUuid(contentUuid)
                        .build());
            }
        }
        wordRepository.saveAll(wordList);
    }

    @Transactional
    public void deleteByContentUuid(String contentUuid) {
        wordRepository.deleteByContentUuid(contentUuid);
    }
}
