package uk.gov.digital.ho.hocs.search.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DeleteTopicRequest {

    @JsonProperty("topicUuid")
    private UUID uuid;

    @JsonProperty("topicName")
    private String topicName;
}
