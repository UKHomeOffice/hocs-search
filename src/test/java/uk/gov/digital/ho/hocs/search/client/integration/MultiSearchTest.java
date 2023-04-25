package uk.gov.digital.ho.hocs.search.client.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.digital.ho.hocs.search.api.CaseDataService;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.aws.listeners.integration.BaseAwsSqsIntegrationTest;
import uk.gov.digital.ho.hocs.search.client.ElasticSearchClient;
import uk.gov.digital.ho.hocs.search.helpers.CaseTypeUuidHelper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles({"localstack"})
class MultiSearchTest {

    @Autowired
    public ObjectMapper objectMapper;

    @SpyBean
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private CaseDataService caseDataService;

    @Test
    void searchMultipleIndexes() {
        Map<String, String> types = Map.of("a1", "MIN", "a2", "TRO");
        LocalDateTime beforeInsert = LocalDateTime.now();
        for (Map.Entry<String, String> caseType : types.entrySet()) {
            var caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid(caseType.getKey());
            caseDataService.createCase(caseUuid,
                new CreateCaseRequest(caseUuid, LocalDateTime.now(), caseType.getValue(), String.format("%s/0000000/1", caseType.getValue()), null, null, Map.of()));

            await().until(() -> elasticSearchClient.findById(caseType.getValue(), caseUuid) != null);
        }
        LocalDateTime afterInsert = LocalDateTime.now();

        await().pollDelay(Duration.ofSeconds(1)).until(() ->
                elasticSearchClient.search(types.values().stream().toList(),
                    generateTimestampQuery(beforeInsert, afterInsert)).size() == 2);
    }

    private BoolQueryBuilder generateTimestampQuery(LocalDateTime beforeInsert, LocalDateTime afterInsert) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.must(QueryBuilders.rangeQuery("created").gte(beforeInsert));
        queryBuilder.must(QueryBuilders.rangeQuery("created").lte(afterInsert));
        return queryBuilder;
    }

}
