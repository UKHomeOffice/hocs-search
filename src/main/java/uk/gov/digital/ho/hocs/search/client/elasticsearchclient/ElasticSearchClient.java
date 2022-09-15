package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import uk.gov.digital.ho.hocs.search.domain.exceptions.ApplicationExceptions;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.hocs.search.application.LogEvent.CASE_NOT_FOUND;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.CASE_SAVE_FAILED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.CASE_UPDATE_FAILED;

@Slf4j
public class ElasticSearchClient {

    private final ObjectMapper objectMapper;

    private final RestHighLevelClient client;

    private final String index;

    private static final String COMPLETED = "completed";

    public ElasticSearchClient(ObjectMapper objectMapper, RestHighLevelClient client, String prefix) {
        this.objectMapper = objectMapper;
        this.client = client;
        this.index = String.format("%s-%s", prefix, "case");
        log.info("Using index {}", index);
    }

    public CaseData findById(UUID uuid) {
        GetRequest getRequest = new GetRequest(index, uuid.toString());

        GetResponse getResponse = null;
        try {
            getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.EntityNotFoundException(
                String.format("Unable to find Case: %s. %s", uuid, e), CASE_NOT_FOUND);
        }
        Map<String, Object> resultMap = getResponse.getSource();

        if (resultMap == null) {
            log.debug("Not found case {}, creating...", uuid);
            return new CaseData(uuid);
        } else {
            log.debug("Found case {}", uuid);
            return objectMapper.convertValue(resultMap, CaseData.class);
        }
    }

    public void save(CaseData caseData) {
        Map<String, Object> documentMapper = removeRedundantMappings(objectMapper.convertValue(caseData, Map.class));

        IndexRequest indexRequest = new IndexRequest(index).id(caseData.getCaseUUID().toString()).source(
            documentMapper);

        try {
            client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.ResourceServerException(
                String.format("Unable to find Case: %s. %s", caseData.getCaseUUID(), e), CASE_SAVE_FAILED);
        }
    }

    public void update(CaseData caseData) {
        CaseData resultDocument = findById(caseData.getCaseUUID());

        UpdateRequest updateRequest = new UpdateRequest(index, resultDocument.getCaseUUID().toString());

        Map<String, Object> documentMapper = removeRedundantMappings(objectMapper.convertValue(caseData, Map.class));

        updateRequest.doc(documentMapper);

        try {
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.ResourceServerException(
                String.format("Unable to update Case: %s. %s", caseData.getCaseUUID(), e), CASE_UPDATE_FAILED);
        }
    }

    public void update(Set<String> keys, CaseData caseData) {
        UpdateRequest updateRequest = new UpdateRequest(index, caseData.getCaseUUID().toString());
        Map<String, Object> documentMapper = objectMapper.convertValue(caseData, Map.class);
        documentMapper.entrySet().removeIf(entry -> !keys.contains(entry.getKey()));
        updateRequest.doc(documentMapper);
        try {
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.ResourceServerException(
                String.format("Unable to update partial Case: %s. %s", caseData.getCaseUUID(), e), CASE_UPDATE_FAILED);
        }
    }

    public Set<UUID> search(BoolQueryBuilder query, int resultsLimit) {
        SearchRequest searchRequest = new SearchRequest(this.index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchSourceBuilder.size(resultsLimit);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.warn("Search failed, returning empty set. {}", e.toString());
            return new HashSet<>();
        }

        return getSearchResult(searchResponse);
    }

    private Set<UUID> getSearchResult(SearchResponse response) {
        if (response != null) {
            SearchHit[] searchHit = response.getHits().getHits();

            Set<CaseData> cases = new HashSet<>();

            if (searchHit.length > 0) {
                Arrays.stream(searchHit).forEach(
                    hit -> cases.add(objectMapper.convertValue(hit.getSourceAsMap(), CaseData.class)));
                return cases.stream().map(CaseData::getCaseUUID).collect(Collectors.toSet());
            } else {
                return new HashSet<>();
            }
        } else {
            return new HashSet<>();
        }
    }

    private Map<String, Object> removeRedundantMappings(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>(map);
        result.values().removeAll(Arrays.asList("", null));

        if (result.containsKey(COMPLETED) && !((boolean) result.get(COMPLETED))) {
            result.remove(COMPLETED);
        }

        return result;
    }

}
