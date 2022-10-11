package uk.gov.digital.ho.hocs.search.aws.listeners.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.application.queue.DataChangeType;
import uk.gov.digital.ho.hocs.search.application.queue.IndexDataChangeRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("localstack")
public class CreateSearchTest extends BaseAwsSqsIntegrationTest {

    @Autowired
    public ObjectMapper objectMapper;

    @Value("${aws.es.index-prefix}")
    String prefix;

    @Autowired
    public RestHighLevelClient client;

    @Test
    public void consumeMessageFromQueue() throws IOException {
        UUID caseUUID = UUID.randomUUID();

        CreateCaseRequest createCaseRequest = new CreateCaseRequest(caseUUID, LocalDateTime.now(), "MIN", "MIN12345",
            LocalDate.now(), LocalDate.now(), Collections.EMPTY_MAP);

        String data = objectMapper.writeValueAsString(createCaseRequest);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUUID, data, DataChangeType.CASE_CREATED.value);
        String message = objectMapper.writeValueAsString(request);

        var elasticRequest = new GetRequest(String.format("%s-%s", prefix, "case"), caseUUID.toString());
        assertThat(client.get(elasticRequest, RequestOptions.DEFAULT).getSource()).isNull();

        amazonSQSAsync.sendMessage(searchQueue, message);
        await().until(() -> getNumberOfMessagesOnQueue() == 0);

        client.get(elasticRequest, RequestOptions.DEFAULT);

        await().until(() -> client.get(elasticRequest, RequestOptions.DEFAULT).getSource() != null);

    }

}
