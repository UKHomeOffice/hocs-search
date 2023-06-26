package uk.gov.digital.ho.hocs.search.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.search.MultiSearchRequest;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.helpers.ObjectMapperConverterHelper;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.model.CorrespondentCaseData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenSearchClientTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Captor
    private ArgumentCaptor<GetRequest> getRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<UpdateRequest> updateRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<MultiSearchRequest> multiSearchRequestArgumentCaptor;

    private OpenSearchClient openSearchClient;

    private ObjectMapper objectMapper;

    private final CorrespondentDetailsDto correspondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(),
        LocalDateTime.now(), "LAW", "FULLNAME",
        new AddressDto("postcode", "address1", "address2", "address3", "country"), "0", "e", "REF", "ExtKey");

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
            .registerModule(new JavaTimeModule());

        openSearchClient = new OpenSearchClient(restHighLevelClient, "test", 10, 3);
    }

    @Test
    void shouldWriteFromCorrectAlias() throws IOException {
        CaseData caseData = new CaseData(
            new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", null, LocalDate.now(),
                LocalDate.now(), Map.of()));

        when(restHighLevelClient.update(updateRequestArgumentCaptor.capture(), any())).thenReturn(null);

        openSearchClient.update(caseData.getType(), caseData.getCaseUUID(), Map.of());

        assertThat(updateRequestArgumentCaptor.getValue().index()).isEqualTo("test-min-write");
    }

    @Test
    void shouldReadFromCorrectAlias() throws IOException {
        GetResponse getResponse = mock(GetResponse.class);

        when(restHighLevelClient.get(getRequestArgumentCaptor.capture(), any())).thenReturn(getResponse);

        openSearchClient.findById("MIN", UUID.randomUUID());

        assertThat(getRequestArgumentCaptor.getValue().index()).isEqualTo("test-min-read");
    }

    @Test
    void shouldOnlyUpdatePartialFields() throws IOException {
        CorrespondentCaseData correspondentCaseData = new CorrespondentCaseData();
        correspondentCaseData.addCorrespondent(correspondentDetailsDto);

        when(restHighLevelClient.update(updateRequestArgumentCaptor.capture(), any())).thenReturn(null);

        Map<String, Object> obj = ObjectMapperConverterHelper.convertObjectToMap(objectMapper, correspondentCaseData);

        openSearchClient.update("TEST", UUID.randomUUID() , obj);

        verify(restHighLevelClient).update(updateRequestArgumentCaptor.capture(), any());

        Map<String, Object> sourceMap = updateRequestArgumentCaptor.getValue().doc().sourceAsMap();
        AssertionsForClassTypes.assertThat(sourceMap).isEqualTo(obj);
    }


    @Test
    void shouldOnlySearchSpecifiedIndexes() throws IOException {
        Map<String, BoolQueryBuilder> searches = Map.of("TEST", new BoolQueryBuilder(), "TEST2", new BoolQueryBuilder());

        openSearchClient.search(searches);

        verify(restHighLevelClient).msearch(multiSearchRequestArgumentCaptor.capture(), any());

        assertThat(multiSearchRequestArgumentCaptor.getValue().requests()).hasSize(2);
    }

}
