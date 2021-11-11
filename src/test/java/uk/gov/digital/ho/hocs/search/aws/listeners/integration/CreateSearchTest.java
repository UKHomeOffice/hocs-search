package uk.gov.digital.ho.hocs.search.aws.listeners.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import uk.gov.digital.ho.hocs.search.api.CaseDataService;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.application.queue.IndexDataChangeRequest;
import uk.gov.digital.ho.hocs.search.application.queue.DataChangeType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Profile("localstack")
public class CreateSearchTest extends BaseAwsSqsIntegrationTest {

    @MockBean
    public CaseDataService caseDataService;

    @Autowired
    public ObjectMapper objectMapper;

    @Test
    public void consumeMessageFromQueue() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();

        CreateCaseRequest createCaseRequest = new CreateCaseRequest(
                UUID.randomUUID(),
                LocalDateTime.now(),
                "MIN",
                "MIN12345",
                LocalDate.now(),
                LocalDate.now(),
                Collections.EMPTY_MAP);

        String data = objectMapper.writeValueAsString(createCaseRequest);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data, DataChangeType.CASE_CREATED.value);
        String message = objectMapper.writeValueAsString(request);

        amazonSQSAsync.sendMessage(searchQueue, message);

        await().until(() -> getNumberOfMessagesOnQueue() == 0);
        await().untilAsserted(() -> verify(caseDataService).createCase(eq(caseUUID), any()));
        await().untilAsserted(() -> verifyNoMoreInteractions(caseDataService));

    }

    @Test
    public void consumeMessageFromQueue_exceptionMakesMessageNotVisible() throws JsonProcessingException {
        UUID caseUUID = UUID.randomUUID();

        CreateCaseRequest createCaseRequest = new CreateCaseRequest(
                UUID.randomUUID(),
                LocalDateTime.now(),
                "MIN",
                "MIN12345",
                LocalDate.now(),
                LocalDate.now(),
                Collections.EMPTY_MAP);

        String data = objectMapper.writeValueAsString(createCaseRequest);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data, DataChangeType.CASE_CREATED.value);
        String message = objectMapper.writeValueAsString(request);

        doThrow(new NullPointerException("TEST")).when(caseDataService).createCase(eq(caseUUID), any());

        amazonSQSAsync.sendMessage(searchQueue, message);

        await().until(() -> getNumberOfMessagesOnQueue() == 0);
        await().until(() -> getNumberOfMessagesNotVisibleOnQueue() == 1);

        await().untilAsserted(() -> verify(caseDataService).createCase(eq(caseUUID), any()));
        await().untilAsserted(() -> verifyNoMoreInteractions(caseDataService));

    }

}
