package io.moup.api.repository;

import io.moup.api.entity.AutoDictate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoDictateRepository extends JpaRepository<AutoDictate, String> {
}
