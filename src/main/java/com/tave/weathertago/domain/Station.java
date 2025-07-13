package com.tave.weathertago.domain;

import com.tave.weathertago.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "station",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "line", "direction"})
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String line;

    private String stationCode;

    private String direction;

    private Double latitude;

    private Double longitude;

    private Integer nx;

    private Integer ny;

    private String phoneNumber;

    private String Address;

    // 위치 좌표를 한 번에 갱신하는 메서드
    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateContact(String phone, String roadAddress) {
        this.phoneNumber = phone;
        this.Address = roadAddress;
    }
}
