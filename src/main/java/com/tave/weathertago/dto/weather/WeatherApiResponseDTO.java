package com.tave.weathertago.dto.weather;

import lombok.Getter;

import java.util.List;

@Getter
public class WeatherApiResponseDTO {
    private Response response;

    @Getter
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    public static class Body {
        private String dataType;
        private Items items;
        private int totalCount;
    }

    @Getter
    public static class Items {
        private List<Item> item;
    }

    @Getter
    public static class Item {
        private String category;
        private String fcstDate;
        private String fcstTime;
        private String fcstValue;
    }
}