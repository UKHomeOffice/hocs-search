package uk.gov.digital.ho.hocs.search.aws.listeners.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.opensearch.action.get.GetRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.application.queue.DataChangeType;
import uk.gov.digital.ho.hocs.search.application.queue.IndexDataChangeRequest;
import uk.gov.digital.ho.hocs.search.helpers.CaseTypeUuidHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles({"localstack", "consumer"})
class CreateSearchTest extends BaseAwsSqsIntegrationTest {

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public RestHighLevelClient client;

    @Value("${aws.es.index-prefix}")
    String prefix;

    @Test
    void consumeMessageFromQueue() throws IOException {
        var caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        CreateCaseRequest createCaseRequest = new CreateCaseRequest(caseUuid, LocalDateTime.now(), "min", "MIN12345",
            null, LocalDate.now(), LocalDate.now(), Collections.EMPTY_MAP);

        String data = objectMapper.writeValueAsString(createCaseRequest);
        IndexDataChangeRequest request = new IndexDataChangeRequest(caseUuid, data, DataChangeType.CASE_CREATED.value);
        String message = objectMapper.writeValueAsString(request);

        var elasticRequest = new GetRequest(String.format("%s-%s-%s", prefix, "min", "read"), caseUuid.toString());
        assertThat(client.get(elasticRequest, RequestOptions.DEFAULT).getSource()).isNull();

        amazonSQSAsync.sendMessage(searchQueue, message);
        await().until(() -> getNumberOfMessagesOnQueue() == 0);
        await().until(() -> client.get(elasticRequest, RequestOptions.DEFAULT).getSource() != null);
    }

}
