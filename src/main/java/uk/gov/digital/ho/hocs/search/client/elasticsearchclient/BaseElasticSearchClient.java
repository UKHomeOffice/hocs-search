package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.hocs.search.application.LogEvent.CASE_NOT_FOUND;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.CASE_SAVE_FAILED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.CASE_UPDATE_FAILED;

@Slf4j
public abstract class BaseElasticSearchClient implements ElasticSearchClient {

    protected final ObjectMapper objectMapper;

    protected final RestHighLevelClient client;

    protected final String aliasPrefix;

    protected BaseElasticSearchClient(ObjectMapper objectMapper, RestHighLevelClient client, String aliasPrefix) {
        this.objectMapper = objectMapper;
        this.client = client;
        this.aliasPrefix = aliasPrefix;
    }

    protected CaseData findById(String alias, UUID uuid) {
        GetRequest getRequest = new GetRequest(alias, uuid.toString());

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

    protected void save(String alias, CaseData caseData) {
        Map<String, Object> documentMapper = removeRedundantMappings(convertCaseDataToMap(caseData));

        IndexRequest indexRequest = new IndexRequest(alias).id(
            caseData.getCaseUUID().toString()).source(documentMapper);

        try {
            client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.ResourceServerException(
                String.format("Unable to find Case: %s. %s", caseData.getCaseUUID(), e), CASE_SAVE_FAILED);
        }
    }

    protected void update(String alias, CaseData caseData) {
        CaseData resultDocument = findById(caseData.getCaseUUID());

        UpdateRequest updateRequest = new UpdateRequest(alias,
            resultDocument.getCaseUUID().toString());

        Map<String, Object> documentMapper = removeRedundantMappings(convertCaseDataToMap(caseData));

        updateRequest.doc(documentMapper);

        try {
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.ResourceServerException(
                String.format("Unable to update Case: %s. %s", caseData.getCaseUUID(), e), CASE_UPDATE_FAILED);
        }
    }

    protected void update(String alias, Set<String> keys, CaseData caseData) {
        UpdateRequest updateRequest = new UpdateRequest(alias, caseData.getCaseUUID().toString());
        Map<String, Object> documentMapper = convertCaseDataToMap(caseData);
        documentMapper.entrySet().removeIf(entry -> !keys.contains(entry.getKey()));
        updateRequest.doc(documentMapper);
        try {
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.ResourceServerException(
                String.format("Unable to update partial Case: %s. %s", caseData.getCaseUUID(), e), CASE_UPDATE_FAILED);
        }
    }

    protected Set<UUID> search(String alias, BoolQueryBuilder query, int resultsLimit) {
        SearchRequest searchRequest = new SearchRequest(alias);
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
                    hit -> cases.add(convertMapToCaseData(hit.getSourceAsMap())));
                return cases.stream().map(CaseData::getCaseUUID).collect(Collectors.toSet());
            } else {
                return new HashSet<>();
            }
        } else {
            return new HashSet<>();
        }
    }

    private Map<String, Object> removeRedundantMappings(Map<String, Object> map) {
        String completed = "completed";
        Map<String, Object> result = new HashMap<>(map);

        result.values().removeAll(Arrays.asList("", null));

        if (result.containsKey(completed) && !((boolean) result.get(completed))) {
            result.remove(completed);
        }

        return result;
    }

    private Map<String, Object> convertCaseDataToMap(CaseData caseData) {
        return objectMapper.convertValue(caseData, new TypeReference<>() {});
    }

    private CaseData convertMapToCaseData(Map<String, Object> caseData) {
        return objectMapper.convertValue(caseData, new TypeReference<>() {});
    }

}
