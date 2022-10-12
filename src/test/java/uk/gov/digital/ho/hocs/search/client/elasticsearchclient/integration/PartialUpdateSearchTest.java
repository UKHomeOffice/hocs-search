package uk.gov.digital.ho.hocs.search.client.elasticsearchclient.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.digital.ho.hocs.search.api.CaseDataService;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateTopicRequest;
import uk.gov.digital.ho.hocs.search.api.dto.SomuItemDto;
import uk.gov.digital.ho.hocs.search.aws.listeners.integration.BaseAwsSqsIntegrationTest;
import uk.gov.digital.ho.hocs.search.client.elasticsearchclient.ElasticSearchClient;
import uk.gov.digital.ho.hocs.search.domain.model.CorrespondentCaseData;
import uk.gov.digital.ho.hocs.search.domain.model.SomuCaseData;
import uk.gov.digital.ho.hocs.search.domain.model.TopicCaseData;
import uk.gov.digital.ho.hocs.search.helpers.AllMapKeyMatcher;
import uk.gov.digital.ho.hocs.search.helpers.CaseTypeUuidHelper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("localstack")
class PartialUpdateSearchTest extends BaseAwsSqsIntegrationTest {

    @Autowired
    public ObjectMapper objectMapper;

    @SpyBean
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private CaseDataService caseDataService;

    @Test
    void onlyUpdateCorrespondents() {
        UUID caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        CorrespondentDetailsDto correspondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(),
            LocalDateTime.now(), "LAW", "FULLNAME",
            new AddressDto("postcode", "address1", "address2", "address3", "country"), "0", "e", "REF", "ExtKey");

        caseDataService.createCorrespondent(caseUuid, correspondentDetailsDto);

        await().until(() -> elasticSearchClient.findById(caseUuid, "MIN") != null);

        var correspondents = objectMapper.convertValue(elasticSearchClient.findById(caseUuid, "MIN"),
            CorrespondentCaseData.class);
        assertThat(correspondents.getAllCorrespondents()).hasSize(1);
        assertThat(correspondents.getCurrentCorrespondents()).hasSize(1);

        verify(elasticSearchClient).update(any(), any(),
            argThat(new AllMapKeyMatcher("currentCorrespondents", "allCorrespondents")));
    }

    @Test
    void onlyUpdateTopics() {
        UUID caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        CreateTopicRequest createTopicRequest = new CreateTopicRequest(UUID.randomUUID(), "TOPIC");

        caseDataService.createTopic(caseUuid, createTopicRequest);

        await().until(() -> elasticSearchClient.findById(caseUuid, "MIN") != null);

        var topics = objectMapper.convertValue(elasticSearchClient.findById(caseUuid, "MIN"), TopicCaseData.class);
        assertThat(topics.getAllTopics()).hasSize(1);
        assertThat(topics.getCurrentTopics()).hasSize(1);

        verify(elasticSearchClient).update(any(), any(), argThat(new AllMapKeyMatcher("allTopics", "currentTopics")));
    }

    @Test
    void onlyUpdateSomu() {
        UUID caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        SomuItemDto somuItemDto = new SomuItemDto(UUID.randomUUID(), UUID.randomUUID(), Map.of("field", "value"));

        caseDataService.createSomuItem(caseUuid, somuItemDto);

        await().until(() -> elasticSearchClient.findById(caseUuid, "MIN") != null);

        var somuItems = objectMapper.convertValue(elasticSearchClient.findById(caseUuid, "MIN"), SomuCaseData.class);
        assertThat(somuItems.getAllSomuItems()).hasSize(1);

        verify(elasticSearchClient).update(any(), any(), argThat(new AllMapKeyMatcher("allSomuItems")));
    }

}
