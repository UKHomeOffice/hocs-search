package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchSingularClientTest {

    @Mock
    RestHighLevelClient restHighLevelClient;

    @Mock
    GetResponse getResponse;

    @Captor
    ArgumentCaptor<GetRequest> getRequestArgumentCaptor;

    @Captor
    ArgumentCaptor<IndexRequest> indexRequestArgumentCaptor;

    private ElasticSearchClient elasticSearchClient;

    @BeforeEach
    public void setup() {
        ObjectMapper m = new ObjectMapper();
        m.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        m.registerModule(new JavaTimeModule());
        elasticSearchClient = new ElasticSearchSingularClient(m, restHighLevelClient, "test");
    }

    @Test
    public void shouldWriteFromCorrectAlias() throws IOException {
        CaseData caseData = new CaseData(UUID.randomUUID());
        caseData.create(new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN",
            "REF", LocalDate.now(), LocalDate.now(), Map.of()));

        when(restHighLevelClient.index(indexRequestArgumentCaptor.capture(), any())).thenReturn(null);

        elasticSearchClient.save(caseData);

        assertThat(indexRequestArgumentCaptor.getValue().index()).isEqualTo("test-case");
    }

    @Test
    public void shouldReadFromCorrectAlias() throws IOException {
        when(restHighLevelClient.get(getRequestArgumentCaptor.capture(), any())).thenReturn(getResponse);

        elasticSearchClient.findById(UUID.randomUUID());

        assertThat(getRequestArgumentCaptor.getValue().index()).isEqualTo("test-case");
    }

}
