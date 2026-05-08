package io.moup.api.repository;

import io.moup.api.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, String> {
    List<Word> findByContentUuidAndStartGreaterThanEqualAndStartLessThanOrderByStartAsc(String contentUuid, Double start, Double end);
    List<Word> findByContentUuid(String contentUuid);
    void deleteByContentUuid(String contentUuid);
}
