package io.moup.api.view;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AutoDictateView {
    private String word;
    private Double startTime;
    private Double endTime;

}
