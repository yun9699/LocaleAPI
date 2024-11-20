package org.esnack24.localeapi.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.esnack24.localeapi.papago.service.PapagoLocaleService;
import org.esnack24.localeapi.product.entity.ProductLocaleEntity;
import org.esnack24.localeapi.product.repository.ProductLocaleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductLocaleService implements ApplicationRunner {

    private final ProductLocaleRepository productLocaleRepository;
    private final PapagoLocaleService papagoLocaleService;

    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY = 1000;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("다국어 컬럼 업데이트 시작");
        updateLocaleColumns();
    }

    public void updateLocaleColumns() {
        List<ProductLocaleEntity> productsToTranslate = productLocaleRepository.findProductNeedLocale();
        log.info("번역이 필요한 상품 수: {}", productsToTranslate.size());

        for (ProductLocaleEntity product : productsToTranslate) {
            try {
                log.info("상품 번역 시작 - ID: {}, 제목: {}", product.getPno(), product.getPtitle_ko());
                translateWithRetry(product);
                productLocaleRepository.save(product);
                log.info("상품 번역 완료 - ID: {}", product.getPno());
                Thread.sleep(100);
            } catch (Exception e) {
                log.error("상품 번역 실패 - ID: {}, 오류: {}", product.getPno(), e.getMessage());
            }
        }

        log.info("다국어 컬럼 업데이트 완료");
    }

    private void translateWithRetry(ProductLocaleEntity product) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            try {
                updateProductColumns(product);
                return;
            } catch (Exception e) {
                retryCount++;
                log.error("번역 실패 (시도 {}/{}) - 상품 ID: {}, 오류: {}",
                        retryCount, MAX_RETRY, product.getPno(), e.getMessage());

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

    private void updateProductColumns(ProductLocaleEntity product) {
        try {
            long startTime = System.currentTimeMillis();

            // 영어 번역
            if (product.getPtitle_en() == null) {
                log.info("영어 번역 시작 - 상품 ID: {}", product.getPno());
                product.setPtitle_en(papagoLocaleService.translateText("ko", "en", product.getPtitle_ko()));
//                product.setPcontent_en(papagoLocaleService.translateText("ko", "en", product.getPcontent_ko()));
//                product.setPcategory_en(papagoLocaleService.translateText("ko", "en", product.getPcategory_ko()));
                log.info("영어 번역 완료 - 상품 ID: {}", product.getPno());
            }

            // 일본어 번역
            if (product.getPtitle_ja() == null) {
                log.info("일본어 번역 시작 - 상품 ID: {}", product.getPno());
                product.setPtitle_ja(papagoLocaleService.translateText("ko", "ja", product.getPtitle_ko()));
//                product.setPcontent_ja(papagoLocaleService.translateText("ko", "ja", product.getPcontent_ko()));
//                product.setPcategory_ja(papagoLocaleService.translateText("ko", "ja", product.getPcategory_ko()));
                log.info("일본어 번역 완료 - 상품 ID: {}", product.getPno());
            }

            // 중국어 번역
            if (product.getPtitle_zh() == null) {
                log.info("중국어 번역 시작 - 상품 ID: {}", product.getPno());
                product.setPtitle_zh(papagoLocaleService.translateText("ko", "zh-CN", product.getPtitle_ko()));
//                product.setPcontent_zh(papagoLocaleService.translateText("ko", "zh-CN", product.getPcontent_ko()));
//                product.setPcategory_zh(papagoLocaleService.translateText("ko", "zh-CN", product.getPcategory_ko()));
                log.info("중국어 번역 완료 - 상품 ID: {}", product.getPno());
            }

            long endTime = System.currentTimeMillis();
            log.info("번역 소요 시간: {}ms - 상품 ID: {}", (endTime - startTime), product.getPno());

        } catch (Exception e) {
            log.error("번역 중 오류 발생 - 상품 ID: {}, 오류: {}", product.getPno(), e.getMessage());
            throw e;
        }
    }
}