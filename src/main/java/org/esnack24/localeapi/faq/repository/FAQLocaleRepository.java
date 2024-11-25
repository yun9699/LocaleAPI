package org.esnack24.localeapi.faq.repository;

import org.esnack24.localeapi.faq.entity.FAQLocaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FAQLocaleRepository extends JpaRepository<FAQLocaleEntity, Long> {
    @Query("SELECT f FROM FAQLocaleEntity f WHERE f.fdelete = false AND (f.ftitle_en IS NULL OR f.ftitle_ja IS NULL OR f.ftitle_zh IS NULL)")
    List<FAQLocaleEntity> findFaqNeedLocale();
}