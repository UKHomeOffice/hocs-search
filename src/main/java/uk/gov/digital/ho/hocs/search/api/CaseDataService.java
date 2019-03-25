package uk.gov.digital.ho.hocs.search.api;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.api.dto.*;
import uk.gov.digital.ho.hocs.search.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.search.client.infoclient.InfoTopic;
import uk.gov.digital.ho.hocs.search.client.elasticsearchclient.ElasticSearchClient;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.model.Topic;

import java.util.*;

@Service
@Slf4j
public class CaseDataService {

    private final ElasticSearchClient elasticSearchClient;

    private final InfoClient infoClient;

    private final int resultsLimit;

    @Autowired
    public CaseDataService(ElasticSearchClient elasticSearchClient, InfoClient infoClient, @Value("${elastic.results.limit}") int resultsLimit) {
        this.elasticSearchClient = elasticSearchClient;
        this.infoClient = infoClient;
        this.resultsLimit = resultsLimit;
    }

    public void createCase(UUID caseUUID, CreateCaseRequest createCaseRequest) {
        log.debug("Creating case {}", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.create(createCaseRequest);
        elasticSearchClient.save(caseData);
        log.info("Created case {}", caseUUID);
    }

    public void updateCase(UUID caseUUID, UpdateCaseRequest updateCaseRequest) {
        log.debug("Updating case {}", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.update(updateCaseRequest);
        elasticSearchClient.update(caseData);
        log.info("Updated case {}", caseUUID);
    }

    public void deleteCase(UUID caseUUID) {
        log.debug("Deleting case {}", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.delete();
        elasticSearchClient.update(caseData);
        log.info("Deleted case {}", caseUUID);
    }

    public void createCorrespondent(UUID caseUUID, CreateCorrespondentRequest createCorrespondentRequest) {
        log.debug("Adding correspondent {} to case {}", createCorrespondentRequest.getUuid(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.addCorrespondent(createCorrespondentRequest);
        elasticSearchClient.update(caseData);
        log.info("Added correspondent {} to case {}", createCorrespondentRequest.getUuid(), caseUUID);
    }

    public void deleteCorrespondent(UUID caseUUID, String correspondentUUID) {
        log.debug("Deleting correspondent {} from case {}", correspondentUUID, caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.removeCorrespondent(UUID.fromString(correspondentUUID));
        elasticSearchClient.update(caseData);
        log.info("Deleted correspondent {} from case {}", correspondentUUID, caseUUID);
    }

    public void createTopic(UUID caseUUID, String topicUUID) {
        log.debug("Adding topic {} to case {}", topicUUID, caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        InfoTopic infoTopic = infoClient.getTopic(UUID.fromString(topicUUID));
        caseData.addTopic(Topic.from(infoTopic));
        elasticSearchClient.update(caseData);
        log.info("Added topic {} to case {}", topicUUID, caseUUID);
    }

    public void deleteTopic(UUID caseUUID, String topicUUID) {
        log.debug("Deleting topic {} from case {}", topicUUID, caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.removeTopic(UUID.fromString(topicUUID));
        elasticSearchClient.update(caseData);
        log.info("Deleted topic {} from case {}", topicUUID, caseUUID);
    }

    Set<UUID> search(SearchRequest request){
        log.info("Searching for case {}", request.toString());
        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(QueryBuilders.boolQuery());
        hocsQueryBuilder.caseTypes(request.getCaseTypes());
        hocsQueryBuilder.dateRange(request.getDateReceived());
        hocsQueryBuilder.correspondent(request.getCorrespondentName());
        hocsQueryBuilder.topic(request.getTopic());
        hocsQueryBuilder.dataFields(request.getData());
        hocsQueryBuilder.activeOnlyFlag(request.getActiveOnly());

        Set<UUID> caseUUIDs;
        if(hocsQueryBuilder.hasClauses()) {
            caseUUIDs = elasticSearchClient.search(hocsQueryBuilder.build());
        } else {
            caseUUIDs = new HashSet<>(0);
        }

        log.info("Results {}", caseUUIDs.size());
        return caseUUIDs;
    }

    private CaseData getCaseData(UUID caseUUID){
        log.debug("Fetching Case {}", caseUUID);
        return elasticSearchClient.findById(caseUUID);
    }

}