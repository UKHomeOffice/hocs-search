package uk.gov.digital.ho.hocs.search.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class SearchRequest {

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("caseType")
    private List<String> caseTypes;

    @JsonProperty("dateReceived")
    private DateRangeDto dateReceived;

    @JsonProperty("correspondentName")
    private String correspondentName;

    @JsonProperty("correspondentNameNotMember")
    private String correspondentNameNotMember;

    @JsonProperty("correspondentReference")
    private String correspondentReference;

    @JsonProperty("correspondentExternalKey")
    private String correspondentExternalKey;

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("poTeamUuid")
    private String privateOfficeTeamUuid;

    @JsonProperty("data")
    private Map<String, String> data;

    @JsonProperty("activeOnly")
    private Boolean activeOnly;

}
