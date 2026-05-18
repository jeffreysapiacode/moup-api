package io.moup.api.repository;

import io.moup.api.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, String> {
    Optional<Content> findByFilename(String filename);
}
