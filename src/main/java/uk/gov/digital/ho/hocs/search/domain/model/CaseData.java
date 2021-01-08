package uk.gov.digital.ho.hocs.search.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    
    private Set<SomuItem> allSomuItems = new HashSet<>();

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

    public void addCorrespondent(CorrespondentDetailsDto correspondentDetailsDto) {
        Correspondent correspondent = Correspondent.from(correspondentDetailsDto);
        this.currentCorrespondents.add(correspondent);
        this.allCorrespondents.add(correspondent);
    }

    public void removeCorrespondent(UUID correspondentUUID) {
        this.currentCorrespondents.removeIf(c -> c.getUuid().equals(correspondentUUID));
    }

    public void updateCorrespondent(CorrespondentDetailsDto correspondentDetailsDto) {
        Correspondent updatedCorrespondent = Correspondent.from(correspondentDetailsDto);
        List<Correspondent> toRemove = new ArrayList<>();
        for(Correspondent correspondent : currentCorrespondents){
            if(correspondent.getUuid().equals(updatedCorrespondent.getUuid())){
                toRemove.add(correspondent);
            }
        }
        this.currentCorrespondents.removeAll(toRemove);
        this.currentCorrespondents.add(updatedCorrespondent);
        this.allCorrespondents.add(updatedCorrespondent);
    }


    public void addTopic(Topic topic) {
        this.currentTopics.add(topic);
        this.allTopics.add(topic);
    }

    public void removeTopic(UUID topicUUID) {
        this.currentTopics.removeIf(c -> c.getUuid().equals(topicUUID));
    }

    public void addSomuItem(SomuItem somuItem) {
        this.allSomuItems.add(somuItem);
    }
    
    public void updateSomuItem(SomuItem somuItem) {
        this.allSomuItems.removeIf(x -> x.getUuid().equals(somuItem.getUuid()));
        this.allSomuItems.add(somuItem);
    }

    public void removeSomuItem(UUID somuItemUuid) {
        this.allSomuItems.removeIf(c -> c.getUuid().equals(somuItemUuid));
    }

}
