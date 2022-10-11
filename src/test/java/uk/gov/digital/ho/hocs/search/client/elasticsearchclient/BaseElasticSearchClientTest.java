package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BaseElasticSearchClientTest {

    private ElasticSearchClient elasticSearchClient;

    @Mock
    RestHighLevelClient restHighLevelClient;

    private UUID caseUUID = UUID.randomUUID();

    @Captor
    ArgumentCaptor<UpdateRequest> updateRequestArgumentCaptor;

    private CreateCaseRequest createCaseRequest = new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN",
        "REF", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), Map.of("field", "value", "field2", "value2"));

    private CorrespondentDetailsDto correspondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(),
        LocalDateTime.now(), "LAW", "FULLNAME",
        new AddressDto("postcode", "address1", "address2", "address3", "country"), "0", "e", "REF", "ExtKey");

    @BeforeEach
    public void setup() {
        ObjectMapper m = new ObjectMapper();
        m.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        m.registerModule(new JavaTimeModule());
        elasticSearchClient = new ElasticSearchSingularClient(m, restHighLevelClient, "test");
    }

    @Test
    public void shouldOnlyUpdatePartialFields() throws IOException {

        CaseData caseData = new CaseData(caseUUID);
        caseData.create(createCaseRequest);

        assertThat(caseData.getCurrentCorrespondents()).isEmpty();
        assertThat(caseData.getAllCorrespondents()).isEmpty();

        when(restHighLevelClient.update(updateRequestArgumentCaptor.capture(), any())).thenReturn(null);

        caseData.addCorrespondent(correspondentDetailsDto);

        elasticSearchClient.update(Set.of("currentCorrespondents"), caseData);
        verify(restHighLevelClient).update(updateRequestArgumentCaptor.capture(), any());

        Map<String, Object> sourceMap = updateRequestArgumentCaptor.getValue().doc().sourceAsMap();
        assertThat(sourceMap).containsOnlyKeys("currentCorrespondents");
        Map<String, String> correspondentMap = ((List<Map<String, String>>) sourceMap.get("currentCorrespondents")).get(
            0);

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("uuid", correspondentDetailsDto.getUuid().toString());
        expectedMap.put("created", correspondentDetailsDto.getCreated().toString());
        expectedMap.put("type", correspondentDetailsDto.getType());
        expectedMap.put("fullname", correspondentDetailsDto.getFullname());
        expectedMap.put("email", correspondentDetailsDto.getEmail());
        expectedMap.put("reference", correspondentDetailsDto.getReference());
        expectedMap.put("country", correspondentDetailsDto.getAddress().getCountry());
        expectedMap.put("address3", correspondentDetailsDto.getAddress().getAddress3());
        expectedMap.put("address2", correspondentDetailsDto.getAddress().getAddress2());
        expectedMap.put("address1", correspondentDetailsDto.getAddress().getAddress1());
        expectedMap.put("postcode", correspondentDetailsDto.getAddress().getPostcode());
        expectedMap.put("telephone", correspondentDetailsDto.getTelephone());
        expectedMap.put("externalKey", correspondentDetailsDto.getExternalKey());

        assertThat(correspondentMap).containsExactlyEntriesOf(expectedMap);
    }

}
