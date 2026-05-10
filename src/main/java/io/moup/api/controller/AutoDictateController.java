package io.moup.api.controller;

import io.moup.api.entity.Word;
import io.moup.api.repository.WordRepository;
import io.moup.api.service.AutoDictateService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("auto-dictate")
@AllArgsConstructor
public class AutoDictateController {

    private final AutoDictateService autoDictateService;
    private final WordRepository wordRepository;

    @PostMapping("upload")
    public void upload(
            @RequestParam("contentUuid") String contentUuid,
            @RequestParam("transcript") MultipartFile transcript) {
        autoDictateService.importTranscript(contentUuid, transcript);
    }

    @Cacheable("words")
    @GetMapping()
    public List<Word> getRange(
            @RequestParam(required = false) Double start,
            @RequestParam(required = false) Double end,
            @RequestParam(required = false) boolean all,
            @RequestParam String contentUuid) {
        if (all) {
            return wordRepository.findByContentUuidOrderByStartAsc(contentUuid);
        }
        return wordRepository.findByContentUuidAndStartGreaterThanEqualAndStartLessThanOrderByStartAsc(contentUuid, start, end);
    }
}
