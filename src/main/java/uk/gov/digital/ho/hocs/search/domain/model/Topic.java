package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.search.api.dto.CreateTopicRequest;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Topic {

    private UUID uuid;

    private String text;

    public static Topic from(CreateTopicRequest topicRequest) {
        return new Topic(topicRequest.getUuid(), topicRequest.getTopicName());
    }
}
