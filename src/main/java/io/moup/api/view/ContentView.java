package io.moup.api.view;

import io.moup.api.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentView {
    private String uuid;
    private String title;
    private String description;
    private Instant uploadedOn;
    private Double duration;
    private String filename;
    private String mmx;
    private ContentType type;
    private Boolean active;
    private Long downloadCount;
    private Long playCount;
}
