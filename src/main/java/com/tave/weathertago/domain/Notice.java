package com.tave.weathertago.domain;

import com.tave.weathertago.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name="notice")
@Getter
public class Notice extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notice_id;

    private String title;

    private String content;
}
