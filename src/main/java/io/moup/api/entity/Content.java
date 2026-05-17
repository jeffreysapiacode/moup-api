package io.moup.api.entity;

import io.moup.api.enums.ContentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    @Id
    @UuidGenerator
    @GeneratedValue
    private String uuid;
    private String title;
    private String description;
    private Instant uploadedOn;
    private Double duration;
    private String filename;
    private String mmx;
    @Enumerated(EnumType.STRING)
    private ContentType type;

}
