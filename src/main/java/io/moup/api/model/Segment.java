package io.moup.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Segment {
    private Double seek;
    private Double start;
    private Double end;
    private String text;
    private List<Word> words;
}
