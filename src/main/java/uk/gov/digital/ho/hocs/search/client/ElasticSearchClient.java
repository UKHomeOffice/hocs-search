package uk.gov.digital.ho.hocs.search.client;

import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.domain.exceptions.ApplicationExceptions;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static uk.gov.digital.ho.hocs.search.application.LogEvent.CASE_NOT_FOUND;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.CASE_UPDATE_FAILED;

@Service
@Slf4j
public class ElasticSearchClient {

    protected final RestHighLevelClient client;

    private final int resultsLimit;

    private final String aliasPrefix;

    protected ElasticSearchClient(RestHighLevelClient client,
                                  @Value("${aws.es.index-prefix}") String aliasPrefix,
                                  @Value("${aws.es.results-limit}") int resultsLimit) {
        this.client = client;
        this.aliasPrefix = aliasPrefix;
        this.resultsLimit = resultsLimit;
    }

    public Map<String, Object> findById(String indexType, UUID documentId) {
        var getRequest = new GetRequest(getReadTypeAlias(indexType), documentId.toString());

        try {
            var getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            var responseMap = getResponse.getSourceAsMap();
            return responseMap == null ? Collections.emptyMap() : responseMap;
        } catch (IOException e) {
            throw new ApplicationExceptions.EntityNotFoundException(
                String.format("Unable to find document: %s. %s", documentId, e), CASE_NOT_FOUND);
        }
    }

    public void update(String indexType, UUID documentId, Map<String, Object> data) {
        var updateRequest = new UpdateRequest(getWriteTypeAlias(indexType), documentId.toString())
            .docAsUpsert(true)
            .doc(data);

        try {
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.ResourceServerException(
                String.format("Unable to update document: %s. %s", documentId, e), CASE_UPDATE_FAILED);
        }
    }

    public List<Map<String, Object>> search(BoolQueryBuilder query) {
        var searchSourceBuilder = new SearchSourceBuilder()
            .query(query)
            .size(resultsLimit);
        var searchRequest = new SearchRequest(new String[] { getReadAlias() }, searchSourceBuilder);

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

    private String getWriteTypeAlias(String type) {
        return String.format("%s-%s-write", aliasPrefix, type.toLowerCase());
    }

    private String getReadAlias() {
        return String.format("%s-read", aliasPrefix);
    }

    private String getReadTypeAlias(String type) {
        return String.format("%s-%s-read", aliasPrefix, type.toLowerCase());
    }

}
