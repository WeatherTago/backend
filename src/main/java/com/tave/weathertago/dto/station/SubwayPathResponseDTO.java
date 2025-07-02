package com.tave.weathertago.dto.station;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "ServiceResult")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubwayPathResponseDTO {

    @JacksonXmlProperty(localName = "msgHeader")
    private MsgHeader msgHeader;

    @JacksonXmlProperty(localName = "msgBody")
    private MsgBody msgBody;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MsgHeader {
        private String headerCd;
        private String headerMsg;
        private int itemCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MsgBody {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "itemList")
        private List<Item> itemList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String time;
        private String distance;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "pathList")
        private List<PathList> pathList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PathList {
        private String routeNm;  // 2호선 등
        private String fname;    // 출발역
        private String tname;    // 도착역
    }
}
