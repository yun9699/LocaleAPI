package org.esnack24.localeapi.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductLocaleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    private String ptitle_ko;
    private String pcontent_ko;
    private String pcategory_ko;

    private String ptitle_en;
    private String pcontent_en;
    private String pcategory_en;

    private String ptitle_ja;
    private String pcontent_ja;
    private String pcategory_ja;

    private String ptitle_zh;
    private String pcontent_zh;
    private String pcategory_zh;

    // 삭제된 제품은 번역기능 작동 안하려고 넣음
    private Boolean pdelete;
}