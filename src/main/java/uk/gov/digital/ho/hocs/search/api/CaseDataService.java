package uk.gov.digital.ho.hocs.search.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateTopicRequest;
import uk.gov.digital.ho.hocs.search.api.dto.DeleteCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.DeleteTopicRequest;
import uk.gov.digital.ho.hocs.search.api.dto.SearchRequest;
import uk.gov.digital.ho.hocs.search.api.dto.SomuItemDto;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.helpers.ObjectMapperConverterHelper;
import uk.gov.digital.ho.hocs.search.client.elasticsearchclient.ElasticSearchClient;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.model.CorrespondentCaseData;
import uk.gov.digital.ho.hocs.search.domain.model.SomuCaseData;
import uk.gov.digital.ho.hocs.search.domain.model.SomuItem;
import uk.gov.digital.ho.hocs.search.domain.model.Topic;
import uk.gov.digital.ho.hocs.search.domain.model.TopicCaseData;
import uk.gov.digital.ho.hocs.search.domain.repositories.CaseTypeMappingRepository;
import uk.gov.digital.ho.hocs.search.domain.repositories.FieldQueryTypeMappingRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.EVENT;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_CASE_COMPLETED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_CASE_CREATED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_CASE_DELETED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_CASE_UPDATED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_CORRESPONDENT_CREATED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_CORRESPONDENT_DELETED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_CORRESPONDENT_UPDATED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_REQUEST;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_RESPONSE;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_TOPIC_CREATED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SEARCH_TOPIC_DELETED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SOMU_ITEM_CREATED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SOMU_ITEM_DELETED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.SOMU_ITEM_UPDATED;

@Service
@Slf4j
public class CaseDataService {

    private final ObjectMapper objectMapper;

    private final ElasticSearchClient elasticSearchClient;

    private final CaseTypeMappingRepository caseTypeMappingRepository;

    private final FieldQueryTypeMappingRepository fieldQueryTypeMappingRepository;

    public CaseDataService(ObjectMapper objectMapper,
                           ElasticSearchClient elasticSearchClient,
                           CaseTypeMappingRepository caseTypeMappingRepository,
                           FieldQueryTypeMappingRepository fieldQueryTypeMappingRepository) {
        this.objectMapper = objectMapper;
        this.elasticSearchClient = elasticSearchClient;
        this.caseTypeMappingRepository = caseTypeMappingRepository;
        this.fieldQueryTypeMappingRepository = fieldQueryTypeMappingRepository;
    }

    public void createCase(UUID caseUUID, CreateCaseRequest createCaseRequest) {
        log.debug("Creating case {}", caseUUID);

        var caseData = new CaseData(createCaseRequest);
        var caseDataMap = ObjectMapperConverterHelper.convertObjectToMap(objectMapper, caseData);
        elasticSearchClient.update(caseUUID,
            createCaseRequest.getType(),
            caseDataMap);

        log.info("Created case {}", caseUUID, value(EVENT, SEARCH_CASE_CREATED));
    }

    public void updateCase(UUID caseUUID, UpdateCaseRequest updateCaseRequest) {
        log.debug("Updating case {}", caseUUID);

        var caseData = new CaseData(updateCaseRequest);
        var caseDataMap = ObjectMapperConverterHelper.convertObjectToMap(objectMapper, caseData);
        elasticSearchClient.update(caseUUID,
            updateCaseRequest.getType(),
            caseDataMap);

        log.info("Updated case {}", caseUUID, value(EVENT, SEARCH_CASE_UPDATED));
    }

    public void deleteCase(UUID caseUUID, DeleteCaseRequest deleteCaseRequest) {
        log.debug("Deleting ({}) case {}", deleteCaseRequest.getDeleted(), caseUUID);

        Map<String, Object> objectMap = Map.of("deleted", deleteCaseRequest.getDeleted());
        elasticSearchClient.update(caseUUID,
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            objectMap);

        log.info("Deleted ({}) case {}", deleteCaseRequest.getDeleted(), caseUUID, value(EVENT, SEARCH_CASE_DELETED));
    }

    public void completeCase(UUID caseUUID) {
        log.debug("Complete case {}", caseUUID);

        Map<String, Object> objectMap = Map.of("completed", true);
        elasticSearchClient.update(caseUUID,
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            objectMap);

        log.info("Completed case {}", caseUUID, value(EVENT, SEARCH_CASE_COMPLETED));
    }

