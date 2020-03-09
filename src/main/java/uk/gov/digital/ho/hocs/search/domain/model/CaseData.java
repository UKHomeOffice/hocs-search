package uk.gov.digital.ho.hocs.search.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;

import java.beans.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Data
public class CaseData {

    private UUID caseUUID;

    private LocalDateTime created;

    private String type;

    private String reference;

    private UUID primaryTopic;

    private UUID primaryCorrespondent;

    private LocalDate caseDeadline;

    private LocalDate dateReceived;

    private Boolean deleted = false;

    private Boolean completed = false;

    private Set<Correspondent> currentCorrespondents = new HashSet<>();

    private Set<Correspondent> allCorrespondents = new HashSet<>();

    private Set<Topic> currentTopics = new HashSet<>();

    private Set<Topic> allTopics = new HashSet<>();

    private Map data;

    @JsonIgnore
    private transient boolean newCaseData = false;

    public CaseData(UUID uuid) {
        this.caseUUID = uuid;
        this.newCaseData = true;
    }

    public void create(CreateCaseRequest createCaseRequest) {
        this.created = createCaseRequest.getCreated();
        this.type = createCaseRequest.getType();
        this.reference = createCaseRequest.getReference();
        this.caseDeadline = createCaseRequest.getCaseDeadline();
        this.dateReceived = createCaseRequest.getDateReceived();
        this.data = createCaseRequest.getData();
    }

    public void update(UpdateCaseRequest updateCaseRequest) {
        this.created = updateCaseRequest.getCreated();
        this.type = updateCaseRequest.getType();
        this.reference = updateCaseRequest.getReference();
        this.primaryTopic = updateCaseRequest.getPrimaryTopic();
        this.primaryCorrespondent = updateCaseRequest.getPrimaryCorrespondent();
        this.caseDeadline = updateCaseRequest.getCaseDeadline();
        this.dateReceived = updateCaseRequest.getDateReceived();
        this.data = updateCaseRequest.getData();
    }

    public void delete(Boolean deleted) {
        this.deleted = deleted;
    }

    public void complete() {
        this.completed = true;
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
