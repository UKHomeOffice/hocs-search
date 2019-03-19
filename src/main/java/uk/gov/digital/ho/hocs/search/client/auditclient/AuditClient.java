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
import uk.gov.digital.ho.hocs.search.client.auditclient.dto.CreateAuditRequest;

import java.time.LocalDateTime;
import java.util.*;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.*;
import static uk.gov.digital.ho.hocs.search.client.auditclient.EventType.SEARCH_REQUEST;

@Slf4j
@Component
public class AuditClient {

    private final String auditQueue;
    private final String raisingService;
    private final String namespace;
    private final ProducerTemplate producerTemplate;
    private final ObjectMapper objectMapper;
    private final RequestData requestData;

    @Autowired
    public AuditClient(ProducerTemplate producerTemplate,
                       @Value("${audit.queue}") String auditQueue,
                       @Value("${auditing.deployment.name}") String raisingService,
                       @Value("${auditing.deployment.namespace}") String namespace,
                       ObjectMapper objectMapper,
                       RequestData requestData,
                       RestHelper restHelper) {
        this.producerTemplate = producerTemplate;
        this.auditQueue = auditQueue;
        this.raisingService = raisingService;
        this.namespace = namespace;
        this.objectMapper = objectMapper;
        this.requestData = requestData;
    }

    public void performSearch(SearchRequest searchRequest)  {
        String data = null;
        try {
            data = objectMapper.writeValueAsString(searchRequest);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse data payload", UNCAUGHT_EXCEPTION);
        }

        try {
            sendAuditMessage(null, objectMapper.writeValueAsString(searchRequest), SEARCH_REQUEST, null,"");
        } catch (JsonProcessingException e) {
            log.error("Failed to parse audit payload", UNCAUGHT_EXCEPTION);
        }
    }

    private void sendAuditMessage(UUID caseUUID, String payload, EventType eventType, UUID stageUUID, String data){
        CreateAuditRequest request = new CreateAuditRequest(
                requestData.correlationId(),
                caseUUID,
                stageUUID,
                raisingService,
                payload,
                data,
                namespace,
                LocalDateTime.now(),
                eventType,
                requestData.userId());

        try {
            producerTemplate.sendBodyAndHeaders(auditQueue, objectMapper.writeValueAsString(request), getQueueHeaders());
            log.info("Create audit for Case UUID: {}, correlationID: {}, UserID: {}", caseUUID, requestData.correlationId(), requestData.userId(), value(EVENT, AUDIT_FAILED));
        } catch (Exception e) {
            log.error("Failed to create audit event for case UUID {} for reason {}", caseUUID, e, value(EVENT, AUDIT_FAILED));
        }
    }

    private Map<String, Object> getQueueHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put(RequestData.CORRELATION_ID_HEADER, requestData.correlationId());
        headers.put(RequestData.USER_ID_HEADER, requestData.userId());
        headers.put(RequestData.USERNAME_HEADER, requestData.username());
        headers.put(RequestData.GROUP_HEADER, requestData.groups());
        return headers;
    }
}
