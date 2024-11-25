package org.esnack24.localeapi.faq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.esnack24.localeapi.papago.service.PapagoLocaleService;
import org.esnack24.localeapi.faq.entity.FAQLocaleEntity;
import org.esnack24.localeapi.faq.repository.FAQLocaleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FAQLocaleService implements ApplicationRunner {

    private final FAQLocaleRepository faqLocaleRepository;
    private final PapagoLocaleService papagoLocaleService;

    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY = 1000;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("FAQ 다국어 컬럼 업데이트 시작");
        updateLocaleColumns();
    }

    public void updateLocaleColumns() {
        List<FAQLocaleEntity> faqsToTranslate = faqLocaleRepository.findFaqNeedLocale();
        log.info("번역이 필요한 FAQ 수: {}", faqsToTranslate.size());

        for (FAQLocaleEntity faq : faqsToTranslate) {
            try {
                log.info("FAQ 번역 시작 - ID: {}, 제목: {}", faq.getFno(), faq.getFtitle());
                translateWithRetry(faq);
                faqLocaleRepository.save(faq);
                log.info("FAQ 번역 완료 - ID: {}", faq.getFno());
                Thread.sleep(100);
            } catch (Exception e) {
                log.error("FAQ 번역 실패 - ID: {}, 오류: {}", faq.getFno(), e.getMessage());
            }
        }

        log.info("FAQ 다국어 컬럼 업데이트 완료");
    }

    private void translateWithRetry(FAQLocaleEntity faq) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            try {
                updateFaqColumns(faq);
                return;
            } catch (Exception e) {
                retryCount++;
                log.error("번역 실패 (시도 {}/{}) - FAQ ID: {}, 오류: {}",
                        retryCount, MAX_RETRY, faq.getFno(), e.getMessage());

                if (retryCount == MAX_RETRY) {
                    throw new RuntimeException("최대 재시도 횟수 초과", e);
                }

                try {
                    Thread.sleep(RETRY_DELAY * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void updateFaqColumns(FAQLocaleEntity faq) {
        try {
            long startTime = System.currentTimeMillis();

            // 영어 번역
            if (faq.getFtitle_en() == null) {
                log.info("영어 번역 시작 - FAQ ID: {}", faq.getFno());
                faq.setFtitle_en(papagoLocaleService.translateText("ko", "en", faq.getFtitle()));
                faq.setFcontent_en(papagoLocaleService.translateText("ko", "en", faq.getFcontent()));
                log.info("영어 번역 완료 - FAQ ID: {}", faq.getFno());
            }

            // 일본어 번역
            if (faq.getFtitle_ja() == null) {
                log.info("일본어 번역 시작 - FAQ ID: {}", faq.getFno());
                faq.setFtitle_ja(papagoLocaleService.translateText("ko", "ja", faq.getFtitle()));
                faq.setFcontent_ja(papagoLocaleService.translateText("ko", "ja", faq.getFcontent()));
                log.info("일본어 번역 완료 - FAQ ID: {}", faq.getFno());
            }

            // 중국어 번역
            if (faq.getFtitle_zh() == null) {
                log.info("중국어 번역 시작 - FAQ ID: {}", faq.getFno());
                faq.setFtitle_zh(papagoLocaleService.translateText("ko", "zh-CN", faq.getFtitle()));
                faq.setFcontent_zh(papagoLocaleService.translateText("ko", "zh-CN", faq.getFcontent()));
                log.info("중국어 번역 완료 - FAQ ID: {}", faq.getFno());
            }

            long endTime = System.currentTimeMillis();
            log.info("번역 소요 시간: {}ms - FAQ ID: {}", (endTime - startTime), faq.getFno());

        } catch (Exception e) {
            log.error("번역 중 오류 발생 - FAQ ID: {}, 오류: {}", faq.getFno(), e.getMessage());
            throw e;
        }
    }
}