package io.moup.api.service;

import io.moup.api.entity.Word;
import io.moup.api.model.whispercpp.Root;
import io.moup.api.model.whispercpp.Transcription;
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
            int index = 0;
            for (Transcription transcription: root.getTranscription()) {
                Double start = (double) transcription.getOffsets().getFrom() / 1000;
                Double end = (double) transcription.getOffsets().getTo() / 1000;
                wordList.add(Word.builder()
                        .start(start)
                        .end(end)
                        .word(transcription.getText() + getPunctuationIfExists(index, root.getTranscription()))
                        .contentUuid(contentUuid)
                        .build());
                index++;
            }
//            Code for original Whisper - 5/7/2026
//            for (Segment segment: root.getSegments()) {
//                for (io.moup.api.model.whisper.Word word: segment.getWords()) {
//                    Word autoDictateWord = mapper.wordToWord(word);
//                    autoDictateWord.setContentUuid(contentUuid);
//                    wordList.add(autoDictateWord);
//                }
//            }
            wordRepository.saveAll(wordList);
        } catch(Exception e) {
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }
    }

    private String getPunctuationIfExists(Integer index, List<Transcription> transcriptions) {
        // If .,?!, append punctuation to text and delete next item in array
        String value = "";
        if ((index + 1) <= (transcriptions.size() - 1) ) {
            Transcription lookAhead = transcriptions.get(index + 1);
            if (lookAhead.getText().matches("\\p{Punct}")) {
                value = lookAhead.getText();
                transcriptions.remove(index + 1);
            }
        }
        return value;
    }
}
