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
    private CreateCaseRequest validCreateCaseRequest = new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    private UpdateCaseRequest validUpdateCaseRequest = new UpdateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    private CreateCorrespondentRequest validCreateCorrespondentRequest = new CreateCorrespondentRequest(UUID.randomUUID(), LocalDateTime.now(), "LAW", "FULLNAME", null, "0", "e", "REF");
    private CreateTopicRequest validCreateTopicRequest = new CreateTopicRequest(UUID.randomUUID(), "Test Topic");

    @Before
    public void setup() {
        caseDataService = new CaseDataService(elasticSearchClient, 10);
    }

    @Test
    public void ShouldCallCollaboratorsCreateCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.createCase(caseUUID, validCreateCaseRequest);

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).save(caseData);

        verify(caseData, times(1)).create(validCreateCaseRequest);

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundCreateCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.createCase(caseUUID, validCreateCaseRequest);

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void ShouldCallCollaboratorsUpdateCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.updateCase(caseUUID, validUpdateCaseRequest);

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(caseData);

        verify(caseData, times(1)).update(validUpdateCaseRequest);

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundUpdateCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.updateCase(caseUUID, validUpdateCaseRequest);

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void ShouldCallCollaboratorsDeleteCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.deleteCase(caseUUID);

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(caseData);

        verify(caseData, times(1)).delete();

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundDeleteCase() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.deleteCase(caseUUID);

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void ShouldCallCollaboratorsCreateCorrespondent() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.createCorrespondent(caseUUID, validCreateCorrespondentRequest);

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(caseData);

        verify(caseData, times(1)).addCorrespondent(validCreateCorrespondentRequest);

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundCreateCorrespondent() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.createCorrespondent(caseUUID, validCreateCorrespondentRequest);

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void ShouldCallCollaboratorsDeleteCorrespondent() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.deleteCorrespondent(caseUUID, validCreateCorrespondentRequest.getUuid().toString());

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(caseData);

        verify(caseData, times(1)).removeCorrespondent(validCreateCorrespondentRequest.getUuid());

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundDeleteCorrespondent() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.deleteCorrespondent(caseUUID, validCreateCorrespondentRequest.getUuid().toString());

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void ShouldCallCollaboratorsCreateTopic() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.createTopic(caseUUID, validCreateTopicRequest);

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(caseData);


        verify(caseData, times(1)).addTopic(any(Topic.class));

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundCreateTopic() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.createTopic(caseUUID, validCreateTopicRequest);

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(any(CaseData.class));


        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void ShouldCallCollaboratorsDeleteTopic() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(caseData);

        caseDataService.deleteTopic(caseUUID, validCreateTopicRequest.getUuid().toString());

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(caseData);

        verify(caseData, times(1)).removeTopic(validCreateTopicRequest.getUuid());

        verifyNoMoreInteractions(elasticSearchClient);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundDeleteTopic() {

        when(elasticSearchClient.findById(caseUUID)).thenReturn(new CaseData(caseUUID));

        caseDataService.deleteTopic(caseUUID, validCreateTopicRequest.getUuid().toString());

        verify(elasticSearchClient, times(1)).findById(caseUUID);
        verify(elasticSearchClient, times(1)).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }

    @Test
    public void ShouldNotSearchIfNoParams() {

        SearchRequest searchRequest = new SearchRequest();
        caseDataService.search(searchRequest);

        verify(elasticSearchClient, times(0)).update(any(CaseData.class));

        verifyNoMoreInteractions(elasticSearchClient);
    }
}