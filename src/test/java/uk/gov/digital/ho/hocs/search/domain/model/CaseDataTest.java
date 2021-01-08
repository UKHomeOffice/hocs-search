package uk.gov.digital.ho.hocs.search.domain.model;

import org.junit.Test;
import uk.gov.digital.ho.hocs.search.api.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CaseDataTest {

    private UUID caseUUID = UUID.randomUUID();
    private CreateCaseRequest validCreateCaseRequest = new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), new HashMap());
    private UpdateCaseRequest validUpdateCaseRequest = new UpdateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), new HashMap());
    private CorrespondentDetailsDto validCorrespondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(), LocalDateTime.now(), "LAW", "FULLNAME", null, "0", "e", "REF", "ExtKey");
    private Topic validTopic = Topic.from(new CreateTopicRequest(UUID.randomUUID(), "VALUE"));
    private final SomuItem validSomuItem = SomuItem.from(new SomuItemDto(UUID.randomUUID(), UUID.randomUUID(), "{}"));

    @Test
    public void shouldCreateCaseDataConstructor() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getCaseUUID()).isEqualTo(caseUUID);
        assertThat(caseData.getCreated()).isNull();
        assertThat(caseData.getType()).isNull();
        assertThat(caseData.getReference()).isNull();
        assertThat(caseData.getPrimaryTopic()).isNull();
        assertThat(caseData.getPrimaryCorrespondent()).isNull();
        assertThat(caseData.getCaseDeadline()).isNull();
        assertThat(caseData.getDateReceived()).isNull();
        assertThat(caseData.getDeleted()).isFalse();

        assertThat(caseData.getCurrentCorrespondents()).isEmpty();
        assertThat(caseData.getAllCorrespondents()).isEmpty();
        assertThat(caseData.getCurrentTopics()).isEmpty();
        assertThat(caseData.getAllTopics()).isEmpty();
    }

    @Test
    public void shouldCreateCaseData() {
        CaseData caseData = new CaseData(caseUUID);

        caseData.create(validCreateCaseRequest);

        assertThat(caseData.getCaseUUID()).isEqualTo(caseUUID);
        assertThat(caseData.getCreated()).isEqualTo(validCreateCaseRequest.getCreated());
        assertThat(caseData.getType()).isEqualTo(validCreateCaseRequest.getType());
        assertThat(caseData.getReference()).isEqualTo(validCreateCaseRequest.getReference());
        assertThat(caseData.getPrimaryTopic()).isNull();
        assertThat(caseData.getPrimaryCorrespondent()).isNull();
        assertThat(caseData.getCaseDeadline()).isEqualTo(validCreateCaseRequest.getCaseDeadline());
        assertThat(caseData.getDateReceived()).isEqualTo(validCreateCaseRequest.getDateReceived());
        assertThat(caseData.getDeleted()).isFalse();

        assertThat(caseData.getCurrentCorrespondents()).isEmpty();
        assertThat(caseData.getAllCorrespondents()).isEmpty();
        assertThat(caseData.getCurrentTopics()).isEmpty();
        assertThat(caseData.getAllTopics()).isEmpty();
    }

    @Test
    public void shouldUpdateCaseData() {
        CaseData caseData = new CaseData(caseUUID);

        caseData.update(validUpdateCaseRequest);

        assertThat(caseData.getCaseUUID()).isEqualTo(caseUUID);
        assertThat(caseData.getCreated()).isEqualTo(validUpdateCaseRequest.getCreated());
        assertThat(caseData.getType()).isEqualTo(validUpdateCaseRequest.getType());
        assertThat(caseData.getReference()).isEqualTo(validUpdateCaseRequest.getReference());
        assertThat(caseData.getPrimaryTopic()).isEqualTo(validUpdateCaseRequest.getPrimaryTopic());
        assertThat(caseData.getPrimaryCorrespondent()).isEqualTo(validUpdateCaseRequest.getPrimaryCorrespondent());
        assertThat(caseData.getCaseDeadline()).isEqualTo(validUpdateCaseRequest.getCaseDeadline());
        assertThat(caseData.getDateReceived()).isEqualTo(validUpdateCaseRequest.getDateReceived());
        assertThat(caseData.getDeleted()).isFalse();

        assertThat(caseData.getCurrentCorrespondents()).isEmpty();
        assertThat(caseData.getAllCorrespondents()).isEmpty();
        assertThat(caseData.getCurrentTopics()).isEmpty();
        assertThat(caseData.getAllTopics()).isEmpty();
    }

    @Test
    public void shouldDeleteCaseData() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getDeleted()).isFalse();

        caseData.delete(true);

        assertThat(caseData.getDeleted()).isTrue();
    }

    @Test
    public void shouldUndeleteCaseData() {
        CaseData caseData = new CaseData(caseUUID);
        caseData.setDeleted(true);

        assertThat(caseData.getDeleted()).isTrue();

        caseData.delete(false);

        assertThat(caseData.getDeleted()).isFalse();
    }

    @Test
    public void shouldCompleteCaseData() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getCompleted()).isFalse();

        caseData.complete();

        assertThat(caseData.getCompleted()).isTrue();
    }

    @Test
    public void shouldAddCorrespondent() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getCurrentCorrespondents()).isEmpty();
        assertThat(caseData.getAllCorrespondents()).isEmpty();

        caseData.addCorrespondent(validCorrespondentDetailsDto);

        assertThat(caseData.getCurrentCorrespondents()).hasSize(1);
        assertThat(caseData.getAllCorrespondents()).hasSize(1);

    }

    @Test
    public void shouldRemoveCorrespondent() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getCurrentCorrespondents()).isEmpty();
        assertThat(caseData.getAllCorrespondents()).isEmpty();

        caseData.addCorrespondent(validCorrespondentDetailsDto);

        caseData.removeCorrespondent(validCorrespondentDetailsDto.getUuid());

        assertThat(caseData.getCurrentCorrespondents()).hasSize(0);
        assertThat(caseData.getAllCorrespondents()).hasSize(1);
    }

    @Test
    public void shouldUpdateCorrespondent() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getCurrentCorrespondents()).isEmpty();
        assertThat(caseData.getAllCorrespondents()).isEmpty();

        caseData.addCorrespondent(validCorrespondentDetailsDto);

        CorrespondentDetailsDto updatedCorrespondentDetailsDto = new CorrespondentDetailsDto(validCorrespondentDetailsDto.getUuid(), LocalDateTime.now(), "LAW2", "FULLNAME2", null, "2", "e2", "REF2", "ExtKey2");


        caseData.updateCorrespondent(updatedCorrespondentDetailsDto);

        assertThat(caseData.getCurrentCorrespondents()).hasSize(1);
        assertThat(caseData.getAllCorrespondents()).hasSize(2);

        Correspondent activeCorrespondent = caseData.getCurrentCorrespondents().iterator().next();
        assertThat(activeCorrespondent.getUuid()).isEqualTo(validCorrespondentDetailsDto.getUuid());
        assertThat(activeCorrespondent.getType()).isEqualTo("LAW2");
        assertThat(activeCorrespondent.getFullname()).isEqualTo("FULLNAME2");
        assertThat(activeCorrespondent.getExternalKey()).isEqualTo("ExtKey2");
    }

    @Test
    public void shouldAddTopic() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getCurrentTopics()).isEmpty();
        assertThat(caseData.getAllTopics()).isEmpty();

        caseData.addTopic(validTopic);

        assertThat(caseData.getCurrentTopics()).hasSize(1);
        assertThat(caseData.getAllTopics()).hasSize(1);

    }

    @Test
    public void shouldRemoveTopic() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getCurrentTopics()).isEmpty();
        assertThat(caseData.getAllTopics()).isEmpty();

        caseData.addTopic(validTopic);

        caseData.removeTopic(validTopic.getUuid());

        assertThat(caseData.getCurrentTopics()).hasSize(0);
        assertThat(caseData.getAllTopics()).hasSize(1);
    }

    @Test
    public void shouldAddSomuItem() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getAllSomuItems()).isEmpty();

        caseData.addSomuItem(validSomuItem);

        assertThat(caseData.getAllSomuItems()).hasSize(1);
    }

    @Test
    public void shouldRemoveSomuItem() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getAllSomuItems()).isEmpty();

        caseData.addSomuItem(validSomuItem);

        assertThat(caseData.getAllSomuItems()).hasSize(1);

        caseData.removeSomuItem(validSomuItem.getUuid());

        assertThat(caseData.getAllSomuItems()).hasSize(0);
    }

    @Test
    public void shouldUpdateSomuItem() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getAllSomuItems()).isEmpty();

        caseData.addSomuItem(validSomuItem);

        assertThat(caseData.getAllSomuItems()).hasSize(1);

        SomuItem updatedSomuItem = SomuItem.from(new SomuItemDto(validSomuItem.getUuid(), validSomuItem.getSomuUuid(), "TEST"));
        
        caseData.updateSomuItem(updatedSomuItem);

        assertThat(caseData.getAllSomuItems()).hasSize(1);
        
        SomuItem caseDataSomuItem = (SomuItem)caseData.getAllSomuItems().toArray()[0];
        assertThat(caseDataSomuItem.getUuid()).isEqualTo(validSomuItem.getUuid());
        assertThat(caseDataSomuItem.getSomuUuid()).isEqualTo(validSomuItem.getSomuUuid());
        assertThat(caseDataSomuItem.getData()).isEqualTo("TEST");
    }
}
