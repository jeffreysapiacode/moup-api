package io.moup.api.repository;

import io.moup.api.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoDictateRepository extends JpaRepository<Word, String> {
}
