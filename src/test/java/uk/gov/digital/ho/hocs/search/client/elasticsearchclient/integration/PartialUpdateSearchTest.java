package uk.gov.digital.ho.hocs.search.client.elasticsearchclient.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateTopicRequest;
import uk.gov.digital.ho.hocs.search.aws.listeners.integration.BaseAwsSqsIntegrationTest;
import uk.gov.digital.ho.hocs.search.client.elasticsearchclient.ElasticSearchClient;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.model.Topic;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("localstack")
public class PartialUpdateSearchTest extends BaseAwsSqsIntegrationTest {

    @Autowired
    public ObjectMapper objectMapper;

    @Value("${aws.es.index-prefix}")
    String prefix;

    @Autowired
    public RestHighLevelClient client;

    @Autowired
    private ElasticSearchClient elasticSearchClient;
    private CreateCaseRequest createCaseRequest;

    @Test
    public void onlyUpdateCorrespondents() throws IOException {
        UUID caseUUID = UUID.randomUUID();

        createCaseRequest = new CreateCaseRequest(
                caseUUID,
                LocalDateTime.now(),
                "MIN",
                "MIN12345",
                LocalDate.now(),
                LocalDate.now(),
                Map.of("field", "value", "field2", "value2"));

        CorrespondentDetailsDto correspondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(),
                LocalDateTime.now(),
                "LAW",
                "FULLNAME",
                new AddressDto("postcode", "address1", "address2", "address3", "country"),
                "0",
                "e",
                "REF",
                "ExtKey");

        CaseData caseData = new CaseData(caseUUID);
        caseData.create(createCaseRequest);
        elasticSearchClient.save(caseData);
        await().until(() -> elasticSearchClient.findById(caseUUID) != null);

        // update request including correspondent and other new data
        CaseData updatedCaseData = new CaseData(caseUUID);
        updatedCaseData.setData(Map.of("newField", "newValue", "field2", "updatedValue"));
        updatedCaseData.addTopic(Topic.from(new CreateTopicRequest(UUID.randomUUID(), "TOPIC")));
        updatedCaseData.addCorrespondent(correspondentDetailsDto);
        elasticSearchClient.update(Set.of("allCorrespondents", "currentCorrespondents"), updatedCaseData);
        await().until(() -> elasticSearchClient.findById(caseUUID) != null);

        //assert that only correspondents has changed
        CaseData caseDataResult = elasticSearchClient.findById(caseUUID);
        assertThat(caseDataResult.getAllCorrespondents()).hasSize(1);
        assertThat(caseDataResult.getAllTopics()).hasSize(0);
        assertThat(caseDataResult.getData()).containsExactlyInAnyOrderEntriesOf(Map.of("field", "value", "field2", "value2"));
    }


}
