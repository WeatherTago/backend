package com.tave.weathertago.domain;

public enum Direction {
    UP(0, "상행"),
    DOWN(1, "하행"),

    // 2호선
    INNER(2, "내선"),
    OUTER(3, "외선");

    private final int code;
    private final String description;

    Direction(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // 코드로 enum 찾기 (필요시)
    public static Direction fromCode(int code) {
        for (Direction d : Direction.values()) {
            if (d.code == code) {
                return d;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
