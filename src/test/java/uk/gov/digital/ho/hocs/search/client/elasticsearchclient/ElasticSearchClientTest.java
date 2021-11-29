package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("localstack")
public class ElasticSearchClientTest {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    private CaseData testCaseData;

    private volatile Throwable threadException;

    @BeforeEach
    public void before() {
        testCaseData = new CaseData(UUID.randomUUID());

        elasticSearchClient.save(testCaseData);
    }

    @Test
    public void parallellyUpdatingElasticDocument_doesNotThrowVersionConflict() throws InterruptedException {
        runCaseUpdateThreads(2);

        // Assert that version_conflict_engine_exception no longer thrown.
        assertNull(threadException);

        // Assert that both data entries are added on retry.
        var caseData = elasticSearchClient.findById(testCaseData.getCaseUUID());
        assertEquals(caseData.getData().size(), 2);
    }

    private void runCaseUpdateThreads(int threadCount) throws InterruptedException {
        CountDownLatch startedLatch = new CountDownLatch(1);
        CountDownLatch finishedLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            var thread = new ElasticUpdateRequestThread(elasticSearchClient, testCaseData.getCaseUUID(), startedLatch, finishedLatch);
            thread.setUncaughtExceptionHandler((th, ex) -> threadException = ex);
            thread.start();
        }

        startedLatch.countDown();
        finishedLatch.await();
    }

    public static class ElasticUpdateRequestThread extends Thread {
        private final ElasticSearchClient elasticSearchClient;
        private final UUID caseUuid;
        private final CountDownLatch startedLatch;
        private final CountDownLatch finishedLatch;

        public ElasticUpdateRequestThread(ElasticSearchClient elasticSearchClient, UUID caseUuid, CountDownLatch startedLatch, CountDownLatch finishedLatch) {
            this.elasticSearchClient = elasticSearchClient;
            this.caseUuid = caseUuid;
            this.startedLatch = startedLatch;
            this.finishedLatch = finishedLatch;
        }

        @Override
        public void run() {
            try {
                startedLatch.await();

                var caseData = elasticSearchClient.findById(caseUuid);
                caseData.setData(Map.of(this.hashCode(), "TEST"));
                elasticSearchClient.update(caseData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                finishedLatch.countDown();
            }
        }
    }
}
