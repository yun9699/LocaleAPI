package org.esnack24.localeapi.papago.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.esnack24.localeapi.papago.dto.PapagoRequestDTO;
import org.esnack24.localeapi.papago.service.PapagoLocaleService;
import org.esnack24.localeapi.papago.util.PapagoLocaleRenameUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/locale")
@Log4j2
@RequiredArgsConstructor
public class PapagoLocaleController {

    private final PapagoLocaleService papagoLocaleService;

    @PostMapping("/{targetLang}")
    public ResponseEntity<Map<String, Object>> locale(@PathVariable String targetLang, @Valid @RequestBody PapagoRequestDTO request
    ) {
        // target 언어 설정
        String target = PapagoLocaleRenameUtil.RenameLocaleCode(targetLang);

        // request의 target 값을 URL의 targetLang 기반으로 설정
        request.setTarget(target);

        log.info("번역 요청: source = {}, target = {}, text = {}",
                request.getSource(), target, request.getText());

        String localeText = papagoLocaleService.translateText(
                request.getSource(),
                target,
                request.getText()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        response.put("localeText", localeText);

        return ResponseEntity.ok(response);
    }
}