package org.esnack24.localeapi.papago.dto;

import lombok.Data;

@Data
public class PapagoResponseDTO {
    private Message message;

    @Data
    public static class Message {
        private Result result;

        @Data
        public static class Result {
            private String srcLangType;
            private String tarLangType;
            private String translatedText;
        }
    }
}