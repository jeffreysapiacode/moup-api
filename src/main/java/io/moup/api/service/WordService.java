package io.moup.api.service;

import io.moup.api.entity.Word;
import io.moup.api.model.whisper.Root;
import io.moup.api.repository.ContentRepository;
import io.moup.api.repository.WordRepository;
import io.moup.api.view.WordView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class WordService {

    @Value("${app.environment.production}")
    private Boolean production;

    private final ObjectMapper objectMapper;
    private final WordRepository wordRepository;
    private final ContentRepository contentRepository;

    public WordService(ObjectMapper objectMapper, WordRepository wordRepository, ContentRepository contentRepository) {
        this.objectMapper = objectMapper;
        this.wordRepository = wordRepository;
        this.contentRepository = contentRepository;
    }

    @Transactional
    public void importTranscript(String contentUuid, MultipartFile transcript) throws IOException {
        if (Objects.isNull(transcript) || transcript.isEmpty()) {
            log.info("No transcript data found. Skipping import...");
            return;
        }
        List<Word> wordList = new ArrayList<>();
        String json = IOUtils.toString(transcript.getInputStream(), StandardCharsets.UTF_8);
        Root root = objectMapper.readValue(json, Root.class);
        root.getSegments().forEach((segment)-> segment
                .getWords().forEach((word) -> wordList
                    .add(Word.builder()
                        .start(word.getStart())
                        .end(word.getEnd())
                        .word(word.getWord())
                        .contentUuid(contentUuid)
                        .build())));
        wordRepository.saveAll(wordList);
    }

    @Transactional
    public void importFromTransfer(List<WordView> words, String contentUuid) {
        if (!production) {
            throw new RuntimeException("Must be in production to receive imports");
        }
        if (!contentRepository.existsById(contentUuid)) {
            throw new RuntimeException("Content with uuid " + contentUuid + " does not exist.");
        }
        List<Word> wordList = new ArrayList<>();
        words.forEach((wordView) ->
                wordList.add(Word.builder()
                        .contentUuid(contentUuid)
                        .start(wordView.getStart())
                        .end(wordView.getEnd())
                        .word(wordView.getWord())
                        .build()));
        wordRepository.saveAll(wordList);
    }

    @Transactional(readOnly = true)
    public List<Word> getAllByContentUuid(String contentUuid) {
        return wordRepository.findAllByContentUuid(contentUuid);
    }

    @Transactional
    public void deleteByContentUuid(String contentUuid) {
        wordRepository.deleteAllByContentUuid(contentUuid);
    }
}
