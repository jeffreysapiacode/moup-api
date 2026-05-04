package io.moup.api.repository;

import io.moup.api.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word, String> {
    Page<Word> findByContentUuidOrderByStartAsc(String contentUuid, Pageable pageable);
}
