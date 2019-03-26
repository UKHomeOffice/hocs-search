package uk.gov.digital.ho.hocs.search.domain.model;

import org.junit.Test;
import uk.gov.digital.ho.hocs.search.api.dto.CreateTopicRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TopicTest {

    @Test
    public void shouldCreateTopic() {
        UUID uuid = UUID.randomUUID();
        String value = "VALUE";

        Topic topic = Topic.from(new CreateTopicRequest(uuid, value));

        assertThat(topic.getUuid()).isEqualTo(uuid);
        assertThat(topic.getText()).isEqualTo(value);
    }

}