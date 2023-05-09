package uk.gov.digital.ho.hocs.search.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.index.query.BoolQueryBuilder;
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
import uk.gov.digital.ho.hocs.search.client.OpenSearchClient;
import uk.gov.digital.ho.hocs.search.client.CaseQueryFactory;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.model.CorrespondentCaseData;
import uk.gov.digital.ho.hocs.search.domain.model.SomuCaseData;
import uk.gov.digital.ho.hocs.search.domain.model.SomuItem;
import uk.gov.digital.ho.hocs.search.domain.model.Topic;
import uk.gov.digital.ho.hocs.search.domain.model.TopicCaseData;
import uk.gov.digital.ho.hocs.search.domain.repositories.CaseTypeMappingRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

    private final OpenSearchClient openSearchClient;

    private final CaseTypeMappingRepository caseTypeMappingRepository;

    private final CaseQueryFactory caseQueryFactory;

    public CaseDataService(ObjectMapper objectMapper,
                           OpenSearchClient openSearchClient,
                           CaseTypeMappingRepository caseTypeMappingRepository,
                           CaseQueryFactory caseQueryFactory) {
        this.objectMapper = objectMapper;
        this.openSearchClient = openSearchClient;
        this.caseTypeMappingRepository = caseTypeMappingRepository;
        this.caseQueryFactory = caseQueryFactory;
    }

    public void createCase(UUID caseUUID, CreateCaseRequest createCaseRequest) {
        log.debug("Creating case {}", caseUUID);

        var caseData = new CaseData(createCaseRequest);
        var caseDataMap = ObjectMapperConverterHelper.convertObjectToMap(objectMapper, caseData);
        openSearchClient.update(
            createCaseRequest.getType(),
            caseUUID,
            caseDataMap);

        log.info("Created case {}", caseUUID, value(EVENT, SEARCH_CASE_CREATED));
    }

    public void updateCase(UUID caseUUID, UpdateCaseRequest updateCaseRequest) {
        log.debug("Updating case {}", caseUUID);

        var caseData = new CaseData(updateCaseRequest);
        var caseDataMap = ObjectMapperConverterHelper.convertObjectToMap(objectMapper, caseData);
        openSearchClient.update(
            updateCaseRequest.getType(),
            caseUUID,
            caseDataMap);

        log.info("Updated case {}", caseUUID, value(EVENT, SEARCH_CASE_UPDATED));
    }

    public void deleteCase(UUID caseUUID, DeleteCaseRequest deleteCaseRequest) {
        log.debug("Deleting ({}) case {}", deleteCaseRequest.getDeleted(), caseUUID);

        Map<String, Object> objectMap = Map.of("deleted", deleteCaseRequest.getDeleted());
        openSearchClient.update(
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            caseUUID,
            objectMap);

        log.info("Deleted ({}) case {}", deleteCaseRequest.getDeleted(), caseUUID, value(EVENT, SEARCH_CASE_DELETED));
    }

    public void completeCase(UUID caseUUID) {
        log.debug("Complete case {}", caseUUID);

        Map<String, Object> objectMap = Map.of("completed", true);
        openSearchClient.update(
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            caseUUID,
            objectMap);

        log.info("Completed case {}", caseUUID, value(EVENT, SEARCH_CASE_COMPLETED));
    }

    public void createCorrespondent(UUID caseUUID, CorrespondentDetailsDto correspondentDetailsDto) {
        log.debug("Adding correspondent {} to case {}", correspondentDetailsDto.getUuid(), caseUUID);

        CorrespondentCaseData correspondentCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), CorrespondentCaseData.class);
        correspondentCaseData.addCorrespondent(correspondentDetailsDto);

        openSearchClient.update(
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            caseUUID,
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, correspondentCaseData));

        log.info("Added correspondent {} to case {}", correspondentDetailsDto.getUuid(), caseUUID,
            value(EVENT, SEARCH_CORRESPONDENT_CREATED));
    }

    public void deleteCorrespondent(UUID caseUUID, CorrespondentDetailsDto correspondentDetailsDto) {
        log.debug("Deleting correspondent {} from case {}", correspondentDetailsDto.getUuid(), caseUUID);

        CorrespondentCaseData correspondentCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), CorrespondentCaseData.class);
        correspondentCaseData.removeCorrespondent(correspondentDetailsDto.getUuid());

        openSearchClient.update(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            caseUUID,
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, correspondentCaseData));

        log.info("Deleted correspondent {} from case {}", correspondentDetailsDto.getUuid(), caseUUID,
            value(EVENT, SEARCH_CORRESPONDENT_DELETED));
    }

    public void updateCorrespondent(UUID caseUUID, CorrespondentDetailsDto correspondentDetailsDto) {
        log.debug("Updating correspondent {} from case {}", correspondentDetailsDto.getUuid(), caseUUID);

        CorrespondentCaseData correspondentCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), CorrespondentCaseData.class);
        correspondentCaseData.updateCorrespondent(correspondentDetailsDto);

        openSearchClient.update(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            caseUUID,
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, correspondentCaseData));

        log.info("Updating correspondent {} for case {}", correspondentDetailsDto.getUuid(), caseUUID,
            value(EVENT, SEARCH_CORRESPONDENT_UPDATED));
    }

    public void createTopic(UUID caseUUID, CreateTopicRequest createTopicRequest) {
        log.debug("Adding topic {} to case {}", createTopicRequest.getUuid(), caseUUID);

        TopicCaseData topicCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), TopicCaseData.class);
        topicCaseData.addTopic(Topic.from(createTopicRequest));

        openSearchClient.update(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            caseUUID,
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, topicCaseData));

        log.info("Added topic {} to case {}", createTopicRequest.getUuid(), caseUUID,
            value(EVENT, SEARCH_TOPIC_CREATED));
    }

    public void deleteTopic(UUID caseUUID, DeleteTopicRequest deleteTopicRequest) {
        log.debug("Deleting topic {} from case {}", deleteTopicRequest.getUuid(), caseUUID);
        TopicCaseData topicCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), TopicCaseData.class);
        topicCaseData.removeTopic(deleteTopicRequest.getUuid());

        openSearchClient.update(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            caseUUID,
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, topicCaseData));

        log.info("Deleted topic {} from case {}. Event {}", deleteTopicRequest.getUuid(), caseUUID,
            value(EVENT, SEARCH_TOPIC_DELETED));
    }

    public void createSomuItem(UUID caseUUID, SomuItemDto somuItemDto) {
        log.debug("Adding somu item {} to case {}", somuItemDto.getUuid(), caseUUID);

        SomuCaseData somuCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), SomuCaseData.class);
        somuCaseData.addSomuItem(SomuItem.from(somuItemDto));

        openSearchClient.update(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            caseUUID,
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, somuCaseData));

        log.info("Added somu item {} to case {}. Event {}", somuItemDto.getUuid(), caseUUID,
            value(EVENT, SOMU_ITEM_CREATED));
    }

    public void deleteSomuItem(UUID caseUUID, SomuItemDto somuItem) {
        log.debug("Deleting somu item {} from case {}", somuItem.getUuid(), caseUUID);

        SomuCaseData somuCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), SomuCaseData.class);
        somuCaseData.removeSomuItem(somuItem.getSomuTypeUuid());

        openSearchClient.update(
            caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            caseUUID,
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, somuCaseData));

        log.info("Deleted somu item {} from case {}. Event {}", somuItem.getUuid(), caseUUID,
            value(EVENT, SOMU_ITEM_DELETED));
    }

    public void updateSomuItem(UUID caseUUID, SomuItemDto somuItemDto) {
        log.debug("Updating somu item {} from case {}", somuItemDto.getUuid(), caseUUID);

        SomuCaseData somuCaseData =
            objectMapper.convertValue(getCaseData(caseUUID), SomuCaseData.class);
        somuCaseData.updateSomuItem(SomuItem.from(somuItemDto));

        openSearchClient.update(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID),
            caseUUID,
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, somuCaseData));

        log.info("Updated somu item {} from case {}. Event {}", somuItemDto.getUuid(), caseUUID,
            value(EVENT, SOMU_ITEM_UPDATED));
    }

    public Set<UUID> search(SearchRequest request) {
        log.debug("Searching for case {}", request.toString(), value(EVENT, SEARCH_REQUEST));

        if (request.getCaseTypes() == null || request.getCaseTypes().isEmpty()) {
            log.error("No case types provided in search request");
            return new HashSet<>();
        }

        Map<String, BoolQueryBuilder> caseTypeQueryBuilders = new HashMap<>();
        for (String caseType : request.getCaseTypes()) {
            CaseQueryFactory.CaseQuery query =
                caseQueryFactory.createCaseQuery()
                    .reference(request.getReference(), caseType)
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

            if (!query.hasClauses()) {
                continue;
            }

            caseTypeQueryBuilders.put(caseType, query.build());
        }

        if (caseTypeQueryBuilders.isEmpty()) {
            return Collections.emptySet();
        }

        var cases = openSearchClient.search(caseTypeQueryBuilders);
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
        return openSearchClient.findById(caseTypeMappingRepository.getCaseTypeByShortCode(caseUuid),
            caseUuid);
    }

    private CaseData convertMapToCaseData(Map<String, Object> caseData) {
        return objectMapper.convertValue(caseData, new TypeReference<>() {});
    }

}
