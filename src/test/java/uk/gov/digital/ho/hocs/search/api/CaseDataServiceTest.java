package uk.gov.digital.ho.hocs.search.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;
import uk.gov.digital.ho.hocs.search.domain.repository.CaseRepository;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.model.Topic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseDataServiceTest {

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private CaseData caseData;

    private CaseDataService caseDataService;
    private UUID caseUUID = UUID.randomUUID();
    private CreateCaseRequest validCreateCaseRequest = new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    private UpdateCaseRequest validUpdateCaseRequest = new UpdateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    private CreateCorrespondentRequest validCreateCorrespondentRequest = new CreateCorrespondentRequest(UUID.randomUUID(), LocalDateTime.now(), "LAW", "FULLNAME", null, "0", "e", "REF");
    private Topic validTopic = new Topic(UUID.randomUUID(), "VALUE");

    @Before
    public void setup(){
        caseDataService = new CaseDataService(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsCreateCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(caseData));

        caseDataService.createCase(caseUUID, validCreateCaseRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(caseData);

        verify(caseData, times(1)).create(validCreateCaseRequest);

        verifyNoMoreInteractions(caseRepository);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundCreateCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.createCase(caseUUID, validCreateCaseRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsUpdateCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(caseData));

        caseDataService.updateCase(caseUUID, validUpdateCaseRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(caseData);

        verify(caseData, times(1)).update(validUpdateCaseRequest);

        verifyNoMoreInteractions(caseRepository);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundUpdateCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.updateCase(caseUUID, validUpdateCaseRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsDeleteCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(caseData));

        caseDataService.deleteCase(caseUUID);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(caseData);

        verify(caseData, times(1)).delete();

        verifyNoMoreInteractions(caseRepository);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundDeleteCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.deleteCase(caseUUID);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsCreateCorrespondent() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(caseData));

        caseDataService.createCorrespondent(caseUUID, validCreateCorrespondentRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(caseData);

        verify(caseData, times(1)).addCorrespondent(validCreateCorrespondentRequest);

        verifyNoMoreInteractions(caseRepository);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundCreateCorrespondent() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.createCorrespondent(caseUUID, validCreateCorrespondentRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsDeleteCorrespondent() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(caseData));

        caseDataService.deleteCorrespondent(caseUUID, validCreateCorrespondentRequest.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(caseData);

        verify(caseData, times(1)).removeCorrespondent(validCreateCorrespondentRequest.getUuid());

        verifyNoMoreInteractions(caseRepository);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundDeleteCorrespondent() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.deleteCorrespondent(caseUUID, validCreateCorrespondentRequest.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsCreateTopic() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(caseData));

        caseDataService.createTopic(caseUUID, validTopic.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(caseData);

        verify(caseData, times(1)).addTopic(any(Topic.class));

        verifyNoMoreInteractions(caseRepository);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundCreateTopic() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.createTopic(caseUUID, validTopic.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsDeleteTopic() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(caseData));

        caseDataService.deleteTopic(caseUUID, validTopic.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(caseData);

        verify(caseData, times(1)).removeTopic(validTopic.getUuid());

        verifyNoMoreInteractions(caseRepository);
        verifyNoMoreInteractions(caseData);
    }

    @Test
    public void ShouldCreateNewIfNotFoundDeleteTopic() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.deleteTopic(caseUUID, validTopic.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }
}