package uk.gov.digital.ho.hocs.search.client.auditclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.search.api.dto.SearchRequest;
import uk.gov.digital.ho.hocs.search.application.RequestData;
import uk.gov.digital.ho.hocs.search.application.RestHelper;

import java.time.LocalDateTime;
import java.util.*;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Component
public class AuditClient {

    private final String auditQueue;
    private final String raisingService;
    private final String namespace;
    private final ProducerTemplate producerTemplate;
    private final ObjectMapper objectMapper;
    private final RequestData requestData;
    private final RestHelper restHelper;
    private final String serviceBaseURL;

    @Autowired
    public AuditClient(ProducerTemplate producerTemplate,
                       @Value("${audit.queue}") String auditQueue,
                       @Value("${auditing.deployment.name}") String raisingService,
                       @Value("${auditing.deployment.namespace}") String namespace,
                       ObjectMapper objectMapper,
                       RequestData requestData,
                       RestHelper restHelper,
                       @Value("${hocs.audit-service}") String auditService) {
        this.producerTemplate = producerTemplate;
        this.auditQueue = auditQueue;
        this.raisingService = raisingService;
        this.namespace = namespace;
        this.objectMapper = objectMapper;
        this.requestData = requestData;
        this.restHelper = restHelper;
        this.serviceBaseURL = auditService;
    }

    //public void performSearch(SearchRequest searchRequest)  {
    //    String data = null;
    //    try {
    //        data = objectMapper.writeValueAsString(searchRequest);
    //    } catch (JsonProcessingException e) {
    //        log.error("Failed to parse data payload", UNCAUGHT_EXCEPTION);
    //    }
    //
    //    try {
    //        sendAuditMessage(caseData.getUuid(), objectMapper.writeValueAsString(new AuditPayload.CaseReference(caseData.getReference())), CASE_UPDATED, stageUUID, data);
    //    } catch (JsonProcessingException e) {
    //        log.error("Failed to parse audit payload", UNCAUGHT_EXCEPTION);
    //    }
    //}
    //
    //private void sendAuditMessage(UUID caseUUID, String payload, EventType eventType, UUID stageUUID, String data){
    //    CreateAuditRequest request = new CreateAuditRequest(
    //            requestData.correlationId(),
    //            caseUUID,
    //            stageUUID,
    //            raisingService,
    //            payload,
    //            data,
    //            namespace,
    //            LocalDateTime.now(),
    //            eventType,
    //            requestData.userId());
    //
    //    try {
    //        producerTemplate.sendBodyAndHeaders(auditQueue, objectMapper.writeValueAsString(request), getQueueHeaders());
    //        log.info("Create audit for Case UUID: {}, correlationID: {}, UserID: {}", caseUUID, requestData.correlationId(), requestData.userId(), value(EVENT, AUDIT_FAILED));
    //    } catch (Exception e) {
    //        log.error("Failed to create audit event for case UUID {} for reason {}", caseUUID, e, value(EVENT, AUDIT_FAILED));
    //    }
    //}
    //
    //private void sendAuditMessage(UUID caseUUID, String payload, EventType eventType, UUID stageUUID) {
    //    sendAuditMessage(caseUUID, payload, eventType, stageUUID, "");
    //}

    //public Set<GetAuditResponse> getAuditLinesForCase(UUID caseUUID, Set<String> requestedEvents) {
    //    try {
    //        String events = String.join(",", requestedEvents);
    //        GetAuditListResponse response = restHelper.get(serviceBaseURL, String.format("/audit/case/%s?types=%s", caseUUID, events), GetAuditListResponse.class);
    //        log.info("Got {} audits", response.getAudits().size(), value(EVENT, AUDIT_CLIENT_GET_AUDITS_FOR_CASE_SUCCESS));
    //        return response.getAudits();
    //    } catch (ApplicationExceptions.ResourceException e) {
    //        log.error("Could not get audit lines", value(EVENT, AUDIT_CLIENT_GET_AUDITS_FOR_CASE_FAILURE));
    //        return new HashSet<>();
    //    }
    //}

    private Map<String, Object> getQueueHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put(RequestData.CORRELATION_ID_HEADER, requestData.correlationId());
        headers.put(RequestData.USER_ID_HEADER, requestData.userId());
        headers.put(RequestData.USERNAME_HEADER, requestData.username());
        headers.put(RequestData.GROUP_HEADER, requestData.groups());
        return headers;
    }
}
