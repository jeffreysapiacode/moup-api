package io.moup.api.model.whisper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Word {
    private String word;
    private Double start;
    private Double end;
}
