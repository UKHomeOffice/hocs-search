package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.RestHighLevelClient;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;
import uk.gov.digital.ho.hocs.search.api.helpers.ObjectMapperConverterHelper;
import uk.gov.digital.ho.hocs.search.domain.model.CorrespondentCaseData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseElasticSearchClientTest {

    private ElasticSearchClient elasticSearchClient;

    private ObjectMapper objectMapper;

    @Mock
    RestHighLevelClient restHighLevelClient;

    @Captor
    ArgumentCaptor<UpdateRequest> updateRequestArgumentCaptor;

    private final CorrespondentDetailsDto correspondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(),
        LocalDateTime.now(), "LAW", "FULLNAME",
        new AddressDto("postcode", "address1", "address2", "address3", "country"), "0", "e", "REF", "ExtKey");

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
            .registerModule(new JavaTimeModule());

        elasticSearchClient = new ElasticSearchSingularClient(restHighLevelClient, "test", 10);
    }

    @Test
    void shouldOnlyUpdatePartialFields() throws IOException {
        CorrespondentCaseData correspondentCaseData = new CorrespondentCaseData();
        correspondentCaseData.addCorrespondent(correspondentDetailsDto);

        when(restHighLevelClient.update(updateRequestArgumentCaptor.capture(), any())).thenReturn(null);

        Map<String, Object> obj = ObjectMapperConverterHelper.convertObjectToMap(objectMapper, correspondentCaseData);

        elasticSearchClient.update(UUID.randomUUID(), "TEST", obj);

        verify(restHighLevelClient).update(updateRequestArgumentCaptor.capture(), any());

        Map<String, Object> sourceMap = updateRequestArgumentCaptor.getValue().doc().sourceAsMap();
        assertThat(sourceMap).isEqualTo(obj);
    }

}
