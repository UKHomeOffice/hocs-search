package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import uk.gov.digital.ho.hocs.search.domain.exceptions.ApplicationExceptions;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static uk.gov.digital.ho.hocs.search.application.LogEvent.CASE_NOT_FOUND;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.CASE_UPDATE_FAILED;

@Slf4j
public abstract class BaseElasticSearchClient implements ElasticSearchClient {

    protected final RestHighLevelClient client;

    private final int resultsLimit;

    protected BaseElasticSearchClient(RestHighLevelClient client,
                                      int resultsLimit) {
        this.client = client;
        this.resultsLimit = resultsLimit;
    }

    protected Map<String, Object> findById(String alias, UUID caseUuid) {
        var getRequest = new GetRequest(alias, caseUuid.toString());

        try {
            var getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            var responseMap = getResponse.getSourceAsMap();
            return responseMap == null ? Collections.emptyMap() : responseMap;
        } catch (IOException e) {
            throw new ApplicationExceptions.EntityNotFoundException(
                String.format("Unable to find Case: %s. %s", caseUuid, e), CASE_NOT_FOUND);
        }
    }

    protected void update(String alias, UUID caseUuid, Map<String, Object> data) {
        var updateRequest = new UpdateRequest(alias, caseUuid.toString())
            .docAsUpsert(true)
            .doc(data);

        try {
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.ResourceServerException(
                String.format("Unable to update Case: %s. %s", caseUuid, e), CASE_UPDATE_FAILED);
        }
    }

    protected List<Map<String, Object>> search(String alias, BoolQueryBuilder query) {
        var searchSourceBuilder = new SearchSourceBuilder()
            .query(query)
            .size(resultsLimit);
        var searchRequest = new SearchRequest(new String[] { alias }, searchSourceBuilder);

        try {
            var searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return getSearchResult(searchResponse);
        } catch (IOException e) {
            log.warn("Search failed, returning empty set. {}", e.toString());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> getSearchResult(SearchResponse response) {
        if (response == null) {
            return Collections.emptyList();
        }

        var searchHit = response.getHits().getHits();
        return Stream.of(searchHit).map(SearchHit::getSourceAsMap).toList();
    }

}
