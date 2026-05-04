package io.moup.api.service;

import io.moup.api.entity.AutoDictate;
import io.moup.api.mapper.AutoDictateMapper;
import io.moup.api.model.AutoDictateModel;
import io.moup.api.model.Segment;
import io.moup.api.model.Word;
import io.moup.api.repository.AutoDictateRepository;
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
    private final AutoDictateMapper mapper;
    private final AutoDictateRepository autoDictateRepository;

    public void importTranscript(MultipartFile transcript) {
        try {
            String json = IOUtils.toString(transcript.getInputStream(), StandardCharsets.UTF_8);
            AutoDictateModel autoDictateModel = OBJECT_MAPPER.readValue(json, AutoDictateModel.class);
            List<AutoDictate> autoDictateList = new ArrayList<>();
            for (Segment segment: autoDictateModel.getSegments()) {
                for (Word word: segment.getWords()) {
                    autoDictateList.add(mapper.wordToAutoDictate(word));
                }
            }
            autoDictateRepository.saveAll(autoDictateList);
        } catch(Exception e) {
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }
    }

}
