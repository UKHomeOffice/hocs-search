package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Document(indexName = "case", type = "caseData")
public class CaseData {

    @Id
    private UUID caseUUID;

    private LocalDateTime created;

    private String type;

    private String reference;

    private UUID primaryTopic;

    private UUID primaryCorrespondent;

    private LocalDate caseDeadline;

    private LocalDate dateReceived;

    private Boolean deleted = false;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Correspondent> currentCorrespondents = new HashSet<>();

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Correspondent> allCorrespondents = new HashSet<>();

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Topic> currentTopics = new HashSet<>();

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Topic> allTopics = new HashSet<>();

    public CaseData(UUID uuid) {
        this.caseUUID = uuid;
    }

    public void create(CreateCaseRequest createCaseRequest) {
        this.created = createCaseRequest.getCreated();
        this.type = createCaseRequest.getType();
        this.reference = createCaseRequest.getReference();
        this.caseDeadline = createCaseRequest.getCaseDeadline();
        this.dateReceived = createCaseRequest.getDateReceived();
    }

    public void update(UpdateCaseRequest updateCaseRequest) {
        this.created = updateCaseRequest.getCreated();
        this.type = updateCaseRequest.getType();
        this.reference = updateCaseRequest.getReference();
        this.primaryTopic = updateCaseRequest.getPrimaryTopic();
        this.primaryCorrespondent = updateCaseRequest.getPrimaryCorrespondent();
        this.caseDeadline = updateCaseRequest.getCaseDeadline();
        this.dateReceived = updateCaseRequest.getDateReceived();
    }

    public void delete() {
        this.deleted = true;
    }

    public void addCorrespondent(CreateCorrespondentRequest createCorrespondentRequest) {
        Correspondent correspondent = Correspondent.from(createCorrespondentRequest);
        this.currentCorrespondents.add(correspondent);
        this.allCorrespondents.add(correspondent);
    }

    public void removeCorrespondent(UUID correspondentUUID) {
        this.currentCorrespondents.removeIf(c -> c.getUuid().equals(correspondentUUID));
    }

    public void addTopic(Topic topic) {
        this.currentTopics.add(topic);
        this.allTopics.add(topic);
    }

    public void removeTopic(UUID topicUUID) {
        this.currentTopics.removeIf(c -> c.getUuid().equals(topicUUID));
    }

}
