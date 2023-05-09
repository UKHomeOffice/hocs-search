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
import uk.gov.digital.ho.hocs.search.client.CaseQueryFactory;
import uk.gov.digital.ho.hocs.search.client.OpenSearchClient;
import uk.gov.digital.ho.hocs.search.helpers.CaseTypeUuidHelper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
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
    private OpenSearchClient openSearchClient;

    @Autowired
    private CaseDataService caseDataService;

    @Autowired
    private CaseQueryFactory caseQueryFactory;

    @Test
    void searchMultipleIndexes() {
        Map<String, String> types = Map.of("a1", "MIN", "a2", "TRO");
        LocalDateTime beforeInsert = LocalDateTime.now();
        for (Map.Entry<String, String> caseType : types.entrySet()) {
            var caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid(caseType.getKey());
            caseDataService.createCase(caseUuid,
                new CreateCaseRequest(caseUuid, LocalDateTime.now(), caseType.getValue(), String.format("%s/0000000/1", caseType.getValue()), null, null, null, Map.of()));

        }

        BoolQueryBuilder timestampQuery = generateTimestampQuery(beforeInsert);
        Map<String, BoolQueryBuilder> caseTypeQueries = Map.of("MIN", timestampQuery, "TRO", timestampQuery);

        // search for all cases
        await().pollDelay(Duration.ofSeconds(1)).until(() -> openSearchClient.search(caseTypeQueries).size() == 2);
    }

    @Test
    void searchMigratedReferences() {
        Map<String, String> types = Map.of("a1", "MIN", "c5", "COMP", "a4", "POGR");
        LocalDateTime beforeInsert = LocalDateTime.now();
        for (Map.Entry<String, String> caseType : types.entrySet()) {
            var caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid(caseType.getKey());
            caseDataService.createCase(caseUuid,
                new CreateCaseRequest(caseUuid, LocalDateTime.now(), caseType.getValue(), String.format("%s/0000000/1",
                    caseType.getValue()),
                    "TEST_MIG_REF", null, null, Map.of()));
        }

        Map<String, BoolQueryBuilder> caseTypeQueries = new HashMap<>();
        for (String caseType : types.values()) {
            BoolQueryBuilder referenceQuery = generateMigratedReferenceQuery("TEST_MIG_REF", caseType, beforeInsert);
            caseTypeQueries.put(caseType, referenceQuery);
        }

        // search for all cases
        await().pollDelay(Duration.ofSeconds(1))
            .until(() -> openSearchClient.search(caseTypeQueries).size() == 2);
        for (Map<String, Object> searchResult : openSearchClient.search(caseTypeQueries)) {
            assertThat(searchResult.get("type")).isIn(List.of("COMP", "POGR"));
        }
    }

    private BoolQueryBuilder generateTimestampQuery(LocalDateTime beforeInsert) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.must(QueryBuilders.rangeQuery("created").gte(beforeInsert));
        return queryBuilder;
    }

    private BoolQueryBuilder generateMigratedReferenceQuery(String reference, String type,
                                                            LocalDateTime beforeInsert) {
        return caseQueryFactory.createCaseQuery()
            .reference(reference, type).build()
            .must(generateTimestampQuery(beforeInsert));
    }

}
