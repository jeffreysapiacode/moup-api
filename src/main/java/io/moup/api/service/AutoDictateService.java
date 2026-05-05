package io.moup.api.service;

import io.moup.api.entity.Word;
import io.moup.api.mapper.WordMapper;
import io.moup.api.model.Root;
import io.moup.api.model.Segment;
import io.moup.api.repository.ContentRepository;
import io.moup.api.repository.WordRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AutoDictateService {

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final WordMapper mapper;
    private final WordRepository wordRepository;
    private final ContentRepository contentRepository;

    public void importTranscript(String contentUuid, MultipartFile transcript) {
        try {
            if (!contentRepository.existsById(contentUuid)) {
                throw new RuntimeException("Content UUID " + contentUuid + " does not exist.");
            }
            String json = IOUtils.toString(transcript.getInputStream(), StandardCharsets.UTF_8);
            Root root = OBJECT_MAPPER.readValue(json, Root.class);
            List<Word> wordList = new ArrayList<>();
            for (Segment segment: root.getSegments()) {
                for (io.moup.api.model.Word word: segment.getWords()) {
                    Word autoDictateWord = mapper.wordToWord(word);
                    autoDictateWord.setContentUuid(contentUuid);
                    wordList.add(autoDictateWord);
                }
            }
            wordRepository.saveAll(wordList);
        } catch(Exception e) {
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }
    }
}
