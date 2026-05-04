package io.moup.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Word {
    @Id
    @JsonIgnore
    @UuidGenerator
    @GeneratedValue
    private String uuid;
    @JsonIgnore
    private String contentUuid;
    private String word;
    private Double start;
    private Double end;
}
