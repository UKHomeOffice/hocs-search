package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.search.domain.exceptions.ApplicationExceptions;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.hocs.search.application.LogEvent.*;

@Slf4j
@Component
public class ElasticSearchClient {

    ObjectMapper objectMapper;

    RestHighLevelClient client;

    @Autowired
    public ElasticSearchClient(ObjectMapper objectMapper, RestHighLevelClient client) {
        this.objectMapper = objectMapper;
        this.client = client;
    }

    public void save(CaseData caseData) {

        Map<String, Object> documentMapper = objectMapper.convertValue(caseData, Map.class);

        IndexRequest indexRequest = new IndexRequest("case", "caseData", caseData.getCaseUUID().toString()).source(documentMapper);

        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.ResourceServerException(String.format("Unable to find Case: %s", caseData.getCaseUUID()), CASE_SAVE_FAILED);
        }
    }

    public CaseData findById(UUID uuid){

        GetRequest getRequest = new GetRequest("case", "caseData", uuid.toString());

        GetResponse getResponse = null;
        try {
            getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.EntityNotFoundException(String.format("Unable to find Case: %s", uuid), CASE_NOT_FOUND);
        }
        Map<String, Object> resultMap = getResponse.getSource();

        if(resultMap == null) {
            return new CaseData(uuid);
        } else {
            return objectMapper.convertValue(resultMap, CaseData.class);
        }
    }

    public void update(CaseData caseData) {

        CaseData resultDocument = findById(caseData.getCaseUUID());

        UpdateRequest updateRequest = new UpdateRequest("case", "caseData", resultDocument.getCaseUUID().toString());

        Map<String, Object> documentMapper = objectMapper.convertValue(caseData, Map.class);

        updateRequest.doc(documentMapper);

        try {
            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ApplicationExceptions.ResourceServerException(String.format("Unable to update Case: %s", caseData.getCaseUUID()), CASE_UPDATE_FAILED);
        }
    }

    public Set<UUID> search(BoolQueryBuilder query) {

        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.warn("Search failed, returning empty set");
            return new HashSet<>();
        }

        return getSearchResult(searchResponse);

    }

    private Set<UUID> getSearchResult(SearchResponse response) {
        if (response != null) {
            SearchHit[] searchHit = response.getHits().getHits();

            Set<CaseData> cases = new HashSet<>();

            if (searchHit.length > 0) {
                Arrays.stream(searchHit).forEach(hit -> cases.add(objectMapper.convertValue(hit.getSourceAsMap(), CaseData.class)));
                return cases.stream().map(CaseData::getCaseUUID).collect(Collectors.toSet());
            } else {
                return new HashSet<>();
            }
        } else {
            return new HashSet<>();
        }
    }
}