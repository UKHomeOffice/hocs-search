package uk.gov.digital.ho.hocs.search.client.infoclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor()
@Getter
public class InfoTopic {

    @JsonProperty("label")
    private String label;

    @JsonProperty("value")
    private UUID value;

}
