package uk.gov.digital.ho.hocs.search.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SearchRequest {

    @JsonProperty("caseTypes")
    private List<String> caseTypes;

    @JsonProperty("dateReceived")
    private DateRangeDto dateReceived;

    @JsonProperty("correspondentName")
    private String correspondentName;

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("data")
    private Map<String, String> data;

    @JsonProperty("activeOnly")
    private Boolean ActiveOnly;

}
