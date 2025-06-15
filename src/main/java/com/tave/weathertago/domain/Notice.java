package com.tave.weathertago.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="notice")
@Getter @Setter
public class Notice {

    @Id @GeneratedValue
    @Column(name="notice_id")
    private Long id;

    private String title;

    private String content;
}
