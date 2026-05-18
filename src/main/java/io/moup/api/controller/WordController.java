package io.moup.api.controller;

import io.moup.api.mapper.WordMapper;
import io.moup.api.repository.WordRepository;
import io.moup.api.service.WordService;
import io.moup.api.view.WordView;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("auto-dictate")
@AllArgsConstructor
public class WordController {

    private final WordRepository wordRepository;
    private final WordService wordService;
    private final WordMapper mapper;

    @PostMapping("receive-transfer")
    public void importFromTransfer(@RequestBody List<WordView> words, @RequestParam String contentUuid) {
        wordService.importFromTransfer(words, contentUuid);
    }

    @Cacheable("word")
    @GetMapping()
    public List<WordView> getRange(
            @RequestParam(required = false) Double start,
            @RequestParam(required = false) Double end,
            @RequestParam(required = false) boolean all,
            @RequestParam String contentUuid) {
        if (all) {
            return wordRepository.findAllByContentUuidOrderByStartAsc(contentUuid)
                    .stream()
                    .map((mapper::WordToWordView))
                    .toList();
        }
        return wordRepository.findAllByContentUuidAndStartGreaterThanEqualAndStartLessThanOrderByStartAsc(contentUuid, start, end)
                .stream()
                .map((mapper::WordToWordView))
                .toList();
    }

    @CacheEvict(value = "word",  allEntries = true)
    @DeleteMapping("/cache")
    public void clearCache() {
    }
}
