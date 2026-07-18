package io.moup.api.controller;

import io.moup.api.entity.Content;
import io.moup.api.mapper.ContentMapper;
import io.moup.api.service.ContentService;
import io.moup.api.view.ContentView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("content")
@AllArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final ContentMapper contentMapper;
    private final ContentMapper mapper;

    @PostMapping("upload")
    public ContentView upload(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "transcript", required = false) MultipartFile transcript) throws Exception {
        Content content = contentService.uploadAndSave(title, description, file, transcript);
        return mapper.contentToContentView(content);
    }

    @PostMapping("reupload")
    public void reupload(@RequestParam String uuid,
                         @RequestParam("file") MultipartFile file) throws IOException {
        contentService.reupload(uuid, file);
    }

    @PostMapping("upload/album-art")
    public void uploadAlbumArt(@RequestParam("file") MultipartFile file) throws IOException {
        contentService.saveAlbumArt(file);
    }

    @PostMapping("transfer")
    public void transfer(@RequestParam String uuid) {
        contentService.transfer(uuid);
    }

    @CacheEvict(value = "content", allEntries = true)
    @PutMapping("{uuid}")
    public ContentView update(@PathVariable String uuid, @RequestBody ContentView contentView) {
        Content content = contentService.update(uuid, contentView.getTitle(), contentView.getDescription());
        return mapper.contentToContentView(content);
    }

    @PutMapping("play/increment")
    public void incrementPlayCount(@RequestParam String uuid) {
        contentService.incrementPlayCount(uuid);
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
    public void delete(@PathVariable String uuid) throws IOException {
        contentService.delete(uuid);
    }
}
