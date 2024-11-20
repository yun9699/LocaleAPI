package org.esnack24.localeapi.papago.util;

public class PapagoLocaleRenameUtil {

    public static String RenameLocaleCode(String targetLang) {
        targetLang = targetLang.toLowerCase();
        return switch (targetLang) {
            case "ja" -> "ja";
            case "en" -> "en";
            case "zh" -> "zh-CN";

            default -> throw new IllegalArgumentException("----wrong target lang tag input------");
        };
    }
}