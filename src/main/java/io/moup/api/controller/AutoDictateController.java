package io.moup.api.controller;

import io.moup.api.entity.Word;
import io.moup.api.repository.WordRepository;
import io.moup.api.service.AutoDictateService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(value = {"http://192.168.1.18:4200"})
public class AutoDictateController {

    private final AutoDictateService autoDictateService;
    private final WordRepository wordRepository;

    @PostMapping("upload")
    public void upload(
            @RequestParam("contentUuid") String contentUuid,
            @RequestParam("transcript") MultipartFile transcript) {
        autoDictateService.importTranscript(contentUuid, transcript);
    }

    @GetMapping()
    public List<Word> getRange(
            @RequestParam Double start,
            @RequestParam Double end,
            @RequestParam String contentUuid) {
        return wordRepository.findByContentUuidAndStartGreaterThanEqualAndEndLessThanOrderByStartAsc(contentUuid, start, end);
    }


}
