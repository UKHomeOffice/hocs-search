package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.RestHighLevelClient;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticSearchSingularClientTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Captor
    private ArgumentCaptor<GetRequest> getRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<UpdateRequest> indexRequestArgumentCaptor;

    private ElasticSearchClient elasticSearchClient;

    @BeforeEach
    public void setup() {
        elasticSearchClient = new ElasticSearchSingularClient(restHighLevelClient, "test", 10);
    }

    @Test
    void shouldWriteFromCorrectAlias() throws IOException {
        CaseData caseData = new CaseData(
            new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", LocalDate.now(),
                LocalDate.now(), Map.of()));

        when(restHighLevelClient.update(indexRequestArgumentCaptor.capture(), any())).thenReturn(null);

        elasticSearchClient.update(caseData.getCaseUUID(), caseData.getType(), Map.of());

        assertThat(indexRequestArgumentCaptor.getValue().index()).isEqualTo("test-case");
    }

    @Test
    void shouldReadFromCorrectAlias() throws IOException {
        GetResponse getResponse = mock(GetResponse.class);

        when(restHighLevelClient.get(getRequestArgumentCaptor.capture(), any())).thenReturn(getResponse);

        elasticSearchClient.findById(UUID.randomUUID(), "MIN");

        assertThat(getRequestArgumentCaptor.getValue().index()).isEqualTo("test-case");
    }

}
