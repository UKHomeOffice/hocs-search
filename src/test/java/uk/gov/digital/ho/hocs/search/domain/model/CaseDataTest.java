package uk.gov.digital.ho.hocs.search.domain.model;

import org.junit.Test;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateTopicRequest;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CaseDataTest {

    private UUID caseUUID = UUID.randomUUID();
    private CreateCaseRequest validCreateCaseRequest = new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    private UpdateCaseRequest validUpdateCaseRequest = new UpdateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    private CreateCorrespondentRequest validCreateCorrespondentRequest = new CreateCorrespondentRequest(UUID.randomUUID(), LocalDateTime.now(), "LAW", "FULLNAME", null, "0", "e", "REF");
    private Topic validTopic = Topic.from(new CreateTopicRequest(UUID.randomUUID(), "VALUE"));

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

        caseData.delete();

        assertThat(caseData.getDeleted()).isTrue();
    }

    @Test
    public void shouldAddCorrespondent() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getCurrentCorrespondents()).isEmpty();
        assertThat(caseData.getAllCorrespondents()).isEmpty();

        caseData.addCorrespondent(validCreateCorrespondentRequest);

        assertThat(caseData.getCurrentCorrespondents()).hasSize(1);
        assertThat(caseData.getAllCorrespondents()).hasSize(1);

    }

    @Test
    public void shouldRemoveCorrespondent() {
        CaseData caseData = new CaseData(caseUUID);

        assertThat(caseData.getCurrentCorrespondents()).isEmpty();
        assertThat(caseData.getAllCorrespondents()).isEmpty();

        caseData.addCorrespondent(validCreateCorrespondentRequest);

        caseData.removeCorrespondent(validCreateCorrespondentRequest.getUuid());

        assertThat(caseData.getCurrentCorrespondents()).hasSize(0);
        assertThat(caseData.getAllCorrespondents()).hasSize(1);
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
}