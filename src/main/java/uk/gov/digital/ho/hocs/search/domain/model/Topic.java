package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import uk.gov.digital.ho.hocs.search.client.infoclient.InfoTopic;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document(indexName = "case", type = "caseData")
public class Topic {

    @Id
    @Getter
    private UUID uuid;

    @Getter
    private String text;


    public static Topic from(InfoTopic infoTopic) {
        return new Topic(infoTopic.getValue(), infoTopic.getLabel());
    }
}
