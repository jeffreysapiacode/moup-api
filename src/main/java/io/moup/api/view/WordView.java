package io.moup.api.view;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WordView {
    private String word;
    private Double start;
    private Double end;
}
