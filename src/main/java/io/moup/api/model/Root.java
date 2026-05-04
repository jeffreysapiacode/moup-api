package io.moup.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Root {
    private String text;
    private String language;
    private List<Segment> segments;
}

