package com.tave.weathertago.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="notice")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    @Id
    private Long noticeId;

    private String title;

    @Lob
    private String content;

    // 크롤링할 데이터가 등록되었던 시간을 저장하는 것이라 baseEntity 사용 X
    private String createAt;

    private String updateAt;

}
