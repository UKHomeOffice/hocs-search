package uk.gov.digital.ho.hocs.search.domain.repository.model;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TopicTest {

    @Test
    public void shouldCreateTopic(){
        UUID uuid = UUID.randomUUID();
        String value = "VALUE";

        Topic topic = new Topic(uuid, value);

        assertThat(topic.getUuid()).isEqualTo(uuid);
        assertThat(topic.getValue()).isEqualTo(value);
    }

}