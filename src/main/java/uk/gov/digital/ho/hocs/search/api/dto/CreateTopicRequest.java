package uk.gov.digital.ho.hocs.search.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateTopicRequest {

    @JsonProperty("topicUuid")
    private UUID uuid;

    @JsonProperty("topicName")
    private String topicName;
}
