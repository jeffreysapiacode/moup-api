package io.moup.api.controller;

import io.moup.api.mapper.ContentMapper;
import io.moup.api.service.ContentService;
import io.moup.api.view.ContentView;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("content")
@AllArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final ContentMapper contentMapper;

    @Cacheable("content")
    @GetMapping
    public List<ContentView> getContentList() {
        return contentService.getList()
                .stream()
                .map(contentMapper::contentToContentView)
                .toList();
    }

    @DeleteMapping("{uuid}")
    public void delete(@PathVariable String uuid) {
        contentService.delete(uuid);
    }
}
