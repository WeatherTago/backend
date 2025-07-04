CREATE TABLE favorite
(
    favorite_id BIGINT   NOT NULL,
    created_at  datetime NULL,
    updated_at  datetime NULL,
    user_id     BIGINT   NOT NULL,
    CONSTRAINT pk_favorite PRIMARY KEY (favorite_id)
);

CREATE TABLE favorite_station
(
    favorite_id BIGINT NOT NULL,
    station_id  BIGINT NOT NULL
);

ALTER TABLE favorite
    ADD CONSTRAINT uc_favorite_user UNIQUE (user_id);

ALTER TABLE favorite
    ADD CONSTRAINT FK_FAVORITE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE favorite_station
    ADD CONSTRAINT fk_favsta_on_favorite FOREIGN KEY (favorite_id) REFERENCES favorite (favorite_id);

ALTER TABLE favorite_station
    ADD CONSTRAINT fk_favsta_on_station FOREIGN KEY (station_id) REFERENCES station (id);
CREATE TABLE favorite
(
    favorite_id BIGINT   NOT NULL,
    created_at  datetime NULL,
    updated_at  datetime NULL,
    user_id     BIGINT   NOT NULL,
    CONSTRAINT pk_favorite PRIMARY KEY (favorite_id)
);

CREATE TABLE favorite_station
(
    favorite_id BIGINT NOT NULL,
    station_id  BIGINT NOT NULL
);

ALTER TABLE favorite
    ADD CONSTRAINT uc_favorite_user UNIQUE (user_id);

ALTER TABLE favorite
    ADD CONSTRAINT FK_FAVORITE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE favorite_station
    ADD CONSTRAINT fk_favsta_on_favorite FOREIGN KEY (favorite_id) REFERENCES favorite (favorite_id);

ALTER TABLE favorite_station
    ADD CONSTRAINT fk_favsta_on_station FOREIGN KEY (station_id) REFERENCES station (id);
CREATE TABLE favorite
(
    favorite_id BIGINT   NOT NULL,
    created_at  datetime NULL,
    updated_at  datetime NULL,
    user_id     BIGINT   NOT NULL,
    CONSTRAINT pk_favorite PRIMARY KEY (favorite_id)
);

CREATE TABLE favorite_station
(
    favorite_id BIGINT NOT NULL,
    station_id  BIGINT NOT NULL
);

ALTER TABLE favorite
    ADD CONSTRAINT uc_favorite_user UNIQUE (user_id);

ALTER TABLE favorite
    ADD CONSTRAINT FK_FAVORITE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE favorite_station
    ADD CONSTRAINT fk_favsta_on_favorite FOREIGN KEY (favorite_id) REFERENCES favorite (favorite_id);

ALTER TABLE favorite_station
    ADD CONSTRAINT fk_favsta_on_station FOREIGN KEY (station_id) REFERENCES station (id);
CREATE TABLE favorite
(
    favorite_id BIGINT   NOT NULL,
    created_at  datetime NULL,
    updated_at  datetime NULL,
    user_id     BIGINT   NOT NULL,
    CONSTRAINT pk_favorite PRIMARY KEY (favorite_id)
);

CREATE TABLE favorite_station
(
    favorite_id BIGINT NOT NULL,
    station_id  BIGINT NOT NULL
);

ALTER TABLE favorite
    ADD CONSTRAINT uc_favorite_user UNIQUE (user_id);

ALTER TABLE favorite
    ADD CONSTRAINT FK_FAVORITE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE favorite_station
    ADD CONSTRAINT fk_favsta_on_favorite FOREIGN KEY (favorite_id) REFERENCES favorite (favorite_id);

ALTER TABLE favorite_station
    ADD CONSTRAINT fk_favsta_on_station FOREIGN KEY (station_id) REFERENCES station (id);
CREATE TABLE favorite
(
    favorite_id BIGINT   NOT NULL,
    created_at  datetime NULL,
    updated_at  datetime NULL,
    user_id     BIGINT   NOT NULL,
    CONSTRAINT pk_favorite PRIMARY KEY (favorite_id)
);

CREATE TABLE favorite_station
(
    favorite_id BIGINT NOT NULL,
    station_id  BIGINT NOT NULL
);

ALTER TABLE favorite
    ADD CONSTRAINT uc_favorite_user UNIQUE (user_id);

ALTER TABLE favorite
    ADD CONSTRAINT FK_FAVORITE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE favorite_station
    ADD CONSTRAINT fk_favsta_on_favorite FOREIGN KEY (favorite_id) REFERENCES favorite (favorite_id);

ALTER TABLE favorite_station
    ADD CONSTRAINT fk_favsta_on_station FOREIGN KEY (station_id) REFERENCES station (id);