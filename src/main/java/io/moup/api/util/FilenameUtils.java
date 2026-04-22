package io.moup.api.util;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FilenameUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.systemDefault());

    public static String formatFilename(String title) {
        String currentDate = DATE_FORMATTER.format(Instant.now());
        String lowered = StringUtils.lowerCase(title);
        String formatted0 = StringUtils.replaceChars(lowered, ",", "");
        String formatted1 = StringUtils.replaceChars(formatted0, " ", "-");
        return currentDate + "-" + formatted1;
    }
}
