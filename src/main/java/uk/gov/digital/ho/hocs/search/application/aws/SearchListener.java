package uk.gov.digital.ho.hocs.search.application.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.api.CaseDataService;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateTopicRequest;
import uk.gov.digital.ho.hocs.search.api.dto.DeleteCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.DeleteTopicRequest;
import uk.gov.digital.ho.hocs.search.api.dto.SomuItemDto;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;
import uk.gov.digital.ho.hocs.search.application.LogEvent;
import uk.gov.digital.ho.hocs.search.application.queue.IndexDataChangeRequest;
import uk.gov.digital.ho.hocs.search.application.queue.DataChangeType;
import uk.gov.digital.ho.hocs.search.domain.exceptions.ApplicationExceptions;

@Slf4j
@Service
public class SearchListener {

    private final ObjectMapper objectMapper;
    private final CaseDataService caseDataService;

    public SearchListener(ObjectMapper objectMapper,
                          CaseDataService caseDataService) {
        this.objectMapper = objectMapper;
        this.caseDataService = caseDataService;
    }

    @SqsListener(value = "${aws.sqs.search.url}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void onDataChange(String message) throws JsonProcessingException {
        IndexDataChangeRequest request = objectMapper.readValue(message, IndexDataChangeRequest.class);

        DataChangeType type = DataChangeType.fromString(request.getType());

        if(type != null) {
            switch (type) {
                case CASE_CREATED:
                    CreateCaseRequest createCaseRequest = objectMapper.readValue(request.getData(), CreateCaseRequest.class);
                    caseDataService.createCase(request.getCaseUUID(), createCaseRequest);
                    break;
                case CASE_UPDATED:
                    UpdateCaseRequest updateCaseRequest = objectMapper.readValue(request.getData(), UpdateCaseRequest.class);
                    caseDataService.updateCase(request.getCaseUUID(), updateCaseRequest);
                    break;
                case CASE_DELETED:
                    DeleteCaseRequest deleteCaseRequest = objectMapper.readValue(request.getData(), DeleteCaseRequest.class);
                    caseDataService.deleteCase(request.getCaseUUID(), deleteCaseRequest);
                    break;
                case CASE_COMPLETED:
                    caseDataService.completeCase(request.getCaseUUID());
                    break;
                case CORRESPONDENT_CREATED:
                    CorrespondentDetailsDto correspondentCreated = objectMapper.readValue(request.getData(), CorrespondentDetailsDto.class);
                    caseDataService.createCorrespondent(request.getCaseUUID(), correspondentCreated);
                    break;
                case CORRESPONDENT_UPDATED:
                    CorrespondentDetailsDto correspondentUpdated = objectMapper.readValue(request.getData(), CorrespondentDetailsDto.class);
                    caseDataService.updateCorrespondent(request.getCaseUUID(), correspondentUpdated);
                    break;
                case CORRESPONDENT_DELETED:
                    CorrespondentDetailsDto correspondentDeleted = objectMapper.readValue(request.getData(), CorrespondentDetailsDto.class);
                    caseDataService.deleteCorrespondent(request.getCaseUUID(), correspondentDeleted);
                    break;
                case CASE_TOPIC_CREATED:
                    CreateTopicRequest createTopicRequest = objectMapper.readValue(request.getData(), CreateTopicRequest.class);
                    caseDataService.createTopic(request.getCaseUUID(), createTopicRequest);
                    break;
                case CASE_TOPIC_DELETED:
                    DeleteTopicRequest deleteTopicRequest = objectMapper.readValue(request.getData(), DeleteTopicRequest.class);
                    caseDataService.deleteTopic(request.getCaseUUID(), deleteTopicRequest);
                    break;
                case SOMU_ITEM_CREATED:
                    SomuItemDto createSomuItem = objectMapper.readValue(request.getData(), SomuItemDto.class);
                    caseDataService.createSomuItem(request.getCaseUUID(), createSomuItem);
                    break;
                case SOMU_ITEM_UPDATED:
                    SomuItemDto updateSomuItem = objectMapper.readValue(request.getData(), SomuItemDto.class);
                    caseDataService.updateSomuItem(request.getCaseUUID(), updateSomuItem);
                    break;
                case SOMU_ITEM_DELETED:
                    SomuItemDto deleteSomuItem = objectMapper.readValue(request.getData(), SomuItemDto.class);
                    caseDataService.deleteSomuItem(request.getCaseUUID(), deleteSomuItem);
                    break;
                default:
                    throw new ApplicationExceptions.InvalidEventTypeException(String.format("Missing Case statement: %s", request.getType()), LogEvent.UNKNOWN_SEARCH_MESSAGE_TYPE);
            }
        } else {
            log.debug("Skipping message, message Type: {}", request.getType(), LogEvent.NULL_SEARCH_MESSAGE_TYPE);
        }
    }
}
