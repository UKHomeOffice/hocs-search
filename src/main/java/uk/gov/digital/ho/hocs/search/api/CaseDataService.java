package uk.gov.digital.ho.hocs.search.api;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.api.dto.*;
import uk.gov.digital.ho.hocs.search.client.elasticsearchclient.ElasticSearchClient;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.model.SomuItem;
import uk.gov.digital.ho.hocs.search.domain.model.Topic;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.*;

@Service
@Slf4j
public class CaseDataService {

    private final ElasticSearchClient elasticSearchClient;

    private final int resultsLimit;

    @Autowired
    public CaseDataService(ElasticSearchClient elasticSearchClient, @Value("${elastic.results.limit}") int resultsLimit) {
        this.elasticSearchClient = elasticSearchClient;
        this.resultsLimit = resultsLimit;
    }

    public void createCase(UUID caseUUID, CreateCaseRequest createCaseRequest) {
        log.debug("Creating case {}", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.create(createCaseRequest);
        if(caseData.isNewCaseData()){
            elasticSearchClient.save(caseData);
        }else{
            log.warn("Updating case {} as already exists in elastic search", caseUUID);
            elasticSearchClient.update(caseData);
        }
        log.info("Created case {}", caseUUID, value(EVENT, SEARCH_CASE_CREATED));

    }

    public void updateCase(UUID caseUUID, UpdateCaseRequest updateCaseRequest) {
        log.debug("Updating case {}", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.update(updateCaseRequest);
        if(caseData.isNewCaseData()){
            log.warn("Creating case {} as does not exists in elastic search", caseUUID);
            elasticSearchClient.save(caseData);
        }else{
            elasticSearchClient.update(caseData);
        }
        log.info("Updated case {}", caseUUID, value(EVENT, SEARCH_CASE_UPDATED));
    }

    public void deleteCase(UUID caseUUID, DeleteCaseRequest deleteCaseRequest) {
        log.debug("Deleting ({}) case {}", deleteCaseRequest.getDeleted(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.delete(deleteCaseRequest.getDeleted());
        elasticSearchClient.update(caseData);
        log.info("Deleted ({}) case {}", deleteCaseRequest.getDeleted(), caseUUID, value(EVENT, SEARCH_CASE_DELETED));
    }

    public void completeCase(UUID caseUUID) {
        log.debug("Complete case {}", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.complete();
        elasticSearchClient.update(caseData);
        log.info("Compeleted case {}", caseUUID, value(EVENT, SEARCH_CASE_DELETED));
    }

    public void createCorrespondent(UUID caseUUID, CorrespondentDetailsDto correspondentDetailsDto) {
        log.debug("Adding correspondent {} to case {}", correspondentDetailsDto.getUuid(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.addCorrespondent(correspondentDetailsDto);
        elasticSearchClient.update(caseData);
        log.info("Added correspondent {} to case {}", correspondentDetailsDto.getUuid(), caseUUID, value(EVENT, SEARCH_CORRESPONDENT_ADDED));
    }

    public void deleteCorrespondent(UUID caseUUID, CorrespondentDetailsDto correspondentDetailsDto) {
        log.debug("Deleting correspondent {} from case {}", correspondentDetailsDto.getUuid(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.removeCorrespondent(correspondentDetailsDto.getUuid());
        elasticSearchClient.update(caseData);
        log.info("Deleted correspondent {} from case {}", correspondentDetailsDto.getUuid(), caseUUID, value(EVENT, SEARCH_CORRESPONDENT_DELETED));
    }

    public void updateCorrespondent(UUID caseUUID, CorrespondentDetailsDto correspondentDetailsDto) {
        log.debug("Updating correspondent {} from case {}", correspondentDetailsDto.getUuid(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.updateCorrespondent(correspondentDetailsDto);
        elasticSearchClient.update(caseData);
        log.info("Updating correspondent {} for case {}", correspondentDetailsDto.getUuid(), caseUUID, value(EVENT, SEARCH_CORRESPONDENT_DELETED));
    }

    public void createTopic(UUID caseUUID, CreateTopicRequest createTopicRequest) {
        log.debug("Adding topic {} to case {}", createTopicRequest.getUuid(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.addTopic(Topic.from(createTopicRequest));
        elasticSearchClient.update(caseData);
        log.info("Added topic {} to case {}", createTopicRequest.getUuid(), caseUUID, value(EVENT, SEARCH_TOPIC_ADDED));
    }

    public void deleteTopic(UUID caseUUID, String topicUUID) {
        log.debug("Deleting topic {} from case {}", topicUUID, caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.removeTopic(UUID.fromString(topicUUID));
        elasticSearchClient.update(caseData);
        log.info("Deleted topic {} from case {}", topicUUID, caseUUID, value(EVENT, SEARCH_TOPIC_DELETED));
    }

    public void createSomuItem(UUID caseUUID, SomuItemDto somuItemDto) {
        log.debug("Adding somu item {} to case {}", somuItemDto.getUuid(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.addSomuItem(SomuItem.from(somuItemDto));
        elasticSearchClient.update(caseData);
        log.info("Added somu item {} to case {}. Event {}", somuItemDto.getUuid(), caseUUID, value(EVENT, SOMU_ITEM_CREATED));
    }

    public void deleteSomuItem(UUID caseUUID, SomuItemDto somuItem) {
        log.debug("Deleting somu item {} from case {}",somuItem.getUuid(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.removeSomuItem(somuItem.getUuid());
        elasticSearchClient.update(caseData);
        log.info("Deleted somu item {} from case {}. Event {}", somuItem.getUuid(), caseUUID, value(EVENT, SOMU_ITEM_DELETED));
    }

    public void updateSomuItem(UUID caseUUID, SomuItemDto somuItemDto) {
        log.debug("Updating somu item {} from case {}", somuItemDto.getUuid(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.updateSomuItem(SomuItem.from(somuItemDto));
        elasticSearchClient.update(caseData);
        log.info("Updated somu item {} from case {}. Event {}", somuItemDto.getUuid(), caseUUID, value(EVENT, SOMU_ITEM_UPDATED));
    }

    Set<UUID> search(SearchRequest request) {
        log.info("Searching for case {}", request.toString(), value(EVENT, SEARCH_REQUEST));
        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(QueryBuilders.boolQuery());
        hocsQueryBuilder.deleted(false);
        hocsQueryBuilder.reference(request.getReference(), request.getCaseTypes());
        hocsQueryBuilder.caseTypes(request.getCaseTypes());
        hocsQueryBuilder.dateRange(request.getDateReceived());
        hocsQueryBuilder.correspondentName(request.getCorrespondentName());
        hocsQueryBuilder.correspondentNameNotMember(request.getCorrespondentNameNotMember());
        hocsQueryBuilder.correspondentReference(request.getCorrespondentReference());
        hocsQueryBuilder.correspondentExternalKey(request.getCorrespondentExternalKey());
        hocsQueryBuilder.topic(request.getTopic());
        hocsQueryBuilder.privateOfficeTeam(request.getPrivateOfficeTeamUuid());
        hocsQueryBuilder.dataFields(request.getData());
        hocsQueryBuilder.activeOnlyFlag(request.getActiveOnly());

        Set<UUID> caseUUIDs;
        if (hocsQueryBuilder.hasClauses()) {
            caseUUIDs = elasticSearchClient.search(hocsQueryBuilder.build(), resultsLimit);
        } else {
            caseUUIDs = new HashSet<>(0);
        }

        log.info("Results {}", caseUUIDs.size(), value(EVENT, SEARCH_RESPONSE));
        return caseUUIDs;
    }

    private CaseData getCaseData(UUID caseUUID) {
        log.debug("Fetching Case {}", caseUUID);
        return elasticSearchClient.findById(caseUUID);
    }

}
