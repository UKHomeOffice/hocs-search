package uk.gov.digital.ho.hocs.search.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;
import uk.gov.digital.ho.hocs.search.domain.repository.CaseRepository;
import uk.gov.digital.ho.hocs.search.domain.repository.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.repository.model.Topic;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CaseDataService {

    private final CaseRepository caseDataRepository;

    @Autowired
    public CaseDataService(CaseRepository caseDataRepository) {
        this.caseDataRepository = caseDataRepository;
    }

    void createCase(UUID caseUUID, CreateCaseRequest createCaseRequest) {
        log.debug("Creating case %s", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.create(createCaseRequest);
        caseDataRepository.save(caseData);
        log.debug("Created case %s", caseUUID);
    }

    void updateCase(UUID caseUUID, UpdateCaseRequest updateCaseRequest) {
        log.debug("Updating case %s", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.update(updateCaseRequest);
        caseDataRepository.save(caseData);
        log.debug("Updated case %s", caseUUID);
    }

    void deleteCase(UUID caseUUID) {
        log.debug("Deleting case %s", caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.delete();
        caseDataRepository.save(caseData);
        log.debug("Deleted case %s", caseUUID);
    }

    void createCorrespondent(UUID caseUUID, CreateCorrespondentRequest createCorrespondentRequest) {
        log.debug("Adding correspondent %s to case %s", createCorrespondentRequest.getUuid(), caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.addCorrespondent(createCorrespondentRequest);
        caseDataRepository.save(caseData);
        log.debug("Added correspondent %s to case %s", createCorrespondentRequest.getUuid(), caseUUID);
    }

    void deleteCorrespondent(UUID caseUUID, UUID correspondentUUID) {
        log.debug("Deleting correspondent %s from case %s", correspondentUUID, caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.removeCorrespondent(correspondentUUID);
        caseDataRepository.save(caseData);
        log.debug("Deleted correspondent %s from case %s", correspondentUUID, caseUUID);
    }

    void createTopic(UUID caseUUID, UUID topicUUID) {
        log.debug("Adding topic %s to case %s", topicUUID, caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        // TODO: info service lookup.
        String value = "";
        Topic topic = new Topic(topicUUID, value);
        caseData.addTopic(topic);
        caseDataRepository.save(caseData);
        log.debug("Added topic %s to case %s", topicUUID, caseUUID);
    }

    void deleteTopic(UUID caseUUID, UUID topicUUID) {
        log.debug("Deleting topic %s from case %s", topicUUID, caseUUID);
        CaseData caseData = getCaseData(caseUUID);
        caseData.removeTopic(topicUUID);
        caseDataRepository.save(caseData);
        log.debug("Deleted topic %s from case %s", topicUUID, caseUUID);
    }

    private CaseData getCaseData(UUID caseUUID){
        log.debug("Fetching Case %s", caseUUID);
        Optional<CaseData> originalCaseData = caseDataRepository.findById(caseUUID);
        if(originalCaseData.isPresent()){
            log.debug("Found Case %s", caseUUID);
        } else {
            log.info("Didn't find case %s, creating new", caseUUID);
        }
        return originalCaseData.orElse(new CaseData(caseUUID));
    }

}