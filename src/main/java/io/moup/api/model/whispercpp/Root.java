package io.moup.api.model.whispercpp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Root {
    List<Transcription> transcription;
}
