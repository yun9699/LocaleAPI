package org.esnack24.localeapi.papago.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.esnack24.localeapi.papago.dto.PapagoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
@RequiredArgsConstructor
public class PapagoLocaleService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private final String PAPAGO_API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";

    public String translateText(String source, String target, String text) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.add("X-NCP-APIGW-API-KEY", clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("source", source);
        body.add("target", target);
        body.add("text", text);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<PapagoResponseDTO> response = restTemplate.exchange(
                    PAPAGO_API_URL,
                    HttpMethod.POST,
                    request,
                    PapagoResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getMessage().getResult().getTranslatedText();
            } else {
                throw new RuntimeException("파파고 API 호출 실패");
            }
        } catch (Exception e) {
            log.error("파파고 API 호출 에러: {}", e.getMessage());
            throw new RuntimeException("파파고 API 호출 에러", e);
        }
    }
}