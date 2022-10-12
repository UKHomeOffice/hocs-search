package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
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

    private Map<String, Object> data;

    public CaseData(CreateCaseRequest createCaseRequest) {
        this.caseUUID = createCaseRequest.getUuid();
        this.created = createCaseRequest.getCreated();
        this.type = createCaseRequest.getType();
        this.reference = createCaseRequest.getReference();
        this.caseDeadline = createCaseRequest.getCaseDeadline();
        this.dateReceived = createCaseRequest.getDateReceived();
        this.data = createCaseRequest.getData();
    }

    public CaseData(UpdateCaseRequest updateCaseRequest) {
        this.created = updateCaseRequest.getCreated();
        this.type = updateCaseRequest.getType();
        this.reference = updateCaseRequest.getReference();
        this.primaryTopic = updateCaseRequest.getPrimaryTopic();
        this.primaryCorrespondent = updateCaseRequest.getPrimaryCorrespondent();
        this.caseDeadline = updateCaseRequest.getCaseDeadline();
        this.dateReceived = updateCaseRequest.getDateReceived();
        this.data = updateCaseRequest.getData();
    }


}
