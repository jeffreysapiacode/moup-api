package io.moup.api.service;

import io.moup.api.entity.Content;
import io.moup.api.repository.ContentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public List<Content> getList() {
        return contentRepository.findAll(Sort.by("uploadedOn").descending());
    }
}
