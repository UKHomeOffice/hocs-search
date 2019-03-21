package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.search.client.infoclient.InfoTopic;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Topic {

    private UUID uuid;

    private String text;

    public static Topic from(InfoTopic infoTopic) {
        return new Topic(infoTopic.getValue(), infoTopic.getLabel());
    }
}
