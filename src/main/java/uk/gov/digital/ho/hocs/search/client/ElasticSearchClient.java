package uk.gov.digital.ho.hocs.search.client;

import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.search.MultiSearchRequest;
import org.opensearch.action.search.MultiSearchResponse;
import org.opensearch.action.search.SearchRequest;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public List<Map<String, Object>> search(List<String> indexes, BoolQueryBuilder query) {
        if (indexes == null || indexes.isEmpty()) {
            log.warn("Search failed, returning empty set. No indexes provided.");
            return Collections.emptyList();
        }

        var searchRequest = new MultiSearchRequest();

        indexes.forEach(index -> {
            var searchSourceBuilder = new SearchSourceBuilder()
                .query(query)
                .size(resultsLimit);
            searchRequest.add(new SearchRequest(new String[] { getReadTypeAlias(index) }, searchSourceBuilder));
        });

        try {
            var searchResponse = client.msearch(searchRequest, RequestOptions.DEFAULT);
            return getSearchResult(searchResponse);
        } catch (IOException e) {
            log.warn("Search failed, returning empty set. {}", e.toString());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> getSearchResult(MultiSearchResponse response) {
        if (response == null || response.getResponses() == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> results = new ArrayList<>();
        for (var searchResponse : response.getResponses()) {
            if (searchResponse.isFailure()) {
                log.warn("Search failed, returning empty set. {}", searchResponse.getFailureMessage());
                return Collections.emptyList();
            }

            for (SearchHit hit : searchResponse.getResponse().getHits()) {
                results.add(hit.getSourceAsMap());

                if (results.size() == resultsLimit) {
                    return results;
                }
            }
        }

        return results;
    }

    private String getWriteTypeAlias(String type) {
        return String.format("%s-%s-write", aliasPrefix, type.toLowerCase());
    }

    private String getReadTypeAlias(String type) {
        return String.format("%s-%s-read", aliasPrefix, type.toLowerCase());
    }

}
