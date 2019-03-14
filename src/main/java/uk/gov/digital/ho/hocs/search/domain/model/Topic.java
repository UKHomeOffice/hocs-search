package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import uk.gov.digital.ho.hocs.search.client.infoclient.InfoTopic;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Document(indexName = "topic", type = "topic")
public class Topic {

    @Id
    private UUID uuid;

    @Field(type = FieldType.Text)
    private String text;

    public static Topic from(InfoTopic infoTopic) {
        return new Topic(infoTopic.getValue(), infoTopic.getLabel());
    }
}
