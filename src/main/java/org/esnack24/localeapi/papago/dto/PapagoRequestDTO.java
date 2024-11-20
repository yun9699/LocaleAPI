package org.esnack24.localeapi.papago.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PapagoRequestDTO {
    @NotNull
    private String source;

    @NotNull
    private String target;

    @NotNull
    private String text;
}
