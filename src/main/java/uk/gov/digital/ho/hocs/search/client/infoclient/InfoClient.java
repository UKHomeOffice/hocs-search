package uk.gov.digital.ho.hocs.search.client.infoclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.search.application.RestHelper;
import uk.gov.digital.ho.hocs.search.domain.exceptions.ApplicationExceptions;

import java.util.Set;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.*;

@Slf4j
@Component
public class InfoClient {

    private final RestHelper restHelper;
    private final String serviceBaseURL;

    @Autowired
    public InfoClient(RestHelper restHelper,
                      @Value("${hocs.info-service}") String infoService) {
        this.restHelper = restHelper;
        this.serviceBaseURL = infoService;
    }

    public Set<String> getAllTopicUUIDs() {
        try {
            Set<String> topics = restHelper.get(serviceBaseURL, String.format("/topics"), Set.class);
            log.info("Got {} Topics", topics.size(), value(EVENT, INFO_CLIENT_GET_ALL_TOPIC_UUID_SUCCESS));
            return topics;
        } catch (ApplicationExceptions.ResourceException e) {
            log.error("Could not get all Topic UUIDs", value(EVENT, INFO_CLIENT_GET_ALL_TOPIC_UUID_FAILURE));
            throw new ApplicationExceptions.EntityNotFoundException(String.format("Could not get all Topic UUIDS"), INFO_CLIENT_GET_ALL_TOPIC_UUID_FAILURE);
        }
    }


    @Cacheable(value = "InfoClientGetTopic")
    public InfoTopic getTopic(UUID topicUUID) {
        try {
            InfoTopic infoTopic = restHelper.get(serviceBaseURL, String.format("/topic/%s", topicUUID), InfoTopic.class);
            log.debug("Got Topic {}", topicUUID, value(EVENT, INFO_CLIENT_GET_TOPIC_SUCCESS));
            return infoTopic;
        } catch (ApplicationExceptions.ResourceException e) {
            log.error("Could not get Topic {}", topicUUID, value(EVENT, INFO_CLIENT_GET_TOPIC_FAILURE));
            return new InfoTopic("", topicUUID);
        }
    }


}