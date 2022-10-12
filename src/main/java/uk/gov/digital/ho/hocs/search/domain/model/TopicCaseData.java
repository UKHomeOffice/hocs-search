package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Data
public class TopicCaseData {

    private Set<Topic> currentTopics = new HashSet<>();

    private Set<Topic> allTopics = new HashSet<>();

    public void addTopic(Topic topic) {
        this.currentTopics.add(topic);
        this.allTopics.add(topic);
    }

    public void removeTopic(UUID topicUUID) {
        this.currentTopics.removeIf(c -> c.getUuid().equals(topicUUID));
    }

}
