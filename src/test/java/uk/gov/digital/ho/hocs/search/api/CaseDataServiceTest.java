package uk.gov.digital.ho.hocs.search.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.search.api.dto.*;
import uk.gov.digital.ho.hocs.search.client.elasticsearchclient.ElasticSearchClient;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.model.Topic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseDataServiceTest {

    @Mock
    private ElasticSearchClient elasticSearchClient;

    @Mock
    private CaseData caseData;

    private CaseDataService caseDataService;
    private UUID caseUUID = UUID.randomUUID();
    private CreateCaseRequest validCreateCaseRequest = new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), new HashMap());
    private UpdateCaseRequest validUpdateCaseRequest = new UpdateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), new HashMap());
    private CorrespondentDetailsDto validCorrespondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(), LocalDateTime.now(), "LAW", "FULLNAME", null, "0", "e", "REF", "ExtKey");
    private CreateTopicRequest validCreateTopicRequest = new CreateTopicRequest(UUID.randomUUID(), "Test Topic");
    private SomuItemDto validSomuItemDto = new SomuItemDto(UUID.randomUUID(),UUID.randomUUID(), "{\"Test\": 1}");

    @Before
    public void setup() {
        caseDataService = new CaseDataService(elasticSearchClient, 10);
    }

    @Test
    public void createCase_newCaseData() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);
        when(caseData.isNewCaseData()).thenReturn(true);

        caseDataService.createCase(caseUUID, validCreateCaseRequest);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).save(caseData);

        verify(caseData).create(validCreateCaseRequest);
        verify(caseData).isNewCaseData();

        verifyNoMoreInteractions(elasticSearchClient, caseData);
    }

    @Test
    public void createCase_oldCaseData() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);
        when(caseData.isNewCaseData()).thenReturn(false);

        caseDataService.createCase(caseUUID, validCreateCaseRequest);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(caseData);

        verify(caseData).create(validCreateCaseRequest);
        verify(caseData).isNewCaseData();

        verifyNoMoreInteractions(elasticSearchClient, caseData);
    }

    @Test
    public void updateCase_newCaseData() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);
        when(caseData.isNewCaseData()).thenReturn(true);

        caseDataService.updateCase(caseUUID, validUpdateCaseRequest);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).save(caseData);

        verify(caseData).update(validUpdateCaseRequest);
        verify(caseData).isNewCaseData();

        verifyNoMoreInteractions(elasticSearchClient, caseData);
    }

    @Test
    public void updateCase_oldCaseData() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);
        when(caseData.isNewCaseData()).thenReturn(false);

        caseDataService.updateCase(caseUUID, validUpdateCaseRequest);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(caseData);

        verify(caseData).update(validUpdateCaseRequest);
        verify(caseData).isNewCaseData();

        verifyNoMoreInteractions(elasticSearchClient, caseData);
    }

    @Test
    public void shouldCreateNewIfNotFoundSaveCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.updateCase(caseUUID, validUpdateCaseRequest);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).save(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient, caseData);
    }

    @Test
    public void shouldCallCollaboratorsDeleteCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);
        DeleteCaseRequest deleteCaseRequest = new DeleteCaseRequest(caseUUID, true);

        caseDataService.deleteCase(caseUUID, deleteCaseRequest);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(caseData);

        verify(caseData).delete(true);

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void shouldCallCollaboratorsCompleteCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.completeCase(caseUUID);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(caseData);

        verify(caseData).complete();

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void shouldCreateNewIfNotFoundDeleteCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));
        DeleteCaseRequest deleteCaseRequest = new DeleteCaseRequest(caseUUID, true);

        caseDataService.deleteCase(caseUUID, deleteCaseRequest);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void shouldCreateNewIfNotFoundCompleteCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.completeCase(caseUUID);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void shouldCallCollaboratorsCreateCorrespondent() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.createCorrespondent(caseUUID, validCorrespondentDetailsDto);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(caseData);

        verify(caseData).addCorrespondent(validCorrespondentDetailsDto);

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void shouldCreateNewIfNotFoundCreateCorrespondent() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.createCorrespondent(caseUUID, validCorrespondentDetailsDto);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void shouldCallCollaboratorsDeleteCorrespondent() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.deleteCorrespondent(caseUUID, validCorrespondentDetailsDto);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(caseData);

        verify(caseData).removeCorrespondent(validCorrespondentDetailsDto.getUuid());

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void shouldCallUpdateCorrespondent() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.updateCorrespondent(caseUUID, validCorrespondentDetailsDto);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(caseData);

        verify(caseData).updateCorrespondent(validCorrespondentDetailsDto);

        verifyNoMoreInteractions(elasticSearchClient, caseData);
    }


    @Test
    public void shouldCreateNewIfNotFoundDeleteCorrespondent() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.deleteCorrespondent(caseUUID, validCorrespondentDetailsDto);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void shouldCallCollaboratorsCreateTopic() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.createTopic(caseUUID, validCreateTopicRequest);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(caseData);


        verify(caseData).addTopic(any(Topic.class));

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void shouldCreateNewIfNotFoundCreateTopic() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.createTopic(caseUUID, validCreateTopicRequest);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(any(CaseData.class));


        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void shouldCallCollaboratorsDeleteTopic() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.deleteTopic(caseUUID, validCreateTopicRequest.getUuid().toString());

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(caseData);

        verify(caseData).removeTopic(validCreateTopicRequest.getUuid());

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void shouldCreateNewIfNotFoundDeleteTopic() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.deleteTopic(caseUUID, validCreateTopicRequest.getUuid().toString());

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void shouldCreateSomuItemIfNotExists() {
        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.createSomuItem(caseUUID, validSomuItemDto);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void shouldDeleteSomuItemIfExists() {
        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.deleteSomuItem(caseUUID, validSomuItemDto);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void shouldUpdateSomuItemIfExists() {
        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.updateSomuItem(caseUUID, validSomuItemDto);

        verify(elasticSearchClient).findById(caseUUID);
        verify(elasticSearchClient).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void shouldNotSearchIfNoParams() {

        SearchRequest searchRequest = new SearchRequest();
        caseDataService.search(searchRequest);

        verify(elasticSearchClient, times(0)).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }
}
