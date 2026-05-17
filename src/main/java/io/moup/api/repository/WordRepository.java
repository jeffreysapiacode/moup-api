package io.moup.api.repository;

import io.moup.api.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, String> {
    List<Word> findAllByContentUuidAndStartGreaterThanEqualAndStartLessThanOrderByStartAsc(String contentUuid, Double start, Double end);
    List<Word> findAllByContentUuidOrderByStartAsc(String contentUuid);
    List<Word> findAllByContentUuid(String contentUuid);
    void deleteAllByContentUuid(String contentUuid);
}
