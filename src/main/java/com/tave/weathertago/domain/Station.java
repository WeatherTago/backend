package com.tave.weathertago.domain;

import com.tave.weathertago.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "station",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "line"})
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

    private String congestionLevel;

    private Integer congestionRate;

}
