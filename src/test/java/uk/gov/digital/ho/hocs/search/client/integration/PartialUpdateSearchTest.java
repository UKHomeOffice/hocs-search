package uk.gov.digital.ho.hocs.search.client.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.internal.matchers.Contains;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.digital.ho.hocs.search.api.CaseDataService;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateTopicRequest;
import uk.gov.digital.ho.hocs.search.api.dto.SomuItemDto;
import uk.gov.digital.ho.hocs.search.api.elastic.scripts.CorrespondentScriptService;
import uk.gov.digital.ho.hocs.search.client.OpenSearchClient;
import uk.gov.digital.ho.hocs.search.domain.model.Correspondent;
import uk.gov.digital.ho.hocs.search.domain.model.CorrespondentCaseData;
import uk.gov.digital.ho.hocs.search.domain.model.SomuCaseData;
import uk.gov.digital.ho.hocs.search.domain.model.TopicCaseData;
import uk.gov.digital.ho.hocs.search.helpers.AllMapKeyMatcher;
import uk.gov.digital.ho.hocs.search.helpers.CaseTypeUuidHelper;
import uk.gov.digital.ho.hocs.search.helpers.ScriptMatcher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles({"localstack"})
class PartialUpdateSearchTest {

    public static final String INDEX_TYPE = "MIN";

    @Autowired
    public ObjectMapper objectMapper;

    @SpyBean
    private OpenSearchClient openSearchClient;

    @Autowired
    private CaseDataService caseDataService;

    private volatile Throwable threadException;

    private UUID testCaseUUID;

    @BeforeEach
    public void setup() {
        threadException = null;
        testCaseUUID = UUID.randomUUID();
    }

    @Test
    void onlyUpdateCorrespondents() {
        UUID caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        CorrespondentDetailsDto correspondentDetailsDto = generateCorrespondentDetailsDto();

        caseDataService.createCorrespondent(caseUuid, correspondentDetailsDto);

        await().until(() -> openSearchClient.findById(INDEX_TYPE, caseUuid) != null);

        var correspondents = objectMapper.convertValue(openSearchClient.findById(INDEX_TYPE, caseUuid),
            CorrespondentCaseData.class);
        assertThat(correspondents.getAllCorrespondents()).hasSize(1);
        assertThat(correspondents.getCurrentCorrespondents()).hasSize(1);

        verify(openSearchClient).update(any(), any(),
            argThat(
                new ScriptMatcher(
                    List.of(new Contains("allCorrespondents"), new Contains("currentCorrespondents")),
                    List.of(new AllMapKeyMatcher("correspondent"))
                )));
    }

    @Test
    public void addingCorrespondentsInParallel_addsAllCorrespondentsAndDoesNotThrowVersionConflict() throws InterruptedException {
        int threadCount = 3;
        runCaseUpdateThreads(threadCount);

        // Assert that version_conflict_engine_exception no longer thrown.
        assertThat(threadException).isNull();

        await().until(() -> openSearchClient.findById(INDEX_TYPE, testCaseUUID) != null);

        // Assert that both data entries are added on retry.
        var caseData = openSearchClient.findById(INDEX_TYPE, testCaseUUID);
        assertThat(((List<?>) caseData.get("currentCorrespondents")).size()).isEqualTo(threadCount);
    }

    @Test
    void onlyUpdateTopics() {
        UUID caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        CreateTopicRequest createTopicRequest = new CreateTopicRequest(UUID.randomUUID(), "TOPIC");

        caseDataService.createTopic(caseUuid, createTopicRequest);

        await().until(() -> openSearchClient.findById(INDEX_TYPE, caseUuid) != null);

        var topics = objectMapper.convertValue(openSearchClient.findById(INDEX_TYPE, caseUuid), TopicCaseData.class);
        assertThat(topics.getAllTopics()).hasSize(1);
        assertThat(topics.getCurrentTopics()).hasSize(1);

        verify(openSearchClient).update(any(), any(), argThat(new AllMapKeyMatcher("allTopics", "currentTopics")));
    }

    @Test
    void onlyUpdateSomu() {
        UUID caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        SomuItemDto somuItemDto = new SomuItemDto(UUID.randomUUID(), UUID.randomUUID(), Map.of("field", "value"));

        caseDataService.createSomuItem(caseUuid, somuItemDto);

        await().until(() -> openSearchClient.findById(INDEX_TYPE, caseUuid) != null);

        var somuItems = objectMapper.convertValue(openSearchClient.findById(INDEX_TYPE, caseUuid), SomuCaseData.class);
        assertThat(somuItems.getAllSomuItems()).hasSize(1);

        verify(openSearchClient).update(any(), any(), argThat(new AllMapKeyMatcher("allSomuItems")));
    }

    private static CorrespondentDetailsDto generateCorrespondentDetailsDto() {
        return new CorrespondentDetailsDto(UUID.randomUUID(), LocalDateTime.now(), "LAW", "FULLNAME",
            new AddressDto("postcode", "address1", "address2", "address3", "country"), "0", "e", "REF", "ExtKey"
        );
    }

    private void runCaseUpdateThreads(int threadCount) throws InterruptedException {
        CountDownLatch startedLatch = new CountDownLatch(1);
        CountDownLatch finishedLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            var thread = new AddCorrespondentRequestThread(openSearchClient, testCaseUUID, startedLatch, finishedLatch,
                objectMapper
            );
            thread.setUncaughtExceptionHandler((th, ex) -> threadException = ex);
            thread.start();
        }

        startedLatch.countDown();
        finishedLatch.await();
    }

    public static class AddCorrespondentRequestThread extends Thread {
        private final OpenSearchClient elasticSearchClient;
        private final UUID caseUuid;
        private final CountDownLatch startedLatch;
        private final CountDownLatch finishedLatch;
        private final ObjectMapper objectMapper;

        public AddCorrespondentRequestThread(OpenSearchClient elasticSearchClient, UUID caseUuid, CountDownLatch startedLatch, CountDownLatch finishedLatch,
                                             ObjectMapper objectMapper
                                            ) {
            this.elasticSearchClient = elasticSearchClient;
            this.caseUuid = caseUuid;
            this.startedLatch = startedLatch;
            this.finishedLatch = finishedLatch;
            this.objectMapper = objectMapper;
        }

        @Override
        public void run() {
            var toAdd = Correspondent.from(generateCorrespondentDetailsDto());
            var scriptService = new CorrespondentScriptService(objectMapper);
            var script = scriptService.upsertCorrespondentScript(toAdd);

            try {
                startedLatch.await();

                elasticSearchClient.update(INDEX_TYPE, caseUuid, script);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                finishedLatch.countDown();
            }
        }
    }
}
