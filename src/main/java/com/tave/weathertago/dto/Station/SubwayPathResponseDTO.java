package com.tave.weathertago.dto.Station;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        private String headerMsg;
        private String headerCd;
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
        private String routeNm;
        private String fname;
        private String tname;
        private String fid;
        private String tid;
        private String distance;
        private String time;
    }
}
