package io.moup.api.controller;

import io.moup.api.entity.Word;
import io.moup.api.repository.WordRepository;
import io.moup.api.service.AutoDictateService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping()
    public Page<Word> getRange(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam String contentUuid) {
        Pageable pageable = PageRequest.of(page, size);
        return wordRepository.findByContentUuidOrderByStartAsc(contentUuid, pageable);
    }


}
