package uk.gov.digital.ho.hocs.search.api;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.api.dto.*;
import uk.gov.digital.ho.hocs.search.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.search.client.infoclient.InfoTopic;
import uk.gov.digital.ho.hocs.search.domain.repository.CaseRepository;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.model.Topic;

import java.util.*;

@Service
@Slf4j
public class CaseDataService {

    private final CaseRepository caseDataRepository;

    private final InfoClient infoClient;

    private final ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public CaseDataService(CaseRepository caseDataRepository, InfoClient infoClient, ElasticsearchTemplate elasticsearchTemplate) {
        this.caseDataRepository = caseDataRepository;
        this.infoClient = infoClient;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public void createCase(UUID caseUUID, CreateCaseRequest createCaseRequest) {
        log.debug("Creating case {}", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.create(createCaseRequest);
        caseDataRepository.save(caseData);
        log.debug("Created case {}", caseUUID);
    }

    public void updateCase(UUID caseUUID, UpdateCaseRequest updateCaseRequest) {
        log.debug("Updating case {}", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.update(updateCaseRequest);
        caseDataRepository.save(caseData);
        log.debug("Updated case {}", caseUUID);
    }

    public void deleteCase(UUID caseUUID) {
        log.debug("Deleting case {}", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.delete();
        caseDataRepository.save(caseData);
        log.debug("Deleted case {}", caseUUID);
    }

    public void createCorrespondent(UUID caseUUID, CreateCorrespondentRequest createCorrespondentRequest) {
        log.debug("Adding correspondent {} to case {}", createCorrespondentRequest.getUuid(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.addCorrespondent(createCorrespondentRequest);
        caseDataRepository.save(caseData);
        log.debug("Added correspondent {} to case {}", createCorrespondentRequest.getUuid(), caseUUID);
    }

    public void deleteCorrespondent(UUID caseUUID, UUID correspondentUUID) {
        log.debug("Deleting correspondent {} from case {}", correspondentUUID, caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.removeCorrespondent(correspondentUUID);
        caseDataRepository.save(caseData);
        log.debug("Deleted correspondent {} from case {}", correspondentUUID, caseUUID);
    }

    public void createTopic(UUID caseUUID, UUID topicUUID) {
        log.debug("Adding topic {} to case {}", topicUUID, caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        InfoTopic infoTopic = infoClient.getTopic(topicUUID);
        caseData.addTopic(Topic.from(infoTopic));
        caseDataRepository.save(caseData);
        log.debug("Added topic {} to case {}", topicUUID, caseUUID);
    }

    public void deleteTopic(UUID caseUUID, UUID topicUUID) {
        log.debug("Deleting topic {} from case {}", topicUUID, caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.removeTopic(topicUUID);
        caseDataRepository.save(caseData);
        log.debug("Deleted topic {} from case {}", topicUUID, caseUUID);
    }

    List<String> search(SearchRequest request){
        log.debug("Searching for case");
        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(QueryBuilders.boolQuery());
        hocsQueryBuilder.caseTypes(request.getCaseTypes());
        hocsQueryBuilder.dateRange(request.getDateReceived());
        hocsQueryBuilder.correspondent(request.getCorrespondentName());
        hocsQueryBuilder.topic(request.getTopic());
        hocsQueryBuilder.dataFields(request.getData());
        hocsQueryBuilder.activeOnlyFlag(request.getActiveOnly());

        NativeSearchQuery query =  new NativeSearchQueryBuilder().withFilter(hocsQueryBuilder.build()).build();

        List<String> caseUUIDs = elasticsearchTemplate.queryForIds(query);
        log.debug("Results {}", caseUUIDs.size());
        return caseUUIDs;
    }

    private CaseData getCaseData(UUID caseUUID){
        log.debug("Fetching Case {}", caseUUID);
        Optional<CaseData> originalCaseData = caseDataRepository.findById(caseUUID);
        if(originalCaseData.isPresent()){
            log.debug("Found Case {}", caseUUID);
        } else {
            log.info("Didn't find case {}, creating new", caseUUID);
        }
        return originalCaseData.orElse(new CaseData(caseUUID));
    }

}