package uk.gov.digital.ho.hocs.search.aws.listeners;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.digital.ho.hocs.search.api.CaseDataService;
import uk.gov.digital.ho.hocs.search.api.dto.*;
import uk.gov.digital.ho.hocs.search.application.aws.SearchListener;
import uk.gov.digital.ho.hocs.search.application.queue.IndexDataChangeRequest;
import uk.gov.digital.ho.hocs.search.application.queue.DataChangeType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles({"localstack", "consumer"})
public class SearchListenerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private CaseDataService caseDataService;

    private final LocalDate now = LocalDate.now();

    @Test
    public void callsSearchServiceWithValidCreateCaseMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        CreateCaseRequest createCaseRequest = (new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "CASE",
            "CASE/12345", null, now.plusDays(1), now.minusDays(1), new HashMap()));
        String data = objectMapper.writeValueAsString(createCaseRequest);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data, DataChangeType.CASE_CREATED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).createCase(eq(caseUUID), eq(createCaseRequest));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidUpdateCaseMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        UpdateCaseRequest updateCaseRequest = (new UpdateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "CASE",
            "CASE/12345", UUID.randomUUID(), UUID.randomUUID(), now.plusDays(1), now.minusDays(1),
            new HashMap(), null));
        String data = objectMapper.writeValueAsString(updateCaseRequest);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data, DataChangeType.CASE_UPDATED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).updateCase(eq(caseUUID), eq(updateCaseRequest));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidDeleteCaseMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        DeleteCaseRequest deleteCaseRequest = new DeleteCaseRequest(UUID.randomUUID(), Boolean.TRUE);

        String data = objectMapper.writeValueAsString(deleteCaseRequest);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data, DataChangeType.CASE_DELETED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).deleteCase(eq(caseUUID), eq(deleteCaseRequest));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidCompleteCaseMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();

        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, null,
            DataChangeType.CASE_COMPLETED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).completeCase(eq(caseUUID));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidCorrespondentCreatedMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        CorrespondentDetailsDto correspondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(),
            LocalDateTime.now(), "Type", "FullName", new AddressDto(), "Phone", "Email", "Reference", "ExternalKey");

        String data = objectMapper.writeValueAsString(correspondentDetailsDto);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data,
            DataChangeType.CORRESPONDENT_CREATED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).createCorrespondent(eq(caseUUID), eq(correspondentDetailsDto));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidCorrespondentUpdatedMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        CorrespondentDetailsDto correspondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(),
            LocalDateTime.now(), "Type", "FullName", new AddressDto(), "Phone", "Email", "Reference", "ExternalKey");

        String data = objectMapper.writeValueAsString(correspondentDetailsDto);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data,
            DataChangeType.CORRESPONDENT_UPDATED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).updateCorrespondent(eq(caseUUID), eq(correspondentDetailsDto));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidCorrespondentDeletedMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        CorrespondentDetailsDto correspondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(),
            LocalDateTime.now(), "Type", "FullName", new AddressDto(), "Phone", "Email", "Reference", "ExternalKey");

        String data = objectMapper.writeValueAsString(correspondentDetailsDto);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data,
            DataChangeType.CORRESPONDENT_DELETED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).deleteCorrespondent(eq(caseUUID), eq(correspondentDetailsDto));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidTopicCreatedMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        CreateTopicRequest createTopicRequest = new CreateTopicRequest(UUID.randomUUID(), "TopicName");

        String data = objectMapper.writeValueAsString(createTopicRequest);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data,
            DataChangeType.CASE_TOPIC_CREATED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).createTopic(eq(caseUUID), eq(createTopicRequest));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidTopicDeletedMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(UUID.randomUUID(), "TopicName");

        String data = objectMapper.writeValueAsString(deleteTopicRequest);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data,
            DataChangeType.CASE_TOPIC_DELETED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).deleteTopic(eq(caseUUID), eq(deleteTopicRequest));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidSomuCreateMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        SomuItemDto somuItemDto = new SomuItemDto(UUID.randomUUID(), UUID.randomUUID(), "{}");

        String data = objectMapper.writeValueAsString(somuItemDto);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data,
            DataChangeType.SOMU_ITEM_CREATED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).createSomuItem(eq(caseUUID), eq(somuItemDto));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidSomuUpdatedMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        SomuItemDto somuItemDto = new SomuItemDto(UUID.randomUUID(), UUID.randomUUID(), "{}");

        String data = objectMapper.writeValueAsString(somuItemDto);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data,
            DataChangeType.SOMU_ITEM_UPDATED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).updateSomuItem(eq(caseUUID), eq(somuItemDto));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsSearchServiceWithValidSomuDeletedMessage() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();
        SomuItemDto somuItemDto = new SomuItemDto(UUID.randomUUID(), UUID.randomUUID(), "{}");

        String data = objectMapper.writeValueAsString(somuItemDto);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data,
            DataChangeType.SOMU_ITEM_DELETED.value);
        String message = objectMapper.writeValueAsString(request);

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(message);

        verify(caseDataService).deleteSomuItem(eq(caseUUID), eq(somuItemDto));
        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsAuditServiceWithValidTypeButNotInterested() throws JsonProcessingException {
        String incorrectMessage = "{\"caseUUID\":\"11111111-1111-1111-1111-111111111111\", \"type\":\"ANY_OTHER_MESSAGE_TYPE\"}";

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(incorrectMessage);

        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsAuditServiceWithMissingType() throws JsonProcessingException {
        String incorrectMessage = "{\"caseUUID\":\"11111111-1111-1111-1111-111111111111\"}";

        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);
        searchListener.onDataChange(incorrectMessage);

        verifyNoMoreInteractions(caseDataService);
    }

    @Test
    public void callsAuditServiceWithNullData() {
        String incorrectMessage = "{\"caseUUID\":\"11111111-1111-1111-1111-111111111111\", \"type\":\"CASE_CREATED\"}";
        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);

        assertThrows(IllegalArgumentException.class, () -> searchListener.onDataChange(incorrectMessage));
    }

    @Test
    public void callsAuditServiceWithNullMessage() {
        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);

        assertThrows(IllegalArgumentException.class, () -> searchListener.onDataChange(null));
    }

    @Test
    public void callsAuditServiceWithInvalidPayload() {
        String incorrectMessage = "{test:1}";
        SearchListener searchListener = new SearchListener(objectMapper, caseDataService);

        assertThrows(JsonParseException.class, () -> searchListener.onDataChange(incorrectMessage));
    }

}
