package com.tave.weathertago.dto.station;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;


import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubwayPathResponseDTO {
    @JacksonXmlProperty(localName = "msgHeader")
    private MsgHeader msgHeader;

    @JacksonXmlProperty(localName = "msgBody")
    private MsgBody msgBody;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MsgHeader {
        private String headerMsg;
        private String headerCd;
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
        @JacksonXmlProperty(localName = "distance")
        private String distance;

        @JacksonXmlProperty(localName = "time")
        private String time;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "pathList")
        private List<Path> pathList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Path {
        @JacksonXmlProperty(localName = "fid")
        private String fid;

        @JacksonXmlProperty(localName = "fname")
        private String fname;

        @JacksonXmlProperty(localName = "tid")
        private String tid;

        @JacksonXmlProperty(localName = "tname")
        private String tname;

        @JacksonXmlProperty(localName = "routeNm")
        private String routeNm;
    }
}
