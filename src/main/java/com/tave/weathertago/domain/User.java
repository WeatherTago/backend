package com.tave.weathertago.domain;

import com.tave.weathertago.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String kakaoId;

    @Column(nullable = false, length = 255)
    private String nickname;

    @Column(nullable = false, length = 255)
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Favorite favorite;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Alarm> alarms = new ArrayList<>();
}
