package io.moup.api.model.whispercpp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Transcription {
    private Offset offsets;
    private String text;
}
