package io.moup.api.controller;

import io.moup.api.mapper.ContentMapper;
import io.moup.api.service.ContentService;
import io.moup.api.view.ContentView;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("content")
@AllArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final ContentMapper contentMapper;

    @PostMapping("upload")
    public ContentView upload(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "transcript", required = false) MultipartFile transcript) throws Exception {
        return contentService.uploadAndSave(title, description, file, transcript);
    }

    @PostMapping("upload/album-art")
    public void uploadAlbumArt(@RequestParam("file") MultipartFile file) throws IOException {
        contentService.saveAlbumArt(file);
    }

    @PostMapping("push")
    public void push(@RequestParam String uuid) {
        contentService.push(uuid);
    }

    @Cacheable("content")
    @GetMapping
    public List<ContentView> getContentList() {
        return contentService.getList()
                .stream()
                .map(contentMapper::contentToContentView)
                .toList();
    }

    @CacheEvict(value = "content", allEntries = true)
    @DeleteMapping("/cache")
    public void clearCache() {
    }

    @DeleteMapping("{uuid}")
    public void delete(@PathVariable String uuid) {
        contentService.delete(uuid);
    }
}
