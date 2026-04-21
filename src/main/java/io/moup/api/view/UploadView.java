package io.moup.api.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class UploadView {
    private Instant uploadedOn;
    private ContentView content;
}
