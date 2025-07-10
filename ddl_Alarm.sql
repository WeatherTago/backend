CREATE TABLE alarm
(
    alarm_id       BIGINT       NOT NULL,
    created_at     datetime     NULL,
    updated_at     datetime     NULL,
    user_id        BIGINT       NOT NULL,
    push_token     VARCHAR(255) NULL,
    alarm_period   VARCHAR(255) NOT NULL,
    reference_time time         NOT NULL,
    station_id     BIGINT       NOT NULL,
    direction      VARCHAR(255) NOT NULL,
    alarm_day      VARCHAR(255) NOT NULL,
    alarm_time     time         NOT NULL,
    CONSTRAINT pk_alarm PRIMARY KEY (alarm_id)
);

ALTER TABLE alarm
    ADD CONSTRAINT FK_ALARM_ON_STATION FOREIGN KEY (station_id) REFERENCES station (id);

ALTER TABLE alarm
    ADD CONSTRAINT FK_ALARM_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);