    public void createCorrespondent(UUID caseUUID, CorrespondentDetailsDto correspondentDetailsDto) {
        log.debug("Adding correspondent {} to case {}", correspondentDetailsDto.getUuid(), caseUUID);

        CorrespondentCaseData correspondentCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), CorrespondentCaseData.class);
        correspondentCaseData.addCorrespondent(correspondentDetailsDto);

        elasticSearchClient.update(caseUUID,
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, correspondentCaseData));

        log.info("Added correspondent {} to case {}", correspondentDetailsDto.getUuid(), caseUUID,
            value(EVENT, SEARCH_CORRESPONDENT_CREATED));
    }

    public void deleteCorrespondent(UUID caseUUID, CorrespondentDetailsDto correspondentDetailsDto) {
        log.debug("Deleting correspondent {} from case {}", correspondentDetailsDto.getUuid(), caseUUID);

        CorrespondentCaseData correspondentCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), CorrespondentCaseData.class);
        correspondentCaseData.removeCorrespondent(correspondentDetailsDto.getUuid());

        elasticSearchClient.update(caseUUID,
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, correspondentCaseData));

        log.info("Deleted correspondent {} from case {}", correspondentDetailsDto.getUuid(), caseUUID,
            value(EVENT, SEARCH_CORRESPONDENT_DELETED));
    }

    public void updateCorrespondent(UUID caseUUID, CorrespondentDetailsDto correspondentDetailsDto) {
        log.debug("Updating correspondent {} from case {}", correspondentDetailsDto.getUuid(), caseUUID);

        CorrespondentCaseData correspondentCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), CorrespondentCaseData.class);
        correspondentCaseData.updateCorrespondent(correspondentDetailsDto);

        elasticSearchClient.update(caseUUID,
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, correspondentCaseData));

        log.info("Updating correspondent {} for case {}", correspondentDetailsDto.getUuid(), caseUUID,
            value(EVENT, SEARCH_CORRESPONDENT_UPDATED));
    }

    public void createTopic(UUID caseUUID, CreateTopicRequest createTopicRequest) {
        log.debug("Adding topic {} to case {}", createTopicRequest.getUuid(), caseUUID);

        TopicCaseData topicCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), TopicCaseData.class);
        topicCaseData.addTopic(Topic.from(createTopicRequest));

        elasticSearchClient.update(caseUUID,
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, topicCaseData));

        log.info("Added topic {} to case {}", createTopicRequest.getUuid(), caseUUID,
            value(EVENT, SEARCH_TOPIC_CREATED));
    }

    public void deleteTopic(UUID caseUUID, DeleteTopicRequest deleteTopicRequest) {
        log.debug("Deleting topic {} from case {}", deleteTopicRequest.getUuid(), caseUUID);
        TopicCaseData topicCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), TopicCaseData.class);
        topicCaseData.removeTopic(deleteTopicRequest.getUuid());

        elasticSearchClient.update(caseUUID,
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, topicCaseData));

        log.info("Deleted topic {} from case {}. Event {}", deleteTopicRequest.getUuid(), caseUUID,
            value(EVENT, SEARCH_TOPIC_DELETED));
    }

    public void createSomuItem(UUID caseUUID, SomuItemDto somuItemDto) {
        log.debug("Adding somu item {} to case {}", somuItemDto.getUuid(), caseUUID);

        SomuCaseData somuCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), SomuCaseData.class);
        somuCaseData.addSomuItem(SomuItem.from(somuItemDto));

        elasticSearchClient.update(caseUUID,
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, somuCaseData));

        log.info("Added somu item {} to case {}. Event {}", somuItemDto.getUuid(), caseUUID,
            value(EVENT, SOMU_ITEM_CREATED));
    }

    public void deleteSomuItem(UUID caseUUID, SomuItemDto somuItem) {
        log.debug("Deleting somu item {} from case {}", somuItem.getUuid(), caseUUID);

        SomuCaseData somuCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), SomuCaseData.class);
        somuCaseData.removeSomuItem(somuItem.getSomuTypeUuid());

        elasticSearchClient.update(caseUUID,
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, somuCaseData));

        log.info("Deleted somu item {} from case {}. Event {}", somuItem.getUuid(), caseUUID,
            value(EVENT, SOMU_ITEM_DELETED));
    }

    public void updateSomuItem(UUID caseUUID, SomuItemDto somuItemDto) {
        log.debug("Updating somu item {} from case {}", somuItemDto.getUuid(), caseUUID);

        SomuCaseData somuCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), SomuCaseData.class);
        somuCaseData.updateSomuItem(SomuItem.from(somuItemDto));

        elasticSearchClient.update(caseUUID,
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, somuCaseData));

        log.info("Updated somu item {} from case {}. Event {}", somuItemDto.getUuid(), caseUUID,
            value(EVENT, SOMU_ITEM_UPDATED));
    }

    public Set<UUID> search(SearchRequest request) {
        log.info("Searching for case {}", request.toString(), value(EVENT, SEARCH_REQUEST));
        HocsQueryBuilder hocsQueryBuilder =
            new HocsQueryBuilder(QueryBuilders.boolQuery(), fieldQueryTypeMappingRepository)
                .reference(request.getReference(), request.getCaseTypes())
                .caseTypes(request.getCaseTypes())
                .dateRange(request.getDateReceived())
                .correspondentAddress1(request.getCorrespondentAddress1())
                .correspondentEmail(request.getCorrespondentEmail())
                .correspondentName(request.getCorrespondentName())
                .correspondentNameNotMember(request.getCorrespondentNameNotMember())
                .correspondentPostcode(request.getCorrespondentPostcode())
                .correspondentReference(request.getCorrespondentReference())
                .correspondentExternalKey(request.getCorrespondentExternalKey())
                .topic(request.getTopic())
                .privateOfficeTeam(request.getPrivateOfficeTeamUuid())
                .dataFields(request.getData())
                .activeOnlyFlag(request.getActiveOnly());

        if (!hocsQueryBuilder.hasClauses()) {
            return Collections.emptySet();
        }

        var cases = elasticSearchClient.search(hocsQueryBuilder.build());
        var casesUuids =
            cases.stream()
                .map(caseMap -> {
                    var caseData = convertMapToCaseData(caseMap);
                    return caseData.getCaseUUID();
                })
                .collect(Collectors.toSet());

        log.info("Results {}", casesUuids.size(), value(EVENT, SEARCH_RESPONSE));
        return casesUuids;
    }

    private Map<String, Object> getCaseData(UUID caseUuid) {
        log.debug("Fetching Case {}", caseUuid);
        return elasticSearchClient.findById(caseUuid,
                caseTypeMappingRepository.getCaseTypeByShortCode(caseUuid));
    }

    private CaseData convertMapToCaseData(Map<String, Object> caseData) {
        return objectMapper.convertValue(caseData, new TypeReference<>() {});
    }

}
