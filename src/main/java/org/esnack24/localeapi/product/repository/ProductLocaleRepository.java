package org.esnack24.localeapi.product.repository;

import org.esnack24.localeapi.product.entity.ProductLocaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductLocaleRepository extends JpaRepository<ProductLocaleEntity, Long> {
    @Query("SELECT p FROM ProductLocaleEntity p WHERE p.pdelete = false AND " +
            "(p.ptitle_en IS NULL OR p.ptitle_ja IS NULL OR p.ptitle_zh IS NULL OR " +
            "p.pcontent_en IS NULL OR p.pcontent_ja IS NULL OR p.pcontent_zh IS NULL)")
    List<ProductLocaleEntity> findProductNeedLocale();
}