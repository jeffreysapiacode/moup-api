package io.moup.api.util;

import org.apache.commons.lang3.StringUtils;

public class FileNameUtils {
    public static String formatFilename(String title) {
        String lowered = StringUtils.lowerCase(title);
        String formatted0 = StringUtils.replaceChars(lowered, ",", "");
        return StringUtils.replaceChars(formatted0, " ", "-");
    }
}
