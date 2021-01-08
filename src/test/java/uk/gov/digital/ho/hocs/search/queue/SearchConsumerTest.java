package uk.gov.digital.ho.hocs.search.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.search.api.CaseDataService;
import uk.gov.digital.ho.hocs.search.api.dto.*;
import uk.gov.digital.ho.hocs.search.application.queue.CreateAuditDto;
import uk.gov.digital.ho.hocs.search.application.queue.EventType;
import uk.gov.digital.ho.hocs.search.application.queue.SearchConsumer;

import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchConsumerTest extends CamelTestSupport {

    private String searchQueue = "direct:search-queue";
    private String dlq = "mock:search-queue-dlq";
    private ObjectMapper mapper;

    private UUID caseUUID;
    private String data;

    @Mock
    private CaseDataService mockDataService;

    @Before
    public void setUpTest() {

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        caseUUID = UUID.randomUUID();
        data = "{}";
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new SearchConsumer(mockDataService, searchQueue, dlq, 0, 0, 0);

    }

    @Test
    public void shouldCallCreateCase() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CASE_CREATED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).createCase(eq(caseUUID), any(CreateCaseRequest.class));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallUpdateCase() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CASE_UPDATED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).updateCase(eq(caseUUID), any(UpdateCaseRequest.class));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallDeleteCase() throws JsonProcessingException {

        DeleteCaseRequest deleteCaseRequest = new DeleteCaseRequest(caseUUID, true);
        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, mapper.writeValueAsString(deleteCaseRequest), EventType.CASE_DELETED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).deleteCase(eq(caseUUID), any(DeleteCaseRequest.class));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallCompleteCase() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CASE_COMPLETED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).completeCase(eq(caseUUID));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallCreateCorrespondent() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CORRESPONDENT_CREATED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).createCorrespondent(eq(caseUUID), any(CorrespondentDetailsDto.class));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallDeleteCorrespondent() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CORRESPONDENT_DELETED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).deleteCorrespondent(eq(caseUUID), any(CorrespondentDetailsDto.class));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallUpdateCorrespondent() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CORRESPONDENT_UPDATED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService).updateCorrespondent(eq(caseUUID), any(CorrespondentDetailsDto.class));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallCreateTopic() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CASE_TOPIC_CREATED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).createTopic(eq(caseUUID), any(CreateTopicRequest.class));
        verifyNoMoreInteractions(mockDataService);
    }
    
    @Test
    public void shouldCallDeleteTopic() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, "\"11111111-2222-2222-2222-333333333333\"", EventType.CASE_TOPIC_DELETED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).deleteTopic(eq(caseUUID), eq("11111111-2222-2222-2222-333333333333"));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallCreateSomuItem() throws JsonProcessingException {
        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.SOMU_ITEM_CREATED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).createSomuItem(eq(caseUUID), any(SomuItemDto.class));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallUpdateSomuItem() throws JsonProcessingException {
        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, "{ \"uuid\": \"11111111-2222-2222-2222-333333333333\" }", EventType.SOMU_ITEM_UPDATED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).updateSomuItem(eq(caseUUID), any(SomuItemDto.class));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallDeleteSomuItem() throws JsonProcessingException {
        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, "{ \"uuid\": \"11111111-2222-2222-2222-333333333333\" }", EventType.SOMU_ITEM_DELETED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).deleteSomuItem(eq(caseUUID), any(SomuItemDto.class));
        verifyNoMoreInteractions(mockDataService);
    }
    
    @Test
    public void shouldNotProcessMessgeWhenMarshellingFails() throws JsonProcessingException, InterruptedException {
        getMockEndpoint(dlq).setExpectedCount(1);
        String json = mapper.writeValueAsString("{invalid:invalid}");
        template.sendBody(searchQueue, json);
        verifyZeroInteractions(mockDataService);
        getMockEndpoint(dlq).assertIsSatisfied();
    }

    @Test
    public void shouldTransferToDLQOnFailure() throws JsonProcessingException, InterruptedException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CASE_CREATED.toString());

        doThrow(RuntimeException.class)
                .when(mockDataService).createCase(eq(caseUUID), any(CreateCaseRequest.class));
        getMockEndpoint(dlq).setExpectedCount(1);
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        getMockEndpoint(dlq).assertIsSatisfied();
    }

}
