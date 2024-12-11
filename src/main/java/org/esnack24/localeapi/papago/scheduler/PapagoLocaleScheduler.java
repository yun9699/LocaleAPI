package org.esnack24.localeapi.papago.scheduler;

import lombok.extern.log4j.Log4j2;
import org.esnack24.localeapi.faq.service.FAQLocaleService;
import org.esnack24.localeapi.product.service.ProductLocaleService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Log4j2
public class PapagoLocaleScheduler {

    private final ProductLocaleService productLocaleService;
    private final FAQLocaleService faqLocaleService;

    // 생성자 추가
    public PapagoLocaleScheduler(
            ProductLocaleService productLocaleService,
            FAQLocaleService faqLocaleService
    ) {
        this.productLocaleService = productLocaleService;
        this.faqLocaleService = faqLocaleService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void performScheduledTranslation() {
        try {
            productLocaleService.updateLocaleColumns();
            faqLocaleService.updateLocaleColumns();
        } catch (Exception e) {
            log.error("파파고 번역 스케줄러 실행 중 오류 발생", e);
        }
    }
}