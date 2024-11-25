package org.esnack24.localeapi.faq.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_faq")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQLocaleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fno;

    private String ftitle;
    private String fcontent;

    private String ftitle_en;
    private String fcontent_en;

    private String ftitle_ja;
    private String fcontent_ja;

    private String ftitle_zh;
    private String fcontent_zh;

    private Boolean fdelete;
}