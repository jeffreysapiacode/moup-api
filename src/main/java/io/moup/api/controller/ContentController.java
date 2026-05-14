package io.moup.api.controller;

import io.moup.api.mapper.ContentMapper;
import io.moup.api.service.ContentService;
import io.moup.api.view.ContentView;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("content")
@AllArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final ContentMapper contentMapper;

    @PostMapping("upload")
    private ContentView upload(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "transcript", required = false) MultipartFile transcript) {
        return contentService.uploadAndSave(title, description, file, transcript);
    }

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
