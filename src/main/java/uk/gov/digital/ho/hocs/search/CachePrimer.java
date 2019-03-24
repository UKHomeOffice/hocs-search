package uk.gov.digital.ho.hocs.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.search.client.infoclient.InfoClient;

import java.util.Set;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.CACHE_PRIME_FAILED;
import static uk.gov.digital.ho.hocs.search.application.LogEvent.EVENT;

@Slf4j
@Component
@Profile({"cache"})
public class CachePrimer {

    private InfoClient infoClient;

    @Autowired
    public CachePrimer(InfoClient infoClient) {
        this.infoClient = infoClient;
    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        primeCaches();
    }

    // At second 0 minute 0 past every 2nd hour from 6 through 18 on every day-of-week from Monday through Friday.
    @Scheduled(cron = "0 0 6-18/2 * * 1-5")
    private void primeCaches() {
        log.info("Priming caches");
        try {
            log.debug("Priming topic cache");
            Set<String> topics = infoClient.getAllTopicUUIDs();
            for (String topic : topics) {
               infoClient.getTopic(UUID.fromString(topic));
            }
        } catch(Exception e) {
            log.warn("Failed to prime topic cache", value(EVENT, CACHE_PRIME_FAILED));
        }
        log.info("Caches primed");
    }
}