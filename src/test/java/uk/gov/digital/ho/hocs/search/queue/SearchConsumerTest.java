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
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateTopicRequest;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;
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

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CASE_DELETED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).deleteCase(eq(caseUUID));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallCompleteCase() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CASE_COMPLETED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).deleteCase(eq(caseUUID));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallCreateCorrespondent() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, data, EventType.CORRESPONDENT_CREATED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).createCorrespondent(eq(caseUUID), any(CreateCorrespondentRequest.class));
        verifyNoMoreInteractions(mockDataService);
    }

    @Test
    public void shouldCallDeleteCorrespondent() throws JsonProcessingException {

        CreateAuditDto auditDto = new CreateAuditDto(caseUUID, "\"11111111-2222-2222-2222-333333333333\"", EventType.CORRESPONDENT_DELETED.toString());
        String json = mapper.writeValueAsString(auditDto);
        template.sendBody(searchQueue, json);
        verify(mockDataService, times(1)).deleteCorrespondent(eq(caseUUID), eq("11111111-2222-2222-2222-333333333333"));
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